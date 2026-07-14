/**
 * Interface for AI Review Summary Providers.
 * Any AI provider (Gemini, OpenAI, etc.) should implement these methods.
 */
class ReviewAiProvider {
  /**
   * Generates a summary for a given set of reviews.
   * @param {Object} product - Product details (name, category, etc.)
   * @param {Array} reviews - Array of reviews to summarize
   * @param {Object} options - Options like segment, language
   * @returns {Promise<Object>} - The structured summary data.
   */
  async generateSummary(product, reviews, options = {}) {
    throw new Error("Method 'generateSummary()' must be implemented.");
  }
}

module.exports = ReviewAiProvider;
