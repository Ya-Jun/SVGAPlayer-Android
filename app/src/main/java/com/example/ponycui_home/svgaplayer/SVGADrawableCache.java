package com.example.ponycui_home.svgaplayer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;
import com.opensource.svgaplayer.SVGADrawable;


public final class SVGADrawableCache {
    private static final String TAG = "SVGADrawableCache";
    private static final int DEFAULT_MAX_SIZE = 1024 * 1024 * 20;

    private static volatile SVGADrawableCache mInstance;
    private static final Object mLock = new Object();
    private LruCache<String, SVGADrawable> mMemoryCache;

    public static SVGADrawableCache getInstance() {
        if (mInstance == null) {
            synchronized (mLock) {
                if (mInstance == null) {
                    mInstance = new SVGADrawableCache();
                    mInstance.init();
                }
            }
        }
        return mInstance;
    }

    private void init() {
        mMemoryCache = new LruCache<String, SVGADrawable>(DEFAULT_MAX_SIZE) {
            @Override
            protected int sizeOf(String key, SVGADrawable value) {
                final int bitmapSize = drawable2Bitmap(value).getByteCount();
                Log.d(TAG, "SVGADrawable bitmapSize = " + bitmapSize);
                return bitmapSize == 0 ? 1 : bitmapSize;
            }
        };
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

    public void put(String key, SVGADrawable value) {
        synchronized (mLock) {
            if (TextUtils.isEmpty(key) || value == null) {
                return;
            }
            if (mMemoryCache.get(key) == null) {
                mMemoryCache.put(key, value);
                Log.d(TAG, "ImageCache [put] success，key = +" + key + ",size = " + mMemoryCache.size() + "/" + DEFAULT_MAX_SIZE);
            } else {
                Log.d(TAG, "ImageCache [put] has in memory, not need put again");
            }
        }
    }


    public SVGADrawable get(String key) {
        synchronized (mLock) {
            if (!TextUtils.isEmpty(key) && mMemoryCache != null) {
                Log.d(TAG, "ImageCache [get] key = " + key);
                return mMemoryCache.get(key);
            }
        }
        return null;
    }


    protected void remove(String key) {
        synchronized (mLock) {
            if (!TextUtils.isEmpty(key) && mMemoryCache != null) {
                mMemoryCache.remove(key);
            }
        }
    }

    protected void evictAll() {
        synchronized (mLock) {
            if (mMemoryCache != null) {
                mMemoryCache.evictAll();
            }
        }
    }

}
