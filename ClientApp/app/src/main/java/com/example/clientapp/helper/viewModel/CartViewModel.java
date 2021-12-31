package com.example.clientapp.helper.viewModel;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.clientapp.model.Cart;

import java.util.ArrayList;
import java.util.List;

public class CartViewModel extends ViewModel {
    private final MutableLiveData<List<Cart>> mutableLiveData = new MutableLiveData<>();


    public boolean addListCarts(List<Cart> cartList){

        try {
            mutableLiveData.setValue(cartList);
            return true;
        } catch (Exception e){
            return false;
        }

    }
    public void addCart(Cart cart) {
        List<Cart> cartList;

        // validate the result;
        try {
            Log.d(CartViewModel.class.getSimpleName(), "addCart 1: " + cart.getId());
            cartList = mutableLiveData.getValue();

            // add new item
            cartList.add(cart);
        } catch (Exception e){
            cartList = new ArrayList<>();
            // add new item
            cartList.add(cart);
        }



        mutableLiveData.setValue(cartList);
        Log.d(CartViewModel.class.getSimpleName(), "addItem: " + cart.getId());
        Log.d(CartViewModel.class.getSimpleName(), "addItem: listSize: " + cart.getOrderList());
    }

    public void resetMutableCartList(){
        mutableLiveData.setValue(new ArrayList<>());
    }

    public LiveData<List<Cart>> getSelectedListCart() {
        return mutableLiveData;
    }

    public List<Cart> getListCart(){
        return mutableLiveData.getValue();
    }
}

