package ui.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.example.frontend.data.model.order.OrderSummaryDto;
import ui.common.FragmentNavigationHelper;

public class OrderTabContentFragment extends Fragment {

    private static final String ARG_STATUS = "order_status";

    private String status;
    private OrderListViewModel viewModel;
    private OrderAdapter adapter;
    private RecyclerView rvOrders;
    private View layoutLoading, layoutError, layoutEmpty;

    public static OrderTabContentFragment newInstance(String status) {
        OrderTabContentFragment fragment = new OrderTabContentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            status = getArguments().getString(ARG_STATUS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_list_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(OrderListViewModel.class);
        
        initViews(view);
        setupRecyclerView();
        observeViewModel();
        
        viewModel.loadOrders(status);
    }

    private void initViews(View view) {
        rvOrders = view.findViewById(R.id.rvOrderList);
        layoutLoading = view.findViewById(R.id.layoutLoading);
        layoutError = view.findViewById(R.id.layoutError);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
    }

    private void setupRecyclerView() {
        adapter = new OrderAdapter();
        adapter.setOnOrderClickListener(new OrderAdapter.OnOrderClickListener() {
            @Override
            public void onOrderClick(OrderSummaryDto order) {
                OrderDetailFragment fragment = OrderDetailFragment.newInstance(order.getId(), order.getOrderNumber());
                FragmentNavigationHelper.replaceFragment(requireActivity(), fragment);
            }

            @Override
            public void onActionClick(OrderSummaryDto order, String action) {
                if ("Đánh giá".equals(action)) {
                    ReviewOrderFragment fragment = ReviewOrderFragment.newInstance(order.getId());
                    FragmentNavigationHelper.replaceFragment(requireActivity(), fragment);
                } else if ("Mua lại".equals(action)) {
                    viewModel.reorderOrder(order.getId());
                } else if ("Trả hàng/Hoàn tiền".equals(action)) {
                    ReturnRefundFragment fragment = ReturnRefundFragment.newInstance(order.getId(), null);
                    FragmentNavigationHelper.replaceFragment(requireActivity(), fragment);
                } else if ("Liên hệ shop".equalsIgnoreCase(action)) {
                    FragmentNavigationHelper.replaceFragment(requireActivity(), new ui.support.HelpCenterFragment());
                }
            }
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

            if (state.reorderSuccess) {
                // Chuyển sang giỏ hàng
                if (getActivity() instanceof com.example.frontend.MainActivity) {
                    ((com.example.frontend.MainActivity) getActivity()).navigateToCart();
                } else {
                    FragmentNavigationHelper.replaceFragment(requireActivity(), new ui.commerce.CartFragment());
                }
            }
            
            if (state.error != null) {
                TextView tvError = layoutError.findViewById(R.id.tvErrorTitle);
                if (tvError != null) tvError.setText(state.error);
                View btnRetry = layoutError.findViewById(R.id.btnErrorRetry);
                if (btnRetry != null) btnRetry.setOnClickListener(v -> viewModel.loadOrders(status));
            }
            
            if (state.empty) {
                TextView tvEmpty = layoutEmpty.findViewById(R.id.tvEmptyTitle);
                if (tvEmpty != null) tvEmpty.setText("Chưa có đơn hàng nào");
            }
        });
    }

    public void refresh() {
        if (viewModel != null) {
            viewModel.loadOrders(status);
        }
    }
}
