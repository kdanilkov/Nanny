package com.ninja.nanny.Fragment;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ninja.nanny.Custom.CustomFragment;
import com.ninja.nanny.MainActivity;
import com.ninja.nanny.Model.Payment;
import com.ninja.nanny.Model.Transaction;
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Common;
import com.ninja.nanny.Utils.Constant;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TransactionFragment extends CustomFragment {


    public TransactionFragment() {
        // Required empty public constructor
    }


    LayoutInflater mInflater;
    View mView;
    MainActivity mContext;
    LinearLayout mLyContainer;
    final SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
    public int nMode;
    public Payment paymentSelected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_transaction, container, false);
        mContext = (MainActivity)getActivity();
        mInflater = inflater;

        setUI();

        return mView;
    }

    void setUI() {
        if(nMode == 0) {
            mView.findViewById(R.id.btnBack).setBackgroundResource(R.drawable.ic_menu);
        } else {
            mView.findViewById(R.id.btnBack).setBackgroundResource(R.drawable.ic_back);
        }

        mView.findViewById(R.id.btnBack).setOnClickListener(this);
        mLyContainer = (LinearLayout)mView.findViewById(R.id.lyContainer);

        presentData();
    }

    void presentData() {
//        String[] arrDate = {"12 October 2016", "11 October 2016", "10 October 2016", "09 October 2016", "12 September 2016", "12 August 2016"};
//        String[] arrDetail = {"Emirates NBD, 100$ > Internet", "Emirates NBD, 100$ > TV", "Emirates NBD, 100$ > Loan", "Emirates NBD, 100$ > Credit Card", "Emirates NBD, 100$ > Coffee", "Emirates NBD, 100$ > Internet"};

        mLyContainer.removeAllViews();

        for(int i = 0; i < Common.getInstance().listTransactions.size(); i ++) {
            final Transaction transaction = Common.getInstance().listTransactions.get(i);

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(transaction.getTimestampCreated());
            Date date = cal.getTime();

            View cell = mInflater.inflate(R.layout.cell_transaction_item, null);

            ((TextView)cell.findViewById(R.id.tvDate)).setText(formatter.format(date));
            String strText = transaction.getText();
            if(strText.length() > 20) strText = strText.substring(0, 20);
            ((TextView)cell.findViewById(R.id.tvDetail)).setText(strText);

            final int pos = i;

            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(nMode == 1) {
                        paymentSelected.setPaidStatus(1);
                        Common.getInstance().dbHelper.updatePayment(paymentSelected);

                        mContext.getSupportFragmentManager().popBackStack(Constant.FRAGMENT_EXPENSE_AS_PAYED, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                        mContext.getSupportFragmentManager().popBackStackImmediate();
                        return;
                    }
                    TransactionDetailFragment f = new TransactionDetailFragment();
                    String title = Constant.FRAGMENT_TRANSACTION_DETAIL;
                    f.smsItem = Common.getInstance().dbHelper.getSms(transaction.getSmsId());

                    FragmentTransaction transaction = mContext.getSupportFragmentManager()
                            .beginTransaction();
                    transaction.add(R.id.content_frame, f, title).addToBackStack(title).commit();
                }
            });

            mLyContainer.addView(cell);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                if(nMode == 1) {
                    mContext.getSupportFragmentManager().popBackStackImmediate();
                } else {
                    mContext.toggleMenu();
                }

                break;
        }
    }
}
