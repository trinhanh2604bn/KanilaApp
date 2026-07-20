package com.example.frontend.ui.category;

import android.content.Intent;
import android.content.res.ColorStateList;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.frontend.R;
import com.example.frontend.model.HomeBannerItem;

import com.example.frontend.data.model.category.CategoryDto;
import com.example.frontend.data.repository.CategoryRepository;
import com.bumptech.glide.Glide;
import com.example.frontend.data.repository.CatalogRepository;
import com.example.frontend.data.repository.ProductRepository;
import com.example.frontend.feature.cart.CartViewModel;
import com.example.frontend.data.model.cart.AddToCartRequest;
import com.example.frontend.data.model.product.ProductVariantDto;
import com.example.frontend.data.remote.TokenManager;
import com.example.frontend.feature.auth.GuestPromptBottomSheet;
import com.example.frontend.core.auth.PendingAuthAction;
import com.example.frontend.feature.product.ProductDetailFragment;
import com.example.frontend.feature.product.VariantSelectorBottomSheet;
import com.example.frontend.model.Brand;
import com.example.frontend.model.Product;
import com.example.frontend.data.remote.NetworkResult;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductCategoryFragment extends Fragment {

    private ViewPager2 vpCategoryBanner;
    private final Handler autoSlideHandler = new Handler(Looper.getMainLooper());
    private Runnable autoSlideRunnable;
    private final List<View> indicators = new ArrayList<>();
    private CategoryRepository categoryRepository;
    private CatalogRepository catalogRepository;
    private ProductRepository productRepository;
    private CartViewModel cartViewModel;
    private RecyclerView rvFlashSaleProducts;
    private FlashSaleProductAdapter flashSaleAdapter;
    private TextView tvFlashHour, tvFlashMinute, tvFlashSecond;
    private final Handler countdownHandler = new Handler(Looper.getMainLooper());
    private Runnable countdownRunnable;
    private long remainingTimeMillis = 46 * 60 * 1000 + 52 * 1000; // Mock 00:46:52

    private final Map<String, Integer> categoryCardMap = new HashMap<String, Integer>() {{
        put("FACE", R.id.cardCategoryFace);
        put("LIPS", R.id.cardCategoryLips);
        put("EYES", R.id.cardCategoryEyes);
        put("CHEEKS", R.id.cardCategoryCheeks);
        put("GIFT", R.id.cardCategoryGift);
        put("MINITRAVEL", R.id.cardCategoryBrushes);
    }};

    private final Map<String, Integer> categoryImageMap = new HashMap<String, Integer>() {{
        put("FACE", R.drawable.img_foudation);
        put("LIPS", R.drawable.img_lipstick);
        put("EYES", R.drawable.img_eyeshadow);
        put("CHEEKS", R.drawable.img_blush);
        put("GIFT", R.drawable.img_gift);
        put("MINITRAVEL", R.drawable.img_brush);
    }};

    private final Map<String, Integer> categoryIconMap = new HashMap<String, Integer>() {{
        put("FACE", R.drawable.ic_face);
        put("LIPS", R.drawable.ic_lipstick);
        put("EYES", R.drawable.ic_eye);
        put("CHEEKS", R.drawable.ic_blush);
        put("GIFT", R.drawable.ic_gift);
        put("MINITRAVEL", R.drawable.ic_brush);
    }};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_product_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        categoryRepository = new CategoryRepository(requireContext());
        catalogRepository = new CatalogRepository(requireContext());
        productRepository = new ProductRepository(requireContext());
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

        setupTopBar(view);
        setupHeroSlider(view);
        
        loadRootCategories(view);
        setupSpecialCollectionCards(view);
        loadFeaturedBrands(view);
        setupFlashSaleProducts(view);
        setupFlashSaleCountdown(view);
        loadFlashSaleProducts();

        TextView tvSeeAllBrands = view.findViewById(R.id.tvSeeAllBrands);
        if (tvSeeAllBrands != null) {
            tvSeeAllBrands.setOnClickListener(v -> {
                ui.common.FragmentNavigationHelper.replaceFragment(getActivity(), new BrandPageFragment());
            });
        }

        TextView tvSeeAllFlashSale = view.findViewById(R.id.tvSeeAllFlashSale);
        if (tvSeeAllFlashSale != null) {
            tvSeeAllFlashSale.setOnClickListener(v -> {
                ui.common.FragmentNavigationHelper.replaceFragment(
                        getActivity(),
                        new FlashSaleFragment()
                );
            });
        }

        TextView tvSeeAllVoucher = view.findViewById(R.id.tvSeeAllVoucher);
        if (tvSeeAllVoucher != null) {
            tvSeeAllVoucher.setOnClickListener(v -> {
                ui.common.FragmentNavigationHelper.replaceFragment(
                        getActivity(),
                        new VoucherCenterFragment()
                );
            });
        }
    }

    private void setupTopBar(View root) {
        View topBar = root.findViewById(R.id.includeTopBar);
        if (topBar == null) return;
        TextView tvTitle = topBar.findViewById(R.id.tvTopBarTitle);
        if (tvTitle != null) tvTitle.setText(R.string.top_bar_category_title);

        ImageButton btnBack = topBar.findViewById(R.id.btnTopBarBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }

        // Repurpose Search icon to Cart for Category landing page
        ImageButton btnCart = topBar.findViewById(R.id.btnTopBarSearch);
        if (btnCart != null) {
            btnCart.setImageResource(R.drawable.ic_cart);
            btnCart.setContentDescription(getString(R.string.cart));
            btnCart.setOnClickListener(v -> {
                ui.common.FragmentNavigationHelper.loadFragment(getActivity(), new ui.commerce.CartFragment());
            });

            // Bind Cart Badge
            ui.common.CartBadgeHelper.bindBadge(getViewLifecycleOwner(), topBar, cartViewModel);
        }
    }

    private void setupHeroSlider(View root) {
        vpCategoryBanner = root.findViewById(R.id.vpCategoryBanner);
        if (vpCategoryBanner == null) return;

        indicators.clear();
        indicators.add(root.findViewById(R.id.indicator0));
        indicators.add(root.findViewById(R.id.indicator1));
        indicators.add(root.findViewById(R.id.indicator2));

        CategoryBannerAdapter bannerAdapter = new CategoryBannerAdapter();
        vpCategoryBanner.setAdapter(bannerAdapter);

        List<HomeBannerItem> items = new ArrayList<>();
        items.add(new HomeBannerItem("1", "", "", "", "Mua ngay", null, R.drawable.img_cate_list_1, "promotion", "1", true, 1));
        items.add(new HomeBannerItem("2", "", "", "", "Khám phá", null, R.drawable.img_cate_list_2, "promotion", "2", true, 2));
        items.add(new HomeBannerItem("3", "", "", "", "Xem thêm", null, R.drawable.img_cate_list_3, "promotion", "3", true, 3));

        bannerAdapter.setItems(items);

        int startPosition = (Integer.MAX_VALUE / 2) - ((Integer.MAX_VALUE / 2) % items.size());
        vpCategoryBanner.setCurrentItem(startPosition, false);
        updateIndicators(startPosition % items.size());

        setupAutoSlide(items.size());

        vpCategoryBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                int realPos = position % items.size();
                updateIndicators(realPos);
                autoSlideHandler.removeCallbacks(autoSlideRunnable);
                if (autoSlideRunnable != null) autoSlideHandler.postDelayed(autoSlideRunnable, 4000);
            }
        });
    }

    private void updateIndicators(int position) {
        for (int i = 0; i < indicators.size(); i++) {
            View indicator = indicators.get(i);
            if (indicator != null) {
                int color = (i == position) ? R.color.accent_dark : R.color.border_divider;
                indicator.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), color)));
            }
        }
    }

    private void setupAutoSlide(int size) {
        if (size <= 1) return;
        autoSlideRunnable = () -> {
            if (vpCategoryBanner != null) {
                vpCategoryBanner.setCurrentItem(vpCategoryBanner.getCurrentItem() + 1, true);
                autoSlideHandler.postDelayed(autoSlideRunnable, 4000);
            }
        };
        autoSlideHandler.postDelayed(autoSlideRunnable, 4000);
    }

    private void loadRootCategories(View root) {
        categoryRepository.getRootCategories().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            switch (result.status) {
                case SUCCESS:
                    if (result.data != null) {
                        bindRootCategoryCards(root, result.data);
                    }
                    break;

                case ERROR:
                    Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                    break;

                case EMPTY:
                    break;
            }
        });

        // AR and New/Hot if not in root categories
        setupCategoryCard(root.findViewById(R.id.cardCategoryAR), R.string.category_ar, R.drawable.ic_ar, R.drawable.img_ar, 
            ProductListingFragment.newCollectionInstance("ar_try_on", "Sản phẩm hỗ trợ AR"));
    }

    private void bindRootCategoryCards(View root, List<CategoryDto> categories) {
        for (CategoryDto category : categories) {
            if (category == null || category.getCategoryCode() == null) continue;

            Integer cardId = categoryCardMap.get(category.getCategoryCode());
            if (cardId == null) continue;

            View card = root.findViewById(cardId);
            bindCategoryCard(card, category);
        }
    }

    private void bindCategoryCard(View card, CategoryDto category) {
        if (card == null || category == null) return;

        TextView tvName = card.findViewById(R.id.tvCategoryName);
        ImageView ivIcon = card.findViewById(R.id.ivCategoryIcon);
        ImageView ivProduct = card.findViewById(R.id.ivCategoryProductImage);

        if (tvName != null) {
            tvName.setText(category.getName());
        }

        Integer imageRes = categoryImageMap.get(category.getCategoryCode());
        if (imageRes != null && ivProduct != null) {
            ivProduct.setImageResource(imageRes);
        }

        Integer iconRes = categoryIconMap.get(category.getCategoryCode());
        if (iconRes != null && ivIcon != null) {
            ivIcon.setImageResource(iconRes);
        }

        card.setOnClickListener(v -> {
            ui.common.FragmentNavigationHelper.replaceFragment(getActivity(), 
                ProductListingFragment.newCategoryInstance(category.getId(), category.getName()));
        });
    }

    private void setupCategoryCard(View card, int titleRes, int iconRes, int imgRes, Fragment destination) {
        if (card == null) return;
        TextView tvName = card.findViewById(R.id.tvCategoryName);
        ImageView ivIcon = card.findViewById(R.id.ivCategoryIcon);
        ImageView ivProduct = card.findViewById(R.id.ivCategoryProductImage);

        if (tvName != null) tvName.setText(titleRes);
        if (ivIcon != null) ivIcon.setImageResource(iconRes);
        if (ivProduct != null) ivProduct.setImageResource(imgRes);

        card.setOnClickListener(v -> {
            if (destination != null) {
                ui.common.FragmentNavigationHelper.replaceFragment(getActivity(), destination);
            } else {
                Toast.makeText(getContext(), getString(titleRes), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSpecialCollectionCards(View root) {
        View hotCard = root.findViewById(R.id.cardCategoryHot);
        View newCard = root.findViewById(R.id.cardCategoryNew);

        if (hotCard != null) {
            setupCategoryCard(hotCard, R.string.category_hot, R.drawable.ic_hot, R.drawable.img_hot, null);
            hotCard.setOnClickListener(v -> {
                ui.common.FragmentNavigationHelper.replaceFragment(getActivity(),
                        ProductListingFragment.newCollectionInstance("hot", getString(R.string.category_hot)));
            });
        }

        if (newCard != null) {
            setupCategoryCard(newCard, R.string.category_new_arrival, R.drawable.ic_new, R.drawable.img_new, null);
            newCard.setOnClickListener(v -> {
                ui.common.FragmentNavigationHelper.replaceFragment(getActivity(),
                        ProductListingFragment.newCollectionInstance("new-arrival", getString(R.string.category_new_arrival)));
            });
        }
    }

    private void loadFeaturedBrands(View root) {
        catalogRepository.getBrands().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            switch (result.status) {
                case LOADING:
                    break;

                case SUCCESS:
                    if (result.data != null) {
                        bindFeaturedBrands(root, result.data);
                    } else {
                        hideFeaturedBrandCards(root);
                    }
                    break;

                case EMPTY:
                    hideFeaturedBrandCards(root);
                    break;

                case ERROR:
                case NO_INTERNET:
                    Toast.makeText(
                        getContext(),
                        result.message != null ? result.message : "Không tải được thương hiệu",
                        Toast.LENGTH_SHORT
                    ).show();
                    hideFeaturedBrandCards(root);
                    break;
            }
        });
    }

    private void bindFeaturedBrands(View root, List<Brand> brands) {
        int[] cardIds = {
            R.id.cardBrandMaybelline,
            R.id.cardBrandHuda,
            R.id.cardBrandFwee,
            R.id.cardBrandJudydoll,
            R.id.cardBrandAnastasia
        };

        for (int i = 0; i < cardIds.length; i++) {
            View card = root.findViewById(cardIds[i]);

            if (card == null) continue;

            if (brands != null && i < brands.size() && brands.get(i) != null) {
                card.setVisibility(View.VISIBLE);
                bindFeaturedBrandCard(card, brands.get(i));
            } else {
                card.setVisibility(View.GONE);
            }
        }
    }

    private void hideFeaturedBrandCards(View root) {
        int[] cardIds = {
            R.id.cardBrandMaybelline,
            R.id.cardBrandHuda,
            R.id.cardBrandFwee,
            R.id.cardBrandJudydoll,
            R.id.cardBrandAnastasia
        };

        for (int cardId : cardIds) {
            View card = root.findViewById(cardId);
            if (card != null) card.setVisibility(View.GONE);
        }
    }

    private void bindFeaturedBrandCard(View card, Brand brand) {
        if (card == null || brand == null) return;

        Log.d("ProductCategory", "Featured brand = " + brand.getBrandName() + ", id = " + brand.getId());

        ImageView ivLogo = card.findViewById(R.id.ivBrandLogo);
        TextView tvName = card.findViewById(R.id.tvBrandName);

        if (tvName != null) {
            tvName.setText(brand.getBrandName());
        }

        if (ivLogo != null) {
            if (brand.getLogoUrl() != null && !brand.getLogoUrl().trim().isEmpty()) {
                Glide.with(ivLogo.getContext())
                    .load(brand.getLogoUrl())
                    .placeholder(R.drawable.bg_circle)
                    .error(R.drawable.bg_circle)
                    .into(ivLogo);
            } else {
                ivLogo.setImageResource(R.drawable.bg_circle);
            }
        }

        card.setOnClickListener(v -> {
            if (brand.getId() == null || brand.getId().trim().isEmpty()) {
                Toast.makeText(getContext(), "Không tìm thấy ID thương hiệu", Toast.LENGTH_SHORT).show();
                return;
            }

            ui.common.FragmentNavigationHelper.replaceFragment(
                getActivity(),
                ProductListingFragment.newBrandInstance(brand.getId(), brand.getBrandName())
            );
        });
    }

    private void setupFlashSaleProducts(View root) {
        rvFlashSaleProducts = root.findViewById(R.id.rvFlashSaleProducts);
        if (rvFlashSaleProducts == null) return;

        flashSaleAdapter = new FlashSaleProductAdapter();
        flashSaleAdapter.setOnFlashSaleProductClickListener(new FlashSaleProductAdapter.OnFlashSaleProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                if (product == null || product.getId() == null) return;

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

        rvFlashSaleProducts.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvFlashSaleProducts.setAdapter(flashSaleAdapter);
    }

    private void setupFlashSaleCountdown(View root) {
        tvFlashHour = root.findViewById(R.id.tvFlashHour);
        tvFlashMinute = root.findViewById(R.id.tvFlashMinute);
        tvFlashSecond = root.findViewById(R.id.tvFlashSecond);

        if (tvFlashHour == null || tvFlashMinute == null || tvFlashSecond == null) return;

        countdownRunnable = new Runnable() {
            @Override
            public void run() {
                if (remainingTimeMillis > 0) {
                    remainingTimeMillis -= 1000;
                    updateCountdownUI();
                    countdownHandler.postDelayed(this, 1000);
                }
            }
        };
        countdownHandler.post(countdownRunnable);
    }

    private void updateCountdownUI() {
        long hours = (remainingTimeMillis / (1000 * 60 * 60)) % 24;
        long minutes = (remainingTimeMillis / (1000 * 60)) % 60;
        long seconds = (remainingTimeMillis / 1000) % 60;

        if (tvFlashHour != null) tvFlashHour.setText(String.format("%02d", hours));
        if (tvFlashMinute != null) tvFlashMinute.setText(String.format("%02d", minutes));
        if (tvFlashSecond != null) tvFlashSecond.setText(String.format("%02d", seconds));
    }

    private void loadFlashSaleProducts() {
        Map<String, String> query = new HashMap<>();
        query.put("page", "1");
        query.put("limit", "10");
        query.put("fields", "card");
        query.put("saleOnly", "true");
        query.put("sort", "hot_deal");

        Log.d("ProductCategory", "Flash sale query = " + query.toString());

        productRepository.getProductsByQuery(query)
                .observe(getViewLifecycleOwner(), result -> {
                    if (result == null) return;

                    switch (result.status) {
                        case SUCCESS:
                            if (result.data != null) {
                                flashSaleAdapter.submitList(result.data);
                            } else {
                                flashSaleAdapter.submitList(new ArrayList<>());
                            }
                            break;

                        case EMPTY:
                            flashSaleAdapter.submitList(new ArrayList<>());
                            break;

                        case ERROR:
                        case NO_INTERNET:
                            Toast.makeText(
                                    getContext(),
                                    result.message != null ? result.message : "Không tải được Flash Sale",
                                    Toast.LENGTH_SHORT
                            ).show();
                            flashSaleAdapter.submitList(new ArrayList<>());
                            break;
                    }
                });
    }

    private void handleAddToCart(Product product) {
        if (product == null || product.getId() == null) return;

        // Check if user is logged in
        if (!TokenManager.getInstance(requireContext()).isLoggedIn()) {
            GuestPromptBottomSheet.newInstance(PendingAuthAction.ActionType.ADD_TO_CART)
                    .show(getChildFragmentManager(), "GuestPromptBottomSheet");
            return;
        }

        // Fetch variants first, then show bottom sheet (like in ProductDetailFragment)
        MutableLiveData<NetworkResult<List<ProductVariantDto>>> variantsResult = new MutableLiveData<>();
        productRepository.getProductVariants(product.getId(), variantsResult);

        variantsResult.observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case SUCCESS:
                    if (result.data != null) {
                        showVariantSelector(product, result.data);
                    } else {
                        // Fallback if no variants
                        performDirectAddToCart(product.getId(), null, 1);
                    }
                    variantsResult.removeObservers(getViewLifecycleOwner());
                    break;
                case ERROR:
                    Toast.makeText(requireContext(), "Không thể tải phân loại sản phẩm", Toast.LENGTH_SHORT).show();
                    variantsResult.removeObservers(getViewLifecycleOwner());
                    break;
                case LOADING:
                    break;
            }
        });
    }

    private void showVariantSelector(Product product, List<ProductVariantDto> variants) {
        VariantSelectorBottomSheet bottomSheet = VariantSelectorBottomSheet.newInstance(
                product, variants, VariantSelectorBottomSheet.ActionMode.ADD_TO_CART);

        bottomSheet.setListener((variant, mode, quantity) -> {
            String variantId = variant != null ? variant.getId() : null;
            performDirectAddToCart(product.getId(), variantId, quantity);
        });
        bottomSheet.show(getChildFragmentManager(), "VariantSelector");
    }

    private void performDirectAddToCart(String productId, String variantId, int quantity) {
        AddToCartRequest request = new AddToCartRequest(productId, variantId, quantity);
        cartViewModel.addToCart(request);

        // One-time observer for result
        cartViewModel.getCartResult().observe(getViewLifecycleOwner(), new androidx.lifecycle.Observer<NetworkResult<com.example.frontend.data.model.cart.CartDto>>() {
            @Override
            public void onChanged(NetworkResult<com.example.frontend.data.model.cart.CartDto> result) {
                if (result == null || result.status == NetworkResult.Status.LOADING) return;
                
                if (result.status == NetworkResult.Status.SUCCESS) {
                    Toast.makeText(requireContext(), "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                    cartViewModel.loadCart(); // Refresh cart badge
                } else if (result.status == NetworkResult.Status.ERROR) {
                    Toast.makeText(requireContext(), result.message != null ? result.message : "Lỗi thêm giỏ hàng", Toast.LENGTH_SHORT).show();
                }
                cartViewModel.getCartResult().removeObserver(this);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        autoSlideHandler.removeCallbacks(autoSlideRunnable);
        countdownHandler.removeCallbacks(countdownRunnable);
    }
}
