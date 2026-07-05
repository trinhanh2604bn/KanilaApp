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

public class SearchSuggestionAdapter extends RecyclerView.Adapter<SearchSuggestionAdapter.ViewHolder> {

    private List<String> suggestionItems = new ArrayList<>();
    private OnSuggestionClickListener listener;

    public interface OnSuggestionClickListener {
        void onSuggestionClick(String keyword);
    }

    public void setOnSuggestionClickListener(OnSuggestionClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<String> items) {
        this.suggestionItems = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_suggestion_chip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String keyword = suggestionItems.get(position);
        holder.tvSuggestionKeyword.setText(keyword);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onSuggestionClick(keyword);
        });
    }

    @Override
    public int getItemCount() {
        return suggestionItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSuggestionKeyword;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSuggestionKeyword = itemView.findViewById(R.id.tvSuggestionKeyword);
        }
    }
}
