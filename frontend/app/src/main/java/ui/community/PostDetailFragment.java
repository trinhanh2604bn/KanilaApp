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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.frontend.R;
import java.util.ArrayList;
import java.util.List;

public class PostDetailFragment extends Fragment {

    private String postId;
    private Post currentPost;
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
                submitComment();
                return true;
            }
            return false;
        });

        btnSendComment.setOnClickListener(v -> submitComment());
        
        btnCancelReply.setOnClickListener(v -> cancelReplyMode());
    }

    private void submitComment() {
        String content = edtComment.getText().toString().trim();
        if (content.isEmpty()) return;

        String parentId = replyingToComment != null ? replyingToComment.getId() : null;
        String newId = String.valueOf(System.currentTimeMillis());
        
        Comment newComment = new Comment(
                newId,
                "User Name", // Should be real current user
                null,
                content,
                "Vừa xong",
                0,
                false,
                parentId
        );

        List<Comment> currentComments = new ArrayList<>(commentAdapter.getComments());
        
        if (parentId == null) {
            // Normal comment - add to end or top depending on preference, here end
            currentComments.add(newComment);
        } else {
            // Reply - find parent and insert after parent or its last reply
            int insertIndex = -1;
            for (int i = 0; i < currentComments.size(); i++) {
                if (currentComments.get(i).getId().equals(parentId)) {
                    insertIndex = i + 1;
                    // Find the end of existing replies for this parent
                    while (insertIndex < currentComments.size() && 
                           currentComments.get(insertIndex).isReply() && 
                           currentComments.get(insertIndex).getParentId().equals(parentId)) {
                        insertIndex++;
                    }
                    break;
                }
            }
            
            if (insertIndex != -1) {
                currentComments.add(insertIndex, newComment);
            } else {
                currentComments.add(newComment);
            }
        }

        commentAdapter.setComments(currentComments);
        edtComment.setText("");
        cancelReplyMode();
        
        // Scroll to new comment
        int scrollPos = currentComments.indexOf(newComment);
        if (scrollPos != -1) {
            rvComments.smoothScrollToPosition(scrollPos);
        }
        
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
        });

        layoutShare.setOnClickListener(v -> {
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
            if (currentPost != null) {
                currentPost.setSaved(!currentPost.isSaved());
                updateSaveUI();
                Toast.makeText(getContext(), currentPost.isSaved() ? "Đã lưu bài viết" : "Đã bỏ lưu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupViewModel() {
        androidx.lifecycle.ViewModelProvider provider = new androidx.lifecycle.ViewModelProvider(requireActivity());
        CommunityViewModel viewModel = provider.get(CommunityViewModel.class);
        viewModel.getFeedPosts().observe(getViewLifecycleOwner(), posts -> {
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
                currentPost = new Post(postId, "Kim Trần", null, "2 giờ trước", 
                    "Serum Niacinamide 10%", "Nội dung bài viết chi tiết...", 
                    new java.util.ArrayList<>(), 1200, 137, 36, true, true);
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
        rvComments.setLayoutManager(new LinearLayoutManager(getContext()));
        rvComments.setAdapter(commentAdapter);
        
        commentAdapter.setOnCommentActionListener(new CommentAdapter.OnCommentActionListener() {
            @Override
            public void onReplyClick(Comment comment) {
                enterReplyMode(comment);
            }
        });

        List<Comment> mockComments = new ArrayList<>();
        mockComments.add(new Comment("1", "Mai Anh", null, "Mình dùng em này thấy da căng mịn hẳn luôn!", "1 giờ trước", 12, false));
        mockComments.add(new Comment("2", "Kim Trần", null, "Cảm ơn bạn nhé!", "45 phút trước", 5, true, "1"));
        mockComments.add(new Comment("3", "Linh Nguyễn", null, "Em này kiềm dầu ổn không ạ?", "30 phút trước", 2, false));
        commentAdapter.setComments(mockComments);
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
