package ui.order;

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

public class ReturnMediaAdapter extends RecyclerView.Adapter<ReturnMediaAdapter.ViewHolder> {

    private List<String> mediaUrls = new ArrayList<>();

    public void setMediaUrls(List<String> urls) {
        this.mediaUrls = urls != null ? urls : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_media, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mediaUrls.get(position));
    }

    @Override
    public int getItemCount() {
        return mediaUrls.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            // Hide remove button as this is view only
            View btnRemove = itemView.findViewById(R.id.btnRemove);
            if (btnRemove != null) btnRemove.setVisibility(View.GONE);
        }

        public void bind(String url) {
            Glide.with(itemView.getContext())
                    .load(url)
                    .placeholder(R.drawable.ic_product)
                    .error(R.drawable.ic_product)
                    .into(ivThumbnail);
        }
    }
}
