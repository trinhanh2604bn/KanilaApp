package ui.category;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.frontend.feature.cart.CartViewModel;
import com.example.frontend.data.model.cart.AddToCartRequest;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.feature.search.SearchActivity;
import com.example.frontend.model.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.lifecycle.ViewModelProvider;
import ui.common.BottomNavigationHelper;

public class ProductListingFragment extends Fragment {

    private static final String ARG_LISTING_TYPE = "listingType";
    private static final String ARG_CATEGORY_NAME = "categoryName";
    private static final String ARG_BRAND_NAME = "brandName";

    public static final String TYPE_CATEGORY = "CATEGORY";
    public static final String TYPE_BRAND = "BRAND";

    private String listingType;
    private String categoryName;
    private String brandName;

    private RecyclerView rvCategoryProducts;
    private ProductAdapter adapter;
    private CartViewModel cartViewModel;
    private List<Product> baseProducts = new ArrayList<>();
    private View containerSearchNoResult;
    private LinearLayout layoutCategoryFilterChips;
    private View hsvCategoryFilterChips;
    private ArrangeBottomSheetDialog.SortOption currentSortOption = ArrangeBottomSheetDialog.SortOption.BEST_MATCH;

    public static ProductListingFragment newCategoryInstance(String categoryName) {
        ProductListingFragment fragment = new ProductListingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LISTING_TYPE, TYPE_CATEGORY);
        args.putString(ARG_CATEGORY_NAME, categoryName);
        fragment.setArguments(args);
        return fragment;
    }

    public static ProductListingFragment newBrandInstance(String brandName) {
        ProductListingFragment fragment = new ProductListingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LISTING_TYPE, TYPE_BRAND);
        args.putString(ARG_BRAND_NAME, brandName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            listingType = getArguments().getString(ARG_LISTING_TYPE);
            categoryName = getArguments().getString(ARG_CATEGORY_NAME);
            brandName = getArguments().getString(ARG_BRAND_NAME);
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

        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

        initViews(view);
        setupHeader();
        setupProductList();
        setupModeLogic();
        setupActions(view);

        BottomNavigationHelper.setup(view, tabIndex -> {
            // Navigation handled by helper
        });
        BottomNavigationHelper.setSelectedTab(view, BottomNavigationHelper.TAB_CATEGORY);
    }

    private void initViews(View root) {
        rvCategoryProducts = root.findViewById(R.id.rvCategoryProducts);
        containerSearchNoResult = root.findViewById(R.id.containerSearchNoResult);
        layoutCategoryFilterChips = root.findViewById(R.id.layoutCategoryFilterChips);
        hsvCategoryFilterChips = root.findViewById(R.id.hsvCategoryFilterChips);

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
        String title = TYPE_CATEGORY.equals(listingType) ? categoryName : brandName;
        View searchBar = getView().findViewById(R.id.layoutCategorySearchBar);
        if (searchBar != null) {
            TextView tvHint = searchBar.findViewById(R.id.tvSearchHint);
            if (tvHint != null) tvHint.setText(title);

            searchBar.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), SearchActivity.class);
                intent.putExtra("initial_query", title);
                intent.putExtra("category", categoryName);
                intent.putExtra("brand", brandName);
                intent.putExtra("listing_type", listingType);
                startActivity(intent);
            });
        }
    }

    private void setupModeLogic() {
        if (TYPE_CATEGORY.equals(listingType)) {
            hsvCategoryFilterChips.setVisibility(View.VISIBLE);
            setupCategoryChips(categoryName);
        } else {
            hsvCategoryFilterChips.setVisibility(View.GONE);
        }
    }

    private void setupCategoryChips(String categoryName) {
        List<String> chips = getChipsForCategory(categoryName);
        layoutCategoryFilterChips.removeAllViews();

        for (int i = 0; i < chips.size(); i++) {
            String chipText = chips.get(i);
            TextView chip = createChipView(chipText, i == 0);
            
            chip.setOnClickListener(v -> {
                selectChip(chip);
                if ("Tất cả".equals(chipText)) {
                    showProducts(baseProducts);
                } else {
                    filterBySubcategory(chipText);
                }
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
                // Navigate to detail
            }

            @Override
            public void onAddToCartClick(Product product) {
                handleAddToCart(product);
            }
        });

        rvCategoryProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvCategoryProducts.setAdapter(adapter);

        loadMockData();
        showProducts(baseProducts);
    }

    private void handleAddToCart(Product product) {
        if (product.getId() == null) return;

        // Use empty string instead of null for variant_id as some backends require it
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

    private void loadMockData() {
        baseProducts.clear();
        // This is a mock implementation. In real app, this would call a repository.
        List<Product> allMockProducts = getAllMockProducts();
        
        for (Product p : allMockProducts) {
            if (TYPE_CATEGORY.equals(listingType)) {
                // For simplicity, we assume some products match the category
                // In real mock, we would check p.getCategory()
                if (categoryName.equals("Face") && p.getId().startsWith("f")) baseProducts.add(p);
                else if (categoryName.equals("Eyes") && p.getId().startsWith("e")) baseProducts.add(p);
                else if (categoryName.equals("Lips") && p.getId().startsWith("l")) baseProducts.add(p);
                else if (categoryName.equals("Cheeks") && p.getId().startsWith("c")) baseProducts.add(p);
                else if (categoryName.equals("Mini & Travel") && p.getId().startsWith("mt")) baseProducts.add(p);
                else if (categoryName.equals("Gift") && p.getId().startsWith("g")) baseProducts.add(p);
            } else if (TYPE_BRAND.equals(listingType)) {
                if (brandName.equalsIgnoreCase(p.getBrand())) {
                    baseProducts.add(p);
                }
            }
        }
    }

    private List<Product> getAllMockProducts() {
        List<Product> list = new ArrayList<>();
        // Face
        list.add(new Product("f1", "BeautyBlender", "Bounce Liquid Foundation", "450000", "4.5", "1.2k", R.drawable.img_foudation, "New", "Foundation"));
        list.add(new Product("f2", "Maybelline", "Fit Me Matte + Poreless", "250000", "4.8", "5.1k", R.drawable.img_brand_1, "Best Seller", "Foundation"));
        list.add(new Product("f3", "BeautyBlender", "Phấn phủ BOUNCE Soft Focus", "450000", "4.2", "800", R.drawable.img_foudation, "", "Powder"));
        list.add(new Product("f4", "Huda Beauty", "Easy Bake Loose Powder", "950000", "4.9", "12k", R.drawable.img_brand_2, "Hot", "Powder"));
        list.add(new Product("f5", "Nars", "Radiant Creamy Concealer", "850000", "4.8", "3.2k", R.drawable.brand_nars, "Essential", "Concealer"));
        
        // Eyes
        list.add(new Product("e1", "L'Oreal", "Voluminous Lash Paradise", "350000", "4.7", "10k", R.drawable.ic_product, "Best Seller", "Mascara"));
        list.add(new Product("e2", "NYX", "Epic Ink Liner", "200000", "4.6", "8k", R.drawable.ic_product, "Hot", "Eyeliner"));
        
        // Lips
        list.add(new Product("l1", "MAC", "Matte Lipstick", "480000", "4.8", "25k", R.drawable.img_lipstick, "Classic", "Lipstick"));
        list.add(new Product("l2", "Fwee", "Lip Suede", "350000", "4.9", "1k", R.drawable.img_lipstick, "New", "Lipstick"));
        
        // Brands specific
        list.add(new Product("fwee1", "Fwee", "Blurry Pudding Pot", "420000", "4.9", "2k", R.drawable.img_brand_1, "Hot", "Blush"));
        list.add(new Product("nars1", "Nars", "Light Reflecting Foundation", "1200000", "4.8", "5k", R.drawable.brand_nars, "Premium", "Foundation"));

        // Mini & Travel
        list.add(new Product("mt1", "Laneige", "Mini Foundation Tint", "320000", "4.7", "500", R.drawable.img_foudation, "Mini", "Mini Foundation"));
        list.add(new Product("mt2", "Innisfree", "Travel Makeup Kit", "550000", "4.5", "300", R.drawable.img_gift, "Kit", "Trial Kits"));

        // Gift
        list.add(new Product("g1", "Judydoll", "All-in-one Face Palette", "450000", "4.9", "1.5k", R.drawable.img_blush, "Palette", "Face Palette"));
        list.add(new Product("g2", "Huda Beauty", "Naughty Nude Eyeshadow", "1650000", "4.9", "2k", R.drawable.img_eyeshadow, "Premium", "Eyeshadow Palette"));

        for (Product p : list) p.setHasAr(true);
        return list;
    }

    private void filterBySubcategory(String subcategory) {
        List<Product> filtered = new ArrayList<>();
        for (Product product : baseProducts) {
            if (subcategory.equalsIgnoreCase(product.getSubcategory())) {
                filtered.add(product);
            }
        }
        showProducts(filtered);
    }

    private List<String> getChipsForCategory(String category) {
        String all = getString(R.string.chip_all);
        switch (category != null ? category : "") {
            case "Face":
                return Arrays.asList(all, getString(R.string.chip_foundation), getString(R.string.chip_concealer),
                        getString(R.string.chip_primer), getString(R.string.chip_powder), getString(R.string.chip_setting_spray),
                        getString(R.string.chip_bb_cc_cream), getString(R.string.chip_tinted_moisturizer));
            case "Eyes":
                return Arrays.asList(all, "Mascara", "Eyeliner", "Eyeshadow", "Eyebrow", "False Lashes");
            case "Lips":
                return Arrays.asList(all, "Lipstick", "Lip Gloss", "Lip Balm", "Lip Liner", "Lip Stain");
            case "Cheeks":
                return Arrays.asList(all, "Blush", "Bronzer", "Highlighter", "Contour");
            case "Mini & Travel":
                return Arrays.asList(all, getString(R.string.chip_mini_foundation), getString(R.string.chip_mini_lipstick), getString(R.string.chip_trial_kits));
            case "Gift":
                return Arrays.asList(all, getString(R.string.chip_eyeshadow_palette), getString(R.string.chip_face_palette), getString(R.string.chip_makeup_kit));
            default:
                return Arrays.asList(all);
        }
    }

    private void setupActions(View root) {
        root.findViewById(R.id.layoutFilterAction).setOnClickListener(v -> {
            FilterBottomSheetDialog dialog = new FilterBottomSheetDialog();
            dialog.show(getChildFragmentManager(), "FilterBottomSheet");
        });

        root.findViewById(R.id.layoutSortAction).setOnClickListener(v -> {
            ArrangeBottomSheetDialog dialog = new ArrangeBottomSheetDialog();
            dialog.setSelectedOption(currentSortOption);
            dialog.setOnSortOptionSelectedListener(option -> {
                currentSortOption = option;
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
