package com.example.clientapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.clientapp.R;
import com.example.clientapp.helper.ItemRecyclerViewAdapter;
import com.example.clientapp.helper.ItemViewModel;
import com.example.clientapp.model.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class StoreDetailsFragment extends Fragment {
    private final String TAG = StoreDetailsFragment.class.getSimpleName();
    private static final String ITEM_COLLECTION = "items";
    private int vendorID;
    private RecyclerView recycler_view_store;
    private List<Item> itemList;
    private ItemRecyclerViewAdapter mAdapter;
    // Item list
    private FirebaseFirestore fireStore;
    private CollectionReference itemCollection;

    private ItemViewModel viewModel;
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
        View view = inflater.inflate(R.layout.fragment_store_details, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
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
        itemCollection = fireStore.collection(ITEM_COLLECTION);

        //Fetch item from server
        itemList = new ArrayList<>(); //Reset value of item List

        fireStore.collection(ITEM_COLLECTION)
                .whereEqualTo("vendorID", vendorID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // validate no value in the list
                            if (task.getResult() == null || task.getResult().isEmpty()){
                                return;
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                itemList.add(document.toObject(Item.class));
                            }
                            // sort again
                            itemList.sort((o1, o2) -> {
                                // reverse sort
                                if (o1.getId() < o2.getId()){
                                    return 1; // normal will return -1
                                } else if (o1.getId() > o2.getId()){
                                    return -1; // reverse
                                }
                                return 0;
                            });

                            // Get recycler view
                            recycler_view_store = view.findViewById(R.id.recycler_view_store);

                            // Initialize list adapter
                            mAdapter = new ItemRecyclerViewAdapter(getActivity(), itemList, viewModel);

                            // linear styles
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            recycler_view_store.setLayoutManager(linearLayoutManager);
                            recycler_view_store.setItemAnimator(new DefaultItemAnimator());
                            recycler_view_store.setHasFixedSize(true);
                            recycler_view_store.setAdapter(mAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


}