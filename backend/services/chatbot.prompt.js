/**
 * chatbot.prompt.js
 * System instruction for the Kanila AI Assistant.
 * Covers: general chat, product recommendation (with personalization),
 *         order tracking, support ticket.
 */

const KANILA_SYSTEM_PROMPT = `Bạn là KANILA BEAUTY ADVISOR AI - chuyên gia tư vấn làm đẹp, chuyên gia trang điểm (makeup expert), và trợ lý mua sắm thân thiện của Kanila.
Mục tiêu của bạn là giúp khách hàng đưa ra quyết định mua sắm chứ không chỉ đơn thuần liệt kê sản phẩm. Mọi câu trả lời của bạn phải mang lại cảm giác như một cuộc trò chuyện thực tế với một nhân viên tư vấn mỹ phẩm tận tâm.

=========================================
1. CẤU TRÚC CÂU TRẢ LỜI CHÍNH (MAIN RESPONSE STYLE)
=========================================
Trạng thái hiện tại: Việc chỉ trả lời "Dưới đây là các sản phẩm..." có cảm giác giống robot.
Cấu trúc phản hồi MỚI bắt buộc:
1. Xác nhận nhu cầu của khách hàng.
2. Giải thích lý do dưới góc độ làm đẹp (beauty reasoning).
3. Gợi ý các sản phẩm.
4. Giải thích chi tiết tại sao từng sản phẩm lại phù hợp.
5. Đặt một câu hỏi nối tiếp (follow-up question) tự nhiên.

Ví dụ Tốt:
"Nếu da bạn thiên dầu, mình sẽ ưu tiên cushion có khả năng kiềm dầu, lớp nền mỏng nhẹ và không dễ xuống tone sau vài tiếng. Mình nghĩ bạn có thể tham khảo 3 lựa chọn này:
1. Cushion A
   - Điểm mạnh: Finish semi-matte, kiềm dầu tốt
   - Hợp với: Makeup đi học/đi làm
..."

=========================================
2. QUY TẮC GỢI Ý SẢN PHẨM (PRODUCT RECOMMENDATION RULE)
=========================================
KHÔNG BAO GIỜ chỉ xuất ra mỗi tên sản phẩm.
Đối với MỖI sản phẩm được gợi ý, bạn PHẢI tạo ra:
- Tại sao lại gợi ý (Why recommended)
- Điểm mạnh (Strength)
- Điểm yếu/Đánh đổi (Weakness/trade-off)
- Trường hợp sử dụng tốt nhất (Best use case)
- Loại khách hàng phù hợp (Suitable customer type)

Khi người dùng đã cung cấp ý định mua sắm (VD: "Mình muốn tìm kem nền"), TUYỆT ĐỐI KHÔNG HỎI LẠI: "Bạn đang tìm sản phẩm trang điểm nào?". Lập tức gợi ý sản phẩm ngay. Bất kỳ câu hỏi đào sâu nào (loại da, ngân sách) chỉ được hỏi SAU KHI đã đưa ra danh sách gợi ý.

=========================================
3. CHẾ ĐỘ SO SÁNH SẢN PHẨM (PRODUCT COMPARISON MODE)
=========================================
Hỗ trợ so sánh tự nhiên. Nếu người dùng nói "So sánh cái này với cái kia", "Loại nào hơn?", "Cái nào hợp mình hơn?", "Chọn giúp mình"... 
QUAN TRỌNG: Nếu khách hàng dùng các từ như "cái này", "hai cái trên", "sản phẩm lúc nãy" -> BẮT BUỘC sử dụng lịch sử trò chuyện. KHÔNG yêu cầu họ phải nói lại tên sản phẩm. So sánh rõ ràng về hiệu quả, tone màu, giá cả và mục đích sử dụng.

=========================================
4. BỘ NHỚ TRÒ CHUYỆN (CONVERSATION MEMORY)
=========================================
Duy trì ngữ cảnh trò chuyện trước đó (sản phẩm, danh mục, loại da, ngân sách, dịp...).
Giải quyết các đại từ tham chiếu. Ví dụ: Nếu khách hỏi "Cây thứ 2 có lâu trôi không?", bạn phải trả lời về sản phẩm thứ 2.
TUYỆT ĐỐI KHÔNG hỏi: "Bạn đang nói sản phẩm nào?" nếu trong ngữ cảnh đã có. Khi có KANILA_PREVIOUS_CONTEXT, bạn đang tiếp tục tư vấn từ cuộc trò chuyện trước. KHÔNG hỏi lại thông tin đã biết.

=========================================
5. TƯ DUY CHUYÊN GIA MAKEUP (MAKEUP EXPERT REASONING)
=========================================
Khi gợi ý makeup, hãy cân nhắc:
- Da: Dầu, khô, hỗn hợp, nhạy cảm. (Da dầu -> kiềm dầu, matte; Da khô -> ẩm, glowy; Da mụn -> mỏng nhẹ, concealer; Da nhạy cảm -> gợi ý thử trước).
- Phong cách Makeup: Tự nhiên kiểu Hàn, Tây sắc sảo, soft glam, makeup hàng ngày, đi làm, đi tiệc.
- Đặc tính sản phẩm: Độ che phủ, finish, độ bám, dải màu, kết cấu, giá cả.
- Dịp sử dụng: Đi học, đi làm, hẹn hò, đám cưới, sự kiện.

=========================================
6. KIẾN THỨC BÊN NGOÀI & TỪ CƠ SỞ DỮ LIỆU
=========================================
- CHỈ giới thiệu các sản phẩm có trong ngữ cảnh (KANILA_PRODUCT_CONTEXT / KANILA_MAKEUP_PRODUCT_CONTEXT).
- KHÔNG TỰ BỊA RA: Sản phẩm Kanila giả, giá giả, tồn kho giả, hoặc thông tin ngoài ngữ cảnh.
- Đối với kiến thức Beauty: BẠN ĐƯỢC PHÉP dùng kiến thức makeup chung (VD: giải thích finish matte vs dewy, cushion vs foundation, các bước makeup).
- Nếu sản phẩm KHÔNG có trong Kanila, hãy nói rõ: "Sản phẩm này hiện mình chưa thấy trong danh mục Kanila, nhưng về mặt makeup thì đây là một lựa chọn được nhiều người yêu thích."

=========================================
7. ĐỘ DÀI CÂU TRẢ LỜI & CÂU HỎI TIẾP NỐI
=========================================
- QUAN TRỌNG: Giữ câu trả lời NGẮN GỌN, tối đa 100-120 từ. Không lan man, không giải thích dài dòng.
- Tránh: Liệt kê quá nhiều sản phẩm một lúc. Chỉ gợi ý 1-2 sản phẩm nổi bật nhất.
- Lý tưởng: 2-3 câu giải thích + sản phẩm chính + 1 câu hỏi tiếp nối.
- Câu hỏi tiếp nối (Follow-up): Luôn kết thúc bằng 1 câu hỏi ngắn hữu ích (VD: "Bạn thích nền lì hay căng bóng?", "Muốn thêm vào giỏ không?").
- KHÔNG hỏi lại những thông tin khách đã cung cấp.

=========================================
8. CÁC TRƯỜNG HỢP ĐẶC BIỆT
=========================================
- Khách nói: "Tư vấn giúp mình sản phẩm phù hợp" -> Hãy đáp: "Mình có thể giúp bạn chọn makeup phù hợp. Cho mình biết thêm một chút nhé: 1. Bạn muốn makeup cho dịp nào? 2. Loại da của bạn? 3. Khoảng ngân sách?"
- Khách nói: "Sản phẩm nào tốt nhất?" -> Hãy giải thích: "Không có sản phẩm tốt nhất cho tất cả mọi người, mình sẽ chọn theo nhu cầu..."

=========================================
9. CÁC NGHIỆP VỤ KHÁC (ĐƠN HÀNG, GIỎ HÀNG, HỖ TRỢ, THÀNH PHẦN)
=========================================
- Hỗ trợ giỏ hàng (KANILA_CART_CONTEXT): Khuyến khích khách hàng kiểm tra giỏ hàng và hoàn tất thanh toán. Gợi ý các sản phẩm đi kèm nếu phù hợp.
- Phân tích thành phần (KANILA_INGREDIENT_CONTEXT): Giải thích thành phần dưới góc độ làm đẹp.
- Không chẩn đoán y tế: Bạn là trợ lý mua sắm, không phải bác sĩ. Nếu khách hàng đề cập kích ứng nặng, khuyên họ ngừng sử dụng và liên hệ bác sĩ hoặc CSKH.

TRẢI NGHIỆM CUỐI CÙNG phải giống như một chuyên gia tư vấn sắc đẹp (Sephora Beauty Advisor), chứ KHÔNG PHẢI là một kết quả tìm kiếm Google.
Luôn trả lời bằng Tiếng Việt.`;


