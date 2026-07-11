package ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.os.BundleCompat;
import com.example.frontend.R;
import com.example.frontend.data.model.beauty.SavedRoutineDto;
import ui.common.ViewUtils;

public class RoutineUsageGuideFragment extends Fragment {

    private SavedRoutineDto routineData;

    public static RoutineUsageGuideFragment newInstance(SavedRoutineDto routine) {
        RoutineUsageGuideFragment fragment = new RoutineUsageGuideFragment();
        Bundle args = new Bundle();
        args.putSerializable("routine_data", routine);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_routine_usage_guide, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            routineData = BundleCompat.getSerializable(getArguments(), "routine_data", SavedRoutineDto.class);
        }

        setupHeader(view);
        setupSteps(view);
        
        view.findViewById(R.id.btnBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void setupHeader(View view) {
        if (routineData == null) return;
        
        TextView tvName = view.findViewById(R.id.tvRoutineName);
        ImageView ivHero = view.findViewById(R.id.ivHeroImage);
        
        if (tvName != null) tvName.setText(routineData.getName());
        if (ivHero != null && routineData.getImageRes() != 0) {
            ivHero.setImageResource(routineData.getImageRes());
        }
    }

    private void setupSteps(View view) {
        LinearLayout layoutSteps = view.findViewById(R.id.layoutSteps);
        if (layoutSteps == null) return;
        layoutSteps.removeAllViews();

        String name = routineData != null ? routineData.getName().toLowerCase() : "";

        if (name.contains("glass skin") || name.contains("hàn quốc")) {
            addStep(layoutSteps, 1, "Chuẩn bị da", "Kem lót bắt sáng", "Sử dụng một lượng nhỏ chấm lên 5 điểm trên mặt, vỗ nhẹ để tạo hiệu ứng căng bóng tự nhiên.", R.drawable.img_foudation);
            addStep(layoutSteps, 2, "Lớp nền mỏng nhẹ", "Cushion", "Dùng bông mút dặm đều từ tâm mặt ra ngoài. Chú ý dặm nhẹ tay để lớp nền bám chặt và mỏng mịn.", R.drawable.cl_product);
            addStep(layoutSteps, 3, "Khoá ẩm", "Xịt khoá nền Glow", "Xịt cách mặt 20cm theo hình chữ X để giữ lớp nền bền màu suốt 8h mà vẫn căng mọng.", R.drawable.img_eyeshadow);
            addStep(layoutSteps, 4, "Tạo điểm nhấn", "Son bóng có màu", "Thoa một lớp mỏng ở lòng môi và tán nhẹ ra viền môi để tạo hiệu ứng môi mọng nước.", R.drawable.img_lipstick);
        } else if (name.contains("oil-control") || name.contains("dầu")) {
            addStep(layoutSteps, 1, "Kiềm dầu", "Kem lót Primer", "Tập trung thoa vào vùng chữ T để làm mờ lỗ chân lông và ngăn đổ dầu.", R.drawable.img_foudation);
            addStep(layoutSteps, 2, "Nền bền màu", "Kem nền Matte", "Sử dụng cọ tán đều để có độ che phủ cao nhưng vẫn mỏng nhẹ, không gây bí da.", R.drawable.cl_product);
            addStep(layoutSteps, 3, "Cố định", "Phấn phủ bột", "Dùng cọ lớn phủ nhẹ một lớp phấn bột để hút bã nhờn dư thừa và giữ nền không bị xê dịch.", R.drawable.img_eyeshadow);
            addStep(layoutSteps, 4, "Hoàn thiện", "Son kem lì", "Thoa đều và chờ 30s để son set lại hoàn toàn, không gây lem khi đeo khẩu trang.", R.drawable.img_lipstick);
        } else {
            // Default steps
            addStep(layoutSteps, 1, "Lớp lót", "Kem dưỡng lót", "Thoa đều để tạo bề mặt da ẩm mịn giúp lớp trang điểm bám tốt hơn.", R.drawable.img_foudation);
            addStep(layoutSteps, 2, "Lớp nền", "Kem nền / BB Cream", "Tán đều bằng mút ẩm để có lớp nền tự nhiên nhất.", R.drawable.cl_product);
            addStep(layoutSteps, 3, "Cố định", "Phấn phủ nhẹ", "Phủ nhẹ vùng chữ T để tránh bóng nhờn.", R.drawable.img_eyeshadow);
            addStep(layoutSteps, 4, "Màu sắc", "Son môi", "Chọn màu son theo sở thích để làm nổi bật khuôn mặt.", R.drawable.img_lipstick);
        }
    }

    private void addStep(LinearLayout parent, int num, String title, String prodName, String usage, int imgRes) {
        View stepView = LayoutInflater.from(requireContext()).inflate(R.layout.item_guide_step, parent, false);
        
        TextView tvNum = stepView.findViewById(R.id.tvStepNumber);
        TextView tvTitle = stepView.findViewById(R.id.tvStepTitle);
        TextView tvProd = stepView.findViewById(R.id.tvProductName);
        TextView tvUsage = stepView.findViewById(R.id.tvUsageInstructions);
        ImageView ivProd = stepView.findViewById(R.id.ivStepProduct);
        View btnViewProduct = stepView.findViewById(R.id.btnViewProduct);

        if (tvNum != null) tvNum.setText(String.valueOf(num));
        if (tvTitle != null) {
            String stepTitle = "Bước " + num + ": " + title;
            tvTitle.setText(stepTitle);
        }
        if (tvProd != null) tvProd.setText(prodName);
        if (tvUsage != null) tvUsage.setText(usage);
        if (ivProd != null && imgRes != 0) ivProd.setImageResource(imgRes);

        parent.addView(stepView);
        
        // Handle click on the suggested product area
        View productArea = stepView.findViewById(R.id.tvSuggestedProduct).getParent().getParent() instanceof View 
                ? (View) stepView.findViewById(R.id.tvSuggestedProduct).getParent().getParent() : null;
        
        if (productArea != null) {
            ViewUtils.applyClickAnimation(productArea);
            productArea.setOnClickListener(v -> {
                // Navigate to product suggestions for this specific step/item
                int containerId = (requireActivity().findViewById(R.id.main_fragment_container) != null)
                        ? R.id.main_fragment_container : R.id.main;
                getParentFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(containerId, StepProductSuggestionsFragment.newInstance(prodName))
                        .addToBackStack(null)
                        .commit();
            });
        }
    }
}
