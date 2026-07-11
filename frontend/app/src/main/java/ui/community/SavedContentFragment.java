package ui.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import java.util.List;

public class SavedContentFragment extends Fragment implements SavedContentAdapter.OnSavedItemClickListener {

    private CommunityProfileViewModel viewModel;
    private SavedContentAdapter adapter;
    private View layoutEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved_content, container, false);
        initViews(view);
        setupRecyclerView(view);
        setupViewModel();
        return view;
    }

    private void initViews(View view) {
        layoutEmpty = view.findViewById(R.id.layoutEmptyState);
        view.findViewById(R.id.btnExplore).setOnClickListener(v -> {
            // Navigate back to Feed or Home
            Toast.makeText(getContext(), "Khám phá Community", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupRecyclerView(View view) {
        RecyclerView rvSaved = view.findViewById(R.id.rvSavedContent);
        adapter = new SavedContentAdapter(this);
        rvSaved.setAdapter(adapter);
    }

    private void setupViewModel() {
        CommunityViewModel communityViewModel = new ViewModelProvider(requireActivity()).get(CommunityViewModel.class);
        BlogViewModel blogViewModel = new ViewModelProvider(requireActivity()).get(BlogViewModel.class);
        
        // Use a combined list and update on any changes
        MutableLiveData<List<SavedContent>> combinedSaved = new MutableLiveData<>(new java.util.ArrayList<>());
        combinedSaved.observe(getViewLifecycleOwner(), this::updateUI);

        // Function to refresh the list
        Runnable refreshList = () -> {
            List<SavedContent> list = new java.util.ArrayList<>();
            
            // Add saved feed posts
            List<Post> posts = communityViewModel.getFeedPosts().getValue();
            if (posts != null) {
                for (Post p : posts) {
                    if (p.isSaved()) {
                        list.add(new SavedContent(p.getId(), SavedContent.TYPE_FEED, p.getTitle(), 
                            p.getImages() != null && !p.getImages().isEmpty() ? p.getImages().get(0) : null, 
                            p.getUserName(), p.getTime()));
                    }
                }
            }

            // Add saved blog posts
            List<BlogPost> blogs = blogViewModel.getFeaturedBlogs().getValue();
            if (blogs != null) {
                for (BlogPost b : blogs) {
                    if (b.isSaved()) {
                        list.add(new SavedContent(b.getId(), SavedContent.TYPE_BLOG, b.getTitle(), 
                            b.getThumbnailUrl(), b.getAuthorName(), b.getCreatedAt()));
                    }
                }
            }
            combinedSaved.setValue(list);
        };

        communityViewModel.getFeedPosts().observe(getViewLifecycleOwner(), posts -> refreshList.run());
        blogViewModel.getFeaturedBlogs().observe(getViewLifecycleOwner(), blogs -> refreshList.run());
    }

    private void updateUI(List<SavedContent> items) {
        if (items == null || items.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            adapter.setItems(java.util.Collections.emptyList());
        } else {
            layoutEmpty.setVisibility(View.GONE);
            adapter.setItems(items);
        }
    }

    @Override
    public void onItemClick(SavedContent item) {
        if (SavedContent.TYPE_BLOG.equals(item.getType())) {
            BlogDetailFragment fragment = BlogDetailFragment.newInstance(item.getId());
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main, fragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            PostDetailFragment fragment = PostDetailFragment.newInstance(item.getId());
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onRemoveClick(SavedContent item) {
        if (SavedContent.TYPE_BLOG.equals(item.getType())) {
            BlogViewModel blogViewModel = new ViewModelProvider(requireActivity()).get(BlogViewModel.class);
            blogViewModel.toggleSaveBlog(item.getId(), false);
        } else {
            CommunityViewModel communityViewModel = new ViewModelProvider(requireActivity()).get(CommunityViewModel.class);
            communityViewModel.toggleSave(item.getId(), false);
        }
        Toast.makeText(getContext(), "Đã gỡ khỏi mục lưu", Toast.LENGTH_SHORT).show();
    }
}