// ─────────────────────────────────────────────────────────────────────────────
// Context builders
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Build a personalized product-context message string for Gemini.
 * Includes CUSTOMER_CONTEXT when available for personalized explanation.
 *
 * @param {object[]} products          — normalized products
 * @param {string} userMessage         — raw user message
 * @param {object|null} customerProfile — from getCustomerContext().customer_profile
 * @returns {string}
 */
function buildProductContextMessage(products, userMessage, customerProfile = null) {
  const parts = [];

  // Inject customer context FIRST so Gemini personalizes explanation
  if (customerProfile && (customerProfile.skin_type || customerProfile.skin_concerns?.length)) {
    const ctxLines = [];
    if (customerProfile.skin_type) ctxLines.push(`Loại da: ${customerProfile.skin_type}`);
    if (customerProfile.skin_concerns?.length)
      ctxLines.push(`Vấn đề da: ${customerProfile.skin_concerns.join(", ")}`);
    if (customerProfile.budget_max)
      ctxLines.push(`Ngân sách tối đa: ${customerProfile.budget_max.toLocaleString("vi-VN")}đ`);
    if (customerProfile.preferred_brands?.length)
      ctxLines.push(`Thương hiệu yêu thích: ${customerProfile.preferred_brands.slice(0, 3).join(", ")}`);
    parts.push(`CUSTOMER_CONTEXT:\n${ctxLines.join("\n")}`);
  }

  // Product list
  if (!products || products.length === 0) {
    parts.push("KANILA_PRODUCT_CONTEXT: []");
  } else {
    const productSummary = products.map((p) => ({
      name: p.name,
      brand: p.brand_name || "Không rõ",
      price: p.price ? p.price.toLocaleString("vi-VN") + "đ" : "Liên hệ",
      category: p.category_name || "",
      attributes: p.score_reasons?.length ? p.score_reasons.join(", ") : (p.reason || "")
    }));
    parts.push(`KANILA_PRODUCT_CONTEXT:\n${JSON.stringify(productSummary, null, 2)}`);
  }

  return `${userMessage}\n\n${parts.join("\n\n")}`;
}

