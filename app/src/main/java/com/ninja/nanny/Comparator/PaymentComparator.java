package com.ninja.nanny.Comparator;

import com.ninja.nanny.Model.Payment;

import java.util.Comparator;

/**
 * Created by Administrator on 11/18/2016.
 *
 * recurrent payment would be priority
 * for single payment, real time stamp would be priority
 *
 */

public class PaymentComparator implements Comparator<Payment> {
    public int compare(Payment paymentA, Payment paymentB) {
        int nResult = 0;

        boolean isSingPaymentA = (paymentA.getPaymentMode() == 1 || paymentA.getPaymentMode() == 3);
        boolean isSingPaymentB = (paymentB.getPaymentMode() == 1 || paymentB.getPaymentMode() == 3);

        if((isSingPaymentA && isSingPaymentB) || (!isSingPaymentA && !isSingPaymentB)) { // Both are single, or recurrent
            long realTimestampA = paymentA.getRealTimeStamp();
            long realTimestampB = paymentB.getRealTimeStamp();

            if(realTimestampB > realTimestampA) {
                nResult = 1;
            } else if (realTimestampB < realTimestampA) {
                    nResult = -1;
            }
        } else if(isSingPaymentA && !isSingPaymentB) { // A-single, B-recurrent
            nResult = 1;
        } else if(!isSingPaymentA && isSingPaymentB) { // A-recurrent, B-single
            nResult = -1;
        }

        return nResult;
    }
}
