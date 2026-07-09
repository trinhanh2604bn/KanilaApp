package ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.example.frontend.data.model.beauty.SavedRoutineDto;
import com.example.frontend.feature.beauty.BeautyProfileViewModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import ui.common.ViewUtils;

public class SavedBeautyRoutinesFragment extends Fragment {

    private BeautyProfileViewModel viewModel;
    private RoutineAdapter adapter;
    private TextView tvEmptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_saved_beauty_routines, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(BeautyProfileViewModel.class);
        
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        setupRecyclerView(view);
        setupEvents(view);
        observeViewModel();
    }

    private void setupRecyclerView(View view) {
        RecyclerView rv = view.findViewById(R.id.rvSavedRoutines);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new RoutineAdapter();
        rv.setAdapter(adapter);
        
        adapter.setOnItemClickListener(this::navigateToDetail);
    }

    private void observeViewModel() {
        viewModel.getSavedRoutines().observe(getViewLifecycleOwner(), routines -> {
            if (routines == null || routines.isEmpty()) {
                tvEmptyState.setVisibility(View.VISIBLE);
                adapter.setItems(new ArrayList<>());
            } else {
                tvEmptyState.setVisibility(View.GONE);
                adapter.setItems(routines);
            }
        });
    }

    private void setupEvents(View view) {
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }
    }

    private void navigateToDetail(SavedRoutineDto routine) {
        int containerId = (requireActivity().findViewById(R.id.main_fragment_container) != null)
                ? R.id.main_fragment_container : R.id.main;
        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(containerId, RecommendationLookFragment.newInstance(routine))
                .addToBackStack(null)
                .commit();
    }

    private class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.ViewHolder> {
        private List<SavedRoutineDto> items = new ArrayList<>();
        private OnItemClickListener listener;

        public void setItems(List<SavedRoutineDto> items) {
            this.items = items;
            notifyDataSetChanged();
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_routine, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SavedRoutineDto item = items.get(position);
            holder.tvName.setText(item.getName());
            holder.tvDate.setText(formatSavedDate(item.getSavedTimestamp()));
            if (item.getImageRes() != 0) {
                holder.ivImage.setImageResource(item.getImageRes());
            }
            
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(item);
            });
            ViewUtils.applyClickAnimation(holder.itemView);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        private String formatSavedDate(long timestamp) {
            long now = System.currentTimeMillis();
            long diff = now - timestamp;
            
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTimeInMillis(now);
            cal2.setTimeInMillis(timestamp);
            
            boolean isSameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                              cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);

            if (isSameDay) {
                if (diff < 60 * 1000) return "Vừa lưu";
                if (diff < 60 * 60 * 1000) return "Lưu " + (diff / (60 * 1000)) + " phút trước";
                return "Lưu " + (diff / (60 * 60 * 1000)) + " giờ trước";
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                return "Đã lưu ngày " + sdf.format(new Date(timestamp));
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvDate;
            ImageView ivImage;
            ViewHolder(View view) {
                super(view);
                tvName = view.findViewById(R.id.tvRoutineName);
                tvDate = view.findViewById(R.id.tvRoutineDate);
                ivImage = view.findViewById(R.id.ivRoutineImage);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(SavedRoutineDto routine);
    }
}
