package com.example.clientapp.chat.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.clientapp.R;
import com.example.clientapp.chat.ClientViewModel;
import com.example.clientapp.chat.adapter.VendorAdapter;
import com.example.clientapp.model.Client;
import com.example.clientapp.model.Vendor;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class VendorsFragment extends Fragment {

    private static final String TAG = "UsersFragment";
    private RecyclerView recyclerView;

    private VendorAdapter vendorAdapter;
    private List<Vendor> mVendors;
    private List<Vendor> searchVendorList;


    private FirebaseFirestore fireStore;
    private CollectionReference vendorCollection;
    private final String VENDOR_COLLECTION = "vendors";
    private ClientViewModel clientViewModel;

    private Client currentClient;
    EditText searchVendors;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_users, container, false);

        searchVendors = view.findViewById(R.id.search_vendors);
        searchVendors.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        recyclerView = view.findViewById(R.id.recycler_view_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mVendors = new ArrayList<>();

        // init firestore
        fireStore = FirebaseFirestore.getInstance();
        vendorCollection = fireStore.collection(VENDOR_COLLECTION);


        // set the current value
        clientViewModel = new ViewModelProvider(requireActivity()).get(ClientViewModel.class);
        currentClient = clientViewModel.getValue();

        loadVendors();

        return view;
    }

    private void searchUsers(String s) {

        // clear list
        mVendors = new ArrayList<>();

        // if search is empty
        if (s.equals("")){
            // load all vendors again
            loadVendors();
            return;
        }

        // iterate through the search list
        for (Vendor vendor: searchVendorList
             ) {

            // check condition
            if (vendor.getUserName().toLowerCase().contains(s)){
                mVendors.add(vendor);
            }
        }

        // set layout
        vendorAdapter = new VendorAdapter(getContext(), mVendors, currentClient, false);
        recyclerView.setAdapter(vendorAdapter);

    }

    private void loadVendors() {


        // load the vendor
        vendorCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                // validate in the normal case without search
                if (searchVendors.getText().toString().equals("")){
                    mVendors = new ArrayList<>();
                    for (int i = value.size() - 1 ; i >= 0; i--){
                        mVendors.add(value.getDocuments().get(i).toObject(Vendor.class));
                    }

                    searchVendorList = mVendors;
                    Log.d(TAG, "mVendors: size" + mVendors.size());
                    if (isAdded()){
                        vendorAdapter = new VendorAdapter(getContext(), mVendors, currentClient, false);
                        recyclerView.setAdapter(vendorAdapter);
                    }

                }

            }
        });
    }
}