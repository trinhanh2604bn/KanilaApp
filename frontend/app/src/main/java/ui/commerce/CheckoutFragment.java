package ui.commerce;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.frontend.R;

public class CheckoutFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Refactored to inflate page_checkout.xml as per requirements
        return inflater.inflate(R.layout.page_checkout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupHeader(view);
        setupAddressCard(view);
        setupShippingCard(view);
        setupPaymentCard(view);
        setupVoucherCard(view);
        setupCoinSection(view);
        setupSummary(view);
    }

    private void setupHeader(View view) {
        View header = view.findViewById(R.id.layoutTopBar);
        if (header == null) return;

        TextView tvTitle = header.findViewById(R.id.tvTopBarTitle);
        if (tvTitle != null) {
            tvTitle.setText(R.string.checkout_order_confirmation_title);
        }

        View btnSearch = header.findViewById(R.id.btnTopBarSearch);
        if (btnSearch != null) {
            btnSearch.setVisibility(View.GONE);
        }
        
        View btnBack = header.findViewById(R.id.btnTopBarBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().getOnBackPressedDispatcher().onBackPressed();
                }
            });
        }
    }

    private void setupAddressCard(View view) {
        View card = view.findViewById(R.id.layoutCheckoutAddress);
        if (card == null) return;
        
        ImageView ivIcon = card.findViewById(R.id.ivCheckoutOptionIcon);
        if (ivIcon != null) ivIcon.setImageResource(R.drawable.ic_location);

        TextView tvTitle = card.findViewById(R.id.tvCheckoutOptionTitle);
        if (tvTitle != null) tvTitle.setText(R.string.checkout_address_title);

        TextView tvPrimary = card.findViewById(R.id.tvCheckoutOptionPrimary);
        if (tvPrimary != null) tvPrimary.setText(R.string.checkout_sample_receiver_2);

        TextView tvSecondary = card.findViewById(R.id.tvCheckoutOptionSecondary);
        if (tvSecondary != null) tvSecondary.setText(R.string.checkout_sample_address_2);

        View vRight = card.findViewById(R.id.tvCheckoutOptionRightValue);
        if (vRight != null) vRight.setVisibility(View.GONE);

        View btnEdit = card.findViewById(R.id.tvCheckoutOptionEdit);
        if (btnEdit != null) {
            btnEdit.setOnClickListener(v -> {
                // TODO: Handle address edit
            });
        }
    }

    private void setupShippingCard(View view) {
        View card = view.findViewById(R.id.layoutCheckoutShipping);
        if (card == null) return;

        ImageView ivIcon = card.findViewById(R.id.ivCheckoutOptionIcon);
        if (ivIcon != null) ivIcon.setImageResource(R.drawable.ic_shipping);

        TextView tvTitle = card.findViewById(R.id.tvCheckoutOptionTitle);
        if (tvTitle != null) tvTitle.setText(R.string.checkout_shipping_title);

        TextView tvPrimary = card.findViewById(R.id.tvCheckoutOptionPrimary);
        if (tvPrimary != null) tvPrimary.setText(R.string.checkout_sample_shipping_2);

        TextView tvSecondary = card.findViewById(R.id.tvCheckoutOptionSecondary);
        if (tvSecondary != null) tvSecondary.setText(R.string.checkout_sample_delivery_date_2);

        TextView tvValue = card.findViewById(R.id.tvCheckoutOptionRightValue);
        if (tvValue != null) tvValue.setText("30.000đ");

        View btnEdit = card.findViewById(R.id.tvCheckoutOptionEdit);
        if (btnEdit != null) {
            btnEdit.setOnClickListener(v -> {
                // TODO: Handle shipping method change
            });
        }
    }

    private void setupPaymentCard(View view) {
        View card = view.findViewById(R.id.layoutCheckoutPayment);
        if (card == null) return;

        ImageView ivIcon = card.findViewById(R.id.ivCheckoutOptionIcon);
        if (ivIcon != null) ivIcon.setImageResource(R.drawable.ic_lock); 

        TextView tvTitle = card.findViewById(R.id.tvCheckoutOptionTitle);
        if (tvTitle != null) tvTitle.setText(R.string.checkout_payment_title);

        TextView tvPrimary = card.findViewById(R.id.tvCheckoutOptionPrimary);
        if (tvPrimary != null) tvPrimary.setText(R.string.checkout_sample_payment);

        View tvSecondary = card.findViewById(R.id.tvCheckoutOptionSecondary);
        if (tvSecondary != null) tvSecondary.setVisibility(View.GONE);

        View vRight = card.findViewById(R.id.tvCheckoutOptionRightValue);
        if (vRight != null) vRight.setVisibility(View.GONE);

        View btnEdit = card.findViewById(R.id.tvCheckoutOptionEdit);
        if (btnEdit != null) {
            btnEdit.setOnClickListener(v -> {
                // TODO: Handle payment method change
            });
        }
    }

    private void setupVoucherCard(View view) {
        View card = view.findViewById(R.id.layoutCheckoutVoucher);
        if (card == null) return;

        ImageView ivIcon = card.findViewById(R.id.ivCheckoutOptionIcon);
        if (ivIcon != null) ivIcon.setImageResource(R.drawable.ic_coupon);

        TextView tvTitle = card.findViewById(R.id.tvCheckoutOptionTitle);
        if (tvTitle != null) tvTitle.setText(R.string.checkout_voucher_title);

        TextView tvPrimary = card.findViewById(R.id.tvCheckoutOptionPrimary);
        if (tvPrimary != null) tvPrimary.setText("GIẢM 100k");

        View tvSecondary = card.findViewById(R.id.tvCheckoutOptionSecondary);
        if (tvSecondary != null) tvSecondary.setVisibility(View.GONE);

        TextView tvValue = card.findViewById(R.id.tvCheckoutOptionRightValue);
        if (tvValue != null) {
            tvValue.setText("-100.000đ");
            if (getContext() != null) {
                tvValue.setTextColor(ContextCompat.getColor(getContext(), R.color.button));
            }
        }

        View btnEdit = card.findViewById(R.id.tvCheckoutOptionEdit);
        if (btnEdit != null) {
            btnEdit.setOnClickListener(v -> {
                // TODO: Handle voucher change
            });
        }
    }

    private void setupCoinSection(View view) {
        View coinCard = view.findViewById(R.id.layoutCheckoutCoins);
        if (coinCard == null) return;
        
        TextView tvAmount = view.findViewById(R.id.tvCheckoutCoinsAmount);
        if (tvAmount != null) {
            tvAmount.setText(getString(R.string.checkout_coins_format, "100"));
        }
        
        coinCard.setOnClickListener(v -> {
            // TODO: Toggle coin usage
        });
    }

    private void setupSummary(View view) {
        TextView tvSubtotal = view.findViewById(R.id.tvCheckoutPriceSubtotalValue);
        TextView tvShipping = view.findViewById(R.id.tvCheckoutPriceShippingValue);
        TextView tvDiscount = view.findViewById(R.id.tvCheckoutPriceDiscountValue);
        TextView tvPoints = view.findViewById(R.id.tvCheckoutPricePointsValue);
        TextView tvTotal = view.findViewById(R.id.tvCheckoutTotalFinal);
        View btnOrder = view.findViewById(R.id.btnPlaceOrder);

        // Sample data binding
        if (tvSubtotal != null) tvSubtotal.setText("970.000đ");
        if (tvShipping != null) tvShipping.setText("40.000đ");
        if (tvDiscount != null) tvDiscount.setText("-100.000đ");
        if (tvPoints != null) tvPoints.setText("-10.000đ");
        if (tvTotal != null) tvTotal.setText("800.000đ");

        if (btnOrder != null) {
            btnOrder.setOnClickListener(v -> {
                // TODO: Process order
            });
        }
    }
}
