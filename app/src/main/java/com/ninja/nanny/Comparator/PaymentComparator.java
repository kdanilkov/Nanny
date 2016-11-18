package com.ninja.nanny.Comparator;

import com.ninja.nanny.Model.Payment;

import java.util.Calendar;
import java.util.Comparator;

/**
 * Created by Administrator on 11/18/2016.
 */

public class PaymentComparator implements Comparator<Payment> {
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
