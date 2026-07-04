const ALLOWED_GROUPS = [
  "skin_type",
  "skin_concern",
  "sensitivity_level",
  "skin_tone",
  "undertone",
  "shade_preference",
  "lip_color_preference",
  "makeup_style",
  "beauty_goal",
  "avoid_ingredient",
  "preferred_ingredient",
  "shopping_preference",
  "budget_range",
  "texture_preference",
  "finish_preference",
  "fragrance_preference",
  "purchase_intent",
];

const validateCreateBeautyReference = (data) => {
  const errors = [];
  
  if (!data.reference_group || !ALLOWED_GROUPS.includes(data.reference_group)) {
    errors.push(`reference_group is required and must be one of: ${ALLOWED_GROUPS.join(', ')}`);
  }
  
  if (!data.reference_code || typeof data.reference_code !== 'string') {
    errors.push("reference_code is required and must be a string");
  } else if (!/^[a-z0-9_]+$/.test(data.reference_code)) {
    errors.push("reference_code must be lowercase snake_case");
  }

  if (!data.display_name_vi || typeof data.display_name_vi !== 'string') {
    errors.push("display_name_vi is required and must be a string");
  }

  if (data.recommendation_weight !== undefined && typeof data.recommendation_weight !== "number") {
    errors.push("recommendation_weight must be a number");
  }

  return errors;
};

const validateUpdateBeautyReference = (data) => {
  const errors = [];
  
  if (data.reference_group && !ALLOWED_GROUPS.includes(data.reference_group)) {
    errors.push(`reference_group must be one of: ${ALLOWED_GROUPS.join(', ')}`);
  }
  
  if (data.reference_code !== undefined) {
    if (typeof data.reference_code !== 'string') {
      errors.push("reference_code must be a string");
    } else if (!/^[a-z0-9_]+$/.test(data.reference_code)) {
      errors.push("reference_code must be lowercase snake_case");
    }
  }

  if (data.display_name_vi !== undefined && (typeof data.display_name_vi !== 'string' || !data.display_name_vi.trim())) {
    errors.push("display_name_vi must be a non-empty string");
  }

  if (data.recommendation_weight !== undefined && typeof data.recommendation_weight !== "number") {
    errors.push("recommendation_weight must be a number");
  }

  return errors;
};

module.exports = {
  validateCreateBeautyReference,
  validateUpdateBeautyReference,
};
