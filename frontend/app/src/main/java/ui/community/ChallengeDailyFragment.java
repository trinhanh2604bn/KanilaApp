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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.google.android.material.button.MaterialButton;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChallengeDailyFragment extends Fragment {

    private static final String ARG_CHALLENGE_ID = "challenge_id";
    private static final String ARG_TASK_ID = "task_id";
    private static final String ARG_TASK_INDEX = "task_index";

    private String challengeId;
    private String taskId;
    private int taskIndex;
    
    private ChallengeViewModel viewModel;
    private com.example.frontend.feature.account.AccountViewModel accountViewModel;
    private Challenge challenge;

    private TextView tvHeaderTitle, tvDayBadge, tvChallengeTitle, tvChallengeDesc, tvChallengeDate, tvChallengePoints;
    private ImageView ivChallengeIcon;
    private EditText edtContent;
    private TextView tvCharCounter, tvMediaCounter, tvProductCounter;
    private RecyclerView rvSelectedMedia, rvSelectedProducts;
    private MaterialButton btnSubmit;

    private ChallengeSelectedMediaAdapter mediaAdapter;
    private ChallengeSelectedProductAdapter productAdapter;
    private final List<Uri> selectedMediaUris = new ArrayList<>();
    private final List<com.example.frontend.model.Product> selectedProducts = new ArrayList<>();

    private Uri pendingCameraUri;
    private boolean isVideoPending = false;

    private ActivityResultLauncher<Uri> takePictureLauncher;
    private ActivityResultLauncher<Intent> recordVideoLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;

    public static ChallengeDailyFragment newInstance(String challengeId, String taskId, int taskIndex) {
        ChallengeDailyFragment fragment = new ChallengeDailyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHALLENGE_ID, challengeId);
        args.putString(ARG_TASK_ID, taskId);
        args.putInt(ARG_TASK_INDEX, taskIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            challengeId = getArguments().getString(ARG_CHALLENGE_ID);
            taskId = getArguments().getString(ARG_TASK_ID);
            taskIndex = getArguments().getInt(ARG_TASK_INDEX);
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
        View view = inflater.inflate(R.layout.fragment_challenge_daily, container, false);
        initViews(view);
        setupViewModel();
        setupMediaList();
        setupProductList();
        return view;
    }

    private void initViews(View view) {
        tvHeaderTitle = view.findViewById(R.id.tvHeaderTitle);
        ivChallengeIcon = view.findViewById(R.id.ivChallengeIcon);
        tvDayBadge = view.findViewById(R.id.tvDayBadge);
        tvChallengeTitle = view.findViewById(R.id.tvChallengeTitle);
        tvChallengeDesc = view.findViewById(R.id.tvChallengeDesc);
        tvChallengeDate = view.findViewById(R.id.tvChallengeDate);
        tvChallengePoints = view.findViewById(R.id.tvChallengePoints);

        edtContent = view.findViewById(R.id.edtContent);
        tvCharCounter = view.findViewById(R.id.tvCharCounter);
        
        rvSelectedMedia = view.findViewById(R.id.rvSelectedMedia);
        tvMediaCounter = view.findViewById(R.id.tvMediaCounter);
        
        rvSelectedProducts = view.findViewById(R.id.rvSelectedProducts);
        tvProductCounter = view.findViewById(R.id.tvProductCounter);
        
        btnSubmit = view.findViewById(R.id.btnSubmit);

        view.findViewById(R.id.btnBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());

        edtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCharCounter.setText(getString(R.string.challenge_char_counter_format, s.length()));
                updateSubmitButtonState();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSubmit.setOnClickListener(v -> submitProgress());
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(ChallengeViewModel.class);
        accountViewModel = new ViewModelProvider(requireActivity()).get(com.example.frontend.feature.account.AccountViewModel.class);
        challenge = viewModel.getChallengeById(challengeId);
        if (challenge != null) {
            displayChallengeInfo();
        }
    }

    private void displayChallengeInfo() {
        String[] vietnameseNumbers = {"nhất", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín", "mười", "mười một", "mười hai", "mười ba", "mười bốn"};
        String dayText = (taskIndex < vietnameseNumbers.length) ? vietnameseNumbers[taskIndex] : String.valueOf(taskIndex + 1);
        tvHeaderTitle.setText(getString(R.string.challenge_daily_title_format, dayText));
        
        tvDayBadge.setText(getString(R.string.challenge_day_format, taskIndex + 1, challenge.getDurationDays()).toUpperCase());
        tvChallengeTitle.setText(challenge.getTitle());
        tvChallengeDesc.setText(challenge.getDescription());
        tvChallengePoints.setText(getString(R.string.challenge_reward_points, String.valueOf(challenge.getRewardPoints())));
        
        if (challenge.getImageResId() != 0) {
            ivChallengeIcon.setImageResource(challenge.getImageResId());
        } else if (challenge.getBannerUrl() != null) {
            Glide.with(this).load(challenge.getBannerUrl()).placeholder(R.drawable.img_challenge1).into(ivChallengeIcon);
        }

        // Mock date
        tvChallengeDate.setText(R.string.challenge_sample_date);
    }

    private void setupMediaList() {
        mediaAdapter = new ChallengeSelectedMediaAdapter(new ChallengeSelectedMediaAdapter.OnMediaActionListener() {
            @Override
            public void onAddClick() {
                showMediaSourceBottomSheet();
            }

            @Override
            public void onRemoveClick(Uri uri) {
                selectedMediaUris.remove(uri);
                mediaAdapter.setMediaUris(new ArrayList<>(selectedMediaUris));
                updateMediaCounter();
                updateSubmitButtonState();
            }
        });
        rvSelectedMedia.setAdapter(mediaAdapter);
        updateMediaCounter();
    }

    private void updateMediaCounter() {
        tvMediaCounter.setText(getString(R.string.challenge_media_count_format, selectedMediaUris.size()));
    }

    private void setupProductList() {
        productAdapter = new ChallengeSelectedProductAdapter(new ChallengeSelectedProductAdapter.OnProductActionListener() {
            @Override
            public void onAddClick() {
                showProductSearchBottomSheet();
            }

            @Override
            public void onRemoveClick(com.example.frontend.model.Product product) {
                selectedProducts.remove(product);
                productAdapter.setProducts(new ArrayList<>(selectedProducts));
                updateProductListUI();
            }
        });
        rvSelectedProducts.setAdapter(productAdapter);
        updateProductListUI();
    }

    private void showProductSearchBottomSheet() {
        ProductSearchBottomSheet bottomSheet = ProductSearchBottomSheet.newInstance(product -> {
            if (selectedProducts.size() < 5) {
                selectedProducts.add(product);
                productAdapter.setProducts(new ArrayList<>(selectedProducts));
                updateProductListUI();
            } else {
                Toast.makeText(getContext(), "Bạn chỉ có thể chọn tối đa 5 sản phẩm.", Toast.LENGTH_SHORT).show();
            }
        });
        bottomSheet.show(getChildFragmentManager(), "ProductSearchBottomSheet");
    }

    private void updateProductListUI() {
        tvProductCounter.setText(getString(R.string.challenge_product_count_format, selectedProducts.size()));
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
        if (selectedMediaUris.size() >= 6) {
            Toast.makeText(getContext(), "Bạn chỉ có thể chọn tối đa 6 ảnh/video.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!selectedMediaUris.contains(uri)) {
            selectedMediaUris.add(uri);
            mediaAdapter.setMediaUris(new ArrayList<>(selectedMediaUris));
            updateMediaCounter();
            updateSubmitButtonState();
        }
    }

    private void updateSubmitButtonState() {
        String content = edtContent.getText().toString().trim();
        btnSubmit.setEnabled(!content.isEmpty() || !selectedMediaUris.isEmpty());
    }

    private void submitProgress() {
        String content = edtContent.getText().toString().trim();
        List<String> imageUrls = new ArrayList<>();
        for (Uri uri : selectedMediaUris) {
            imageUrls.add(uri.toString());
        }

        // Get user info
        String userName = "Bạn";
        String userAvatar = null;
        com.example.frontend.data.remote.NetworkResult<com.example.frontend.data.model.account.ProfileHubDto> userResult = accountViewModel.getProfileHubResult().getValue();
        if (userResult != null && userResult.status == com.example.frontend.data.remote.NetworkResult.Status.SUCCESS && userResult.data != null) {
            if (userResult.data.getProfile() != null) {
                userName = userResult.data.getProfile().getFullName();
                userAvatar = userResult.data.getProfile().getAvatarUrl();
            }
        }

        Post progressPost = new Post(
                "post_" + System.currentTimeMillis(),
                userName,
                userAvatar,
                "Vừa xong",
                "Challenge Ngày " + (taskIndex + 1),
                content,
                imageUrls,
                0, 0, 0,
                true, false
        );
        progressPost.setProducts(new ArrayList<>(selectedProducts));

        // Complete the task in ViewModel/Repo and save post
        viewModel.completeTask(challengeId, taskId, progressPost);
        
        Toast.makeText(getContext(), "Đã đăng tiến trình thành công!", Toast.LENGTH_SHORT).show();
        
        // Return to Challenge Active screen
        getParentFragmentManager().popBackStack();
    }

    private void showRules() {
        if (challenge != null) {
            Toast.makeText(getContext(), "Quy tắc: " + challenge.getRules(), Toast.LENGTH_LONG).show();
        }
    }
}
