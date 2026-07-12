package com.example.frontend.feature.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.frontend.R;
import com.example.frontend.feature.community.reels.ReelsFeedFragment;
import com.example.frontend.feature.search.SearchActivity;
import com.example.frontend.feature.wishlist.WishlistViewModel;
import com.example.frontend.feature.cart.CartViewModel;
import com.example.frontend.data.model.cart.AddToCartRequest;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.model.HomeBannerItem;
import com.example.frontend.model.HomeShortcutItem;
import com.example.frontend.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ui.common.FragmentNavigationHelper;
import ui.account.AccountFragment;
import ui.account.BeautyProfileOverviewFragment;
import ui.account.KocRegistrationFragment;
import ui.account.KocDashboardFragment;
import ui.support.HelpCenterFragment;

public class HomeFragment extends Fragment {

    private ViewPager2 vpHomeBanner;
    private View layoutSearchBar;
    private android.widget.ImageButton btnNotification, btnCart, btnWishlist;
    private RecyclerView rvHomeShortcuts;
    private RecyclerView rvRecommendedProducts;
    private RecyclerView rvAllProducts;
    private View layoutHomeStateContainer, viewHomeLoading, viewHomeError;
    
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
    private WishlistViewModel wishlistViewModel;
    private CartViewModel cartViewModel;

    private final Handler autoSlideHandler = new Handler(Looper.getMainLooper());
    private Runnable autoSlideRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        wishlistViewModel = new ViewModelProvider(requireActivity()).get(WishlistViewModel.class);
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

        initViews(view);
        setupSearchBar(view);
        setupBannerSlider();
        setupHomeShortcuts();
        setupProductLists();
        setupSocialSection();
        setupReelsVideos();

        observeViewModel();

