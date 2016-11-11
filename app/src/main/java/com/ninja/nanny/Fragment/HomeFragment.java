package com.ninja.nanny.Fragment;


import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ninja.nanny.Custom.CustomFragment;
import com.ninja.nanny.MainActivity;
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Common;
import com.ninja.nanny.Utils.Constant;

public class HomeFragment extends CustomFragment {


    public HomeFragment() {
        // Required empty public constructor
    }

    LayoutInflater mInflater;
    View mView;
    MainActivity mContext;
    TextView tvLeftThisWeek, tvLeftThisMonth, tvUpcomingPayments, tvSpentForWish, tvShowDebit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        mContext = (MainActivity)getActivity();
        mInflater = inflater;

        setUI();

        return mView;
    }

    void setUI() {
        mView.findViewById(R.id.btnAdvice).setOnClickListener(this);
        mView.findViewById(R.id.btnMenu).setOnClickListener(this);

        tvLeftThisWeek = (TextView)mView.findViewById(R.id.tvLeftThisWeek);
        tvLeftThisMonth = (TextView)mView.findViewById(R.id.tvLeftThisMonth);
        tvUpcomingPayments = (TextView)mView.findViewById(R.id.tvUpcomingPayment);
        tvSpentForWish = (TextView)mView.findViewById(R.id.tvSpentForWish);
        tvShowDebit = (TextView)mView.findViewById(R.id.tvMoneyDebit);

        tvLeftThisWeek.setText(Common.getInstance().leftOnThisWeek() + " $");
        tvLeftThisMonth.setText(Common.getInstance().leftOnThisMonth() + " $");
        tvUpcomingPayments.setText(Common.getInstance().upcomingPayments() + " $");
        tvSpentForWish.setText(Common.getInstance().sumOfWishesForMonth() + " $");
        tvShowDebit.setText("");
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnAdvice:
                AdviceFragment f = new AdviceFragment();
                String title = Constant.FRAGMENT_ADVICE;

                FragmentTransaction transaction = mContext.getSupportFragmentManager()
                        .beginTransaction();
                transaction.add(R.id.content_frame, f, title).addToBackStack(title).commit();
                break;
            case R.id.btnMenu:
                mContext.toggleMenu();
                break;
            default:
        }
    }


}
