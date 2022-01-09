package com.example.vendorapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vendorapp.R;
import com.example.vendorapp.helper.viewModel.OrderViewModel;
import com.example.vendorapp.helper.adapter.OrderRecyclerViewAdapter;
import com.example.vendorapp.model.Order;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


public class OrderListFragment extends Fragment {


    private int vendorID;


    public OrderListFragment(int vendorId) {
        // Required empty public constructor
        this.vendorID = vendorId;
    }


    private static final String TAG = OrderListFragment.class.getSimpleName();
    private RecyclerView recyclerView;
    private OrderRecyclerViewAdapter mAdapter;
    private OrderViewModel orderViewModel;

    public void initService(View view){
        // init fireStore db
        Log.d(TAG, "initService: vendorId: " + vendorID);

        // order view
        orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        orderViewModel.getSelectedListOrder().observe(getViewLifecycleOwner(), orders -> {
            recyclerView = view.findViewById(R.id.recycler_view);

            // check not null
            if (isAdded()){
                mAdapter = new OrderRecyclerViewAdapter(requireActivity(), orders);

                // linear styles
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setNestedScrollingEnabled(true);
                recyclerView.setAdapter(mAdapter);
            }

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initService(view);
    }
}