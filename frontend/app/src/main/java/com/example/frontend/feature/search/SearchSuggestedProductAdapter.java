package com.example.frontend.feature.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.Product;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;

public class SearchSuggestedProductAdapter extends RecyclerView.Adapter<SearchSuggestedProductAdapter.ViewHolder> {

    private List<Product> productList = new ArrayList<>();
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Product> items) {
        this.productList = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvProductName.setText(product.getName());
        holder.tvProductBrand.setText(product.getBrand());
        holder.tvProductPrice.setText(product.getPrice());
        holder.tvProductReviewCount.setText("(" + product.getReviewCount() + ")");
        
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(holder.ivProductImage.getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_product)
                    .error(R.drawable.ic_product)
                    .into(holder.ivProductImage);
        } else {
            holder.ivProductImage.setImageResource(product.getImageResource() != 0 ? product.getImageResource() : R.drawable.ic_product);
        }
        
        if (product.getBadgeText() != null && !product.getBadgeText().isEmpty()) {
            holder.tvProductBadge.setText(product.getBadgeText());
            holder.layoutProductStatusBadge.setVisibility(View.VISIBLE);
        } else {
            holder.layoutProductStatusBadge.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onProductClick(product);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductBrand, tvProductPrice, tvProductReviewCount, tvProductBadge;
        View layoutProductStatusBadge;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductBrand = itemView.findViewById(R.id.tvProductBrand);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductReviewCount = itemView.findViewById(R.id.tvProductReviewCount);
            tvProductBadge = itemView.findViewById(R.id.tvProductBadge);
            layoutProductStatusBadge = itemView.findViewById(R.id.layoutProductStatusBadge);
        }
    }
}
