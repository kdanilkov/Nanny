package com.ninja.nanny.Fragment;


import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ninja.nanny.Custom.CustomFragment;
import com.ninja.nanny.MainActivity;
import com.ninja.nanny.Model.Payment;
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Common;
import com.ninja.nanny.Utils.Constant;

import java.util.List;

public class PaymentFragment extends CustomFragment {


    public PaymentFragment() {
        // Required empty public constructor
    }

    LayoutInflater mInflater;
    View mView;
    MainActivity mContext;
    LinearLayout mLyContainer;
    boolean isCalendarClicked;
    RelativeLayout rlyThisPeriodBottom, rlyAllBottom;
    int nSelectedTab;

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
        mView.findViewById(R.id.btnCalendar).setOnClickListener(this);
        mView.findViewById(R.id.rlyThisPeriod).setOnClickListener(this);
        mView.findViewById(R.id.rlyAll).setOnClickListener(this);

        rlyThisPeriodBottom = (RelativeLayout)mView.findViewById(R.id.rlyThisPeriodBottom);
        rlyAllBottom = (RelativeLayout)mView.findViewById(R.id.rlyAllBottom);
        mLyContainer = (LinearLayout)mView.findViewById(R.id.lyContainer);

        initTab();
        nSelectedTab = 0;
        rlyThisPeriodBottom.setVisibility(View.VISIBLE);
    }

    void initTab() {
        rlyThisPeriodBottom.setVisibility(View.INVISIBLE);
        rlyAllBottom.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        presentData();
        isCalendarClicked = false;
    }

    void presentData() {
        mLyContainer.removeAllViews();

//        String[] arrName = {"Savings 30%", "Tenancy", "Hot Water", "Mobile", "Internet", "TV"};
//        String[] arrDetail = {"500$, to Savings Account", "500$, to Provider", "500$, to Provider", "200$, to Provider", "200$, to Provider", "75$, to Provider"};
//        int[] arrStatus = {2, 1, 1, 1, 1, 0}; //2-green, 1-blue, 0-false(gray)

        if(Common.getInstance().listAllPayments.size() == 0) {
            Toast.makeText(mContext, "There is no data to show", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Payment> listActive = Common.getInstance().listAllPayments;

        if(nSelectedTab == 0){
            listActive = Common.getInstance().getCurrentPayments();
        }

        for(int i = 0; i < listActive.size(); i ++) {
            final Payment payment = listActive.get(i);

            int nPaymentMode = payment.getPaymentMode();
            int nPaidStatus = payment.getPaidStatus();

            String strDetail = payment.getAmount() + "$, to Savings Account";

            if(nPaymentMode > 1) {
                strDetail = payment.getAmount() + "$, to Provider";
            }

            View cell = mInflater.inflate(R.layout.cell_payment_item, null);

            final ImageView imgvCircle = (ImageView)cell.findViewById(R.id.imgvCircle);
            final ToggleButton tbSwitch = (ToggleButton)cell.findViewById(R.id.tbtnSwitch);

            if(nPaymentMode < 2) {
                if(nPaidStatus == 0) {
                    tbSwitch.setChecked(true);
                    tbSwitch.setBackgroundResource(R.drawable.tbtn_selector_yellow);
                    imgvCircle.setBackgroundResource(R.drawable.circle_view_yellow);
                } else {
                    tbSwitch.setChecked(false);
                    tbSwitch.setBackgroundResource(R.drawable.tbtn_selector_orange);
                    imgvCircle.setBackgroundResource(R.drawable.circle_view_orange);
                }
            } else {
                if(nPaidStatus == 0) {
                    tbSwitch.setChecked(true);
                    tbSwitch.setBackgroundResource(R.drawable.tbtn_selector_blue);
                    imgvCircle.setBackgroundResource(R.drawable.circle_view_blue);
                } else {
                    tbSwitch.setChecked(false);
                    tbSwitch.setBackgroundResource(R.drawable.tbtn_selector_green);
                    imgvCircle.setBackgroundResource(R.drawable.circle_view_green);
                }
            }

            ((TextView)cell.findViewById(R.id.tvName)).setText(payment.getTitle());
            ((TextView)cell.findViewById(R.id.tvDetail)).setText(strDetail);

            tbSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b) {
                        tbSwitch.setChecked(false);
                        return;
                    }

                    tbSwitch.setChecked(true);

//                    int nPaymentMode = payment.getPaymentMode();
//
//                    if(nPaymentMode < 2) {
//                        tbSwitch.setBackgroundResource(R.drawable.tbtn_selector_light_orange);
//                        imgvCircle.setBackgroundResource(R.drawable.circle_view_light_orange);
//                    } else {
//                        tbSwitch.setBackgroundResource(R.drawable.tbtn_selector_light_blue);
//                        imgvCircle.setBackgroundResource(R.drawable.circle_view_light_blue);
//                    }

                    ExpenseAsPayedFragment f = new ExpenseAsPayedFragment();
                    String title = Constant.FRAGMENT_EXPENSE_AS_PAYED;
                    f.paymentSelected = payment;

                    FragmentTransaction transaction = mContext.getSupportFragmentManager()
                            .beginTransaction();
                    transaction.add(R.id.content_frame, f, title).addToBackStack(title).commit();
                }
            });

            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditPaymentFragment f = new EditPaymentFragment();
                    String title = Constant.FRAGMENT_EDIT_PAYMENT;
                    f.paymentSelected = payment;

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

            case R.id.btnCalendar:
                if(!isCalendarClicked) {
                    CalendarPaymentFragment f1 = new CalendarPaymentFragment();
                    String title1 = Constant.FRAGMENT_CALENDAR_PAYMENT;

                    FragmentTransaction transaction1 = mContext.getSupportFragmentManager()
                            .beginTransaction();
                    transaction1.add(R.id.content_frame, f1, title1).addToBackStack(title1).commit();

                    isCalendarClicked = true;
                }

                break;

            case R.id.rlyThisPeriod:
                initTab();
                rlyThisPeriodBottom.setVisibility(View.VISIBLE);
                nSelectedTab = 0;
                presentData();
                break;

            case R.id.rlyAll:
                initTab();
                rlyAllBottom.setVisibility(View.VISIBLE);
                nSelectedTab = 1;
                presentData();
                break;
        }
    }

}
