package ui.category;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.frontend.R;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.model.Brand;
import com.example.frontend.model.HomeBannerItem;
import java.util.ArrayList;
import java.util.List;
import ui.common.BottomNavigationHelper;

public class BrandPageFragment extends Fragment {

    private RecyclerView rvBrandGrid;
    private List<Brand> fullBrandList = new ArrayList<>();
    private BrandAdapter adapter;
    private LinearLayout layoutFilterChips;
    private View loadingState;
    private View emptyState;
    
    private ViewPager2 vpBrandBanner;
    private CategoryBannerAdapter bannerAdapter;
    private final Handler autoSlideHandler = new Handler(Looper.getMainLooper());
    private Runnable autoSlideRunnable;
    private final List<View> indicators = new ArrayList<>();

    private CatalogViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_brand, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(CatalogViewModel.class);

        initViews(view);
        setupTopBar(view);
        setupFilterLogic();
        setupHeroSlider(view);
        observeViewModel();
        
        viewModel.loadBrands();

        BottomNavigationHelper.setup(view, tabIndex -> {
            // Handle tab navigation
        });
        BottomNavigationHelper.setSelectedTab(view, BottomNavigationHelper.TAB_CATEGORY);
    }

    private void initViews(View root) {
        rvBrandGrid = root.findViewById(R.id.rvBrandGrid);
        layoutFilterChips = root.findViewById(R.id.layoutFilterChips);
        loadingState = root.findViewById(R.id.viewBrandLoading);
        emptyState = root.findViewById(R.id.viewBrandEmpty);
        
        rvBrandGrid.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        
        // Initial adapter setup
        adapter = new BrandAdapter(new ArrayList<>());
        adapter.setOnBrandClickListener(brand -> {
            // Handle brand click - navigate to products by brand
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, ProductListingFragment.newBrandInstance(brand.getBrandName()))
                        .addToBackStack(null)
                        .commit();
            }
        });
        rvBrandGrid.setAdapter(adapter);
    }

    private void setupTopBar(View root) {
        View topBar = root.findViewById(R.id.includeBrandTopBar);
        TextView tvTitle = topBar.findViewById(R.id.tvTopBarTitle);
        if (tvTitle != null) tvTitle.setText(R.string.top_bar_brand_title);

        ImageButton btnBack = topBar.findViewById(R.id.btnTopBarBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) getActivity().getSupportFragmentManager().popBackStack();
            });
        }

        ImageButton btnSearch = topBar.findViewById(R.id.btnTopBarSearch);
        View containerSearch = root.findViewById(R.id.containerBrandSearchBar);
        if (btnSearch != null && containerSearch != null) {
            btnSearch.setOnClickListener(v -> {
                int visibility = containerSearch.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
                containerSearch.setVisibility(visibility);
            });
        }
    }

    private void setupHeroSlider(View root) {
        vpBrandBanner = root.findViewById(R.id.vpBrandBanner);
        if (vpBrandBanner == null) return;

        indicators.clear();
        indicators.add(root.findViewById(R.id.indicator0));
        indicators.add(root.findViewById(R.id.indicator1));
        indicators.add(root.findViewById(R.id.indicator2));

        bannerAdapter = new CategoryBannerAdapter();
        vpBrandBanner.setAdapter(bannerAdapter);

        List<HomeBannerItem> items = new ArrayList<>();
        items.add(new HomeBannerItem("1", "", "", "", "Xem ngay", null, R.drawable.img_brand_1, "promotion", "4", true, 1));
        items.add(new HomeBannerItem("2", "", "", "", "Xem ngay", null, R.drawable.img_brand_2, "promotion", "5", true, 2));
        items.add(new HomeBannerItem("3", "", "", "", "Xem ngay", null, R.drawable.img_brand_3, "promotion", "1", true, 3));
        items.add(new HomeBannerItem("4", "", "", "", "Xem ngay", null, R.drawable.img_brand_4, "promotion", "1", true, 3));

        bannerAdapter.setItems(items);

        int startPosition = (Integer.MAX_VALUE / 2) - ((Integer.MAX_VALUE / 2) % items.size());
        vpBrandBanner.setCurrentItem(startPosition, false);
        updateIndicators(startPosition % items.size());

        setupAutoSlide(items.size());

        vpBrandBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                int realPos = position % items.size();
                updateIndicators(realPos);
                
                autoSlideHandler.removeCallbacks(autoSlideRunnable);
                if (autoSlideRunnable != null) {
                    autoSlideHandler.postDelayed(autoSlideRunnable, 4000);
                }
            }
        });
    }

    private void updateIndicators(int position) {
        for (int i = 0; i < indicators.size(); i++) {
            View indicator = indicators.get(i);
            if (indicator != null) {
                if (i == position) {
                    indicator.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.accent_dark)));
                } else {
                    indicator.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.border_divider)));
                }
            }
        }
    }

    private void setupAutoSlide(int size) {
        if (size <= 1) return;

        autoSlideRunnable = new Runnable() {
            @Override
            public void run() {
                if (vpBrandBanner == null) return;
                int currentItem = vpBrandBanner.getCurrentItem();
                vpBrandBanner.setCurrentItem(currentItem + 1, true);
                autoSlideHandler.postDelayed(this, 4000);
            }
        };

        autoSlideHandler.postDelayed(autoSlideRunnable, 4000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        autoSlideHandler.removeCallbacks(autoSlideRunnable);
    }

    private void observeViewModel() {
        viewModel.getBrands().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            
            switch (result.status) {
                case LOADING:
                    showLoading(true);
                    showEmpty(false);
                    break;
                case SUCCESS:
                    showLoading(false);
                    if (result.data != null) {
                        fullBrandList = result.data;
                        adapter.updateData(fullBrandList);
                        showEmpty(fullBrandList.isEmpty());
                    }
                    break;
                case EMPTY:
                    showLoading(false);
                    showEmpty(true);
                    break;
                case ERROR:
                case NO_INTERNET:
                    showLoading(false);
                    Toast.makeText(getContext(), result.message != null ? result.message : "Error loading brands", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void showLoading(boolean isLoading) {
        if (loadingState != null) loadingState.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        if (isLoading) {
            rvBrandGrid.setVisibility(View.GONE);
        } else {
            rvBrandGrid.setVisibility(View.VISIBLE);
        }
    }

    private void showEmpty(boolean isEmpty) {
        if (emptyState != null) emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        if (isEmpty) rvBrandGrid.setVisibility(View.GONE);
    }

    private void setupFilterLogic() {
        if (layoutFilterChips == null) return;
        for (int i = 0; i < layoutFilterChips.getChildCount(); i++) {
            View child = layoutFilterChips.getChildAt(i);
            if (child instanceof TextView) {
                TextView chip = (TextView) child;
                chip.setOnClickListener(v -> {
                    updateFilterUI(chip);
                    // Filter brands based on region - Currently disabled as per requirements
                    // filterBrands(chip.getText().toString());
                });
            }
        }
    }

    private void updateFilterUI(TextView selectedChip) {
        for (int i = 0; i < layoutFilterChips.getChildCount(); i++) {
            View child = layoutFilterChips.getChildAt(i);
            if (child instanceof TextView) {
                TextView chip = (TextView) child;
                if (chip == selectedChip) {
                    chip.setBackgroundResource(R.drawable.bg_chip_selected);
                    chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.background_main));
                } else {
                    chip.setBackgroundResource(R.drawable.bg_chip_outline);
                    chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary));
                }
            }
        }
    }
}
