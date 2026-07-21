package ui.account;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.data.model.beauty.CustomerBeautyProfileDto;
import com.example.frontend.data.model.recommendation.AiAnalysis;
import com.example.frontend.data.model.recommendation.RecommendationData;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.feature.beauty.BeautyProfileViewModel;
import com.example.frontend.feature.beauty.BeautyReferenceMapper;
import com.example.frontend.feature.beauty.BeautyReferenceResolver;
import com.example.frontend.feature.cart.CartViewModel;
import com.example.frontend.feature.recommendation.RecommendationProductAdapter;
import com.example.frontend.feature.recommendation.RecommendationViewModel;
import com.example.frontend.model.Product;

import ui.account.StepProductSuggestionsFragment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ui.common.ViewUtils;

public class BeautyProfileOverviewFragment extends Fragment {

    private LinearLayout layoutSectionsList, layoutEmptyState;
    private TextView tvSummarySkinType, tvProgressLabel;
    private ProgressBar progressProfile;
    private View layoutRecommendations;
    private RecyclerView rvRecommendedProducts;
    private RecommendationProductAdapter productAdapter;
    
    // AI Analysis UI
    private View layoutAiAnalysis, cardHealthScore;
    private TextView tvScore, tvScoreStatus, tvAiVerdict, tvAiStats;
    private ProgressBar progressScore;
    
    private boolean isRecExpanded = false;
    private CustomerBeautyProfileDto currentProfile;
    private BeautyProfileViewModel viewModel;
    private RecommendationViewModel recommendationViewModel;
    private CartViewModel cartViewModel;
    private final Map<String, Boolean> expandedSections = new HashMap<>();
    private final Handler ctaHandler = new Handler(Looper.getMainLooper());
    private boolean isCtaShown = false;

    private int brandPink, darkText;
    private final List<SectionData> sections = new ArrayList<>();
    private final Map<String, Integer> referenceIcons = new HashMap<>();

    public BeautyProfileOverviewFragment() {
        super(R.layout.fragment_beauty_profile_overview);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(BeautyProfileViewModel.class);
        recommendationViewModel = new ViewModelProvider(requireActivity()).get(RecommendationViewModel.class);
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

        initColors();
        initIconMapping();
        initSectionData();
        setupViews(view);
        setupEvents(view);
        observeViewModel();

        viewModel.loadReferences();
        recommendationViewModel.fetchHomepageRecommendations();
        
        NetworkResult<CustomerBeautyProfileDto> currentResult = viewModel.getProfileResult().getValue();
        if (currentResult == null) {
            String customerId = com.example.frontend.data.remote.TokenManager.getInstance(requireContext()).getCustomerId();
            viewModel.loadProfile(customerId);
        } else if (currentResult.status == NetworkResult.Status.SUCCESS) {
            bindProfileData(currentResult.data);
        }
    }

