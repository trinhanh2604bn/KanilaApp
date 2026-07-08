package com.example.frontend.feature.product.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.example.frontend.data.model.product.ProductMediaDto;
import java.util.ArrayList;
import java.util.List;

public class ProductImageAdapter extends RecyclerView.Adapter<ProductImageAdapter.ViewHolder> {
    private List<ProductMediaDto> mediaList = new ArrayList<>();

    public void setMediaList(List<ProductMediaDto> mediaList) {
        this.mediaList = mediaList != null ? mediaList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_image_slider, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductMediaDto media = mediaList.get(position);
        Glide.with(holder.ivImage.getContext())
                .load(media.getUrl())
                .placeholder(R.drawable.ic_product)
                .into(holder.ivImage);
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        ViewHolder(View view) {
            super(view);
            ivImage = view.findViewById(R.id.ivProductSliderImage);
        }
    }
}
