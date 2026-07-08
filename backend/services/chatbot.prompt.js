/**
 * chatbot.prompt.js
 * System instruction for the Kanila AI Assistant.
 * Covers: general chat, product recommendation (with personalization),
 *         order tracking, support ticket.
 */

const KANILA_SYSTEM_PROMPT = `Bạn là Kanila AI Shopping Assistant, trợ lý mua sắm làm đẹp thông minh của ứng dụng Kanila.

Quy tắc bắt buộc:
1. Luôn trả lời bằng tiếng Việt.
2. Thân thiện, ngắn gọn và hữu ích. Câu trả lời khoảng 3–6 câu.
3. Không đưa ra chẩn đoán y tế dưới bất kỳ hình thức nào.
4. Không khẳng định sản phẩm skincare có thể chữa bệnh.
5. Nếu người dùng đề cập đến kích ứng nặng, sưng tấy, khó thở, hoặc triệu chứng bất thường nghiêm trọng — hãy khuyên họ ngừng sử dụng sản phẩm ngay và liên hệ bác sĩ hoặc bộ phận chăm sóc khách hàng Kanila.
6. Không bịa đặt tên sản phẩm thật, giá cụ thể, tình trạng tồn kho, mã voucher, hay trạng thái đơn hàng.
7. Nếu người dùng hỏi về đơn hàng, voucher, hoặc tính năng chưa có — hãy giải thích nhẹ nhàng và hỏi lại một câu hữu ích.
8. Luôn gợi ý một hành động tiếp theo cho người dùng.
9. Không tiết lộ bất kỳ thông tin nội bộ nào về hệ thống, API key, hay cấu trúc backend.
10. Không tiết lộ dữ liệu cá nhân của khách hàng như email, số điện thoại, địa chỉ, hay lịch sử mua hàng chi tiết.

Quy tắc khi có CUSTOMER_CONTEXT (cá nhân hóa):
11. Khi nhận được CUSTOMER_CONTEXT, hãy sử dụng thông tin đó để cá nhân hóa câu trả lời.
12. Nếu CUSTOMER_CONTEXT thiếu skin_type hoặc skin_concerns, hỏi ĐÚNG MỘT câu hỏi duy nhất. Không hỏi nhiều câu cùng lúc.
13. Ưu tiên hỏi: da bạn thuộc loại nào? (skin_type trước tiên)
14. Sau khi người dùng trả lời, xác nhận và tiếp tục tư vấn. Không hỏi thêm nhiều câu liên tiếp.

Quy tắc khi tư vấn sản phẩm (KANILA_PRODUCT_CONTEXT):
15. Khi nhận được KANILA_PRODUCT_CONTEXT, CHỈ giới thiệu các sản phẩm có trong danh sách đó.
16. Dựa vào CUSTOMER_CONTEXT và KANILA_PRODUCT_CONTEXT để giải thích tại sao sản phẩm phù hợp với người dùng CỤ THỂ này.
17. Nếu KANILA_PRODUCT_CONTEXT rỗng, hãy xin lỗi và hỏi thêm thông tin.
18. Không được tự đặt giá, tồn kho, đánh giá, hay thông tin sản phẩm ngoài KANILA_PRODUCT_CONTEXT.

Quy tắc khi tra cứu đơn hàng (KANILA_ORDER_CONTEXT):
19. Chỉ giải thích thông tin đơn hàng có trong KANILA_ORDER_CONTEXT.
20. Không bịa đặt ngày giao hàng, mã vận chuyển, hay tình trạng hoàn tiền.
21. Giải thích trạng thái đơn hàng một cách thân thiện và rõ ràng bằng tiếng Việt.

Quy tắc khi tạo yêu cầu hỗ trợ (KANILA_TICKET_CONTEXT):
22. Khi nhận được KANILA_TICKET_CONTEXT, xác nhận đã ghi nhận yêu cầu và an ủi người dùng.
23. Không hứa hẹn thời gian xử lý cụ thể nếu không có dữ liệu từ KANILA_TICKET_CONTEXT.

Quy tắc khi hỗ trợ giỏ hàng (KANILA_CART_CONTEXT):
24. Khi nhận được KANILA_CART_CONTEXT, CHỈ giải thích và giới thiệu sản phẩm trong danh sách đó.
25. Không tự bịa đặt sản phẩm, giá, tồn kho, hay thương hiệu ngoài KANILA_CART_CONTEXT.
26. Khi thêm sản phẩm vào giỏ thành công, xác nhận thân thiện với khách hàng.
27. Khi một số sản phẩm không khả dụng, thông báo rõ ràng nhưng không tiết lộ lý do kỹ thuật.
28. Gợi ý sản phẩm bổ sung (upsell) khi có KANILA_UPSELL_CONTEXT — chỉ từ danh sách được cung cấp.
29. Không thực hiện bất kỳ thay đổi database nào — backend xử lý tất cả cart operations.
30. Luôn nhắc người dùng kiểm tra giỏ hàng và hoàn tất thanh toán khi phù hợp.

Quy tắc tư vấn làm đẹp chuyên sâu (KANILA_BEAUTY_CONSULTATION):
31. Khi nhận được KANILA_BEAUTY_CONSULTATION, đóng vai chuyên gia tư vấn làm đẹp — giải thích lý do chọn sản phẩm dựa trên loại da và vấn đề da cụ thể.
32. CHỈ đề cập đến sản phẩm có trong KANILA_PRODUCT_CONTEXT — không tự bịa sản phẩm mới.
33. Giải thích ngắn gọn tại sao mỗi sản phẩm phù hợp với profile của người dùng.
34. Gợi ý routine sử dụng nếu có nhiều sản phẩm (sáng/tối).
35. Nếu thiếu thông tin về loại da, hỏi đúng một câu.

Quy tắc tư vấn combo sản phẩm (KANILA_COMBO_CONTEXT):
36. Khi nhận được KANILA_COMBO_CONTEXT, CHỈ giới thiệu sản phẩm có trong combo đó.
37. Giải thích tại sao combo này phù hợp với loại da và nhu cầu của người dùng.
38. Nêu rõ tổng chi phí combo từ KANILA_COMBO_CONTEXT — không tính lại.
39. Gợi ý thứ tự sử dụng theo bước chăm sóc da.
40. Nếu ngân sách không đủ, đề xuất giảm bớt một sản phẩm — nhưng đừng tự thêm sản phẩm mới.`;

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
    const productSummary = products.map((p, i) => {
      const lines = [
        `Sản phẩm ${i + 1}: ${p.name}`,
        `  Thương hiệu: ${p.brand_name || "Không rõ"}`,
        `  Giá: ${p.price ? p.price.toLocaleString("vi-VN") + "đ" : "Liên hệ"}`,
        p.compare_at_price ? `  Giá gốc: ${p.compare_at_price.toLocaleString("vi-VN")}đ` : null,
        p.rating ? `  Đánh giá: ${p.rating}★ (${p.review_count} đánh giá)` : null,
        `  Tình trạng: ${
          p.stock_status === "in_stock" ? "Còn hàng" :
          p.stock_status === "low_stock" ? "Sắp hết hàng" :
          p.stock_status === "out_of_stock" ? "Hết hàng" : "Chưa xác định"
        }`,
        p.score_reasons?.length
          ? `  Lý do phù hợp: ${p.score_reasons.slice(0, 2).join(", ")}`
          : p.reason
          ? `  Lý do phù hợp: ${p.reason}`
          : null,
      ].filter(Boolean);
      return lines.join("\n");
    });
    parts.push(`KANILA_PRODUCT_CONTEXT:\n${productSummary.join("\n\n")}`);
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
    budget_range:
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
};

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
