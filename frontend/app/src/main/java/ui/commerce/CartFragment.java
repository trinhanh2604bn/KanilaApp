package ui.commerce;

import android.graphics.Canvas;
import android.os.Bundle;
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
import com.example.frontend.feature.cart.CartViewModel;

import java.util.List;
import java.util.Locale;

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
    private boolean useCoins = false;

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

        View btnSearch = header.findViewById(R.id.btnTopBarSearch);
        if (btnSearch instanceof ImageView) {
            ((ImageView) btnSearch).setImageResource(R.drawable.ic_heart_outline);
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

        adapter.setOnCartItemChangeListener(new CartAdapter.OnCartItemChangeListener() {
            @Override
            public void onItemSelectedChanged(CartItemDto item, boolean isSelected) {
                if (item == null || item.getId() == null) return;
                viewModel.toggleItemSelection(item.getId(), isSelected);
            }

            @Override
            public void onQuantityChanged(CartItemDto item, int newQuantity) {
                if (item == null || item.getId() == null) return;
                viewModel.updateItemQuantity(item.getId(), newQuantity);
            }

            @Override
            public void onVariantClick(CartItemDto item, int position) {
                // showVariantBottomSheet(item, position);
            }

            @Override
            public void onDeleteClick(CartItemDto item, int position) {
                if (item == null || item.getId() == null) return;
                viewModel.removeItem(item.getId());
            }
        });

        setupSwipeToReveal();
    }

    private void observeViewModel() {
        viewModel.getCartResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            switch (result.status) {
                case LOADING:
                    showLoading();
                    break;

                case SUCCESS:
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
    }

    private void showLoading() {
        if (layoutCartLoading != null) {
            layoutCartLoading.setVisibility(View.VISIBLE);
        }

        if (layoutCartEmpty != null) {
            layoutCartEmpty.setVisibility(View.GONE);
        }

        if (rvCartItems != null) {
            rvCartItems.setVisibility(View.GONE);
        }

        if (layoutCartCheckoutSummary != null) {
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
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.notifyItemChanged(viewHolder.getAdapterPosition());
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
                    float translationX = Math.max(-actionWidth, dX);
                    holder.layoutFront.setTranslationX(translationX);
                } else {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                CartAdapter.CartViewHolder holder = (CartAdapter.CartViewHolder) viewHolder;
                holder.layoutFront.setTranslationX(0);
            }
        };

        new ItemTouchHelper(callback).attachToRecyclerView(rvCartItems);
    }

    private void setupSelectAll() {
        View root = getView();
        if (root == null) return;

        View layoutSelectAll = root.findViewById(R.id.layoutCartSelectAll);
        if (layoutSelectAll != null) {
            layoutSelectAll.setOnClickListener(v -> {
                if (cbSelectAll == null) return;
                boolean isChecked = !cbSelectAll.isChecked();
                cbSelectAll.setChecked(isChecked);
                viewModel.selectAllItems(isChecked);
            });
        }
    }

    private void updateSelectAllState() {
        if (adapter == null || cbSelectAll == null) return;

        boolean allSelected = true;
        List<CartItemDto> items = adapter.getItems();

        if (items == null || items.isEmpty()) {
            cbSelectAll.setChecked(false);
            return;
        }

        for (CartItemDto item : items) {
            if (item == null || !item.isSelected()) {
                allSelected = false;
                break;
            }
        }

        cbSelectAll.setChecked(allSelected);
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
                if (getContext() != null) {
                    new VoucherBottomSheetDialog(getContext()).show();
                }
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
                    checkoutFragment.setArguments(args);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_fragment_container, checkoutFragment)
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

        if (cbSelectAll != null) {
            cbSelectAll.setChecked(false);
        }

        if (ivUseCoinsCheck != null) {
            ivUseCoinsCheck.setSelected(false);
        }

        useCoins = false;
    }

    private void updateSummaryLocal() {
        // Simple local update for preview/mock mode
        if (adapter == null || adapter.getItems() == null) return;

        double subtotal = 0;
        for (CartItemDto item : adapter.getItems()) {
            if (item != null && item.isSelected()) {
                subtotal += item.getFinalUnitPriceAmount() * item.getQuantity();
            }
        }

        double discount = subtotal > 0 ? 100000 : 0;
        double coins = useCoins ? 20000 : 0; // Mock coins
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
            tvDiscountValue.setText(text + ", miễn phí vận chuyển");
        }
    }

    private String formatPrice(double price) {
        return String.format(Locale.US, "%,.0fđ", price).replace(",", ".");
    }
}
