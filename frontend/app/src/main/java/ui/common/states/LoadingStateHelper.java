package ui.common.states;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;

public class LoadingStateHelper {

    public static void showLoading(View loadingView, View contentView) {
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
            startPulseAnimation(loadingView);
        }
        if (contentView != null) {
            contentView.setVisibility(View.GONE);
        }
    }

    public static void hideLoading(View loadingView, View contentView) {
        if (loadingView != null) {
            stopPulseAnimation(loadingView);
            loadingView.setVisibility(View.GONE);
        }
        if (contentView != null) {
            contentView.setVisibility(View.VISIBLE);
        }
    }

    public static void startPulseAnimation(View loadingView) {
        ObjectAnimator pulse = ObjectAnimator.ofFloat(loadingView, "alpha", 0.3f, 1.0f);
        pulse.setDuration(1000);
        pulse.setRepeatMode(ValueAnimator.REVERSE);
        pulse.setRepeatCount(ValueAnimator.INFINITE);
        pulse.start();
        loadingView.setTag(pulse);
    }

    public static void stopPulseAnimation(View loadingView) {
        ObjectAnimator pulse = (ObjectAnimator) loadingView.getTag();
        if (pulse != null) {
            pulse.cancel();
            loadingView.setTag(null);
        }
        loadingView.setAlpha(1.0f);
    }
}