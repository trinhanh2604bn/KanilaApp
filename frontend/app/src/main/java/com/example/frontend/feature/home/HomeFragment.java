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
import com.example.frontend.feature.recommendation.RecommendationViewModel;
import com.example.frontend.feature.recommendation.RecommendationProductAdapter;
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
import ui.account.BeautyProfileOverviewFragment;
import ui.support.HelpCenterFragment;

public class HomeFragment extends Fragment {

    private ViewPager2 vpHomeBanner;
    private View layoutSearchBar;
    private android.widget.ImageButton btnNotification, btnCart, btnWishlist;
    private RecyclerView rvHomeShortcuts;
    private RecyclerView rvRecommendedProducts;
    private RecyclerView rvAllProducts;
    private View layoutHomeStateContainer, viewHomeLoading, viewHomeError;
    private View viewRecommendationLoading, viewRecommendationEmpty, viewRecommendationGuest, viewRecommendationError;
    
    private View layoutKanilaReelsCard, layoutReelThumbOne, layoutReelThumbTwo, layoutReelThumbThree;
    private ImageView ivReelThumbOne, ivReelThumbTwo, ivReelThumbThree;
    private android.widget.VideoView vvReelOne, vvReelTwo, vvReelThree;

    private View layoutKanilaChallengeCard, btnJoinChallenge;
    private TextView tvChallengeProgress, tvChallengeParticipants, tvChallengeReward;

    private HomeBannerAdapter bannerAdapter;
    private HomeShortcutAdapter shortcutAdapter;
    private RecommendationProductAdapter recommendationProductAdapter;
    private HomeProductAdapter allProductAdapter;
    private HomeViewModel viewModel;
    private RecommendationViewModel recommendationViewModel;
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
        recommendationViewModel = new ViewModelProvider(requireActivity()).get(RecommendationViewModel.class);
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
                recommendationViewModel.fetchHomepageRecommendations();
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

        viewRecommendationLoading = view.findViewById(R.id.viewRecommendationLoading);
        viewRecommendationEmpty = view.findViewById(R.id.viewRecommendationEmpty);
        viewRecommendationGuest = view.findViewById(R.id.viewRecommendationGuest);
        viewRecommendationError = view.findViewById(R.id.viewRecommendationError);

        if (viewRecommendationGuest != null) {
            viewRecommendationGuest.findViewById(R.id.btnRecommendationLogin).setOnClickListener(v -> {
                com.example.frontend.core.auth.AuthNavigationHelper.showAuthPrompt(requireActivity(),
                    new com.example.frontend.core.auth.PendingAuthAction(com.example.frontend.core.auth.PendingAuthAction.ActionType.OPEN_ACCOUNT, "HomeRecommendation", 0, null));
            });
        }

