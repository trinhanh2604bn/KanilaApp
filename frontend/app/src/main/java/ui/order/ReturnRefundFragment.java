package ui.order;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
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
import com.example.frontend.data.model.returnrefund.ReturnRefundRequestDto;
import com.example.frontend.data.remote.NetworkResult;
import ui.common.FragmentNavigationHelper;
import ui.community.MediaSourceBottomSheet;
import ui.community.SelectedMediaAdapter;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReturnRefundFragment extends Fragment {

    private static final String ARG_ORDER_ID = "order_id";
    private static final String ARG_ORDER_ITEM_ID = "order_item_id";

    private String orderId;
    private String orderItemId;
    private ReturnRefundViewModel viewModel;

    private RadioGroup rgReturnReasons, rgShippingMethods;
    private EditText edtDescription;
    private RecyclerView rvSelectedMedia;
    private View btnSubmit, layoutLoading;

    private SelectedMediaAdapter mediaAdapter;
    private final List<Uri> selectedMediaUris = new ArrayList<>();
    private Uri pendingCameraUri;
    private boolean isVideoPending = false;

    public static ReturnRefundFragment newInstance(String orderId, String orderItemId) {
        ReturnRefundFragment fragment = new ReturnRefundFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ORDER_ID, orderId);
        args.putString(ARG_ORDER_ITEM_ID, orderItemId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderId = getArguments().getString(ARG_ORDER_ID);
            orderItemId = getArguments().getString(ARG_ORDER_ITEM_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_return_refund, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ReturnRefundViewModel.class);

        initViews(view);
        setupHeader(view);
        setupMediaList();
        setupListeners();
        observeViewModel();
    }

    private void initViews(View view) {
        rgReturnReasons = view.findViewById(R.id.rgReturnReasons);
        rgShippingMethods = view.findViewById(R.id.rgShippingMethods);
        edtDescription = view.findViewById(R.id.edtDescription);
        rvSelectedMedia = view.findViewById(R.id.rvSelectedMedia);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        layoutLoading = view.findViewById(R.id.layoutLoading);
    }

    private void setupHeader(View view) {
        View header = view.findViewById(R.id.layoutHeader);
        if (header != null) {
            TextView tvTitle = header.findViewById(R.id.tvTopBarTitle);
            if (tvTitle != null) tvTitle.setText("Đổi trả / Hoàn tiền");

            header.findViewById(R.id.btnTopBarBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());
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
        btnSubmit.setOnClickListener(v -> {
            if (validate()) {
                submitRequest();
            }
        });

        View cardRefundMethod = getView() != null ? getView().findViewById(R.id.cardRefundMethod) : null;
        if (cardRefundMethod != null) {
            cardRefundMethod.setOnClickListener(v -> {
                FragmentNavigationHelper.replaceFragment(requireActivity(), new ui.support.LinkedWalletsFragment());
            });
        }
    }

    private boolean validate() {
        if (rgReturnReasons.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getContext(), "Vui lòng chọn lý do đổi trả", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (rgShippingMethods.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getContext(), "Vui lòng chọn phương thức trả hàng", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void submitRequest() {
        int checkedReasonId = rgReturnReasons.getCheckedRadioButtonId();
        String reason = "";
        if (checkedReasonId == R.id.rbReasonWrongProduct) reason = "Giao sai sản phẩm";
        else if (checkedReasonId == R.id.rbReasonDefective) reason = "Sản phẩm bị lỗi / hư hỏng";
        else if (checkedReasonId == R.id.rbReasonIrritation) reason = "Kích ứng / không phù hợp";
        else if (checkedReasonId == R.id.rbReasonMissingItems) reason = "Thiếu hàng";
        else if (checkedReasonId == R.id.rbReasonChangeMind) reason = "Đổi ý không muốn mua nữa";
        else if (checkedReasonId == R.id.rbReasonOther) reason = "Khác";

        int checkedShipId = rgShippingMethods.getCheckedRadioButtonId();
        String shipMethod = checkedShipId == R.id.rbShipPickup ? "Shipper đến lấy" : "Tự gửi bưu cục";

        String description = edtDescription.getText().toString().trim();

        List<ReturnRefundRequestDto.EvidenceMedia> media = new ArrayList<>();
        for (Uri uri : selectedMediaUris) {
            // Assuming the system uses string URIs as placeholders for now, as seen in ReviewWriteFragment
            media.add(new ReturnRefundRequestDto.EvidenceMedia("image", uri.toString()));
        }

        viewModel.submitReturnRefund(orderId, orderItemId, reason, description, media, shipMethod, "Ví Kanila", "wallet_id_placeholder");
    }

    private void observeViewModel() {
        viewModel.getSubmitResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            layoutLoading.setVisibility(result.status == NetworkResult.Status.LOADING ? View.VISIBLE : View.GONE);
            if (result.status == NetworkResult.Status.SUCCESS) {
                Toast.makeText(getContext(), "Gửi yêu cầu thành công!", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();
            } else if (result.status == NetworkResult.Status.ERROR) {
                Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
            }
        });
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
