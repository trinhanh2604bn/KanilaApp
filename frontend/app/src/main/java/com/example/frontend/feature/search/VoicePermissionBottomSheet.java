package com.example.frontend.feature.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.frontend.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class VoicePermissionBottomSheet extends BottomSheetDialogFragment {

    private Runnable onAllowListener;

    public void setOnAllowListener(Runnable onAllowListener) {
        this.onAllowListener = onAllowListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_voice_permission, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View btnAllow = view.findViewById(R.id.btnAllow);
        View btnDeny = view.findViewById(R.id.btnDeny);

        if (btnAllow != null) {
            btnAllow.setOnClickListener(v -> {
                dismiss();
                if (onAllowListener != null) {
                    onAllowListener.run();
                }
            });
        }

        if (btnDeny != null) {
            btnDeny.setOnClickListener(v -> dismiss());
        }
    }
}
