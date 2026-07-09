const fs = require('fs');
const path = require('path');

const file = path.join(__dirname, '../chatbot.service.js');
let content = fs.readFileSync(file, 'utf8');

// 1. Imports
const importsToAdd = `
// Phase 6 & 7 & 8 & 9
const { handleProductComparison } = require("./chatbotComparison.service");
const { handleIngredientAnalysis, handleIngredientCompatibility } = require("./chatbotIngredient.service");
const { classifyIntent, resolveRoutingIntent } = require("./chatbotIntent.classifier");
const { extractShoppingContext, CANONICAL_CATEGORY_MAP } = require("./chatbotShoppingContext");
const { findMakeupProductsPipeline } = require("./makeupRecommendation.service");
const { buildMakeupProductContextMessage } = require("./chatbot.prompt");
const { generateMakeupReply } = require("./gemini.provider");
const { logRecommendation } = require("./chatbotRecommendationLogger");
`;
if (!content.includes('require("./chatbotIntent.classifier")')) {
  content = content.replace('const MAX_HISTORY_MESSAGES = 10;', importsToAdd + '\nconst MAX_HISTORY_MESSAGES = 10;');
}

// 2. Constants and handlers
const handlersToAdd = `
const INTENT_TO_CATEGORY_HINT = {
  lipstick_recommendation:           ["CAT_LIP_TINT", "CAT_LIPSTICK"],
  cushion_foundation_recommendation: ["CAT_CUSHION", "CAT_FOUNDATION"],
  concealer_recommendation:          ["CAT_CONCEALER"],
  blush_recommendation:              ["CAT_BLUSH"],
  eye_makeup_recommendation:         ["CAT_MASCARA", "CAT_EYELINER", "CAT_EYESHADOW"],
  base_makeup_recommendation:        ["CAT_PRIMER", "CAT_POWDER", "CAT_SETTING_SPRAY"],
  makeup_set_builder:                null,
  event_makeup_look:                 null,
  daily_makeup_look:                 null,
  shade_tone_advice:                 null,
  product_availability:              null,
};

const MAKEUP_RECOMMEND_QUICK_REPLIES = [
  "Thêm vào giỏ hàng",
  "Xem chi tiết sản phẩm",
  "Tìm sản phẩm tương tự",
  "Có voucher makeup không?",
];

const MAKEUP_SET_QUICK_REPLIES = [
  "Thêm toàn bộ vào giỏ",
  "Đổi sản phẩm khác",
  "Tư vấn kỹ hơn về makeup look này"
];

const SHADE_QUICK_REPLIES = [
  "Màu này có hợp da ngăm không?",
  "Có màu nào sáng hơn không?",
  "Thêm vào giỏ hàng"
];

const VOUCHER_QUICK_REPLIES = [
  "Xem tất cả ưu đãi",
  "Mình muốn tìm son đang giảm giá",
  "Cushion bán chạy nhất"
];

async function handleMakeupRecommendation(intent, message, user, history) {
  let customerProfile = null;
  try {
    if (user && user.account_id) {
      const ctx = await getCustomerContext({ accountId: user.account_id });
      customerProfile = ctx?.customer_profile || null;
    }
  } catch (_) {}

  const intentHint = intent;
  let filters = null;
  let products = [];
  let candidateCount = 0;
  let dbFilter = {};

  try {
    let shoppingContext = extractShoppingContext(message, intent);
    
    if ((!shoppingContext.categoryNames || shoppingContext.categoryNames.length === 0) && INTENT_TO_CATEGORY_HINT[intent]) {
      const hintCodes = INTENT_TO_CATEGORY_HINT[intent];
      const fallbackNames = [];
      for (const code of hintCodes) {
        if (CANONICAL_CATEGORY_MAP && CANONICAL_CATEGORY_MAP[code]) {
          fallbackNames.push(...CANONICAL_CATEGORY_MAP[code].names);
        }
      }
      if (fallbackNames.length > 0) {
        shoppingContext.categoryNames = fallbackNames;
      }
    }
    
    const pipelineResult = await findMakeupProductsPipeline(shoppingContext, 5);
    products = pipelineResult.products || [];
    filters = pipelineResult.filters;
    candidateCount = pipelineResult.candidateCount;
    dbFilter = pipelineResult.dbFilter;
    
    try {
      logRecommendation(user?.account_id, intentHint, message, shoppingContext, products, candidateCount);
    } catch(e) {}

    if (process.env.NODE_ENV === "development" || process.env.CHATBOT_DEBUG === "true") {
      const debugLog = {
        USER: message,
        INTENT: intent,
        SHOPPING_CONTEXT: {
          categoryCode: shoppingContext.categoryCode,
          categoryNames: shoppingContext.categoryNames,
          budget: shoppingContext.budget,
          skinType: shoppingContext.skinType,
          occasion: shoppingContext.occasion
        },
        DATABASE_QUERY: dbFilter || {},
        PRODUCT_FOUND: candidateCount || products.length,
        SELECTED_PRODUCTS: products.map(p => ({ name: p.name, score: p.score })),
      };
      console.log("\\n[CHATBOT_DEBUG]\\n" + JSON.stringify(debugLog, null, 2) + "\\n");
    }
  } catch (err) {
    console.error("[MakeupHandler] findMakeupProducts error:", err.message);
  }

  let quickReplies = MAKEUP_RECOMMEND_QUICK_REPLIES;
  if (intent === "makeup_set_builder" || intent === "event_makeup_look") {
    quickReplies = MAKEUP_SET_QUICK_REPLIES;
  }

  const supportActions = ["view_product_detail", "add_to_cart"];
  if (filters?.maxPrice) supportActions.push("filter_by_price");

  let botText;
  try {
    const promptMsg = buildMakeupProductContextMessage(products, message, filters, customerProfile);
    botText = await generateMakeupReply(promptMsg, history);
  } catch (err) {
    if (products.length > 0) {
      const topProduct = products[0];
      botText = \`Mình tìm được \${products.length} sản phẩm phù hợp cho bạn! \` +
        \`Sản phẩm nổi bật: \${topProduct.productName} (\${topProduct.brandName}) - \` +
        \`\${topProduct.price.toLocaleString("vi-VN")}đ. \${topProduct.matchedReason || ''}\`;
    } else {
      botText = "Mình không tìm thấy sản phẩm phù hợp trong danh mục hiện tại. " +
        "Bạn có thể thử điều chỉnh ngân sách hoặc loại sản phẩm khác nhé!";
    }
  }

  return {
    botText,
    products,
    filters,
    quickReplies,
    replyType: "makeup_recommendation",
    supportActions,
  };
}

async function handleVoucherQuery(message, history) {
  return { botText: "Bạn có thể tham khảo mục Ưu Đãi trên app Kanila để xem các mã voucher hiện có nhé." };
}

`;
if (!content.includes('async function handleMakeupRecommendation')) {
  content = content.replace('async function handleUserMessage', handlersToAdd + '\nasync function handleUserMessage');
}

