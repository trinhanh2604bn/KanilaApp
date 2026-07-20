package ui.account;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.example.frontend.model.Product;
import com.example.frontend.ui.category.ProductAdapter;
import java.util.ArrayList;
import java.util.List;

public class KocProductSelectorFragment extends Fragment {

    private RecyclerView rvProducts;
    private ProductAdapter adapter;
    private List<Product> allProducts = new ArrayList<>();
    private OnProductsSelectedListener listener;

    public interface OnProductsSelectedListener {
        void onProductsSelected(List<Product> selectedProducts);
    }

    public void setOnProductsSelectedListener(OnProductsSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_koc_product_selector, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvProducts = view.findViewById(R.id.rvProducts);
        EditText etSearch = view.findViewById(R.id.etSearch);
        
        view.findViewById(R.id.btnBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        view.findViewById(R.id.btnDone).setOnClickListener(v -> {
            if (listener != null) {
                List<Product> selected = new ArrayList<>();
                for (Product p : allProducts) {
                    if (adapter.getSelectedProductIds().contains(p.getId())) {
                        selected.add(p);
                    }
                }
                listener.onProductsSelected(selected);
            }
            getParentFragmentManager().popBackStack();
        });

        setupRecyclerView();
        loadDummyProducts();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupRecyclerView() {
        adapter = new ProductAdapter();
        adapter.setSelectionMode(true);
        rvProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvProducts.setAdapter(adapter);
    }

    private void loadDummyProducts() {
        allProducts.add(new Product("1", "The Ordinary", "Serum Cấp Ẩm B5", "250000", "4.8", "120", R.drawable.cl_product, "10% Commission", "Serum"));
        allProducts.add(new Product("2", "La Roche-Posay", "Kem Chống Nắng", "450000", "4.9", "250", R.drawable.cl_product, "15% Commission", "Sunscreen"));
        allProducts.add(new Product("3", "3CE", "Son Kem Lì Velvet", "380000", "4.7", "180", R.drawable.cl_product, "12% Commission", "Lipstick"));
        allProducts.add(new Product("4", "Laneige", "Mặt Nạ Ngủ Môi", "150000", "4.6", "90", R.drawable.cl_product, "8% Commission", "Lip Care"));
        allProducts.add(new Product("5", "Innisfree", "Sữa Rửa Mặt Trà Xanh", "220000", "4.5", "300", R.drawable.cl_product, "5% Commission", "Cleanser"));
        
        adapter.setProducts(allProducts);
    }

    private void filter(String text) {
        List<Product> filteredList = new ArrayList<>();
        for (Product item : allProducts) {
            if (item.getName().toLowerCase().contains(text.toLowerCase()) || 
                item.getBrand().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.setProducts(filteredList);
    }
}
