package com.ninja.nanny.Comparator;

import com.ninja.nanny.Model.Wish;

import java.util.Comparator;

/**
 * Created by Administrator on 11/18/2016.
 */

public class WishComparator implements Comparator<Wish> {
    public int compare(Wish wishA, Wish wishB) {
        int nResult = 0;
        if(wishA.getTimestampCreated() > wishB.getTimestampCreated()) nResult = 1;
        if(wishA.getTimestampCreated() < wishB.getTimestampCreated()) nResult = -1;
        return nResult;
    }
}
