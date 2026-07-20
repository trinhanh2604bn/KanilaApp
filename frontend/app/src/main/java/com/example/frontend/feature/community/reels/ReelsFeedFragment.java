package com.example.frontend.feature.community.reels;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.frontend.databinding.FragmentReelsFeedBinding;
import com.example.frontend.feature.community.reels.mock.MockReelsDataSource;

public class ReelsFeedFragment extends Fragment {

    private FragmentReelsFeedBinding binding;
    private ReelsViewModel viewModel;
    private ReelsAdapter adapter;
    private ReelPlayerManager playerManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReelsFeedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ReelsViewModel.class);
        
        setupViewPager();
        observeViewModel();
        
        viewModel.loadReels();
    }

    private void setupViewPager() {
        adapter = new ReelsAdapter();
        adapter.setOnReelActionListener(new ReelsAdapter.OnReelActionListener() {
            @Override
            public void onProductPillClick(MockReelsDataSource.MockReel reel) {
                ReelsProductBottomSheet bottomSheet = ReelsProductBottomSheet.newInstance(reel.getId());
                bottomSheet.show(getChildFragmentManager(), "ReelsProductBottomSheet");
            }

            @Override
            public void onLikeClick(MockReelsDataSource.MockReel reel) {
                // Local toggle is handled in adapter, here we could notify server in real app
            }

            @Override
            public void onCommentClick(MockReelsDataSource.MockReel reel) {
                Toast.makeText(getContext(), "Bình luận sẽ được làm ở phase sau", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSaveClick(MockReelsDataSource.MockReel reel) {
                // Local toggle handled in adapter
            }

            @Override
            public void onShareClick(MockReelsDataSource.MockReel reel) {
                Toast.makeText(getContext(), "Chia sẻ demo", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBackClick() {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }

            @Override
            public void onCreateReelClick() {
                com.example.frontend.data.remote.TokenManager tm = com.example.frontend.data.remote.TokenManager.getInstance(requireContext());
                if (tm.isLoggedIn()) {
                    if (tm.isKoc()) {
                        // Nếu đã là KOC, chuyển tới Dashboard (Creator Center)
                        ui.common.FragmentNavigationHelper.replaceFragment(requireActivity(), new ui.account.KocDashboardFragment());
                    } else {
                        // Nếu chưa là KOC, chuyển tới trang đăng ký KOC
                        ui.common.FragmentNavigationHelper.replaceFragment(requireActivity(), new ui.account.KocRegistrationFragment());
                    }
                } else {
                    // Yêu cầu đăng nhập nếu chưa login
                    com.example.frontend.core.auth.AuthNavigationHelper.showAuthPrompt(requireActivity(),
                        new com.example.frontend.core.auth.PendingAuthAction(
                            com.example.frontend.core.auth.PendingAuthAction.ActionType.CREATE_REELS, 
                            "ReelsFeed", 0, null));
                }
            }
        });

        binding.viewPagerReels.setAdapter(adapter);
        
        playerManager = new ReelPlayerManager(binding.viewPagerReels);
        binding.viewPagerReels.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                playerManager.playAtPosition(position);
            }
        });
    }

    private void observeViewModel() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            binding.layoutLoading.layoutLoadingState.setVisibility(state.loading ? View.VISIBLE : View.GONE);
            
            if (state.reels != null) {
                adapter.setItems(state.reels);
                binding.layoutEmpty.getRoot().setVisibility(state.reels.isEmpty() ? View.VISIBLE : View.GONE);
            } else {
                binding.layoutEmpty.getRoot().setVisibility(state.loading ? View.GONE : View.VISIBLE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        playerManager.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        playerManager.pause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        playerManager.release();
        binding = null;
    }
}
