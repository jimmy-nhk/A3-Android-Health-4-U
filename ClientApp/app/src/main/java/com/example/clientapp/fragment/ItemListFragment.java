package com.example.clientapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clientapp.R;
import com.example.clientapp.helper.ItemRecyclerViewAdapter;
import com.example.clientapp.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemListFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<Item> itemList;
    private ItemRecyclerViewAdapter mAdapter;
    private static final String TAG = ItemListFragment.class.getSimpleName();


    public ItemListFragment(){

        Log.d(TAG, "FoodListFragment: onCreate");
    }

    public static ItemListFragment newInstance(String param1, String param2) {
        ItemListFragment fragment = new ItemListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.fragment_food_list, container, false);

        itemList = new ArrayList<>();
//            public Item(String name, String vendorName, String category, double price) {
        itemList.add(new Item("Test" , "Phuc" , "Food", 10));
        itemList.add(new Item("Test1" , "Phuc1" , "Food", 101));
        itemList.add(new Item("Test2" , "Phuc1" , "Food", 102));
        itemList.add(new Item("Test" , "Phuc" , "Food", 10));
        itemList.add(new Item("Test1" , "Phuc1" , "Food", 101));
        itemList.add(new Item("Test2" , "Phuc1" , "Food", 102));
        itemList.add(new Item("Test" , "Phuc" , "Food", 10));
        itemList.add(new Item("Test1" , "Phuc1" , "Food", 101));
        itemList.add(new Item("Test2" , "Phuc1" , "Food", 102));

        recyclerView = view.findViewById(R.id.recycler_view);

        mAdapter = new ItemRecyclerViewAdapter(getActivity(), itemList);


        // linear styles
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setAdapter(mAdapter);

        // grid styles
//        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
//        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setAdapter(mAdapter);
//        recyclerView.setNestedScrollingEnabled(false);
        Log.d(TAG, "FoodListFragment: onCreateView");

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}