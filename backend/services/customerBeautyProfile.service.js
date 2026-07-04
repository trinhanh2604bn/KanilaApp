const CustomerBeautyProfile = require("../models/customerBeautyProfile.model");
const CustomerRecommendationSnapshot = require("../models/customerRecommendationSnapshot.model");

class CustomerBeautyProfileService {
  async getProfileByCustomerId(customerId) {
    const profile = await CustomerBeautyProfile.findOne({ customer_id: customerId })
      .populate("preferred_brands")
      .populate("disliked_brands")
      .populate("preferred_categories");
    return profile;
  }

  async upsertProfile(customerId, data) {
    let profile = await CustomerBeautyProfile.findOne({ customer_id: customerId });
    
    let oldHash = null;
    if (profile) {
      oldHash = profile.profile_hash;
    } else {
      profile = new CustomerBeautyProfile({ customer_id: customerId });
    }

    // Update fields
    const safeFields = [
      "skin_type", "skin_concerns", "sensitivity_level", "skin_tone", "undertone",
      "shade_preference", "lip_color_preference", "makeup_style", "beauty_goals",
      "avoid_ingredients", "preferred_ingredients", "budget_range", 
      "preferred_brands", "disliked_brands", "preferred_categories",
      "texture_preference", "finish_preference", "fragrance_preference",
      "purchase_intent", "source"
    ];

    for (const field of safeFields) {
      if (data[field] !== undefined) {
        profile[field] = data[field];
      }
    }

    await profile.save(); // pre-save hook handles profile_completion_rate and profile_hash

    // If profile hash changed, invalidate recommendation snapshot
    if (oldHash && oldHash !== profile.profile_hash) {
      await CustomerRecommendationSnapshot.updateMany(
        { customer_id: customerId }, 
        { invalidated_at: new Date() }
      );
    }

    return profile;
  }
}

module.exports = new CustomerBeautyProfileService();