        if (viewRecommendationEmpty != null) {
            viewRecommendationEmpty.findViewById(R.id.btnRecommendationProfile).setOnClickListener(v -> {
                navigateToFragment(new BeautyProfileOverviewFragment());
            });
        }

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
                    // Logic yêu cầu: Click icon ngoài trang chủ thì hiện Popup CTA trước
                    // Trong Popup CTA, nếu chưa đăng nhập mới hiện GuestPrompt từ dưới lên
                    navigateToFragment(new BeautyProfileOverviewFragment());
                }
            }
            else if ("orders".equals(item.getId())) navigateToFragment(new com.example.frontend.feature.order.OrderListFragment());
            else if ("support".equals(item.getId())) navigateToFragment(new HelpCenterFragment());
            else if ("policy".equals(item.getId())) navigateToFragment(new ui.support.PolicyFragment());
            else Toast.makeText(requireContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        });

        rvHomeShortcuts.setAdapter(shortcutAdapter);
        List<HomeShortcutItem> shortcuts = new ArrayList<>();
        shortcuts.add(new HomeShortcutItem("orders", "Đơn hàng", R.drawable.ic_shortcut_order, "orders", "", false, false));
        shortcuts.add(new HomeShortcutItem("voucher", "Voucher", R.drawable.ic_shortcut_voucher, "voucher", "", false, false));
        shortcuts.add(new HomeShortcutItem("ar", "AR", R.drawable.ic_shortcut_ar, "ar_try_on", "", false, false));
        shortcuts.add(new HomeShortcutItem("kanila_beauty", "Kanila Beauty", R.drawable.ic_shortcut_kanila_beauty, "beauty", "", false, false));
        shortcuts.add(new HomeShortcutItem("support", "Trợ giúp", R.drawable.ic_shortcut_help, "support", "", false, false));
        shortcuts.add(new HomeShortcutItem("policy", "Chính sách & Điều khoản", R.drawable.ic_shortcut_policy, "policy", "", false, false));
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
        recommendationProductAdapter = new RecommendationProductAdapter();
        recommendationProductAdapter.setOnProductClickListener(new RecommendationProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                if (product.getId() != null) {
                    navigateToFragment(com.example.frontend.feature.product.ProductDetailFragment.newInstance(product.getId()));
                }
            }

            @Override
            public void onAddToCartClick(Product product) {
                handleAddToCart(product);
            }
        });
        rvRecommendedProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvRecommendedProducts.setAdapter(recommendationProductAdapter);
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

        AddToCartRequest request = new AddToCartRequest(product.getId(), null, 1);
        cartViewModel.addToCart(request);
        
        cartViewModel.getCartResult().observe(getViewLifecycleOwner(), new androidx.lifecycle.Observer<NetworkResult<com.example.frontend.data.model.cart.CartDto>>() {
            @Override
            public void onChanged(NetworkResult<com.example.frontend.data.model.cart.CartDto> result) {
                if (result == null) return;
                if (result.status == NetworkResult.Status.SUCCESS) {
                    Toast.makeText(requireContext(), "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                    cartViewModel.getCartResult().removeObserver(this);
                } else if (result.status == NetworkResult.Status.ERROR) {
                    Toast.makeText(requireContext(), result.message != null ? result.message : "Lỗi thêm giỏ hàng", Toast.LENGTH_SHORT).show();
                    cartViewModel.getCartResult().removeObserver(this);
                }
            }
        });
    }

    private void observeViewModel() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;
            if (state.loading) showLoading();
            else if (state.error != null) showError(state.error);
            else {
                showContent();
                if (state.allProducts != null) allProductAdapter.setProducts(state.allProducts);
            }
        });

        recommendationViewModel.getHomepageRecommendations().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case LOADING:
                    showRecommendationState(viewRecommendationLoading);
                    break;
                case SUCCESS:
                    showRecommendationState(rvRecommendedProducts);
                    if (result.data != null && result.data.getProducts() != null) {
                        recommendationProductAdapter.setItems(result.data.getProducts());
                    }
                    break;
                case EMPTY:
                    showRecommendationState(viewRecommendationEmpty);
                    break;
                case GUEST:
                    showRecommendationState(viewRecommendationGuest);
                    break;
                case UNAUTHORIZED:
                    // Token expired
                    Toast.makeText(requireContext(), R.string.auth_token_expired_msg, Toast.LENGTH_SHORT).show();
                    showRecommendationState(viewRecommendationGuest);
                    break;
                case ERROR:
                    showRecommendationState(viewRecommendationError);
                    break;
            }
        });

        recommendationViewModel.getBeautyProfile().observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.status == com.example.frontend.data.remote.NetworkResult.Status.SUCCESS && result.data != null) {
                updateRecommendationSummary(result.data);
            }
        });
    }

    private void updateRecommendationSummary(com.example.frontend.data.model.beauty.CustomerBeautyProfileDto profile) {
        TextView tvSubtitle = getView() != null ? getView().findViewById(R.id.tvRecommendationSubtitle) : null;
        if (tvSubtitle == null) return;

        StringBuilder sb = new StringBuilder("Dựa trên ");
        if (profile.getSkinType() != null && !profile.getSkinType().isEmpty()) {
            sb.append("tình trạng ").append(profile.getSkinType());
        } else {
            sb.append("loại da của bạn");
        }

        if (profile.getSkinConcerns() != null && !profile.getSkinConcerns().isEmpty()) {
            sb.append(" và mục tiêu cải thiện ").append(String.join(", ", profile.getSkinConcerns()));
        }

        if (profile.getAvoidIngredients() != null && !profile.getAvoidIngredients().isEmpty()) {
            sb.append(", Kanila đề xuất các sản phẩm không chứa ").append(String.join(", ", profile.getAvoidIngredients()));
        } else {
            sb.append(", Kanila dành riêng cho bạn:");
        }

        tvSubtitle.setText(sb.toString());
    }

    private void showRecommendationState(View visibleView) {
        rvRecommendedProducts.setVisibility(visibleView == rvRecommendedProducts ? View.VISIBLE : View.GONE);
        if (viewRecommendationLoading != null) viewRecommendationLoading.setVisibility(visibleView == viewRecommendationLoading ? View.VISIBLE : View.GONE);
        if (viewRecommendationEmpty != null) viewRecommendationEmpty.setVisibility(visibleView == viewRecommendationEmpty ? View.VISIBLE : View.GONE);
        if (viewRecommendationGuest != null) viewRecommendationGuest.setVisibility(visibleView == viewRecommendationGuest ? View.VISIBLE : View.GONE);
        if (viewRecommendationError != null) viewRecommendationError.setVisibility(visibleView == viewRecommendationError ? View.VISIBLE : View.GONE);
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
