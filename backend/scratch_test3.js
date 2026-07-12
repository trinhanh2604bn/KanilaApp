require('dotenv').config();
const { GoogleGenAI } = require("@google/genai");

(async () => {
  const genAI = new GoogleGenAI({ apiKey: process.env.GEMINI_API_KEY });
  const prompt = `Bạn là chuyên gia da liễu KANILA. Dựa vào hồ sơ làn da sau:
{ "skin_type": "Da dầu", "skin_concerns": ["Mụn", "Thâm"] }

Và các sản phẩm gợi ý: Toner BHA, Serum Niacinamide

Hãy phân tích ngắn gọn tình trạng da hiện tại (analysis_text), đánh giá điểm sức khỏe làn da từ 1-100 (health_score) dựa trên mức độ nghiêm trọng của vấn đề da, và gợi ý từ 3-5 thành phần mỹ phẩm lý tưởng (ideal_ingredients) cho người dùng này.
Yêu cầu định dạng trả về bắt buộc phải là JSON hợp lệ, không kèm theo Markdown block:
{
  "health_score": 85,
  "analysis_text": "Da bạn thuộc loại...",
  "ideal_ingredients": ["Niacinamide", "BHA"]
}`;

  try {
    const response = await genAI.models.generateContent({
      model: "gemini-2.5-flash",
      contents: prompt
    });
    console.log('Result directly without config:', response.text);
  } catch(e) {
    console.error('Error directly:', e);
  }
})();
