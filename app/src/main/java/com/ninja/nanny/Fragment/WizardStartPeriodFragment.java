package com.ninja.nanny.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ninja.nanny.Custom.RegularEditText;
import com.ninja.nanny.R;
import com.ninja.nanny.WizardActivity;

/**
 * Created by petra on 12.03.2017.
 */

public class WizardStartPeriodFragment extends BaseWizardFragment {

    private RegularEditText mTextPeriodStart;

    public WizardStartPeriodFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_wizard_period, container, false);
        mContext = (WizardActivity)getActivity();
        initData();
        setUI();
        return mView;
    }

    void initData() {

    }

    private void setUI() {
        mTextPeriodStart = (RegularEditText) mView.findViewById(R.id.etSalaryDate);
    }

    @Override
    public boolean isValidate() {
        return false;
    }
}
