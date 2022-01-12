package com.example.vendorapp.activity;

import com.example.vendorapp.R;
import com.example.vendorapp.helper.adapter.OrderItemListViewAdapter;
import com.example.vendorapp.model.Client;
import com.example.vendorapp.model.Item;
import com.example.vendorapp.model.Order;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {
    private static final String TAG = "OrderDetailActivity";

    private TextView nameTxt;
    private TextView phoneTxt;
    private TextView addressTxt;
    private TextView statusTxt;
    private TextView dateTxt;
    private ListView orderItemListView;
    private TextView moneyTxt;
    private Button cancelBtn;
    private Button processBtn;

    private static final String ORDER_COLLECTION = "orders";
    private FirebaseFirestore fireStore;
    private CollectionReference orderCollection;
    private DocumentReference orderRef;
    //Params
    Order order;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        order = getIntent().getParcelableExtra("order");

        initViews();
        setLayout();
    }

    // init views
    private void initViews() {
        // billings recycler
        nameTxt= findViewById(R.id.orderClientName);
        phoneTxt= findViewById(R.id.orderClientPhoneTxt);
        addressTxt= findViewById(R.id.orderClientAddressTxt);
        statusTxt= findViewById(R.id.orderStatus);
        dateTxt= findViewById(R.id.orderDate);
        orderItemListView= findViewById(R.id.orderItemListView);
        moneyTxt= findViewById(R.id.orderMoneyTotal);
        cancelBtn= findViewById(R.id.orderCancel);
        processBtn= findViewById(R.id.orderProcess);

    }
    // set layout
    public void setLayout() {

        final int GREEN_COLOR = ContextCompat.getColor(OrderDetailActivity.this, R.color.green);
        final int BLACK_COLOR = ContextCompat.getColor(OrderDetailActivity.this, R.color.black);
        final int RED_COLOR = ContextCompat.getColor(OrderDetailActivity.this, R.color.red);

        //Check to check Processing condition
        statusTxt.setText(order.getIsProcessed() ? "PROCESSED" : order.getIsCancelled() ? "CANCELLED" : "Not yet process");
        statusTxt.setTextColor(order.getIsProcessed() ? GREEN_COLOR : order.getIsCancelled() ? RED_COLOR : BLACK_COLOR);

        cancelBtn.setVisibility(order.getIsProcessed() ? View.GONE : order.getIsCancelled() ? View.GONE : View.VISIBLE);
        processBtn.setVisibility(order.getIsProcessed() ? View.GONE : order.getIsCancelled() ? View.GONE : View.VISIBLE);

        // price
        moneyTxt.setText(order.getPrice() + "$");
        //date
        dateTxt.setText( order.getDate() + "");


        // init fireStore db
        fireStore = FirebaseFirestore.getInstance();
        orderCollection = fireStore.collection(ORDER_COLLECTION);
        orderRef = orderCollection.document(String.valueOf(order.getId()));

        processBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                order.setIsProcessed(true);
                orderCollection.document(order.getId() + "").set(order).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Order successfully updated!");

                        Log.d(TAG, "processBtn: click: " + order.toString());

                        statusTxt.setText("Processed!");
                        statusTxt.setTextColor(GREEN_COLOR);
                        cancelBtn.setVisibility(View.GONE);
                        cancelBtn.setEnabled(false);
                        processBtn.setEnabled(false);
                        processBtn.setVisibility(View.GONE);

                    }
                });
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order.setIsCancelled(true);
                order.setPrice(0);
                orderCollection.document(order.getId() + "").set(order).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Order successfully updated!");

                        Log.d(TAG, "cancelBtn: click: " + order.toString());

                        statusTxt.setText("Cancel!");
                        statusTxt.setTextColor(RED_COLOR);

                        cancelBtn.setVisibility(View.GONE);
                        cancelBtn.setEnabled(false);
                        processBtn.setEnabled(false);
                        processBtn.setVisibility(View.GONE);
                    }
                });
            }
        });

        //Embed list view form list order of cart
        try {

            List<Item> itemList = order.getItemList();
            List<Integer> quantityList = order.getQuantity();
            // linear styles
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OrderDetailActivity.this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            orderItemListView.setNestedScrollingEnabled(false);
            orderItemListView.setAdapter(new OrderItemListViewAdapter(itemList, quantityList, OrderDetailActivity.this));
            orderItemListView.setDivider(null);
            setListViewHeightBasedOnChildren(orderItemListView);
        } catch (Exception ignored) {

        }
        getClientById(order.getClientID()+"",order.getItemList(),order.getQuantity());
    }
    private void getClientById( String s, List<Item> itemList, List<Integer> quantityList) {
        fireStore = FirebaseFirestore.getInstance();
        DocumentReference docRef = fireStore.collection("clients").document(s);

        try {
            // load items
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot != null) {
                        Log.d(TAG, "value " + s);
                        Log.d(TAG, "value != null");
                        Client c = documentSnapshot.toObject(Client.class);
                        Log.d(TAG, c.toString());
                        onBindViewHolder2( c, itemList, quantityList);

                    }
                }
            });
        } catch (Exception ignored) {

        }
    }
    private void onBindViewHolder2( Client c,
                                   List<Item> itemList,
                                   List<Integer> quantityList) {

        //
        nameTxt.setText(c.getFullName());
        phoneTxt.setText(c.getPhone());
        addressTxt.setText(c.getAddress());
    }

    public void finishIntentOnClick(View view) {
        finish();
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}