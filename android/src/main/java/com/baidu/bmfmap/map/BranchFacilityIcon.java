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

public class BranchFacilityIcon {
    // Local Colors
    private static final int wrapperColor = Color.argb(255, 255, 255, 255);
    private static final int shadowColor = Color.argb(16, 16, 29, 52);
    private static final int ovalColor = Color.argb(255, 46, 53, 63);
    // Local Shadows
    private static final PaintCodeShadow shadow = CacheForCanvas.shadow.get(shadowColor, 0f, 4f, 8f);

    public enum BranchFacilityType {
        store("store"),

        v72("v72"),

        v60("v60"),

        rest("rest"),

        $unknown("unknown");

        private final String value;

        BranchFacilityType(String text) {
            value = text;
        }

        public String getValue() {
            return value;
        }

        static BranchFacilityType fromString(String text) {
            for (BranchFacilityType b : BranchFacilityType.values()) {
                if (b.value.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return $unknown;
        }

        public String getIconText() {
            switch (this) {
                case v72:
                    return "72V";
                case v60:
                    return "60V";
                default:
                    return "";
            }
        }
    }

    public static class Model {
        public static int DESIGIN_WIDTH = 90;
        public static int DESIGIN_HEIGHT = 104;
        private final BranchFacilityType branchFacilityType;

        public BranchFacilityType getBranchFacilityType() {
            return branchFacilityType;
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

        private final int color;

        public int getColor() {
            return color;
        }

        public Model(String type, int number, double scale, String color) {
            this.branchFacilityType = BranchFacilityType.fromString(type.toLowerCase());
            this.number = number;
            this.scale = scale;
            this.width = DESIGIN_WIDTH * getScale();
            this.height = DESIGIN_HEIGHT * getScale();
            this.color = Color.parseColor(color);
        }
    }

    public static Model fromMap(Map<String, Object> map) {
        Integer number = (Integer) map.get("number");
        Double scale = (Double) map.get("scale");

        return new Model(
                (String) map.get("type"),
                number == null ? 0 : number,
                scale == null ? 0.0 : scale,
                (String) map.get("color")
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
        private static final RectF branchFacilityTypeRect = new RectF();
        private static final TextPaint branchFacilityTypeTextPaint = new TextPaint();
        private static final PaintCodeStaticLayout branchFacilityTypeStaticLayout = new PaintCodeStaticLayout();
        private static final RectF numberRect = new RectF();
        private static final TextPaint numberTextPaint = new TextPaint();
        private static final PaintCodeStaticLayout numberStaticLayout = new PaintCodeStaticLayout();
    }

    public static void draw(Model branchModel, Canvas canvas, Context context) {
        BranchFacilityIcon.draw(
                branchModel,
                canvas,
                context,
                new RectF(0f, 0f, (float) branchModel.getWidth(), (float) branchModel.getHeight()),
                ResizingBehavior.AspectFit,
                branchModel.getColor()
        );
    }

    public static void draw(Model branchModel, Canvas canvas, Context context, RectF targetFrame, ResizingBehavior resizing, int iconColor) {
        // General Declarations
        Paint paint = CacheForCanvas.paint;
        GlobalCache.initializeTypefaceBuilders(context.getAssets());

        // Resize to Target Frame
        canvas.save();
        RectF resizedFrame = CacheForCanvas.resizedFrame;
        BranchFacilityIcon.resizingBehaviorApply(resizing, CacheForCanvas.originalFrame, targetFrame, resizedFrame);
        canvas.translate(resizedFrame.left, resizedFrame.top);
        canvas.scale(resizedFrame.width() / 90f, resizedFrame.height() / 104f);

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
        paint.setColor(iconColor);
        canvas.drawPath(circlePath, paint);

        if (branchModel.branchFacilityType == BranchFacilityType.v60 || branchModel.branchFacilityType == BranchFacilityType.v72) {
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

            // Battery Model
            RectF branchFacilityTypeRect = CacheForCanvas.branchFacilityTypeRect;
            branchFacilityTypeRect.set(27f, 56f, 63f, 70f);
            TextPaint branchFacilityTypeTextPaint = CacheForCanvas.branchFacilityTypeTextPaint;
            branchFacilityTypeTextPaint.reset();
            branchFacilityTypeTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            branchFacilityTypeTextPaint.setColor(Color.WHITE);
            branchFacilityTypeTextPaint.setTypeface(GlobalCache.lucidaGrande);
            branchFacilityTypeTextPaint.setTextSize(16f);
            StaticLayout branchFacilityTypeStaticLayout = CacheForCanvas.branchFacilityTypeStaticLayout.get((int) branchFacilityTypeRect.width(), Layout.Alignment.ALIGN_CENTER, branchModel.getBranchFacilityType().getIconText(), branchFacilityTypeTextPaint);
            canvas.save();
            canvas.clipRect(branchFacilityTypeRect);
            canvas.translate(branchFacilityTypeRect.left, branchFacilityTypeRect.top + (branchFacilityTypeRect.height() - branchFacilityTypeStaticLayout.getHeight()) / 2f);
            branchFacilityTypeStaticLayout.draw(canvas);
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
            StaticLayout numberStaticLayout = CacheForCanvas.numberStaticLayout.get((int) numberRect.width(), Layout.Alignment.ALIGN_CENTER, String.valueOf(branchModel.getNumber()), numberTextPaint);
            canvas.save();
            canvas.clipRect(numberRect);
            canvas.translate(numberRect.left, numberRect.top + (numberRect.height() - numberStaticLayout.getHeight()) / 2f);
            numberStaticLayout.draw(canvas);
            canvas.restore();
        }

        if (branchModel.branchFacilityType == BranchFacilityType.rest) {
            // Rest Icon
            RectF restIconRect = new RectF();
            restIconRect.set(23.02f, 22.16f, 66.98f, 59.84f);
            Path restIconPath = getRestBezierPath();
            paint.reset();
            paint.setFlags(Paint.ANTI_ALIAS_FLAG);
            restIconPath.setFillType(Path.FillType.EVEN_ODD);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(wrapperColor);
            canvas.drawPath(restIconPath, paint);
            canvas.restore();
        }

        if (branchModel.branchFacilityType == BranchFacilityType.store) {
            RectF storeIconRect = new RectF();
            storeIconRect.set(25f, 25f, 65f, 60f);
            Path storeIconPath = getStoreIconPath();

            paint.reset();
            paint.setFlags(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(wrapperColor);
            canvas.drawPath(storeIconPath, paint);
            canvas.restore();
        }
    }

    private static @NonNull Path getStoreIconPath() {
        Path storeIconPath = new Path();
        storeIconPath.reset();
        storeIconPath.moveTo(55f, 40.3f);
        storeIconPath.cubicTo(55.24f, 40.62f, 55.53f, 40.92f, 55.85f, 41.2f);
        storeIconPath.cubicTo(56.97f, 42.14f, 58.44f, 42.65f, 60f, 42.65f);
        storeIconPath.cubicTo(60.69f, 42.65f, 61.36f, 42.55f, 61.99f, 42.36f);
        storeIconPath.lineTo(61.99f, 56.3f);
        storeIconPath.cubicTo(61.99f, 58.35f, 60.35f, 60f, 58.33f, 60f);
        storeIconPath.lineTo(31.67f, 60f);
        storeIconPath.cubicTo(29.65f, 60f, 28.01f, 58.35f, 28.01f, 56.3f);
        storeIconPath.lineTo(28.01f, 42.36f);
        storeIconPath.cubicTo(28.64f, 42.55f, 29.31f, 42.65f, 30f, 42.65f);
        storeIconPath.cubicTo(31.56f, 42.65f, 33.03f, 42.14f, 34.14f, 41.2f);
        storeIconPath.cubicTo(34.47f, 40.92f, 34.76f, 40.62f, 35f, 40.3f);
        storeIconPath.cubicTo(35.24f, 40.62f, 35.53f, 40.92f, 35.85f, 41.2f);
        storeIconPath.cubicTo(36.97f, 42.14f, 38.44f, 42.65f, 40f, 42.65f);
        storeIconPath.cubicTo(41.56f, 42.65f, 43.03f, 42.14f, 44.14f, 41.2f);
        storeIconPath.cubicTo(44.47f, 40.92f, 44.76f, 40.62f, 45f, 40.3f);
        storeIconPath.cubicTo(45.24f, 40.62f, 45.53f, 40.92f, 45.85f, 41.2f);
        storeIconPath.cubicTo(46.97f, 42.14f, 48.44f, 42.65f, 50f, 42.65f);
        storeIconPath.cubicTo(51.56f, 42.65f, 53.03f, 42.14f, 54.14f, 41.2f);
        storeIconPath.cubicTo(54.47f, 40.92f, 54.76f, 40.62f, 55f, 40.3f);
        storeIconPath.close();
        storeIconPath.moveTo(39.38f, 44.31f);
        storeIconPath.cubicTo(38.92f, 44.39f, 38.36f, 44.73f, 38.04f, 45.26f);
        storeIconPath.lineTo(40.36f, 46.73f);
        storeIconPath.lineTo(40.46f, 48.67f);
        storeIconPath.lineTo(38.69f, 49.43f);
        storeIconPath.lineTo(36.37f, 47.96f);
        storeIconPath.cubicTo(36f, 48.55f, 35.98f, 49.17f, 36.11f, 49.59f);
        storeIconPath.cubicTo(36.24f, 50.03f, 36.59f, 50.85f, 37.67f, 51.53f);
        storeIconPath.cubicTo(38.76f, 52.21f, 40.57f, 52.36f, 41.62f, 51.46f);
        storeIconPath.cubicTo(41.77f, 51.33f, 42f, 51.18f, 42.35f, 51.39f);
        storeIconPath.cubicTo(42.74f, 51.63f, 50.78f, 57.07f, 50.78f, 57.07f);
        storeIconPath.cubicTo(51.49f, 57.59f, 52.51f, 57.31f, 52.98f, 56.57f);
        storeIconPath.cubicTo(53.44f, 55.82f, 53.25f, 54.77f, 52.47f, 54.36f);
        storeIconPath.cubicTo(52.47f, 54.36f, 50.45f, 53.17f, 48.35f, 51.93f);
        storeIconPath.lineTo(47.82f, 51.62f);
        storeIconPath.cubicTo(45.81f, 50.43f, 43.87f, 49.28f, 43.72f, 49.19f);
        storeIconPath.cubicTo(43.39f, 48.98f, 43.42f, 48.71f, 43.46f, 48.49f);
        storeIconPath.cubicTo(43.77f, 46.74f, 42.68f, 45.49f, 41.78f, 44.89f);
        storeIconPath.cubicTo(40.85f, 44.26f, 39.83f, 44.23f, 39.38f, 44.31f);
        storeIconPath.close();
        storeIconPath.moveTo(51.87f, 55.21f);
        storeIconPath.cubicTo(52.12f, 55.37f, 52.2f, 55.71f, 52.04f, 55.97f);
        storeIconPath.cubicTo(51.88f, 56.23f, 51.54f, 56.31f, 51.28f, 56.15f);
        storeIconPath.cubicTo(51.03f, 55.99f, 50.95f, 55.64f, 51.11f, 55.38f);
        storeIconPath.cubicTo(51.27f, 55.13f, 51.61f, 55.05f, 51.87f, 55.21f);
        storeIconPath.close();
        storeIconPath.moveTo(59.89f, 25f);
        storeIconPath.cubicTo(60.79f, 25f, 61.58f, 25.65f, 61.87f, 26.61f);
        storeIconPath.lineTo(65f, 37.08f);
        storeIconPath.cubicTo(65f, 39.41f, 62.76f, 41.29f, 60f, 41.29f);
        storeIconPath.cubicTo(57.24f, 41.29f, 55f, 39.41f, 55f, 37.08f);
        storeIconPath.cubicTo(55f, 39.41f, 52.76f, 41.29f, 50f, 41.29f);
        storeIconPath.cubicTo(47.24f, 41.29f, 45f, 39.41f, 45f, 37.08f);
        storeIconPath.cubicTo(45f, 39.41f, 42.76f, 41.29f, 40f, 41.29f);
        storeIconPath.cubicTo(37.24f, 41.29f, 35f, 39.41f, 35f, 37.08f);
        storeIconPath.cubicTo(35f, 39.41f, 32.76f, 41.29f, 30f, 41.29f);
        storeIconPath.cubicTo(27.24f, 41.29f, 25f, 39.41f, 25f, 37.08f);
        storeIconPath.lineTo(28.13f, 26.61f);
        storeIconPath.cubicTo(28.41f, 25.65f, 29.21f, 25f, 30.11f, 25f);
        storeIconPath.lineTo(59.89f, 25f);
        storeIconPath.close();
        return storeIconPath;
    }

    private static @NonNull Path getRestBezierPath() {
        Path path = new Path();
        path.reset();
        path.moveTo(45f, 29f);
        path.lineTo(60.7f, 42.66f);
        path.lineTo(60.7f, 56.7f);
        path.cubicTo(60.7f, 58.27f, 59.13f, 59.84f, 57.56f, 59.84f);
        path.lineTo(32.44f, 59.84f);
        path.cubicTo(30.87f, 59.84f, 29.3f, 58.27f, 29.3f, 56.7f);
        path.lineTo(29.3f, 42.66f);
        path.lineTo(45f, 29f);
        path.close();
        path.moveTo(46.57f, 44.14f);
        path.lineTo(43.43f, 44.14f);
        path.cubicTo(41.7f, 44.14f, 40.29f, 45.55f, 40.29f, 47.28f);
        path.lineTo(40.29f, 55.13f);
        path.lineTo(49.71f, 55.13f);
        path.lineTo(49.71f, 47.28f);
        path.cubicTo(49.71f, 45.55f, 48.3f, 44.14f, 46.57f, 44.14f);
        path.close();
        path.moveTo(45f, 22.16f);
        path.cubicTo(45.82f, 22.16f, 46.63f, 22.44f, 47.28f, 23f);
        path.lineTo(52.85f, 27.85f);
        path.lineTo(52.85f, 25.3f);
        path.cubicTo(52.85f, 24.43f, 53.55f, 23.73f, 54.42f, 23.73f);
        path.lineTo(55.99f, 23.73f);
        path.cubicTo(56.86f, 23.73f, 57.56f, 24.43f, 57.56f, 25.3f);
        path.lineTo(57.56f, 31.95f);
        path.lineTo(66.41f, 39.66f);
        path.cubicTo(66.93f, 40.11f, 67.12f, 40.84f, 66.88f, 41.49f);
        path.cubicTo(66.64f, 42.14f, 66.03f, 42.57f, 65.35f, 42.57f);
        path.lineTo(62.99f, 42.57f);
        path.lineTo(45f, 26.92f);
        path.lineTo(27.01f, 42.57f);
        path.lineTo(24.65f, 42.57f);
        path.cubicTo(23.97f, 42.57f, 23.36f, 42.14f, 23.12f, 41.49f);
        path.cubicTo(22.88f, 40.84f, 23.07f, 40.11f, 23.59f, 39.66f);
        path.lineTo(42.72f, 23f);
        path.cubicTo(43.37f, 22.44f, 44.18f, 22.16f, 45f, 22.16f);
        path.close();
        return path;
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