    private void initIconMapping() {
        // Skin Type
        referenceIcons.put("OILY_SKIN", R.drawable.ic_drops_filled);
        referenceIcons.put("oily", R.drawable.ic_drops_filled);
        referenceIcons.put("DRY_SKIN", R.drawable.ic_drops);
        referenceIcons.put("dry", R.drawable.ic_drops);
        referenceIcons.put("COMBINATION_SKIN", R.drawable.ic_skin_mixed);
        referenceIcons.put("combination", R.drawable.ic_skin_mixed);
        referenceIcons.put("NORMAL_SKIN", R.drawable.ic_skin_normal);
        referenceIcons.put("normal", R.drawable.ic_skin_normal);
        referenceIcons.put("SENSITIVE_SKIN", R.drawable.ic_skin_sensitive);
        referenceIcons.put("sensitive", R.drawable.ic_skin_sensitive);
        referenceIcons.put("UNKNOWN_SKIN", R.drawable.ic_unsure);
        referenceIcons.put("unknown", R.drawable.ic_unsure);

        // Concerns
        referenceIcons.put("ACNE", R.drawable.ic_skin_acne);
        referenceIcons.put("acne", R.drawable.ic_skin_acne);
        referenceIcons.put("DARK_SPOT", R.drawable.ic_skin_spots);
        referenceIcons.put("dark_spots", R.drawable.ic_skin_spots);
        referenceIcons.put("MELASMA", R.drawable.ic_skin_spots);
        referenceIcons.put("melasma", R.drawable.ic_skin_spots);
        referenceIcons.put("DULLNESS", R.drawable.ic_skin_dullness);
        referenceIcons.put("dullness", R.drawable.ic_skin_dullness);
        referenceIcons.put("LARGE_PORES", R.drawable.ic_skin_pores);
        referenceIcons.put("large_pores", R.drawable.ic_skin_pores);
        referenceIcons.put("BLACKHEADS", R.drawable.ic_skin_acne);
        referenceIcons.put("blackheads", R.drawable.ic_skin_acne);
        referenceIcons.put("REDNESS", R.drawable.ic_skin_redness);
        referenceIcons.put("redness", R.drawable.ic_skin_redness);
        referenceIcons.put("DEHYDRATED", R.drawable.ic_drops);
        referenceIcons.put("dehydrated", R.drawable.ic_drops);
        referenceIcons.put("AGING", R.drawable.ic_skin_aging);
        referenceIcons.put("wrinkles", R.drawable.ic_skin_aging);
        referenceIcons.put("UNEVEN", R.drawable.ic_skin_mixed);
        referenceIcons.put("uneven_texture", R.drawable.ic_skin_mixed);
        referenceIcons.put("DAMAGED", R.drawable.ic_goal_recovery);
        referenceIcons.put("damaged_barrier", R.drawable.ic_goal_recovery);
        referenceIcons.put("SUN_DAMAGE", R.drawable.ic_sun);
        referenceIcons.put("sun_damage", R.drawable.ic_sun);

        // Sensitivity
        referenceIcons.put("LOW_SENSITIVITY", R.drawable.ic_shield_star);
        referenceIcons.put("low", R.drawable.ic_shield_star);
        referenceIcons.put("MEDIUM_SENSITIVITY", R.drawable.ic_skin_sensitive);
        referenceIcons.put("medium", R.drawable.ic_skin_sensitive);
        referenceIcons.put("HIGH_SENSITIVITY", R.drawable.ic_alert);
        referenceIcons.put("high", R.drawable.ic_alert);
        referenceIcons.put("REACTIVE_SENSITIVITY", R.drawable.ic_skin_redness);
        referenceIcons.put("reactive", R.drawable.ic_skin_redness);

        // Finish
        referenceIcons.put("NATURAL_FINISH", R.drawable.ic_face);
        referenceIcons.put("natural", R.drawable.ic_face);
        referenceIcons.put("BRIGHT_FINISH", R.drawable.ic_lightbulb);
        referenceIcons.put("glowy", R.drawable.ic_lightbulb);
        referenceIcons.put("WARM_FINISH", R.drawable.ic_sun);
        referenceIcons.put("PINKISH_FINISH", R.drawable.ic_face);
        referenceIcons.put("MATTE_FINISH", R.drawable.ic_face);
        referenceIcons.put("matte", R.drawable.ic_face);
        referenceIcons.put("dewy", R.drawable.ic_drops);

        // Avoid
        referenceIcons.put("FRAGRANCE", R.drawable.ic_drops);
        referenceIcons.put("fragrance", R.drawable.ic_drops);
        referenceIcons.put("ALCOHOL", R.drawable.ic_beaker);
        referenceIcons.put("alcohol_denat", R.drawable.ic_beaker);
        referenceIcons.put("ESSENTIAL_OIL", R.drawable.ic_drops);
        referenceIcons.put("essential_oil", R.drawable.ic_drops);
        referenceIcons.put("PARABEN", R.drawable.ic_beaker);
        referenceIcons.put("paraben", R.drawable.ic_beaker);
        referenceIcons.put("MINERAL_OIL", R.drawable.ic_drops);
        referenceIcons.put("mineral_oil", R.drawable.ic_drops);
        referenceIcons.put("SILICONE", R.drawable.ic_beaker);
        referenceIcons.put("silicone", R.drawable.ic_beaker);
        referenceIcons.put("SULFATE", R.drawable.ic_beaker);
        referenceIcons.put("sulfate", R.drawable.ic_beaker);
        referenceIcons.put("LANOLIN", R.drawable.ic_beaker);
        referenceIcons.put("lanolin", R.drawable.ic_beaker);
        referenceIcons.put("RETINOID", R.drawable.ic_beaker);
        referenceIcons.put("retinoid", R.drawable.ic_beaker);
        referenceIcons.put("HIGH_ACID", R.drawable.ic_beaker);
        referenceIcons.put("aha_bha_high", R.drawable.ic_beaker);
        
        referenceIcons.put("BUDGET", R.drawable.ic_wallet);
    }

    private void initColors() {
        brandPink = ContextCompat.getColor(requireContext(), R.color.button);
        darkText = ContextCompat.getColor(requireContext(), R.color.accent_dark);
    }

