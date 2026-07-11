"use strict";

const { findMakeupProductsPipeline } = require("./makeupRecommendation.service");

const BUNDLE_SLOTS = {
  party: ["Kem lót", "Kem nền", "Che khuyết điểm", "Má hồng", "Son môi", "Xịt khóa nền"],
  daily: ["Cushion", "Má hồng", "Son tint"],
  wedding: ["Kem lót", "Kem nền", "Che khuyết điểm", "Má hồng", "Son môi", "Xịt khóa nền", "Phấn bắt sáng"],
  office: ["Cushion", "Che khuyết điểm", "Son môi"],
  school: ["Cushion", "Son tint"],
};

/**
 * Build a structured makeup bundle based on the occasion.
 * 
 * @param {string} occasion 
 * @param {object} shoppingContext 
 * @returns {Promise<{ slots: object[], total: number, occasion: string }>}
 */
async function buildMakeupBundle(occasion, shoppingContext) {
  const occasionKey = occasion || "daily";
  const slots = BUNDLE_SLOTS[occasionKey] || BUNDLE_SLOTS["daily"];

  const bundle = [];
  let total = 0;
  
  for (const slotName of slots) {
    const slotContext = {
      ...shoppingContext,
      categoryNames: [slotName],
      bundle: true,
    };

    // Use isEventMakeup for event makeup intents to trigger weight override in scorer
    if (["party", "wedding"].includes(occasionKey)) {
      slotContext.isEventMakeup = true;
    }

    try {
      const { products } = await findMakeupProductsPipeline(slotContext, 1);
      
      if (products && products.length > 0) {
        const topProduct = products[0];
        topProduct.slot = slotName;
        bundle.push(topProduct);
        total += topProduct.price || 0;
      }
    } catch (err) {
      console.error(`[MakeupBundle] Error fetching product for slot ${slotName}:`, err.message);
    }
  }

  return { slots: bundle, total, occasion: occasionKey };
}

module.exports = {
  buildMakeupBundle,
};
