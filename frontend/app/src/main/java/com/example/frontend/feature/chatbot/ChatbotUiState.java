package com.example.frontend.feature.chatbot;

import com.example.frontend.feature.chatbot.model.ChatMessageUiModel;

import java.util.Collections;
import java.util.List;

public class ChatbotUiState {
    private final List<ChatMessageUiModel> messages;
    private final boolean isLoading;
    private final String error;
    private final boolean isWelcomeVisible;
    private final List<String> quickReplies;

    public ChatbotUiState(List<ChatMessageUiModel> messages, boolean isLoading, String error, boolean isWelcomeVisible) {
        this(messages, isLoading, error, isWelcomeVisible, Collections.emptyList());
    }

    public ChatbotUiState(List<ChatMessageUiModel> messages, boolean isLoading, String error, boolean isWelcomeVisible, List<String> quickReplies) {
        this.messages = messages;
        this.isLoading = isLoading;
        this.error = error;
        this.isWelcomeVisible = isWelcomeVisible;
        this.quickReplies = quickReplies;
    }

    public List<ChatMessageUiModel> getMessages() {
        return messages;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public String getError() {
        return error;
    }

    public boolean isWelcomeVisible() {
        return isWelcomeVisible;
    }

    public List<String> getQuickReplies() {
        return quickReplies;
    }

    public static ChatbotUiState empty() {
        return new ChatbotUiState(Collections.emptyList(), false, null, true, Collections.emptyList());
    }
}
