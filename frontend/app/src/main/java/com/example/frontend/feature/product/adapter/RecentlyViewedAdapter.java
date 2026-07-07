package com.example.frontend.feature.product.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        // Make it a bit more compact for horizontal scroll if needed
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp != null) {
            lp.width = parent.getContext().getResources().getDimensionPixelSize(R.dimen.category_card_product_image_width) + 80;
            view.setLayoutParams(lp);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(product.getPrice());
        
        Glide.with(holder.ivImage.getContext())
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_product)
                .into(holder.ivImage);

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
        TextView tvName, tvPrice;
        ViewHolder(View view) {
            super(view);
            ivImage = view.findViewById(R.id.ivProductImage);
            tvName = view.findViewById(R.id.tvProductName);
            tvPrice = view.findViewById(R.id.tvProductPrice);
            // Hide some fields to make it compact
            if (view.findViewById(R.id.tvProductBrand) != null) view.findViewById(R.id.tvProductBrand).setVisibility(View.GONE);
            if (view.findViewById(R.id.layoutRating) != null) view.findViewById(R.id.layoutRating).setVisibility(View.GONE);
            if (view.findViewById(R.id.btnWishlist) != null) view.findViewById(R.id.btnWishlist).setVisibility(View.GONE);
            if (view.findViewById(R.id.btnAddToCart) != null) view.findViewById(R.id.btnAddToCart).setVisibility(View.GONE);
            if (view.findViewById(R.id.layoutProductStatusBadge) != null) view.findViewById(R.id.layoutProductStatusBadge).setVisibility(View.GONE);
        }
    }
}
