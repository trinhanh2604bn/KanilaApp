package com.example.frontend.feature.voucher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.example.frontend.data.model.coupon.CouponDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.ViewHolder> {
    private List<CouponDto> vouchers = new ArrayList<>();
    private OnVoucherClickListener listener;

    public interface OnVoucherClickListener {
        void onVoucherClick(CouponDto voucher);
        void onCopyClick(CouponDto voucher);
    }

    public void setOnVoucherClickListener(OnVoucherClickListener listener) {
        this.listener = listener;
    }

    public void setVouchers(List<CouponDto> vouchers) {
        this.vouchers = vouchers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voucher, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CouponDto voucher = vouchers.get(position);
        
        if ("percentage".equalsIgnoreCase(voucher.getDiscountType())) {
            holder.tvDiscountPercent.setText(String.format(Locale.US, "%.0f%%", voucher.getDiscountValue()));
        } else {
            holder.tvDiscountPercent.setText(formatPrice(voucher.getDiscountValue()));
        }
        
        holder.tvDiscountLimit.setText(String.format("GIẢM TỐI ĐA\n%s", formatPrice(voucher.getMaxDiscountAmount())));
        holder.tvVoucherCode.setText(voucher.getCouponCode());
        holder.tvCondition.setText(String.format("Đơn tối thiểu %s", formatPrice(voucher.getMinSpendAmount())));
        holder.tvExpiredDate.setText(String.format("HSD: %s", voucher.getEndDate()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onVoucherClick(voucher);
        });

        holder.btnCopy.setOnClickListener(v -> {
            if (listener != null) listener.onCopyClick(voucher);
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
        TextView tvDiscountPercent, tvDiscountLimit, tvVoucherCode, tvCondition, tvExpiredDate;
        View btnCopy;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDiscountPercent = itemView.findViewById(R.id.tvVoucherDiscountPercent);
            tvDiscountLimit = itemView.findViewById(R.id.tvVoucherDiscountLimit);
            tvVoucherCode = itemView.findViewById(R.id.tvVoucherCode);
            tvCondition = itemView.findViewById(R.id.tvVoucherCondition);
            tvExpiredDate = itemView.findViewById(R.id.tvVoucherExpiredDate);
            btnCopy = itemView.findViewById(R.id.btnCopyVoucher);
        }
    }
}
