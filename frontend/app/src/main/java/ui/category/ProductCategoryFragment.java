package ui.category;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.frontend.R;
import ui.common.BottomNavigationHelper;

public class ProductCategoryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_product_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Setup Top Bar
        setupTopBar(view);

        // 2. Bind Category Grid
        bindCategoryCards(view);

        // 3. Bind Demand Section
        bindDemandCards(view);

        // 4. Bind Brand Section
        bindBrandCards(view);

        // 5. Setup Bottom Navigation
        BottomNavigationHelper.setup(view, tabIndex -> {
            // Navigation handled by BottomNavigationHelper
        });
        BottomNavigationHelper.setSelectedTab(view, BottomNavigationHelper.TAB_CATEGORY);
    }

    private void setupTopBar(View root) {
        View topBar = root.findViewById(R.id.includeTopBar);
        if (topBar == null) return;

        TextView tvTitle = topBar.findViewById(R.id.tvTopBarTitle);
        if (tvTitle != null) tvTitle.setText(R.string.top_bar_category_title);

        ImageButton btnSearch = topBar.findViewById(R.id.btnTopBarSearch);
        View containerSearchExpanded = root.findViewById(R.id.containerSearchExpanded);

        if (btnSearch != null && containerSearchExpanded != null) {
            btnSearch.setOnClickListener(v -> {
                int visibility = containerSearchExpanded.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
                containerSearchExpanded.setVisibility(visibility);
            });
        }
    }

    private void bindCategoryCards(View root) {
        bindCategoryCard(root.findViewById(R.id.cardCategoryFace), R.drawable.ic_face, R.drawable.img_foudation, "Face");
        bindCategoryCard(root.findViewById(R.id.cardCategoryLips), R.drawable.ic_lipstick, R.drawable.img_lipstick, "Lips");
        bindCategoryCard(root.findViewById(R.id.cardCategoryEyes), R.drawable.ic_eyeshadow, R.drawable.img_eyeshadow, "Eyes");
        bindCategoryCard(root.findViewById(R.id.cardCategoryCheeks), R.drawable.ic_blush, R.drawable.img_blush, "Cheeks");
        bindCategoryCard(root.findViewById(R.id.cardCategoryGift), R.drawable.ic_gift, R.drawable.img_gift, "Gift");
        bindCategoryCard(root.findViewById(R.id.cardCategoryNew), R.drawable.ic_new, R.drawable.img_new, "New");
        bindCategoryCard(root.findViewById(R.id.cardCategoryHot), R.drawable.ic_hot, R.drawable.img_hot, "Hot");
        bindCategoryCard(root.findViewById(R.id.cardCategoryBrushes), R.drawable.ic_brush, R.drawable.img_brush, "Mini & Travel");
        bindCategoryCard(root.findViewById(R.id.cardCategoryAR), R.drawable.ic_ar, R.drawable.img_ar, "AR");
    }

    private void bindCategoryCard(View card, int iconRes, int imageRes, String title) {
        if (card == null) return;
        ImageView icon = card.findViewById(R.id.ivCategoryIcon);
        ImageView image = card.findViewById(R.id.ivCategoryProductImage);
        TextView titleView = card.findViewById(R.id.tvCategoryName);

        if (icon != null) icon.setImageResource(iconRes);
        if (image != null) image.setImageResource(imageRes);
        if (titleView != null) titleView.setText(title);
    }

    private void bindDemandCards(View root) {
        bindDemandCard(root.findViewById(R.id.cardDemandConcealing), R.drawable.ic_face, "Concealing", R.color.status_success_bg);
        bindDemandCard(root.findViewById(R.id.cardDemandMoisturizing), R.drawable.ic_drops, "Dưỡng ẩm", R.color.status_preparing_bg);
        bindDemandCard(root.findViewById(R.id.cardDemandSunproof), R.drawable.ic_sun, "Sunproof", R.color.status_pending_bg);
        bindDemandCard(root.findViewById(R.id.cardDemandToneUp), R.drawable.ic_cream, "Nâng tone", R.color.pink_bg);
        bindDemandCard(root.findViewById(R.id.cardDemandWaterproof), R.drawable.ic_check, "Waterproof", R.color.status_exchange_bg);
    }

    private void bindDemandCard(View card, int iconRes, String title, int circleColorRes) {
        if (card == null) return;
        ImageView icon = card.findViewById(R.id.ivDemandIcon);
        TextView titleView = card.findViewById(R.id.tvDemandTitle);
        View circle = card.findViewById(R.id.layoutCategoryIconCircle);

        if (icon != null) icon.setImageResource(iconRes);
        if (titleView != null) titleView.setText(title);
        if (circle != null && getContext() != null) {
            circle.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), circleColorRes)));
        }
    }

    private void bindBrandCards(View root) {
        bindBrandCard(root.findViewById(R.id.cardBrandMaybelline), R.drawable.img_foudation, R.color.text_main);
        bindBrandCard(root.findViewById(R.id.cardBrandHuda), R.drawable.img_lipstick, R.color.button);
        bindBrandCard(root.findViewById(R.id.cardBrandFwee), R.drawable.brand_fee, R.color.info);
        bindBrandCard(root.findViewById(R.id.cardBrandJudydoll), R.drawable.img_blush, R.color.primary);
        bindBrandCard(root.findViewById(R.id.cardBrandAnastasia), R.drawable.img_eyeshadow, R.color.background_main);
    }

    private void bindBrandCard(View card, int logoRes, int bgColorRes) {
        if (card == null) return;
        ImageView logo = card.findViewById(R.id.ivBrandLogo);
        if (logo != null) {
            logo.setImageResource(logoRes);
            logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        if (getContext() != null) {
            card.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), bgColorRes)));
        }
    }
}
