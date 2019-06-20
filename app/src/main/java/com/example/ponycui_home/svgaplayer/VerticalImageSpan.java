package com.example.ponycui_home.svgaplayer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.text.style.ImageSpan;

import com.opensource.svgaplayer.SVGADrawable;

/**
 * 垂直居中的 ImageSpan
 */
public class VerticalImageSpan extends ImageSpan {

    public VerticalImageSpan(Drawable d) {
        super(d);
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
//        Drawable drawable = getDrawable();
//        canvas.save();
//        int transY = 0;
//        transY = ((bottom - top) - drawable.getBounds().bottom) / 2 + top;
//        canvas.translate(x, transY);
//        drawable.draw(canvas);
//        canvas.restore();


        Drawable drawable = getDrawable();
        if (drawable == null) return;
        if (drawable instanceof SVGADrawable) {

            Bitmap bitmap = drawable2Bitmap(drawable);
            if (bitmap == null || bitmap.isRecycled()) {
                return;
            }
//            Bitmap newBmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
//            Bitmap newBmp = Bitmap.createScaledBitmap(bitmap, drawable.getBounds().right, drawable.getBounds().bottom,true);

            canvas.save();
            int transY = ((bottom - top) - drawable.getBounds().bottom) / 2 + top;
            canvas.translate(x, transY);
            canvas.drawBitmap(bitmap, 0, 0, paint);
            canvas.restore();
        }
    }


    Bitmap drawable2Bitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getBounds().right,
                        drawable.getBounds().bottom,
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
//            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
//                    drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;

    }
}
