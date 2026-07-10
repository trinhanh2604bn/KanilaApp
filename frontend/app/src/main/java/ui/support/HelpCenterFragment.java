package ui.support;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.frontend.R;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import com.example.frontend.feature.chatbot.ChatConversationFragment;
import ui.common.FragmentNavigationHelper;

public class HelpCenterFragment extends Fragment {

    private FaqViewModel viewModel;
    private List<FaqViewModel.FaqItemData> userQuestions;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(FaqViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help_center, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUserQuestions(view);
        setupEvents(view);
    }

    private void setupUserQuestions(View view) {
        List<FaqViewModel.FaqItemData> questions = viewModel.getUserQuestions();
        if (questions.isEmpty()) return;

        // Map latest user questions to the top slots
        // FAQ 1 is special (it uses a separate layout)
        View faq1 = view.findViewById(R.id.faq1);
        TextView tv1 = faq1.findViewById(R.id.tvFaqQuestion);
        tv1.setText(questions.get(0).question);
        faq1.setOnClickListener(v -> replaceFragment(FaqDetailFragment.newInstance(questions.get(0).question, questions.get(0).answer)));

        // FAQ 2-5
        int[] ids = {R.id.faq2, R.id.faq3, R.id.faq4, R.id.faq5};
        int[] tvIds = {R.id.tvFaq2, R.id.tvFaq3, R.id.tvFaq4, R.id.tvFaq5};

        for (int i = 0; i < ids.length; i++) {
            int qIndex = i + 1; // Start from the second user question
            if (questions.size() > qIndex) {
                View faqView = view.findViewById(ids[i]);
                TextView tv = view.findViewById(tvIds[i]);
                tv.setText(questions.get(qIndex).question);
                faqView.setOnClickListener(v -> replaceFragment(FaqDetailFragment.newInstance(questions.get(qIndex).question, questions.get(qIndex).answer)));
            }
        }
    }

