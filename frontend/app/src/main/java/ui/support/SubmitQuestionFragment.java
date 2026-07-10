package ui.support;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.frontend.R;

public class SubmitQuestionFragment extends Fragment {

    private FaqViewModel viewModel;
    private EditText etQuestion;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(FaqViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_submit_question, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etQuestion = view.findViewById(R.id.etQuestion);

        view.findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });

        view.findViewById(R.id.btnSubmit).setOnClickListener(v -> {
            String question = etQuestion.getText().toString().trim();
            if (question.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập nội dung câu hỏi", Toast.LENGTH_SHORT).show();
                return;
            }

            String category = getSelectedCategory(view);
            viewModel.addQuestion(question, category);

            Toast.makeText(getContext(), "Câu hỏi của bạn đã được lưu và hiển thị trong mục " + category + "!", Toast.LENGTH_LONG).show();
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });
    }

    private String getSelectedCategory(View view) {
        if (((CheckBox) view.findViewById(R.id.cbOrders)).isChecked()) return "Đơn hàng";
        if (((CheckBox) view.findViewById(R.id.cbReturns)).isChecked()) return "Đổi trả";
        if (((CheckBox) view.findViewById(R.id.cbPayment)).isChecked()) return "Thanh toán";
        if (((CheckBox) view.findViewById(R.id.cbAccount)).isChecked()) return "Tài khoản";
        if (((CheckBox) view.findViewById(R.id.cbProduct)).isChecked()) return "Sản phẩm";
        if (((CheckBox) view.findViewById(R.id.cbPromotion)).isChecked()) return "Khuyến mãi";
        return "Mục khác";
    }
}
