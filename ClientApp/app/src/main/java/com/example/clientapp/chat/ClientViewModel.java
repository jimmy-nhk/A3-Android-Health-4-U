package com.example.clientapp.chat;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.clientapp.helper.viewModel.CartViewModel;
import com.example.clientapp.model.Cart;
import com.example.clientapp.model.Client;

import java.util.ArrayList;
import java.util.List;

public class ClientViewModel extends ViewModel {

    private final MutableLiveData<Client> clientMutableLiveData = new MutableLiveData<>();



    public void setValue(Client client){
        clientMutableLiveData.setValue(client);
    }

    public LiveData<Client> getSelectedClient() {
        return clientMutableLiveData;
    }

    public Client getValue(){
        return clientMutableLiveData.getValue();
    }
}
