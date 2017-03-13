package com.ninja.nanny.Model;

import java.util.Calendar;

/**
 * Created by Administrator on 10/28/2016.
 */

public class Sms {
    private int _id;
    private String _address;
    private String _text;
    private long _timestamp;
    private int day;

    public Sms() {
    }

    public Sms(String address, String text, long timestamp) {
        _address = address;
        _text = text;
        _timestamp = timestamp;
    }

    public Sms(int id, String address, String text, long timestamp) {
        _id = id;
        _address = address;
        _text = text;
        _timestamp = timestamp;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        day = cal.getTime().getDay();
    }

    public int getId() {
        return _id;
    }

    public String getAddress() {
        return _address;
    }

    public String getText() {
        return _text;
    }

    public long getTimestamp() {
        return _timestamp;
    }

    public void setId(int id) {
        _id = id;
    }

    public void setAddress(String address) {
        _address = address;
    }

    public void setText(String text) {
        _text = text;
    }

    public void setTimestamp(long timestamp) {
        _timestamp = timestamp;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