    private void setupEvents(View view) {
        view.findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else {
                requireActivity().onBackPressed();
            }
        });

        view.findViewById(R.id.btnSupportIcon).setOnClickListener(v -> {
            replaceFragment(new SupportHistoryFragment());
        });

        view.findViewById(R.id.catOrders).setOnClickListener(v -> {
            replaceFragment(FaqTopicFragment.newInstance("Đơn hàng"));
        });

        view.findViewById(R.id.catReturns).setOnClickListener(v -> {
            replaceFragment(FaqTopicFragment.newInstance("Đổi trả"));
        });

        view.findViewById(R.id.catPayment).setOnClickListener(v -> {
            replaceFragment(FaqTopicFragment.newInstance("Thanh toán"));
        });

        view.findViewById(R.id.catAccount).setOnClickListener(v -> {
            replaceFragment(FaqTopicFragment.newInstance("Tài khoản"));
        });

        view.findViewById(R.id.catProduct).setOnClickListener(v -> {
            replaceFragment(FaqTopicFragment.newInstance("Sản phẩm"));
        });

        view.findViewById(R.id.catPromotion).setOnClickListener(v -> {
            replaceFragment(FaqTopicFragment.newInstance("Khuyến mãi"));
        });

        view.findViewById(R.id.chatNowFooter).setOnClickListener(v -> {
            replaceFragment(ChatConversationFragment.newInstance(""));
        });

        // "Xem tất cả" FAQ button
        view.findViewById(R.id.btnViewAllFaqs).setOnClickListener(v -> {
            replaceFragment(FaqTopicFragment.newInstance("Tất cả câu hỏi"));
        });

        List<FaqViewModel.FaqItemData> userQs = viewModel.getUserQuestions();

        // FAQ manual items (Example: FAQ 1)
        if (userQs.size() <= 0) {
            view.findViewById(R.id.faq1).setOnClickListener(v -> {
                replaceFragment(FaqDetailFragment.newInstance(
                    getString(R.string.faq_1),
                    "Để theo dõi đơn hàng, bạn có thể vào mục 'Đơn hàng của tôi' trong trang Cá nhân, hoặc sử dụng tính năng 'Tra cứu đơn hàng' ngay tại Trung tâm hỗ trợ này."
                ));
            });
        }

        if (userQs.size() <= 1) {
            view.findViewById(R.id.faq2).setOnClickListener(v -> {
                replaceFragment(FaqDetailFragment.newInstance(
                    getString(R.string.faq_2),
                    "Thời gian giao hàng tiêu chuẩn là từ 2-4 ngày làm việc tùy khu vực. Đối với các thành phố lớn như TP.HCM và Hà Nội, bạn có thể nhận hàng trong vòng 1-2 ngày."
                ));
            });
        }

        if (userQs.size() <= 2) {
            view.findViewById(R.id.faq3).setOnClickListener(v -> {
                replaceFragment(FaqDetailFragment.newInstance(
                    getString(R.string.faq_3),
                    "Kanila Beauty hỗ trợ đổi trả sản phẩm trong vòng 7 ngày kể từ khi nhận hàng nếu sản phẩm có lỗi từ nhà sản xuất hoặc bị hư hỏng trong quá trình vận chuyển."
                ));
            });
        }

        if (userQs.size() <= 3) {
            view.findViewById(R.id.faq4).setOnClickListener(v -> {
                replaceFragment(FaqDetailFragment.newInstance(
                    getString(R.string.faq_4),
                    "Nếu sản phẩm bị hư hỏng, bạn vui lòng chụp ảnh/quay video gói hàng và liên hệ ngay với Kanila qua mục 'Gửi yêu cầu' hoặc Chat trực tiếp để được hỗ trợ đổi trả miễn phí."
                ));
            });
        }

        if (userQs.size() <= 4) {
            view.findViewById(R.id.faq5).setOnClickListener(v -> {
                replaceFragment(FaqDetailFragment.newInstance(
                    getString(R.string.faq_5),
                    "Chúng tôi chấp nhận nhiều phương thức thanh toán bao gồm: Tiền mặt khi nhận hàng (COD), Thẻ tín dụng/ghi nợ, Chuyển khoản ngân hàng, và các ví điện tử như MoMo, ZaloPay."
                ));
            });
        }

        // More Info Section
        view.findViewById(R.id.itemPrioritySupport).setOnClickListener(v -> {
            replaceFragment(ChatConversationFragment.newInstance(""));
        });

        view.findViewById(R.id.itemCallSupport).setOnClickListener(v -> {
            try {
                android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_DIAL);
                intent.setData(android.net.Uri.parse("tel:19001234"));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Không thể thực hiện cuộc gọi", Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.tvSubmitQuestionLink).setOnClickListener(v -> {
            replaceFragment(new SubmitQuestionFragment());
        });

        setupSearchLogic(view);
    }

    private void setupSearchLogic(View view) {
        EditText etSearch = view.findViewById(R.id.etSearchSupport);
        if (etSearch == null) return;

        View layoutCategories = view.findViewById(R.id.categoriesGrid);
        View tvFaqTitle = view.findViewById(R.id.tvFaqTitle);
        View layoutMoreInfo = view.findViewById(R.id.layoutMoreInfo);
        View btnViewAll = view.findViewById(R.id.btnViewAllFaqs);

        // FAQ rows and their dividers
        View[] faqs = {
            view.findViewById(R.id.faq1), view.findViewById(R.id.faq2),
            view.findViewById(R.id.faq3), view.findViewById(R.id.faq4),
            view.findViewById(R.id.faq5)
        };
        View[] dividers = {
            view.findViewById(R.id.divider1), view.findViewById(R.id.divider2),
            view.findViewById(R.id.divider3), view.findViewById(R.id.divider4)
        };

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().toLowerCase().trim();
                String normalizedQuery = removeAccents(query);
                boolean isSearching = !query.isEmpty();

                // Ẩn/Hiện các thành phần khác khi tìm kiếm
                if (layoutCategories != null) layoutCategories.setVisibility(isSearching ? View.GONE : View.VISIBLE);
                if (layoutMoreInfo != null) layoutMoreInfo.setVisibility(isSearching ? View.GONE : View.VISIBLE);
                if (btnViewAll != null) btnViewAll.setVisibility(isSearching ? View.GONE : View.VISIBLE);

                // Thay đổi tiêu đề khi tìm kiếm
                if (tvFaqTitle instanceof TextView) {
                    ((TextView) tvFaqTitle).setText(isSearching ? "Kết quả tìm kiếm" : "Câu hỏi thường gặp");
                }

                for (int i = 0; i < faqs.length; i++) {
                    filterFaq(faqs[i], query, normalizedQuery);

                    if (i < dividers.length && dividers[i] != null) {
                        dividers[i].setVisibility(isSearching ? View.GONE : (faqs[i].getVisibility() == View.VISIBLE ? View.VISIBLE : View.GONE));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private boolean filterFaq(View faqView, String query, String normalizedQuery) {
        if (faqView == null) return false;

        TextView tvQuestion = null;
        if (faqView.getId() == R.id.faq1) {
            tvQuestion = faqView.findViewById(R.id.tvFaqQuestion);
        } else {
            if (faqView.getId() == R.id.faq2) tvQuestion = faqView.findViewById(R.id.tvFaq2);
            if (faqView.getId() == R.id.faq3) tvQuestion = faqView.findViewById(R.id.tvFaq3);
            if (faqView.getId() == R.id.faq4) tvQuestion = faqView.findViewById(R.id.tvFaq4);
            if (faqView.getId() == R.id.faq5) tvQuestion = faqView.findViewById(R.id.tvFaq5);
        }

        if (tvQuestion != null) {
            String text = tvQuestion.getText().toString().toLowerCase();
            String normalizedText = removeAccents(text);

            boolean matches = text.contains(query) || normalizedText.contains(normalizedQuery);

            // Bổ sung keyword thông minh: Nếu tìm "giao" mà câu hỏi có "hàng" hoặc "đơn hàng"
            if (!matches && query.equals("giao")) {
                if (text.contains("hàng") || text.contains("vận chuyển")) matches = true;
            }
            if (!matches && query.equals("hoàn")) {
                if (text.contains("đổi trả") || text.contains("trả hàng")) matches = true;
            }

            faqView.setVisibility(matches ? View.VISIBLE : View.GONE);
            return matches;
        }
        return false;
    }

    private String removeAccents(String s) {
        if (s == null) return "";
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").toLowerCase().replace('đ', 'd');
    }

    private void replaceFragment(Fragment fragment) {
        FragmentNavigationHelper.replaceFragment(requireActivity(), fragment);
    }
}
