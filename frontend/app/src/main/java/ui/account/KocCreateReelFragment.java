package ui.account;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.Product;

import java.util.List;

public class KocCreateReelFragment extends Fragment {

    private RecyclerView rvSelectedProducts;
    private com.example.frontend.feature.home.HomeProductAdapter selectedProductsAdapter;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private Uri selectedVideoUri;
    
    private View btnSelectVideo;
    private ImageView ivPlayIcon;
    private TextView tvSelectLabel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Khởi tạo launcher để chọn video từ thư viện máy
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                selectedVideoUri = uri;
                updateVideoPreviewUI();
                Toast.makeText(requireContext(), "Đã chọn video thành công", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_koc_create_reel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupSelectedProductsList();

        view.findViewById(R.id.btnBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        
        btnSelectVideo.setOnClickListener(v -> {
            // Mở thư viện chọn video (chỉ lọc video)
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.VideoOnly.INSTANCE)
                    .build());
        });
            
        view.findViewById(R.id.btnAddProduct).setOnClickListener(v -> {
            KocProductSelectorFragment selector = new KocProductSelectorFragment();
            selector.setOnProductsSelectedListener(selectedProducts -> {
                selectedProductsAdapter.setProducts(selectedProducts);
                rvSelectedProducts.setVisibility(selectedProducts.isEmpty() ? View.GONE : View.VISIBLE);
            });
            
            int containerId = (requireActivity().findViewById(R.id.main_fragment_container) != null)
                    ? R.id.main_fragment_container : R.id.main;
            
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(containerId, selector)
                    .addToBackStack(null)
                    .commit();
        });
            
        view.findViewById(R.id.btnPublish).setOnClickListener(v -> {
            if (selectedVideoUri == null) {
                Toast.makeText(requireContext(), "Vui lòng chọn video trước khi đăng", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(requireContext(), "Đang đăng tải Reels của bạn...", Toast.LENGTH_LONG).show();
            getParentFragmentManager().popBackStack();
        });
    }

    private void initViews(View view) {
        rvSelectedProducts = view.findViewById(R.id.rvSelectedProducts);
        btnSelectVideo = view.findViewById(R.id.btnSelectVideo);
        ivPlayIcon = view.findViewById(R.id.ivPlayIcon);
        tvSelectLabel = view.findViewById(R.id.tvSelectLabel);
    }

    private void updateVideoPreviewUI() {
        if (selectedVideoUri != null) {
            if (tvSelectLabel != null) {
                tvSelectLabel.setText("Video đã sẵn sàng");
            }
            if (ivPlayIcon != null) {
                ivPlayIcon.setImageResource(R.drawable.ic_check_circle);
                ivPlayIcon.setColorFilter(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.success));
            }
        }
    }

    private void setupSelectedProductsList() {
        selectedProductsAdapter = new com.example.frontend.feature.home.HomeProductAdapter();
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        selectedProductsAdapter.setItemWidth((int) (screenWidth * 0.4));
        
        rvSelectedProducts.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvSelectedProducts.setAdapter(selectedProductsAdapter);
        rvSelectedProducts.setVisibility(View.GONE);
    }
}
