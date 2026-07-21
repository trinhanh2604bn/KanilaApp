const mongoose = require('mongoose');
const ProductVariant = require('../models/productVariant.model');
const Product = require('../models/product.model');
const ArTryOnEvent = require('../models/arTryOnEvent.model');

// Delegate to the canonical AR config controller
const { getArConfigByProductId } = require('./arConfig.controller');
exports.getArConfig = getArConfigByProductId;

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
