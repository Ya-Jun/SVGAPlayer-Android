package com.example.ponycui_home.svgaplayer;

import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;

public final class SVGADrawableCache {
    private static final String TAG = "SVGAImageSpan";
    private static final int DEFAULT_MAX_SIZE = 10;// 最多存10个表情

    private static volatile SVGADrawableCache mInstance;
    private static final Object mLock = new Object();
    private LruCache<String, MySVGADrawable> mMemoryCache;

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
        mMemoryCache = new LruCache<String, MySVGADrawable>(DEFAULT_MAX_SIZE) {
            @Override
            protected int sizeOf(String key, MySVGADrawable value) {
                final int size = value.getSize();
                Log.d(TAG, "SVGADrawable size = " + size);
                return size == 0 ? 1 : size;
            }
        };
    }

    public void put(String key, MySVGADrawable value) {
        synchronized (mLock) {
            if (TextUtils.isEmpty(key) || value == null) {
                return;
            }

            if (mMemoryCache.get(key) == null) {
                mMemoryCache.put(key, value);
                Log.d(TAG, "ImageCache [put] success，key = +" + key + ",size = " + mMemoryCache.size() + "/" + DEFAULT_MAX_SIZE);
            } else {
//                mMemoryCache.remove(key);
//                mMemoryCache.put(key, value);
//                Log.d(TAG, "ImageCache [update] success，key = +" + key + ",size = " + mMemoryCache.size() + "/" + DEFAULT_MAX_SIZE);

                Log.d(TAG, "ImageCache [put] has in memory, not need put again");
            }
            Log.d(TAG, mMemoryCache.toString());
        }
    }

    public MySVGADrawable get(String key) {
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
                Log.d(TAG, "ImageCache [remove] key = " + key);
                mMemoryCache.remove(key);
            }
        }
    }

    public void evictAll() {
        synchronized (mLock) {
            if (mMemoryCache != null) {
                mMemoryCache.evictAll();
                Log.d(TAG, "ImageCache [evictAll] success , size = " + mMemoryCache.size());
            }
        }
    }

}
