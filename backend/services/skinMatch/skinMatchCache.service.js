const CustomerProductMatchSnapshot = require("../../models/customerProductMatchSnapshot.model");
const config = require("./skinMatch.config");
const productMatchContextService = require("./productMatchContext.service");
const skinMatchService = require("./skinMatch.service");

class SkinMatchCacheService {
  async getOrComputeMatch(customer, beautyProfile, productId) {
    if (!beautyProfile) {
      return { status: "PROFILE_REQUIRED" };
    }

    const productContext = await productMatchContextService.getContext(productId);
    const productMatchHash = productMatchContextService.generateHash(productContext);
    const profileHash = beautyProfile.profile_hash;

    const cache = await CustomerProductMatchSnapshot.findOne({
      customer_id: customer._id,
      product_id: productId
    }).lean();

    const isCacheValid = cache &&
      !cache.invalidated_at &&
      cache.profile_hash === profileHash &&
      cache.product_match_hash === productMatchHash &&
      cache.algorithm_version === config.SKIN_MATCH_ALGORITHM_VERSION &&
      (!cache.expires_at || cache.expires_at > new Date());

    if (isCacheValid) {
      return this._formatResponse(cache);
    }

    // Cache miss or invalid
    const result = skinMatchService.calculateSkinMatchScore(beautyProfile, productContext);
    
    // Save to cache
    const snapshotData = {
      ...result,
      customer_id: customer._id,
      product_id: productId,
      profile_hash: profileHash,
      product_match_hash: productMatchHash,
      algorithm_version: config.SKIN_MATCH_ALGORITHM_VERSION,
      invalidated_at: null,
      generated_at: new Date(),
      expires_at: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000) // 30 days
    };

    await CustomerProductMatchSnapshot.findOneAndUpdate(
      { customer_id: customer._id, product_id: productId },
      { $set: snapshotData },
      { upsert: true, new: true }
    );

    return this._formatResponse(snapshotData);
  }

  async invalidateByCustomerId(customerId) {
    await CustomerProductMatchSnapshot.updateMany(
      { customer_id: customerId },
      { $set: { invalidated_at: new Date() } }
    );
  }

  _formatResponse(snapshot) {
    return {
      status: snapshot.status,
      product_id: String(snapshot.product_id),
      score: snapshot.score,
      estimated_score: snapshot.estimated_score,
      estimated: snapshot.estimated_score !== null,
      match_level: snapshot.match_level,
      confidence_score: snapshot.confidence_score,
      profile_completion_rate: snapshot.profile_completion_rate,
      matching_data_completeness: snapshot.matching_data_completeness,
      match_explanation: snapshot.match_explanation,
      reasons: snapshot.reasons,
      cautions: snapshot.cautions,
      hard_conflicts: snapshot.hard_conflicts,
      matched_attributes: snapshot.matched_attributes,
      generated_at: snapshot.generated_at,
      expires_at: snapshot.expires_at
    };
  }
}

module.exports = new SkinMatchCacheService();
