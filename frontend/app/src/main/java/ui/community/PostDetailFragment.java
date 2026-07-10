package ui.community;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.frontend.R;
import java.util.ArrayList;
import java.util.List;

public class PostDetailFragment extends Fragment {

    private String postId;
    private Post currentPost;
    private CommunityViewModel communityViewModel;
    private com.example.frontend.feature.account.AccountViewModel accountViewModel;
    private CommentAdapter commentAdapter;
    private EditText edtComment;
    private ImageButton btnSendComment;
    private RecyclerView rvComments;

    // Reply state
    private Comment replyingToComment = null;
    private View layoutReplyStatus;
    private TextView tvReplyingTo;
    private ImageButton btnCancelReply;

    // View components from included item_post
    private ImageView ivLikeIcon;
    private TextView tvLikeCount, tvShareCount, tvCommentCount;
    private ImageButton btnSave;
    private View layoutLike, layoutShare;
    
    // Missing view components for badges, products, and images
    private TextView tvPostType, tvPostSkinType;
    private View hsvPostProducts, hsvPostImages;
    private ViewGroup layoutPostProducts;
    private ImageView ivImage1, ivImage2, ivImage3;

    public static PostDetailFragment newInstance(String postId) {
        PostDetailFragment fragment = new PostDetailFragment();
        Bundle args = new Bundle();
        args.putString("postId", postId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postId = getArguments().getString("postId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);
        initViews(view);
        setupPostContent(view);
        setupComments();
        setupViewModel();
        return view;
    }

    private void initViews(View view) {
        edtComment = view.findViewById(R.id.edtComment);
        btnSendComment = view.findViewById(R.id.btnSendComment);
        rvComments = view.findViewById(R.id.rvComments);
        
        layoutReplyStatus = view.findViewById(R.id.layoutReplyStatus);
        tvReplyingTo = view.findViewById(R.id.tvReplyingTo);
        btnCancelReply = view.findViewById(R.id.btnCancelReply);

        // Bind included item_post views
        ivLikeIcon = view.findViewById(R.id.ivPostLikeIcon);
        tvLikeCount = view.findViewById(R.id.tvPostLikeCount);
        tvShareCount = view.findViewById(R.id.tvPostShareCount);
        tvCommentCount = view.findViewById(R.id.tvPostCommentCount);
        btnSave = view.findViewById(R.id.btnPostSave);
        layoutLike = view.findViewById(R.id.layoutPostLike);
        layoutShare = view.findViewById(R.id.layoutPostShare);
        
        // Bind missing UI elements
        tvPostType = view.findViewById(R.id.tvPostType);
        tvPostSkinType = view.findViewById(R.id.tvPostSkinType);
        hsvPostProducts = view.findViewById(R.id.hsvPostProducts);
        layoutPostProducts = view.findViewById(R.id.layoutPostProducts);
        hsvPostImages = view.findViewById(R.id.hsvPostImages);
        ivImage1 = view.findViewById(R.id.ivPostImage1);
        ivImage2 = view.findViewById(R.id.ivPostImage2);
        ivImage3 = view.findViewById(R.id.ivPostImage3);
        
        view.findViewById(R.id.btnBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        
        btnSendComment.setEnabled(false);
        btnSendComment.setAlpha(0.5f);

        edtComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean hasText = !s.toString().trim().isEmpty();
                btnSendComment.setEnabled(hasText);
                btnSendComment.setAlpha(hasText ? 1.0f : 0.5f);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        edtComment.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                if (btnSendComment.isEnabled()) {
                    btnSendComment.performClick();
                }
                return true;
            }
            return false;
        });

        btnSendComment.setOnClickListener(v -> {
            if (ui.community.util.CommunityAuthGuard.checkMember(this, com.example.frontend.core.auth.PendingAuthAction.ActionType.COMMUNITY_INTERACTION)) {
                submitComment();
            }
        });

        edtComment.setOnTouchListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                v.performClick();
                return !ui.community.util.CommunityAuthGuard.checkMember(this, com.example.frontend.core.auth.PendingAuthAction.ActionType.COMMUNITY_INTERACTION);
            }
            return false;
        });
        
        btnCancelReply.setOnClickListener(v -> cancelReplyMode());
    }

    private void submitComment() {
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

        // Notify ViewModel to update comment count and persist
        communityViewModel.addComment(postId, newComment);

        edtComment.setText("");
        cancelReplyMode();
        
        Toast.makeText(getContext(), parentId == null ? "Đã đăng bình luận" : "Đã trả lời", Toast.LENGTH_SHORT).show();
    }

    private void cancelReplyMode() {
        replyingToComment = null;
        layoutReplyStatus.setVisibility(View.GONE);
        edtComment.setHint(getString(R.string.comment_hint));
        hideKeyboard();
    }

    private void hideKeyboard() {
        View view = getActivity() != null ? getActivity().getCurrentFocus() : null;
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void showKeyboard() {
        if (getActivity() == null) return;
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(edtComment, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void setupPostContent(View view) {
        // Handle actions
        layoutLike.setOnClickListener(v -> {
            if (ui.community.util.CommunityAuthGuard.checkMember(this, com.example.frontend.core.auth.PendingAuthAction.ActionType.COMMUNITY_INTERACTION)) {
                if (currentPost != null) {
                    boolean newLikedState = !currentPost.isLiked();
                    currentPost.setLiked(newLikedState);
                    int currentCount = currentPost.getLikeCount();
                    if (newLikedState) {
                        currentPost.setLikeCount(currentCount + 1);
                    } else {
                        currentPost.setLikeCount(Math.max(0, currentCount - 1));
                    }
                    updateLikeUI();
                }
            }
        });

        layoutShare.setOnClickListener(v -> {
            // Share is allowed for guest
            if (currentPost != null) {
                CommunityShareBottomSheet shareSheet = CommunityShareBottomSheet.newInstance(currentPost);
                shareSheet.setOnShareListener(p -> {
                    p.setShareCount(p.getShareCount() + 1);
                    p.setShared(true);
                    tvShareCount.setText(String.valueOf(p.getShareCount()));
                    layoutShare.setSelected(true);
                });
                shareSheet.show(getChildFragmentManager(), "CommunityShareBottomSheet");
            }
        });

        btnSave.setOnClickListener(v -> {
            if (ui.community.util.CommunityAuthGuard.checkMember(this, com.example.frontend.core.auth.PendingAuthAction.ActionType.COMMUNITY_INTERACTION)) {
                if (currentPost != null) {
                    communityViewModel.toggleSave(currentPost.getId(), !currentPost.isSaved());
                    Toast.makeText(getContext(), currentPost.isSaved() ? "Đã lưu bài viết" : "Đã bỏ lưu", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupViewModel() {
        communityViewModel = new ViewModelProvider(requireActivity()).get(CommunityViewModel.class);
        accountViewModel = new ViewModelProvider(requireActivity()).get(com.example.frontend.feature.account.AccountViewModel.class);

        communityViewModel.getFeedPosts().observe(getViewLifecycleOwner(), posts -> {
            if (posts != null) {
                for (Post p : posts) {
                    if (p.getId().equals(postId)) {
                        currentPost = p;
                        bindPostData();
                        return;
                    }
                }
            }
            // Fallback mock if not found
            if (currentPost == null) {
                currentPost = new Post(postId, "Gia Ngân Helus", null, "Vừa xong", 
                    "Before/After", "dsdfdsfdfdfsfds", 
                    new java.util.ArrayList<>(), 0, 0, 0, false, false);
                currentPost.setPostType("Before/After");
                currentPost.setSkinType("Da dầu mụn");
                
                List<com.example.frontend.model.Product> mockProds = new ArrayList<>();
                mockProds.add(new com.example.frontend.model.Product("1", "Brand", "Serum Glow", "250000", "4.5", "100", 0, "REVIEW", "Category"));
                mockProds.add(new com.example.frontend.model.Product("2", "Brand", "Moisturizer", "300000", "4.8", "50", 0, "", "Category"));
                currentPost.setProducts(mockProds);
                
                List<String> mockImages = new ArrayList<>();
                mockImages.add("https://picsum.photos/400/400?random=1");
                mockImages.add("https://picsum.photos/400/400?random=2");
                currentPost.setImages(mockImages);
                
                bindPostData();
            }
        });
    }

    private void bindPostData() {
        View view = getView();
        if (currentPost == null || view == null) return;
        
        TextView tvUserName = view.findViewById(R.id.tvPostUserName);
        TextView tvTime = view.findViewById(R.id.tvPostTime);
        TextView tvTitle = view.findViewById(R.id.tvPostTitle);
        TextView tvContent = view.findViewById(R.id.tvPostContent);
        ImageView ivAvatar = view.findViewById(R.id.ivPostAvatar);
        ImageView ivVerified = view.findViewById(R.id.ivPostVerified);
        View layoutVerifiedPurchase = view.findViewById(R.id.layoutVerifiedPurchase);

        if (tvUserName != null) tvUserName.setText(currentPost.getUserName());
        if (tvTime != null) tvTime.setText(currentPost.getTime());
        if (tvTitle != null) tvTitle.setText(currentPost.getTitle());
        if (tvContent != null) tvContent.setText(currentPost.getContent());
        
        if (ivAvatar != null && currentPost.getUserAvatar() != null) {
            Glide.with(this).load(currentPost.getUserAvatar()).circleCrop().into(ivAvatar);
        } else if (ivAvatar != null) {
            ivAvatar.setImageResource(R.drawable.ic_account);
        }

        if (ivVerified != null) {
            ivVerified.setVisibility(currentPost.isVerified() ? View.VISIBLE : View.GONE);
        }
        if (layoutVerifiedPurchase != null) {
            layoutVerifiedPurchase.setVisibility(currentPost.isPurchased() ? View.VISIBLE : View.GONE);
        }

        tvCommentCount.setText(String.valueOf(currentPost.getCommentCount()));
        tvShareCount.setText(String.valueOf(currentPost.getShareCount()));
        
        if (commentAdapter != null && currentPost.getComments() != null) {
            commentAdapter.setComments(new ArrayList<>(currentPost.getComments()));
        }
        
        // Bind Badges
        String type = currentPost.getPostType();
        if (type == null) type = currentPost.getTitle(); // Fallback to title as per mock logic
        
        if (type != null && getContext() != null) {
            tvPostType.setVisibility(View.VISIBLE);
            tvPostType.setText(type.toUpperCase());
            
            int bgColor, textColor;
            switch (type) {
                case "Review":
                    bgColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.post_type_review_bg);
                    textColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.post_type_review_text);
                    break;
                case "Routine":
                    bgColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.post_type_routine_bg);
                    textColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.post_type_routine_text);
                    break;
                case "Before/After":
                    bgColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.post_type_before_after_bg);
                    textColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.post_type_before_after_text);
                    break;
                case "Hỏi đáp":
                    bgColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.post_type_qa_bg);
                    textColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.post_type_qa_text);
                    break;
                default:
                    bgColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.border_divider);
                    textColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_tertiary);
                    break;
            }
            if (tvPostType.getBackground() != null) {
                tvPostType.getBackground().setTint(bgColor);
            }
            tvPostType.setTextColor(textColor);
        } else {
            tvPostType.setVisibility(View.GONE);
        }

        if (currentPost.getSkinType() != null && !currentPost.getSkinType().isEmpty()) {
            tvPostSkinType.setVisibility(View.VISIBLE);
            tvPostSkinType.setText(currentPost.getSkinType());
        } else {
            tvPostSkinType.setVisibility(View.GONE);
        }

        // Bind Products
        List<com.example.frontend.model.Product> products = currentPost.getProducts();
        if (products != null && !products.isEmpty()) {
            hsvPostProducts.setVisibility(View.VISIBLE);
            layoutPostProducts.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            for (com.example.frontend.model.Product product : products) {
                View productTag = inflater.inflate(R.layout.item_post_product_tag, layoutPostProducts, false);
                ImageView ivProd = productTag.findViewById(R.id.ivProductImage);
                TextView tvProd = productTag.findViewById(R.id.tvProductName);
                
                tvProd.setText(product.getName());
                Glide.with(this)
                        .load(product.getImageUrl())
                        .placeholder(R.drawable.ic_product)
                        .into(ivProd);
                
                layoutPostProducts.addView(productTag);
            }
        } else {
            hsvPostProducts.setVisibility(View.GONE);
        }

        // Bind Images
        List<String> images = currentPost.getImages();
        if (images != null && !images.isEmpty()) {
            hsvPostImages.setVisibility(View.VISIBLE);
            ImageView[] ivs = {ivImage1, ivImage2, ivImage3};
            for (int i = 0; i < ivs.length; i++) {
                if (i < images.size()) {
                    ivs[i].setVisibility(View.VISIBLE);
                    Glide.with(this)
                            .load(images.get(i))
                            .centerCrop()
                            .placeholder(R.drawable.bg_skeleton_placeholder)
                            .into(ivs[i]);
                } else {
                    ivs[i].setVisibility(View.GONE);
                }
            }
        } else {
            hsvPostImages.setVisibility(View.GONE);
        }

        updateLikeUI();
        updateSaveUI();
        updateShareUI();
    }

    private void updateLikeUI() {
        if (currentPost == null) return;
        ivLikeIcon.setImageResource(currentPost.isLiked() ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
        layoutLike.setSelected(currentPost.isLiked());
        tvLikeCount.setText(String.valueOf(currentPost.getLikeCount()));
    }

    private void updateSaveUI() {
        if (currentPost == null) return;
        btnSave.setImageResource(currentPost.isSaved() ? R.drawable.ic_bookmark : R.drawable.ic_bookmark_outline);
        btnSave.setSelected(currentPost.isSaved());
    }

    private void updateShareUI() {
        if (currentPost == null) return;
        layoutShare.setSelected(currentPost.isShared());
    }

    private void setupComments() {
        commentAdapter = new CommentAdapter();
        if (currentPost != null) {
            commentAdapter.setPostAuthorName(currentPost.getUserName());
            commentAdapter.setComments(new ArrayList<>(currentPost.getComments()));
        }
        rvComments.setLayoutManager(new LinearLayoutManager(getContext()));
        rvComments.setAdapter(commentAdapter);
        
        commentAdapter.setOnCommentActionListener(new CommentAdapter.OnCommentActionListener() {
            @Override
            public void onReplyClick(Comment comment) {
                if (ui.community.util.CommunityAuthGuard.checkMember(PostDetailFragment.this, com.example.frontend.core.auth.PendingAuthAction.ActionType.COMMUNITY_INTERACTION)) {
                    enterReplyMode(comment);
                }
            }
        });
    }

    private void enterReplyMode(Comment comment) {
        replyingToComment = comment;
        layoutReplyStatus.setVisibility(View.VISIBLE);
        tvReplyingTo.setText(getString(R.string.replying_to_format, comment.getUserName()));
        edtComment.setHint(getString(R.string.reply_hint_format, comment.getUserName()));
        edtComment.requestFocus();
        showKeyboard();
    }
}
