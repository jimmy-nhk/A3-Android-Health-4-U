package com.example.vendorapp.helper.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.vendorapp.R;
import com.example.vendorapp.model.Item;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class OrderItemListViewAdapter extends BaseAdapter {

    private static final String TAG = "BillingStoreItemRecycleViewAdapter";
    private List<Item> itemList;
    private List<Integer> quantityList;
    private Context context;
    private LayoutInflater mLayoutInflater;
    private FirebaseFirestore fireStore;

    //Params

    public OrderItemListViewAdapter(List<Item> itemList, List<Integer> quantityList, Context context) {
        this.itemList = itemList;
        this.quantityList = quantityList;
        this.context = context;
        this.mLayoutInflater = LayoutInflater.from(context);

        Log.d(TAG, "constructor");

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        BillingStoreItemRecycleViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.order_item_cardview, parent, false);
            holder = new BillingStoreItemRecycleViewHolder();
            holder.billingstoreItemName =  convertView.findViewById(R.id.orderItemName);
            holder.billingstoreItemQuantity =  convertView.findViewById(R.id.orderItemQuantity);
            holder.billingstoreItemPrice =  convertView.findViewById(R.id.orderItemPrice);
            convertView.setTag(holder);
        } else {
            holder = (BillingStoreItemRecycleViewHolder) convertView.getTag();
        }

        // take the cart
        Item item = itemList.get(position);
        Log.d(TAG,"Item "+itemList.get(0).toString());
        if (item== null){
            Log.d(TAG,"Item "+itemList.get(0).toString());
        }
        Log.d(TAG,"Item "+item.toString());

        holder.billingstoreItemName.setText(item.getName());
        holder.billingstoreItemQuantity.setText(quantityList.get(position)+"");
        holder.billingstoreItemPrice.setText(item.getPrice()+" $");

        return convertView;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return  itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return  position;
    }
    static class BillingStoreItemRecycleViewHolder {


        TextView billingstoreItemQuantity;
        TextView billingstoreItemName;
        TextView billingstoreItemPrice;

    }

}



