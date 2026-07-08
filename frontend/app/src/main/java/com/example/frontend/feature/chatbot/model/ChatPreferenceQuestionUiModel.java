package com.example.frontend.feature.chatbot.model;

import java.util.List;

public class ChatPreferenceQuestionUiModel {
    private final String questionType;
    private final String question;
    private final List<String> options;

    public ChatPreferenceQuestionUiModel(String questionType, String question, List<String> options) {
        this.questionType = questionType;
        this.question = question;
        this.options = options;
    }

    public String getQuestionType() {
        return questionType;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return options;
    }
}
