package com.example.frontend.feature.chatbot.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.example.frontend.feature.chatbot.model.ChatProductUiModel;

import java.util.ArrayList;
import java.util.List;

public class ChatProductAdapter extends RecyclerView.Adapter<ChatProductAdapter.ProductViewHolder> {

    private final List<ChatProductUiModel> products = new ArrayList<>();
    private final OnProductClickListener listener;
    private OnAddToCartClickListener addToCartListener;
    private boolean customerContextUsed = false;

    public interface OnProductClickListener {
        void onProductClick(ChatProductUiModel product);
    }

    public interface OnAddToCartClickListener {
        void onAddToCartClick(ChatProductUiModel product);
    }

    public ChatProductAdapter(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void setOnAddToCartClickListener(OnAddToCartClickListener addToCartListener) {
        this.addToCartListener = addToCartListener;
    }

    public void setProducts(List<ChatProductUiModel> newProducts) {
        setProducts(newProducts, false);
    }

    public void setProducts(List<ChatProductUiModel> newProducts, boolean customerContextUsed) {
        this.customerContextUsed = customerContextUsed;
        products.clear();
        if (newProducts != null) {
            products.addAll(newProducts);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_product_card, parent, false);
        return new ProductViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(products.get(position), customerContextUsed, addToCartListener);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvBrandName, tvProductName, tvCategory, tvSalePrice, tvOriginalPrice, tvMatchedReason, tvRecommendationBadge, btnViewDetail, btnAddToCart;
        private final OnProductClickListener listener;

        ProductViewHolder(@NonNull View itemView, OnProductClickListener listener) {
            super(itemView);
            this.listener = listener;
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvBrandName = itemView.findViewById(R.id.tvBrandName);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvSalePrice = itemView.findViewById(R.id.tvSalePrice);
            tvOriginalPrice = itemView.findViewById(R.id.tvOriginalPrice);
            tvMatchedReason = itemView.findViewById(R.id.tvMatchedReason);
            tvRecommendationBadge = itemView.findViewById(R.id.tvRecommendationBadge);
            btnViewDetail = itemView.findViewById(R.id.btnViewDetail);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }

        void bind(ChatProductUiModel product, boolean customerContextUsed, OnAddToCartClickListener addToCartListener) {
            tvBrandName.setText(product.getBrandName());
            tvProductName.setText(product.getName());
            tvCategory.setText(product.getCategoryName());
            
            // Handle prices
            String displayPrice = product.getFinalPriceText() != null ? product.getFinalPriceText() : product.getPriceText();
            String strikePrice = product.getCompareAtPriceText() != null ? product.getCompareAtPriceText() : 
                                (product.getFinalPriceText() != null ? product.getPriceText() : null);
            
            tvSalePrice.setText(displayPrice);
            
            if (strikePrice != null && !strikePrice.isEmpty() && !strikePrice.equals(displayPrice)) {
                tvOriginalPrice.setText(strikePrice);
                tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvOriginalPrice.setVisibility(View.VISIBLE);
            } else {
                tvOriginalPrice.setVisibility(View.GONE);
            }

            tvMatchedReason.setText(product.getReason());
            
            if (product.getSuggestedUse() != null && !product.getSuggestedUse().isEmpty()) {
                tvRecommendationBadge.setText(product.getSuggestedUse());
                tvRecommendationBadge.setVisibility(View.VISIBLE);
            } else {
                tvRecommendationBadge.setVisibility(View.GONE);
            }

            Glide.with(ivProductImage.getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_product)
                    .error(R.drawable.ic_product)
                    .into(ivProductImage);

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onProductClick(product);
            });

            btnViewDetail.setOnClickListener(v -> {
                if (listener != null) listener.onProductClick(product);
            });

            if (btnAddToCart != null) {
                btnAddToCart.setOnClickListener(v -> {
                    if (addToCartListener != null) addToCartListener.onAddToCartClick(product);
                });
            }
        }
    }
}
