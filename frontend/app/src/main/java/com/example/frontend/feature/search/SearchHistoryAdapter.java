package com.example.frontend.feature.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;

import java.util.ArrayList;
import java.util.List;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder> {

    private List<String> historyItems = new ArrayList<>();
    private OnHistoryClickListener listener;

    public interface OnHistoryClickListener {
        void onHistoryClick(String keyword);
    }

    public void setOnHistoryClickListener(OnHistoryClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<String> items) {
        this.historyItems = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_history_chip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String keyword = historyItems.get(position);
        holder.tvHistoryKeyword.setText(keyword);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onHistoryClick(keyword);
        });
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHistoryKeyword;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHistoryKeyword = itemView.findViewById(R.id.tvHistoryKeyword);
        }
    }
}
