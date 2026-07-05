package ui.commerce;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.frontend.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class VoucherBottomSheetDialog extends BottomSheetDialog {

    private static final float VOUCHER_SHEET_HEIGHT_RATIO = 0.65f;

    public VoucherBottomSheetDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_sheet_voucher);

        setOnShowListener(dialog -> setupHeight());
        setupActions();
    }

    private void setupHeight() {
        View bottomSheet = findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            if (getWindow() != null) {
                getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            }
            int screenHeight = displayMetrics.heightPixels;
            int maxSheetHeight = (int) (screenHeight * VOUCHER_SHEET_HEIGHT_RATIO);

            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setMaxHeight(maxSheetHeight);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setSkipCollapsed(true);
            behavior.setFitToContents(true);

            ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                bottomSheet.setLayoutParams(layoutParams);
            }
        }
    }

    private void setupActions() {
        View btnClose = findViewById(R.id.ivCloseVoucherSheet);
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> dismiss());
        }

        View btnConfirm = findViewById(R.id.btnConfirmVoucher);
        if (btnConfirm != null) {
            btnConfirm.setOnClickListener(v -> {
                // TODO: Apply selected voucher logic
                dismiss();
            });
        }
    }
}
