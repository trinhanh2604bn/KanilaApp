"use strict";

const mongoose = require("mongoose");
const searchConfig = require("../config/search.config");

/**
 * SearchValidator
 *
 * Validates and sanitizes all public search API input.
 * Returns { isValid, errors, sanitized } — never throws.
 */
class SearchValidator {
  /**
   * Validate GET /api/search/products query parameters.
   */
  static validateSearchParams(rawQuery) {
    const errors = [];
    const sanitized = {};

    // q — optional text query
    if (rawQuery.q != null) {
      if (typeof rawQuery.q !== "string") {
        errors.push("q must be a string");
      } else {
        sanitized.q = rawQuery.q.slice(0, 120);
      }
    } else {
      sanitized.q = "";
    }

    // page
    const rawPage = rawQuery.page;
    if (rawPage !== undefined) {
      const p = parseInt(rawPage, 10);
      if (!Number.isFinite(p) || p < 1) {
        errors.push("page must be a positive integer");
      } else {
        sanitized.page = Math.min(p, 10000);
      }
    } else {
      sanitized.page = 1;
    }

    // limit
    const rawLimit = rawQuery.limit;
    if (rawLimit !== undefined) {
      const l = parseInt(rawLimit, 10);
      if (!Number.isFinite(l) || l < 1) {
        errors.push("limit must be a positive integer");
      } else {
        sanitized.limit = Math.min(l, searchConfig.maxLimit);
      }
    } else {
      sanitized.limit = searchConfig.defaultLimit;
    }

    // sort
    const rawSort = rawQuery.sort;
    if (rawSort !== undefined) {
      if (!searchConfig.sortOptions.includes(rawSort)) {
        errors.push(`sort must be one of: ${searchConfig.sortOptions.join(", ")}`);
      } else {
        sanitized.sort = rawSort;
      }
    } else {
      sanitized.sort = "relevance";
    }

    // ObjectId arrays: category_ids, brand_ids
    for (const key of ["category_ids", "brand_ids"]) {
      if (rawQuery[key] != null) {
        const ids = String(rawQuery[key])
          .split(",")
          .map((s) => s.trim())
          .filter(Boolean);
        if (ids.length > searchConfig.maxFilterArrayLength) {
          errors.push(`${key} must contain at most ${searchConfig.maxFilterArrayLength} values`);
        } else {
          const valid = ids.filter((id) => mongoose.isValidObjectId(id));
          if (valid.length !== ids.length) {
            errors.push(`${key} contains invalid ObjectId values`);
          } else {
            sanitized[key] = valid;
          }
        }
      }
    }

    // Numeric filters
    for (const key of ["min_price", "max_price", "min_rating"]) {
      if (rawQuery[key] != null && rawQuery[key] !== "") {
        const v = Number(rawQuery[key]);
        if (!Number.isFinite(v) || v < 0) {
          errors.push(`${key} must be a non-negative number`);
        } else {
          sanitized[key] = v;
        }
      }
    }

    // Price range sanity
    if (sanitized.min_price != null && sanitized.max_price != null) {
      if (sanitized.min_price > sanitized.max_price) {
        errors.push("min_price must not exceed max_price");
      }
    }

    // Boolean filters
    for (const key of ["in_stock", "on_sale", "ar_supported", "waterproof",
                       "transfer_proof", "long_wear", "smudge_proof",
                       "oil_control", "sensitive_friendly"]) {
      if (rawQuery[key] != null) {
        const v = rawQuery[key];
        if (v === "true" || v === true) sanitized[key] = true;
        else if (v === "false" || v === false) sanitized[key] = false;
        else errors.push(`${key} must be true or false`);
      }
    }

    // String-array enum filters
    const enumFilters = {
      finish_types:    ["matte", "glossy", "dewy", "satin", "velvet", "natural", "glowy", "sheer", "shimmer"],
      coverage_levels: ["full", "medium", "light", "buildable", "sheer"],
      formula_types:   ["liquid", "powder", "stick", "cream", "gel", "mousse", "balm", "pencil"],
      texture_types:   ["velvet", "mousse", "balm", "watery", "buttery", "light"],
      makeup_styles:   ["natural", "glam", "everyday", "office", "smoky", "bold", "editorial"],
    };

    for (const [key, validValues] of Object.entries(enumFilters)) {
      if (rawQuery[key] != null) {
        const vals = String(rawQuery[key]).split(",").map((s) => s.trim().toLowerCase()).filter(Boolean);
        if (vals.length > searchConfig.maxFilterArrayLength) {
          errors.push(`${key} must contain at most ${searchConfig.maxFilterArrayLength} values`);
        } else {
          const invalid = vals.filter((v) => !validValues.includes(v));
          if (invalid.length > 0) {
            errors.push(`${key} contains unsupported values: ${invalid.join(", ")}`);
          } else {
            sanitized[key] = vals;
          }
        }
      }
    }

    // String-array free-form: color_families, shade_codes, shade_names, skin_tones, undertones
    for (const key of ["color_families", "shade_codes", "shade_names", "skin_tones", "undertones"]) {
      if (rawQuery[key] != null) {
        const vals = String(rawQuery[key])
          .split(",")
          .map((s) => s.trim().slice(0, 60))
          .filter(Boolean);
        if (vals.length > searchConfig.maxFilterArrayLength) {
          errors.push(`${key} must contain at most ${searchConfig.maxFilterArrayLength} values`);
        } else {
          sanitized[key] = vals;
        }
      }
    }

    return {
      isValid: errors.length === 0,
      errors,
      sanitized,
    };
  }

