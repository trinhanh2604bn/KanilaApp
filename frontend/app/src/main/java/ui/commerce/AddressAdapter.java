package ui.commerce;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private List<Address> addressList;
    private int selectedPosition = -1;
    private OnAddressClickListener listener;

    public interface OnAddressClickListener {
        void onAddressClick(Address address, int position);
        void onEditClick(Address address);
    }

    public AddressAdapter(List<Address> addressList, OnAddressClickListener listener) {
        this.addressList = addressList;
        this.listener = listener;
        
        // Initialize selectedPosition. 
        // 1. Check for explicitly selected address
        for (int i = 0; i < addressList.size(); i++) {
            if (addressList.get(i).isSelected()) {
                selectedPosition = i;
                break;
            }
        }
        
        // 2. Fallback to default address if none is selected
        if (selectedPosition == -1) {
            for (int i = 0; i < addressList.size(); i++) {
                if (addressList.get(i).isDefault()) {
                    selectedPosition = i;
                    break;
                }
            }
        }
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkout_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = addressList.get(position);
        holder.bind(address, position == selectedPosition);
    }

    @Override
    public int getItemCount() {
        return addressList != null ? addressList.size() : 0;
    }

    class AddressViewHolder extends RecyclerView.ViewHolder {
        View root;
        ImageView ivRadio;
        TextView tvName;
        TextView tvPhone;
        TextView tvDetail;
        TextView tvDefaultText;
        View layoutDefaultTag;
        View btnEdit;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.layoutAddressItemRoot);
            ivRadio = itemView.findViewById(R.id.ivAddressRadio);
            tvName = itemView.findViewById(R.id.tvAddressName);
            tvPhone = itemView.findViewById(R.id.tvAddressPhone);
            tvDetail = itemView.findViewById(R.id.tvAddressDetail);
            tvDefaultText = itemView.findViewById(R.id.tvAddressDefaultText);
            layoutDefaultTag = itemView.findViewById(R.id.layoutAddressDefaultTag);
            btnEdit = itemView.findViewById(R.id.btnAddressEdit);
        }

        public void bind(Address address, boolean isSelected) {
            tvName.setText(address.getName());
            tvPhone.setText(address.getPhone());
            tvDetail.setText(address.getDetail());

            if (address.isDefault()) {
                layoutDefaultTag.setVisibility(View.VISIBLE);
                if (tvDefaultText != null) {
                    tvDefaultText.setText("Mặc định");
                }
            } else {
                layoutDefaultTag.setVisibility(View.GONE);
            }
            
            // Selected state controls card background and radio indicator via selectors
            root.setSelected(isSelected);
            ivRadio.setSelected(isSelected);

            root.setOnClickListener(v -> {
                int adapterPos = getAdapterPosition();
                if (adapterPos != RecyclerView.NO_POSITION && adapterPos != selectedPosition) {
                    int oldPosition = selectedPosition;
                    selectedPosition = adapterPos;
                    if (oldPosition != -1) notifyItemChanged(oldPosition);
                    notifyItemChanged(selectedPosition);
                    
                    if (listener != null) {
                        listener.onAddressClick(address, selectedPosition);
                    }
                }
            });

            // Edit icon click must not trigger item selection
            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(address);
                }
            });
        }
    }

    /**
     * Data model for address selection
     */
    public static class Address {
        private String name;
        private String phone;
        private String detail;
        private boolean isDefault;
        private boolean isSelected;

        public Address(String name, String phone, String detail, boolean isDefault, boolean isSelected) {
            this.name = name;
            this.phone = phone;
            this.detail = detail;
            this.isDefault = isDefault;
            this.isSelected = isSelected;
        }

        public String getName() { return name; }
        public String getPhone() { return phone; }
        public String getDetail() { return detail; }
        public boolean isDefault() { return isDefault; }
        public boolean isSelected() { return isSelected; }
        
        public void setSelected(boolean selected) { isSelected = selected; }
        public void setDefault(boolean isDefault) { this.isDefault = isDefault; }
    }
}
