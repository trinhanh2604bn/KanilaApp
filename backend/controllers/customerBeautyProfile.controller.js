const customerBeautyProfileService = require("../services/customerBeautyProfile.service");
const { validateCustomerBeautyProfile } = require("../validations/customerBeautyProfile.validation");
const validateObjectId = require("../utils/validateObjectId");

const getProfile = async (req, res) => {
  try {
    const { customer_id } = req.params;
    if (!validateObjectId(customer_id)) {
      return res.status(400).json({ success: false, message: "Invalid customer ID" });
    }
    const profile = await customerBeautyProfileService.getProfileByCustomerId(customer_id);
    if (!profile) {
      return res.status(404).json({ success: false, message: "Customer beauty profile not found" });
    }
    return res.status(200).json({
      success: true,
      message: "Fetched customer beauty profile successfully",
      data: profile,
      error: null
    });
  } catch (error) {
    return res.status(500).json({ success: false, message: error.message, error: error.message });
  }
};

const upsertProfile = async (req, res) => {
  try {
    const { customer_id } = req.params;
    if (!validateObjectId(customer_id)) {
      return res.status(400).json({ success: false, message: "Invalid customer ID" });
    }

    const validationErrors = await validateCustomerBeautyProfile(req.body);
    if (validationErrors.length > 0) {
      return res.status(400).json({ success: false, message: "Validation error", error: validationErrors });
    }

    const profile = await customerBeautyProfileService.upsertProfile(customer_id, req.body);
    
    return res.status(200).json({
      success: true,
      message: "Upserted customer beauty profile successfully",
      data: profile,
      error: null
    });
  } catch (error) {
    return res.status(500).json({ success: false, message: error.message, error: error.message });
  }
};

module.exports = {
  getProfile,
  upsertProfile,
};
