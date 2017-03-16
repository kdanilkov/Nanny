package com.ninja.nanny.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        setUI(mView);
        initData();
        return mView;
    }

    private void initData() {
        trySetSalaryDay();
    }

    // todo: Sms order is unstable, and is changing from direct to reverse. Thus the logic below doesn't work properly.
    private void trySetSalaryDay() {
        try {
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
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void setSalaryDay(int day) {
        mTextPeriodStart.setText(String.valueOf(day), TextView.BufferType.EDITABLE);
        UserPreference.getInstance().putSharedPreference(Constant.PREF_KEY_SALARY_DATE, day);
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

    }
}
