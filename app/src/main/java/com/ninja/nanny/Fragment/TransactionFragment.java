package com.ninja.nanny.Fragment;


import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ninja.nanny.Custom.CustomFragment;
import com.ninja.nanny.MainActivity;
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Constant;

public class TransactionFragment extends CustomFragment {


    public TransactionFragment() {
        // Required empty public constructor
    }


    LayoutInflater mInflater;
    View mView;
    MainActivity mContext;
    LinearLayout mLyContainer;

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
        mView.findViewById(R.id.btnBack).setOnClickListener(this);
        mLyContainer = (LinearLayout)mView.findViewById(R.id.lytContainer);

        presentData();
    }

    void presentData() {
        String[] arrDate = {"12 October 2016", "11 October 2016", "10 October 2016", "09 October 2016", "12 September 2016", "12 August 2016"};
        String[] arrDetail = {"Emirates NBD, 100$ > Internet", "Emirates NBD, 100$ > TV", "Emirates NBD, 100$ > Loan", "Emirates NBD, 100$ > Credit Card", "Emirates NBD, 100$ > Coffee", "Emirates NBD, 100$ > Internet"};

        mLyContainer.removeAllViews();

        for(int i = 0; i < arrDate.length; i ++) {
            String strDate = arrDate[i];
            String strDetail = arrDetail[i];

            View cell = mInflater.inflate(R.layout.cell_transaction_item, null);

            ((TextView)cell.findViewById(R.id.tvDate)).setText(strDate);
            ((TextView)cell.findViewById(R.id.tvDetail)).setText(strDetail);

            final int pos = i;

            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TransactionDetailFragment f = new TransactionDetailFragment();
                    String title = Constant.FRAGMENT_TRANSACTION_DETAIL;

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
                mContext.toggleMenu();
                break;
        }
    }
}
