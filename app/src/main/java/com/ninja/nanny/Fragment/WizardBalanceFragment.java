package com.ninja.nanny.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ninja.nanny.R;

/**
 * Created by petra on 12.03.2017.
 */

public class WizardBalanceFragment extends BaseWizardFragment {

    public WizardBalanceFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_wizard_balance, container, false);
        return mView;
    }

    @Override
    public boolean isValidate() {
        return false;
    }
}
