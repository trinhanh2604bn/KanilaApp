package com.example.frontend.feature.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.HomeShortcutItem;

import java.util.ArrayList;
import java.util.List;

public class HomeShortcutAdapter extends RecyclerView.Adapter<HomeShortcutAdapter.ShortcutViewHolder> {

    private List<HomeShortcutItem> items = new ArrayList<>();
    private OnShortcutClickListener listener;

    public interface OnShortcutClickListener {
        void onShortcutClick(HomeShortcutItem item);
    }

    public void setItems(List<HomeShortcutItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setOnShortcutClickListener(OnShortcutClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ShortcutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_shortcut, parent, false);
        return new ShortcutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShortcutViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ShortcutViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivIcon;
        private final TextView tvTitle;
        private final View viewBadge;

        public ShortcutViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivShortcutIcon);
            tvTitle = itemView.findViewById(R.id.tvShortcutTitle);
            viewBadge = itemView.findViewById(R.id.viewShortcutBadge);
        }

        public void bind(HomeShortcutItem item, OnShortcutClickListener listener) {
            ivIcon.setImageResource(item.getIconRes());
            ivIcon.setContentDescription(item.getTitle());
            tvTitle.setText(item.getTitle());
            viewBadge.setVisibility(item.isShowBadge() ? View.VISIBLE : View.GONE);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onShortcutClick(item);
                }
            });
        }
    }
}
