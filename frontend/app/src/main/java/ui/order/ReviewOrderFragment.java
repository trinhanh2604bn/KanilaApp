package ui.order;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.example.frontend.data.model.order.ReviewOrderItemsDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.feature.product.ReviewHubFragment;
import ui.common.FragmentNavigationHelper;
import ui.order.ReviewDetailFragment;
import ui.order.ReviewWriteFragment;

public class ReviewOrderFragment extends Fragment {

    private static final String ARG_ORDER_ID = "order_id";

    private String orderId;
    private ReviewOrderViewModel viewModel;
    private ReviewOrderAdapter adapter;

    private View layoutLoading, layoutError;
    private TextView tvOrderStatus, tvDeliveredDate, tvOrderCode, tvProductCount;

    public static ReviewOrderFragment newInstance(String orderId) {
        ReviewOrderFragment fragment = new ReviewOrderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ORDER_ID, orderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderId = getArguments().getString(ARG_ORDER_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_review_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ReviewOrderViewModel.class);

        initViews(view);
        setupHeader(view);
        setupRecyclerView(view);
        observeViewModel();

        viewModel.loadReviewItems(orderId);
    }

    private void initViews(View view) {
        layoutLoading = view.findViewById(R.id.layoutLoading);
        layoutError = view.findViewById(R.id.layoutError);
        tvOrderStatus = view.findViewById(R.id.tvOrderStatus);
        tvDeliveredDate = view.findViewById(R.id.tvDeliveredDate);
        tvOrderCode = view.findViewById(R.id.tvOrderCode);
        tvProductCount = view.findViewById(R.id.tvProductCount);

        view.findViewById(R.id.btnCopyOrderCode).setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Order Code", tvOrderCode.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), R.string.order_detail_copy_success, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupHeader(View view) {
        View header = view.findViewById(R.id.layoutHeader);
        if (header != null) {
            TextView tvTitle = header.findViewById(R.id.tvTopBarTitle);
            if (tvTitle != null) tvTitle.setText("Đánh giá đơn hàng");

            View btnBack = header.findViewById(R.id.btnTopBarBack);
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
            }

            View btnHelp = header.findViewById(R.id.btnTopBarSearch);
            if (btnHelp != null && btnHelp instanceof android.widget.ImageButton) {
                ((android.widget.ImageButton) btnHelp).setImageResource(R.drawable.ic_shortcut_help);
            }
        }
    }

    private void setupRecyclerView(View view) {
        RecyclerView rv = view.findViewById(R.id.rvReviewProducts);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ReviewOrderAdapter(item -> {
            if ("reviewed".equals(item.getReviewStatus())) {
                // Navigate to Review Detail screen
                if (item.getReviewId() != null) {
                    FragmentNavigationHelper.replaceFragment(requireActivity(), ReviewDetailFragment.newInstance(item.getReviewId()));
                } else {
                    Toast.makeText(getContext(), "Review ID not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Navigate to Review Creation screen
                FragmentNavigationHelper.replaceFragment(requireActivity(), ReviewWriteFragment.newInstance(item.getOrderItemId()));
            }
        });
        rv.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getReviewItems().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            layoutLoading.setVisibility(result.status == NetworkResult.Status.LOADING ? View.VISIBLE : View.GONE);
            layoutError.setVisibility(result.status == NetworkResult.Status.ERROR ? View.VISIBLE : View.GONE);

            if (result.status == NetworkResult.Status.SUCCESS && result.data != null) {
                bindData(result.data);
            }
        });
    }

    private void bindData(ReviewOrderItemsDto data) {
        tvOrderCode.setText(data.getOrderCode());
        tvProductCount.setText(getString(R.string.chat_cart_items_count, data.getItems().size()));

        // Date formatting or display
        if (data.getDeliveredAt() != null) {
            // Simple display for now: 2025-05-15T... -> 15/05/2025
            String rawDate = data.getDeliveredAt().split("T")[0];
            String[] parts = rawDate.split("-");
            if (parts.length == 3) {
                tvDeliveredDate.setText("Đã giao " + parts[2] + "/" + parts[1] + "/" + parts[0]);
            } else {
                tvDeliveredDate.setText("Đã giao " + rawDate);
            }
        }

        adapter.setItems(data.getItems());
    }
}
