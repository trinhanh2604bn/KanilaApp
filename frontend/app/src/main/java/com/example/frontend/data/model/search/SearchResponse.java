package com.example.frontend.data.model.search;

import com.example.frontend.model.Product;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SearchResponse {
    @SerializedName("query")
    public String query;

    @SerializedName("normalized_query")
    public String normalizedQuery;

    @SerializedName("corrected_query")
    public String correctedQuery;

    @SerializedName("items")
    public List<Product> items;

    @SerializedName("pagination")
    public Pagination pagination;

    public static class Pagination {
        @SerializedName("page")
        public int page;

        @SerializedName("limit")
        public int limit;

        @SerializedName("total")
        public int total;

        @SerializedName("total_pages")
        public int totalPages;

        @SerializedName("has_more")
        public boolean hasMore;
    }
}
