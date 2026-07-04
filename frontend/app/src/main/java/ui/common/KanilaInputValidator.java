package ui.common;

import android.content.Context;
import android.util.Patterns;
import com.example.frontend.R;

public class KanilaInputValidator {

    public static boolean isRequiredValid(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isEmailValid(String value) {
        return value != null && Patterns.EMAIL_ADDRESS.matcher(value).matches();
    }

    public static boolean isPhoneValid(String value) {
        return value != null && Patterns.PHONE.matcher(value).matches();
    }

    public static boolean isPasswordValid(String value) {
        return value != null && value.length() >= 8;
    }

    public static String getRequiredError(Context context) {
        return context.getString(R.string.error_required_field);
    }

    public static String getEmailError(Context context) {
        return context.getString(R.string.error_invalid_email);
    }

    public static String getPhoneError(Context context) {
        return context.getString(R.string.error_invalid_phone);
    }

    public static String getPasswordError(Context context) {
        return context.getString(R.string.error_password_short);
    }
}