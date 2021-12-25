package com.example.clientapp.helper;

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
import androidx.recyclerview.widget.RecyclerView;

import com.example.clientapp.R;
import com.example.clientapp.model.Cart;

import java.util.List;

public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryViewHolder> {

    private List<Cart> cartList;
    private Context context;
    private LayoutInflater mLayoutInflater;

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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {

        // take the cart
        Cart cart = cartList.get(position);

        // set the value to the xml file
        holder.historyId.setText("CartId: " + cart.getId() + "");
        holder.historyDate.setText("Date: " + cart.getDate());
        holder.cartPrice.setText("Total Price: " + cart.getPrice() + "$");

        Log.d("HistoryRecycler" , "onBindViewHolder: load data");
        holder.detailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
    Button detailBtn;

    public HistoryViewHolder(@NonNull View itemView) {
        super(itemView);

        historyDate = itemView.findViewById(R.id.historyDate);
        historyId = itemView.findViewById(R.id.historyId);
        cartPrice = itemView.findViewById(R.id.cartPrice);
        detailBtn = itemView.findViewById(R.id.detailBtn);

    }

}
