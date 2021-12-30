package com.example.clientapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.clientapp.R;
import com.example.clientapp.helper.adapter.CategoryHomeAdapter;
import com.example.clientapp.helper.NewStoreRecyclerViewAdapter;
import com.example.clientapp.model.Item;
import com.example.clientapp.model.Vendor;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.clientapp.activity.MainActivity;
import com.example.clientapp.helper.adapter.CategoryHomeAdapter;


import java.util.ArrayList;

public class HomeFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private static final String TAG = HomeFragment.class.getSimpleName();

    // Views
    private RecyclerView categoryRecycleView;
    private RecyclerView recyclerView;
    private CategoryHomeAdapter categoryHomeAdapter;
    private NewStoreRecyclerViewAdapter newStoresAdapter;

    // List
    private String selectedCategory = "";
    private Vendor selectedStore;
    
    // Firestore
    private static final String ITEM_COLLECTION = "items";
    private static final String VENDOR_COLLECTION = "vendors";
    private FirebaseFirestore fireStore;
    private CollectionReference storeCollection;

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
        categoryRecycleView.setLayoutManager(horizontalLayoutManager);
        categoryHomeAdapter = new CategoryHomeAdapter(view.getContext(), listCategoryValue);
        categoryRecycleView.setAdapter(categoryHomeAdapter);
    }

    private void initNewStoreListAdapter(View view) {
        ArrayList<Vendor> newStoreList = new ArrayList<>();

        //This set list adapter for category


        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        categoryRecycleView.setLayoutManager(horizontalLayoutManager);
        newStoresAdapter = new NewStoreRecyclerViewAdapter(view.getContext(), newStoreList);
        categoryHomeAdapter.setClickListener((view1, position) -> {
            //Set category on Clicked category
//            selectedCategory = storeList.get(position);
//            loadStoreDetailFragment(selectedCategory);
        });
        categoryRecycleView.setAdapter(categoryHomeAdapter);
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
                initNewStoreListAdapter(view);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void initService(View view) {
        // init fireStore db
        fireStore = FirebaseFirestore.getInstance();
        storeCollection = fireStore.collection(VENDOR_COLLECTION);

        // Load data from Firestore
        loadNewStoreList(view);
    }

    private void loadItemListFragment(String category) {
        Fragment fragment = new ItemListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("category", category);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void loadStoreDetailFragment(Vendor vendor) {
        Fragment fragment = new StoreDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("vendor", vendor);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void getViews(View view) {
        categoryRecycleView = view.findViewById(R.id.recyclerCategoryHome);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");

        onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

    }
}