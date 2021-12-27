package com.example.clientapp.model;

import android.util.Log;

import java.util.List;

public class Cart {

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
        this.price = price;
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
    }

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
}
