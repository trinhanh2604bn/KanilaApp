package ui.commerce;

import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.data.model.cart.CartDto;
import com.example.frontend.data.model.cart.CartItemDto;
import com.example.frontend.data.model.coupon.CouponDto;
import com.example.frontend.data.model.product.ProductDetailResponse;
import com.example.frontend.data.remote.ApiClient;
import com.example.frontend.data.remote.ApiResponse;
import com.example.frontend.data.remote.ApiService;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.feature.cart.CartViewModel;
import com.example.frontend.feature.product.VariantSelectorBottomSheet;
import com.example.frontend.feature.wishlist.WishlistViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ui.common.ViewUtils;

public class CartFragment extends Fragment {

    private RecyclerView rvCartItems;
    private CartAdapter adapter;
    private CheckBox cbSelectAll;
    private ImageView ivUseCoinsCheck;
    private TextView tvTotalValue, tvDiscountValue;
    private View btnContinueCheckout, btnChooseVoucher;
    private View layoutCartLoading, layoutCartEmpty, layoutCartContent, layoutCartCheckoutSummary;

    private CartViewModel viewModel;
    private WishlistViewModel wishlistViewModel;
    private CouponDto selectedVoucher;
    private boolean useCoins = false;
    private boolean isUpdatingSelectAll = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(CartViewModel.class);
        wishlistViewModel = new ViewModelProvider(this).get(WishlistViewModel.class);

        initViews(view);
        setupHeader(view);
        setupRecyclerView();
        setupSelectAll();
        setupCoinToggle(view);
        setupActions();
        observeViewModel();

