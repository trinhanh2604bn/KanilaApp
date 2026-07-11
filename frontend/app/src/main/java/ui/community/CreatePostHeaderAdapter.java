package ui.community;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;

public class CreatePostHeaderAdapter extends RecyclerView.Adapter<CreatePostHeaderAdapter.HeaderViewHolder> {

    private final View.OnClickListener listener;

    public CreatePostHeaderAdapter(View.OnClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public HeaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_community_create_post_header, parent, false);
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeaderViewHolder holder, int position) {
        holder.itemView.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
