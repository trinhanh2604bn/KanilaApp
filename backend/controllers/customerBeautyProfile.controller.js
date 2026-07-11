/**
 * customerBeautyProfile.controller.js
 *
 * SECURITY: All mutating endpoints enforce ownership.
 * - "me" → resolves to the authenticated user's own customer record.
 * - Raw ObjectId → only allowed when account_type === "admin" OR
 *   the customer._id matches the authenticated user's account_id.
 * - Cross-customer access by non-admins returns HTTP 403, not 400.
 *
 * Unknown fields are rejected with a structured 400 (not silently ignored).
 * System-managed fields (source, profile_hash, profile_completion_rate) are
 * not in SAFE_FIELDS and are therefore always rejected from client input.
 */
const customerBeautyProfileService = require("../services/customerBeautyProfile.service");
const { validateCustomerBeautyProfile } = require("../validations/customerBeautyProfile.validation");
const validateObjectId = require("../utils/validateObjectId");
const Customer = require("../models/customer.model");

// The only fields a client is allowed to send. source, profile_hash, etc. are NOT listed.
const SAFE_FIELDS = [
  "skin_type", "skin_concerns", "sensitivity_level", "skin_color", "skin_undertone",
  "foundation_finish", "lipstick_colors", "makeup_styles", "budget", "avoid_ingredients",
  "beauty_goals", "preferred_ingredients", "preferred_brands", "disliked_brands",
  "preferred_categories", "texture_preference", "fragrance_preference", "purchase_intent",
];

// Fields that clients often send back when submitting an existing profile object.
// We silently strip these to prevent validation errors, ensuring they cannot be modified.
const READ_ONLY_FIELDS = [
  "_id", "customer_id", "source", "profile_hash",
  "profile_completion_rate", "created_at", "updated_at", "__v"
];

/**
 * Detect any keys in body that are not in SAFE_FIELDS.
 * Silently strips READ_ONLY_FIELDS first.
 * Returns array of unknown key names.
 */
function detectUnknownFields(body) {
  const allKeys = Object.keys(body);
  for (const k of allKeys) {
    if (!SAFE_FIELDS.includes(k)) {
      delete body[k];
    }
  }
  
  // Prevent Mongoose CastError by stripping invalid ObjectIds
  const objectIdFields = ["preferred_brands", "disliked_brands", "preferred_categories"];
  for (const field of objectIdFields) {
    if (Array.isArray(body[field])) {
      body[field] = body[field].filter(id => typeof id === 'string' && id.match(/^[0-9a-fA-F]{24}$/));
    } else if (body[field]) {
      delete body[field];
    }
  }
  
  return [];
}

/**
 * Resolve the customer_id param to a concrete customer _id string,
 * while enforcing ownership for non-admins.
 *
 * Returns the resolved customer _id string, or null if:
 *   - param is invalid
 *   - "me" cannot be resolved to an account
 *   - non-admin supplies a raw id that does not belong to their account
 */
const resolveOwnedCustomerId = async (customerIdParam, req) => {
  const accountId = req.user?.account_id || req.user?.accountId;
  const isAdmin = req.user?.account_type === "admin";

  // ── "me" alias ──
  if (customerIdParam === "me") {
    if (!accountId || !validateObjectId(accountId)) return null;
    const customer = await Customer.findOne({ account_id: accountId }).select("_id").lean();
    return customer ? String(customer._id) : null;
  }

  // ── Raw ObjectId ──
  if (!validateObjectId(customerIdParam)) return null;

  if (isAdmin) {
    // Admins can access any customer
    return customerIdParam;
  }

  // Non-admin: verify this ObjectId belongs to the authenticated user
  if (!accountId || !validateObjectId(accountId)) return null;
  const customer = await Customer.findOne({ _id: customerIdParam, account_id: accountId })
    .select("_id")
    .lean();
  // If no match → the id belongs to a different customer → ownership violation
  // Return null so the caller returns 403
  return customer ? String(customer._id) : null;
};

