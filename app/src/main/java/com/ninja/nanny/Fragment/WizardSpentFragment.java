package com.ninja.nanny.Fragment;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ninja.nanny.Custom.RegularEditText;
import com.ninja.nanny.Model.SettingWizardModel;
import com.ninja.nanny.Model.Transaction;
import com.ninja.nanny.Model.UsedAmount;
import com.ninja.nanny.Preference.UserPreference;
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Common;
import com.ninja.nanny.Utils.Constant;

import java.util.Calendar;

/**
 * Created by petra on 12.03.2017.
 */

public class WizardSpentFragment extends BaseWizardFragment {
    private RegularEditText mSpendingsEdit;

    public WizardSpentFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_wizard_spent, container, false);
        mSpendingsEdit = ((RegularEditText)mView.findViewById((R.id.etSpendings)));
        initData();
        return mView;
    }

    @Override
    public boolean isValidate() {
        String text = mSpendingsEdit.getText().toString();
        if (text.isEmpty()) {
            mSpendingsEdit.setError(Html.fromHtml("<font color='red'>please input the balance</font>"));
            return false;
        }
        return true;
    }

    private void initData() {
        int spendings = Common.getInstance().getUsedAmount(Common.getInstance().getTimestampCurrentPeriodEnd()).getUsedAmount();
        mSpendingsEdit.setText(String.valueOf(spendings));
    }

    @Override
    public void setData() {
        try {
            int spendings = Integer.parseInt(mSpendingsEdit.getText().toString());
            Common.getInstance().updateUsedAmount(Common.getInstance().getTimestampCurrentPeriodEnd(), spendings);
            UserPreference.getInstance().putSharedPreference(Constant.PREF_KEY_INIT_USED_MONEY, spendings);
//            Common.getInstance().updateTimestamp();
        } catch (Exception e) {
            Log.e(Constant.TAG_CURRENT, Log.getStackTraceString(e));
        }
    }
}
