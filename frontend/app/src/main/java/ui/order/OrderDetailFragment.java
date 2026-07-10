package ui.order;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.MainActivity;
import com.example.frontend.R;
import com.example.frontend.data.model.order.OrderDetailDto;
import com.example.frontend.feature.home.HomeProductAdapter;
import com.example.frontend.feature.wishlist.WishlistViewModel;
import java.util.List;
import java.util.Locale;

public class OrderDetailFragment extends Fragment {

    private static final String ARG_ORDER_ID = "order_id";
    private static final String ARG_ORDER_CODE = "order_code";

    private String orderId, orderCode;
    private OrderDetailViewModel viewModel;
    private WishlistViewModel wishlistViewModel;
    private HomeProductAdapter recommendationAdapter;
    
    private View layoutLoading, layoutError, scrollContent;
    private View layoutBanner, layoutRefundDetail, layoutShipping, layoutAddress, layoutOrderDetails;
    private TextView tvBannerStatus, tvBannerTime, tvRefundAmount;
    private TextView tvShippingMethod, tvTrackingNumber;
    private TextView tvRecipientInfo, tvFullAddress;
    private LinearLayout layoutItemsList;
    private TextView tvGrandTotal, tvOrderNumber, tvOrderPlacedTime, tvPaymentMethod;
    private com.google.android.material.button.MaterialButton btnActionPrimary, btnActionSecondary;
    private View btnCopyOrderNumber;

    public static OrderDetailFragment newInstance(String orderId) {
        return newInstance(orderId, null);
    }

