package ui.commerce;

import android.text.TextUtils;
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

public class CheckoutAddressAdapter extends RecyclerView.Adapter<CheckoutAddressAdapter.ViewHolder> {

    private List<AddressDto> addressList = new ArrayList<>();
    private String selectedAddressId;
    private final OnAddressClickListener listener;

    public interface OnAddressClickListener {
        void onAddressSelected(AddressDto address, int position);
        void onAddressEdit(AddressDto address, int position);
        void onSetDefault(AddressDto address, int position);
    }

    public CheckoutAddressAdapter(OnAddressClickListener listener) {
        this.listener = listener;
    }

    public void setAddresses(List<AddressDto> addresses) {
        this.addressList = addresses != null ? addresses : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setSelectedAddressId(String selectedAddressId) {
        this.selectedAddressId = selectedAddressId;
        for (AddressDto address : addressList) {
            address.setSelected(address.getId() != null && address.getId().equals(selectedAddressId));
        }
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
        
        holder.tvName.setText(safeText(address.getRecipientName()));
        holder.tvPhone.setText(safeText(address.getPhone()));
        holder.tvDetail.setText(formatFullAddress(address));
        
        holder.layoutDefaultTag.setVisibility(address.isDefaultShipping() ? View.VISIBLE : View.GONE);
        if (address.isDefaultShipping() && holder.tvDefaultText != null) {
            // Set runtime text for default badge
            holder.tvDefaultText.setText("Mặc định");
        }
        
        if (holder.tvSetDefault != null) {
            holder.tvSetDefault.setVisibility(!address.isDefaultShipping() ? View.VISIBLE : View.GONE);
            holder.tvSetDefault.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSetDefault(address, position);
                }
            });
        }
        
        boolean isSelected = address.isSelected();
        holder.ivRadio.setSelected(isSelected);
        holder.layoutRoot.setSelected(isSelected);

        holder.layoutRoot.setOnClickListener(v -> {
            handleSelection(address, position);
        });

        holder.ivRadio.setOnClickListener(v -> {
            handleSelection(address, position);
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddressEdit(address, position);
            }
        });
    }

    private void handleSelection(AddressDto address, int position) {
        if (selectedAddressId != null && selectedAddressId.equals(address.getId())) return;

        int oldSelectedPosition = -1;
        for (int i = 0; i < addressList.size(); i++) {
            if (addressList.get(i).getId() != null && addressList.get(i).getId().equals(selectedAddressId)) {
                oldSelectedPosition = i;
                addressList.get(i).setSelected(false);
                break;
            }
        }

        selectedAddressId = address.getId();
        address.setSelected(true);
        
        if (oldSelectedPosition != -1) {
            notifyItemChanged(oldSelectedPosition);
        }
        notifyItemChanged(position);
        
        if (listener != null) {
            listener.onAddressSelected(address, position);
        }
    }

    private String safeText(String text) {
        return text != null ? text : "";
    }

    private String formatFullAddress(AddressDto address) {
        List<String> parts = new ArrayList<>();
        addIfNotEmpty(parts, address.getAddressLine1());
        addIfNotEmpty(parts, address.getAddressLine2());
        addIfNotEmpty(parts, address.getWard());
        addIfNotEmpty(parts, address.getDistrict());
        addIfNotEmpty(parts, address.getCity());
        return TextUtils.join(", ", parts);
    }

    private void addIfNotEmpty(List<String> parts, String value) {
        if (value != null && !value.trim().isEmpty()) {
            parts.add(value.trim());
        }
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvDetail, tvDefaultText, tvSetDefault;
        ImageView ivRadio, btnEdit;
        View layoutDefaultTag, layoutRoot;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutRoot = itemView.findViewById(R.id.layoutAddressItemRoot);
            tvName = itemView.findViewById(R.id.tvAddressName);
            tvPhone = itemView.findViewById(R.id.tvAddressPhone);
            tvDetail = itemView.findViewById(R.id.tvAddressDetail);
            ivRadio = itemView.findViewById(R.id.ivAddressRadio);
            btnEdit = itemView.findViewById(R.id.btnAddressEdit);
            layoutDefaultTag = itemView.findViewById(R.id.layoutAddressDefaultTag);
            tvDefaultText = itemView.findViewById(R.id.tvAddressDefaultText);
            tvSetDefault = itemView.findViewById(R.id.tvSetDefault);
        }
    }
}
