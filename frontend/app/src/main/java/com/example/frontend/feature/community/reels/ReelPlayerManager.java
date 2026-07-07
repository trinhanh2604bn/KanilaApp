package com.example.frontend.feature.community.reels;

import android.view.View;
import android.widget.VideoView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.frontend.databinding.ItemReelBinding;

public class ReelPlayerManager {

    private final ViewPager2 viewPager;
    private VideoView currentVideoView;
    private ItemReelBinding currentBinding;

    public ReelPlayerManager(ViewPager2 viewPager) {
        this.viewPager = viewPager;
    }

    public void playAtPosition(int position) {
        stopCurrent();

        RecyclerView recyclerView = (RecyclerView) viewPager.getChildAt(0);
        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(position);

        if (holder instanceof ReelsAdapter.ReelViewHolder) {
            View itemView = holder.itemView;
            currentBinding = ItemReelBinding.bind(itemView);
            currentVideoView = currentBinding.videoView;
            
            currentVideoView.setOnInfoListener((mp, what, extra) -> {
                if (what == android.media.MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    if (currentBinding != null) {
                        currentBinding.ivThumbnail.setVisibility(View.GONE);
                    }
                }
                return false;
            });
            
            currentVideoView.start();
        }
    }

    public void pause() {
        if (currentVideoView != null && currentVideoView.isPlaying()) {
            currentVideoView.pause();
        }
    }

    public void resume() {
        if (currentVideoView != null) {
            currentVideoView.start();
            if (currentBinding != null) {
                currentBinding.ivThumbnail.setVisibility(View.GONE);
            }
        }
    }

    public void stopCurrent() {
        if (currentVideoView != null) {
            currentVideoView.stopPlayback();
            currentVideoView = null;
        }
        if (currentBinding != null) {
            currentBinding.ivThumbnail.setVisibility(View.VISIBLE);
            currentBinding = null;
        }
    }

    public void release() {
        stopCurrent();
    }
}
