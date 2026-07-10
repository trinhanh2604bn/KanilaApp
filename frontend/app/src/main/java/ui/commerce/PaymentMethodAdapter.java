package ui.commerce;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.radiobutton.MaterialRadioButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.data.model.payment.PaymentMethodDto;

import java.util.ArrayList;
import java.util.List;

public class PaymentMethodAdapter extends RecyclerView.Adapter<PaymentMethodAdapter.PaymentViewHolder> {

    private List<PaymentMethodDto> paymentMethods = new ArrayList<>();
    private String selectedMethodId;
    private OnPaymentMethodSelectedListener listener;

    public interface OnPaymentMethodSelectedListener {
        void onPaymentMethodSelected(PaymentMethodDto method);
    }

    public PaymentMethodAdapter(OnPaymentMethodSelectedListener listener) {
        this.listener = listener;
    }

    public void setPaymentMethods(List<PaymentMethodDto> methods, String selectedId) {
        this.paymentMethods = methods;
        this.selectedMethodId = selectedId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_method, parent, false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        PaymentMethodDto method = paymentMethods.get(position);
        holder.bind(method);
    }

    @Override
    public int getItemCount() {
        return paymentMethods.size();
    }

    class PaymentViewHolder extends RecyclerView.ViewHolder {
        private final View layoutPaymentMethod;
        private final MaterialRadioButton rbPaymentMethod;
        private final ImageView ivPaymentMethodIcon;
        private final TextView tvPaymentMethodName;
        private final TextView tvPaymentMethodDesc;

        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutPaymentMethod = itemView.findViewById(R.id.layoutPaymentMethod);
            rbPaymentMethod = itemView.findViewById(R.id.rbPaymentMethod);
            ivPaymentMethodIcon = itemView.findViewById(R.id.ivPaymentMethodIcon);
            tvPaymentMethodName = itemView.findViewById(R.id.tvPaymentMethodName);
            tvPaymentMethodDesc = itemView.findViewById(R.id.tvPaymentMethodDesc);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    PaymentMethodDto method = paymentMethods.get(position);
                    selectedMethodId = method.getId();
                    notifyDataSetChanged();
                    if (listener != null) {
                        listener.onPaymentMethodSelected(method);
                    }
                }
            });
        }

        public void bind(PaymentMethodDto method) {
            tvPaymentMethodName.setText(method.getName());
            tvPaymentMethodDesc.setText(method.getDescription());
            
            boolean isSelected = method.getId().equals(selectedMethodId);
            rbPaymentMethod.setChecked(isSelected);
            layoutPaymentMethod.setBackgroundResource(isSelected ? R.drawable.bg_selection_selected : 0);

            // Icon mapping (Simplified, should use Glide if URL or dynamic)
            if (method.getCode().equals("COD")) {
                ivPaymentMethodIcon.setImageResource(R.drawable.ic_delivery_truck);
            } else {
                ivPaymentMethodIcon.setImageResource(R.drawable.ic_paymeny_card);
            }
        }
    }
}
