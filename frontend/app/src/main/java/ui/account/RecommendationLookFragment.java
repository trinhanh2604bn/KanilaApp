package ui.account;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.frontend.R;

public class RecommendationLookFragment extends Fragment {

    public RecommendationLookFragment() {
        // Kết nối Fragment với fragment_recommendation_look.xml
        super(R.layout.fragment_recommendation_look);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        setupViews(view);
        setupEvents(view);
    }

    private void setupViews(@NonNull View view) {
        // Ánh xạ các View khác tại đây khi cần
    }

    private void setupEvents(@NonNull View view) {

        /*
         * Trong XML, nút quay lại có ID là btnBackRec.
         */
        View btnBackRec = view.findViewById(R.id.btnBackRec);

        if (btnBackRec != null) {
            // Thêm hiệu ứng vòng tròn khi click
            TypedValue outValue = new TypedValue();
            requireContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
            btnBackRec.setBackgroundResource(outValue.resourceId);

            btnBackRec.setOnClickListener(v ->
                    handleBackNavigation()
            );
        }
    }

    /**
     * Quay lại màn hình BeautyProfileOverviewFragment.
     */
    private void handleBackNavigation() {

        FragmentManager fragmentManager =
                getParentFragmentManager();

        /*
         * Nếu có màn hình trong Back Stack,
         * quay lại màn hình trước.
         */
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {

            /*
             * Trường hợp Fragment không được mở bằng Back Stack,
             * sử dụng nút Back của Activity.
             */
            requireActivity()
                    .getOnBackPressedDispatcher()
                    .onBackPressed();
        }
    }
}