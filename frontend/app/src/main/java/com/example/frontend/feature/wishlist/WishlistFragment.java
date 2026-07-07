package com.example.frontend.feature.wishlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.example.frontend.data.model.wishlist.WishlistItemResponse;
import com.example.frontend.model.Product;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import ui.category.ProductAdapter;
import ui.common.BottomNavigationHelper;

public class WishlistFragment extends Fragment implements ProductAdapter.OnSelectionChangeListener {
    private WishlistViewModel viewModel;
    private ProductAdapter adapter;
    private RecyclerView rvWishlist;
    private View layoutLoading, layoutEmpty, layoutControls, layoutBulkAction;
    private TextView tvSelectAction, tvSelectedCount, tvClearAll;
    private Chip chipSort;
    private List<WishlistItemResponse> currentItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wishlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(WishlistViewModel.class);
        
        initViews(view);
        observeViewModel();
        
        viewModel.loadWishlist();
    }

    private void initViews(View view) {
        View topBar = view.findViewById(R.id.layoutTopBar);
        if (topBar != null) {
            TextView tvTitle = topBar.findViewById(R.id.tvTopBarTitle);
            if (tvTitle != null) tvTitle.setText("Sản phẩm yêu thích");
            topBar.findViewById(R.id.btnTopBarBack).setOnClickListener(v -> {
                if (getActivity() != null) getActivity().getOnBackPressedDispatcher().onBackPressed();
            });
        }

        rvWishlist = view.findViewById(R.id.rvWishlist);
        layoutLoading = view.findViewById(R.id.layoutWishlistLoading);
        layoutEmpty = view.findViewById(R.id.layoutWishlistEmpty);
        layoutControls = view.findViewById(R.id.layoutWishlistControls);
        layoutBulkAction = view.findViewById(R.id.layoutBulkAction);
        tvSelectAction = view.findViewById(R.id.tvSelectAction);
        tvClearAll = view.findViewById(R.id.tvClearAll);
        tvSelectedCount = view.findViewById(R.id.tvSelectedCount);
        chipSort = view.findViewById(R.id.chipSort);

        adapter = new ProductAdapter();
        adapter.setShowSimilarAction(true);
        adapter.setOnProductClickListener(product -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, com.example.frontend.feature.product.ProductDetailFragment.newInstance(product.getId()))
                        .addToBackStack(null)
                        .commit();
            }
        });

        adapter.setOnWishlistClickListener((product, position) -> {
            viewModel.removeFromWishlist(product.getId());
            currentItems.removeIf(item -> item.getProduct().getId().equals(product.getId()));
            updateList();
        });

        adapter.setOnSimilarClickListener(product -> {
            // Navigate to similar products (e.g. search with query or category)
            Toast.makeText(getContext(), "Finding similar products for " + product.getName(), Toast.LENGTH_SHORT).show();
            // Implement navigation to a product listing with category filter if possible
        });

        rvWishlist.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvWishlist.setAdapter(adapter);

        tvClearAll.setOnClickListener(v -> confirmClearAll());
        tvSelectAction.setOnClickListener(v -> toggleSelectionMode());
        view.findViewById(R.id.btnCancelSelect).setOnClickListener(v -> toggleSelectionMode());
        view.findViewById(R.id.btnBulkRemove).setOnClickListener(v -> confirmBulkRemove());
        
        chipSort.setOnClickListener(v -> showSortBottomSheet());

        if (layoutEmpty != null) {
            View btnExplore = layoutEmpty.findViewById(R.id.btnEmptyAction);
            if (btnExplore != null) {
                btnExplore.setOnClickListener(v -> {
                   if (getActivity() != null) {
                       getActivity().getSupportFragmentManager().popBackStack();
                   }
                });
            }
        }
    }

    private void toggleSelectionMode() {
        boolean isMode = !adapter.isSelectionMode();
        adapter.setSelectionMode(isMode);
        layoutBulkAction.setVisibility(isMode ? View.VISIBLE : View.GONE);
        layoutControls.setVisibility(isMode ? View.GONE : View.VISIBLE);
        tvSelectAction.setText(isMode ? "Hủy" : "Chọn");
        tvClearAll.setVisibility(isMode ? View.GONE : View.VISIBLE);
        if (!isMode) {
            onSelectionChanged(0);
        }
    }

    private void confirmBulkRemove() {
        int count = adapter.getSelectedProductIds().size();
        if (count == 0) return;

        new AlertDialog.Builder(requireContext())
                .setTitle("Bỏ thích sản phẩm")
                .setMessage("Bạn có chắc chắn muốn bỏ thích " + count + " sản phẩm đã chọn?")
                .setPositiveButton("Bỏ thích", (dialog, which) -> {
                    List<String> productIds = new ArrayList<>(adapter.getSelectedProductIds());
                    // We need itemIds for bulk delete API, or we find them from currentItems
                    List<String> itemIds = currentItems.stream()
                            .filter(item -> productIds.contains(item.getProduct().getId()))
                            .map(WishlistItemResponse::getWishlistItemId)
                            .collect(Collectors.toList());
                    viewModel.bulkDelete(itemIds);
                    toggleSelectionMode();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void confirmClearAll() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa tất cả sản phẩm yêu thích?")
                .setMessage("Bạn có chắc muốn xóa toàn bộ wishlist không? Thao tác này không ảnh hưởng đến giỏ hàng.")
                .setPositiveButton("Xóa tất cả", (dialog, which) -> {
                    viewModel.clearWishlist();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showSortBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_arrange, null);
        dialog.setContentView(view);

        view.findViewById(R.id.btnCloseArrange).setOnClickListener(v -> dialog.dismiss());
        
        view.findViewById(R.id.layoutSortNewest).setOnClickListener(v -> {
            viewModel.setSort("latest");
            chipSort.setText("Mới nhất");
            dialog.dismiss();
        });
        view.findViewById(R.id.layoutSortPriceLowToHigh).setOnClickListener(v -> {
            viewModel.setSort("price_asc");
            chipSort.setText("Giá thấp - cao");
            dialog.dismiss();
        });
        view.findViewById(R.id.layoutSortPriceHighToLow).setOnClickListener(v -> {
            viewModel.setSort("price_desc");
            chipSort.setText("Giá cao - thấp");
            dialog.dismiss();
        });

        dialog.show();
    }

    private void observeViewModel() {
        viewModel.getWishlistResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case LOADING:
                    layoutLoading.setVisibility(View.VISIBLE);
                    layoutEmpty.setVisibility(View.GONE);
                    rvWishlist.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    layoutLoading.setVisibility(View.GONE);
                    currentItems = result.data;
                    updateList();
                    break;
                case ERROR:
                    layoutLoading.setVisibility(View.GONE);
                    Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        viewModel.getBulkDeleteResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.status == com.example.frontend.data.remote.NetworkResult.Status.SUCCESS) {
                Toast.makeText(getContext(), "Đã xóa sản phẩm khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                viewModel.loadWishlist();
            }
        });
    }

    private void updateList() {
        if (currentItems == null || currentItems.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvWishlist.setVisibility(View.GONE);
            layoutControls.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvWishlist.setVisibility(View.VISIBLE);
            layoutControls.setVisibility(View.VISIBLE);
            
            List<Product> products = currentItems.stream().map(item -> {
                Product p = item.getProduct();
                if (p != null) {
                    p.setFavorite(true);
                }
                return p;
            }).filter(p -> p != null).collect(Collectors.toList());
            
            adapter.setProducts(products);
        }
    }

    @Override
    public void onSelectionChanged(int count) {
        tvSelectedCount.setText("Đã chọn " + count);
    }
}
