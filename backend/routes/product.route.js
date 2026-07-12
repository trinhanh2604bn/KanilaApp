const express = require("express");
const router = express.Router();
const {
  getAllProducts,
  getProductById,
  getProductBySlug,
  getSimilarProducts,
  createProduct,
  updateProduct,
  patchProduct,
  deleteProduct,
  getSkinMatchForProduct,
  getReviewInsightsForProduct
} = require("../controllers/product.controller");
const verifyToken = require("../middlewares/auth.middleware");
const optionalAuth = require("../middlewares/optionalAuth.middleware");

router.get("/", getAllProducts);
router.get("/slug/:slug", getProductBySlug);
router.get("/:id/similar", getSimilarProducts);
router.get("/:id/skin-match/me", verifyToken, getSkinMatchForProduct);
router.get("/:id/review-insights", optionalAuth, getReviewInsightsForProduct);
router.get("/:id", getProductById);
router.post("/", createProduct);
router.put("/:id", updateProduct);
router.patch("/:id", patchProduct);
router.delete("/:id", deleteProduct);

module.exports = router;
