package com.example.ponycui_home.svgaplayer;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.widget.TextView;

public class ImageSpanAnimationActivity extends Activity {

    TextView textView = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_span);

        textView = (TextView) findViewById(R.id.textview);

        String st = "动画不动，[m30]显示个不动的动画，动不动";
        SVGAImageSpan imgSpan = new SVGAImageSpan(this, textView);
        SpannableString spanString = new SpannableString(st);
        spanString.setSpan(imgSpan, 5, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spanString);
    }
}
