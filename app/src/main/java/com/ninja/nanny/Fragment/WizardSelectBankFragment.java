package com.ninja.nanny.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.ninja.nanny.Adapter.CustomSpinnerAdapter;
import com.ninja.nanny.MainActivity;
import com.ninja.nanny.Model.Bank;
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Common;
import com.ninja.nanny.Utils.Constant;
import com.ninja.nanny.WizardActivity;

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
                e.printStackTrace();
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
        try{
            JSONObject bankObj = Common.getInstance().jsonArrayBankInfo.getJSONObject(mIndex);
            bank.setAccountName(bankObj.getString(Constant.JSON_NAME));
            bank.setIdxKind(mIndex);
        }
        catch (Exception ex)
        {
            Log.e(Constant.TAG_CURRENT, ex.getMessage());
        }
        mModel.setBank(bank);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (i != mIndex) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.LAUNCH_FRAGMENT_PARAM, 5);
            intent.putExtras(bundle);
            startActivity(intent);
            getActivity().finish();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
