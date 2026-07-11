package com.example.frontend.data.model.product;

import java.util.ArrayList;
import java.util.List;

public class ProductFilterParams {
    public List<String> skinTypes = new ArrayList<>();
    public List<String> tones = new ArrayList<>();
    public List<String> brandIds = new ArrayList<>();
    public List<String> ingredients = new ArrayList<>();
    public List<String> ingredientFlags = new ArrayList<>();
    public List<String> concerns = new ArrayList<>();

    public String minPrice = "";
    public String maxPrice = "";
    public String minRating = "";

    public boolean sensitiveOnly = false;
    public boolean bestSellerOnly = false;

    // Keep this for UI state only. Do not send it to backend yet.
    public boolean arOnly = false;

    public boolean isEmpty() {
        return skinTypes.isEmpty()
                && tones.isEmpty()
                && brandIds.isEmpty()
                && ingredients.isEmpty()
                && ingredientFlags.isEmpty()
                && concerns.isEmpty()
                && (minPrice == null || minPrice.trim().isEmpty())
                && (maxPrice == null || maxPrice.trim().isEmpty())
                && (minRating == null || minRating.trim().isEmpty())
                && !sensitiveOnly
                && !bestSellerOnly
                && !arOnly;
    }
}
