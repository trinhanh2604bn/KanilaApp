package ui.community;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.frontend.R;
import java.util.ArrayList;
import java.util.List;

public class SavedContentAdapter extends RecyclerView.Adapter<SavedContentAdapter.ViewHolder> {
    private List<SavedContent> items = new ArrayList<>();
    private final OnSavedItemClickListener listener;

    public interface OnSavedItemClickListener {
        void onItemClick(SavedContent item);
        void onRemoveClick(SavedContent item);
    }

    public SavedContentAdapter(OnSavedItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<SavedContent> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SavedContent item = items.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        TextView tvType, tvTitle, tvMeta;
        ImageButton btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivSavedThumbnail);
            tvType = itemView.findViewById(R.id.tvSavedType);
            tvTitle = itemView.findViewById(R.id.tvSavedTitle);
            tvMeta = itemView.findViewById(R.id.tvSavedMeta);
            btnRemove = itemView.findViewById(R.id.btnRemoveSave);
        }

        void bind(SavedContent item, OnSavedItemClickListener listener) {
            tvType.setText(item.getType());
            tvTitle.setText(item.getTitle());
            tvMeta.setText(item.getAuthorName() + " • " + item.getCreatedAt());
            
            if (item.getThumbnailUrl() != null) {
                Glide.with(itemView.getContext()).load(item.getThumbnailUrl()).placeholder(R.drawable.bg_slide_4).into(ivThumbnail);
            } else {
                ivThumbnail.setImageResource(R.drawable.bg_slide_4);
            }

            itemView.setOnClickListener(v -> listener.onItemClick(item));
            btnRemove.setOnClickListener(v -> listener.onRemoveClick(item));
        }
    }
}
