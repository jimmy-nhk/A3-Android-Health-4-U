package com.example.vendorapp.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vendorapp.R;
import com.example.vendorapp.model.Item;
import com.example.vendorapp.model.Order;

import java.net.URL;
import java.util.List;

public class OrderRecyclerViewAdapter extends RecyclerView.Adapter<OrderViewHolder> {

    private static final String TAG = "OrderRecycleViewAdapter";
    private List<Order> orderList;
    private Context context;
    private LayoutInflater mLayoutInflater;
    private
    URL imageURL = null;

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
    public OrderViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        //TODO: switch the xml file
        View recyclerViewOrder = mLayoutInflater.inflate(R.layout.order_cart_view, parent, false);

//        Log.d(TAG, "onCreateViewHolder: ");

        return new OrderViewHolder(recyclerViewOrder);

    }



    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
//        Log.d("OrderRecyclerViewAdapter", "render position: " + position);

        Order order = this.orderList.get(position);

        // TODO: Fix order name
        holder.name.setText("OrderID: "+ order.getId() + "");

        // price
        holder.price.setText("Price: "+ order.getPrice() + "$");
        // name
        holder.vendorName.setText("vendor name"+order.getVendorID() + "");
        //date
        holder.category.setText("Date: "+ order.getDate()+ "");

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


    }

    @Override
    public int getItemCount() {

        Log.d(TAG, "OrderRecyclerViewAdapter: getItemCount: " + orderList.size());
        return orderList.size();
    }
}

class OrderViewHolder extends RecyclerView.ViewHolder {

    ImageView image;
    TextView name;
    TextView vendorName;
    TextView price;
    TextView category;
    Button addBtn;

    public OrderViewHolder(@NonNull View orderView) {
        super(orderView);

        Log.d("OrderRecyclerViewAdapter", "OrderViewHolder: constructor");
        image = orderView.findViewById(R.id.itemImage);
        name = orderView.findViewById(R.id.itemName);
        vendorName = orderView.findViewById(R.id.itemVendorName);
        category = orderView.findViewById(R.id.itemCategory);
        price = orderView.findViewById(R.id.itemPrice);
        addBtn = orderView.findViewById(R.id.addItem);
    }
}