const express = require("express");
const router = express.Router({ mergeParams: true });
const customerBeautyProfileController = require("../controllers/customerBeautyProfile.controller");
const authMiddleware = require("../middlewares/auth.middleware");
const beautyProfileLegacyAdapter = require("../middlewares/beautyProfileLegacyAdapter.middleware");

// The router is mounted on /api/customers/:customer_id/beauty-profile
// Auth middleware is required so that "me" alias can resolve to the current user's customer ID
router.get("/", authMiddleware, customerBeautyProfileController.getProfile);

// Legacy adapter runs before validation/controller to translate old Android fields (e.g., skin_tone) to canonical fields (e.g., skin_color)
router.post("/", authMiddleware, beautyProfileLegacyAdapter, customerBeautyProfileController.createProfile);
router.patch("/", authMiddleware, beautyProfileLegacyAdapter, customerBeautyProfileController.updateProfile);

// Deprecate PUT in favor of explicit PATCH
router.put("/", authMiddleware, customerBeautyProfileController.putNotAllowed);

module.exports = router;
