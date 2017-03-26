package com.test.lifcare.model;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajatbeck on 3/25/2017.
 */

public class Data {

    private String phoneId;
    private String phoneNumber;
    private String name;
    private String imageUrl;
    private Bitmap mBitmap;
    private List<Phone> mPhone = new ArrayList<>();

    public void setmPhone(List<Phone> mPhone) {
        this.mPhone = mPhone;
    }

    public List<Phone> getmPhone() {
        return mPhone;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneId(String phoneId) {
        this.phoneId = phoneId;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getPhoneId() {
        return phoneId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
