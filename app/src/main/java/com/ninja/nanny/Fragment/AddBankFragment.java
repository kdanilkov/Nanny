package com.ninja.nanny.Fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.ninja.nanny.Adapter.CustomSpinnerAdapter;
import com.ninja.nanny.Custom.CustomFragment;
import com.ninja.nanny.MainActivity;
import com.ninja.nanny.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AddBankFragment extends CustomFragment {


    public AddBankFragment() {
        // Required empty public constructor
    }

    LayoutInflater mInflater;
    View mView;
    MainActivity mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_add_bank, container, false);
        mContext = (MainActivity)getActivity();
        mInflater = inflater;

        setUI();

        return mView;
    }

    void setUI() {
        mView.findViewById(R.id.btnBack).setOnClickListener(this);
        mView.findViewById(R.id.btnSave).setOnClickListener(this);

        Spinner spinnerBank = (Spinner)mView.findViewById(R.id.spinnerBank);

        String[] myBankArray = getResources().getStringArray(R.array.bank_array);
        List<String> myBankList = Arrays.asList(myBankArray);

        ArrayList<String>myBankArrayList = new ArrayList<String>(myBankList);

        CustomSpinnerAdapter spinnerAdapterBank = new CustomSpinnerAdapter(mContext,myBankArrayList);
        spinnerBank.setAdapter(spinnerAdapterBank);
        spinnerBank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item = parent.getItemAtPosition(position).toString();

                Toast.makeText(parent.getContext(), "selected bank is..." + item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner spinnerAccountType = (Spinner)mView.findViewById(R.id.spinnerAccountType);

        String[] myAccountTypeArray = getResources().getStringArray(R.array.acccount_type_array);
        List<String> myAccountTypeList = Arrays.asList(myAccountTypeArray);

        ArrayList<String>myAccountTypeArrayList = new ArrayList<String>(myAccountTypeList);

        CustomSpinnerAdapter spinnerAdapterAccountType = new CustomSpinnerAdapter(mContext,myAccountTypeArrayList);
        spinnerAccountType.setAdapter(spinnerAdapterAccountType);
        spinnerAccountType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item = parent.getItemAtPosition(position).toString();

                Toast.makeText(parent.getContext(), "selected bank is..." + item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                mContext.getSupportFragmentManager().popBackStackImmediate();
                break;
            case R.id.btnSave:
                break;
        }
    }

}
