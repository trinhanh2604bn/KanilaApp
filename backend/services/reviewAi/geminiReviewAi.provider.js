const ReviewAiProvider = require("./reviewAiProvider.interface");
const { GoogleGenAI } = require("@google/genai");

class GeminiReviewAiProvider extends ReviewAiProvider {
  constructor() {
    super();
    this.ai = new GoogleGenAI({ apiKey: process.env.GEMINI_API_KEY });
    this.modelName = process.env.GEMINI_REVIEW_SUMMARY_MODEL || "gemini-2.5-flash";
  }

  async generateSummary(product, reviews, options = {}) {
    const language = options.language || "vi";
    
    let reviewsText = reviews.map((r, i) => `Review ${i + 1}:\nRating: ${r.rating}\nTitle: ${r.reviewTitle || "No title"}\nContent: ${r.reviewContent || "No content"}`).join("\n\n");
    
    // We should safely escape or limit the prompt size (say, first 100 reviews).
    const systemPrompt = `You are an AI assistant for a cosmetics ecommerce app named Kanila App.
Your task is to analyze user reviews for the product: "${product.productName}" (Type: ${product.categoryId?.categoryName || "Cosmetics"}).
Provide a structured JSON output with the following keys:
- "short_summary": A very brief overall summary (max 3 sentences).
- "positive_themes": Array of objects { "code": "THEME_CODE", "title": "...", "description": "...", "supporting_review_refs": ["Review 1"] }
- "negative_themes": Array of objects { "code": "THEME_CODE", "title": "...", "description": "...", "supporting_review_refs": ["Review 2"] }
- "common_experiences": Array of strings.
- "usage_tips": Array of strings.
- "cautions": Array of strings (WARNING: Do NOT make medical claims, just summarize what users experienced).

Respond strictly in valid JSON format matching this schema. Write your content in ${language === 'vi' ? 'Vietnamese' : 'English'}.
Do not include markdown blocks like \`\`\`json, just output the raw JSON object.`;

    const prompt = `Here are the reviews to analyze:\n\n${reviewsText}`;

    const response = await this.ai.models.generateContent({
      model: this.modelName,
      contents: prompt,
      config: {
        systemInstruction: systemPrompt,
        responseMimeType: "application/json",
      },
    });

    try {
      const text = response.text || "";
      const json = JSON.parse(text);
      return json;
    } catch (err) {
      console.error("Failed to parse Gemini response as JSON:", err);
      throw new Error("Invalid response format from AI provider");
    }
  }
}

module.exports = new GeminiReviewAiProvider();
