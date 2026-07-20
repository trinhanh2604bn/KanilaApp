package com.example.frontend.feature.product.adapter;

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
import java.util.ArrayList;
import java.util.List;

public class RecentlyViewedAdapter extends RecyclerView.Adapter<RecentlyViewedAdapter.ViewHolder> {
    private List<Product> products = new ArrayList<>();
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onAddToCartClick(Product product);
    }

    public void setProducts(List<Product> products) {
        this.products = products != null ? products : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_card, parent, false);
        
        // Set fixed width for horizontal display
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp != null) {
            lp.width = parent.getContext().getResources().getDimensionPixelSize(R.dimen.product_card_width_horizontal);
            view.setLayoutParams(lp);
        }
        
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        
        // Brand & Name
        holder.tvBrand.setText(product.getBrand());
        holder.tvName.setText(product.getName());
        
        // Image
        Glide.with(holder.ivImage.getContext())
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_product)
                .error(R.drawable.ic_product)
                .into(holder.ivImage);

        // Price
        holder.tvPrice.setText(product.getPrice());
        holder.tvOriginalPrice.setVisibility(View.GONE);

        // Rating
        holder.rbRating.setRating((float) product.getAverageRatingValue());
        holder.tvReviewCount.setText(String.format(java.util.Locale.US, "(%s)", product.getReviewCount()));

        // Badge
        if (product.getBadgeText() != null && !product.getBadgeText().isEmpty()) {
            holder.layoutBadge.setVisibility(View.VISIBLE);
            holder.tvBadge.setText(product.getBadgeText());
        } else {
            holder.layoutBadge.setVisibility(View.GONE);
        }

        // Wishlist state
        holder.btnWishlist.setSelected(product.isFavorite());

        // Clicks
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onProductClick(product);
        });

        if (holder.btnAddToCart != null) {
            holder.btnAddToCart.setOnClickListener(v -> {
                if (listener != null) listener.onAddToCartClick(product);
            });
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvBrand, tvName, tvPrice, tvOriginalPrice, tvReviewCount, tvBadge;
        RatingBar rbRating;
        View layoutBadge;
        ImageButton btnWishlist, btnAddToCart;

        ViewHolder(View view) {
            super(view);
            ivImage = view.findViewById(R.id.ivProductImage);
            tvBrand = view.findViewById(R.id.tvProductBrand);
            tvName = view.findViewById(R.id.tvProductName);
            tvPrice = view.findViewById(R.id.tvProductPrice);
            tvOriginalPrice = view.findViewById(R.id.tvProductOriginalPrice);
            tvReviewCount = view.findViewById(R.id.tvProductReviewCount);
            tvBadge = view.findViewById(R.id.tvProductBadge);
            rbRating = view.findViewById(R.id.tvProductRating);
            layoutBadge = view.findViewById(R.id.layoutProductStatusBadge);
            btnWishlist = view.findViewById(R.id.btnWishlist);
            btnAddToCart = view.findViewById(R.id.btnAddToCart);
        }
    }
}
