package com.example.frontend.feature.chatbot.data.response;

import com.google.gson.annotations.SerializedName;

public class ChatOrderTimelineResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("label")
    private String label;

    @SerializedName("time")
    private String time;

    @SerializedName("description")
    private String description;

    public String getStatus() {
        return status;
    }

    public String getLabel() {
        return label;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }
}
