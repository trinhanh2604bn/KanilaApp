package com.example.frontend.feature.product.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.frontend.R;
import java.util.ArrayList;
import java.util.List;

public class ReviewMediaAdapter extends RecyclerView.Adapter<ReviewMediaAdapter.ViewHolder> {
    private List<String> mediaUrls = new ArrayList<>();

    public void setMediaUrls(List<String> mediaUrls) {
        this.mediaUrls = mediaUrls != null ? mediaUrls : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thumbnail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String url = mediaUrls.get(position);
        Glide.with(holder.ivImage.getContext())
                .load(url)
                .placeholder(R.drawable.bg_skeleton_placeholder)
                .into(holder.ivImage);
        
        // If it's the last item and there are more, show overlay? No, keeping it simple first.
    }

    @Override
    public int getItemCount() {
        return mediaUrls.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        ViewHolder(View view) {
            super(view);
            ivImage = view.findViewById(R.id.ivThumbnail);
        }
    }
}
