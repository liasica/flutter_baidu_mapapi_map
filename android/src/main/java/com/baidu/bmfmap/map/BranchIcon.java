package com.baidu.bmfmap.map;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.graphics.Typeface;
import android.content.res.AssetManager;

import androidx.annotation.NonNull;

import java.util.Map;

public class BranchIcon {
    // Local Colors
    private static final int v72Color = Color.argb(255, 0, 223, 202);
    private static final int v60Color = Color.argb(255, 0, 191, 255);
    private static final int wrapperColor = Color.argb(255, 255, 255, 255);
    private static final int shadowColor = Color.argb(16, 16, 29, 52);
    private static final int ovalColor = Color.argb(255, 46, 53, 63);
    // Local Shadows
    private static final PaintCodeShadow shadow = CacheForCanvas.shadow.get(shadowColor, 0f, 4f, 8f);

    public enum BatteryModel {
        V72("72V"),
        V60("60V");

        private final String value;

        BatteryModel(String bm) {
            value = bm;
        }

        public String getValue() {
            return value;
        }

        static BatteryModel fromString(String bm) {
            if (bm.equals("72V")) {
                return BatteryModel.V72;
            }
            return BatteryModel.V60;
        }

        public int getColor() {
            if (this == V72) {
                return v72Color;
            }
            return v60Color;
        }
    }

    public static class Model {
        public static int DESIGIN_WIDTH = 90;
        public static int DESIGIN_HEIGHT = 104;
        private final BatteryModel batteryModel;

        public BatteryModel getBatteryModel() {
            return batteryModel;
        }

        private final int number;
        public int getNumber() {
            return number;
        }
        private final double scale;

        public double getScale() {
            return scale;
        }

        private final double width;
        public double getWidth() {
            return width;
        }

        private final double height;
        public double getHeight() {
            return height;
        }

        public Model(String model, int number, double scale) {
            this.batteryModel = BatteryModel.fromString(model);
            this.number = number;
            this.scale = scale;
            this.width = DESIGIN_WIDTH * scale;
            this.height = DESIGIN_HEIGHT * scale;
        }
    }

    public static Model fromMap(Map<String, Object> map) {
        Integer number = (Integer) map.get("number");
        Double scale = (Double) map.get("scale");

        return new Model(
                (String) map.get("batteryModel"),
                number == null ? 0 : number,
                scale == null ? 0.0 : scale
        );
    }

    private static class GlobalCache {
        static PorterDuffXfermode blendModeSourceIn = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        static Typeface lucidaGrande = null;
        
        private static void initializeTypefaceBuilders(AssetManager assetManager) {
            if (GlobalCache.lucidaGrande == null) {
                lucidaGrande = Typeface.createFromAsset(assetManager, "Lucida-Grande-Regular.ttf");
            }
        }
    }
    
    // Resizing Behavior
    public enum ResizingBehavior {
        AspectFit, //!< The content is proportionally resized to fit into the target rectangle.
        AspectFill, //!< The content is proportionally resized to completely fill the target rectangle.
        Stretch, //!< The content is stretched to match the entire target rectangle.
        Center, //!< The content is centered in the target rectangle, but it is NOT resized.
    }
    
    // Canvas Drawings
    // Tab
    
    private static class CacheForCanvas {
        private static final Paint paint = new Paint();
        private static final Paint shadowPaint = new Paint();
        private static final PaintCodeShadow shadow = new PaintCodeShadow();
        private static final RectF originalFrame = new RectF(0f, 0f, 90f, 104f);
        private static final RectF resizedFrame = new RectF();
        private static final RectF bezierRect = new RectF();
        private static final Path bezierPath = new Path();
        private static final RectF circleRect = new RectF();
        private static final Path circlePath = new Path();
        private static final RectF ovalRect = new RectF();
        private static final Path ovalPath = new Path();
        private static final RectF batteryModelRect = new RectF();
        private static final TextPaint batteryModelTextPaint = new TextPaint();
        private static final PaintCodeStaticLayout batteryModelStaticLayout = new PaintCodeStaticLayout();
        private static final RectF numberRect = new RectF();
        private static final TextPaint numberTextPaint = new TextPaint();
        private static final PaintCodeStaticLayout numberStaticLayout = new PaintCodeStaticLayout();
    }
    
    public static void draw(Model branchModel, Canvas canvas, Context context) {
        BranchIcon.draw(branchModel, canvas, context, new RectF(0f, 0f, (float) branchModel.getWidth(), (float) branchModel.getHeight()), ResizingBehavior.AspectFit);
    }
    
