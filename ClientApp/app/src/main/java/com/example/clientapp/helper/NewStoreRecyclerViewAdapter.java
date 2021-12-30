package com.example.clientapp.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clientapp.model.Vendor;

import java.util.ArrayList;

public class NewStoreRecyclerViewAdapter extends
        RecyclerView.Adapter<NewStoreRecyclerViewAdapter.ViewHolder> {
    private ArrayList<Vendor> vendorList;
    private LayoutInflater mInflater;

    public NewStoreRecyclerViewAdapter(Context context, ArrayList<Vendor> vendorList) {
        this.mInflater = LayoutInflater.from(context);
        this.vendorList = vendorList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return vendorList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
//        private ImageView mImageHero;
//        private TextView mTextName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            mImageHero = itemView.findViewById(R.id.image_hero);
//            mTextName = itemView.findViewById(R.id.text_name);
        }
    }
}
