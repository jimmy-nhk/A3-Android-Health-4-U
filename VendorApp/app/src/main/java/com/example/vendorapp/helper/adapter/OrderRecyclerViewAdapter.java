package com.example.vendorapp.helper.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vendorapp.R;
import com.example.vendorapp.activity.OrderDetailActivity;
import com.example.vendorapp.model.Client;
import com.example.vendorapp.model.Item;
import com.example.vendorapp.model.Order;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.URL;
import java.util.List;

public class OrderRecyclerViewAdapter extends RecyclerView.Adapter<OrderViewHolder> {

    private static final String TAG = "OrderRecycleViewAdapter";
    private List<Order> orderList;
    private Context context;
    private LayoutInflater mLayoutInflater;
    private
    URL imageURL = null;
    private DocumentReference orderDocRef;

    // init db
    private static final String ORDER_COLLECTION = "orders";
    private FirebaseFirestore fireStore;
    private CollectionReference orderCollection;
    private DocumentReference orderRef;

    public OrderRecyclerViewAdapter(Context context, List<Order> data) {
//        Log.d("OrderRecyclerViewAdapter", "constructor");
//        Log.d("OrderRecyclerViewAdapter", "context: " + context.toString());

        this.context = context;
        this.orderList = data;
        this.mLayoutInflater = LayoutInflater.from(context);

//        Log.d("OrderRecyclerViewAdapter", "constructor: orderList: " + data.size());

    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //switch the xml file
        View recyclerViewOrder = mLayoutInflater.inflate(R.layout.order_cart_view, parent, false);

//        Log.d(TAG, "onCreateViewHolder: ");

        recyclerViewOrder.setOnClickListener(v -> handleRecyclerOrderClick((RecyclerView) parent, v));

        return new OrderViewHolder(recyclerViewOrder);

    }

    // pass to order details intent
    private void handleRecyclerOrderClick(RecyclerView parent, View v) {
        int position = parent.getChildLayoutPosition(v);
        Order order = orderList.get(position);
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra("order",order);
        context.startActivity(intent);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
//        Log.d("OrderRecyclerViewAdapter", "render position: " + position);
        final int GREEN_COLOR = ContextCompat.getColor(context, R.color.green);
        final int BLACK_COLOR = ContextCompat.getColor(context, R.color.black);
        final int RED_COLOR = ContextCompat.getColor(context, R.color.red);

        Order order = this.orderList.get(position);

        // init fireStore db
        fireStore = FirebaseFirestore.getInstance();
        orderCollection = fireStore.collection(ORDER_COLLECTION);
        orderRef = orderCollection.document(String.valueOf(order.getId()));


//        // Fix order name
//        holder.orderIdText.setText("OrderID: " + order.getId() + "");

        // price
        holder.price.setText(order.getPrice() + "$");
        //date
        holder.dateOrder.setText( order.getDate() + "");

        //check again the text appearance
        holder.announcementTxt.setText(order.getIsProcessed() ? "PROCESSED" : order.getIsCancelled() ? "CANCELLED" : "Not yet process");
        holder.announcementTxt.setTextColor(order.getIsProcessed() ? GREEN_COLOR : order.getIsCancelled() ? RED_COLOR : BLACK_COLOR);

        holder.cancelBtn.setVisibility(order.getIsProcessed() ? View.GONE : order.getIsCancelled() ? View.GONE : View.VISIBLE);
        holder.processBtn.setVisibility(order.getIsProcessed() ? View.GONE : order.getIsCancelled() ? View.GONE : View.VISIBLE);


        //Image and button
        holder.processBtn.setOnClickListener(v -> {
            initProcessDialog(context, holder, order);
        });

        holder.cancelBtn.setOnClickListener(v -> {
            initCancelDialog(context, holder, order);

        });

        //ListView

        List<Item> itemList = order.getItemList();
        List<Integer> quantityList = order.getQuantity();

        // linear styles
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        holder.itemListV.setNestedScrollingEnabled(false);
        holder.itemListV.setAdapter(new OrderItemListViewAdapter(itemList, quantityList, context));
        holder.itemListV.setDivider(null);
        setListViewHeightBasedOnChildren(holder.itemListV);

        //get client by id
        getClientById(holder, order.getClientID() + "", itemList, quantityList);

    }

    // init cancel dialog
    private void initCancelDialog(Context context, OrderViewHolder holder, Order order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                context);
//        builder.setIcon(context.getResources().getDrawable(
//                R.drawable.ic_launcher_foreground));
        builder.setTitle("Cancel order");
        builder.setMessage("Are you sure you want to cancel this order?");
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        handleCancelOrder(holder, order);
                    }
                });
        builder.setNegativeButton("No",
                (dialog, which) -> dialog.dismiss());
        builder.setCancelable(false);
        AlertDialog alert = builder.create();
        alert.show();
    }

    // init process
    private void initProcessDialog(Context context, OrderViewHolder holder, Order order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                context);
