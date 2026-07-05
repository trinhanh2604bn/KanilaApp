package com.example.frontend.feature.voucher;

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
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.example.frontend.data.model.coupon.CouponDto;

public class VoucherListFragment extends Fragment {
    private CouponViewModel viewModel;
    private VoucherAdapter adapter;
    private RecyclerView rvVouchers;
    private View layoutLoading, layoutEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_voucher_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CouponViewModel.class);
        
        initViews(view);
        observeViewModel();
        
        // Load available coupons for general wallet, or my coupons if needed
        viewModel.loadAvailableCoupons();
    }

    private void initViews(View view) {
        View topBar = view.findViewById(R.id.layoutTopBar);
        if (topBar != null) {
            TextView tvTitle = topBar.findViewById(R.id.tvTopBarTitle);
            if (tvTitle != null) tvTitle.setText("Ví Voucher");
            topBar.findViewById(R.id.btnTopBarBack).setOnClickListener(v -> {
                if (getActivity() != null) getActivity().getOnBackPressedDispatcher().onBackPressed();
            });
        }

        rvVouchers = view.findViewById(R.id.rvVouchers);
        layoutLoading = view.findViewById(R.id.layoutVoucherLoading);
        layoutEmpty = view.findViewById(R.id.layoutVoucherEmpty);

        adapter = new VoucherAdapter();
        adapter.setOnVoucherClickListener(new VoucherAdapter.OnVoucherClickListener() {
            @Override
            public void onVoucherClick(CouponDto voucher) {
                // Show detail or something
            }

            @Override
            public void onCopyClick(CouponDto voucher) {
                copyToClipboard(voucher.getCouponCode());
            }
        });
        rvVouchers.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getAvailableCouponsResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case LOADING:
                    layoutLoading.setVisibility(View.VISIBLE);
                    layoutEmpty.setVisibility(View.GONE);
                    rvVouchers.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    layoutLoading.setVisibility(View.GONE);
                    if (result.data != null && !result.data.isEmpty()) {
                        layoutEmpty.setVisibility(View.GONE);
                        rvVouchers.setVisibility(View.VISIBLE);
                        adapter.setVouchers(result.data);
                    } else {
                        layoutEmpty.setVisibility(View.VISIBLE);
                        rvVouchers.setVisibility(View.GONE);
                    }
                    break;
                case ERROR:
                    layoutLoading.setVisibility(View.GONE);
                    Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Voucher Code", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getContext(), "Đã sao chép mã: " + text, Toast.LENGTH_SHORT).show();
    }
}
