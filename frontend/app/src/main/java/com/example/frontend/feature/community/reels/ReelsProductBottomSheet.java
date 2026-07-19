package com.example.frontend.feature.community.reels;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.frontend.databinding.BottomSheetReelProductsBinding;
import com.example.frontend.feature.cart.CartViewModel;
import com.example.frontend.feature.community.reels.mock.MockReelsDataSource;
import com.example.frontend.feature.product.ProductDetailFragment;
import com.example.frontend.feature.product.QuickAddHelper;
import com.example.frontend.model.Product;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import ui.common.FragmentNavigationHelper;

public class ReelsProductBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_REEL_ID = "reel_id";
    private BottomSheetReelProductsBinding binding;
    private CartViewModel cartViewModel;
    private String reelId;

    public static ReelsProductBottomSheet newInstance(String reelId) {
        ReelsProductBottomSheet fragment = new ReelsProductBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_REEL_ID, reelId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            reelId = getArguments().getString(ARG_REEL_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetReelProductsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        ReelsViewModel reelsViewModel = new ViewModelProvider(requireParentFragment()).get(ReelsViewModel.class);
        
        ReelProductAdapter adapter = new ReelProductAdapter();
        adapter.setOnProductActionListener(new ReelProductAdapter.OnProductActionListener() {
            @Override
            public void onAddToCart(Product product) {
                QuickAddHelper.quickAddToCart(getContext(), getParentFragmentManager(), getViewLifecycleOwner(), product, cartViewModel);
            }

            @Override
            public void onBuyNow(Product product) {
                QuickAddHelper.quickBuyNow(getContext(), getParentFragmentManager(), getViewLifecycleOwner(), product, cartViewModel);
            }

            @Override
            public void onDetail(Product product) {
                dismiss();
                FragmentNavigationHelper.loadFragment(getActivity(), ProductDetailFragment.newInstance(product.getId()));
            }
        });

        binding.rvReelProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvReelProducts.setAdapter(adapter);

        List<Product> products = reelsViewModel.getProductsByReelId(reelId);
        adapter.setItems(products);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