//        builder.setIcon(context.getResources().getDrawable(
//                R.drawable.ic_launcher_foreground));
        builder.setTitle("Process order");
        builder.setMessage("Would you like to process this order?");
        builder.setPositiveButton("Confirm",
                (dialog, which) -> handleProcessOrder(holder, order));
        builder.setNegativeButton("Cancel",
                (dialog, which) -> dialog.dismiss());
        builder.setCancelable(false);
        AlertDialog alert = builder.create();
        alert.show();
    }

    // handle order process
    private void handleProcessOrder(OrderViewHolder holder, Order order) {
        order.setIsProcessed(true);
        orderCollection.document(order.getId() + "").set(order.toMap()).addOnSuccessListener(unused -> {
            Log.d(TAG, "Order successfully updated!");

            Log.d(TAG, "processBtn: click: " + order.toString());
//            Toast.makeText(context, "Successfully process order " + order.getId()
//                    , Toast.LENGTH_SHORT).show();

            holder.announcementTxt.setText("Processed!");
            holder.announcementTxt.setTextColor(ContextCompat.getColor(context, R.color.green));
            holder.cancelBtn.setVisibility(View.GONE);
            holder.cancelBtn.setEnabled(false);
            holder.processBtn.setEnabled(false);
            holder.processBtn.setVisibility(View.GONE);
        });
    }

    // handle cancel order
    private void handleCancelOrder(OrderViewHolder holder, Order order) {

        // set order cancel
        order.setIsCancelled(true);
        order.setPrice(0);
        Log.d(TAG, "cancel order=" + order.toString());
        orderCollection.document(order.getId() + "").set(order.toMap()).addOnSuccessListener(unused -> {
            Log.d(TAG, "Order successfully updated!");

            Log.d(TAG, "cancelBtn: click: " + order.toString());
//            Toast.makeText(context, "Cancelled order " + order.getId(), Toast.LENGTH_SHORT).show();

            holder.announcementTxt.setText("Cancel!");
            holder.announcementTxt.setTextColor(ContextCompat.getColor(context, R.color.red));

            holder.cancelBtn.setVisibility(View.GONE);
            holder.cancelBtn.setEnabled(false);
            holder.processBtn.setEnabled(false);
            holder.processBtn.setVisibility(View.GONE);
        });
    }

    // get client by id
    private void getClientById(OrderViewHolder holder, String s, List<Item> itemList, List<Integer> quantityList) {
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
                        onBindViewHolder2(holder, c, itemList, quantityList);

                    }
                }
            });
        } catch (Exception ignored) {

        }
    }

    // onbind view holder2
    private void onBindViewHolder2(@NonNull OrderViewHolder holder, Client c,
                                   List<Item> itemList,
                                   List<Integer> quantityList) {

        // name
        holder.clientName.setText(c.getFullName());
        // phone
        holder.clientPhone.setText(c.getPhone());
        // address
        holder.clientAddress.setText(c.getAddress());
    }

    @Override
    public int getItemCount() {

//        Log.d(TAG, "OrderRecyclerViewAdapter: getItemCount: " + orderList.size());
        return orderList.size();
    }

    // set list view height based on children
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

class OrderViewHolder extends RecyclerView.ViewHolder {

    // attributes
    TextView clientName;
    TextView clientPhone;
    TextView clientAddress;
    ListView itemListV;
    TextView dateOrder;
    TextView price;
    TextView announcementTxt;
    Button processBtn;
    Button cancelBtn;

    public OrderViewHolder(@NonNull View orderView) {
        super(orderView);

        // attach view
        Log.d("OrderRecyclerViewAdapter", "OrderViewHolder: constructor");
        clientName = orderView.findViewById(R.id.orderClientName);
        clientPhone = orderView.findViewById(R.id.orderClientPhoneTxt);
        clientAddress = orderView.findViewById(R.id.orderClientAddressTxt);
        itemListV = orderView.findViewById(R.id.orderItemListView);
        dateOrder = orderView.findViewById(R.id.dateOrder);
        announcementTxt = orderView.findViewById(R.id.announcementTxt);
        price = orderView.findViewById(R.id.itemPrice);
        processBtn = orderView.findViewById(R.id.processBtn);
        cancelBtn = orderView.findViewById(R.id.cancelBtn);
    }
}