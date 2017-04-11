package com.moneynanny.nanny.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.moneynanny.nanny.Adapter.CustomSpinnerAdapter;
import com.moneynanny.nanny.Model.Bank;
import com.moneynanny.nanny.R;
import com.moneynanny.nanny.Utils.Common;
import com.moneynanny.nanny.Utils.Constant;
import com.moneynanny.nanny.WizardActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by petra on 10.03.2017.
 */

public class WizardSelectBankFragment extends BaseWizardFragment implements AdapterView.OnItemSelectedListener {
    private Spinner mSpinnerBank;
    private int mIndex = 0;
    private ArrayList<String> mMyBankArrayList;

    public  WizardSelectBankFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_wizard_bank, container, false);
        mContext = (WizardActivity)getActivity();
        initData();
        setUI();
        return mView;
    }

    private void initData() {
        mMyBankArrayList = new ArrayList<>();

        for(int i = 0; i < Common.getInstance().jsonArrayBankInfo.length(); i ++) {
            try {
                JSONObject jsonObject = Common.getInstance().jsonArrayBankInfo.getJSONObject(i);
                mMyBankArrayList.add(jsonObject.getString(Constant.JSON_NAME));
            } catch (JSONException e) {
                Log.e(Constant.TAG_CURRENT, Log.getStackTraceString(e));
            }
        }

    }

    private void setUI() {
        mSpinnerBank = (Spinner) mView.findViewById(R.id.spinnerBank);

        CustomSpinnerAdapter spinnerAdapterBank = new CustomSpinnerAdapter(mContext, mMyBankArrayList);
        mSpinnerBank.setAdapter(spinnerAdapterBank);
        // set bank from messages
        if(Common.getInstance().listAllTransactions.size() > 0) {
            mIndex = Common.getInstance().listAllTransactions.get(Common.getInstance().listAllTransactions.size() - 1).getBankId();
        } else if(Common.getInstance().isActiveBankExist()) {
            mIndex = Common.getInstance().bankActive.getIdxKind();
        }
        mSpinnerBank.setSelection(mIndex);
        mSpinnerBank.setOnItemSelectedListener(this);
    }

    @Override
    public boolean isValidate() {
        return true;
    }

    @Override
    public void setData() {
        Bank bank = new Bank();
        try {
            JSONObject bankObj = Common.getInstance().jsonArrayBankInfo.getJSONObject(mIndex);
            bank.setAccountName(bankObj.getString(Constant.JSON_NAME));
            bank.setIdxKind(mIndex);
            bank.setFlagActive(1);
            bank.setTimestamp(0);
            Common.getInstance().addOrUpdateBank(bank);
        }
        catch (Exception ex)
        {
            Log.e(Constant.TAG_CURRENT, ex.getMessage());
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mIndex = i;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
