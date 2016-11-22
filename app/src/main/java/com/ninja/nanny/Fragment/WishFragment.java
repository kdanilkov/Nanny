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
import com.ninja.nanny.Model.Wish;
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Common;
import com.ninja.nanny.Utils.Constant;

import java.util.ArrayList;
import java.util.List;

public class WishFragment extends CustomFragment {


    public WishFragment() {
        // Required empty public constructor
    }

    LayoutInflater mInflater;
    View mView;
    MainActivity mContext;
    RelativeLayout rlyAllBottom, rlyActiveBottom, rlyFinishedBottom;
    LinearLayout mLyContainer;
    int nSelectedTab;


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
        nSelectedTab = 0;
        rlyActiveBottom.setVisibility(View.VISIBLE);
    }

    void initTab() {
        rlyActiveBottom.setVisibility(View.INVISIBLE);
        rlyAllBottom.setVisibility(View.INVISIBLE);
        rlyFinishedBottom.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        presentData();
    }

    void presentData() {
        mLyContainer.removeAllViews();

        List<Wish> listCurrent = new ArrayList<Wish>();

        switch (nSelectedTab) {
            case 0:
                listCurrent = Common.getInstance().listActiveWishes;
                break;
            case 1:
                listCurrent = Common.getInstance().listAllWishes;
                break;
            case 2:
                listCurrent = Common.getInstance().listFinishedWishes;
        }

        if(listCurrent.size() == 0) {
            Toast.makeText(mContext, "There is no data to display", Toast.LENGTH_SHORT).show();
            return;
        }

        for(int i = 0; i < listCurrent.size(); i ++) {
            final Wish wishItem = listCurrent.get(i);

            View cell = mInflater.inflate(R.layout.cell_wish_item, null);

            boolean flagActive = (wishItem.getFlagActive() == 1);

            if(flagActive) {
                ((ImageView)cell.findViewById(R.id.imgvCircle)).setImageResource(R.drawable.circle_view_blue);
            } else {
                ((ImageView)cell.findViewById(R.id.imgvCircle)).setImageResource(R.drawable.circle_view_grey);
            }

            ((ToggleButton)cell.findViewById(R.id.tbtnSwitch)).setChecked(flagActive);
            ((TextView)cell.findViewById(R.id.tvName)).setText(wishItem.getTitle());
            ((TextView)cell.findViewById(R.id.tvDetail)).setText("saved:" + wishItem.getSavedAmount() + "$ / total:" + wishItem.getTotalAmount() + "$");

            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ViewWishFragment f = new ViewWishFragment();
                    String title = Constant.FRAGMENT_VIEW_WISH;

                    f.wishSelected = wishItem;

                    FragmentTransaction transaction = mContext.getSupportFragmentManager()
                            .beginTransaction();
                    transaction.add(R.id.content_frame, f, title).addToBackStack(title).commit();
                }
            });

            ((ToggleButton)cell.findViewById(R.id.tbtnSwitch)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    compoundButton.setChecked(!b);
//                    if(b) {
//                        wishItem.setFlagActive(1);
//                        Common.getInstance().dbHelper.updateWish(wishItem);
//
//                        int nIdx = 0;
//
//                        for(nIdx = 0; nIdx < Common.getInstance().listAllWishes.size(); nIdx ++) {
//                            Wish wishTmp = Common.getInstance().listAllWishes.get(nIdx);
//
//                            if(wishItem.getId() == wishTmp.getId()) break;
//                        }
//
//                        Common.getInstance().listAllWishes.remove(nIdx);
//                        Common.getInstance().listAllWishes.add(nIdx, wishItem);
//
//                        for(nIdx = 0; nIdx < Common.getInstance().listFinishedWishes.size(); nIdx ++){
//                            Wish wishTmp = Common.getInstance().listFinishedWishes.get(nIdx);
//
//                            if(wishItem.getId() == wishTmp.getId()) break;
//                        }
//
//                        Common.getInstance().listFinishedWishes.remove(nIdx);
//
//                        Common.getInstance().listActiveWishes.add(wishItem);
//                    } else {
//                        wishItem.setFlagActive(0);
//                        Common.getInstance().dbHelper.updateWish(wishItem);
//
//                        int nIdx = 0;
//
//                        for(nIdx = 0; nIdx < Common.getInstance().listAllWishes.size(); nIdx ++) {
//                            Wish wishTmp = Common.getInstance().listAllWishes.get(nIdx);
//
//                            if(wishItem.getId() == wishTmp.getId()) break;
//                        }
//
//                        Common.getInstance().listAllWishes.remove(nIdx);
//                        Common.getInstance().listAllWishes.add(nIdx, wishItem);
//
//                        for(nIdx = 0; nIdx < Common.getInstance().listActiveWishes.size(); nIdx ++){
//                            Wish wishTmp = Common.getInstance().listActiveWishes.get(nIdx);
//
//                            if(wishItem.getId() == wishTmp.getId()) break;
//                        }
//
//                        Common.getInstance().listActiveWishes.remove(nIdx);
//
//                        Common.getInstance().listFinishedWishes.add(wishItem);
//                    }
//
//                    presentData();
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

            case R.id.rlyActive:
                initTab();
                rlyActiveBottom.setVisibility(View.VISIBLE);
                nSelectedTab = 0;
                presentData();
                break;

            case R.id.rlyAll:
                initTab();
                rlyAllBottom.setVisibility(View.VISIBLE);
                nSelectedTab = 1;
                presentData();
                break;

            case R.id.rlyFinished:
                initTab();
                rlyFinishedBottom.setVisibility(View.VISIBLE);
                nSelectedTab = 2;
                presentData();
                break;
        }
    }
}
