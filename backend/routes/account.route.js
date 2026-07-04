const express = require("express");
const router = express.Router();
const authMiddleware = require("../middlewares/auth.middleware");
const {
  getAllAccounts,
  getAccountById,
  getProfileHub,
  patchMyProfile,
  getMySkinProfile,
  patchMySkinProfile,
  getMyAddresses,
  postMyAddress,
  patchMyAddress,
  deleteMyAddress,
  patchMyDefaultAddress,
  getMyProviders,
  getMySecurityStatus,
  unlinkMyProvider,
  createAccount,
  updateAccount,
  patchAccount,
  deleteAccount,
} = require("../controllers/account.controller");
const { getMyReviews, patchMyReview, deleteMyReview } = require("../controllers/review.controller");

// ─── Authenticated account routes ─────────────────────────────────────────────

router.get("/profile-hub", authMiddleware, getProfileHub);
router.patch("/profile", authMiddleware, patchMyProfile);
router.get("/skin-profile", authMiddleware, getMySkinProfile);
router.patch("/skin-profile", authMiddleware, patchMySkinProfile);
router.post("/addresses", authMiddleware, postMyAddress);
router.get("/addresses", authMiddleware, getMyAddresses);
router.patch("/addresses/:id/default", authMiddleware, patchMyDefaultAddress);
router.patch("/addresses/:id", authMiddleware, patchMyAddress);
router.delete("/addresses/:id", authMiddleware, deleteMyAddress);

// Security & linked providers
router.get("/providers", authMiddleware, getMyProviders);
router.get("/security-status", authMiddleware, getMySecurityStatus);
router.delete("/providers/:provider", authMiddleware, unlinkMyProvider);

// NOTE: POST /change-password has been removed.
// Kanila now uses passwordless email-OTP authentication.
// Password management endpoints are no longer applicable.

// ─── Customer review management ───────────────────────────────────────────────

router.get("/reviews", authMiddleware, getMyReviews);
router.patch("/reviews/:id", authMiddleware, patchMyReview);
router.delete("/reviews/:id", authMiddleware, deleteMyReview);

// ─── Admin / internal account CRUD ───────────────────────────────────────────

router.get("/", getAllAccounts);
router.post("/", createAccount);
router.get("/:id", getAccountById);
router.put("/:id", updateAccount);
router.patch("/:id", patchAccount);
router.delete("/:id", deleteAccount);

module.exports = router;
