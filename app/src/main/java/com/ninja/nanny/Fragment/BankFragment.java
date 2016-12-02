package com.ninja.nanny.Fragment;


import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ninja.nanny.Custom.CustomFragment;
import com.ninja.nanny.MainActivity;
import com.ninja.nanny.Model.Bank;
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Common;
import com.ninja.nanny.Utils.Constant;


public class BankFragment extends CustomFragment {


    public BankFragment() {
        // Required empty public constructor
    }

    LayoutInflater mInflater;
    View mView;
    MainActivity mContext;
    LinearLayout lyContainer;
    int nActiveIndex = 0;

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
        lyContainer = (LinearLayout)mView.findViewById(R.id.lyContainer);
    }

    @Override
    public void onResume() {
        super.onResume();
        presentData();
    }

    void presentData() {
        lyContainer.removeAllViews();

        if(Common.getInstance().listBanks == null || Common.getInstance().listBanks.size() == 0) {
            Toast.makeText(mContext, "There is no bank info", Toast.LENGTH_SHORT).show();
            return;
        }

        for(int i = 0; i < Common.getInstance().listBanks.size(); i ++) {
            Bank bankItem = Common.getInstance().listBanks.get(i);

            if(bankItem.getFlagActive() == 1) {
                nActiveIndex = i;
            }

            final int nIdx = i;
            View cell = mInflater.inflate(R.layout.cell_bank_item, null);

            ((TextView)cell.findViewById(R.id.tvBankName)).setText(bankItem.getAccountName());
            ((TextView)cell.findViewById(R.id.tvDetail)).setText(bankItem.getBankName() + ", " + bankItem.getBankType());
            ((TextView)cell.findViewById(R.id.tvBalance)).setText("Balance: " + bankItem.getBalance() + "AED");

            final ImageView imgvCircle = (ImageView)cell.findViewById(R.id.imgvCircle);
            final Button btnCheck = (Button)cell.findViewById(R.id.btnCheck);

            if(bankItem.getFlagActive() == 1) {
                imgvCircle.setImageResource(R.drawable.circle_view_blue);
                btnCheck.setBackgroundResource(R.drawable.ic_checked);
            } else {
                imgvCircle.setImageResource(R.drawable.circle_view_grey);
                btnCheck.setBackgroundResource(R.drawable.ic_unchecked);
            }

            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditBankFragment f = new EditBankFragment();
                    String title = Constant.FRAGMENT_EDIT_BANK;
                    f.nIndex = nIdx;

                    FragmentTransaction transaction = mContext.getSupportFragmentManager()
                            .beginTransaction();
                    transaction.add(R.id.content_frame, f, title).addToBackStack(title).commit();
                }
            });

            btnCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(nIdx == nActiveIndex) return;

                    Bank bankSelected = Common.getInstance().listBanks.get(nIdx);

                    imgvCircle.setImageResource(R.drawable.circle_view_blue);
                    btnCheck.setBackgroundResource(R.drawable.ic_checked);

                    bankSelected.setFlagActive(1);
                    Common.getInstance().dbHelper.updateBank(bankSelected);

                    Bank bankPrevious = Common.getInstance().listBanks.get(nActiveIndex);
                    View cellPrevious = lyContainer.getChildAt(nActiveIndex);

                    ((ImageView)cellPrevious.findViewById(R.id.imgvCircle)).setImageResource(R.drawable.circle_view_grey);
                    ((Button)cellPrevious.findViewById(R.id.btnCheck)).setBackgroundResource(R.drawable.ic_unchecked);

                    bankPrevious.setFlagActive(0);
                    Common.getInstance().dbHelper.updateBank(bankPrevious);

                    nActiveIndex = nIdx;

                }
            });

            lyContainer.addView(cell);
        }
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