/**
 * Build a missing-info question message for Gemini.
 * Used when the customer profile is incomplete — ask exactly ONE question.
 *
 * @param {string} missingField — the first missing field (e.g. "skin_type")
 * @param {string} userMessage
 * @returns {string}
 */
function buildMissingInfoMessage(missingField, userMessage) {
  const questions = {
    skin_type:
      "Để tư vấn chính xác hơn, bạn có thể cho mình biết loại da của bạn không? " +
      "Gợi ý: Da dầu / Da khô / Da hỗn hợp / Da nhạy cảm / Da thường",
    skin_concerns:
      "Bạn đang gặp vấn đề gì về da mà muốn cải thiện? " +
      "Ví dụ: Mụn, Thâm, Xỉn màu, Khô ráp, Lão hóa...",
    budget:
      "Bạn thường chi bao nhiêu cho một sản phẩm chăm sóc da? " +
      "Ví dụ: Dưới 200k / 200k-500k / Trên 500k",
  };

  const instruction = questions[missingField] ||
    "Bạn có thể cho mình biết thêm thông tin về nhu cầu của bạn không?";

  return `${userMessage}\n\nKANILA_CUSTOMER_QUESTION: ${instruction}`;
}

/**
 * Build an order-context message string for Gemini.
 * @param {object} order — normalized order
 * @param {string} userMessage
 * @returns {string}
 */
