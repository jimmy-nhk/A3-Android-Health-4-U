package com.example.clientapp.helper.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clientapp.R;
import com.example.clientapp.activity.BillingActivity;
import com.example.clientapp.model.Cart;
import com.example.clientapp.model.Order;

import java.util.List;

public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryViewHolder> {

    private List<Cart> cartList;
    private Context context;
    private LayoutInflater mLayoutInflater;

    // constructor
    public HistoryRecyclerViewAdapter(List<Cart> cartList, Context context) {
        this.cartList = cartList;
        this.context = context;
        this.mLayoutInflater = LayoutInflater.from(context);

        Log.d("HistoryRecycler" , "constructor");

    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View recyclerViewCart = mLayoutInflater.inflate(R.layout.history_cardview, parent, false);

        Log.d("HistoryRecycler" , "onBindViewHolder: onCreateViewHolder");

        return new HistoryViewHolder(recyclerViewCart);
    }

    @SuppressLint({"SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {

        // take the cart
        Cart cart = cartList.get(position);

        // orderList
        List<Order> orderList = cart.getOrderList();

        // idString
        String idString = "";
        for (Order order: orderList){
            String id = idString + " " + String.valueOf(order.getId());
            idString = id;
        }

        // set the value to the xml file
        holder.historyId.setText("OrderIdList: " + idString );
        holder.historyDate.setText("Date: " + cart.getDate());
        holder.cartPrice.setText("Total Price: " + cart.getPrice() + "$");

        holder.isProcessing.setText(cart.getIsFinished() ? "Finished" : "isProcessing");
        holder.isProcessing.setTextColor(cart.getIsFinished() ? ContextCompat.getColor(context, R.color.green) : ContextCompat.getColor(context, R.color.black));

        //TODO: Show order in the cart
        Log.d("HistoryRecycler" , "onBindViewHolder: load data");
        holder.detailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BillingActivity.class);
                intent.putExtra("cart", cart);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }
}


class HistoryViewHolder extends RecyclerView.ViewHolder {


    TextView historyDate;
    TextView historyId;
    TextView cartPrice;
    TextView isProcessing;
    Button detailBtn;

    public HistoryViewHolder(@NonNull View itemView) {
        super(itemView);

        historyDate = itemView.findViewById(R.id.historyDate);
        historyId = itemView.findViewById(R.id.historyId);
        cartPrice = itemView.findViewById(R.id.cartPrice);
        detailBtn = itemView.findViewById(R.id.detailBtn);
        isProcessing = itemView.findViewById(R.id.isProcessingTxt);

    }

}
