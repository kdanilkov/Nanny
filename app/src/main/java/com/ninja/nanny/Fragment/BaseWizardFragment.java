package com.ninja.nanny.Fragment;

import android.support.v4.app.Fragment;
import android.view.View;

import com.ninja.nanny.Custom.CustomFragment;
import com.ninja.nanny.Model.SettingWizardModel;

/**
 * Created by petra on 10.03.2017.
 */

public abstract class BaseWizardFragment extends CustomFragment {
    protected View mView;
    protected SettingWizardModel mModel;

    public void setModel(SettingWizardModel model){
        mModel = model;
    }

    public SettingWizardModel getModel() {
        return mModel;
    }

    /**
     * Validate input data
     * */
    public abstract boolean isValidate();

    /**
     * Set input data to model
     * */
    public abstract void setData();
}
