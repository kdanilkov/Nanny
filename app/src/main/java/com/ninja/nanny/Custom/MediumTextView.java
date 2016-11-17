package com.ninja.nanny.Custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Administrator on 11/17/2016.
 */

public class MediumTextView extends TextView {
    public MediumTextView(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public MediumTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public MediumTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = Typeface.createFromAsset(context.getAssets(), "Roboto_Medium.ttf");
        setTypeface(customFont);
    }
}
