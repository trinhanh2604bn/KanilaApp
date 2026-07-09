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
import com.example.frontend.data.model.address.AddressDto;
import com.example.frontend.data.model.checkout.CheckoutSessionDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.remote.TokenManager;
import com.example.frontend.feature.checkout.CheckoutAddressViewModel;
import com.example.frontend.feature.checkout.CheckoutViewModel;

import java.util.Locale;

public class CheckoutFragment extends Fragment {

    private CheckoutViewModel viewModel;
    private CheckoutAddressViewModel addressViewModel;
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

        viewModel = new ViewModelProvider(requireActivity()).get(CheckoutViewModel.class);
        addressViewModel = new ViewModelProvider(requireActivity()).get(CheckoutAddressViewModel.class);
        
        initViews(view);
        setupHeader(view);
        observeViewModel();

        if (getArguments() != null) {
            java.util.List<com.example.frontend.data.model.cart.CartItemDto> selectedItems = 
                (java.util.List<com.example.frontend.data.model.cart.CartItemDto>) getArguments().getSerializable("selected_items");
            double coinsDiscount = getArguments().getDouble("coins_discount", 0);
            com.example.frontend.data.model.coupon.CouponDto selectedVoucher = 
                (com.example.frontend.data.model.coupon.CouponDto) getArguments().getSerializable("selected_voucher");
            
            if (selectedItems != null && !selectedItems.isEmpty()) {
                viewModel.setMockDataFromCart(selectedItems, coinsDiscount, selectedVoucher);
            }
        }
        
        viewModel.prepareCheckout();

        // Load address if logged in
        if (TokenManager.getInstance(getContext()).isLoggedIn()) {
            addressViewModel.loadCustomerAddresses();
        }
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
            ((TextView) addressCard.findViewById(R.id.tvCheckoutOptionTitle)).setText(R.string.checkout_address_title);
            ((ImageView) addressCard.findViewById(R.id.ivCheckoutOptionIcon)).setImageResource(R.drawable.ic_location);

