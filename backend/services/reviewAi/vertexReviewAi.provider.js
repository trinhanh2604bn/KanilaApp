/**
 * services/reviewAi/vertexReviewAi.provider.js
 *
 * Vertex AI implementation of ReviewAiProvider.
 * Replaces geminiReviewAi.provider.js (Google AI Studio) with Vertex AI.
 *
 * Uses shared vertexGemini.service — callers cannot override model or key.
 *
 * Security:
 *   - Does NOT send: email, phone, address, token, orderId, paymentData
 *   - Sends: rating, reviewTitle, reviewContent (truncated)
 *   - Limits: max 100 reviews, max 500 chars per review
 *   - responseMimeType: application/json
 *   - Server-side JSON validation after model response
 */

"use strict";

const ReviewAiProvider = require("./reviewAiProvider.interface");
const { generateContent } = require("../ai/vertexGemini.service");
const { getVertexModel } = require("../../config/vertex.config");

// ─── PII field safety list ────────────────────────────────────────────────────
// Only these fields are included in the prompt — all others are stripped.
const SAFE_REVIEW_FIELDS = ["rating", "reviewTitle", "reviewContent"];

const MAX_REVIEWS_TO_SEND = 100;
const MAX_REVIEW_CONTENT_CHARS = 500;
const MAX_REVIEW_TITLE_CHARS = 150;

// ─── Output validation ────────────────────────────────────────────────────────

const MAX_THEME_TITLE_CHARS = 120;
const MAX_THEME_DESC_CHARS = 500;
const MAX_ARRAY_ITEMS = 10;
const MAX_STRING_ITEMS_CHARS = 300;

/**
 * Validate and sanitize the AI JSON output.
 * Throws with code VERTEX_INVALID_JSON_RESPONSE if invalid.
 *
 * @param {*} parsed
 * @returns {object} sanitized output
 */
function _validateAndSanitizeOutput(parsed) {
  if (!parsed || typeof parsed !== "object" || Array.isArray(parsed)) {
    const err = new Error("AI output is not a JSON object.");
    err.code = "VERTEX_INVALID_JSON_RESPONSE";
    throw err;
  }

  if (typeof parsed.short_summary !== "string" || !parsed.short_summary.trim()) {
    const err = new Error("AI output missing required field: short_summary.");
    err.code = "VERTEX_INVALID_JSON_RESPONSE";
    throw err;
  }

  function sanitizeThemeArray(arr, fieldName) {
    if (!Array.isArray(arr)) {
      const err = new Error(`AI output field '${fieldName}' must be an array.`);
      err.code = "VERTEX_INVALID_JSON_RESPONSE";
      throw err;
    }
    return arr.slice(0, MAX_ARRAY_ITEMS).map((item) => {
      if (!item || typeof item !== "object") {
        const err = new Error(`AI output: invalid item in '${fieldName}'.`);
        err.code = "VERTEX_INVALID_JSON_RESPONSE";
        throw err;
      }
      // Sanitize supporting_review_refs: must be strings only
      const refs = Array.isArray(item.supporting_review_refs)
        ? item.supporting_review_refs
            .filter((r) => typeof r === "string")
            .slice(0, 20)
        : [];

      return {
        code: typeof item.code === "string"
          ? item.code.replace(/[^A-Z0-9_]/g, "").slice(0, 50)
          : "UNKNOWN",
        title: typeof item.title === "string"
          ? item.title.slice(0, MAX_THEME_TITLE_CHARS)
          : "",
        description: typeof item.description === "string"
          ? item.description.slice(0, MAX_THEME_DESC_CHARS)
          : "",
        supporting_review_refs: refs,
      };
    });
  }

  function sanitizeStringArray(arr, fieldName) {
    if (!Array.isArray(arr)) {
      const err = new Error(`AI output field '${fieldName}' must be an array.`);
      err.code = "VERTEX_INVALID_JSON_RESPONSE";
      throw err;
    }
    return arr
      .filter((s) => typeof s === "string")
      .slice(0, MAX_ARRAY_ITEMS)
      .map((s) => s.slice(0, MAX_STRING_ITEMS_CHARS));
  }

  return {
    short_summary: parsed.short_summary.trim().slice(0, 1000),
    positive_themes: sanitizeThemeArray(parsed.positive_themes || [], "positive_themes"),
    negative_themes: sanitizeThemeArray(parsed.negative_themes || [], "negative_themes"),
    common_experiences: sanitizeStringArray(parsed.common_experiences || [], "common_experiences"),
    usage_tips: sanitizeStringArray(parsed.usage_tips || [], "usage_tips"),
    cautions: sanitizeStringArray(parsed.cautions || [], "cautions"),
  };
}

