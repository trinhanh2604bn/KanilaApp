package com.example.frontend.feature.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.example.frontend.data.model.order.OrderDto;
import com.example.frontend.utils.ToastHelper;
import java.util.List;

public class OrderListFragment extends Fragment {
    private OrderViewModel viewModel;
    private OrderListAdapter adapter;
    private RecyclerView rvOrders;
    private View layoutLoading, layoutEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        
        initViews(view);
        observeViewModel();
        viewModel.loadMyOrders();
    }

    private void initViews(View view) {
        View topBar = view.findViewById(R.id.layoutTopBar);
        if (topBar != null) {
            TextView tvTitle = topBar.findViewById(R.id.tvTopBarTitle);
            if (tvTitle != null) tvTitle.setText("Đơn hàng của tôi");
            topBar.findViewById(R.id.btnTopBarBack).setOnClickListener(v -> {
                if (getActivity() != null) getActivity().getOnBackPressedDispatcher().onBackPressed();
            });
        }

        rvOrders = view.findViewById(R.id.rvOrders);
        layoutLoading = view.findViewById(R.id.layoutOrderLoading);
        layoutEmpty = view.findViewById(R.id.layoutOrderEmpty);

        adapter = new OrderListAdapter();
        rvOrders.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getOrdersResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case LOADING:
                    layoutLoading.setVisibility(View.VISIBLE);
                    layoutEmpty.setVisibility(View.GONE);
                    rvOrders.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    layoutLoading.setVisibility(View.GONE);
                    bindOrders(result.data);
                    break;
                case EMPTY:
                    layoutLoading.setVisibility(View.GONE);
                    layoutEmpty.setVisibility(View.VISIBLE);
                    rvOrders.setVisibility(View.GONE);
                    break;
                case ERROR:
                    layoutLoading.setVisibility(View.GONE);
                    ToastHelper.showShort(getContext(), result.message);
                    break;
            }
        });
    }

    private void bindOrders(List<OrderDto> orders) {
        if (orders == null || orders.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvOrders.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvOrders.setVisibility(View.VISIBLE);
            adapter.setOrders(orders);
        }
    }
}
