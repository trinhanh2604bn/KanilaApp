package ui.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.frontend.R;
import java.util.List;

public class CommunityBlogFragment extends Fragment implements BlogAdapter.OnBlogClickListener {

    private BlogViewModel blogViewModel;
    private CommunityViewModel communityViewModel;
    private BlogAdapter adapter;
    private ImageView ivHeroThumbnail;
    private TextView tvHeroCategory, tvHeroTitle;
    private List<BlogPost> allBlogs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community_blog, container, false);
        initViews(view);
        setupRecyclerView(view);
        setupViewModel();
        return view;
    }

    private void initViews(View view) {
        ivHeroThumbnail = view.findViewById(R.id.ivHeroThumbnail);
        tvHeroCategory = view.findViewById(R.id.tvHeroCategory);
        tvHeroTitle = view.findViewById(R.id.tvHeroTitle);
    }

    private void setupRecyclerView(View view) {
        RecyclerView rvBlogs = view.findViewById(R.id.rvBlogs);
        adapter = new BlogAdapter(this);
        rvBlogs.setAdapter(adapter);
    }

    private void setupViewModel() {
        blogViewModel = new ViewModelProvider(this).get(BlogViewModel.class);
        communityViewModel = new ViewModelProvider(requireActivity()).get(CommunityViewModel.class);
        
        blogViewModel.getFeaturedBlogs().observe(getViewLifecycleOwner(), blogs -> {
            allBlogs = blogs;
            filterAndSetBlogs(blogs, communityViewModel.getSearchQuery().getValue());
        });

        communityViewModel.getSearchQuery().observe(getViewLifecycleOwner(), query -> {
            filterAndSetBlogs(allBlogs, query);
        });
    }

    private void filterAndSetBlogs(List<BlogPost> blogs, String query) {
        if (blogs == null) return;
        
        View layoutHero = getView() != null ? getView().findViewById(R.id.layoutHero) : null;
        
        if (query == null || query.isEmpty()) {
            if (layoutHero != null) layoutHero.setVisibility(View.VISIBLE);
            updateUI(blogs);
            return;
        }

        if (layoutHero != null) layoutHero.setVisibility(View.GONE);

        java.util.List<BlogPost> filteredList = new java.util.ArrayList<>();
        String lowerQuery = query.toLowerCase();
        for (BlogPost blog : blogs) {
            if (blog.getTitle().toLowerCase().contains(lowerQuery) ||
                blog.getCategory().toLowerCase().contains(lowerQuery) ||
                (blog.getExcerpt() != null && blog.getExcerpt().toLowerCase().contains(lowerQuery))) {
                filteredList.add(blog);
            }
        }
        
        adapter.setBlogs(filteredList);
    }

    private void updateUI(List<BlogPost> blogs) {
        if (blogs != null && !blogs.isEmpty()) {
            // Restore hero section
            View layoutHero = getView().findViewById(R.id.layoutHero);
            if (layoutHero != null) {
                layoutHero.setVisibility(View.VISIBLE);
            }

            BlogPost hero = blogs.get(0);
            tvHeroTitle.setText(hero.getTitle());
            tvHeroCategory.setText(hero.getCategory().toUpperCase());
            if (hero.getThumbnailUrl() != null) {
                Glide.with(this).load(hero.getThumbnailUrl()).placeholder(R.drawable.bg_slide_2).into(ivHeroThumbnail);
            }
            adapter.setBlogs(blogs.subList(1, blogs.size()));
        }
    }

    @Override
    public void onBlogClick(BlogPost blog) {
        BlogDetailFragment fragment = BlogDetailFragment.newInstance(blog.getId());
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onSaveClick(BlogPost blog) {
        blog.setSaved(!blog.isSaved());
        adapter.notifyDataSetChanged();
        String msg = blog.isSaved() ? "Đã lưu bài viết" : "Đã bỏ lưu bài viết";
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
