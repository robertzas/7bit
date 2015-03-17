package com.intercom.video.twoway.Models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 * Created by charles on 3/9/15.
 */
public class ContactsEntity implements Serializable{

    private String deviceName;
    private Bitmap picture;

    public ContactsEntity(String deviceName, Bitmap picture)
    {
        this.deviceName = deviceName;
        this.picture = picture;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }


    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public String getDeviceName() {
        return deviceName;
    }


    public Bitmap getPicture() {
        return picture;
    }

    public static String encodePictureToBase64(Bitmap picture) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

    public static Bitmap decodePictureFromBase64(String picToDecode)
    {
        byte[] b = Base64.decode(picToDecode, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        return bitmap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactsEntity that = (ContactsEntity) o;

        if (deviceName != null ? !deviceName.equals(that.deviceName) : that.deviceName != null)
            return false;
        if (picture != null ? !picture.equals(that.picture) : that.picture != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = deviceName != null ? deviceName.hashCode() : 0;
        result = 31 * result + (picture != null ? picture.hashCode() : 0);
        return result;
    }
}
