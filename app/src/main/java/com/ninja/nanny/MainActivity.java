package com.ninja.nanny;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.ninja.nanny.Adapter.LeftNavAdapter;
import com.ninja.nanny.Custom.CustomActivity;
import com.ninja.nanny.Fragment.AdviceFragment;
import com.ninja.nanny.Fragment.BankFragment;
import com.ninja.nanny.Fragment.HomeFragment;
import com.ninja.nanny.Fragment.PaymentFragment;
import com.ninja.nanny.Fragment.SettingFragment;
import com.ninja.nanny.Fragment.TransactionFragment;
import com.ninja.nanny.Fragment.WishFragment;
import com.ninja.nanny.Helper.DatabaseHelper;
import com.ninja.nanny.Preference.UserPreference;
import com.ninja.nanny.Utils.Common;
import com.ninja.nanny.Utils.Constant;

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

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initSetting();
        setupDrawer();
        setupContainer();

    }

    void initSetting() {
        UserPreference.getInstance().pref = getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
        Common.getInstance().dbHelper = new DatabaseHelper(getApplicationContext());
        Common.getInstance().listBanks = Common.getInstance().dbHelper.getAllBanks();
        Common.getInstance().listAllWishes = Common.getInstance().dbHelper.getAllWishes();
        Common.getInstance().listActiveWishes = Common.getInstance().dbHelper.getActiveWishes();
        Common.getInstance().listFinishedWishes = Common.getInstance().dbHelper.getFinishedWishes();
    }


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
        launchFragment(0);
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();

        int nStackCount = manager.getBackStackEntryCount();

        Log.e("number", nStackCount + "");

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
                title = Constant.FRAGMENT_TRANSACTION;
                break;

            case 5:
                f = new SettingFragment();
                title = Constant.FRAGMENT_SETTING;
                break;

            default:
                f = new AdviceFragment();
                title = Constant.FRAGMENT_ADVICE;
                break;
        }

        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, f, title).addToBackStack(title).commit();
    }


}
