package ui.community;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import java.util.ArrayList;
import java.util.List;

public class ChallengeTaskAdapter extends RecyclerView.Adapter<ChallengeTaskAdapter.ViewHolder> {
    private List<ChallengeTask> tasks = new ArrayList<>();
    private final boolean isCheckable;
    private OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskClick(ChallengeTask task, int position);
    }

    public ChallengeTaskAdapter(boolean isCheckable) {
        this.isCheckable = isCheckable;
    }

    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.listener = listener;
    }

    public void setTasks(List<ChallengeTask> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_daily_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChallengeTask task = tasks.get(position);
        holder.tvTitle.setText(task.getTitle());
        if (task.getSubtitle() != null) {
            holder.tvSubtitle.setText(task.getSubtitle());
            holder.tvSubtitle.setVisibility(View.VISIBLE);
        } else {
            holder.tvSubtitle.setVisibility(View.GONE);
        }
        
        if (task.isCompleted()) {
            holder.ivStatus.setImageResource(R.drawable.ic_check_circle);
            holder.ivStatus.setColorFilter(holder.itemView.getContext().getColor(R.color.success));
        } else {
            holder.ivStatus.setImageResource(R.drawable.ic_chevron_right);
            holder.ivStatus.setColorFilter(holder.itemView.getContext().getColor(R.color.text_tertiary));
        }

        if (task.getIconResId() != 0) {
            holder.ivIcon.setImageResource(task.getIconResId());
        } else {
            // Fallback icons if not set
            if (position == 0) holder.ivIcon.setImageResource(R.drawable.ic_calendar);
            else if (position == 1) holder.ivIcon.setImageResource(R.drawable.ic_camera);
            else holder.ivIcon.setImageResource(R.drawable.ic_checklist);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskClick(task, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        android.widget.ImageView ivIcon, ivStatus;
        TextView tvTitle, tvSubtitle;
        View checkBox;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivTaskIcon);
            ivStatus = itemView.findViewById(R.id.ivTaskStatus);
            tvTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvSubtitle = itemView.findViewById(R.id.tvTaskSubtitle);
            checkBox = itemView.findViewById(R.id.cbTask);
        }
    }
}
