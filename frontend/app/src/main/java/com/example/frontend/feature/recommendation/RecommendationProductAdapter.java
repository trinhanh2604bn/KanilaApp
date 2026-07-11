package com.example.frontend.feature.recommendation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.example.frontend.data.model.recommendation.RecommendedProduct;
import com.example.frontend.model.Product;
import java.util.ArrayList;
import java.util.List;

public class RecommendationProductAdapter extends RecyclerView.Adapter<RecommendationProductAdapter.ViewHolder> {
    private List<RecommendedProduct> items = new ArrayList<>();
    private OnProductClickListener listener;
    private int itemWidth = -1;

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onAddToCartClick(Product product);
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<RecommendedProduct> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setItemWidth(int width) {
        this.itemWidth = width;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommendation_product, parent, false);
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
        RecommendedProduct recommendedProduct = items.get(position);
        Product product = recommendedProduct.getProduct();

        if (product == null) return;

        holder.tvName.setText(product.getName());
        holder.tvBrand.setText(product.getBrand());
        holder.tvPrice.setText(product.getPrice());

        // Score Badge
        String badgeText = "";
        if (recommendedProduct.getBadges() != null && !recommendedProduct.getBadges().isEmpty()) {
            badgeText = recommendedProduct.getBadges().get(0);
        } else {
            badgeText = recommendedProduct.getScore() + "% phù hợp";
        }
        holder.tvMatchScore.setText(badgeText);

        // Reason Chips
        holder.layoutReasonChips.removeAllViews();
        List<String> reasons = recommendedProduct.getReasons();
        if (reasons != null) {
            int limit = Math.min(reasons.size(), 2);
            for (int i = 0; i < limit; i++) {
                addChip(holder.layoutReasonChips, reasons.get(i));
            }
        }

        // Image
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(holder.ivImage.getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_product)
                    .error(R.drawable.ic_product)
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.ic_product);
        }

        holder.btnWishlist.setSelected(product.isFavorite());
        holder.btnWishlist.setOnClickListener(v -> {
            boolean newState = !product.isFavorite();
            product.setFavorite(newState);
            v.setSelected(newState);
        });

        holder.btnAddToCart.setOnClickListener(v -> {
            if (listener != null) listener.onAddToCartClick(product);
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onProductClick(product);
        });
    }

    private void addChip(LinearLayout container, String text) {
        TextView chip = new TextView(container.getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 8, 0);
        chip.setLayoutParams(params);
        chip.setBackgroundResource(R.drawable.bg_chip_pill);
        chip.setPadding(16, 4, 16, 4);
        chip.setTextSize(10f);
        chip.setTextColor(container.getContext().getResources().getColor(R.color.text_main));
        chip.setText(text);
        container.addView(chip);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvBrand, tvPrice, tvMatchScore;
        LinearLayout layoutReasonChips;
        ImageButton btnWishlist, btnAddToCart;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivProductImage);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvBrand = itemView.findViewById(R.id.tvProductBrand);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            tvMatchScore = itemView.findViewById(R.id.tvMatchScore);
            layoutReasonChips = itemView.findViewById(R.id.layoutReasonChips);
            btnWishlist = itemView.findViewById(R.id.btnWishlist);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}
