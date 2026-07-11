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
import com.example.frontend.data.repository.ProductRepository;
import com.example.frontend.model.Product;
import com.example.frontend.ui.category.ProductAdapter;
import java.util.ArrayList;
import java.util.List;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import ui.community.ChallengeDailyFragment;

public class ChallengeActiveFragment extends Fragment {

    private static final String ARG_CHALLENGE_ID = "challenge_id";
    private String challengeId;
    private ChallengeViewModel viewModel;
    private Challenge challenge;
    private ProductRepository productRepository;

    private ImageView ivBanner;
    private TextView tvRules, tvProgressText, tvRemainingTime, tvJoinStatus;
    private LinearProgressIndicator progressIndicator;
    private MaterialButton btnAction;
    private RecyclerView rvTasks, rvProducts;
    private ChallengeTaskAdapter taskAdapter;
    private ProductAdapter productsAdapter;

    public static ChallengeActiveFragment newInstance(String challengeId) {
        ChallengeActiveFragment fragment = new ChallengeActiveFragment();
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
        View view = inflater.inflate(R.layout.fragment_challenge_active, container, false);
        initViews(view);
        setupViewModel();
        return view;
    }

    private void initViews(View view) {
        productRepository = new ProductRepository(requireContext());
        ivBanner = view.findViewById(R.id.ivChallengeBanner);
        tvRules = view.findViewById(R.id.tvRules);
        tvProgressText = view.findViewById(R.id.tvProgressText);
        tvRemainingTime = view.findViewById(R.id.tvRemainingTime);
        tvJoinStatus = view.findViewById(R.id.tvJoinStatus);
        progressIndicator = view.findViewById(R.id.progressIndicator);
        btnAction = view.findViewById(R.id.btnAction);
        rvTasks = view.findViewById(R.id.rvTasks);
        rvProducts = view.findViewById(R.id.rvProducts);

        view.findViewById(R.id.btnBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());

        setupProductsRecyclerView();
    }

    private void setupProductsRecyclerView() {
        productsAdapter = new ProductAdapter() {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_card, parent, false);
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

    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(ChallengeViewModel.class);
        
        // Observe active challenges to refresh UI when a task is completed
        viewModel.getActiveChallenges().observe(getViewLifecycleOwner(), challenges -> {
            challenge = viewModel.getChallengeById(challengeId);
            if (challenge != null) {
                displayChallenge();
            }
        });

        challenge = viewModel.getChallengeById(challengeId);
        if (challenge != null) {
            displayChallenge();
        }
    }

    private void displayChallenge() {
        tvRules.setText(challenge.getRules());
        
        if (challenge.getImageResId() != 0) {
            ivBanner.setImageResource(challenge.getImageResId());
        } else if (challenge.getBannerUrl() != null) {
            Glide.with(this).load(challenge.getBannerUrl()).placeholder(R.drawable.bg_slide_1).into(ivBanner);
        }

        if (challenge.getRemainingTime() != null) {
            tvRemainingTime.setText(challenge.getRemainingTime());
            tvRemainingTime.setVisibility(View.VISIBLE);
        }

        tvJoinStatus.setVisibility(View.VISIBLE);
        tvProgressText.setText(getString(R.string.home_social_challenge_progress_format, String.valueOf(challenge.getCurrentProgress()), String.valueOf(challenge.getDurationDays())));
        progressIndicator.setProgress((int) ((challenge.getCurrentProgress() / (float) challenge.getDurationDays()) * 100));

        loadChallengeProducts();

        if (challenge.getTasks() != null) {
            // Sequential interactive tasks
            taskAdapter = new ChallengeTaskAdapter(true);
            rvTasks.setAdapter(taskAdapter);
            taskAdapter.setTasks(challenge.getTasks());
            taskAdapter.setOnTaskClickListener((task, position) -> {
                if (!task.isCompleted()) {
                    ChallengeDailyFragment fragment = ChallengeDailyFragment.newInstance(challengeId, task.getId(), position);
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }

        btnAction.setOnClickListener(v -> {
            // Find first uncompleted task
            int firstUncompleted = -1;
            if (challenge.getTasks() != null) {
                for (int i = 0; i < challenge.getTasks().size(); i++) {
                    if (!challenge.getTasks().get(i).isCompleted()) {
                        firstUncompleted = i;
                        break;
                    }
                }
            }
            
            if (firstUncompleted != -1) {
                ChallengeDailyFragment fragment = ChallengeDailyFragment.newInstance(challengeId, 
                        challenge.getTasks().get(firstUncompleted).getId(), firstUncompleted);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, fragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast.makeText(getContext(), "Bạn đã hoàn thành tất cả nhiệm vụ!", Toast.LENGTH_SHORT).show();
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
}
