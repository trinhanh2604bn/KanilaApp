package ui.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;

public class SwipeToDeleteDecorator {

    private final Paint paint = new Paint();
    private final Context context;

    public SwipeToDeleteDecorator(Context context) {
        this.context = context;
    }

    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (dX < 0) { // Swiping left
            View itemView = viewHolder.itemView;
            float height = (float) itemView.getBottom() - (float) itemView.getTop();
            float width = height / 3;

            // Draw red background
            paint.setColor(Color.parseColor("#FFE5E5")); // Muted red/pinkish to match Kanila theme
            RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
            c.drawRect(background, paint);

            // Draw trash icon
            Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_trash);
            if (icon != null) {
                int margin = (int) (height - width) / 2;
                int iconTop = itemView.getTop() + (int) (height - width) / 2;
                int iconLeft = itemView.getRight() - margin - (int) width;
                int iconRight = itemView.getRight() - margin;
                int iconBottom = iconTop + (int) width;

                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                icon.setTint(ContextCompat.getColor(context, R.color.error));
                icon.draw(c);
            }
        }
    }
}
