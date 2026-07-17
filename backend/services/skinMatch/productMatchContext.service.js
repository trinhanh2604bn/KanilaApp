const crypto = require("crypto");
const Product = require("../../models/product.model");
const ProductAttribute = require("../../models/productAttribute.model");
const ProductBeautyProfile = require("../../models/productBeautyProfile.model");

class ProductMatchContextService {
  async getContext(productId) {
    // 1. Fetch data
    const product = await Product.findById(productId)
      .populate("categoryId")
      .lean();
    if (!product) {
      throw new Error("Product not found");
    }

    const beautyProfile = await ProductBeautyProfile.findOne({ product_id: productId }).lean();
    const attributes = await ProductAttribute.find({ productId }).lean();
    const attrMap = new Map();
    attributes.forEach(a => attrMap.set(a.attributeName, a.attributeValue));

    // 2. Build canonical context, prioritizing ProductBeautyProfile > ProductAttribute > Product
    const context = {
      product_id: productId,
      product_type: this._getProductType(product.categoryId?.categoryCode || ""),
      skin_types_supported: beautyProfile?.suitable_skin_types?.length ? beautyProfile.suitable_skin_types : (this._parseArrayAttr(attrMap.get("skin_types")) || product.skin_types_supported || []),
      skin_concerns_supported: beautyProfile?.suitable_skin_concerns?.length ? beautyProfile.suitable_skin_concerns : (this._parseArrayAttr(attrMap.get("concerns")) || product.concerns_targeted || []),
      beauty_goals_supported: beautyProfile?.supported_beauty_goals?.length ? beautyProfile.supported_beauty_goals : (this._parseArrayAttr(attrMap.get("beauty_goals")) || []),
      ingredients: beautyProfile?.key_ingredients?.length ? beautyProfile.key_ingredients : (this._parseArrayAttr(attrMap.get("ingredients")) || product.key_ingredients || []),
      avoid_for_ingredients: beautyProfile?.avoid_for_ingredients?.length ? beautyProfile.avoid_for_ingredients : [],
      ingredient_flags: this._parseArrayAttr(attrMap.get("ingredient_flags")) || product.ingredient_flags || [],
      texture_codes: beautyProfile?.texture ? [beautyProfile.texture] : (this._parseArrayAttr(attrMap.get("texture")) || []),
      finish_codes: beautyProfile?.finish ? [beautyProfile.finish] : (attrMap.get("finish") ? [attrMap.get("finish")] : (product.finish_type ? [product.finish_type] : [])),
      skin_color_codes: beautyProfile?.suitable_skin_tones?.length ? beautyProfile.suitable_skin_tones : (this._parseArrayAttr(attrMap.get("skin_tones")) || product.tone_match_supported || []),
      undertone_codes: beautyProfile?.suitable_undertones?.length ? beautyProfile.suitable_undertones : (this._parseArrayAttr(attrMap.get("undertones")) || []),
      sensitivity_flags: beautyProfile?.suitable_sensitivity_levels?.length ? beautyProfile.suitable_sensitivity_levels : [],
      is_sensitive_friendly: beautyProfile?.suitable_sensitivity_levels?.includes("sensitive") || product.is_sensitive_friendly || false,
      ingredient_data_complete: !!beautyProfile?.key_ingredients?.length || !!product.key_ingredients?.length || !!attrMap.get("ingredients"),
    };

    // Calculate completeness
    let populatedFields = 0;
    const targetFields = ["skin_types_supported", "skin_concerns_supported", "beauty_goals_supported", "ingredients", "texture_codes", "finish_codes", "skin_color_codes", "undertone_codes"];
    for (const field of targetFields) {
      if (context[field] && context[field].length > 0) {
        populatedFields++;
      }
    }
    context.matching_data_completeness = Math.round((populatedFields / targetFields.length) * 100);

    return context;
  }

  generateHash(context) {
    const hashObj = {
      product_type: context.product_type,
      skin_types_supported: [...context.skin_types_supported].sort(),
      skin_concerns_supported: [...context.skin_concerns_supported].sort(),
      beauty_goals_supported: [...context.beauty_goals_supported].sort(),
      ingredients: [...context.ingredients].sort(),
      avoid_for_ingredients: [...context.avoid_for_ingredients].sort(),
      ingredient_flags: [...context.ingredient_flags].sort(),
      texture_codes: [...context.texture_codes].sort(),
      finish_codes: [...context.finish_codes].sort(),
      skin_color_codes: [...context.skin_color_codes].sort(),
      undertone_codes: [...context.undertone_codes].sort(),
      sensitivity_flags: [...context.sensitivity_flags].sort(),
      is_sensitive_friendly: context.is_sensitive_friendly,
    };
    
    const sortedKeys = Object.keys(hashObj).sort();
    const canonicalObj = {};
    for (const k of sortedKeys) {
      canonicalObj[k] = hashObj[k];
    }
    const hashString = JSON.stringify(canonicalObj);
    return crypto.createHash("md5").update(hashString).digest("hex");
  }

  _getProductType(categoryCode) {
    const code = String(categoryCode).toLowerCase();
    // Full list of makeup-related category codes to ensure correct weight set is applied
    const makeupKeywords = [
      "makeup", "make_up", "make-up",
      "foundation", "bb_cream", "cc_cream", "cushion",
      "lipstick", "lip_gloss", "lip_liner", "lip_tint", "lip_balm",
      "mascara", "eyeliner", "eyeshadow", "eyebrow",
      "blush", "blusher", "contour", "bronzer", "highlighter",
      "concealer", "color_corrector", "primer",
      "setting_powder", "setting_spray", "face_powder",
    ];
    if (makeupKeywords.some((kw) => code.includes(kw))) return "MAKEUP";
    return "SKINCARE";
  }

  _parseArrayAttr(val) {
    if (!val) return null;
    return val.split(",").map(v => v.trim()).filter(Boolean);
  }
}

module.exports = new ProductMatchContextService();
