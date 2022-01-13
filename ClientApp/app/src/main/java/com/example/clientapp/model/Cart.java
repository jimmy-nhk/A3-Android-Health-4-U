package com.example.clientapp.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Cart implements Parcelable {

    private int id;
    private String date;
    private List<Order> orderList;
    private double price;
    private boolean isFinished;

    public Cart(){}

    public Cart(int id, String date, List<Order> orderList , double price) {
        this.id = id;
        this.date = date;
        this.orderList = orderList;
        this.price = round(price);
        this.isFinished = false;
    }

    public Cart(int id, String date, List<Order> orderList ) {
        this.id = id;
        this.date = date;
        this.orderList = orderList;
        this.isFinished = false;
        price = 0;

        // calculate the price
        for (int i = 0 ; i < orderList.size() ; i++){
            Log.d("CartConstructor", "constructor: " + orderList.get(i).toString());
            price += orderList.get(i).getPrice();
        }

        price = round(price);
    }

    // round up price
    public static double round(double d) {
        BigDecimal bd = new BigDecimal(d);
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);return bd.doubleValue();
    }

    public Cart(int id, String date, List<Order> orderList, double price, boolean isFinished) {
        this.id = id;
        this.date = date;
        this.orderList = orderList;
        this.price = price;
        this.isFinished = isFinished;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(date);
        dest.writeTypedList(orderList);
        dest.writeDouble(price);
        dest.writeByte((byte) (isFinished ? 1 : 0));
    }

    protected Cart(Parcel in) {
        id = in.readInt();
        date = in.readString();
//        orderList = new ArrayList<Order>();
//        in.readList(orderList,null);
        orderList = in.createTypedArrayList(Order.CREATOR);

        price = in.readDouble();
        isFinished = in.readByte() != 0;
    }

    public static final Creator<Cart> CREATOR = new Creator<Cart>() {
        @Override
        public Cart createFromParcel(Parcel in) {
            return new Cart(in);
        }

        @Override
        public Cart[] newArray(int size) {
            return new Cart[size];
        }
    };



    public boolean getIsFinished() {
        return isFinished;
    }

    public void setIsFinished(boolean finished) {
        isFinished = finished;
    }


    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    @Override
    public int describeContents() {
        return 0;
    }



    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", orderList=" + orderList +
                ", price=" + price +
                ", isFinished=" + isFinished +
                '}';
    }
}
