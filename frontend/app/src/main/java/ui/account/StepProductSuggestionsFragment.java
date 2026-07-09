package ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.example.frontend.feature.home.HomeProductAdapter;
import com.example.frontend.model.Product;
import java.util.ArrayList;
import java.util.List;

public class StepProductSuggestionsFragment extends Fragment {

    private String stepName;

    public static StepProductSuggestionsFragment newInstance(String stepName) {
        StepProductSuggestionsFragment fragment = new StepProductSuggestionsFragment();
        Bundle args = new Bundle();
        args.putString("step_name", stepName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            stepName = getArguments().getString("step_name");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_step_product_suggestions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        TextView tvTitle = view.findViewById(R.id.tvStepTitle);
        if (stepName != null) {
            tvTitle.setText(stepName);
        }

        view.findViewById(R.id.btnBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());

        RecyclerView rvProducts = view.findViewById(R.id.rvStepProducts);
        HomeProductAdapter adapter = new HomeProductAdapter();
        rvProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvProducts.setAdapter(adapter);

        // Load dummy products based on step
        loadDummyProducts(adapter);
    }

    private void loadDummyProducts(HomeProductAdapter adapter) {
        List<Product> products = new ArrayList<>();
        if (stepName == null) return;

        if (stepName.contains("Ceramide")) {
            products.add(new Product("7", "CeraVe", "Kem dưỡng ẩm Ceramide", "450000", "4.8", "3200", R.drawable.cl_product, "Best Seller", "Moisturizer"));
            products.add(new Product("8", "Cosrx", "Serum Balancium Ceramide", "520000", "4.7", "1100", R.drawable.cl_product, "Hot", "Serum"));
        } else if (stepName.contains("BHA")) {
            products.add(new Product("9", "Paula's Choice", "Skin Perfecting 2% BHA", "910000", "4.9", "8500", R.drawable.cl_product, "Premium", "Exfoliant"));
            products.add(new Product("10", "Obagi", "Clenziderm MD Pore Therapy", "850000", "4.8", "2100", R.drawable.cl_product, "Hot", "Toner"));
        } else if (stepName.contains("Vitamin C")) {
            products.add(new Product("11", "The Ordinary", "Vitamin C Suspension 23%", "250000", "4.5", "5400", R.drawable.cl_product, "Value", "Serum"));
            products.add(new Product("12", "Kiehl's", "Powerful-Strength Vitamin C", "1850000", "4.9", "1200", R.drawable.cl_product, "Premium", "Serum"));
        } else if (stepName.contains("Làm sạch") || stepName.contains("Tẩy trang")) {
            products.add(new Product("1", "Bioderma", "Tẩy trang Sensibio H2O", "395000", "4.9", "2500", R.drawable.cl_product, "Best Seller", "Cleansing"));
            products.add(new Product("2", "La Roche-Posay", "Sữa rửa mặt Effaclar", "425000", "4.8", "1800", R.drawable.cl_product, "Hot", "Cleansing"));
            products.add(new Product("3", "CeraVe", "Sữa rửa mặt Hydrating", "370000", "4.7", "1200", R.drawable.cl_product, "New", "Cleansing"));
        } else {
            products.add(new Product("4", "Neutrogena", "Kem dưỡng ẩm Hydro Boost", "350000", "4.6", "2100", R.drawable.cl_product, "Hot", "Moisturizer"));
            products.add(new Product("5", "Innisfree", "Kem chống nắng Mild", "280000", "4.5", "900", R.drawable.cl_product, "Trending", "Sunscreen"));
            products.add(new Product("6", "Laneige", "Mặt nạ ngủ môi", "450000", "4.9", "3500", R.drawable.cl_product, "Signature", "Lip Care"));
        }
        adapter.setProducts(products);
    }
}
