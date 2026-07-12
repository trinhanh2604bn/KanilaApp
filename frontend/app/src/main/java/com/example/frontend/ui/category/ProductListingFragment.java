package com.example.frontend.ui.category;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.data.model.category.CategoryDto;
import com.example.frontend.data.repository.CategoryRepository;
import com.example.frontend.data.repository.ProductRepository;
import com.example.frontend.feature.product.ProductDetailFragment;
import com.example.frontend.feature.cart.CartViewModel;
import com.example.frontend.data.model.cart.AddToCartRequest;
import com.example.frontend.data.model.product.ProductFilterParams;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.feature.search.SearchActivity;
import com.example.frontend.model.Product;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.lifecycle.ViewModelProvider;
import ui.common.BottomNavigationHelper;

public class ProductListingFragment extends Fragment {

    private static final String TAG = "ProductListingFrag";
    private static final String ARG_LISTING_TYPE = "listingType";
    private static final String ARG_CATEGORY_ID = "categoryId";
    private static final String ARG_CATEGORY_NAME = "categoryName";
    private static final String ARG_BRAND_ID = "brandId";
    private static final String ARG_BRAND_NAME = "brandName";
    private static final String ARG_COLLECTION_TYPE = "collectionType";
    private static final String ARG_COLLECTION_TITLE = "collectionTitle";

    public static final String TYPE_CATEGORY = "CATEGORY";
    public static final String TYPE_BRAND = "BRAND";
    public static final String TYPE_COLLECTION = "COLLECTION";

    private String listingType;
    private String categoryId;
    private String categoryName;
    private String brandId;
    private String brandName;
    private String collectionType;
    private String collectionTitle;

    private ProductFilterParams currentFilterParams = new ProductFilterParams();
    private String currentSelectedCategoryId;
    private boolean currentIncludeChildren = true;

    private RecyclerView rvCategoryProducts;
    private ProductAdapter adapter;
    private CartViewModel cartViewModel;
    private List<Product> baseProducts = new ArrayList<>();
    private View containerSearchNoResult;
    private LinearLayout layoutCategoryFilterChips;
    private View hsvCategoryFilterChips;
    private View layoutLoading;
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private ArrangeBottomSheetDialog.SortOption currentSortOption = ArrangeBottomSheetDialog.SortOption.BEST_MATCH;
    private boolean hasUserAppliedSort = false;

