package com.example.ponycui_home.svgaplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MySVGADrawable {
    private final String TAG = "SVGAImageSpan";
    private Context context;
    private String filePath;

    private SVGADrawable svgaDrawable;
    private SVGAVideoEntity svgaVideoEntity;
    private long duration;
    private int i = 1;
    private boolean isAnimationRunning = false;

    private List<RefreshListener> mRefreshListeners = new ArrayList<>();
    private Handler mHandler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (svgaDrawable != null && mRefreshListeners.size() != 0) {
                isAnimationRunning = true;
                svgaDrawable.setCurrentFrame$library_debug(i);
                if (i == svgaVideoEntity.getFrames()) {
                    i = 0;
                }
                i++;

                mHandler.postDelayed(this, duration);

                // 通知textview刷新
                Iterator<RefreshListener> it = mRefreshListeners.iterator();
                while (it.hasNext()) {
                    RefreshListener listener = it.next();
                    boolean valid = listener.onRefresh(svgaDrawable);
                    if (!valid) {
                        it.remove();
                        Log.d(TAG, "Runnable removeRefreshListener" + mRefreshListeners.size());
                    }
                }
            } else {
                stopAnimation();
            }
        }
    };

    public MySVGADrawable(Context context, String filePath) {
        this.context = context;
        this.filePath = filePath;

        parserSvgaDrawable();
    }

    public int getSize() {
        return 1;
        //由于SVGADrawable取到的size都是固定值，所以返回1，按照个数限制。
        //return drawableSize(svgaDrawable);
    }

    private int drawableSize(Drawable drawable) {
        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getBounds().right,
                        drawable.getBounds().bottom,
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return bitmap.getByteCount();

    }

    public void parserSvgaDrawable() {
        if (svgaDrawable != null) {
            Log.d(TAG, "SvgaDrawable ready");
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

                    svgaDrawable.setBounds(0, 0, 150, 150);
                    svgaDrawable.setScaleType(ImageView.ScaleType.FIT_XY);
                    svgaDrawable.setCleared$library_debug(false);

                    svgaVideoEntity = svgaDrawable.getVideoItem();
                    duration = 1000 / svgaVideoEntity.getFPS();

                    Log.d(TAG, "SvgaDrawable parser end FPS = " + svgaVideoEntity.getFPS());
//                    SVGADrawableCache.getInstance().put(filePath, MySVGADrawable.this);
                    loadAnimation();
                }

                @Override
                public void onError() {

                }
            }, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void loadAnimation() {
        if (mHandler != null && runnable != null) {
            mHandler.removeCallbacks(runnable);
            mHandler.post(runnable);
            Log.d(TAG, "loadAnimation");
        }
    }

    private void stopAnimation() {
        if (mHandler != null && runnable != null) {
            mHandler.removeCallbacks(runnable);
            isAnimationRunning = false;
            Log.d(TAG, "Listeners.size = 0 stopAnimation");
        }
    }


    public void addRefreshListener(RefreshListener refreshListener) {
        mRefreshListeners.add(refreshListener);
        if (svgaDrawable != null && !isAnimationRunning) {
            loadAnimation();
        }
        Log.d(TAG, "addRefreshListener" + mRefreshListeners.size());
    }

    public void removeRefreshListener(RefreshListener refreshListener) {
        mRefreshListeners.remove(refreshListener);
        Log.d(TAG, "removeRefreshListener" + mRefreshListeners.size());
    }

    public interface RefreshListener {
        boolean onRefresh(SVGADrawable svgaDrawable);
    }

}
