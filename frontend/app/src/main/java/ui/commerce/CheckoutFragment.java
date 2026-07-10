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
import androidx.core.os.BundleCompat;
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
import com.example.frontend.feature.checkout.ShippingViewModel;
import com.example.frontend.data.model.shipping.ShippingMethodDto;
import com.example.frontend.feature.product.VariantSelectorBottomSheet;
import com.example.frontend.data.repository.ProductRepository;

import java.util.Locale;

public class CheckoutFragment extends Fragment {

    private CheckoutViewModel viewModel;
    private CheckoutAddressViewModel addressViewModel;
    private ShippingViewModel shippingViewModel;
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
        shippingViewModel = new ViewModelProvider(requireActivity()).get(ShippingViewModel.class);

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
                android.util.Log.d("CheckoutFragment", "Setting mock data from cart arguments. Item count: " + selectedItems.size());
                viewModel.setMockDataFromCart(selectedItems, coinsDiscount, selectedVoucher);
            }
        }

        viewModel.prepareCheckout();
        shippingViewModel.loadShippingMethods();

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

                    ui.common.FragmentNavigationHelper.loadFragment(getActivity(), targetFragment);
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

            View.OnClickListener shippingClickListener = v -> {
                if (getActivity() != null) {
                    ui.common.FragmentNavigationHelper.loadFragment(getActivity(), new CheckoutShippingFragment());
                }
            };

            View btnEdit = shippingCard.findViewById(R.id.tvCheckoutOptionEdit);
            if (btnEdit != null) {
                btnEdit.setOnClickListener(shippingClickListener);
            }
            shippingCard.setOnClickListener(shippingClickListener);
        }

        View paymentCard = view.findViewById(R.id.layoutCheckoutPayment);
        if (paymentCard != null) {
            ((TextView) paymentCard.findViewById(R.id.tvCheckoutOptionTitle)).setText(R.string.checkout_payment_title);
            ((ImageView) paymentCard.findViewById(R.id.ivCheckoutOptionIcon)).setImageResource(R.drawable.ic_paymeny_card);

            View.OnClickListener paymentClickListener = v -> {
                if (getActivity() != null) {
                    ui.common.FragmentNavigationHelper.loadFragment(getActivity(), new PaymentMethodFragment());
                }
            };

            View btnEdit = paymentCard.findViewById(R.id.tvCheckoutOptionEdit);
            if (btnEdit != null) {
                btnEdit.setOnClickListener(paymentClickListener);
            }
            paymentCard.setOnClickListener(paymentClickListener);
        }

        View voucherCard = view.findViewById(R.id.layoutCheckoutVoucher);
        if (voucherCard != null) {
            ((TextView) voucherCard.findViewById(R.id.tvCheckoutOptionTitle)).setText(R.string.checkout_voucher_title);
            ((ImageView) voucherCard.findViewById(R.id.ivCheckoutOptionIcon)).setImageResource(R.drawable.ic_coupon);

            View.OnClickListener voucherClickListener = v -> {
                VoucherBottomSheetDialog bottomSheet = new VoucherBottomSheetDialog();
                bottomSheet.setOnVoucherAppliedListener(voucher -> {
                    if (voucher != null) {
                        android.util.Log.d("CheckoutFragment", "Voucher selected from bottom sheet: " + voucher.getCouponCode());
                        // Update the session via ViewModel
                        CheckoutSessionDto session = viewModel.getCheckoutSession().getValue() != null ?
                                viewModel.getCheckoutSession().getValue().data : null;

                        if (session != null) {
                            // Calculate new discount
                            double subtotal = session.getSubtotalAmount() != null ? session.getSubtotalAmount() : 0.0;
                            double discount = 0;
                            if ("percentage".equalsIgnoreCase(voucher.getDiscountType())) {
                                discount = subtotal * (voucher.getDiscountValue() / 100.0);
                                if (voucher.getMaxDiscountAmount() > 0) {
                                    discount = Math.min(discount, voucher.getMaxDiscountAmount());
                                }
                            } else {
                                discount = voucher.getDiscountValue();
                            }

                            session.setCouponCode(voucher.getCouponCode());
                            session.setDiscountAmount(discount);

                            // Recalculate total
                            double shipping = session.getShippingAmount() != null ? session.getShippingAmount() : 0.0;
                            double points = session.getPointsAmount() != null ? session.getPointsAmount() : 0.0;
                            double total = subtotal + shipping - discount - points;
                            session.setTotalAmount(Math.max(0, total));

                            viewModel.updateCheckoutSession(session);
                        }
                    }
                });
                bottomSheet.show(getChildFragmentManager(), "VoucherBottomSheet");
            };

            View btnEdit = voucherCard.findViewById(R.id.tvCheckoutOptionEdit);
            if (btnEdit != null) {
                btnEdit.setOnClickListener(voucherClickListener);
            }
            voucherCard.setOnClickListener(voucherClickListener);
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
            boolean isLoggedIn = TokenManager.getInstance(getContext()).isLoggedIn();
            android.util.Log.d("CheckoutFragment", "Address result observed. Logged in: " + isLoggedIn);

            if (result == null || result.data == null) {
                android.util.Log.d("CheckoutFragment", "Address result is null or data is null");
                return;
            }

            if (result.status == NetworkResult.Status.SUCCESS) {
                android.util.Log.d("CheckoutFragment", "Address list size: " + result.data.size());
                if (result.data.isEmpty()) {
                    android.util.Log.d("CheckoutFragment", "Empty address state triggered");
                    viewModel.setSelectedAddress(null);
                    return;
                }

                // If already has selection in checkoutViewModel, don't overwrite
                if (viewModel.getSelectedAddress().getValue() != null) {
                    android.util.Log.d("CheckoutFragment", "Already have selected address: " + viewModel.getSelectedAddress().getValue().getRecipientName());
                    return;
                }

                // Find default address
                for (AddressDto address : result.data) {
                    if (address.isDefaultShipping()) {
                        android.util.Log.d("CheckoutFragment", "Setting default shipping address: " + address.getRecipientName());
                        viewModel.setSelectedAddress(address);
                        break;
                    }
                }

                // If no default, pick first
                if (viewModel.getSelectedAddress().getValue() == null && !result.data.isEmpty()) {
                    android.util.Log.d("CheckoutFragment", "No default address found, picking first: " + result.data.get(0).getRecipientName());
                    viewModel.setSelectedAddress(result.data.get(0));
                }
            }
        });

        shippingViewModel.getShippingMethodsResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null || result.status != NetworkResult.Status.SUCCESS || result.data == null) return;

            CheckoutSessionDto session = viewModel.getCheckoutSession().getValue() != null ?
                    viewModel.getCheckoutSession().getValue().data : null;

            String currentMethod = session != null ? session.getShippingMethod() : null;
            android.util.Log.d("CheckoutFragment", "Shipping methods received. Current method in session: '" + currentMethod + "'");

            if (session != null && (currentMethod == null || currentMethod.trim().isEmpty())) {
                android.util.Log.d("CheckoutFragment", "Shipping method is empty, finding default...");
                // Find default shipping method from Database
                ShippingMethodDto defaultMethod = null;
                
                // Priority 1: Check is_default flag
                for (ShippingMethodDto method : result.data) {
                    if (method.isDefault()) {
                        defaultMethod = method;
                        break;
                    }
                }
                
                // Priority 2: Check for "Tiêu chuẩn" name if no default flag
                if (defaultMethod == null) {
                    for (ShippingMethodDto method : result.data) {
                        if (method.getName() != null && method.getName().toLowerCase().contains("tiêu chuẩn")) {
                            defaultMethod = method;
                            break;
                        }
                    }
                }
                
                // Priority 3: Just pick the first one
                if (defaultMethod == null && !result.data.isEmpty()) {
                    defaultMethod = result.data.get(0);
                }

                if (defaultMethod != null) {
                    android.util.Log.d("CheckoutFragment", "Automatically selecting default shipping: " + defaultMethod.getName());
                    viewModel.updateShippingMethod(defaultMethod);
                }
            } else {
                android.util.Log.d("CheckoutFragment", "Shipping method already set, skipping auto-select.");
            }
        });

        viewModel.getSelectedAddress().observe(getViewLifecycleOwner(), address -> {
            android.util.Log.d("CheckoutFragment", "Selected address changed: " + (address != null ? address.getRecipientName() : "null"));
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

        viewModel.getPlaceOrderResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case LOADING:
                    // Show loading
                    break;
                case SUCCESS:
                    if (getActivity() != null) {
                        CheckoutSessionDto session = viewModel.getCheckoutSession().getValue() != null ?
                                viewModel.getCheckoutSession().getValue().data : null;
                        com.example.frontend.data.model.order.OrderDto order = result.data;

                        String paymentMethod = session != null ? session.getPaymentMethod() : "Thanh toán khi nhận hàng (COD)";
                        String deliveryTime = (session != null && session.getEstimatedDelivery() != null) ? session.getEstimatedDelivery() : "Dự kiến 2-3 ngày";
                        double total = order != null ? order.getTotalAmount() : (session != null ? session.getTotalAmount() : 0);
                        int points = (int) (total / 5000);

                        OrderSuccessFragment successFragment = OrderSuccessFragment.newInstance(
                                order != null ? order.getOrderNumber() : "KNL" + System.currentTimeMillis() / 1000,
                                paymentMethod,
                                deliveryTime,
                                total,
                                points
                        );

                        // Use FragmentManager to clear backstack of checkout flow if possible, or just replace
                        // To keep it simple and consistent with requirement "Integrate with existing navigation"
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.main_fragment_container, successFragment)
                                .commit();
                    }
                    break;
                case ERROR:
                    Toast.makeText(getContext(), "Lỗi: " + result.message, Toast.LENGTH_SHORT).show();
                    break;
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
        android.util.Log.d("CheckoutFragment", "Binding checkout data. Shipping: " + session.getShippingMethod() + ", Fee: " + session.getShippingAmount());

        // 1. Address
        setupAddress(session.getShippingAddress());

        // 2. Shipping
        setupShipping(session.getShippingMethod(), safeDouble(session.getShippingAmount()), session.getEstimatedDelivery());

        // 3. Payment
        setupPayment(session.getPaymentMethod());

        // 4. Voucher
        setupVoucher(safeDouble(session.getDiscountAmount()));

        // 5. Items
        bindItems(session.getItems());

        // 6. Summary
        TextView tvSubtotalLabel = getView().findViewById(R.id.tvCheckoutPriceSubtotalLabel);
        if (tvSubtotalLabel != null && session.getItems() != null) {
            tvSubtotalLabel.setText("Tạm tính (" + session.getItems().size() + " sản phẩm)");
        }

        tvSubtotal.setText(formatPrice(safeDouble(session.getSubtotalAmount())));
        tvShipping.setText(formatPrice(safeDouble(session.getShippingAmount())));
        tvDiscount.setText("-" + formatPrice(safeDouble(session.getDiscountAmount())));
        tvPoints.setText("-" + formatPrice(safeDouble(session.getPointsAmount())));
        
        double total = safeDouble(session.getTotalAmount());
        if (total <= 0) {
            total = safeDouble(session.getSubtotalAmount()) + safeDouble(session.getShippingAmount()) 
                    - safeDouble(session.getDiscountAmount()) - safeDouble(session.getPointsAmount());
        }
        tvTotal.setText(formatPrice(Math.max(0, total)));



        View btnOrder = getView().findViewById(R.id.btnPlaceOrder);
        if (btnOrder != null) {
            btnOrder.setOnClickListener(v -> {
                if (viewModel.getSelectedAddress().getValue() == null && session.getShippingAddress() == null) {
                    Toast.makeText(getContext(), "Vui lòng thêm địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
                    return;
                }

                viewModel.placeOrder();
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
            if (session != null && session.getCouponCode() != null && !session.getCouponCode().isEmpty()) {
                tvPrimary.setText(session.getCouponCode());
            } else {
                tvPrimary.setText("Ưu đãi đã áp dụng");
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

    private void setupShipping(String method, double amount, String estimate) {
        android.util.Log.d("CheckoutFragment", "Displaying shipping info. Method: " + method + ", Fee: " + amount + ", Estimate: " + estimate);
        View card = getView().findViewById(R.id.layoutCheckoutShipping);
        if (card == null) return;

        TextView tvPrimary = card.findViewById(R.id.tvCheckoutOptionPrimary);
        TextView tvSecondary = card.findViewById(R.id.tvCheckoutOptionSecondary);
        TextView tvValue = card.findViewById(R.id.tvCheckoutOptionRightValue);

        boolean hasMethod = method != null && !method.isEmpty();
        if (hasMethod) {
            tvPrimary.setText(method);
            if (tvSecondary != null) {
                if (estimate != null && !estimate.isEmpty()) {
                    tvSecondary.setText("Dự kiến giao: " + estimate);
                    tvSecondary.setVisibility(View.VISIBLE);
                } else {
                    tvSecondary.setVisibility(View.GONE);
                }
            }
            if (tvValue != null) {
                tvValue.setText(formatPrice(amount));
                tvValue.setVisibility(View.VISIBLE);
            }
        } else {
            // State when no method is selected yet (waiting for DB)
            tvPrimary.setText("Đang tải phương thức vận chuyển...");
            if (tvSecondary != null) tvSecondary.setVisibility(View.GONE);
            if (tvValue != null) tvValue.setVisibility(View.GONE);
        }
    }

    private void setupPayment(String method) {
        View card = getView().findViewById(R.id.layoutCheckoutPayment);
        if (card == null) return;

        TextView tvPrimary = card.findViewById(R.id.tvCheckoutOptionPrimary);
        TextView tvSecondary = card.findViewById(R.id.tvCheckoutOptionSecondary);
        TextView tvValue = card.findViewById(R.id.tvCheckoutOptionRightValue);
        ImageView ivIcon = card.findViewById(R.id.ivCheckoutOptionIcon);

        String displayMethod = (method != null && !method.isEmpty()) ? method : "Thanh toán khi nhận hàng (COD)";
        tvPrimary.setText(displayMethod);

        // Change icon based on selection
        if (ivIcon != null) {
            if (displayMethod.contains("COD") || displayMethod.contains("nhận hàng")) {
                ivIcon.setImageResource(R.drawable.ic_delivery_truck);
            } else {
                ivIcon.setImageResource(R.drawable.ic_paymeny_card);
            }
        }

        if (tvSecondary != null) tvSecondary.setVisibility(View.GONE);
        if (tvValue != null) tvValue.setVisibility(View.GONE);
    }

    private void bindItems(java.util.List<CheckoutSessionDto.CheckoutItemDto> items) {
        if (items == null || layoutCheckoutItemsList == null) return;
        layoutCheckoutItemsList.removeAllViews();

        for (int i = 0; i < items.size(); i++) {
            CheckoutSessionDto.CheckoutItemDto item = items.get(i);
            
            // Only show selected items
            if (!item.isSelected()) continue;

            View itemView = getLayoutInflater().inflate(R.layout.item_cart_selected, layoutCheckoutItemsList, false);
            TextView tvName = itemView.findViewById(R.id.tvSelectedCartProductName);
            
            View layoutVariant = itemView.findViewById(R.id.item_variant_selection);
            TextView tvVariantDisplay = null;
            if (layoutVariant != null) {
                tvVariantDisplay = layoutVariant.findViewById(R.id.tvVariantName);
            } else {
                // Fallback to original ID if include was not added/rolled back
                layoutVariant = itemView.findViewById(R.id.tvSelectedCartVariant);
                if (layoutVariant instanceof TextView) {
                    tvVariantDisplay = (TextView) layoutVariant;
                }
            }
            
            TextView tvPrice = itemView.findViewById(R.id.tvSelectedCartPrice);
            TextView tvQuantity = itemView.findViewById(R.id.tvSelectedCartQuantity);
            ImageView ivProduct = itemView.findViewById(R.id.ivSelectedCartProductImage);

            tvName.setText(item.getProductName());
            if (tvVariantDisplay != null) {
                tvVariantDisplay.setText(getDisplayVariantName(item));
            }
            tvPrice.setText(formatPrice(item.getPrice()));
            tvQuantity.setText("Số lượng: " + item.getQuantity());

            Glide.with(this)
                    .load(item.getImageUrl() != null ? item.getImageUrl() : "")
                    .placeholder(R.drawable.ic_product)
                    .error(R.drawable.ic_product)
                    .into(ivProduct);

            if (layoutVariant != null) {
                layoutVariant.setOnClickListener(v -> {
                    if (getContext() == null || item.getProductId() == null) return;

                    android.util.Log.d("CheckoutFragment", "Variant click for product: " + item.getProductName());
                    ProductRepository repo = new ProductRepository(getContext());
                    repo.getProductDetail(item.getProductId()).observe(getViewLifecycleOwner(), result -> {
                        if (result != null && result.status == NetworkResult.Status.SUCCESS && result.data != null) {
                            VariantSelectorBottomSheet bottomSheet = VariantSelectorBottomSheet.newInstance(
                                    result.data.getProduct(),
                                    result.data.getVariants(),
                                    VariantSelectorBottomSheet.ActionMode.CONFIRM
                            );
                            bottomSheet.setListener((variant, mode, newQuantity) -> {
                                if (variant != null) {
                                    android.util.Log.d("CheckoutFragment", "New variant selected: " + variant.getVariantName() + ", Qty: " + newQuantity);
                                    
                                    // Update item data
                                    item.setVariantId(variant.getId());
                                    item.setVariantName(variant.getVariantName());
                                    if (variant.getPrice() != null) {
                                        item.setPrice(variant.getPrice());
                                    }
                                    if (variant.getImageUrl() != null && !variant.getImageUrl().isEmpty()) {
                                        item.setImageUrl(variant.getImageUrl());
                                    }
                                    item.setQuantity(newQuantity);

                                    // Update items in ViewModel (handles recalculation and API sync)
                                    viewModel.updateItems(items);
                                }
                            });
                            bottomSheet.show(getChildFragmentManager(), "VariantSelector");
                        } else if (result != null && result.status == NetworkResult.Status.ERROR) {
                            Toast.makeText(getContext(), "Không thể tải thông tin sản phẩm", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }

            layoutCheckoutItemsList.addView(itemView);
        }
    }

    private String getDisplayVariantName(CheckoutSessionDto.CheckoutItemDto item) {
        String variantName = item.getVariantName();
        String productName = item.getProductName();

        if (variantName == null || variantName.isEmpty()) return "Mặc định";
        if (productName == null || productName.isEmpty()) return variantName;

        String display = variantName;

        // Xử lý loại bỏ tên sản phẩm nếu nó nằm trong tên variant
        if (display.contains(productName + " - ")) {
            display = display.replace(productName + " - ", "");
        } else if (display.startsWith(productName)) {
            String potential = display.substring(productName.length()).trim();
            if (!potential.isEmpty()) {
                if (potential.startsWith("-") || potential.startsWith(":") || potential.startsWith("•")) {
                    display = potential.substring(1).trim();
                } else {
                    display = potential;
                }
            }
        }

        return display.isEmpty() ? "Mặc định" : display;
    }

    private double safeDouble(Double value) {
        return value != null ? value : 0.0;
    }

    private String formatPrice(double price) {
        return String.format(Locale.US, "%,.0fđ", price).replace(",", ".");
    }
}
