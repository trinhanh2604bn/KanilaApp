package ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.frontend.MainActivity;
import com.example.frontend.R;
import com.example.frontend.feature.home.HomeBannerAdapter;
import com.example.frontend.feature.home.HomeProductAdapter;
import com.example.frontend.feature.home.HomeShortcutAdapter;
import com.example.frontend.feature.home.HomeViewModel;
import com.example.frontend.feature.search.SearchActivity;
import com.example.frontend.feature.wishlist.WishlistViewModel;
import com.example.frontend.model.HomeBannerItem;
import com.example.frontend.model.HomeShortcutItem;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private ViewPager2 vpHomeBanner;
    private View layoutSearchBar;
    private ImageButton btnNotification, btnCart, btnWishlist;
    private RecyclerView rvHomeShortcuts;
    private RecyclerView rvRecommendedProducts;
    private RecyclerView rvAllProducts;
    private View layoutHomeStateContainer, viewHomeLoading, viewHomeError;

    private View layoutKanilaReelsCard, layoutReelThumbOne, layoutReelThumbTwo;
    private ImageView ivReelThumbOne, ivReelThumbTwo;
    private View layoutKanilaChallengeCard, btnJoinChallenge;
    private TextView tvChallengeProgress, tvChallengeParticipants, tvChallengeReward;

    private HomeBannerAdapter bannerAdapter;
    private HomeShortcutAdapter shortcutAdapter;
    private HomeProductAdapter recommendedProductAdapter;
    private HomeProductAdapter allProductAdapter;
    private HomeViewModel viewModel;
    private WishlistViewModel wishlistViewModel;

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

        initViews(view);
        setupSearchBehavior();
        setupBannerSlider();
        setupProductLists();

        observeViewModel();
        
        if (viewModel.getUiState().getValue() == null || viewModel.getUiState().getValue().allProducts == null) {
            viewModel.loadHomeData();
        }
    }

    private void initViews(View view) {
        vpHomeBanner = view.findViewById(R.id.viewPagerBanner);
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
        ivReelThumbOne = view.findViewById(R.id.ivReelThumbOne);
        ivReelThumbTwo = view.findViewById(R.id.ivReelThumbTwo);
        layoutKanilaChallengeCard = view.findViewById(R.id.layoutKanilaChallengeCard);
        btnJoinChallenge = view.findViewById(R.id.btnJoinChallenge);
        tvChallengeProgress = view.findViewById(R.id.tvChallengeProgress);
        tvChallengeParticipants = view.findViewById(R.id.tvChallengeParticipants);
        tvChallengeReward = view.findViewById(R.id.tvChallengeReward);

        View btnAllRec = view.findViewById(R.id.btnViewAllRecommended);
        if (btnAllRec != null) {
            btnAllRec.setOnClickListener(v -> Toast.makeText(requireContext(), "See All Recommended", Toast.LENGTH_SHORT).show());
        }
    }

    private void setupSearchBehavior() {
        layoutSearchBar.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SearchActivity.class);
            startActivity(intent);
        });

        btnCart.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToCart();
            }
        });

        btnNotification.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new ui.notification.NotificationCenterFragment(), true);
            }
        });

        btnWishlist.setOnClickListener(v -> {
            if (com.example.frontend.data.remote.TokenManager.getInstance(requireContext()).isLoggedIn()) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).loadFragment(new com.example.frontend.feature.wishlist.WishlistFragment(), true);
                }
            } else {
                showLoginPrompt();
            }
        });

        setupHomeShortcuts();
        setupSocialSection();
    }

    private void setupProductLists() {
        recommendedProductAdapter = new HomeProductAdapter();
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        recommendedProductAdapter.setItemWidth((int) (screenWidth * 0.46));

        recommendedProductAdapter.setOnProductClickListener(product -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(com.example.frontend.feature.product.ProductDetailFragment.newInstance(product.getId()), true);
            }
        });

        recommendedProductAdapter.setOnWishlistToggleListener((product, wasWishlisted) -> {
            if (com.example.frontend.data.remote.TokenManager.getInstance(requireContext()).isLoggedIn()) {
                wishlistViewModel.toggleWishlist(product.getId(), wasWishlisted);
            } else {
                product.setFavorite(wasWishlisted);
                recommendedProductAdapter.notifyDataSetChanged();
                showLoginPrompt();
            }
        });

        rvRecommendedProducts.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvRecommendedProducts.setAdapter(recommendedProductAdapter);

        allProductAdapter = new HomeProductAdapter();
        allProductAdapter.setOnProductClickListener(product -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(com.example.frontend.feature.product.ProductDetailFragment.newInstance(product.getId()), true);
            }
        });

        allProductAdapter.setOnWishlistToggleListener((product, wasWishlisted) -> {
            if (com.example.frontend.data.remote.TokenManager.getInstance(requireContext()).isLoggedIn()) {
                wishlistViewModel.toggleWishlist(product.getId(), wasWishlisted);
            } else {
                product.setFavorite(wasWishlisted);
                allProductAdapter.notifyDataSetChanged();
                showLoginPrompt();
            }
        });
        
        rvAllProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvAllProducts.setAdapter(allProductAdapter);
        rvAllProducts.setNestedScrollingEnabled(false);
    }

    private void observeViewModel() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            if (state.loading) {
                showLoading();
            } else if (state.error != null) {
                showError(state.error);
            } else {
                showContent();
                if (state.recommendedProducts != null) {
                    recommendedProductAdapter.setProducts(state.recommendedProducts);
                }
                if (state.allProducts != null) {
                    allProductAdapter.setProducts(state.allProducts);
                }
            }
        });
    }

    private void showLoginPrompt() {
        if (getActivity() instanceof MainActivity) {
            com.example.frontend.core.auth.PendingAuthAction action = new com.example.frontend.core.auth.PendingAuthAction(
                com.example.frontend.core.auth.PendingAuthAction.ActionType.OPEN_ACCOUNT,
                "Home",
                0,
                null
            );
            com.example.frontend.core.auth.AuthNavigationHelper.showAuthPrompt(getActivity(), action);
        }
    }

    private void showLoading() {
        layoutHomeStateContainer.setVisibility(View.VISIBLE);
        viewHomeLoading.setVisibility(View.VISIBLE);
        viewHomeError.setVisibility(View.GONE);
        if (getView() != null) {
            getView().findViewById(R.id.layoutHomeRecommendation).setVisibility(View.GONE);
            getView().findViewById(R.id.layoutHomeCatalog).setVisibility(View.GONE);
        }
    }

    private void showContent() {
        layoutHomeStateContainer.setVisibility(View.GONE);
        if (getView() != null) {
            getView().findViewById(R.id.layoutHomeRecommendation).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.layoutHomeCatalog).setVisibility(View.VISIBLE);
        }
    }

    private void showError(String message) {
        layoutHomeStateContainer.setVisibility(View.VISIBLE);
        viewHomeLoading.setVisibility(View.GONE);
        viewHomeError.setVisibility(View.VISIBLE);
        if (getView() != null) {
            getView().findViewById(R.id.layoutHomeRecommendation).setVisibility(View.GONE);
            getView().findViewById(R.id.layoutHomeCatalog).setVisibility(View.GONE);
        }

        TextView tvError = viewHomeError.findViewById(R.id.tvErrorTitle);
        if (tvError != null) tvError.setText(message);

        View btnRetry = viewHomeError.findViewById(R.id.btnErrorRetry);
        if (btnRetry != null) btnRetry.setOnClickListener(v -> viewModel.loadHomeData());
    }

    private void setupSocialSection() {
        // Tối ưu: Tạm thời không load ảnh từ URL ngoài để tránh lag luồng chính
        // String thumbOneUrl = "https://img.youtube.com/vi/JytbqPADyQc/0.jpg";
        // String thumbTwoUrl = "https://img.youtube.com/vi/LwHA4UF3XQI/0.jpg";
        // Glide.with(this).load(thumbOneUrl).into(ivReelThumbOne);
        // Glide.with(this).load(thumbTwoUrl).into(ivReelThumbTwo);

        ivReelThumbOne.setImageResource(R.drawable.img_blush); // Dùng ảnh local làm placeholder
        ivReelThumbTwo.setImageResource(R.drawable.img_brush);

        layoutKanilaReelsCard.setOnClickListener(v -> Toast.makeText(requireContext(), "Kanila Reels", Toast.LENGTH_SHORT).show());

        layoutReelThumbOne.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/shorts/JytbqPADyQc?si=cXt-VYSr5hhdpOQg"));
            startActivity(intent);
        });

        layoutReelThumbTwo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/shorts/LwHA4UF3XQI?si=icecCgY-kTDcaYTz"));
            startActivity(intent);
        });

        layoutKanilaChallengeCard.setOnClickListener(v -> Toast.makeText(requireContext(), "Kanila Challenge", Toast.LENGTH_SHORT).show());

        if (btnJoinChallenge != null) {
            btnJoinChallenge.setOnClickListener(v -> Toast.makeText(requireContext(), "Tham gia challenge", Toast.LENGTH_SHORT).show());
        }

        if (tvChallengeProgress != null) tvChallengeProgress.setText("8/14 ngày");
        if (tvChallengeParticipants != null) tvChallengeParticipants.setText("12.6K tham gia");
        if (tvChallengeReward != null) tvChallengeReward.setText("200 điểm");
    }

    private void setupHomeShortcuts() {
        shortcutAdapter = new HomeShortcutAdapter();
        shortcutAdapter.setOnShortcutClickListener(item -> Toast.makeText(requireContext(), item.getTitle(), Toast.LENGTH_SHORT).show());

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
        bannerAdapter.setOnBannerClickListener(item -> Toast.makeText(requireContext(), "Clicked: " + item.getButtonText(), Toast.LENGTH_SHORT).show());

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
                vpHomeBanner.setCurrentItem(nextItem, true);
                autoSlideHandler.postDelayed(this, 4000);
            }
        };

        autoSlideHandler.postDelayed(autoSlideRunnable, 4000);
    }

    @Override
    public void onDestroyView() {
        if (autoSlideHandler != null && autoSlideRunnable != null) {
            autoSlideHandler.removeCallbacks(autoSlideRunnable);
        }
        super.onDestroyView();
    }
}
