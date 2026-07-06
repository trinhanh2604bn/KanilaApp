package ui.account;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.frontend.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import ui.common.ViewUtils;

public class SkinAnalysisFragment extends Fragment {

    public SkinAnalysisFragment() {
        super(R.layout.fragment_skin_analysis);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupEvents(view);
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
                getParentFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.main, new RecommendationLookFragment())
                        .addToBackStack(null)
                        .commit();
            });
        }
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
