package com.ninja.nanny.Model;

/**
 * Created by Administrator on 10/28/2016.
 */

public class WishSaving {
    private int _id;
    private int _wishId;
    private int _savedAmount;
    private int _dateCreated; // 201501, 201502, ..., 201512. Year + Month

    public WishSaving() {

    }

    public WishSaving(int wishId, int savedAmount, int dateCreated) {
        _wishId = wishId;
        _savedAmount = savedAmount;
        _dateCreated = dateCreated;
    }

    public void setId(int id) {
        _id = id;
    }

    public void setWishId(int wishId) { _wishId = wishId; }

    public void setSavedAmount(int savedAmount) {
        _savedAmount = savedAmount;
    }

    public void setDateCreated(int dateCreated) {
        _dateCreated = dateCreated;
    }

    public int getId() {
        return _id;
    }

    public int getWishId() {
        return _wishId;
    }

    public int getSavedAmount() {
        return _savedAmount;
    }

    public int getDateCreated() {
        return _dateCreated;
    }

}
