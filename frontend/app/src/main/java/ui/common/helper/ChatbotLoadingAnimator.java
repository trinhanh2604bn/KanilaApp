package ui.common.helper;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.example.frontend.R;

import java.util.ArrayList;
import java.util.List;

public class ChatbotLoadingAnimator {

    private final View root;
    private final View robot;
    private final View bubble;
    private final View dot1;
    private final View dot2;
    private final View dot3;

    private final float robotLift;
    private final float dotLift;

    private final List<ValueAnimator> animators = new ArrayList<>();

    public ChatbotLoadingAnimator(View root) {
        this.root = root;
        this.robot = root.findViewById(R.id.ivLoadingRobot);
        this.bubble = root.findViewById(R.id.containerTypingBubble);
        this.dot1 = root.findViewById(R.id.typingDot1);
        this.dot2 = root.findViewById(R.id.typingDot2);
        this.dot3 = root.findViewById(R.id.typingDot3);

        this.robotLift = root.getResources().getDimension(R.dimen.chatbot_loading_robot_lift);
        this.dotLift = root.getResources().getDimension(R.dimen.chatbot_loading_dot_lift);
    }

    public void start() {
        stop();

        addRobotFloatAnimation();
        addBubbleBreathingAnimation();
        addDotTypingAnimation(dot1, 0L);
        addDotTypingAnimation(dot2, 140L);
        addDotTypingAnimation(dot3, 280L);

        for (ValueAnimator animator : animators) {
            animator.start();
        }
    }

    public void stop() {
        for (ValueAnimator animator : animators) {
            animator.cancel();
        }
        animators.clear();

        resetView(robot);
        resetView(bubble);
        resetView(dot1);
        resetView(dot2);
        resetView(dot3);
    }

    private void addRobotFloatAnimation() {
        if (robot == null) return;

        ObjectAnimator animator = ObjectAnimator.ofFloat(
                robot,
                View.TRANSLATION_Y,
                0f,
                -robotLift,
                0f
        );

        animator.setDuration(1500L);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        animators.add(animator);
    }

    private void addBubbleBreathingAnimation() {
        if (bubble == null) return;

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(
                bubble,
                View.SCALE_X,
                1f,
                1.06f,
                1f
        );

        ObjectAnimator scaleY = ObjectAnimator.ofFloat(
                bubble,
                View.SCALE_Y,
                1f,
                1.06f,
                1f
        );

        ObjectAnimator alpha = ObjectAnimator.ofFloat(
                bubble,
                View.ALPHA,
                0.82f,
                1f,
                0.82f
        );

        scaleX.setDuration(1100L);
        scaleY.setDuration(1100L);
        alpha.setDuration(1100L);

        scaleX.setRepeatCount(ValueAnimator.INFINITE);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        alpha.setRepeatCount(ValueAnimator.INFINITE);

        scaleX.setRepeatMode(ValueAnimator.RESTART);
        scaleY.setRepeatMode(ValueAnimator.RESTART);
        alpha.setRepeatMode(ValueAnimator.RESTART);

        scaleX.setInterpolator(new DecelerateInterpolator());
        scaleY.setInterpolator(new DecelerateInterpolator());
        alpha.setInterpolator(new DecelerateInterpolator());

        animators.add(scaleX);
        animators.add(scaleY);
        animators.add(alpha);
    }

    private void addDotTypingAnimation(View dot, long delay) {
        if (dot == null) return;

        ObjectAnimator jump = ObjectAnimator.ofFloat(
                dot,
                View.TRANSLATION_Y,
                0f,
                -dotLift,
                0f
        );

        ObjectAnimator alpha = ObjectAnimator.ofFloat(
                dot,
                View.ALPHA,
                0.35f,
                1f,
                0.35f
        );

        jump.setDuration(780L);
        alpha.setDuration(780L);

        jump.setStartDelay(delay);
        alpha.setStartDelay(delay);

        jump.setRepeatCount(ValueAnimator.INFINITE);
        alpha.setRepeatCount(ValueAnimator.INFINITE);

        jump.setRepeatMode(ValueAnimator.RESTART);
        alpha.setRepeatMode(ValueAnimator.RESTART);

        jump.setInterpolator(new AccelerateDecelerateInterpolator());
        alpha.setInterpolator(new AccelerateDecelerateInterpolator());

        animators.add(jump);
        animators.add(alpha);
    }

    private void resetView(View view) {
        if (view == null) return;

        view.setTranslationY(0f);
        view.setScaleX(1f);
        view.setScaleY(1f);
        view.setAlpha(1f);
    }
}