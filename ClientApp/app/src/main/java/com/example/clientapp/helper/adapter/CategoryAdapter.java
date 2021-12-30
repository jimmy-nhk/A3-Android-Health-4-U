package com.example.clientapp.helper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clientapp.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    ArrayList<String> arrayList;

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public CategoryAdapter(Context context, ArrayList<String> arrayList) {
        this.mInflater = LayoutInflater.from(context);
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_cardview, parent, false);

        return new ViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.categoryBtn.setText(arrayList.get(position));
        holder.categoryBtn.setOnClickListener(v -> {
            if (mClickListener != null) mClickListener.onItemClick(v, holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return arrayList.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements com.example.clientapp.helper.adapter.ViewHolder {
        public Button categoryBtn;

        public ViewHolder(View view) {
            super(view);
            categoryBtn = view.findViewById(R.id.list_view_category);
        }
    }
}