function buildOrderContextMessage(order, userMessage) {
  if (!order) return `${userMessage}\n\nKANILA_ORDER_CONTEXT: null`;

  const lines = [
    `Mã đơn hàng: ${order.order_code}`,
    `Trạng thái đơn: ${order.status_label}`,
    `Trạng thái giao hàng: ${order.fulfillment_status_label}`,
    `Trạng thái thanh toán: ${order.payment_status_label}`,
    order.total_amount != null
      ? `Tổng tiền: ${order.total_amount.toLocaleString("vi-VN")}đ`
      : null,
    `Số lượng sản phẩm: ${order.items_count}`,
    order.items_preview?.length
      ? `Sản phẩm: ${order.items_preview.map((i) => `${i.name} x${i.quantity}`).join(", ")}`
      : null,
    `Đặt lúc: ${order.created_at ? new Date(order.created_at).toLocaleDateString("vi-VN") : "Không rõ"}`,
    `Hành động tiếp theo: ${order.next_action}`,
    `Lịch sử trạng thái: ${order.timeline
      .slice(-3)
      .map((t) => `${t.label} (${t.time ? new Date(t.time).toLocaleDateString("vi-VN") : "?"})`)
      .join(" → ")}`,
  ].filter(Boolean);

  return `${userMessage}\n\nKANILA_ORDER_CONTEXT:\n${lines.join("\n")}`;
}

/**
 * Build a ticket-context message string for Gemini.
 * @param {object} ticket — normalized ticket
 * @param {string} userMessage
 * @returns {string}
 */
function buildTicketContextMessage(ticket, userMessage) {
  if (!ticket) return `${userMessage}\n\nKANILA_TICKET_CONTEXT: null`;

  const lines = [
    `Mã ticket: ${ticket.ticket_code}`,
    `Loại hỗ trợ: ${ticket.category_label}`,
    `Trạng thái: ${ticket.status_label}`,
    `Ưu tiên: ${ticket.priority === "high" ? "Cao" : "Bình thường"}`,
    `Ngày tạo: ${ticket.created_at ? new Date(ticket.created_at).toLocaleDateString("vi-VN") : "Vừa tạo"}`,
  ];

  return `${userMessage}\n\nKANILA_TICKET_CONTEXT:\n${lines.join("\n")}`;
}

module.exports = {
  KANILA_SYSTEM_PROMPT,
  buildProductContextMessage,
  buildMissingInfoMessage,
  buildOrderContextMessage,
  buildTicketContextMessage,
  buildCartRecommendationMessage,
  buildAddToCartMessage,
  buildCartSummaryMessage,
  // Phase 5 shopping assistant
  buildBeautyConsultationMessage,
  buildComboRecommendationMessage,
  // Phase 6A product comparison
  buildProductComparisonMessage,
  // Phase 7A ingredient intelligence
  buildIngredientMessage,
  // Phase 8: Makeup Commerce
  buildMakeupProductContextMessage,
  // AI Skin Analysis
  buildSkinAnalysisPrompt,
};

function buildSkinAnalysisPrompt(profile, products) {
  const profileDetails = JSON.stringify({
    skin_type: profile.skin_type || profile.skin_types?.[0],
    skin_concerns: profile.skin_concerns || profile.concerns,
    sensitivity_level: profile.sensitivity_level,
    beauty_goals: profile.beauty_goals || profile.routine_goal,
    avoid_ingredients: profile.avoid_ingredients
  }, null, 2);

  const productList = products.map(p => p.name || p.productName).join(", ");

  return `Bạn là chuyên gia da liễu KANILA. Dựa vào hồ sơ làn da sau:
${profileDetails}

Và các sản phẩm gợi ý: ${productList}

Hãy phân tích ngắn gọn tình trạng da hiện tại (analysis_text), đánh giá điểm sức khỏe làn da từ 1-100 (health_score) dựa trên mức độ nghiêm trọng của vấn đề da, và gợi ý từ 3-5 thành phần mỹ phẩm lý tưởng (ideal_ingredients) cho người dùng này.
Yêu cầu định dạng trả về bắt buộc phải là JSON hợp lệ, không kèm theo Markdown block (không dùng \`\`\`json) hay ký tự giải thích dư thừa:
{
  "health_score": 85,
  "analysis_text": "Da bạn thuộc loại... cần chú ý...",
  "ideal_ingredients": ["Niacinamide", "BHA"]
}`;
}

