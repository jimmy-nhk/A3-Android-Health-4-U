package com.example.clientapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.clientapp.R;
import com.example.clientapp.helper.ItemRecyclerViewAdapter;
import com.example.clientapp.model.Item;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

public class StoreDetailsFragment extends Fragment {
    private final String TAG = StoreDetailsFragment.class.getSimpleName();
    private int vendorID;

    // Item list
    private FirebaseFirestore fireStore;
    private CollectionReference itemCollection;

    public StoreDetailsFragment() {
        // Required empty public constructor
    }

    public static StoreDetailsFragment newInstance(int vendorID) {
        StoreDetailsFragment fragment = new StoreDetailsFragment();
        Bundle args = new Bundle();
        args.putInt("vendorID", vendorID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            vendorID = getArguments().getInt("vendorID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_store_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initService(view);
    }

    private void initService(View view) {
        // init fireStore db
        fireStore = FirebaseFirestore.getInstance();
        fireStore.collection("vendors")
                .document(String.valueOf(vendorID))
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.d(TAG, error.getMessage());
                    } else {
                        //TODO: add Vendor to model and cast to Vendor here
//                        Toast.makeText(getContext(), value.toString(), Toast.LENGTH_SHORT).show();
                    }
        });
    }
}