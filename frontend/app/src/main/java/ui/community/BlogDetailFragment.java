package ui.community;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.ArrayList;
import java.util.List;

public class BlogDetailFragment extends Fragment {

    private static final String ARG_BLOG_ID = "blog_id";
    private String blogId;
    private BlogViewModel viewModel;
    private BlogPost blog;

    private ImageView ivThumbnail, ivLikeIcon, btnSaveDetail, ivLikeIconBottom, ivVerified;
    private TextView tvTitle, tvCategory, tvAuthor, tvTime, tvContent, tvLikeCountDetail, tvCommentsTitle;
    private TextView tvLikeCountBottom, tvCommentCountDetail, tvShareCountDetail;
    private RecyclerView rvSuggestedProducts, rvBlogComments;
    private CommentAdapter commentAdapter;

    public static BlogDetailFragment newInstance(String blogId) {
        BlogDetailFragment fragment = new BlogDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BLOG_ID, blogId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            blogId = getArguments().getString(ARG_BLOG_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blog_detail, container, false);
        initViews(view);
        setupViewModel();
        return view;
    }

    private void initViews(View view) {
        ivThumbnail = view.findViewById(R.id.ivBlogThumbnail);
        ivLikeIcon = view.findViewById(R.id.ivLikeIcon);
        ivLikeIconBottom = view.findViewById(R.id.ivLikeIconBottom);
        btnSaveDetail = view.findViewById(R.id.btnSaveDetail);
        tvTitle = view.findViewById(R.id.tvBlogTitle);
        tvCategory = view.findViewById(R.id.tvBlogCategory);
        tvAuthor = view.findViewById(R.id.tvBlogAuthor);
        tvTime = view.findViewById(R.id.tvBlogTime);
        tvContent = view.findViewById(R.id.tvBlogContent);
        tvLikeCountDetail = view.findViewById(R.id.tvLikeCountDetail);
        tvLikeCountBottom = view.findViewById(R.id.tvLikeCountBottom);
        tvCommentCountDetail = view.findViewById(R.id.tvCommentCountDetail);
        tvShareCountDetail = view.findViewById(R.id.tvShareCountDetail);
        tvCommentsTitle = view.findViewById(R.id.tvCommentsTitle);
        ivVerified = view.findViewById(R.id.ivVerified);
        rvSuggestedProducts = view.findViewById(R.id.rvSuggestedProducts);
        rvBlogComments = view.findViewById(R.id.rvBlogComments);

        view.findViewById(R.id.btnBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        
        View.OnClickListener shareListener = v -> showShareBottomSheet();

        view.findViewById(R.id.layoutLikeDetail).setOnClickListener(v -> {
            if (ui.community.util.CommunityAuthGuard.checkMember(this, com.example.frontend.core.auth.PendingAuthAction.ActionType.COMMUNITY_INTERACTION)) {
                toggleLike();
            }
        });
        if (view.findViewById(R.id.btnLikeBottom) != null) {
            view.findViewById(R.id.btnLikeBottom).setOnClickListener(v -> {
                if (ui.community.util.CommunityAuthGuard.checkMember(this, com.example.frontend.core.auth.PendingAuthAction.ActionType.COMMUNITY_INTERACTION)) {
                    toggleLike();
                }
            });
        }
        
        btnSaveDetail.setOnClickListener(v -> {
            if (ui.community.util.CommunityAuthGuard.checkMember(this, com.example.frontend.core.auth.PendingAuthAction.ActionType.COMMUNITY_INTERACTION)) {
                toggleSave();
            }
        });
        
        EditText edtComment = view.findViewById(R.id.edtComment);
        view.findViewById(R.id.btnSendComment).setOnClickListener(v -> {
            if (ui.community.util.CommunityAuthGuard.checkMember(this, com.example.frontend.core.auth.PendingAuthAction.ActionType.COMMUNITY_INTERACTION)) {
                if (edtComment != null && !edtComment.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Đã gửi bình luận", Toast.LENGTH_SHORT).show();
                    edtComment.setText("");
                }
            }
        });

        if (edtComment != null) {
            edtComment.setOnTouchListener((v, event) -> {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    v.performClick();
                    return !ui.community.util.CommunityAuthGuard.checkMember(this, com.example.frontend.core.auth.PendingAuthAction.ActionType.COMMUNITY_INTERACTION);
                }
                return false;
            });
        }

        if (view.findViewById(R.id.btnViewAllProducts) != null) {
            view.findViewById(R.id.btnViewAllProducts).setOnClickListener(v -> 
                Toast.makeText(getContext(), "Xem tất cả sản phẩm gợi ý", Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void showShareBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View sheet = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_community_share, null, false);
        dialog.setContentView(sheet);

        String shareUrl = "https://kanila.com/blog/" + blogId;

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
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, blog != null ? blog.getTitle() : "Kanila Blog");
            sendIntent.putExtra(Intent.EXTRA_TEXT, (blog != null ? blog.getTitle() : "") + "\n" + shareUrl);
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

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(BlogViewModel.class);
        blog = viewModel.getBlogById(blogId);
        if (blog != null) {
            displayBlog();
        }
    }

    private void displayBlog() {
        if (blog == null) return;
        tvTitle.setText(blog.getTitle());
        tvCategory.setText(blog.getCategory().toUpperCase());
        tvAuthor.setText(blog.getAuthorName());
        tvTime.setText(String.format("%s • %s", blog.getCreatedAt(), blog.getCategory()));
        tvContent.setText(blog.getContent());
        
        String likeText = formatCount(blog.getLikeCount());
        tvLikeCountDetail.setText(likeText);
        if (tvLikeCountBottom != null) tvLikeCountBottom.setText(likeText);
        if (tvCommentCountDetail != null) tvCommentCountDetail.setText(formatCount(blog.getCommentCount()));
        if (tvShareCountDetail != null) tvShareCountDetail.setText(formatCount(blog.getShareCount()));
        
        tvCommentsTitle.setText("Tất cả bình luận");

        if (blog.getImageResId() != 0) {
            ivThumbnail.setImageResource(blog.getImageResId());
        } else if (blog.getThumbnailUrl() != null) {
            Glide.with(this).load(blog.getThumbnailUrl()).placeholder(R.drawable.bg_slide_2).into(ivThumbnail);
        } else {
            ivThumbnail.setImageResource(R.drawable.img_blog1);
        }
        
        if (ivVerified != null) {
            ivVerified.setVisibility(blog.isAuthorVerified() ? View.VISIBLE : View.GONE);
        }

        updateLikeUI();
        updateSaveUI();
        
        setupSuggestedProducts();
        setupComments();
    }

    private String formatCount(int count) {
        if (count >= 1000) {
            return String.format(java.util.Locale.US, "%.1fK", count / 1000.0);
        }
        return String.valueOf(count);
    }

    private void setupSuggestedProducts() {
        com.example.frontend.feature.home.HomeProductAdapter productsAdapter = new com.example.frontend.feature.home.HomeProductAdapter();
        // Set item width for horizontal scroll
        int width = (int) (160 * getResources().getDisplayMetrics().density);
        productsAdapter.setItemWidth(width);
        
        rvSuggestedProducts.setAdapter(productsAdapter);
        // Mock products
        List<com.example.frontend.model.Product> mock = new ArrayList<>();
        mock.add(new com.example.frontend.model.Product("p1", "The Ordinary", "Niacinamide 10% + Zinc 1%", "250.000đ", "4.8", "1.2K", R.drawable.ic_product, "HOT", "Serum"));
        mock.add(new com.example.frontend.model.Product("p2", "Kanila", "Airy Cushion", "580.000đ", "4.8", "246", R.drawable.bg_slide_1, "NEW", "Makeup"));
        mock.add(new com.example.frontend.model.Product("p3", "Kanila", "Tone Up Sun Cream", "420.000đ", "4.9", "312", R.drawable.bg_slide_2, "HOT", "Skincare"));
        productsAdapter.setProducts(mock);
    }

    private void setupComments() {
        commentAdapter = new CommentAdapter();
        rvBlogComments.setAdapter(commentAdapter);
        // Mock comments
        List<Comment> mock = new ArrayList<>();
        mock.add(new Comment("c1", "Minh Thư", null, "Bài viết rất hữu ích ạ!", "2 giờ trước", 5, false));
        mock.add(new Comment("c2", "Ngọc Anh", null, "Mình cũng đang dùng serum này, mê xỉu", "5 giờ trước", 12, false));
        commentAdapter.setComments(mock);
    }

    private void toggleLike() {
        if (blog == null) return;
        blog.setLiked(!blog.isLiked());
        blog.setLikeCount(blog.isLiked() ? blog.getLikeCount() + 1 : blog.getLikeCount() - 1);
        updateLikeUI();
    }

    private void updateLikeUI() {
        if (blog == null || getContext() == null) return;
        int iconRes = blog.isLiked() ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline;
        int color = blog.isLiked() ? 
            androidx.core.content.ContextCompat.getColor(getContext(), R.color.button) : 
            androidx.core.content.ContextCompat.getColor(getContext(), R.color.text_main);
            
        ivLikeIcon.setImageResource(iconRes);
        ivLikeIcon.setColorFilter(color);
        
        if (ivLikeIconBottom != null) {
            ivLikeIconBottom.setImageResource(iconRes);
            ivLikeIconBottom.setColorFilter(color);
        }
        
        String likeText = formatCount(blog.getLikeCount());
        tvLikeCountDetail.setText(likeText);
        if (tvLikeCountBottom != null) tvLikeCountBottom.setText(likeText);
    }

    private void toggleSave() {
        if (blog == null) return;
        blog.setSaved(!blog.isSaved());
        updateSaveUI();
        Toast.makeText(getContext(), blog.isSaved() ? "Đã lưu" : "Đã bỏ lưu", Toast.LENGTH_SHORT).show();
    }

    private void updateSaveUI() {
        if (blog == null || getContext() == null) return;
        btnSaveDetail.setImageResource(blog.isSaved() ? R.drawable.ic_bookmark : R.drawable.ic_bookmark_outline);
        btnSaveDetail.setColorFilter(androidx.core.content.ContextCompat.getColor(getContext(), 
            blog.isSaved() ? R.color.button : R.color.accent_dark));
    }
}
