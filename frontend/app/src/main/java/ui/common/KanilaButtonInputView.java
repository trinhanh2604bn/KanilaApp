package ui.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.frontend.R;

public class KanilaButtonInputView extends LinearLayout {

    private TextView tvLabel;
    private LinearLayout containerInput;
    private ImageView ivLeadingIcon;
    private EditText etInput;
    private ImageButton ibTrailingIcon;
    private Button btnAction;
    private TextView tvMessage;

    private int mode = 0; // normal
    private boolean isPasswordVisible = false;
    private boolean isRequired = false;

    private int colorPrimary;
    private int colorError;
    private int colorSuccess;
    private int colorBorder;
    private int colorBackground;
    private int colorBackgroundSub;

    public KanilaButtonInputView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public KanilaButtonInputView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public KanilaButtonInputView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.view_kanila_button_input, this, true);

        tvLabel = findViewById(R.id.tvKbiLabel);
        containerInput = findViewById(R.id.containerKbiInput);
        ivLeadingIcon = findViewById(R.id.ivKbiLeadingIcon);
        etInput = findViewById(R.id.etKbiInput);
        ibTrailingIcon = findViewById(R.id.ibKbiTrailingIcon);
        btnAction = findViewById(R.id.btnKbiAction);
        tvMessage = findViewById(R.id.tvKbiMessage);

        colorPrimary = ContextCompat.getColor(context, R.color.primary);
        colorError = ContextCompat.getColor(context, R.color.error);
        colorSuccess = ContextCompat.getColor(context, R.color.success);
        colorBorder = ContextCompat.getColor(context, R.color.border_divider);
        colorBackground = ContextCompat.getColor(context, R.color.background_main);
        colorBackgroundSub = ContextCompat.getColor(context, R.color.background_sub);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.KanilaButtonInputView);

            setLabel(a.getString(R.styleable.KanilaButtonInputView_kbiLabel));
            setHint(a.getString(R.styleable.KanilaButtonInputView_kbiHint));
            
            String helperText = a.getString(R.styleable.KanilaButtonInputView_kbiHelperText);
            if (helperText != null) setHelperText(helperText);
            
            mode = a.getInt(R.styleable.KanilaButtonInputView_kbiMode, 0);
            isRequired = a.getBoolean(R.styleable.KanilaButtonInputView_kbiRequired, false);
            
            boolean enabled = a.getBoolean(R.styleable.KanilaButtonInputView_kbiEnabled, true);
            setInputEnabled(enabled);

            setActionText(a.getString(R.styleable.KanilaButtonInputView_kbiActionText));
            
            boolean showActionButton = a.getBoolean(R.styleable.KanilaButtonInputView_kbiShowActionButton, mode == 3);
            btnAction.setVisibility(showActionButton ? VISIBLE : GONE);

            boolean showLabel = a.getBoolean(R.styleable.KanilaButtonInputView_kbiShowLabel, true);
            if (!showLabel) tvLabel.setVisibility(GONE);

            boolean showLeading = a.getBoolean(R.styleable.KanilaButtonInputView_kbiShowLeadingIcon, mode == 2);
            ivLeadingIcon.setVisibility(showLeading ? VISIBLE : GONE);

            boolean showTrailing = a.getBoolean(R.styleable.KanilaButtonInputView_kbiShowTrailingIcon, mode == 1);
            ibTrailingIcon.setVisibility(showTrailing ? VISIBLE : GONE);

            int maxLines = a.getInt(R.styleable.KanilaButtonInputView_kbiMaxLines, -1);
            if (maxLines > 0) etInput.setMaxLines(maxLines);

            int inputType = a.getInt(R.styleable.KanilaButtonInputView_kbiInputType, -1);
            if (inputType != -1) etInput.setInputType(inputType);

            String errorText = a.getString(R.styleable.KanilaButtonInputView_kbiErrorText);
            if (errorText != null) showErrorState(errorText);

            String successText = a.getString(R.styleable.KanilaButtonInputView_kbiSuccessText);
            if (successText != null) showSuccessState(successText);

            applyMode(mode);

            a.recycle();
        }

        setupListeners();
        showDefaultState();
    }

    private void applyMode(int mode) {
        switch (mode) {
            case 1: // password
                etInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ibTrailingIcon.setVisibility(VISIBLE);
                ibTrailingIcon.setImageResource(R.drawable.ic_eye);
                ibTrailingIcon.setOnClickListener(v -> togglePasswordVisibility());
                break;
            case 2: // search
                ivLeadingIcon.setVisibility(VISIBLE);
                ivLeadingIcon.setImageResource(R.drawable.ic_search);
                etInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        ibTrailingIcon.setVisibility(s.length() > 0 ? VISIBLE : GONE);
                        ibTrailingIcon.setImageResource(R.drawable.ic_close);
                        ibTrailingIcon.setOnClickListener(v -> etInput.setText(""));
                    }
                    @Override
                    public void afterTextChanged(Editable s) {}
                });
                break;
            case 3: // action
                btnAction.setVisibility(VISIBLE);
                break;
            case 4: // multiline
                etInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                etInput.setGravity(Gravity.TOP);
                etInput.setMinHeight(getResources().getDimensionPixelSize(R.dimen.kbi_multiline_min_height));
                break;
        }
    }

    private void setupListeners() {
        etInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) showFocusedState();
            else showDefaultState();
        });
    }

    private void togglePasswordVisibility() {
        int selectionStart = etInput.getSelectionStart();
        int selectionEnd = etInput.getSelectionEnd();
        if (isPasswordVisible) {
            etInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
            ibTrailingIcon.setImageResource(R.drawable.ic_eye);
        } else {
            etInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            ibTrailingIcon.setImageResource(R.drawable.ic_eye_off);
        }
        isPasswordVisible = !isPasswordVisible;
        etInput.setSelection(selectionStart, selectionEnd);
    }

    public void showDefaultState() {
        updateBackground(colorBackground, colorBorder);
        tvMessage.setVisibility(GONE);
    }

    public void showFocusedState() {
        updateBackground(colorBackground, colorPrimary);
    }

    public void showErrorState(String message) {
        updateBackground(colorBackground, colorError);
        tvMessage.setText(message);
        tvMessage.setTextColor(colorError);
        tvMessage.setVisibility(VISIBLE);
    }

    public void showSuccessState(String message) {
        updateBackground(colorBackground, colorSuccess);
        tvMessage.setText(message);
        tvMessage.setTextColor(colorSuccess);
        tvMessage.setVisibility(VISIBLE);
    }

    public void showDisabledState() {
        updateBackground(colorBackgroundSub, colorBorder);
        etInput.setEnabled(false);
        btnAction.setEnabled(false);
        ibTrailingIcon.setEnabled(false);
    }

    private void updateBackground(int bgColor, int strokeColor) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(bgColor);
        gd.setCornerRadius(getResources().getDimension(R.dimen.kbi_radius));
        int strokeWidth = (int) (1.5f * getResources().getDisplayMetrics().density);
        gd.setStroke(strokeWidth, strokeColor);
        containerInput.setBackground(gd);
    }

    // Public API
    public String getText() { return etInput.getText().toString(); }
    public void setText(String value) { etInput.setText(value); }
    public void setHint(String hint) { etInput.setHint(hint); }
    public void setLabel(String label) {
        if (label != null && !label.isEmpty()) {
            if (isRequired) {
                tvLabel.setText(label + " *");
            } else {
                tvLabel.setText(label);
            }
            tvLabel.setVisibility(VISIBLE);
        } else {
            tvLabel.setVisibility(GONE);
        }
    }
    public void setHelperText(String message) {
        tvMessage.setText(message);
        tvMessage.setTextColor(colorBorder);
        tvMessage.setVisibility(VISIBLE);
    }
    public void setActionText(String text) { btnAction.setText(text); }
    public void setError(String message) { showErrorState(message); }
    public void setSuccess(String message) { showSuccessState(message); }
    public void clearState() { showDefaultState(); }
    public void setInputEnabled(boolean enabled) {
        if (enabled) {
            etInput.setEnabled(true);
            btnAction.setEnabled(true);
            ibTrailingIcon.setEnabled(true);
            showDefaultState();
        } else {
            showDisabledState();
        }
    }
    public void setRequired(boolean required) { 
        this.isRequired = required;
        // Refresh label to show/hide asterisk
        CharSequence currentLabel = tvLabel.getText();
        if (currentLabel != null && currentLabel.length() > 0) {
            String labelStr = currentLabel.toString();
            if (labelStr.endsWith(" *")) {
                labelStr = labelStr.substring(0, labelStr.length() - 2);
            }
            setLabel(labelStr);
        }
    }
    public void setOnActionClickListener(OnClickListener listener) { btnAction.setOnClickListener(listener); }
    public EditText getEditText() { return etInput; }
    public Button getActionButton() { return btnAction; }
}
