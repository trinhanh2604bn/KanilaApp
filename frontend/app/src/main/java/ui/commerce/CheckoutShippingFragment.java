package ui.commerce;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.example.frontend.data.model.shipping.ShippingMethodDto;
import com.example.frontend.feature.checkout.CheckoutViewModel;
import com.example.frontend.feature.checkout.ShippingViewModel;
import com.example.frontend.model.ShippingMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CheckoutShippingFragment extends Fragment {

    private RecyclerView rvShippingMethods;
    private ShippingMethodAdapter adapter;
    private ShippingMethodDto selectedDto;
    private ShippingViewModel shippingViewModel;
    private CheckoutViewModel checkoutViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_checkout_shipping, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shippingViewModel = new ViewModelProvider(this).get(ShippingViewModel.class);
        checkoutViewModel = new ViewModelProvider(requireActivity()).get(CheckoutViewModel.class);

        setupTopBar(view);
        setupSectionTitle(view);
        setupRecyclerView(view);
        setupConfirmButton(view);
        observeViewModels();

        shippingViewModel.loadShippingMethods();
    }

    private void setupTopBar(View view) {
        View topBar = view.findViewById(R.id.layoutTopBar);
        if (topBar == null) return;

        TextView tvTitle = topBar.findViewById(R.id.tvTopBarTitle);
        if (tvTitle != null) {
            tvTitle.setText(getString(R.string.checkout_shipping_title));
        }

        ImageButton btnBack = topBar.findViewById(R.id.btnTopBarBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        }

        View rightAction = topBar.findViewById(R.id.layoutTopBarRightAction);
        if (rightAction != null) {
            rightAction.setVisibility(View.GONE);
        }
    }

    private void setupSectionTitle(View view) {
        TextView tvSectionTitle = view.findViewById(R.id.tvShippingMethodSectionTitle);
        if (tvSectionTitle != null) {
            tvSectionTitle.setText("Chọn phương thức vận chuyển");
        }
    }

    private void setupRecyclerView(View view) {
        rvShippingMethods = view.findViewById(R.id.rvShippingMethods);
        rvShippingMethods.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void observeViewModels() {
        shippingViewModel.getShippingMethodsResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case LOADING:
                    android.util.Log.d("CheckoutShipping", "Loading shipping methods...");
                    break;
                case SUCCESS:
                    if (result.data != null) {
                        android.util.Log.d("CheckoutShipping", "Loaded " + result.data.size() + " shipping methods");
                        displayShippingMethods(result.data);
                    }
                    break;
                case ERROR:
                    android.util.Log.e("CheckoutShipping", "Error loading shipping methods: " + result.message);
                    Toast.makeText(getContext(), "Lỗi: " + result.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void displayShippingMethods(List<ShippingMethodDto> dtos) {
        List<ShippingMethod> uiModels = new ArrayList<>();
        
        CheckoutSessionDto session = checkoutViewModel.getCheckoutSession().getValue() != null ? 
                checkoutViewModel.getCheckoutSession().getValue().data : null;
        
        String currentMethodName = session != null ? session.getShippingMethod() : null;

        for (ShippingMethodDto dto : dtos) {
            String priceStr = dto.getShippingFee() > 0 ? formatPrice(dto.getShippingFee()) : "Miễn phí";
            ShippingMethod uiModel = new ShippingMethod(
                    dto.getId(),
                    dto.getName(),
                    "Dự kiến giao: " + dto.getEstimatedDelivery(),
                    dto.getDescription(),
                    priceStr,
                    R.drawable.ic_delivery_truck
            );
            
            if (currentMethodName != null && currentMethodName.equals(dto.getName())) {
                uiModel.setSelected(true);
                selectedDto = dto;
            }
            
            uiModels.add(uiModel);
        }

        if (selectedDto == null && !uiModels.isEmpty()) {
            uiModels.get(0).setSelected(true);
            selectedDto = dtos.get(0);
        }

        adapter = new ShippingMethodAdapter(method -> {
            for (ShippingMethodDto dto : dtos) {
                if (dto.getId().equals(method.getId())) {
                    selectedDto = dto;
                    break;
                }
            }
        });
        adapter.setShippingMethods(uiModels);
        rvShippingMethods.setAdapter(adapter);
    }

    private void setupConfirmButton(View view) {
        View btnConfirm = view.findViewById(R.id.btnConfirmShippingMethod);
        if (btnConfirm instanceof TextView) {
            ((TextView) btnConfirm).setText("Xác nhận phương thức vận chuyển");
        }
        
        btnConfirm.setOnClickListener(v -> {
            if (selectedDto != null) {
                checkoutViewModel.updateShippingMethod(selectedDto);
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                Toast.makeText(getContext(), "Vui lòng chọn phương thức vận chuyển", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatPrice(double price) {
        return String.format(Locale.US, "%,.0fđ", price).replace(",", ".");
    }
}
