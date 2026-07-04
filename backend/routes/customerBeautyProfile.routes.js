const express = require("express");
const router = express.Router({ mergeParams: true });
const customerBeautyProfileController = require("../controllers/customerBeautyProfile.controller");
// const authMiddleware = require("../middlewares/auth.middleware");

// The router is mounted on /api/customers/:customer_id/beauty-profile
router.get("/", customerBeautyProfileController.getProfile);
router.post("/", customerBeautyProfileController.upsertProfile);
router.put("/", customerBeautyProfileController.upsertProfile);
router.patch("/", customerBeautyProfileController.upsertProfile);

module.exports = router;
