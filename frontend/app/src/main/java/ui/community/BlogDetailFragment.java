package ui.community;

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
import java.util.ArrayList;
import java.util.List;

public class BlogDetailFragment extends Fragment {

    private static final String ARG_BLOG_ID = "blog_id";
    private String blogId;
    private BlogViewModel viewModel;
    private BlogPost blog;

    private ImageView ivThumbnail, ivLikeIcon, btnSaveDetail;
    private TextView tvTitle, tvCategory, tvAuthor, tvTime, tvContent, tvLikeCountDetail, tvCommentsTitle;
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
        btnSaveDetail = view.findViewById(R.id.btnSaveDetail);
        tvTitle = view.findViewById(R.id.tvBlogTitle);
        tvCategory = view.findViewById(R.id.tvBlogCategory);
        tvAuthor = view.findViewById(R.id.tvBlogAuthor);
        tvTime = view.findViewById(R.id.tvBlogTime);
        tvContent = view.findViewById(R.id.tvBlogContent);
        tvLikeCountDetail = view.findViewById(R.id.tvLikeCountDetail);
        tvCommentsTitle = view.findViewById(R.id.tvCommentsTitle);
        rvSuggestedProducts = view.findViewById(R.id.rvSuggestedProducts);
        rvBlogComments = view.findViewById(R.id.rvBlogComments);

        view.findViewById(R.id.btnBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        
        view.findViewById(R.id.btnShare).setOnClickListener(v -> {
            com.example.frontend.feature.product.ShareProductBottomSheet shareSheet = 
                com.example.frontend.feature.product.ShareProductBottomSheet.newInstance("https://kanila.com/blog/" + blogId);
            shareSheet.show(getChildFragmentManager(), "ShareBlog");
        });
        
        view.findViewById(R.id.layoutLikeDetail).setOnClickListener(v -> toggleLike());
        btnSaveDetail.setOnClickListener(v -> toggleSave());
        
        view.findViewById(R.id.btnSendComment).setOnClickListener(v -> {
            EditText edt = view.findViewById(R.id.edtComment);
            if (!edt.getText().toString().trim().isEmpty()) {
                Toast.makeText(getContext(), "Đã gửi bình luận", Toast.LENGTH_SHORT).show();
                edt.setText("");
            }
        });
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
        tvTime.setText(blog.getCreatedAt());
        tvContent.setText(blog.getContent());
        tvLikeCountDetail.setText(String.valueOf(blog.getLikeCount()));
        tvCommentsTitle.setText(getString(R.string.comment_count_format, blog.getCommentCount()));

        if (blog.getThumbnailUrl() != null) {
            Glide.with(this).load(blog.getThumbnailUrl()).placeholder(R.drawable.bg_slide_2).into(ivThumbnail);
        }

        updateLikeUI();
        updateSaveUI();
        
        setupSuggestedProducts();
        setupComments();
    }

    private void setupSuggestedProducts() {
        ui.category.ProductAdapter productsAdapter = new ui.category.ProductAdapter();
        rvSuggestedProducts.setAdapter(productsAdapter);
        // Mock products
        List<com.example.frontend.model.Product> mock = new ArrayList<>();
        mock.add(new com.example.frontend.model.Product("p1", "The Ordinary", "Niacinamide 10% + Zinc 1%", "250000", "4.8", "1200", 0, "HOT", "Serum"));
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
        ivLikeIcon.setImageResource(blog.isLiked() ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
        ivLikeIcon.setColorFilter(blog.isLiked() ? 
            androidx.core.content.ContextCompat.getColor(getContext(), R.color.button) : 
            androidx.core.content.ContextCompat.getColor(getContext(), R.color.text_main));
        tvLikeCountDetail.setText(String.valueOf(blog.getLikeCount()));
    }

    private void toggleSave() {
        if (blog == null) return;
        blog.setSaved(!blog.isSaved());
        updateSaveUI();
        Toast.makeText(getContext(), blog.isSaved() ? "Đã lưu" : "Đã bỏ lưu", Toast.LENGTH_SHORT).show();
    }

    private void updateSaveUI() {
        if (blog == null) return;
        btnSaveDetail.setImageResource(blog.isSaved() ? R.drawable.ic_bookmark : R.drawable.ic_bookmark_outline);
    }
}
