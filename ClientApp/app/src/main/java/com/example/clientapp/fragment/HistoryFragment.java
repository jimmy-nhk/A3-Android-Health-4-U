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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HistoryFragment.
     */
    public static HistoryFragment newInstance(int currentClientId) {
        HistoryFragment fragment = new HistoryFragment(currentClientId);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
//        List<Order> orderList = new ArrayList<>();
//
//
//        // load orders
//        orderCollection.whereEqualTo("clientID" , currentClientId)
//                .addSnapshotListener((value, error) -> {
//
//            // clear to list
//            cartList = new ArrayList<>();
//
//            // init necessary variable for doing logic
//            int countCart = 0;
//            Cart currentCart = new Cart();
//            int countProcessed;
//            double originalPrice = 0;
//            int countCancel;
//
//
//            //scan the value from db
//            for (int i = value.size() - 1 ; i >= 0; i--){
//                orderList.add(value.getDocuments().get(i).toObject(Order.class));
//            }
//
//            // sort reverse way
//            orderList.sort((o1, o2) -> {
//                // reverse sort
//                if (o1.getId() < o2.getId()){
//                    return 1; // normal will return -1
//                } else if (o1.getId() > o2.getId()){
//                    return -1; // reverse
//                }
//                return 0;
//            });
//
//
//            for (int i = 0 ; i < orderList.size(); i++){
//
//                Log.d(TAG, "loadCart: order: " + orderList.get(i).toString());
//
//
//                String time = filterDate(orderList.get(i).getDate());
//                Log.d(TAG, "loadCart: time: " + time);
//
//                // take the list of order in the same time
//                List<Order> orderByDate = orderList.stream().filter(order -> {
//                    Log.d(TAG, "filter: " + order.getDate().trim().equals(time));
//                    Log.d(TAG, "filter: isProcessed: " + order.getIsProcessed());
//                    Log.d(TAG, "filter: object " + order.toString() + " orderList size: " + orderList.size());
//                    return order.getDate().trim().equals(time) ;
//                }).collect(Collectors.toList());
//
//
//                // create cart object
//                currentCart = new Cart(countCart, time ,orderByDate );
//
//                originalPrice = currentCart.getPrice();
//
//                // init count processed
//                countProcessed = 0;
//                countCancel = 0;
//                for (Order order: orderByDate){
//
//                    // validate if the order is cancel
//                    if (order.getIsCancelled()){
//                        countCancel++;
//                        continue;
//                    }
//
//                    Log.d(TAG, "filter-condition: isProcess: " + order.getIsProcessed());
//                    // check if processed yet ?
//                    if (order.getIsProcessed()){
//                        countProcessed ++;
//                    }
//                }
//                Log.d(TAG, "loadCart: orderByDate size: " +orderByDate.size());
//                Log.d(TAG, "loadCart: countProcessed : " +countProcessed);
//
//
//                // validate if the order is already processed.
//                if ((countProcessed + countCancel) == orderByDate.size()){
//                    currentCart.setIsFinished(true);
//                }
//
//                countCart++;
//
//                // add cart to cartList
//                cartList.add(currentCart);
//
//
//                i += orderByDate.size() - 1;
//
//
//            }
//
//            //TODO: send notification when the order is cancelled
//
////            if (originalPrice > currentCart.getPrice()){
////
////                //TODO: send noti here
////                Intent intent = new Intent(MainActivity.CANCEL_NOTIFICATION);
////                intent.putExtra("cart",  currentCart);
////                getContext().sendBroadcast(intent);
////                Log.d(TAG,"your cart has some orders cancelled: original: "+ originalPrice + " with current: " + currentCart.getPrice());
////            }
//            // reset the list
//            orderList.clear();
//            currentCart = new Cart();
//
//            Log.d(TAG, "loadCart: cardList size: " +cartList.size());
//
//            // set layout
//            setLayout(view);
//
//            // reset
//
//        });
    }


    // filter the string date
    public String filterDate (String rawString){

        // initialize the new string
        char [] filterString = new char[rawString.length()];

        int countColon = 0;

        // iterate through each character in the string
        for (int i = 0 ; i < rawString.length(); i++){


            // check if the character is :
            if(rawString.charAt(i) == ':'){
                countColon++;
                if (countColon == 2){
                    filterString[i] = rawString.charAt(i);
                    filterString[i+1] = rawString.charAt(i+1);
                    filterString[i+2] = rawString.charAt(i+2);
                    return String.valueOf(filterString).trim();
                }

            }

            filterString[i] = rawString.charAt(i);
        }

        return null;
    }

    // set layout
    public void setLayout(View view){
        recyclerView = view.findViewById(R.id.history_recycler_view);

        mAdapter = new HistoryRecyclerViewAdapter(cartList, getActivity());

        // linear styles
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setAdapter(mAdapter);
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


