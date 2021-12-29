package com.example.clientapp.helper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clientapp.R;

import java.util.ArrayList;

public class CategoryHomeAdapter extends RecyclerView.Adapter<CategoryHomeAdapter.ViewHolder> {
    private ArrayList<String> arrayList;

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public CategoryHomeAdapter(Context context, ArrayList<String> arrayList) {
        this.mInflater = LayoutInflater.from(context);
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_home_cardview, parent, false);

        return new ViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.categoryText.setText(arrayList.get(position));
        int imgSrc;
        float textSize = 24;
        switch(position) {
            case 1:
                imgSrc = R.drawable.category_noodles;
                textSize = 23;
                break;
            case 2:
                imgSrc = R.drawable.category_banhmi;
                textSize = 20;
                break;
            case 3:
                imgSrc = R.drawable.category_salad;
                break;
            case 4:
                imgSrc = R.drawable.category_snacks;
                break;
            case 5:
                imgSrc = R.drawable.category_drinks;
                break;
            default:
                imgSrc = R.drawable.category_rice;
                textSize = 24;
        }
        holder.categoryImg.setImageResource(imgSrc);
        holder.categoryText.setTextSize(textSize);

        holder.categoryBtn.setOnClickListener(v -> {
            if (mClickListener != null) mClickListener.onItemClick(v, holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    // Convenience method for getting data at click position
    public String getItem(int id) {
        return arrayList.get(id);
    }

    // Allows clicks events to be caught
    public void setClickListener(CategoryHomeAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // Parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements com.example.clientapp.helper.ViewHolder {
        public LinearLayout categoryBtn;
        public TextView categoryText;
        public ImageView categoryImg;

        public ViewHolder(View view) {
            super(view);
            categoryBtn = view.findViewById(R.id.category_home_btn);
            categoryText = view.findViewById(R.id.category_home_text);
            categoryImg = view.findViewById(R.id.category_home_img);
        }
    }
}