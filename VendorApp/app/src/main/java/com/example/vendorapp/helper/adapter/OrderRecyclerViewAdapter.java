package com.example.vendorapp.helper.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vendorapp.R;
import com.example.vendorapp.model.Order;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //TODO: switch the xml file
        View recyclerViewOrder = mLayoutInflater.inflate(R.layout.order_cart_view, parent, false);

//        Log.d(TAG, "onCreateViewHolder: ");

        return new OrderViewHolder(recyclerViewOrder);

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


        // TODO: Fix order name
        holder.orderIdText.setText("OrderID: " + order.getId() + "");

        // price
        holder.price.setText("Price: " + order.getPrice() + "$");
        // name
        holder.vendorName.setText("vendor name" + order.getVendorID() + "");
        //date
        holder.dateOrder.setText("Date: " + order.getDate() + "");

        //TODO: check again the text appearance
        holder.announcementTxt.setText(order.getIsProcessed() ? "Processed" : order.getIsCancelled() ? "Cancel" :"Not yet process");
        holder.announcementTxt.setTextColor(order.getIsProcessed() ? GREEN_COLOR :  order.getIsCancelled() ? RED_COLOR :BLACK_COLOR);

        holder.cancelBtn.setVisibility(order.getIsProcessed() ? View.GONE : order.getIsCancelled() ? View.GONE : View.VISIBLE);
        holder.processBtn.setVisibility(order.getIsProcessed() ? View.GONE : order.getIsCancelled() ? View.GONE : View.VISIBLE);


//        try {
//            Thread thread = new Thread(new Runnable(){
//                @Override
//                public void run() {
//                    try {
//                        imageURL = new URL(item.getImage());
//                        Bitmap mIcon_val = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
//                        holder.image.setImageBitmap(mIcon_val);
//                    } catch (Exception e) {
//                        Log.e(TAG, e.getMessage());
//                    }
//                }
//            });
//            thread.start();
//        } catch (Exception e) {
//            holder.image.setImageResource(R.drawable.food);
//            e.printStackTrace();
//        }
        //TODO: Image and Button
        holder.processBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                order.setIsProcessed(true);
                orderCollection.document(order.getId() + "").set(order).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Order successfully updated!");

                        Log.d(TAG, "processBtn: click: " + order.toString());

                        holder.announcementTxt.setText("Processed!");
                        holder.announcementTxt.setTextColor( GREEN_COLOR );

                        holder.cancelBtn.setVisibility(View.GONE);
                        holder.cancelBtn.setEnabled(false);
                        holder.processBtn.setEnabled(false);
                        holder.processBtn.setVisibility(View.GONE);

                    }
                });
            }
        });

        holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order.setIsCancelled(true);
                order.setPrice(0);
                orderCollection.document(order.getId() + "").set(order).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Order successfully updated!");

                        Log.d(TAG, "cancelBtn: click: " + order.toString());

                        holder.announcementTxt.setText("Cancel!");
                        holder.announcementTxt.setTextColor( RED_COLOR );

                        holder.cancelBtn.setVisibility(View.GONE);
                        holder.cancelBtn.setEnabled(false);
                        holder.processBtn.setEnabled(false);
                        holder.processBtn.setVisibility(View.GONE);
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {

//        Log.d(TAG, "OrderRecyclerViewAdapter: getItemCount: " + orderList.size());
        return orderList.size();
    }
}

class OrderViewHolder extends RecyclerView.ViewHolder {

    ImageView image;
    TextView orderIdText;
    TextView vendorName;
    TextView price;
    TextView dateOrder;
    TextView announcementTxt;
    Button processBtn;
    Button cancelBtn;

    public OrderViewHolder(@NonNull View orderView) {
        super(orderView);

        Log.d("OrderRecyclerViewAdapter", "OrderViewHolder: constructor");
        image = orderView.findViewById(R.id.itemImage);
        orderIdText = orderView.findViewById(R.id.orderIdTxt);
        vendorName = orderView.findViewById(R.id.itemVendorName);
        dateOrder = orderView.findViewById(R.id.dateOrder);
        announcementTxt = orderView.findViewById(R.id.announcementTxt);
        price = orderView.findViewById(R.id.itemPrice);
        processBtn = orderView.findViewById(R.id.processBtn);
        cancelBtn = orderView.findViewById(R.id.cancelBtn);
    }
}