    private void initSectionData() {
        sections.clear();
        sections.add(new SectionData("Loại da", BeautyReferenceMapper.SKIN_TYPE, "Nền tảng quan trọng nhất để chọn sản phẩm phù hợp."));
        sections.add(new SectionData("Tình trạng da", BeautyReferenceMapper.SKIN_CONCERN, "Tập trung giải quyết các vấn đề da cụ thể."));
        sections.add(new SectionData("Mức độ nhạy cảm", BeautyReferenceMapper.SENSITIVITY_LEVEL, "Đảm bảo an toàn và không gây kích ứng."));
        sections.add(new SectionData("Mục tiêu làm đẹp", BeautyReferenceMapper.BEAUTY_GOAL, "Định hướng chu trình dưỡng da dài hạn."));
        sections.add(new SectionData("Thành phần yêu thích", BeautyReferenceMapper.PREFERRED_INGREDIENT, "Ưu tiên các hoạt chất bạn tin dùng."));
        sections.add(new SectionData("Thành phần cần tránh", BeautyReferenceMapper.AVOID_INGREDIENT, "Loại bỏ các nguy cơ gây hại cho da bạn."));
        sections.add(new SectionData("Màu da & Sắc độ", BeautyReferenceMapper.SKIN_COLOR, "Chọn màu nền và son chuẩn xác."));
        sections.add(new SectionData("Ngân sách", BeautyReferenceMapper.BUDGET, "Cân đối chi phí tối ưu nhất cho bạn."));
    }

    private void setupViews(@NonNull View view) {
        layoutSectionsList = view.findViewById(R.id.layoutSectionsList);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
        tvSummarySkinType = view.findViewById(R.id.tvSummarySkinType);
        tvProgressLabel = view.findViewById(R.id.tvProgressLabel);
        progressProfile = view.findViewById(R.id.progressProfile);
        
        // AI Analysis Views
        layoutAiAnalysis = view.findViewById(R.id.layoutAiAnalysis);
        cardHealthScore = view.findViewById(R.id.cardHealthScore);
        tvScore = view.findViewById(R.id.tvScore);
        tvScoreStatus = view.findViewById(R.id.tvScoreStatus);
        tvAiVerdict = view.findViewById(R.id.tvAiVerdict);
        tvAiStats = view.findViewById(R.id.tvAiStats);
        progressScore = view.findViewById(R.id.progressScore);
        
        layoutRecommendations = view.findViewById(R.id.layoutRecommendations);
        rvRecommendedProducts = view.findViewById(R.id.rvRecommendedProducts);
        
        productAdapter = new RecommendationProductAdapter();
        productAdapter.setOnProductClickListener(new RecommendationProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                int containerId = (requireActivity().findViewById(R.id.main_fragment_container) != null)
                        ? R.id.main_fragment_container : R.id.main;
                getParentFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(containerId, com.example.frontend.feature.product.ProductDetailFragment.newInstance(product.getId()))
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onAddToCartClick(Product product) {
                cartViewModel.addToCart(product.getId(), null, 1);
                Toast.makeText(getContext(), "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });

        if (rvRecommendedProducts != null) {
            rvRecommendedProducts.setAdapter(productAdapter);
        }
    }

    private void observeViewModel() {
        viewModel.getProfileResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                if (result.status == NetworkResult.Status.SUCCESS) {
                    bindProfileData(result.data);
                } else if (result.status == NetworkResult.Status.ERROR) {
                    if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.VISIBLE);
                }
            }
        });

