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
import com.example.frontend.data.repository.ProductRepository;
import com.example.frontend.model.Product;
import ui.category.ProductAdapter;
import java.util.ArrayList;
import java.util.List;
import com.google.android.material.button.MaterialButton;

import com.google.android.material.progressindicator.LinearProgressIndicator;

public class ChallengeDetailFragment extends Fragment {

    private static final String ARG_CHALLENGE_ID = "challenge_id";
    private String challengeId;
    private ChallengeViewModel viewModel;
    private com.example.frontend.feature.account.AccountViewModel accountViewModel;
    private Challenge challenge;
    private ProductRepository productRepository;

    private ImageView ivBanner;
    private TextView tvTitle, tvRules, tvProgressText, tvRemainingTime, tvJoinStatus;
    private LinearProgressIndicator progressIndicator;
    private MaterialButton btnAction;
    private View layoutProgress;
    private View layoutDailyTasks;
    private TextView tvParticipantCount;
    private RecyclerView rvTasks, rvProducts, rvParticipants;
    private ChallengeTaskAdapter taskAdapter;
    private ProductAdapter productsAdapter;
    private ChallengeParticipantAdapter participantAdapter;



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
        productRepository = new ProductRepository(requireContext());
        ivBanner = view.findViewById(R.id.ivChallengeBanner);
        tvTitle = view.findViewById(R.id.tvChallengeTitle);
        tvRules = view.findViewById(R.id.tvRules);
        tvProgressText = view.findViewById(R.id.tvProgressText);
        tvRemainingTime = view.findViewById(R.id.tvRemainingTime);
        tvJoinStatus = view.findViewById(R.id.tvJoinStatus);
        progressIndicator = view.findViewById(R.id.progressIndicator);
        btnAction = view.findViewById(R.id.btnAction);
        layoutProgress = view.findViewById(R.id.layoutProgress);
        layoutDailyTasks = view.findViewById(R.id.layoutDailyTasks);
        tvParticipantCount = view.findViewById(R.id.tvParticipantCount);
        rvTasks = view.findViewById(R.id.rvTasks);
        rvProducts = view.findViewById(R.id.rvProducts);
        rvParticipants = view.findViewById(R.id.rvParticipants);

        view.findViewById(R.id.btnBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());

