package ui.commerce;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.example.frontend.data.model.address.AddressDto;
import java.util.ArrayList;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {
    private List<AddressDto> addressList = new ArrayList<>();
    private final OnAddressClickListener listener;

    public interface OnAddressClickListener {
        void onAddressClick(AddressDto address, int position);
        void onEditClick(AddressDto address);
        void onDeleteClick(AddressDto address);
    }

    public AddressAdapter(List<AddressDto> addressList, OnAddressClickListener listener) {
        if (addressList != null) this.addressList = addressList;
        this.listener = listener;
    }

    public void setAddresses(List<AddressDto> addresses) {
        this.addressList = addresses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkout_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AddressDto address = addressList.get(position);
        holder.tvName.setText(address.getFullName());
        holder.tvPhone.setText(address.getPhone());
        holder.tvDetail.setText(address.getAddressLine());
        
        // Handle selection indicator if needed
        holder.layoutItem.setSelected(address.isDefaultShipping());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onAddressClick(address, position);
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(address);
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvDetail;
        ImageView btnEdit;
        View layoutItem;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutItem = itemView.findViewById(R.id.layoutAddressItemRoot);
            tvName = itemView.findViewById(R.id.tvAddressName);
            tvPhone = itemView.findViewById(R.id.tvAddressPhone);
            tvDetail = itemView.findViewById(R.id.tvAddressDetail);
            btnEdit = itemView.findViewById(R.id.btnAddressEdit);
        }
    }
}
