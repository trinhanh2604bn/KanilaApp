package ui.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CommunityProfileFragment extends Fragment {

    private CommunityProfileViewModel viewModel;
    private ImageView ivAvatar;
    private TextView tvName, tvUsername, tvFollowers, tvFollowing, tvLikes;
    private ChipGroup cgSkinTags;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

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
        tvUsername = view.findViewById(R.id.tvProfileUsername);
        tvFollowers = view.findViewById(R.id.tvFollowerCount);
        tvFollowing = view.findViewById(R.id.tvFollowingCount);
        tvLikes = view.findViewById(R.id.tvTotalLikes);
        cgSkinTags = view.findViewById(R.id.cgSkinTags);
        viewPager = view.findViewById(R.id.profileViewPager);
        tabLayout = view.findViewById(R.id.profileTabLayout);

        view.findViewById(R.id.btnEditAvatar).setOnClickListener(v -> openAvatarBottomSheet());
        view.findViewById(R.id.btnMyRewards).setOnClickListener(v -> {
            RewardCenterFragment fragment = new RewardCenterFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main, fragment)
                    .addToBackStack(null)
                    .commit();
        });
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
        tvUsername.setText(getString(R.string.username_format, profile.getUsername()));
        tvFollowers.setText(String.valueOf(profile.getFollowerCount()));
        tvFollowing.setText(String.valueOf(profile.getFollowingCount()));
        tvLikes.setText(String.valueOf(profile.getTotalLikes()));
        
        if (profile.getAvatarUrl() != null) {
            Glide.with(this).load(profile.getAvatarUrl()).placeholder(R.drawable.ic_account).into(ivAvatar);
        }

        cgSkinTags.removeAllViews();
        if (profile.getSkinTags() != null && getContext() != null) {
            for (String tag : profile.getSkinTags()) {
                Chip chip = new Chip(getContext());
                chip.setText(tag);
                chip.setChipBackgroundColorResource(R.color.bg_section);
                chip.setTextColor(getContext().getColor(R.color.text_main));
                chip.setChipStrokeWidth(0f);
                chip.setTextSize(12);
                cgSkinTags.addView(chip);
            }
        }
    }

    private void openAvatarBottomSheet() {
        ProfileAvatarBottomSheet bottomSheet = new ProfileAvatarBottomSheet();
        bottomSheet.setOnAvatarActionListener(new ProfileAvatarBottomSheet.OnAvatarActionListener() {
            @Override
            public void onTakePhoto() {
                openMediaPicker();
            }

            @Override
            public void onChooseGallery() {
                openMediaPicker();
            }

            @Override
            public void onRemoveAvatar() {
                ivAvatar.setImageResource(R.drawable.ic_account);
            }
        });
        bottomSheet.show(getChildFragmentManager(), "AvatarBottomSheet");
    }

    private void openMediaPicker() {
        CommunityMediaPickerBottomSheet picker = new CommunityMediaPickerBottomSheet();
        picker.setOnMediaPickedListener(uris -> {
            if (uris != null && !uris.isEmpty()) {
                ivAvatar.setImageURI(uris.get(0));
            }
        });
        picker.show(getChildFragmentManager(), "AvatarPicker");
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
