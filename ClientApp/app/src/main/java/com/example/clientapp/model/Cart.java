package com.example.clientapp.model;

import java.util.List;

public class Cart {

    private int id;
    private String date;
    private List<Order> orderList;
    private double price;

    public Cart(int id, String date, List<Order> orderList , double price) {
        this.id = id;
        this.date = date;
        this.orderList = orderList;
        this.price = price;
    }

    public Cart(int id, String date, List<Order> orderList ) {
        this.id = id;
        this.date = date;
        this.orderList = orderList;

        price = 0;

        // calculate the price
        for (int i = 0 ; i < orderList.size() ; i++){
            price += orderList.get(i).getPrice();
        }
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
