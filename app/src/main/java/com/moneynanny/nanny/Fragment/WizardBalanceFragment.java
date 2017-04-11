package com.moneynanny.nanny.Fragment;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moneynanny.nanny.Custom.RegularEditText;
import com.moneynanny.nanny.R;
import com.moneynanny.nanny.Utils.Common;
import com.moneynanny.nanny.Utils.Constant;

/**
 * Created by petra on 12.03.2017.
 */

public class WizardBalanceFragment extends BaseWizardFragment {

    private RegularEditText mBalanceEdit;

    public WizardBalanceFragment() {
    }

    private void initData() {
        mBalanceEdit.setText(String.valueOf(Common.getInstance().bankActive.getBalance()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_wizard_balance, container, false);
        mBalanceEdit = ((RegularEditText)mView.findViewById((R.id.etBalance)));
        initData();
        return mView;
    }

    @Override
    public boolean isValidate() {

        String text = mBalanceEdit.getText().toString();
        if (text.isEmpty()) {
            mBalanceEdit.setError(Html.fromHtml("<font color='red'>please input the balance</font>"));
            return false;
        }
        return true;
    }

    @Override
    public void setData() {
        try {
            String text = mBalanceEdit.getText().toString();
            int balance = Integer.valueOf(text);
            Common.getInstance().bankActive.setBalance(balance);
//            Common.getInstance().updateTimestamp();
        } catch (Exception e) {
            Log.e(Constant.TAG_CURRENT, Log.getStackTraceString(e));
        }
    }
}
