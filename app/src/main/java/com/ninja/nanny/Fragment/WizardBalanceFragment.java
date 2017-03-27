package com.ninja.nanny.Fragment;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ninja.nanny.Custom.RegularEditText;
import com.ninja.nanny.Model.Bank;
import com.ninja.nanny.Model.Transaction;
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Common;

/**
 * Created by petra on 12.03.2017.
 */

public class WizardBalanceFragment extends BaseWizardFragment {

    private RegularEditText mBalanceEdit;

    public WizardBalanceFragment() {
    }

    private void initData() {
        if (!Common.getInstance().isActiveBankExist()) {
            for (Transaction transaction : Common.getInstance().listAllTransactions) {
                if (transaction.getAccountName().equals(mModel.getBank().getAccountName())) {
                    mBalanceEdit.setText(transaction.getAmountBalance() + "");
                    break;
                }
            }
        } else {
            mBalanceEdit.setText(Common.getInstance().bankActive.getBalance() + "");
        }
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
        String text = mBalanceEdit.getText().toString();
        int vl = Integer.valueOf(text);
        Bank bank = mModel.getBank();
        bank.setBalance(vl);
        bank.setFlagActive(1);
        Common.getInstance().updateBank(bank);
    }
}
