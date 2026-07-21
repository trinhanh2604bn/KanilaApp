package com.example.frontend.feature.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;

import java.util.ArrayList;
import java.util.List;

public class SearchQuickDiscoveryAdapter extends RecyclerView.Adapter<SearchQuickDiscoveryAdapter.ViewHolder> {

    public static class DiscoveryItem {
        private final String title;
        private final int imageResId;
        private final String categoryId;

        public DiscoveryItem(String title, int imageResId, String categoryId) {
            this.title = title;
            this.imageResId = imageResId;
            this.categoryId = categoryId;
        }

        public String getTitle() { return title; }
        public int getImageResId() { return imageResId; }
        public String getCategoryId() { return categoryId; }
    }

    private final List<DiscoveryItem> items = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(DiscoveryItem item);
    }

    public SearchQuickDiscoveryAdapter() {
        // Mock default discovery items with category codes from system
        items.add(new DiscoveryItem("Son môi", R.drawable.bg_circle, "LIPS")); 
        items.add(new DiscoveryItem("Kem nền", R.drawable.bg_circle, "FACE"));
        items.add(new DiscoveryItem("Phấn phủ", R.drawable.bg_circle, "FACE"));
        items.add(new DiscoveryItem("Kẻ mắt", R.drawable.bg_circle, "EYES"));
        items.add(new DiscoveryItem("Xịt khoáng", R.drawable.bg_circle, "FACE"));
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_quick_discovery, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DiscoveryItem item = items.get(position);
        holder.tvDiscoveryTitle.setText(item.getTitle());
        holder.ivDiscoveryImage.setImageResource(item.getImageResId());
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDiscoveryImage;
        TextView tvDiscoveryTitle;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDiscoveryImage = itemView.findViewById(R.id.ivDiscoveryImage);
            tvDiscoveryTitle = itemView.findViewById(R.id.tvDiscoveryTitle);
        }
    }
}
