package ui.community;

import android.net.Uri;
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

public class MediaGridAdapter extends RecyclerView.Adapter<MediaGridAdapter.MediaViewHolder> {

    private List<MediaItem> mediaItems = new ArrayList<>();
    private final List<MediaItem> selectedItems = new ArrayList<>();
    private OnMediaSelectionListener selectionListener;
    private static final int MAX_SELECTION = 10;

    public interface OnMediaSelectionListener {
        void onSelectionChanged(List<MediaItem> selectedItems);
        void onMaxLimitReached();
    }

    public void setOnMediaSelectionListener(OnMediaSelectionListener listener) {
        this.selectionListener = listener;
    }

    public void setMediaItems(List<MediaItem> items) {
        this.mediaItems = items;
        notifyDataSetChanged();
    }
    
    public void updateSelection(List<Uri> selectedUris) {
        selectedItems.clear();
        for (MediaItem item : mediaItems) {
            boolean isSelected = selectedUris.contains(item.getUri());
            item.setSelected(isSelected);
            if (isSelected) {
                selectedItems.add(item);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media_picker, parent, false);
        return new MediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        holder.bind(mediaItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mediaItems.size();
    }

    class MediaViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        View viewSelectedOverlay;
        ImageView ivSelector;

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            viewSelectedOverlay = itemView.findViewById(R.id.viewSelectedOverlay);
            ivSelector = itemView.findViewById(R.id.ivSelector);
        }

        public void bind(MediaItem item) {
            Glide.with(itemView.getContext())
                .load(item.getUri())
                .thumbnail(0.2f)
                .placeholder(R.drawable.bg_skeleton_placeholder)
                .centerCrop()
                .override(240, 240)
                .into(ivThumbnail);
            
            updateSelectedState(item.isSelected());

            itemView.setOnClickListener(v -> {
                if (item.isSelected()) {
                    item.setSelected(false);
                    selectedItems.remove(item);
                    updateSelectedState(false);
                } else {
                    if (selectedItems.size() >= MAX_SELECTION) {
                        if (selectionListener != null) {
                            selectionListener.onMaxLimitReached();
                        }
                        return;
                    }
                    item.setSelected(true);
                    selectedItems.add(item);
                    updateSelectedState(true);
                }
                
                if (selectionListener != null) {
                    selectionListener.onSelectionChanged(new ArrayList<>(selectedItems));
                }
            });
        }

        private void updateSelectedState(boolean isSelected) {
            viewSelectedOverlay.setVisibility(isSelected ? View.VISIBLE : View.GONE);
            ivSelector.setImageResource(isSelected ? R.drawable.ic_check_circle : R.drawable.bg_circle_outline);
        }
    }
}
