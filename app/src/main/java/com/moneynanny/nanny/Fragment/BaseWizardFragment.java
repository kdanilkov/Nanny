package com.moneynanny.nanny.Fragment;

import android.view.View;

import com.moneynanny.nanny.Custom.CustomFragment;

/**
 * Created by petra on 10.03.2017.
 */

public abstract class BaseWizardFragment extends CustomFragment {
    protected View mView;

    /**
     * Validate input data
     * */
    public abstract boolean isValidate();

    /**
     * Set input data to model
     * */
    public abstract void setData();
}
