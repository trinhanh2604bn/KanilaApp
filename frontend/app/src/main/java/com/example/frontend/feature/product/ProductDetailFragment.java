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
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;
import android.util.Log;

public class ProductDetailFragment extends Fragment {
    private static final String TAG = "ProductDetail";
    private static final String ARG_PRODUCT_ID = "product_id";
    
    private ProductDetailViewModel viewModel;
    private String productId;
    
    private TextView tvName, tvBrand, tvPrice, tvComparePrice, tvGalleryCounter, tvRating, tvReviewCount, tvSoldCount, tvDesc, tvSelectedVariantName;
    private ViewPager2 vpGallery;
    private RecyclerView rvThumbnails, rvRecentlyViewed, rvRelatedProducts;
    private ChipGroup cgBadges;
    private View layoutSkinMatch, layoutReviewSummary, layoutOutOfStock, layoutLoading, layoutError, layoutRecentlyViewed;
    private View btnAddToCart, btnBuyNow;
    private View layoutSectionDesc, layoutSectionIngredients, layoutSectionUsage;

    private ProductImageAdapter imageAdapter;
    private ThumbnailAdapter thumbnailAdapter;
    private RecentlyViewedAdapter recentlyViewedAdapter;
    private HomeProductAdapter relatedAdapter;
    private ReviewMediaAdapter reviewMediaAdapter;

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
        
        initViews(view);
        setupAdapters(view);
        observeViewModel();
        
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
                if (state != null && state.skinMatch != null) {
                    SkinMatchScoreFragment fragment = SkinMatchScoreFragment.newInstance(
                            state.skinMatch.getScore(),
                            state.skinMatch.getLevel(),
                            state.skinMatch.getProfileChips()
                    );
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.main, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }

        if (layoutReviewSummary != null) {
            View btnViewAllReviews = layoutReviewSummary.findViewById(R.id.btnViewAllReviews);
            if (btnViewAllReviews != null) {
                btnViewAllReviews.setOnClickListener(v -> {
                    ReviewHubFragment fragment = ReviewHubFragment.newInstance(productId);
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.main, fragment)
                            .addToBackStack(null)
                            .commit();
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
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.main, fragment)
                            .addToBackStack(null)
                            .commit();
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
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.main, fragment)
                        .addToBackStack(null)
                        .commit();
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
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.main, fragment)
                        .addToBackStack(null)
                        .commit();
            });
        }

        View btnWishlist = view.findViewById(R.id.btnWishlist);
        if (btnWishlist != null) {
            btnWishlist.setOnClickListener(v -> viewModel.toggleWishlist());
        }
        
        View btnCart = view.findViewById(R.id.btnCart);
        if (btnCart != null) {
            btnCart.setOnClickListener(v -> {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.main, new ui.commerce.CartFragment())
                        .addToBackStack(null)
                        .commit();
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
        recentlyViewedAdapter.setListener(product -> {
            ProductDetailFragment fragment = ProductDetailFragment.newInstance(product.getId());
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.main, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        if (rvRecentlyViewed != null) {
            rvRecentlyViewed.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
            rvRecentlyViewed.setAdapter(recentlyViewedAdapter);
        }

        relatedAdapter = new HomeProductAdapter();
        relatedAdapter.setOnProductClickListener(product -> {
            ProductDetailFragment fragment = ProductDetailFragment.newInstance(product.getId());
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.main, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        if (rvRelatedProducts != null) {
            rvRelatedProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
            rvRelatedProducts.setAdapter(relatedAdapter);
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
                    // Navigate to Cart
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.main, new ui.commerce.CartFragment())
                            .addToBackStack(null)
                            .commit();
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
                    // Navigate to Checkout
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.main, new ui.commerce.CheckoutFragment())
                            .addToBackStack(null)
                            .commit();
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
        if (tvRating != null) tvRating.setText(String.format(Locale.US, "%.1f", product.getAverageRatingValue()));
        if (tvReviewCount != null) tvReviewCount.setText(String.format(Locale.US, "%d đánh giá", state.reviewSummary != null ? state.reviewSummary.getReviewCount() : 0));
        if (tvSoldCount != null) tvSoldCount.setText(String.format(Locale.US, "Đã bán %d+", product.getBought()));
        
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
        
        if (state.skinMatch != null && layoutSkinMatch != null) {
            layoutSkinMatch.setVisibility(View.VISIBLE);
            TextView tvScore = layoutSkinMatch.findViewById(R.id.tvSkinMatchScore);
            if (tvScore != null) tvScore.setText(String.format(Locale.US, "%d%%", state.skinMatch.getScore()));
            View ivIcon = layoutSkinMatch.findViewById(R.id.ivSkinMatchIcon);
            if (ivIcon != null) ivIcon.setVisibility(View.VISIBLE);
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
                viewModel.buyNow(productId, variantId, selectedQuantity);
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
}
