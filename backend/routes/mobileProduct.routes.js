const express = require("express");
const router = express.Router();
const { getProductDetail } = require("../controllers/mobileProduct.controller");
const optionalAuth = require("../middlewares/optionalAuth.middleware");

// GET /api/mobile/products/:id/detail
router.get("/:id/detail", optionalAuth, getProductDetail);

module.exports = router;
