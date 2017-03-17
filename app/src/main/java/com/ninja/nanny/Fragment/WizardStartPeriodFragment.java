package com.ninja.nanny.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ninja.nanny.Custom.RegularEditText;
import com.ninja.nanny.Helper.TimestampHelper;
import com.ninja.nanny.Model.Transaction;
import com.ninja.nanny.Preference.UserPreference;
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Common;
import com.ninja.nanny.Utils.Constant;
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
        // order is critical. initData() would fail if we don't set up UI first.
        setUI(mView);
        initData();
        return mView;
    }

    private void initData() {
        trySetSalaryDay();
    }

    // todo: test with Transaction list instead of Sms list
    private void trySetSalaryDay() {
        try {
            for (Transaction tran : Common.getInstance().listAllTransactions) {
                int change = tran.getAmountChange();
                if (change > getResources().getInteger(R.integer.salary_limit)) {
                    int day = TimestampHelper.getDayOfMonthFromTimestamp(tran.getTimestampCreated());
                    mTextPeriodStart.setText(String.valueOf(day), TextView.BufferType.EDITABLE);
                    break;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
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
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
