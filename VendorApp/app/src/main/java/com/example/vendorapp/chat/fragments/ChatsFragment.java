package com.example.vendorapp.chat.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vendorapp.R;
import com.example.vendorapp.chat.VendorViewModel;
import com.example.vendorapp.chat.adapter.ClientAdapter;
import com.example.vendorapp.chat.model.MessageObject;
import com.example.vendorapp.model.Client;
import com.example.vendorapp.model.Vendor;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class ChatsFragment extends Fragment {

    private FirebaseFirestore fireStore;
    private CollectionReference clientCollection;
    private final String CLIENT_COLLECTION = "clients";
    private CollectionReference messageCollection;
    private final String MESSAGE_COLLECTION = "messages";
    private ClientAdapter clientAdapter;

    private List<String> clientsList;
    private List<Client> mClients;

    private static final String TAG = "ChatsFragment";
    private RecyclerView recyclerView;
    private Vendor currentVendor;

    private VendorViewModel vendorViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_chats, container, false);


        // set the current value
        vendorViewModel = new ViewModelProvider(requireActivity()).get(VendorViewModel.class);
        currentVendor = vendorViewModel.getValue();

        recyclerView = view.findViewById(R.id.recycler_view_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // init firestore
        fireStore = FirebaseFirestore.getInstance();
        messageCollection = fireStore.collection(MESSAGE_COLLECTION);
        clientCollection = fireStore.collection(CLIENT_COLLECTION);
        Log.d(TAG, "ChatFragment inits everything successfully");
        loadMessage();

        return view;
    }

    private void loadMessage() {

        messageCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                clientsList = new ArrayList<>();

                MessageObject messageObject;
                // iterate through value in db
                for (DocumentSnapshot ds: value
                     ) {
                    messageObject = ds.toObject(MessageObject.class);

                    // get the one who receives message from the current user
                    if (messageObject.getSender().equals(currentVendor.getUserName())){
                        clientsList.add(messageObject.getReceiver());
                    }

                    // get the one who sends message to the current user
                    if (messageObject.getReceiver().equals(currentVendor.getUserName())){
                        clientsList.add(messageObject.getSender());
                    }
                }

                loadClients();

            }

        });
    }


    private void loadClients() {


        // load the vendor
        clientCollection
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                mClients = new ArrayList<>();

                // this function below is used for getting unique value from the list
                clientsList = clientsList.stream().distinct().collect(Collectors.toList());
                Log.d(TAG, "clientListString: " + clientsList);

                Client client;

                Log.d(TAG, "client size in db: " + value.size());

                for (DocumentSnapshot ds: value
                ){
                    client = ds.toObject(Client.class);

                    Log.d(TAG, "client: " + client.toString());
                    for (String userName: clientsList){
                        if (userName.equals(client.getUserName())){
                            mClients.add(client);
                        }
                    }
                }

                Log.d(TAG, "mClients: size " + mClients.size());
                clientAdapter = new ClientAdapter(getContext(), mClients, currentVendor, true);
                recyclerView.setAdapter(clientAdapter);

            }
        });
    }
}