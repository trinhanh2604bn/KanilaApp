package com.example.frontend.feature.ar.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import com.example.frontend.feature.ar.domain.LandmarkPoint;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import java.util.List;

public class LipOverlayView extends View {

    private Path lipPath;
    private Paint lipPaint;
    private Paint debugPaint;
    private List<LandmarkPoint> rawPoints;
    private boolean isDebugMode = false;

    public LipOverlayView(Context context) {
        super(context);
        init();
    }

    public LipOverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Hardware acceleration ignores BlurMaskFilter on some devices/OS versions,
        // so we need to set the layer type to software for the mask filter to work.
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        lipPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lipPaint.setStyle(Paint.Style.FILL);
        
        debugPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        debugPaint.setColor(Color.GREEN);
        debugPaint.setStyle(Paint.Style.FILL);
        debugPaint.setStrokeWidth(4f);
    }

    public void setLipPath(Path path, Paint paint, List<LandmarkPoint> points) {
        this.lipPath = path;
        this.lipPaint = paint;
        this.rawPoints = points;
        invalidate();
    }
    
    public void clear() {
        this.lipPath = null;
        this.rawPoints = null;
        invalidate();
    }

    public void setDebugMode(boolean debug) {
        this.isDebugMode = debug;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (lipPath != null && lipPaint != null) {
            canvas.drawPath(lipPath, lipPaint);
        }

        if (isDebugMode && rawPoints != null) {
            for (LandmarkPoint point : rawPoints) {
                canvas.drawCircle(point.x, point.y, 4f, debugPaint);
            }
        }
    }
}
