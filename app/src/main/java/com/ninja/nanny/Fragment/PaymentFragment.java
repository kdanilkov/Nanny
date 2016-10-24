package com.ninja.nanny.Fragment;


import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ninja.nanny.Custom.CustomFragment;
import com.ninja.nanny.MainActivity;
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Constant;

public class PaymentFragment extends CustomFragment {


    public PaymentFragment() {
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
        mView = inflater.inflate(R.layout.fragment_payment, container, false);
        mContext = (MainActivity)getActivity();
        mInflater = inflater;

        setUI();

        return mView;
    }

    void setUI() {
        mView.findViewById(R.id.btnBack).setOnClickListener(this);
        mView.findViewById(R.id.btnAdd).setOnClickListener(this);

        mLyContainer = (LinearLayout)mView.findViewById(R.id.lyContainer);

        presentData();
    }

    void presentData() {
        mLyContainer.removeAllViews();

        String[] arrName = {"Savings 30%", "Tenancy", "Hot Water", "Mobile", "Internet", "TV"};
        String[] arrDetail = {"500$, to Savings Account", "500$, to Provider", "500$, to Provider", "200$, to Provider", "200$, to Provider", "75$, to Provider"};
        int[] arrStatus = {2, 1, 1, 1, 1, 0}; //2-green, 1-blue, 0-false(gray)

        for(int i = 0; i < arrName.length; i ++) {
            String strName = arrName[i];
            String strDetail = arrDetail[i];
            int nStatus = arrStatus[i];

            View cell = mInflater.inflate(R.layout.cell_wish_item, null);

            ImageView imgvCircle = (ImageView)cell.findViewById(R.id.imgvCircle);
            ToggleButton tbSwitch = (ToggleButton)cell.findViewById(R.id.tbtnSwitch);

            switch (nStatus) {
                case 0:
                    imgvCircle.setImageResource(R.drawable.ic_circle_gray);
                    tbSwitch.setChecked(false);
                    break;
                case 1:
                    imgvCircle.setImageResource(R.drawable.ic_circle_blue);
                    tbSwitch.setBackgroundResource(R.drawable.tbtn_selector_blue);
                    tbSwitch.setChecked(true);
                    break;
                case 2:
                    imgvCircle.setImageResource(R.drawable.ic_circle_green);
                    tbSwitch.setBackgroundResource(R.drawable.tbtn_selector_green);
                    tbSwitch.setChecked(true);
                    break;
            }

            ((TextView)cell.findViewById(R.id.tvName)).setText(strName);
            ((TextView)cell.findViewById(R.id.tvDetail)).setText(strDetail);

            final int pos = i;

            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ExpenseAsPayedFragment f = new ExpenseAsPayedFragment();
                    String title = Constant.FRAGMENT_EXPENSE_AS_PAYED;

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

            case R.id.btnAdd:
                NewPaymentFragment f = new NewPaymentFragment();
                String title = Constant.FRAGMENT_NEW_PAYMENT;

                FragmentTransaction transaction = mContext.getSupportFragmentManager()
                        .beginTransaction();
                transaction.add(R.id.content_frame, f, title).addToBackStack(title).commit();
                break;
        }
    }

}
