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
            int sheetHeight = (int) (screenHeight * VOUCHER_SHEET_HEIGHT_RATIO);

            ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.height = sheetHeight;
                bottomSheet.setLayoutParams(layoutParams);
            }

            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setPeekHeight(sheetHeight);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setSkipCollapsed(true);
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
