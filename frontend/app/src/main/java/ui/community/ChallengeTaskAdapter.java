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

    public ChallengeTaskAdapter(boolean isCheckable) {
        this.isCheckable = isCheckable;
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
        holder.checkBox.setChecked(task.isCompleted());
        holder.checkBox.setEnabled(isCheckable);
        
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> task.setCompleted(isChecked));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView tvTitle;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.cbTask);
            tvTitle = itemView.findViewById(R.id.tvTaskTitle);
        }
    }
}
