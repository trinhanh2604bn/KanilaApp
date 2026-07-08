package ui.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import java.util.List;

public class SavedContentFragment extends Fragment implements SavedContentAdapter.OnSavedItemClickListener {

    private CommunityProfileViewModel viewModel;
    private SavedContentAdapter adapter;
    private View layoutEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved_content, container, false);
        initViews(view);
        setupRecyclerView(view);
        setupViewModel();
        return view;
    }

    private void initViews(View view) {
        layoutEmpty = view.findViewById(R.id.layoutEmptyState);
        view.findViewById(R.id.btnExplore).setOnClickListener(v -> {
            // Navigate back to Feed or Home
            Toast.makeText(getContext(), "Khám phá Community", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupRecyclerView(View view) {
        RecyclerView rvSaved = view.findViewById(R.id.rvSavedContent);
        adapter = new SavedContentAdapter(this);
        rvSaved.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CommunityProfileViewModel.class);
        viewModel.getSavedContent().observe(getViewLifecycleOwner(), this::updateUI);
    }

    private void updateUI(List<SavedContent> items) {
        if (items == null || items.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            adapter.setItems(java.util.Collections.emptyList());
        } else {
            layoutEmpty.setVisibility(View.GONE);
            adapter.setItems(items);
        }
    }

    @Override
    public void onItemClick(SavedContent item) {
        if (SavedContent.TYPE_BLOG.equals(item.getType())) {
            BlogDetailFragment fragment = BlogDetailFragment.newInstance(item.getId());
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main, fragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            PostDetailFragment fragment = PostDetailFragment.newInstance(item.getId());
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onRemoveClick(SavedContent item) {
        Toast.makeText(getContext(), "Đã gỡ khỏi mục lưu", Toast.LENGTH_SHORT).show();
        // In real app, call VM to remove and refresh
    }
}
