package ui.commerce;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.ShippingMethod;

import java.util.ArrayList;
import java.util.List;

public class CheckoutShippingFragment extends Fragment {

    private RecyclerView rvShippingMethods;
    private ShippingMethodAdapter adapter;
    private ShippingMethod selectedMethod;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_checkout_shipping, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupTopBar(view);
        setupSectionTitle(view);
        setupRecyclerView(view);
        setupConfirmButton(view);
    }

    private void setupTopBar(View view) {
        View topBar = view.findViewById(R.id.layoutTopBar);
        if (topBar == null) return;

        TextView tvTitle = topBar.findViewById(R.id.tvTopBarTitle);
        if (tvTitle != null) {
            // "Phương thức vận chuyển"
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

        adapter = new ShippingMethodAdapter(method -> selectedMethod = method);
        rvShippingMethods.setAdapter(adapter);

        loadShippingMethods();
    }

    private void loadShippingMethods() {
        List<ShippingMethod> methods = new ArrayList<>();
        
        ShippingMethod standard = new ShippingMethod("1", "Tiêu chuẩn", "Dự kiến giao: 2 - 3 ngày", 
                "Giao hàng tiêu chuẩn, phù hợp mọi đơn hàng", "15.000đ", R.drawable.ic_delivery_truck, "Phổ biến");
        standard.setSelected(true);
        selectedMethod = standard;

        methods.add(standard);
        methods.add(new ShippingMethod("2", "Nhanh", "Dự kiến giao: 1 - 2 ngày", 
                "Giao nhanh, tiết kiệm thời gian", "25.000đ", R.drawable.ic_delivery_truck));
        methods.add(new ShippingMethod("3", "Hỏa tốc", "Dự kiến giao: Trong ngày", 
                "Giao siêu tốc trong ngày (áp dụng nội thành)", "45.000đ", R.drawable.ic_delivery_truck));
        methods.add(new ShippingMethod("4", "Nhận tại cửa hàng", "Dự kiến nhận: 2 - 3 ngày", 
                "Nhận hàng tại cửa hàng Kanila gần bạn", "Miễn phí", R.drawable.ic_delivery_truck));

        adapter.setShippingMethods(methods);
    }

    private void setupConfirmButton(View view) {
        View btnConfirm = view.findViewById(R.id.btnConfirmShippingMethod);
        if (btnConfirm instanceof TextView) {
            ((TextView) btnConfirm).setText("Xác nhận phương thức vận chuyển");
        }
        
        btnConfirm.setOnClickListener(v -> {
            if (selectedMethod != null) {
                // TODO: Return selected method to CheckoutFragment
                // Since I don't have a shared ViewModel or direct callback mechanism defined yet, 
                // I will just pop the backstack as per instruction.
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }
}
