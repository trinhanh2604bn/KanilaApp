/**
 * chatbot.prompt.js
 * System instruction for the Kanila AI Assistant.
 * Covers: general chat, product recommendation, order tracking, support ticket.
 */

const KANILA_SYSTEM_PROMPT = `Bạn là Kanila AI Assistant, trợ lý mua sắm làm đẹp thông minh của ứng dụng Kanila.

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

Quy tắc khi tư vấn sản phẩm (KANILA_PRODUCT_CONTEXT):
10. Khi nhận được KANILA_PRODUCT_CONTEXT, CHỈ giới thiệu các sản phẩm có trong danh sách đó. Không bịa thêm sản phẩm khác.
11. Dựa vào thông tin trong KANILA_PRODUCT_CONTEXT để giải thích tại sao sản phẩm phù hợp với người dùng.
12. Nếu KANILA_PRODUCT_CONTEXT rỗng, hãy xin lỗi và hỏi thêm thông tin để tìm sản phẩm phù hợp hơn.
13. Không được tự đặt giá, tồn kho, đánh giá, hay thông tin sản phẩm ngoài KANILA_PRODUCT_CONTEXT.

Quy tắc khi tra cứu đơn hàng (KANILA_ORDER_CONTEXT):
14. Khi nhận được KANILA_ORDER_CONTEXT, chỉ giải thích thông tin đơn hàng có trong đó.
15. Không bịa đặt ngày giao hàng, mã vận chuyển, nhà vận chuyển, tình trạng hoàn tiền, hay bất kỳ thông tin nào không có trong KANILA_ORDER_CONTEXT.
16. Giải thích trạng thái đơn hàng một cách thân thiện và rõ ràng bằng tiếng Việt.
17. Luôn gợi ý hành động tiếp theo phù hợp với trạng thái đơn hàng.

Quy tắc khi tạo yêu cầu hỗ trợ (KANILA_TICKET_CONTEXT):
18. Khi nhận được KANILA_TICKET_CONTEXT, xác nhận đã ghi nhận yêu cầu và an ủi người dùng.
19. Không hứa hẹn thời gian xử lý cụ thể nếu không có dữ liệu từ KANILA_TICKET_CONTEXT.
20. Khuyến khích người dùng cung cấp thêm thông tin nếu hỗ trợ tốt hơn.`;

// ─────────────────────────────────────────────────────────────────────────────
// Context builders
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Build a product-context message string for Gemini.
 * @param {object[]} products
 * @param {string} userMessage
 * @returns {string}
 */
function buildProductContextMessage(products, userMessage) {
  if (!products || products.length === 0) {
    return `${userMessage}\n\nKANILA_PRODUCT_CONTEXT: []`;
  }

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
      p.reason ? `  Lý do phù hợp: ${p.reason}` : null,
    ].filter(Boolean);
    return lines.join("\n");
  });

  return `${userMessage}\n\nKANILA_PRODUCT_CONTEXT:\n${productSummary.join("\n\n")}`;
}

/**
 * Build an order-context message string for Gemini.
 * Only passes safe, normalized order fields — never raw DB fields.
 * @param {object} order — normalized order from chatbotOrder.tool.js
 * @param {string} userMessage
 * @returns {string}
 */
function buildOrderContextMessage(order, userMessage) {
  if (!order) {
    return `${userMessage}\n\nKANILA_ORDER_CONTEXT: null`;
  }

  const lines = [
    `Mã đơn hàng: ${order.order_code}`,
    `Trạng thái đơn: ${order.status_label}`,
    `Trạng thái giao hàng: ${order.fulfillment_status_label}`,
    `Trạng thái thanh toán: ${order.payment_status_label}`,
    order.total_amount != null
      ? `Tổng tiền: ${order.total_amount.toLocaleString("vi-VN")}đ`
      : null,
    `Số lượng sản phẩm: ${order.items_count}`,
    order.items_preview && order.items_preview.length > 0
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
 * @param {object} ticket — normalized ticket from chatbotSupport.tool.js
 * @param {string} userMessage
 * @returns {string}
 */
function buildTicketContextMessage(ticket, userMessage) {
  if (!ticket) {
    return `${userMessage}\n\nKANILA_TICKET_CONTEXT: null`;
  }

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
  buildOrderContextMessage,
  buildTicketContextMessage,
};