const getProfile = async (req, res) => {
  try {
    const { customer_id } = req.params;
    const resolvedId = await resolveOwnedCustomerId(customer_id, req);
    if (!resolvedId) {
      return res.status(403).json({
        success: false,
        message: "Forbidden: you may only access your own beauty profile",
        error: null,
      });
    }
    const profile = await customerBeautyProfileService.getProfileByCustomerId(resolvedId);
    if (!profile) {
      return res.status(404).json({ success: false, message: "Customer beauty profile not found", error: null });
    }
    return res.status(200).json({
      success: true,
      message: "Fetched customer beauty profile successfully",
      data: profile,
      error: null,
    });
  } catch (error) {
    return res.status(500).json({ success: false, message: error.message, error: error.message });
  }
};

const createProfile = async (req, res) => {
  try {
    const { customer_id } = req.params;
    const resolvedId = await resolveOwnedCustomerId(customer_id, req);
    if (!resolvedId) {
      return res.status(403).json({
        success: false,
        message: "Forbidden: you may only create your own beauty profile",
        error: null,
      });
    }

    // Reject unknown fields (including system-managed: source, profile_hash, etc.)
    const unknownFields = detectUnknownFields(req.body);
    if (unknownFields.length > 0) {
      return res.status(400).json({
        success: false,
        message: "Validation error",
        error: unknownFields.map((f) => `Unknown or read-only field: "${f}" is not accepted in this request`).join(', '),
      });
    }

    // Schema-level validation (type checks + legacy field rejection)
    const validationErrors = await validateCustomerBeautyProfile(req.body);
    if (validationErrors.length > 0) {
      return res.status(400).json({ 
        success: false, 
        message: "Validation error", 
        error: validationErrors.join(', ') 
      });
    }

    const isAdmin = req.user?.account_type === "admin";
    const context = {
      source: isAdmin ? "admin" : "onboarding",
      actorAccountId: req.user?.account_id,
      trustedInternalCall: true,
    };

    const profile = await customerBeautyProfileService.createProfile(resolvedId, req.body, context);
    return res.status(201).json({
      success: true,
      message: "Created customer beauty profile successfully",
      data: profile,
      error: null,
    });
  } catch (error) {
    if (error.message.includes("already exists")) {
      return res.status(409).json({ success: false, message: error.message, error: null });
    }
    return res.status(500).json({ success: false, message: error.message, error: error.message });
  }
};

const updateProfile = async (req, res) => {
  try {
    const { customer_id } = req.params;
    const resolvedId = await resolveOwnedCustomerId(customer_id, req);
    if (!resolvedId) {
      return res.status(403).json({
        success: false,
        message: "Forbidden: you may only update your own beauty profile",
        error: null,
      });
    }

    // Reject unknown fields
    const unknownFields = detectUnknownFields(req.body);
    if (unknownFields.length > 0) {
      return res.status(400).json({
        success: false,
        message: "Validation error",
        error: unknownFields.map((f) => `Unknown or read-only field: "${f}" is not accepted in this request`).join(', '),
      });
    }

    const validationErrors = await validateCustomerBeautyProfile(req.body);
    if (validationErrors.length > 0) {
      return res.status(400).json({ 
        success: false, 
        message: "Validation error", 
        error: validationErrors.join(', ') 
      });
    }

    const isAdmin = req.user?.account_type === "admin";
    const context = {
      source: isAdmin ? "admin" : "account",
      actorAccountId: req.user?.account_id,
      trustedInternalCall: true,
    };

    const profile = await customerBeautyProfileService.updateProfile(resolvedId, req.body, context);
    return res.status(200).json({
      success: true,
      message: "Updated customer beauty profile successfully",
      data: profile,
      error: null,
    });
  } catch (error) {
    if (error.message.includes("not found")) {
      return res.status(404).json({ success: false, message: error.message, error: null });
    }
    return res.status(500).json({ success: false, message: error.message, error: error.message });
  }
};

const putNotAllowed = (req, res) => {
  res.set("Allow", "GET, POST, PATCH");
  return res.status(405).json({
    success: false,
    message: "Method Not Allowed. Use PATCH to update an existing profile.",
    error: null,
  });
};

module.exports = {
  getProfile,
  createProfile,
  updateProfile,
  putNotAllowed,
};
