package ui.community;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.frontend.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.ArrayList;
import java.util.List;

public class CommunityBlogFragment extends Fragment implements BlogAdapter.OnBlogClickListener {

    private BlogViewModel blogViewModel;
    private CommunityViewModel communityViewModel;
    private BlogAdapter adapter;
    private BlogBannerAdapter bannerAdapter;
    private ViewPager2 vpBlogBanner;
    private View[] indicators;
    private List<BlogPost> allBlogs;
    
    private final Handler bannerHandler = new Handler(Looper.getMainLooper());
    private Runnable bannerRunnable;
    private int bannerCount = 0;

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
        vpBlogBanner = view.findViewById(R.id.vpBlogBanner);
        indicators = new View[]{
                view.findViewById(R.id.indicator0),
                view.findViewById(R.id.indicator1),
                view.findViewById(R.id.indicator2),
                view.findViewById(R.id.indicator3)
        };
        
        view.findViewById(R.id.btnViewAll).setOnClickListener(v -> {
            // Reset filter and show all
            if (communityViewModel != null) {
                communityViewModel.setSearchQuery("");
            }
            Toast.makeText(getContext(), "Hiển thị tất cả bài viết", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupRecyclerView(View view) {
        // Main blog list
        RecyclerView rvBlogs = view.findViewById(R.id.rvBlogs);
        adapter = new BlogAdapter(this);
        rvBlogs.setAdapter(adapter);

        // Hero banner
        bannerAdapter = new BlogBannerAdapter();
        bannerAdapter.setOnBannerClickListener(this::onBlogClick);
        vpBlogBanner.setAdapter(bannerAdapter);
        vpBlogBanner.setUserInputEnabled(true);
        vpBlogBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateIndicators(position);
            }
        });
    }

    private void updateIndicators(int position) {
        for (int i = 0; i < indicators.length; i++) {
            if (indicators[i] != null) {
                indicators[i].setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                        getResources().getColor(i == position ? R.color.accent_dark : R.color.border_divider, null)
                ));
            }
        }
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
        stopAutoSlide();

        List<BlogPost> filteredList = new ArrayList<>();
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
        if (blogs == null || blogs.isEmpty()) {
            adapter.setBlogs(new ArrayList<>());
            bannerAdapter.setItems(new ArrayList<>());
            return;
        }

        // Restore hero section
        View layoutHero = getView() != null ? getView().findViewById(R.id.layoutHero) : null;
        if (layoutHero != null) {
            layoutHero.setVisibility(View.VISIBLE);
        }

        // Set top 4 blogs as featured banners
        bannerCount = Math.min(blogs.size(), 4);
        List<BlogPost> bannerBlogs = new ArrayList<>(blogs.subList(0, bannerCount));
        bannerAdapter.setItems(bannerBlogs);
        
        // Update dots visibility
        View layoutDots = getView() != null ? getView().findViewById(R.id.layoutDots) : null;
        if (layoutDots != null) {
            layoutDots.setVisibility(bannerCount > 1 ? View.VISIBLE : View.GONE);
            for (int i = 0; i < indicators.length; i++) {
                if (indicators[i] != null) {
                    indicators[i].setVisibility(i < bannerCount ? View.VISIBLE : View.GONE);
                }
            }
        }

        // All blogs displayed in the list below
        adapter.setBlogs(new ArrayList<>(blogs));
        
        startAutoSlide();
    }

    private void startAutoSlide() {
        stopAutoSlide();
        if (bannerCount <= 1) return;

        bannerRunnable = new Runnable() {
            @Override
            public void run() {
                if (vpBlogBanner != null && bannerCount > 1) {
                    int next = (vpBlogBanner.getCurrentItem() + 1) % bannerCount;
                    vpBlogBanner.setCurrentItem(next, true);
                    bannerHandler.postDelayed(this, 3000);
                }
            }
        };
        bannerHandler.postDelayed(bannerRunnable, 3000);
    }

    private void stopAutoSlide() {
        if (bannerRunnable != null) {
            bannerHandler.removeCallbacks(bannerRunnable);
            bannerRunnable = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (allBlogs != null && (communityViewModel.getSearchQuery().getValue() == null || communityViewModel.getSearchQuery().getValue().isEmpty())) {
            startAutoSlide();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAutoSlide();
    }

    @Override
    public void onDestroyView() {
        stopAutoSlide();
        vpBlogBanner = null;
        super.onDestroyView();
    }

    @Override
    public void onBlogClick(BlogPost blog) {
        if (getActivity() instanceof com.example.frontend.MainActivity) {
            ((com.example.frontend.MainActivity) getActivity()).loadFragment(BlogDetailFragment.newInstance(blog.getId()));
        }
    }

    @Override
    public void onSaveClick(BlogPost blog) {
        if (ui.community.util.CommunityAuthGuard.checkMember(this, com.example.frontend.core.auth.PendingAuthAction.ActionType.COMMUNITY_INTERACTION)) {
            blogViewModel.toggleSaveBlog(blog.getId(), !blog.isSaved());
            String msg = !blog.isSaved() ? "Đã lưu bài viết" : "Đã bỏ lưu bài viết";
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLikeClick(BlogPost blog) {
        if (ui.community.util.CommunityAuthGuard.checkMember(this, com.example.frontend.core.auth.PendingAuthAction.ActionType.COMMUNITY_INTERACTION)) {
            blogViewModel.toggleLikeBlog(blog.getId(), !blog.isLiked());
        }
    }

    @Override
    public void onShareClick(BlogPost blog) {
        showCommunityShareBottomSheet(blog);
    }

    private void showCommunityShareBottomSheet(BlogPost blog) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View sheet = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_community_share, null, false);
        dialog.setContentView(sheet);

        String shareUrl = "https://kanila.com/blog/" + blog.getId();

        sheet.findViewById(R.id.btnCopyLink).setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Kanila Blog", shareUrl);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(requireContext(), "Đã sao chép liên kết", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        sheet.findViewById(R.id.btnShareOther).setOnClickListener(v -> {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, blog.getTitle());
            sendIntent.putExtra(Intent.EXTRA_TEXT, blog.getTitle() + "\n" + shareUrl);
            startActivity(Intent.createChooser(sendIntent, getString(R.string.action_share)));
            dialog.dismiss();
        });

        View.OnClickListener developingListener = v -> {
            Toast.makeText(requireContext(), "Tính năng đang được phát triển", Toast.LENGTH_SHORT).show();
        };

        sheet.findViewById(R.id.btnShareZalo).setOnClickListener(developingListener);
        sheet.findViewById(R.id.btnShareMessenger).setOnClickListener(developingListener);
        sheet.findViewById(R.id.btnShareInstagram).setOnClickListener(developingListener);

        dialog.show();
    }
}
