const mongoose = require("mongoose");
const ProductArConfig = require("../models/productArConfig.model");
const Product = require("../models/product.model");

// @desc    Get AR config by product ID
// @route   GET /api/ar-config/:productId
// @route   GET /api/products/:productId/ar-config  (alias)
// @access  Public
exports.getArConfigByProductId = async (req, res) => {
  try {
    const { productId } = req.params;

    // Build query supporting both ObjectId and plain string
    const queries = [{ product_id: productId }];
    if (mongoose.Types.ObjectId.isValid(productId)) {
      queries.push({ product_id: new mongoose.Types.ObjectId(productId) });
    }

    const config = await ProductArConfig.findOne({ $or: queries });

    if (!config) {
      return res.status(404).json({
        success: false,
        message: "AR configuration not found for this product",
      });
    }

    // Map to format the Android app expects
    const responseData = {
      product_id: config.product_id?.toString(),
      product_name: config.product_name,
      status: config.status || "active",
      ar_type: config.ar_type || "LIPS",
      renderer_version: config.renderer_version || "v2",
      variants: (config.variants || []).filter((v) => v.enabled !== false),
    };

    res.status(200).json({
      success: true,
      data: responseData,
    });
  } catch (error) {
    console.error("[ArConfig] getArConfigByProductId error:", error);
    res.status(500).json({
      success: false,
      message: "Server Error",
      error: error.message,
    });
  }
};

// @desc    Create or update AR config for a product
// @route   POST /api/ar-config/:productId
// @access  Private (Admin)
exports.upsertArConfig = async (req, res) => {
  try {
    const { productId } = req.params;
    const body = req.body;

    const product = await Product.findById(productId);
    if (!product) {
      return res.status(404).json({
        success: false,
        message: "Product not found",
      });
    }

    const config = await ProductArConfig.findOneAndUpdate(
      {
        $or: [
          { product_id: productId },
          ...(mongoose.Types.ObjectId.isValid(productId)
            ? [{ product_id: new mongoose.Types.ObjectId(productId) }]
            : []),
        ],
      },
      { ...body, product_id: productId },
      { upsert: true, new: true, setDefaultsOnInsert: true }
    );

    res.status(200).json({
      success: true,
      data: config,
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      message: "Server Error",
      error: error.message,
    });
  }
};