        setupProductsRecyclerView();
        setupParticipantsRecyclerView();
    }

    private void setupParticipantsRecyclerView() {
        participantAdapter = new ChallengeParticipantAdapter(participant -> {
            ParticipantProgressBottomSheet sheet = ParticipantProgressBottomSheet.newInstance(
                    participant.getUserName(), 
                    participant.getUserAvatar(), 
                    participant.getProgressPosts()
            );
            sheet.show(getChildFragmentManager(), "ParticipantProgressBottomSheet");
        });
        rvParticipants.setAdapter(participantAdapter);
    }

    private void setupProductsRecyclerView() {
        // Use a customized version of ProductAdapter to set fixed width for horizontal items
        productsAdapter = new ProductAdapter() {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_card, parent, false);
                // Set fixed width for horizontal display
                ViewGroup.LayoutParams lp = view.getLayoutParams();
                lp.width = (int) (180 * parent.getContext().getResources().getDisplayMetrics().density);
                view.setLayoutParams(lp);
                return new ViewHolder(view);
            }
        };
        rvProducts.setAdapter(productsAdapter);
        productsAdapter.setOnProductClickListener(new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                com.example.frontend.feature.product.ProductDetailFragment fragment =
                        com.example.frontend.feature.product.ProductDetailFragment.newInstance(product.getId());
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.main, fragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onAddToCartClick(Product product) {
                // Handled if necessary
            }
        });
    }

    private void loadChallengeProducts() {
        List<String> productIds = challenge.getProductIds();
        if (productIds == null || productIds.isEmpty()) {
            rvProducts.setVisibility(View.GONE);
            return;
        }

        List<Product> products = new ArrayList<>();
        productsAdapter.setProducts(products);
        rvProducts.setVisibility(View.VISIBLE);

        for (String id : productIds) {
            productRepository.getProductById(id).observe(getViewLifecycleOwner(), result -> {
                if (result != null && result.status == com.example.frontend.data.remote.NetworkResult.Status.SUCCESS && result.data != null) {
                    products.add(result.data);
                    productsAdapter.setProducts(new ArrayList<>(products));
                }
            });
        }
    }

    private void setupViewModel() {
        accountViewModel = new ViewModelProvider(requireActivity()).get(com.example.frontend.feature.account.AccountViewModel.class);
        viewModel = new ViewModelProvider(requireActivity()).get(ChallengeViewModel.class);
        challenge = viewModel.getChallengeById(challengeId);
        if (challenge != null) {
            displayChallenge();
        }
    }

    private void displayChallenge() {
        // Hide title as it's already in the banner image
        tvTitle.setVisibility(View.GONE);
        tvTitle.setText(challenge.getTitle());
        tvRules.setText(challenge.getRules());
        
        if (challenge.getImageResId() != 0) {
            ivBanner.setImageResource(challenge.getImageResId());
        } else if (challenge.getBannerUrl() != null) {
            Glide.with(this).load(challenge.getBannerUrl()).placeholder(R.drawable.bg_slide_1).into(ivBanner);
        } else {
            ivBanner.setImageResource(R.drawable.bg_slide_1);
        }

        if (challenge.getRemainingTime() != null) {
            tvRemainingTime.setText(challenge.getRemainingTime());
            tvRemainingTime.setVisibility(View.VISIBLE);
        } else {
            tvRemainingTime.setVisibility(View.GONE);
        }

        if (challenge.isJoined()) {
            tvJoinStatus.setVisibility(View.VISIBLE);
            tvJoinStatus.setText("Đã tham gia");
            layoutProgress.setVisibility(View.VISIBLE);
            tvProgressText.setText(getString(R.string.home_social_challenge_progress_format, String.valueOf(challenge.getCurrentProgress()), String.valueOf(challenge.getDurationDays())));
            progressIndicator.setProgress((int) ((challenge.getCurrentProgress() / (float) challenge.getDurationDays()) * 100));
            btnAction.setText(R.string.challenge_action_post_progress);
        } else {
            tvJoinStatus.setVisibility(View.GONE);
            layoutProgress.setVisibility(View.GONE);
            btnAction.setText(R.string.challenge_action_join_challenge);
        }

        // Always show daily tasks as information
        layoutDailyTasks.setVisibility(View.VISIBLE);

        loadChallengeProducts();
        
        if (challenge.getParticipants() != null) {
            participantAdapter.setParticipants(challenge.getParticipants());
            if (tvParticipantCount != null) {
                tvParticipantCount.setText(getString(R.string.challenge_participants_count_format, 
                        String.valueOf(challenge.getParticipants().size())));
            }
        }

        if (challenge.getTasks() != null) {
            // Informational tasks (not clickable as per user request for "default" view)
            taskAdapter = new ChallengeTaskAdapter(false);
            rvTasks.setAdapter(taskAdapter);
            taskAdapter.setTasks(challenge.getTasks());
            taskAdapter.setOnTaskClickListener(null); // Disable clicks
        }


        btnAction.setOnClickListener(v -> {
            if (challenge.isJoined()) {
                // Member action: go to Active Challenge page
                ChallengeActiveFragment fragment = ChallengeActiveFragment.newInstance(challengeId);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, fragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                // Guard: Join challenge
                if (ui.community.util.CommunityAuthGuard.checkMember(this, com.example.frontend.core.auth.PendingAuthAction.ActionType.JOIN_CHALLENGE)) {
                    
                    ChallengeParticipant userParticipant = null;
                    com.example.frontend.data.remote.NetworkResult<com.example.frontend.data.model.account.ProfileHubDto> userResult = accountViewModel.getProfileHubResult().getValue();
                    if (userResult != null && userResult.status == com.example.frontend.data.remote.NetworkResult.Status.SUCCESS && userResult.data != null) {
                        if (userResult.data.getProfile() != null) {
                            userParticipant = new ChallengeParticipant(
                                    userResult.data.getProfile().getCustomerId() != null ? userResult.data.getProfile().getCustomerId() : "current_user",
                                    userResult.data.getProfile().getFullName(),
                                    userResult.data.getProfile().getAvatarUrl(),
                                    new ArrayList<>()
                            );
                        }
                    } else {
                        // Fallback mock user if profile not loaded
                        userParticipant = new ChallengeParticipant("u_me", "Bạn", null, new ArrayList<>());
                    }

                    viewModel.joinChallenge(challengeId, userParticipant);
                    
                    // Refresh current object state
                    challenge = viewModel.getChallengeById(challengeId);
                    
                    // Navigate to Active Challenge page immediately after joining
                    ChallengeActiveFragment fragment = ChallengeActiveFragment.newInstance(challengeId);
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main, fragment)
                            .addToBackStack(null)
                            .commit();

                    Toast.makeText(getContext(), "Đã tham gia thử thách!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
