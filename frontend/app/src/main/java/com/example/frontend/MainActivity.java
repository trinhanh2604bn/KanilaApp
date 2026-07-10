package com.example.frontend;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.frontend.feature.chatbot.ChatConversationFragment;
import com.example.frontend.feature.home.HomeBannerAdapter;
import com.example.frontend.feature.home.HomeProductAdapter;
import com.example.frontend.feature.home.HomeShortcutAdapter;
import com.example.frontend.feature.home.HomeViewModel;
import com.example.frontend.feature.cart.CartViewModel;
import com.example.frontend.data.model.cart.AddToCartRequest;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.feature.search.SearchActivity;
import com.example.frontend.model.HomeBannerItem;
import com.example.frontend.model.HomeShortcutItem;

import java.util.ArrayList;
import java.util.List;

import ui.account.AccountFragment;
import ui.category.ProductCategoryFragment;
import ui.commerce.CartFragment;
import ui.commerce.CheckoutFragment;
import ui.common.BottomNavigationHelper;
import com.example.frontend.feature.community.reels.ReelsFeedFragment;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 vpHomeBanner;
    private View layoutSearchBar;
    private ImageButton btnNotification, btnCart, btnWishlist;
    private RecyclerView rvHomeShortcuts;
    private RecyclerView rvRecommendedProducts;
    private RecyclerView rvAllProducts;
    private View layoutHomeStateContainer, viewHomeLoading, viewHomeError;
    private View ivChatbot;
    private View layoutHomeScroll;
    private View mainFragmentContainer;

    private View layoutSearchExpandedBar;
    private EditText edtExpandedSearchQuery;
    private ImageButton btnExpandedSearchBack;

    private View layoutKanilaReelsCard, layoutReelThumbOne, layoutReelThumbTwo, layoutReelThumbThree;
    private ImageView ivReelThumbOne, ivReelThumbTwo, ivReelThumbThree;
    private android.widget.VideoView vvReelOne, vvReelTwo, vvReelThree;
    private View layoutKanilaChallengeCard, btnJoinChallenge;
    private TextView tvChallengeProgress, tvChallengeParticipants, tvChallengeReward;

    private HomeBannerAdapter bannerAdapter;
    private HomeShortcutAdapter shortcutAdapter;
    private HomeProductAdapter recommendedProductAdapter;
    private HomeProductAdapter allProductAdapter;
    private HomeViewModel viewModel;
    private com.example.frontend.feature.wishlist.WishlistViewModel wishlistViewModel;
    private CartViewModel cartViewModel;

    private final Handler autoSlideHandler = new Handler(Looper.getMainLooper());
    private Runnable autoSlideRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        wishlistViewModel = new ViewModelProvider(this).get(com.example.frontend.feature.wishlist.WishlistViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupSearchBehavior();
        setupBottomNavigation();
        setupBannerSlider();
        setupProductLists();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStackImmediate();
                    updateHomeVisibility();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                    setEnabled(true);
                }
            }
        });

        getSupportFragmentManager().addOnBackStackChangedListener(this::updateHomeVisibility);

        observeViewModel();

        // Delay home data loading slightly to ensure UI is ready and prevent ANR
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            viewModel.loadHomeData();
            checkAuthStatus();
        }, 500);
    }

    private void checkAuthStatus() {
        com.example.frontend.data.remote.TokenManager tm = com.example.frontend.data.remote.TokenManager.getInstance(this);
        if (tm.isLoggedIn()) {
            // Validate token by calling /me
            com.example.frontend.data.remote.ApiClient.getClient(this)
                    .create(com.example.frontend.data.remote.ApiService.class)
                    .getMe()
                    .enqueue(new retrofit2.Callback<com.example.frontend.data.remote.ApiResponse<Object>>() {
                        @Override
                        public void onResponse(retrofit2.Call<com.example.frontend.data.remote.ApiResponse<Object>> call, retrofit2.Response<com.example.frontend.data.remote.ApiResponse<Object>> response) {
                            if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()) {
                                tm.clearToken();
                                Toast.makeText(MainActivity.this, "Phien dang nhap het han", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<com.example.frontend.data.remote.ApiResponse<Object>> call, Throwable t) {
                            // Network error, maybe don't clear token yet
                        }
                    });
        }
    }

    public void loadFragment(Fragment fragment) {
        if (fragment == null) return;

        if (layoutHomeScroll != null) layoutHomeScroll.setVisibility(View.GONE);
        if (mainFragmentContainer != null) mainFragmentContainer.setVisibility(View.VISIBLE);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void updateHomeVisibility() {
        boolean hasFragments = getSupportFragmentManager().getBackStackEntryCount() > 0;
        if (layoutHomeScroll != null) {
            layoutHomeScroll.setVisibility(hasFragments ? View.GONE : View.VISIBLE);
        }
        if (mainFragmentContainer != null) {
            mainFragmentContainer.setVisibility(hasFragments ? View.VISIBLE : View.GONE);
        }
    }

    public void navigateToCart() {
        loadFragment(new CartFragment());
    }

    private void initViews() {
        vpHomeBanner = findViewById(R.id.vpHomeBanner);
        layoutSearchBar = findViewById(R.id.layoutSearchBar);
        btnNotification = findViewById(R.id.btnNotification);
        btnCart = findViewById(R.id.btnCart);
        btnWishlist = findViewById(R.id.btnWishlist);
        rvHomeShortcuts = findViewById(R.id.rvHomeShortcuts);
        rvRecommendedProducts = findViewById(R.id.rvRecommendedProducts);
        rvAllProducts = findViewById(R.id.rvAllProducts);
        layoutHomeStateContainer = findViewById(R.id.layoutHomeStateContainer);
        viewHomeLoading = findViewById(R.id.viewHomeLoading);
        viewHomeError = findViewById(R.id.viewHomeError);
        ivChatbot = findViewById(R.id.ivChatbot);
        layoutHomeScroll = findViewById(R.id.layoutHomeScroll);
        mainFragmentContainer = findViewById(R.id.main_fragment_container);

        layoutKanilaReelsCard = findViewById(R.id.layoutKanilaReelsCard);
        layoutReelThumbOne = findViewById(R.id.layoutReelThumbOne);
        layoutReelThumbTwo = findViewById(R.id.layoutReelThumbTwo);
        layoutReelThumbThree = findViewById(R.id.layoutReelThumbThree);
        ivReelThumbOne = findViewById(R.id.ivReelThumbOne);
        ivReelThumbTwo = findViewById(R.id.ivReelThumbTwo);
        ivReelThumbThree = findViewById(R.id.ivReelThumbThree);
        vvReelOne = findViewById(R.id.vvReelOne);
        vvReelTwo = findViewById(R.id.vvReelTwo);
        vvReelThree = findViewById(R.id.vvReelThree);
        layoutKanilaChallengeCard = findViewById(R.id.layoutKanilaChallengeCard);
        btnJoinChallenge = findViewById(R.id.btnJoinChallenge);
        tvChallengeProgress = findViewById(R.id.tvChallengeProgress);
        tvChallengeParticipants = findViewById(R.id.tvChallengeParticipants);
        tvChallengeReward = findViewById(R.id.tvChallengeReward);

        layoutSearchExpandedBar = findViewById(R.id.layoutSearchExpandedBar);
        edtExpandedSearchQuery = findViewById(R.id.edtExpandedSearchQuery);
        btnExpandedSearchBack = findViewById(R.id.btnExpandedSearchBack);

        if (findViewById(R.id.btnViewAllRecommended) != null) {
            findViewById(R.id.btnViewAllRecommended).setOnClickListener(v -> {
                Toast.makeText(this, "See All Recommended", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void setupSearchBehavior() {
        if (layoutSearchBar != null) {
            layoutSearchBar.setOnClickListener(v -> {
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
            });
        }

        if (btnCart != null) btnCart.setOnClickListener(v -> navigateToCart());

        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> {
                loadFragment(new ui.notification.NotificationCenterFragment());
            });
        }

        if (btnWishlist != null) {
            btnWishlist.setOnClickListener(v -> {
                if (com.example.frontend.data.remote.TokenManager.getInstance(this).isLoggedIn()) {
                    loadFragment(new com.example.frontend.feature.wishlist.WishlistFragment());
                } else {
                    showLoginPrompt();
                }
            });
        }

        if (ivChatbot != null) {
            ivChatbot.setOnClickListener(v -> {
                loadFragment(ChatConversationFragment.newInstance(null));
            });
        }

        setupHomeShortcuts();
        setupSocialSection();
        setupReelsVideos();
    }

    private void setupReelsVideos() {
        if (vvReelOne == null) return;
        vvReelOne.setVideoURI(Uri.parse(com.example.frontend.feature.community.reels.mock.MockReelsDataSource.VIDEO_URL_01));
        vvReelTwo.setVideoURI(Uri.parse(com.example.frontend.feature.community.reels.mock.MockReelsDataSource.VIDEO_URL_02));
        vvReelThree.setVideoURI(Uri.parse(com.example.frontend.feature.community.reels.mock.MockReelsDataSource.VIDEO_URL_03));

        setupHomeVideo(vvReelOne, ivReelThumbOne);
        setupHomeVideo(vvReelTwo, ivReelThumbTwo);
        setupHomeVideo(vvReelThree, ivReelThumbThree);
    }

    private void setupHomeVideo(android.widget.VideoView videoView, ImageView thumbnail) {
        videoView.setOnPreparedListener(mp -> {
            mp.setVolume(0f, 0f);
            mp.setLooping(true);
            videoView.start();
        });
        videoView.setOnInfoListener((mp, what, extra) -> {
            if (what == android.media.MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                if (thumbnail != null) thumbnail.setVisibility(View.GONE);
            }
            return false;
        });
    }

    private void setupBottomNavigation() {
        View bottomNav = findViewById(R.id.layoutBottomNavigation);
        if (bottomNav != null) {
            BottomNavigationHelper.setup(bottomNav, tabIndex -> {
                if (tabIndex == BottomNavigationHelper.TAB_ACCOUNT) {
                    loadFragment(new AccountFragment());
                } else if (tabIndex == BottomNavigationHelper.TAB_CATEGORY) {
                    loadFragment(new ProductCategoryFragment());
                } else if (tabIndex == BottomNavigationHelper.TAB_REELS) {
                    loadFragment(new ReelsFeedFragment());
                } else if (tabIndex == BottomNavigationHelper.TAB_COMMUNITY) {
                    loadFragment(new ui.community.CommunityHomeFragment());
                } else if (tabIndex == BottomNavigationHelper.TAB_HOME) {
                    // Back to Activity UI (remove fragments)
                    if (layoutHomeScroll != null) layoutHomeScroll.setVisibility(View.VISIBLE);
                    if (mainFragmentContainer != null) mainFragmentContainer.setVisibility(View.GONE);

                    while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStackImmediate();
                    }
                }
            });
            BottomNavigationHelper.setSelectedTab(bottomNav, BottomNavigationHelper.TAB_HOME);
        }
    }

    private void setupProductLists() {
        recommendedProductAdapter = new HomeProductAdapter();

        recommendedProductAdapter.setOnProductClickListener(new HomeProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(com.example.frontend.model.Product product) {
                loadFragment(com.example.frontend.feature.product.ProductDetailFragment.newInstance(product.getId()));
            }

            @Override
            public void onAddToCartClick(com.example.frontend.model.Product product) {
                handleAddToCart(product);
            }
        });

        recommendedProductAdapter.setOnWishlistToggleListener((product, wasWishlisted) -> {
            if (com.example.frontend.data.remote.TokenManager.getInstance(this).isLoggedIn()) {
                wishlistViewModel.toggleWishlist(product.getId(), wasWishlisted);
            } else {
                product.setFavorite(wasWishlisted); // rollback UI
                recommendedProductAdapter.notifyDataSetChanged();

                Bundle extras = new Bundle();
                extras.putString("productId", product.getId());
                extras.putBoolean("wasWishlisted", wasWishlisted);

                com.example.frontend.core.auth.PendingAuthAction action = new com.example.frontend.core.auth.PendingAuthAction(
                        com.example.frontend.core.auth.PendingAuthAction.ActionType.ADD_TO_WISHLIST,
                        "Home",
                        0,
                        extras
                );
                com.example.frontend.core.auth.AuthNavigationHelper.showAuthPrompt(this, action);
            }
        });

        if (rvRecommendedProducts != null) {
            rvRecommendedProducts.setLayoutManager(new GridLayoutManager(this, 2));
            rvRecommendedProducts.setAdapter(recommendedProductAdapter);
            rvRecommendedProducts.setNestedScrollingEnabled(false);
        }

        // All Products (Vertical Grid)
        allProductAdapter = new HomeProductAdapter();
        allProductAdapter.setOnProductClickListener(new HomeProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(com.example.frontend.model.Product product) {
                loadFragment(com.example.frontend.feature.product.ProductDetailFragment.newInstance(product.getId()));
            }

            @Override
            public void onAddToCartClick(com.example.frontend.model.Product product) {
                handleAddToCart(product);
            }
        });

        allProductAdapter.setOnWishlistToggleListener((product, wasWishlisted) -> {
            if (com.example.frontend.data.remote.TokenManager.getInstance(this).isLoggedIn()) {
                wishlistViewModel.toggleWishlist(product.getId(), wasWishlisted);
            } else {
                product.setFavorite(wasWishlisted); // rollback UI
                allProductAdapter.notifyDataSetChanged();

                Bundle extras = new Bundle();
                extras.putString("productId", product.getId());
                extras.putBoolean("wasWishlisted", wasWishlisted);

                com.example.frontend.core.auth.PendingAuthAction action = new com.example.frontend.core.auth.PendingAuthAction(
                        com.example.frontend.core.auth.PendingAuthAction.ActionType.ADD_TO_WISHLIST,
                        "Home",
                        0,
                        extras
                );
                com.example.frontend.core.auth.AuthNavigationHelper.showAuthPrompt(this, action);
            }
        });

        if (rvAllProducts != null) {
            rvAllProducts.setLayoutManager(new GridLayoutManager(this, 2));
            rvAllProducts.setAdapter(allProductAdapter);
            rvAllProducts.setNestedScrollingEnabled(false);
        }
    }

    private void observeViewModel() {
        if (viewModel == null) return;
        viewModel.getUiState().observe(this, state -> {
            if (state == null) return;

            if (state.loading) {
                showLoading();
            } else if (state.error != null) {
                showError(state.error);
            } else {
                showContent();
                List<com.example.frontend.model.Product> allProductsToUpdate = new java.util.ArrayList<>();
                if (state.recommendedProducts != null) {
                    recommendedProductAdapter.setProducts(state.recommendedProducts);
                    allProductsToUpdate.addAll(state.recommendedProducts);
                }
                if (state.allProducts != null) {
                    allProductAdapter.setProducts(state.allProducts);
                    allProductsToUpdate.addAll(state.allProducts);
                }
                
                if (!allProductsToUpdate.isEmpty() && com.example.frontend.data.remote.TokenManager.getInstance(this).isLoggedIn()) {
                    List<String> productIds = new java.util.ArrayList<>();
                    for (com.example.frontend.model.Product p : allProductsToUpdate) {
                        productIds.add(p.getId());
                    }
                    wishlistViewModel.loadWishlistStatus(productIds);
                }
            }
        });

        if (wishlistViewModel != null) {
            wishlistViewModel.getStatusResult().observe(this, result -> {
                if (result != null && result.status == com.example.frontend.data.remote.NetworkResult.Status.SUCCESS && result.data != null) {
                    updateProductFavoriteStates(result.data);
                }
            });

            wishlistViewModel.getToggleResult().observe(this, result -> {
                if (result != null && result.status == com.example.frontend.data.remote.NetworkResult.Status.ERROR) {
                    Toast.makeText(this, "Lỗi: " + result.message, Toast.LENGTH_SHORT).show();
                    // We might need to refresh status to rollback UI accurately
                    viewModel.loadHomeData();
                }
            });
        }
    }

    private void updateProductFavoriteStates(java.util.Map<String, Boolean> statusMap) {
        if (recommendedProductAdapter.getProducts() != null) {
            for (com.example.frontend.model.Product p : recommendedProductAdapter.getProducts()) {
                Boolean isFav = statusMap.get(p.getId());
                if (isFav != null) p.setFavorite(isFav);
            }
            recommendedProductAdapter.notifyDataSetChanged();
        }
        if (allProductAdapter.getProducts() != null) {
            for (com.example.frontend.model.Product p : allProductAdapter.getProducts()) {
                Boolean isFav = statusMap.get(p.getId());
                if (isFav != null) p.setFavorite(isFav);
            }
            allProductAdapter.notifyDataSetChanged();
        }
    }

    private void showLoginPrompt() {
        com.example.frontend.core.auth.PendingAuthAction action = new com.example.frontend.core.auth.PendingAuthAction(
                com.example.frontend.core.auth.PendingAuthAction.ActionType.OPEN_ACCOUNT,
                "Home",
                0,
                null
        );
        com.example.frontend.core.auth.AuthNavigationHelper.showAuthPrompt(this, action);
    }

    private void showLoading() {
        if (layoutHomeStateContainer != null) layoutHomeStateContainer.setVisibility(View.VISIBLE);
        if (viewHomeLoading != null) viewHomeLoading.setVisibility(View.VISIBLE);
        if (viewHomeError != null) viewHomeError.setVisibility(View.GONE);
        if (findViewById(R.id.layoutHomeRecommendation) != null) findViewById(R.id.layoutHomeRecommendation).setVisibility(View.GONE);
        if (findViewById(R.id.layoutHomeCatalog) != null) findViewById(R.id.layoutHomeCatalog).setVisibility(View.GONE);
    }

    private void showContent() {
        if (layoutHomeStateContainer != null) layoutHomeStateContainer.setVisibility(View.GONE);
        if (findViewById(R.id.layoutHomeRecommendation) != null) findViewById(R.id.layoutHomeRecommendation).setVisibility(View.VISIBLE);
        if (findViewById(R.id.layoutHomeCatalog) != null) findViewById(R.id.layoutHomeCatalog).setVisibility(View.VISIBLE);
    }

    private void showError(String message) {
        if (layoutHomeStateContainer != null) layoutHomeStateContainer.setVisibility(View.VISIBLE);
        if (viewHomeLoading != null) viewHomeLoading.setVisibility(View.GONE);
        if (viewHomeError != null) viewHomeError.setVisibility(View.VISIBLE);
        if (findViewById(R.id.layoutHomeRecommendation) != null) findViewById(R.id.layoutHomeRecommendation).setVisibility(View.GONE);
        if (findViewById(R.id.layoutHomeCatalog) != null) findViewById(R.id.layoutHomeCatalog).setVisibility(View.GONE);

        if (viewHomeError != null) {
            TextView tvError = viewHomeError.findViewById(R.id.tvErrorTitle);
            if (tvError != null) tvError.setText(message);

            View btnRetry = viewHomeError.findViewById(R.id.btnErrorRetry);
            if (btnRetry != null) btnRetry.setOnClickListener(v -> viewModel.loadHomeData());
        }
    }

    private void setupSocialSection() {
        if (layoutKanilaReelsCard != null) {
            layoutKanilaReelsCard.setOnClickListener(v -> {
                loadFragment(new com.example.frontend.feature.community.reels.ReelsFeedFragment());
            });
        }

        if (layoutReelThumbOne != null) {
            layoutReelThumbOne.setOnClickListener(v -> {
                loadFragment(new com.example.frontend.feature.community.reels.ReelsFeedFragment());
            });
        }

        if (layoutReelThumbTwo != null) {
            layoutReelThumbTwo.setOnClickListener(v -> {
                loadFragment(new com.example.frontend.feature.community.reels.ReelsFeedFragment());
            });
        }

        if (layoutReelThumbThree != null) {
            layoutReelThumbThree.setOnClickListener(v -> {
                loadFragment(new com.example.frontend.feature.community.reels.ReelsFeedFragment());
            });
        }

        if (layoutKanilaChallengeCard != null) layoutKanilaChallengeCard.setOnClickListener(v -> Toast.makeText(this, "Kanila Challenge", Toast.LENGTH_SHORT).show());

        if (btnJoinChallenge != null) btnJoinChallenge.setOnClickListener(v -> Toast.makeText(this, "Tham gia challenge", Toast.LENGTH_SHORT).show());

        if (tvChallengeProgress != null) tvChallengeProgress.setText(getString(R.string.home_social_challenge_progress_format, "8", "14"));
        if (tvChallengeParticipants != null) tvChallengeParticipants.setText(getString(R.string.home_social_challenge_participants_format, "12.6K"));
        if (tvChallengeReward != null) tvChallengeReward.setText(getString(R.string.home_social_challenge_reward_format, "200"));
    }

    private void setupHomeShortcuts() {
        shortcutAdapter = new HomeShortcutAdapter();
        shortcutAdapter.setOnShortcutClickListener(item -> {
            if ("orders".equals(item.getId())) {
                loadFragment(new ui.order.OrderListFragment());
            } else if ("kanila_beauty".equals(item.getId())) {
                loadFragment(new ui.account.BeautyProfileOverviewFragment());
            } else if ("support".equals(item.getId())) {
                loadFragment(new ui.support.HelpCenterFragment());
            } else {
                Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        if (rvHomeShortcuts != null) rvHomeShortcuts.setAdapter(shortcutAdapter);

        List<HomeShortcutItem> shortcuts = new ArrayList<>();
        shortcuts.add(new HomeShortcutItem("orders", "Don hang", R.drawable.ic_shortcut_order, "orders", "", false, false));
        shortcuts.add(new HomeShortcutItem("voucher", "Voucher", R.drawable.ic_shortcut_voucher, "voucher", "", false, false));
        shortcuts.add(new HomeShortcutItem("ar", "AR", R.drawable.ic_shortcut_ar, "ar_try_on", "", false, false));
        shortcuts.add(new HomeShortcutItem("kanila_beauty", "Kanila Beauty", R.drawable.ic_shortcut_kanila_beauty, "beauty", "", false, false));
        shortcuts.add(new HomeShortcutItem("creator", "Creator", R.drawable.ic_shortcut_creator, "creator", "", false, false));
        shortcuts.add(new HomeShortcutItem("royalty", "Royalty", R.drawable.ic_shortcut_royalty, "loyalty", "", false, false));
        shortcuts.add(new HomeShortcutItem("support", "Tro giup", R.drawable.ic_shortcut_help, "support", "", false, false));
        shortcuts.add(new HomeShortcutItem("policy", "Chinh sach", R.drawable.ic_shortcut_policy, "policy", "", false, false));

        shortcutAdapter.setItems(shortcuts);
    }

    private void setupBannerSlider() {
        bannerAdapter = new HomeBannerAdapter();
        bannerAdapter.setOnBannerClickListener(item -> Toast.makeText(this, "Clicked: " + item.getButtonText(), Toast.LENGTH_SHORT).show());

        if (vpHomeBanner != null) {
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
            items.add(new HomeBannerItem("1", "", "", "", "Kham pha ngay", null, R.drawable.bg_slide_1, "category", "123", true, 1));
            items.add(new HomeBannerItem("2", "", "", "", "Mua ngay", null, R.drawable.bg_slide_2, "product", "456", true, 2));
            items.add(new HomeBannerItem("3", "", "", "", "Xem uu dai", null, R.drawable.bg_slide_3, "promotion", "789", true, 3));
            items.add(new HomeBannerItem("4", "", "", "", "Goi y cho ban", null, R.drawable.bg_slide_4, "recommendation", "012", true, 4));
            items.add(new HomeBannerItem("5", "", "", "", "Nhan voucher", null, R.drawable.bg_slide_5, "voucher", "345", true, 5));

            bannerAdapter.setItems(items);

            int startPosition = (Integer.MAX_VALUE / 2) - ((Integer.MAX_VALUE / 2) % items.size());
            vpHomeBanner.setCurrentItem(startPosition, false);

            // Delay auto-slide start to prevent blocking main thread during layout
            vpHomeBanner.post(() -> setupAutoSlide(items.size()));
        }
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

        if (vpHomeBanner != null) {
            vpHomeBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    autoSlideHandler.removeCallbacks(autoSlideRunnable);
                    autoSlideHandler.postDelayed(autoSlideRunnable, 4000);
                }
            });
        }
    }

    private void handleAddToCart(com.example.frontend.model.Product product) {
        if (product.getId() == null) return;

        // Use empty string instead of null for variant_id as some backends require it
        AddToCartRequest request = new AddToCartRequest(product.getId(), null, 1);
        cartViewModel.addToCart(request);

        cartViewModel.getCartResult().observe(this, new androidx.lifecycle.Observer<NetworkResult<com.example.frontend.data.model.cart.CartDto>>() {
            @Override
            public void onChanged(NetworkResult<com.example.frontend.data.model.cart.CartDto> result) {
                if (result == null) return;
                if (result.status == NetworkResult.Status.SUCCESS) {
                    Toast.makeText(MainActivity.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                    cartViewModel.getCartResult().removeObserver(this);
                } else if (result.status == NetworkResult.Status.ERROR) {
                    Toast.makeText(MainActivity.this, result.message != null ? result.message : "Lỗi thêm giỏ hàng", Toast.LENGTH_SHORT).show();
                    cartViewModel.getCartResult().removeObserver(this);
                }
            }
        });
    }

    private void smoothScrollTo(int position, long duration) {
        if (vpHomeBanner == null || vpHomeBanner.isFakeDragging()) return;

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
    protected void onResume() {
        super.onResume();
        if (vvReelOne != null) vvReelOne.start();
        if (vvReelTwo != null) vvReelTwo.start();
        if (vvReelThree != null) vvReelThree.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (vvReelOne != null) vvReelOne.pause();
        if (vvReelTwo != null) vvReelTwo.pause();
        if (vvReelThree != null) vvReelThree.pause();
    }

    @Override
    protected void onDestroy() {
        if (autoSlideHandler != null && autoSlideRunnable != null) {
            autoSlideHandler.removeCallbacks(autoSlideRunnable);
        }
        super.onDestroy();
    }
}
