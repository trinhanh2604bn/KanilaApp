package ui.support;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.frontend.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ChatbotQuickMenuBottomSheet extends BottomSheetDialogFragment {

    public static ChatbotQuickMenuBottomSheet newInstance() {
        return new ChatbotQuickMenuBottomSheet();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_chatbot_quick_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.menuTrackOrder).setOnClickListener(v -> {
            replaceFragment(new OrderTrackingResultFragment());
            dismiss();
        });
        view.findViewById(R.id.menuIngredientsCheck).setOnClickListener(v -> {
            // Logic for Ingredients Check
            dismiss();
        });
        view.findViewById(R.id.menuSkinRoutine).setOnClickListener(v -> {
            // Logic for Skin Routine
            dismiss();
        });
        view.findViewById(R.id.menuCreateSupport).setOnClickListener(v -> {
            replaceFragment(new CreateTicketFragment());
            dismiss();
        });
        view.findViewById(R.id.menuTalkHuman).setOnClickListener(v -> {
            replaceFragment(new HumanHandoffFragment());
            dismiss();
        });
        view.findViewById(R.id.menuClearChat).setOnClickListener(v -> {
            dismiss();
        });
    }

    private void replaceFragment(androidx.fragment.app.Fragment fragment) {
        // R.id.container7 is from MainActivity7
        getParentFragmentManager().beginTransaction()
                .replace(R.id.container7, fragment)
                .addToBackStack(null)
                .commit();
    }
}
