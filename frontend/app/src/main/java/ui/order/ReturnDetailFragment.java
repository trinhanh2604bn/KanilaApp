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
import com.example.frontend.data.model.returnrefund.ReturnDetailDto;
import com.example.frontend.data.remote.NetworkResult;
import ui.common.FragmentNavigationHelper;
import java.util.ArrayList;
import java.util.List;

public class ReturnDetailFragment extends Fragment {

    private static final String ARG_ORDER_ID = "order_id";
    private static final String ARG_ORDER_NUMBER = "order_number";

    private String orderId;
    private String orderNumber;
    private ReturnDetailViewModel viewModel;
    private ReturnMediaAdapter mediaAdapter;

    private View layoutLoading, layoutError, scrollContent, layoutEvidence;
    private TextView tvOrderNumber, tvReturnStatus, tvReturnReason, tvReturnNote, tvShippingMethod, tvRequestedAt;

    public static ReturnDetailFragment newInstance(String orderId, String orderNumber) {
        ReturnDetailFragment fragment = new ReturnDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ORDER_ID, orderId);
        args.putString(ARG_ORDER_NUMBER, orderNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderId = getArguments().getString(ARG_ORDER_ID);
            orderNumber = getArguments().getString(ARG_ORDER_NUMBER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_return_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ReturnDetailViewModel.class);

        initViews(view);
        setupHeader(view);
        observeViewModel();

        viewModel.loadReturnDetail(orderId);
    }

    private void initViews(View view) {
        layoutLoading = view.findViewById(R.id.layoutLoading);
        layoutError = view.findViewById(R.id.layoutError);
        scrollContent = view.findViewById(R.id.scrollReturnDetail);
        layoutEvidence = view.findViewById(R.id.layoutEvidence);

        tvOrderNumber = view.findViewById(R.id.tvOrderNumber);
        tvReturnStatus = view.findViewById(R.id.tvReturnStatus);
        tvReturnReason = view.findViewById(R.id.tvReturnReason);
        tvReturnNote = view.findViewById(R.id.tvReturnNote);
        tvShippingMethod = view.findViewById(R.id.tvShippingMethod);
        tvRequestedAt = view.findViewById(R.id.tvRequestedAt);

        RecyclerView rvMedia = view.findViewById(R.id.rvEvidenceMedia);
        rvMedia.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        mediaAdapter = new ReturnMediaAdapter();
        rvMedia.setAdapter(mediaAdapter);

        view.findViewById(R.id.btnContactSupport).setOnClickListener(v -> 
            FragmentNavigationHelper.replaceFragment(requireActivity(), new ui.support.HelpCenterFragment())
        );
        
        if (orderNumber != null) {
            tvOrderNumber.setText("Mã đơn hàng: " + orderNumber);
        }
    }

    private void setupHeader(View view) {
        View header = view.findViewById(R.id.layoutHeader);
        if (header != null) {
            TextView tvTitle = header.findViewById(R.id.tvTopBarTitle);
            if (tvTitle != null) tvTitle.setText("Chi tiết hoàn trả");

            View btnBack = header.findViewById(R.id.btnTopBarBack);
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
            }
        }
    }

    private void observeViewModel() {
        viewModel.getReturnDetailResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            layoutLoading.setVisibility(result.status == NetworkResult.Status.LOADING ? View.VISIBLE : View.GONE);
            layoutError.setVisibility(result.status == NetworkResult.Status.ERROR ? View.VISIBLE : View.GONE);
            scrollContent.setVisibility(result.status == NetworkResult.Status.SUCCESS ? View.VISIBLE : View.GONE);

            if (result.status == NetworkResult.Status.SUCCESS && result.data != null) {
                bindData(result.data);
            } else if (result.status == NetworkResult.Status.ERROR) {
                TextView tvError = layoutError.findViewById(R.id.tvErrorTitle);
                if (tvError != null) tvError.setText(result.message);
            }
        });
    }

    private void bindData(ReturnDetailDto data) {
        tvReturnStatus.setText("Trạng thái: " + getStatusText(data.getReturnStatus()));
        tvReturnReason.setText(data.getReturnReason());
        tvReturnNote.setText(data.getNote() != null && !data.getNote().isEmpty() ? data.getNote() : "Không có mô tả");
        tvShippingMethod.setText(data.getShippingMethod());
        tvRequestedAt.setText(data.getRequestedAt());

        if (data.getMedia() != null && !data.getMedia().isEmpty()) {
            List<String> urls = new ArrayList<>();
            for (ReturnDetailDto.ReturnMediaDto m : data.getMedia()) {
                urls.add(m.getMediaUrl());
            }
            mediaAdapter.setMediaUrls(urls);
            layoutEvidence.setVisibility(View.VISIBLE);
        } else {
            layoutEvidence.setVisibility(View.GONE);
        }
    }

    private String getStatusText(String status) {
        if (status == null) return "Chờ xử lý";
        switch (status) {
            case "requested": return "Chờ xác nhận";
            case "approved": return "Đã chấp nhận";
            case "received": return "Đã nhận hàng";
            case "completed": return "Hoàn tất";
            case "rejected": return "Bị từ chối";
            default: return status;
        }
    }
}
