package ui.community;

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

public class ProductThumbnailAdapter extends RecyclerView.Adapter<ProductThumbnailAdapter.ViewHolder> {
    private List<String> imageUrls = new ArrayList<>();

    public void setImageUrls(List<String> urls) {
        this.imageUrls = urls;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_thumbnail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String url = imageUrls.get(position);
        Glide.with(holder.itemView.getContext()).load(url).placeholder(R.drawable.ic_product).into(holder.ivThumbnail);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        ViewHolder(View view) {
            super(view);
            ivThumbnail = view.findViewById(R.id.ivProductThumbnail);
        }
    }
}
