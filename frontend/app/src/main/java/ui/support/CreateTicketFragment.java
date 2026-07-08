package ui.support;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;

public class CreateTicketFragment extends Fragment {

    private ActivityResultLauncher<String> pickImageLauncher;
    private final List<Uri> selectedImages = new ArrayList<>();
    private UploadImageAdapter imageAdapter;
    private RecyclerView rvImages;
    
    private TextView tvSubject, tvCharCount;
    private EditText edtDescription, edtOrderId;
    private ImageView ivErrorSubject, ivErrorOrderId, ivErrorDescription, ivErrorIssueType, ivErrorContactMethod;
    
    private LinearLayout chipReturn, chipMissing, chipPayment, chipConsult, chipOther;
    private MaterialCardView cardContactCall, cardContactEmail, cardContactChat;
    
    private String selectedIssueType = ""; // Start empty to force selection
    private String selectedContactMethod = ""; // Start empty to force selection

    public static CreateTicketFragment newInstance() {
        return new CreateTicketFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        onImagePicked(uri);
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_ticket, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupImageRecyclerView();
        setupListeners();
        
        // Initial state
        updateIssueTypeUI();
        updateContactMethodUI();
    }

    private void initViews(View view) {
        rvImages = view.findViewById(R.id.rvImages);
        tvSubject = view.findViewById(R.id.tvSubject);
        edtDescription = view.findViewById(R.id.edtDescription);
        tvCharCount = view.findViewById(R.id.tvCharCount);
        edtOrderId = view.findViewById(R.id.edtOrderId);

        ivErrorSubject = view.findViewById(R.id.ivErrorSubject);
        ivErrorOrderId = view.findViewById(R.id.ivErrorOrderId);
        ivErrorDescription = view.findViewById(R.id.ivErrorDescription);
        ivErrorIssueType = view.findViewById(R.id.ivErrorIssueType);
        ivErrorContactMethod = view.findViewById(R.id.ivErrorContactMethod);
        
        chipReturn = view.findViewById(R.id.chipReturn);
        chipMissing = view.findViewById(R.id.chipMissing);
        chipPayment = view.findViewById(R.id.chipPayment);
        chipConsult = view.findViewById(R.id.chipConsult);
        chipOther = view.findViewById(R.id.chipOther);
        
        cardContactCall = view.findViewById(R.id.cardContactCall);
        cardContactEmail = view.findViewById(R.id.cardContactEmail);
        cardContactChat = view.findViewById(R.id.cardContactChat);
    }

    private void setupListeners() {
        getView().findViewById(R.id.btnBack).setOnClickListener(v -> requireActivity().onBackPressed());

        getView().findViewById(R.id.layoutSubjectPicker).setOnClickListener(v -> showSubjectDialog());

        getView().findViewById(R.id.layoutUpload).setOnClickListener(v -> {
            if (selectedImages.size() < 5) {
                pickImageLauncher.launch("image/*");
            } else {
                Toast.makeText(getContext(), "Bạn chỉ có thể tải lên tối đa 5 ảnh", Toast.LENGTH_SHORT).show();
            }
        });

        edtDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCharCount.setText(s.length() + "/500");
                ivErrorDescription.setVisibility(View.GONE);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        edtOrderId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ivErrorOrderId.setVisibility(View.GONE);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        chipReturn.setOnClickListener(v -> { selectedIssueType = "return"; updateIssueTypeUI(); ivErrorIssueType.setVisibility(View.GONE); });
        chipMissing.setOnClickListener(v -> { selectedIssueType = "missing"; updateIssueTypeUI(); ivErrorIssueType.setVisibility(View.GONE); });
        chipPayment.setOnClickListener(v -> { selectedIssueType = "payment"; updateIssueTypeUI(); ivErrorIssueType.setVisibility(View.GONE); });
        chipConsult.setOnClickListener(v -> { selectedIssueType = "consult"; updateIssueTypeUI(); ivErrorIssueType.setVisibility(View.GONE); });
        chipOther.setOnClickListener(v -> { selectedIssueType = "other"; updateIssueTypeUI(); ivErrorIssueType.setVisibility(View.GONE); });

        cardContactCall.setOnClickListener(v -> { selectedContactMethod = "call"; updateContactMethodUI(); ivErrorContactMethod.setVisibility(View.GONE); });
        cardContactEmail.setOnClickListener(v -> { selectedContactMethod = "email"; updateContactMethodUI(); ivErrorContactMethod.setVisibility(View.GONE); });
        cardContactChat.setOnClickListener(v -> { selectedContactMethod = "chat"; updateContactMethodUI(); ivErrorContactMethod.setVisibility(View.GONE); });

        getView().findViewById(R.id.btnSubmit).setOnClickListener(v -> {
            if (validateFields()) {
                Toast.makeText(getContext(), "Gửi yêu cầu thành công!", Toast.LENGTH_SHORT).show();
                replaceFragment(new SupportHistoryFragment());
            }
        });
    }

