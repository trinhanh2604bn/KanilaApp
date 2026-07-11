package com.example.frontend.ui.category;

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
import com.example.frontend.model.Product;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class FlashSalePageProductAdapter extends RecyclerView.Adapter<FlashSalePageProductAdapter.ViewHolder> {

    private List<Product> products = new ArrayList<>();
    private OnFlashSaleActionListener listener;

    public interface OnFlashSaleActionListener {
        void onProductClick(Product product);
        void onBuyNowClick(Product product);
    }

    public void setOnFlashSaleActionListener(OnFlashSaleActionListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Product> newProducts) {
        products = newProducts != null ? new ArrayList<>(newProducts) : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_flash_sale_page_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        if (product == null) return;

        // Badge
        holder.tvFlashProductBadge.setVisibility(View.VISIBLE);
        holder.tvFlashProductBadge.setText(holder.itemView.getContext().getString(R.string.badge_sale));

        // Image
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(holder.ivFlashProductImage.getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_product)
                    .error(R.drawable.ic_product)
                    .into(holder.ivFlashProductImage);
        } else {
            holder.ivFlashProductImage.setImageResource(R.drawable.ic_product);
        }

        // Name
        holder.tvFlashProductName.setText(product.getName());

        // Rating
        try {
            float rating = Float.parseFloat(product.getRating());
            holder.ratingFlashProduct.setRating(rating);
        } catch (Exception e) {
            holder.ratingFlashProduct.setRating(0);
        }
        holder.tvFlashProductReviewCount.setText("(" + product.getReviewCount() + ")");

        // Price
        holder.tvFlashProductPrice.setText(product.getPrice());

        // Original price & Discount
        Double compareAtPrice = product.getCompareAtPrice();
        double price = product.getPriceValue();

        if (compareAtPrice != null && compareAtPrice > price) {
            holder.tvFlashProductOriginalPrice.setVisibility(View.VISIBLE);
            String formattedCompare = String.format(java.util.Locale.US, "%,.0fđ", compareAtPrice).replace(",", ".");
            holder.tvFlashProductOriginalPrice.setText(formattedCompare);
            holder.tvFlashProductOriginalPrice.setPaintFlags(holder.tvFlashProductOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            int percent = (int) Math.round((compareAtPrice - price) * 100 / compareAtPrice);
            holder.tvFlashProductDiscountPercent.setVisibility(View.VISIBLE);
            holder.tvFlashProductDiscountPercent.setText("-" + percent + "%");
        } else {
            holder.tvFlashProductOriginalPrice.setVisibility(View.GONE);
            holder.tvFlashProductDiscountPercent.setVisibility(View.GONE);
        }

        // Sold badge
        if (product.getBought() > 0) {
            holder.tvFlashProductSoldBadge.setText(holder.itemView.getContext().getString(R.string.badge_trending));
        } else {
            holder.tvFlashProductSoldBadge.setText(holder.itemView.getContext().getString(R.string.flash_sale_title));
        }

        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onProductClick(product);
        });

        holder.btnFlashBuyNow.setOnClickListener(v -> {
            if (listener != null) listener.onBuyNowClick(product);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFlashProductImage;
        TextView tvFlashProductBadge;
        TextView tvFlashProductName;
        RatingBar ratingFlashProduct;
        TextView tvFlashProductReviewCount;
        TextView tvFlashProductOriginalPrice;
        TextView tvFlashProductDiscountPercent;
        TextView tvFlashProductPrice;
        TextView tvFlashProductSoldBadge;
        MaterialButton btnFlashBuyNow;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFlashProductImage = itemView.findViewById(R.id.ivFlashProductImage);
            tvFlashProductBadge = itemView.findViewById(R.id.tvFlashProductBadge);
            tvFlashProductName = itemView.findViewById(R.id.tvFlashProductName);
            ratingFlashProduct = itemView.findViewById(R.id.ratingFlashProduct);
            tvFlashProductReviewCount = itemView.findViewById(R.id.tvFlashProductReviewCount);
            tvFlashProductOriginalPrice = itemView.findViewById(R.id.tvFlashProductOriginalPrice);
            tvFlashProductDiscountPercent = itemView.findViewById(R.id.tvFlashProductDiscountPercent);
            tvFlashProductPrice = itemView.findViewById(R.id.tvFlashProductPrice);
            tvFlashProductSoldBadge = itemView.findViewById(R.id.tvFlashProductSoldBadge);
            btnFlashBuyNow = itemView.findViewById(R.id.btnFlashBuyNow);
        }
    }
}
