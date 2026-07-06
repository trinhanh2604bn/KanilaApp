package ui.commerce;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.ShippingMethod;

import java.util.ArrayList;
import java.util.List;

public class ShippingMethodAdapter extends RecyclerView.Adapter<ShippingMethodAdapter.ShippingViewHolder> {

    private List<ShippingMethod> shippingMethods = new ArrayList<>();
    private OnShippingMethodSelectedListener listener;

    public interface OnShippingMethodSelectedListener {
        void onShippingMethodSelected(ShippingMethod method);
    }

    public ShippingMethodAdapter(OnShippingMethodSelectedListener listener) {
        this.listener = listener;
    }

    public void setShippingMethods(List<ShippingMethod> methods) {
        this.shippingMethods = methods;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShippingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shipping_method, parent, false);
        return new ShippingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShippingViewHolder holder, int position) {
        ShippingMethod method = shippingMethods.get(position);
        holder.bind(method);
    }

    @Override
    public int getItemCount() {
        return shippingMethods.size();
    }

    class ShippingViewHolder extends RecyclerView.ViewHolder {
        private final View root;
        private final ImageView ivRadio;
        private final ImageView ivIcon;
        private final TextView tvName;
        private final TextView tvTag;
        private final TextView tvEstimate;
        private final TextView tvDescription;
        private final TextView tvPrice;

        public ShippingViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.layoutShippingMethodRoot);
            ivRadio = itemView.findViewById(R.id.ivShippingMethodRadio);
            ivIcon = itemView.findViewById(R.id.ivShippingMethodIcon);
            tvName = itemView.findViewById(R.id.tvShippingMethodName);
            tvTag = itemView.findViewById(R.id.tvShippingMethodTag);
            tvEstimate = itemView.findViewById(R.id.tvShippingMethodEstimate);
            tvDescription = itemView.findViewById(R.id.tvShippingMethodDescription);
            tvPrice = itemView.findViewById(R.id.tvShippingMethodPrice);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    selectItem(position);
                }
            });
        }

        public void bind(ShippingMethod method) {
            tvName.setText(method.getName());
            tvEstimate.setText(method.getEstimate());
            tvDescription.setText(method.getDescription());
            tvPrice.setText(method.getPrice());
            ivIcon.setImageResource(method.getIconRes());

            if (tvTag != null) {
                if (method.getTag() != null) {
                    tvTag.setText(method.getTag());
                    tvTag.setVisibility(View.VISIBLE);
                } else {
                    tvTag.setVisibility(View.GONE);
                }
            }

            root.setSelected(method.isSelected());
            ivRadio.setSelected(method.isSelected());
        }

        private void selectItem(int position) {
            for (int i = 0; i < shippingMethods.size(); i++) {
                shippingMethods.get(i).setSelected(i == position);
            }
            notifyDataSetChanged();
            if (listener != null) {
                listener.onShippingMethodSelected(shippingMethods.get(position));
            }
        }
    }
}
