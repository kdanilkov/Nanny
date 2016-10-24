package com.ninja.nanny.Fragment;


import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ninja.nanny.Custom.CustomFragment;
import com.ninja.nanny.MainActivity;
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Constant;


public class BankFragment extends CustomFragment {


    public BankFragment() {
        // Required empty public constructor
    }

    LayoutInflater mInflater;
    View mView;
    MainActivity mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_bank, container, false);
        mContext = (MainActivity)getActivity();
        mInflater = inflater;

        setUI();

        return mView;
    }

    void setUI() {
        mView.findViewById(R.id.btnBack).setOnClickListener(this);
        mView.findViewById(R.id.btnAddBankItem).setOnClickListener(this);

        View viewItemChecked = mView.findViewById(R.id.bankItemChekced);
        View viewItemUnChecked = mView.findViewById(R.id.bankItemUnChekced);

        ((ImageView)viewItemChecked.findViewById(R.id.imgvCircle)).setImageResource(R.drawable.ic_circle_blue);
        ((TextView)viewItemChecked.findViewById(R.id.tvBankName)).setText("Emirates NBD");
        ((TextView)viewItemChecked.findViewById(R.id.tvDetail)).setText("******123, Current, by SMS");
        ((TextView)viewItemChecked.findViewById(R.id.tvBalance)).setText("Balance: 7500$");
        ((Button)viewItemChecked.findViewById(R.id.btnCheck)).setBackgroundResource(R.drawable.ic_checked);

        ((ImageView)viewItemUnChecked.findViewById(R.id.imgvCircle)).setImageResource(R.drawable.ic_circle_gray);
        ((TextView)viewItemUnChecked.findViewById(R.id.tvBankName)).setText("City Bank Card");
        ((TextView)viewItemUnChecked.findViewById(R.id.tvDetail)).setText("******345, Savings");
        ((TextView)viewItemUnChecked.findViewById(R.id.tvBalance)).setText("Balance: 15,000$");
        ((Button)viewItemUnChecked.findViewById(R.id.btnCheck)).setBackgroundResource(R.drawable.ic_unchecked);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                mContext.toggleMenu();
                break;
            case R.id.btnAddBankItem:
                AddBankFragment f = new AddBankFragment();
                String title = Constant.FRAGMENT_ADD_BANK;

                FragmentTransaction transaction = mContext.getSupportFragmentManager()
                        .beginTransaction();
                transaction.add(R.id.content_frame, f, title).addToBackStack(title).commit();
                break;
        }
    }

}
