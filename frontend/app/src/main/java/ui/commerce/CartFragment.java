package ui.commerce;

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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Canvas;
import com.example.frontend.R;
import com.example.frontend.model.CartItem;
import com.example.frontend.model.Product;

import java.util.ArrayList;
import java.util.List;

import ui.common.ViewUtils;

public class CartFragment extends Fragment {

    private RecyclerView rvCartItems;
    private CartAdapter adapter;
    private CheckBox cbSelectAll;
    private ImageView ivUseCoinsCheck;
    private TextView tvTotalValue, tvDiscountValue;
    private View btnContinueCheckout, btnChooseVoucher;
    
    private boolean useCoins = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupHeader(view);
        setupRecyclerView();
        setupSelectAll();
        setupCoinToggle(view);
        setupActions();
        
        loadCartItems();
        updateSummary();
    }

    private void initViews(View view) {
        rvCartItems = view.findViewById(R.id.rvCartItems);
        cbSelectAll = view.findViewById(R.id.cbCartSelectAll);
        ivUseCoinsCheck = view.findViewById(R.id.ivCartUseCoinsCheck);
        tvTotalValue = view.findViewById(R.id.tvCartTotalValue);
        tvDiscountValue = view.findViewById(R.id.tvCartDiscountValue);
        btnContinueCheckout = view.findViewById(R.id.btnCartContinueCheckout);
        btnChooseVoucher = view.findViewById(R.id.btnCartChooseVoucher);
        
        TextView tvUseCoins = view.findViewById(R.id.tvCartUseCoins);
        if (tvUseCoins != null) {
            tvUseCoins.setText("Sử dụng 100đ xu"); // Placeholder as requested
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
        if (btnSearch != null) {
            // Heart icon on the right if existing cart design supports it
            if (btnSearch instanceof ImageView) {
                ((ImageView) btnSearch).setImageResource(R.drawable.ic_heart_outline);
            }
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
            public void onItemSelectedChanged() {
                updateSelectAllState();
                updateSummary();
            }

            @Override
            public void onQuantityChanged() {
                updateSummary();
            }

            @Override
            public void onVariantClick(CartItem item, int position) {
                showVariantBottomSheet(item, position);
            }
        });

        setupSwipeToReveal();
    }

    private void setupSwipeToReveal() {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Refresh the item so it doesn't get "deleted" from the UI
                adapter.notifyItemChanged(viewHolder.getAdapterPosition());
            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return 0.7f; // Require a long swipe to trigger onSwiped (which we use to reset)
            }

            @Override
            public float getSwipeEscapeVelocity(float defaultValue) {
                return defaultValue * 5f; // Harder to trigger swipe-out by velocity
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    CartAdapter.CartViewHolder holder = (CartAdapter.CartViewHolder) viewHolder;
                    float actionWidth = 160 * recyclerView.getContext().getResources().getDisplayMetrics().density;
                    
                    // Clamp dX
                    float translationX = Math.max(-actionWidth, dX);
                    holder.layoutFront.setTranslationX(translationX);
                } else {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                // The super call might not reset translation of our custom view if we didn't use getDefaultUIUtil()
                // So we do it manually to ensure it snaps back when released (unless we implement stay-open)
                CartAdapter.CartViewHolder holder = (CartAdapter.CartViewHolder) viewHolder;
                holder.layoutFront.setTranslationX(0);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(rvCartItems);
    }

    private void setupSelectAll() {
        View layoutSelectAll = getView().findViewById(R.id.layoutCartSelectAll);
        if (layoutSelectAll != null) {
            layoutSelectAll.setOnClickListener(v -> {
                cbSelectAll.setChecked(!cbSelectAll.isChecked());
                selectAllItems(cbSelectAll.isChecked());
            });
        }
        
        cbSelectAll.setOnClickListener(v -> selectAllItems(cbSelectAll.isChecked()));
    }

    private void selectAllItems(boolean isSelected) {
        for (CartItem item : adapter.getItems()) {
            item.setSelected(isSelected);
        }
        adapter.notifyDataSetChanged();
        updateSummary();
    }

    private void updateSelectAllState() {
        boolean allSelected = true;
        for (CartItem item : adapter.getItems()) {
            if (!item.isSelected()) {
                allSelected = false;
                break;
            }
        }
        cbSelectAll.setChecked(allSelected && !adapter.getItems().isEmpty());
    }

    private void setupCoinToggle(View view) {
        View layoutUseCoins = view.findViewById(R.id.layoutCartUseCoins);
        if (layoutUseCoins != null) {
            layoutUseCoins.setOnClickListener(v -> {
                useCoins = !useCoins;
                ivUseCoinsCheck.setSelected(useCoins);
                updateSummary();
            });
        }
        ivUseCoinsCheck.setSelected(useCoins);
    }

    private void setupActions() {
        btnChooseVoucher.setOnClickListener(v -> showVoucherBottomSheet());

        btnContinueCheckout.setOnClickListener(v -> {
            List<CartItem> selectedItems = new ArrayList<>();
            for (CartItem item : adapter.getItems()) {
                if (item.isSelected()) {
                    selectedItems.add(item);
                }
            }

            if (selectedItems.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng chọn ít nhất 1 sản phẩm", Toast.LENGTH_SHORT).show();
                return;
            }

            // Navigate to CheckoutFragment
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, new CheckoutFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void showVoucherBottomSheet() {
        if (getContext() != null) {
            VoucherBottomSheetDialog dialog = new VoucherBottomSheetDialog(getContext());
            dialog.show();
        }
    }

    private void showVariantBottomSheet(CartItem item, int position) {
        if (getContext() != null) {
            VariantBottomSheetDialog dialog = new VariantBottomSheetDialog(getContext(), item);
            dialog.setOnVariantAppliedListener((variant, quantity) -> {
                item.setVariant(variant);
                item.setQuantity(quantity);
                adapter.notifyItemChanged(position);
                updateSummary();
            });
            dialog.show();
        }
    }

    private void loadCartItems() {
        List<CartItem> items = new ArrayList<>();
        Product p1 = new Product("1", "Kanila", "Kanila Sweet Lip For You", "245.000đ", "5.0", "100", R.drawable.ic_lipstick, "New");
        items.add(new CartItem(p1, "#001 Rose", 1, true));
        items.add(new CartItem(p1, "#002 Pink", 1, true));
        items.add(new CartItem(p1, "#003 Red", 1, false));
        items.add(new CartItem(p1, "#004 Coral", 1, false));
        items.add(new CartItem(p1, "#003 Red", 1, false));
        items.add(new CartItem(p1, "#004 Coral", 1, false));
        
        adapter.setItems(items);
        updateSelectAllState();
    }

    private void updateSummary() {
        long total = 0;
        int selectedCount = 0;
        for (CartItem item : adapter.getItems()) {
            if (item.isSelected()) {
                // Simplified price parsing for demo
                String priceStr = item.getProduct().getPrice().replace(".", "").replace("đ", "");
                long price = Long.parseLong(priceStr);
                total += price * item.getQuantity();
                selectedCount++;
            }
        }

        if (useCoins) {
            total -= 100;
        }
        
        // Apply a fake discount if any item is selected
        long discount = selectedCount > 0 ? 100000 : 0;
        total -= discount;
        
        if (total < 0) total = 0;

        tvTotalValue.setText(formatPrice(total));
        tvDiscountValue.setText("-" + formatPrice(discount) + ", miễn phí vận chuyển");
    }

    private String formatPrice(long price) {
        return String.format("%,d", price).replace(',', '.') + "đ";
    }
}
