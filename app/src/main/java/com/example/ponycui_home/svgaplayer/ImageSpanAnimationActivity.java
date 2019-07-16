package com.example.ponycui_home.svgaplayer;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.View;
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


        String st = "[m01][m01][m01][m01][m01][m01]" +
                "[m02][m02][m02][m02][m02][m02]" +
                "[m04][m04][m04][m04][m04][m04]" +
                "[m03][m03][m03][m03][m03][m03]";
        String st2 = "[m01][m01][m01][m01][m01][m01]" +
                "[m02][m02][m02][m02][m02][m02]" +
                "[m04][m04][m04][m04][m04][m04]" +
                "[m03][m03][m03][m03][m03][m03]";

        SpannableString spannableString = getSpannableString(st);
        SpannableString spannableString2 = getSpannableString(st2);
        spannableStringBuilder.append(spannableString);
        spannableStringBuilder.append(spannableString2);

        textView.setText(spannableStringBuilder);
    }

    private SpannableString getSpannableString(String str) {
        // 地址是通过TIM发送到手机SDcard的地址
        String file1 = "/mnt/sdcard/tencent/TIMfile_recv/angel.svga";
        String file2 = "/mnt/sdcard/tencent/TIMfile_recv/posche.svga";
        String file3 = "/mnt/sdcard/tencent/TIMfile_recv/heartbeat.svga";
        String file4 = "/mnt/sdcard/tencent/TIMfile_recv/yelaila.svga";

        SpannableString spanString = new SpannableString(str);
        Matcher localMatcher = EMOTION_URL.matcher(spanString);
        while (localMatcher.find()) {
            String name = localMatcher.group();
            int k = localMatcher.start();
            int m = localMatcher.end();
            String path;
            if (name.equals("[m01]")) {
                path = file1;
            } else if (name.equals("[m02]")) {
                path = file2;
            } else if (name.equals("[m03]")) {
                path = file3;
            } else {
                path = file4;
            }
            Drawable default_drawable = getResources().getDrawable(R.drawable.ic_launcher);
            default_drawable.setBounds(0, 0, 150, 150);
            SVGAImageSpan imgSpan = new SVGAImageSpan(this, path, textView, default_drawable);
            spanString.setSpan(imgSpan, k, m, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return spanString;

    }

    public void recover(View view) {
        Spanned spannedString = (Spanned) textView.getText();
        SVGAImageSpan[] link = spannedString.getSpans(0, spannedString.length(), SVGAImageSpan.class);
        for (int i = 0; i < link.length; i++) {
            link[i].addRefreshListener();
        }
    }

    public void stop(View view) {
        Spanned spannedString = (Spanned) textView.getText();
        SVGAImageSpan[] link = spannedString.getSpans(0, spannedString.length(), SVGAImageSpan.class);
        for (int i = 0; i < link.length; i++) {
            link[i].removeRefreshListener();
        }
    }

    @Override
    protected void onDestroy() {
        SVGADrawableCache.getInstance().evictAll();
        super.onDestroy();
    }
}
