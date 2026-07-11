package ui.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.List;

public class ParticipantProgressBottomSheet extends BottomSheetDialogFragment {

    private String participantName;
    private String participantAvatar;
    private List<Post> posts;
    private ParticipantProgressPostAdapter adapter;

    public static ParticipantProgressBottomSheet newInstance(String name, String avatar, List<Post> posts) {
        ParticipantProgressBottomSheet fragment = new ParticipantProgressBottomSheet();
        fragment.participantName = name;
        fragment.participantAvatar = avatar;
        fragment.posts = posts;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_participant_progress, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvName = view.findViewById(R.id.tvSheetParticipantName);
        TextView tvSummary = view.findViewById(R.id.tvSheetProgressSummary);
        ImageView ivAvatar = view.findViewById(R.id.ivSheetAvatar);

        tvName.setText(participantName);
        tvSummary.setText(getString(R.string.challenge_progress_summary_full, String.valueOf(posts != null ? posts.size() : 0), "7")); // Mock 7 days

        Glide.with(this)
                .load(participantAvatar)
                .placeholder(R.drawable.ic_account)
                .into(ivAvatar);

        view.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());

        RecyclerView rvPosts = view.findViewById(R.id.rvPosts);
        adapter = new ParticipantProgressPostAdapter();
        rvPosts.setAdapter(adapter);
        adapter.setPosts(posts);
    }
}
