package com.example.frontend.feature.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    private OnAddToCartListener addToCartListener;
    private int itemWidth = -1;

    @FunctionalInterface
    public interface OnProductClickListener {
        void onProductClick(Product product);
        default void onAddToCartClick(Product product) {}
    }

    public interface OnWishlistToggleListener {
        void onWishlistToggle(Product product, boolean isWishlisted);
    }

    public interface OnAddToCartListener {
        void onAddToCart(Product product);
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void setOnWishlistToggleListener(OnWishlistToggleListener listener) {
        this.wishlistToggleListener = listener;
    }

    public void setOnAddToCartListener(OnAddToCartListener listener) {
        this.addToCartListener = listener;
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
        
        // Đảm bảo kích thước view luôn chuẩn xác khi reuse
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (itemWidth > 0) {
            layoutParams.width = itemWidth;
        } else {
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        holder.itemView.setLayoutParams(layoutParams);

        holder.tvName.setText(product.getName());
        holder.tvBrand.setText(product.getBrand());
        holder.tvPrice.setText(product.getPrice());
        holder.tvReviewCount.setText("(" + product.getReviewCount() + ")");
        if (holder.tvRating != null) {
            double rating = product.getAverageRatingValue();
            holder.tvRating.setText(rating > 0 ? String.format("★ %.1f", rating) : "");
            holder.tvRating.setVisibility(rating > 0 ? View.VISIBLE : View.GONE);
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

        if (holder.btnAddToCart != null) {
            holder.btnAddToCart.setOnClickListener(v -> {
                if (addToCartListener != null) {
                    addToCartListener.onAddToCart(product);
                } else if (listener != null) {
                    listener.onAddToCartClick(product);
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
        } else if (product.getScore() > 0) {
            holder.tvBadge.setText(Math.round(product.getScore()) + "% Match");
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
        TextView tvName, tvBrand, tvPrice, tvReviewCount, tvBadge, tvRating;
        View layoutBadge;
        ImageButton btnWishlist, btnAddToCart;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivProductImage);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvBrand = itemView.findViewById(R.id.tvProductBrand);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            tvReviewCount = itemView.findViewById(R.id.tvProductReviewCount);
            tvRating = itemView.findViewById(R.id.tvProductRating);
            tvBadge = itemView.findViewById(R.id.tvProductBadge);
            layoutBadge = itemView.findViewById(R.id.layoutProductStatusBadge);
            btnWishlist = itemView.findViewById(R.id.btnWishlist);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}
