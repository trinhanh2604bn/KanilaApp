package com.example.frontend.ui.category;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<Product> products = new ArrayList<>();
    private OnProductClickListener listener;
    private OnWishlistClickListener wishlistListener;
    private OnSimilarClickListener similarClickListener;
    private boolean isSelectionMode = false;
    private boolean showSimilarAction = false;
    private final Set<String> selectedProductIds = new HashSet<>();

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public interface OnWishlistClickListener {
        void onWishlistClick(Product product, int position);
    }

    public interface OnSimilarClickListener {
        void onSimilarClick(Product product);
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void setOnWishlistClickListener(OnWishlistClickListener listener) {
        this.wishlistListener = listener;
    }

    public void setOnSimilarClickListener(OnSimilarClickListener listener) {
        this.similarClickListener = listener;
    }

    public void setShowSimilarAction(boolean showSimilarAction) {
        this.showSimilarAction = showSimilarAction;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setSelectionMode(boolean selectionMode) {
        isSelectionMode = selectionMode;
        if (!selectionMode) {
            selectedProductIds.clear();
        }
        notifyDataSetChanged();
    }

    public boolean isSelectionMode() {
        return isSelectionMode;
    }

    public Set<String> getSelectedProductIds() {
        return selectedProductIds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        
        holder.tvBrand.setText(product.getBrand());
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(product.getPrice());
        
        if (holder.btnWishlist != null) {
            holder.btnWishlist.setSelected(product.isFavorite());
            holder.btnWishlist.setVisibility(isSelectionMode ? View.GONE : View.VISIBLE);
        }

        if (holder.tvFindSimilar != null) {
            holder.tvFindSimilar.setVisibility(showSimilarAction && !isSelectionMode ? View.VISIBLE : View.GONE);
            holder.tvFindSimilar.setOnClickListener(v -> {
                if (similarClickListener != null) {
                    similarClickListener.onSimilarClick(product);
                }
            });
        }

        if (holder.cbSelect != null) {
            holder.cbSelect.setVisibility(isSelectionMode ? View.VISIBLE : View.GONE);
            holder.cbSelect.setChecked(selectedProductIds.contains(product.getId()));
            holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedProductIds.add(product.getId());
                } else {
                    selectedProductIds.remove(product.getId());
                }
                if (listener instanceof OnSelectionChangeListener) {
                    ((OnSelectionChangeListener) listener).onSelectionChanged(selectedProductIds.size());
                }
            });
        }

        try {
            holder.ratingBar.setRating(Float.parseFloat(product.getRating()));
        } catch (Exception e) {
            holder.ratingBar.setRating(0);
        }
        holder.tvReviewCount.setText("(" + product.getReviewCount() + ")");

        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(holder.ivImage.getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_product)
                    .error(R.drawable.ic_product)
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(product.getImageResource() != 0 ? product.getImageResource() : R.drawable.ic_product);
        }

        String badge = product.getBadgeText();
        if (badge != null && !badge.isEmpty()) {
            holder.tvBadge.setText(badge);
            holder.layoutBadge.setVisibility(View.VISIBLE);
        } else {
            holder.layoutBadge.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (isSelectionMode) {
                if (holder.cbSelect != null) {
                    holder.cbSelect.setChecked(!holder.cbSelect.isChecked());
                }
            } else if (listener != null) {
                listener.onProductClick(product);
            }
        });

        if (holder.btnWishlist != null) {
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
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public interface OnSelectionChangeListener {
        void onSelectionChanged(int count);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvBrand, tvPrice, tvReviewCount, tvBadge;
        RatingBar ratingBar;
        View layoutBadge;
        ImageButton btnAddToCart, btnWishlist;
        CheckBox cbSelect;
        TextView tvFindSimilar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivProductImage);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvBrand = itemView.findViewById(R.id.tvProductBrand);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            ratingBar = itemView.findViewById(R.id.tvProductRating);
            tvReviewCount = itemView.findViewById(R.id.tvProductReviewCount);
            tvBadge = itemView.findViewById(R.id.tvProductBadge);
            layoutBadge = itemView.findViewById(R.id.layoutProductStatusBadge);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
            btnWishlist = itemView.findViewById(R.id.btnWishlist);
            cbSelect = itemView.findViewById(R.id.cbSelect);
            tvFindSimilar = itemView.findViewById(R.id.tvFindSimilar);
        }
    }
}
