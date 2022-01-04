package com.example.vendorapp.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vendorapp.model.Vendor;


public class VendorViewModel extends ViewModel {

    private final MutableLiveData<Vendor> vendorMutableLiveData = new MutableLiveData<>();



    public void setValue(Vendor vendor){
        vendorMutableLiveData.setValue(vendor);
    }

    public LiveData<Vendor> getSelectedVendor() {
        return vendorMutableLiveData;
    }

    public Vendor getValue(){
        return vendorMutableLiveData.getValue();
    }
}
