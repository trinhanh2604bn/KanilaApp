package com.example.frontend.feature.product;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.frontend.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ShareProductBottomSheet extends BottomSheetDialogFragment {
    private String productUrl;

    public static ShareProductBottomSheet newInstance(String productUrl) {
        ShareProductBottomSheet fragment = new ShareProductBottomSheet();
        fragment.productUrl = productUrl;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_share_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btnCopyLink).setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Product Link", productUrl);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "Đã sao chép liên kết", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        view.findViewById(R.id.btnSaveImage).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Đã lưu ảnh sản phẩm", Toast.LENGTH_SHORT).show();
            dismiss();
        });
    }
}
