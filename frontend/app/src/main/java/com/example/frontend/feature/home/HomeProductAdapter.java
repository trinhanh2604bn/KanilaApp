package com.example.frontend.feature.home;

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

public class HomeProductAdapter extends RecyclerView.Adapter<HomeProductAdapter.ViewHolder> {
    private List<Product> products = new ArrayList<>();
    private OnProductClickListener listener;
    private OnWishlistToggleListener wishlistToggleListener;
    private int itemWidth = -1;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public interface OnWishlistToggleListener {
        void onWishlistToggle(Product product, boolean isWishlisted);
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void setOnWishlistToggleListener(OnWishlistToggleListener listener) {
        this.wishlistToggleListener = listener;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setItemWidth(int width) {
        this.itemWidth = width;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_card, parent, false);
        if (itemWidth > 0) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp != null) {
                lp.width = itemWidth;
                view.setLayoutParams(lp);
            }
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.tvName.setText(product.getName());
        holder.tvBrand.setText(product.getBrand());
        holder.tvPrice.setText(product.getPrice());
        holder.tvReviewCount.setText("(" + product.getReviewCount() + ")");
        if (holder.rbRating != null) {
            holder.rbRating.setRating((float) product.getAverageRatingValue());
        }

        if (holder.btnWishlist != null) {
            holder.btnWishlist.setSelected(product.isFavorite());
            holder.btnWishlist.setOnClickListener(v -> {
                boolean newState = !product.isFavorite();
                product.setFavorite(newState);
                v.setSelected(newState);
                if (wishlistToggleListener != null) {
                    wishlistToggleListener.onWishlistToggle(product, !newState); // passing old state
                }
            });
        }

        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(holder.ivImage.getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_product)
                    .error(R.drawable.ic_product)
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(product.getImageResource() != 0 ? product.getImageResource() : R.drawable.ic_product);
        }

        if (product.getBadgeText() != null && !product.getBadgeText().isEmpty()) {
            holder.tvBadge.setText(product.getBadgeText());
            holder.layoutBadge.setVisibility(View.VISIBLE);
        } else {
            holder.layoutBadge.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onProductClick(product);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvBrand, tvPrice, tvReviewCount, tvBadge;
        RatingBar rbRating;
        View layoutBadge;
        ImageButton btnWishlist;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivProductImage);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvBrand = itemView.findViewById(R.id.tvProductBrand);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            tvReviewCount = itemView.findViewById(R.id.tvProductReviewCount);
            rbRating = itemView.findViewById(R.id.tvProductRating);
            tvBadge = itemView.findViewById(R.id.tvProductBadge);
            layoutBadge = itemView.findViewById(R.id.layoutProductStatusBadge);
            btnWishlist = itemView.findViewById(R.id.btnWishlist);
        }
    }
}
