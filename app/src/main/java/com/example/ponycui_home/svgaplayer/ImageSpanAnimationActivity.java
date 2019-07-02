package com.example.ponycui_home.svgaplayer;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageSpanAnimationActivity extends Activity {

    TextView textView = null;
    private Pattern EMOTION_URL = Pattern.compile("\\[(\\S+?)\\]");
    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_span);

        textView = (TextView) findViewById(R.id.textview);


        String st = "[m01][m02][m03]";
        SpannableString spannableString = getSpannableString(st);
        spannableStringBuilder.append(spannableString);
        spannableStringBuilder.append("------------------");

        // 延时500加载是为了让剩余的动画从cache获取
        textView.postDelayed(new Runnable() {
            @Override
            public void run() {
                String st1 = "[m01][m01][m01][m01][m01][m01][m01][m01]" +
                        "[m02][m02][m02][m02][m02][m02][m02][m02]" +
                        "[m03][m03][m03][m03][m03][m03][m03][m03]";
                SpannableString spannableString1 = getSpannableString(st1);
                spannableStringBuilder.append(spannableString1);

                textView.setText(spannableStringBuilder);
            }
        },500);
    }

    private SpannableString getSpannableString(String str){
        // 地址是通过TIM发送到手机SDcard的地址
        String file1 = "/mnt/sdcard/tencent/TIMfile_recv/angel.svga";
        String file2 = "/mnt/sdcard/tencent/TIMfile_recv/posche.svga";
        String file3 = "/mnt/sdcard/tencent/TIMfile_recv/heartbeat.svga";

        SpannableString spanString = new SpannableString(str);
        Matcher localMatcher = EMOTION_URL.matcher(spanString);
        while (localMatcher.find()) {
            String name = localMatcher.group();
            int k = localMatcher.start();
            int m = localMatcher.end();
            String path;
            if (name.equals("[m01]")){
                path = file1;
            } else if (name.equals("[m02]")){
                path = file2;
            }else{
                path = file3;
            }
            SVGAImageSpan imgSpan = new SVGAImageSpan(this, path, textView);
            spanString.setSpan(imgSpan, k, m, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return spanString;

    }

}
