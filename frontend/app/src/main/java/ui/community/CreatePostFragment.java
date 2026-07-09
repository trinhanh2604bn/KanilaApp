package ui.community;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreatePostFragment extends Fragment {

    private EditText edtCaption;
    private TextView tvCharCounter;
    private Button btnPost;
    private RecyclerView rvSelectedMedia;
    private SelectedMediaAdapter mediaAdapter;
    private final List<Uri> selectedMediaUris = new ArrayList<>();
    
    private Uri pendingCameraUri;
    private boolean isVideoPending = false;

    private String selectedPostType = "Review";
    private String selectedSkinType = "Da dầu mụn";

    private final String[] postTypes = {"Review", "Routine", "Before/After", "Hỏi đáp"};
    private final int[] postTypeIcons = {R.drawable.ic_star, R.drawable.ic_routine, R.drawable.ic_photo, R.drawable.ic_comment};
    private final String[] skinTypes = {"Da dầu mụn", "Da khô", "Da hỗn hợp", "Da nhạy cảm"};

    private final ActivityResultLauncher<String> cameraPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    if (isVideoPending) {
                        openCameraForVideo();
                    } else {
                        openCameraForPhoto();
                    }
                } else {
                    Toast.makeText(getContext(), "Camera permission is required", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    if (result.getData().getClipData() != null) {
                        int count = result.getData().getClipData().getItemCount();
                        List<Uri> uris = new ArrayList<>();
                        for (int i = 0; i < count; i++) {
                            uris.add(result.getData().getClipData().getItemAt(i).getUri());
                        }
                        addMediaUris(uris);
                    } else if (result.getData().getData() != null) {
                        List<Uri> uris = new ArrayList<>();
                        uris.add(result.getData().getData());
                        addMediaUris(uris);
                    }
                }
            }
    );

    private final ActivityResultLauncher<Uri> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            result -> {
                if (result && pendingCameraUri != null) {
                    List<Uri> uris = new ArrayList<>();
                    uris.add(pendingCameraUri);
                    addMediaUris(uris);
                }
            }
    );

    private final ActivityResultLauncher<Intent> recordVideoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    Uri videoUri = result.getData().getData();
                    if (videoUri != null) {
                        List<Uri> uris = new ArrayList<>();
                        uris.add(videoUri);
                        addMediaUris(uris);
                    }
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_post, container, false);
        initViews(view);
        setupMediaList();
            setupPostTypes(view);
            setupSkinTypes(view);
            setupListeners(view);
            return view;
        }
    
        private void initViews(View view) {
            edtCaption = view.findViewById(R.id.edtCaption);
            tvCharCounter = view.findViewById(R.id.tvCharCounter);
            btnPost = view.findViewById(R.id.btnPost);
            rvSelectedMedia = view.findViewById(R.id.rvSelectedMedia);
    
            view.findViewById(R.id.btnClose).setOnClickListener(v -> getParentFragmentManager().popBackStack());
            
            TextView tvDraft = view.findViewById(R.id.tvDraft);
            if (tvDraft != null) {
                tvDraft.setOnClickListener(v -> {
                    Toast.makeText(getContext(), "Saved to draft", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                });
            }

            View btnAddMedia = view.findViewById(R.id.btnAddMedia);
            if (btnAddMedia != null) {
                btnAddMedia.setOnClickListener(v -> showMediaSourceBottomSheet());
            }
        }
    
        private void setupPostTypes(View view) {
            ViewGroup layoutPostTypes = view.findViewById(R.id.layoutPostTypes);
            if (layoutPostTypes == null) return;
            layoutPostTypes.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(getContext());
    
            for (int i = 0; i < postTypes.length; i++) {
                final String type = postTypes[i];
                final int icon = postTypeIcons[i];
                View chipView = inflater.inflate(R.layout.item_create_post_type_chip, layoutPostTypes, false);
                
                TextView tvLabel = chipView.findViewById(R.id.tvChipLabel);
                ImageView ivIcon = chipView.findViewById(R.id.ivChipIcon);
                
                tvLabel.setText(type);
                ivIcon.setImageResource(icon);
                
                updatePostTypeChipStyle(chipView, type.equals(selectedPostType));
                
                chipView.setOnClickListener(v -> {
                    selectedPostType = type;
                    setupPostTypes(view);
                });
                
                layoutPostTypes.addView(chipView);
            }
        }
    
        private void updatePostTypeChipStyle(View view, boolean isSelected) {
            TextView tvLabel = view.findViewById(R.id.tvChipLabel);
            ImageView ivIcon = view.findViewById(R.id.ivChipIcon);
            
            if (isSelected) {
                view.setBackgroundResource(R.drawable.bg_create_post_chip_selected);
                tvLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.button));
                ivIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.button));
            } else {
                view.setBackgroundResource(R.drawable.bg_create_post_chip_default);
                tvLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_main));
                ivIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.text_tertiary));
            }
        }
    
        private void setupSkinTypes(View view) {
            ViewGroup layoutSkinTypes = view.findViewById(R.id.layoutSkinTypes);
            if (layoutSkinTypes == null) return;
            layoutSkinTypes.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(getContext());
    
            for (String type : skinTypes) {
                View chipView = inflater.inflate(R.layout.item_skin_type_chip, layoutSkinTypes, false);
                
                TextView tvLabel = chipView.findViewById(R.id.tvChipLabel);
                ImageView ivCheck = chipView.findViewById(R.id.ivCheckIcon);
                
                tvLabel.setText(type);
                
                boolean isSelected = type.equals(selectedSkinType);
                ivCheck.setVisibility(isSelected ? View.VISIBLE : View.GONE);
                
                if (isSelected) {
                    chipView.setBackgroundResource(R.drawable.bg_create_post_chip_selected);
                    tvLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.button));
                } else {
                    chipView.setBackgroundResource(R.drawable.bg_create_post_chip_default);
                    tvLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_main));
                }
                
                chipView.setOnClickListener(v -> {
                    selectedSkinType = type;
                    setupSkinTypes(view);
                });
                
                layoutSkinTypes.addView(chipView);
            }
        }

    private void showMediaSourceBottomSheet() {
        MediaSourceBottomSheet bottomSheet = new MediaSourceBottomSheet();
        bottomSheet.setOnMediaSourceSelectedListener(new MediaSourceBottomSheet.OnMediaSourceSelectedListener() {
            @Override
            public void onTakePhotoSelected() {
                CreatePostFragment.this.onTakePhotoSelected();
            }

            @Override
            public void onChooseGallerySelected() {
                openGallery();
            }

            @Override
            public void onRecordVideoSelected() {
                CreatePostFragment.this.onRecordVideoSelected();
            }
        });
        bottomSheet.show(getChildFragmentManager(), "MediaSourceBottomSheet");
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/* video/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
        galleryLauncher.launch(Intent.createChooser(intent, "Chọn ảnh/video"));
    }

    private void setupMediaList() {
        mediaAdapter = new SelectedMediaAdapter();
        rvSelectedMedia.setAdapter(mediaAdapter);
        mediaAdapter.setOnMediaRemoveListener(uri -> {
            selectedMediaUris.remove(uri);
            mediaAdapter.setMediaUris(new ArrayList<>(selectedMediaUris));
            updatePostButtonState();
        });
        mediaAdapter.setOnAddClickListener(this::showMediaSourceBottomSheet);
    }

    private void setupListeners(View view) {
        edtCaption.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length();
                tvCharCounter.setText(getString(R.string.char_counter_format, length));
                updatePostButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        btnPost.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Posting...", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        });
    }

    public void addMediaUris(List<Uri> uris) {
        for (Uri uri : uris) {
            if (selectedMediaUris.size() < 10 && !selectedMediaUris.contains(uri)) {
                selectedMediaUris.add(uri);
            }
        }
        mediaAdapter.setMediaUris(new ArrayList<>(selectedMediaUris));
        updatePostButtonState();
    }

    private void updatePostButtonState() {
        boolean hasCaption = edtCaption.getText() != null && edtCaption.getText().toString().trim().length() > 0;
        boolean hasMedia = !selectedMediaUris.isEmpty();
        btnPost.setEnabled(hasCaption || hasMedia);
    }

    public void onTakePhotoSelected() {
        isVideoPending = false;
        checkCameraPermissionAndOpen();
    }

    public void onRecordVideoSelected() {
        isVideoPending = true;
        checkCameraPermissionAndOpen();
    }

    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            if (isVideoPending) {
                openCameraForVideo();
            } else {
                openCameraForPhoto();
            }
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCameraForPhoto() {
        try {
            File photoFile = createImageFile();
            pendingCameraUri = FileProvider.getUriForFile(requireContext(),
                    requireContext().getPackageName() + ".fileprovider",
                    photoFile);
            takePictureLauncher.launch(pendingCameraUri);
        } catch (IOException e) {
            Toast.makeText(getContext(), "Error creating file", Toast.LENGTH_SHORT).show();
        }
    }

    private void openCameraForVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
            recordVideoLauncher.launch(intent);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }
}
