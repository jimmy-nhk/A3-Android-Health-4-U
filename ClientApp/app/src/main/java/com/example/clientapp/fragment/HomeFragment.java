package com.example.clientapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.clientapp.R;
import com.example.clientapp.helper.CategoryAdapter;
import com.example.clientapp.helper.CategoryHomeAdapter;
import com.example.clientapp.helper.ItemRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Map;

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
    private CategoryHomeAdapter adapter;
    private String selectedCategory = "";

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
        initListAdapter(view);
    }

    private void initListAdapter(View view) {
        //This set list adapter for category
        ArrayList<String> listCategoryValue = new ArrayList<>();
        listCategoryValue.add("Rice");
        listCategoryValue.add("Noodles");
        listCategoryValue.add("Banh mi/Sticky rice");
        listCategoryValue.add("Salad");
        listCategoryValue.add("Snacks");
        listCategoryValue.add("Drinks");

        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        categoryRecycleView.setLayoutManager(horizontalLayoutManager);
        adapter = new CategoryHomeAdapter(view.getContext(), listCategoryValue);
        adapter.setClickListener((view1, position) -> {
            //Set category on Clicked category
            selectedCategory = listCategoryValue.get(position);
        });
        categoryRecycleView.setAdapter(adapter);
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