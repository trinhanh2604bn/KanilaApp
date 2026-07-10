package ui.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;

public class ProfilePostsFragment extends Fragment {

    private CommunityViewModel communityViewModel;
    private com.example.frontend.feature.account.AccountViewModel accountViewModel;
    private PostAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_posts, container, false);
        setupRecyclerView(view);
        setupViewModel();
        return view;
    }

    private void setupRecyclerView(View view) {
        RecyclerView rvPosts = view.findViewById(R.id.rvMyPosts);
        adapter = new PostAdapter();
        rvPosts.setAdapter(adapter);
        
        adapter.setOnPostClickListener(new PostAdapter.OnPostClickListener() {
            @Override
            public void onPostClick(Post post) {
                PostDetailFragment fragment = PostDetailFragment.newInstance(post.getId());
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, fragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onSaveClick(Post post) {
                post.setSaved(!post.isSaved());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onMoreClick(Post post, View view) {
                android.widget.PopupMenu popup = new android.widget.PopupMenu(getContext(), view);
                popup.getMenu().add("Chỉnh sửa");
                popup.getMenu().add("Xóa");
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getTitle().equals("Xóa")) {
                        communityViewModel.deletePost(post.getId());
                        Toast.makeText(getContext(), "Đã xóa bài viết", Toast.LENGTH_SHORT).show();
                    } else {
                        // Edit post
                        CreatePostFragment fragment = CreatePostFragment.newEditInstance(post.getId());
                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main, fragment)
                                .addToBackStack(null)
                                .commit();
                    }
                    return true;
                });
                popup.show();
            }

            @Override
            public void onLikeClick(Post post) {
                // Locally already updated in adapter
            }

            @Override
            public void onCommentClick(Post post) {
                onPostClick(post);
            }

            @Override
            public void onShareClick(Post post) {
                CommunityShareBottomSheet shareSheet = CommunityShareBottomSheet.newInstance(post);
                shareSheet.setOnShareListener(p -> {
                    p.setShareCount(p.getShareCount() + 1);
                    p.setShared(true);
                    int index = adapter.getPosts().indexOf(p);
                    if (index != -1) {
                        adapter.notifyItemChanged(index);
                    }
                });
                shareSheet.show(getChildFragmentManager(), "CommunityShareBottomSheet");
            }
        });
    }

    private void setupViewModel() {
        communityViewModel = new ViewModelProvider(requireActivity()).get(CommunityViewModel.class);
        accountViewModel = new ViewModelProvider(requireActivity()).get(com.example.frontend.feature.account.AccountViewModel.class);

        communityViewModel.getFeedPosts().observe(getViewLifecycleOwner(), allPosts -> {
            if (allPosts == null) return;

            // Get current user's name
            String currentUserName = "";
            com.example.frontend.data.remote.NetworkResult<com.example.frontend.data.model.account.ProfileHubDto> userResult = accountViewModel.getProfileHubResult().getValue();
            if (userResult != null && userResult.status == com.example.frontend.data.remote.NetworkResult.Status.SUCCESS && userResult.data != null) {
                if (userResult.data.getProfile() != null) {
                    currentUserName = userResult.data.getProfile().getFullName();
                }
            }

            // Filter posts by user name
            java.util.List<Post> myPosts = new java.util.ArrayList<>();
            for (Post p : allPosts) {
                if (p.getUserName().equals(currentUserName)) {
                    myPosts.add(p);
                }
            }
            adapter.setPosts(myPosts);
        });
    }
}
