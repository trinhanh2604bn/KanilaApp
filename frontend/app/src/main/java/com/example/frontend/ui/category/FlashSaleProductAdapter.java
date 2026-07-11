package com.example.frontend.ui.category;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.example.frontend.model.Product;

public class FlashSaleProductAdapter extends ListAdapter<Product, FlashSaleProductAdapter.ViewHolder> {

    private OnFlashSaleProductClickListener listener;

    public interface OnFlashSaleProductClickListener {
        void onProductClick(Product product);
        void onAddToCartClick(Product product);
    }

    public FlashSaleProductAdapter() {
        super(new DiffUtil.ItemCallback<Product>() {
            @Override
            public boolean areItemsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
                return oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
                return oldItem.getName().equals(newItem.getName()) &&
                        oldItem.getPriceValue() == newItem.getPriceValue() &&
                        (oldItem.getCompareAtPrice() == null ? newItem.getCompareAtPrice() == null : oldItem.getCompareAtPrice().equals(newItem.getCompareAtPrice()));
            }
        });
    }

    public void setOnFlashSaleProductClickListener(OnFlashSaleProductClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_card, parent, false);
        // Set fixed width for horizontal scroll
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = (int) (160 * parent.getContext().getResources().getDisplayMetrics().density);
        view.setLayoutParams(params);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = getItem(position);
        if (product == null) return;

        // Force SALE badge
        holder.layoutProductStatusBadge.setVisibility(View.VISIBLE);
        holder.tvProductBadge.setText(holder.itemView.getContext().getString(R.string.badge_sale));
        holder.layoutProductStatusBadge.setBackgroundTintList(
                ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.button)
        );
        holder.tvProductBadge.setTextColor(
                ContextCompat.getColor(holder.itemView.getContext(), R.color.white)
        );

        // Brand & Name
        holder.tvProductBrand.setText(product.getBrand());
        holder.tvProductName.setText(product.getName());

        // Image
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(holder.ivProductImage.getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_product)
                    .error(R.drawable.ic_product)
                    .into(holder.ivProductImage);
        } else {
            holder.ivProductImage.setImageResource(R.drawable.ic_product);
        }

        // Rating
        try {
            float rating = Float.parseFloat(product.getRating());
            holder.tvProductRating.setRating(rating);
        } catch (Exception e) {
            holder.tvProductRating.setRating(0);
        }
        holder.tvProductReviewCount.setText("(" + product.getReviewCount() + ")");

        // Price
        holder.tvProductPrice.setText(product.getPrice());

        // Original Price (Compare At Price)
        Double compareAtPrice = product.getCompareAtPrice();
        if (compareAtPrice != null && compareAtPrice > product.getPriceValue()) {
            holder.tvProductOriginalPrice.setVisibility(View.VISIBLE);
            String formattedCompare = String.format(java.util.Locale.US, "%,.0fđ", compareAtPrice).replace(",", ".");
            holder.tvProductOriginalPrice.setText(formattedCompare);
            holder.tvProductOriginalPrice.setPaintFlags(holder.tvProductOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.tvProductOriginalPrice.setVisibility(View.GONE);
        }

        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onProductClick(product);
        });

        holder.btnAddToCart.setOnClickListener(v -> {
            if (listener != null) listener.onAddToCartClick(product);
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutProductStatusBadge;
        TextView tvProductBadge;
        ImageView ivProductImage;
        TextView tvProductBrand;
        TextView tvProductName;
        RatingBar tvProductRating;
        TextView tvProductReviewCount;
        TextView tvProductPrice;
        TextView tvProductOriginalPrice;
        ImageButton btnAddToCart;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutProductStatusBadge = itemView.findViewById(R.id.layoutProductStatusBadge);
            tvProductBadge = itemView.findViewById(R.id.tvProductBadge);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductBrand = itemView.findViewById(R.id.tvProductBrand);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductRating = itemView.findViewById(R.id.tvProductRating);
            tvProductReviewCount = itemView.findViewById(R.id.tvProductReviewCount);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductOriginalPrice = itemView.findViewById(R.id.tvProductOriginalPrice);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}
