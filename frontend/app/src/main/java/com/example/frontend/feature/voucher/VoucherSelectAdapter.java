package com.example.frontend.feature.voucher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.example.frontend.data.model.coupon.CouponDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VoucherSelectAdapter extends RecyclerView.Adapter<VoucherSelectAdapter.ViewHolder> {
    private List<CouponDto> vouchers = new ArrayList<>();
    private String selectedVoucherId = null;
    private OnVoucherSelectedListener listener;

    public interface OnVoucherSelectedListener {
        void onVoucherSelected(CouponDto voucher);
    }

    public void setOnVoucherSelectedListener(OnVoucherSelectedListener listener) {
        this.listener = listener;
    }

    public void setVouchers(List<CouponDto> vouchers) {
        this.vouchers = vouchers;
        notifyDataSetChanged();
    }

    public void setSelectedVoucherId(String selectedVoucherId) {
        this.selectedVoucherId = selectedVoucherId;
        notifyDataSetChanged();
    }

    public String getSelectedVoucherId() {
        return selectedVoucherId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voucher_select, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CouponDto voucher = vouchers.get(position);
        
        holder.tvVoucherCode.setText(voucher.getCouponCode());
        
        String discountText;
        if ("percentage".equalsIgnoreCase(voucher.getDiscountType())) {
            discountText = String.format(Locale.US, "Giảm %.0f%%", voucher.getDiscountValue());
        } else {
            discountText = String.format(Locale.US, "Giảm %s", formatPrice(voucher.getDiscountValue()));
        }
        
        String conditionText = String.format(Locale.US, " · Đơn từ %s", formatPrice(voucher.getMinOrderAmount()));
        holder.tvVoucherDesc.setText(discountText + conditionText);
        
        holder.tvVoucherExpiry.setText(String.format("HSD: %s", voucher.getValidTo() != null ? voucher.getValidTo() : "Không giới hạn"));

        boolean isSelected = voucher.getId().equals(selectedVoucherId);
        holder.ivVoucherRadio.setImageResource(isSelected ? R.drawable.ic_radio_on : R.drawable.ic_radio_off);
        holder.itemView.setBackgroundResource(isSelected ? R.drawable.bg_voucher_selected : R.drawable.bg_voucher_default);

        holder.itemView.setOnClickListener(v -> {
            selectedVoucherId = voucher.getId();
            notifyDataSetChanged();
            if (listener != null) {
                listener.onVoucherSelected(voucher);
            }
        });
    }

    @Override
    public int getItemCount() {
        return vouchers.size();
    }

    private String formatPrice(double price) {
        return String.format(Locale.US, "%,.0fđ", price).replace(",", ".");
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvVoucherCode, tvVoucherDesc, tvVoucherExpiry;
        ImageView ivVoucherRadio;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVoucherCode = itemView.findViewById(R.id.tvVoucherCode);
            tvVoucherDesc = itemView.findViewById(R.id.tvVoucherDesc);
            tvVoucherExpiry = itemView.findViewById(R.id.tvVoucherExpiry);
            ivVoucherRadio = itemView.findViewById(R.id.ivVoucherRadio);
        }
    }
}
