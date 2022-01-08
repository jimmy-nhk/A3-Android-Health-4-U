package com.example.clientapp.chat.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.clientapp.R;
import com.example.clientapp.chat.ClientViewModel;
import com.example.clientapp.chat.adapter.VendorAdapter;
import com.example.clientapp.chat.model.MessageObject;
import com.example.clientapp.model.Client;
import com.example.clientapp.model.Vendor;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class ChatsFragment extends Fragment {

    // attributes
    private FirebaseFirestore fireStore;
    private CollectionReference vendorCollection;
    private final String VENDOR_COLLECTION = "vendors";
    private CollectionReference messageCollection;
    private final String MESSAGE_COLLECTION = "messages";
    private VendorAdapter vendorAdapter;

    private List<String> vendorList;
    private List<Vendor> mVendors;
    private static final String TAG = "ChatsFragment";
    private RecyclerView recyclerView;
    private Client currentClient;

    private ClientViewModel clientViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_chats, container, false);


        // set the current value
        clientViewModel = new ViewModelProvider(requireActivity()).get(ClientViewModel.class);
        currentClient = clientViewModel.getValue();

        recyclerView = view.findViewById(R.id.recycler_view_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // init firestore
        fireStore = FirebaseFirestore.getInstance();
        vendorCollection = fireStore.collection(VENDOR_COLLECTION);
        messageCollection = fireStore.collection(MESSAGE_COLLECTION);

        loadMessage();

        return view;
    }


// load messages
    private void loadMessage() {

        // message collection
        messageCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                // clear vendor
                vendorList = new ArrayList<>();

                MessageObject messageObject;
                // iterate through value in db
                for (DocumentSnapshot ds: value
                     ) {
                    messageObject = ds.toObject(MessageObject.class);

                    // get the one who receives message from the current user
                    if (messageObject.getSender().equals(currentClient.getUserName())){
                        vendorList.add(messageObject.getReceiver());
                    }

                    // get the one who sends message to the current user
                    if (messageObject.getReceiver().equals(currentClient.getUserName())){
                        vendorList.add(messageObject.getSender());
                    }
                }

                loadVendors();

            }

        });
    }

    // load vendors
    private void loadVendors() {


        // load the vendor
        vendorCollection
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                mVendors = new ArrayList<>();

                // this function below is used for getting unique value from the list
                vendorList = vendorList.stream().distinct().collect(Collectors.toList());
                Log.d(TAG, "vendorListString: " + vendorList);

                Vendor vendor;

                Log.d(TAG, "vendor size in db: " + value.size());

                for (DocumentSnapshot ds: value
                ){
                    vendor = ds.toObject(Vendor.class);

                    Log.d(TAG, "vendor: " + vendor.toString());
                    for (String userName: vendorList){
                        if (userName.equals(vendor.getUserName())){
                            mVendors.add(vendor);
                        }
                    }
                }

                Log.d(TAG, "mVendors: size " + mVendors.size());

                if (isAdded()){
                    vendorAdapter = new VendorAdapter(getContext(), mVendors, currentClient, true);
                    recyclerView.setAdapter(vendorAdapter);
                }


            }
        });
    }
}