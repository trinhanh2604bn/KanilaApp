package ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.example.frontend.feature.beauty.BeautyProfileViewModel;
import com.example.frontend.feature.cart.CartViewModel;
import com.example.frontend.feature.home.HomeProductAdapter;
import com.example.frontend.feature.product.ProductDetailFragment;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.ProductRepository;
import com.example.frontend.model.Product;
import com.example.frontend.data.model.beauty.CustomerBeautyProfileDto;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class StepProductSuggestionsFragment extends Fragment {

    private String stepName;
    private String categoryId;
    private CartViewModel cartViewModel;
    private BeautyProfileViewModel beautyViewModel;
    private ProductRepository productRepository;
    private View layoutLoading;

    public static StepProductSuggestionsFragment newInstance(String stepName) {
        return newInstance(stepName, null);
    }

    public static StepProductSuggestionsFragment newInstance(String stepName, String categoryId) {
        StepProductSuggestionsFragment fragment = new StepProductSuggestionsFragment();
        Bundle args = new Bundle();
        args.putString("step_name", stepName);
        args.putString("category_id", categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            stepName = getArguments().getString("step_name");
            categoryId = getArguments().getString("category_id");
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
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        beautyViewModel = new ViewModelProvider(requireActivity()).get(BeautyProfileViewModel.class);
        productRepository = new ProductRepository(requireContext());
        
        TextView tvTitle = view.findViewById(R.id.tvStepTitle);
        if (stepName != null) {
            tvTitle.setText(stepName);
        }

        view.findViewById(R.id.btnBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());

        RecyclerView rvProducts = view.findViewById(R.id.rvStepProducts);
        HomeProductAdapter adapter = new HomeProductAdapter();
        adapter.setOnAddToCartListener(product -> {
            cartViewModel.addToCart(product.getId(), null, 1);
            Toast.makeText(getContext(), "Đã thêm " + product.getName() + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
        });
        adapter.setOnProductClickListener(product -> {
            int containerId = (requireActivity().findViewById(R.id.main_fragment_container) != null)
                    ? R.id.main_fragment_container : R.id.main;
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(containerId, ProductDetailFragment.newInstance(product.getId()))
                    .addToBackStack("step_suggest_to_detail")
                    .commit();
        });
        rvProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvProducts.setAdapter(adapter);

        // Load real products from database based on step
        loadRealProducts(adapter);
    }

    private void loadRealProducts(HomeProductAdapter adapter) {
        if (stepName == null) return;

        // Get user budget from beauty profile
        String userBudget = null;
        NetworkResult<CustomerBeautyProfileDto> profileResult = beautyViewModel.getProfileResult().getValue();
        if (profileResult != null && profileResult.status == NetworkResult.Status.SUCCESS && profileResult.data != null) {
            userBudget = profileResult.data.getBudget();
        }
        
        final String finalBudget = userBudget;
        
        // Use the step name as a search query to get relevant products from DB
        productRepository.getProducts(stepName, categoryId, null).observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                if (result.status == NetworkResult.Status.SUCCESS && result.data != null) {
                    List<Product> filtered = filterByBudget(result.data, finalBudget);
                    
                    // Sắp xếp theo score giảm dần (attribute trong Product)
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        filtered.sort((p1, p2) -> Double.compare(p2.getScore(), p1.getScore()));
                    } else {
                        java.util.Collections.sort(filtered, (p1, p2) -> Double.compare(p2.getScore(), p1.getScore()));
                    }
                    
                    adapter.setProducts(filtered);
                } else if (result.status == NetworkResult.Status.ERROR) {
                    Toast.makeText(getContext(), "Không thể tải sản phẩm: " + result.message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private List<Product> filterByBudget(List<Product> products, String budget) {
        if (budget == null || budget.isEmpty()) return products;
        
        List<Product> filtered = new ArrayList<>();
        for (Product p : products) {
            double price = p.getPriceValue();
            boolean match = false;
            
            if ("Dưới 300K".equalsIgnoreCase(budget)) {
                match = price < 300000;
            } else if ("300K - 500K".equalsIgnoreCase(budget)) {
                match = price >= 300000 && price <= 500000;
            } else if ("500K +".equalsIgnoreCase(budget)) {
                match = price > 500000;
            } else {
                match = true;
            }
            
            if (match) {
                filtered.add(p);
            }
        }
        return filtered;
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