// 3. handleUserMessage body replacements
const handleUserMessageRegex = /async function handleUserMessage\(\{ sessionId, message, sourceScreen, user, productIds, cartItems \}\) \{([\s\S]*?)return \{/m;
const handleUserMessageMatch = content.match(handleUserMessageRegex);

if (handleUserMessageMatch) {
  let inner = handleUserMessageMatch[1];
  
  // Replace intent detection
  inner = inner.replace(
    /const intent = detectIntent\(message\);/g,
    \`let parsedQuickReply = null;
  try {
    if (message && message.trim().startsWith("{") && message.trim().endsWith("}")) {
      const payload = JSON.parse(message);
      if (payload.action && payload.action.type) {
        parsedQuickReply = payload;
        message = payload.text || message;
      }
    }
  } catch (e) {}

  let classification = { intent: "find_product", needsClarification: false };
  let fallbackIntent = "find_product";
  
  if (parsedQuickReply && parsedQuickReply.action.type === "PRODUCT_SEARCH") {
    classification.intent = parsedQuickReply.action.category || "find_product";
    fallbackIntent = classification.intent;
    classification.needsClarification = false;
  } else {
    classification = classifyIntent(message.trim());
    fallbackIntent = detectIntent(message);
  }

  const intent = resolveRoutingIntent(classification.intent, fallbackIntent);

  const shoppingContext = extractShoppingContext(message, intent);
  const DIRECT_SEARCH_INTENTS = [
    "cushion_foundation_recommendation",
    "lipstick_recommendation",
    "mascara_recommendation",
    "eye_makeup_recommendation",
    "blush_recommendation",
    "concealer_recommendation",
    "base_makeup_recommendation",
    "makeup_set_builder",
    "event_makeup_look",
    "daily_makeup_look",
    "shade_tone_advice"
  ];
  
  let needsClarification = classification.needsClarification;
  if (shoppingContext.categoryNames && shoppingContext.categoryNames.length > 0) needsClarification = false;
  if (shoppingContext.occasion) needsClarification = false;
  if (DIRECT_SEARCH_INTENTS.includes(intent)) needsClarification = false;

  if (needsClarification && classification.clarificationPrompt && !["find_product", "order_tracking", "support_ticket"].includes(intent)) {
    const history = await buildGeminiHistory(session._id);
    await ChatbotMessage.create({
      session_id: session._id,
      sender_type: "user",
      message_text: message.trim(),
      intent,
      response_type: "text",
    });
    const botText = classification.clarificationPrompt.text;
    const quickReplies = classification.clarificationPrompt.quickReplies;
    await ChatbotMessage.create({
      session_id: session._id,
      sender_type: "bot",
      message_text: botText,
      intent,
      response_type: "text",
      metadata: { reply_type: "text", quick_replies: quickReplies }
    });
    return {
      session_id: session._id,
      bot_message: botText,
      products: [], quick_replies: quickReplies,
      reply_type: "text", handoff_required: false, customer_context_used: false,
    };
  }\`
  );

  // Add branches to intent branching
  const branchesToAdd = \`
    } else if (intent === "product_comparison") {
      const r = await handleProductComparison(message.trim(), user, pendingProductIds, history);
      botText = r.botText; comparison = r.comparison || null; quickReplies = r.quickReplies; replyType = r.replyType;
    } else if (intent === "ingredient_analysis") {
      const r = await handleIngredientAnalysis(message.trim(), user, history);
      botText = r.botText; ingredientContext = r.ingredientContext || null; quickReplies = r.quickReplies; replyType = r.replyType;
    } else if (intent === "ingredient_compatibility") {
      const r = await handleIngredientCompatibility(message.trim(), user, history);
      botText = r.botText; ingredientContext = r.ingredientContext || null; quickReplies = r.quickReplies; replyType = r.replyType;
    } else if (DIRECT_SEARCH_INTENTS.includes(intent)) {
      const r = await handleMakeupRecommendation(intent, message.trim(), user, history);
      botText = r.botText; products = r.products || []; quickReplies = r.quickReplies; replyType = r.replyType; makeupFilters = r.filters || null; supportActions = r.supportActions || [];
    } else if (intent === "voucher_promotion_question") {
      const r = await handleVoucherQuery(message.trim(), history);
      botText = r.botText; quickReplies = VOUCHER_QUICK_REPLIES; replyType = "text"; supportActions = ["open_voucher_wallet"];
    } else if (intent === "product_availability") {
      const r = await handleMakeupRecommendation(intent, message.trim(), user, history);
      botText = r.botText; products = r.products || []; quickReplies = MAKEUP_RECOMMEND_QUICK_REPLIES; replyType = r.replyType; makeupFilters = r.filters || null;
  \`;
  
  if (!inner.includes('intent === "product_comparison"')) {
    inner = inner.replace('} else if (intent === "product_recommendation") {', branchesToAdd + '\\n    } else if (intent === "product_recommendation") {');
  }

  // add variables at top of Branch by intent
  if (!inner.includes('let comparison = null;')) {
    inner = inner.replace('let needsVariantSelection = false;', 'let needsVariantSelection = false;\\n  let comparison = null;\\n  let ingredientContext = null;\\n  let makeupFilters = null;\\n  let supportActions = [];');
  }
  
  // replace the inner string back
  content = content.replace(handleUserMessageMatch[1], inner);
  
  // update the return object
  content = content.replace('upsell_products: upsellProducts,', 'upsell_products: upsellProducts,\\n    comparison: comparison,\\n    ingredient_context: ingredientContext,\\n    filters: makeupFilters,\\n    support_actions: supportActions,');
  content = content.replace('metadata: {\\n      reply_type', 'metadata: {\\n      comparison,\\n      ingredient_context: ingredientContext,\\n      filters: makeupFilters,\\n      support_actions: supportActions,\\n      reply_type');
}

fs.writeFileSync(file, content, 'utf8');
console.log('Restored chatbot.service.js successfully.');
