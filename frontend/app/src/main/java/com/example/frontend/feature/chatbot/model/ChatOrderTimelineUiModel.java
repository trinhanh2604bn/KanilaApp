package com.example.frontend.feature.chatbot.model;

public class ChatOrderTimelineUiModel {
    private final String status;
    private final String label;
    private final String time;
    private final String description;

    public ChatOrderTimelineUiModel(String status, String label, String time, String description) {
        this.status = status;
        this.label = label;
        this.time = time;
        this.description = description;
    }

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
