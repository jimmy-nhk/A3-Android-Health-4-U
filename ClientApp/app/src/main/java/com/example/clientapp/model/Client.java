package com.example.clientapp.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import java.util.HashMap;
import java.util.Map;

public class Client implements Parcelable {

    private String name;
    private String email;
    private String phone;

    public static final String Client_NAME ="name";
    public static final String Client_EMAIL ="email";
    public static final String Client_PHONE ="phone";


    public Client(){};
    public Client(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected Client(Parcel in) {
        name = in.readString();
        email = in.readString();
        phone = in.readString();
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

        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(phone);
    }

    @Override
    public String toString() {
        return "Client{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(Client_NAME, name);
        result.put(Client_EMAIL, email);
        result.put(Client_PHONE, phone);
        return result;
    }
}