    public static OrderDetailFragment newInstance(String orderId, String orderCode) {
        OrderDetailFragment fragment = new OrderDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ORDER_ID, orderId);
        args.putString(ARG_ORDER_CODE, orderCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderId = getArguments().getString(ARG_ORDER_ID);
            orderCode = getArguments().getString(ARG_ORDER_CODE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_order_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(OrderDetailViewModel.class);
        wishlistViewModel = new ViewModelProvider(requireActivity()).get(WishlistViewModel.class);

        initViews(view);
        setupHeader(view);
        setupSupport(view);
        setupRecommendations(view);
        observeViewModel();

        viewModel.loadOrderDetail(orderId);
    }

    private void initViews(View view) {
        layoutLoading = view.findViewById(R.id.layoutLoading);
        layoutError = view.findViewById(R.id.layoutError);
        scrollContent = view.findViewById(R.id.scrollOrderDetail);

        layoutBanner = view.findViewById(R.id.layoutBannerStatus);
        layoutRefundDetail = view.findViewById(R.id.layoutRefundDetail);
        layoutShipping = view.findViewById(R.id.layoutShippingInfo);
        layoutAddress = view.findViewById(R.id.layoutAddressInfo);
        layoutOrderDetails = view.findViewById(R.id.layoutOrderDetails);

        tvBannerStatus = view.findViewById(R.id.tvOrderBannerStatus);
        tvBannerTime = view.findViewById(R.id.tvOrderBannerTime);
        tvRefundAmount = view.findViewById(R.id.tvRefundAmount);

        tvShippingMethod = view.findViewById(R.id.tvOrderShippingMethod);
        tvTrackingNumber = view.findViewById(R.id.tvOrderTrackingNumber);

        tvRecipientInfo = view.findViewById(R.id.tvOrderRecipientNamePhone);
        tvFullAddress = view.findViewById(R.id.tvOrderFullAddress);

        layoutItemsList = view.findViewById(R.id.layoutCheckoutItemsList);
        tvGrandTotal = view.findViewById(R.id.tvOrderGrandTotal);

        tvOrderNumber = view.findViewById(R.id.tvOrderNumber);
        tvOrderPlacedTime = view.findViewById(R.id.tvOrderPlacedTime);
        tvPaymentMethod = view.findViewById(R.id.tvPaymentMethod);

        btnActionPrimary = view.findViewById(R.id.btnActionPrimary);
        btnActionSecondary = view.findViewById(R.id.btnActionSecondary);
        btnCopyOrderNumber = view.findViewById(R.id.btnCopyOrderNumber);
        
        if (btnCopyOrderNumber != null) {
            btnCopyOrderNumber.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Order Number", tvOrderNumber.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), R.string.order_detail_copy_success, Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void setupHeader(View view) {
        View header = view.findViewById(R.id.layoutHeader);
        if (header != null) {
            TextView tvTitle = header.findViewById(R.id.tvTopBarTitle);
            if (tvTitle != null) tvTitle.setText(R.string.order_detail_title);

            View btnBack = header.findViewById(R.id.btnTopBarBack);
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
            }
        }
    }

    private void setupSupport(View view) {
        View menuContact = view.findViewById(R.id.menuContactShop);
        if (menuContact != null) {
            TextView tvTitle = menuContact.findViewById(R.id.tvMenuTitle);
            ImageView ivIcon = menuContact.findViewById(R.id.ivMenuIcon);
            if (tvTitle != null) tvTitle.setText(R.string.order_detail_contact_shop);
            if (ivIcon != null) ivIcon.setImageResource(R.drawable.ic_support);
        }
    }

    private void setupRecommendations(View view) {
        RecyclerView rv = view.findViewById(R.id.rvRecommendedProducts);
        if (rv == null) return;

        rv.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recommendationAdapter = new HomeProductAdapter();
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        recommendationAdapter.setItemWidth((int) (screenWidth * 0.46));
        recommendationAdapter.setOnProductClickListener(new HomeProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(com.example.frontend.model.Product product) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_container, com.example.frontend.feature.product.ProductDetailFragment.newInstance(product.getId()))
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onAddToCartClick(com.example.frontend.model.Product product) {
                // Not implemented
            }
        });
        rv.setAdapter(recommendationAdapter);
    }

    private void observeViewModel() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;
            layoutLoading.setVisibility(state.loading ? View.VISIBLE : View.GONE);
            layoutError.setVisibility(state.error != null ? View.VISIBLE : View.GONE);
            scrollContent.setVisibility(state.order != null ? View.VISIBLE : View.GONE);

            if (state.order != null) {
                bindOrderData(state.order);
                setupActions(state.order.getOrderStatus());
            }

            if (state.recommendations != null && recommendationAdapter != null) {
                recommendationAdapter.setProducts(state.recommendations);
            }

            if (state.cancelSuccess) {
                Toast.makeText(getContext(), getString(R.string.order_detail_cancel_success), Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();
            }
        });
    }

    private void bindOrderData(OrderDetailDto order) {
        String status = order.getOrderStatus();
        
        // 1. Phân loại giao diện BANNER/REFUND
        if ("returned".equals(status)) {
            layoutBanner.setVisibility(View.GONE);
            layoutRefundDetail.setVisibility(View.VISIBLE);
            tvRefundAmount.setText(formatPrice(order.getTotal() != null ? order.getTotal().getGrandTotal() : 0));
            layoutShipping.setVisibility(View.VISIBLE); // Vẫn hiện vận chuyển vì đã từng giao
        } else if ("cancelled".equals(status)) {
            layoutBanner.setVisibility(View.VISIBLE);
            layoutRefundDetail.setVisibility(View.GONE);
            layoutShipping.setVisibility(View.GONE); // Đã hủy thì không có vận chuyển
            layoutBanner.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.status_cancelled_text));
            tvBannerStatus.setText("Đã hủy đơn hàng");
            tvBannerTime.setText("Lý do: Tôi muốn đổi địa chỉ nhận hàng");
        } else {
            layoutBanner.setVisibility(View.VISIBLE);
            layoutRefundDetail.setVisibility(View.GONE);
            layoutShipping.setVisibility(View.VISIBLE);
            layoutBanner.setBackgroundColor(android.graphics.Color.parseColor("#26A69A"));
            tvBannerStatus.setText(getStatusBannerText(status));
            tvBannerTime.setText(getStatusTimeText(status, order));
        }
        
        // 2. Thông tin vận chuyển (Chỉ hiện khi không phải đơn mới/đã hủy)
        if (layoutShipping.getVisibility() == View.VISIBLE) {
            tvShippingMethod.setText("Giao hàng nhanh");
            if (order.getShipment() != null && order.getShipment().getTrackingNumber() != null) {
                tvTrackingNumber.setText(getString(R.string.order_detail_tracking_number, order.getShipment().getTrackingNumber()));
                tvTrackingNumber.setVisibility(View.VISIBLE);
            } else if ("pending".equals(status)) {
                tvTrackingNumber.setText("Thông tin vận đơn sẽ được cập nhật sau");
            } else {
                tvTrackingNumber.setText("Mã vận đơn: KNL" + System.currentTimeMillis() / 10000);
            }
        }

        // 3. Địa chỉ
        OrderDetailDto.OrderAddressDto shippingAddr = null;
        if (order.getAddresses() != null) {
            for (OrderDetailDto.OrderAddressDto addr : order.getAddresses()) {
                if ("shipping".equalsIgnoreCase(addr.getAddressType())) {
                    shippingAddr = addr;
                    break;
                }
            }
        }
        if (shippingAddr != null) {
            tvRecipientInfo.setText(shippingAddr.getRecipientName() + " (" + shippingAddr.getPhone() + ")");
            tvFullAddress.setText(shippingAddr.getAddressLine1() + ", " + shippingAddr.getWard() + ", " + shippingAddr.getDistrict());
        }

        // 4. Danh sách sản phẩm
        layoutItemsList.removeAllViews();
        if (order.getItems() != null) {
            for (OrderDetailDto.OrderItemDetailDto item : order.getItems()) {
                View itemView = getLayoutInflater().inflate(R.layout.item_cart_selected, layoutItemsList, false);
                setupOrderItemView(itemView, item);
                layoutItemsList.addView(itemView);
            }
        }

        if (tvGrandTotal != null && order.getTotal() != null) {
            tvGrandTotal.setText(formatPrice(order.getTotal().getGrandTotal()));
        }

        // 5. Thông tin đơn hàng
        tvOrderNumber.setText(order.getOrderNumber());
        tvOrderPlacedTime.setText(order.getPlacedAt());
        
        View layoutPayment = getView() != null ? getView().findViewById(R.id.layoutPaymentMethod) : null;
        if (layoutPayment != null) {
            tvPaymentMethod.setText(order.getPaymentStatus() != null ? order.getPaymentStatus() : "Thanh toán khi nhận hàng");
            layoutPayment.setVisibility(View.VISIBLE);
        }
    }

    private void setupOrderItemView(View itemView, OrderDetailDto.OrderItemDetailDto item) {
        ((TextView) itemView.findViewById(R.id.tvSelectedCartProductName)).setText(item.getProductName());
        
        String displayVariant = item.getVariantName();
        if (displayVariant != null && displayVariant.contains(" - ")) {
            String[] parts = displayVariant.split(" - ");
            displayVariant = parts[parts.length - 1];
        }
        ((TextView) itemView.findViewById(R.id.tvSelectedCartVariant)).setText(displayVariant);
        
        ((TextView) itemView.findViewById(R.id.tvSelectedCartQuantity)).setText("Số lượng: x" + item.getQuantity());
        ((TextView) itemView.findViewById(R.id.tvSelectedCartPrice)).setText(formatPrice(item.getUnitPrice()));
        
        ImageView ivProduct = itemView.findViewById(R.id.ivSelectedCartProductImage);
        
        // Cập nhật load ảnh giống CheckoutFragment
        com.bumptech.glide.Glide.with(this)
            .load(item.getImageUrl() != null ? item.getImageUrl() : "")
            .placeholder(R.drawable.ic_product)
            .error(R.drawable.ic_product)
            .into(ivProduct);
    }

    private void setupActions(String status) {
        if (status == null || btnActionPrimary == null || btnActionSecondary == null) return;
        btnActionPrimary.setVisibility(View.VISIBLE);
        btnActionSecondary.setVisibility(View.VISIBLE);
        btnActionPrimary.setEnabled(true);
        btnActionSecondary.setEnabled(true);

        switch (status) {
            case "pending":
                btnActionSecondary.setVisibility(View.GONE);
                btnActionPrimary.setText("Hủy đơn hàng");
                btnActionPrimary.setOnClickListener(v -> showCancelDialog());
                break;
            case "confirmed":
                btnActionSecondary.setText("Hủy đơn hàng");
                btnActionSecondary.setOnClickListener(v -> showCancelDialog());
                btnActionPrimary.setText("Liên hệ Shop");
                break;
            case "processing":
                btnActionSecondary.setText("Theo dõi đơn");
                btnActionPrimary.setText("Đã nhận được hàng");
                btnActionPrimary.setEnabled(false);
                break;
            case "completed":
                btnActionSecondary.setText("Trả hàng/Hoàn tiền");
                btnActionPrimary.setText("Đánh giá");
                break;
            case "cancelled":
            case "returned":
                btnActionSecondary.setText("Mua lại");
                btnActionPrimary.setText("Xem chi tiết");
                if ("returned".equals(status)) btnActionSecondary.setVisibility(View.GONE);
                break;
        }
    }

    private String getStatusBannerText(String status) {
        if (status == null) return "Thông tin đơn hàng";
        switch (status) {
            case "pending": return "Chờ người bán xác nhận";
            case "confirmed": return "Người bán đang chuẩn bị hàng";
            case "processing": return "Đơn hàng đang được giao";
            case "completed": return "Giao hàng thành công";
            default: return "Thông tin đơn hàng";
        }
    }

    private String getStatusTimeText(String status, OrderDetailDto order) {
        if ("confirmed".equals(status)) return "Dự kiến giao hàng sau 2-3 ngày";
        if ("processing".equals(status)) return "Shipper đang trên đường giao đến bạn";
        return "Cảm ơn bạn đã mua sắm tại Kanila!";
    }

    private void showCancelDialog() {
        CancelReasonBottomSheet sheet = new CancelReasonBottomSheet();
        sheet.setOnReasonSelectedListener(reason -> viewModel.cancelOrder(orderId, reason));
        sheet.show(getChildFragmentManager(), "CancelReasonBottomSheet");
    }

    private String formatPrice(double price) {
        return String.format(Locale.getDefault(), "%,.0fđ", price).replace(",", ".");
    }
}
