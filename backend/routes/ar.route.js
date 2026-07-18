const express = require('express');
const router = express.Router();
const arController = require('../controllers/ar.controller');

// Need auth middleware if there is one, but guest can also try AR.
// Assuming some optional auth middleware is used for context
// For MVP, we will assume standard auth middleware sets req.user or req.guest

// GET AR config for a product
router.get('/products/:productId/ar-config', arController.getArConfig);

// POST AR events batch
router.post('/ar/events/batch', arController.postBatchEvents);

module.exports = router;