  /**
   * Validate GET /api/search/suggestions query parameters.
   */
  static validateSuggestionParams(rawQuery) {
    const errors = [];
    const sanitized = {};

    if (!rawQuery.q || typeof rawQuery.q !== "string") {
      sanitized.q = "";
    } else {
      sanitized.q = rawQuery.q.slice(0, 120);
    }

    const rawLimit = rawQuery.limit;
    if (rawLimit !== undefined) {
      const l = parseInt(rawLimit, 10);
      if (!Number.isFinite(l) || l < 1 || l > 20) {
        errors.push("limit must be between 1 and 20");
        sanitized.limit = 10;
      } else {
        sanitized.limit = l;
      }
    } else {
      sanitized.limit = 10;
    }

    return { isValid: errors.length === 0, errors, sanitized };
  }

  /**
   * Validate GET /api/search/scan.
   */
  static validateScanParams(rawQuery) {
    const errors = [];
    const sanitized = {};

    if (!rawQuery.value || typeof rawQuery.value !== "string") {
      errors.push("value is required");
    } else {
      sanitized.value = rawQuery.value.trim().slice(0, 120);
      if (sanitized.value.length === 0) errors.push("value must not be empty");
    }

    return { isValid: errors.length === 0, errors, sanitized };
  }

  /**
   * Validate POST /api/search/events.
   */
  static validateEventPayload(body) {
    const { ALLOWED_EVENT_TYPES } = require("../models/searchEvent.model");
    const errors = [];
    const sanitized = {};

    if (!body.event_type || !ALLOWED_EVENT_TYPES.includes(body.event_type)) {
      errors.push(`event_type must be one of: ${ALLOWED_EVENT_TYPES.join(", ")}`);
    } else {
      sanitized.event_type = body.event_type;
    }

    sanitized.query = typeof body.query === "string" ? body.query.slice(0, 120) : "";
    sanitized.query_id = typeof body.query_id === "string" ? body.query_id.slice(0, 64) : null;
    sanitized.latency_ms = Number.isFinite(Number(body.latency_ms)) ? Number(body.latency_ms) : 0;
    sanitized.result_position = Number.isFinite(Number(body.result_position)) ? Number(body.result_position) : null;
    sanitized.result_count = Number.isFinite(Number(body.result_count)) ? Number(body.result_count) : 0;
    sanitized.suggestion_type = typeof body.suggestion_type === "string" ? body.suggestion_type.slice(0, 30) : null;

    if (body.product_id && mongoose.isValidObjectId(body.product_id)) {
      sanitized.product_id = body.product_id;
    }
    if (body.variant_id && mongoose.isValidObjectId(body.variant_id)) {
      sanitized.variant_id = body.variant_id;
    }
    if (body.customer_id && mongoose.isValidObjectId(body.customer_id)) {
      sanitized.customer_id = body.customer_id;
    }

    sanitized.shade_code = typeof body.shade_code === "string" ? body.shade_code.slice(0, 30) : null;
    sanitized.guest_session_id = typeof body.guest_session_id === "string" ? body.guest_session_id.slice(0, 64) : null;

    // Filters: accept only plain object, strip nested objects and MongoDB operator keys
    if (body.filters && typeof body.filters === "object" && !Array.isArray(body.filters)) {
      sanitized.filters = {};
      for (const [k, v] of Object.entries(body.filters)) {
        // Strip MongoDB operator keys (starting with $) to prevent NoSQL injection
        if (k.startsWith("$")) continue;
        if (typeof v === "string" || typeof v === "number" || typeof v === "boolean") {
          sanitized.filters[k.slice(0, 30)] = v;
        }
      }
    } else {
      sanitized.filters = {};
    }

    return { isValid: errors.length === 0, errors, sanitized };
  }

  /**
   * Validate image search (POST /api/search/image).
   * Only checks MIME type and size — actual image validation happens in middleware.
   */
  static validateImageSearch(file) {
    if (!file) return { isValid: false, errors: ["Image file is required"] };
    const allowedTypes = ["image/jpeg", "image/png", "image/webp"];
    if (!allowedTypes.includes(file.mimetype)) {
      return { isValid: false, errors: ["Image must be JPEG, PNG, or WebP"] };
    }
    const maxBytes = 5 * 1024 * 1024; // 5 MB
    if (file.size > maxBytes) {
      return { isValid: false, errors: ["Image must be smaller than 5 MB"] };
    }
    return { isValid: true, errors: [] };
  }
}

module.exports = SearchValidator;
