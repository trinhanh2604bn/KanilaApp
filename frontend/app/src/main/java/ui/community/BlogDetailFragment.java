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
    private BlogViewModel blogViewModel;
    private com.example.frontend.feature.account.AccountViewModel accountViewModel;
    private BlogPost blog;

    private ImageView ivThumbnail, ivLikeIcon, btnSaveDetail, ivLikeIconBottom, ivVerified;
    private TextView tvTitle, tvCategory, tvAuthor, tvTime, tvContent, tvLikeCountDetail, tvCommentsTitle;
    private TextView tvLikeCountBottom, tvCommentCountDetail, tvShareCountDetail;
    private android.widget.ImageButton btnSendComment;
    private RecyclerView rvSuggestedProducts, rvBlogComments;
    private CommentAdapter commentAdapter;
    private com.example.frontend.feature.home.HomeProductAdapter suggestedProductsAdapter;

    // Reply state
    private Comment replyingToComment = null;
    private View layoutReplyStatus;
    private TextView tvReplyingTo;
    private android.widget.ImageButton btnCancelReply;

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

        layoutReplyStatus = view.findViewById(R.id.layoutReplyStatus);
        tvReplyingTo = view.findViewById(R.id.tvReplyingTo);
        btnCancelReply = view.findViewById(R.id.btnCancelReply);

        view.findViewById(R.id.btnBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        
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
        btnSendComment = view.findViewById(R.id.btnSendComment);
        
        btnSendComment.setEnabled(false);
        btnSendComment.setAlpha(0.5f);

        btnSendComment.setOnClickListener(v -> {
            if (ui.community.util.CommunityAuthGuard.checkMember(this, com.example.frontend.core.auth.PendingAuthAction.ActionType.COMMUNITY_INTERACTION)) {
                submitComment(edtComment);
            }
        });

        if (edtComment != null) {
            edtComment.addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    boolean hasText = !s.toString().trim().isEmpty();
                    btnSendComment.setEnabled(hasText);
                    btnSendComment.setAlpha(hasText ? 1.0f : 0.5f);
                }

                @Override
                public void afterTextChanged(android.text.Editable s) {}
            });

            edtComment.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEND) {
                    if (btnSendComment.isEnabled()) {
                        btnSendComment.performClick();
                    }
                    return true;
                }
                return false;
            });

            edtComment.setOnTouchListener((v, event) -> {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    v.performClick();
                    return !ui.community.util.CommunityAuthGuard.checkMember(this, com.example.frontend.core.auth.PendingAuthAction.ActionType.COMMUNITY_INTERACTION);
                }
                return false;
            });
        }
        
        btnCancelReply.setOnClickListener(v -> cancelReplyMode());

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
        blogViewModel = new ViewModelProvider(requireActivity()).get(BlogViewModel.class);
        accountViewModel = new ViewModelProvider(requireActivity()).get(com.example.frontend.feature.account.AccountViewModel.class);
        
        if (com.example.frontend.data.remote.TokenManager.getInstance(requireContext()).isLoggedIn()) {
            accountViewModel.loadProfileHub();
        }

        blogViewModel.getFeaturedBlogs().observe(getViewLifecycleOwner(), blogs -> {
            if (blogs != null) {
                for (BlogPost b : blogs) {
                    if (b.getId().equals(blogId)) {
                        blog = b;
                        displayBlog();
                        // Load real products from backend
                        blogViewModel.loadSuggestedProducts(blog.getProductIds());
                        break;
                    }
                }
            }
        });

        // Observe suggested products once here
        blogViewModel.getSuggestedProductsResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null || suggestedProductsAdapter == null) return;
            if (result.status == com.example.frontend.data.remote.NetworkResult.Status.SUCCESS && result.data != null) {
                suggestedProductsAdapter.setProducts(result.data);
                rvSuggestedProducts.setVisibility(View.VISIBLE);
            } else if (result.status != com.example.frontend.data.remote.NetworkResult.Status.LOADING) {
                rvSuggestedProducts.setVisibility(View.GONE);
            }
        });
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
        
        if (commentAdapter != null && blog.getComments() != null) {
            commentAdapter.setComments(new ArrayList<>(blog.getComments()));
        }

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
        if (suggestedProductsAdapter == null) {
            suggestedProductsAdapter = new com.example.frontend.feature.home.HomeProductAdapter();
            // Set item width for horizontal scroll
            int width = (int) (160 * getResources().getDisplayMetrics().density);
            suggestedProductsAdapter.setItemWidth(width);

            suggestedProductsAdapter.setOnProductClickListener(product -> {
                if (getActivity() instanceof com.example.frontend.MainActivity) {
                    ((com.example.frontend.MainActivity) getActivity())
                            .loadFragment(com.example.frontend.feature.product.ProductDetailFragment.newInstance(product.getId()));
                }
            });
            
            rvSuggestedProducts.setAdapter(suggestedProductsAdapter);
        }
    }

    private void setupComments() {
        commentAdapter = new CommentAdapter();
        if (blog != null) {
            commentAdapter.setPostAuthorName(blog.getAuthorName());
            commentAdapter.setComments(new ArrayList<>(blog.getComments()));
        }
        rvBlogComments.setAdapter(commentAdapter);
        
        commentAdapter.setOnCommentActionListener(new CommentAdapter.OnCommentActionListener() {
            @Override
            public void onReplyClick(Comment comment) {
                if (ui.community.util.CommunityAuthGuard.checkMember(BlogDetailFragment.this, com.example.frontend.core.auth.PendingAuthAction.ActionType.COMMUNITY_INTERACTION)) {
                    enterReplyMode(comment, (EditText) getView().findViewById(R.id.edtComment));
                }
            }
        });
    }

    private void toggleLike() {
        if (blog == null) return;
        blogViewModel.toggleLikeBlog(blog.getId(), !blog.isLiked());
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
        blogViewModel.toggleSaveBlog(blog.getId(), !blog.isSaved());
        Toast.makeText(getContext(), !blog.isSaved() ? "Đã lưu" : "Đã bỏ lưu", Toast.LENGTH_SHORT).show();
    }

    private void updateSaveUI() {
        if (blog == null || getContext() == null) return;
        btnSaveDetail.setImageResource(blog.isSaved() ? R.drawable.ic_bookmark : R.drawable.ic_bookmark_outline);
        btnSaveDetail.setColorFilter(androidx.core.content.ContextCompat.getColor(getContext(), 
            blog.isSaved() ? R.color.button : R.color.accent_dark));
    }

    private void submitComment(EditText edtComment) {
        if (edtComment == null || blog == null) return;
        String content = edtComment.getText().toString().trim();
        if (content.isEmpty()) return;

        String userName = "Người dùng Kanila";
        String userAvatar = null;

        // Get real user info
        com.example.frontend.data.remote.NetworkResult<com.example.frontend.data.model.account.ProfileHubDto> userResult = accountViewModel.getProfileHubResult().getValue();
        if (userResult != null && userResult.status == com.example.frontend.data.remote.NetworkResult.Status.SUCCESS && userResult.data != null) {
            if (userResult.data.getProfile() != null) {
                userName = userResult.data.getProfile().getFullName();
                userAvatar = userResult.data.getProfile().getAvatarUrl();
            }
        }

        String parentId = replyingToComment != null ? replyingToComment.getId() : null;
        String newId = String.valueOf(System.currentTimeMillis());

        Comment newComment = new Comment(
                newId,
                userName,
                userAvatar,
                content,
                "Vừa xong",
                0,
                false,
                parentId
        );

        // Persist comment via ViewModel
        blogViewModel.addComment(blogId, newComment);

        edtComment.setText("");
        cancelReplyMode();
        
        Toast.makeText(getContext(), parentId == null ? "Đã gửi bình luận" : "Đã trả lời", Toast.LENGTH_SHORT).show();
        
        // Hide keyboard
        hideKeyboard();
    }

    private void enterReplyMode(Comment comment, EditText edtComment) {
        replyingToComment = comment;
        layoutReplyStatus.setVisibility(View.VISIBLE);
        tvReplyingTo.setText("Đang trả lời " + comment.getUserName());
        if (edtComment != null) {
            edtComment.setHint("Trả lời " + comment.getUserName() + "...");
            edtComment.requestFocus();
            showKeyboard(edtComment);
        }
    }

    private void cancelReplyMode() {
        replyingToComment = null;
        layoutReplyStatus.setVisibility(View.GONE);
        EditText edtComment = getView().findViewById(R.id.edtComment);
        if (edtComment != null) {
            edtComment.setHint("Viết bình luận...");
        }
        hideKeyboard();
    }

    private void hideKeyboard() {
        View view = getActivity() != null ? getActivity().getCurrentFocus() : null;
        if (view != null) {
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void showKeyboard(View view) {
        if (getActivity() == null || view == null) return;
        android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
