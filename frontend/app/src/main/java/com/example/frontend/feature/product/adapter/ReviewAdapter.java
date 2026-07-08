package com.example.frontend.feature.product.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.RatingBar;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.example.frontend.R;
import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private List<Object> reviews = new ArrayList<>(); // Use real Review DTO if available

    public void setReviews(List<Object> reviews) {
        this.reviews = reviews != null ? reviews : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Placeholder binding
        holder.tvUserName.setText("Kim Trân");
        holder.tvContent.setText("Màu son lên chuẩn, chất son nhẹ môi, bám khá tốt và giúp gương mặt trông tươi tắn.");
        holder.rbStars.setRating(5f);
        
        Glide.with(holder.ivAvatar.getContext())
                .load(R.drawable.bg_avatar_circle)
                .into(holder.ivAvatar);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvContent;
        RatingBar rbStars;
        ImageView ivAvatar;
        
        ViewHolder(View view) {
            super(view);
            tvUserName = view.findViewById(R.id.tvReviewUserName);
            tvContent = view.findViewById(R.id.tvReviewContent);
            rbStars = view.findViewById(R.id.rbReviewStars);
            ivAvatar = view.findViewById(R.id.ivReviewAvatar);
        }
    }
}
