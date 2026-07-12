/**
 * beautyProfileLegacyAdapter.middleware.js
 * 
 * Maps legacy Android frontend fields to their canonical equivalents
 * before they reach the validation layer.
 * Controlled by BEAUTY_PROFILE_ACCEPT_LEGACY_FIELDS feature flag.
 */

const legacyFieldMap = {
  skin_tone: "skin_color",
  undertone: "skin_undertone",
  finish_preference: "foundation_finish",
  lip_color_preference: "lipstick_colors",
  makeup_style: "makeup_styles",
  budget_range: "budget",
};

const beautyProfileLegacyAdapter = (req, res, next) => {
  if (process.env.BEAUTY_PROFILE_ACCEPT_LEGACY_FIELDS !== "true") {
    return next();
  }

  let mappedCount = 0;
  const conflicts = [];
  
  for (const [legacyKey, canonicalKey] of Object.entries(legacyFieldMap)) {
    if (req.body[legacyKey] !== undefined) {
      if (req.body[canonicalKey] !== undefined) {
        // Conflict detected: both legacy and canonical exist
        conflicts.push(`Conflict: Cannot send both legacy '${legacyKey}' and canonical '${canonicalKey}' in the same request.`);
      } else {
        // Safe to map
        req.body[canonicalKey] = req.body[legacyKey];
      }
      
      // Remove legacy key to prevent validation errors downstream
      delete req.body[legacyKey];
      mappedCount++;
    }
  }

  if (conflicts.length > 0) {
    return res.status(400).json({
      success: false,
      message: "Validation error: Conflicting legacy and canonical fields.",
      error: conflicts.join(', ')
    });
  }

  if (mappedCount > 0) {
    console.warn(`[DEPRECATION] Mapped ${mappedCount} legacy Beauty Profile fields for Customer ${req.params.customer_id || "unknown"}.`);
  }

  next();
};

module.exports = beautyProfileLegacyAdapter;
