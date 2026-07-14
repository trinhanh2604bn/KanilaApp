const mongoose = require("mongoose");
const Product = require("../models/product.model");
const Brand = require("../models/brand.model");
const Category = require("../models/category.model");
const ProductMedia = require("../models/productMedia.model");
const ProductVariant = require("../models/productVariant.model");
const VariantMedia = require("../models/variantMedia.model");
const ProductAttribute = require("../models/productAttribute.model");
const InventoryBalance = require("../models/inventoryBalance.model");
const ReviewSummary = require("../models/reviewSummary.model");
const ReviewMedia = require("../models/reviewMedia.model");
const WishlistItem = require("../models/wishlistItem.model");
const Wishlist = require("../models/wishlist.model");
const Customer = require("../models/customer.model");
const CustomerBeautyProfile = require("../models/customerBeautyProfile.model");
const validateObjectId = require("../utils/validateObjectId");
const { getAggregateReviewSummary } = require("../services/reviewSummary.service");

const getProductDetail = async (req, res) => {
  try {
    const { id } = req.params;
    const accountId = req.user?.account_id;

    console.log(`[MobileAPI] Fetching detail for Product ID: ${id}`);

    if (!validateObjectId(id)) {
      console.warn(`[MobileAPI] Invalid ObjectId received: ${id}`);
      return res.status(400).json({ success: false, message: "Invalid product ID format" });
    }

    const product = await Product.findById(id)
      .populate("brandId")
      .populate("categoryId")
      .lean();

    if (!product || product.productStatus !== "active") {
      return res.status(404).json({ success: false, message: "Product not found or inactive" });
    }

    const productObjectId = product._id;
    const categoryObjectId = product.categoryId?._id || product.categoryId;
    const brandObject = product.brandId || null;
    const categoryObject = product.categoryId || null;

    // Load customer profile if logged in
    let customerId = null;
    if (accountId) {
        const customer = await Customer.findOne({ account_id: accountId }).select("_id").lean();
        customerId = customer?._id;
    }

    // Load variants once
    const variants = await ProductVariant.find({ productId: id, variantStatus: "active" }).lean();
    const variantIds = variants.map(v => v._id);

    // Parallel loading of secondary data
    const [
      media,
      attributes,
      reviewSummary,
      inventoryBalances,
      variantMedia
    ] = await Promise.all([
      ProductMedia.find({ productId: id }).sort({ isPrimary: -1, sortOrder: 1 }).lean(),
      ProductAttribute.find({ productId: id }).sort({ displayOrder: 1 }).lean(),
      getAggregateReviewSummary(id),
      InventoryBalance.find({ variantId: { $in: variantIds } }).lean(),
      VariantMedia.find({ variantId: { $in: variantIds } }).lean()
    ]);

    // Related products (same category) - FIX: Use categoryObjectId and populate
    const relatedProducts = await Product.find({
      categoryId: categoryObjectId,
      _id: { $ne: id },
      productStatus: "active"
    })
    .limit(10)
    .populate("brandId")
    .populate("categoryId")
    .lean();

    // Media fallback logic
    let normalizedMedia = [...media];
    if (normalizedMedia.length === 0) {
        if (product.imageUrl) {
            normalizedMedia.push({
                mediaType: "image",
                mediaUrl: product.imageUrl,
                isPrimary: true
            });
        } else if (variantMedia.length > 0) {
            normalizedMedia.push({
                mediaType: variantMedia[0].mediaType || "image",
                mediaUrl: variantMedia[0].mediaUrl,
                isPrimary: true
            });
        }
    }

    // Wishlist status
    let isWishlisted = false;
    if (customerId) {
        const wishlist = await Wishlist.findOne({ customer_id: customerId, isDefault: true }).lean();
        if (wishlist) {
            const wishlistItem = await WishlistItem.findOne({ wishlistId: wishlist._id, productId: id }).lean();
            isWishlisted = !!wishlistItem;
        }
    }

    // Skin match
    let skinMatch = null;
    if (customerId) {
        const beautyProfile = await CustomerBeautyProfile.findOne({ customer_id: customerId }).lean();
        if (beautyProfile) {
            const isMatch = product.skin_types_supported?.includes(beautyProfile.skin_type);
            skinMatch = {
                score: isMatch ? 95 : 60,
                level: isMatch ? "Phù hợp rất cao" : "Phù hợp trung bình",
                profileChips: [beautyProfile.skin_type, ...(beautyProfile.skin_concerns || [])],
                reasons: isMatch ? [{ title: "Dành cho da của bạn", description: `Sản phẩm này được thiết kế đặc biệt cho ${beautyProfile.skin_type}` }] : [],
                warnings: !isMatch ? [{ title: "Lưu ý", description: "Sản phẩm này có thể không tối ưu cho loại da của bạn" }] : []
            };
        }
    }

    // Inventory status aggregation
    const totalAvailable = inventoryBalances.length > 0
        ? inventoryBalances.reduce((acc, curr) => acc + (curr.availableQty !== undefined ? curr.availableQty : (curr.onHandQty - (curr.reservedQty || 0))), 0)
        : (product.stock || 0);

    const inventoryStatus = totalAvailable <= 0 ? "out_of_stock" : (totalAvailable <= 10 ? "low_stock" : "in_stock");

    res.status(200).json({
      success: true,
      data: {
        product,
        brand: brandObject,
        categories: categoryObject ? [categoryObject] : [],
        media: normalizedMedia,
        variants,
        variantMedia,
        attributes,
        inventory: {
          totalAvailable,
          status: inventoryStatus,
          variantStocks: inventoryBalances
        },
        price: {
          currentPrice: product.price,
          compareAtPrice: product.compareAtPrice,
          currency: "VND"
        },
        reviewSummary: reviewSummary,
        reviewMediaPreview: reviewSummary.reviewMediaPreview,
        isWishlisted,
        skinMatch,
        relatedProducts: {
          items: relatedProducts,
          page: 1,
          limit: 10,
          hasMore: false
        }
      }
    });
  } catch (error) {
    console.error("[MobileAPI] Error fetching product detail:", error);
    res.status(500).json({ success: false, message: "Internal server error" });
  }
};

module.exports = {
  getProductDetail
};
