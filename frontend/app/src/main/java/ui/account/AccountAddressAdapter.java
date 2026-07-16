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
import java.util.List;

public class AccountAddressAdapter extends RecyclerView.Adapter<AccountAddressAdapter.ViewHolder> {
    private List<AddressDto> addressList = new ArrayList<>();
    private final OnAddressActionListener listener;

    public interface OnAddressActionListener {
        void onSetDefault(AddressDto address);
        void onEdit(AddressDto address);
    }

    public AccountAddressAdapter(OnAddressActionListener listener) {
        this.listener = listener;
    }

    public void setAddresses(List<AddressDto> addresses) {
        this.addressList = addresses != null ? addresses : new ArrayList<>();
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

        holder.tvName.setText(address.getFullName() != null ? address.getFullName() : "");
        holder.tvPhone.setText(address.getPhone() != null ? address.getPhone() : "");
        holder.tvDetail.setText(address.getAddressLine() != null ? address.getAddressLine() : "");

        boolean isDefault = address.isDefaultShipping();

        // 1. Nền hồng cho địa chỉ mặc định (Sử dụng state_selected của bg_address_card)
        holder.layoutItem.setSelected(isDefault);

        // 2. Badge mặc định
        if (isDefault) {
            holder.layoutDefaultTag.setVisibility(View.VISIBLE);
            if (holder.tvDefaultText != null) holder.tvDefaultText.setText("Mặc định");
        } else {
            holder.layoutDefaultTag.setVisibility(View.GONE);
        }

        // 3. Ẩn Radio Button (theo yêu cầu quản lý Account)
        holder.ivRadio.setVisibility(View.GONE);

        // 4. Nút "Thiết lập mặc định" (giống Checkout)
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
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvDetail, tvDefaultText, tvSetDefault;
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
            ivRadio = itemView.findViewById(R.id.ivAddressRadio);
            btnEdit = itemView.findViewById(R.id.btnAddressEdit);
        }
    }
}
