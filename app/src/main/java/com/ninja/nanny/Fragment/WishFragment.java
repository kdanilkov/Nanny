package com.ninja.nanny.Fragment;


import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ninja.nanny.Custom.CustomFragment;
import com.ninja.nanny.MainActivity;
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Constant;

public class WishFragment extends CustomFragment {


    public WishFragment() {
        // Required empty public constructor
    }

    LayoutInflater mInflater;
    View mView;
    MainActivity mContext;
    RelativeLayout rlyAllBottom, rlyActiveBottom, rlyFinishedBottom;
    LinearLayout mLyContainer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_wish, container, false);
        mContext = (MainActivity)getActivity();
        mInflater = inflater;

        setUI();

        return mView;
    }

    void setUI() {
        mView.findViewById(R.id.btnBack).setOnClickListener(this);
        mView.findViewById(R.id.btnAdd).setOnClickListener(this);
        mView.findViewById(R.id.rlyAll).setOnClickListener(this);
        mView.findViewById(R.id.rlyActive).setOnClickListener(this);
        mView.findViewById(R.id.rlyFinished).setOnClickListener(this);

        rlyAllBottom = (RelativeLayout)mView.findViewById(R.id.rlyAllBottom);
        rlyActiveBottom = (RelativeLayout)mView.findViewById(R.id.rlyActiveBottom);
        rlyFinishedBottom = (RelativeLayout)mView.findViewById(R.id.rlyFinishedBottom);

        mLyContainer = (LinearLayout)mView.findViewById(R.id.lyContainer);

        initTab();
        rlyAllBottom.setVisibility(View.VISIBLE);

        presentData();
    }

    void initTab() {
        rlyAllBottom.setVisibility(View.INVISIBLE);
        rlyActiveBottom.setVisibility(View.INVISIBLE);
        rlyFinishedBottom.setVisibility(View.INVISIBLE);
    }

    void presentData() {
        mLyContainer.removeAllViews();

        String[] arrName = {"Red Dress", "Trip to Maldives", "Ferrari California"};
        String[] arrDetail = {"200$, next week", "1000$, in December", "300,000$, in 2017"};
        boolean[] arrStatus = {true, true, false};

        for(int i = 0; i < arrName.length; i ++) {
            String strName = arrName[i];
            String strDetail = arrDetail[i];
            boolean blStatus = arrStatus[i];

            View cell = mInflater.inflate(R.layout.cell_wish_item, null);

            if(blStatus) {
                ((ImageView)cell.findViewById(R.id.imgvCircle)).setImageResource(R.drawable.ic_circle_blue);
            } else {
                ((ImageView)cell.findViewById(R.id.imgvCircle)).setImageResource(R.drawable.ic_circle_gray);
            }

            ((ToggleButton)cell.findViewById(R.id.tbtnSwitch)).setChecked(blStatus);
            ((TextView)cell.findViewById(R.id.tvName)).setText(strName);
            ((TextView)cell.findViewById(R.id.tvDetail)).setText(strDetail);

            final int pos = i;

            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ViewWishFragment f = new ViewWishFragment();
                    String title = Constant.FRAGMENT_VIEW_WISH;

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
                NewWishFragment f = new NewWishFragment();
                String title = Constant.FRAGMENT_NEW_WISH;

                FragmentTransaction transaction = mContext.getSupportFragmentManager()
                        .beginTransaction();
                transaction.add(R.id.content_frame, f, title).addToBackStack(title).commit();
                break;

            case R.id.rlyAll:
                initTab();
                rlyAllBottom.setVisibility(View.VISIBLE);
                break;

            case R.id.rlyActive:
                initTab();
                rlyActiveBottom.setVisibility(View.VISIBLE);
                break;

            case R.id.rlyFinished:
                initTab();
                rlyFinishedBottom.setVisibility(View.VISIBLE);
                break;
        }
    }
}
