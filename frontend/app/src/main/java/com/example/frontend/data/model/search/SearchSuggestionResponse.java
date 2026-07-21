package com.example.frontend.data.model.search;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SearchSuggestionResponse {

    @SerializedName("query_suggestions")
    public List<String> querySuggestions;

    @SerializedName("products")
    public List<SuggestedProduct> products;

    @SerializedName("brands")
    public List<SuggestedBrand> brands;

    @SerializedName("categories")
    public List<SuggestedCategory> categories;

    public static class SuggestedProduct {
        @SerializedName("id")
        public String id;
        @SerializedName("name")
        public String name;
        @SerializedName("imageUrl")
        public String imageUrl;
        @SerializedName("price")
        public double price;
    }

    public static class SuggestedBrand {
        @SerializedName("id")
        public String id;
        @SerializedName("name")
        public String name;
    }

    public static class SuggestedCategory {
        @SerializedName("name")
        public String name;
    }
}
