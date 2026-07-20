package com.example.frontend.feature.product.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.example.frontend.data.model.review.ReviewMediaDto;
import com.example.frontend.utils.UrlUtils;
import java.util.ArrayList;
import java.util.List;

public class ReviewMediaAdapter extends RecyclerView.Adapter<ReviewMediaAdapter.ViewHolder> {
    private List<ReviewMediaDto> mediaList = new ArrayList<>();

    public void setMediaList(List<ReviewMediaDto> mediaList) {
        this.mediaList = mediaList != null ? mediaList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setMediaUrls(List<String> mediaUrls) {
        List<ReviewMediaDto> list = new ArrayList<>();
        if (mediaUrls != null) {
            for (String url : mediaUrls) {
                list.add(new ReviewMediaDto(url, "image"));
            }
        }
        setMediaList(list);
    }

    public void submitList(List<ReviewMediaDto> mediaList) {
        setMediaList(mediaList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thumbnail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReviewMediaDto media = mediaList.get(position);
        String url = UrlUtils.getFullUrl(media.getMediaUrl());
        
        Glide.with(holder.ivImage.getContext())
                .load(url)
                .placeholder(R.drawable.bg_skeleton_placeholder)
                .error(R.drawable.ic_product)
                .into(holder.ivImage);

        if (media.isVideo()) {
            holder.ivPlay.setVisibility(View.VISIBLE);
        } else {
            holder.ivPlay.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage, ivPlay;
        ViewHolder(View view) {
            super(view);
            ivImage = view.findViewById(R.id.ivThumbnail);
            ivPlay = view.findViewById(R.id.ivPlayIcon);
        }
    }
}
