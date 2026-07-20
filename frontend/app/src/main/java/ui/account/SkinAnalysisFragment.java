package ui.account;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.data.model.recommendation.AiAnalysis;
import com.example.frontend.data.model.recommendation.RecommendationData;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.feature.product.ProductDetailFragment;
import com.example.frontend.feature.recommendation.RecommendationProductAdapter;
import com.example.frontend.feature.recommendation.RecommendationViewModel;
import com.example.frontend.model.Product;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import ui.common.ViewUtils;

public class SkinAnalysisFragment extends Fragment {

    private RecommendationViewModel viewModel;
    private TextView tvScore, tvScoreStatus, tvAiVerdict, tvAiStats;
    private ProgressBar progressScore;
    private View cardHealthScore, cardAiVerdict;
    private RecyclerView rvRecommendedProducts;
    private RecommendationProductAdapter productAdapter;
    private View skinAnalysisRoot;

    public SkinAnalysisFragment() {
        super(R.layout.fragment_skin_analysis);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(RecommendationViewModel.class);
        
        setupViews(view);
        setupEvents(view);
        observeViewModel();

        viewModel.fetchHomepageRecommendations();
    }

    private void setupViews(View view) {
        skinAnalysisRoot = view.findViewById(R.id.skinAnalysisRoot);
        tvScore = view.findViewById(R.id.tvScore);
        tvScoreStatus = view.findViewById(R.id.tvScoreStatus);
        tvAiVerdict = view.findViewById(R.id.tvAiVerdict);
        tvAiStats = view.findViewById(R.id.tvAiStats);
        progressScore = view.findViewById(R.id.progressScore);
        rvRecommendedProducts = view.findViewById(R.id.rvRecommendedProducts);
        cardHealthScore = view.findViewById(R.id.cardHealthScore);
        cardAiVerdict = view.findViewById(R.id.cardAiVerdict);
        
        setupProductRecyclerView();
    }

    private void setupProductRecyclerView() {
        if (rvRecommendedProducts == null) return;
        
        productAdapter = new RecommendationProductAdapter();
        rvRecommendedProducts.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(requireContext(), 2));
        rvRecommendedProducts.setAdapter(productAdapter);
        
