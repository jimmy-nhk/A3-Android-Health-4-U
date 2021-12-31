package com.example.clientapp.helper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clientapp.helper.ItemViewModel;
import com.example.clientapp.model.Item;

import java.util.ArrayList;

public class NewItemRecyclerViewAdapter extends
        RecyclerView.Adapter<NewItemRecyclerViewAdapter.ViewHolder> {
    private ArrayList<Item> itemList;
    private Context context;
    private LayoutInflater mLayoutInflater;
    private ItemViewModel viewModel;

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
        return 0;
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
