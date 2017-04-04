package com.ninja.nanny;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.ninja.nanny.Custom.CustomActivity;
import com.ninja.nanny.Custom.MediumTextView;
import com.ninja.nanny.Fragment.BaseWizardFragment;
import com.ninja.nanny.Fragment.WizardAmountPerDayFragment;
import com.ninja.nanny.Fragment.WizardAverageIncomeFragment;
import com.ninja.nanny.Fragment.WizardBalanceFragment;
import com.ninja.nanny.Fragment.WizardSelectBankFragment;
import com.ninja.nanny.Fragment.WizardSpentFragment;
import com.ninja.nanny.Fragment.WizardStartPeriodFragment;
import com.ninja.nanny.Model.Bank;
import com.ninja.nanny.Model.SettingWizardModel;
import com.ninja.nanny.Utils.Common;
import com.ninja.nanny.Utils.SmsTransactionFiller;
import com.ninja.nanny.Utils.Tester;
import com.ninja.nanny.Utils.WizardSteps;

import java.io.InputStream;

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
        // fill fake Sms messagesat app start.
        // Simulates a case when you install app and already have Sms bank messages.
        // Beware: data wipe may be needed to avoid duplicates.
//        Tester.fillSmsEndb(WizardActivity.this);
        Tester.fillSmsRak(WizardActivity.this);
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
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void setStep() {
        String title = "";
        BaseWizardFragment f;
        switch (mCurrentStep) {
            case Bank:
                f = new WizardSelectBankFragment();
                ((MediumTextView)findViewById(R.id.textView3)).setText(getString(R.string.step_1));
                break;
            case Balance:
                f = new WizardBalanceFragment();
                ((MediumTextView)findViewById(R.id.textView3)).setText(getString(R.string.step_2));
                break;
            case Period:
                f = new WizardStartPeriodFragment();
                ((MediumTextView)findViewById(R.id.textView3)).setText(getString(R.string.step_3));
                break;
            case Spent:
                f = new WizardSpentFragment();
                ((MediumTextView)findViewById(R.id.textView3)).setText(getString(R.string.step_4));
                break;
            case AverageIncome:
                f = new WizardAverageIncomeFragment();
                ((MediumTextView)findViewById(R.id.textView3)).setText(getString(R.string.step_5));
                break;
            case AmountPerDay:
                f = new WizardAmountPerDayFragment();
                ((MediumTextView)findViewById(R.id.textView3)).setText(getString(R.string.step_6));
                break;
            default:
                startActivity(new Intent(WizardActivity.this, MainActivity.class));
                finish();
                return;
        }
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
                mCurrentStep = WizardSteps.AmountPerDay;
                break;
            case AmountPerDay:
                startActivity(new Intent(WizardActivity.this, MainActivity.class));
                finish();
                return;
        }
        setStep();
    }

//    public void fillSms(View v) {
//        Bank bank = new Bank("Account 1", 0, 0, 1, 0);
//        InputStream is = getResources().openRawResource(R.raw.demobank_endb);
//        SmsTransactionFiller.fillSmsFromDemoJSON(bank, is);
//    }
}
