package com.example.frontend.ui.category;

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

public class VoucherProductAdapter extends RecyclerView.Adapter<VoucherProductAdapter.ViewHolder> {
    private List<Product> products = new ArrayList<>();
    private OnProductClickListener listener;
    private final boolean isHorizontal;

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onAddToCartClick(Product product);
    }

    public VoucherProductAdapter(boolean isHorizontal) {
        this.isHorizontal = isHorizontal;
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_card, parent, false);
        if (isHorizontal) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = (int) (parent.getContext().getResources().getDisplayMetrics().density * 160);
            view.setLayoutParams(params);
        }
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
        }

        try {
            try { float r = Float.parseFloat(product.getRating()); holder.ratingBar.setText(r > 0 ? String.format("\u2605 %.1f", r) : ""); holder.ratingBar.setVisibility(r > 0 ? android.view.View.VISIBLE : android.view.View.GONE); } catch (Exception e) { holder.ratingBar.setVisibility(android.view.View.GONE); }
        } catch (Exception e) {
            holder.ratingBar.setVisibility(android.view.View.GONE);
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
            if (listener != null) {
                listener.onProductClick(product);
            }
        });

        if (holder.btnAddToCart != null) {
            holder.btnAddToCart.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddToCartClick(product);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivImage;
        public TextView tvName, tvBrand, tvPrice, tvReviewCount, tvBadge;
        public TextView ratingBar;
        public View layoutBadge;
        public ImageButton btnAddToCart, btnWishlist;

        public ViewHolder(@NonNull View itemView) {
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
        }
    }
}
