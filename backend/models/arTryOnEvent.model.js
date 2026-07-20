const mongoose = require("mongoose");

const arTryOnEventSchema = new mongoose.Schema(
  {
    session_uuid: {
      type: String,
      required: true,
      index: true,
    },
    account_id: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Account",
      default: null,
    },
    customer_id: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Customer",
      default: null,
    },
    guest_session_id: {
      type: String,
      default: null,
    },
    product_id: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Product",
      required: true,
    },
    variant_id: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "ProductVariant",
      default: null,
    },
    event_type: {
      type: String,
      enum: [
        "AR_OPENED",
        "CAMERA_PERMISSION_GRANTED",
        "CAMERA_PERMISSION_DENIED",
        "CAMERA_PERMISSION_PERMANENTLY_DENIED",
        "FACE_DETECTED",
        "FACE_LOST",
        "SHADE_SELECTED",
        "BEFORE_AFTER_USED",
        "ADD_TO_CART_STARTED",
        "ADD_TO_CART_SUCCEEDED",
        "ADD_TO_CART_FAILED",
        "AR_EXITED",
        "AR_ERROR",
      ],
      required: true,
    },
    source: {
      type: String,
      default: "APP",
    },
    occurred_at: {
      type: Date,
      required: true,
    },
    metadata: {
      type: mongoose.Schema.Types.Mixed,
      default: {},
    },
  },
  { timestamps: { createdAt: "created_at", updatedAt: false }, collection: "ar_tryon_events" }
);

arTryOnEventSchema.index({ product_id: 1, created_at: -1 });
arTryOnEventSchema.index({ event_type: 1, created_at: -1 });

module.exports = mongoose.model("ArTryOnEvent", arTryOnEventSchema);
