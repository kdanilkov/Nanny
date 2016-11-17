package com.ninja.nanny;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.ninja.nanny.Adapter.LeftNavAdapter;
import com.ninja.nanny.Custom.CustomActivity;
import com.ninja.nanny.Fragment.AddBankFragment;
import com.ninja.nanny.Fragment.BankFragment;
import com.ninja.nanny.Fragment.HomeFragment;
import com.ninja.nanny.Fragment.PaymentFragment;
import com.ninja.nanny.Fragment.SettingFragment;
import com.ninja.nanny.Fragment.TransactionFragment;
import com.ninja.nanny.Fragment.WishFragment;
import com.ninja.nanny.Helper.DatabaseHelper;
import com.ninja.nanny.Model.Payment;
import com.ninja.nanny.Model.Sms;
import com.ninja.nanny.Model.Transaction;
import com.ninja.nanny.Model.Wish;
import com.ninja.nanny.Model.WishSaving;
import com.ninja.nanny.Preference.UserPreference;
import com.ninja.nanny.Utils.Common;
import com.ninja.nanny.Utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends CustomActivity {

    private DrawerLayout drawerLayout;

    /** ListView for left side drawer. */
    private ListView drawerLeft;

    /** The left navigation list adapter. */
    private LeftNavAdapter adapterLeft;

    private boolean isMenuOpened;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //Do your operation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initSetting();
        setupDrawer();
        setupContainer();

    }

    void produceVirtualSms() {
        String[] arrText = getResources().getStringArray(R.array.sample_sms_data);

        Common.getInstance().listSms = new ArrayList<Sms>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -15);

        for(int i = 0; i < arrText.length; i ++) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
            Sms sms = new Sms(i + 1, "EmiratesNBD", arrText[i], cal.getTimeInMillis());
            Common.getInstance().listSms.add(sms);
        }
    }

    void initSetting() {
        try {
            Common.getInstance().jsonArrayBankInfo = new JSONArray(Constant.strBankJsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        UserPreference.getInstance().pref = getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
        Common.getInstance().dbHelper = new DatabaseHelper(getApplicationContext());
        Common.getInstance().listBanks = Common.getInstance().dbHelper.getAllBanks();
        Common.getInstance().listAllWishes = Common.getInstance().dbHelper.getAllWishes();
        Common.getInstance().listActiveWishes = Common.getInstance().dbHelper.getActiveWishes();
        Common.getInstance().listFinishedWishes = Common.getInstance().dbHelper.getFinishedWishes();
        Common.getInstance().listAllPayments = Common.getInstance().dbHelper.getAllPayments();
        Common.getInstance().listSms = Common.getInstance().dbHelper.getAllSms();
        Common.getInstance().listTransactions = new ArrayList<Transaction>();
        syncSettingInfo();
        checkPayingWishes();
        produceVirtualSms();

        if(Common.getInstance().isActiveBankExist()) {
            Common.getInstance().getTransaction();
        }


//        if (weHavePermissionToReadSMS()) {
//            syncSms();
//        } else {
//            requestReadSMSPermissionFirst();
//        }

        processPayments();

    }

    void checkPayingWishes() {
        Calendar c = Calendar.getInstance();
        int nDayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        int nMonth = c.get(Calendar.MONTH);
        int nYear = c.get(Calendar.YEAR);

        if(nDayOfMonth >= Common.getInstance().nSalaryDate) {
            nMonth ++;
            if(nMonth == 12) {
                nYear ++;
                nMonth = 0;
            }
        }

        int nDateSaving = nYear * 100 + nMonth;

        for(int i = 0; i < Common.getInstance().listActiveWishes.size(); i ++) {
            Wish wish = Common.getInstance().listActiveWishes.get(i);

            int nLastSavingId = wish.getLastSavingId();

            if(nLastSavingId >= 0) {
                WishSaving wishSaving = Common.getInstance().dbHelper.getWishSaving(nLastSavingId);

                if(wishSaving.getDateCreated() == nDateSaving) {
                    continue;
                }
            }

            int nTotalAmount = wish.getTotalAmount();
            int nSavingAmount = wish.getMonthlyPayment();
            int nUpdatedSavedAmount = wish.getSavedAmount() + nSavingAmount;

            if(nUpdatedSavedAmount > nTotalAmount) {
                nUpdatedSavedAmount = nTotalAmount;
                nSavingAmount = nTotalAmount - wish.getSavedAmount();
            }

            WishSaving wishSaving = new WishSaving(wish.getId(), nSavingAmount, nDateSaving);
            int nWishSavingId = Common.getInstance().dbHelper.createWishSaving(wishSaving);
            wishSaving.setId(nWishSavingId);

            wish.setLastSavingId(nWishSavingId);
            wish.setSavedAmount(nUpdatedSavedAmount);

            if(nUpdatedSavedAmount == nTotalAmount) {
                wish.setFlagActive(0); // this is finished
            }

            Common.getInstance().dbHelper.updateWish(wish);
        }

        Common.getInstance().listActiveWishes = Common.getInstance().dbHelper.getActiveWishes();
        Common.getInstance().listFinishedWishes = Common.getInstance().dbHelper.getFinishedWishes();
    }

    void syncSettingInfo() {
        Common.getInstance().nMinimalDayAmount = UserPreference.getInstance().getSharedPreference(Constant.PREF_KEY_MINIMAL_AMOUNT_PER_DAY, 0);
        Common.getInstance().nSalaryDate = UserPreference.getInstance().getSharedPreference(Constant.PREF_KEY_SALARY_DATE, 15);
        Common.getInstance().nMonthlyIncome = UserPreference.getInstance().getSharedPreference(Constant.PREF_KEY_MONTHLY_INCOME, 0);
        Common.getInstance().nUsedAmount = UserPreference.getInstance().getSharedPreference(Constant.PREF_KEY_USED_SALARY, 0);
        Common.getInstance().nToleranceDays = UserPreference.getInstance().getSharedPreference(Constant.PREF_KEY_TOLERANCE_DAYS, 2);
        Common.getInstance().nTolerancePercents = UserPreference.getInstance().getSharedPreference(Constant.PREF_KEY_TOLERANCE_PERCENT, 5);
    }

    void processPayments() {
        Collections.sort(Common.getInstance().listAllPayments, new PaymentComparator());

        Common.getInstance().listCurrentPayments = new ArrayList<Payment>();

        Calendar cal = Calendar.getInstance();
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayNow = cal.get(Calendar.DAY_OF_MONTH);
        int daySalary = UserPreference.getInstance().getSharedPreference(Constant.PREF_KEY_SALARY_DATE, 15);
        int nLowDayLimit = 0;
        int nHighDayLimit = 0;

        if(dayNow <= daySalary) {
            nHighDayLimit = yearNow * 32 * 12 + monthNow * 32 + daySalary;

            monthNow --;
            if(monthNow == -1) {
                monthNow = 11;
                yearNow --;
            }

            nLowDayLimit = yearNow * 32 * 12 + monthNow * 32 + daySalary;
        } else {
            nLowDayLimit = yearNow * 32 * 12 + monthNow * 32 + daySalary;

            monthNow ++;
            if(monthNow == 12) {
                monthNow = 0;
                yearNow ++;
            }

            nHighDayLimit = yearNow * 32 * 12 + monthNow * 32 + daySalary;
        }


        for(int i = 0; i < Common.getInstance().listAllPayments.size(); i ++) {
            Payment paymentTmp = Common.getInstance().listAllPayments.get(i);
            int nPaymentMode = paymentTmp.getPaymentMode();

            if(nPaymentMode == 0 || nPaymentMode == 2) {
                Common.getInstance().listCurrentPayments.add(paymentTmp);
                continue;
            }

            cal.setTimeInMillis(paymentTmp.getTimestampCreated());
            int monthTmp = cal.get(Calendar.MONTH);
            int yearTmp = cal.get(Calendar.YEAR);
            int dayTmp = cal.get(Calendar.DAY_OF_MONTH);

            if(dayTmp > paymentTmp.getDateOfMonth()) {
                monthTmp ++;
                if(monthTmp == 12) {
                    monthTmp = 0;
                    yearTmp ++;
                }
            }

            int nDay = yearTmp * 32 * 12 + monthTmp * 32 + paymentTmp.getDateOfMonth();

            if(nDay <= nLowDayLimit) break;
            if(nDay > nHighDayLimit) continue;

            Common.getInstance().listCurrentPayments.add(paymentTmp);
        }
    }

    private boolean weHavePermissionToReadSMS() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestReadSMSPermissionFirst() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)) {
            Toast.makeText(this, "We need permission so you can text your friends.", Toast.LENGTH_LONG).show();
            requestForResultSMSPermission();
        } else {
            requestForResultSMSPermission();
        }
    }

    private void requestForResultSMSPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 123);
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

        long currentTimeStamp = UserPreference.getInstance().getSharedPreference(Constant.PREF_KEY_SMS_TIMESTAMP, (long)0);
        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(message, null, null, null, null);

        int totalSMS = c.getCount();
        long maxTimestamp = currentTimeStamp;

        if(c.moveToFirst()) {
            for(int i = 0; i < totalSMS; i ++) {
                long lTimeStamp = c.getLong(c.getColumnIndexOrThrow("date"));

                if(lTimeStamp > currentTimeStamp) {
                    Sms objSms = new Sms();

                    objSms.setAddress(c.getString(c.getColumnIndexOrThrow("address")));
                    objSms.setText(c.getString(c.getColumnIndexOrThrow("body")));
                    objSms.setTimestamp(lTimeStamp);

                    int sms_id = Common.getInstance().dbHelper.createSMS(objSms);
                    objSms.setId(sms_id);

                    Common.getInstance().listSms.add(objSms);

                    if(lTimeStamp > maxTimestamp) maxTimestamp = lTimeStamp;
                } else {
                    break;
                }

                c.moveToNext();
            }

            UserPreference.getInstance().putSharedPreference(Constant.PREF_KEY_SMS_TIMESTAMP, maxTimestamp);
        }

        Collections.sort(Common.getInstance().listSms, new SmsComparator());

        if(Common.getInstance().listSms.size() == 0) {
//            addSampleSmsForTransaction();
            produceVirtualSms();
        }
    }

