package com.ninja.nanny.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ninja.nanny.Custom.RegularEditText;
import com.ninja.nanny.Model.Sms;
import com.ninja.nanny.Model.Transaction;
import com.ninja.nanny.Preference.UserPreference;
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Common;
import com.ninja.nanny.Utils.Constant;
import com.ninja.nanny.Utils.ParseSms;
import com.ninja.nanny.WizardActivity;

import java.util.List;
import java.util.ListIterator;

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

    private void initData() {
        trySetSalaryDay();
    }

    // todo: check the logic when the Wizard is running. We have to create a proper SMS for that, manually.
    private void trySetSalaryDay() {
        ListIterator<Sms> iter = Common.getInstance().listSms.listIterator(Common.getInstance().listSms.size());
        while (iter.hasPrevious()) {
            final Sms sms = iter.previous();
            Transaction tran = ParseSms.getInstance().getSmsByTemplate4(sms.getText());
            if (tran != null) {
                int change = tran.getAmountChange();
                if (change > getResources().getInteger(R.integer.salary_limit)) {
                    setSalaryDay(sms.getDay());
                    break;
                }
            }
        }
    }

    private void setSalaryDay(int day) {
        mTextPeriodStart.setText(day);
        UserPreference.getInstance().putSharedPreference(Constant.PREF_KEY_SALARY_DATE, day);
    }

    private void setUI() {
        mTextPeriodStart = (RegularEditText) mView.findViewById(R.id.etSalaryDate);
    }

    @Override
    public boolean isValidate() {
        return false;
    }

    @Override
    public void setData() {

    }
}
