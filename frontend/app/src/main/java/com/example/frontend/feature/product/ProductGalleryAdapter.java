package com.example.frontend.feature.product;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.example.frontend.data.model.product.ProductMediaDto;
import com.example.frontend.data.remote.UrlUtils;
import java.util.ArrayList;
import java.util.List;

public class ProductGalleryAdapter extends RecyclerView.Adapter<ProductGalleryAdapter.ViewHolder> {
    private List<ProductMediaDto> mediaList = new ArrayList<>();

    public void setMediaList(List<ProductMediaDto> mediaList) {
        this.mediaList = mediaList;
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
                .load(UrlUtils.getFullUrl(media.getUrl()))
                .placeholder(R.drawable.ic_product)
                .error(R.drawable.ic_product)
                .into(holder.ivImage);
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivProductSliderImage);
        }
    }
}
