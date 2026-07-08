/**
 * chatbot.prompt.js
 * System instruction for the Kanila AI Assistant.
 * This prompt is sent to Gemini with every request to define the assistant's persona and rules.
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
13. Không được tự đặt giá, tồn kho, đánh giá, hay thông tin sản phẩm ngoài KANILA_PRODUCT_CONTEXT.`;

/**
 * Build a product-context prompt string to inject into the user message turn.
 * Gemini will see this as part of the conversation context.
 *
 * @param {object[]} products — normalized product objects from chatbotProduct.tool.js
 * @param {string} userMessage — original user message
 * @returns {string}
 */
function buildProductContextMessage(products, userMessage) {
  if (!products || products.length === 0) {
    return `${userMessage}\n\nKANILA_PRODUCT_CONTEXT: []`;
  }

  // Build a compact, safe product summary for Gemini — no internal IDs or costs
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

module.exports = { KANILA_SYSTEM_PROMPT, buildProductContextMessage };
