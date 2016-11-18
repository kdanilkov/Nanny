package com.ninja.nanny.Comparator;

import com.ninja.nanny.Model.Transaction;

import java.util.Comparator;

/**
 * Created by Administrator on 11/18/2016.
 */

public class TransactionComparator implements Comparator<Transaction> {
    public int compare(Transaction transA, Transaction transB) {
        int nResult = 0;
        if(transB.getTimestampCreated() > transA.getTimestampCreated()) nResult = 1;
        if(transB.getTimestampCreated() < transA.getTimestampCreated()) nResult = -1;
        return nResult;
    }
}