//    void addSampleSmsForTransaction() {
//        String[] arrTransacitons = getResources().getStringArray(R.array.sample_transaction_data);
//
//        for(int i = 0; i < arrTransacitons.length; i ++) {
//            Sms objSms = new Sms();
//
//            objSms.setAddress("10001");
//            objSms.setText(arrTransacitons[i]);
//            objSms.setTimestamp(new Date().getTime());
//
//            int sms_id = Common.getInstance().dbHelper.createSMS(objSms);
//            objSms.setId(sms_id);
//
//            Common.getInstance().listSms.add(objSms);
//        }
//    }

    class SmsComparator implements Comparator<Sms> {
        public int compare(Sms smsA, Sms smsB) {
            int nResult = 0;
            if(smsB.getTimestamp() > smsA.getTimestamp()) nResult = 1;
            if(smsB.getTimestamp() < smsA.getTimestamp()) nResult = -1;
            return nResult;
        }
    }

    class PaymentComparator implements Comparator<Payment> {
        public int compare(Payment paymentA, Payment paymentB) {
            int nResult = 0;
            int nTotlaPaymentMode = paymentA.getPaymentMode() * 4 + paymentB.getPaymentMode();

            Calendar cal = Calendar.getInstance();
            int yearNow = cal.get(Calendar.YEAR);
            int monthNow = cal.get(Calendar.MONTH);

            cal.setTimeInMillis(paymentA.getTimestampCreated());
            int monthA = cal.get(Calendar.MONTH);
            int yearA = cal.get(Calendar.YEAR);
            int dayA = cal.get(Calendar.DAY_OF_MONTH);

            if(dayA >= paymentA.getDateOfMonth()) {
                monthA ++;
                if(monthA == 12) {
                    monthA = 0;
                    yearA ++;
                }
            }

            dayA = paymentA.getDateOfMonth();

            cal.setTimeInMillis(paymentB.getTimestampCreated());
            int monthB = cal.get(Calendar.MONTH);
            int yearB = cal.get(Calendar.YEAR);
            int dayB = cal.get(Calendar.DAY_OF_MONTH);

            if(dayB >= paymentB.getDateOfMonth()) {
                monthB ++;
                if(monthB == 12) {
                    monthB = 0;
                    yearB ++;
                }
            }

            dayB = paymentB.getDateOfMonth();

            switch (nTotlaPaymentMode) {
                case 0:   // A-saving recurrent, B-saving recurrent
                case 2:   // A-saving recurrent, B-bill recurrent
                case 8:   // A-bill recurrent, B-saving recurrent
                case 10:  // A-bill recurrent, B-bill recurrent
                    nResult = paymentB.getDateOfMonth() - paymentA.getDateOfMonth();
                    break;

                case 1:   // A-saving recurrent, B-saving single
                case 3:   // A-saving recurret, B-bill single
                case 9:   // A-bill recurrent, B-saving single
                case 11:  // A-bill recurrent, B-bill single
                    if((yearB < yearNow) || (yearB == yearNow && monthB < monthNow)) {
                        nResult = -1;
                    } else {
                        nResult = paymentB.getDateOfMonth() - paymentA.getDateOfMonth();
                    }
                    break;

                case 4:   // A-saving single, B-saving recurrent
                case 6:   // A-saving single, B-bill recurrent
                case 12:  // A-bill single, B-saving recurrent
                case 14:  // A-bill single, B-bill recurrent
                    if((yearA < yearNow) || (yearA == yearNow && monthA < monthNow)) {
                        nResult = 1;
                    } else {
                        nResult = paymentB.getDateOfMonth() - paymentA.getDateOfMonth();
                    }
                    break;

                case 5:   // A-saving single, B-saving single
                case 7:   // A-saving single, B-bill single
                case 13:  // A-bill single, B-saving single
                case 15:  // A-bill single, B-bill single
                    nResult = (yearB * 32 * 12 + monthB * 32 + dayB) - (yearA * 32 * 12 + monthA * 32 + dayA);
                    break;
            }
            return nResult;
        }
    }

