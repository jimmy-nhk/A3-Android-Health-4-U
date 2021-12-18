package com.example.clientapp.model;

import java.util.List;

public class Order {
    private int id;
    private String date;
    private boolean isProcessed;
    private List<Item> itemList;
    private List<Integer> quantity;

    public Order() {
    }

    public Order(int id, String date, boolean isProcessed, List<Item> itemList, List<Integer> quantity) {
        this.id = id;
        this.date = date;
        this.isProcessed = isProcessed;
        this.itemList = itemList;
        this.quantity = quantity;
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

    public boolean isProcessed() {
        return isProcessed;
    }

    public void setProcessed(boolean processed) {
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
}