        viewModel.getReferencesResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.status == NetworkResult.Status.SUCCESS) {
                if (currentProfile != null) bindProfileData(currentProfile);
            }
        });

        viewModel.getRecommendationsResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.status == NetworkResult.Status.SUCCESS && result.data != null) {
                if (result.data.getProducts() != null && !result.data.getProducts().isEmpty()) {
                    productAdapter.setItems(result.data.getProducts());
                    if (layoutRecommendations != null) layoutRecommendations.setVisibility(View.VISIBLE);
                }
            }
        });

        recommendationViewModel.getHomepageRecommendations().observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.status == NetworkResult.Status.SUCCESS && result.data != null) {
                bindAnalysisData(result.data);
            }
        });
    }

    private void bindAnalysisData(RecommendationData recommendationData) {
        AiAnalysis analysis = recommendationData.getAiAnalysis();
        if (analysis == null) {
            if (layoutAiAnalysis != null) layoutAiAnalysis.setVisibility(View.GONE);
            return;
        }

        if (layoutAiAnalysis != null) layoutAiAnalysis.setVisibility(View.VISIBLE);
        
        int healthScore = analysis.getHealthScore() != null ? analysis.getHealthScore() : 0;
        if (tvScore != null) tvScore.setText(String.valueOf(healthScore));
        if (progressScore != null) {
            progressScore.setProgress(healthScore);
            int colorRes = (healthScore >= 75) ? R.color.success : 
                         (healthScore >= 50) ? android.R.color.holo_orange_light : 
                         android.R.color.holo_red_light;
            progressScore.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), colorRes)));
            if (tvScoreStatus != null) {
                tvScoreStatus.setText(healthScore >= 75 ? "Khỏe mạnh" : healthScore >= 50 ? "Trung bình" : "Cần cải thiện");
                tvScoreStatus.setTextColor(ContextCompat.getColor(requireContext(), colorRes));
            }
        }

        if (tvAiVerdict != null) tvAiVerdict.setText(analysis.getAnalysisText());
        if (tvAiStats != null) tvAiStats.setText("Chỉ số của bạn cao hơn " + (healthScore - 5) + "% người dùng.");

        List<String> ingredients = analysis.getIdealIngredients();
        View view = getView();
        if (ingredients != null && view != null) {
            updateIngredientCard(view, R.id.cardIngredient1, ingredients.size() > 0 ? ingredients.get(0) : null, R.drawable.ic_shield_star);
            updateIngredientCard(view, R.id.cardIngredient2, ingredients.size() > 1 ? ingredients.get(1) : null, R.drawable.ic_beaker);
            updateIngredientCard(view, R.id.cardIngredient3, ingredients.size() > 2 ? ingredients.get(2) : null, R.drawable.ic_goal_sparkle);
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
            icon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.button)));
        }
        card.setOnClickListener(v -> navigateToFragment(StepProductSuggestionsFragment.newInstance("Sản phẩm chứa " + name)));
    }

    private void bindProfileData(@Nullable CustomerBeautyProfileDto profile) {
        if (profile == null) return;
        this.currentProfile = profile;
        BeautyReferenceResolver resolver = viewModel.getReferenceResolver();
        
        int completion = profile.getProfileCompletionRate();
        if (progressProfile != null) progressProfile.setProgress(completion);
        if (tvProgressLabel != null) tvProgressLabel.setText(String.format("Hồ sơ hoàn thành %d%%", completion));

        if (resolver != null) {
            String skinTypeName = resolver.resolveName(profile.getSkinType());
            tvSummarySkinType.setText((skinTypeName != null && !skinTypeName.isEmpty()) ? skinTypeName : "Chưa xác định");
        }

        if (layoutSectionsList != null && resolver != null) {
            layoutSectionsList.removeAllViews();
            
            renderSection(sections.get(0), profile.getSkinType(), resolver);
            renderMultiSection(sections.get(1), profile.getSkinConcerns(), resolver);
            renderSection(sections.get(2), profile.getSensitivityLevel(), resolver);
            renderMultiSection(sections.get(3), profile.getBeautyGoals(), resolver);
            renderMultiSection(sections.get(4), profile.getPreferredIngredients(), resolver);
            renderMultiSection(sections.get(5), profile.getAvoidIngredients(), resolver);
            renderSection(sections.get(6), profile.getSkinColor(), resolver);
            renderSection(sections.get(7), profile.getBudget(), resolver);

            boolean hasContent = layoutSectionsList.getChildCount() > 0;
            if (layoutEmptyState != null) layoutEmptyState.setVisibility(hasContent ? View.GONE : View.VISIBLE);

            if (hasContent) {
                viewModel.loadRecommendations();
            }
        }
    }

    private void renderSection(SectionData section, String code, BeautyReferenceResolver resolver) {
        if (code == null || code.isEmpty() || code.contains("UNKNOWN")) return;
        List<String> codes = new ArrayList<>();
        codes.add(code);
        renderFlexItems(section, codes, resolver);
    }

    private void renderMultiSection(SectionData section, List<String> codes, BeautyReferenceResolver resolver) {
        if (codes != null && !codes.isEmpty()) {
            renderFlexItems(section, codes, resolver);
        }
    }

    private void renderFlexItems(SectionData section, List<String> selectedCodes, BeautyReferenceResolver resolver) {
        if (selectedCodes == null || selectedCodes.isEmpty()) return;
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        boolean isExpanded = Boolean.TRUE.equals(expandedSections.get(section.title));
        
        TextView tvTitle = new TextView(requireContext());
        tvTitle.setText(section.title);
        tvTitle.setTextColor(darkText);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        tvTitle.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(-2, -2);
        titleParams.topMargin = dpToPx(24);
        titleParams.bottomMargin = dpToPx(12);
        tvTitle.setLayoutParams(titleParams);
        layoutSectionsList.addView(tvTitle);

        LinearLayout sectionBox = new LinearLayout(requireContext());
        sectionBox.setOrientation(LinearLayout.VERTICAL);
        sectionBox.setBackgroundResource(R.drawable.bg_beauty_profile_section);
        sectionBox.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
        sectionBox.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));

        com.google.android.material.chip.ChipGroup chipGroup = new com.google.android.material.chip.ChipGroup(requireContext());
        chipGroup.setChipSpacingHorizontal(dpToPx(4));
        chipGroup.setChipSpacingVertical(dpToPx(4));
        
        List<View> allChips = new ArrayList<>();
        for (String code : selectedCodes) {
            String name = resolver.resolveName(code);
            if (name == null) continue;

            View chipItem = inflater.inflate(R.layout.item_overview_chip, chipGroup, false);
            ImageView icon = chipItem.findViewById(R.id.ivIcon);
            TextView label = chipItem.findViewById(R.id.tvLabel);
            
            Integer iconRes = referenceIcons.get(code);
            icon.setImageResource(iconRes != null ? iconRes : R.drawable.ic_face);
            label.setText(name);
            
            chipGroup.addView(chipItem);
            allChips.add(chipItem);
            if (!isExpanded && allChips.size() > 3) chipItem.setVisibility(View.GONE);
        }
        sectionBox.addView(chipGroup);

        if (allChips.size() > 3) {
            TextView tvToggle = new TextView(requireContext());
            tvToggle.setText(isExpanded ? "Thu gọn" : "Xem thêm (" + (allChips.size() - 3) + ")");
            tvToggle.setTextColor(brandPink);
            tvToggle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            tvToggle.setPadding(dpToPx(4), dpToPx(8), dpToPx(4), dpToPx(8));
            tvToggle.setOnClickListener(v -> {
                expandedSections.put(section.title, !isExpanded);
                bindProfileData(currentProfile);
            });
            chipGroup.addView(tvToggle);
        }

        TextView tvInsight = new TextView(requireContext());
        tvInsight.setText(section.insight);
        tvInsight.setTextColor(Color.parseColor("#9B8F8F"));
        tvInsight.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        LinearLayout.LayoutParams insightParams = new LinearLayout.LayoutParams(-1, -2);
        insightParams.topMargin = dpToPx(12);
        tvInsight.setLayoutParams(insightParams);
        sectionBox.addView(tvInsight);

        layoutSectionsList.addView(sectionBox);
    }

    private void setupEvents(@NonNull View view) {
        view.findViewById(R.id.btnBack).setOnClickListener(v -> handleBackNavigation());
        view.findViewById(R.id.btnEdit).setOnClickListener(v -> navigateToEditProfile());
        
        View btnAnalyzeSkin = view.findViewById(R.id.btnAnalyzeSkin);
        if (btnAnalyzeSkin != null) {
            btnAnalyzeSkin.setOnClickListener(v -> navigateToFragment(new SkinAnalysisFragment()));
        }

        View btnSavedRoutines = view.findViewById(R.id.btnSavedRoutines);
        if (btnSavedRoutines != null) {
            btnSavedRoutines.setOnClickListener(v -> navigateToFragment(new SavedBeautyRoutinesFragment()));
        }

        View btnViewAllRec = view.findViewById(R.id.btnViewAllRec);
        if (btnViewAllRec != null) {
            btnViewAllRec.setOnClickListener(v -> navigateToFragment(new SkinAnalysisFragment()));
        }
    }

    private void navigateToFragment(Fragment fragment) {
        int containerId = (requireActivity().findViewById(R.id.main_fragment_container) != null)
                ? R.id.main_fragment_container : R.id.main;
        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToEditProfile() {
        int containerId = (requireActivity().findViewById(R.id.main_fragment_container) != null)
                ? R.id.main_fragment_container : R.id.main;
        getParentFragmentManager().beginTransaction()
                .replace(containerId, new EditSkinProfileFragment())
                .addToBackStack(null)
                .commit();
    }

    private void handleBackNavigation() {
        if (getParentFragmentManager().getBackStackEntryCount() > 0) getParentFragmentManager().popBackStack();
        else requireActivity().getOnBackPressedDispatcher().onBackPressed();
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private static class SectionData {
        String title;
        String groupCode;
        String insight;
        SectionData(String title, String groupCode, String insight) {
            this.title = title;
            this.groupCode = groupCode;
            this.insight = insight;
        }
    }
}
