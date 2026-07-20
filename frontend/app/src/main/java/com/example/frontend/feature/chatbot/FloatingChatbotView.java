package com.example.frontend.feature.chatbot;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.frontend.R;

public class FloatingChatbotView extends FrameLayout {

    private float dX, dY;
    private float startX, startY;
    private int parentWidth, parentHeight;
    private boolean isDragging = false;
    private static final int CLICK_THRESHOLD = 10;

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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dX = getX() - event.getRawX();
                dY = getY() - event.getRawY();
                startX = event.getRawX();
                startY = event.getRawY();
                isDragging = false;

                if (getParent() instanceof View) {
                    parentWidth = ((View) getParent()).getWidth();
                    parentHeight = ((View) getParent()).getHeight();
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                float newX = event.getRawX() + dX;
                float newY = event.getRawY() + dY;

                if (Math.abs(event.getRawX() - startX) > CLICK_THRESHOLD || Math.abs(event.getRawY() - startY) > CLICK_THRESHOLD) {
                    isDragging = true;
                }

                if (parentWidth > 0 && parentHeight > 0) {
                    newX = Math.max(0, Math.min(newX, parentWidth - getWidth()));
                    newY = Math.max(0, Math.min(newY, parentHeight - getHeight()));
                }

                setX(newX);
                setY(newY);
                return true;

            case MotionEvent.ACTION_UP:
                if (!isDragging) {
                    performClick();
                } else {
                    snapToEdge();
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void snapToEdge() {
        if (parentWidth == 0 || parentHeight == 0) return;

        float currentX = getX();
        float currentY = getY();

        float distLeft = currentX;
        float distRight = parentWidth - currentX - getWidth();
        float distTop = currentY;
        float distBottom = parentHeight - currentY - getHeight();

        float min = Math.min(Math.min(distLeft, distRight), Math.min(distTop, distBottom));

        float targetX = currentX;
        float targetY = currentY;

        if (min == distLeft) {
            targetX = 0;
        } else if (min == distRight) {
            targetX = parentWidth - getWidth();
        } else if (min == distTop) {
            targetY = 0;
        } else if (min == distBottom) {
            targetY = parentHeight - getHeight();
        }

        animate()
                .x(targetX)
                .y(targetY)
                .setDuration(300)
                .start();
    }
}
