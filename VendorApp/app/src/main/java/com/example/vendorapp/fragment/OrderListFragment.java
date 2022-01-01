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
import com.example.vendorapp.helper.OrderViewModel;
import com.example.vendorapp.helper.adapter.OrderRecyclerViewAdapter;
import com.example.vendorapp.model.Order;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private int vendorID;
    private List<Order> orderList;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrderListFragment(int vendorId) {
        // Required empty public constructor
        this.vendorID = vendorId;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderListFragment newInstance(int vendorId, String param1, String param2) {
        OrderListFragment fragment = new OrderListFragment(vendorId);
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

    private static final String TAG = OrderListFragment.class.getSimpleName();
    private static final String ORDER_COLLECTION = "orders";
    private FirebaseFirestore fireStore;
    private CollectionReference orderCollection;
    private RecyclerView recyclerView;
    private OrderRecyclerViewAdapter mAdapter;
    private OrderViewModel orderViewModel;

    public void initService(View view){
        // init fireStore db
        fireStore = FirebaseFirestore.getInstance();
        orderCollection = fireStore.collection(ORDER_COLLECTION);

        Log.d(TAG, "initService: vendorId: " + vendorID);

        orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        orderViewModel.getSelectedListOrder().observe(getViewLifecycleOwner(), orders -> {
            recyclerView = view.findViewById(R.id.recycler_view);

            mAdapter = new OrderRecyclerViewAdapter(requireActivity(), orders);


            // linear styles
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setNestedScrollingEnabled(true);
            recyclerView.setAdapter(mAdapter);

        });

//        orderCollection.whereEqualTo("vendorID", vendorID)
//                .addSnapshotListener((value, error) -> {
//
//                    orderList = new ArrayList<>();
//
////                    Log.d(TAG, "orderCollectionLoadDb: listSize: " + value.size());
//
//                    // validate 0 case
//                    if (value.size() == 0){
//                        return;
//                    }
//
//                    //reverse way (newest show first)
//                    for (int i = value.size() - 1 ; i >= 0; i--){
//
//                        Order order = value.getDocuments().get(i).toObject(Order.class);
////                        Log.d(TAG, "orderCollectionLoadDb: order from db: " + order.toString());
//                        orderList.add(order);
//                    }
//
//                    orderList.sort((o1, o2) -> {
//                        // reverse sort
//                        if (o1.getId() < o2.getId()){
//                            return 1; // normal will return -1
//                        } else if (o1.getId() > o2.getId()){
//                            return -1; // reverse
//                        }
//                        return 0;
//                    });
//
//                    recyclerView = view.findViewById(R.id.recycler_view);
//
//                    mAdapter = new OrderRecyclerViewAdapter(requireActivity(), orderList);
//
//
//                    // linear styles
//                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
//                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//                    recyclerView.setLayoutManager(linearLayoutManager);
//                    recyclerView.setItemAnimator(new DefaultItemAnimator());
//                    recyclerView.setNestedScrollingEnabled(true);
//                    recyclerView.setAdapter(mAdapter);
//
//                });
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