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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChallengeProgressPostFragment extends Fragment {

    private static final String ARG_CHALLENGE_ID = "challenge_id";
    private static final int MAX_MEDIA_COUNT = 10;
    
    private String challengeId;
    private ChallengeViewModel viewModel;
    private Challenge challenge;

    private TextView tvChallengeName, tvProgressDay, tvCharCounter;
    private EditText edtCaption;
    private RecyclerView rvSelectedMedia;
    private SelectedMediaAdapter mediaAdapter;
    private final List<Uri> selectedMediaUris = new ArrayList<>();
    private MaterialButton btnSubmit;
    private RecyclerView rvTasks, rvProductsUsed;
    private ChallengeTaskAdapter taskAdapter;
    private ProductThumbnailAdapter productsAdapter;

    private Uri pendingCameraUri;
    private boolean isVideoPending = false;

    private ActivityResultLauncher<Uri> takePictureLauncher;
    private ActivityResultLauncher<Intent> recordVideoLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;

    public static ChallengeProgressPostFragment newInstance(String challengeId) {
        ChallengeProgressPostFragment fragment = new ChallengeProgressPostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHALLENGE_ID, challengeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            challengeId = getArguments().getString(ARG_CHALLENGE_ID);
        }
        setupActivityResultLaunchers();
    }

    private void setupActivityResultLaunchers() {
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                result -> {
                    if (result && pendingCameraUri != null) {
                        addSelectedMedia(pendingCameraUri);
                    }
                }
        );

        recordVideoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                        Uri videoUri = result.getData().getData();
                        if (videoUri != null) {
                            addSelectedMedia(videoUri);
                        }
                    }
                }
        );

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                        if (result.getData().getClipData() != null) {
                            int count = result.getData().getClipData().getItemCount();
                            for (int i = 0; i < count; i++) {
                                addSelectedMedia(result.getData().getClipData().getItemAt(i).getUri());
                            }
                        } else if (result.getData().getData() != null) {
                            addSelectedMedia(result.getData().getData());
                        }
                    }
                }
        );

        cameraPermissionLauncher = registerForActivityResult(
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
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenge_progress_post, container, false);
        initViews(view);
        setupViewModel();
        setupMediaList();
        return view;
    }

    private void initViews(View view) {
        tvChallengeName = view.findViewById(R.id.tvChallengeName);
        tvProgressDay = view.findViewById(R.id.tvProgressDay);
        tvCharCounter = view.findViewById(R.id.tvCharCounter);
        edtCaption = view.findViewById(R.id.edtCaption);
        rvSelectedMedia = view.findViewById(R.id.rvSelectedMedia);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        rvTasks = view.findViewById(R.id.rvTasks);
        rvProductsUsed = view.findViewById(R.id.rvProductsUsed);

        view.findViewById(R.id.btnBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());


        view.findViewById(R.id.btnAddMedia).setOnClickListener(v -> showMediaSourceBottomSheet());

        edtCaption.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCharCounter.setText(getString(R.string.char_counter_format, s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSubmit.setOnClickListener(v -> {
            // Update progress in repo
            challenge.setCurrentProgress(challenge.getCurrentProgress() + 1);
            Toast.makeText(getContext(), "Đã đăng tiến trình thành công!", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        });
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(ChallengeViewModel.class);
        challenge = viewModel.getChallengeById(challengeId);
        if (challenge != null) {
            tvChallengeName.setText(challenge.getTitle());
            tvProgressDay.setText(getString(R.string.challenge_day_format, challenge.getCurrentProgress() + 1, challenge.getDurationDays()));
            
            if (challenge.getTasks() != null) {

                taskAdapter = new ChallengeTaskAdapter(true);
                rvTasks.setAdapter(taskAdapter);
                taskAdapter.setTasks(challenge.getTasks());
            }

            productsAdapter = new ProductThumbnailAdapter();
            rvProductsUsed.setAdapter(productsAdapter);
            List<String> mockProducts = new ArrayList<>();
            mockProducts.add("https://example.com/p1.jpg");
            mockProducts.add("https://example.com/p2.jpg");
            productsAdapter.setImageUrls(mockProducts);
        }


    }

    private void setupMediaList() {
        mediaAdapter = new SelectedMediaAdapter();
        rvSelectedMedia.setAdapter(mediaAdapter);
        mediaAdapter.setOnMediaRemoveListener(uri -> {
            selectedMediaUris.remove(uri);
            mediaAdapter.setMediaUris(new ArrayList<>(selectedMediaUris));
        });
    }

    private void showMediaSourceBottomSheet() {
        MediaSourceBottomSheet bottomSheet = new MediaSourceBottomSheet();
        bottomSheet.setOnMediaSourceSelectedListener(new MediaSourceBottomSheet.OnMediaSourceSelectedListener() {
            @Override
            public void onTakePhotoSelected() {
                isVideoPending = false;
                checkCameraPermissionAndOpen();
            }

            @Override
            public void onChooseGallerySelected() {
                openGallery();
            }

            @Override
            public void onRecordVideoSelected() {
                isVideoPending = true;
                checkCameraPermissionAndOpen();
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

    private void addSelectedMedia(Uri uri) {
        if (uri == null) return;
        if (selectedMediaUris.size() >= MAX_MEDIA_COUNT) {
            Toast.makeText(getContext(), R.string.max_media_reached, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!selectedMediaUris.contains(uri)) {
            selectedMediaUris.add(uri);
            mediaAdapter.setMediaUris(new ArrayList<>(selectedMediaUris));
        }
    }
}