        // Delay load to prevent ANR like in MainActivity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isAdded()) {
                viewModel.loadHomeData();
                checkAuthStatus();
            }
        }, 500);
    }

    private void checkAuthStatus() {
        com.example.frontend.data.remote.TokenManager tm = com.example.frontend.data.remote.TokenManager.getInstance(requireContext());
        if (tm.isLoggedIn()) {
            com.example.frontend.data.remote.ApiClient.getClient(requireContext())
                    .create(com.example.frontend.data.remote.ApiService.class)
                    .getMe()
                    .enqueue(new retrofit2.Callback<com.example.frontend.data.remote.ApiResponse<Object>>() {
                        @Override
                        public void onResponse(retrofit2.Call<com.example.frontend.data.remote.ApiResponse<Object>> call, retrofit2.Response<com.example.frontend.data.remote.ApiResponse<Object>> response) {
                            if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()) {
                                tm.clearToken();
                                if (isAdded()) Toast.makeText(requireContext(), "Phiên đăng nhập hết hạn", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(retrofit2.Call<com.example.frontend.data.remote.ApiResponse<Object>> call, Throwable t) {}
                    });
        }
    }

    private void setupSearchBar(View view) {
        if (layoutSearchBar != null) {
            layoutSearchBar.setOnClickListener(v -> {
                Intent intent = new Intent(requireActivity(), SearchActivity.class);
                startActivity(intent);
            });
        }

        if (btnCart != null) btnCart.setOnClickListener(v -> navigateToFragment(new ui.commerce.CartFragment()));

        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> navigateToFragment(new ui.notification.NotificationCenterFragment()));
        }

        if (btnWishlist != null) {
            btnWishlist.setOnClickListener(v -> {
                if (com.example.frontend.data.remote.TokenManager.getInstance(requireContext()).isLoggedIn()) {
                    navigateToFragment(new com.example.frontend.feature.wishlist.WishlistFragment());
                } else {
                    com.example.frontend.core.auth.AuthNavigationHelper.showAuthPrompt(requireActivity(),
                        new com.example.frontend.core.auth.PendingAuthAction(com.example.frontend.core.auth.PendingAuthAction.ActionType.OPEN_ACCOUNT, "Home", 0, null));
                }
            });
        }
    }

    private void navigateToFragment(Fragment fragment) {
        FragmentNavigationHelper.replaceFragment(requireActivity(), fragment);
    }

    private void initViews(View view) {
        vpHomeBanner = view.findViewById(R.id.vpHomeBanner);
        layoutSearchBar = view.findViewById(R.id.layoutSearchBar);
        btnNotification = view.findViewById(R.id.btnNotification);
        btnCart = view.findViewById(R.id.btnCart);
        btnWishlist = view.findViewById(R.id.btnWishlist);
        rvHomeShortcuts = view.findViewById(R.id.rvHomeShortcuts);
        rvRecommendedProducts = view.findViewById(R.id.rvRecommendedProducts);
        rvAllProducts = view.findViewById(R.id.rvAllProducts);
        layoutHomeStateContainer = view.findViewById(R.id.layoutHomeStateContainer);
        viewHomeLoading = view.findViewById(R.id.viewHomeLoading);
        viewHomeError = view.findViewById(R.id.viewHomeError);

        layoutKanilaReelsCard = view.findViewById(R.id.layoutKanilaReelsCard);
        layoutReelThumbOne = view.findViewById(R.id.layoutReelThumbOne);
        layoutReelThumbTwo = view.findViewById(R.id.layoutReelThumbTwo);
        layoutReelThumbThree = view.findViewById(R.id.layoutReelThumbThree);
        ivReelThumbOne = view.findViewById(R.id.ivReelThumbOne);
        ivReelThumbTwo = view.findViewById(R.id.ivReelThumbTwo);
        ivReelThumbThree = view.findViewById(R.id.ivReelThumbThree);
        vvReelOne = view.findViewById(R.id.vvReelOne);
        vvReelTwo = view.findViewById(R.id.vvReelTwo);
        vvReelThree = view.findViewById(R.id.vvReelThree);

        layoutKanilaChallengeCard = view.findViewById(R.id.layoutKanilaChallengeCard);
        btnJoinChallenge = view.findViewById(R.id.btnJoinChallenge);
        tvChallengeProgress = view.findViewById(R.id.tvChallengeProgress);
        tvChallengeParticipants = view.findViewById(R.id.tvChallengeParticipants);
        tvChallengeReward = view.findViewById(R.id.tvChallengeReward);

        view.findViewById(R.id.btnViewAllRecommended).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Gợi ý cho bạn", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupHomeShortcuts() {
        shortcutAdapter = new HomeShortcutAdapter();
        shortcutAdapter.setOnShortcutClickListener(item -> {
            if ("kanila_beauty".equals(item.getId())) {
                if (com.example.frontend.data.remote.TokenManager.getInstance(requireContext()).isLoggedIn()) {
                    navigateToFragment(new BeautyProfileOverviewFragment());
                } else {
                    navigateToFragment(new BeautyProfileOverviewFragment());
                }
            }
            else if ("creator".equals(item.getId())) {
                com.example.frontend.data.remote.TokenManager tm = com.example.frontend.data.remote.TokenManager.getInstance(requireContext());
                if (tm.isLoggedIn()) {
                    if (tm.isKoc()) {
                        navigateToFragment(new KocDashboardFragment());
                    } else {
                        navigateToFragment(new KocRegistrationFragment());
                    }
                } else {
                    com.example.frontend.core.auth.AuthNavigationHelper.showAuthPrompt(requireActivity(),
                        new com.example.frontend.core.auth.PendingAuthAction(
                            com.example.frontend.core.auth.PendingAuthAction.ActionType.OPEN_ACCOUNT, 
                            "CreatorShortcut", 0, null));
                }
            }
            else if ("orders".equals(item.getId())) navigateToFragment(new com.example.frontend.feature.order.OrderListFragment());
            else if ("support".equals(item.getId())) navigateToFragment(new HelpCenterFragment());
            else if ("policy".equals(item.getId())) navigateToFragment(new ui.support.PolicyFragment());
            else Toast.makeText(requireContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        });

        rvHomeShortcuts.setAdapter(shortcutAdapter);
        List<HomeShortcutItem> shortcuts = new ArrayList<>();
        shortcuts.add(new HomeShortcutItem("voucher", "Voucher", R.drawable.ic_shortcut_voucher, "voucher", "", false, false));
        shortcuts.add(new HomeShortcutItem("ar", "AR", R.drawable.ic_shortcut_ar, "ar_try_on", "", false, false));
        shortcuts.add(new HomeShortcutItem("kanila_beauty", "Kanila Beauty", R.drawable.ic_shortcut_kanila_beauty, "beauty", "", false, false));
        shortcuts.add(new HomeShortcutItem("creator", "Creator", R.drawable.ic_shortcut_creator, "creator", "", false, false));
        shortcuts.add(new HomeShortcutItem("royalty", "Royalty", R.drawable.ic_shortcut_royalty, "royalty", "", false, false));
        shortcuts.add(new HomeShortcutItem("support", "Trợ giúp", R.drawable.ic_shortcut_help, "support", "", false, false));
        shortcuts.add(new HomeShortcutItem("orders", "Đơn hàng", R.drawable.ic_shortcut_order, "orders", "", false, false));
        shortcuts.add(new HomeShortcutItem("policy", "Chính sách", R.drawable.ic_shortcut_policy, "policy", "", false, false));
        shortcutAdapter.setItems(shortcuts);
    }

    private void setupBannerSlider() {
        bannerAdapter = new HomeBannerAdapter();
        vpHomeBanner.setAdapter(bannerAdapter);
        vpHomeBanner.setOffscreenPageLimit(3);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer((int) getResources().getDimension(R.dimen.spacing_s)));
        vpHomeBanner.setPageTransformer(compositePageTransformer);

        List<HomeBannerItem> items = new ArrayList<>();
        items.add(new HomeBannerItem("1", "", "", "", "Khám phá ngay", null, R.drawable.bg_slide_1, "category", "123", true, 1));
        items.add(new HomeBannerItem("2", "", "", "", "Mua ngay", null, R.drawable.bg_slide_2, "product", "456", true, 2));
        bannerAdapter.setItems(items);
        
        vpHomeBanner.post(() -> setupAutoSlide(items.size()));
    }

    private void setupAutoSlide(int size) {
        if (size <= 1) return;
        autoSlideRunnable = new Runnable() {
            @Override
            public void run() {
                if (vpHomeBanner == null) return;
                vpHomeBanner.setCurrentItem(vpHomeBanner.getCurrentItem() + 1, true);
                autoSlideHandler.postDelayed(this, 4000);
            }
        };
        autoSlideHandler.postDelayed(autoSlideRunnable, 4000);
    }

    private void setupProductLists() {
        recommendedProductAdapter = new HomeProductAdapter();
        recommendedProductAdapter.setOnProductClickListener(new HomeProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                // Navigate to Product Detail if needed
                Toast.makeText(requireContext(), "Sản phẩm: " + product.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddToCartClick(Product product) {
                handleAddToCart(product);
            }
        });
        rvRecommendedProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvRecommendedProducts.setAdapter(recommendedProductAdapter);
        rvRecommendedProducts.setNestedScrollingEnabled(false);

        allProductAdapter = new HomeProductAdapter();
        allProductAdapter.setOnProductClickListener(new HomeProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                Toast.makeText(requireContext(), "Sản phẩm: " + product.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddToCartClick(Product product) {
                handleAddToCart(product);
            }
        });
        rvAllProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvAllProducts.setAdapter(allProductAdapter);
        rvAllProducts.setNestedScrollingEnabled(false);
    }

    private void setupSocialSection() {
        View.OnClickListener reelsClick = v -> navigateToFragment(new ReelsFeedFragment());
        if (layoutKanilaReelsCard != null) layoutKanilaReelsCard.setOnClickListener(reelsClick);
        if (layoutReelThumbOne != null) layoutReelThumbOne.setOnClickListener(reelsClick);
        if (layoutReelThumbTwo != null) layoutReelThumbTwo.setOnClickListener(reelsClick);
        if (layoutReelThumbThree != null) layoutReelThumbThree.setOnClickListener(reelsClick);
    }

    private void setupReelsVideos() {
        if (vvReelOne == null) return;
        vvReelOne.setVideoURI(Uri.parse("https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"));
        vvReelTwo.setVideoURI(Uri.parse("https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"));
        vvReelThree.setVideoURI(Uri.parse("https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4"));

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
                thumbnail.setVisibility(View.GONE);
            }
            return false;
        });
    }

    private void handleAddToCart(Product product) {
        if (product.getId() == null) return;
        com.example.frontend.feature.product.QuickAddHelper.quickAddToCart(
            requireContext(), getChildFragmentManager(), getViewLifecycleOwner(), product, cartViewModel);
    }

    private void observeViewModel() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;
            if (state.loading) showLoading();
            else if (state.error != null) showError(state.error);
            else {
                showContent();
                if (state.recommendedProducts != null) recommendedProductAdapter.setProducts(state.recommendedProducts);
                if (state.allProducts != null) allProductAdapter.setProducts(state.allProducts);
            }
        });

        cartViewModel.getCartResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            if (result.status == NetworkResult.Status.SUCCESS) {
                Toast.makeText(requireContext(), "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            } else if (result.status == NetworkResult.Status.ERROR) {
                Toast.makeText(requireContext(), result.message != null ? result.message : "Lỗi thêm giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading() {
        if (layoutHomeStateContainer != null) {
            layoutHomeStateContainer.setVisibility(View.VISIBLE);
            if (viewHomeLoading != null) viewHomeLoading.setVisibility(View.VISIBLE);
            if (viewHomeError != null) viewHomeError.setVisibility(View.GONE);
        }
    }

    private void showContent() {
        if (layoutHomeStateContainer != null) layoutHomeStateContainer.setVisibility(View.GONE);
    }

    private void showError(String message) {
        if (layoutHomeStateContainer != null) {
            layoutHomeStateContainer.setVisibility(View.VISIBLE);
            if (viewHomeLoading != null) viewHomeLoading.setVisibility(View.GONE);
            if (viewHomeError != null) {
                viewHomeError.setVisibility(View.VISIBLE);
                TextView tvError = viewHomeError.findViewById(R.id.tvErrorTitle);
                if (tvError != null) tvError.setText(message);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (vvReelOne != null) vvReelOne.start();
        if (vvReelTwo != null) vvReelTwo.start();
        if (vvReelThree != null) vvReelThree.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (vvReelOne != null) vvReelOne.pause();
        if (vvReelTwo != null) vvReelTwo.pause();
        if (vvReelThree != null) vvReelThree.pause();
    }

    @Override
    public void onDestroyView() {
        autoSlideHandler.removeCallbacks(autoSlideRunnable);
        super.onDestroyView();
    }
}
