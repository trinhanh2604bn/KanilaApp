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
        
        viewModel.prepareCheckout();
    }

    private void initViews(View view) {
        layoutCheckoutItemsList = view.findViewById(R.id.layoutCheckoutItemsList);
        tvSubtotal = view.findViewById(R.id.tvCheckoutPriceSubtotalValue);
        tvShipping = view.findViewById(R.id.tvCheckoutPriceShippingValue);
        tvDiscount = view.findViewById(R.id.tvCheckoutPriceDiscountValue);
        tvPoints = view.findViewById(R.id.tvCheckoutPricePointsValue);
        tvTotal = view.findViewById(R.id.tvCheckoutTotalFinal);
        
        // layoutCheckoutLoading = view.findViewById(R.id.viewCheckoutLoading); // Add this to XML if needed
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

        // 4. Items
        bindItems(session.getItems());

        // 5. Summary
        tvSubtotal.setText(formatPrice(session.getSubtotalAmount()));
        tvShipping.setText(formatPrice(session.getShippingAmount()));
        tvDiscount.setText("-" + formatPrice(session.getDiscountAmount()));
        tvPoints.setText("-" + formatPrice(session.getPointsAmount()));
        tvTotal.setText(formatPrice(session.getTotalAmount()));

        View btnOrder = getView().findViewById(R.id.btnPlaceOrder);
        if (btnOrder != null) {
            btnOrder.setOnClickListener(v -> {
                // viewModel.placeOrder(session.getId());
            });
        }
    }

    private void setupAddress(CheckoutSessionDto.CheckoutAddressDto address) {
        View card = getView().findViewById(R.id.layoutCheckoutAddress);
        if (card == null || address == null) return;

        TextView tvPrimary = card.findViewById(R.id.tvCheckoutOptionPrimary);
        TextView tvSecondary = card.findViewById(R.id.tvCheckoutOptionSecondary);

        tvPrimary.setText(address.getFullName() + " | " + address.getPhone());
        tvSecondary.setText(address.getAddressLine());
        
        card.findViewById(R.id.tvCheckoutOptionEdit).setOnClickListener(v -> {
            // Navigate to address selector
        });
    }

    private void setupShipping(String method, double amount) {
        View card = getView().findViewById(R.id.layoutCheckoutShipping);
        if (card == null) return;

        TextView tvPrimary = card.findViewById(R.id.tvCheckoutOptionPrimary);
        TextView tvValue = card.findViewById(R.id.tvCheckoutOptionRightValue);

        tvPrimary.setText(method != null ? method : "Giao hàng tiêu chuẩn");
        tvValue.setText(formatPrice(amount));
    }

    private void setupPayment(String method) {
        View card = getView().findViewById(R.id.layoutCheckoutPayment);
        if (card == null) return;

        TextView tvPrimary = card.findViewById(R.id.tvCheckoutOptionPrimary);
        tvPrimary.setText(method != null ? method : "Thanh toán khi nhận hàng (COD)");
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
