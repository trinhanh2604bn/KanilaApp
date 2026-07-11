package ui.support;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.frontend.R;
import com.google.android.material.button.MaterialButton;

public class TicketDetailFragment extends Fragment {

    private String ticketId = "#SR24059980";
    private String ticketType = "Tư vấn sản phẩm";
    private int selectedRating = 5;

    public static TicketDetailFragment newInstance(String id, String type) {
        TicketDetailFragment fragment = new TicketDetailFragment();
        Bundle args = new Bundle();
        args.putString("id", id);
        args.putString("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ticketId = getArguments().getString("id", ticketId);
            ticketType = getArguments().getString("type", ticketType);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ticket_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        ((TextView) view.findViewById(R.id.tvTicketId)).setText(ticketId);
        ((TextView) view.findViewById(R.id.tvTicketType)).setText(ticketType);

        view.findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });

        setupRatingLogic(view);
    }

    private void setupRatingLogic(View view) {
        LinearLayout starContainer = view.findViewById(R.id.starContainer);
        if (starContainer != null) {
            for (int i = 0; i < starContainer.getChildCount(); i++) {
                final int rating = i + 1;
                View star = starContainer.getChildAt(i);
                star.setOnClickListener(v -> updateStars(starContainer, rating));
            }
        }

        MaterialButton btnSubmit = view.findViewById(R.id.btnSubmitRating);
        if (btnSubmit != null) {
            btnSubmit.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Cảm ơn bạn đã đánh giá " + selectedRating + " sao cho yêu cầu " + ticketId, Toast.LENGTH_LONG).show();
                view.findViewById(R.id.ratingCard).setVisibility(View.GONE);
            });
        }
    }

    private void updateStars(LinearLayout container, int rating) {
        selectedRating = rating;
        for (int i = 0; i < container.getChildCount(); i++) {
            ImageView star = (ImageView) container.getChildAt(i);
            if (i < rating) {
                star.setColorFilter(ContextCompat.getColor(requireContext(), R.color.button));
            } else {
                star.setColorFilter(ContextCompat.getColor(requireContext(), R.color.border_divider));
            }
        }
        
        TextView tvRatingLabel = getView() != null ? getView().findViewById(R.id.tvRatingLabel) : null;
        if (tvRatingLabel != null) {
            String label;
            if (rating == 5) label = "Tuyệt vời!";
            else if (rating == 4) label = "Rất tốt";
            else if (rating == 3) label = "Tạm ổn";
            else if (rating == 2) label = "Chưa hài lòng";
            else label = "Rất tệ";
            tvRatingLabel.setText(label);
        }
    }
}
