package com.example.frontend.feature.product.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.example.frontend.data.model.product.ProductMediaDto;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;

public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ViewHolder> {
    private List<ProductMediaDto> mediaList = new ArrayList<>();
    private int selectedPosition = 0;
    private OnThumbnailClickListener listener;

    public interface OnThumbnailClickListener {
        void onThumbnailClick(int position);
    }

    public void setMediaList(List<ProductMediaDto> mediaList) {
        this.mediaList = mediaList != null ? mediaList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setSelectedPosition(int position) {
        int oldPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(oldPosition);
        notifyItemChanged(selectedPosition);
    }

    public void setListener(OnThumbnailClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thumbnail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductMediaDto media = mediaList.get(position);
        Glide.with(holder.ivThumbnail.getContext())
                .load(media.getUrl())
                .placeholder(R.drawable.ic_product)
                .into(holder.ivThumbnail);

        if (position == selectedPosition) {
            holder.cardRoot.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.button));
            holder.cardRoot.setStrokeWidth(holder.itemView.getContext().getResources().getDimensionPixelSize(R.dimen.border_hairline) * 2);
        } else {
            holder.cardRoot.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.border_divider));
            holder.cardRoot.setStrokeWidth(holder.itemView.getContext().getResources().getDimensionPixelSize(R.dimen.border_hairline));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onThumbnailClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        MaterialCardView cardRoot;
        ViewHolder(View view) {
            super(view);
            ivThumbnail = view.findViewById(R.id.ivThumbnail);
            cardRoot = view.findViewById(R.id.cardThumbnail);
        }
    }
}
