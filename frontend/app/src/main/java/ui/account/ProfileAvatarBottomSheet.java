package ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.frontend.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ProfileAvatarBottomSheet extends BottomSheetDialogFragment {

    public interface OnAvatarActionSelectedListener {
        void onTakePhotoSelected();
        void onChooseGallerySelected();
        void onRemoveAvatarSelected();
    }

    private OnAvatarActionSelectedListener listener;

    public void setOnAvatarActionSelectedListener(OnAvatarActionSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_profile_avatar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btnTakePhoto).setOnClickListener(v -> {
            if (listener != null) listener.onTakePhotoSelected();
            dismiss();
        });

        view.findViewById(R.id.btnChooseGallery).setOnClickListener(v -> {
            if (listener != null) listener.onChooseGallerySelected();
            dismiss();
        });

        view.findViewById(R.id.btnRemoveAvatar).setOnClickListener(v -> {
            if (listener != null) listener.onRemoveAvatarSelected();
            dismiss();
        });
    }
}
