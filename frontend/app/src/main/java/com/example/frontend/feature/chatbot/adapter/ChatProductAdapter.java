package com.example.frontend.feature.chatbot.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
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

    public interface OnProductClickListener {
        void onProductClick(ChatProductUiModel product);
    }

    public ChatProductAdapter(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void setProducts(List<ChatProductUiModel> newProducts) {
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
        holder.bind(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvBrandName, tvProductName, tvPrice, tvCompareAtPrice, tvReason, tvReviewCount, btnViewDetail;
        RatingBar rbRating;
        View layoutRating;
        private final OnProductClickListener listener;

        ProductViewHolder(@NonNull View itemView, OnProductClickListener listener) {
            super(itemView);
            this.listener = listener;
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvBrandName = itemView.findViewById(R.id.tvBrandName);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvCompareAtPrice = itemView.findViewById(R.id.tvCompareAtPrice);
            tvReason = itemView.findViewById(R.id.tvReason);
            tvReviewCount = itemView.findViewById(R.id.tvReviewCount);
            btnViewDetail = itemView.findViewById(R.id.btnViewDetail);
            rbRating = itemView.findViewById(R.id.rbRating);
            layoutRating = itemView.findViewById(R.id.layoutRating);
        }

        void bind(ChatProductUiModel product) {
            tvBrandName.setText(product.getBrandName());
            tvProductName.setText(product.getName());
            tvPrice.setText(product.getPriceText());
            
            if (product.getCompareAtPriceText() != null && !product.getCompareAtPriceText().isEmpty()) {
                tvCompareAtPrice.setText(product.getCompareAtPriceText());
                tvCompareAtPrice.setPaintFlags(tvCompareAtPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvCompareAtPrice.setVisibility(View.VISIBLE);
            } else {
                tvCompareAtPrice.setVisibility(View.GONE);
            }

            tvReason.setText(product.getReason());

            if (product.getRatingText() != null && !product.getRatingText().isEmpty()) {
                try {
                    float rating = Float.parseFloat(product.getRatingText());
                    rbRating.setRating(rating);
                    tvReviewCount.setText(tvReviewCount.getContext().getString(R.string.chat_product_review_count, product.getReviewCountText()));
                    layoutRating.setVisibility(View.VISIBLE);
                } catch (NumberFormatException e) {
                    layoutRating.setVisibility(View.GONE);
                }
            } else {
                layoutRating.setVisibility(View.GONE);
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
        }
    }
}
