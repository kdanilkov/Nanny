package com.moneynanny.nanny.Comparator;

import com.moneynanny.nanny.Model.Sms;

import java.util.Comparator;

/**
 * Created by Administrator on 11/18/2016.
 */

public class SmsComparator implements Comparator<Sms> {
    public int compare(Sms smsA, Sms smsB) {
        int nResult = 0;
        if(smsB.getTimestamp() > smsA.getTimestamp()) nResult = 1;
        if(smsB.getTimestamp() < smsA.getTimestamp()) nResult = -1;
        return nResult;
    }
}
