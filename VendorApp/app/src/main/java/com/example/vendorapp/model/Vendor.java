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
    private String storeName;
    private String fullName;
    private String userName;
    private String email;
    private String phone;
    private String address;
    private double rating;
    private int totalSale;
    private String image;
    private String status = "offline";


    public static final String VENDOR_ID = "id";
    public static final String VENDOR_STORENAME = "storeName";
    public static final String VENDOR_FULLNAME = "fullName";
    public static final String VENDOR_EMAIL = "email";
    public static final String VENDOR_USERNAME = "userName";
    public static final String VENDOR_PHONE = "phone";
    public static final String VENDOR_ADDRESS = "address";
    public static final String VENDOR_RATING = "rating";
    public static final String VENDOR_TOTALSALE = "totalSale";
    public static final String VENDOR_IMAGE = "image";


    public Vendor() {

    }

    public Vendor(int id, String storeName, String fullName, String userName, String email, String phone, String address, double rating, int totalSale, String image, String status) {
        this.id = id;
        this.storeName = storeName;
        this.fullName = fullName;
        this.userName = userName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.rating = rating;
        this.totalSale = totalSale;
        this.image = image;
        this.status = status;
    }

    public Vendor(String storeName, String fullName, String userName, String email, String phone, String address, int rating, int totalSale, String image) {
        this.storeName = storeName;
        this.fullName = fullName;
        this.userName = userName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.rating = rating;
        this.totalSale = totalSale;

        this.image =  "/clients/default_profile.png";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(storeName);
        dest.writeString(fullName);
        dest.writeString(userName);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(address);
        dest.writeDouble(rating);
        dest.writeInt(totalSale);
        dest.writeString(image);
        dest.writeString(status);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected Vendor(Parcel in) {
        id = in.readInt();
        storeName = in.readString();
        fullName = in.readString();
        userName = in.readString();
        email = in.readString();
        phone = in.readString();
        address = in.readString();
        rating = in.readDouble();
        totalSale = in.readInt();
        image = in.readString();
        status = in.readString();
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
                ", storeName='" + storeName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", rating=" + rating +
                ", totalSale=" + totalSale +
                ", image='" + image + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(VENDOR_ID, id);
        result.put(VENDOR_STORENAME, storeName);
        result.put(VENDOR_USERNAME, userName);
        result.put(VENDOR_FULLNAME, fullName);
        result.put(VENDOR_EMAIL, email);
        result.put(VENDOR_PHONE, phone);
        result.put(VENDOR_ADDRESS, address);
        result.put(VENDOR_RATING, rating);
        result.put(VENDOR_TOTALSALE, totalSale);
        result.put(VENDOR_IMAGE, image);
        result.put("status", status);
        return result;
    }
}