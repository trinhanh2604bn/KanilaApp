package com.example.frontend.ui.common;

import android.view.View;
import android.widget.TextView;
import com.example.frontend.R;

public class NotLoggedInStateHelper {

    public static void bindDefault(View root, View.OnClickListener loginListener, View.OnClickListener registerListener) {
        setLoginClick(root, loginListener);
        setRegisterClick(root, registerListener);
        showGuestAction(root, false);
    }

    public static void bindWithGuestAction(View root, View.OnClickListener loginListener, View.OnClickListener registerListener, View.OnClickListener guestListener) {
        setLoginClick(root, loginListener);
        setRegisterClick(root, registerListener);
        setGuestClick(root, guestListener);
        showGuestAction(root, true);
    }

    public static void setTitle(View root, String title) {
        TextView tv = root.findViewById(R.id.tvNotLoggedInTitle);
        if (tv != null) {
            tv.setText(title);
        }
    }

    public static void setDescription(View root, String description) {
        TextView tv = root.findViewById(R.id.tvNotLoggedInDescription);
        if (tv != null) {
            tv.setText(description);
        }
    }

    public static void setPrimaryActionText(View root, String text) {
        View btn = root.findViewById(R.id.btnLoginNow);
        if (btn instanceof TextView) {
            ((TextView) btn).setText(text);
        }
    }

    public static void showGuestAction(View root, boolean show) {
        View view = root.findViewById(R.id.tvContinueShopping);
        if (view != null) {
            view.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public static void setLoginClick(View root, View.OnClickListener listener) {
        View view = root.findViewById(R.id.btnLoginNow);
        if (view != null) {
            view.setOnClickListener(listener);
        }
    }

    public static void setRegisterClick(View root, View.OnClickListener listener) {
        View view = root.findViewById(R.id.tvCreateAccount);
        if (view != null) {
            view.setOnClickListener(listener);
        }
    }

    public static void setGuestClick(View root, View.OnClickListener listener) {
        View view = root.findViewById(R.id.tvContinueShopping);
        if (view != null) {
            view.setOnClickListener(listener);
        }
    }
}
