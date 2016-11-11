package com.ninja.nanny.Fragment;


import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ninja.nanny.Custom.CustomFragment;
import com.ninja.nanny.MainActivity;
import com.ninja.nanny.Model.Payment;
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Common;
import com.ninja.nanny.Utils.Constant;


public class ExpenseAsPayedFragment extends CustomFragment {


    public ExpenseAsPayedFragment() {
        // Required empty public constructor
    }

    LayoutInflater mInflater;
    View mView;
    MainActivity mContext;
    public Payment paymentSelected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_expense_as_payed, container, false);
        mContext = (MainActivity)getActivity();
        mInflater = inflater;

        setUI();

        return mView;
    }

    void setUI() {
        mView.findViewById(R.id.btnBack).setOnClickListener(this);
        mView.findViewById(R.id.btnWire).setOnClickListener(this);
        mView.findViewById(R.id.btnCash).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {


        if(v.getId() == R.id.btnWire) {
            TransactionFragment f = new TransactionFragment();
            f.nMode = 1;
            f.paymentSelected = paymentSelected;
            String title = Constant.FRAGMENT_TRANSACTION;

            FragmentTransaction transaction = mContext.getSupportFragmentManager()
                    .beginTransaction();
            transaction.add(R.id.content_frame, f, title).addToBackStack(title).commit();
         } else if(v.getId() == R.id.btnCash) {
            paymentSelected.setPaidStatus(1);
            Common.getInstance().dbHelper.updatePayment(paymentSelected);

            mContext.getSupportFragmentManager().popBackStackImmediate();
        } else if(v.getId() == R.id.btnBack) {
            mContext.getSupportFragmentManager().popBackStackImmediate();
        }

//
//        switch (v.getId()) {
//            case R.id.btnBack:
//
//                mContext.getSupportFragmentManager().popBackStackImmediate();
//                break;
//
//            case R.id.btnWire:
////                TransactionFragment f = new TransactionFragment();
////                String title = Constant.FRAGMENT_TRANSACTION;
//
//                SettingFragment f = new SettingFragment();
//                String title = Constant.FRAGMENT_SETTING;
//
//                FragmentTransaction transaction = mContext.getSupportFragmentManager()
//                        .beginTransaction();
//                transaction.add(R.id.content_frame, f, title).addToBackStack(title).commit();
//
//            case R.id.btnCash:
//                mContext.getSupportFragmentManager().popBackStackImmediate();
//                break;
//        }
    }

}
