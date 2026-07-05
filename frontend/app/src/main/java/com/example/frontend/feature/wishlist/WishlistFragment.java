package com.example.frontend.feature.wishlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import ui.category.ProductAdapter;

public class WishlistFragment extends Fragment {
    private WishlistViewModel viewModel;
    private ProductAdapter adapter;
    private RecyclerView rvWishlist;
    private View layoutLoading, layoutEmpty;

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
            if (tvTitle != null) tvTitle.setText("Danh sách yêu thích");
            topBar.findViewById(R.id.btnTopBarBack).setOnClickListener(v -> {
                if (getActivity() != null) getActivity().getOnBackPressedDispatcher().onBackPressed();
            });
        }

        rvWishlist = view.findViewById(R.id.rvWishlist);
        layoutLoading = view.findViewById(R.id.layoutWishlistLoading);
        layoutEmpty = view.findViewById(R.id.layoutWishlistEmpty);

        adapter = new ProductAdapter();
        // Clicks could navigate to product detail
        rvWishlist.setAdapter(adapter);
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
                    // Mapping Object to Product might be needed here depending on real API response
                    // For now keeping it empty if data is not correctly typed
                    layoutEmpty.setVisibility(View.VISIBLE);
                    break;
                case EMPTY:
                    layoutLoading.setVisibility(View.GONE);
                    layoutEmpty.setVisibility(View.VISIBLE);
                    rvWishlist.setVisibility(View.GONE);
                    break;
                case ERROR:
                    layoutLoading.setVisibility(View.GONE);
                    Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }
}
