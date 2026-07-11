package com.example.frontend.feature.beauty;

import java.util.HashMap;
import java.util.Map;

public class BeautyReferenceMapper {
    
    public static final String SKIN_TYPE = "skin_type";
    public static final String SKIN_CONCERN = "skin_concern";
    public static final String SENSITIVITY_LEVEL = "sensitivity_level";
    public static final String SKIN_COLOR = "skin_color";
    public static final String SKIN_UNDERTONE = "skin_undertone";
    public static final String FOUNDATION_FINISH = "foundation_finish";
    public static final String LIPSTICK_COLOR = "lipstick_color";
    public static final String MAKEUP_STYLE = "makeup_style";
    public static final String BUDGET = "budget";
    public static final String AVOID_INGREDIENT = "avoid_ingredient";

    private static final Map<String, String> uiToBackendMap = new HashMap<>();

    static {
        uiToBackendMap.put("skinTypeGroup", SKIN_TYPE);
        uiToBackendMap.put("skinConditionGroup", SKIN_CONCERN);
        uiToBackendMap.put("sensitivityGroup", SENSITIVITY_LEVEL);
        uiToBackendMap.put("skinColorGroup", SKIN_COLOR);
        uiToBackendMap.put("skinUndertoneGroup", SKIN_UNDERTONE);
        uiToBackendMap.put("finishGroup", FOUNDATION_FINISH);
        uiToBackendMap.put("lipstickGroup", LIPSTICK_COLOR);
        uiToBackendMap.put("makeupStyleGroup", MAKEUP_STYLE);
        uiToBackendMap.put("budgetGroup", BUDGET);
        uiToBackendMap.put("avoidIngredientsGroup", AVOID_INGREDIENT);
    }

    public static String getBackendGroup(String uiGroup) {
        return uiToBackendMap.getOrDefault(uiGroup, uiGroup);
    }
}
