package com.example.frontend.feature.community.reels;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.frontend.databinding.ItemReelProductMockBinding;
import com.example.frontend.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ReelProductAdapter extends RecyclerView.Adapter<ReelProductAdapter.ProductViewHolder> {

    private final List<Product> items = new ArrayList<>();
    private OnProductActionListener listener;

    public interface OnProductActionListener {
        void onAddToCart(Product product);
        void onBuyNow(Product product);
        void onDetail(Product product);
    }

    public void setOnProductActionListener(OnProductActionListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Product> newItems) {
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

        public void bind(Product product) {
            binding.tvBrandName.setText(product.getBrand());
            binding.tvProductName.setText(product.getName());
            binding.tvProductPrice.setText(product.getPrice());
            
            Double comparePrice = product.getCompareAtPrice();
            if (comparePrice != null && comparePrice > 0) {
                binding.tvOldPrice.setVisibility(View.VISIBLE);
                String oldPriceText = String.format(java.util.Locale.US, "%,.0fđ", comparePrice).replace(",", ".");
                binding.tvOldPrice.setText(oldPriceText);
                binding.tvOldPrice.setPaintFlags(binding.tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                binding.tvOldPrice.setVisibility(View.GONE);
            }

            binding.tvRating.setText(product.getRating());
            binding.tvReviewCount.setText("(" + product.getReviewCount() + ")");
            
            int stock = product.getStock();
            binding.tvStockStatus.setText(stock > 0 ? "Còn hàng" : "Hết hàng");

            boolean inStock = stock > 0;
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

            // Load Image with Glide
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                Glide.with(binding.ivProductImage.getContext())
                        .load(product.getImageUrl())
                        .placeholder(com.example.frontend.R.drawable.ic_product)
                        .into(binding.ivProductImage);
            } else if (product.getImageResource() != 0) {
                binding.ivProductImage.setImageResource(product.getImageResource());
            }
        }
    }
}
