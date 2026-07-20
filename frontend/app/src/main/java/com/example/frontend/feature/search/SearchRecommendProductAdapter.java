package com.example.frontend.feature.search;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.example.frontend.model.Product;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchRecommendProductAdapter extends RecyclerView.Adapter<SearchRecommendProductAdapter.ViewHolder> {

    private final List<Product> productList = new ArrayList<>();
    private OnProductClickListener clickListener;
    private OnWishlistClickListener wishlistClickListener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onAddToCartClick(Product product);
    }

    public interface OnWishlistClickListener {
        void onWishlistClick(Product product, int position);
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnWishlistClickListener(OnWishlistClickListener listener) {
        this.wishlistClickListener = listener;
    }

    public void setItems(List<Product> newItems) {
        productList.clear();
        if (newItems != null) {
            productList.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product, clickListener, wishlistClickListener, position);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductBrand;
        TextView tvProductName;
        TextView tvProductPrice;
        TextView tvProductOriginalPrice;
        RatingBar tvProductRating;
        TextView tvProductReviewCount;
        ImageButton btnAddToCart;
        ImageButton btnWishlist;
        View layoutProductStatusBadge;
        TextView tvProductBadge;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductBrand = itemView.findViewById(R.id.tvProductBrand);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductOriginalPrice = itemView.findViewById(R.id.tvProductOriginalPrice);
            tvProductRating = itemView.findViewById(R.id.tvProductRating);
            tvProductReviewCount = itemView.findViewById(R.id.tvProductReviewCount);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
            btnWishlist = itemView.findViewById(R.id.btnWishlist);
            layoutProductStatusBadge = itemView.findViewById(R.id.layoutProductStatusBadge);
            tvProductBadge = itemView.findViewById(R.id.tvProductBadge);
        }

        void bind(Product product, OnProductClickListener listener, OnWishlistClickListener wishlistListener, int position) {
            if (product.getBrand() != null && !product.getBrand().isEmpty()) {
                tvProductBrand.setText(product.getBrand());
            } else {
                tvProductBrand.setText("");
            }
            
            tvProductName.setText(product.getName() != null ? product.getName() : "");

            // Format price
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            tvProductPrice.setText(format.format(product.getPriceValue()));

            // Original price / sale
            if (product.getCompareAtPrice() != null && product.getCompareAtPrice() > product.getPriceValue()) {
                tvProductOriginalPrice.setVisibility(View.VISIBLE);
                tvProductOriginalPrice.setText(format.format(product.getCompareAtPrice()));
                tvProductOriginalPrice.setPaintFlags(tvProductOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                
                layoutProductStatusBadge.setVisibility(View.VISIBLE);
                tvProductBadge.setText("SALE");
            } else {
                tvProductOriginalPrice.setVisibility(View.GONE);
                layoutProductStatusBadge.setVisibility(View.GONE);
            }

            // Rating
            if (product.getAverageRatingValue() > 0) {
                tvProductRating.setRating((float) product.getAverageRatingValue());
            } else {
                tvProductRating.setRating(0);
            }
            tvProductReviewCount.setText(product.getReviewCount() != null ? "(" + product.getReviewCount() + ")" : "(0)");

            // Image
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(product.getImageUrl())
                        .placeholder(R.drawable.ic_product)
                        .into(ivProductImage);
            } else {
                ivProductImage.setImageResource(R.drawable.ic_product);
            }

            // Wishlist state
            btnWishlist.setSelected(product.isFavorite());

            // Click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onProductClick(product);
            });

            btnAddToCart.setOnClickListener(v -> {
                if (listener != null) listener.onAddToCartClick(product);
            });

            btnWishlist.setOnClickListener(v -> {
                if (wishlistListener != null) wishlistListener.onWishlistClick(product, position);
            });
        }
    }
}
