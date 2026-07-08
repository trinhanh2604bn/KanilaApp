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
import android.widget.HorizontalScrollView;
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
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.feature.beauty.BeautyProfileViewModel;

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
    private com.example.frontend.feature.home.HomeProductAdapter productAdapter;
    private boolean isRecExpanded = false;
    private CustomerBeautyProfileDto currentProfile;
    private BeautyProfileViewModel viewModel;
    private final Map<String, Boolean> expandedSections = new HashMap<>();
    private final Handler ctaHandler = new Handler(Looper.getMainLooper());
    private boolean isCtaShown = false;

    private int brandPink, darkText, grayBorder;
    private final List<SectionData> sections = new ArrayList<>();

    public BeautyProfileOverviewFragment() {
        super(R.layout.fragment_beauty_profile_overview);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(BeautyProfileViewModel.class);

        initColors();
        initSectionData();
        setupViews(view);
        setupEvents(view);
        observeViewModel();

        NetworkResult<CustomerBeautyProfileDto> currentResult = viewModel.getProfileResult().getValue();
        if (currentResult == null) {
            viewModel.loadProfile("me");
            // If data is not available immediately, schedule CTA popup to show after a short delay
            // This ensures the user isn't stuck waiting for a failing network call (like in your logs)
            startCtaTimer();
        } else if (currentResult.status == NetworkResult.Status.SUCCESS) {
            bindProfileData(currentResult.data);
        } else {
            showCtaPopup();
        }
    }

    private void startCtaTimer() {
        ctaHandler.postDelayed(() -> {
            if (isAdded() && !isCtaShown && (currentProfile == null || currentProfile.getSkinType() == null)) {
                showCtaPopup();
            }
        }, 600); // Reduced delay to 0.6 seconds for faster interaction
    }

    private void initColors() {
        brandPink = ContextCompat.getColor(requireContext(), R.color.button);
        darkText = ContextCompat.getColor(requireContext(), R.color.accent_dark);
        grayBorder = ContextCompat.getColor(requireContext(), R.color.border_divider);
    }

    private void initSectionData() {
        sections.clear();
        sections.add(new SectionData("1. Loại da", new String[]{"Da dầu", "Da khô", "Da hỗn hợp", "Da thường", "Da nhạy cảm", "Chưa xác định"}, new int[]{R.drawable.ic_drops_filled, R.drawable.ic_drops, R.drawable.ic_skin_mixed, R.drawable.ic_skin_normal, R.drawable.ic_skin_sensitive, R.drawable.ic_unsure}, "Loại da quyết định nền tảng các sản phẩm dưỡng da của bạn."));
        sections.add(new SectionData("2. Tình trạng da", new String[]{"Mụn", "Thâm mụn", "Nám, sạm màu", "Da xỉn màu", "Lỗ chân lông to", "Mụn đầu đen", "Da dễ đỏ", "Da thiếu nước", "Nếp nhăn, lão hóa", "Bề mặt da không mịn", "Hàng rào da yếu", "Da chịu tác động của nắng"}, new int[]{R.drawable.ic_skin_acne, R.drawable.ic_skin_spots, R.drawable.ic_skin_spots, R.drawable.ic_skin_dullness, R.drawable.ic_skin_pores, R.drawable.ic_skin_acne, R.drawable.ic_skin_redness, R.drawable.ic_drops, R.drawable.ic_skin_aging, R.drawable.ic_skin_mixed, R.drawable.ic_goal_recovery, R.drawable.ic_sun}, "Kanila sẽ chọn hoạt chất đặc trị riêng cho từng vấn đề bạn đang gặp phải."));
        sections.add(new SectionData("3. Mức độ nhạy cảm", new String[]{"Ít nhạy cảm", "Dễ kích ứng nhẹ", "Rất nhạy cảm", "Dễ đỏ hoặc rát khi đổi sản phẩm"}, new int[]{R.drawable.ic_shield_star, R.drawable.ic_skin_sensitive, R.drawable.ic_alert, R.drawable.ic_skin_redness}, "Hồ sơ nhạy cảm giúp chúng mình loại bỏ các thành phần có nguy cơ kích ứng cao."));
        sections.add(new SectionData("4. Màu da", new String[]{"Da rất sáng", "Da sáng", "Da trung bình", "Da ngăm", "Da sẫm màu"}, new int[]{R.drawable.ic_sun, R.drawable.ic_sun, R.drawable.ic_sun, R.drawable.ic_sun, R.drawable.ic_sun}, "Tone màu da giúp gợi ý kem nền và phấn phủ chuẩn xác hơn."));
        sections.add(new SectionData("5. Sắc độ da", new String[]{"Sắc lạnh", "Sắc ấm", "Sắc trung tính", "Sắc ô liu", "Chưa xác định"}, new int[]{R.drawable.skin_tone, R.drawable.skin_tone, R.drawable.skin_tone, R.drawable.skin_tone, R.drawable.ic_unsure}, "Undertone là chìa khóa để chọn màu son và màu má phù hợp nhất."));
        sections.add(new SectionData("6. Hiệu ứng nền", new String[]{"Tự nhiên", "Sáng hơn tông da", "Căng bóng ánh ấm", "Tươi sáng ánh hồng", "Lì, ít bóng"}, new int[]{R.drawable.ic_face, R.drawable.ic_lightbulb, R.drawable.ic_sun, R.drawable.ic_face, R.drawable.ic_face}, "Lớp nền ưng ý sẽ mang lại vẻ ngoài rạng rỡ đúng ý muốn của bạn."));
        sections.add(new SectionData("7. Màu son", new String[]{"Màu nude", "Màu hồng", "Màu cam san hô", "Màu đỏ", "Màu nâu", "Màu môi tự nhiên", "Màu đậm, nổi bật"}, new int[]{R.drawable.ic_lipstick, R.drawable.ic_lipstick, R.drawable.ic_lipstick, R.drawable.ic_lipstick, R.drawable.ic_lipstick, R.drawable.ic_lipstick, R.drawable.ic_lipstick}, "Danh sách màu son yêu thích giúp Kanila thu hẹp phạm vi tìm kiếm."));
        sections.add(new SectionData("8. Trang điểm", new String[]{"Trang điểm tự nhiên", "Phong cách Hàn Quốc", "Trang điểm sắc sảo", "Trang điểm công sở", "Trang điểm dự tiệc", "Trang điểm hằng ngày"}, new int[]{R.drawable.ic_face, R.drawable.ic_face, R.drawable.ic_face, R.drawable.ic_face, R.drawable.ic_face, R.drawable.ic_face}, "Phong cách này sẽ định hình các sản phẩm trong bộ trang điểm gợi ý."));
        sections.add(new SectionData("9. Ngân sách", new String[]{"Dưới 300K", "300K - 500K", "500K +"}, new int[]{R.drawable.ic_wallet, R.drawable.ic_wallet, R.drawable.ic_wallet}, "Chúng mình sẽ cân đối các sản phẩm tốt nhất trong tầm giá bạn mong muốn."));
        sections.add(new SectionData("10. Cần tránh", new String[]{"Hương liệu", "Cồn khô", "Tinh dầu", "Paraben", "Dầu khoáng", "Silicone", "Sulfate", "Lanolin", "Retinoid", "Acid cao"}, new int[]{R.drawable.ic_drops, R.drawable.ic_beaker, R.drawable.ic_drops, R.drawable.ic_beaker, R.drawable.ic_drops, R.drawable.ic_beaker, R.drawable.ic_beaker, R.drawable.ic_beaker, R.drawable.ic_beaker, R.drawable.ic_beaker}, "Sản phẩm được gợi ý sẽ hoàn toàn không chứa các thành phần này."));
    }

    private void setupViews(@NonNull View view) {
        layoutSectionsList = view.findViewById(R.id.layoutSectionsList);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
        tvSummarySkinType = view.findViewById(R.id.tvSummarySkinType);
        tvProgressLabel = view.findViewById(R.id.tvProgressLabel);
        progressProfile = view.findViewById(R.id.progressProfile);
        
        layoutRecommendations = view.findViewById(R.id.layoutRecommendations);
        rvRecommendedProducts = view.findViewById(R.id.rvRecommendedProducts);
        
        productAdapter = new com.example.frontend.feature.home.HomeProductAdapter();
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        // Reduce width slightly to show more of the next card and fit better
        productAdapter.setItemWidth((int) (screenWidth * 0.43));

        if (rvRecommendedProducts != null) {
            rvRecommendedProducts.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
            rvRecommendedProducts.setAdapter(productAdapter);
        }

        // Show empty state by default until data is loaded
        if (layoutEmptyState != null) {
            layoutEmptyState.setVisibility(View.VISIBLE);
        }
    }

    private void observeViewModel() {
        viewModel.getProfileResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                if (result.status == NetworkResult.Status.SUCCESS) {
                    bindProfileData(result.data);
                } else if (result.status == NetworkResult.Status.ERROR) {
                    if (layoutEmptyState != null) {
                        layoutEmptyState.setVisibility(View.VISIBLE);
                        // Even on error (like ConnectException), show the popup to let user edit manually
                        showCtaPopup();
                    }
                }
            }
        });
    }

    private void bindProfileData(@Nullable CustomerBeautyProfileDto profile) {
        if (profile == null) return;
        this.currentProfile = profile;
        
        int completion = profile.getProfileCompletion();
        if (progressProfile != null) progressProfile.setProgress(completion);
        if (tvProgressLabel != null) tvProgressLabel.setText(getString(R.string.profile_completion_format, completion));

        if (profile.getSkinType() != null) {
            tvSummarySkinType.setText(profile.getSkinType());
        }

        if (layoutSectionsList != null) {
            layoutSectionsList.removeAllViews();
            renderSummarySection(sections.get(0), profile.getSkinType());
            renderMultiSummarySection(sections.get(1), profile.getSkinConcerns());
            renderSummarySection(sections.get(2), profile.getSensitivityLevel());
            renderSummarySection(sections.get(3), profile.getSkinColor());
            renderSummarySection(sections.get(4), profile.getSkinUndertone());
            renderSummarySection(sections.get(5), profile.getFoundationFinish());
            renderMultiSummarySection(sections.get(6), profile.getLipstickColors());
            renderMultiSummarySection(sections.get(7), profile.getMakeupStyles());
            renderSummarySection(sections.get(8), profile.getBudget());
            renderMultiSummarySection(sections.get(9), profile.getAvoidIngredients());

            if (layoutEmptyState != null) {
                boolean isEmpty = layoutSectionsList.getChildCount() == 0;
                layoutEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                
                if (isEmpty) {
                    showCtaPopup();
                }

                // Hide recommendations if no profile data yet
                if (layoutRecommendations != null) {
                    layoutRecommendations.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
                }
            }
        }

        if (layoutSectionsList != null && layoutSectionsList.getChildCount() > 0) {
            showRecommendations(profile);
        }
    }

    private void showRecommendations(CustomerBeautyProfileDto profile) {
        if (layoutRecommendations != null) {
            layoutRecommendations.setVisibility(View.VISIBLE);
            
            List<com.example.frontend.model.Product> mockProducts = new ArrayList<>();
            String skinType = profile.getSkinType() != null ? profile.getSkinType() : "Da dầu";
            
            if (skinType.contains("dầu")) {
                mockProducts.add(new com.example.frontend.model.Product("1", "Cosrx", "Sữa rửa mặt BHA", "250000", "4.5", "120", R.drawable.cl_product, "Hot", "Face"));
                mockProducts.add(new com.example.frontend.model.Product("2", "The Ordinary", "Serum Niacinamide", "220000", "4.8", "450", R.drawable.cl_product, "Best Seller", "Serum"));
                mockProducts.add(new com.example.frontend.model.Product("3", "La Roche-Posay", "Kem dưỡng kiềm dầu", "480000", "4.7", "310", R.drawable.cl_product, "New", "Moisturizer"));
                if (isRecExpanded) {
                    mockProducts.add(new com.example.frontend.model.Product("6", "Paula's Choice", "BHA Liquid Exfoliant", "910000", "4.9", "1200", R.drawable.cl_product, "Premium", "Toner"));
                    mockProducts.add(new com.example.frontend.model.Product("7", "Innisfree", "Jeju Volcanic Clay Mask", "340000", "4.6", "800", R.drawable.cl_product, "Popular", "Mask"));
                }
            } else {
                mockProducts.add(new com.example.frontend.model.Product("4", "Neutrogena", "Kem dưỡng ẩm HA", "350000", "4.6", "215", R.drawable.cl_product, "Hot", "Moisturizer"));
                mockProducts.add(new com.example.frontend.model.Product("5", "Bioderma", "Tẩy trang dịu nhẹ", "390000", "4.9", "890", R.drawable.cl_product, "Recommended", "Cleansing"));
                if (isRecExpanded) {
                    mockProducts.add(new com.example.frontend.model.Product("8", "Laneige", "Water Bank Cream", "850000", "4.8", "600", R.drawable.cl_product, "Best Seller", "Moisturizer"));
                }
            }
            
            productAdapter.setProducts(mockProducts);
        }
    }

    private void renderSummarySection(SectionData section, String selectedValue) {
        if (selectedValue == null) return;
        List<String> list = new ArrayList<>();
        list.add(selectedValue);
        renderFlexItems(section, list);
    }

    private void renderMultiSummarySection(SectionData section, List<String> selectedValues) {
        if (selectedValues != null && !selectedValues.isEmpty()) {
            renderFlexItems(section, selectedValues);
        }
    }

    private void renderFlexItems(SectionData section, List<String> selectedLabels) {
        if (selectedLabels == null || selectedLabels.isEmpty()) return;
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        boolean isExpanded = Boolean.TRUE.equals(expandedSections.get(section.title));
        
        // 1. Title
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

        // 2. Section Box (Vertical Container)
        LinearLayout sectionBox = new LinearLayout(requireContext());
        sectionBox.setOrientation(LinearLayout.VERTICAL);
        sectionBox.setBackgroundResource(R.drawable.bg_beauty_profile_section);
        sectionBox.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
        LinearLayout.LayoutParams boxParams = new LinearLayout.LayoutParams(-1, -2);
        sectionBox.setLayoutParams(boxParams);

        // 3. Chips Container (ChipGroup for auto-wrapping)
        com.google.android.material.chip.ChipGroup chipGroup = new com.google.android.material.chip.ChipGroup(requireContext());
        chipGroup.setChipSpacingHorizontal(dpToPx(4));
        chipGroup.setChipSpacingVertical(dpToPx(4));
        
        List<View> allChips = new ArrayList<>();
        for (int i = 0; i < section.labels.length; i++) {
            final String labelText = section.labels[i];
            boolean isSelected = false;
            for (String sel : selectedLabels) {
                if (sel.equalsIgnoreCase(labelText)) {
                    isSelected = true;
                    break;
                }
            }

            if (isSelected) {
                View chipItem = inflater.inflate(R.layout.item_overview_chip, chipGroup, false);
                ImageView icon = chipItem.findViewById(R.id.ivIcon);
                TextView label = chipItem.findViewById(R.id.tvLabel);
                
                icon.setImageResource(section.icons[i]);
                label.setText(labelText);
                
                chipGroup.addView(chipItem);
                allChips.add(chipItem);
                
                // Show only 3 chips if not expanded
                if (!isExpanded && allChips.size() > 3) {
                    chipItem.setVisibility(View.GONE);
                }
            }
        }
        sectionBox.addView(chipGroup);

        // 4. "Xem thêm" Button inline with chips
        if (allChips.size() > 3) {
            TextView tvToggle = new TextView(requireContext());
            tvToggle.setText(isExpanded ? "Thu gọn" : "Xem thêm (" + (allChips.size() - 3) + ")");
            tvToggle.setTextColor(brandPink);
            tvToggle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            tvToggle.setPadding(dpToPx(4), dpToPx(8), dpToPx(4), dpToPx(8));
            tvToggle.setGravity(android.view.Gravity.CENTER_VERTICAL);
            
            tvToggle.setOnClickListener(v -> {
                expandedSections.put(section.title, !isExpanded);
                bindProfileData(currentProfile);
            });
            chipGroup.addView(tvToggle);
        }

        // 5. Insight/Tip (Below chips)
        TextView tvInsight = new TextView(requireContext());
        tvInsight.setText(section.insight);
        tvInsight.setTextColor(Color.parseColor("#9B8F8F"));
        tvInsight.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tvInsight.setLineSpacing(0, 1.2f);
        LinearLayout.LayoutParams insightParams = new LinearLayout.LayoutParams(-1, -2);
        insightParams.topMargin = dpToPx(12);
        tvInsight.setLayoutParams(insightParams);
        sectionBox.addView(tvInsight);

        layoutSectionsList.addView(sectionBox);
    }

    private void setupEvents(@NonNull View view) {
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> handleBackNavigation());
        
        View btnEdit = view.findViewById(R.id.btnEdit);
        if (btnEdit != null) btnEdit.setOnClickListener(v -> navigateToEditProfile());
        
        View btnSavedRoutines = view.findViewById(R.id.btnSavedRoutines);
        if (btnSavedRoutines != null) {
            btnSavedRoutines.setOnClickListener(v -> {
                getParentFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.main_fragment_container, new SavedBeautyRoutinesFragment())
                        .addToBackStack("beauty_profile_to_saved")
                        .commit();
            });
        }

        View btnAnalyzeSkin = view.findViewById(R.id.btnAnalyzeSkin);
        if (btnAnalyzeSkin != null) {
            btnAnalyzeSkin.setOnClickListener(v -> {
                getParentFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.main_fragment_container, new SkinAnalysisFragment())
                        .addToBackStack("beauty_profile_to_analysis")
                        .commit();
            });
        }

        View btnViewAllRec = view.findViewById(R.id.btnViewAllRec);
        if (btnViewAllRec != null) {
            btnViewAllRec.setOnClickListener(v -> toggleRecommendations());
        }
    }

    private void navigateToEditProfile() {
        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.main_fragment_container, new EditSkinProfileFragment())
                .addToBackStack("beauty_profile_to_edit")
                .commit();
    }

    private void toggleRecommendations() {
        if (currentProfile == null) return;
        
        isRecExpanded = !isRecExpanded;
        
        if (isRecExpanded) {
            rvRecommendedProducts.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(requireContext(), 2));
            rvRecommendedProducts.setNestedScrollingEnabled(false);
            productAdapter.setItemWidth(-1);
            
            View btnViewAll = getView().findViewById(R.id.btnViewAllRec);
            if (btnViewAll instanceof TextView) {
                ((TextView) btnViewAll).setText("Thu gọn");
            }
        } else {
            rvRecommendedProducts.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
            rvRecommendedProducts.setNestedScrollingEnabled(true);
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            productAdapter.setItemWidth((int) (screenWidth * 0.46));
            
            View btnViewAll = getView().findViewById(R.id.btnViewAllRec);
            if (btnViewAll instanceof TextView) {
                ((TextView) btnViewAll).setText(R.string.action_see_all);
            }
        }
        
        showRecommendations(currentProfile);
    }

    private void handleBackNavigation() {
        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
            getParentFragmentManager().popBackStack();
        } else {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        }
    }

    private void showCtaPopup() {
        if (!isAdded() || getContext() == null || isCtaShown) return;
        isCtaShown = true;
        ctaHandler.removeCallbacksAndMessages(null);
        
        android.app.Dialog dialog = new android.app.Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_beauty_profile_cta);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        View btnEdit = dialog.findViewById(R.id.btnGoToEdit);
        if (btnEdit != null) {
            btnEdit.setOnClickListener(v -> {
                dialog.dismiss();
                navigateToEditProfile();
            });
        }

        View btnLater = dialog.findViewById(R.id.btnMaybeLater);
        if (btnLater != null) {
            btnLater.setOnClickListener(v -> dialog.dismiss());
        }

        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = 0.6f;
            window.setAttributes(params);
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onDestroyView() {
        ctaHandler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private static class SectionData {
        String title;
        String[] labels;
        int[] icons;
        String insight;
        SectionData(String title, String[] labels, int[] icons, String insight) {
            this.title = title;
            this.labels = labels;
            this.icons = icons;
            this.insight = insight;
        }
    }
}
