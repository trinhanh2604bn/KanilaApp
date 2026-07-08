package ui.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.frontend.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ProfileAvatarBottomSheet extends BottomSheetDialogFragment {

    public interface OnAvatarActionListener {
        void onTakePhoto();
        void onChooseGallery();
        void onRemoveAvatar();
    }

    private OnAvatarActionListener listener;

    public void setOnAvatarActionListener(OnAvatarActionListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_profile_avatar, container, false);
        
        view.findViewById(R.id.btnTakePhoto).setOnClickListener(v -> {
            if (listener != null) listener.onTakePhoto();
            dismiss();
        });

        view.findViewById(R.id.btnChooseGallery).setOnClickListener(v -> {
            if (listener != null) listener.onChooseGallery();
            dismiss();
        });

        view.findViewById(R.id.btnRemoveAvatar).setOnClickListener(v -> {
            if (listener != null) listener.onRemoveAvatar();
            dismiss();
        });

        return view;
    }
}
