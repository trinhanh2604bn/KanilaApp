package ui.commerce;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.example.frontend.data.model.checkout.CheckoutSessionDto;
import com.example.frontend.feature.checkout.CheckoutViewModel;

import java.util.Locale;

public class CheckoutFragment extends Fragment {

    private CheckoutViewModel viewModel;
    private View layoutCheckoutLoading;
    private LinearLayout layoutCheckoutItemsList;
    private TextView tvSubtotal, tvShipping, tvDiscount, tvPoints, tvTotal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_checkout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(CheckoutViewModel.class);
        
        initViews(view);
        setupHeader(view);
        observeViewModel();

        if (getArguments() != null) {
            java.util.List<com.example.frontend.data.model.cart.CartItemDto> selectedItems = 
                (java.util.List<com.example.frontend.data.model.cart.CartItemDto>) getArguments().getSerializable("selected_items");
            double coinsDiscount = getArguments().getDouble("coins_discount", 0);
            
            if (selectedItems != null && !selectedItems.isEmpty()) {
                viewModel.setMockDataFromCart(selectedItems, coinsDiscount);
            }
        }
        
        viewModel.prepareCheckout();
    }

    private void initViews(View view) {
        layoutCheckoutItemsList = view.findViewById(R.id.layoutCheckoutItemsList);
        tvSubtotal = view.findViewById(R.id.tvCheckoutPriceSubtotalValue);
        tvShipping = view.findViewById(R.id.tvCheckoutPriceShippingValue);
        tvDiscount = view.findViewById(R.id.tvCheckoutPriceDiscountValue);
        tvPoints = view.findViewById(R.id.tvCheckoutPricePointsValue);
        tvTotal = view.findViewById(R.id.tvCheckoutTotalFinal);
        
        // Find option card titles for specific cards
        bindOptionCardHeaders(view);
    }

    private void bindOptionCardHeaders(View view) {
        View addressCard = view.findViewById(R.id.layoutCheckoutAddress);
        if (addressCard != null) {
            ((TextView) addressCard.findViewById(R.id.tvCheckoutOptionTitle)).setText("Địa chỉ nhận hàng");
            ((ImageView) addressCard.findViewById(R.id.ivCheckoutOptionIcon)).setImageResource(R.drawable.ic_location);
        }

        View shippingCard = view.findViewById(R.id.layoutCheckoutShipping);
        if (shippingCard != null) {
            ((TextView) shippingCard.findViewById(R.id.tvCheckoutOptionTitle)).setText("Phương thức vận chuyển");
            ((ImageView) shippingCard.findViewById(R.id.ivCheckoutOptionIcon)).setImageResource(R.drawable.ic_shipping);
        }

        View paymentCard = view.findViewById(R.id.layoutCheckoutPayment);
        if (paymentCard != null) {
            ((TextView) paymentCard.findViewById(R.id.tvCheckoutOptionTitle)).setText("Phương thức thanh toán");
            ((ImageView) paymentCard.findViewById(R.id.ivCheckoutOptionIcon)).setImageResource(R.drawable.ic_paymeny_card);
        }

        View voucherCard = view.findViewById(R.id.layoutCheckoutVoucher);
        if (voucherCard != null) {
            ((TextView) voucherCard.findViewById(R.id.tvCheckoutOptionTitle)).setText("Kanila Voucher");
            ((ImageView) voucherCard.findViewById(R.id.ivCheckoutOptionIcon)).setImageResource(R.drawable.ic_coupon);
        }
    }

    private void setupHeader(View view) {
        View header = view.findViewById(R.id.layoutTopBar);
        if (header == null) return;

        TextView tvTitle = header.findViewById(R.id.tvTopBarTitle);
        if (tvTitle != null) tvTitle.setText(R.string.checkout_order_confirmation_title);

        View btnSearch = header.findViewById(R.id.btnTopBarSearch);
        if (btnSearch != null) btnSearch.setVisibility(View.GONE);
        
        View btnBack = header.findViewById(R.id.btnTopBarBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) getActivity().getOnBackPressedDispatcher().onBackPressed();
            });
        }
    }

    private void observeViewModel() {
        viewModel.getCheckoutSession().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            
            switch (result.status) {
                case LOADING:
                    // showLoading();
                    break;
                case SUCCESS:
                    bindCheckoutData(result.data);
                    break;
                case ERROR:
                    Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void bindCheckoutData(CheckoutSessionDto session) {
        if (session == null) return;

        // 1. Address
        setupAddress(session.getShippingAddress());

        // 2. Shipping
        setupShipping(session.getShippingMethod(), session.getShippingAmount());

        // 3. Payment
        setupPayment(session.getPaymentMethod());
        
        // 4. Voucher
        setupVoucher(session.getDiscountAmount());

        // 5. Items
        bindItems(session.getItems());

        // 6. Summary
        TextView tvSubtotalLabel = getView().findViewById(R.id.tvCheckoutPriceSubtotalLabel);
        if (tvSubtotalLabel != null && session.getItems() != null) {
            tvSubtotalLabel.setText("Tạm tính (" + session.getItems().size() + " sản phẩm)");
        }

        tvSubtotal.setText(formatPrice(session.getSubtotalAmount()));
        tvShipping.setText(formatPrice(session.getShippingAmount()));
        tvDiscount.setText("-" + formatPrice(session.getDiscountAmount()));
        tvPoints.setText("-" + formatPrice(session.getPointsAmount()));
        tvTotal.setText(formatPrice(session.getTotalAmount()));
        
        // Coins
        TextView tvCoinsAmount = getView().findViewById(R.id.tvCheckoutCoinsAmount);
        if (tvCoinsAmount != null) {
            tvCoinsAmount.setText(formatPrice(session.getPointsAmount()));
        }
        
        ImageView ivCoinsCheck = getView().findViewById(R.id.ivCheckoutCoinsCheck);
        if (ivCoinsCheck != null) {
            ivCoinsCheck.setSelected(session.getPointsAmount() > 0);
            ivCoinsCheck.setVisibility(session.getPointsAmount() > 0 ? View.VISIBLE : View.GONE);
        }

        View btnOrder = getView().findViewById(R.id.btnPlaceOrder);
        if (btnOrder != null) {
            btnOrder.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }
    }

    private void setupVoucher(double discountAmount) {
        View card = getView().findViewById(R.id.layoutCheckoutVoucher);
        if (card == null) return;

        TextView tvPrimary = card.findViewById(R.id.tvCheckoutOptionPrimary);
        TextView tvValue = card.findViewById(R.id.tvCheckoutOptionRightValue);
        TextView tvSecondary = card.findViewById(R.id.tvCheckoutOptionSecondary);

        if (discountAmount > 0) {
            tvPrimary.setText("GIẢM 100k");
            tvValue.setText("-" + formatPrice(discountAmount));
            tvValue.setVisibility(View.VISIBLE);
        } else {
            tvPrimary.setText("Chọn hoặc nhập mã");
            tvValue.setVisibility(View.GONE);
        }
        
        if (tvSecondary != null) tvSecondary.setVisibility(View.GONE);
    }

    private void setupAddress(CheckoutSessionDto.CheckoutAddressDto address) {
        View card = getView().findViewById(R.id.layoutCheckoutAddress);
        if (card == null || address == null) return;

        TextView tvPrimary = card.findViewById(R.id.tvCheckoutOptionPrimary);
        TextView tvSecondary = card.findViewById(R.id.tvCheckoutOptionSecondary);
        TextView tvValue = card.findViewById(R.id.tvCheckoutOptionRightValue);

        tvPrimary.setText(address.getFullName() + " | " + address.getPhone());
        tvSecondary.setText(address.getAddressLine());
        if (tvValue != null) tvValue.setVisibility(View.GONE);
        
        View btnEdit = card.findViewById(R.id.tvCheckoutOptionEdit);
        if (btnEdit != null) {
            btnEdit.setOnClickListener(v -> {
                // Navigate to address selector
            });
        }
    }

    private void setupShipping(String method, double amount) {
        View card = getView().findViewById(R.id.layoutCheckoutShipping);
        if (card == null) return;

        TextView tvPrimary = card.findViewById(R.id.tvCheckoutOptionPrimary);
        TextView tvSecondary = card.findViewById(R.id.tvCheckoutOptionSecondary);
        TextView tvValue = card.findViewById(R.id.tvCheckoutOptionRightValue);

        tvPrimary.setText(method != null ? method : "Giao hàng tiêu chuẩn");
        if (tvSecondary != null) {
            tvSecondary.setText("Nhận hàng: Ngày 09/7");
            tvSecondary.setVisibility(View.VISIBLE);
        }
        
        if (tvValue != null) {
            tvValue.setText(formatPrice(amount));
            tvValue.setVisibility(View.VISIBLE);
        }

        View btnEdit = card.findViewById(R.id.tvCheckoutOptionEdit);
        if (btnEdit != null) {
            btnEdit.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_fragment_container, new CheckoutShippingFragment())
                            .addToBackStack(null)
                            .commit();
                }
            });
        }
    }

    private void setupPayment(String method) {
        View card = getView().findViewById(R.id.layoutCheckoutPayment);
        if (card == null) return;

        TextView tvPrimary = card.findViewById(R.id.tvCheckoutOptionPrimary);
        TextView tvSecondary = card.findViewById(R.id.tvCheckoutOptionSecondary);
        TextView tvValue = card.findViewById(R.id.tvCheckoutOptionRightValue);

        tvPrimary.setText(method != null ? method : "Thanh toán khi nhận hàng (COD)");
        if (tvSecondary != null) tvSecondary.setVisibility(View.GONE);
        if (tvValue != null) tvValue.setVisibility(View.GONE);
    }

    private void bindItems(java.util.List<CheckoutSessionDto.CheckoutItemDto> items) {
        if (items == null || layoutCheckoutItemsList == null) return;
        layoutCheckoutItemsList.removeAllViews();
        
        for (CheckoutSessionDto.CheckoutItemDto item : items) {
            View itemView = getLayoutInflater().inflate(R.layout.item_cart_selected, layoutCheckoutItemsList, false);
            TextView tvName = itemView.findViewById(R.id.tvSelectedCartProductName);
            TextView tvVariant = itemView.findViewById(R.id.tvSelectedCartVariant);
            TextView tvPrice = itemView.findViewById(R.id.tvSelectedCartPrice);
            TextView tvQuantity = itemView.findViewById(R.id.tvSelectedCartQuantity);
            ImageView ivProduct = itemView.findViewById(R.id.ivSelectedCartProductImage);

            tvName.setText(item.getProductName());
            tvVariant.setText(item.getVariantName());
            tvPrice.setText(formatPrice(item.getPrice()));
            tvQuantity.setText("Số lượng: " + item.getQuantity());

            Glide.with(this).load(item.getImageUrl()).placeholder(R.drawable.ic_product).into(ivProduct);
            
            layoutCheckoutItemsList.addView(itemView);
        }
    }

    private String formatPrice(double price) {
        return String.format(Locale.US, "%,.0fđ", price).replace(",", ".");
    }
}
