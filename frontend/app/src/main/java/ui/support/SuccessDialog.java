package ui.support;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.example.frontend.R;
import com.google.android.material.button.MaterialButton;

public class SuccessDialog extends Dialog {

    private final String title;
    private final String message;
    private final String buttonText;
    private Runnable onConfirmListener;

    public SuccessDialog(@NonNull Context context, String title, String message, String buttonText) {
        super(context);
        this.title = title;
        this.message = message;
        this.buttonText = buttonText;
    }

    public void setOnConfirmListener(Runnable listener) {
        this.onConfirmListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_success);
        
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvMsg = findViewById(R.id.tvMessage);
        MaterialButton btnConfirm = findViewById(R.id.btnConfirm);

        tvTitle.setText(title);
        tvMsg.setText(message);
        btnConfirm.setText(buttonText);

        btnConfirm.setOnClickListener(v -> {
            dismiss();
            if (onConfirmListener != null) {
                onConfirmListener.run();
            }
        });

        findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());
    }
}
