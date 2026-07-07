package com.example.frontend.ui.category;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.example.frontend.data.remote.UrlUtils;
import com.example.frontend.model.HomeBannerItem;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class CategoryBannerAdapter extends RecyclerView.Adapter<CategoryBannerAdapter.BannerViewHolder> {

    private List<HomeBannerItem> items = new ArrayList<>();
    private OnBannerClickListener listener;

    public interface OnBannerClickListener {
        void onBannerClick(HomeBannerItem item);
    }

    public void setItems(List<HomeBannerItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setOnBannerClickListener(OnBannerClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        if (items.isEmpty()) return;
        int realPosition = position % items.size();
        holder.bind(items.get(realPosition), realPosition + 1, items.size());
    }

    @Override
    public int getItemCount() {
        return items.isEmpty() ? 0 : Integer.MAX_VALUE;
    }

    class BannerViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivBackground;
        private final MaterialButton btnCTA;
        private final TextView tvCounter;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBackground = itemView.findViewById(R.id.ivBannerBackground);
            btnCTA = itemView.findViewById(R.id.btnCTA);
            tvCounter = itemView.findViewById(R.id.tvCounter);
            
            // For category slider, we might want to remove horizontal margin if it's already in the parent padding
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) itemView.getLayoutParams();
            if (params != null) {
                params.leftMargin = 0;
                params.rightMargin = 0;
                itemView.setLayoutParams(params);
            }
        }

        public void bind(HomeBannerItem item, int currentPos, int total) {
            btnCTA.setText(item.getButtonText());
            tvCounter.setText(currentPos + "/" + total);

            if (item.getBackgroundImageUrl() != null && !item.getBackgroundImageUrl().isEmpty()) {
                Glide.with(ivBackground.getContext())
                        .load(UrlUtils.getFullUrl(item.getBackgroundImageUrl()))
                        .placeholder(item.getBackgroundDrawableRes())
                        .error(item.getBackgroundDrawableRes())
                        .into(ivBackground);
            } else {
                ivBackground.setImageResource(item.getBackgroundDrawableRes());
            }

            View.OnClickListener clickListener = v -> {
                if (listener != null) {
                    listener.onBannerClick(item);
                }
            };

            itemView.setOnClickListener(clickListener);
            btnCTA.setOnClickListener(clickListener);
        }
    }
}
