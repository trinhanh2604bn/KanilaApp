package ui.commerce;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.data.model.checkout.CheckoutSessionDto;
import com.example.frontend.data.model.payment.PaymentMethodDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.feature.checkout.CheckoutViewModel;
import com.example.frontend.feature.checkout.PaymentViewModel;

import java.util.List;

public class PaymentMethodFragment extends Fragment {

    private View layoutHeader;
    private RecyclerView rvPaymentMethods;
    private PaymentMethodAdapter adapter;
    private CheckBox cbInvoice;
    private View layoutInvoiceDetails;
    private View btnAgree;

    private PaymentViewModel paymentViewModel;
    private CheckoutViewModel checkoutViewModel;
    private PaymentMethodDto selectedMethod;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_payment_method, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        paymentViewModel = new ViewModelProvider(this).get(PaymentViewModel.class);
        checkoutViewModel = new ViewModelProvider(requireActivity()).get(CheckoutViewModel.class);

        initViews(view);
        setupHeader();
        setupRecyclerView();
        setupInvoiceSection();
        setupAgreeButton();
        observeViewModel();

        paymentViewModel.loadPaymentMethods();
    }

    private void initViews(View view) {
        layoutHeader = view.findViewById(R.id.layoutHeader);
        rvPaymentMethods = view.findViewById(R.id.rvPaymentMethods);
        cbInvoice = view.findViewById(R.id.cbInvoice);
        layoutInvoiceDetails = view.findViewById(R.id.layoutInvoiceDetails);
        btnAgree = view.findViewById(R.id.btnAgree);
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

    private void setupRecyclerView() {
        rvPaymentMethods.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PaymentMethodAdapter(method -> {
            selectedMethod = method;
        });
        rvPaymentMethods.setAdapter(adapter);
    }

    private void setupInvoiceSection() {
        if (cbInvoice == null || layoutInvoiceDetails == null) return;
        cbInvoice.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutInvoiceDetails.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });
        layoutInvoiceDetails.setVisibility(cbInvoice.isChecked() ? View.VISIBLE : View.GONE);
    }

    private void setupAgreeButton() {
        if (btnAgree != null) {
            btnAgree.setOnClickListener(v -> {
                if (selectedMethod != null) {
                    // Update CheckoutSession with selected payment method
                    checkoutViewModel.updatePaymentMethod(selectedMethod.getName());
                    
                    if (getActivity() != null) {
                        getActivity().getOnBackPressedDispatcher().onBackPressed();
                    }
                } else {
                    Toast.makeText(getContext(), "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void observeViewModel() {
        paymentViewModel.getPaymentMethodsResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case LOADING:
                    // Show progress if needed
                    break;
                case SUCCESS:
                    if (result.data != null) {
                        displayPaymentMethods(result.data);
                    }
                    break;
                case ERROR:
                    Toast.makeText(getContext(), "Lỗi: " + result.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void displayPaymentMethods(List<PaymentMethodDto> methods) {
        CheckoutSessionDto session = checkoutViewModel.getCheckoutSession().getValue() != null ?
                checkoutViewModel.getCheckoutSession().getValue().data : null;
        
        String currentMethodName = session != null ? session.getPaymentMethod() : null;
        String selectedId = null;

        if (currentMethodName != null) {
            for (PaymentMethodDto m : methods) {
                if (m.getName().equals(currentMethodName)) {
                    selectedId = m.getId();
                    selectedMethod = m;
                    break;
                }
            }
        }

        // If nothing selected yet, pick first one (e.g. COD)
        if (selectedId == null && !methods.isEmpty()) {
            selectedId = methods.get(0).getId();
            selectedMethod = methods.get(0);
        }

        adapter.setPaymentMethods(methods, selectedId);
    }
}
