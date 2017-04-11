package com.moneynanny.nanny.Fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moneynanny.nanny.Custom.CustomFragment;
import com.moneynanny.nanny.MainActivity;
import com.moneynanny.nanny.Model.Transaction;
import com.moneynanny.nanny.R;

public class TransactionDetailFragment extends CustomFragment {


    public TransactionDetailFragment() {
        // Required empty public constructor
    }


    LayoutInflater mInflater;
    View mView;
    MainActivity mContext;
    public Transaction transactionItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_transaction_detail, container, false);
        mContext = (MainActivity)getActivity();
        mInflater = inflater;

        setUI();

        return mView;
    }

    void setUI() {
        mView.findViewById(R.id.btnBack).setOnClickListener(this);
        ((TextView)mView.findViewById(R.id.tvDetail)).setText(transactionItem.getText());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                mContext.getSupportFragmentManager().popBackStackImmediate();
                break;
        }
    }


}
