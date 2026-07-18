package com.example.frontend.feature.ar.ui;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class LipColorRenderer {

    public enum FinishType {
        MATTE, SATIN, GLOSS, TINT;

        public static FinishType fromString(String finish) {
            if (finish == null) return MATTE;
            try {
                return valueOf(finish.toUpperCase());
            } catch (IllegalArgumentException e) {
                return MATTE; // Fallback
            }
        }
    }

    public Paint getLipPaint(String hexColor, FinishType finish, float opacity) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int color = ShadeColorParser.parseOrFallback(hexColor);
        
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        int a = (int) (255 * Math.max(0, Math.min(1, opacity)));
        
        paint.setColor(Color.argb(a, r, g, b));
        paint.setStyle(Paint.Style.FILL);
        
        if (finish == null) finish = FinishType.MATTE;
        
        switch (finish) {
            case GLOSS:
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
                break;
            case TINT:
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
                break;
            case MATTE:
            case SATIN:
            default:
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
                break;
        }

        return paint;
    }
}
