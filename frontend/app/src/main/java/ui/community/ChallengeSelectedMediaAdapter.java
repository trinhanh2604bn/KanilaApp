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

public class ChallengeSelectedMediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ADD = 1;
    private static final int VIEW_TYPE_MEDIA = 2;

    private List<Uri> mediaUris = new ArrayList<>();
    private OnMediaActionListener listener;

    public interface OnMediaActionListener {
        void onAddClick();
        void onRemoveClick(Uri uri);
    }

    public ChallengeSelectedMediaAdapter(OnMediaActionListener listener) {
        this.listener = listener;
    }

    public void setMediaUris(List<Uri> uris) {
        this.mediaUris = uris;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mediaUris.size()) return VIEW_TYPE_ADD;
        return VIEW_TYPE_MEDIA;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ADD) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_challenge_add_media, parent, false);
            return new AddViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_media, parent, false);
            return new MediaViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AddViewHolder) {
            ((AddViewHolder) holder).bind();
        } else if (holder instanceof MediaViewHolder) {
            ((MediaViewHolder) holder).bind(mediaUris.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mediaUris.size() + 1;
    }

    class AddViewHolder extends RecyclerView.ViewHolder {
        public AddViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        public void bind() {
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onAddClick();
            });
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
                if (listener != null) listener.onRemoveClick(uri);
            });
        }
    }

}
