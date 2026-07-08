package ui.community;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.ArrayList;
import java.util.List;

public class ReportPostBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_POST_ID = "arg_post_id";
    private String postId;
    private RecyclerView rvReasons;
    private Button btnSubmit;
    private ImageButton btnCloseReport;
    private ReportReasonAdapter adapter;
    private OnReportListener onReportListener;

    public interface OnReportListener {
        void onReportSubmitted(String postId, String reason, String note);
    }

    public static ReportPostBottomSheet newInstance(String postId) {
        ReportPostBottomSheet fragment = new ReportPostBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_POST_ID, postId);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnReportListener(OnReportListener listener) {
        this.onReportListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postId = getArguments().getString(ARG_POST_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_report_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupReasonsList();
        setupActions();
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog == null) return;

        Window window = dialog.getWindow();
        if (window != null) {
            // Remove the default white background of the dialog window
            window.setBackgroundDrawableResource(android.R.color.transparent);
            
            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = 0.45f;
            window.setAttributes(params);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        if (dialog instanceof BottomSheetDialog) {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialog;
            View bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

            if (bottomSheet != null) {
                // Make the container transparent so our layout's rounded corners show
                bottomSheet.setBackgroundResource(android.R.color.transparent);

                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setDraggable(true);
                
                // Set height to approx 75% of screen
                android.util.DisplayMetrics displayMetrics = new android.util.DisplayMetrics();
                requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = (int) (displayMetrics.heightPixels * 0.75);
                
                ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
                layoutParams.height = height;
                bottomSheet.setLayoutParams(layoutParams);

                behavior.setPeekHeight(height);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }
    }

    private void initViews(View view) {
        rvReasons = view.findViewById(R.id.rvReasons);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        btnCloseReport = view.findViewById(R.id.btnCloseReport);
    }

    private void setupReasonsList() {
        List<String> reasons = new ArrayList<>();
        reasons.add(getString(R.string.report_reason_irrelevant));
        reasons.add(getString(R.string.report_reason_misleading));
        reasons.add(getString(R.string.report_reason_offensive));
        reasons.add(getString(R.string.report_reason_spam));
        reasons.add(getString(R.string.report_reason_harassment));
        reasons.add(getString(R.string.report_reason_fake));
        reasons.add(getString(R.string.report_reason_misleading_after));
        reasons.add(getString(R.string.report_reason_privacy));
        reasons.add(getString(R.string.report_reason_other));

        adapter = new ReportReasonAdapter(reasons);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvReasons.setLayoutManager(layoutManager);
        rvReasons.setAdapter(adapter);

        // Add divider between items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvReasons.getContext(),
                layoutManager.getOrientation());
        rvReasons.addItemDecoration(dividerItemDecoration);

        adapter.setOnReasonSelectedListener(reason -> btnSubmit.setEnabled(true));
    }

    private void setupActions() {
        if (btnCloseReport != null) {
            btnCloseReport.setOnClickListener(v -> dismiss());
        }

        btnSubmit.setOnClickListener(v -> {
            String selectedReason = adapter.getSelectedReason();

            if (selectedReason == null) {
                Toast.makeText(getContext(), "Vui lòng chọn lý do báo cáo", Toast.LENGTH_SHORT).show();
                return;
            }

            if (onReportListener != null) {
                // Pass empty string for note as it was removed from UI
                onReportListener.onReportSubmitted(postId, selectedReason, "");
            }

            // TODO: connect report API later
            Toast.makeText(getContext(), R.string.report_success, Toast.LENGTH_SHORT).show();
            dismiss();
        });
    }
}
