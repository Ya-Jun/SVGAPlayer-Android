package com.example.ponycui_home.svgaplayer;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ImageViewAnimationActivity extends Activity {

    ImageView animationView = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animationView = new ImageView(this);
        animationView.setBackgroundColor(Color.BLACK);
        animationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAnimation();
            }
        });
        loadAnimation();
        setContentView(animationView);
    }


    int i =1;
    private void loadAnimation() {
        SVGAParser parser = new SVGAParser(this);
        parser.decodeFromAssets(this.randomSample(), new SVGAParser.ParseCompletion() {
            @Override
            public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                SVGADrawable drawable = new SVGADrawable(videoItem);
                animationView.setImageDrawable(drawable);


                Drawable b = animationView.getDrawable();
                if (b instanceof SVGADrawable) {
                    final SVGADrawable c = (SVGADrawable) b;
                    c.setCleared$library_debug(false);

                    final SVGAVideoEntity it = c.getVideoItem();
                    final long duration = 1000 / it.getFPS();

                    final Handler mHandler = new Handler();
                    mHandler.post(new Runnable() {
                        public void run() {
                            c.setCurrentFrame$library_debug(i);
                            i++;
                            if (i == it.getFrames()) {
                                i = 1;
                            }
                            mHandler.postDelayed(this, duration);
                        }
                    });
                }

//                animationView.setVideoItem(videoItem);
//                animationView.startAnimation();
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
            samples.add("alarm.svga");
            samples.add("EmptyState.svga");
            samples.add("heartbeat.svga");
            samples.add("posche.svga");
            samples.add("rose_1.5.0.svga");
            samples.add("rose_2.0.0.svga");
            samples.add("test.svga");
            samples.add("test2.svga");
        }
        return samples.get((int) Math.floor(Math.random() * samples.size()));
    }

}
