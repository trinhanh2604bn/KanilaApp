package ui.order;

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
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.example.frontend.data.model.review.ReviewEligibilityDto;
import com.example.frontend.data.remote.NetworkResult;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import ui.community.MediaSourceBottomSheet;
import ui.community.SelectedMediaAdapter;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewWriteFragment extends Fragment {

    private static final String ARG_ORDER_ITEM_ID = "order_item_id";

    private String orderItemId;
    private ReviewWriteViewModel viewModel;

    private View layoutLoading, layoutProductPreview;
    private RatingBar rbRating;
    private TextView tvRatingLabel, tvCharCounter;
    private ChipGroup cgReviewTags, cgSkinTypes;
    private EditText edtReviewContent;
    private RecyclerView rvSelectedMedia;
    private View btnSubmit;

    private SelectedMediaAdapter mediaAdapter;
    private final List<Uri> selectedMediaUris = new ArrayList<>();
    private Uri pendingCameraUri;
    private boolean isVideoPending = false;

    private final String[] reviewTags = {"Thấm nhanh", "Dịu da", "Mờ thâm", "Đóng gói đẹp", "Mùi dễ chịu"};
    private final String[] skinTypes = {"Da dầu", "Da khô", "Da hỗn hợp", "Da nhạy cảm", "Da thường"};
    private final String[] ratingLabels = {"Rất tệ", "Không hài lòng", "Bình thường", "Hài lòng", "Tuyệt vời!"};

    public static ReviewWriteFragment newInstance(String orderItemId) {
        ReviewWriteFragment fragment = new ReviewWriteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ORDER_ITEM_ID, orderItemId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderItemId = getArguments().getString(ARG_ORDER_ITEM_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_review_write, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ReviewWriteViewModel.class);

        initViews(view);
        setupHeader(view);
        setupRating();
        setupChips();
        setupMediaList();
        setupListeners();
        observeViewModel();

        viewModel.loadEligibility(orderItemId);
    }

    private void initViews(View view) {
        layoutLoading = view.findViewById(R.id.layoutLoading);
        layoutProductPreview = view.findViewById(R.id.layoutProductPreview);
        rbRating = view.findViewById(R.id.rbRating);
        tvRatingLabel = view.findViewById(R.id.tvRatingLabel);
        tvCharCounter = view.findViewById(R.id.tvCharCounter);
        cgReviewTags = view.findViewById(R.id.cgReviewTags);
        cgSkinTypes = view.findViewById(R.id.cgSkinTypes);
        edtReviewContent = view.findViewById(R.id.edtReviewContent);
        rvSelectedMedia = view.findViewById(R.id.rvSelectedMedia);
        btnSubmit = view.findViewById(R.id.btnSubmit);
    }

    private void setupHeader(View view) {
        View header = view.findViewById(R.id.layoutHeader);
        if (header != null) {
            TextView tvTitle = header.findViewById(R.id.tvTopBarTitle);
            if (tvTitle != null) tvTitle.setText(R.string.review_write_title);

            header.findViewById(R.id.btnTopBarBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }
    }

    private void setupRating() {
        rbRating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            int index = (int) rating - 1;
            if (index >= 0 && index < ratingLabels.length) {
                tvRatingLabel.setText(ratingLabels[index]);
                tvRatingLabel.setVisibility(View.VISIBLE);
            } else {
                tvRatingLabel.setVisibility(View.INVISIBLE);
            }
        });
        tvRatingLabel.setVisibility(View.INVISIBLE);
    }

    private void setupChips() {
        for (String tag : reviewTags) {
            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.item_review_chip, cgReviewTags, false);
            chip.setText(tag);
            cgReviewTags.addView(chip);
        }

        for (String type : skinTypes) {
            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.item_review_chip, cgSkinTypes, false);
            chip.setText(type);
            cgSkinTypes.addView(chip);
        }
    }

    private void setupMediaList() {
        mediaAdapter = new SelectedMediaAdapter();
        rvSelectedMedia.setAdapter(mediaAdapter);
        mediaAdapter.setOnMediaRemoveListener(uri -> {
            selectedMediaUris.remove(uri);
            mediaAdapter.setMediaUris(new ArrayList<>(selectedMediaUris));
        });
        mediaAdapter.setOnAddClickListener(this::showMediaSourceBottomSheet);
    }

    private void setupListeners() {
        edtReviewContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCharCounter.setText(getString(R.string.review_write_char_counter_format, s.length()));
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSubmit.setOnClickListener(v -> {
            int rating = (int) rbRating.getRating();
            if (rating == 0) {
                Toast.makeText(getContext(), R.string.review_write_rating_required, Toast.LENGTH_SHORT).show();
                return;
            }

            String content = edtReviewContent.getText().toString().trim();
            List<String> tags = new ArrayList<>();
            for (int i = 0; i < cgReviewTags.getChildCount(); i++) {
                Chip chip = (Chip) cgReviewTags.getChildAt(i);
                if (chip.isChecked()) tags.add(chip.getText().toString());
            }

            List<String> selectedSkinTypes = new ArrayList<>();
            for (int i = 0; i < cgSkinTypes.getChildCount(); i++) {
                Chip chip = (Chip) cgSkinTypes.getChildAt(i);
                if (chip.isChecked()) selectedSkinTypes.add(chip.getText().toString());
            }

            List<String> mediaUrls = new ArrayList<>();
            for (Uri uri : selectedMediaUris) mediaUrls.add(uri.toString());

            viewModel.submitReview(orderItemId, rating, "", content, tags, selectedSkinTypes, mediaUrls);
        });
    }

    private void observeViewModel() {
        viewModel.getEligibility().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            layoutLoading.setVisibility(result.status == NetworkResult.Status.LOADING ? View.VISIBLE : View.GONE);
            if (result.status == NetworkResult.Status.SUCCESS && result.data != null) {
                bindProductPreview(result.data.getPreview());
            } else if (result.status == NetworkResult.Status.ERROR) {
                Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();
            }
        });

        viewModel.getSubmitResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            layoutLoading.setVisibility(result.status == NetworkResult.Status.LOADING ? View.VISIBLE : View.GONE);
            if (result.status == NetworkResult.Status.SUCCESS) {
                Toast.makeText(getContext(), R.string.review_write_success, Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();
            } else if (result.status == NetworkResult.Status.ERROR) {
                Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindProductPreview(ReviewEligibilityDto.ReviewPreview preview) {
        if (preview == null) return;
        ImageView ivImage = layoutProductPreview.findViewById(R.id.ivSelectedCartProductImage);
        TextView tvName = layoutProductPreview.findViewById(R.id.tvSelectedCartProductName);
        TextView tvVariant = layoutProductPreview.findViewById(R.id.tvSelectedCartVariant);
        TextView tvQuantity = layoutProductPreview.findViewById(R.id.tvSelectedCartQuantity);
        TextView tvPrice = layoutProductPreview.findViewById(R.id.tvSelectedCartPrice);

        tvName.setText(preview.getProductName());
        tvVariant.setText(preview.getVariantLabel());
        tvQuantity.setVisibility(View.GONE);
        tvPrice.setVisibility(View.GONE);

        Glide.with(this)
                .load(preview.getProductImageUrl())
                .placeholder(R.drawable.ic_product)
                .error(R.drawable.ic_product)
                .centerCrop()
                .into(ivImage);
    }

    private void showMediaSourceBottomSheet() {
        MediaSourceBottomSheet bottomSheet = new MediaSourceBottomSheet();
        bottomSheet.setOnMediaSourceSelectedListener(new MediaSourceBottomSheet.OnMediaSourceSelectedListener() {
            @Override
            public void onTakePhotoSelected() {
                checkCameraPermissionAndOpen(false);
            }
            @Override
            public void onChooseGallerySelected() {
                openGallery();
            }
            @Override
            public void onRecordVideoSelected() {
                checkCameraPermissionAndOpen(true);
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

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    if (result.getData().getClipData() != null) {
                        int count = result.getData().getClipData().getItemCount();
                        for (int i = 0; i < count; i++) {
                            addMediaUri(result.getData().getClipData().getItemAt(i).getUri());
                        }
                    } else if (result.getData().getData() != null) {
                        addMediaUri(result.getData().getData());
                    }
                }
            }
    );

    private final ActivityResultLauncher<String> cameraPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    if (isVideoPending) openCameraForVideo();
                    else openCameraForPhoto();
                } else {
                    Toast.makeText(getContext(), "Camera permission is required", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private final ActivityResultLauncher<Uri> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            result -> {
                if (result && pendingCameraUri != null) {
                    addMediaUri(pendingCameraUri);
                }
            }
    );

    private final ActivityResultLauncher<Intent> recordVideoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    Uri videoUri = result.getData().getData();
                    if (videoUri != null) addMediaUri(videoUri);
                }
            }
    );

    private void addMediaUri(Uri uri) {
        if (selectedMediaUris.size() < 5 && !selectedMediaUris.contains(uri)) {
            selectedMediaUris.add(uri);
            mediaAdapter.setMediaUris(new ArrayList<>(selectedMediaUris));
        }
    }

    private void checkCameraPermissionAndOpen(boolean isVideo) {
        this.isVideoPending = isVideo;
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            if (isVideo) openCameraForVideo();
            else openCameraForPhoto();
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
        recordVideoLauncher.launch(intent);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }
}
