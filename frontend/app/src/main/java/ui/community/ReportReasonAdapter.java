package ui.community;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import java.util.List;

public class ReportReasonAdapter extends RecyclerView.Adapter<ReportReasonAdapter.ViewHolder> {

    private final List<String> reasons;
    private int selectedPosition = -1;
    private OnReasonSelectedListener listener;

    public interface OnReasonSelectedListener {
        void onReasonSelected(String reason);
    }

    public ReportReasonAdapter(List<String> reasons) {
        this.reasons = reasons;
    }

    public void setOnReasonSelectedListener(OnReasonSelectedListener listener) {
        this.listener = listener;
    }

    public String getSelectedReason() {
        if (selectedPosition != -1) {
            return reasons.get(selectedPosition);
        }
        return null;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report_reason, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String reason = reasons.get(position);
        holder.tvReasonName.setText(reason);
        holder.rbReason.setChecked(position == selectedPosition);

        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getBindingAdapterPosition();
            if (selectedPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPosition);
                if (listener != null) {
                    listener.onReasonSelected(reason);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return reasons.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvReasonName;
        RadioButton rbReason;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReasonName = itemView.findViewById(R.id.tvReasonName);
            rbReason = itemView.findViewById(R.id.rbReason);
        }
    }
}
