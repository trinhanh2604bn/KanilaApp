package ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.frontend.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AvatarBottomSheet extends BottomSheetDialogFragment {

    public interface OnAvatarSourceSelectedListener {
        void onTakePhotoSelected();
        void onChooseGallerySelected();
    }

    private OnAvatarSourceSelectedListener listener;
    private boolean showRemoveOption = false;

    public void setOnAvatarSourceSelectedListener(OnAvatarSourceSelectedListener listener) {
        this.listener = listener;
    }

    public void setShowRemoveOption(boolean showRemoveOption) {
        this.showRemoveOption = showRemoveOption;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_profile_avatar, container, false);

        view.findViewById(R.id.btnTakePhoto).setOnClickListener(v -> {
            if (listener != null) {
                listener.onTakePhotoSelected();
            }
            dismiss();
        });

        view.findViewById(R.id.btnChooseGallery).setOnClickListener(v -> {
            if (listener != null) {
                listener.onChooseGallerySelected();
            }
            dismiss();
        });

        View btnRemove = view.findViewById(R.id.btnRemoveAvatar);
        btnRemove.setVisibility(showRemoveOption ? View.VISIBLE : View.GONE);
        btnRemove.setOnClickListener(v -> {
            // TODO: Handle remove
            dismiss();
        });

        return view;
    }
}
