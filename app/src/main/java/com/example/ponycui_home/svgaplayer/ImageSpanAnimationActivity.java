package com.example.ponycui_home.svgaplayer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.widget.ImageView;
import android.widget.TextView;

import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ImageSpanAnimationActivity extends Activity {

    TextView textView = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_span);

        textView = (TextView)findViewById(R.id.textview);
        loadAnimation();
    }


    int i =1;
    private void loadAnimation() {
        SVGAParser parser = new SVGAParser(this);
        parser.decodeFromAssets(this.randomSample(), new SVGAParser.ParseCompletion() {
            @Override
            public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                final SVGADrawable svgaDrawable = new SVGADrawable(videoItem);

                String st = "动画不动，[m30]显示个不动的动画，动不动";
                VerticalImageSpan imgSpan = new VerticalImageSpan(svgaDrawable);
                SpannableString spanString = new SpannableString(st);
                spanString.setSpan(imgSpan, 5, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(spanString);


                svgaDrawable.setBounds(0, 0, 200, 200);
                svgaDrawable.setScaleType(ImageView.ScaleType.FIT_XY);
                svgaDrawable.setCleared$library_debug(false);

                final SVGAVideoEntity svgaVideoEntity = svgaDrawable.getVideoItem();
                final long duration = 1000 / svgaVideoEntity.getFPS();
                final Handler mHandler = new Handler();
                mHandler.post(new Runnable() {
                    public void run() {
                        svgaDrawable.setCurrentFrame$library_debug(i);
                        i++;
                        if (i == svgaVideoEntity.getFrames()) {
                            i = 1;
                        }
                        mHandler.postDelayed(this, duration);
                        textView.postInvalidate();
                    }
                });

            }

            @Override
            public void onError() {

            }
        });
    }

    private ArrayList<String> samples = new ArrayList();

    private String randomSample() {
        if (samples.size() == 0) {
            samples.add("angel.svga");
//            samples.add("alarm.svga");
//            samples.add("EmptyState.svga");
//            samples.add("heartbeat.svga");
//            samples.add("posche.svga");
//            samples.add("rose_1.5.0.svga");
//            samples.add("rose_2.0.0.svga");
//            samples.add("test.svga");
//            samples.add("test2.svga");
        }
        return samples.get((int) Math.floor(Math.random() * samples.size()));
    }

}
