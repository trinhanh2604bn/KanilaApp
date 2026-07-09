package ui.commerce;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.frontend.R;

import java.util.ArrayList;
import java.util.List;

public class PaymentMethodFragment extends Fragment {

    private View layoutHeader;
    private LinearLayout layoutCod, layoutVnpay, layoutMomo, layoutZalopay, layoutBankCard, layoutAppleGoogle;
    private CheckBox cbInvoice;
    private View layoutInvoiceDetails;
    
    private final List<LinearLayout> paymentRows = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_payment_method, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupHeader();
        setupPaymentMethods();
        setupInvoiceSection();
    }

    private void initViews(View view) {
        layoutHeader = view.findViewById(R.id.layoutHeader);

        layoutCod = view.findViewById(R.id.layoutCod);
        layoutVnpay = view.findViewById(R.id.layoutVnpay);
        layoutMomo = view.findViewById(R.id.layoutMomo);
        layoutZalopay = view.findViewById(R.id.layoutZalopay);
        layoutBankCard = view.findViewById(R.id.layoutBankCard);
        layoutAppleGoogle = view.findViewById(R.id.layoutAppleGoogle);

        cbInvoice = view.findViewById(R.id.cbInvoice);
        layoutInvoiceDetails = view.findViewById(R.id.layoutInvoiceDetails);

        paymentRows.add(layoutCod);
        paymentRows.add(layoutVnpay);
        paymentRows.add(layoutMomo);
        paymentRows.add(layoutZalopay);
        paymentRows.add(layoutBankCard);
        paymentRows.add(layoutAppleGoogle);
    }

    private void setupHeader() {
        if (layoutHeader == null) return;

        TextView tvTitle = layoutHeader.findViewById(R.id.tvTopBarTitle);
        if (tvTitle != null) {
            tvTitle.setText(R.string.payment_method_title);
        }

        View btnSearch = layoutHeader.findViewById(R.id.btnTopBarSearch);
        if (btnSearch != null) {
            btnSearch.setVisibility(View.GONE);
        }

        View btnBack = layoutHeader.findViewById(R.id.btnTopBarBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().getOnBackPressedDispatcher().onBackPressed();
                }
            });
        }
    }

    private void setupPaymentMethods() {
        for (LinearLayout row : paymentRows) {
            row.setOnClickListener(v -> updatePaymentSelection(row));
        }

        // Default selection: COD
        updatePaymentSelection(layoutCod);
    }

    private void updatePaymentSelection(LinearLayout selectedRow) {
        for (LinearLayout row : paymentRows) {
            RadioButton rb = (RadioButton) row.getChildAt(0);

            if (row == selectedRow) {
                row.setBackgroundResource(R.drawable.bg_selection_selected);
                rb.setChecked(true);
            } else {
                row.setBackground(null);
                rb.setChecked(false);
            }
        }
    }

    private void setupInvoiceSection() {
        if (cbInvoice == null || layoutInvoiceDetails == null) return;
        
        cbInvoice.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutInvoiceDetails.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            // Or keep it visible but faded:
            // layoutInvoiceDetails.setAlpha(isChecked ? 1.0f : 0.5f);
        });
        
        // Initial state
        layoutInvoiceDetails.setVisibility(cbInvoice.isChecked() ? View.VISIBLE : View.GONE);
    }
}
