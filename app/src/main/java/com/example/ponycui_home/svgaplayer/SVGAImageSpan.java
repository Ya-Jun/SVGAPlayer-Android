package com.example.ponycui_home.svgaplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.TextView;

import com.opensource.svgaplayer.SVGADrawable;

import java.lang.reflect.Field;

/**
 * 垂直居中的 ImageSpan
 */
public class SVGAImageSpan extends ImageSpan implements MySVGADrawable.RefreshListener {
    private final String TAG = "SVGAImageSpan";
    private Context context;
    private TextView textView;
    private String filePath;
    private MySVGADrawable mySVGADrawable;
    private long mLastTime;
    private static final int REFRESH_INTERVAL = 60;


    public SVGAImageSpan(Context context, String filePath, TextView textView, Drawable default_drawable) {
        super(default_drawable);
        this.context = context;
        this.textView = textView;
        this.filePath = filePath;

        getSvgaDrawable();
    }

    @Override
    public Drawable getDrawable() {
        return super.getDrawable();
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end,
                       Paint.FontMetricsInt fontMetricsInt) {
        Drawable drawable = getDrawable();
        Rect rect = drawable.getBounds();
        if (fontMetricsInt != null) {
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            int fontHeight = fmPaint.bottom - fmPaint.top;
            int drHeight = rect.bottom - rect.top;

            int top = drHeight / 2 - fontHeight / 4;
            int bottom = drHeight / 2 + fontHeight / 4;

            fontMetricsInt.ascent = -bottom;
            fontMetricsInt.top = -bottom;
            fontMetricsInt.bottom = top;
            fontMetricsInt.descent = top;
        }
        return rect.right;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, Paint paint) {
        Drawable drawable = getDrawable();
        if (drawable == null) return;

        Bitmap bitmap = drawable2Bitmap(drawable);
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }

        canvas.save();
        int transY = ((bottom - top) - drawable.getBounds().bottom) / 2 + top;
        canvas.translate(x, transY);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.restore();

    }

    Bitmap drawable2Bitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getBounds().right,
                        drawable.getBounds().bottom,
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return bitmap;

    }

    private void getSvgaDrawable() {
        mySVGADrawable = SVGADrawableCache.getInstance().get(filePath);
        // 防止多次new MySVGADrawable,但是一次发送多个新的动画，在刷新之前只会展示一个
        if (mySVGADrawable == null) {
            Log.d(TAG, "new MySVGADrawable");
            mySVGADrawable = new MySVGADrawable(context, filePath);
            SVGADrawableCache.getInstance().put(filePath, mySVGADrawable);
        }
        if (mySVGADrawable != null) {
            mySVGADrawable.addRefreshListener(SVGAImageSpan.this);
        }
    }

    public void removeRefreshListener() {
        if (mySVGADrawable != null) {
            mySVGADrawable.removeRefreshListener(SVGAImageSpan.this);
        }
    }

    public void addRefreshListener() {
        if (mySVGADrawable != null) {
            mySVGADrawable.addRefreshListener(SVGAImageSpan.this);
        }
    }

    private void updateDrawable(SVGADrawable svgaDrawable) {
        if (getDrawable() instanceof SVGADrawable) {
            return;
        }
        try {
            Field mDrawable;
            Field mDrawableRef;
            mDrawable = ImageSpan.class.getDeclaredField("mDrawable");
            mDrawable.setAccessible(true);
            mDrawable.set(SVGAImageSpan.this, svgaDrawable);

            mDrawableRef = DynamicDrawableSpan.class.getDeclaredField("mDrawableRef");
            mDrawableRef.setAccessible(true);
            mDrawableRef.set(SVGAImageSpan.this, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onRefresh(SVGADrawable svgaDrawable) {
        if (textView == null) {
            return false;
        }
        updateDrawable(svgaDrawable);

        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastTime > REFRESH_INTERVAL) {
            mLastTime = currentTime;
            textView.postInvalidate();
        }
        return true;
    }
}
