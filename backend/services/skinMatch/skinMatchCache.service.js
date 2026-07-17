const CustomerProductMatchSnapshot = require("../../models/customerProductMatchSnapshot.model");
const config = require("./skinMatch.config");
const productMatchContextService = require("./productMatchContext.service");
const skinMatchService = require("./skinMatch.service");

class SkinMatchCacheService {
  async getOrComputeMatch(customer, beautyProfile, productId) {
    // MOCK DATA: Sinh điểm giả lập ngẫu nhiên nhưng ổn định (deterministic) dựa trên ID sản phẩm
    if (!beautyProfile) {
      return { status: "PROFILE_REQUIRED" };
    }

    const possibleScores = [30, 40, 50, 80, 90, 95];
    const hash = String(productId).split('').reduce((acc, char) => acc + char.charCodeAt(0), 0);
    const score = possibleScores[hash % possibleScores.length];

    let match_level, match_explanation, reasons, cautions = [];

    if (score >= 90) {
      match_level = "EXCELLENT_MATCH";
      match_explanation = "Sản phẩm rất phù hợp vì thành phần an toàn và hỗ trợ đúng vấn đề da của bạn.";
      reasons = [
        { code: "SKIN_TYPE_MATCH", text: "Phù hợp tuyệt đối với loại da của bạn", contribution: 40 },
        { code: "CONCERN_MATCH", text: "Giải quyết hiệu quả các vấn đề da bạn đang gặp phải", contribution: 30 },
        { code: "INGREDIENT_MATCH", text: "Chứa các thành phần bạn yêu thích", contribution: 25 }
      ];
    } else if (score >= 80) {
      match_level = "GOOD_MATCH";
      match_explanation = "Sản phẩm tốt cho làn da của bạn.";
      reasons = [
        { code: "SKIN_TYPE_MATCH", text: "Phù hợp với loại da của bạn", contribution: 40 },
        { code: "CONCERN_MATCH", text: "Hỗ trợ một số vấn đề da của bạn", contribution: 40 }
      ];
    } else if (score >= 50) {
      match_level = "MODERATE_MATCH";
      match_explanation = "Sản phẩm phù hợp ở mức trung bình. Có thể kết hợp với các sản phẩm khác.";
      reasons = [
        { code: "SKIN_TYPE_MATCH", text: "Tương thích ở mức cơ bản với loại da của bạn", contribution: 50 }
      ];
    } else {
      match_level = "CAUTION";
      match_explanation = "Mức độ phù hợp thấp. Nên cân nhắc kỹ trước khi sử dụng.";
      reasons = [
        { code: "NO_CONCERN_MATCH", text: "Không tập trung vào các vấn đề da hiện tại của bạn", contribution: 0 }
      ];
      cautions = [{ code: "LOW_MATCH", text: "Sản phẩm này có thể không mang lại hiệu quả mong muốn cho tình trạng da của bạn.", severity: "INFO" }];
    }

    const possibleAttributes = [
      "Da dầu", "Da khô", "Da nhạy cảm", "Da mụn", 
      "Lỗ chân lông to", "Thâm nám", "Lão hóa", 
      "Da không đều màu", "Niacinamide", "BHA", "Vitamin C", "HA"
    ];
    const numAttributes = 2 + (hash % 3); // Chọn từ 2 đến 4 thuộc tính
    const matched_attributes = [];
    for (let i = 0; i < numAttributes; i++) {
      const attrIndex = (hash + i * 7) % possibleAttributes.length;
      matched_attributes.push(possibleAttributes[attrIndex]);
    }

    return {
      status: "READY",
      product_id: String(productId),
      score: score,
      estimated_score: null,
      estimated: false,
      match_level: match_level,
      confidence_score: 90,
      profile_completion_rate: 100,
      matching_data_completeness: 100,
      match_explanation: match_explanation,
      reasons: reasons,
      cautions: cautions,
      hard_conflicts: [],
      matched_attributes: matched_attributes,
      generated_at: new Date(),
      expires_at: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000)
    };

    /* --- BỎ QUA LOGIC THỰC TẾ BÊN DƯỚI VÌ ĐÃ MOCK DATA ---
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
    */
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
