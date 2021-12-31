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

import com.example.clientapp.R;
import com.example.clientapp.helper.viewModel.ItemViewModel;
import com.example.clientapp.helper.adapter.CategoryHomeAdapter;
import com.example.clientapp.helper.adapter.ItemRecyclerViewAdapter;
import com.example.clientapp.helper.adapter.NewStoreRecyclerViewAdapter;
import com.example.clientapp.model.Item;
import com.example.clientapp.model.Vendor;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;

public class HomeFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private static final String TAG = HomeFragment.class.getSimpleName();

    // Views & Adapters
    private ItemViewModel viewModel;
    private ItemRecyclerViewAdapter mAdapter;
    private RecyclerView categoryRecyclerView;
    private RecyclerView newStoreRecyclerView;
    private RecyclerView newItemRecyclerView;
    private CategoryHomeAdapter categoryHomeAdapter;
    private NewStoreRecyclerViewAdapter newStoresAdapter;
    private ItemRecyclerViewAdapter newItemsAdapter;

    // List
    private String selectedCategory = "";
    private Vendor selectedStore;
    
    // Firestore
    private static final String ITEM_COLLECTION = "items";
    private static final String VENDOR_COLLECTION = "vendors";
    private FirebaseFirestore fireStore;
    private CollectionReference storeCollection;
    private CollectionReference itemCollection;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        getViews(view);
        initService(view);
        initCategoryListAdapter(view);
    }

    private void initCategoryListAdapter(View view) {
        //This set list adapter for category
        ArrayList<String> listCategoryValue = new ArrayList<>();
        listCategoryValue.add("Rice");
        listCategoryValue.add("Noodles");
        listCategoryValue.add("Banh mi/Sticky rice");
        listCategoryValue.add("Salad");
        listCategoryValue.add("Snacks");
        listCategoryValue.add("Drinks");

        // set linear layout
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        categoryRecyclerView.setLayoutManager(horizontalLayoutManager);
        categoryHomeAdapter = new CategoryHomeAdapter(view.getContext(), listCategoryValue);
        categoryRecyclerView.setAdapter(categoryHomeAdapter);
    }

    private void initNewStoreListAdapter(View view, ArrayList<Vendor> newStoreList) {
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        newStoreRecyclerView.setLayoutManager(horizontalLayoutManager);
        newStoresAdapter = new NewStoreRecyclerViewAdapter(view.getContext(), newStoreList);
        newStoreRecyclerView.setAdapter(newStoresAdapter);
    }

    private void initNewItemListAdapter(View view, ArrayList<Item> newItemList) {
        // sort again
        newItemList.sort((o1, o2) -> {
            // reverse sort
            if (o1.getId() < o2.getId()){
                return 1; // normal will return -1
            } else if (o1.getId() > o2.getId()){
                return -1; // reverse
            }
            return 0;
        });

        // Get recycler view
        newItemRecyclerView = view.findViewById(R.id.recyclerNewItems);

        // Initialize list adapter
        mAdapter = new ItemRecyclerViewAdapter(getActivity(), newItemList, viewModel);

        // linear styles
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        newItemRecyclerView.setLayoutManager(linearLayoutManager);
        newItemRecyclerView.setItemAnimator(new DefaultItemAnimator());
        newItemRecyclerView.setHasFixedSize(true);
        newItemRecyclerView.setAdapter(mAdapter);
    }

    private void loadNewStoreList(View view) {
        try {
            ArrayList<Vendor> storeList = new ArrayList<>();

            storeCollection.addSnapshotListener((value, error) -> {
                if (value == null || value.isEmpty())
                    return;

                int size = value.size();
                int maxListSize = Math.min(size, 8);

                for (int i = size - 1, j = 0; j < maxListSize; i--, j++)
                    storeList.add(value.getDocuments().get(i).toObject(Vendor.class));

                // Load
                initNewStoreListAdapter(view, storeList);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadNewItemList(View view) {
        try {
            ArrayList<Item> itemList = new ArrayList<>();

            itemCollection.addSnapshotListener((value, error) -> {
                if (value == null || value.isEmpty())
                    return;

                int size = value.size();
                int maxListSize = Math.min(size, 10);

                for (int i = size - 1, j = 0; j < maxListSize; i--, j++) {
                    itemList.add(value.getDocuments().get(i).toObject(Item.class));
                    Log.d("HomeFragment", "item=" + itemList.get(j).toString());
                }

                // Load
                initNewItemListAdapter(view, itemList);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void initService(View view) {
        // init fireStore db
        fireStore = FirebaseFirestore.getInstance();
        storeCollection = fireStore.collection(VENDOR_COLLECTION);
        itemCollection = fireStore.collection(ITEM_COLLECTION);

        // Load data from Firestore
        loadNewStoreList(view);
        loadNewItemList(view);
    }

    private void getViews(View view) {
        categoryRecyclerView = view.findViewById(R.id.recyclerCategoryHome);
        newStoreRecyclerView = view.findViewById(R.id.recyclerNewStores);
    }
}