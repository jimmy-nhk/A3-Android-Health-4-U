package com.example.clientapp.helper.viewModel;

import android.util.Log;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.clientapp.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemViewModel extends ViewModel {
    private final MutableLiveData<List<Item>> mutableItemList = new MutableLiveData<>();
    private final MutableLiveData<Double> mutableLivePrice = new MutableLiveData<>();

    public void addItem(Item item) {
        List<Item> itemList;


        // validate the result;
        try {
            Log.d(ItemViewModel.class.getSimpleName(), "addItem 1: " + item.getName());
            itemList = mutableItemList.getValue();
            // add new item
            itemList.add(item);
        } catch (Exception e){
            itemList = new ArrayList<>();
            // add new item
            itemList.add(item);
        }

        // set mutable list
        setMutableItemList(itemList);

        Log.d(ItemViewModel.class.getSimpleName(), "addItem: " + item.getName());
        Log.d(ItemViewModel.class.getSimpleName(), "addItem: listSize: " + itemList.size());
    }

    public void setMutableItemList(List<Item> itemList){

        double price = 0;

        for (Item i: itemList
        ) {
            price += i.getPrice();
        }

        mutableItemList.setValue(itemList);
        mutableLivePrice.setValue(price);
    }

    public void resetMutableItemList(){
        mutableItemList.setValue(new ArrayList<>());
        mutableLivePrice.setValue(0.0);
    }

    public Double getTotalPrice(){
        return mutableLivePrice.getValue();
    }

    public LiveData<Double> getLiveTotalPrice(){
        return mutableLivePrice;
    }

    public LiveData<List<Item>> getSelectedItem() {
        return mutableItemList;
    }

    public List<Item> getListItem(){
        return mutableItemList.getValue();
    }
}
