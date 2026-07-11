const CustomerBeautyProfile = require("../models/customerBeautyProfile.model");
const CustomerRecommendationSnapshot = require("../models/customerRecommendationSnapshot.model");

// Allowed fields for customer-facing update.
const safeFields = [
  "skin_type", "skin_concerns", "sensitivity_level", "skin_color", "skin_undertone",
  "foundation_finish", "lipstick_colors", "makeup_styles", "budget", "avoid_ingredients",
  "beauty_goals", "preferred_ingredients", "preferred_brands", "disliked_brands",
  "preferred_categories", "texture_preference", "fragrance_preference", "purchase_intent"
];

class CustomerBeautyProfileService {
  async getProfileByCustomerId(customerId) {
    return await CustomerBeautyProfile.findOne({ customer_id: customerId })
      .populate("preferred_brands")
      .populate("disliked_brands")
      .populate("preferred_categories");
  }

  async createProfile(customerId, data, context = {}) {
    const existing = await CustomerBeautyProfile.findOne({ customer_id: customerId });
    if (existing) {
      throw new Error("Profile already exists. Use PATCH to update.");
    }

    const profile = new CustomerBeautyProfile({ customer_id: customerId });
    
    for (const field of safeFields) {
      if (data[field] !== undefined) {
        profile[field] = data[field];
      }
    }

    if (context.source) {
      profile.source = context.source;
    }

    await profile.save();
    return profile;
  }

  async updateProfile(customerId, data, context = {}) {
    const profile = await CustomerBeautyProfile.findOne({ customer_id: customerId });
    if (!profile) {
      throw new Error("Profile not found.");
    }

    const oldHash = profile.profile_hash;

    for (const field of safeFields) {
      if (data[field] !== undefined) {
        profile[field] = data[field];
      }
    }

    if (context.source) {
      profile.source = context.source;
    }

    await profile.save();

    // If profile has changed, invalidate any cached recommendation snapshots
    if (oldHash && oldHash !== profile.profile_hash) {
      await CustomerRecommendationSnapshot.updateMany(
        { customer_id: customerId },
        { invalidated_at: new Date() }
      );
    }

    return profile;
  }

  // Deprecated, keep for internal use if needed but routes should use create/update
  async upsertProfile(customerId, data, context = {}) {
    const existing = await CustomerBeautyProfile.findOne({ customer_id: customerId });
    if (existing) {
      return this.updateProfile(customerId, data, context);
    } else {
      return this.createProfile(customerId, data, context);
    }
  }
}

module.exports = new CustomerBeautyProfileService();
