package ui.order;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import java.util.ArrayList;
import java.util.List;

public class OrderListFragment extends Fragment {

    private OrderListViewModel viewModel;
    private OrderAdapter adapter;
    private RecyclerView rvOrders;
    private View layoutLoading, layoutError, layoutEmpty;
    private LinearLayout layoutTabs;
    
    private final List<TextView> tabViews = new ArrayList<>();
    private String currentStatus = null; // null for "All"

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_order_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(OrderListViewModel.class);
        
        initViews(view);
        setupHeader(view);
        setupTabs(view);
        setupRecyclerView();
        observeViewModel();
        
        getParentFragmentManager().setFragmentResultListener("order_detail_result", getViewLifecycleOwner(), (requestKey, result) -> {
            boolean cancelled = result.getBoolean("cancelled", false);
            if (cancelled) {
                viewModel.loadOrders(currentStatus);
            }
        });

        viewModel.loadOrders(null);
    }

    private void initViews(View view) {
        rvOrders = view.findViewById(R.id.rvOrderList);
        layoutLoading = view.findViewById(R.id.layoutLoading);
        layoutError = view.findViewById(R.id.layoutError);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        layoutTabs = view.findViewById(R.id.layoutOrderTabs);
    }

    private void setupHeader(View view) {
        View header = view.findViewById(R.id.layoutHeader);
        TextView tvTitle = header.findViewById(R.id.tvTopBarTitle);
        if (tvTitle != null) tvTitle.setText("Đơn mua");

        ImageButton btnBack = header.findViewById(R.id.btnTopBarBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }
        
        View rightAction = header.findViewById(R.id.layoutTopBarRightAction);
        if (rightAction != null) rightAction.setVisibility(View.GONE);
    }

    private void setupTabs(View view) {
        tabViews.clear();
        tabViews.add(view.findViewById(R.id.tabAll));
        tabViews.add(view.findViewById(R.id.tabPending));
        tabViews.add(view.findViewById(R.id.tabConfirmed));
        tabViews.add(view.findViewById(R.id.tabProcessing));
        tabViews.add(view.findViewById(R.id.tabCompleted));
        tabViews.add(view.findViewById(R.id.tabReturned));
        tabViews.add(view.findViewById(R.id.tabCancelled));

        for (int i = 0; i < tabViews.size(); i++) {
            final int index = i;
            TextView tab = tabViews.get(i);
            tab.setOnClickListener(v -> onTabSelected(index));
        }
    }

    private void onTabSelected(int index) {
        for (int i = 0; i < tabViews.size(); i++) {
            TextView tab = tabViews.get(i);
            if (i == index) {
                tab.setTextColor(ContextCompat.getColor(requireContext(), R.color.button));
            } else {
                tab.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_main));
            }
        }

        switch (index) {
            case 0: currentStatus = null; break;
            case 1: currentStatus = "pending"; break;
            case 2: currentStatus = "confirmed"; break;
            case 3: currentStatus = "processing"; break;
            case 4: currentStatus = "completed"; break;
            case 5: currentStatus = "returned"; break;
            case 6: currentStatus = "cancelled"; break;
        }
        
        viewModel.loadOrders(currentStatus);
    }

    private void setupRecyclerView() {
        adapter = new OrderAdapter();
        adapter.setOnOrderClickListener(order -> {
            OrderDetailFragment fragment = OrderDetailFragment.newInstance(order.getId());
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.main, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        rvOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvOrders.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;
            
            layoutLoading.setVisibility(state.loading ? View.VISIBLE : View.GONE);
            layoutError.setVisibility(state.error != null ? View.VISIBLE : View.GONE);
            layoutEmpty.setVisibility(state.empty ? View.VISIBLE : View.GONE);
            rvOrders.setVisibility(state.orders != null && !state.orders.isEmpty() ? View.VISIBLE : View.GONE);
            
            if (state.orders != null) {
                adapter.setOrders(state.orders);
            }
            
            if (state.error != null) {
                TextView tvError = layoutError.findViewById(R.id.tvErrorTitle);
                if (tvError != null) tvError.setText(state.error);
                View btnRetry = layoutError.findViewById(R.id.btnErrorRetry);
                if (btnRetry != null) btnRetry.setOnClickListener(v -> viewModel.loadOrders(currentStatus));
            }
            
            if (state.empty) {
                TextView tvEmpty = layoutEmpty.findViewById(R.id.tvEmptyTitle);
                if (tvEmpty != null) tvEmpty.setText("Chưa có đơn hàng nào");
            }
        });
    }
}
