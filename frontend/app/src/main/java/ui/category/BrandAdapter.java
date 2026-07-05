package ui.category;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.example.frontend.model.Brand;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter dùng để hiển thị danh sách thương hiệu động (Dynamic List).
 */
public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.BrandViewHolder> {

    private List<Brand> brandList;

    public BrandAdapter(List<Brand> brandList) {
        this.brandList = new ArrayList<>(brandList);
    }

    public void updateData(List<Brand> newList) {
        this.brandList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BrandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_brand_card, parent, false);
        return new BrandViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BrandViewHolder holder, int position) {
        Brand brand = brandList.get(position);
        holder.ivBrandLogo.setImageResource(brand.getLogoRes());
        holder.tvBrandName.setText(brand.getName());
        holder.btnBrandFavorite.setSelected(brand.isFavorite());

        holder.btnBrandFavorite.setOnClickListener(v -> {
            boolean newState = !brand.isFavorite();
            brand.setFavorite(newState);
            holder.btnBrandFavorite.setSelected(newState);
        });
    }

    @Override
    public int getItemCount() {
        return brandList != null ? brandList.size() : 0;
    }

    static class BrandViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBrandLogo;
        ImageButton btnBrandFavorite;
        TextView tvBrandName;

        public BrandViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBrandLogo = itemView.findViewById(R.id.ivBrandLogo);
            btnBrandFavorite = itemView.findViewById(R.id.btnBrandFavorite);
            tvBrandName = itemView.findViewById(R.id.tvBrandName);
        }
    }
}
