package com.moneynanny.nanny.Model;

/**
 * Created by Administrator on 10/28/2016.
 */

public class UsedAmount {
    private int _id;
    private int _usedAmount;
    private long _timestampPeriod;
    private long _timestampUpdated;

    public UsedAmount() {

    }

    public UsedAmount(int usedAmount, long timestampPeriod, long timestampUpdated) {
        _usedAmount = usedAmount;
        _timestampPeriod = timestampPeriod;
        _timestampUpdated = timestampUpdated;
    }

    public int getId() {
        return _id;
    }

    public int getUsedAmount() {
        return _usedAmount;
    }

    public long getTimestampPeriod() {
        return _timestampPeriod;
    }

    public long getTimestampUpdated() {
        return _timestampUpdated;
    }

    public void setId(int id){
        _id = id;
    }

    public void setUsedAmount(int usedAmount) {
        _usedAmount = usedAmount;
    }

    public void setTimestampPeriod(long timestampPeriod) {
        _timestampPeriod = timestampPeriod;
    }

    public void setTimestampUpdated(long timestampUpdated) {
        _timestampUpdated = timestampUpdated;
    }

}