// ─────────────────────────────────────────────────────────────────────────────
// Phase 8: Makeup Commerce context builder
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Build a makeup-specific product context message for Gemini.
 * Includes customer profile, active filters, and product list.
 *
 * @param {object[]} products          — normalized makeup products from findMakeupProducts()
 * @param {string}   userMessage       — raw user message
 * @param {object}   filters           — parsed filters from findMakeupProducts()
 * @param {object|null} customerProfile — from getCustomerContext()
 * @param {boolean}  isFollowUp        — whether this is a follow up message
 * @returns {string}
 */
function buildMakeupProductContextMessage(products, userMessage, filters = {}, customerProfile = null, isFollowUp = false) {
  const parts = [];

  if (isFollowUp) {
    parts.push(`KANILA_PREVIOUS_CONTEXT: true`);
  }

  // Customer context (optional personalization)
  if (customerProfile) {
    const ctxLines = [];
    if (customerProfile.skin_type) ctxLines.push(`Loại da: ${customerProfile.skin_type}`);
    if (customerProfile.skin_concerns?.length)
      ctxLines.push(`Vấn đề da: ${customerProfile.skin_concerns.join(", ")}`);
    if (customerProfile.budget_max)
      ctxLines.push(`Ngân sách tối đa: ${customerProfile.budget_max.toLocaleString("vi-VN")}đ`);
    if (customerProfile.preferred_brands?.length)
      ctxLines.push(`Thương hiệu yêu thích: ${customerProfile.preferred_brands.slice(0, 3).join(", ")}`);
    if (ctxLines.length) parts.push(`CUSTOMER_CONTEXT:\n${ctxLines.join("\n")}`);
  }

  // Active search filters
  const filterLines = [];
  if (filters.category) filterLines.push(`Danh mục: ${filters.category}`);
  if (filters.finish) filterLines.push(`Finish: ${filters.finish}`);
  if (filters.tone) filterLines.push(`Tông da: ${filters.tone}`);
  if (filters.occasion) filterLines.push(`Dịp: ${filters.occasion}`);
  if (filters.maxPrice) filterLines.push(`Ngân sách tối đa: ${filters.maxPrice.toLocaleString("vi-VN")}đ`);
  if (filters.minPrice) filterLines.push(`Ngân sách tối thiểu: ${filters.minPrice.toLocaleString("vi-VN")}đ`);
  if (filters.useCaseHints?.waterproof) filterLines.push("Yêu cầu: chống nước / chống lem");
  if (filters.useCaseHints?.longWear) filterLines.push("Yêu cầu: lâu trôi");
  if (filterLines.length) parts.push(`KANILA_SEARCH_FILTERS:\n${filterLines.join("\n")}`);

  // Product list
  if (!products || products.length === 0) {
    parts.push("KANILA_MAKEUP_PRODUCT_CONTEXT: []");
  } else {
    const productLines = products.map((p, i) => {
      const lines = [
        `Sản phẩm ${i + 1}: ${p.productName}`,
        `  Thương hiệu: ${p.brandName || "Không rõ"}`,
        `  Danh mục: ${p.categoryName || ""}`,
        `  Giá: ${p.price ? p.price.toLocaleString("vi-VN") + "đ" : "Liên hệ"}`,
        p.compareAtPrice ? `  Giá gốc: ${p.compareAtPrice.toLocaleString("vi-VN")}đ` : null,
        p.finish_type ? `  Finish: ${p.finish_type}` : null,
        p.shades?.length
          ? `  Màu sắc: ${p.shades.map((s) => s.name).join(", ")}`
          : null,
        p.rating ? `  Đánh giá: ${p.rating}★ (${p.reviewCount || 0} reviews)` : null,
        `  Tình trạng: ${
          p.stockStatus === "in_stock" ? "Còn hàng" :
          p.stockStatus === "low_stock" ? "Sắp hết hàng" :
          p.stockStatus === "out_of_stock" ? "Hết hàng" : "Chưa xác định"
        }`,
        p.matchedReason ? `  Lý do phù hợp: ${p.matchedReason}` : null,
        p.suggestedUse ? `  Cách dùng gợi ý: ${p.suggestedUse}` : null,
      ].filter(Boolean);
      return lines.join("\n");
    });
    parts.push(`KANILA_MAKEUP_PRODUCT_CONTEXT:\n${productLines.join("\n\n")}`);
  }

  return `${userMessage}\n\n${parts.join("\n\n")}`;
}



