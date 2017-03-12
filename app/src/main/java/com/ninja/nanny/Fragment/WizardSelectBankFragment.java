package com.ninja.nanny.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.ninja.nanny.Adapter.CustomSpinnerAdapter;
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

public class WizardSelectBankFragment extends BaseWizardFragment  {
        private Spinner mSpinnerBank;
    private ArrayList<String> mMyBankArrayList;
    public  WizardSelectBankFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_wizard_bank, container, false);
        mContext = (WizardActivity)getActivity();
        initData();
        setUI();
        return mView;
    }

    void initData() {
        mMyBankArrayList = new ArrayList<>();

        for(int i = 0; i < Common.getInstance().jsonArrayBankInfo.length(); i ++) {
            try {
                JSONObject jsonObject = Common.getInstance().jsonArrayBankInfo.getJSONObject(i);
                mMyBankArrayList.add(jsonObject.getString(Constant.JSON_NAME));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //bankItem = Common.getInstance().listBanks.get(nIndex);
    }

    private void setUI()
    {
        mSpinnerBank = (Spinner) mView.findViewById(R.id.spinnerBank);
        CustomSpinnerAdapter spinnerAdapterBank = new CustomSpinnerAdapter(mContext,mMyBankArrayList);
        mSpinnerBank.setAdapter(spinnerAdapterBank);
    }

    @Override
    public boolean isValidate() {
        return false;
    }
}
