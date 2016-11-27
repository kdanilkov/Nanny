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
import com.ninja.nanny.Comparator.SmsComparator;
import com.ninja.nanny.Custom.CustomActivity;
import com.ninja.nanny.Fragment.AddBankFragment;
import com.ninja.nanny.Fragment.BankFragment;
import com.ninja.nanny.Fragment.HomeFragment;
import com.ninja.nanny.Fragment.PaymentFragment;
import com.ninja.nanny.Fragment.SettingFragment;
import com.ninja.nanny.Fragment.SmsFragment;
import com.ninja.nanny.Fragment.TransactionFragment;
import com.ninja.nanny.Fragment.WishFragment;
import com.ninja.nanny.Helper.DatabaseHelper;
import com.ninja.nanny.Model.Sms;
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

    void initSetting() {
        try {
            Common.getInstance().jsonArrayBankInfo = new JSONArray(Constant.strBankJsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        UserPreference.getInstance().pref = getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
        Common.getInstance().syncSettingInfo();

        Common.getInstance().dbHelper = new DatabaseHelper(getApplicationContext());
        Common.getInstance().listBanks = Common.getInstance().dbHelper.getAllBanks();
        Common.getInstance().listAllWishes = Common.getInstance().dbHelper.getAllWishes();
        Common.getInstance().listActiveWishes = Common.getInstance().dbHelper.getActiveWishes();
        Common.getInstance().listFinishedWishes = Common.getInstance().dbHelper.getFinishedWishes();
        Common.getInstance().listAllPayments = Common.getInstance().dbHelper.getAllPayments();
        Common.getInstance().listSms = Common.getInstance().dbHelper.getAllSms();
        Common.getInstance().listAllTransactions = Common.getInstance().dbHelper.getAllTransactions();

        if(Common.getInstance().timestampInitConfig > 0) {

            if(Common.getInstance().isActiveBankExist()) {
                //        if (weHavePermissionToReadSMS()) {
                //            syncSms();
                //        } else {
                //            requestReadSMSPermissionFirst();
                //        }

                Common.getInstance().syncBetweenTransactionAndSms();
                Common.getInstance().checkWishSavingPast();
            }
        }

//        sampleWishData();
    }

    void sampleWishData() {
        Calendar c = Calendar.getInstance();
        Wish wish = new Wish("wish1", 5000, 500, 1200, c.getTimeInMillis(), -1, 1);

        int nWishId = Common.getInstance().dbHelper.createWish(wish);
        wish.setId(nWishId);

        WishSaving wishSaving = new WishSaving(nWishId, 100, 201605);
        int nWishSavingId = Common.getInstance().dbHelper.createWishSaving(wishSaving);

        wish.setLastSavingId(nWishSavingId);
        Common.getInstance().dbHelper.updateWish(wish);

        wishSaving = new WishSaving(nWishId, 200, 201606);
        nWishSavingId = Common.getInstance().dbHelper.createWishSaving(wishSaving);

        wish.setLastSavingId(nWishSavingId);
        Common.getInstance().dbHelper.updateWish(wish);

        wishSaving = new WishSaving(nWishId, 300, 201607);
        nWishSavingId = Common.getInstance().dbHelper.createWishSaving(wishSaving);

        wish.setLastSavingId(nWishSavingId);
        Common.getInstance().dbHelper.updateWish(wish);

        wishSaving = new WishSaving(nWishId, 400, 201608);
        nWishSavingId = Common.getInstance().dbHelper.createWishSaving(wishSaving);

        wish.setLastSavingId(nWishSavingId);
        Common.getInstance().dbHelper.updateWish(wish);

        wishSaving = new WishSaving(nWishId, 200, 201609);
        nWishSavingId = Common.getInstance().dbHelper.createWishSaving(wishSaving);

        wish.setLastSavingId(nWishSavingId);
        Common.getInstance().dbHelper.updateWish(wish);

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

        Collections.sort(Common.getInstance().listSms, new SmsComparator());
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

        if(Common.getInstance().timestampInitConfig > 0) {
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
            if(Common.getInstance().timestampInitConfig == 0) {
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

            case 6:
                f = new SmsFragment();
                title = Constant.FRAGMENT_SMS;
        }

        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, f, title).addToBackStack(title).commit();
    }


}
