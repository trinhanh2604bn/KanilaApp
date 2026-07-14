const reviewAiSummaryService = require("../services/reviewAi/reviewAiSummary.service");

class ReviewAiWorker {
  constructor() {
    this.timer = null;
  }

  start() {
    const intervalMs = parseInt(process.env.AI_REVIEW_WORKER_INTERVAL_MS) || 60000; // 1 minute default
    console.log(`[ReviewAiWorker] Starting with interval ${intervalMs}ms`);
    
    // Run once immediately
    this._runCycle();
    
    this.timer = setInterval(() => {
      this._runCycle();
    }, intervalMs);
  }

  stop() {
    if (this.timer) {
      clearInterval(this.timer);
      this.timer = null;
      console.log("[ReviewAiWorker] Stopped");
    }
  }

  async _runCycle() {
    try {
      await reviewAiSummaryService.processPendingOrStaleSummaries();
    } catch (err) {
      console.error("[ReviewAiWorker] Error during cycle:", err);
    }
  }
}

module.exports = new ReviewAiWorker();