    public static ProductListingFragment newCategoryInstance(String categoryId, String categoryName) {
        ProductListingFragment fragment = new ProductListingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LISTING_TYPE, TYPE_CATEGORY);
        args.putString(ARG_CATEGORY_ID, categoryId);
        args.putString(ARG_CATEGORY_NAME, categoryName);
        fragment.setArguments(args);
        return fragment;
    }

    @Deprecated
    public static ProductListingFragment newCategoryInstance(String categoryName) {
        ProductListingFragment fragment = new ProductListingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LISTING_TYPE, TYPE_CATEGORY);
        args.putString(ARG_CATEGORY_NAME, categoryName);
        fragment.setArguments(args);
        return fragment;
    }

    public static ProductListingFragment newBrandInstance(String brandId, String brandName) {
        ProductListingFragment fragment = new ProductListingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LISTING_TYPE, TYPE_BRAND);
        args.putString(ARG_BRAND_ID, brandId);
        args.putString(ARG_BRAND_NAME, brandName);
        fragment.setArguments(args);
        return fragment;
    }

    @Deprecated
    public static ProductListingFragment newBrandInstance(String brandName) {
        ProductListingFragment fragment = new ProductListingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LISTING_TYPE, TYPE_BRAND);
        args.putString(ARG_BRAND_NAME, brandName);
        fragment.setArguments(args);
        return fragment;
    }

    public static ProductListingFragment newCollectionInstance(String collectionType, String collectionTitle) {
        ProductListingFragment fragment = new ProductListingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LISTING_TYPE, TYPE_COLLECTION);
        args.putString(ARG_COLLECTION_TYPE, collectionType);
        args.putString(ARG_COLLECTION_TITLE, collectionTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            listingType = getArguments().getString(ARG_LISTING_TYPE);
            categoryId = getArguments().getString(ARG_CATEGORY_ID);
            categoryName = getArguments().getString(ARG_CATEGORY_NAME);
            brandId = getArguments().getString(ARG_BRAND_ID);
            brandName = getArguments().getString(ARG_BRAND_NAME);
            collectionType = getArguments().getString(ARG_COLLECTION_TYPE);
            collectionTitle = getArguments().getString(ARG_COLLECTION_TITLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_category_products, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productRepository = new ProductRepository(requireContext());
        categoryRepository = new CategoryRepository(requireContext());
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

        initViews(view);
        setupHeader();
        setupProductList();
        setupModeLogic();
        setupActions(view);

        BottomNavigationHelper.setupStandardNavigation(this, view);
        BottomNavigationHelper.setSelectedTab(view, BottomNavigationHelper.TAB_CATEGORY);
    }

    private void initViews(View root) {
        rvCategoryProducts = root.findViewById(R.id.rvCategoryProducts);
        containerSearchNoResult = root.findViewById(R.id.containerSearchNoResult);
        layoutCategoryFilterChips = root.findViewById(R.id.layoutCategoryFilterChips);
        hsvCategoryFilterChips = root.findViewById(R.id.hsvCategoryFilterChips);
        layoutLoading = root.findViewById(R.id.layoutLoading);

        ImageButton btnBack = root.findViewById(R.id.btnCategoryBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }
    }

    private void setupHeader() {
        String title = "";
        if (TYPE_CATEGORY.equals(listingType)) title = categoryName;
        else if (TYPE_BRAND.equals(listingType)) title = brandName;
        else if (TYPE_COLLECTION.equals(listingType)) title = collectionTitle;

        final String finalTitle = title;

        View searchBar = getView().findViewById(R.id.layoutCategorySearchBar);
        if (searchBar != null) {
            TextView tvHint = searchBar.findViewById(R.id.tvSearchHint);
            if (tvHint != null) tvHint.setText(finalTitle);

            searchBar.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), SearchActivity.class);
                intent.putExtra("initial_query", finalTitle);
                intent.putExtra("category", categoryName);
                intent.putExtra("brand", brandName);
                intent.putExtra("listing_type", listingType);
                intent.putExtra("collection_type", collectionType);
                intent.putExtra("collection_title", collectionTitle);
                startActivity(intent);
            });
        }
    }

    private void setupModeLogic() {
        if (TYPE_CATEGORY.equals(listingType)) {
            hsvCategoryFilterChips.setVisibility(View.VISIBLE);
            currentSelectedCategoryId = categoryId;
            currentIncludeChildren = true;
            loadChildCategoryChips();
            loadProductsWithCurrentContext();
        } else if (TYPE_BRAND.equals(listingType)) {
            hsvCategoryFilterChips.setVisibility(View.GONE);
            loadProductsWithCurrentContext();
        } else if (TYPE_COLLECTION.equals(listingType)) {
            hsvCategoryFilterChips.setVisibility(View.GONE);
            loadProductsWithCurrentContext();
        } else {
            hsvCategoryFilterChips.setVisibility(View.GONE);
            loadRealData();
        }
    }

    private void loadProductsWithCurrentContext() {
        Map<String, String> query = buildCurrentProductQuery();
        Log.d(TAG, "Product query = " + query.toString());

        if (layoutLoading != null) layoutLoading.setVisibility(View.VISIBLE);

        productRepository.getProductsByQuery(query)
                .observe(getViewLifecycleOwner(), result -> {
                    if (layoutLoading != null) layoutLoading.setVisibility(View.GONE);

                    if (result == null) return;

                    switch (result.status) {
                        case SUCCESS:
                            baseProducts = result.data != null ? result.data : new ArrayList<>();
                            showProducts(baseProducts);
                            break;

                        case EMPTY:
                            baseProducts = new ArrayList<>();
                            showProducts(baseProducts);
                            break;

                        case ERROR:
                            Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                            showProducts(new ArrayList<>());
                            break;
                    }
                });
    }

    private Map<String, String> buildCurrentProductQuery() {
        Map<String, String> query = new HashMap<>();

        query.put("page", "1");
        query.put("limit", "50");
        query.put("fields", "card");

        if (TYPE_CATEGORY.equals(listingType)) {
            String selectedId = currentSelectedCategoryId;
            if (selectedId == null || selectedId.trim().isEmpty()) {
                selectedId = categoryId;
            }

            if (selectedId != null && !selectedId.trim().isEmpty()) {
                query.put("categoryId", selectedId);
                query.put("includeChildren", String.valueOf(currentIncludeChildren));
            }

        } else if (TYPE_BRAND.equals(listingType)) {
            if (brandId != null && !brandId.trim().isEmpty()) {
                query.put("brandId", brandId.trim());
            }

        } else if (TYPE_COLLECTION.equals(listingType)) {
            if (collectionType != null && !collectionType.trim().isEmpty()) {
                query.put("collection", collectionType);
            }
        }

        appendFilterParams(query, currentFilterParams);

        if (hasUserAppliedSort && currentSortOption != null) {
            query.put("sort", currentSortOption.getBackendSortKey());
        }

        Log.d(TAG, "Product query with context: " + query.toString());

        return query;
    }

    private void appendFilterParams(Map<String, String> query, ProductFilterParams params) {
        if (params == null) return;

        if (params.minPrice != null && !params.minPrice.trim().isEmpty()) {
            query.put("minPrice", params.minPrice.trim());
        }

        if (params.maxPrice != null && !params.maxPrice.trim().isEmpty()) {
            query.put("maxPrice", params.maxPrice.trim());
        }

        if (params.minRating != null && !params.minRating.trim().isEmpty()) {
            query.put("minRating", params.minRating.trim());
        }

        putCsv(query, "skinTypes", params.skinTypes);
        putCsv(query, "tones", params.tones);
        if (!query.containsKey("brandId")) {
            putCsv(query, "brandId", params.brandIds);
        }
        putCsv(query, "ingredients", params.ingredients);
        putCsv(query, "ingredientFlags", params.ingredientFlags);
        putCsv(query, "concerns", params.concerns);

        if (params.sensitiveOnly) {
            query.put("sensitiveOnly", "true");
        }

        if (params.bestSellerOnly) {
            query.put("bestSellerOnly", "true");
        }
    }

    private void putCsv(Map<String, String> query, String key, List<String> values) {
        if (values == null || values.isEmpty()) return;

        List<String> cleanValues = new ArrayList<>();
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                cleanValues.add(value.trim());
            }
        }

        if (!cleanValues.isEmpty()) {
            query.put(key, TextUtils.join(",", cleanValues));
        }
    }

    private void loadChildCategoryChips() {
        if (categoryId == null || categoryId.trim().isEmpty()) {
            createCategoryChips(new ArrayList<>());
            return;
        }

        categoryRepository.getChildCategories(categoryId)
                .observe(getViewLifecycleOwner(), result -> {
                    if (result == null) return;

                    switch (result.status) {
                        case SUCCESS:
                            createCategoryChips(result.data);
                            break;

                        case EMPTY:
                            createCategoryChips(new ArrayList<>());
                            break;

                        case ERROR:
                            Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                            createCategoryChips(new ArrayList<>());
                            break;
                    }
                });
    }

    private void createCategoryChips(List<CategoryDto> childCategories) {
        layoutCategoryFilterChips.removeAllViews();

        TextView allChip = createChipView(getString(R.string.chip_all), true);
        allChip.setTag(categoryId);

        allChip.setOnClickListener(v -> {
            selectChip(allChip);
            currentSelectedCategoryId = categoryId;
            currentIncludeChildren = true;
            loadProductsWithCurrentContext();
        });

        layoutCategoryFilterChips.addView(allChip);

        if (childCategories == null) return;

        for (CategoryDto category : childCategories) {
            if (category == null || category.getId() == null || category.getName() == null) {
                continue;
            }

            TextView chip = createChipView(category.getName(), false);
            chip.setTag(category.getId());

            chip.setOnClickListener(v -> {
                selectChip(chip);
                currentSelectedCategoryId = (String) chip.getTag();
                currentIncludeChildren = false;
                loadProductsWithCurrentContext();
            });

            layoutCategoryFilterChips.addView(chip);
        }
    }

    private TextView createChipView(String text, boolean isSelected) {
        TextView chip = new TextView(requireContext());
        chip.setText(text);
        chip.setGravity(Gravity.CENTER);
        chip.setClickable(true);
        chip.setFocusable(true);
        TextViewCompat.setTextAppearance(chip, R.style.Text_Kanila_Caption);
        
        int chipHeight = getResources().getDimensionPixelSize(R.dimen.chip_height);
        int paddingM = getResources().getDimensionPixelSize(R.dimen.spacing_m);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            chipHeight
        );
        params.setMarginStart(getResources().getDimensionPixelSize(R.dimen.spacing_s));

        chip.setLayoutParams(params);
        chip.setPadding(paddingM, 0, paddingM, 0);
        
        if (isSelected) {
            chip.setBackgroundResource(R.drawable.bg_chip_selected);
            chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.background_main));
        } else {
            chip.setBackgroundResource(R.drawable.bg_chip_outline);
            chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary));
        }
        
        return chip;
    }

    private void selectChip(TextView selectedChip) {
        for (int i = 0; i < layoutCategoryFilterChips.getChildCount(); i++) {
            View v = layoutCategoryFilterChips.getChildAt(i);
            if (v instanceof TextView) {
                TextView chip = (TextView) v;
                chip.setSelected(false);
                chip.setBackgroundResource(R.drawable.bg_chip_outline);
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary));
            }
        }
        selectedChip.setSelected(true);
        selectedChip.setBackgroundResource(R.drawable.bg_chip_selected);
        selectedChip.setTextColor(ContextCompat.getColor(requireContext(), R.color.background_main));
    }

    private void setupProductList() {
        adapter = new ProductAdapter();
        adapter.setOnProductClickListener(new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                String productId = product.getId();
                Log.d(TAG, "Clicked product id = " + productId);
                if (productId != null && productId.matches("^[a-fA-F0-9]{24}$")) {
                    ProductDetailFragment fragment = ProductDetailFragment.newInstance(productId);
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.main_fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    Toast.makeText(getContext(), "Product ID không hợp lệ, không thể mở chi tiết sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAddToCartClick(Product product) {
                handleAddToCart(product);
            }
        });

        rvCategoryProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvCategoryProducts.setAdapter(adapter);
    }

    private void handleAddToCart(Product product) {
        if (product.getId() == null) return;
        com.example.frontend.feature.product.QuickAddHelper.quickAddToCart(
            requireContext(), getChildFragmentManager(), getViewLifecycleOwner(), product, cartViewModel);
    }

    private void loadRealData() {
        String category = TYPE_CATEGORY.equals(listingType) ? categoryName.toLowerCase() : null;
        String brand = TYPE_BRAND.equals(listingType) ? brandName : null;

        if (layoutLoading != null) layoutLoading.setVisibility(View.VISIBLE);

        productRepository.getProducts(brand, category, null).observe(getViewLifecycleOwner(), result -> {
            if (layoutLoading != null) layoutLoading.setVisibility(View.GONE);
            if (result == null) return;

            switch (result.status) {
                case SUCCESS:
                    if (result.data != null) {
                        baseProducts = result.data;
                        showProducts(baseProducts);
                    }
                    break;
                case ERROR:
                    Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                    break;
                case EMPTY:
                    showProducts(new ArrayList<>());
                    break;
            }
        });
    }

    private void setupActions(View root) {
        cartViewModel.getCartResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            if (result.status == NetworkResult.Status.SUCCESS) {
                Toast.makeText(requireContext(), "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            } else if (result.status == NetworkResult.Status.ERROR) {
                Toast.makeText(requireContext(), result.message != null ? result.message : "Lỗi thêm giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });

        root.findViewById(R.id.layoutFilterAction).setOnClickListener(v -> {
            FilterBottomSheetDialog dialog = new FilterBottomSheetDialog();
            dialog.setInitialFilterParams(currentFilterParams);
            dialog.setOnFilterAppliedListener(filterParams -> {
                currentFilterParams = filterParams != null ? filterParams : new ProductFilterParams();
                loadProductsWithCurrentContext();
            });
            dialog.show(getChildFragmentManager(), "FilterBottomSheet");
        });

        root.findViewById(R.id.layoutSortAction).setOnClickListener(v -> {
            ArrangeBottomSheetDialog dialog = new ArrangeBottomSheetDialog();
            dialog.setSelectedOption(currentSortOption);
            dialog.setOnSortOptionSelectedListener(option -> {
                currentSortOption = option != null
                        ? option
                        : ArrangeBottomSheetDialog.SortOption.BEST_MATCH;
                hasUserAppliedSort = true;
                loadProductsWithCurrentContext();
                Toast.makeText(getContext(), "Đã áp dụng sắp xếp", Toast.LENGTH_SHORT).show();
            });
            dialog.show(getChildFragmentManager(), "ArrangeBottomSheet");
        });
    }

    private void showProducts(List<Product> products) {
        if (products == null || products.isEmpty()) {
            rvCategoryProducts.setVisibility(View.GONE);
            containerSearchNoResult.setVisibility(View.VISIBLE);
            adapter.setProducts(new ArrayList<>());
        } else {
            containerSearchNoResult.setVisibility(View.GONE);
            rvCategoryProducts.setVisibility(View.VISIBLE);
            adapter.setProducts(new ArrayList<>(products));
        }
    }
}