//    Map<Integer, List<Sms>> getAllSms() {
//        Map<Integer, List<Sms>> smsMap = new TreeMap<Integer, List<Sms>>();
//        Sms objSms = null;
//        Uri message = Uri.parse("content://sms/");
//        ContentResolver cr = getContentResolver();
//        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
//
//        Cursor c = cr.query(message, null, null, null, null);
//        startManagingCursor(c);
//        int totalSMS = c.getCount();
//
//        if (c.moveToFirst()) {
//            for (int i = 0; i < totalSMS; i++) {
//
//                objSms = new Sms();
////                objSms.setId(c.getString(c.getColumnIndexOrThrow("_id")));
//                objSms.setAddress(c.getString(c.getColumnIndexOrThrow("address")));
//                objSms.setMsg(c.getString(c.getColumnIndexOrThrow("body")));
//                objSms.setReadState(c.getString(c.getColumnIndex("read")));
//                objSms.setTime(c.getLong(c.getColumnIndexOrThrow("date")));
//
//                if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
//                    objSms.setFolderName("inbox");
//                } else {
//                    objSms.setFolderName("sent");
//                }
//
//                cal.setTimeInMillis(objSms.getTime());
//                int month = cal.get(Calendar.MONTH);
//
//                if (!smsMap.containsKey(month))
//                    smsMap.put(month, new ArrayList<Sms>());
//
//                smsMap.get(month).add(objSms);
//
//                c.moveToNext();
//            }
//        }
//        // else {
//        // throw new RuntimeException("You have no SMS");
//        // }
//        c.close();
//
//        return smsMap;
//    }

    void setupContainer()
    {
        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {

                    @Override
                    public void onBackStackChanged() {

                        FragmentManager manager = getSupportFragmentManager();
                        Fragment currFrag = manager.findFragmentById(R.id.content_frame);
                        if(currFrag == null) return;
                        currFrag.onResume();
                    }
                });

        boolean isSetInitConfig = UserPreference.getInstance().getSharedPreference(Constant.PREF_KEY_IS_INIT_CONFIG, false);

        if(isSetInitConfig) {
            launchFragment(0);
        } else {
            Toast.makeText(getBaseContext(), "You should set initial configuration", Toast.LENGTH_SHORT).show();
            launchFragment(5);
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment currFrag = manager.findFragmentById(R.id.content_frame);
        int nStackCount = manager.getBackStackEntryCount();

        Log.e("number", nStackCount + "");
        if(currFrag instanceof SettingFragment) {
            boolean isSetInitConfig = UserPreference.getInstance().getSharedPreference(Constant.PREF_KEY_IS_INIT_CONFIG, false);

            if(!isSetInitConfig) {
                Toast.makeText(getBaseContext(), "You should set initial configuration", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if(currFrag instanceof AddBankFragment) {
            if(!Common.getInstance().isActiveBankExist()) {
                Toast.makeText(getBaseContext(), "You should set active bank info", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if(currFrag instanceof HomeFragment) {
            return;
        }

        if(nStackCount == 1) {
            launchFragment(0);
            return;
        }
        manager.popBackStackImmediate();
    }

    void setupDrawer()
    {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
//                GravityCompat.START);

        drawerLayout.closeDrawers();
        isMenuOpened = false;

        setupLeftNavDrawer();

        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                isMenuOpened = true;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                isMenuOpened = false;
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    public void toggleMenu() {
        isMenuOpened = !isMenuOpened;

        if(isMenuOpened)
            drawerLayout.openDrawer(drawerLeft);
        else
            drawerLayout.closeDrawers();
    }

    void setupLeftNavDrawer()
    {
        drawerLeft = (ListView) findViewById(R.id.left_drawer);

        View header = getLayoutInflater().inflate(R.layout.left_nav_header, null);

        ImageView imgvBG = (ImageView)header.findViewById(R.id.imgvBG);
        imgvBG.setImageResource(R.drawable.bg_left_nav_header);

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        drawerLeft.addHeaderView(header);

        adapterLeft = new LeftNavAdapter(this); // home, my chat, location, setting, invite friend
        drawerLeft.setAdapter(adapterLeft);
        drawerLeft.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {
                drawerLayout.closeDrawers();

                launchFragment(pos - 1);
                adapterLeft.setSelection(pos - 1);
            }
        });
    }

    public void launchFragment(int pos){
        Fragment f = null;
        String title = null;

        switch (pos){
            case 0:
                f = new HomeFragment();
                title = Constant.FRAGMENT_HOME;
                break;

            case 1:
                f = new BankFragment();
                title = Constant.FRAGMENT_BANK;
                break;

            case 2:
                f = new WishFragment();
                title = Constant.FRAGMENT_WISH;
                break;

            case 3:
                f = new PaymentFragment();
                title = Constant.FRAGMENT_PAYMENT;
                break;

            case 4:
                f = new TransactionFragment();
                ((TransactionFragment)f).nMode = 0;
                title = Constant.FRAGMENT_TRANSACTION;
                break;

            case 5:
                f = new SettingFragment();
                title = Constant.FRAGMENT_SETTING;
                break;
        }

        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, f, title).addToBackStack(title).commit();
    }


}
