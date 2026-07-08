package ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.frontend.R;
import com.example.frontend.data.model.beauty.SavedRoutineDto;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import ui.common.ViewUtils;

public class SavedBeautyRoutinesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_saved_beauty_routines, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        setupEvents(view);
    }

    private void setupViews(View view) {
        TextView tvDate1 = view.findViewById(R.id.tvDate1);
        TextView tvDate2 = view.findViewById(R.id.tvDate2);

        String today = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        if (tvDate1 != null) {
            String savedDate = "08/07/2026"; // Mock date for item 1
            tvDate1.setText(formatSavedDate(savedDate, today));
        }

        if (tvDate2 != null) {
            String savedDate = "05/07/2026"; // Mock date for item 2
            tvDate2.setText(formatSavedDate(savedDate, today));
        }
    }

    private String formatSavedDate(String savedDate, String today) {
        if (savedDate.equals(today)) {
            return "Vừa lưu";
        } else {
            return "Đã lưu ngày " + savedDate;
        }
    }

    private void setupEvents(View view) {
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }

        View cardRoutine1 = view.findViewById(R.id.cardRoutine1);
        if (cardRoutine1 != null) {
            ViewUtils.applyClickAnimation(cardRoutine1);
            cardRoutine1.setOnClickListener(v -> {
                SavedRoutineDto data = new SavedRoutineDto("1", "Clean Girl Look", "08/07/2026", R.drawable.hinh_nen);
                navigateToDetail(data);
            });
        }

        View cardRoutine2 = view.findViewById(R.id.cardRoutine2);
        if (cardRoutine2 != null) {
            ViewUtils.applyClickAnimation(cardRoutine2);
            cardRoutine2.setOnClickListener(v -> {
                SavedRoutineDto data = new SavedRoutineDto("2", "Glass Skin Routine", "05/07/2026", R.drawable.bg_slide_1);
                navigateToDetail(data);
            });
        }
    }

    private void navigateToDetail(SavedRoutineDto routine) {
        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.main_fragment_container, RecommendationLookFragment.newInstance(routine))
                .addToBackStack(null)
                .commit();
    }
}