        viewModel.loadCart();
    }

    private void initViews(View view) {
        rvCartItems = view.findViewById(R.id.rvCartItems);
        cbSelectAll = view.findViewById(R.id.cbCartSelectAll);
        ivUseCoinsCheck = view.findViewById(R.id.ivCartUseCoinsCheck);
        tvTotalValue = view.findViewById(R.id.tvCartTotalValue);
        tvDiscountValue = view.findViewById(R.id.tvCartDiscountValue);
        btnContinueCheckout = view.findViewById(R.id.btnCartContinueCheckout);
        btnChooseVoucher = view.findViewById(R.id.btnCartChooseVoucher);

        layoutCartLoading = view.findViewById(R.id.viewCartLoading);
        layoutCartEmpty = view.findViewById(R.id.viewCartEmpty);
        layoutCartContent = view.findViewById(R.id.layoutCartContent);
        layoutCartCheckoutSummary = view.findViewById(R.id.layoutCartCheckoutSummary);

        TextView tvUseCoins = view.findViewById(R.id.tvCartUseCoins);
        if (tvUseCoins != null) {
            tvUseCoins.setText("Sử dụng Kanila xu");
        }

        if (ivUseCoinsCheck != null) {
            ivUseCoinsCheck.setSelected(useCoins);
        }
    }

    private void setupHeader(View view) {
        View header = view.findViewById(R.id.layoutTopBar);
        if (header == null) return;

        TextView tvTitle = header.findViewById(R.id.tvTopBarTitle);
        if (tvTitle != null) {
            tvTitle.setText(R.string.cart);
        }

        View btnWishlist = header.findViewById(R.id.btnTopBarSearch);
        if (btnWishlist instanceof ImageView) {
            ((ImageView) btnWishlist).setImageResource(R.drawable.ic_heart_outline);
            btnWishlist.setVisibility(View.VISIBLE);

            btnWishlist.setOnClickListener(v -> {
                if (com.example.frontend.data.remote.TokenManager.getInstance(getContext()).isLoggedIn()) {
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.main, new com.example.frontend.feature.wishlist.WishlistFragment())
                            .addToBackStack(null)
                            .commit();
                } else {
                    com.example.frontend.core.auth.PendingAuthAction action = new com.example.frontend.core.auth.PendingAuthAction(
                            com.example.frontend.core.auth.PendingAuthAction.ActionType.OPEN_WISHLIST,
                            "Cart",
                            0,
                            null
                    );
                    com.example.frontend.core.auth.AuthNavigationHelper.showAuthPrompt(requireActivity(), action);
                }
            });
        }

        View btnBack = header.findViewById(R.id.btnTopBarBack);
        if (btnBack != null) {
            ViewUtils.applyClickAnimation(btnBack);
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().getOnBackPressedDispatcher().onBackPressed();
                }
            });
        }
    }

    private void setupRecyclerView() {
        adapter = new CartAdapter();
        rvCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCartItems.setAdapter(adapter);

        rvCartItems.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    if (adapter != null && adapter.getSwipedPosition() != -1) {
                        adapter.setSwipedPosition(-1);
                    }
                }
            }
        });

        adapter.setOnCartItemChangeListener(new CartAdapter.OnCartItemChangeListener() {
            @Override
            public void onItemSelectedChanged(CartItemDto item, boolean isSelected) {
                if (item == null || item.getId() == null) return;

                item.setSelected(isSelected);
                updateSelectAllState();
                updateSummaryLocal();

                viewModel.toggleItemSelection(item.getId(), isSelected);
            }

            @Override
            public void onQuantityChanged(CartItemDto item, int newQuantity) {
                if (item == null || item.getId() == null) return;

                String token = com.example.frontend.data.remote.TokenManager.getInstance(getContext()).getAccessToken();
                Log.d("CartFragment", "Quantity update: ID=" + item.getId() +
                        ", Product=" + item.getProductNameSnapshot() +
                        ", OldQty=" + item.getQuantity() +
                        ", NewQty=" + newQuantity +
                        ", TokenExists=" + (token != null && !token.isEmpty()));

                item.setQuantity(newQuantity);
                updateSummaryLocal();

                viewModel.updateItemQuantity(item.getId(), newQuantity);
            }

            @Override
            public void onVariantClick(CartItemDto item, int position) {
                if (item == null || getContext() == null) return;

                // Show loading or just start fetching
                ApiService apiService = ApiClient.getClient(getContext()).create(ApiService.class);
                apiService.getProductDetail(item.getProductId()).enqueue(new Callback<ApiResponse<ProductDetailResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<ProductDetailResponse>> call, Response<ApiResponse<ProductDetailResponse>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            ProductDetailResponse detail = response.body().getData();
                            if (detail != null && detail.getProduct() != null) {
                                VariantSelectorBottomSheet dialog = VariantSelectorBottomSheet.newInstance(
                                        detail.getProduct(),
                                        detail.getVariants(),
                                        VariantSelectorBottomSheet.ActionMode.ADD_TO_CART
                                );
                                dialog.setListener((variant, mode, quantity) -> {
                                    if (variant != null) {
                                        item.setVariantId(variant.getId());
                                        item.setVariantNameSnapshot(variant.getVariantName());
                                        item.setSkuSnapshot(variant.getSku());
                                        if (variant.getImageUrl() != null && !variant.getImageUrl().isEmpty()) {
                                            item.setImageUrlSnapshot(variant.getImageUrl());
                                        }
                                        if (variant.getPrice() != null) {
                                            item.setFinalUnitPriceAmount(variant.getPrice());
                                        }

                                        // Update UI immediately
                                        item.setQuantity(quantity);
                                        adapter.notifyItemChanged(position);
                                        updateSummaryLocal();

                                        viewModel.updateItemVariant(item.getId(), variant.getId(), quantity);
                                    }
                                });
                                dialog.show(getChildFragmentManager(), "VariantSelector");
                            }
                        } else {
                            Toast.makeText(getContext(), "Không thể tải thông tin sản phẩm", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<ProductDetailResponse>> call, Throwable t) {
                        Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onDeleteClick(CartItemDto item, int position) {
                if (item == null || item.getId() == null) return;
                viewModel.removeItem(item.getId());
            }

            @Override
            public void onWishlistClick(CartItemDto item, int position) {
                if (item == null || item.getProductId() == null) return;

                boolean wasWishlisted = !item.isFavorite(); // Since it was toggled in adapter

                if (com.example.frontend.data.remote.TokenManager.getInstance(getContext()).isLoggedIn()) {
                    wishlistViewModel.toggleWishlist(item.getProductId(), wasWishlisted);

                    String message = !wasWishlisted ? "Đã thêm vào yêu thích" : "Đã xóa khỏi yêu thích";
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                } else {
                    // Rollback UI state in adapter
                    item.setFavorite(wasWishlisted);
                    adapter.notifyItemChanged(position);

                    Bundle extras = new Bundle();
                    extras.putString("productId", item.getProductId());
                    extras.putBoolean("wasWishlisted", wasWishlisted);

                    com.example.frontend.core.auth.PendingAuthAction action = new com.example.frontend.core.auth.PendingAuthAction(
                            com.example.frontend.core.auth.PendingAuthAction.ActionType.ADD_TO_WISHLIST,
                            "Cart",
                            0,
                            extras
                    );
                    com.example.frontend.core.auth.AuthNavigationHelper.showAuthPrompt(requireActivity(), action);
                }
            }

            @Override
            public void onSimilarClick(CartItemDto item, int position) {
                if (getActivity() != null) {
                    // Điều hướng sang trang ProductListingFragment với category mặc định "Face" (hoặc logic tương tự)
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main, com.example.frontend.ui.category.ProductListingFragment.newCategoryInstance("Face"))
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        setupSwipeToReveal();
    }

    private void observeViewModel() {
        viewModel.getCartResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            switch (result.status) {
                case LOADING:
                    Log.d("CartFragment", "Cart loading...");
                    showLoading();
                    break;

                case SUCCESS:
                    Log.d("CartFragment", "Cart API response received: " + (result.data != null ? result.data.getItems().size() : 0) + " items");
                    showContent(result.data);
                    break;

                case EMPTY:
                    showEmpty();
                    break;

                case ERROR:
                    showError(result.message);
                    break;
            }
        });

        wishlistViewModel.getStatusResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.status == NetworkResult.Status.SUCCESS && result.data != null) {
                Map<String, Boolean> statusMap = result.data;
                if (adapter != null && adapter.getItems() != null) {
                    for (CartItemDto item : adapter.getItems()) {
                        Boolean isFavorite = statusMap.get(item.getProductId());
                        if (isFavorite != null) {
                            item.setFavorite(isFavorite);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void showLoading() {
        if (layoutCartLoading != null) {
            layoutCartLoading.setVisibility(View.VISIBLE);
        }

        if (layoutCartEmpty != null) {
            layoutCartEmpty.setVisibility(View.GONE);
        }

        // Only hide list if it's currently empty to avoid flickering on item updates
        if (rvCartItems != null && (adapter == null || adapter.getItemCount() == 0)) {
            rvCartItems.setVisibility(View.GONE);
        }

        if (layoutCartCheckoutSummary != null && (adapter == null || adapter.getItemCount() == 0)) {
            layoutCartCheckoutSummary.setVisibility(View.GONE);
        }
    }

    private void showContent(CartDto cart) {
        boolean isEmpty = cart == null
                || cart.getItems() == null
                || cart.getItems().isEmpty();

        if (isEmpty) {
            showEmpty();
            return;
        }

        if (layoutCartLoading != null) {
            layoutCartLoading.setVisibility(View.GONE);
        }

        if (layoutCartEmpty != null) {
            layoutCartEmpty.setVisibility(View.GONE);
        }

        if (rvCartItems != null) {
            rvCartItems.setVisibility(View.VISIBLE);
        }

        if (layoutCartCheckoutSummary != null) {
            layoutCartCheckoutSummary.setVisibility(View.VISIBLE);
        }

        adapter.setItems(cart.getItems());

        // Load wishlist status for all items in cart
        if (cart.getItems() != null && !cart.getItems().isEmpty()) {
            List<String> productIds = new ArrayList<>();
            for (CartItemDto item : cart.getItems()) {
                if (item.getProductId() != null) {
                    productIds.add(item.getProductId());
                }
            }
            wishlistViewModel.loadWishlistStatus(productIds);
        }

        updateSummary(cart);
        updateSelectAllState();
    }

    private void showEmpty() {
        if (layoutCartLoading != null) {
            layoutCartLoading.setVisibility(View.GONE);
        }

        if (layoutCartEmpty != null) {
            layoutCartEmpty.setVisibility(View.VISIBLE);
        }

        if (rvCartItems != null) {
            rvCartItems.setVisibility(View.GONE);
        }

        if (layoutCartCheckoutSummary != null) {
            layoutCartCheckoutSummary.setVisibility(View.GONE);
        }

        if (adapter != null) {
            adapter.setItems(null);
        }

        updateEmptySummary();
    }

    private void showError(String message) {
        if (layoutCartLoading != null) {
            layoutCartLoading.setVisibility(View.GONE);
        }

        boolean hasCurrentItems = adapter != null
                && adapter.getItems() != null
                && !adapter.getItems().isEmpty();

        if (!hasCurrentItems) {
            showEmpty();
        } else {
            if (rvCartItems != null) {
                rvCartItems.setVisibility(View.VISIBLE);
            }

            if (layoutCartCheckoutSummary != null) {
                layoutCartCheckoutSummary.setVisibility(View.VISIBLE);
            }
        }

        if (message != null && !message.trim().isEmpty()) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSwipeToReveal() {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                int swipeFlags;
                if (adapter != null && position == adapter.getSwipedPosition()) {
                    swipeFlags = ItemTouchHelper.RIGHT;
                } else {
                    swipeFlags = ItemTouchHelper.LEFT;
                }
                return makeMovementFlags(0, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                boolean isOpened = (direction == ItemTouchHelper.LEFT);
                Log.d("CartSwipe", "Swipe Event - Position: " + position +
                        ", Direction: " + (isOpened ? "LEFT (Open)" : "RIGHT (Close)") +
                        ", State: " + (isOpened ? "Opened" : "Closed"));

                if (isOpened) {
                    adapter.setSwipedPosition(position);
                } else {
                    adapter.setSwipedPosition(-1);
                }
            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                // Return a threshold based on action width
                float actionWidth = 160 * viewHolder.itemView.getContext().getResources().getDisplayMetrics().density;
                float threshold = (actionWidth / viewHolder.itemView.getWidth()) * 0.5f;
                return Math.max(0.1f, Math.min(threshold, 0.5f));
            }

            @Override
            public float getSwipeEscapeVelocity(float defaultValue) {
                return defaultValue * 10;
            }

            @Override
            public void onChildDraw(@NonNull Canvas c,
                                    @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX,
                                    float dY,
                                    int actionState,
                                    boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    CartAdapter.CartViewHolder holder = (CartAdapter.CartViewHolder) viewHolder;
                    float actionWidth = 160 * recyclerView.getContext().getResources().getDisplayMetrics().density;
                    int currentPos = viewHolder.getAdapterPosition();

                    if (isCurrentlyActive && adapter.getSwipedPosition() != -1 && adapter.getSwipedPosition() != currentPos) {
                        adapter.setSwipedPosition(-1);
                    }

                    float translationX;
                    if (currentPos == adapter.getSwipedPosition()) {
                        translationX = Math.min(0, -actionWidth + dX);
                    } else {
                        translationX = Math.max(-actionWidth, dX);
                    }

                    Log.v("CartSwipe", "Swipe Drawing - Position: " + currentPos +
                            ", dX: " + dX + ", translationX: " + translationX +
                            ", isCurrentlyActive: " + isCurrentlyActive);

                    holder.layoutFront.setTranslationX(translationX);
                } else {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                CartAdapter.CartViewHolder holder = (CartAdapter.CartViewHolder) viewHolder;

                if (position != -1 && position == adapter.getSwipedPosition()) {
                    float actionWidth = 160 * recyclerView.getContext().getResources().getDisplayMetrics().density;
                    holder.layoutFront.setTranslationX(-actionWidth);
                } else {
                    holder.layoutFront.setTranslationX(0);
                }
                super.clearView(recyclerView, viewHolder);
            }
        };

        new ItemTouchHelper(callback).attachToRecyclerView(rvCartItems);
    }

    private void setupSelectAll() {
        View root = getView();
        if (root == null || cbSelectAll == null) return;

        View layoutSelectAll = root.findViewById(R.id.layoutCartSelectAll);

        if (layoutSelectAll != null) {
            layoutSelectAll.setOnClickListener(v -> {
                boolean newCheckedState = !cbSelectAll.isChecked();
                applySelectAllState(newCheckedState);
            });
        }

        cbSelectAll.setOnClickListener(v -> {
            boolean newCheckedState = cbSelectAll.isChecked();
            applySelectAllState(newCheckedState);
        });
    }

    private void applySelectAllState(boolean isChecked) {
        if (isUpdatingSelectAll) return;

        isUpdatingSelectAll = true;

        if (cbSelectAll != null && cbSelectAll.isChecked() != isChecked) {
            cbSelectAll.setChecked(isChecked);
        }

        if (adapter != null && adapter.getItems() != null) {
            for (CartItemDto item : adapter.getItems()) {
                if (item != null) {
                    item.setSelected(isChecked);
                }
            }

            adapter.notifyDataSetChanged();
            updateSummaryLocal();
        }

        isUpdatingSelectAll = false;

        viewModel.selectAllItems(isChecked);
    }

    private void updateSelectAllState() {
        if (adapter == null || cbSelectAll == null) return;

        boolean allSelected = true;
        List<CartItemDto> items = adapter.getItems();

        if (items == null || items.isEmpty()) {
            setSelectAllCheckedSilently(false);
            return;
        }

        for (CartItemDto item : items) {
            if (item == null || !item.isSelected()) {
                allSelected = false;
                break;
            }
        }

        setSelectAllCheckedSilently(allSelected);
    }

    private void setSelectAllCheckedSilently(boolean checked) {
        if (cbSelectAll == null) return;

        isUpdatingSelectAll = true;
        cbSelectAll.setChecked(checked);
        isUpdatingSelectAll = false;
    }

    private void setupCoinToggle(View view) {
        View layoutUseCoins = view.findViewById(R.id.layoutCartUseCoins);
        if (layoutUseCoins != null) {
            layoutUseCoins.setOnClickListener(v -> {
                useCoins = !useCoins;

                if (ivUseCoinsCheck != null) {
                    ivUseCoinsCheck.setSelected(useCoins);
                }

                updateSummaryLocal();
            });
        }
    }

    private void setupActions() {
        if (btnChooseVoucher != null) {
            btnChooseVoucher.setOnClickListener(v -> {
                VoucherBottomSheetDialog dialog = new VoucherBottomSheetDialog();
                dialog.setOnVoucherAppliedListener(voucher -> {
                    selectedVoucher = voucher;
                    if (btnChooseVoucher instanceof com.google.android.material.button.MaterialButton) {
                        ((com.google.android.material.button.MaterialButton) btnChooseVoucher).setText(voucher.getCouponCode());
                    }
                    updateSummaryLocal();
                });
                dialog.show(getChildFragmentManager(), "VoucherSheet");
            });
        }

        if (btnContinueCheckout != null) {
            btnContinueCheckout.setOnClickListener(v -> {
                boolean hasSelection = false;

                if (adapter != null && adapter.getItems() != null) {
                    for (CartItemDto item : adapter.getItems()) {
                        if (item != null && item.isSelected()) {
                            hasSelection = true;
                            break;
                        }
                    }
                }

                if (!hasSelection) {
                    Toast.makeText(getContext(), "Vui lòng chọn ít nhất 1 sản phẩm", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (getActivity() != null) {
                    List<CartItemDto> selectedItems = new java.util.ArrayList<>();
                    if (adapter != null && adapter.getItems() != null) {
                        for (CartItemDto item : adapter.getItems()) {
                            if (item != null && item.isSelected()) {
                                selectedItems.add(item);
                            }
                        }
                    }

                    CheckoutFragment checkoutFragment = new CheckoutFragment();
                    Bundle args = new Bundle();
                    args.putSerializable("selected_items", (java.io.Serializable) selectedItems);
                    args.putDouble("coins_discount", useCoins ? 20000.0 : 0.0);
                    if (selectedVoucher != null) {
                        args.putSerializable("selected_voucher", selectedVoucher);
                    }
                    checkoutFragment.setArguments(args);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main, checkoutFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }
    }

    private void updateSummary(CartDto cart) {
        if (cart == null) {
            updateEmptySummary();
            return;
        }

        if (tvTotalValue != null) {
            tvTotalValue.setText(formatPrice(cart.getTotalAmount()));
        }

        if (tvDiscountValue != null) {
            tvDiscountValue.setText("-" + formatPrice(cart.getDiscountAmount()) + ", miễn phí vận chuyển");
        }
    }

    private void updateEmptySummary() {
        if (tvTotalValue != null) {
            tvTotalValue.setText(formatPrice(0));
        }

        if (tvDiscountValue != null) {
            tvDiscountValue.setText(formatPrice(0));
        }

        setSelectAllCheckedSilently(false);

        if (ivUseCoinsCheck != null) {
            ivUseCoinsCheck.setSelected(false);
        }

        useCoins = false;
    }

    private void updateSummaryLocal() {
        if (adapter == null || adapter.getItems() == null) return;

        double subtotal = 0;
        for (CartItemDto item : adapter.getItems()) {
            if (item != null && item.isSelected()) {
                subtotal += item.getFinalUnitPriceAmount() * item.getQuantity();
            }
        }

        double discount = 0;
        if (selectedVoucher != null) {
            if ("percentage".equalsIgnoreCase(selectedVoucher.getDiscountType())) {
                discount = subtotal * (selectedVoucher.getDiscountValue() / 100.0);
                if (selectedVoucher.getMaxDiscountAmount() > 0) {
                    discount = Math.min(discount, selectedVoucher.getMaxDiscountAmount());
                }
            } else {
                discount = selectedVoucher.getDiscountValue();
            }
        }
        discount = Math.min(discount, subtotal);

        double coins = useCoins ? 20000 : 0;
        double total = subtotal - discount - coins;
        if (total < 0) total = 0;

        if (tvTotalValue != null) {
            tvTotalValue.setText(formatPrice(total));
        }

        if (tvDiscountValue != null) {
            String text = "-" + formatPrice(discount);
            if (useCoins) {
                text += " - " + formatPrice(coins) + " xu";
            }
            if (selectedVoucher != null) {
                text += " (" + selectedVoucher.getCouponCode() + ")";
            }
            tvDiscountValue.setText(text + ", miễn phí vận chuyển");
        }
    }

    private String formatPrice(double price) {
        return String.format(Locale.US, "%,.0fđ", price).replace(",", ".");
    }
}