            View.OnClickListener addressClickListener = v -> {
                if (getActivity() != null) {
                    boolean isLoggedIn = TokenManager.getInstance(getContext()).isLoggedIn();
                    Fragment targetFragment;
                    
                    if (isLoggedIn) {
                        NetworkResult<java.util.List<AddressDto>> result = addressViewModel.getAddressResult().getValue();
                        if (result != null && result.status == NetworkResult.Status.SUCCESS && result.data != null && !result.data.isEmpty()) {
                            targetFragment = new CheckoutAddressFragment();
                        } else {
                            targetFragment = new CheckoutAddressAddFragment();
                        }
                    } else {
                        targetFragment = new CheckoutAddressAddFragment();
                        Bundle args = new Bundle();
                        args.putBoolean("is_guest", true);
                        
                        // Pass current guest address if available
                        AddressDto current = viewModel.getSelectedAddress().getValue();
                        if (current != null) {
                            args.putSerializable("guest_address", current);
                        }

                        targetFragment.setArguments(args);
                    }
                    
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main, targetFragment)
                            .addToBackStack(null)
                            .commit();
                }
            };

            View btnEdit = addressCard.findViewById(R.id.tvCheckoutOptionEdit);
            if (btnEdit != null) {
                btnEdit.setOnClickListener(addressClickListener);
            }
            addressCard.setOnClickListener(addressClickListener);
        }

        View shippingCard = view.findViewById(R.id.layoutCheckoutShipping);
        if (shippingCard != null) {
            ((TextView) shippingCard.findViewById(R.id.tvCheckoutOptionTitle)).setText(R.string.checkout_shipping_title);
            ((ImageView) shippingCard.findViewById(R.id.ivCheckoutOptionIcon)).setImageResource(R.drawable.ic_shipping);
        }

        View paymentCard = view.findViewById(R.id.layoutCheckoutPayment);
        if (paymentCard != null) {
            ((TextView) paymentCard.findViewById(R.id.tvCheckoutOptionTitle)).setText(R.string.checkout_payment_title);
            ((ImageView) paymentCard.findViewById(R.id.ivCheckoutOptionIcon)).setImageResource(R.drawable.ic_paymeny_card);
        }

        View voucherCard = view.findViewById(R.id.layoutCheckoutVoucher);
        if (voucherCard != null) {
            ((TextView) voucherCard.findViewById(R.id.tvCheckoutOptionTitle)).setText(R.string.checkout_voucher_title);
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

        addressViewModel.getAddressResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null || result.data == null) return;
            
            if (result.status == NetworkResult.Status.SUCCESS) {
                if (result.data.isEmpty()) {
                    // No addresses, prompt to add
                    viewModel.setSelectedAddress(null);
                    return;
                }

                // If already has selection in checkoutViewModel, don't overwrite
                if (viewModel.getSelectedAddress().getValue() != null) return;

                // Find default address
                for (AddressDto address : result.data) {
                    if (address.isDefaultShipping()) {
                        viewModel.setSelectedAddress(address);
                        break;
                    }
                }
                
                // If no default, pick first
                if (viewModel.getSelectedAddress().getValue() == null && !result.data.isEmpty()) {
                    viewModel.setSelectedAddress(result.data.get(0));
                }
            }
        });

        viewModel.getSelectedAddress().observe(getViewLifecycleOwner(), address -> {
            if (address == null) {
                setupAddress(null);
                return;
            }
            
            CheckoutSessionDto session = viewModel.getCheckoutSession().getValue() != null ? 
                viewModel.getCheckoutSession().getValue().data : null;
            
            if (session != null) {
                CheckoutSessionDto.CheckoutAddressDto checkoutAddress = new CheckoutSessionDto.CheckoutAddressDto();
                checkoutAddress.setFullName(address.getRecipientName());
                checkoutAddress.setPhone(address.getPhone());
                
                // Format for secondary text
                StringBuilder sb = new StringBuilder();
                appendIfNotEmpty(sb, address.getAddressLine1());
                appendIfNotEmpty(sb, address.getAddressLine2());
                appendIfNotEmpty(sb, address.getWard());
                appendIfNotEmpty(sb, address.getDistrict());
                appendIfNotEmpty(sb, address.getCity());
                checkoutAddress.setAddressLine(sb.toString());
                
                session.setShippingAddress(checkoutAddress);
                setupAddress(checkoutAddress);
            }
        });
    }

    private void appendIfNotEmpty(StringBuilder sb, String text) {
        if (text != null && !text.trim().isEmpty()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(text);
        }
    }

    private void bindCheckoutData(CheckoutSessionDto session) {
        if (session == null) return;

        // 1. Address
        AddressDto selectedAddr = viewModel.getSelectedAddress().getValue();
        if (selectedAddr != null) {
            CheckoutSessionDto.CheckoutAddressDto checkoutAddress = new CheckoutSessionDto.CheckoutAddressDto();
            checkoutAddress.setFullName(selectedAddr.getRecipientName());
            checkoutAddress.setPhone(selectedAddr.getPhone());
            
            StringBuilder sb = new StringBuilder();
            appendIfNotEmpty(sb, selectedAddr.getAddressLine1());
            appendIfNotEmpty(sb, selectedAddr.getAddressLine2());
            appendIfNotEmpty(sb, selectedAddr.getWard());
            appendIfNotEmpty(sb, selectedAddr.getDistrict());
            appendIfNotEmpty(sb, selectedAddr.getCity());
            checkoutAddress.setAddressLine(sb.toString());
            
            setupAddress(checkoutAddress);
        } else {
            setupAddress(session.getShippingAddress());
        }

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
                if (viewModel.getSelectedAddress().getValue() == null && session.getShippingAddress() == null) {
                    Toast.makeText(getContext(), "Vui lòng thêm địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
                    return;
                }

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

        CheckoutSessionDto session = viewModel.getCheckoutSession().getValue() != null ? 
            viewModel.getCheckoutSession().getValue().data : null;

        if (discountAmount > 0) {
            if (session != null && session.getCouponCode() != null) {
                tvPrimary.setText(session.getCouponCode());
            } else {
                tvPrimary.setText("GIẢM 100k");
            }
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
        if (card == null) return;

        TextView tvPrimary = card.findViewById(R.id.tvCheckoutOptionPrimary);
        TextView tvSecondary = card.findViewById(R.id.tvCheckoutOptionSecondary);
        TextView tvValue = card.findViewById(R.id.tvCheckoutOptionRightValue);

        if (address != null) {
            tvPrimary.setText(address.getFullName() + " | " + address.getPhone());
            tvSecondary.setText(address.getAddressLine());
            tvSecondary.setVisibility(View.VISIBLE);
        } else {
            tvPrimary.setText("Hãy nhập địa chỉ nhận hàng");
            tvSecondary.setVisibility(View.GONE);
        }

        if (tvValue != null) tvValue.setVisibility(View.GONE);
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
                            .replace(R.id.main, new CheckoutShippingFragment())
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

            Glide.with(this)
                    .load(item.getImageUrl() != null ? item.getImageUrl() : "")
                    .placeholder(R.drawable.ic_product)
                    .error(R.drawable.ic_product)
                    .into(ivProduct);
            
            itemView.setOnClickListener(v -> {
                if (getContext() == null) return;
                
                // Convert CheckoutItemDto to CartItemDto for dialog
                com.example.frontend.data.model.cart.CartItemDto cartItem = 
                    com.example.frontend.data.model.cart.CartItemDto.createMock(
                        item.getId(), item.getProductName(), item.getVariantName(), 
                        item.getPrice(), item.getQuantity(), true, item.getImageUrl()
                    );
                cartItem.setProductId(item.getProductId());
                cartItem.setVariantId(item.getVariantId());
                cartItem.setBrandNameSnapshot(item.getBrandName());
                cartItem.setStockStatus(item.getStockStatus());

                VariantBottomSheetDialog dialog = new VariantBottomSheetDialog(getContext(), cartItem);
                dialog.setOnVariantAppliedListener((variant, quantity) -> {
                    if (variant != null) {
                        item.setVariantId(variant.getId());
                        item.setVariantName(variant.getVariantName());
                        if (variant.getPrice() != null) {
                            item.setPrice(variant.getPrice());
                        }
                    }
                    item.setQuantity(quantity);
                    
                    // Update UI locally
                    tvVariant.setText(item.getVariantName());
                    tvPrice.setText(formatPrice(item.getPrice()));
                    tvQuantity.setText("Số lượng: " + item.getQuantity());
                    
                    // TODO: Update summary in ViewModel if price/quantity changed
                    updatePriceSummaryLocally();
                });
                dialog.show();
            });

            layoutCheckoutItemsList.addView(itemView);
        }
    }

    private void updatePriceSummaryLocally() {
        if (viewModel == null) return;
        
        CheckoutSessionDto session = viewModel.getCheckoutSession().getValue() != null ? 
            viewModel.getCheckoutSession().getValue().data : null;
            
        if (session == null || session.getItems() == null) return;
        
        double subtotal = 0;
        for (CheckoutSessionDto.CheckoutItemDto item : session.getItems()) {
            subtotal += item.getPrice() * item.getQuantity();
        }
        
        session.setSubtotalAmount(subtotal);
        double total = subtotal + session.getShippingAmount() - session.getDiscountAmount() - session.getPointsAmount();
        session.setTotalAmount(Math.max(0, total));
        
        tvSubtotal.setText(formatPrice(session.getSubtotalAmount()));
        tvTotal.setText(formatPrice(session.getTotalAmount()));
        
        TextView tvSubtotalLabel = getView().findViewById(R.id.tvCheckoutPriceSubtotalLabel);
        if (tvSubtotalLabel != null) {
            tvSubtotalLabel.setText("Tạm tính (" + session.getItems().size() + " sản phẩm)");
        }
    }

    private String formatPrice(double price) {
        return String.format(Locale.US, "%,.0fđ", price).replace(",", ".");
    }
}
