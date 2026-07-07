package com.example.frontend.feature.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.data.remote.UrlUtils;
import com.example.frontend.model.Product;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;

public class SearchRecommendProductAdapter extends RecyclerView.Adapter<SearchRecommendProductAdapter.ViewHolder> {

    private List<Product> productList = new ArrayList<>();
    private OnProductClickListener listener;
    private OnWishlistClickListener wishlistListener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public interface OnWishlistClickListener {
        void onWishlistClick(Product product, int position);
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void setOnWishlistClickListener(OnWishlistClickListener listener) {
        this.wishlistListener = listener;
    }

    public void setItems(List<Product> items) {
        this.productList = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_card, parent, false);
        // Ensure item fills the grid column
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        view.setLayoutParams(params);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvProductName.setText(product.getName());
        holder.tvProductBrand.setText(product.getBrand());
        holder.tvProductPrice.setText(product.getPrice());
        
        if (holder.tvProductReviewCount != null) {
            holder.tvProductReviewCount.setText("(" + product.getReviewCount() + ")");
        }
        
        if (holder.tvProductRating != null) {
            try {
                holder.tvProductRating.setRating(Float.parseFloat(product.getRating()));
            } catch (Exception e) {
                holder.tvProductRating.setRating(0);
            }
        }

        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(holder.ivProductImage.getContext())
                    .load(UrlUtils.getFullUrl(product.getImageUrl()))
                    .placeholder(R.drawable.ic_product)
                    .error(R.drawable.ic_product)
                    .into(holder.ivProductImage);
        } else {
            holder.ivProductImage.setImageResource(product.getImageResource() != 0 ? product.getImageResource() : R.drawable.ic_product);
        }

        if (holder.layoutProductStatusBadge != null) {
            if (product.getBadgeText() != null && !product.getBadgeText().isEmpty()) {
                holder.tvProductBadge.setText(product.getBadgeText());
                holder.layoutProductStatusBadge.setVisibility(View.VISIBLE);
            } else {
                holder.layoutProductStatusBadge.setVisibility(View.GONE);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onProductClick(product);
        });
        
        if (holder.btnWishlist != null) {
            holder.btnWishlist.setSelected(product.isFavorite());
            holder.btnWishlist.setOnClickListener(v -> {
                if (wishlistListener != null) {
                    wishlistListener.onWishlistClick(product, position);
                } else {
                    boolean newState = !product.isFavorite();
                    product.setFavorite(newState);
                    v.setSelected(newState);
                }
            });
        }
        
        if (holder.btnAddToCart != null) {
            holder.btnAddToCart.setOnClickListener(v -> {
                // TODO: Handle add to cart
            });
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductBrand, tvProductPrice, tvProductReviewCount, tvProductBadge;
        RatingBar tvProductRating;
        View layoutProductStatusBadge;
        ImageButton btnWishlist, btnAddToCart;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductBrand = itemView.findViewById(R.id.tvProductBrand);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductReviewCount = itemView.findViewById(R.id.tvProductReviewCount);
            tvProductBadge = itemView.findViewById(R.id.tvProductBadge);
            tvProductRating = itemView.findViewById(R.id.tvProductRating);
            layoutProductStatusBadge = itemView.findViewById(R.id.layoutProductStatusBadge);
            btnWishlist = itemView.findViewById(R.id.btnWishlist);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}
