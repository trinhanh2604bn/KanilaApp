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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.frontend.R;
import ui.community.Post;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreatePostFragment extends Fragment {

    private static final String ARG_EDIT_POST_ID = "edit_post_id";
    private String editPostId;

    public static CreatePostFragment newInstance() {
        return new CreatePostFragment();
    }

    public static CreatePostFragment newEditInstance(String postId) {
        CreatePostFragment fragment = new CreatePostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EDIT_POST_ID, postId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            editPostId = getArguments().getString(ARG_EDIT_POST_ID);
        }
    }

    private CommunityViewModel communityViewModel;
    private com.example.frontend.feature.account.AccountViewModel accountViewModel;
    private EditText edtCaption;
    private TextView tvCharCounter;
    private Button btnPost;
    private RecyclerView rvSelectedMedia;
    private SelectedMediaAdapter mediaAdapter;
    private final List<Uri> selectedMediaUris = new ArrayList<>();

    private SelectedProductAdapter productAdapter;
    private ViewGroup layoutProductsUsed;

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
        
        // 1. Initialize Product Adapter first (no view needed)
        setupProductList();
        
        // 2. Initialize Views (initializes rvSelectedMedia, layoutProductsUsed, etc.)
        initViews(view);
        
        // 3. Setup Media List (requires rvSelectedMedia to be initialized)
        setupMediaList();
        
        // Initial sync of dynamic layouts
        if (editPostId != null) {
            mediaAdapter.setMediaUris(new ArrayList<>(selectedMediaUris));
        }
        updateLayoutProductsUsed();

        setupPostTypes(view);
        setupSkinTypes(view);
        setupListeners(view);
        updatePostButtonState(); // Initial state check
        return view;
    }

    private void setupProductList() {
        productAdapter = new SelectedProductAdapter(new SelectedProductAdapter.OnProductActionListener() {
            @Override
            public void onProductClick(com.example.frontend.model.Product product) {
                // Navigate to ProductDetailFragment
                com.example.frontend.feature.product.ProductDetailFragment fragment =
                        com.example.frontend.feature.product.ProductDetailFragment.newInstance(product.getId());
                
                if (getActivity() instanceof com.example.frontend.MainActivity) {
                    ((com.example.frontend.MainActivity) getActivity()).loadFragment(fragment);
                } else {
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.main_fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            }

            @Override
            public void onProductRemove(com.example.frontend.model.Product product) {
                productAdapter.removeProduct(product);
                updateLayoutProductsUsed();
            }
        });
        updateLayoutProductsUsed();
    }

    private void updateLayoutProductsUsed() {
        if (layoutProductsUsed == null) return;
        layoutProductsUsed.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        List<com.example.frontend.model.Product> selectedProducts = productAdapter.getSelectedProducts();
        for (com.example.frontend.model.Product product : selectedProducts) {
            View productView = inflater.inflate(R.layout.item_create_post_product_used, layoutProductsUsed, false);
            SelectedProductAdapter.ViewHolder holder = productAdapter.new ViewHolder(
                    com.example.frontend.databinding.ItemCreatePostProductUsedBinding.bind(productView));
            holder.bind(product);
            layoutProductsUsed.addView(productView);
        }

        // Add "Add Product" placeholder
        View placeholder = inflater.inflate(R.layout.item_add_product_placeholder, layoutProductsUsed, false);
        placeholder.setOnClickListener(v -> showProductSearchBottomSheet());
        layoutProductsUsed.addView(placeholder);
    }

    private void showProductSearchBottomSheet() {
        ProductSearchBottomSheet bottomSheet = ProductSearchBottomSheet.newInstance(product -> {
            productAdapter.addProduct(product);
            updateLayoutProductsUsed();
        });
        bottomSheet.show(getChildFragmentManager(), "ProductSearchBottomSheet");
    }
    
        private void initViews(View view) {
        communityViewModel = new ViewModelProvider(requireActivity()).get(CommunityViewModel.class);
        accountViewModel = new ViewModelProvider(requireActivity()).get(com.example.frontend.feature.account.AccountViewModel.class);

        edtCaption = view.findViewById(R.id.edtCaption);
        tvCharCounter = view.findViewById(R.id.tvCharCounter);
        btnPost = view.findViewById(R.id.btnPost);
        rvSelectedMedia = view.findViewById(R.id.rvSelectedMedia);
        layoutProductsUsed = view.findViewById(R.id.layoutProductsUsed);

        TextView tvHeaderTitle = view.findViewById(R.id.tvHeaderTitle);

        // Load User Info from AccountViewModel
        TextView tvUserName = view.findViewById(R.id.tvUserName);
        ImageView ivUserAvatar = view.findViewById(R.id.ivUserAvatar);
        accountViewModel.getProfileHubResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.status == com.example.frontend.data.remote.NetworkResult.Status.SUCCESS && result.data != null) {
                if (result.data.getProfile() != null) {
                    if (tvUserName != null) tvUserName.setText(result.data.getProfile().getFullName());
                    if (ivUserAvatar != null) {
                        Glide.with(this)
                                .load(result.data.getProfile().getAvatarUrl())
                                .placeholder(R.drawable.ic_account)
                                .error(R.drawable.ic_account)
                                .into(ivUserAvatar);
                    }
                }
            }
        });
        accountViewModel.loadProfileHub();

        if (editPostId != null) {
            Post post = communityViewModel.getPostById(editPostId);
            if (post != null) {
                edtCaption.setText(post.getContent());
                
                // Load existing type and skin type
                if (post.getPostType() != null) selectedPostType = post.getPostType();
                else selectedPostType = post.getTitle(); // Fallback
                
                if (post.getSkinType() != null) selectedSkinType = post.getSkinType();

                if (tvHeaderTitle != null) tvHeaderTitle.setText("Chỉnh sửa bài viết");
                btnPost.setText("Lưu");

                // Load existing images
                if (post.getImages() != null) {
                    for (String s : post.getImages()) {
                        selectedMediaUris.add(Uri.parse(s));
                    }
                }
                
                // Load existing products
                if (post.getProducts() != null) {
                    productAdapter.setProducts(new ArrayList<>(post.getProducts()));
                }
            }
        }
    
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
            String caption = edtCaption.getText().toString().trim();
            
            if (editPostId != null) {
                Post existing = communityViewModel.getPostById(editPostId);
                if (existing != null) {
                    List<String> images = new ArrayList<>();
                    for (Uri uri : selectedMediaUris) images.add(uri.toString());
                    
                    Post updated = new Post(
                            existing.getId(),
                            existing.getUserName(),
                            existing.getUserAvatar(),
                            existing.getTime(),
                            selectedPostType,
                            caption,
                            images,
                            existing.getLikeCount(),
                            existing.getCommentCount(),
                            existing.getShareCount(),
                            existing.isVerified(),
                            existing.isPurchased()
                    );
                    updated.setSaved(existing.isSaved());
                    updated.setLiked(existing.isLiked());
                    updated.setSkinType(selectedSkinType);
                    updated.setProducts(new ArrayList<>(productAdapter.getSelectedProducts()));
                    updated.setComments(existing.getComments());
                    
                    communityViewModel.updatePost(updated);
                    Toast.makeText(getContext(), "Đã cập nhật bài viết!", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                    return;
                }
            }

            String userName = "Người dùng Kanila";
            String userAvatar = null;

            // Get real user info
            com.example.frontend.data.remote.NetworkResult<com.example.frontend.data.model.account.ProfileHubDto> userResult = accountViewModel.getProfileHubResult().getValue();
            if (userResult != null && userResult.status == com.example.frontend.data.remote.NetworkResult.Status.SUCCESS && userResult.data != null) {
                if (userResult.data.getProfile() != null) {
                    userName = userResult.data.getProfile().getFullName();
                    userAvatar = userResult.data.getProfile().getAvatarUrl();
                }
            }

            List<String> images = new ArrayList<>();
            for (Uri uri : selectedMediaUris) {
                images.add(uri.toString());
            }

            Post newPost = new Post(
                    String.valueOf(System.currentTimeMillis()),
                    userName,
                    userAvatar,
                    "Vừa xong",
                    selectedPostType,
                    caption,
                    images,
                    0, 0, 0, false, false
            );
            newPost.setSkinType(selectedSkinType);
            newPost.setProducts(new ArrayList<>(productAdapter.getSelectedProducts()));

            communityViewModel.addPost(newPost);
            Toast.makeText(getContext(), "Đã đăng bài viết!", Toast.LENGTH_SHORT).show();
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
