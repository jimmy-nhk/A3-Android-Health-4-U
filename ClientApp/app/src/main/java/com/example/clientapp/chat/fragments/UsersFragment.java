package com.example.clientapp.chat.fragments;

import android.content.Intent;
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
import com.example.clientapp.chat.adapter.UserAdapter;
import com.example.clientapp.helper.viewModel.CartViewModel;
import com.example.clientapp.model.Client;
import com.example.clientapp.model.Order;
import com.example.clientapp.model.Vendor;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {

    private static final String TAG = "UsersFragment";
    private RecyclerView recyclerView;

    private UserAdapter userAdapter;
    private List<Vendor> mVendors;

    private FirebaseFirestore fireStore;
    private CollectionReference vendorCollection;
    private final String VENDOR_COLLECTION = "vendors";
    private ClientViewModel clientViewModel;

    private Client currentClient;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_users, container, false);


        recyclerView = view.findViewById(R.id.recycler_view_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mVendors = new ArrayList<>();

        // set the current value
        clientViewModel = new ViewModelProvider(requireActivity()).get(ClientViewModel.class);
        currentClient = clientViewModel.getValue();

        loadVendors();

        return view;
    }

    private void loadVendors() {

        // init firestore
        fireStore = FirebaseFirestore.getInstance();
        vendorCollection = fireStore.collection(VENDOR_COLLECTION);

        // load the vendor
        vendorCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                mVendors = new ArrayList<>();
                for (int i = value.size() - 1 ; i >= 0; i--){
                    mVendors.add(value.getDocuments().get(i).toObject(Vendor.class));
                }

                Log.d(TAG, "mVendors: size" + mVendors.size());
                userAdapter = new UserAdapter(getContext(), mVendors, currentClient);
                recyclerView.setAdapter(userAdapter);

            }
        });
    }
}