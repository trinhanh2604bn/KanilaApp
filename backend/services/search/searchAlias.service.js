"use strict";

const mongoose = require("mongoose");
const SearchAlias = require("../../models/searchAlias.model");

/**
 * SearchAliasService
 *
 * Resolves makeup aliases and abbreviations to canonical terms.
 * Prevents: infinite loops, override of exact codes, duplicate expansions.
 */
class SearchAliasService {
  /**
   * Look up an alias for the given normalized query.
   * Returns null when no alias matches.
   *
   * @param {string} normalizedQuery  - Already folded/lowercased query
   * @param {string} [categoryContext] - Optional category hint e.g. "lip"
   * @returns {Promise<{canonical_term: string, canonical_normalized: string}|null>}
   */
  static async resolveAlias(normalizedQuery, categoryContext = null) {
    if (!normalizedQuery || normalizedQuery.trim() === "") return null;

    // Try context-specific alias first, then generic
    const query = {
      alias_normalized: normalizedQuery,
      is_active: true,
    };

    let alias = null;

    if (categoryContext) {
      alias = await SearchAlias.findOne({
        ...query,
        category_context: categoryContext,
      })
        .sort({ priority: -1 })
        .lean();
    }

    if (!alias) {
      // If we still don't have an alias, try to find ANY alias matching the query (generic first, then anything)
      alias = await SearchAlias.findOne({
        ...query,
      })
        // Sort to prefer category_context: null (generic) first, then by priority
        .sort({ category_context: 1, priority: -1 })
        .lean();
    }

    if (!alias) return null;

    return {
      canonical_term: alias.canonical_term,
      canonical_normalized: alias.canonical_normalized,
    };
  }

  /**
   * Bulk insert aliases (idempotent — upserts by alias_normalized).
   * Used by the seed script.
   */
  static async bulkUpsert(aliases) {
    if (!aliases || aliases.length === 0) return { inserted: 0, updated: 0 };

    const ops = aliases.map((a) => ({
      updateOne: {
        filter: {
          alias_normalized: a.alias_normalized,
          category_context: a.category_context || null,
        },
        update: { $set: a },
        upsert: true,
      },
    }));

    const result = await SearchAlias.bulkWrite(ops, { ordered: false });
    return {
      inserted: result.upsertedCount || 0,
      updated: result.modifiedCount || 0,
    };
  }
}

module.exports = SearchAliasService;
