const reviewEventService = require("../services/reviewAi/reviewEvent.service");
const mongoose = require("mongoose");
const ProductReviewAiSummary = require("../models/productReviewAiSummary.model");

describe("Review AI Event Service", () => {
  let ProductReviewAiSummaryMock;

  beforeAll(() => {
    // Mocking updateMany
    ProductReviewAiSummaryMock = jest.spyOn(ProductReviewAiSummary, "updateMany").mockImplementation(() => Promise.resolve({ modifiedCount: 1 }));
  });

  afterAll(() => {
    ProductReviewAiSummaryMock.mockRestore();
  });

  it("should mark AI summary as stale when reviews change", async () => {
    const productId = new mongoose.Types.ObjectId();
    await reviewEventService.markProductReviewAiSummaryStale(productId);
    
    expect(ProductReviewAiSummaryMock).toHaveBeenCalledWith(
      { 
        product_id: productId,
        status: "READY"
      },
      { 
        $set: expect.objectContaining({ 
          status: "STALE",
        }) 
      }
    );
  });
});
