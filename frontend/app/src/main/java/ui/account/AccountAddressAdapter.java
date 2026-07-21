package ui.account;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.example.frontend.data.model.address.AddressDto;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AccountAddressAdapter extends RecyclerView.Adapter<AccountAddressAdapter.ViewHolder> {
    private List<AddressDto> addressList = new ArrayList<>();
    private final OnAddressActionListener listener;

    public interface OnAddressActionListener {
        void onSetDefault(AddressDto address);
        void onEdit(AddressDto address);
        default void onAddressSelected(AddressDto address) {}
    }

    private boolean selectionMode = false;
    private String selectedAddressId;

    public AccountAddressAdapter(OnAddressActionListener listener) {
        this.listener = listener;
    }

    public void setSelectionMode(boolean selectionMode) {
        this.selectionMode = selectionMode;
        notifyDataSetChanged();
    }

    public void setSelectedAddressId(String id) {
        this.selectedAddressId = id;
        notifyDataSetChanged();
    }

    public void setAddresses(List<AddressDto> addresses) {
        if (addresses != null) {
            this.addressList = new ArrayList<>(addresses);
            // Sắp xếp: Địa chỉ mặc định lên đầu
            Collections.sort(this.addressList, (a, b) -> {
                boolean defA = a.isDefault();
                boolean defB = b.isDefault();
                if (defA == defB) return 0;
                return defA ? -1 : 1;
            });
        } else {
            this.addressList = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_checkout_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AddressDto address = addressList.get(position);

        holder.tvName.setText(address.getRecipientName() != null && !address.getRecipientName().isEmpty() 
                ? address.getRecipientName() : "Người nhận chưa có tên");
        holder.tvPhone.setText(address.getPhone() != null && !address.getPhone().isEmpty() 
                ? address.getPhone() : "Chưa có số điện thoại");
        holder.tvDetail.setText(address.getFullAddress() != null && !address.getFullAddress().isEmpty() 
                ? address.getFullAddress() : "Chưa có địa chỉ chi tiết");

        boolean isDefault = address.isDefault();
        boolean isSelected = (selectionMode && address.getId() != null && address.getId().equals(selectedAddressId)) || (!selectionMode && isDefault);

        // 1. Highlight card
        holder.layoutItem.setSelected(isSelected);

        // 2. Badge mặc định
        if (isDefault) {
            holder.layoutDefaultTag.setVisibility(View.VISIBLE);
            if (holder.tvDefaultText != null) holder.tvDefaultText.setText("Mặc định");
        } else {
            holder.layoutDefaultTag.setVisibility(View.GONE);
        }

        // 3. Radio Button visibility
        holder.ivRadio.setVisibility(selectionMode ? View.VISIBLE : View.GONE);
        holder.ivRadio.setSelected(isSelected);

        // 4. Click handling for selection
        holder.layoutItem.setOnClickListener(v -> {
            if (selectionMode && listener != null) {
                listener.onAddressSelected(address);
            }
        });

        // 5. Nút "Thiết lập mặc định" (giống Checkout)
        if (holder.tvSetDefault != null) {
            holder.tvSetDefault.setVisibility(isDefault ? View.GONE : View.VISIBLE);
            holder.tvSetDefault.setOnClickListener(v -> {
                if (listener != null) listener.onSetDefault(address);
            });
        }

        // 5. Nút chỉnh sửa
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(address);
        });

        // 6. Ẩn nút "Lưu vào sổ địa chỉ" (vì đây đã là sổ địa chỉ rồi)
        if (holder.tvSaveToAccount != null) {
            holder.tvSaveToAccount.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvDetail, tvDefaultText, tvSetDefault, tvSaveToAccount;
        View layoutDefaultTag, layoutItem;
        ImageView ivRadio, btnEdit;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutItem = itemView.findViewById(R.id.layoutAddressItemRoot);
            tvName = itemView.findViewById(R.id.tvAddressName);
            tvPhone = itemView.findViewById(R.id.tvAddressPhone);
            tvDetail = itemView.findViewById(R.id.tvAddressDetail);
            layoutDefaultTag = itemView.findViewById(R.id.layoutAddressDefaultTag);
            tvDefaultText = itemView.findViewById(R.id.tvAddressDefaultText);
            tvSetDefault = itemView.findViewById(R.id.tvSetDefault);
            tvSaveToAccount = itemView.findViewById(R.id.tvSaveToAccount);
            ivRadio = itemView.findViewById(R.id.ivAddressRadio);
            btnEdit = itemView.findViewById(R.id.btnAddressEdit);
        }
    }
}
