package com.ninja.nanny.Fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ninja.nanny.Custom.CustomFragment;
import com.ninja.nanny.FancyChart.ChartData;
import com.ninja.nanny.FancyChart.FancyChart;
import com.ninja.nanny.MainActivity;
import com.ninja.nanny.Model.Wish;
import com.ninja.nanny.Model.WishSaving;
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Common;
import com.ninja.nanny.Utils.Constant;

import java.util.List;

import at.grabner.circleprogress.CircleProgressView;


public class ViewWishFragment extends CustomFragment {


    public ViewWishFragment() {
        // Required empty public constructor
    }

    LayoutInflater mInflater;
    View mView;
    MainActivity mContext;
    CircleProgressView mCircleView;
    public Wish wishSelected;
    List<WishSaving> listSaving;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_view_wish, container, false);
        mContext = (MainActivity)getActivity();
        mInflater = inflater;

        setUI();

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();

        setUI();
    }

    void setUI() {
        int nWishId = wishSelected.getId();
        listSaving = Common.getInstance().dbHelper.getAllWishSavings(nWishId);


        mView.findViewById(R.id.btnBack).setOnClickListener(this);
        mView.findViewById(R.id.btnDelete).setOnClickListener(this);
        mView.findViewById(R.id.btnEdit).setOnClickListener(this);

        mCircleView = (CircleProgressView)mView.findViewById(R.id.circleView);

        ((TextView)mView.findViewById(R.id.tvTitle)).setText(wishSelected.getTitle());
        ((TextView)mView.findViewById(R.id.tvAmount)).setText(wishSelected.getTotalAmount() + "");
        ((TextView)mView.findViewById(R.id.tvPayment)).setText(wishSelected.getMonthlyPayment() + "");
        ((TextView)mView.findViewById(R.id.tvSaved)).setText(wishSelected.getSavedAmount() + "");
        ((TextView)mView.findViewById(R.id.tvLeft)).setText((wishSelected.getTotalAmount() - wishSelected.getSavedAmount()) + "");

        int nPercent = wishSelected.getSavedAmount() * 100 / wishSelected.getTotalAmount();

        mCircleView.setValue(nPercent);

        FancyChart chart = (FancyChart)mView.findViewById(R.id.fancyChart);

        // First data set
        ChartData data1 = new ChartData(ChartData.LINE_COLOR_BLUE);
        ChartData data2 = new ChartData(ChartData.LINE_COLOR_RED);
//        int[] yValues = new int[]{0, 8, 9, 18, 35, 30, 33, 32, 46, 53, 50, 42};
        data1.addPoint(0, 0);
        data2.addPoint(0, 0);

        data1.addXValue(0, "0");
        data2.addXValue(0, "0");

        int nSum = 0;

        for(int i = 1; i < listSaving.size() + 1; i++) {
            WishSaving wishSaving = listSaving.get(i - 1);
            nSum += wishSaving.getSavedAmount();

            data1.addPoint(i, wishSaving.getSavedAmount());
            data2.addPoint(i, nSum);

            int nSavingDate = wishSaving.getDateCreated();
            int nMonth = nSavingDate % 100;
            int nYear = (nSavingDate / 100) % 100; // in case  of 2016, result is 16

            data1.addXValue(i, nYear + "/" + nMonth);
            data2.addXValue(i, nYear + "/" + nMonth);
        }

        chart.addData(data1);
        chart.addData(data2);

        // Second data set
//        ChartData data2 = new ChartData(ChartData.LINE_COLOR_RED);
//        int[] yValues2 = new int[]{0, 5, 9, 23, 15, 35, 45, 50, 41, 45, 32, 24};
//        for(int i = 8; i <= 19; i++) {
//            data2.addPoint(i, yValues2[i-8]);
//            data2.addXValue(i, i + ":00");
//        }
//        chart.addData(data2);

    }

    void removeItem() {
        new AlertDialog.Builder(mContext)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        Common.getInstance().dbHelper.deleteBank(wishSelected.getId());

                        int nIdx = 0;

                        for(nIdx = 0; nIdx < Common.getInstance().listAllWishes.size(); nIdx ++) {
                            Wish wishTmp = Common.getInstance().listAllWishes.get(nIdx);

                            if(wishTmp.getId() == wishSelected.getId()) break;
                        }

                        Common.getInstance().dbHelper.deleteWish(wishSelected.getId());
                        Common.getInstance().dbHelper.deleteWishSavingGroup(wishSelected.getId());
                        Common.getInstance().listAllWishes.remove(nIdx);

                        if(wishSelected.getFlagActive() == 0) {
                            for(nIdx = 0; nIdx < Common.getInstance().listFinishedWishes.size(); nIdx ++) {
                                Wish wishTmp = Common.getInstance().listFinishedWishes.get(nIdx);

                                if(wishTmp.getId() == wishSelected.getId()) break;
                            }

                            Common.getInstance().listFinishedWishes.remove(nIdx);
                        } else {
                            for(nIdx = 0; nIdx < Common.getInstance().listActiveWishes.size(); nIdx ++) {
                                Wish wishTmp = Common.getInstance().listActiveWishes.get(nIdx);

                                if(wishTmp.getId() == wishSelected.getId()) break;
                            }

                            Common.getInstance().listActiveWishes.remove(nIdx);
                        }

                        mContext.getSupportFragmentManager().popBackStackImmediate();
                        Toast.makeText(mContext, "wish info has been deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                mContext.getSupportFragmentManager().popBackStackImmediate();
                break;

            case R.id.btnDelete:
                removeItem();
                break;

            case R.id.btnEdit:
                EditWishFragment f = new EditWishFragment();
                String title = Constant.FRAGMENT_EDIT_WISH;

                f.wishSelected = wishSelected;

                FragmentTransaction transaction = mContext.getSupportFragmentManager()
                        .beginTransaction();
                transaction.add(R.id.content_frame, f, title).addToBackStack(title).commit();
                break;
        }
    }

}