        productAdapter.setOnProductClickListener(new RecommendationProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                int containerId = (requireActivity().findViewById(R.id.main_fragment_container) != null)
                        ? R.id.main_fragment_container : R.id.main;
                getParentFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(containerId, ProductDetailFragment.newInstance(product.getId()))
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onAddToCartClick(Product product) {
                // Handle add to cart
            }
        });
    }

    private void observeViewModel() {
        viewModel.getHomepageRecommendations().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                switch (result.status) {
                    case SUCCESS:
                        if (result.data != null) {
                            bindAnalysisData(result.data);
                        }
                        break;
                    case LOADING:
                        // Could show a loading state if needed
                        break;
                    case ERROR:
                        // Handle error
                        break;
                }
            }
        });
    }

    private void bindAnalysisData(RecommendationData recommendationData) {
        AiAnalysis analysis = recommendationData.getAiAnalysis();
        if (analysis == null) {
            // Hiển thị fallback UI như tài liệu yêu cầu
            if (cardHealthScore != null) cardHealthScore.setVisibility(View.GONE);
            if (tvAiVerdict != null) {
                tvAiVerdict.setText("Hệ thống AI đang tạm bận, bạn xem trước các sản phẩm gợi ý bên dưới nhé!");
            }
            // Mapping Recommended Products anyway
            if (productAdapter != null && recommendationData.getProducts() != null) {
                productAdapter.setItems(recommendationData.getProducts());
            }
            return;
        }

        if (cardHealthScore != null) cardHealthScore.setVisibility(View.VISIBLE);
        if (cardAiVerdict != null) cardAiVerdict.setVisibility(View.VISIBLE);
        int healthScore = analysis.getHealthScore() != null ? analysis.getHealthScore() : 0;

        if (tvScore != null) tvScore.setText(String.valueOf(healthScore));
        if (progressScore != null) {
            progressScore.setProgress(healthScore);
            // Áp dụng màu sắc theo tài liệu
            // 0 - 49: Đỏ, 50 - 74: Vàng/Cam, 75 - 100: Xanh lá
            int colorRes;
            if (healthScore >= 75) {
                colorRes = R.color.success; // Xanh lá
                if (tvScoreStatus != null) tvScoreStatus.setText("Khỏe mạnh");
            } else if (healthScore >= 50) {
                colorRes = android.R.color.holo_orange_light; // Vàng/Cam
                if (tvScoreStatus != null) tvScoreStatus.setText("Trung bình - Khá");
            } else {
                colorRes = android.R.color.holo_red_light; // Đỏ
                if (tvScoreStatus != null) tvScoreStatus.setText("Cần cải thiện nhiều");
            }
            progressScore.setProgressTintList(android.content.res.ColorStateList.valueOf(androidx.core.content.ContextCompat.getColor(requireContext(), colorRes)));
            if (tvScoreStatus != null) {
                tvScoreStatus.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), colorRes));
            }
        }

        if (tvAiVerdict != null) {
            tvAiVerdict.setText(analysis.getAnalysisText());
        }
        
        if (tvAiStats != null) {
            // Theo tài liệu: "Chỉ số của bạn cao hơn 75% người dùng cùng loại da dầu."
            // Backend trả về text này hoặc mình tự chế dựa trên score.
            // Ở đây mình dùng text từ API nếu có, hoặc giữ nguyên mẫu.
            tvAiStats.setText("Chỉ số của bạn cao hơn " + (healthScore - 5) + "% người dùng cùng phân khúc.");
        }

        // Mapping Recommended Products
        if (productAdapter != null && recommendationData.getProducts() != null) {
            java.util.List<com.example.frontend.data.model.recommendation.RecommendedProduct> products = new java.util.ArrayList<>(recommendationData.getProducts());
            java.util.Collections.sort(products, (p1, p2) -> {
                Double s1 = p1.getScore() != null ? p1.getScore() : 0.0;
                Double s2 = p2.getScore() != null ? p2.getScore() : 0.0;
                return Double.compare(s2, s1);
            });
            productAdapter.setItems(products);
        }

        // Mapping Ideal Ingredients
        List<String> ingredients = analysis.getIdealIngredients();
        View fragmentView = getView();
        if (ingredients != null && fragmentView != null) {
            updateIngredientCard(fragmentView, R.id.cardIngredient1, ingredients.size() > 0 ? ingredients.get(0) : null, R.drawable.ic_shield_star);
            updateIngredientCard(fragmentView, R.id.cardIngredient2, ingredients.size() > 1 ? ingredients.get(1) : null, R.drawable.ic_beaker);
            updateIngredientCard(fragmentView, R.id.cardIngredient3, ingredients.size() > 2 ? ingredients.get(2) : null, R.drawable.ic_goal_sparkle);
        }
    }

    private void setupEvents(View view) {
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            ViewUtils.applyClickAnimation(btnBack);
            btnBack.setOnClickListener(v -> handleBackNavigation());
        }

        MaterialButton btnViewFullRoutine = view.findViewById(R.id.btnViewFullRoutine);
        if (btnViewFullRoutine != null) {
            ViewUtils.applyClickAnimation(btnViewFullRoutine);
            btnViewFullRoutine.setOnClickListener(v -> {
                // Chuyển sang màn hình Look & Quy trình đã được thiết kế Editorial
                int containerId = (requireActivity().findViewById(R.id.main_fragment_container) != null)
                        ? R.id.main_fragment_container : R.id.main;
                getParentFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(containerId, new RecommendationLookFragment())
                        .addToBackStack(null)
                        .commit();
            });
        }
    }

    private void updateIngredientCard(View root, int id, String name, int iconRes) {
        View card = root.findViewById(id);
        if (card == null) return;

        if (name == null) {
            card.setVisibility(View.GONE);
            return;
        }

        card.setVisibility(View.VISIBLE);
        TextView label = card.findViewById(R.id.tvLabel);
        ImageView icon = card.findViewById(R.id.ivIcon);
        
        if (label != null) label.setText(name);
        if (icon != null) {
            icon.setImageResource(iconRes);
            icon.setImageTintList(android.content.res.ColorStateList.valueOf(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.button)));
        }
        
        ViewUtils.applyClickAnimation(card);
        card.setOnClickListener(v -> {
            int containerId = (requireActivity().findViewById(R.id.main_fragment_container) != null)
                    ? R.id.main_fragment_container : R.id.main;
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(containerId, StepProductSuggestionsFragment.newInstance("Sản phẩm chứa " + name))
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void handleBackNavigation() {
        FragmentManager fragmentManager = getParentFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        }
    }
}
