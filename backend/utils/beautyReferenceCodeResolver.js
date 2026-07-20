const BeautyReference = require("../models/beautyReference.model");

/**
 * Cache for active beauty reference codes.
 * Key: reference_group
 * Value: Set of reference_code strings
 */
let codeCache = null;
let lastFetchTime = 0;
const CACHE_TTL_MS = 60 * 60 * 1000; // 1 hour

/**
 * Ensures the cache is loaded. If expired or empty, fetches from DB.
 */
async function ensureCache() {
  const now = Date.now();
  if (codeCache && now - lastFetchTime < CACHE_TTL_MS) {
    return;
  }

  codeCache = new Map();
  try {
    const references = await BeautyReference.find({ is_active: true }).select("reference_group reference_code -_id").lean();
    for (const ref of references) {
      if (!codeCache.has(ref.reference_group)) {
        codeCache.set(ref.reference_group, new Set());
      }
      codeCache.get(ref.reference_group).add(ref.reference_code);
    }
    lastFetchTime = now;
  } catch (error) {
    console.error("❌ Error fetching BeautyReferences for validation cache:", error);
    // If cache fails and it's completely empty, we initialize it empty so requests don't hang,
    // though this means validation might fail safely (default deny).
    if (!codeCache) codeCache = new Map();
  }
}

/**
 * Validates if a code belongs to a specific reference group.
 * @param {string} group The reference group (e.g., 'skin_type')
 * @param {string} code The code to check (e.g., 'oily')
 * @returns {Promise<boolean>} True if valid, false otherwise.
 */
async function isValidCode(group, code) {
  if (!code) return false;
  await ensureCache();
  const groupSet = codeCache.get(group);
  return groupSet ? groupSet.has(code) : false;
}

/**
 * Validates multiple codes against a group.
 * @param {string} group The reference group
 * @param {string[]} codes Array of codes to check
 * @returns {Promise<boolean>} True if ALL codes are valid.
 */
async function areValidCodes(group, codes) {
  if (!Array.isArray(codes)) return false;
  if (codes.length === 0) return true; // Empty array is valid (used for clearing)
  
  await ensureCache();
  const groupSet = codeCache.get(group);
  if (!groupSet) return false;

  return codes.every(c => groupSet.has(c));
}

/**
 * Clears the in-memory cache, forcing a DB fetch on next validation.
 */
function invalidateCache() {
  codeCache = null;
  lastFetchTime = 0;
}

/**
 * Returns all active codes for a group (useful for defaults/migrations).
 * @param {string} group 
 * @returns {Promise<string[]>}
 */
async function getCodesForGroup(group) {
  await ensureCache();
  const groupSet = codeCache.get(group);
  return groupSet ? Array.from(groupSet) : [];
}

module.exports = {
  isValidCode,
  areValidCodes,
  invalidateCache,
  getCodesForGroup,
  ensureCache
};
