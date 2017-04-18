package com.moneynanny.nanny.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moneynanny.nanny.Custom.RegularEditText;
import com.moneynanny.nanny.Preference.UserPreference;
import com.moneynanny.nanny.R;
import com.moneynanny.nanny.Utils.Common;
import com.moneynanny.nanny.Utils.Constant;
import com.moneynanny.nanny.Utils.SmsReader;
import com.moneynanny.nanny.WizardActivity;

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
        // order is critical. initData() would fail if we don't set up UI first.
        setUI(mView);
        initData();
        return mView;
    }

    private void initData() {
        trySetSalaryDay();
    }

    private void trySetSalaryDay() {
        int day = SmsReader.readSalaryDay(mContext, Common.getInstance().bankActive);
        mTextPeriodStart.setText(String.valueOf(day), TextView.BufferType.EDITABLE);
    }


    private void setUI(View mView) {
        View view = mView.findViewById(R.id.etSalaryDate);
        mTextPeriodStart = (RegularEditText) view;
    }

    @Override
    public boolean isValidate() {
        return true;
    }

    @Override
    public void setData() {
        try {
            int day = Integer.parseInt(mTextPeriodStart.getText().toString());
            UserPreference.getInstance().putSharedPreference(Constant.PREF_KEY_SALARY_DATE, day);
            // due to spaghetti code these should be updated manually. bleh.
            Common.getInstance().nSalaryDate = day;
            Common.getInstance().calculateUsedAmount();
        } catch(Exception e) {
            Log.e(Constant.TAG_CURRENT, Log.getStackTraceString(e));
        }
    }
}