// ─── Prompt builder ───────────────────────────────────────────────────────────

function _buildPrompt(product, reviews) {
  // Only include safe fields — never include PII
  const safeReviews = reviews
    .slice(0, MAX_REVIEWS_TO_SEND)
    .map((r, i) => {
      const title = typeof r.reviewTitle === "string"
        ? r.reviewTitle.slice(0, MAX_REVIEW_TITLE_CHARS)
        : "";
      const content = typeof r.reviewContent === "string"
        ? r.reviewContent.slice(0, MAX_REVIEW_CONTENT_CHARS)
        : "";
      const lines = [`Review ${i + 1}:`, `Rating: ${Number(r.rating) || 0}`];
      if (title) lines.push(`Title: ${title}`);
      if (content) lines.push(`Content: ${content}`);
      return lines.join("\n");
    });

  const productName = product.productName || "Sản phẩm";
  const categoryName = product.categoryId?.categoryName || "Mỹ phẩm";

  return `Đây là các đánh giá cần phân tích:\n\n${safeReviews.join("\n\n")}`;
}

function _buildSystemInstruction(product) {
  const productName = product.productName || "Sản phẩm";
  const categoryName = product.categoryId?.categoryName || "Mỹ phẩm";

  return `Bạn là trợ lý AI của ứng dụng thương mại điện tử mỹ phẩm Kanila App.
Nhiệm vụ: Phân tích các đánh giá của khách hàng cho sản phẩm "${productName}" (Loại: ${categoryName}).

Quy tắc bắt buộc:
- Chỉ phân tích nội dung review được cung cấp — không tự thêm thông tin.
- Không tự bịa trải nghiệm, không tạo công dụng không có trong review.
- Không đưa ra chẩn đoán y khoa, không khẳng định chữa bệnh.
- Không suy diễn thành phần nếu review không đề cập.
- Viết bằng tiếng Việt.
- Trả về JSON thuần (không có markdown, không có code block).

Schema JSON yêu cầu:
{
  "short_summary": "string — tóm tắt tổng quan ngắn gọn (tối đa 3 câu)",
  "positive_themes": [
    {
      "code": "UPPER_SNAKE_CASE",
      "title": "string",
      "description": "string",
      "supporting_review_refs": ["Review 1", "Review 2"]
    }
  ],
  "negative_themes": [
    {
      "code": "UPPER_SNAKE_CASE",
      "title": "string",
      "description": "string",
      "supporting_review_refs": ["Review 1"]
    }
  ],
  "common_experiences": ["string"],
  "usage_tips": ["string"],
  "cautions": ["string — chỉ ghi nhận trải nghiệm thực tế, không phán đoán y khoa"]
}`;
}

// ─── Provider class ───────────────────────────────────────────────────────────

class VertexReviewAiProvider extends ReviewAiProvider {
  /**
   * Generate a review summary using Vertex AI.
   *
   * @param {object} product
   * @param {Array}  reviews
   * @param {object} [options]
   * @returns {Promise<object>} — validated and sanitized summary
   */
  async generateSummary(product, reviews, options = {}) {
    const prompt = _buildPrompt(product, reviews);
    const systemInstruction = _buildSystemInstruction(product);

    // generateContent handles timeout, error mapping, empty response detection.
    // It makes a single call — the ReviewAiSummaryService worker owns retry logic.
    const result = await generateContent({
      prompt,
      systemInstruction,
      responseMimeType: "application/json",
      generationConfig: {
        temperature: 0.3,
        maxOutputTokens: 2048,
      },
    });

    let parsed;
    try {
      parsed = JSON.parse(result.text);
    } catch (parseErr) {
      const err = new Error(
        "VERTEX_INVALID_JSON_RESPONSE: Failed to parse Vertex AI response as JSON."
      );
      err.code = "VERTEX_INVALID_JSON_RESPONSE";
      throw err;
    }

    // Server-side validation — never trust model output
    const sanitized = _validateAndSanitizeOutput(parsed);

    return sanitized;
  }
}

module.exports = {
  // Export singleton instance for use as a drop-in replacement
  provider: new VertexReviewAiProvider(),
  // Export class for testing
  VertexReviewAiProvider,
  // Export internal helpers for unit testing
  _validateAndSanitizeOutput,
  _buildPrompt,
  _buildSystemInstruction,
};
