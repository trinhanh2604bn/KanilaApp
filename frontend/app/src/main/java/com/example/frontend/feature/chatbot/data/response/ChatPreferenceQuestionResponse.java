package com.example.frontend.feature.chatbot.data.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChatPreferenceQuestionResponse {
    @SerializedName("question_type")
    private String questionType;

    @SerializedName("question")
    private String question;

    @SerializedName("options")
    private List<String> options;

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
