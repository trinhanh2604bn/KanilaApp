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

    private final List<String> suggestions = new ArrayList<>();
    private OnSuggestionClickListener clickListener;

    public interface OnSuggestionClickListener {
        void onSuggestionClick(String query);
    }

    public void setOnSuggestionClickListener(OnSuggestionClickListener listener) {
        this.clickListener = listener;
    }

    public void setItems(List<String> newItems) {
        suggestions.clear();
        if (newItems != null) {
            suggestions.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_suggestion_chip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String query = suggestions.get(position);
        holder.tvSuggestionKeyword.setText(query);

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onSuggestionClick(query);
            }
        });
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSuggestionKeyword;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSuggestionKeyword = itemView.findViewById(R.id.tvSuggestionKeyword);
        }
    }
}
