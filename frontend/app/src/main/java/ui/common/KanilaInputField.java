package ui.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.frontend.R;

public class KanilaInputField extends LinearLayout {

    private TextView tvLabel;
    private LinearLayout containerInput;
    private ImageView ivLeadingIcon;
    private EditText etInput;
    private ImageView ivTrailingIcon;
    private Button btnAction;

    private int mode = 0; // default text
    private PasswordToggleHelper passwordToggleHelper;

    public KanilaInputField(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public KanilaInputField(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public KanilaInputField(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.view_kanila_input_field, this, true);

        tvLabel = findViewById(R.id.tvLabel);
        containerInput = findViewById(R.id.containerInput);
        ivLeadingIcon = findViewById(R.id.ivLeadingIcon);
        etInput = findViewById(R.id.etInput);
        ivTrailingIcon = findViewById(R.id.ivTrailingIcon);
        btnAction = findViewById(R.id.btnAction);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.KanilaInputField);

            String label = a.getString(R.styleable.KanilaInputField_kanilaLabel);
            setLabelText(label);

            String hint = a.getString(R.styleable.KanilaInputField_kanilaHint);
            setHintText(hint);

            mode = a.getInt(R.styleable.KanilaInputField_kanilaMode, 0);
            applyMode(mode);

            boolean enabled = a.getBoolean(R.styleable.KanilaInputField_kanilaEnabled, true);
            setDisabledState(!enabled);

            String actionText = a.getString(R.styleable.KanilaInputField_kanilaActionText);
            setActionButtonText(actionText);

            boolean showActionButton = a.getBoolean(R.styleable.KanilaInputField_kanilaShowActionButton, mode == 4);
            if (btnAction != null) btnAction.setVisibility(showActionButton ? VISIBLE : GONE);

            boolean showLeading = a.getBoolean(R.styleable.KanilaInputField_kanilaShowLeadingIcon, mode == 3);
            if (ivLeadingIcon != null) ivLeadingIcon.setVisibility(showLeading ? VISIBLE : GONE);
            
            int leadingIconResId = a.getResourceId(R.styleable.KanilaInputField_kanilaLeadingIcon, 0);
            if (leadingIconResId != 0) {
                if (ivLeadingIcon != null) {
                    ivLeadingIcon.setImageResource(leadingIconResId);
                    ivLeadingIcon.setVisibility(VISIBLE);
                }
            } else if (mode == 3) {
                if (ivLeadingIcon != null) ivLeadingIcon.setImageResource(R.drawable.ic_search);
            }

            a.recycle();
        }

        setupListeners();
    }

    private void applyMode(int mode) {
        switch (mode) {
            case 1: // password
                etInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                if (ivTrailingIcon != null) {
                    ivTrailingIcon.setVisibility(VISIBLE);
                    ivTrailingIcon.setImageResource(R.drawable.ic_eye);
                    passwordToggleHelper = new PasswordToggleHelper();
                    ivTrailingIcon.setOnClickListener(v -> passwordToggleHelper.togglePasswordVisibility(etInput, ivTrailingIcon));
                }
                break;
            case 2: // multiline
                etInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                etInput.setGravity(Gravity.TOP);
                etInput.setMinHeight(getResources().getDimensionPixelSize(R.dimen.input_multiline_min_height));
                break;
            case 3: // search
                etInput.setInputType(InputType.TYPE_CLASS_TEXT);
                if (ivLeadingIcon != null) {
                    ivLeadingIcon.setVisibility(VISIBLE);
                    ivLeadingIcon.setImageResource(R.drawable.ic_search);
                }
                break;
            case 4: // actionButton
                if (btnAction != null) btnAction.setVisibility(VISIBLE);
                break;
            default:
                etInput.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
        }
    }

    private void setupListeners() {
        etInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                setFocusedState();
            } else {
                setDefaultState();
            }
        });
    }

    public void setDefaultState() {
        containerInput.setBackgroundResource(R.drawable.bg_kanila_input_default);
    }

    public void setFocusedState() {
        containerInput.setBackgroundResource(R.drawable.bg_kanila_input_focused);
    }

    public void setErrorState(String message) {
        containerInput.setBackgroundResource(R.drawable.bg_kanila_input_error);
    }

    public void setSuccessState(String message) {
        containerInput.setBackgroundResource(R.drawable.bg_kanila_input_success);
    }

    public void setDisabledState(boolean disabled) {
        setEnabled(!disabled);
        etInput.setEnabled(!disabled);
        if (btnAction != null) btnAction.setEnabled(!disabled);
        if (disabled) {
            containerInput.setBackgroundResource(R.drawable.bg_kanila_input_disabled);
        } else {
            setDefaultState();
        }
    }

    public void clearMessage() {
        setDefaultState();
    }

    public String getTextValue() {
        return etInput.getText().toString();
    }

    public void setTextValue(String value) {
        etInput.setText(value);
    }

    public void setHintText(String hint) {
        etInput.setHint(hint);
    }

    public void setLabelText(String label) {
        if (tvLabel != null) {
            if (label != null && !label.isEmpty()) {
                tvLabel.setText(label);
                tvLabel.setVisibility(VISIBLE);
            } else {
                tvLabel.setVisibility(GONE);
            }
        }
    }

    public void setActionButtonText(String text) {
        if (btnAction != null) btnAction.setText(text);
    }

    public void setOnActionClickListener(OnClickListener listener) {
        if (btnAction != null) btnAction.setOnClickListener(listener);
    }

    public void setLeadingIcon(int resId) {
        if (ivLeadingIcon != null) {
            if (resId != 0) {
                ivLeadingIcon.setImageResource(resId);
                ivLeadingIcon.setVisibility(VISIBLE);
            } else {
                ivLeadingIcon.setVisibility(GONE);
            }
        }
    }

    public void setInputType(int inputType) {
        etInput.setInputType(inputType);
    }

    public void showMessage(String message) {
        // Method kept for compatibility, but no message view to show
    }

    public void hideMessage() {
        // Method kept for compatibility, but no message view to hide
    }

    public void setTrailingIcon(int resId) {
        if (ivTrailingIcon != null) {
            if (resId != 0) {
                ivTrailingIcon.setImageResource(resId);
                ivTrailingIcon.setVisibility(VISIBLE);
            } else {
                ivTrailingIcon.setVisibility(GONE);
            }
        }
    }

    public void setTrailingIconTint(int color) {
        if (ivTrailingIcon != null) ivTrailingIcon.setColorFilter(color);
    }

    public String getText() {
        return etInput.getText().toString();
    }

    public EditText getEditText() {
        return etInput;
    }
}
