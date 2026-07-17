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

    private final List<String> historyList = new ArrayList<>();
    private OnHistoryClickListener clickListener;
    private OnDeleteClickListener deleteClickListener;

    public interface OnHistoryClickListener {
        void onHistoryClick(String query);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(String query);
    }

    public void setOnHistoryClickListener(OnHistoryClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    public void setItems(List<String> newItems) {
        historyList.clear();
        if (newItems != null) {
            historyList.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_history_chip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String query = historyList.get(position);
        holder.tvHistoryKeyword.setText(query);
        
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onHistoryClick(query);
            }
        });
        
        // Long click to delete history item
        holder.itemView.setOnLongClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(query);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHistoryKeyword;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHistoryKeyword = itemView.findViewById(R.id.tvHistoryKeyword);
        }
    }
}
