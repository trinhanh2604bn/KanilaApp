const express = require("express");
const { getArConfigByProductId, upsertArConfig } = require("../controllers/arConfig.controller");

const router = express.Router({ mergeParams: true }); // mergeParams allows :productId from parent router

// Route: GET/POST /api/ar-config/:productId
// Route: GET/POST /api/products/:productId/ar-config (via alias in index.js, root path is "/")
router.route("/").get(getArConfigByProductId).post(upsertArConfig);
router.route("/:productId").get(getArConfigByProductId).post(upsertArConfig);

module.exports = router;
