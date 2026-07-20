package ui.commerce;

import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.example.frontend.data.model.address.AddressDto;
import com.example.frontend.data.model.checkout.CheckoutSessionDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.remote.TokenManager;
import com.example.frontend.feature.account.AccountViewModel;
import com.example.frontend.feature.checkout.CheckoutViewModel;
import com.example.frontend.feature.checkout.ShippingViewModel;
import com.example.frontend.data.model.shipping.ShippingMethodDto;
import com.example.frontend.feature.product.VariantSelectorBottomSheet;
import com.example.frontend.data.repository.ProductRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CheckoutFragment extends Fragment {

    private CheckoutViewModel viewModel;
    private AccountViewModel accountViewModel;
    private ShippingViewModel shippingViewModel;
    private LinearLayout layoutCheckoutItemsList;
    private TextView tvSubtotal, tvShipping, tvDiscount, tvTotal;

    // Address Form Views
    private ViewGroup cardCheckoutAddress;
    private View layoutAddressSummary, layoutAddressForm;
    private EditText edtAddressFullName, edtAddressPhone, edtAddressCity, edtAddressDistrict, edtAddressWard, edtAddressDetail, edtAddressNote;
    private com.google.android.material.checkbox.MaterialCheckBox cbSaveToAddressBook;
    private com.google.android.material.button.MaterialButton btnApplyAddressEdit;

    private String selectedProvince, selectedDistrict, selectedWard;
    private Map<String, Map<String, List<String>>> locationMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_checkout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(CheckoutViewModel.class);
        accountViewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);
        shippingViewModel = new ViewModelProvider(requireActivity()).get(ShippingViewModel.class);

        initViews(view);
        setupAddressForm(view);
        setupHeader(view);
        observeViewModel();

        if (getArguments() != null) {
            CheckoutSessionDto session = (CheckoutSessionDto) getArguments().getSerializable("checkout_session");
            
            if (session != null) {
                viewModel.updateCheckoutSession(session);
            } else {
                @SuppressWarnings("unchecked")
                java.util.List<com.example.frontend.data.model.cart.CartItemDto> selectedItems =
                        (java.util.List<com.example.frontend.data.model.cart.CartItemDto>) getArguments().getSerializable("selected_items");
                com.example.frontend.data.model.coupon.CouponDto selectedVoucher =
                        (com.example.frontend.data.model.coupon.CouponDto) getArguments().getSerializable("selected_voucher");

                if (selectedItems != null && !selectedItems.isEmpty()) {
                    viewModel.setMockDataFromCart(selectedItems, selectedVoucher);
                }
            }
        }

        viewModel.prepareCheckout();
        shippingViewModel.loadShippingMethods();

        if (TokenManager.getInstance(requireContext()).isLoggedIn()) {
            accountViewModel.loadAccountAddresses();
        }
    }

    private void initViews(View view) {
        layoutCheckoutItemsList = view.findViewById(R.id.layoutCheckoutItemsList);
        tvSubtotal = view.findViewById(R.id.tvCheckoutPriceSubtotalValue);
        tvShipping = view.findViewById(R.id.tvCheckoutPriceShippingValue);
        tvDiscount = view.findViewById(R.id.tvCheckoutPriceDiscountValue);
        tvTotal = view.findViewById(R.id.tvCheckoutTotalFinal);

        bindOptionCardHeaders(view);
    }

    private void bindOptionCardHeaders(View view) {
        View addressCard = view.findViewById(R.id.cardCheckoutAddress);
        if (addressCard != null) {
            View summaryInclude = addressCard.findViewById(R.id.layoutCheckoutAddressSummary);
            ((TextView) summaryInclude.findViewById(R.id.tvCheckoutOptionTitle)).setText(R.string.checkout_address_title);
            ((ImageView) summaryInclude.findViewById(R.id.ivCheckoutOptionIcon)).setImageResource(R.drawable.ic_location);

            View btnEdit = summaryInclude.findViewById(R.id.tvCheckoutOptionEdit);
            if (btnEdit != null) {
                btnEdit.setOnClickListener(v -> toggleAddressForm(true));
            }
            summaryInclude.setOnClickListener(v -> toggleAddressForm(true));
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
                        CheckoutSessionDto session = viewModel.getCheckoutSession().getValue() != null ?
                                viewModel.getCheckoutSession().getValue().data : null;

                        if (session != null) {
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

                            double shipping = session.getShippingAmount() != null ? session.getShippingAmount() : 0.0;
                            double totalValue = subtotal + shipping - discount;
                            session.setTotalAmount(Math.max(0, totalValue));

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
                case SUCCESS:
                    bindCheckoutData(result.data);
                    break;
                case ERROR:
                    Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                    break;
                default: break;
            }
        });

        accountViewModel.getAddAccountAddressResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case LOADING:
                    if (btnApplyAddressEdit != null) btnApplyAddressEdit.setEnabled(false);
                    break;
                case SUCCESS:
                    if (btnApplyAddressEdit != null) btnApplyAddressEdit.setEnabled(true);
                    if (result.data != null) {
                        viewModel.setSelectedAddress(result.data);
                    }
                    accountViewModel.loadAccountAddresses();
                    accountViewModel.resetAddAccountAddressResult();
                    toggleAddressForm(false);
                    Toast.makeText(getContext(), "Đã lưu địa chỉ vào danh sách", Toast.LENGTH_SHORT).show();
                    break;
                case ERROR:
                    if (btnApplyAddressEdit != null) btnApplyAddressEdit.setEnabled(true);
                    Toast.makeText(getContext(), "Lỗi lưu địa chỉ: " + result.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        accountViewModel.getAccountAddressesResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null || result.status != NetworkResult.Status.SUCCESS || result.data == null) return;
            if (result.data.isEmpty()) {
                viewModel.setSelectedAddress(null);
                return;
            }

            // Tìm địa chỉ mặc định trong danh sách
            AddressDto defaultAddr = null;
            for (AddressDto address : result.data) {
                if (address != null && address.isDefault()) {
                    defaultAddr = address;
                    break;
                }
            }

            // Logic chọn địa chỉ:
            // 1. Nếu có địa chỉ mặc định, luôn ưu tiên hiển thị nó lúc mới vào hoặc reload.
            // 2. Nếu không có địa chỉ mặc định và hiện tại chưa có cái nào được chọn, lấy cái đầu tiên.
            if (defaultAddr != null) {
                viewModel.setSelectedAddress(defaultAddr);
            } else if (viewModel.getSelectedAddress().getValue() == null && !result.data.isEmpty()) {
                viewModel.setSelectedAddress(result.data.get(0));
            }
        });

        shippingViewModel.getShippingMethodsResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null || result.status != NetworkResult.Status.SUCCESS || result.data == null) return;

            CheckoutSessionDto session = viewModel.getCheckoutSession().getValue() != null ?
                    viewModel.getCheckoutSession().getValue().data : null;

            String currentMethod = session != null ? session.getShippingMethod() : null;

            if (session != null && (currentMethod == null || currentMethod.trim().isEmpty() || currentMethod.equalsIgnoreCase("null"))) {
                ShippingMethodDto defaultMethod = null;
                for (ShippingMethodDto method : result.data) {
                    if (method != null && method.isDefault()) {
                        defaultMethod = method;
                        break;
                    }
                }
                if (defaultMethod == null) {
                    for (ShippingMethodDto method : result.data) {
                        if (method != null && method.getName() != null && method.getName().toLowerCase().contains("tiêu chuẩn")) {
                            defaultMethod = method;
                            break;
                        }
                    }
                }
                if (defaultMethod == null && !result.data.isEmpty()) {
                    defaultMethod = result.data.get(0);
                }

                if (defaultMethod != null) {
                    viewModel.updateShippingMethod(defaultMethod);
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
                checkoutAddress.setAddressLine(address.getFullAddress());

                session.setShippingAddress(checkoutAddress);
                setupAddress(checkoutAddress);
                viewModel.updateCheckoutSession(session);
            }
        });

        viewModel.getPlaceOrderResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case SUCCESS:
                    if (getActivity() != null) {
                        CheckoutSessionDto session = viewModel.getCheckoutSession().getValue() != null ?
                                viewModel.getCheckoutSession().getValue().data : null;
                        com.example.frontend.data.model.order.OrderDto order = result.data;

                        String paymentMethod = session != null ? session.getPaymentMethod() : "Thanh toán khi nhận hàng (COD)";
                        String deliveryTime = (session != null && session.getEstimatedDelivery() != null) ? session.getEstimatedDelivery() : "Dự kiến 2-3 ngày";
                        double totalValue = order != null ? order.getTotalAmount() : (session != null ? session.getTotalAmount() : 0);
                        int pointsValue = (int) (totalValue / 5000);

                        OrderSuccessFragment successFragment = OrderSuccessFragment.newInstance(
                                order != null ? order.getOrderNumber() : "KNL" + System.currentTimeMillis() / 1000,
                                paymentMethod,
                                deliveryTime,
                                totalValue,
                                pointsValue
                        );

                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.main_fragment_container, successFragment)
                                .commit();
                    }
                    break;
                case ERROR:
                    Toast.makeText(getContext(), "Lỗi: " + result.message, Toast.LENGTH_SHORT).show();
                    break;
                default: break;
            }
        });
    }

    private void bindCheckoutData(CheckoutSessionDto session) {
        if (session == null) return;

        CheckoutSessionDto.CheckoutAddressDto displayAddress = session.getShippingAddress();
        if (displayAddress == null && viewModel.getSelectedAddress().getValue() != null) {
            AddressDto selected = viewModel.getSelectedAddress().getValue();
            displayAddress = new CheckoutSessionDto.CheckoutAddressDto();
            displayAddress.setFullName(selected.getRecipientName());
            displayAddress.setPhone(selected.getPhone());
            displayAddress.setAddressLine(selected.getFullAddress());
            session.setShippingAddress(displayAddress);
        }
        setupAddress(displayAddress);

        setupShipping(session.getShippingMethod(), safeDouble(session.getShippingAmount()), session.getEstimatedDelivery());
        setupPayment(session.getPaymentMethod());
        setupVoucher(safeDouble(session.getDiscountAmount()));
        bindItems(session.getItems());

        View root = getView();
        if (root != null) {
            TextView tvSubtotalLabel = root.findViewById(R.id.tvCheckoutPriceSubtotalLabel);
            if (tvSubtotalLabel != null && session.getItems() != null) {
                int totalQuantity = 0;
                for (CheckoutSessionDto.CheckoutItemDto item : session.getItems()) {
                    if (item.isSelected()) {
                        totalQuantity += item.getQuantity();
                    }
                }
                tvSubtotalLabel.setText(String.format(Locale.getDefault(), "Tạm tính (%d sản phẩm)", totalQuantity));
            }
        }

        tvSubtotal.setText(formatPrice(safeDouble(session.getSubtotalAmount())));
        tvShipping.setText(formatPrice(safeDouble(session.getShippingAmount())));
        tvDiscount.setText(String.format("-%s", formatPrice(safeDouble(session.getDiscountAmount()))));
        
        double totalValue = safeDouble(session.getTotalAmount());
        if (totalValue <= 0) {
            totalValue = safeDouble(session.getSubtotalAmount()) + safeDouble(session.getShippingAmount()) 
                    - safeDouble(session.getDiscountAmount());
        }
        tvTotal.setText(formatPrice(Math.max(0, totalValue)));

        View btnOrder = getView() != null ? getView().findViewById(R.id.btnPlaceOrder) : null;
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
        View root = getView();
        if (root == null) return;
        View card = root.findViewById(R.id.layoutCheckoutVoucher);
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
            tvValue.setText(String.format("-%s", formatPrice(discountAmount)));
            tvValue.setVisibility(View.VISIBLE);
        } else {
            tvPrimary.setText("Chọn hoặc nhập mã");
            tvValue.setVisibility(View.GONE);
        }

        if (tvSecondary != null) tvSecondary.setVisibility(View.GONE);
    }

    private void setupAddress(CheckoutSessionDto.CheckoutAddressDto address) {
        View root = getView();
        if (root == null) return;
        View card = root.findViewById(R.id.cardCheckoutAddress);
        if (card == null) return;
        View summaryInclude = card.findViewById(R.id.layoutCheckoutAddressSummary);

        TextView tvPrimary = summaryInclude.findViewById(R.id.tvCheckoutOptionPrimary);
        TextView tvSecondary = summaryInclude.findViewById(R.id.tvCheckoutOptionSecondary);
        TextView tvValue = summaryInclude.findViewById(R.id.tvCheckoutOptionRightValue);

        if (address != null) {
            tvPrimary.setText(String.format("%s | %s", address.getFullName(), address.getPhone()));
            tvSecondary.setText(address.getAddressLine());
            tvSecondary.setVisibility(View.VISIBLE);
        } else {
            tvPrimary.setText("Hãy nhập địa chỉ nhận hàng");
            tvSecondary.setVisibility(View.GONE);
        }

        if (tvValue != null) tvValue.setVisibility(View.GONE);
    }

    private void setupAddressForm(View view) {
        initLocationData();
        cardCheckoutAddress = view.findViewById(R.id.cardCheckoutAddress);
        layoutAddressSummary = view.findViewById(R.id.layoutCheckoutAddressSummary);
        layoutAddressForm = view.findViewById(R.id.layoutCheckoutAddressForm);

        edtAddressFullName = view.findViewById(R.id.checkout_edtAddressFullName);
        edtAddressPhone = view.findViewById(R.id.checkout_edtAddressPhone);
        edtAddressCity = view.findViewById(R.id.checkout_edtAddressCity);
        edtAddressDistrict = view.findViewById(R.id.checkout_edtAddressDistrict);
        edtAddressWard = view.findViewById(R.id.checkout_edtAddressWard);
        edtAddressDetail = view.findViewById(R.id.checkout_edtAddressDetail);
        edtAddressNote = view.findViewById(R.id.checkout_edtAddressNote);
        cbSaveToAddressBook = view.findViewById(R.id.checkout_cbSaveToAddressBook);

        btnApplyAddressEdit = view.findViewById(R.id.checkout_btnApplyAddressEdit);
        View btnCancel = view.findViewById(R.id.checkout_btnCancelAddressEdit);
        View btnOther = view.findViewById(R.id.checkout_btnSelectOtherAddress);

        if (edtAddressCity != null) {
            edtAddressCity.setOnClickListener(v -> {
                List<String> provinces = new ArrayList<>(locationMap.keySet());
                AddressPickerBottomSheet picker = new AddressPickerBottomSheet(requireContext(), "Chọn tỉnh/thành phố", provinces, item -> {
                    selectedProvince = item;
                    selectedDistrict = null;
                    selectedWard = null;
                    edtAddressCity.setText(selectedProvince);
                    if (edtAddressDistrict != null) edtAddressDistrict.setText("");
                    if (edtAddressWard != null) edtAddressWard.setText("");
                });
                picker.show();
            });
        }

        if (edtAddressDistrict != null) {
            edtAddressDistrict.setOnClickListener(v -> {
                if (selectedProvince == null || selectedProvince.isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng chọn tỉnh/thành phố trước", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, List<String>> districts = locationMap.get(selectedProvince);
                if (districts != null) {
                    List<String> districtNames = new ArrayList<>(districts.keySet());
                    AddressPickerBottomSheet picker = new AddressPickerBottomSheet(requireContext(), "Chọn quận/huyện", districtNames, item -> {
                        selectedDistrict = item;
                        selectedWard = null;
                        edtAddressDistrict.setText(selectedDistrict);
                        if (edtAddressWard != null) edtAddressWard.setText("");
                    });
                    picker.show();
                }
            });
        }

        if (edtAddressWard != null) {
            edtAddressWard.setOnClickListener(v -> {
                if (selectedProvince == null || selectedDistrict == null || selectedDistrict.isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng chọn quận/huyện trước", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, List<String>> districts = locationMap.get(selectedProvince);
                if (districts != null) {
                    List<String> wards = districts.get(selectedDistrict);
                    if (wards != null) {
                        AddressPickerBottomSheet picker = new AddressPickerBottomSheet(requireContext(), "Chọn phường/xã", wards, item -> {
                            selectedWard = item;
                            edtAddressWard.setText(selectedWard);
                        });
                        picker.show();
                    }
                }
            });
        }

        if (btnCancel != null) btnCancel.setOnClickListener(v -> toggleAddressForm(false));

        if (btnOther != null) {
            btnOther.setOnClickListener(v -> {
                if (getActivity() != null) {
                    if (TokenManager.getInstance(requireContext()).isLoggedIn()) {
                        ui.common.FragmentNavigationHelper.loadFragment(getActivity(), new CheckoutAddressFragment());
                    } else {
                        Toast.makeText(getContext(), "Vui lòng đăng nhập để sử dụng sổ địa chỉ", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if (btnApplyAddressEdit != null) btnApplyAddressEdit.setOnClickListener(v -> applyAddressForm());

        android.text.TextWatcher watcher = new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { validateForm(); }
            @Override public void afterTextChanged(android.text.Editable s) {}
        };

        if (edtAddressFullName != null) edtAddressFullName.addTextChangedListener(watcher);
        if (edtAddressPhone != null) edtAddressPhone.addTextChangedListener(watcher);
        if (edtAddressCity != null) edtAddressCity.addTextChangedListener(watcher);
        if (edtAddressDistrict != null) edtAddressDistrict.addTextChangedListener(watcher);
        if (edtAddressWard != null) edtAddressWard.addTextChangedListener(watcher);
        if (edtAddressDetail != null) edtAddressDetail.addTextChangedListener(watcher);
    }

    private void initLocationData() {
        locationMap = new HashMap<>();
        Map<String, List<String>> hcmDistricts = new HashMap<>();
        hcmDistricts.put("Quận 1", Arrays.asList("Phường Bến Thành", "Phường Đa Kao", "Phường Tân Định"));
        hcmDistricts.put("Quận 7", Arrays.asList("Phường Tân Phong", "Phường Tân Kiểng", "Phường Phú Mỹ"));
        hcmDistricts.put("Thành phố Thủ Đức", Arrays.asList("Phường Linh Trung", "Phường Linh Tây", "Phường Hiệp Phú"));
        locationMap.put("TP. Hồ Chí Minh", hcmDistricts);

        Map<String, List<String>> hnDistricts = new HashMap<>();
        hnDistricts.put("Quận Hoàn Kiếm", Arrays.asList("Phường Hàng Đào", "Phường Tràng Tiền", "Phường Lý Thái Tổ"));
        hnDistricts.put("Quận Cầu Giấy", Arrays.asList("Phường Dịch Vọng", "Phường Yên Hòa", "Phường Quan Hoa"));
        locationMap.put("Hà Nội", hnDistricts);
        
        Map<String, List<String>> dnDistricts = new HashMap<>();
        dnDistricts.put("Quận Hải Châu", Arrays.asList("Phường Hòa Thuận Đông", "Phường Phước Ninh"));
        locationMap.put("Đà Nẵng", dnDistricts);
    }

    private void toggleAddressForm(boolean expand) {
        if (cardCheckoutAddress == null) return;

        TransitionManager.beginDelayedTransition(cardCheckoutAddress, new AutoTransition());
        layoutAddressSummary.setVisibility(expand ? View.GONE : View.VISIBLE);
        layoutAddressForm.setVisibility(expand ? View.VISIBLE : View.GONE);

        if (expand) {
            fillFormFromCurrent();
            validateForm();
        } else {
            View view = getActivity() != null ? getActivity().getCurrentFocus() : null;
            if (view != null) {
                android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) requireActivity().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void fillFormFromCurrent() {
        AddressDto current = viewModel.getSelectedAddress().getValue();
        if (current != null) {
            edtAddressFullName.setText(current.getRecipientName());
            edtAddressPhone.setText(current.getPhone());
            edtAddressCity.setText(current.getCity());
            edtAddressDistrict.setText(current.getDistrict());
            edtAddressWard.setText(current.getWard());
            edtAddressDetail.setText(current.getAddressLine1());
            
            selectedProvince = current.getCity();
            selectedDistrict = current.getDistrict();
            selectedWard = current.getWard();
        }
    }

    private boolean validateForm() {
        boolean isValid = true;
        if (isEmpty(edtAddressFullName)) isValid = false;
        if (isEmpty(edtAddressPhone)) isValid = false;
        if (isEmpty(edtAddressCity)) isValid = false;
        if (isEmpty(edtAddressDistrict)) isValid = false;
        if (isEmpty(edtAddressWard)) isValid = false;
        if (isEmpty(edtAddressDetail)) isValid = false;

        if (btnApplyAddressEdit != null) btnApplyAddressEdit.setEnabled(isValid);
        return isValid;
    }

    private boolean isEmpty(EditText et) {
        return et == null || et.getText().toString().trim().isEmpty();
    }

    private void applyAddressForm() {
        if (!validateForm()) return;

        String name = edtAddressFullName.getText().toString().trim();
        String phone = edtAddressPhone.getText().toString().trim();
        String city = edtAddressCity.getText().toString().trim();
        String district = edtAddressDistrict.getText().toString().trim();
        String ward = edtAddressWard.getText().toString().trim();
        String detail = edtAddressDetail.getText().toString().trim();
        String noteValue = edtAddressNote.getText().toString().trim();

        if (cbSaveToAddressBook.isChecked()) {
            if (!TokenManager.getInstance(requireContext()).isLoggedIn()) {
                Toast.makeText(getContext(), "Vui lòng đăng nhập để lưu địa chỉ", Toast.LENGTH_SHORT).show();
                return;
            }
            Map<String, Object> data = new HashMap<>();
            data.put("recipient_name", name);
            data.put("phone", phone);
            data.put("city", city);
            data.put("district", district);
            data.put("ward", ward);
            data.put("address_line_1", detail);
            data.put("address_note", noteValue);
            data.put("address_type", "other");

            accountViewModel.addAccountAddress(data);
        } else {
            AddressDto address = new AddressDto();
            address.setRecipientName(name);
            address.setPhone(phone);
            address.setCity(city);
            address.setDistrict(district);
            address.setWard(ward);
            address.setAddressLine1(detail);
            address.setAddressNote(noteValue);
            
            viewModel.setSelectedAddress(address);
            toggleAddressForm(false);
        }
    }

    private void setupShipping(String method, double amount, String estimate) {
        View root = getView();
        if (root == null) return;
        View card = root.findViewById(R.id.layoutCheckoutShipping);
        if (card == null) return;

        TextView tvPrimary = card.findViewById(R.id.tvCheckoutOptionPrimary);
        TextView tvSecondary = card.findViewById(R.id.tvCheckoutOptionSecondary);
        TextView tvValue = card.findViewById(R.id.tvCheckoutOptionRightValue);

        boolean hasMethod = method != null && !method.isEmpty();
        if (hasMethod) {
            tvPrimary.setText(method);
            if (tvSecondary != null) {
                if (estimate != null && !estimate.isEmpty()) {
                    tvSecondary.setText(String.format("Dự kiến giao: %s", estimate));
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
            tvPrimary.setText("Đang tải phương thức vận chuyển...");
            if (tvSecondary != null) tvSecondary.setVisibility(View.GONE);
            if (tvValue != null) tvValue.setVisibility(View.GONE);
        }
    }

    private void setupPayment(String method) {
        View root = getView();
        if (root == null) return;
        View card = root.findViewById(R.id.layoutCheckoutPayment);
        if (card == null) return;

        TextView tvPrimary = card.findViewById(R.id.tvCheckoutOptionPrimary);
        TextView tvSecondary = card.findViewById(R.id.tvCheckoutOptionSecondary);
        TextView tvValue = card.findViewById(R.id.tvCheckoutOptionRightValue);
        ImageView ivIcon = card.findViewById(R.id.ivCheckoutOptionIcon);

        String displayMethod = (method != null && !method.isEmpty()) ? method : "Thanh toán khi nhận hàng (COD)";
        tvPrimary.setText(displayMethod);

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

    @android.annotation.SuppressLint("MissingInflatedId")
    private void bindItems(java.util.List<CheckoutSessionDto.CheckoutItemDto> items) {
        if (items == null || layoutCheckoutItemsList == null) return;
        layoutCheckoutItemsList.removeAllViews();

        for (int i = 0; i < items.size(); i++) {
            CheckoutSessionDto.CheckoutItemDto item = items.get(i);
            if (!item.isSelected()) continue;

            View itemView = getLayoutInflater().inflate(R.layout.item_cart, layoutCheckoutItemsList, false);
            
            View cbSelected = itemView.findViewById(R.id.cbCartSelected);
            if (cbSelected != null) cbSelected.setVisibility(View.GONE);
            
            View btnWishlist = itemView.findViewById(R.id.btnCartWishlist);
            if (btnWishlist != null) btnWishlist.setVisibility(View.GONE);

            TextView tvName = itemView.findViewById(R.id.tvCartProductName);
            TextView tvPriceValue = itemView.findViewById(R.id.tvCartPrice);
            TextView tvQuantityValue = itemView.findViewById(R.id.tvCartQuantity);
            ImageView ivProduct = itemView.findViewById(R.id.ivCartProductImage);
            
            View tvOldPrice = itemView.findViewById(R.id.tvCartOldPrice);
            View tvDiscountBadge = itemView.findViewById(R.id.tvCartDiscount);
            if (tvOldPrice != null) tvOldPrice.setVisibility(View.GONE);
            if (tvDiscountBadge != null) tvDiscountBadge.setVisibility(View.GONE);

            tvName.setText(item.getProductName());
            tvPriceValue.setText(formatPrice(item.getPrice()));
            tvQuantityValue.setText(String.valueOf(item.getQuantity()));

            Glide.with(this)
                    .load(item.getImageUrl() != null ? item.getImageUrl() : "")
                    .placeholder(R.drawable.ic_product)
                    .error(R.drawable.ic_product)
                    .into(ivProduct);

            View layoutVariant = itemView.findViewById(R.id.item_variant_selection);
            if (layoutVariant != null) {
                TextView tvVariantDisplay = layoutVariant.findViewById(R.id.tvVariantName);
                if (tvVariantDisplay != null) {
                    tvVariantDisplay.setText(getDisplayVariantName(item));
                }

                layoutVariant.setOnClickListener(v -> {
                    if (getContext() == null || item.getProductId() == null) return;
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
                                    item.setVariantId(variant.getId());
                                    item.setVariantName(variant.getVariantName());
                                    if (variant.getPrice() != null) item.setPrice(variant.getPrice());
                                    if (variant.getImageUrl() != null && !variant.getImageUrl().isEmpty()) item.setImageUrl(variant.getImageUrl());
                                    item.setQuantity(newQuantity);
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

            View btnDecrease = itemView.findViewById(R.id.btnDecreaseQuantity);
            View btnIncrease = itemView.findViewById(R.id.btnIncreaseQuantity);
            
            if (btnDecrease != null) {
                btnDecrease.setOnClickListener(v -> {
                    if (item.getQuantity() > 1) {
                        item.setQuantity(item.getQuantity() - 1);
                        viewModel.updateItems(items);
                    } else {
                        Toast.makeText(getContext(), "Số lượng tối thiểu là 1", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            
            if (btnIncrease != null) {
                btnIncrease.setOnClickListener(v -> {
                    item.setQuantity(item.getQuantity() + 1);
                    viewModel.updateItems(items);
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
