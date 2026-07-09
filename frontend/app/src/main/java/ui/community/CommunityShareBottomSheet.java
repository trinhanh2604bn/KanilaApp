package ui.community;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.frontend.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CommunityShareBottomSheet extends BottomSheetDialogFragment {
    private Post post;
    private OnShareListener listener;

    public interface OnShareListener {
        void onShareSelected(Post post);
    }

    public static CommunityShareBottomSheet newInstance(Post post) {
        CommunityShareBottomSheet fragment = new CommunityShareBottomSheet();
        fragment.post = post;
        return fragment;
    }

    public void setOnShareListener(OnShareListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_community_share, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String shareLink = "https://kanila.app/community/posts/" + (post != null ? post.getId() : "preview");
        String postTitle = (post != null ? post.getTitle() : "");
        String shareText = getString(R.string.share_preview_link_text, postTitle);

        view.findViewById(R.id.btnCopyLink).setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Post Link", shareLink);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), R.string.share_link_copied, Toast.LENGTH_SHORT).show();
            notifyShare();
            dismiss();
        });

        view.findViewById(R.id.btnShareZalo).setOnClickListener(v -> {
            shareToApp("com.zing.zalo", shareText, shareLink);
            notifyShare();
        });
        view.findViewById(R.id.btnShareMessenger).setOnClickListener(v -> {
            shareToApp("com.facebook.orca", shareText, shareLink);
            notifyShare();
        });
        view.findViewById(R.id.btnShareInstagram).setOnClickListener(v -> {
            shareToApp("com.instagram.android", shareText, shareLink);
            notifyShare();
        });
        view.findViewById(R.id.btnShareOther).setOnClickListener(v -> {
            shareGeneric(shareText, shareLink);
            notifyShare();
        });
    }

    private void notifyShare() {
        if (listener != null) {
            listener.onShareSelected(post);
        }
    }

    private void shareToApp(String packageName, String text, String link) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text + " " + link);
        
        if (isAppInstalled(packageName)) {
            intent.setPackage(packageName);
            startActivity(intent);
        } else {
            // Fallback to generic share or show toast
            Toast.makeText(getContext(), R.string.share_app_not_installed, Toast.LENGTH_SHORT).show();
            shareGeneric(text, link);
        }
        dismiss();
    }

    private void shareGeneric(String text, String link) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text + " " + link);
        startActivity(Intent.createChooser(intent, getString(R.string.share_chooser_title)));
        dismiss();
    }

    private boolean isAppInstalled(String packageName) {
        PackageManager pm = requireContext().getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
