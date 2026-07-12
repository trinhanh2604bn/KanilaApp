const mongoose = require("mongoose");

const returnMediaSchema = new mongoose.Schema(
  {
    return_request_id: { type: mongoose.Schema.Types.ObjectId, ref: "Return", required: true },
    media_type: { type: String, enum: ["image", "video"], default: "image" },
    media_url: { type: String, required: true },
  },
  { timestamps: true }
);

module.exports = mongoose.model("ReturnMedia", returnMediaSchema);
