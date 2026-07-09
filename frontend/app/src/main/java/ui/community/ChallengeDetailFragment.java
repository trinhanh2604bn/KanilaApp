package ui.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.google.android.material.button.MaterialButton;

import com.google.android.material.progressindicator.LinearProgressIndicator;

public class ChallengeDetailFragment extends Fragment {

    private static final String ARG_CHALLENGE_ID = "challenge_id";
    private String challengeId;
    private ChallengeViewModel viewModel;
    private Challenge challenge;

    private ImageView ivBanner;
    private TextView tvTitle, tvRules, tvProgressText, tvRemainingTime;
    private LinearProgressIndicator progressIndicator;
    private MaterialButton btnAction;
    private View layoutProgress;
    private RecyclerView rvTasks, rvProducts;
    private ChallengeTaskAdapter taskAdapter;
    private ProductThumbnailAdapter productsAdapter;



    public static ChallengeDetailFragment newInstance(String challengeId) {
        ChallengeDetailFragment fragment = new ChallengeDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHALLENGE_ID, challengeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            challengeId = getArguments().getString(ARG_CHALLENGE_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenge_detail, container, false);
        initViews(view);
        setupViewModel();
        return view;
    }

    private void initViews(View view) {
        ivBanner = view.findViewById(R.id.ivChallengeBanner);
        tvTitle = view.findViewById(R.id.tvChallengeTitle);
        tvRules = view.findViewById(R.id.tvRules);
        tvProgressText = view.findViewById(R.id.tvProgressText);
        tvRemainingTime = view.findViewById(R.id.tvRemainingTime);
        progressIndicator = view.findViewById(R.id.progressIndicator);
        btnAction = view.findViewById(R.id.btnAction);
        layoutProgress = view.findViewById(R.id.layoutProgress);
        rvTasks = view.findViewById(R.id.rvTasks);
        rvProducts = view.findViewById(R.id.rvProducts);

        view.findViewById(R.id.btnBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());


    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(ChallengeViewModel.class);
        challenge = viewModel.getChallengeById(challengeId);
        if (challenge != null) {
            displayChallenge();
        }
    }

    private void displayChallenge() {
        tvTitle.setText(challenge.getTitle());
        tvRules.setText(challenge.getRules());
        
        if (challenge.getBannerUrl() != null) {
            Glide.with(this).load(challenge.getBannerUrl()).placeholder(R.drawable.bg_slide_1).into(ivBanner);
        }

        if (challenge.isJoined()) {
            layoutProgress.setVisibility(View.VISIBLE);
            tvProgressText.setText(getString(R.string.home_social_challenge_progress_format, String.valueOf(challenge.getCurrentProgress()), String.valueOf(challenge.getDurationDays())));
            progressIndicator.setProgress((int) ((challenge.getCurrentProgress() / (float) challenge.getDurationDays()) * 100));
            btnAction.setText(R.string.challenge_action_post_progress);
        } else {
            layoutProgress.setVisibility(View.GONE);
            btnAction.setText(R.string.challenge_action_join_challenge);
        }

        if (challenge.getTasks() != null) {
            taskAdapter = new ChallengeTaskAdapter(false);
            rvTasks.setAdapter(taskAdapter);
            taskAdapter.setTasks(challenge.getTasks());
        }

        productsAdapter = new ProductThumbnailAdapter();
        rvProducts.setAdapter(productsAdapter);
        List<String> mockProducts = new java.util.ArrayList<>();
        mockProducts.add("https://example.com/p1.jpg");
        mockProducts.add("https://example.com/p2.jpg");
        productsAdapter.setImageUrls(mockProducts);



        btnAction.setOnClickListener(v -> {
            if (challenge.isJoined()) {
                ChallengeProgressPostFragment fragment = ChallengeProgressPostFragment.newInstance(challengeId);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, fragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                challenge.setJoined(true);
                challenge.setCurrentProgress(1);
                displayChallenge();
                Toast.makeText(getContext(), "Đã tham gia thử thách!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
