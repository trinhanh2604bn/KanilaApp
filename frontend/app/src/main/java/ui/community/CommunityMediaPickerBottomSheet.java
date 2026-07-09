package ui.community;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommunityMediaPickerBottomSheet extends BottomSheetDialogFragment {

    public interface OnMediaPickedListener {
        void onMediaPicked(List<Uri> uris);
    }

    private OnMediaPickedListener listener;
    private RecyclerView rvMediaGrid, rvSelectedTray;
    private SelectedMediaAdapter trayAdapter;
    private MediaGridAdapter gridAdapter;
    private final List<Uri> selectedUris = new ArrayList<>();
    private final List<Uri> initialSelectedUris = new ArrayList<>();
    
    private Uri cameraImageUri;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void setOnMediaPickedListener(OnMediaPickedListener listener) {
        this.listener = listener;
    }

    public void setInitialSelectedUris(List<Uri> uris) {
        this.initialSelectedUris.clear();
        if (uris != null) {
            this.initialSelectedUris.addAll(uris);
            this.selectedUris.clear();
            this.selectedUris.addAll(uris);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
            if (result && cameraImageUri != null) {
                if (selectedUris.size() < 10) {
                    selectedUris.add(cameraImageUri);
                    updateTray();
                    if (gridAdapter != null) {
                        gridAdapter.updateSelection(selectedUris);
                    }
                }
            }
        });

        cameraPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                startCameraLauncher();
            } else {
                Toast.makeText(getContext(), "Camera permission is required to take photos", Toast.LENGTH_SHORT).show();
            }
        });

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            boolean allGranted = true;
            for (Boolean granted : result.values()) {
                if (!granted) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                loadMedia();
            } else {
                Toast.makeText(getContext(), "Permission denied to read media", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_community_media_picker, container, false);
        initViews(view);
        setupTray();
        setupGrid();
        checkPermissionsAndLoadMedia();
        return view;
    }

    private void initViews(View view) {
        rvMediaGrid = view.findViewById(R.id.rvMediaGrid);
        rvSelectedTray = view.findViewById(R.id.rvSelectedTray);
        
        view.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.btnNext).setOnClickListener(v -> {
            if (listener != null) {
                listener.onMediaPicked(selectedUris);
            }
            dismiss();
        });

        View btnCamera = view.findViewById(R.id.btnCamera);
        if (btnCamera != null) {
            btnCamera.setOnClickListener(v -> openCamera());
        }
    }

    private void setupTray() {
        trayAdapter = new SelectedMediaAdapter();
        rvSelectedTray.setAdapter(trayAdapter);
        trayAdapter.setMediaUris(new ArrayList<>(selectedUris));
        trayAdapter.setOnMediaRemoveListener(uri -> {
            selectedUris.remove(uri);
            updateTray();
            if (gridAdapter != null) {
                gridAdapter.updateSelection(selectedUris);
            }
        });
        updateTrayVisibility();
    }

    private void updateTray() {
        if (trayAdapter != null) {
            trayAdapter.setMediaUris(new ArrayList<>(selectedUris));
        }
        updateTrayVisibility();
    }

    private void updateTrayVisibility() {
        View tray = getView();
        if (tray != null) {
            View layoutTray = tray.findViewById(R.id.layoutSelectionTray);
            if (layoutTray != null) {
                layoutTray.setVisibility(selectedUris.isEmpty() ? View.GONE : View.VISIBLE);
            }
        }
    }

    private void setupGrid() {
        gridAdapter = new MediaGridAdapter();
        rvMediaGrid.setAdapter(gridAdapter);
        gridAdapter.setOnMediaSelectionListener(new MediaGridAdapter.OnMediaSelectionListener() {
            @Override
            public void onSelectionChanged(List<MediaItem> selectedItems) {
                selectedUris.clear();
                for (MediaItem item : selectedItems) {
                    selectedUris.add(item.getUri());
                }
                updateTray();
            }

            @Override
            public void onMaxLimitReached() {
                Toast.makeText(getContext(), R.string.max_media_reached, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkPermissionsAndLoadMedia() {
        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO};
        } else {
            permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        }

        boolean hasAll = true;
        for (String p : permissions) {
            if (ContextCompat.checkSelfPermission(requireContext(), p) != PackageManager.PERMISSION_GRANTED) {
                hasAll = false;
                break;
            }
        }

        if (hasAll) {
            loadMedia();
        } else {
            requestPermissionLauncher.launch(permissions);
        }
    }

    private void loadMedia() {
        executorService.execute(() -> {
            List<MediaItem> items = new ArrayList<>();
            ContentResolver contentResolver = requireContext().getContentResolver();

            Uri collection;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL);
            } else {
                collection = MediaStore.Files.getContentUri("external");
            }

            String[] projection = new String[]{
                    MediaStore.Files.FileColumns._ID,
                    MediaStore.Files.FileColumns.MEDIA_TYPE,
                    MediaStore.Files.FileColumns.DATE_ADDED
            };

            String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

            String sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";

            try (Cursor cursor = contentResolver.query(collection, projection, selection, null, sortOrder)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
                    int typeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE);

                    do {
                        long id = cursor.getLong(idColumn);
                        int type = cursor.getInt(typeColumn);
                        boolean isVideo = (type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);

                        Uri contentUri;
                        if (isVideo) {
                            contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
                        } else {
                            contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                        }

                        items.add(new MediaItem(contentUri, isVideo));
                        
                        // Limit to 200 items for performance
                        if (items.size() >= 200) break;
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (gridAdapter != null) {
                        gridAdapter.setMediaItems(items);
                        gridAdapter.updateSelection(selectedUris);
                    }
                });
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }

    private void openCamera() {
        if (selectedUris.size() >= 10) {
            Toast.makeText(getContext(), R.string.max_media_reached, Toast.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCameraLauncher();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void startCameraLauncher() {
        try {
            File photoFile = createImageFile();
            cameraImageUri = FileProvider.getUriForFile(requireContext(),
                    requireContext().getPackageName() + ".fileprovider",
                    photoFile);
            takePictureLauncher.launch(cameraImageUri);
        } catch (Exception ex) {
            Toast.makeText(getContext(), "Không tìm thấy ứng dụng camera hoặc lỗi khi mở máy ảnh.", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }
}
