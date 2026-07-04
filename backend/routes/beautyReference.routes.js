const express = require("express");
const router = express.Router();
const beautyReferenceController = require("../controllers/beautyReference.controller");
// Assuming there might be an admin middleware, if not we keep it simple as per requirement
// const authMiddleware = require("../middlewares/auth.middleware");
// const roleMiddleware = require("../middlewares/role.middleware");

// GET /api/beauty-references
router.get("/", beautyReferenceController.getReferences);

// GET /api/beauty-references/group/:reference_group
router.get("/group/:reference_group", beautyReferenceController.getReferenceGroup);

// POST /api/beauty-references
router.post("/", beautyReferenceController.createReference);

// PUT /api/beauty-references/:id
router.put("/:id", beautyReferenceController.updateReference);

// DELETE /api/beauty-references/:id
router.delete("/:id", beautyReferenceController.deleteReference);

module.exports = router;
