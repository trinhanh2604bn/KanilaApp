package ui.community;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommunityFeedFragment extends Fragment {

    private CommunityViewModel viewModel;
    private PostAdapter adapter;
    private CreatePostHeaderAdapter headerAdapter;
    private RecyclerView rvFeed;
    private View layoutEmpty;
    private boolean isNavigating = false;
    private Set<String> hiddenPostIds = new HashSet<>();
    private static final String PREFS_NAME = "community_prefs";
    private static final String KEY_HIDDEN_POSTS = "community_hidden_post_ids";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community_feed, container, false);
        loadHiddenPostIds();
        initViews(view);
        setupRecyclerView();
        setupViewModel();
        return view;
    }

    private void loadHiddenPostIds() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> savedIds = prefs.getStringSet(KEY_HIDDEN_POSTS, new HashSet<>());
        hiddenPostIds = new HashSet<>(savedIds);
    }

    private void saveHiddenPostIds() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putStringSet(KEY_HIDDEN_POSTS, hiddenPostIds).apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        isNavigating = false;
    }

    private void initViews(View view) {
        rvFeed = view.findViewById(R.id.rvFeed);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
    }

    private void setupRecyclerView() {
        adapter = new PostAdapter();
        headerAdapter = new CreatePostHeaderAdapter(v -> {
            if (isNavigating) return;
            if (ui.community.util.CommunityAuthGuard.checkMember(this, com.example.frontend.core.auth.PendingAuthAction.ActionType.CREATE_COMMUNITY_POST)) {
                isNavigating = true;
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, new CreatePostFragment())
                        .addToBackStack("community_create_post")
                        .commit();
            }
        });

        ConcatAdapter concatAdapter = new ConcatAdapter(headerAdapter, adapter);
        rvFeed.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFeed.setAdapter(concatAdapter);
        
        adapter.setOnPostClickListener(new PostAdapter.OnPostClickListener() {
            @Override
            public void onPostClick(Post post) {
                if (isNavigating) return;
                isNavigating = true;
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, PostDetailFragment.newInstance(post.getId()))
                        .addToBackStack("community_post_detail")
                        .commit();
            }

            @Override
            public void onSaveClick(Post post) {
                if (ui.community.util.CommunityAuthGuard.checkMember(CommunityFeedFragment.this, com.example.frontend.core.auth.PendingAuthAction.ActionType.COMMUNITY_INTERACTION)) {
                    post.setSaved(!post.isSaved());
                    int index = adapter.getPosts().indexOf(post);
                    if (index != -1) {
                        adapter.notifyItemChanged(index);
                    }
                    Toast.makeText(getContext(), post.isSaved() ? "Đã lưu bài viết" : "Đã bỏ lưu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onMoreClick(Post post, View view) {
                if (ui.community.util.CommunityAuthGuard.checkMember(CommunityFeedFragment.this, com.example.frontend.core.auth.PendingAuthAction.ActionType.COMMUNITY_INTERACTION)) {
                    android.widget.PopupMenu popup = new android.widget.PopupMenu(getContext(), view);
                    popup.getMenu().add(getString(R.string.action_report_post));
                    popup.getMenu().add(getString(R.string.action_hide_post));
                    popup.setOnMenuItemClickListener(item -> {
                        CharSequence title = item.getTitle();
                        if (title == null) return false;

                        if (title.equals(getString(R.string.action_hide_post))) {
                            hidePost(post);
                        } else if (title.equals(getString(R.string.action_report_post))) {
                            showReportPostBottomSheet(post);
                        }
                        return true;
                    });
                    popup.show();
                }
            }

            private void hidePost(Post post) {
                hiddenPostIds.add(post.getId());
                saveHiddenPostIds();
                
                // TODO: Future API: POST /api/community/posts/{postId}/hide
                
                filterAndSetPosts(viewModel.getFeedPosts().getValue(), viewModel.getSearchQuery().getValue());
                Toast.makeText(getContext(), R.string.msg_post_hidden, Toast.LENGTH_SHORT).show();
            }

            private void showReportPostBottomSheet(Post post) {
                ReportPostBottomSheet reportSheet = ReportPostBottomSheet.newInstance(post.getId());
                reportSheet.setOnReportListener((postId, reason, note) -> {
                    // TODO: Future API: POST /api/community/posts/{postId}/report
                });
                reportSheet.show(getChildFragmentManager(), "ReportPostBottomSheet");
            }

            @Override
            public void onLikeClick(Post post) {
                if (ui.community.util.CommunityAuthGuard.checkMember(CommunityFeedFragment.this, com.example.frontend.core.auth.PendingAuthAction.ActionType.COMMUNITY_INTERACTION)) {
                    boolean newLikedState = !post.isLiked();
                    post.setLiked(newLikedState);

                    int currentCount = post.getLikeCount();
                    if (newLikedState) {
                        post.setLikeCount(currentCount + 1);
                    } else {
                        post.setLikeCount(Math.max(0, currentCount - 1));
                    }
                    
                    int index = adapter.getPosts().indexOf(post);
                    if (index != -1) {
                        adapter.notifyItemChanged(index);
                    }
                }
            }

            @Override
            public void onCommentClick(Post post) {
                if (isNavigating) return;
                isNavigating = true;
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, PostDetailFragment.newInstance(post.getId()))
                        .addToBackStack("community_post_detail")
                        .commit();
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
        viewModel = new ViewModelProvider(requireActivity()).get(CommunityViewModel.class);
        viewModel.getFeedPosts().observe(getViewLifecycleOwner(), posts -> {
            filterAndSetPosts(posts, viewModel.getSearchQuery().getValue());
        });

        viewModel.getSearchQuery().observe(getViewLifecycleOwner(), query -> {
            filterAndSetPosts(viewModel.getFeedPosts().getValue(), query);
        });
    }

    private void filterAndSetPosts(java.util.List<Post> posts, String query) {
        if (posts == null) return;
        
        java.util.List<Post> filteredList = new java.util.ArrayList<>();
        String lowerQuery = (query != null) ? query.toLowerCase() : "";

        for (Post post : posts) {
            // Check if hidden
            if (hiddenPostIds.contains(post.getId())) {
                continue;
            }

            // Check query
            if (lowerQuery.isEmpty() ||
                post.getUserName().toLowerCase().contains(lowerQuery) ||
                (post.getContent() != null && post.getContent().toLowerCase().contains(lowerQuery)) ||
                (post.getTitle() != null && post.getTitle().toLowerCase().contains(lowerQuery))) {
                filteredList.add(post);
            }
        }
        
        adapter.setPosts(filteredList);
        layoutEmpty.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
