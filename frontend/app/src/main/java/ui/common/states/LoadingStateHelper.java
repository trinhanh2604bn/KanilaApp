package ui.common.states;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.example.frontend.R;
import ui.common.helper.ChatbotLoadingAnimator;

import java.util.ArrayList;
import java.util.List;

public class LoadingStateHelper {

    // --- Spinner Animation ---
    public static void startDotSpinner(View root) {
        View spinner = root.findViewById(R.id.containerDotSpinner);
        if (spinner == null) return;

        ObjectAnimator rotate = ObjectAnimator.ofFloat(spinner, "rotation", 0f, 360f);
        rotate.setDuration(1200);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setRepeatCount(ValueAnimator.INFINITE);
        rotate.start();
        spinner.setTag(R.id.containerDotSpinner, rotate);
    }

    public static void stopDotSpinner(View root) {
        View spinner = root.findViewById(R.id.containerDotSpinner);
        if (spinner != null && spinner.getTag(R.id.containerDotSpinner) instanceof Animator) {
            ((Animator) spinner.getTag(R.id.containerDotSpinner)).cancel();
        }
    }

    // --- Chatbot Loading Animator ---
    private static ChatbotLoadingAnimator getChatbotAnimator(View root) {
        ChatbotLoadingAnimator animator = (ChatbotLoadingAnimator) root.getTag(R.id.layoutChatbotLoading);
        if (animator == null) {
            animator = new ChatbotLoadingAnimator(root);
            root.setTag(R.id.layoutChatbotLoading, animator);
        }
        return animator;
    }

    // --- Skeleton Pulse Animation ---
    public static void startSkeletonPulse(View root) {
        if (root == null) return;
        List<View> placeholders = findPlaceholderViews(root);
        AnimatorSet pulseSet = new AnimatorSet();
        List<Animator> animators = new ArrayList<>();

        for (View v : placeholders) {
            ObjectAnimator alpha = ObjectAnimator.ofFloat(v, "alpha", 0.45f, 1.0f);
            alpha.setDuration(900);
            alpha.setRepeatMode(ValueAnimator.REVERSE);
            alpha.setRepeatCount(ValueAnimator.INFINITE);
            animators.add(alpha);
        }

        pulseSet.playTogether(animators);
        pulseSet.start();
        root.setTag(R.id.layoutSkeletonContainer, pulseSet);
    }

    private static List<View> findPlaceholderViews(View root) {
        List<View> placeholders = new ArrayList<>();
        if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) root;
            for (int i = 0; i < group.getChildCount(); i++) {
                placeholders.addAll(findPlaceholderViews(group.getChildAt(i)));
            }
        } else if (root.getBackground() != null || root.getId() != View.NO_ID) {
            placeholders.add(root);
        }
        return placeholders;
    }

    public static void stopSkeletonPulse(View root) {
        if (root != null && root.getTag(R.id.layoutSkeletonContainer) instanceof Animator) {
            ((Animator) root.getTag(R.id.layoutSkeletonContainer)).cancel();
            resetAlpha(root);
        }
    }

    private static void resetAlpha(View root) {
        if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) root;
            for (int i = 0; i < group.getChildCount(); i++) {
                resetAlpha(group.getChildAt(i));
            }
        } else {
            root.setAlpha(1.0f);
        }
    }

    // --- Show/Hide Helpers ---
    public static void showLoading(View loadingView, View contentView) {
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
            
            if (loadingView.findViewById(R.id.containerDotSpinner) != null) {
                startDotSpinner(loadingView);
            }
            if (loadingView.findViewById(R.id.layoutChatbotLoading) != null) {
                getChatbotAnimator(loadingView).start();
            }
            if (loadingView.findViewById(R.id.layoutSkeletonContainer) != null) {
                startSkeletonPulse(loadingView);
            }
        }
        if (contentView != null) {
            contentView.setVisibility(View.GONE);
        }
    }

    public static void hideLoading(View loadingView, View contentView) {
        if (loadingView != null) {
            stopDotSpinner(loadingView);
            if (loadingView.findViewById(R.id.layoutChatbotLoading) != null) {
                getChatbotAnimator(loadingView).stop();
            }
            stopSkeletonPulse(loadingView);
            loadingView.setVisibility(View.GONE);
        }
        if (contentView != null) {
            contentView.setVisibility(View.VISIBLE);
        }
    }
}