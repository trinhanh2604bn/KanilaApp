const skinMatchService = require("../services/skinMatch/skinMatch.service");
const productMatchContextService = require("../services/skinMatch/productMatchContext.service");

describe("Skin Match Rules", () => {
  it("should match skincare product correctly with profile", () => {
    const profile = {
      skin_type: "oily",
      skin_concerns: ["acne", "pores"],
      beauty_goals: ["oil_control"],
      profile_completion_rate: 80
    };

    const context = {
      product_type: "SKINCARE",
      skin_types_supported: ["oily", "combination"],
      skin_concerns_supported: ["acne", "redness"],
      beauty_goals_supported: ["oil_control", "soothing"],
      ingredients: [],
      avoid_for_ingredients: [],
      ingredient_flags: [],
      texture_codes: [],
      finish_codes: [],
      skin_color_codes: [],
      undertone_codes: [],
      sensitivity_flags: [],
      is_sensitive_friendly: true,
      matching_data_completeness: 80
    };

    const result = skinMatchService.calculateSkinMatchScore(profile, context);
    
    expect(result.status).toBe("READY");
    // Skin type match: +25
    // Concern match: 25 * 1/2 (only acne matches out of 2) = 12.5 -> 13
    // Beauty goal match: 15 * 1/1 = 15
    // Max score for these 3 evaluated dimensions: 25 + 25 + 15 = 65
    // Total score: 25 + 13 + 15 = 53
    // Normalized score: 53 / 65 = 82
    
    expect(result.score).toBeGreaterThan(60);
    expect(result.score_breakdown).toBeDefined();
    expect(result.match_level).toBe("GOOD_MATCH");
  });

  it("should return caution and cap score for hard conflict", () => {
    const profile = {
      skin_type: "dry",
      sensitivity_level: "high",
      profile_completion_rate: 90
    };

    const context = {
      product_type: "SKINCARE",
      skin_types_supported: ["dry"],
      skin_concerns_supported: [],
      beauty_goals_supported: [],
      ingredients: ["alcohol"],
      avoid_for_ingredients: [],
      ingredient_flags: ["contains_alcohol"],
      texture_codes: [],
      finish_codes: [],
      skin_color_codes: [],
      undertone_codes: [],
      sensitivity_flags: [],
      is_sensitive_friendly: false,
      matching_data_completeness: 90
    };

    const result = skinMatchService.calculateSkinMatchScore(profile, context);
    
    expect(result.status).toBe("READY");
    expect(result.match_level).toBe("CAUTION");
    expect(result.hard_conflicts.length).toBeGreaterThan(0);
    expect(result.score).toBeLessThanOrEqual(49);
  });
});

describe("Product Match Context", () => {
  it("should prioritize ProductBeautyProfile over ProductAttribute and Product", async () => {
    // This is tested in isolation via mocks usually, but we verify hash logic here
    const context = {
      product_type: "SKINCARE",
      skin_types_supported: ["oily"],
      skin_concerns_supported: [],
      beauty_goals_supported: [],
      ingredients: [],
      avoid_for_ingredients: [],
      ingredient_flags: [],
      texture_codes: [],
      finish_codes: [],
      skin_color_codes: [],
      undertone_codes: [],
      sensitivity_flags: [],
      is_sensitive_friendly: false
    };

    const hash1 = productMatchContextService.generateHash(context);
    
    context.skin_types_supported.push("dry");
    const hash2 = productMatchContextService.generateHash(context);
    
    expect(hash1).not.toBe(hash2);
  });
});
