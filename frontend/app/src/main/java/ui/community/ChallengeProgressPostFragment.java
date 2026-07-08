package ui.community;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;

public class ChallengeProgressPostFragment extends Fragment {

    private static final String ARG_CHALLENGE_ID = "challenge_id";
    private String challengeId;
    private ChallengeViewModel viewModel;
    private Challenge challenge;

    private TextView tvChallengeName, tvProgressDay, tvCharCounter;
    private EditText edtCaption;
    private RecyclerView rvSelectedMedia;
    private SelectedMediaAdapter mediaAdapter;
    private final List<Uri> selectedMediaUris = new ArrayList<>();
    private MaterialButton btnSubmit;
    private RecyclerView rvTasks, rvProductsUsed;
    private ChallengeTaskAdapter taskAdapter;
    private ProductThumbnailAdapter productsAdapter;

    public static ChallengeProgressPostFragment newInstance(String challengeId) {


        ChallengeProgressPostFragment fragment = new ChallengeProgressPostFragment();
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
        View view = inflater.inflate(R.layout.fragment_challenge_progress_post, container, false);
        initViews(view);
        setupViewModel();
        setupMediaList();
        return view;
    }

    private void initViews(View view) {
        tvChallengeName = view.findViewById(R.id.tvChallengeName);
        tvProgressDay = view.findViewById(R.id.tvProgressDay);
        tvCharCounter = view.findViewById(R.id.tvCharCounter);
        edtCaption = view.findViewById(R.id.edtCaption);
        rvSelectedMedia = view.findViewById(R.id.rvSelectedMedia);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        rvTasks = view.findViewById(R.id.rvTasks);
        rvProductsUsed = view.findViewById(R.id.rvProductsUsed);

        view.findViewById(R.id.btnBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());


        view.findViewById(R.id.btnAddMedia).setOnClickListener(v -> openMediaPicker());

        edtCaption.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCharCounter.setText(getString(R.string.char_counter_format, s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSubmit.setOnClickListener(v -> {
            // Update progress in repo
            challenge.setCurrentProgress(challenge.getCurrentProgress() + 1);
            Toast.makeText(getContext(), "Đã đăng tiến trình thành công!", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        });
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(ChallengeViewModel.class);
        challenge = viewModel.getChallengeById(challengeId);
        if (challenge != null) {
            tvChallengeName.setText(challenge.getTitle());
            tvProgressDay.setText(getString(R.string.challenge_day_format, challenge.getCurrentProgress() + 1, challenge.getDurationDays()));
            
            if (challenge.getTasks() != null) {

                taskAdapter = new ChallengeTaskAdapter(true);
                rvTasks.setAdapter(taskAdapter);
                taskAdapter.setTasks(challenge.getTasks());
            }

            productsAdapter = new ProductThumbnailAdapter();
            rvProductsUsed.setAdapter(productsAdapter);
            List<String> mockProducts = new ArrayList<>();
            mockProducts.add("https://example.com/p1.jpg");
            mockProducts.add("https://example.com/p2.jpg");
            productsAdapter.setImageUrls(mockProducts);
        }


    }

    private void setupMediaList() {
        mediaAdapter = new SelectedMediaAdapter();
        rvSelectedMedia.setAdapter(mediaAdapter);
        mediaAdapter.setOnMediaRemoveListener(uri -> {
            selectedMediaUris.remove(uri);
            mediaAdapter.setMediaUris(new ArrayList<>(selectedMediaUris));
        });
    }

    private void openMediaPicker() {
        CommunityMediaPickerBottomSheet picker = new CommunityMediaPickerBottomSheet();
        picker.setInitialSelectedUris(new ArrayList<>(selectedMediaUris));
        picker.setOnMediaPickedListener(uris -> {
            selectedMediaUris.clear();
            selectedMediaUris.addAll(uris);
            mediaAdapter.setMediaUris(new ArrayList<>(selectedMediaUris));
        });
        picker.show(getChildFragmentManager(), "MediaPicker");
    }
}
