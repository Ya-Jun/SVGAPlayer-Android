package com.example.ponycui_home.svgaplayer;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageSpanAnimationActivity extends Activity {

    TextView textView = null;
    private Pattern EMOTION_URL = Pattern.compile("\\[(\\S+?)\\]");
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_span);

        textView = (TextView) findViewById(R.id.textview);

//        String st = "动画不动，[m00]显示个不动的动画，动不动[m01][m01][m01][m01][m01][m01][m01][m01][m01][m01][m01]" +
//                "[m01][m01][m01][m01][m01][m01][m01][m01][m01][m01][m01][m01][m01][m01][m01][m01][m01]"+
//                "[m01][m01][m01][m01][m01][m01][m01][m01][m01][m01][m01][m01][m01][m01][m01][m01][m01]";
        String st = "动画不动，[m00]显示个不动的动画，动不动[m01][m01][m01][m01][m01][m01][m01][m01][m01]";
        SpannableString spanString = new SpannableString(st);

        Matcher localMatcher = EMOTION_URL.matcher(st);
        while (localMatcher.find()) {
            String name = localMatcher.group();
            int k = localMatcher.start();
            int m = localMatcher.end();
            SVGAImageSpan imgSpan = new SVGAImageSpan(this, textView);
            spanString.setSpan(imgSpan, k, m, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setText(spanString);
    }
}
