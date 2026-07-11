const express = require("express");
const router = express.Router();

const {
  getAllCategories,
  getCategoryById,
  getRootCategories,
  getChildCategories,
  createCategory,
  updateCategory,
  deleteCategory,
} = require("../controllers/category.controller");

// Lấy category cha: Face, Eyes, Lips, Cheeks, Gift, Mini & Travel
router.get("/root", getRootCategories);

// Lấy category con theo parentCategoryId:
// Ví dụ Face -> Foundation, Concealer, Primer...
router.get("/:parentId/children", getChildCategories);

// Lấy toàn bộ categories
router.get("/", getAllCategories);

// Lấy 1 category theo id
// Route này phải đặt sau /root và /:parentId/children
router.get("/:id", getCategoryById);

router.post("/", createCategory);
router.put("/:id", updateCategory);
router.delete("/:id", deleteCategory);

module.exports = router;