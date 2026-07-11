package ui.commerce;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.data.model.coupon.CouponDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.feature.voucher.CouponViewModel;
import com.example.frontend.feature.voucher.VoucherSelectAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class VoucherBottomSheetDialog extends BottomSheetDialogFragment {

    private static final float VOUCHER_SHEET_HEIGHT_RATIO = 0.75f;

    private RecyclerView rvVoucherList;
    private VoucherSelectAdapter adapter;
    private CouponViewModel viewModel;
    private TextView tvSelectedVoucherName;
    private View btnConfirm;
    private CouponDto selectedVoucher;
    private OnVoucherAppliedListener listener;

    public interface OnVoucherAppliedListener {
        void onVoucherApplied(CouponDto voucher);
    }

    public void setOnVoucherAppliedListener(OnVoucherAppliedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_voucher, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CouponViewModel.class);

        initViews(view);
        setupRecyclerView();
        observeViewModel();
        
        viewModel.loadAvailableCoupons();
    }

    private void initViews(View view) {
        rvVoucherList = view.findViewById(R.id.rvVoucherList);
        tvSelectedVoucherName = view.findViewById(R.id.tvSelectedVoucherName);
        btnConfirm = view.findViewById(R.id.btnConfirmVoucher);

        if (tvSelectedVoucherName != null) {
            tvSelectedVoucherName.setText("Chưa chọn");
        }

        View btnClose = view.findViewById(R.id.ivCloseVoucherSheet);
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> dismiss());
        }

        if (btnConfirm != null) {
            btnConfirm.setOnClickListener(v -> {
                if (selectedVoucher != null && listener != null) {
                    listener.onVoucherApplied(selectedVoucher);
                }
                dismiss();
            });
        }
    }

    private void setupRecyclerView() {
        adapter = new VoucherSelectAdapter();
        rvVoucherList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvVoucherList.setAdapter(adapter);

        adapter.setOnVoucherSelectedListener(voucher -> {
            selectedVoucher = voucher;
            if (tvSelectedVoucherName != null) {
                tvSelectedVoucherName.setText(voucher.getCouponCode());
            }
        });
    }

    private void observeViewModel() {
        viewModel.getAvailableCouponsResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case LOADING:
                    // Show loading if needed, but the UI doesn't have a dedicated loading view in the layout
                    break;
                case SUCCESS:
                    if (result.data != null) {
                        adapter.setVouchers(result.data);
                    }
                    break;
                case ERROR:
                    Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        setupHeight();
    }

    private void setupHeight() {
        View bottomSheet = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenHeight = displayMetrics.heightPixels;
            int maxSheetHeight = (int) (screenHeight * VOUCHER_SHEET_HEIGHT_RATIO);

            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setPeekHeight(maxSheetHeight);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }
}
