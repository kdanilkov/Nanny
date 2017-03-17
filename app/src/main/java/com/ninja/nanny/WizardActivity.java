package com.ninja.nanny;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ninja.nanny.Comparator.SmsComparator;
import com.ninja.nanny.Custom.CustomActivity;
import com.ninja.nanny.Fragment.BaseWizardFragment;
import com.ninja.nanny.Fragment.WizardAverageIncomeFragment;
import com.ninja.nanny.Fragment.WizardBalanceFragment;
import com.ninja.nanny.Fragment.WizardSelectBankFragment;
import com.ninja.nanny.Fragment.WizardSpentFragment;
import com.ninja.nanny.Fragment.WizardStartPeriodFragment;
import com.ninja.nanny.Model.SettingWizardModel;
import com.ninja.nanny.Model.Sms;
import com.ninja.nanny.Utils.Common;
import com.ninja.nanny.Utils.WizardSteps;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by petra on 10.03.2017.
 */

public class WizardActivity extends CustomActivity {

    private WizardSteps mCurrentStep = WizardSteps.Bank;
    private SettingWizardModel mWizardModel;
    private BaseWizardFragment mCurrentFragment;
    public WizardActivity(){
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard);
        if(!weHavePermissionToReadSMS()) {
            requestReadSMSPermissionFirst();
        }
        else {
            syncSms();
        }
        mWizardModel = new SettingWizardModel();
        setStep();
    }

    private boolean weHavePermissionToReadSMS() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestReadSMSPermissionFirst() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_SMS)) {
            Toast.makeText(this, "We need permission so you can text your friends.", Toast.LENGTH_LONG).show();
            requestForResultSMSPermission();
        } else {
            requestForResultSMSPermission();
        }
    }

    private void requestForResultSMSPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_SMS}, 123);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission For Reading Sms Granted", Toast.LENGTH_SHORT).show();
            syncSms();
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    void syncSms() {
        if(Common.getInstance().listSms != null) {
            if (Common.getInstance().listAllTransactions == null || Common.getInstance().listAllTransactions.size() == 0) {
                Common.getInstance().fillAllTransactions();
            }
            return;
        }
        Common.getInstance().listSms = new ArrayList<>();

        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(message, null, null, null, null);

        int totalSMS = c.getCount();

        if(c.moveToFirst()) {
            for(int i = 0; i < totalSMS; i ++) {
                long lTimeStamp = c.getLong(c.getColumnIndexOrThrow("date"));

                Sms objSms = new Sms();

                objSms.setAddress(c.getString(c.getColumnIndexOrThrow("address")));
                objSms.setText(c.getString(c.getColumnIndexOrThrow("body")));
                objSms.setTimestamp(lTimeStamp);

                int sms_id = Common.getInstance().dbHelper.createSMS(objSms);
                objSms.setId(sms_id);

                Common.getInstance().listSms.add(objSms);

                c.moveToNext();
            }
        }
        c.close();

        Collections.sort(Common.getInstance().listSms, new SmsComparator());
        Common.getInstance().fillAllTransactions();
    }

    private void setStep() {
        String title = "";
        BaseWizardFragment f;
        switch (mCurrentStep) {
            case Bank:
                f = new WizardSelectBankFragment();
                break;
            case Balance:
                f = new WizardBalanceFragment();
                ((Button)findViewById(R.id.btnNext)).setText(getString(R.string.save_and_next_button));
                break;
            case Period:
                f = new WizardStartPeriodFragment();
                break;
            case Spent:
                f = new WizardSpentFragment();
                break;
            case AverageIncome:
                f = new WizardAverageIncomeFragment();
                ((Button)findViewById(R.id.btnNext)).setText(getString(R.string.finish_button));
                break;
            default:
                startActivity(new Intent(WizardActivity.this, MainActivity.class));
                finish();
                return;
        }
        f.setModel(mWizardModel);
        mCurrentFragment = f;
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mCurrentFragment, title).commit();
    }

    public void onNextStep(View v) {
        // validate data on current step
        if(!mCurrentFragment.isValidate()) {
            return;
        }
        // fill wizard data in current fragment
        mCurrentFragment.setData();
        switch (mCurrentStep) {
            case Bank:
                mCurrentStep = WizardSteps.Balance;
                break;
            case Balance:
                mCurrentStep = WizardSteps.Period;
                break;
            case Period:
                mCurrentStep = WizardSteps.Spent;
                break;
            case Spent:
                mCurrentStep = WizardSteps.AverageIncome;
                break;
            case AverageIncome:
                startActivity(new Intent(WizardActivity.this, MainActivity.class));
                break;
        }
        setStep();
    }
}
