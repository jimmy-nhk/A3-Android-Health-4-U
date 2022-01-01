package com.example.vendorapp.helper;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vendorapp.model.Order;

import java.util.ArrayList;
import java.util.List;


public class OrderViewModel extends ViewModel {
    private final MutableLiveData<List<Order>> mutableLiveData = new MutableLiveData<>();


    public boolean addListOrders(List<Order> orderList){

        try {
            mutableLiveData.setValue(orderList);
            return true;
        } catch (Exception e){
            return false;
        }

    }
    public void addOrder(Order order) {
        List<Order> orderList;

        // validate the result;
        try {
            Log.d(OrderViewModel.class.getSimpleName(), "addOrder 1: " + order.getId());
            orderList = mutableLiveData.getValue();

            // add new item
            orderList.add(order);
        } catch (Exception e){
            orderList = new ArrayList<>();
            // add new item
            orderList.add(order);
        }



        mutableLiveData.setValue(orderList);
        Log.d(OrderViewModel.class.getSimpleName(), "addOrder: " + order.getId());
        Log.d(OrderViewModel.class.getSimpleName(), "addOrder: listSize: " + order.getItemList());
    }

    public void resetMutableOrderList(){
        mutableLiveData.setValue(new ArrayList<>());
    }

    public LiveData<List<Order>> getSelectedListOrder() {
        return mutableLiveData;
    }

    public List<Order> getListOrder(){
        return mutableLiveData.getValue();
    }
}
