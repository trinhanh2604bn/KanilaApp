package com.example.frontend.feature.chatbot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

        setupClickListeners(view);
    }

    private void setupClickListeners(View view) {
        view.findViewById(R.id.menuProductConsult).setOnClickListener(v -> handleMenuClick(getString(R.string.starter_product_consult)));
        view.findViewById(R.id.menuCreateRoutine).setOnClickListener(v -> handleMenuClick(getString(R.string.starter_create_routine)));
        view.findViewById(R.id.menuIngredientsCheck).setOnClickListener(v -> handleMenuClick(getString(R.string.starter_ingredients_check)));
        view.findViewById(R.id.menuTrackOrder).setOnClickListener(v -> handleMenuClick(getString(R.string.starter_track_order)));
        view.findViewById(R.id.menuGetSupport).setOnClickListener(v -> handleMenuClick(getString(R.string.starter_get_support)));
    }

    private void handleMenuClick(String starterMessage) {
        if (getActivity() != null) {
            ui.common.FragmentNavigationHelper.loadFragment(getActivity(), ChatConversationFragment.newInstance(starterMessage));
        }
        dismiss();
    }
}
