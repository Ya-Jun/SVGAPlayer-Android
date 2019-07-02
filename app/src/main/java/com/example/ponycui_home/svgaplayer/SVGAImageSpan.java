package com.example.ponycui_home.svgaplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;

/**
 * 垂直居中的 ImageSpan
 */
public class SVGAImageSpan extends ImageSpan {
    private final String TAG = "SVGAImageSpan";
    private Context context;
    private TextView textView;
    private String filePath;

    private SVGADrawable svgaDrawable;
    private SVGAVideoEntity svgaVideoEntity;
    private long duration;
    private int i = 1;

    private Handler mHandler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (svgaDrawable != null) {
                svgaDrawable.setCurrentFrame$library_debug(i);
                i++;
                if (i == svgaVideoEntity.getFrames()) {
                    i = 1;
                }
                mHandler.postDelayed(this, duration);
                textView.postInvalidate();
            } else {
                stopAnimation();
            }
        }
    };

    public SVGAImageSpan(Context context,String filePath ,TextView textView) {
        super(context, R.drawable.ic_launcher);
        this.context = context;
        this.textView = textView;
        this.filePath = filePath;

        textView.post(new Runnable() {
            @Override
            public void run() {
                getSvgaDrawable();
            }
        });
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
        svgaDrawable = SVGADrawableCache.getInstance().get(filePath);
        if (svgaDrawable != null) {
            Log.d(TAG, filePath + "get SVGADrawable from Cache");
            loadAnimation();
            return;
        }

        SVGAParser parser = new SVGAParser(context);
        try {
            FileInputStream inputStream = new FileInputStream(filePath);
            parser.decodeFromInputStream(inputStream, filePath, new SVGAParser.ParseCompletion() {
                @Override
                public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                    svgaDrawable = new SVGADrawable(videoItem);
                    if (svgaDrawable != null) {
                        Log.d(TAG,filePath + "get SVGADrawable from inputStream");
                        loadAnimation();
                        SVGADrawableCache.getInstance().put(filePath, svgaDrawable);
                    }
                }

                @Override
                public void onError() {

                }
            }, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadAnimation() {
        svgaDrawable.setBounds(0, 0, 200, 200);
        svgaDrawable.setScaleType(ImageView.ScaleType.FIT_XY);
        svgaDrawable.setCleared$library_debug(false);

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

        svgaVideoEntity = svgaDrawable.getVideoItem();
        duration = 1000 / svgaVideoEntity.getFPS();
        mHandler.post(runnable);
    }

    private void stopAnimation() {
        if (mHandler != null && runnable != null) {
            mHandler.removeCallbacks(runnable);
            mHandler = null;
            runnable = null;
        }
    }
}
