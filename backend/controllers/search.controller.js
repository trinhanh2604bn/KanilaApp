"use strict";

const SearchService   = require("../services/search.service");
const SearchValidator = require("../validations/search.validator");
const jwt             = require("jsonwebtoken");

/**
 * Search Controller
 * All endpoints delegate validation to SearchValidator, logic to SearchService.
 */

/**
 * GET /api/search/products
 */
async function searchProducts(req, res) {
  const { isValid, errors, sanitized } = SearchValidator.validateSearchParams(req.query);
  if (!isValid) {
    return res.status(400).json({ success: false, message: "Invalid search parameters", errors });
  }

  try {
    const result = await SearchService.searchProducts(sanitized.q, sanitized);
    return res.json({ success: true, data: result });
  } catch (err) {
    console.error("[SearchController] searchProducts error:", err);
    return res.status(500).json({ success: false, message: "Search temporarily unavailable" });
  }
}

/**
 * GET /api/search/suggestions
 */
async function getSuggestions(req, res) {
  const { isValid, errors, sanitized } = SearchValidator.validateSuggestionParams(req.query);
  if (!isValid) {
    return res.status(400).json({ success: false, message: "Invalid suggestion parameters", errors });
  }

  try {
    const result = await SearchService.getSuggestions(sanitized.q, sanitized);
    return res.json({ success: true, data: result });
  } catch (err) {
    console.error("[SearchController] getSuggestions error:", err);
    return res.status(500).json({ success: false, message: "Suggestions temporarily unavailable" });
  }
}

/**
 * GET /api/search/scan
 * Exact barcode / QR / SKU / shade code lookup.
 */
async function scanSearch(req, res) {
  const { isValid, errors, sanitized } = SearchValidator.validateScanParams(req.query);
  if (!isValid) {
    return res.status(400).json({ success: false, message: "Invalid scan parameters", errors });
  }

  try {
    const result = await SearchService.scanSearch(sanitized.value);
    return res.json({ success: true, data: result });
  } catch (err) {
    console.error("[SearchController] scanSearch error:", err);
    return res.status(500).json({ success: false, message: "Scan search temporarily unavailable" });
  }
}

/**
 * GET /api/search/discovery
 * Trending makeup products for the empty search screen.
 */
async function getDiscovery(req, res) {
  try {
    const items = await SearchService.getDiscovery();
    return res.json({ success: true, data: { items } });
  } catch (err) {
    console.error("[SearchController] getDiscovery error:", err);
    return res.status(500).json({ success: false, message: "Discovery temporarily unavailable" });
  }
}

/**
 * POST /api/search/events
 * Records client-side interactions (clicks, add-to-cart, etc.).
 */
async function recordEvent(req, res) {
  const { isValid, errors, sanitized } = SearchValidator.validateEventPayload(req.body);
  if (!isValid) {
    return res.status(400).json({ success: false, message: "Invalid event payload", errors });
  }

  // Attempt to extract authenticated customer
  let customerId = null;
  try {
    const token = (req.headers.authorization || "").replace("Bearer ", "").trim();
    if (token) {
      const decoded = jwt.verify(
        token,
        process.env.JWT_ACCESS_SECRET || process.env.JWT_SECRET || "access_secret"
      );
      // Normalize payload to ensure account_id is set
      if (!decoded.account_id && decoded.accountId) {
        decoded.account_id = decoded.accountId;
      }
      if (decoded?.account_id) {
        customerId = decoded.account_id;
      }
    }
  } catch (_) {}

  const guestSessionId = req.headers["x-guest-session"] || req.body.guest_session_id || null;

  try {
    await SearchService.recordEvent(sanitized, { customerId, guestSessionId });
    return res.json({ success: true });
  } catch (err) {
    console.error("[SearchController] recordEvent error:", err);
    return res.status(500).json({ success: false, message: "Event recording failed" });
  }
}

module.exports = { searchProducts, getSuggestions, scanSearch, getDiscovery, recordEvent };
