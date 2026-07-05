package ui.category;

import android.content.Intent;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.example.frontend.feature.search.SearchActivity;
import com.example.frontend.model.Product;
import java.util.ArrayList;
import java.util.List;
import ui.common.BottomNavigationHelper;

public class FaceFragment extends Fragment {

    private RecyclerView rvFaceProducts;
    private ProductAdapter adapter;
    private List<Product> allFaceProducts = new ArrayList<>();
    private View containerSearchNoResult;
    private LinearLayout layoutFaceFilterChips;
    private TextView selectedChipView;

    private TextView chipAllFace;
    private TextView chipFoundation;
    private TextView chipConcealer;
    private TextView chipPrimer;
    private TextView chipPowder;
    private TextView chipSettingSpray;
    private TextView chipBbCcCream;
    private TextView chipTintedMoisturizer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_face, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupSearch(view);
        setupFilterChips();
        setupProductList();
        setupActions(view);

        BottomNavigationHelper.setup(view, tabIndex -> {
            // Handle bottom nav
        });
        BottomNavigationHelper.setSelectedTab(view, BottomNavigationHelper.TAB_CATEGORY);
    }

    private void initViews(View root) {
        rvFaceProducts = root.findViewById(R.id.rvFaceProducts);
        containerSearchNoResult = root.findViewById(R.id.containerSearchNoResult);
        layoutFaceFilterChips = root.findViewById(R.id.layoutFaceFilterChips);

        chipAllFace = root.findViewById(R.id.chipAllFace);
        chipFoundation = root.findViewById(R.id.chipFoundation);
        chipConcealer = root.findViewById(R.id.chipConcealer);
        chipPrimer = root.findViewById(R.id.chipPrimer);
        chipPowder = root.findViewById(R.id.chipPowder);
        chipSettingSpray = root.findViewById(R.id.chipSettingSpray);
        chipBbCcCream = root.findViewById(R.id.chipBbCcCream);
        chipTintedMoisturizer = root.findViewById(R.id.chipTintedMoisturizer);
        
        ImageButton btnBack = root.findViewById(R.id.btnFaceBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }
    }

    private void setupSearch(View root) {
        View searchBar = root.findViewById(R.id.layoutFaceSearchBar);
        if (searchBar != null) {
            TextView tvHint = searchBar.findViewById(R.id.tvSearchHint);
            if (tvHint != null) tvHint.setText(R.string.category_face);
            
            searchBar.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), SearchActivity.class);
                intent.putExtra("initial_query", "Face");
                intent.putExtra("category", "Face");
                startActivity(intent);
            });
        }
    }

    private void setupFilterChips() {
        if (layoutFaceFilterChips == null) return;
        
        chipAllFace.setOnClickListener(v -> {
            selectChip(chipAllFace);
            showProducts(allFaceProducts);
        });

        setupChip(chipFoundation, "Foundation");
        setupChip(chipConcealer, "Concealer");
        setupChip(chipPrimer, "Primer");
        setupChip(chipPowder, "Powder");
        setupChip(chipSettingSpray, "Setting Spray");
        setupChip(chipBbCcCream, "BB & CC Cream");
        setupChip(chipTintedMoisturizer, "Tinted Moisturizer");
    }

    private void setupChip(TextView chip, String subcategory) {
        if (chip == null) return;
        chip.setOnClickListener(v -> filterBySubcategory(chip, subcategory));
    }

    private void filterBySubcategory(TextView selectedChip, String subcategory) {
        selectChip(selectedChip);

        List<Product> filtered = new ArrayList<>();
        for (Product product : allFaceProducts) {
            if (subcategory.equalsIgnoreCase(product.getSubcategory())) {
                filtered.add(product);
            }
        }

        showProducts(filtered);
    }

    private void selectChip(TextView selectedChip) {
        resetAllFaceChips();

        selectedChip.setSelected(true);
        selectedChip.setBackgroundResource(R.drawable.bg_chip_selected);
        selectedChip.setTextColor(ContextCompat.getColor(requireContext(), R.color.background_main));
        selectedChipView = selectedChip;
    }

    private void resetAllFaceChips() {
        TextView[] chips = {
            chipAllFace,
            chipFoundation,
            chipConcealer,
            chipPrimer,
            chipPowder,
            chipSettingSpray,
            chipBbCcCream,
            chipTintedMoisturizer
        };

        for (TextView chip : chips) {
            if (chip == null) continue;
            chip.setSelected(false);
            chip.setBackgroundResource(R.drawable.bg_chip_outline);
            chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary));
        }
    }

    private void setupProductList() {
        adapter = new ProductAdapter();
        rvFaceProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvFaceProducts.setAdapter(adapter);

        loadMockData();
        
        // Initial state: "Tất cả" selected and all products shown
        selectChip(chipAllFace);
        showProducts(allFaceProducts);
    }

    private void loadMockData() {
        allFaceProducts.clear();
        
        // Foundation
        Product p1 = new Product("f1", "BeautyBlender", "Bounce Liquid Foundation", "450000", "4.5", "1.2k", R.drawable.img_foudation, "New", "Foundation");
        p1.setHasAr(true);
        allFaceProducts.add(p1);

        Product p2 = new Product("f2", "Maybelline", "Fit Me Matte + Poreless", "250000", "4.8", "5.1k", R.drawable.img_brand_1, "Best Seller", "Foundation");
        p2.setHasAr(false);
        allFaceProducts.add(p2);
        
        // Powder
        Product p3 = new Product("f3", "BeautyBlender", "Phấn phủ BOUNCE Soft Focus", "450000", "4.2", "800", R.drawable.img_foudation, "", "Powder");
        p3.setHasAr(true);
        allFaceProducts.add(p3);

        Product p4 = new Product("f4", "Huda Beauty", "Easy Bake Loose Powder", "950000", "4.9", "12k", R.drawable.img_brand_2, "Hot", "Powder");
        p4.setHasAr(false);
        allFaceProducts.add(p4);
        
        // Concealer
        Product p5 = new Product("f5", "Nars", "Radiant Creamy Concealer", "850000", "4.8", "3.2k", R.drawable.brand_nars, "Essential", "Concealer");
        p5.setHasAr(true);
        allFaceProducts.add(p5);
        
        // Primer
        Product p6 = new Product("f6", "Benefit", "The POREfessional Face Primer", "750000", "4.7", "2.1k", R.drawable.img_brand_3, "", "Primer");
        p6.setHasAr(false);
        allFaceProducts.add(p6);

        // Setting Spray (Mock)
        Product p7 = new Product("f7", "MAC", "Prep + Prime Fix+", "650000", "4.6", "4.5k", R.drawable.ic_product, "", "Setting Spray");
        p7.setHasAr(false);
        allFaceProducts.add(p7);
    }

    private void setupActions(View root) {
        View layoutFilter = root.findViewById(R.id.layoutFilterAction);
        if (layoutFilter != null) {
            layoutFilter.setOnClickListener(v -> {
                FilterBottomSheetDialog dialog = new FilterBottomSheetDialog();
                dialog.setOnFilterAppliedListener(filterState -> {
                    // Logic lọc demo
                    Toast.makeText(getContext(), "Đã áp dụng bộ lọc", Toast.LENGTH_SHORT).show();
                    // In a real app, we would use filterState to filter the list
                });
                dialog.show(getChildFragmentManager(), "FilterBottomSheet");
            });
        }

        View layoutSort = root.findViewById(R.id.layoutSortAction);
        if (layoutSort != null) {
            layoutSort.setOnClickListener(v -> Toast.makeText(getContext(), "Mở sắp xếp", Toast.LENGTH_SHORT).show());
        }
    }

    private void showProducts(List<Product> products) {
        if (products == null || products.isEmpty()) {
            rvFaceProducts.setVisibility(View.GONE);
            containerSearchNoResult.setVisibility(View.VISIBLE);
            adapter.setProducts(new ArrayList<>());
            return;
        }

        containerSearchNoResult.setVisibility(View.GONE);
        rvFaceProducts.setVisibility(View.VISIBLE);
        adapter.setProducts(new ArrayList<>(products));
    }
}
