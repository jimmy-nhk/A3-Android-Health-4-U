package com.example.clientapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class Item implements Parcelable {

    private String name = "";
    //FIXME: @ALL need quantity ?
    private int quantity = 0;
    private String image ="";
    private String vendorName="";
    private String category="";
    private String date="";
    private double price=0;
    //TODO: need discount ?


    protected Item(Parcel in) {
        name = in.readString();
        quantity = in.readInt();
        image = in.readString();
        vendorName = in.readString();
        category = in.readString();
        date = in.readString();
        price = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(quantity);
        dest.writeString(image);
        dest.writeString(vendorName);
        dest.writeString(category);
        dest.writeString(date);
        dest.writeDouble(price);
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("quantity", quantity);
        result.put("image", image);
        result.put("vendorName", vendorName);
        result.put("category", category);
        result.put("date", date);
        result.put("price", price);
        return result;
    }

    public Item() {
    }

    public Item(String name, int quantity, String image, String vendorName, String category) {
        this.name = name;
        this.quantity = quantity;
        this.image = image;
        this.vendorName = vendorName;
        this.category = category;
    }

    public Item(String name, String vendorName, String category, double price) {
        this.name = name;
        this.price = price;
        this.vendorName = vendorName;
        this.category = category;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


}
