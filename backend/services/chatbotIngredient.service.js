/**
 * chatbotIngredient.service.js
 * Handles ingredient intelligence queries for Kanila AI Assistant.
 */

const Product = require("../models/product.model");
const { getCustomerContext } = require("./chatbotCustomerContext.service");
const { getIngredientData, checkCompatibility, normalizeName } = require("./ingredientCompatibility.service");
const { generateIngredientReply } = require("./gemini.provider");

/**
 * Extract an ingredient name from the message.
 * This is a basic NLP implementation for Phase 7A.
 */
function extractIngredientName(message) {
  const lower = message.toLowerCase();
  const knownIngredients = ["niacinamide", "retinol", "aha", "bha", "vitamin c", "b5", "hyaluronic", "centella", "b3", "ha"];
  for (const ing of knownIngredients) {
    if (lower.includes(ing)) return ing;
  }
  return null;
}

/**
 * Extract multiple ingredient names for compatibility check.
 */
function extractMultipleIngredients(message) {
  const lower = message.toLowerCase();
  const knownIngredients = ["niacinamide", "retinol", "aha", "bha", "vitamin c", "b5", "hyaluronic", "centella"];
  const found = [];
  for (const ing of knownIngredients) {
    if (lower.includes(ing)) found.push(ing);
  }
  return found;
}

/**
 * Extract product name roughly.
 */
function extractProductName(message) {
  const lower = message.toLowerCase();
  const productMarkers = ["sản phẩm", "chai", "hũ", "lọ", "tuýp", "kem", "serum", "toner", "sữa rửa mặt"];
  
  let productName = null;
  // If we find a marker, try to take the words after it.
  // In a real NLP we'd do better, but for MVP we search for exact product matches or use regex.
  // Instead of extracting, let's just search the DB directly using words from the message.
  return null; 
}

async function findProductFromMessage(message) {
  const stopWords = ["có", "chứa", "không", "thành", "phần", "gì", "tác", "dụng", "sản", "phẩm", "này", "dùng", "chung", "hợp", "da"];
  const words = message.toLowerCase().replace(/[^\w\sđ]/g, "").split(/\s+/).filter(w => w.length >= 2 && !stopWords.includes(w));
  
  if (words.length === 0) return null;

  const searchRegex = new RegExp(words.join(".*"), "i");
  return Product.findOne({
    isActive: true,
    productStatus: "active",
    productName: { $regex: searchRegex }
  }).lean();
}

/**
 * Handle ingredient_analysis intent
 */
async function handleIngredientAnalysis(message, user, history) {
  let customerProfile = null;
  if (user) {
    // Need customer ID... wait, we can just use the provided user object if it has customer_id.
    // For simplicity, let's load it exactly as other services do.
    const customerId = user.customer_id || (user.account_id ? (await require("../models/customer.model").findOne({ account_id: user.account_id }).lean())?._id : null);
    if (customerId) {
      const ctx = await getCustomerContext(customerId);
      customerProfile = ctx.customer_profile;
    }
  }

  const ingName = extractIngredientName(message);
  const ingData = ingName ? getIngredientData(ingName) : null;
  
  const product = await findProductFromMessage(message);
  
  let productContext = null;
  if (product && ingName) {
    // Check if product contains the ingredient
    const hasIng = (product.key_ingredients && product.key_ingredients.some(i => normalizeName(i) === normalizeName(ingName))) || 
                   (product.ingredientText && product.ingredientText.toLowerCase().includes(ingName.toLowerCase()));
                   
    productContext = {
      name: product.productName,
      has_ingredient: !!hasIng,
      ingredients: product.key_ingredients || []
    };
  }

  let compatibilityContext = null;
  if (ingData && customerProfile && customerProfile.skin_type) {
    const isSuitable = ingData.suitable_skin_types.includes(customerProfile.skin_type);
    compatibilityContext = {
      compatibility: isSuitable ? "suitable" : "warning",
      reason: isSuitable ? `Phù hợp với da ${customerProfile.skin_type}` : `Cần cẩn thận khi dùng cho da ${customerProfile.skin_type}`
    };
  }
  
  if (!ingData && !productContext) {
    return {
      botText: "Bạn muốn tìm hiểu về thành phần nào? (Ví dụ: Niacinamide, Retinol, BHA, Vitamin C...)",
      replyType: "text",
      quickReplies: ["Retinol có tác dụng gì?", "BHA và Retinol", "Tư vấn sản phẩm"]
    };
  }

  const contextObj = {
    ingredient: ingData,
    product: productContext,
    customer: customerProfile ? { skin_type: customerProfile.skin_type, concerns: customerProfile.skin_concerns } : null,
    skin_compatibility: compatibilityContext
  };

  let botReply;
  try {
    botReply = await generateIngredientReply(message, contextObj, history);
  } catch (e) {
    if (ingData) {
      botReply = `${ingData.name} là một thành phần tốt. ${compatibilityContext ? compatibilityContext.reason : ''} ${productContext ? (productContext.has_ingredient ? `Sản phẩm ${productContext.name} có chứa thành phần này.` : `Sản phẩm ${productContext.name} không chứa thành phần này.`) : ''}`;
    } else if (productContext) {
      botReply = `Sản phẩm ${productContext.name} có các thành phần chính: ${productContext.ingredients.join(', ')}.`;
    } else {
      botReply = "Đây là thông tin về thành phần bạn quan tâm.";
    }
  }

  return {
    botText: botReply,
    replyType: "ingredient_analysis",
    ingredientContext: contextObj,
    quickReplies: ["Tìm hiểu thành phần khác", "Sản phẩm chứa " + (ingData ? ingData.name : "thành phần này"), "Tư vấn chăm sóc da"]
  };
}

/**
 * Handle ingredient_compatibility intent
 */
async function handleIngredientCompatibility(message, user, history) {
  const ingredients = extractMultipleIngredients(message);
  
  if (ingredients.length < 2) {
    return {
      botText: "Bạn muốn kiểm tra sự kết hợp của những thành phần nào? (Ví dụ: AHA và Retinol có dùng chung được không?)",
      replyType: "text",
      quickReplies: ["AHA và Retinol", "BHA và Niacinamide", "Vitamin C và Retinol"]
    };
  }

  const result = checkCompatibility(ingredients[0], ingredients[1]);
  
  const contextObj = {
    compatibility_check: {
      ingredient1: getIngredientData(ingredients[0])?.name || ingredients[0],
      ingredient2: getIngredientData(ingredients[1])?.name || ingredients[1],
      level: result.level, // "safe" | "warning" | "avoid"
      reason: result.reason
    }
  };

  let botReply;
  try {
    botReply = await generateIngredientReply(message, contextObj, history);
  } catch (e) {
    botReply = `Mức độ kết hợp giữa ${contextObj.compatibility_check.ingredient1} và ${contextObj.compatibility_check.ingredient2} là ${contextObj.compatibility_check.level}. Lý do: ${contextObj.compatibility_check.reason}`;
  }

  return {
    botText: botReply,
    replyType: "ingredient_analysis", // keep same contract
    ingredientContext: contextObj,
    quickReplies: ["Cách dùng " + (getIngredientData(ingredients[0])?.name || ingredients[0]), "Tìm hiểu thành phần khác", "Tư vấn sản phẩm"]
  };
}

module.exports = {
  handleIngredientAnalysis,
  handleIngredientCompatibility
};
