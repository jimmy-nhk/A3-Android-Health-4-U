package com.example.clientapp.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clientapp.R;
import com.example.clientapp.helper.ItemRecyclerViewAdapter;
import com.example.clientapp.helper.ItemViewModel;
import com.example.clientapp.model.Item;
import com.example.clientapp.model.Vendor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StoreDetailsFragment extends Fragment {
    //Init parameters
    TextView storeNameTxt, sellerNameTxt,phoneTxt, emailTxt, addressTxt, ratingTxt, soldQuantityTxt;
    ImageView coverImg;
    //
    private final String TAG = StoreDetailsFragment.class.getSimpleName();
    private static final String ITEM_COLLECTION = "items";
    private static final String VENDOR_COLLECTION = "vendors";
    private int vendorID;
    private Vendor vendor;
    private RecyclerView recycler_view_store;
    private List<Item> itemList;
    private ItemRecyclerViewAdapter mAdapter;
    // Item list
    private FirebaseFirestore fireStore;
    private CollectionReference itemCollection;
    URL imageURL = null;

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
        getViews(view);
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

        fireStore.collection(VENDOR_COLLECTION)
                .whereEqualTo("id", vendorID)
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
                                vendor= document.toObject(Vendor.class);
                            }
                            storeNameTxt.setText(vendor.getStoreName());
                            sellerNameTxt.setText(vendor.getFullName());
                            phoneTxt.setText(vendor.getPhone());
                            emailTxt.setText(vendor.getEmail());
                            addressTxt.setText(vendor.getAddress());
                            ratingTxt.setText(vendor.getRating()+"");
                            soldQuantityTxt.setText(vendor.getTotalSale()+"");
                            //set image by URL
                            try {
                                Thread thread = new Thread(new Runnable(){
                                    @Override
                                    public void run() {
                                        try {
                                            imageURL = new URL(vendor.getImage());
                                            Bitmap mIcon_val = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                                            coverImg.setImageBitmap(mIcon_val);
                                        } catch (Exception e) {
                                            Log.e(TAG, e.getMessage());
                                        }
                                    }
                                });
                                thread.start();
                            } catch (Exception e) {
                                coverImg.setImageResource(R.drawable.bun); //Set something else
                                e.printStackTrace();
                            }                            Log.d(TAG,"vendor: "+vendor.toString());
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    // attach components
    public void getViews(View view) {
        storeNameTxt = view.findViewById(R.id.storeName);
        sellerNameTxt = view.findViewById(R.id.storeFullname);
        phoneTxt = view.findViewById(R.id.storePhone);
        emailTxt = view.findViewById(R.id.storeMail);
        addressTxt = view.findViewById(R.id.storeAddress);
        ratingTxt = view.findViewById(R.id.storeRating);
        soldQuantityTxt = view.findViewById(R.id.storeSoldQuantity);
        coverImg = view.findViewById(R.id.storeCoverImg);

    }
}