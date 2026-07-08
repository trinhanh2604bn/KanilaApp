package com.example.frontend.feature.community.reels;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.frontend.databinding.BottomSheetReelProductsBinding;
import com.example.frontend.feature.community.reels.mock.MockReelsDataSource;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class ReelsProductBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_REEL_ID = "reel_id";
    private BottomSheetReelProductsBinding binding;
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
        
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        ReelProductAdapter adapter = new ReelProductAdapter();
        adapter.setOnProductActionListener(new ReelProductAdapter.OnProductActionListener() {
            @Override
            public void onAddToCart(MockReelsDataSource.MockReelProduct product) {
                Toast.makeText(getContext(), "Đã thêm vào giỏ hàng demo", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBuyNow(MockReelsDataSource.MockReelProduct product) {
                Toast.makeText(getContext(), "MVP demo - chưa kết nối thanh toán", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDetail(MockReelsDataSource.MockReelProduct product) {
                Toast.makeText(getContext(), "Xem chi tiết: " + product.getProductName(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.rvReelProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvReelProducts.setAdapter(adapter);

        List<MockReelsDataSource.MockReelProduct> products = MockReelsDataSource.getProductsByReelId(reelId);
        adapter.setItems(products);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