// ─────────────────────────────────────────────────────────────────────────────
// Cart context builders (Phase 5A)
// ─────────────────────────────────────────────────────────────────────────────

function buildCartRecommendationMessage(products, upsellProducts, customerProfile, userMessage) {
  const parts = [];

  if (customerProfile && (customerProfile.skin_type || customerProfile.skin_concerns?.length)) {
    const ctxLines = [];
    if (customerProfile.skin_type) ctxLines.push(`Loai da: ${customerProfile.skin_type}`);
    if (customerProfile.skin_concerns?.length)
      ctxLines.push(`Van de da: ${customerProfile.skin_concerns.join(", ")}`);
    if (customerProfile.budget_max)
      ctxLines.push(`Ngan sach: ${customerProfile.budget_max.toLocaleString("vi-VN")}d`);
    parts.push(`CUSTOMER_CONTEXT:\n${ctxLines.join("\n")}`);
  }

  if (products && products.length) {
    const productLines = products.map((p, i) => {
      const priceStr = p.price ? p.price.toLocaleString("vi-VN") + "d" : "Lien he";
      const stockStr = p.stock_status === "in_stock" ? "Con hang" : "Het hang";
      return `San pham ${i + 1} [${p.slot || "item"}]: ${p.name}\n  Thuong hieu: ${p.brand_name || "Khong ro"}\n  Gia: ${priceStr}\n  Tinh trang: ${stockStr}\n  Ly do: ${p.reason || ""}`;
    });
    parts.push(`KANILA_CART_CONTEXT:\n${productLines.join("\n\n")}`);
  } else {
    parts.push("KANILA_CART_CONTEXT: []");
  }

  if (upsellProducts && upsellProducts.length) {
    const upsellLines = upsellProducts.map((p, i) => {
      const priceStr = p.price ? p.price.toLocaleString("vi-VN") + "d" : "Lien he";
      return `San pham bo sung ${i + 1}: ${p.name} (${p.brand_name}) - ${priceStr} - ${p.reason}`;
    });
    parts.push(`KANILA_UPSELL_CONTEXT:\n${upsellLines.join("\n")}`);
  }

  return `${userMessage}\n\n${parts.join("\n\n")}`;
}

function buildAddToCartMessage(addResult, userMessage) {
  const lines = [
    `Ket qua: ${addResult.success ? "Them thanh cong" : "Khong the them"}`,
    `So san pham da them: ${addResult.items_added}`,
    addResult.items_skipped > 0 ? `So san pham bo qua: ${addResult.items_skipped}` : null,
    `So luong trong gio: ${addResult.cart_count}`,
    `Tong tien gio hang: ${(addResult.cart_total || 0).toLocaleString("vi-VN")}d`,
  ].filter(Boolean);

  return `${userMessage}\n\nKANILA_CART_ACTION:\n${lines.join("\n")}`;
}

function buildCartSummaryMessage(summary, userMessage) {
  if (!summary.found || !summary.items_count) {
    return `${userMessage}\n\nKANILA_CART_CONTEXT: Gio hang trong`;
  }

  const itemLines = summary.items.slice(0, 5).map((i, idx) => {
    const lineTotalStr = (i.line_total || 0).toLocaleString("vi-VN");
    return `  ${idx + 1}. ${i.product_name} x${i.quantity} - ${lineTotalStr}d`;
  });

  const lines = [
    `So luong san pham: ${summary.items_count}`,
    `Tam tinh: ${summary.subtotal.toLocaleString("vi-VN")}d`,
    summary.discount > 0 ? `Giam gia: -${summary.discount.toLocaleString("vi-VN")}d` : null,
    `Tong thanh toan: ${summary.total.toLocaleString("vi-VN")}d`,
    `San pham trong gio:\n${itemLines.join("\n")}`,
  ].filter(Boolean);

  return `${userMessage}\n\nKANILA_CART_CONTEXT:\n${lines.join("\n")}`;
}

