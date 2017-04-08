package com.fastrrr.Type;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * Created by Allied on 05-Apr-17.
 */

public class UserType {
    String name;
    Drawable icon;
    String UserImage;

    public String getUserImage() {
        return UserImage;
    }

    public void setUserImage(String UserImage) {
        this.UserImage = UserImage;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    String phone;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
