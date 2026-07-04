const beautyReferenceService = require("../services/beautyReference.service");
const { validateCreateBeautyReference, validateUpdateBeautyReference } = require("../validations/beautyReference.validation");

const getReferences = async (req, res) => {
  try {
    const items = await beautyReferenceService.getReferences(req.query);
    return res.status(200).json({
      success: true,
      message: "Fetched beauty references successfully",
      data: items,
      error: null
    });
  } catch (error) {
    return res.status(500).json({ success: false, message: error.message, error: error.message });
  }
};

const getReferenceGroup = async (req, res) => {
  try {
    const { reference_group } = req.params;
    const items = await beautyReferenceService.getReferenceGroup(reference_group);
    return res.status(200).json({
      success: true,
      message: `Fetched ${reference_group} references successfully`,
      data: items,
      error: null
    });
  } catch (error) {
    return res.status(500).json({ success: false, message: error.message, error: error.message });
  }
};

const createReference = async (req, res) => {
  try {
    const validationErrors = validateCreateBeautyReference(req.body);
    if (validationErrors.length > 0) {
      return res.status(400).json({ success: false, message: "Validation error", error: validationErrors });
    }
    const newRef = await beautyReferenceService.createReference(req.body);
    return res.status(201).json({
      success: true,
      message: "Created beauty reference successfully",
      data: newRef,
      error: null
    });
  } catch (error) {
    return res.status(500).json({ success: false, message: error.message, error: error.message });
  }
};

const updateReference = async (req, res) => {
  try {
    const { id } = req.params;
    const validationErrors = validateUpdateBeautyReference(req.body);
    if (validationErrors.length > 0) {
      return res.status(400).json({ success: false, message: "Validation error", error: validationErrors });
    }
    const updatedRef = await beautyReferenceService.updateReference(id, req.body);
    if (!updatedRef) {
      return res.status(404).json({ success: false, message: "Beauty reference not found", error: "Not found" });
    }
    return res.status(200).json({
      success: true,
      message: "Updated beauty reference successfully",
      data: updatedRef,
      error: null
    });
  } catch (error) {
    return res.status(500).json({ success: false, message: error.message, error: error.message });
  }
};

const deleteReference = async (req, res) => {
  try {
    const { id } = req.params;
    const deletedRef = await beautyReferenceService.deleteReference(id);
    if (!deletedRef) {
      return res.status(404).json({ success: false, message: "Beauty reference not found", error: "Not found" });
    }
    return res.status(200).json({
      success: true,
      message: "Deleted beauty reference (soft delete) successfully",
      data: deletedRef,
      error: null
    });
  } catch (error) {
    return res.status(500).json({ success: false, message: error.message, error: error.message });
  }
};

module.exports = {
  getReferences,
  getReferenceGroup,
  createReference,
  updateReference,
  deleteReference,
};
