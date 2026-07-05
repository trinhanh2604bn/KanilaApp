package com.example.frontend.feature.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;

import java.util.ArrayList;
import java.util.List;

public class SearchQuickDiscoveryAdapter extends RecyclerView.Adapter<SearchQuickDiscoveryAdapter.ViewHolder> {

    private List<SearchQuickDiscovery> items = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(SearchQuickDiscovery item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<SearchQuickDiscovery> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_quick_discovery, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchQuickDiscovery item = items.get(position);
        holder.ivDiscoveryImage.setImageResource(item.getImageResource());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDiscoveryImage;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDiscoveryImage = itemView.findViewById(R.id.ivDiscoveryImage);
        }
    }
}
