package com.example.frontend.feature.community.reels;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.databinding.ItemReelProductMockBinding;
import com.example.frontend.feature.community.reels.mock.MockReelsDataSource;

import java.util.ArrayList;
import java.util.List;

public class ReelProductAdapter extends RecyclerView.Adapter<ReelProductAdapter.ProductViewHolder> {

    private final List<MockReelsDataSource.MockReelProduct> items = new ArrayList<>();
    private OnProductActionListener listener;

    public interface OnProductActionListener {
        void onAddToCart(MockReelsDataSource.MockReelProduct product);
        void onBuyNow(MockReelsDataSource.MockReelProduct product);
        void onDetail(MockReelsDataSource.MockReelProduct product);
    }

    public void setOnProductActionListener(OnProductActionListener listener) {
        this.listener = listener;
    }

    public void setItems(List<MockReelsDataSource.MockReelProduct> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReelProductMockBinding binding = ItemReelProductMockBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ProductViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ItemReelProductMockBinding binding;

        public ProductViewHolder(ItemReelProductMockBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(MockReelsDataSource.MockReelProduct product) {
            binding.tvBrandName.setText(product.getBrandName());
            binding.tvProductName.setText(product.getProductName());
            binding.tvProductPrice.setText(product.getPriceText());
            
            if (product.getOldPriceText() != null && !product.getOldPriceText().isEmpty()) {
                binding.tvOldPrice.setVisibility(View.VISIBLE);
                binding.tvOldPrice.setText(product.getOldPriceText());
                binding.tvOldPrice.setPaintFlags(binding.tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                binding.tvOldPrice.setVisibility(View.GONE);
            }

            binding.tvRating.setText(product.getRatingText());
            binding.tvReviewCount.setText("(" + product.getReviewCountText() + ")");
            binding.tvStockStatus.setText(product.getStockText());

            boolean inStock = product.isInStock();
            binding.btnAddToCart.setEnabled(inStock);
            binding.btnBuyNow.setEnabled(inStock);

            binding.btnAddToCart.setOnClickListener(v -> {
                if (listener != null) listener.onAddToCart(product);
            });

            binding.btnBuyNow.setOnClickListener(v -> {
                if (listener != null) listener.onBuyNow(product);
            });

            binding.btnDetail.setOnClickListener(v -> {
                if (listener != null) listener.onDetail(product);
            });
        }
    }
}
