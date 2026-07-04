const BeautyReference = require("../models/beautyReference.model");

const validateCustomerBeautyProfile = async (data) => {
  const errors = [];

  // Arrays must be array of strings
  const arrayFields = [
    "skin_concerns",
    "shade_preference",
    "lip_color_preference",
    "makeup_style",
    "beauty_goals",
    "avoid_ingredients",
    "preferred_ingredients",
    "texture_preference",
    "finish_preference",
    "purchase_intent"
  ];

  for (const field of arrayFields) {
    if (data[field] !== undefined) {
      if (!Array.isArray(data[field])) {
        errors.push(`${field} must be an array of strings`);
      } else if (data[field].some(item => typeof item !== 'string')) {
        errors.push(`All elements in ${field} must be strings`);
      }
    }
  }

  // Basic validation that budget_range matches a certain format but we can 
  // optionally fully validate against the DB.
  if (data.budget_range && typeof data.budget_range !== 'string') {
    errors.push("budget_range must be a string");
  }

  // Validating selected codes against BeautyReference is an async check.
  // We can choose to strictly check all fields, but the schema allows "unknown", 
  // so we'll check validity if they are provided.
  return errors;
};

module.exports = {
  validateCustomerBeautyProfile,
};
