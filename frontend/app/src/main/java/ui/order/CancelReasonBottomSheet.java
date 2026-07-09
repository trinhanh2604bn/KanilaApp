package ui.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.frontend.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

public class CancelReasonBottomSheet extends BottomSheetDialogFragment {

    public interface OnReasonSelectedListener {
        void onReasonSelected(String reason);
    }

    private OnReasonSelectedListener listener;

    public void setOnReasonSelectedListener(OnReasonSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_cancel_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RadioGroup rgReasons = view.findViewById(R.id.rgCancelReasons);
        MaterialButton btnConfirm = view.findViewById(R.id.btnConfirmCancel);
        View btnClose = view.findViewById(R.id.btnClose);

        btnClose.setOnClickListener(v -> dismiss());

        rgReasons.setOnCheckedChangeListener((group, checkedId) -> {
            btnConfirm.setEnabled(true);
        });

        btnConfirm.setOnClickListener(v -> {
            int selectedId = rgReasons.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton rb = view.findViewById(selectedId);
                if (listener != null) {
                    listener.onReasonSelected(rb.getText().toString());
                }
                dismiss();
            }
        });
    }
}
