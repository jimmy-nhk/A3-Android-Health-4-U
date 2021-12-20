package com.example.vendorapp.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.HashMap;
import java.util.Map;

public class Vendor implements Parcelable {

    private int id;
    private String fullName;
    private String username;
    private String email;
    private String phone;
    private String address;
    private int rating;
    private int totalSale;
    private String image;


    public static final String VENDOR_ID = "id";
    public static final String VENDOR_FULLNAME = "fullName";
    public static final String VENDOR_EMAIL = "email";
    public static final String VENDOR_USERNAME = "username";
    public static final String VENDOR_PHONE = "phone";
    public static final String VENDOR_ADDRESS = "address";
    public static final String VENDOR_RATING = "rating";
    public static final String VENDOR_TOTALSALE = "totalSale";
    public static final String VENDOR_IMAGE = "image";


    public Vendor() {

    }

    public Vendor(String fullName, String username, String email, String phone, String address, int rating, int totalSale, String image) {
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.rating = rating;
        this.totalSale = totalSale;

        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getTotalSale() {
        return totalSale;
    }

    public void setTotalSale(int totalSale) {
        this.totalSale = totalSale;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id);
        dest.writeString(fullName);
        dest.writeString(email);
        dest.writeString(username);
        dest.writeString(phone);
        dest.writeString(address);
        dest.writeInt(rating);
        dest.writeInt(totalSale);
        dest.writeString(image );

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected Vendor(Parcel in) {
        id = in.readInt();
        fullName = in.readString();
        email = in.readString();
        username = in.readString();
        phone = in.readString();
        address = in.readString();
        rating=in.readInt();
        totalSale=in.readInt();
        image = in.readString();
    }


    public static final Creator<Vendor> CREATOR = new Creator<Vendor>() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public Vendor createFromParcel(Parcel in) {
            return new Vendor(in);
        }

        @Override
        public Vendor[] newArray(int size) {
            return new Vendor[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public String toString() {
        return "Vendor{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", rating=" + rating +
                ", totalSale=" + totalSale +
                ", image='" + image + '\'' +
                '}';
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(VENDOR_ID, id);
        result.put(VENDOR_USERNAME, username);
        result.put(VENDOR_FULLNAME, fullName);
        result.put(VENDOR_EMAIL, email);
        result.put(VENDOR_PHONE, phone);
        result.put(VENDOR_ADDRESS, address);
        result.put(VENDOR_RATING, rating);
        result.put(VENDOR_TOTALSALE, totalSale);
        result.put(VENDOR_IMAGE, image);
        return result;
    }
}




