const ProductVariant = require('../models/productVariant.model');
const Product = require('../models/product.model');
const ArTryOnEvent = require('../models/arTryOnEvent.model');

exports.getArConfig = async (req, res) => {
    try {
        const { productId } = req.params;

        if (!productId || !productId.match(/^[0-9a-fA-F]{24}$/)) {
            return res.status(400).json({ status: "ERROR", error: "Invalid product ID" });
        }

        const product = await Product.findById(productId);
        if (!product) {
            return res.status(404).json({ status: "ERROR", error: "Product not found" });
        }

        const variants = await ProductVariant.find({ 
            productId: productId, 
            variantStatus: "active",
            "ar_config.enabled": true
        });

        if (variants.length === 0) {
            return res.status(200).json({
                success: true,
                data: {
                    status: "NOT_SUPPORTED",
                    product_id: productId,
                    variants: []
                }
            });
        }

        // Hardcoded 5 colors for testing AR Try-On as requested
        const formattedVariants = [
            {
                variant_id: "AR-TEST-01",
                sku: "AR-TEST-01",
                variant_name: "Màu 1",
                shade_hex: "#FF99CC",
                finish_type: "MATTE",
                opacity: 0.62,
                price: 239000,
                currency_code: "VND",
                in_stock: true,
                thumbnail_url: ""
            },
            {
                variant_id: "AR-TEST-02",
                sku: "AR-TEST-02",
                variant_name: "Màu 2",
                shade_hex: "#FFF0F5",
                finish_type: "SATIN",
                opacity: 0.60,
                price: 249000,
                currency_code: "VND",
                in_stock: true,
                thumbnail_url: ""
            },
            {
                variant_id: "AR-TEST-03",
                sku: "AR-TEST-03",
                variant_name: "Màu 3",
                shade_hex: "#CD9B9B",
                finish_type: "MATTE",
                opacity: 0.58,
                price: 259000,
                currency_code: "VND",
                in_stock: true,
                thumbnail_url: ""
            },
            {
                variant_id: "AR-TEST-04",
                sku: "AR-TEST-04",
                variant_name: "Màu 4",
                shade_hex: "#CD5C5C",
                finish_type: "TINT",
                opacity: 0.50,
                price: 239000,
                currency_code: "VND",
                in_stock: true,
                thumbnail_url: ""
            },
            {
                variant_id: "AR-TEST-05",
                sku: "AR-TEST-05",
                variant_name: "Màu 5",
                shade_hex: "#EE6363",
                finish_type: "SATIN",
                opacity: 0.55,
                price: 249000,
                currency_code: "VND",
                in_stock: true,
                thumbnail_url: ""
            }
        ];

        res.status(200).json({
            success: true,
            data: {
                status: "READY",
                product_id: productId,
                ar_type: "LIP",
                renderer_version: "lip_v1",
                disclaimer: "Màu thực tế có thể thay đổi tùy ánh sáng, camera, màu môi tự nhiên và màn hình.",
                variants: formattedVariants
            }
        });

    } catch (error) {
        console.error("Error in getArConfig:", error);
        res.status(500).json({ status: "ERROR", error: "Internal server error" });
    }
};

exports.postBatchEvents = async (req, res) => {
    try {
        const { session_uuid, product_id, events } = req.body;

        if (!session_uuid || !product_id || !Array.isArray(events)) {
            return res.status(400).json({ error: "Invalid request payload" });
        }

        if (events.length > 50) {
            return res.status(400).json({ error: "Batch too large, max 50 events allowed" });
        }

        let accepted_count = 0;
        let rejected_count = 0;

        for (const evt of events) {
            try {
                // Strip NoSQL operators (simple check)
                if (evt.metadata) {
                    const metaStr = JSON.stringify(evt.metadata);
                    if (metaStr.includes('"$')) {
                        throw new Error("Invalid metadata keys");
                    }
                }

                await ArTryOnEvent.create({
                    session_uuid,
                    product_id,
                    customer_id: req.user ? req.user._id : null,
                    event_type: evt.event_type,
                    variant_id: evt.variant_id,
                    occurred_at: evt.occurred_at,
                    metadata: evt.metadata
                });
                accepted_count++;
            } catch (err) {
                rejected_count++;
            }
        }

        res.status(200).json({
            accepted_count,
            rejected_count
        });

    } catch (error) {
        console.error("Error in postBatchEvents:", error);
        res.status(500).json({ error: "Internal server error" });
    }
};
