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
import com.example.frontend.model.Brand;
import java.util.ArrayList;
import java.util.List;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.BrandViewHolder> {

    private List<Brand> brandList;
    private OnBrandClickListener listener;

    public interface OnBrandClickListener {
        void onBrandClick(Brand brand);
    }

    public void setOnBrandClickListener(OnBrandClickListener listener) {
        this.listener = listener;
    }

    public BrandAdapter(List<Brand> brandList) {
        this.brandList = brandList != null ? new ArrayList<>(brandList) : new ArrayList<>();
    }

    public void updateData(List<Brand> newList) {
        this.brandList = newList != null ? new ArrayList<>(newList) : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BrandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_brand_card, parent, false);
        return new BrandViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BrandViewHolder holder, int position) {
        Brand brand = brandList.get(position);
        
        if (brand.getLogoUrl() != null && !brand.getLogoUrl().isEmpty()) {
            Glide.with(holder.ivBrandLogo.getContext())
                    .load(brand.getLogoUrl())
                    .placeholder(R.drawable.bg_circle)
                    .error(R.drawable.bg_circle)
                    .into(holder.ivBrandLogo);
        } else {
            holder.ivBrandLogo.setImageResource(R.drawable.bg_circle); // Reusing bg_circle as placeholder
        }

        holder.tvBrandName.setText(brand.getBrandName());
        holder.btnBrandFavorite.setSelected(brand.isFavorite());

        holder.btnBrandFavorite.setOnClickListener(v -> {
            boolean newState = !brand.isFavorite();
            brand.setFavorite(newState);
            holder.btnBrandFavorite.setSelected(newState);
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBrandClick(brand);
            }
        });
    }

    @Override
    public int getItemCount() {
        return brandList != null ? brandList.size() : 0;
    }

    static class BrandViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBrandLogo;
        ImageButton btnBrandFavorite;
        TextView tvBrandName;

        public BrandViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBrandLogo = itemView.findViewById(R.id.ivBrandLogo);
            btnBrandFavorite = itemView.findViewById(R.id.btnBrandFavorite);
            tvBrandName = itemView.findViewById(R.id.tvBrandName);
        }
    }
}
