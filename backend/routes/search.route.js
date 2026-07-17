"use strict";

const express  = require("express");
const router   = express.Router();
const ctrl     = require("../controllers/search.controller");

// Public endpoints — no auth required
router.get("/discovery",    ctrl.getDiscovery);
router.get("/suggestions",  ctrl.getSuggestions);
router.get("/products",     ctrl.searchProducts);
router.get("/scan",         ctrl.scanSearch);

// Analytics — optional auth
router.post("/events", ctrl.recordEvent);

module.exports = router;
