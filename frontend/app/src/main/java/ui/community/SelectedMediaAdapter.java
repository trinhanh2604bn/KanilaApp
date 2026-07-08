package ui.community;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.frontend.R;
import java.util.ArrayList;
import java.util.List;

public class SelectedMediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_MEDIA = 1;
    private static final int VIEW_TYPE_ADD = 2;

    private List<Uri> mediaUris = new ArrayList<>();
    private OnMediaRemoveListener removeListener;
    private OnAddClickListener addClickListener;

    public interface OnMediaRemoveListener {
        void onRemove(Uri uri);
    }

    public interface OnAddClickListener {
        void onAddClick();
    }

    public void setOnMediaRemoveListener(OnMediaRemoveListener listener) {
        this.removeListener = listener;
    }

    public void setOnAddClickListener(OnAddClickListener listener) {
        this.addClickListener = listener;
    }

    public void setMediaUris(List<Uri> uris) {
        this.mediaUris = uris;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mediaUris.size()) {
            return VIEW_TYPE_MEDIA;
        } else {
            return VIEW_TYPE_ADD;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MEDIA) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_media, parent, false);
            return new MediaViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_media_placeholder, parent, false);
            return new AddViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MediaViewHolder) {
            ((MediaViewHolder) holder).bind(mediaUris.get(position));
        } else if (holder instanceof AddViewHolder) {
            ((AddViewHolder) holder).bind();
        }
    }

    @Override
    public int getItemCount() {
        if (mediaUris.size() >= 10) {
            return mediaUris.size();
        } else {
            return mediaUris.size() + 1;
        }
    }

    class MediaViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        ImageButton btnRemove;

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }

        public void bind(Uri uri) {
            Glide.with(itemView.getContext()).load(uri).into(ivThumbnail);
            btnRemove.setOnClickListener(v -> {
                if (removeListener != null) {
                    removeListener.onRemove(uri);
                }
            });
        }
    }

    class AddViewHolder extends RecyclerView.ViewHolder {
        public AddViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind() {
            itemView.findViewById(R.id.btnAddMedia).setOnClickListener(v -> {
                if (addClickListener != null) {
                    addClickListener.onAddClick();
                }
            });
        }
    }
}
