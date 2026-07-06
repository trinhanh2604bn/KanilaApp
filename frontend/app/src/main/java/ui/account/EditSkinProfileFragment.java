package ui.account;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.frontend.R;

public class EditSkinProfileFragment extends Fragment {

    public EditSkinProfileFragment() {
        // Kết nối Fragment Java với fragment_edit_skin_profile.xml
        super(R.layout.fragment_edit_skin_profile);
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
        // Khai báo và ánh xạ View trong XML tại đây.
        // Ví dụ:
        // TextView tvTitle = view.findViewById(R.id.tvTitle);
    }

    private void setupEvents(@NonNull View view) {
        // Nút quay lại ở góc trái
        ImageView btnBack = view.findViewById(R.id.btnBack);

        if (btnBack != null) {
            // Thêm hiệu ứng vòng tròn khi click
            TypedValue outValue = new TypedValue();
            requireContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
            btnBack.setBackgroundResource(outValue.resourceId);

            btnBack.setOnClickListener(v -> handleBackNavigation());
        }
    }

    /**
     * Quay lại màn hình trước.
     */
    private void handleBackNavigation() {
        FragmentManager fragmentManager = getParentFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            requireActivity()
                    .getOnBackPressedDispatcher()
                    .onBackPressed();
        }
    }
}