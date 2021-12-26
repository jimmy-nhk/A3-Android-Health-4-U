package com.example.clientapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Item implements Parcelable, Comparable<Item> {
    private int id = 0;
    private String name = "";
    private int quantity = 0;
    private String image = "";
    private String description = "";
    private int vendorID = 0;
    private String category = "";
    private String expireDate = "";
    private double price = 0;
    private double calories=0;
    //TODO: need discount ?


    protected Item(Parcel in) {
        id = in.readInt();
        name = in.readString();
        quantity = in.readInt();
        image = in.readString();
        description = in.readString();
        vendorID = in.readInt();
        category = in.readString();
        expireDate = in.readString();
        price = in.readDouble();
        calories = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(quantity);
        dest.writeString(image);
        dest.writeString(description);
        dest.writeInt(vendorID);
        dest.writeString(category);
        dest.writeString(expireDate);
        dest.writeDouble(price);
        dest.writeDouble(calories);
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("quantity", quantity);
        result.put("image", image);
        result.put("description", description);
        result.put("vendorID", vendorID);
        result.put("category", category);
        result.put("expireDate", expireDate);
        result.put("price", price);
        result.put("calories", calories);
        return result;
    }

    public Item() {
    }

    public Item(int id, String name, int quantity, String image, String description, int vendorID, String category) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.image = image;
        this.description = description;
        this.vendorID = vendorID;
        this.category = category;
    }

    public Item(String name, int vendorID, String category, double price) {
        this.name = name;
        this.price = price;
        this.vendorID = vendorID;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getVendorID() {
        return vendorID;
    }

    public void setVendorID(int vendorID) {
        this.vendorID = vendorID;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", image='" + image + '\'' +
                ", description='" + description + '\'' +
                ", vendorID=" + vendorID +
                ", category='" + category + '\'' +
                ", expireDate='" + expireDate + '\'' +
                ", price=" + price +
                ", calories=" + calories +
                '}';
    }

    @Override
    public int compareTo(Item o) {
        if (this.price - o.price == 0) {
            return 0;
        }
        if (this.price - o.price > 0) {
            return 1;
        }
        return -1;
    }

//    @Override
//    public int compare(Item o1, Item o2) {
//        if (o1.price - o2.price == 0) {
//            return 0;
//        }
//        if (o1.price - o2.price > 0) {
//            return 1;
//        }
//        return -1;
//    }
}
