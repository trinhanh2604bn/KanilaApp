package ui.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.frontend.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class MediaSourceBottomSheet extends BottomSheetDialogFragment {

    public interface OnMediaSourceSelectedListener {
        void onTakePhotoSelected();
        void onChooseGallerySelected();
        void onRecordVideoSelected();
    }

    private OnMediaSourceSelectedListener listener;

    public void setOnMediaSourceSelectedListener(OnMediaSourceSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_media_source, container, false);

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

        view.findViewById(R.id.btnRecordVideo).setOnClickListener(v -> {
            if (listener != null) {
                listener.onRecordVideoSelected();
            }
            dismiss();
        });

        return view;
    }
}
