package com.example.frontend.feature.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.RatingBar;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager;
import com.example.frontend.R;
import com.example.frontend.data.model.product.ProductDetailResponse;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.model.Product;
import com.example.frontend.feature.product.adapter.ProductImageAdapter;
import com.example.frontend.feature.product.adapter.ThumbnailAdapter;
import com.example.frontend.feature.product.adapter.RecentlyViewedAdapter;
import com.example.frontend.feature.home.HomeProductAdapter;
import com.example.frontend.feature.product.adapter.ReviewMediaAdapter;
import com.example.frontend.data.model.review.ReviewMediaDto;
import com.example.frontend.data.model.review.ReviewDto;
import com.example.frontend.data.model.cart.CartItemDto;
import com.example.frontend.data.model.checkout.CheckoutSessionDto;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import android.util.Log;

public class ProductDetailFragment extends Fragment {
    private static final String TAG = "ProductDetail";
    private static final String ARG_PRODUCT_ID = "product_id";

    private ProductDetailViewModel viewModel;
    private ReviewViewModel reviewActionViewModel;
    private String productId;

    private TextView tvName, tvBrand, tvPrice, tvComparePrice, tvGalleryCounter, tvRating, tvReviewCount, tvSoldCount, tvDesc, tvSelectedVariantName;
    private ViewPager2 vpGallery;
    private RecyclerView rvThumbnails, rvRecentlyViewed, rvRelatedProducts, rvReviewPreview;
    private ChipGroup cgBadges;
    private View layoutSkinMatch, layoutReviewSummary, layoutOutOfStock, layoutLoading, layoutError, layoutRecentlyViewed;
    private View btnAddToCart, btnBuyNow;
    private View layoutSectionDesc, layoutSectionIngredients, layoutSectionUsage;

    private ProductImageAdapter imageAdapter;
    private ThumbnailAdapter thumbnailAdapter;
    private RecentlyViewedAdapter recentlyViewedAdapter;
    private HomeProductAdapter relatedAdapter;
    private ReviewMediaAdapter reviewMediaAdapter;
    private com.example.frontend.feature.product.adapter.ReviewAdapter reviewPreviewAdapter;