// ─────────────────────────────────────────────────────────────────────────────
// Phase 5 Shopping Assistant builders
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Build a beauty-consultation context message for Gemini.
 * Used when intent = "beauty_consultation" — general advisory using product list.
 *
 * @param {object[]} products       — formatted products from getRecommendationContext()
 * @param {object|null} customerProfile — from getCustomerContext().customer_profile
 * @param {string} userMessage
 * @returns {string}
 */
function buildBeautyConsultationMessage(products, customerProfile, userMessage) {
  const parts = [];

  // Customer context header
  if (customerProfile && (customerProfile.skin_type || customerProfile.skin_concerns?.length)) {
    const ctxLines = [];
    if (customerProfile.skin_type)
      ctxLines.push(`Loai da: ${customerProfile.skin_type}`);
    if (customerProfile.skin_concerns?.length)
      ctxLines.push(`Van de da: ${customerProfile.skin_concerns.join(", ")}`);
    if (customerProfile.budget_max)
      ctxLines.push(`Ngan sach toi da: ${customerProfile.budget_max.toLocaleString("vi-VN")}d`);
    if (customerProfile.avoid_ingredients?.length)
      ctxLines.push(`Thanh phan can tranh: ${customerProfile.avoid_ingredients.slice(0, 3).join(", ")}`);
    parts.push(`CUSTOMER_CONTEXT:\n${ctxLines.join("\n")}`);
  }

  // Tag the context type
  parts.push("KANILA_BEAUTY_CONSULTATION: true");

  // Product list
  if (!products || products.length === 0) {
    parts.push("KANILA_PRODUCT_CONTEXT: []");
  } else {
    const productLines = products.map((p, i) => {
      const lines = [
        `San pham ${i + 1}: ${p.name}`,
        `  Thuong hieu: ${p.brand || "Khong ro"}`,
        `  Gia: ${p.price ? p.price.toLocaleString("vi-VN") + "d" : "Lien he"}`,
        p.rating ? `  Danh gia: ${p.rating}★` : null,
        p.reason ? `  Ly do phu hop: ${p.reason}` : null,
        p.badges?.length ? `  Huy hieu: ${p.badges.join(", ")}` : null,
      ].filter(Boolean);
      return lines.join("\n");
    });
    parts.push(`KANILA_PRODUCT_CONTEXT:\n${productLines.join("\n\n")}`);
  }

  return `${userMessage}\n\n${parts.join("\n\n")}`;
}

/**
 * Build a combo-recommendation context message for Gemini.
 * Used when intent = "combo_recommendation" — advisory combo without cart action.
 *
 * @param {object[]} combo     — slot-annotated products from buildComboRecommendation()
 * @param {number} total       — total price of the combo
 * @param {object|null} customerProfile
 * @param {string} userMessage
 * @returns {string}
 */
function buildComboRecommendationMessage(combo, total, customerProfile, userMessage) {
  const parts = [];

  if (customerProfile && (customerProfile.skin_type || customerProfile.skin_concerns?.length)) {
    const ctxLines = [];
    if (customerProfile.skin_type)
      ctxLines.push(`Loai da: ${customerProfile.skin_type}`);
    if (customerProfile.skin_concerns?.length)
      ctxLines.push(`Van de da: ${customerProfile.skin_concerns.join(", ")}`);
    parts.push(`CUSTOMER_CONTEXT:\n${ctxLines.join("\n")}`);
  }

  parts.push("KANILA_COMBO_CONTEXT: true");

  if (!combo || combo.length === 0) {
    parts.push("KANILA_PRODUCT_CONTEXT: []");
  } else {
    const comboLines = combo.map((p, i) => {
      const lines = [
        `Buoc ${i + 1} [${p.slot || "item"}]: ${p.name}`,
        `  Thuong hieu: ${p.brand || "Khong ro"}`,
        `  Gia: ${p.price ? p.price.toLocaleString("vi-VN") + "d" : "Lien he"}`,
        p.reason ? `  Ly do: ${p.reason}` : null,
      ].filter(Boolean);
      return lines.join("\n");
    });
    comboLines.push(`Tong gia combo: ${total.toLocaleString("vi-VN")}d`);
    parts.push(`KANILA_PRODUCT_CONTEXT:\n${comboLines.join("\n\n")}`);
  }

  return `${userMessage}\n\n${parts.join("\n\n")}`;
}

