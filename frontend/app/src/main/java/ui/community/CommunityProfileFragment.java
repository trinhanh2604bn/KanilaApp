package ui.community;

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
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CommunityProfileFragment extends Fragment {

    private CommunityProfileViewModel viewModel;
    private ImageView ivAvatar;
    private TextView tvName, tvFollowers, tvFollowing, tvLikes;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

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
                        Toast.makeText(getContext(), "Camera permission is required", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void updateAvatar(Uri uri) {
        Glide.with(this)
                .load(uri)
                .placeholder(R.drawable.ic_account)
                .into(ivAvatar);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community_profile, container, false);
        initViews(view);
        setupTabs();
        setupViewModel();
        return view;
    }

    private void initViews(View view) {
        ivAvatar = view.findViewById(R.id.ivProfileAvatar);
        tvName = view.findViewById(R.id.tvProfileName);
        tvFollowers = view.findViewById(R.id.tvFollowerCount);
        tvFollowing = view.findViewById(R.id.tvFollowingCount);
        tvLikes = view.findViewById(R.id.tvTotalLikes);

        viewPager = view.findViewById(R.id.profileViewPager);
        tabLayout = view.findViewById(R.id.profileTabLayout);

        view.findViewById(R.id.btnEditAvatar).setOnClickListener(v -> showMediaSourceBottomSheet());
        view.findViewById(R.id.btnMyRewards).setOnClickListener(v -> {
            RewardCenterFragment fragment = new RewardCenterFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        }
    }

    private void setupTabs() {
        viewPager.setAdapter(new ProfilePagerAdapter(this));
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText(R.string.profile_subtab_posts); break;
                case 1: tab.setText(R.string.profile_subtab_challenges); break;
                case 2: tab.setText(R.string.profile_subtab_saved); break;
            }
        }).attach();
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CommunityProfileViewModel.class);
        viewModel.getMyProfile().observe(getViewLifecycleOwner(), this::updateHeader);
    }

    private void updateHeader(CommunityProfile profile) {
        tvName.setText(profile.getName());
        tvFollowers.setText(String.valueOf(profile.getFollowerCount()));
        tvFollowing.setText(String.valueOf(profile.getFollowingCount()));
        tvLikes.setText(String.valueOf(profile.getTotalLikes()));
        
        if (profile.getAvatarUrl() != null) {
            Glide.with(this).load(profile.getAvatarUrl()).placeholder(R.drawable.ic_account).into(ivAvatar);
        }



    }

    private void showMediaSourceBottomSheet() {
        MediaSourceBottomSheet bottomSheet = new MediaSourceBottomSheet();
        bottomSheet.setOnMediaSourceSelectedListener(new MediaSourceBottomSheet.OnMediaSourceSelectedListener() {
            @Override
            public void onTakePhotoSelected() {
                checkCameraPermissionAndOpen();
            }

            @Override
            public void onChooseGallerySelected() {
                openGalleryForImage();
            }

            @Override
            public void onRecordVideoSelected() {
                Toast.makeText(getContext(), "Video avatar is not supported yet", Toast.LENGTH_SHORT).show();
            }
        });
        bottomSheet.show(getChildFragmentManager(), "MediaSourceBottomSheet");
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

    private static class ProfilePagerAdapter extends FragmentStateAdapter {
        public ProfilePagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return new ProfilePostsFragment();
                case 1: return ChallengeListFragment.newInstance(ChallengeListFragment.TYPE_JOINED);
                case 2: return new SavedContentFragment();
                default: return new PlaceholderFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}
