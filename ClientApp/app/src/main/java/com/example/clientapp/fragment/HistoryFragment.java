package com.example.clientapp.fragment;

import android.content.IntentFilter;
import android.os.Bundle;

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
import com.example.clientapp.helper.viewModel.CartViewModel;
import com.example.clientapp.helper.adapter.HistoryRecyclerViewAdapter;
import com.example.clientapp.helper.broadcast.NotificationReceiver;
import com.example.clientapp.model.Cart;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {


    // attributes:
    private RecyclerView recyclerView;
    private List<Cart> cartList;
    private HistoryRecyclerViewAdapter mAdapter;
    private static final String TAG = HistoryFragment.class.getSimpleName();
    private static final String ORDER_COLLECTION = "orders";
    private FirebaseFirestore fireStore;
    private CollectionReference orderCollection;
    private int currentClientId;

    private NotificationReceiver notificationReceiver;
    private IntentFilter intentFilter;

    public HistoryFragment(int currentClientId) {
        this.currentClientId = currentClientId;
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        initService(view);

        return view;
    }

    private CartViewModel cartViewModel;

    public void initService(View view){
        // init fireStore db
        fireStore = FirebaseFirestore.getInstance();
        orderCollection = fireStore.collection(ORDER_COLLECTION);

        cartList = new ArrayList<>(); //Reset value of cart List
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

        cartViewModel.getSelectedListCart().observe(getViewLifecycleOwner(), cartList1 -> {
            cartList = cartList1;
            setLayout(view);
        });
    }

    // set layout
    public void setLayout(View view){
        recyclerView = view.findViewById(R.id.history_recycler_view);

        // validate the activity is not null
        if (isAdded()){
            mAdapter = new HistoryRecyclerViewAdapter(cartList, getActivity());

            // linear styles
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setNestedScrollingEnabled(true);
            recyclerView.setAdapter(mAdapter);
        }

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


