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
import com.example.frontend.data.repository.ProductRepository;
import com.example.frontend.feature.product.ProductDetailFragment;
import com.example.frontend.feature.search.SearchActivity;
import com.example.frontend.model.Product;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ui.common.BottomNavigationHelper;

public class ProductListingFragment extends Fragment {

    private static final String TAG = "ProductListing";
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
    private List<Product> baseProducts = new ArrayList<>();
    private View containerSearchNoResult;
    private LinearLayout layoutCategoryFilterChips;
    private View hsvCategoryFilterChips;
    private View layoutLoading;
    private ProductRepository productRepository;
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
        productRepository = new ProductRepository(requireContext());

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
                    filterAndShowProducts();
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
        adapter.setOnProductClickListener(product -> {
            String productId = product.getId();
            Log.d(TAG, "Clicked product id = " + productId);
            if (productId != null && productId.matches("^[a-fA-F0-9]{24}$")) {
                ProductDetailFragment fragment = ProductDetailFragment.newInstance(productId);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.main, fragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast.makeText(getContext(), "Product ID không hợp lệ, không thể mở chi tiết sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
        rvCategoryProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvCategoryProducts.setAdapter(adapter);

        loadRealData();
    }

    private void loadRealData() {
        if (layoutLoading != null) layoutLoading.setVisibility(View.VISIBLE);
        
        productRepository.getProducts(null, null, null).observe(getViewLifecycleOwner(), result -> {
            if (layoutLoading != null) layoutLoading.setVisibility(View.GONE);
            if (result == null) return;

            switch (result.status) {
                case SUCCESS:
                    baseProducts = result.data;
                    filterAndShowProducts();
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

    private void filterAndShowProducts() {
        if (baseProducts == null) return;
        
        List<Product> filtered = new ArrayList<>();
        for (Product p : baseProducts) {
            if (TYPE_BRAND.equals(listingType)) {
                if (brandName != null && brandName.equalsIgnoreCase(p.getBrand())) {
                    filtered.add(p);
                }
            } else {
                // If it's category listing, we show all products from the list-all query for now
                // since we don't have categoryId to query the backend properly here.
                filtered.add(p);
            }
        }
        showProducts(filtered);
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
