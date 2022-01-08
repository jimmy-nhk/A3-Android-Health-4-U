package com.example.clientapp.helper.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.clientapp.R;
import com.example.clientapp.model.Item;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class BillingStoreItemListViewAdapter extends BaseAdapter {

    private static final String TAG = "BillingStoreItemRecycleViewAdapter";
    private List<Item> itemList;
    private List<Integer> quantityList;
    private Context context;
    private LayoutInflater mLayoutInflater;
    private FirebaseFirestore fireStore;

    //Params

    public BillingStoreItemListViewAdapter(List<Item> itemList,List<Integer> quantityList, Context context) {
        this.itemList = itemList;
        this.quantityList = quantityList;
        this.context = context;
        this.mLayoutInflater = LayoutInflater.from(context);

        Log.d(TAG, "constructor");

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        BillingStoreItemRecycleViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.billingstore_item_cardview, parent, false);
            holder = new BillingStoreItemRecycleViewHolder();
            holder.billingstoreItemName =  convertView.findViewById(R.id.billingstoreItemName);
            holder.billingstoreItemQuantity =  convertView.findViewById(R.id.billingstoreItemQuantity);
            holder.billingstoreItemPrice =  convertView.findViewById(R.id.billingstoreItemPrice);
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



