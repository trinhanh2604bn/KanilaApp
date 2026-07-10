package ui.account;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.example.frontend.data.model.account.ProfileHubDto;
import com.example.frontend.feature.account.AccountViewModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProfileOverviewFragment extends Fragment {

    private AccountViewModel viewModel;
    private ImageView ivAvatarLarge;
    private TextView tvNameValue, tvEmailValue, tvPhoneValue, tvBirthValue, tvGenderValue, btnSaveProfile;
    
    private String currentFullName;
    private String currentPhone;
    private String currentBirthday;
    private String currentGender;

    private Uri pendingCameraUri;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private ActivityResultLauncher<String> getContentLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActivityResultLaunchers();
    }

    private void setupActivityResultLaunchers() {
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                result -> {
                    if (result && pendingCameraUri != null) {
                        updateLocalAvatar(pendingCameraUri);
                    }
                }
        );

        getContentLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        updateLocalAvatar(uri);
                    }
                }
        );

        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openCameraForPhoto();
                    } else {
                        Toast.makeText(getContext(), "Cần quyền camera để chụp ảnh", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void updateLocalAvatar(Uri uri) {
        Glide.with(this)
                .load(uri)
                .circleCrop()
                .into(ivAvatarLarge);
        // TODO: Upload to server and get URL if needed
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_profile_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);
        
        initViews(view);
        observeViewModel();
    }

    private void initViews(View view) {
        ivAvatarLarge = view.findViewById(R.id.ivAvatarLarge);
        tvNameValue = view.findViewById(R.id.tvNameValue);
        tvEmailValue = view.findViewById(R.id.tvEmailValue);
        tvPhoneValue = view.findViewById(R.id.tvPhoneValue);
        tvBirthValue = view.findViewById(R.id.tvBirthValue);
        tvGenderValue = view.findViewById(R.id.tvGenderValue);
        btnSaveProfile = view.findViewById(R.id.btnSaveProfile);

        view.findViewById(R.id.btnBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        
        // In a real app, these would open edit dialogs or bottom sheets.
        // For this task, we will simulate the "filling in" part if they are empty.
        
        tvNameValue.setOnClickListener(v -> showEditDialog("Họ tên", tvNameValue.getText().toString(), value -> {
            if (!value.isEmpty()) {
                tvNameValue.setText(value);
                currentFullName = value;
            }
        }));

        tvPhoneValue.setOnClickListener(v -> showEditDialog("Số điện thoại", tvPhoneValue.getText().toString(), value -> {
            if (!value.isEmpty()) {
                tvPhoneValue.setText(value);
                currentPhone = value;
            }
        }));

        tvBirthValue.setOnClickListener(v -> showEditDialog("Ngày sinh (DD/MM/YYYY)", tvBirthValue.getText().toString(), value -> {
            tvBirthValue.setText(value);
            currentBirthday = value;
        }));

        tvGenderValue.setOnClickListener(v -> {
            String[] genders = {"Nam", "Nữ", "Khác"};
            new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Chọn giới tính")
                .setItems(genders, (dialog, which) -> {
                    tvGenderValue.setText(genders[which]);
                    currentGender = genders[which];
                })
                .show();
        });

        view.findViewById(R.id.btnChangeAvatar).setOnClickListener(v -> showAvatarBottomSheet());

        btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private void showAvatarBottomSheet() {
        AvatarBottomSheet bottomSheet = new AvatarBottomSheet();
        bottomSheet.setShowRemoveOption(false); // Only Take Photo and Choose Gallery
        bottomSheet.setOnAvatarSourceSelectedListener(new AvatarBottomSheet.OnAvatarSourceSelectedListener() {
            @Override
            public void onTakePhotoSelected() {
                checkCameraPermissionAndOpen();
            }

            @Override
            public void onChooseGallerySelected() {
                openGalleryForImage();
            }
        });
        bottomSheet.show(getChildFragmentManager(), "AvatarBottomSheet");
    }

    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCameraForPhoto();
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

    private void openGalleryForImage() {
        getContentLauncher.launch("image/*");
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void showEditDialog(String title, String currentValue, OnValueEnteredListener listener) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Chỉnh sửa " + title);
        
        final android.widget.EditText input = new android.widget.EditText(requireContext());
        if (!"Chưa cập nhật".equals(currentValue)) {
            input.setText(currentValue);
        }
        builder.setView(input);
        
        builder.setPositiveButton("OK", (dialog, which) -> {
            listener.onValueEntered(input.getText().toString());
        });
        builder.setNegativeButton("Hủy", (dialog, id) -> dialog.cancel());
        
        builder.show();
    }

    private void observeViewModel() {
        viewModel.getProfileHubResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.status == com.example.frontend.data.remote.NetworkResult.Status.SUCCESS) {
                bindData(result.data);
            }
        });

        viewModel.getUpdateProfileResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.status) {
                case LOADING:
                    btnSaveProfile.setEnabled(false);
                    btnSaveProfile.setText("Đang lưu...");
                    break;
                case SUCCESS:
                    Toast.makeText(getContext(), "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show();
                    viewModel.loadProfileHub(); // Sync back to AccountFragment
                    viewModel.resetUpdateProfileResult();
                    getParentFragmentManager().popBackStack();
                    break;
                case ERROR:
                    btnSaveProfile.setEnabled(true);
                    btnSaveProfile.setText("Lưu thay đổi");
                    Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void bindData(ProfileHubDto data) {
        if (data == null || data.getProfile() == null) return;
        
        ProfileHubDto.AccountInfo profile = data.getProfile();
        tvNameValue.setText(profile.getFullName() != null ? profile.getFullName() : "Chưa cập nhật");
        tvEmailValue.setText(profile.getEmail());
        tvPhoneValue.setText(profile.getPhone() != null ? profile.getPhone() : "Chưa cập nhật");
        tvBirthValue.setText(profile.getBirthday() != null ? profile.getBirthday() : "Chưa cập nhật");
        tvGenderValue.setText(profile.getGender() != null ? profile.getGender() : "Chưa cập nhật");
        
        currentFullName = profile.getFullName();
        currentPhone = profile.getPhone();
        currentBirthday = profile.getBirthday();
        currentGender = profile.getGender();
        
        Glide.with(this)
                .load(profile.getAvatarUrl())
                .placeholder(R.drawable.ic_account)
                .into(ivAvatarLarge);
    }

    private void saveProfile() {
        if (currentFullName == null || currentFullName.trim().isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập họ tên", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("fullName", currentFullName);
        data.put("full_name", currentFullName); // Send both to be safe
        if (currentPhone != null) data.put("phone", currentPhone);
        if (currentBirthday != null) data.put("birthday", currentBirthday);
        if (currentGender != null) data.put("gender", currentGender);
        
        viewModel.updateProfile(data);
    }

    interface OnValueEnteredListener {
        void onValueEntered(String value);
    }
}