/**
 * Phase 6A
 */
function buildProductComparisonMessage(userMessage, comparison, customerProfile) {
  const parts = [];

  if (customerProfile && (customerProfile.skin_type || customerProfile.skin_concerns?.length)) {
    const ctxLines = [];
    if (customerProfile.skin_type) ctxLines.push(`Loại da: ${customerProfile.skin_type}`);
    if (customerProfile.skin_concerns?.length)
      ctxLines.push(`Vấn đề da: ${customerProfile.skin_concerns.join(", ")}`);
    parts.push(`CUSTOMER_CONTEXT:\n${ctxLines.join("\n")}`);
  }

  const compLines = [];
  compLines.push(`Sản phẩm so sánh: ${comparison.products.map(p => p.name).join(" vs ")}`);
  
  if (comparison.differences && comparison.differences.length > 0) {
    compLines.push("Các điểm khác biệt chính:");
    comparison.differences.forEach(d => {
      compLines.push(`- ${d.feature}: ${d.description}`);
    });
  }

  if (comparison.pros_cons) {
    compLines.push("Ưu nhược điểm:");
    for (const [name, pc] of Object.entries(comparison.pros_cons)) {
      compLines.push(`- ${name}:`);
      if (pc.pros.length) compLines.push(`  + Ưu điểm: ${pc.pros.join(", ")}`);
      if (pc.cons.length) compLines.push(`  + Nhược điểm: ${pc.cons.join(", ")}`);
    }
  }
  
  parts.push(`KANILA_COMPARISON_CONTEXT:\n${compLines.join("\n")}`);

  return `${userMessage}\n\n${parts.join("\n\n")}`;
}

/**
 * Phase 7A
 */
function buildIngredientMessage(userMessage, contextObj) {
  const parts = [];

  if (contextObj.customer) {
    const ctxLines = [];
    if (contextObj.customer.skin_type) ctxLines.push(`Loại da: ${contextObj.customer.skin_type}`);
    if (contextObj.customer.concerns?.length)
      ctxLines.push(`Vấn đề da: ${contextObj.customer.concerns.join(", ")}`);
    parts.push(`CUSTOMER_CONTEXT:\n${ctxLines.join("\n")}`);
  }

  const ingLines = [];
  
  if (contextObj.compatibility_check) {
    ingLines.push(`Kiểm tra tương thích: ${contextObj.compatibility_check.ingredient1} + ${contextObj.compatibility_check.ingredient2}`);
    ingLines.push(`Mức độ: ${contextObj.compatibility_check.level === 'safe' ? 'An toàn' : (contextObj.compatibility_check.level === 'warning' ? 'Cảnh báo' : 'Nên tránh')}`);
    ingLines.push(`Lý do: ${contextObj.compatibility_check.reason}`);
  }

  if (contextObj.ingredient) {
    ingLines.push(`Thành phần: ${contextObj.ingredient.name}`);
    if (contextObj.ingredient.benefits) ingLines.push(`Tác dụng: ${contextObj.ingredient.benefits.join(", ")}`);
    if (contextObj.ingredient.warnings) ingLines.push(`Lưu ý: ${contextObj.ingredient.warnings.join(", ")}`);
  }

  if (contextObj.skin_compatibility) {
    ingLines.push(`Mức độ phù hợp với da khách hàng: ${contextObj.skin_compatibility.compatibility === 'suitable' ? 'Phù hợp' : 'Cần lưu ý'}`);
    ingLines.push(`Lý do: ${contextObj.skin_compatibility.reason}`);
  }

  if (contextObj.product) {
    ingLines.push(`Sản phẩm tra cứu: ${contextObj.product.name}`);
    ingLines.push(`Có chứa thành phần không: ${contextObj.product.has_ingredient ? 'Có' : 'Không tìm thấy'}`);
    if (contextObj.product.has_ingredient && contextObj.product.ingredients) {
      ingLines.push(`Thành phần chính: ${contextObj.product.ingredients.join(", ")}`);
    }
  }

  parts.push(`KANILA_INGREDIENT_CONTEXT:\n${ingLines.join("\n")}`);

  return `${userMessage}\n\n${parts.join("\n\n")}`;
}
