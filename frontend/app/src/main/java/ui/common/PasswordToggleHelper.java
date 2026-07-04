package ui.common;

import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;
import com.example.frontend.R;

public class PasswordToggleHelper {
    private boolean isPasswordVisible = false;

    public void togglePasswordVisibility(EditText etInput, ImageView ivToggle) {
        if (isPasswordVisible) {
            etInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            ivToggle.setImageResource(R.drawable.ic_eye);
        } else {
            etInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            ivToggle.setImageResource(R.drawable.ic_eye_off);
        }
        isPasswordVisible = !isPasswordVisible;
        etInput.setSelection(etInput.getText().length());
    }
}