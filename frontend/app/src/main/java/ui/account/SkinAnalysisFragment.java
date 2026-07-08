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

import com.example.frontend.R;
import com.example.frontend.data.model.beauty.CustomerBeautyProfileDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.feature.beauty.BeautyProfileViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import ui.common.ViewUtils;

public class SkinAnalysisFragment extends Fragment {

    private BeautyProfileViewModel viewModel;
    private TextView tvScore, tvScoreStatus, tvAiVerdict, tvAiStats;
    private ProgressBar progressScore;

    public SkinAnalysisFragment() {
        super(R.layout.fragment_skin_analysis);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(BeautyProfileViewModel.class);
        
        setupViews(view);
        setupEvents(view);
        observeViewModel();
    }

    private void setupViews(View view) {
        tvScore = view.findViewById(R.id.tvScore);
        tvScoreStatus = view.findViewById(R.id.tvScoreStatus);
        tvAiVerdict = view.findViewById(R.id.tvAiVerdict);
        tvAiStats = view.findViewById(R.id.tvAiStats);
        progressScore = view.findViewById(R.id.progressScore);
    }

    private void observeViewModel() {
        viewModel.getProfileResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.status == NetworkResult.Status.SUCCESS) {
                bindAnalysisData(result.data);
            }
        });
    }

    private void bindAnalysisData(CustomerBeautyProfileDto profile) {
        if (profile == null) return;

        // Giả lập điểm sức khỏe dựa trên độ hoàn thiện hồ sơ hoặc dữ liệu có sẵn
        int healthScore = 75; 
        if (profile.getSkinIndicators() != null && !profile.getSkinIndicators().isEmpty()) {
            healthScore = profile.getSkinIndicators().get(0).getScore();
        } else {
            // Logic giả lập: Hồ sơ càng chi tiết điểm càng cao (vui vẻ thôi)
            healthScore = 60 + (profile.getProfileCompletion() / 4);
        }

        if (tvScore != null) tvScore.setText(String.valueOf(healthScore));
        if (progressScore != null) progressScore.setProgress(healthScore);
        
        if (tvScoreStatus != null) {
            if (healthScore >= 80) tvScoreStatus.setText("Tuyệt vời");
            else if (healthScore >= 60) tvScoreStatus.setText("Tốt");
            else tvScoreStatus.setText("Cần cải thiện");
        }

        // AI Verdict dựa trên Skin Type và Concerns
        StringBuilder verdict = new StringBuilder();
        String skinType = profile.getSkinType() != null ? profile.getSkinType().toLowerCase() : "da thường";
        
        verdict.append("Dựa trên loại ").append(skinType);
        if (profile.getSkinConcerns() != null && !profile.getSkinConcerns().isEmpty()) {
            verdict.append(" và tình trạng ").append(String.join(", ", profile.getSkinConcerns()).toLowerCase());
        }
        verdict.append(", làn da của bạn cần sự chú ý đặc biệt vào việc ");
        
        if (skinType.contains("dầu")) {
            verdict.append("kiểm soát bã nhờn và làm sạch sâu lỗ chân lông.");
        } else if (skinType.contains("khô")) {
            verdict.append("cấp ẩm tầng sâu và phục hồi hàng rào bảo vệ da.");
        } else {
            verdict.append("duy trì sự cân bằng độ ẩm và bảo vệ trước tác động môi trường.");
        }

        if (profile.getSensitivityLevel() != null && profile.getSensitivityLevel().contains("nhạy cảm")) {
            verdict.append(" Hãy ưu tiên các sản phẩm không chứa hương liệu và cồn khô.");
        }

        if (tvAiVerdict != null) tvAiVerdict.setText(verdict.toString());
        
        if (tvAiStats != null) {
            tvAiStats.setText("Chỉ số của bạn cao hơn " + (healthScore - 5) + "% người dùng cùng loại " + skinType + ".");
        }
    }

    private void setupEvents(View view) {
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            ViewUtils.applyClickAnimation(btnBack);
            btnBack.setOnClickListener(v -> handleBackNavigation());
        }

        // Setup Ingredients
        setupIngredientCard(view, R.id.cardIngredient1, "Ceramide", R.drawable.ic_shield_star);
        setupIngredientCard(view, R.id.cardIngredient2, "BHA", R.drawable.ic_beaker);
        setupIngredientCard(view, R.id.cardIngredient3, "Vitamin C", R.drawable.ic_goal_sparkle);

        MaterialButton btnViewFullRoutine = view.findViewById(R.id.btnViewFullRoutine);
        if (btnViewFullRoutine != null) {
            ViewUtils.applyClickAnimation(btnViewFullRoutine);
            btnViewFullRoutine.setOnClickListener(v -> {
                // Chuyển sang màn hình Look & Quy trình đã được thiết kế Editorial
                getParentFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.main_fragment_container, new RecommendationLookFragment())
                        .addToBackStack(null)
                        .commit();
            });
        }
    }

    private void setupIngredientCard(View root, int id, String name, int iconRes) {
        View card = root.findViewById(id);
        if (card == null) return;
        
        TextView label = card.findViewById(R.id.tvLabel);
        ImageView icon = card.findViewById(R.id.ivIcon);
        
        if (label != null) label.setText(name);
        if (icon != null) {
            icon.setImageResource(iconRes);
            icon.setImageTintList(android.content.res.ColorStateList.valueOf(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.button)));
        }
        
        ViewUtils.applyClickAnimation(card);
        card.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.main_fragment_container, StepProductSuggestionsFragment.newInstance("Sản phẩm chứa " + name))
                .addToBackStack(null)
                .commit());
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
