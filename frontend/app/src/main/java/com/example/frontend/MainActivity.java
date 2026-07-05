package com.example.frontend;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.frontend.feature.home.HomeBannerAdapter;
import com.example.frontend.feature.home.HomeProductAdapter;
import com.example.frontend.feature.home.HomeShortcutAdapter;
import com.example.frontend.feature.home.HomeViewModel;
import com.example.frontend.feature.search.SearchActivity;
import com.example.frontend.model.HomeBannerItem;
import com.example.frontend.model.HomeShortcutItem;

import java.util.ArrayList;
import java.util.List;

import ui.category.ProductCategoryFragment;
import ui.commerce.CartFragment;
import ui.commerce.CheckoutFragment;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 vpHomeBanner;
    private View layoutSearchBar;
    private ImageButton btnNotification, btnCart, btnWishlist;
    private RecyclerView rvHomeShortcuts;
    private RecyclerView rvRecommendedProducts;
    private View layoutHomeStateContainer, viewHomeLoading, viewHomeError;

    private View layoutKanilaReelsCard, layoutReelThumbOne, layoutReelThumbTwo;
    private ImageView ivReelThumbOne, ivReelThumbTwo;
    private View layoutKanilaChallengeCard, btnJoinChallenge;
    private TextView tvChallengeProgress, tvChallengeParticipants, tvChallengeReward;

    private View layoutSearchExpandedBar;
    private EditText edtExpandedSearchQuery;
    private ImageButton btnExpandedSearchBack;

    private HomeBannerAdapter bannerAdapter;
    private HomeShortcutAdapter shortcutAdapter;
    private HomeProductAdapter productAdapter;
    private HomeViewModel viewModel;

    private final Handler autoSlideHandler = new Handler(Looper.getMainLooper());
    private Runnable autoSlideRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupSearchBehavior();
        setupBannerSlider();
        setupProductList();

        observeViewModel();
        viewModel.loadHomeData();
    }

    private void initViews() {
        vpHomeBanner = findViewById(R.id.vpHomeBanner);
        layoutSearchBar = findViewById(R.id.layoutSearchBar);
        btnNotification = findViewById(R.id.btnNotification);
        btnCart = findViewById(R.id.btnCart);
        btnWishlist = findViewById(R.id.btnWishlist);
        rvHomeShortcuts = findViewById(R.id.rvHomeShortcuts);
        rvRecommendedProducts = findViewById(R.id.rvRecommendedProducts);
        layoutHomeStateContainer = findViewById(R.id.layoutHomeStateContainer);
        viewHomeLoading = findViewById(R.id.viewHomeLoading);
        viewHomeError = findViewById(R.id.viewHomeError);

        layoutKanilaReelsCard = findViewById(R.id.layoutKanilaReelsCard);
        layoutReelThumbOne = findViewById(R.id.layoutReelThumbOne);
        layoutReelThumbTwo = findViewById(R.id.layoutReelThumbTwo);
        ivReelThumbOne = findViewById(R.id.ivReelThumbOne);
        ivReelThumbTwo = findViewById(R.id.ivReelThumbTwo);
        layoutKanilaChallengeCard = findViewById(R.id.layoutKanilaChallengeCard);
        btnJoinChallenge = findViewById(R.id.btnJoinChallenge);
        tvChallengeProgress = findViewById(R.id.tvChallengeProgress);
        tvChallengeParticipants = findViewById(R.id.tvChallengeParticipants);
        tvChallengeReward = findViewById(R.id.tvChallengeReward);

        layoutSearchExpandedBar = findViewById(R.id.layoutSearchExpandedBar);
        edtExpandedSearchQuery = findViewById(R.id.edtExpandedSearchQuery);
        btnExpandedSearchBack = findViewById(R.id.btnExpandedSearchBack);
    }

    private void setupSearchBehavior() {
        layoutSearchBar.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        });


        //btnNotification.setOnClickListener(v -> Toast.makeText(this, R.string.notification, Toast.LENGTH_SHORT).show());

        btnNotification.setOnClickListener(v -> {
            // Tạm thời thay thế bằng việc mở CheckoutFragment để xem giao diện
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main, new ui.notification.NotificationCenterFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnCart.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main, new ui.commerce.CartFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnWishlist.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main, new ui.category.ProductCategoryFragment())
                    .addToBackStack(null)
                    .commit();
        });

        setupHomeShortcuts();
        setupSocialSection();
    }

    private void setupProductList() {
        productAdapter = new HomeProductAdapter();
        productAdapter.setOnProductClickListener(product -> {
            Toast.makeText(this, "Product: " + product.getName(), Toast.LENGTH_SHORT).show();
        });

        rvRecommendedProducts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvRecommendedProducts.setAdapter(productAdapter);
    }

    private void observeViewModel() {
        viewModel.getUiState().observe(this, state -> {
            if (state == null) return;

            if (state.loading) {
                showLoading();
            } else if (state.error != null) {
                showError(state.error);
            } else if (state.products != null) {
                showContent();
                productAdapter.setProducts(state.products);
            }
        });
    }

    private void showLoading() {
        layoutHomeStateContainer.setVisibility(View.VISIBLE);
        viewHomeLoading.setVisibility(View.VISIBLE);
        viewHomeError.setVisibility(View.GONE);
        rvRecommendedProducts.setVisibility(View.GONE);
    }

    private void showContent() {
        layoutHomeStateContainer.setVisibility(View.GONE);
        rvRecommendedProducts.setVisibility(View.VISIBLE);
    }

    private void showError(String message) {
        layoutHomeStateContainer.setVisibility(View.VISIBLE);
        viewHomeLoading.setVisibility(View.GONE);
        viewHomeError.setVisibility(View.VISIBLE);
        rvRecommendedProducts.setVisibility(View.GONE);

        TextView tvError = viewHomeError.findViewById(R.id.tvErrorTitle);
        if (tvError != null) tvError.setText(message);

        View btnRetry = viewHomeError.findViewById(R.id.btnErrorRetry);
        if (btnRetry != null) btnRetry.setOnClickListener(v -> viewModel.loadHomeData());
    }

    private void setupSocialSection() {
        String reelOneUrl = "https://youtube.com/shorts/JytbqPADyQc?si=cXt-VYSr5hhdpOQg";
        String reelTwoUrl = "https://youtube.com/shorts/LwHA4UF3XQI?si=icecCgY-kTDcaYTz";

        String thumbOneUrl = "https://img.youtube.com/vi/JytbqPADyQc/0.jpg";
        String thumbTwoUrl = "https://img.youtube.com/vi/LwHA4UF3XQI/0.jpg";

        Glide.with(this).load(thumbOneUrl).into(ivReelThumbOne);
        Glide.with(this).load(thumbTwoUrl).into(ivReelThumbTwo);

        layoutKanilaReelsCard.setOnClickListener(v -> Toast.makeText(this, "Kanila Reels", Toast.LENGTH_SHORT).show());

        layoutReelThumbOne.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(reelOneUrl));
            startActivity(intent);
        });

        layoutReelThumbTwo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(reelTwoUrl));
            startActivity(intent);
        });

        layoutKanilaChallengeCard.setOnClickListener(v -> Toast.makeText(this, "Kanila Challenge", Toast.LENGTH_SHORT).show());

        btnJoinChallenge.setOnClickListener(v -> Toast.makeText(this, "Tham gia challenge", Toast.LENGTH_SHORT).show());

        tvChallengeProgress.setText(getString(R.string.home_social_challenge_progress_format, "8", "14"));
        tvChallengeParticipants.setText(getString(R.string.home_social_challenge_participants_format, "12.6K"));
        tvChallengeReward.setText(getString(R.string.home_social_challenge_reward_format, "200"));
    }

    private void setupHomeShortcuts() {
        shortcutAdapter = new HomeShortcutAdapter();
        shortcutAdapter.setOnShortcutClickListener(item -> Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show());

        rvHomeShortcuts.setAdapter(shortcutAdapter);

        List<HomeShortcutItem> shortcuts = new ArrayList<>();
        shortcuts.add(new HomeShortcutItem("orders", "Đơn hàng", R.drawable.ic_shortcut_order, "orders", "", false, false));
        shortcuts.add(new HomeShortcutItem("voucher", "Voucher", R.drawable.ic_shortcut_voucher, "voucher", "", false, false));
        shortcuts.add(new HomeShortcutItem("ar", "AR", R.drawable.ic_shortcut_ar, "ar_try_on", "", false, false));
        shortcuts.add(new HomeShortcutItem("kanila_beauty", "Kanila Beauty", R.drawable.ic_shortcut_kanila_beauty, "beauty", "", false, false));
        shortcuts.add(new HomeShortcutItem("creator", "Creator", R.drawable.ic_shortcut_creator, "creator", "", false, false));
        shortcuts.add(new HomeShortcutItem("royalty", "Royalty", R.drawable.ic_shortcut_royalty, "loyalty", "", false, false));
        shortcuts.add(new HomeShortcutItem("help", "Trợ giúp", R.drawable.ic_shortcut_help, "support", "", false, false));
        shortcuts.add(new HomeShortcutItem("policy", "Chính sách", R.drawable.ic_shortcut_policy, "policy", "", false, false));

        shortcutAdapter.setItems(shortcuts);
    }

    private void setupBannerSlider() {
        bannerAdapter = new HomeBannerAdapter();
        bannerAdapter.setOnBannerClickListener(item -> Toast.makeText(this, "Clicked: " + item.getButtonText(), Toast.LENGTH_SHORT).show());

        vpHomeBanner.setAdapter(bannerAdapter);
        vpHomeBanner.setOffscreenPageLimit(3);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer((int) getResources().getDimension(R.dimen.spacing_s)));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
            page.setAlpha(0.5f + r * 0.5f);
            float translationOffset = position * -getResources().getDimension(R.dimen.spacing_m) * 2;
            page.setTranslationX(translationOffset);
        });

        vpHomeBanner.setPageTransformer(compositePageTransformer);

        List<HomeBannerItem> items = new ArrayList<>();
        items.add(new HomeBannerItem("1", "", "", "", "Khám phá ngay", null, R.drawable.bg_slide_1, "category", "123", true, 1));
        items.add(new HomeBannerItem("2", "", "", "", "Mua ngay", null, R.drawable.bg_slide_2, "product", "456", true, 2));
        items.add(new HomeBannerItem("3", "", "", "", "Xem ưu đãi", null, R.drawable.bg_slide_3, "promotion", "789", true, 3));
        items.add(new HomeBannerItem("4", "", "", "", "Gợi ý cho bạn", null, R.drawable.bg_slide_4, "recommendation", "012", true, 4));
        items.add(new HomeBannerItem("5", "", "", "", "Nhận voucher", null, R.drawable.bg_slide_5, "voucher", "345", true, 5));

        bannerAdapter.setItems(items);

        int startPosition = (Integer.MAX_VALUE / 2) - ((Integer.MAX_VALUE / 2) % items.size());
        vpHomeBanner.setCurrentItem(startPosition, false);

        setupAutoSlide(items.size());
    }

    private void setupAutoSlide(int size) {
        if (size <= 1) return;

        autoSlideRunnable = new Runnable() {
            @Override
            public void run() {
                if (vpHomeBanner == null) return;
                int currentItem = vpHomeBanner.getCurrentItem();
                int nextItem = currentItem + 1;
                smoothScrollTo(nextItem, 800);
                autoSlideHandler.postDelayed(this, 4000);
            }
        };

        autoSlideHandler.postDelayed(autoSlideRunnable, 4000);

        vpHomeBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                autoSlideHandler.removeCallbacks(autoSlideRunnable);
                autoSlideHandler.postDelayed(autoSlideRunnable, 4000);
            }
        });
    }

    private void smoothScrollTo(int position, long duration) {
        if (vpHomeBanner.isFakeDragging()) return;

        int currentItem = vpHomeBanner.getCurrentItem();
        int itemsToScroll = position - currentItem;
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1f);
        final float[] previousStep = {0f};
        float totalPxToDrag = (float) vpHomeBanner.getWidth() * itemsToScroll;

        animator.addUpdateListener(animation -> {
            if (!vpHomeBanner.isFakeDragging()) return;
            float currentStep = (float) animation.getAnimatedValue();
            float deltaStep = currentStep - previousStep[0];
            float pixelsToDragNow = deltaStep * totalPxToDrag;
            try {
                vpHomeBanner.fakeDragBy(-pixelsToDragNow);
            } catch (Exception e) {
                animation.cancel();
            }
            previousStep[0] = currentStep;
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                vpHomeBanner.beginFakeDrag();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (vpHomeBanner.isFakeDragging()) vpHomeBanner.endFakeDrag();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (vpHomeBanner.isFakeDragging()) vpHomeBanner.endFakeDrag();
            }
        });

        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(duration);
        animator.start();
    }

    @Override
    protected void onDestroy() {
        if (autoSlideHandler != null && autoSlideRunnable != null) {
            autoSlideHandler.removeCallbacks(autoSlideRunnable);
        }
        super.onDestroy();
    }
}
