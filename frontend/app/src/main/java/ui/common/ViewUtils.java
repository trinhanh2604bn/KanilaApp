package ui.common;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;

public class ViewUtils {

    @SuppressLint("ClickableViewAccessibility")
    public static void applyClickAnimation(View view) {
        if (view == null) return;
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate()
                            .scaleX(0.95f)
                            .scaleY(0.95f)
                            .setDuration(100)
                            .setInterpolator(new DecelerateInterpolator())
                            .start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(150)
                            .setInterpolator(new OvershootInterpolator())
                            .start();
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        v.performClick();
                    }
                    break;
            }
            return true;
        });
    }

    public static void customizeDialogButtons(androidx.appcompat.app.AlertDialog dialog) {
        if (dialog == null) return;
        android.widget.Button positive = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE);
        android.widget.Button negative = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE);
        int color = androidx.core.content.ContextCompat.getColor(dialog.getContext(), com.example.frontend.R.color.button);
        if (positive != null) {
            positive.setTextColor(color);
            positive.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        }
        if (negative != null) {
            negative.setTextColor(color);
            negative.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        }
    }
}