    public static ProductDetailFragment newInstance(String productId) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_ID, productId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productId = getArguments().getString(ARG_PRODUCT_ID);
            Log.d(TAG, "Received productId = " + productId);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProductDetailViewModel.class);
        reviewActionViewModel = new ViewModelProvider(this).get(ReviewViewModel.class);

        initViews(view);
        setupAdapters(view);
        observeViewModel();
        observeReviewActionViewModel();

        if (productId != null) {
            viewModel.loadProductDetails(productId);
        }
    }

    private void initViews(View view) {
        View btnBack = view.findViewById(R.id.btnTopBarBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) getActivity().getOnBackPressedDispatcher().onBackPressed();
            });
        }

        tvName = view.findViewById(R.id.tvProductDetailName);
        tvBrand = view.findViewById(R.id.tvProductDetailBrand);
        tvPrice = view.findViewById(R.id.tvProductDetailPrice);
        tvComparePrice = view.findViewById(R.id.tvProductDetailComparePrice);
        tvGalleryCounter = view.findViewById(R.id.tvProductGalleryCounter);
        tvRating = view.findViewById(R.id.tvProductRating);
        tvReviewCount = view.findViewById(R.id.tvProductReviewCount);
        tvSoldCount = view.findViewById(R.id.tvProductSoldCount);
        tvDesc = view.findViewById(R.id.tvProductDetailDesc);
        tvSelectedVariantName = view.findViewById(R.id.tvSelectedVariantName);

        vpGallery = view.findViewById(R.id.vpProductGallery);
        rvThumbnails = view.findViewById(R.id.rvProductThumbnails);
        rvRecentlyViewed = view.findViewById(R.id.rvRecentlyViewed);
        rvRelatedProducts = view.findViewById(R.id.rvRelatedProducts);
        rvReviewPreview = view.findViewById(R.id.rvProductDetailReviewPreview);
        cgBadges = view.findViewById(R.id.cgProductBadges);

        layoutSkinMatch = view.findViewById(R.id.layoutSkinMatchScore);
        layoutReviewSummary = view.findViewById(R.id.layoutReviewSummary);
        layoutRecentlyViewed = view.findViewById(R.id.layoutRecentlyViewed);
        layoutOutOfStock = view.findViewById(R.id.layoutOutOfStockNotice);

        View btnNotifyStock = view.findViewById(R.id.btnNotifyStock);
        if (btnNotifyStock != null) {
            btnNotifyStock.setOnClickListener(v -> Toast.makeText(getContext(), "Bạn sẽ nhận được thông báo khi có hàng", Toast.LENGTH_SHORT).show());
        }

        layoutLoading = view.findViewById(R.id.layoutLoading);
        layoutError = view.findViewById(R.id.layoutError);

        if (layoutError != null) {
            View btnRetry = layoutError.findViewById(R.id.btnErrorRetry);
            if (btnRetry != null) {
                btnRetry.setOnClickListener(v -> {
                    if (productId != null) viewModel.loadProductDetails(productId);
                });
            }
        }

        View layoutStickyCta = view.findViewById(R.id.layoutProductStickyCTA);
        if (layoutStickyCta != null) {
            btnAddToCart = layoutStickyCta.findViewById(R.id.btnAddToCartSticky);
            btnBuyNow = layoutStickyCta.findViewById(R.id.btnBuyNowSticky);

            if (btnAddToCart != null) {
                btnAddToCart.setOnClickListener(v -> handleStickyCtaClick(VariantSelectorBottomSheet.ActionMode.ADD_TO_CART));
            }

            if (btnBuyNow != null) {
                btnBuyNow.setOnClickListener(v -> handleStickyCtaClick(VariantSelectorBottomSheet.ActionMode.BUY_NOW));
            }
        }

        View btnShare = view.findViewById(R.id.btnShare);
        if (btnShare != null) {
            btnShare.setOnClickListener(v -> {
                ShareProductBottomSheet bottomSheet = ShareProductBottomSheet.newInstance("https://kanila.vn/products/" + productId);
                bottomSheet.show(getChildFragmentManager(), "ShareProduct");
            });
        }

        if (layoutSkinMatch != null) {
            layoutSkinMatch.setOnClickListener(v -> {
                ProductDetailUiState state = viewModel.getUiState().getValue();
                if (state == null) return;

                if (state.detailedSkinMatch != null) {
                    switch (state.detailedSkinMatch.getStatus()) {
                        case READY:
                        case PROFILE_INCOMPLETE:
                        case CAUTION:
                            SkinMatchDetailBottomSheet bottomSheet = SkinMatchDetailBottomSheet.newInstance(state.detailedSkinMatch);
                            bottomSheet.show(getChildFragmentManager(), "SkinMatchDetail");
                            break;
                        case PROFILE_REQUIRED:
                            startActivity(new android.content.Intent(getContext(), ui.account.BeautyProfileActivity.class));
                            break;
                        default:
                            // If status is something else but visible, maybe show what we have
                            SkinMatchDetailBottomSheet defaultSheet = SkinMatchDetailBottomSheet.newInstance(state.detailedSkinMatch);
                            defaultSheet.show(getChildFragmentManager(), "SkinMatchDetail");
                            break;
                    }
                } else if (state.skinMatch != null) {
                    // Legacy fallback
                    SkinMatchDetailBottomSheet bottomSheet = SkinMatchDetailBottomSheet.newInstance(state.skinMatch);
                    bottomSheet.show(getChildFragmentManager(), "SkinMatchDetail");
                } else {
                    Toast.makeText(getContext(), "Đang cập nhật dữ liệu phân tích da...", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (layoutReviewSummary != null) {
            View btnViewAllReviews = layoutReviewSummary.findViewById(R.id.btnViewAllReviews);
            if (btnViewAllReviews != null) {
                btnViewAllReviews.setOnClickListener(v -> {
                    ReviewHubFragment fragment = ReviewHubFragment.newInstance(productId);
                    ui.common.FragmentNavigationHelper.loadFragment(getActivity(), fragment);
                });
            }
        }

        layoutSectionDesc = view.findViewById(R.id.layoutSectionDesc);
        if (layoutSectionDesc != null) {
            ((TextView) layoutSectionDesc.findViewById(R.id.tvSectionTitle)).setText("Mô tả sản phẩm");
            layoutSectionDesc.findViewById(R.id.ivSectionIcon).setVisibility(View.VISIBLE);
            ((ImageView) layoutSectionDesc.findViewById(R.id.ivSectionIcon)).setImageResource(R.drawable.ic_list);
            layoutSectionDesc.setOnClickListener(v -> {
                ProductDetailUiState state = viewModel.getUiState().getValue();
                if (state != null && state.product != null) {
                    ProductInfoDetailFragment fragment = ProductInfoDetailFragment.newInstance(
                            "Mô tả sản phẩm",
                            state.product.getLongDescription(),
                            ProductInfoDetailFragment.InfoMode.DESCRIPTION
                    );
                    ui.common.FragmentNavigationHelper.loadFragment(getActivity(), fragment);
                }
            });
        }

        layoutSectionIngredients = view.findViewById(R.id.layoutSectionIngredients);
        if (layoutSectionIngredients != null) {
            ((TextView) layoutSectionIngredients.findViewById(R.id.tvSectionTitle)).setText("Thành phần sản phẩm");
            layoutSectionIngredients.findViewById(R.id.ivSectionIcon).setVisibility(View.VISIBLE);
            ((ImageView) layoutSectionIngredients.findViewById(R.id.ivSectionIcon)).setImageResource(R.drawable.ic_beaker);
            layoutSectionIngredients.setOnClickListener(v -> {
                ProductDetailUiState state = viewModel.getUiState().getValue();
                String ingredients = (state != null && state.product != null) ? state.product.getIngredientText() : "Đang cập nhật...";
                ProductInfoDetailFragment fragment = ProductInfoDetailFragment.newInstance(
                        "Thành phần sản phẩm",
                        ingredients,
                        ProductInfoDetailFragment.InfoMode.INGREDIENTS
                );
                ui.common.FragmentNavigationHelper.loadFragment(getActivity(), fragment);
            });
        }

        layoutSectionUsage = view.findViewById(R.id.layoutSectionUsage);
        if (layoutSectionUsage != null) {
            ((TextView) layoutSectionUsage.findViewById(R.id.tvSectionTitle)).setText("Hướng dẫn sử dụng");
            layoutSectionUsage.findViewById(R.id.ivSectionIcon).setVisibility(View.VISIBLE);
            ((ImageView) layoutSectionUsage.findViewById(R.id.ivSectionIcon)).setImageResource(R.drawable.ic_routine);
            layoutSectionUsage.setOnClickListener(v -> {
                ProductDetailUiState state = viewModel.getUiState().getValue();
                String usage = (state != null && state.product != null) ? state.product.getUsageInstruction() : "Đang cập nhật...";
                ProductInfoDetailFragment fragment = ProductInfoDetailFragment.newInstance(
                        "Hướng dẫn sử dụng",
                        usage,
                        ProductInfoDetailFragment.InfoMode.USAGE
                );
                ui.common.FragmentNavigationHelper.loadFragment(getActivity(), fragment);
            });
        }

        View btnWishlist = view.findViewById(R.id.btnWishlist);
        if (btnWishlist != null) {
            btnWishlist.setOnClickListener(v -> viewModel.toggleWishlist());
        }

        View btnCart = view.findViewById(R.id.btnCart);
        if (btnCart != null) {
            btnCart.setOnClickListener(v -> {
                ui.common.FragmentNavigationHelper.loadFragment(getActivity(), new ui.commerce.CartFragment());
            });
        }

        View btnArTryOn = view.findViewById(R.id.btnArTryOn);
        if (btnArTryOn != null) {
            btnArTryOn.setOnClickListener(v -> {
                ProductDetailUiState state = viewModel.getUiState().getValue();
                if (state != null && state.product != null && state.product.hasAr()) {
                    android.content.Intent intent = new android.content.Intent(getContext(), com.example.frontend.feature.arcore.ArCoreTryOnActivity.class);
                    intent.putExtra("product_id", state.product.getId());
                    startActivity(intent);
                }
            });
        }
    }

    private void setupAdapters(View view) {
        imageAdapter = new ProductImageAdapter();
        if (vpGallery != null) {
            vpGallery.setAdapter(imageAdapter);
            vpGallery.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    updateGalleryCounter(position);
                    if (thumbnailAdapter != null) thumbnailAdapter.setSelectedPosition(position);
                    if (rvThumbnails != null) rvThumbnails.smoothScrollToPosition(position);
                    updateSelectedVariantName(position);
                }
            });
        }

        thumbnailAdapter = new ThumbnailAdapter();
        thumbnailAdapter.setListener(position -> {
            if (vpGallery != null) vpGallery.setCurrentItem(position, true);
        });
        if (rvThumbnails != null) {
            rvThumbnails.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
            rvThumbnails.setAdapter(thumbnailAdapter);
        }

        recentlyViewedAdapter = new RecentlyViewedAdapter();
        recentlyViewedAdapter.setListener(new RecentlyViewedAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                ProductDetailFragment fragment = ProductDetailFragment.newInstance(product.getId());
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onAddToCartClick(Product product) {
                handleAddToCart(product);
            }
        });
        if (rvRecentlyViewed != null) {
            rvRecentlyViewed.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
            rvRecentlyViewed.setAdapter(recentlyViewedAdapter);
        }

        relatedAdapter = new HomeProductAdapter();
        relatedAdapter.setOnProductClickListener(new HomeProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                ProductDetailFragment fragment = ProductDetailFragment.newInstance(product.getId());
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onAddToCartClick(Product product) {
                handleAddToCart(product);
            }
        });
        if (rvRelatedProducts != null) {
            rvRelatedProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
            rvRelatedProducts.setAdapter(relatedAdapter);
        }

        reviewPreviewAdapter = new com.example.frontend.feature.product.adapter.ReviewAdapter();
        reviewPreviewAdapter.setOnReviewLikeListener(review -> {
            if (com.example.frontend.data.remote.TokenManager.getInstance(requireContext()).isLoggedIn()) {
                if (reviewActionViewModel != null) {
                    reviewActionViewModel.toggleReviewVote(review.getId());
                }
            } else {
                com.example.frontend.feature.auth.GuestPromptBottomSheet.newInstance(
                        com.example.frontend.core.auth.PendingAuthAction.ActionType.COMMUNITY_INTERACTION
                ).show(getChildFragmentManager(), "GuestPromptBottomSheet");
            }
        });
        reviewPreviewAdapter.setOnReviewReplyListener(this::showReplyDialog);
        reviewPreviewAdapter.setOnReviewClickListener(review -> {
            ReviewHubFragment fragment = ReviewHubFragment.newInstance(productId);
            ui.common.FragmentNavigationHelper.loadFragment(getActivity(), fragment);
        });
        if (rvReviewPreview != null) {
            rvReviewPreview.setLayoutManager(new LinearLayoutManager(getContext()));
            rvReviewPreview.setAdapter(reviewPreviewAdapter);
            rvReviewPreview.setNestedScrollingEnabled(false);
        }

        reviewMediaAdapter = new ReviewMediaAdapter();
        if (layoutReviewSummary != null) {
            RecyclerView rvReviewMedia = layoutReviewSummary.findViewById(R.id.rvReviewMediaPreview);
            if (rvReviewMedia != null) {
                rvReviewMedia.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
                rvReviewMedia.setAdapter(reviewMediaAdapter);
            }
        }
    }

    private void observeViewModel() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            // Xử lý trạng thái Loading
            if (layoutLoading != null) layoutLoading.setVisibility(state.isLoading ? View.VISIBLE : View.GONE);

            // Xử lý trạng thái Error
            if (!state.isLoading && state.error != null) {
                if (layoutError != null) {
                    layoutError.setVisibility(View.VISIBLE);
                    TextView tvMsg = layoutError.findViewById(R.id.tvErrorDescription);
                    if (tvMsg != null) tvMsg.setText(state.error);
                }
                Toast.makeText(getContext(), state.error, Toast.LENGTH_LONG).show();
            } else {
                if (layoutError != null) layoutError.setVisibility(View.GONE);
            }

            if (state.product != null) {
                Log.d(TAG, "Loaded product = " + state.product.getName());
                bindProductData(state);
            }
        });

        viewModel.getAddToCartResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case SUCCESS:
                    Toast.makeText(getContext(), "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                    break;
                case ERROR:
                    Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        viewModel.getBuyNowResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case SUCCESS:
                    // Navigate to Checkout with selected items from session
                    if (result.data != null && result.data.getItems() != null) {
                        ArrayList<CartItemDto> selectedItems = new ArrayList<>();
                        for (CheckoutSessionDto.CheckoutItemDto checkoutItem : result.data.getItems()) {
                            CartItemDto cartItem = CartItemDto.createMock(
                                checkoutItem.getId(),
                                checkoutItem.getProductName(),
                                checkoutItem.getVariantName(),
                                checkoutItem.getPrice(),
                                checkoutItem.getQuantity(),
                                true,
                                checkoutItem.getImageUrl()
                            );
                            cartItem.setProductId(checkoutItem.getProductId());
                            cartItem.setVariantId(checkoutItem.getVariantId());
                            cartItem.setBrandNameSnapshot(checkoutItem.getBrandName());
                            selectedItems.add(cartItem);
                        }

                        ui.commerce.CheckoutFragment checkoutFragment = new ui.commerce.CheckoutFragment();
                        Bundle args = new Bundle();
                        args.putSerializable("selected_items", selectedItems);
                        checkoutFragment.setArguments(args);

                        ui.common.FragmentNavigationHelper.loadFragment(getActivity(), checkoutFragment);
                    } else {
                        ui.common.FragmentNavigationHelper.loadFragment(getActivity(), new ui.commerce.CheckoutFragment());
                    }
                    break;
                case ERROR:
                    Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                    break;
                case LOADING:
                    // Show some loading indicator if needed
                    break;
            }
        });
    }

    private void bindProductData(ProductDetailUiState state) {
        Product product = state.product;
        if (product == null) return;

        if (tvName != null) tvName.setText(product.getName());
        if (tvBrand != null) tvBrand.setText(product.getBrand());

        TextView tvReviewSectionTitle = null;
        if (layoutReviewSummary != null) {
            tvReviewSectionTitle = layoutReviewSummary.findViewById(R.id.tvReviewSectionTitle);
        }

        double averageRating = 0;
        int reviewCount = 0;

        if (state.reviewSummary != null) {
            averageRating = state.reviewSummary.getAverageRating();
            reviewCount = state.reviewSummary.getReviewCount();
        } else if (state.product != null) {
            averageRating = state.product.getAverageRatingValue();
        }

        if (tvReviewCount != null) tvReviewCount.setText(String.format(Locale.US, "%d đánh giá", reviewCount));
        if (tvRating != null) tvRating.setText(String.format(Locale.US, "%.1f", averageRating));

        bindReviewSectionTitle(tvReviewSectionTitle, averageRating, reviewCount);

        if (tvSoldCount != null) tvSoldCount.setText(String.format(Locale.US, "Đã bán %d+", product.getBought()));

        if (layoutReviewSummary != null) {
            TextView tvAiSummary = layoutReviewSummary.findViewById(R.id.tvAiSummaryText);
            View cardAi = layoutReviewSummary.findViewById(R.id.cardAiSummary);
            if (tvAiSummary != null && state.reviewSummary != null && state.reviewSummary.getAiSummary() != null && !state.reviewSummary.getAiSummary().isEmpty()) {
                tvAiSummary.setText(state.reviewSummary.getAiSummary());
                if (cardAi != null) cardAi.setVisibility(View.VISIBLE);
            } else if (tvAiSummary != null) {
                if (cardAi != null) cardAi.setVisibility(View.GONE);
            }

            RecyclerView rvReviewMedia = layoutReviewSummary.findViewById(R.id.rvReviewMediaPreview);
            if (rvReviewMedia != null) {
                List<ReviewMediaDto> mediaPreview = null;
                if (state.reviewSummary != null) {
                    mediaPreview = state.reviewSummary.getReviewMediaPreview();
                }
                
                if ((mediaPreview == null || mediaPreview.isEmpty()) && state.reviewMediaPreview != null) {
                    mediaPreview = state.reviewMediaPreview;
                }

                if (mediaPreview == null || mediaPreview.isEmpty()) {
                    rvReviewMedia.setVisibility(View.GONE);
                } else {
                    List<ReviewMediaDto> validMedia = new ArrayList<>();
                    for (ReviewMediaDto media : mediaPreview) {
                        if (media == null) continue;
                        String url = media.getMediaUrl();
                        if (url == null || url.trim().isEmpty()) continue;
                        if (url.startsWith("content://") || url.startsWith("file://")) {
                            Log.e(TAG, "Invalid review media URL from backend: " + url);
                            continue;
                        }
                        validMedia.add(media);
                    }

                    if (validMedia.isEmpty()) {
                        rvReviewMedia.setVisibility(View.GONE);
                    } else {
                        rvReviewMedia.setVisibility(View.VISIBLE);
                        reviewMediaAdapter.submitList(validMedia);
                    }
                }
            }
        }

        if (tvDesc != null) {
            tvDesc.setText(product.getShortDescription() != null ? product.getShortDescription() : product.getSubcategory());
        }

        // Section descriptions as fallbacks
        if (layoutSectionDesc != null) {
            TextView tvSub = layoutSectionDesc.findViewById(R.id.tvSectionSubtitle);
            if (tvSub != null) tvSub.setText(product.getShortDescription());
        }
        if (layoutSectionIngredients != null) {
            TextView tvSub = layoutSectionIngredients.findViewById(R.id.tvSectionSubtitle);
            if (tvSub != null) {
                String ingredients = product.getIngredientText();
                if (ingredients != null && ingredients.length() > 30) ingredients = ingredients.substring(0, 30) + "...";
                tvSub.setText(ingredients != null ? ingredients : "Xem chi tiết");
            }
        }

        if (tvPrice != null) tvPrice.setText(formatPrice(product.getPriceValue()));

        if (reviewPreviewAdapter != null) {
            if (state.reviewPreviewList != null && !state.reviewPreviewList.isEmpty()) {
                rvReviewPreview.setVisibility(View.VISIBLE);
                
                // Chỉ hiển thị tối đa 2 đánh giá (ngẫu nhiên)
                List<ReviewDto> previewList = new ArrayList<>(state.reviewPreviewList);
                if (previewList.size() > 2) {
                    Collections.shuffle(previewList);
                    previewList = previewList.subList(0, 2);
                }
                reviewPreviewAdapter.submitList(previewList);
            } else {
                rvReviewPreview.setVisibility(View.GONE);
            }
        }

        if (tvComparePrice != null) {
            if (product.getCompareAtPrice() != null && product.getCompareAtPrice() > product.getPriceValue()) {
                tvComparePrice.setVisibility(View.VISIBLE);
                tvComparePrice.setText(formatPrice(product.getCompareAtPrice()));
                tvComparePrice.setPaintFlags(tvComparePrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                tvComparePrice.setVisibility(View.GONE);
            }
        }

        if (imageAdapter != null) imageAdapter.setMediaList(state.mediaList);
        if (thumbnailAdapter != null) thumbnailAdapter.setMediaList(state.mediaList);
        if (vpGallery != null) updateGalleryCounter(vpGallery.getCurrentItem());

        if (relatedAdapter != null) relatedAdapter.setProducts(state.relatedProducts);
        if (recentlyViewedAdapter != null) recentlyViewedAdapter.setProducts(state.recentlyViewed);

        if (cgBadges != null) {
            cgBadges.removeAllViews();
            if (product.isBestSeller()) addBadgeChip("Best Seller");
            if (product.hasAr()) addBadgeChip("AR Try-on");
            if (state.inventory != null && "low_stock".equals(state.inventory.getStatus())) addBadgeChip("Low Stock");
        }

        if (layoutRecentlyViewed != null) {
            if (state.recentlyViewed == null || state.recentlyViewed.isEmpty()) {
                layoutRecentlyViewed.setVisibility(View.GONE);
            } else {
                layoutRecentlyViewed.setVisibility(View.VISIBLE);
            }
        }

        if (state.detailedSkinMatch != null && layoutSkinMatch != null) {
            bindSkinMatchData(state.detailedSkinMatch);
        } else if (state.skinMatch != null && layoutSkinMatch != null) {
            String fallbackStatus = state.skinMatch.getStatus();
            if ("INSUFFICIENT_PRODUCT_DATA".equals(fallbackStatus) || "TEMPORARILY_UNAVAILABLE".equals(fallbackStatus)) {
                layoutSkinMatch.setVisibility(View.GONE);
            } else {
                // Fallback to legacy data
                layoutSkinMatch.setVisibility(View.VISIBLE);
                TextView tvScore = layoutSkinMatch.findViewById(R.id.tvSkinMatchScore);
            int fallbackScore = state.skinMatch.getScore();
            if (state.skinMatch.getEstimatedScore() != null && state.skinMatch.getEstimatedScore() > 0 && fallbackScore == 0) {
                if (tvScore != null) tvScore.setText(String.format(Locale.US, "≈%d%%", state.skinMatch.getEstimatedScore()));
            } else {
                if (tvScore != null) tvScore.setText(String.format(Locale.US, "%d%%", fallbackScore));
            }
            TextView tvSubtitle = layoutSkinMatch.findViewById(R.id.tvSkinMatchSubtitle);
            if (tvSubtitle != null) tvSubtitle.setText("Phù hợp với làn da của bạn");
        }
        } else if (layoutSkinMatch != null) {
            layoutSkinMatch.setVisibility(View.GONE);
        }

        if (state.inventory != null && layoutOutOfStock != null) {
            if ("out_of_stock".equals(state.inventory.getStatus())) {
                layoutOutOfStock.setVisibility(View.VISIBLE);
                if (btnAddToCart != null) btnAddToCart.setEnabled(false);
                if (btnBuyNow != null) btnBuyNow.setEnabled(false);
            } else {
                layoutOutOfStock.setVisibility(View.GONE);
                if (btnAddToCart != null) btnAddToCart.setEnabled(true);
                if (btnBuyNow != null) btnBuyNow.setEnabled(true);
            }
        }

        View btnWishlist = getView() != null ? getView().findViewById(R.id.btnWishlist) : null;
        if (btnWishlist != null) btnWishlist.setSelected(state.isWishlisted);

        View btnArTryOn = getView() != null ? getView().findViewById(R.id.btnArTryOn) : null;
        if (btnArTryOn != null) {
            btnArTryOn.setVisibility(product.hasAr() ? View.VISIBLE : View.GONE);
            if (product.hasAr() && btnArTryOn instanceof TextView) {
                String arType = product.getArType() != null ? product.getArType().toUpperCase() : "";
                String arLabel = "Thử màu AR";
                if ("LIPS".equals(arType)) {
                    arLabel = "Thử màu AR (Son môi)";
                } else if ("CHEEKS".equals(arType)) {
                    arLabel = "Thử màu AR (Phấn má)";
                } else if ("EYES".equals(arType)) {
                    arLabel = "Thử màu AR (Phấn mắt)";
                }
                ((TextView) btnArTryOn).setText(arLabel);
            }
        }
    }

    private void bindSkinMatchData(com.example.frontend.data.model.product.SkinMatchDto data) {
        if (layoutSkinMatch == null) return;

        TextView tvTitle = layoutSkinMatch.findViewById(R.id.tvSkinMatchTitle);
        TextView tvSubtitle = layoutSkinMatch.findViewById(R.id.tvSkinMatchSubtitle);
        TextView tvScore = layoutSkinMatch.findViewById(R.id.tvSkinMatchScore);
        View layoutScoreCircle = layoutSkinMatch.findViewById(R.id.layoutScoreCircle);
        View ivIcon = layoutSkinMatch.findViewById(R.id.ivSkinMatchIcon);

        switch (data.getStatus()) {
            case READY:
                layoutSkinMatch.setVisibility(View.VISIBLE);

                // If confidence is low, backend returns score=0 but estimated_score has the value.
                // Show "≈XX%" to indicate the score is an estimate, not a guaranteed match.
                if ((data.getScore() == null || data.getScore() == 0) && data.getEstimatedScore() != null && data.getEstimatedScore() > 0) {
                    if (tvScore != null) tvScore.setText(String.format(Locale.US, "≈%d%%", data.getEstimatedScore()));
                } else {
                    if (tvScore != null) tvScore.setText(String.format(Locale.US, "%d%%", data.getScore() != null ? data.getScore() : 0));
                }

                if (tvSubtitle != null) {
                    if (data.getMatchExplanation() != null && !data.getMatchExplanation().isEmpty()) {
                        tvSubtitle.setText(data.getMatchExplanation());
                    } else if (data.getMatchLevel() != null) {
                        switch (data.getMatchLevel()) {
                            case EXCELLENT_MATCH: tvSubtitle.setText("Phù hợp tuyệt vời với làn da của bạn"); break;
                            case GOOD_MATCH: tvSubtitle.setText("Sản phẩm tốt cho làn da của bạn"); break;
                            case MODERATE_MATCH: tvSubtitle.setText("Phù hợp ở mức trung bình"); break;
                            case CAUTION: tvSubtitle.setText("Cần lưu ý khi sử dụng"); break;
                            default: tvSubtitle.setText("Phù hợp với làn da của bạn"); break;
                        }
                    } else {
                        tvSubtitle.setText("Phù hợp với làn da của bạn");
                    }
                }

                // Color mapping based on match level
                int colorRes = R.color.button;
                if (data.getMatchLevel() != null) {
                    switch (data.getMatchLevel()) {
                        case EXCELLENT_MATCH:
                        case GOOD_MATCH:
                            colorRes = R.color.success;
                            break;
                        case MODERATE_MATCH:
                            colorRes = R.color.status_pending_text;
                            break;
                        case CAUTION:
                            // CAUTION = hard conflict present; use warning amber, not full error red
                            colorRes = R.color.status_pending_text;
                            break;
                        default:
                            colorRes = R.color.button;
                            break;
                    }
                }
                if (tvScore != null) tvScore.setTextColor(ContextCompat.getColor(getContext(), colorRes));
                if (layoutScoreCircle != null) layoutScoreCircle.setBackgroundTintList(android.content.res.ColorStateList.valueOf(ContextCompat.getColor(getContext(), colorRes)));
                break;

            case PROFILE_REQUIRED:
                layoutSkinMatch.setVisibility(View.VISIBLE);
                if (tvScore != null) tvScore.setText("?");
                if (tvSubtitle != null) {
                    if (data.getMatchExplanation() != null && !data.getMatchExplanation().isEmpty()) {
                        tvSubtitle.setText(data.getMatchExplanation());
                    } else {
                        tvSubtitle.setText("Hoàn thiện hồ sơ để xem độ phù hợp");
                    }
                }
                break;

            case PROFILE_INCOMPLETE:
                layoutSkinMatch.setVisibility(View.VISIBLE);
                // Show estimated score if available, otherwise "?"
                if (data.getEstimatedScore() != null && data.getEstimatedScore() > 0) {
                    if (tvScore != null) tvScore.setText(String.format(Locale.US, "≈%d%%", data.getEstimatedScore()));
                } else if (data.getScore() != null && data.getScore() > 0) {
                    if (tvScore != null) tvScore.setText(String.format(Locale.US, "%d%%", data.getScore()));
                } else {
                    if (tvScore != null) tvScore.setText("?");
                }
                if (tvSubtitle != null) {
                    if (data.getMatchExplanation() != null && !data.getMatchExplanation().isEmpty()) {
                        tvSubtitle.setText(data.getMatchExplanation());
                    } else {
                        tvSubtitle.setText("Cập nhật thêm thông tin da để kết quả chính xác hơn");
                    }
                }
                // Use muted color for estimated/incomplete state
                if (tvScore != null) tvScore.setTextColor(ContextCompat.getColor(getContext(), R.color.status_pending_text));
                if (layoutScoreCircle != null) layoutScoreCircle.setBackgroundTintList(android.content.res.ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.status_pending_text)));
                break;

            case CAUTION:
                // Product-level caution (e.g. hard conflict with profile) — show with warning style
                layoutSkinMatch.setVisibility(View.VISIBLE);
                if (tvScore != null) {
                    if (data.getScore() != null && data.getScore() > 0) {
                        tvScore.setText(String.format(Locale.US, "%d%%", data.getScore()));
                    } else {
                        tvScore.setText("!");
                    }
                    tvScore.setTextColor(ContextCompat.getColor(getContext(), R.color.error));
                }
                if (layoutScoreCircle != null) layoutScoreCircle.setBackgroundTintList(android.content.res.ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.error)));
                if (tvSubtitle != null) {
                    if (data.getMatchExplanation() != null && !data.getMatchExplanation().isEmpty()) {
                        tvSubtitle.setText(data.getMatchExplanation());
                    } else {
                        tvSubtitle.setText("Cần lưu ý: sản phẩm có thể không phù hợp");
                    }
                }
                break;

            case INSUFFICIENT_PRODUCT_DATA:
            case TEMPORARILY_UNAVAILABLE:
            default:
                layoutSkinMatch.setVisibility(View.GONE);
                break;
        }
    }

    private void bindReviewInsightData(com.example.frontend.data.model.product.ReviewInsightDto data) {
        if (layoutReviewSummary == null) return;

        View cardAiSummary = layoutReviewSummary.findViewById(R.id.cardAiSummary);
        TextView tvAiSummaryText = layoutReviewSummary.findViewById(R.id.tvAiSummaryText);

        if (cardAiSummary == null || tvAiSummaryText == null) return;

        switch (data.getStatus()) {
            case READY:
            case STALE:
                cardAiSummary.setVisibility(View.VISIBLE);
                tvAiSummaryText.setText(data.getShortSummary());
                break;

            case PENDING:
            case GENERATING:
                cardAiSummary.setVisibility(View.VISIBLE);
                tvAiSummaryText.setText("Đang tổng hợp đánh giá bằng AI...");
                break;

            case INSUFFICIENT_REVIEWS:
            case FAILED:
            case DISABLED:
            default:
                cardAiSummary.setVisibility(View.GONE);
                break;
        }
    }

    private void handleAddToCart(Product product) {
        if (product == null || product.getId() == null) return;
        viewModel.addToCart(product.getId(), null, 1);
    }

    private void handleStickyCtaClick(VariantSelectorBottomSheet.ActionMode mode) {
        ProductDetailUiState state = viewModel.getUiState().getValue();
        if (state == null || state.product == null) {
            Toast.makeText(getContext(), "Đang tải thông tin sản phẩm...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (state.inventory != null && "out_of_stock".equals(state.inventory.getStatus())) {
            Toast.makeText(getContext(), "Sản phẩm hiện đang hết hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        VariantSelectorBottomSheet bottomSheet = VariantSelectorBottomSheet.newInstance(state.product, state.variants, mode);
        bottomSheet.setListener((variant, selectedMode, selectedQuantity) -> {
            String variantId = variant != null ? variant.getId() : null;
            if (selectedMode == VariantSelectorBottomSheet.ActionMode.BUY_NOW) {
                // Mock "Buy Now" by constructing CartItemDto locally and navigating to Checkout
                Product product = state.product;
                if (product != null) {
                    CartItemDto cartItem = CartItemDto.createMock(
                        "buy_now_" + System.currentTimeMillis(),
                        product.getName(),
                        variant != null ? variant.getVariantName() : "Mặc định",
                        variant != null && variant.getPrice() != null ? variant.getPrice() : product.getPriceValue(),
                        selectedQuantity,
                        true,
                        variant != null && variant.getImageUrl() != null && !variant.getImageUrl().isEmpty() ?
                            variant.getImageUrl() : (state.mediaList != null && !state.mediaList.isEmpty() ? state.mediaList.get(0).getUrl() : "")
                    );
                    cartItem.setProductId(productId);
                    cartItem.setVariantId(variantId);
                    cartItem.setBrandNameSnapshot(product.getBrand());

                    ArrayList<CartItemDto> selectedItems = new ArrayList<>();
                    selectedItems.add(cartItem);

                    ui.commerce.CheckoutFragment checkoutFragment = new ui.commerce.CheckoutFragment();
                    Bundle args = new Bundle();
                    args.putSerializable("selected_items", selectedItems);
                    checkoutFragment.setArguments(args);

                    ui.common.FragmentNavigationHelper.loadFragment(getActivity(), checkoutFragment);
                }
            } else {
                viewModel.addToCart(productId, variantId, selectedQuantity);
            }
        });
        bottomSheet.show(getChildFragmentManager(), "VariantSelector");
    }

    private void updateGalleryCounter(int position) {
        if (imageAdapter == null || tvGalleryCounter == null) return;
        int total = imageAdapter.getItemCount();
        if (total > 0) {
            tvGalleryCounter.setText(String.format(Locale.US, "%d/%d", position + 1, total));
            tvGalleryCounter.setVisibility(View.VISIBLE);
        } else {
            tvGalleryCounter.setVisibility(View.GONE);
        }
    }

    private void updateSelectedVariantName(int position) {
        ProductDetailUiState state = viewModel.getUiState().getValue();
        if (tvSelectedVariantName != null && state != null && state.mediaList != null && position < state.mediaList.size()) {
            tvSelectedVariantName.setVisibility(View.GONE);
        }
    }

    private void addBadgeChip(String text) {
        if (getContext() == null || cgBadges == null) return;
        Chip chip = new Chip(getContext());
        chip.setText(text);
        chip.setChipBackgroundColorResource(R.color.pink_bg);
        chip.setTextColor(ContextCompat.getColor(getContext(), R.color.button));
        chip.setChipStrokeWidth(0f);
        chip.setTextSize(10f);
        cgBadges.addView(chip);
    }

    private String formatPrice(double price) {
        return String.format(Locale.US, "%,.0fđ", price).replace(",", ".");
    }

    private String formatAverageRating(double rating) {
        return String.format(Locale.US, "%.1f", Math.max(0, rating));
    }

    private String formatReviewCount(int count) {
        if (count >= 1_000_000) {
            return String.format(Locale.US, "%.1fM", count / 1_000_000f).replace(".0M", "M");
        }
        if (count >= 1000) {
            return String.format(Locale.US, "%.1fK", count / 1000f).replace(".0K", "K");
        }
        return String.valueOf(Math.max(0, count));
    }

    private void bindReviewSectionTitle(TextView titleView, double averageRating, int reviewCount) {
        if (titleView == null) return;

        String ratingText = formatAverageRating(averageRating);
        String countText = formatReviewCount(reviewCount);

        String fullText = ratingText + "  Đánh giá sản phẩm (" + countText + ")";
        SpannableString spannable = new SpannableString(fullText);

        Drawable starDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_star_filled_16);
        if (starDrawable != null) {
            int size = (int) (14 * getResources().getDisplayMetrics().density);
            starDrawable.setBounds(0, 0, size, size);
            DrawableCompat.setTint(starDrawable.mutate(), ContextCompat.getColor(requireContext(), R.color.button));

            int starIndex = ratingText.length() + 1;
            spannable.setSpan(
                    new ImageSpan(starDrawable, ImageSpan.ALIGN_BASELINE),
                    starIndex,
                    starIndex + 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        titleView.setText(spannable);
    }

    private void observeReviewActionViewModel() {
        if (reviewActionViewModel == null) return;

        reviewActionViewModel.getVoteResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case SUCCESS:
                    if (result.data != null && reviewPreviewAdapter != null) {
                        reviewPreviewAdapter.updateReviewVoteState(
                                result.data.getReviewId(),
                                result.data.isLiked(),
                                result.data.getHelpfulCount()
                        );
                    }
                    break;
                case ERROR:
                    Toast.makeText(getContext(), result.message != null ? result.message : "Không thể cập nhật yêu thích", Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        reviewActionViewModel.getCommentResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case SUCCESS:
                    Toast.makeText(getContext(), "Đã gửi phản hồi", Toast.LENGTH_SHORT).show();
                    if (result.data != null && reviewPreviewAdapter != null) {
                        reviewPreviewAdapter.addCommentToReview(result.data);
                    }
                    break;
                case ERROR:
                    Toast.makeText(getContext(), result.message != null ? result.message : "Không thể gửi phản hồi", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void showReplyDialog(com.example.frontend.data.model.review.ReviewDto review) {
        if (!com.example.frontend.data.remote.TokenManager.getInstance(requireContext()).isLoggedIn()) {
            com.example.frontend.feature.auth.GuestPromptBottomSheet.newInstance(
                    com.example.frontend.core.auth.PendingAuthAction.ActionType.COMMUNITY_INTERACTION
            ).show(getChildFragmentManager(), "GuestPromptBottomSheet");
            return;
        }

        com.google.android.material.bottomsheet.BottomSheetDialog dialog = new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.layout_comment_input, null);

        android.widget.EditText edtComment = view.findViewById(R.id.edtCommentContent);
        View btnSend = view.findViewById(R.id.btnSendComment);
        TextView tvTitle = view.findViewById(R.id.tvCommentTitle);

        if (tvTitle != null) {
            String userName = review.getCustomer() != null ? review.getCustomer().getFullName() : "người dùng";
            tvTitle.setText(getString(R.string.reply_hint_format, userName));
        }

        btnSend.setOnClickListener(v -> {
            String content = edtComment.getText().toString().trim();
            if (content.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show();
                return;
            }
            if (reviewActionViewModel != null) {
                reviewActionViewModel.addReviewComment(review.getId(), content);
            }
            dialog.dismiss();
        });

        dialog.setContentView(view);
        dialog.show();
    }
}
