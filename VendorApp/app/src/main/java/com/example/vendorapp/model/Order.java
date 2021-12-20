package com.example.vendorapp.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Order {
    private int id;
    private String date;
    private boolean isProcessed;
    private List<Item> itemList;
    private List<Integer> quantity;
    private int vendorID;
    private int clientID;

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("date", date);
        result.put("isProcessed", isProcessed);
        result.put("itemList", itemList);
        result.put("quantity", quantity);
        result.put("vendorID", vendorID);
        result.put("clientID", clientID);
        return result;
    }

    public Order() {
    }

    public Order(int id, String date, boolean isProcessed, List<Item> itemList, List<Integer> quantity, int vendorId) {
        this.id = id;
        this.date = date;
        this.isProcessed = isProcessed;
        this.itemList = itemList;
        this.quantity = quantity;
        this.vendorID = vendorId;
        System.out.println("InConstructor: " + itemList.size());
        System.out.println("InConstructor: " + quantity.size());

    }

    public Order(int id, String date, boolean isProcessed, List<Item> itemList, List<Integer> quantity, int vendorID, int clientID) {
        this.id = id;
        this.date = date;
        this.isProcessed = isProcessed;
        this.itemList = itemList;
        this.quantity = quantity;
        this.vendorID = vendorID;
        this.clientID = clientID;
        System.out.println("InConstructor: " + itemList.size());
        System.out.println("InConstructor: " + quantity.size());
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
                ", itemList=" + itemList +
                ", quantity=" + quantity +
                ", vendorID=" + vendorID +
                ", clientId=" + clientID +
                '}';
    }
}
