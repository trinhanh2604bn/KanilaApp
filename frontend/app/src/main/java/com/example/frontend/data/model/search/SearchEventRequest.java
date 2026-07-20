package com.example.frontend.data.model.search;

public class SearchEventRequest {
    public String type;
    public String query;
    public String sessionId;
    public Object metadata;

    public SearchEventRequest(String type, String query, String sessionId, Object metadata) {
        this.type = type;
        this.query = query;
        this.sessionId = sessionId;
        this.metadata = metadata;
    }
}
