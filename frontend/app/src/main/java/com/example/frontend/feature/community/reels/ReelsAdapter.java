package com.example.frontend.feature.community.reels;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.databinding.ItemReelBinding;
import com.example.frontend.feature.community.reels.mock.MockReelsDataSource;

import java.util.ArrayList;
import java.util.List;

public class ReelsAdapter extends RecyclerView.Adapter<ReelsAdapter.ReelViewHolder> {

    private final List<MockReelsDataSource.MockReel> items = new ArrayList<>();
    private OnReelActionListener listener;

    public interface OnReelActionListener {
        void onProductPillClick(MockReelsDataSource.MockReel reel);
        void onLikeClick(MockReelsDataSource.MockReel reel);
        void onCommentClick(MockReelsDataSource.MockReel reel);
        void onSaveClick(MockReelsDataSource.MockReel reel);
        void onShareClick(MockReelsDataSource.MockReel reel);
        void onBackClick();
    }

    public void setOnReelActionListener(OnReelActionListener listener) {
        this.listener = listener;
    }

    public void setItems(List<MockReelsDataSource.MockReel> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReelBinding binding = ItemReelBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ReelViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReelViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ReelViewHolder extends RecyclerView.ViewHolder {
        private final ItemReelBinding binding;

        public ReelViewHolder(ItemReelBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(MockReelsDataSource.MockReel reel) {
            binding.tvCreatorUsername.setText(reel.getCreatorUsername());
            binding.tvCaption.setText(reel.getCaption());
            binding.tvHashtags.setText(reel.getHashtagText());
            binding.tvAudioName.setText(reel.getAudioName());
            binding.tvLikeCount.setText(reel.getLikeCountText());
            binding.tvCommentCount.setText(reel.getCommentCountText());
            binding.tvSaveCount.setText(reel.getSaveCountText());
            
            binding.btnProductPill.setVisibility(reel.hasProducts() ? View.VISIBLE : View.GONE);
            binding.tvProductPillCount.setText(reel.getProductPillText());

            updateLikeUI(reel);
            updateSaveUI(reel);

            binding.btnProductPill.setOnClickListener(v -> {
                if (listener != null) listener.onProductPillClick(reel);
            });

            binding.btnLike.setOnClickListener(v -> {
                reel.toggleLiked();
                updateLikeUI(reel);
                if (listener != null) listener.onLikeClick(reel);
            });

            binding.btnSave.setOnClickListener(v -> {
                reel.toggleSaved();
                updateSaveUI(reel);
                if (listener != null) listener.onSaveClick(reel);
            });

            binding.btnComment.setOnClickListener(v -> {
                if (listener != null) listener.onCommentClick(reel);
            });

            binding.btnShare.setOnClickListener(v -> {
                if (listener != null) listener.onShareClick(reel);
            });

            binding.btnBack.setOnClickListener(v -> {
                if (listener != null) listener.onBackClick();
            });

            // Initialize VideoView
            binding.videoView.setVideoURI(Uri.parse(reel.getVideoUrl()));
            binding.videoView.setOnPreparedListener(mp -> {
                mp.setLooping(true);
                // Thumbnail will be hidden when video starts playing
            });
            binding.videoView.setOnErrorListener((mp, what, extra) -> {
                binding.ivThumbnail.setVisibility(View.VISIBLE);
                return true; // Handle error to prevent system dialog
            });
        }

        private void updateLikeUI(MockReelsDataSource.MockReel reel) {
            binding.btnLike.setImageResource(reel.isLiked() ? R.drawable.ic_heart_filled : R.drawable.ic_heart);
            binding.btnLike.setColorFilter(reel.isLiked() ? 
                binding.getRoot().getContext().getColor(R.color.button) : 
                binding.getRoot().getContext().getColor(R.color.white));
        }

        private void updateSaveUI(MockReelsDataSource.MockReel reel) {
            binding.btnSave.setImageResource(reel.isSaved() ? R.drawable.ic_bookmark : R.drawable.ic_bookmark_outline);
            binding.btnSave.setColorFilter(reel.isSaved() ? 
                binding.getRoot().getContext().getColor(R.color.button) : 
                binding.getRoot().getContext().getColor(R.color.white));
        }
    }
}
