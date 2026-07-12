package ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.feature.home.HomeProductAdapter;
import com.example.frontend.model.Product;

import java.util.ArrayList;
import java.util.List;

public class KocDashboardFragment extends Fragment {

    private RecyclerView rvHotProducts;
    private HomeProductAdapter productAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_koc_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        loadDummyHotProducts();
    }

    private void initViews(View view) {
        view.findViewById(R.id.btnBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        
        view.findViewById(R.id.btnSettings).setOnClickListener(v -> 
            Toast.makeText(requireContext(), "Cài đặt tài khoản Creator", Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.btnWithdraw).setOnClickListener(v -> 
            Toast.makeText(requireContext(), "Yêu cầu rút tiền đã được gửi. Kanila sẽ xử lý trong 24h.", Toast.LENGTH_SHORT).show());

        // Logic cho các công cụ sáng tạo
        view.findViewById(R.id.btnFreeSample).setOnClickListener(v -> 
            navigateToTool(new KocFreeSamplesFragment()));
            
        view.findViewById(R.id.btnManageBio).setOnClickListener(v -> 
            navigateToTool(new KocLinkBioFragment()));
            
        view.findViewById(R.id.btnDetailedReport).setOnClickListener(v -> 
            navigateToTool(new KocReportsFragment()));
            
        view.findViewById(R.id.btnCreatorAcademy).setOnClickListener(v -> 
            navigateToTool(new KocAcademyFragment()));

        rvHotProducts = view.findViewById(R.id.rvHotProducts);
        rvHotProducts.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        
        productAdapter = new HomeProductAdapter();
        // Custom width for horizontal scrolling cards
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        productAdapter.setItemWidth((int) (screenWidth * 0.45));
        
        rvHotProducts.setAdapter(productAdapter);
    }

    private void navigateToTool(Fragment fragment) {
        int containerId = (requireActivity().findViewById(R.id.main_fragment_container) != null)
                ? R.id.main_fragment_container : R.id.main;
        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void loadDummyHotProducts() {
        List<Product> products = new ArrayList<>();
        // Note: Using existing constructor: Product(id, brandName, productName, price, averageRating, reviewCount, imageResource, badgeText, subcategory)
        products.add(new Product("1", "The Ordinary", "Serum Cấp Ẩm B5", "250000", "4.8", "120", R.drawable.cl_product, "10% Commission", "Serum"));
        products.add(new Product("2", "La Roche-Posay", "Kem Chống Nắng", "450000", "4.9", "250", R.drawable.cl_product, "15% Commission", "Sunscreen"));
        products.add(new Product("3", "3CE", "Son Kem Lì Velvet", "380000", "4.7", "180", R.drawable.cl_product, "12% Commission", "Lipstick"));
        
        productAdapter.setProducts(products);
    }
}
