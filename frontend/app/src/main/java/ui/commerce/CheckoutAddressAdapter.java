package ui.commerce;

import android.annotation.SuppressLint;
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
    private List<AddressDto> accountAddressList = new ArrayList<>();
    private String selectedAddressId;
    private final OnAddressClickListener listener;
    private boolean isSelectionMode = true;

    public interface OnAddressClickListener {
        void onAddressSelected(AddressDto address, int position);
        void onAddressEdit(AddressDto address, int position);
        void onSetDefault(AddressDto address, int position);
        void onSaveToAccount(AddressDto address, int position);
    }

    public CheckoutAddressAdapter(OnAddressClickListener listener) {
        this.listener = listener;
    }

    public void setSelectionMode(boolean selectionMode) {
        this.isSelectionMode = selectionMode;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAddresses(List<AddressDto> addresses) {
        this.addressList = new ArrayList<>();
        if (addresses != null) {
            // Sort: Default address first
            List<AddressDto> sorted = new ArrayList<>(addresses);
            sorted.sort((a, b) -> Boolean.compare(b.isDefaultShipping(), a.isDefaultShipping()));
            this.addressList.addAll(sorted);
        }
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAccountAddresses(List<AddressDto> addresses) {
        this.accountAddressList = addresses != null ? addresses : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setSelectedAddressId(String selectedAddressId) {
        this.selectedAddressId = selectedAddressId;
        for (AddressDto a : addressList) {
            a.setSelected(a.getId() != null && a.getId().equals(selectedAddressId));
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

        if (holder.tvSaveToAccount != null) {
            // Nút này hiện cho địa chỉ chưa có trong sổ địa chỉ (account_addresses)
            boolean isAlreadyInAccount = checkIfAddressInAccount(address);
            
            // Nếu là địa chỉ mẫu thì luôn hiện (hoặc xử lý riêng)
            boolean isMock = address.getId() != null && address.getId().startsWith("mock_");
            
            if (isAlreadyInAccount && !isMock) {
                holder.tvSaveToAccount.setVisibility(View.GONE);
            } else {
                holder.tvSaveToAccount.setVisibility(View.VISIBLE);
                holder.tvSaveToAccount.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onSaveToAccount(address, position);
                    }
                });
            }
        }
        
        // Highlight logic: Pink background (selected) 
        // - In selection mode (Checkout): depends on address.isSelected()
        // - In management mode (Account): always highlight the default one
        boolean isHighlighted = isSelectionMode ? address.isSelected() : address.isDefaultShipping();
        holder.ivRadio.setSelected(isHighlighted);
        holder.layoutRoot.setSelected(isHighlighted);
        
        holder.ivRadio.setVisibility(isSelectionMode ? View.VISIBLE : View.GONE);

        holder.layoutRoot.setOnClickListener(v -> {
            if (isSelectionMode) {
                handleSelection(address, position);
            } else if (listener != null) {
                listener.onAddressSelected(address, position);
            }
        });

        holder.ivRadio.setOnClickListener(v -> {
            if (isSelectionMode) {
                handleSelection(address, position);
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddressEdit(address, position);
            }
        });
    }

    private void handleSelection(AddressDto address, int position) {
        if (selectedAddressId != null && selectedAddressId.equals(address.getId())) {
            if (listener != null) listener.onAddressSelected(address, position);
            return;
        }

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
        StringBuilder sb = new StringBuilder();
        addIfNotEmpty(sb, address.getAddressLine1());
        addIfNotEmpty(sb, address.getWard());
        addIfNotEmpty(sb, address.getDistrict());
        addIfNotEmpty(sb, address.getCity());
        return sb.toString();
    }

    private void addIfNotEmpty(StringBuilder sb, String text) {
        if (text != null && !text.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(text.trim());
        }
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public AddressDto getAddressAt(int position) {
        if (position >= 0 && position < addressList.size()) {
            return addressList.get(position);
        }
        return null;
    }

    private boolean checkIfAddressInAccount(AddressDto address) {
        if (address == null) return false;
        
        // Luôn hiện cho địa chỉ mock
        if (address.getId() != null && address.getId().startsWith("mock_")) return false;
        
        if (accountAddressList == null || accountAddressList.isEmpty()) return false;
        
        String currentName = address.getRecipientName();
        String currentPhone = address.getPhone();
        
        // Nếu thông tin của địa chỉ hiện tại trống thì ko so sánh
        if (currentName == null || currentName.trim().isEmpty()) return false;
        if (currentPhone == null || currentPhone.trim().isEmpty()) return false;
        
        String cleanCurrentPhone = currentPhone.replaceAll("\\s+", "");
        
        for (AddressDto accAddr : accountAddressList) {
            if (accAddr == null) continue;
            
            String accName = accAddr.getRecipientName();
            if (accName == null) accName = accAddr.getFullName();
            
            String accPhone = accAddr.getPhone();
            
            // Bỏ qua các địa chỉ trong sổ bị thiếu thông tin quan trọng
            if (accName == null || accName.trim().isEmpty()) continue;
            if (accPhone == null || accPhone.trim().isEmpty()) continue;
            
            String cleanAccPhone = accPhone.replaceAll("\\s+", "");
            
            // So sánh tên và SĐT
            boolean nameMatch = currentName.trim().equalsIgnoreCase(accName.trim());
            boolean phoneMatch = cleanCurrentPhone.equals(cleanAccPhone);
            
            if (nameMatch && phoneMatch) {
                return true; // Đã tồn tại trong account_addresses
            }
        }
        return false;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvDetail, tvDefaultText, tvSetDefault, tvSaveToAccount;
        ImageView ivRadio, btnEdit;
        View layoutDefaultTag, layoutRoot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutRoot = itemView.findViewById(R.id.layoutAddressItemRoot);
            ivRadio = itemView.findViewById(R.id.ivAddressRadio);
            tvName = itemView.findViewById(R.id.tvAddressName);
            tvPhone = itemView.findViewById(R.id.tvAddressPhone);
            tvDetail = itemView.findViewById(R.id.tvAddressDetail);
            layoutDefaultTag = itemView.findViewById(R.id.layoutAddressDefaultTag);
            tvDefaultText = itemView.findViewById(R.id.tvAddressDefaultText);
            tvSetDefault = itemView.findViewById(R.id.tvSetDefault);
            tvSaveToAccount = itemView.findViewById(R.id.tvSaveToAccount);
            btnEdit = itemView.findViewById(R.id.btnAddressEdit);
        }
    }
}
