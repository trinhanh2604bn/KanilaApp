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
    private List<Product> fullProductList = new ArrayList<>();
    private List<Product> currentFilteredList = new ArrayList<>();
    private View containerSearchNoResult;
    private LinearLayout layoutFaceFilterChips;
    private TextView selectedChipView;

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
        setupFilterChips(view);
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

    private void setupFilterChips(View root) {
        if (layoutFaceFilterChips == null) return;
        
        // Initial selection: Foundation
        selectedChipView = root.findViewById(R.id.chipFoundation);
        
        setupChip(root.findViewById(R.id.chipFoundation), "Foundation");
        setupChip(root.findViewById(R.id.chipConcealer), "Concealer");
        setupChip(root.findViewById(R.id.chipPrimer), "Primer");
        setupChip(root.findViewById(R.id.chipPowder), "Powder");
        setupChip(root.findViewById(R.id.chipSettingSpray), "Setting Spray");
        setupChip(root.findViewById(R.id.chipBbCcCream), "BB & CC Cream");
        setupChip(root.findViewById(R.id.chipTintedMoisturizer), "Tinted Moisturizer");
    }

    private void setupChip(TextView chip, String subcategory) {
        if (chip == null) return;
        chip.setOnClickListener(v -> {
            updateChipSelection(chip);
            filterProductsBySubcategory(subcategory);
        });
    }

    private void updateChipSelection(TextView newSelected) {
        if (selectedChipView != null) {
            selectedChipView.setBackgroundResource(R.drawable.bg_chip_outline);
            selectedChipView.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary));
        }
        
        selectedChipView = newSelected;
        selectedChipView.setBackgroundResource(R.drawable.bg_chip_selected);
        selectedChipView.setTextColor(ContextCompat.getColor(requireContext(), R.color.background_main));
    }

    private void setupProductList() {
        adapter = new ProductAdapter();
        rvFaceProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvFaceProducts.setAdapter(adapter);

        loadMockData();
        // Initial filter by first subcategory
        filterProductsBySubcategory("Foundation");
    }

    private void loadMockData() {
        fullProductList.clear();
        
        // Foundation
        fullProductList.add(new Product("f1", "BeautyBlender", "Bounce Liquid Foundation", "450000", "4.5", "1.2k", R.drawable.img_foudation, "New", "Foundation"));
        fullProductList.add(new Product("f2", "Maybelline", "Fit Me Matte + Poreless", "250000", "4.8", "5.1k", R.drawable.img_brand_1, "Best Seller", "Foundation"));
        
        // Powder
        fullProductList.add(new Product("f3", "BeautyBlender", "Phấn phủ BOUNCE Soft Focus", "450000", "4.2", "800", R.drawable.img_foudation, "", "Powder"));
        fullProductList.add(new Product("f4", "Huda Beauty", "Easy Bake Loose Powder", "950000", "4.9", "12k", R.drawable.img_brand_2, "Hot", "Powder"));
        
        // Concealer
        fullProductList.add(new Product("f5", "Nars", "Radiant Creamy Concealer", "850000", "4.8", "3.2k", R.drawable.brand_nars, "Essential", "Concealer"));
        
        // Primer
        fullProductList.add(new Product("f6", "Benefit", "The POREfessional Face Primer", "750000", "4.7", "2.1k", R.drawable.img_brand_3, "", "Primer"));
    }

    private void filterProductsBySubcategory(String subcategory) {
        currentFilteredList.clear();
        for (Product product : fullProductList) {
            if (product.getSubcategory().equalsIgnoreCase(subcategory)) {
                currentFilteredList.add(product);
            }
        }
        
        adapter.setProducts(new ArrayList<>(currentFilteredList));
        showNoResult(currentFilteredList.isEmpty());
    }

    private void setupActions(View root) {
        View layoutFilter = root.findViewById(R.id.layoutFilterAction);
        if (layoutFilter != null) {
            layoutFilter.setOnClickListener(v -> Toast.makeText(getContext(), "Mở bộ lọc", Toast.LENGTH_SHORT).show());
        }

        View layoutSort = root.findViewById(R.id.layoutSortAction);
        if (layoutSort != null) {
            layoutSort.setOnClickListener(v -> Toast.makeText(getContext(), "Mở sắp xếp", Toast.LENGTH_SHORT).show());
        }
    }

    private void showNoResult(boolean show) {
        rvFaceProducts.setVisibility(show ? View.GONE : View.VISIBLE);
        containerSearchNoResult.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