    private void showSubjectDialog() {
        String[] subjects = {
            getString(R.string.issue_return),
            getString(R.string.issue_missing),
            getString(R.string.issue_payment),
            getString(R.string.issue_consult),
            getString(R.string.issue_other)
        };

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.label_subject)
                .setItems(subjects, (dialog, which) -> {
                    tvSubject.setText(subjects[which]);
                    tvSubject.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_main));
                    ivErrorSubject.setVisibility(View.GONE);
                })
                .show();
    }

    private void updateIssueTypeUI() {
        resetIssueChips();
        switch (selectedIssueType) {
            case "return": setChipSelected(chipReturn, R.id.ivIconReturn, R.id.tvTextReturn); break;
            case "missing": setChipSelected(chipMissing, R.id.ivIconMissing, R.id.tvTextMissing); break;
            case "payment": setChipSelected(chipPayment, R.id.ivIconPayment, R.id.tvTextPayment); break;
            case "consult": setChipSelected(chipConsult, R.id.ivIconConsult, R.id.tvTextConsult); break;
            case "other": setChipSelected(chipOther, R.id.ivIconOther, R.id.tvTextOther); break;
        }
    }

    private void resetIssueChips() {
        setChipNormal(chipReturn, R.id.ivIconReturn, R.id.tvTextReturn);
        setChipNormal(chipMissing, R.id.ivIconMissing, R.id.tvTextMissing);
        setChipNormal(chipPayment, R.id.ivIconPayment, R.id.tvTextPayment);
        setChipNormal(chipConsult, R.id.ivIconConsult, R.id.tvTextConsult);
        setChipNormal(chipOther, R.id.ivIconOther, R.id.tvTextOther);
    }

    private void setChipSelected(LinearLayout chip, int iconId, int textId) {
        chip.setBackgroundResource(R.drawable.bg_chip_pink);
        ((ImageView) chip.findViewById(iconId)).setColorFilter(ContextCompat.getColor(requireContext(), R.color.button));
        TextView tv = chip.findViewById(textId);
        tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.button));
        tv.setTypeface(null, android.graphics.Typeface.BOLD);
    }

    private void setChipNormal(LinearLayout chip, int iconId, int textId) {
        chip.setBackgroundResource(R.drawable.bg_chip_outline);
        ((ImageView) chip.findViewById(iconId)).setColorFilter(ContextCompat.getColor(requireContext(), R.color.text_tertiary));
        TextView tv = chip.findViewById(textId);
        tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_main));
        tv.setTypeface(null, android.graphics.Typeface.NORMAL);
    }

    private void updateContactMethodUI() {
        resetContactCards();
        switch (selectedContactMethod) {
            case "call": setContactCardSelected(cardContactCall, R.id.ivIconContactCall, R.id.tvTextContactCall, R.id.tvSubContactCall, 0); break;
            case "email": setContactCardSelected(cardContactEmail, R.id.ivIconContactEmail, R.id.tvTextContactEmail, R.id.tvSubContactEmail, 0); break;
            case "chat": setContactCardSelected(cardContactChat, R.id.ivIconContactChat, R.id.tvTextContactChat, R.id.tvSubContactChat, R.id.ivCheckContactChat); break;
        }
    }

    private void resetContactCards() {
        setContactCardNormal(cardContactCall, R.id.ivIconContactCall, R.id.tvTextContactCall, R.id.tvSubContactCall, 0);
        setContactCardNormal(cardContactEmail, R.id.ivIconContactEmail, R.id.tvTextContactEmail, R.id.tvSubContactEmail, 0);
        setContactCardNormal(cardContactChat, R.id.ivIconContactChat, R.id.tvTextContactChat, R.id.tvSubContactChat, R.id.ivCheckContactChat);
    }

    private void setContactCardSelected(MaterialCardView card, int iconId, int textId, int subId, int checkId) {
        card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.pink_bg));
        card.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.button));
        ((ImageView) card.findViewById(iconId)).setColorFilter(ContextCompat.getColor(requireContext(), R.color.button));
        ((TextView) card.findViewById(textId)).setTextColor(ContextCompat.getColor(requireContext(), R.color.button));
        TextView tvSub = card.findViewById(subId);
        tvSub.setTextColor(ContextCompat.getColor(requireContext(), R.color.button));
        tvSub.setAlpha(0.8f);
        if (checkId != 0) card.findViewById(checkId).setVisibility(View.VISIBLE);
    }

    private void setContactCardNormal(MaterialCardView card, int iconId, int textId, int subId, int checkId) {
        card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
        card.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.border_divider));
        ((ImageView) card.findViewById(iconId)).setColorFilter(ContextCompat.getColor(requireContext(), R.color.text_secondary));
        ((TextView) card.findViewById(textId)).setTextColor(ContextCompat.getColor(requireContext(), R.color.text_main));
        TextView tvSub = card.findViewById(subId);
        tvSub.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_tertiary));
        tvSub.setAlpha(1.0f);
        if (checkId != 0) card.findViewById(checkId).setVisibility(View.GONE);
    }

    private boolean validateFields() {
        boolean isValid = true;
        
        if (tvSubject.getText().toString().equals(getString(R.string.hint_subject))) {
            ivErrorSubject.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            ivErrorSubject.setVisibility(View.GONE);
        }

        if (edtOrderId.getText().toString().trim().isEmpty()) {
            ivErrorOrderId.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            ivErrorOrderId.setVisibility(View.GONE);
        }

        if (edtDescription.getText().toString().trim().isEmpty()) {
            ivErrorDescription.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            ivErrorDescription.setVisibility(View.GONE);
        }

        if (selectedIssueType.isEmpty()) {
            ivErrorIssueType.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            ivErrorIssueType.setVisibility(View.GONE);
        }

        if (selectedContactMethod.isEmpty()) {
            ivErrorContactMethod.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            ivErrorContactMethod.setVisibility(View.GONE);
        }
        
        return isValid;
    }

    private void setupImageRecyclerView() {
        imageAdapter = new UploadImageAdapter(selectedImages, position -> {
            selectedImages.remove(position);
            imageAdapter.notifyItemRemoved(position);
            imageAdapter.notifyItemRangeChanged(position, selectedImages.size());
            updateImagesVisibility();
        });
        rvImages.setAdapter(imageAdapter);
        updateImagesVisibility();
    }

    private void onImagePicked(Uri uri) {
        selectedImages.add(uri);
        imageAdapter.notifyItemInserted(selectedImages.size() - 1);
        updateImagesVisibility();
    }

    private void updateImagesVisibility() {
        rvImages.setVisibility(selectedImages.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void replaceFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.container7, fragment)
                .addToBackStack(null)
                .commit();
    }
}
