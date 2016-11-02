package com.ninja.nanny.Utils;

import com.ninja.nanny.Helper.DatabaseHelper;
import com.ninja.nanny.Model.Bank;
import com.ninja.nanny.Model.Wish;

import java.util.List;

/**
 * Created by Administrator on 10/28/2016.
 */

public class Common {

    static Common instance = null;

    public static Common getInstance() {
        if(instance == null){
            instance = new Common();
        }

        return instance;
    }

    public DatabaseHelper dbHelper;
    public List<Bank> listBanks;
    public List<Wish> listAllWishes;
    public List<Wish> listActiveWishes;
    public List<Wish> listFinishedWishes;
}
