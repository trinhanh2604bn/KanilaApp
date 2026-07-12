const config = require("./skinMatch.config");

class SkinMatchService {
  calculateSkinMatchScore(profile, productContext) {
    if (!profile) return this._insufficientDataResult();

    const isMakeup = productContext.product_type === "MAKEUP";
    const weights = isMakeup ? config.WEIGHTS.MAKEUP : config.WEIGHTS.SKINCARE;
    
    let totalScore = 0;
    let maxPossibleScore = 0;
    let reasons = [];
    let cautions = [];
    let hardConflicts = [];
    let matchedAttributes = [];
    let scoreBreakdown = {};

    // 1. Check Hard Conflicts first
    if (productContext.is_sensitive_friendly === false && config.CONFLICT_RULES.SENSITIVE_SKIN.check(profile)) {
       hardConflicts.push({ code: "NOT_SENSITIVE_FRIENDLY", text: "Sản phẩm không khuyên dùng cho da nhạy cảm." });
    }
    
    if (profile.avoid_ingredients && profile.avoid_ingredients.length > 0) {
       const avoided = profile.avoid_ingredients.filter(i => productContext.ingredients.includes(i) || productContext.ingredient_flags.includes(i));
       if (avoided.length > 0) {
         hardConflicts.push({ code: "AVOID_INGREDIENT", text: `Chứa thành phần bạn muốn tránh: ${avoided.join(", ")}` });
       }
    }

    if (productContext.avoid_for_ingredients && productContext.avoid_for_ingredients.length > 0) {
       // if any profile preference matches avoid_for_ingredients
       const avoided2 = productContext.avoid_for_ingredients.filter(i => profile.preferred_ingredients && profile.preferred_ingredients.includes(i));
       if (avoided2.length > 0) {
         hardConflicts.push({ code: "PRODUCT_AVOIDS_INGREDIENT", text: `Sản phẩm chống chỉ định dùng chung với: ${avoided2.join(", ")}` });
       }
    }

    // 2. Score Calculation Based on Available Data
    // We only add to maxPossibleScore if we have data for that dimension from BOTH sides (or at least one side to penalize)
    
    // Skin Type
    if (profile.skin_type && profile.skin_type !== "unknown" && productContext.skin_types_supported.length > 0) {
      const weight = isMakeup ? weights.skin_type_compatibility : weights.skin_type;
      maxPossibleScore += weight;
      if (productContext.skin_types_supported.includes(profile.skin_type)) {
        totalScore += weight;
        matchedAttributes.push(profile.skin_type);
        reasons.push({ code: "SKIN_TYPE_MATCH", text: "Phù hợp với loại da của bạn", contribution: weight });
        scoreBreakdown.skin_type = weight;
      } else {
        scoreBreakdown.skin_type = 0;
      }
    }

    // Skin Concerns
    if (profile.skin_concerns && profile.skin_concerns.length > 0 && productContext.skin_concerns_supported.length > 0 && !isMakeup) {
      const weight = weights.skin_concerns;
      maxPossibleScore += weight;
      const matches = profile.skin_concerns.filter(c => productContext.skin_concerns_supported.includes(c));
      if (matches.length > 0) {
        const score = Math.round(weight * (matches.length / Math.min(profile.skin_concerns.length, 3))); // Max 3 concerns considered for 100%
        const finalScore = Math.min(score, weight);
        totalScore += finalScore;
        matchedAttributes.push(...matches);
        reasons.push({ code: "CONCERN_MATCH", text: "Hỗ trợ các vấn đề da của bạn", contribution: finalScore });
        scoreBreakdown.skin_concerns = finalScore;
      } else {
        scoreBreakdown.skin_concerns = 0;
      }
    }

    // Tone & Undertone (Makeup)
    if (isMakeup && profile.skin_color && profile.skin_color !== "unknown" && productContext.skin_color_codes.length > 0) {
      const weight = weights.skin_color_tone_compatibility;
      maxPossibleScore += weight;
      if (productContext.skin_color_codes.includes(profile.skin_color)) {
        totalScore += weight;
        matchedAttributes.push(profile.skin_color);
        reasons.push({ code: "SKIN_COLOR_MATCH", text: "Phù hợp với màu da của bạn", contribution: weight });
        scoreBreakdown.skin_color = weight;
      } else {
        scoreBreakdown.skin_color = 0;
      }
    }

    // Finish Preference (Makeup)
    if (isMakeup && profile.foundation_finish && profile.foundation_finish !== "unknown" && productContext.finish_codes.length > 0) {
      const weight = weights.finish_preference;
      maxPossibleScore += weight;
      if (productContext.finish_codes.includes(profile.foundation_finish)) {
        totalScore += weight;
        matchedAttributes.push(profile.foundation_finish);
        reasons.push({ code: "FINISH_MATCH", text: "Phù hợp với lớp finish bạn thích", contribution: weight });
        scoreBreakdown.finish = weight;
      } else {
        scoreBreakdown.finish = 0;
      }
    }

    // Beauty Goals
    if (profile.beauty_goals && profile.beauty_goals.length > 0 && productContext.beauty_goals_supported.length > 0) {
      const weight = weights.beauty_goals || 0;
      if (weight > 0) {
        maxPossibleScore += weight;
        const matches = profile.beauty_goals.filter(c => productContext.beauty_goals_supported.includes(c));
        if (matches.length > 0) {
          const score = Math.round(weight * (matches.length / Math.min(profile.beauty_goals.length, 2)));
          const finalScore = Math.min(score, weight);
          totalScore += finalScore;
          matchedAttributes.push(...matches);
          reasons.push({ code: "BEAUTY_GOAL_MATCH", text: "Hỗ trợ mục tiêu làm đẹp của bạn", contribution: finalScore });
          scoreBreakdown.beauty_goals = finalScore;
        } else {
          scoreBreakdown.beauty_goals = 0;
        }
      }
    }

    // 3. Normalize score (0-100)
    let normalizedScore = 0;
    if (maxPossibleScore > 0) {
      normalizedScore = Math.round((totalScore / maxPossibleScore) * 100);
    } else {
      // Not enough intersecting data to form a reasonable score
      return {
        status: "INSUFFICIENT_PRODUCT_DATA",
        score: 0,
        estimated_score: null,
        match_level: "INSUFFICIENT_DATA",
        confidence_score: 0,
        profile_completion_rate: profile.profile_completion_rate,
        matching_data_completeness: productContext.matching_data_completeness,
        reasons: [],
        cautions: [],
        hard_conflicts: [],
        matched_attributes: [],
        score_breakdown: {}
      };
    }

    // Deduct for hard conflicts or cap
    if (hardConflicts.length > 0) {
      normalizedScore = Math.min(normalizedScore, 49); // Cap at CAUTION
    }

    // 4. Calculate Confidence Score
    const profileCompleteness = profile.profile_completion_rate || 0;
    const productCompleteness = productContext.matching_data_completeness || 0;
    const confidenceScore = Math.round((profileCompleteness + productCompleteness) / 2);
    
    // 5. Determine Match Level
    let matchLevel = "INSUFFICIENT_DATA";
    if (hardConflicts.length > 0) {
      matchLevel = "CAUTION";
    } else if (normalizedScore >= config.MATCH_LEVEL_THRESHOLDS.EXCELLENT_MATCH) {
      matchLevel = "EXCELLENT_MATCH";
    } else if (normalizedScore >= config.MATCH_LEVEL_THRESHOLDS.GOOD_MATCH) {
      matchLevel = "GOOD_MATCH";
    } else if (normalizedScore >= config.MATCH_LEVEL_THRESHOLDS.MODERATE_MATCH) {
      matchLevel = "MODERATE_MATCH";
    } else {
      matchLevel = "CAUTION";
    }

    const estimatedScore = confidenceScore < 50 ? normalizedScore : null;

    if (hardConflicts.length > 0) {
      cautions.push({ code: "HARD_CONFLICT", text: "Có yếu tố không phù hợp rõ rệt với hồ sơ của bạn.", severity: "HIGH" });
    } else if (productContext.is_sensitive_friendly === false && profile.sensitivity_level === "unknown") {
      cautions.push({ code: "PATCH_TEST_RECOMMENDED", text: "Nên thử trước trên một vùng da nhỏ nếu da nhạy cảm", severity: "INFO" });
    }

    // 6. Generate match explanation text
    let explanationText = "";
    if (hardConflicts.length > 0) {
      explanationText = "Sản phẩm này có chứa thành phần hoặc đặc tính không phù hợp với hồ sơ của bạn, có thể gây kích ứng.";
    } else if (normalizedScore >= config.MATCH_LEVEL_THRESHOLDS.GOOD_MATCH) {
      const strengths = reasons.map(r => r.text.toLowerCase()).join(", ");
      explanationText = `Sản phẩm rất phù hợp vì ${strengths}.`;
    } else if (normalizedScore >= config.MATCH_LEVEL_THRESHOLDS.MODERATE_MATCH) {
      explanationText = "Sản phẩm ở mức tương đối phù hợp, đáp ứng được một phần nhu cầu theo hồ sơ làm đẹp của bạn.";
    } else {
      explanationText = "Sản phẩm này có thể chưa tối ưu nhất cho loại da và nhu cầu hiện tại của bạn.";
    }
    
    // Fallback if empty array of reasons made it weird
    if (explanationText.includes("vì .")) {
      explanationText = "Sản phẩm phù hợp với hồ sơ làm đẹp của bạn.";
    }

    return {
      status: confidenceScore < 30 ? "PROFILE_INCOMPLETE" : "READY",
      score: confidenceScore < 50 ? 0 : normalizedScore,
      estimated_score: estimatedScore,
      match_level: matchLevel,
      confidence_score: confidenceScore,
      profile_completion_rate: profileCompleteness,
      matching_data_completeness: productCompleteness,
      match_explanation: explanationText,
      reasons,
      cautions,
      hard_conflicts: hardConflicts,
      matched_attributes: [...new Set(matchedAttributes)],
      score_breakdown: scoreBreakdown,
    };
  }

  _insufficientDataResult() {
    return {
      status: "PROFILE_REQUIRED",
      score: 0,
      estimated_score: null,
      match_level: "INSUFFICIENT_DATA",
      confidence_score: 0,
      profile_completion_rate: 0,
      matching_data_completeness: 0,
      match_explanation: "Chưa đủ dữ liệu để đưa ra đánh giá phù hợp.",
      reasons: [],
      cautions: [],
      hard_conflicts: [],
      matched_attributes: [],
      score_breakdown: {}
    };
  }
}

module.exports = new SkinMatchService();
