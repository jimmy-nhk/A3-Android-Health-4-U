package com.example.clientapp.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.HashMap;
import java.util.Map;

public class Client implements Parcelable {

    private String fullName;
    private String username;
    private String email;
    private String phone;
    private String address;
    private String dob;
    private double weight;
    private double height;
    private double bmi;
    private String image;

    public static final String CLIENT_FULLNAME ="fullName";
    public static final String CLIENT_USERNAME ="username";
    public static final String CLIENT_EMAIL ="email";
    public static final String CLIENT_PHONE ="phone";
    public static final String CLIENT_ADDRESS ="address";
    public static final String CLIENT_DOB ="dob";
    public static final String CLIENT_WEIGHT ="weight";
    public static final String CLIENT_HEIGHT ="height";
    public static final String CLIENT_BMI ="bmi";
    public static final String CLIENT_IMAGE ="image";


    public Client() {

    }

    public Client(String fullName, String username, String email, String phone, String address, String dob, double weight, double height, double bmi, String image) {
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.dob = dob;
        this.weight = weight;
        this.height = height;
        this.bmi = bmi;
        this.image = image;
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

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected Client(Parcel in) {
        fullName = in.readString();
        email = in.readString();
        phone = in.readString();
        dob = in.readString();
        username = in.readString();
        address = in.readString();
        weight = in.readDouble();
        height = in.readDouble();
        bmi = in.readDouble();
        image = in.readString();
    }


    public static final Creator<Client> CREATOR = new Creator<Client>() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public Client createFromParcel(Parcel in) {
            return new Client(in);
        }

        @Override
        public Client[] newArray(int size) {
            return new Client[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fullName);
        dest.writeString(email);
        dest.writeString(username);
    }

    @NonNull
    @Override
    public String toString() {
        return "Client{" +
                "fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(CLIENT_USERNAME, username);
        result.put(CLIENT_FULLNAME, fullName);
        result.put(CLIENT_EMAIL, email);
        result.put(CLIENT_PHONE, phone);
        result.put(CLIENT_DOB, dob);

        result.put(CLIENT_ADDRESS, phone);

        result.put(CLIENT_WEIGHT, weight);
        result.put(CLIENT_HEIGHT, height);
        result.put(CLIENT_BMI, bmi);
        result.put(CLIENT_IMAGE, image);
        return result;
    }
}




