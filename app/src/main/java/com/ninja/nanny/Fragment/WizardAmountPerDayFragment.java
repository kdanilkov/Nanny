package com.ninja.nanny.Fragment;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ninja.nanny.Custom.RegularEditText;
import com.ninja.nanny.Preference.UserPreference;
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Common;
import com.ninja.nanny.Utils.Constant;

/**
 * Created by nikolay on 29.03.17.
 */

public class WizardAmountPerDayFragment extends BaseWizardFragment {
    private RegularEditText mAmountEdit;

    public WizardAmountPerDayFragment() {
    }

    private void initData() {
        int amount = UserPreference.getInstance().getSharedPreference(Constant.PREF_KEY_MINIMAL_AMOUNT_PER_DAY, 0);
        mAmountEdit.setText(String.valueOf(amount));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_wizard_amount_per_day, container, false);
        mAmountEdit = ((RegularEditText)mView.findViewById((R.id.etText)));
        initData();
        return mView;
    }

    @Override
    public boolean isValidate() {
        String text = mAmountEdit.getText().toString();
        if (text.isEmpty()) {
            mAmountEdit.setError(Html.fromHtml("<font color='red'>please input the amount</font>"));
            return false;
        }
        return true;
    }

    @Override
    public void setData() {
        int amount = Integer.parseInt(mAmountEdit.getText().toString());
        UserPreference.getInstance().putSharedPreference(Constant.PREF_KEY_MINIMAL_AMOUNT_PER_DAY, amount);
        Common.getInstance().nMinimalDayAmount = amount;
        Common.getInstance().updateTimestamp();
    }
}