    public static void draw(Model branchModel, Canvas canvas, Context context, RectF targetFrame, ResizingBehavior resizing) {
        // General Declarations
        Paint paint = CacheForCanvas.paint;
        GlobalCache.initializeTypefaceBuilders(context.getAssets());
        
        // Resize to Target Frame
        canvas.save();
        RectF resizedFrame = CacheForCanvas.resizedFrame;
        BranchIcon.resizingBehaviorApply(resizing, CacheForCanvas.originalFrame, targetFrame, resizedFrame);
        canvas.translate(resizedFrame.left, resizedFrame.top);
        canvas.scale(resizedFrame.width() / 90f, resizedFrame.height() / 104f);
        
        // IconGroup
        {
            // Bezier
            RectF bezierRect = CacheForCanvas.bezierRect;
            bezierRect.set(8f, 4f, 82f, 92f);
            Path bezierPath = getBezierPath();

            paint.reset();
            paint.setFlags(Paint.ANTI_ALIAS_FLAG);
            canvas.saveLayerAlpha(null, 255, Canvas.ALL_SAVE_FLAG);
            {
                canvas.translate(shadow.dx, shadow.dy);
                
                Paint shadowPaint = CacheForCanvas.shadowPaint;
                shadowPaint.set(paint);
                shadow.setBlurOfPaint(shadowPaint);
                canvas.drawPath(bezierPath, shadowPaint);
                shadowPaint.setXfermode(GlobalCache.blendModeSourceIn);
                canvas.saveLayer(null, shadowPaint, Canvas.ALL_SAVE_FLAG);
                {
                    canvas.drawColor(shadow.color);
                }
                canvas.restore();
            }
            canvas.restore();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(wrapperColor);
            canvas.drawPath(bezierPath, paint);
            
            // Circle
            RectF circleRect = CacheForCanvas.circleRect;
            circleRect.set(12f, 8f, 78f, 74f);
            Path circlePath = CacheForCanvas.circlePath;
            circlePath.reset();
            circlePath.addOval(circleRect, Path.Direction.CW);
            
            paint.reset();
            paint.setFlags(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(branchModel.getBatteryModel().getColor());
            canvas.drawPath(circlePath, paint);
            
            // Oval
            RectF ovalRect = CacheForCanvas.ovalRect;
            ovalRect.set(14.84f, 50f, 75.16f, 74f);
            Path ovalPath = CacheForCanvas.ovalPath;
            ovalPath.reset();
            ovalPath.moveTo(75.16f, 54.4f);
            ovalPath.cubicTo(70.02f, 65.95f, 58.45f, 74f, 45f, 74f);
            ovalPath.cubicTo(31.55f, 74f, 19.98f, 65.95f, 14.84f, 54.4f);
            ovalPath.cubicTo(23.54f, 51.62f, 33.89f, 50f, 45f, 50f);
            ovalPath.cubicTo(56.11f, 50f, 66.46f, 51.62f, 75.16f, 54.4f);
            ovalPath.close();
            
            paint.reset();
            paint.setFlags(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(ovalColor);
            canvas.drawPath(ovalPath, paint);
        }
        
        // Battery Model
        RectF batteryModelRect = CacheForCanvas.batteryModelRect;
        batteryModelRect.set(27f, 56f, 63f, 70f);
        TextPaint batteryModelTextPaint = CacheForCanvas.batteryModelTextPaint;
        batteryModelTextPaint.reset();
        batteryModelTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        batteryModelTextPaint.setColor(Color.WHITE);
        batteryModelTextPaint.setTypeface(GlobalCache.lucidaGrande);
        batteryModelTextPaint.setTextSize(16f);
        StaticLayout batteryModelStaticLayout = CacheForCanvas.batteryModelStaticLayout.get((int) batteryModelRect.width(), Layout.Alignment.ALIGN_CENTER, branchModel.getBatteryModel().value, batteryModelTextPaint);
        canvas.save();
        canvas.clipRect(batteryModelRect);
        canvas.translate(batteryModelRect.left, batteryModelRect.top + (batteryModelRect.height() - batteryModelStaticLayout.getHeight()) / 2f);
        batteryModelStaticLayout.draw(canvas);
        canvas.restore();
        
        // Number
        RectF numberRect = CacheForCanvas.numberRect;
        numberRect.set(12f, 14f, 78f, 50f);
        TextPaint numberTextPaint = CacheForCanvas.numberTextPaint;
        numberTextPaint.reset();
        numberTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        numberTextPaint.setColor(Color.WHITE);
        numberTextPaint.setTypeface(GlobalCache.lucidaGrande);
        numberTextPaint.setTextSize(30f);
        StaticLayout numberStaticLayout = CacheForCanvas.numberStaticLayout.get((int) numberRect.width(), Layout.Alignment.ALIGN_CENTER, String.valueOf(branchModel.number), numberTextPaint);
        canvas.save();
        canvas.clipRect(numberRect);
        canvas.translate(numberRect.left, numberRect.top + (numberRect.height() - numberStaticLayout.getHeight()) / 2f);
        numberStaticLayout.draw(canvas);
        canvas.restore();
        
        canvas.restore();
    }

    @NonNull
    private static Path getBezierPath() {
        Path bezierPath = CacheForCanvas.bezierPath;
        bezierPath.reset();
        bezierPath.moveTo(71.16f, 14.68f);
        bezierPath.cubicTo(56.71f, 0.44f, 33.28f, 0.44f, 18.84f, 14.68f);
        bezierPath.cubicTo(6.77f, 26.57f, 4.78f, 44.64f, 12.88f, 58.56f);
        bezierPath.cubicTo(14.47f, 61.3f, 16.46f, 63.88f, 18.84f, 66.23f);
        bezierPath.lineTo(45f, 92f);
        bezierPath.lineTo(71.16f, 66.23f);
        bezierPath.cubicTo(73.54f, 63.88f, 75.53f, 61.3f, 77.12f, 58.56f);
        bezierPath.cubicTo(85.22f, 44.64f, 83.23f, 26.57f, 71.16f, 14.68f);
        bezierPath.close();
        return bezierPath;
    }


    // Resizing Behavior
    public static void resizingBehaviorApply(ResizingBehavior behavior, RectF rect, RectF target, RectF result) {
        if (rect.equals(target) || target == null) {
            result.set(rect);
            return;
        }
        
        if (behavior == ResizingBehavior.Stretch) {
            result.set(target);
            return;
        }
        
        float xRatio = Math.abs(target.width() / rect.width());
        float yRatio = Math.abs(target.height() / rect.height());
        float scale = 0f;
        
        switch (behavior) {
            case AspectFit: {
                scale = Math.min(xRatio, yRatio);
                break;
            }
            case AspectFill: {
                scale = Math.max(xRatio, yRatio);
                break;
            }
            case Center: {
                scale = 1f;
                break;
            }
        }
        
        float newWidth = Math.abs(rect.width() * scale);
        float newHeight = Math.abs(rect.height() * scale);
        result.set(target.centerX() - newWidth / 2,
            target.centerY() - newHeight / 2,
            target.centerX() + newWidth / 2,
            target.centerY() + newHeight / 2);
    }
    
    
}
class PaintCodeShadow {
    int color;
    float dx, dy;
    private float radius;
    private BlurMaskFilter blurMaskFilter;
    
    PaintCodeShadow() {
        
    }
    
    PaintCodeShadow(int color, float dx, float dy, float radius) {
        this.get(color, dx, dy, radius);
    }
    
    PaintCodeShadow get(int color, float dx, float dy, float radius) {
        this.color = color;
        this.dx = dx;
        this.dy = dy;
        
        if (this.radius != radius) {
            this.blurMaskFilter = null;
            this.radius = radius;
        }
        
        return this;
    }
    
    void setBlurOfPaint(Paint paint) {
        if (this.radius <= 0)
            return;
        
        if (this.blurMaskFilter == null)
            this.blurMaskFilter = new BlurMaskFilter(this.radius, BlurMaskFilter.Blur.NORMAL);
        
        paint.setMaskFilter(this.blurMaskFilter);
    }
}


class PaintCodeStaticLayout {
    private StaticLayout layout;
    private int width;
    private Layout.Alignment alignment;
    private CharSequence source;
    private TextPaint paint;
    
    StaticLayout get(int width, Layout.Alignment alignment, CharSequence source, TextPaint paint) {
        if (this.layout == null || this.width != width || this.alignment != alignment || !this.source.equals(source) || !this.paint.equals(paint)) {
            this.width = width;
            this.alignment = alignment;
            this.source = source;
            this.paint = paint;
            this.layout = new StaticLayout(source, paint, width, alignment, 1, 0, false);
        }
        return this.layout;
    }
}
