package com.example.frontend.feature.chatbot;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.frontend.R;

public class FloatingChatbotView extends FrameLayout {

    public FloatingChatbotView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public FloatingChatbotView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FloatingChatbotView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_floating_chatbot, this, true);
        setClickable(true);
        setFocusable(true);
    }
}
