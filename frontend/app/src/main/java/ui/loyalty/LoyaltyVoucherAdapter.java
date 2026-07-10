package ui.loyalty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import ui.loyalty.model.LoyaltyCouponDto;

public class LoyaltyVoucherAdapter extends RecyclerView.Adapter<LoyaltyVoucherAdapter.VoucherViewHolder> {

    public interface OnVoucherSaveListener {
        void onSaveClick(LoyaltyCouponDto voucher);
    }

    private final List<LoyaltyCouponDto> vouchers = new ArrayList<>();
    private final OnVoucherSaveListener listener;

    public LoyaltyVoucherAdapter(OnVoucherSaveListener listener) {
        this.listener = listener;
    }

    public void setVouchers(List<LoyaltyCouponDto> newVouchers) {
        vouchers.clear();
        if (newVouchers != null) vouchers.addAll(newVouchers);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loyalty_voucher, parent, false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        holder.bind(vouchers.get(position));
    }

    @Override
    public int getItemCount() {
        return vouchers.size();
    }

    class VoucherViewHolder extends RecyclerView.ViewHolder {
        TextView tvTier, tvTitle, tvMinSpend, tvExpiry;
        MaterialButton btnSave;

        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTier = itemView.findViewById(R.id.tvVoucherTierLabel);
            tvTitle = itemView.findViewById(R.id.tvVoucherTitle);
            tvMinSpend = itemView.findViewById(R.id.tvVoucherMinSpend);
            tvExpiry = itemView.findViewById(R.id.tvVoucherExpiry);
            btnSave = itemView.findViewById(R.id.btnSaveVoucher);
        }

        public void bind(LoyaltyCouponDto voucher) {
            Context context = itemView.getContext();
            
            // Logic for tier badge text (could be mapped better but using CLASSIC as default in XML)
            // tvTier.setText("GOLD\nMEMBER"); // Example
            
            tvTitle.setText(voucher.getPromotionName());
            
            String minSpend = String.format(Locale.getDefault(), "%,.0fđ", voucher.getMinOrderAmount()).replace(",", ".");
            tvMinSpend.setText(context.getString(R.string.checkout_price_subtotal, 0).replace("Tạm tính (0 sản phẩm)", "Đơn tối thiểu " + minSpend));
            // Alternative: use a specific string
            tvMinSpend.setText("Đơn tối thiểu " + minSpend);

            tvExpiry.setText("Hạn dùng đến " + (voucher.getValidTo() != null ? voucher.getValidTo().split("T")[0] : "N/A"));

            if (voucher.isSaved()) {
                btnSave.setText(R.string.loyalty_saved_action);
                btnSave.setEnabled(false);
                btnSave.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.border_divider));
            } else {
                btnSave.setText(R.string.loyalty_save_action);
                btnSave.setEnabled(true);
                btnSave.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.button));
                btnSave.setOnClickListener(v -> {
                    if (listener != null) listener.onSaveClick(voucher);
                });
            }
        }
    }
}
