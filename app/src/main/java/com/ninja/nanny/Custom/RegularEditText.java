package com.ninja.nanny.Custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Administrator on 11/17/2016.
 */

public class RegularEditText extends EditText {
    public RegularEditText(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public RegularEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public RegularEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = Typeface.createFromAsset(context.getAssets(), "Roboto_Regular.ttf");
        setTypeface(customFont);
    }
}
