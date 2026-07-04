package com.example.frontend;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.frontend.feature.home.HomeBannerAdapter;
import com.example.frontend.model.HomeBannerItem;

import java.util.ArrayList;
import java.util.List;

import ui.category.ProductCategoryFragment;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 vpHomeBanner;
    private View layoutSearchBar;
    private View layoutSearchExpandedBar;
    private ImageButton btnNotification, btnCart, btnWishlist;
    private EditText edtExpandedSearchQuery;
    private ImageButton btnExpandedSearchBack;

    private HomeBannerAdapter bannerAdapter;
    private final Handler autoSlideHandler = new Handler(Looper.getMainLooper());
    private Runnable autoSlideRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupSearchBehavior();
        setupBannerSlider();
        setupBackPressedHandling();
    }

    private void initViews() {
        vpHomeBanner = findViewById(R.id.vpHomeBanner);
        layoutSearchBar = findViewById(R.id.layoutSearchBar);
        layoutSearchExpandedBar = findViewById(R.id.layoutSearchExpandedBar);
        btnNotification = findViewById(R.id.btnNotification);
        btnCart = findViewById(R.id.btnCart);
        btnWishlist = findViewById(R.id.btnWishlist);

        edtExpandedSearchQuery = findViewById(R.id.edtExpandedSearchQuery);
        btnExpandedSearchBack = findViewById(R.id.btnExpandedSearchBack);
    }

    private void setupSearchBehavior() {
        layoutSearchBar.setOnClickListener(v -> showExpandedSearch());

        btnExpandedSearchBack.setOnClickListener(v -> collapseExpandedSearch());

        btnNotification.setOnClickListener(v -> Toast.makeText(this, R.string.notification, Toast.LENGTH_SHORT).show());
        btnCart.setOnClickListener(v -> Toast.makeText(this, R.string.cart, Toast.LENGTH_SHORT).show());
        btnWishlist.setOnClickListener(v -> Toast.makeText(this, R.string.wishlist, Toast.LENGTH_SHORT).show());


//        btnCart.setOnClickListener(v -> {
            // Tạm thời thay thế bằng việc mở CheckoutFragment để xem giao diện
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.main, new ui.commerce.CheckoutFragment())
//                    .addToBackStack(null)
//                    .commit();
//        });
    }

    private void showExpandedSearch() {
        layoutSearchBar.setVisibility(View.GONE);
        layoutSearchExpandedBar.setVisibility(View.VISIBLE);

        edtExpandedSearchQuery.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(edtExpandedSearchQuery, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void collapseExpandedSearch() {
        layoutSearchExpandedBar.setVisibility(View.GONE);
        layoutSearchBar.setVisibility(View.VISIBLE);

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtExpandedSearchQuery.getWindowToken(), 0);
        }
    }

    private boolean isSearchExpanded() {
        return layoutSearchExpandedBar.getVisibility() == View.VISIBLE;
    }

    private void setupBackPressedHandling() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isSearchExpanded()) {
                    collapseExpandedSearch();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void setupBannerSlider() {
        bannerAdapter = new HomeBannerAdapter();
        bannerAdapter.setOnBannerClickListener(item -> {
            Toast.makeText(this, "Clicked: " + item.getButtonText(), Toast.LENGTH_SHORT).show();
            // TODO: Handle deeplink navigation (item.getDeepLinkType(), item.getDeepLinkValue())
        });

        vpHomeBanner.setAdapter(bannerAdapter);
        vpHomeBanner.setOffscreenPageLimit(3);

        // Add a CompositePageTransformer for a smoother, more modern transition effect
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        // MarginPageTransformer provides consistent spacing between items
        compositePageTransformer.addTransformer(new MarginPageTransformer((int) getResources().getDimension(R.dimen.spacing_s)));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            // Smoothly scale the items as they move
            page.setScaleY(0.85f + r * 0.15f);
            // Fade out the items that are not in focus
            page.setAlpha(0.5f + r * 0.5f);

            // Adjust translation to keep neighbors partially visible and neatly tucked
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

        // Start at a large enough position to allow backward scrolling if needed
        // Choosing a multiple of items.size() ensures we start at the first item
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

                // Use custom smooth scroll for a more fluid movement
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

    /**
     * Performs a smooth scroll to the specified position with a custom duration.
     */
    private void smoothScrollTo(int position, long duration) {
        if (vpHomeBanner.isFakeDragging()) return;

        int currentItem = vpHomeBanner.getCurrentItem();
        int itemsToScroll = position - currentItem;

        // Handle wrap-around for a smoother loop feel (optional, but here we just follow position)
        // If we want to always scroll forward even at the end, we'd need a different adapter setup.

        ValueAnimator animator = ValueAnimator.ofFloat(0, 1f);
        final float[] previousStep = {0f};

        // Calculate the total pixels to scroll
        // In ViewPager2, one page scroll corresponds to its full width
        float totalPxToDrag = (float) vpHomeBanner.getWidth() * itemsToScroll;

        animator.addUpdateListener(animation -> {
            if (!vpHomeBanner.isFakeDragging()) return;

            float currentStep = (float) animation.getAnimatedValue();
            float deltaStep = currentStep - previousStep[0];
            float pixelsToDragNow = deltaStep * totalPxToDrag;

            try {
                vpHomeBanner.fakeDragBy(-pixelsToDragNow);
            } catch (Exception e) {
                // Fail-safe in case fake drag is interrupted
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
                if (vpHomeBanner.isFakeDragging()) {
                    vpHomeBanner.endFakeDrag();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (vpHomeBanner.isFakeDragging()) {
                    vpHomeBanner.endFakeDrag();
                }
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
