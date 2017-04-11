package com.moneynanny.nanny.Custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.moneynanny.nanny.R;

/**
 * Created by Administrator on 11/17/2016.
 */

public class MediumOrangeButton extends Button {
    public MediumOrangeButton(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public MediumOrangeButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public MediumOrangeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = Typeface.createFromAsset(context.getAssets(), "Roboto_Medium.ttf");
        setTypeface(customFont);
        setBackgroundResource(R.color.orange);
        setTextSize(14);
        setTextColor(getResources().getColor(R.color.white));
    }
}
