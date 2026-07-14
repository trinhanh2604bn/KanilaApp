const CustomerBeautyProfile = require("../models/customerBeautyProfile.model");
const CustomerRecommendationSnapshot = require("../models/customerRecommendationSnapshot.model");
const skinMatchCacheService = require("./skinMatch/skinMatchCache.service");
const WishlistItem = require("../models/wishlistItem.model");
const CartItem = require("../models/cartItem.model");
const Cart = require("../models/cart.model");
const Wishlist = require("../models/wishlist.model");

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

    // If profile has changed, invalidate any cached recommendation snapshots and skin match cache
    if (oldHash && oldHash !== profile.profile_hash) {
      await CustomerRecommendationSnapshot.updateMany(
        { customer_id: customerId },
        { invalidated_at: new Date() }
      );
      
      await skinMatchCacheService.invalidateByCustomerId(customerId);
      
      // Best-effort warm-up in background
      this._warmupSkinMatchAsync(customerId, profile).catch(e => console.error("Skin Match Warmup failed:", e.message));
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

  async _warmupSkinMatchAsync(customerId, profile) {
    const productIds = new Set();
    
    try {
      // 1. Get from wishlist
      const userWishlist = await Wishlist.findOne({ customer_id: customerId }).lean();
      if (userWishlist) {
        const wishlistItems = await WishlistItem.find({ wishlistId: userWishlist._id }).lean();
        wishlistItems.forEach(item => productIds.add(String(item.productId)));
      }

      // 2. Get from cart
      const userCart = await Cart.findOne({ customer_id: customerId }).lean();
      if (userCart) {
        const cartItems = await CartItem.find({ cart_id: userCart._id }).lean();
        cartItems.forEach(item => {
          if (item.product_id) productIds.add(String(item.product_id));
        });
      }

      // 3. Get recommendations
      const recSnapshots = await CustomerRecommendationSnapshot.find({ customer_id: customerId }).lean();
      for (const snap of recSnapshots) {
        if (snap.product_ids) {
          snap.product_ids.forEach(id => productIds.add(String(id)));
        }
      }

      // Pick top 10 products
      const limit = 10;
      const targetProductIds = Array.from(productIds).slice(0, limit);

      for (const productId of targetProductIds) {
        try {
          await skinMatchCacheService.getOrComputeMatch({ _id: customerId }, profile, productId);
        } catch (err) {
          console.error(`Warmup failed for product ${productId}:`, err.message);
        }
      }
    } catch (e) {
      console.error("Error during skin match warmup:", e);
    }
  }
}

module.exports = new CustomerBeautyProfileService();
