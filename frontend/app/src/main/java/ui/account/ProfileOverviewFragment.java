package ui.account;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProfileOverviewFragment extends Fragment {

    private AccountViewModel viewModel;
    private ImageView ivAvatarLarge;
    private TextView tvNameValue, tvEmailValue, tvPhoneValue, tvBirthValue, tvGenderValue, btnSaveProfile;
    private View btnChangeAvatar;
    
    private String currentFullName;
    private String currentPhone;
    private String currentBirthday;
    private String currentGender;
    private String currentEmail;

    private Uri pendingCameraUri;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private ActivityResultLauncher<String> getContentLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;

    private String pendingAvatarBase64 = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_profile_overview, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActivityResultLaunchers();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);
        
        initViews(view);
        observeViewModel();
    }

    private void setupActivityResultLaunchers() {
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                result -> {
                    if (result && pendingCameraUri != null) {
                        updateAvatar(pendingCameraUri);
                    }
                }
        );

        getContentLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        updateAvatar(uri);
                    }
                }
        );

        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openCameraForPhoto();
                    } else {
                        Toast.makeText(getContext(), "Cấp quyền camera để chụp ảnh", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void updateAvatar(Uri uri) {
        try {
            // Quan trọng: Xóa tint để ảnh không bị ám màu accent_dark từ XML
            if (ivAvatarLarge != null) {
                ivAvatarLarge.setImageTintList(null);
            }

            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            if (bitmap == null) return;

            // Resize nhỏ lại trước khi encode (tránh quá nặng khi gửi API)
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, 400, 400, true);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            String base64 = "data:image/jpeg;base64," +
                    Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);

            // Hiển thị lên giao diện
            Glide.with(this)
                    .load(uri) // Load từ URI gốc để chất lượng tốt nhất trên UI
                    .placeholder(R.drawable.ic_account)
                    .circleCrop()
                    .into(ivAvatarLarge);

            // Lưu base64 để gửi đi khi nhấn "Lưu thay đổi"
            pendingAvatarBase64 = base64;

        } catch (Exception e) {
            Log.e("ProfileOverview", "Error processing image", e);
            Toast.makeText(getContext(), "Lỗi xử lý ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews(View view) {
        ivAvatarLarge = view.findViewById(R.id.ivAvatarLarge);
        btnChangeAvatar = view.findViewById(R.id.btnChangeAvatar);
        tvNameValue = view.findViewById(R.id.tvNameValue);
        tvEmailValue = view.findViewById(R.id.tvEmailValue);
        tvEmailValue.setOnClickListener(v -> {
            String emailValue = tvEmailValue.getText().toString();
            if (!emailValue.isEmpty() && !"Chưa cập nhật".equals(emailValue)) {
                Toast.makeText(getContext(), "Email đăng ký không thể thay đổi", Toast.LENGTH_SHORT).show();
                return;
            }
            showEditDialog("Email", "", value -> {
                if (!value.isEmpty()) {
                    tvEmailValue.setText(value);
                    currentEmail = value;
                }
            });
        });
        tvPhoneValue = view.findViewById(R.id.tvPhoneValue);
        tvBirthValue = view.findViewById(R.id.tvBirthValue);
        tvGenderValue = view.findViewById(R.id.tvGenderValue);
        btnSaveProfile = view.findViewById(R.id.btnSaveProfile);

        view.findViewById(R.id.btnBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        
        btnChangeAvatar.setOnClickListener(v -> showAvatarBottomSheet());
        
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

        btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private void showAvatarBottomSheet() {
        ProfileAvatarBottomSheet bottomSheet = new ProfileAvatarBottomSheet();
        bottomSheet.setOnAvatarActionSelectedListener(new ProfileAvatarBottomSheet.OnAvatarActionSelectedListener() {
            @Override
            public void onTakePhotoSelected() {
                checkCameraPermissionAndOpen();
            }

            @Override
            public void onChooseGallerySelected() {
                openGalleryForImage();
            }

            @Override
            public void onRemoveAvatarSelected() {
                if (ivAvatarLarge != null) {
                    ivAvatarLarge.setImageResource(R.drawable.ic_account);
                    // Có thể cần set lại tint nếu quay về icon mặc định
                    ivAvatarLarge.setImageTintList(android.content.res.ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), R.color.accent_dark)));
                }
                pendingAvatarBase64 = ""; // Hoặc giá trị để xóa ảnh trên server
            }
        });
        bottomSheet.show(getChildFragmentManager(), "ProfileAvatarBottomSheet");
    }

    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
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
            Toast.makeText(getContext(), "Lỗi khi tạo file ảnh", Toast.LENGTH_SHORT).show();
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
        tvEmailValue.setText(profile.getEmail() != null && !profile.getEmail().isEmpty() ? profile.getEmail() : "Chưa cập nhật");
        tvPhoneValue.setText(profile.getPhone() != null ? profile.getPhone() : "Chưa cập nhật");
        
        // Convert ISO date (YYYY-MM-DD) from DB to UI format (DD/MM/YYYY)
        String rawBirthday = profile.getBirthday();
        if (rawBirthday != null && !rawBirthday.isEmpty()) {
            currentBirthday = formatBirthdayForUI(rawBirthday);
            tvBirthValue.setText(currentBirthday);
        } else {
            tvBirthValue.setText("Chưa cập nhật");
        }
        
        tvGenderValue.setText(profile.getGender() != null ? profile.getGender() : "Chưa cập nhật");
        
        currentFullName = profile.getFullName();
        currentPhone = profile.getPhone();
        currentGender = profile.getGender();
        currentEmail = profile.getEmail();
        
        if (profile.getAvatarUrl() != null && !profile.getAvatarUrl().isEmpty()) {
            if (ivAvatarLarge != null) ivAvatarLarge.setImageTintList(null);
            Glide.with(this)
                    .load(profile.getAvatarUrl())
                    .placeholder(R.drawable.ic_account)
                    .circleCrop()
                    .into(ivAvatarLarge);
        }
    }

    private String formatBirthdayForUI(String rawDate) {
        try {
            // ISO might include time like "1987-12-31T00:00:00.000Z"
            String datePart = rawDate.contains("T") ? rawDate.split("T")[0] : rawDate;
            String[] parts = datePart.split("-");
            if (parts.length == 3) {
                return parts[2] + "/" + parts[1] + "/" + parts[0];
            }
        } catch (Exception ignored) {}
        return rawDate;
    }

    private void saveProfile() {
        if (currentFullName == null || currentFullName.trim().isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập họ tên", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        
        // Send both camelCase and snake_case to ensure backend compatibility
        data.put("fullName", currentFullName);
        data.put("full_name", currentFullName);

        if (currentPhone != null) {
            data.put("phone", currentPhone);
            data.put("phoneNumber", currentPhone);
        }

        if (currentEmail != null && !currentEmail.isEmpty()) data.put("email", currentEmail);

        if (currentBirthday != null && !currentBirthday.equals("Chưa cập nhật")) {
            String isoDate = convertToISODate(currentBirthday);
            data.put("birthday", isoDate);
            data.put("date_of_birth", isoDate);
        }
        
        if (currentGender != null) {
            data.put("gender", currentGender);
            data.put("sex", currentGender);
        }
        
        if (pendingAvatarBase64 != null) {
            data.put("avatar_url", pendingAvatarBase64);
            data.put("avatar", pendingAvatarBase64);
        }
        
        viewModel.updateProfile(data);
    }

    private String convertToISODate(String uiDate) {
        try {
            String[] parts = uiDate.split("/");
            if (parts.length == 3) {
                // From DD/MM/YYYY to YYYY-MM-DD
                return parts[2] + "-" + parts[1] + "-" + parts[0];
            }
        } catch (Exception ignored) {}
        return uiDate;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (pendingCameraUri != null) {
            outState.putParcelable("pending_camera_uri", pendingCameraUri);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            pendingCameraUri = savedInstanceState.getParcelable("pending_camera_uri");
        }
    }

    interface OnValueEnteredListener {
        void onValueEntered(String value);
    }
}
