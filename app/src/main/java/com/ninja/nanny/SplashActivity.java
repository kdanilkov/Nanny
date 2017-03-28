package com.ninja.nanny;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.ninja.nanny.Helper.DatabaseHelper;
import com.ninja.nanny.Preference.UserPreference;
import com.ninja.nanny.Utils.Common;
import com.ninja.nanny.Utils.Constant;

import java.util.ArrayList;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //Do your operation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);


        Thread m_Thread = new Thread(new Timer());
        m_Thread.start();
    }


    private class Timer implements Runnable{

        @Override
        public void run() {
            try {
                // init common
                Common.getInstance().readBankJsonData(SplashActivity.this);
                Common.getInstance().readTemplateJsonData(SplashActivity.this);

                UserPreference.getInstance().pref = getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
                Common.getInstance().syncSettingInfo();

                Common.getInstance().dbHelper = new DatabaseHelper(getApplicationContext());
//        sampleWishData();
                Common.getInstance().listBanks = Common.getInstance().dbHelper.getAllBanks();
                Common.getInstance().listAllWishes = Common.getInstance().dbHelper.getAllWishes();
                Common.getInstance().listActiveWishes = Common.getInstance().dbHelper.getActiveWishes();
                Common.getInstance().listFinishedWishes = Common.getInstance().dbHelper.getFinishedWishes();
                Common.getInstance().listAllPayments = Common.getInstance().dbHelper.getAllPayments();
                Common.getInstance().listAllTransactions = Common.getInstance().dbHelper.getAllTransactions();
//                Common.getInstance().fillTestTransactions(SplashActivity.this);
                Common.getInstance().listSms = new ArrayList<>();
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(Constant.TAG_CURRENT, Log.getStackTraceString(e));
            }
            //if (Common.getInstance().timestampInitConfig > 0) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            /*} else {
                startActivity(new Intent(SplashActivity.this, WizardActivity.class));
            }*/
            finish();
        }
    }
}
