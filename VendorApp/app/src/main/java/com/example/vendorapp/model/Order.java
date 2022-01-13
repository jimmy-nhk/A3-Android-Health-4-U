package com.example.vendorapp.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Order implements Parcelable {
    private int id;
    private String date;
    private boolean isProcessed;
    private boolean isCancelled;
    private List<Item> itemList;
    private List<Integer> quantity;
    private int vendorID;
    private int clientID;
    private double price;

    protected Order(Parcel in) {
        id = in.readInt();
        date = in.readString();
        isProcessed = in.readByte() != 0;
        isCancelled = in.readByte() != 0;
        itemList = in.createTypedArrayList(Item.CREATOR);
        quantity = new ArrayList<Integer>(); // or any other type of List
        in.readList(quantity, null);
        vendorID = in.readInt();
        clientID = in.readInt();
        price = in.readDouble();
        Log.e("OrderClass", "Constructor : " + this.toString());

    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id);
        dest.writeString(date);
        dest.writeByte((byte) (isProcessed ? 1 : 0));
        dest.writeByte((byte) (isCancelled ? 1 : 0));
        dest.writeTypedList(itemList);
        dest.writeList(quantity);
        dest.writeInt(vendorID);
        dest.writeInt(clientID);
        dest.writeDouble(price);

        Log.e("OrderClass", "write : " + this.toString());

    }

    // is new message
    public boolean isNewestOrder(){

        String currentTime = filterDate(LocalDateTime.now().toString());
        int currentTimeInt = convertInt(currentTime);

        Log.d("OrderClass", "currentTime: " + currentTime);
        Log.d("OrderClass", "OrderClass object currentTime: " + this.date);

        // check if the date is current
        if (!this.date.substring(0, 11).equals(currentTime.substring(0, 11))){
            return false;
        }

        // if yes, check the time is close to current
        if (convertInt(this.date) >= currentTimeInt - 5){
            return true;
        }
        return false;

    }

    // filter the string date
    public String filterDate (String rawString){

        Log.d("OrderClass" , "timestamp before changed: " + rawString);
        // initialize the new string
        char [] filterString = new char[rawString.length()];


        // iterate through each character in the string
        for (int i = 0 ; i < rawString.length(); i++){

            // check if the character is T then replace it with T
            if (rawString.charAt(i) == 'T'){
                filterString[i] = ' ';
                continue;
            }

            // check if the character is :
            if(rawString.charAt(i) == '.'){
                return String.valueOf(filterString).trim();
            }

            filterString[i] = rawString.charAt(i);
        }

        return null;
    }

    // convert time to integer
    private int convertInt(String currentTime) {
       try {
           String withoutDate = currentTime.substring(11, currentTime.length() );
           Log.d("OrderClass", "without date:" + withoutDate);
           int hour = Integer.parseInt(withoutDate.substring(0,2));
           int min = Integer.parseInt(withoutDate.substring(3,5));
           int second = Integer.parseInt(withoutDate.substring(6,8));

           Log.d("OrderClass", "time second: " + hour +" " + min + " " + second);

           return hour * 3600 + min * 60 + second;
       } catch (Exception e){
           return 0;
       }

    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("date", date);
        result.put("isProcessed", isProcessed);
        result.put("isCancelled", isCancelled);
        result.put("itemList", itemList);
        result.put("quantity", quantity);
        result.put("vendorID", vendorID);
        result.put("clientID", clientID);
        result.put("price", price);
        return result;
    }


    public Order() {
    }

    public Order(int id, String date, boolean isProcessed, List<Item> itemList, List<Integer> quantity, int vendorID) {
        this.id = id;
        this.date = date;
        this.isProcessed = isProcessed;
        this.itemList = itemList;
        this.quantity = quantity;
        this.vendorID = vendorID;
        System.out.println("InConstructor: " + itemList.size());
        System.out.println("InConstructor: " + quantity.size());

    }

    public Order(int id, String date, boolean isProcessed, List<Item> itemList, List<Integer> quantity, int vendorID, int clientID, double price) {
        this.id = id;
        this.date = date;
        this.isProcessed = isProcessed;
        isCancelled = false;
        this.itemList = itemList;
        this.quantity = quantity;
        this.vendorID = vendorID;
        this.clientID = clientID;
        this.price = price;
        System.out.println("InConstructor: " + itemList.size());
        System.out.println("InConstructor: " + quantity.size());
    }

    public boolean getIsCancelled() {
        return isCancelled;
    }

    public void setIsCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean getIsProcessed() {
        return isProcessed;
    }

    public void setIsProcessed(boolean processed) {
        isProcessed = processed;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    public List<Integer> getQuantity() {
        return quantity;
    }

    public void setQuantity(List<Integer> quantity) {
        this.quantity = quantity;
    }

    public int getVendorID() {
        return vendorID;
    }

    public void setVendorID(int vendorID) {
        this.vendorID = vendorID;
    }

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", isProcessed=" + isProcessed +
                ", isCancelled=" + isCancelled +
                ", itemList=" + itemList +
                ", quantity=" + quantity +
                ", vendorID=" + vendorID +
                ", clientID=" + clientID +
                ", price=" + price +
                '}';
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }


}
