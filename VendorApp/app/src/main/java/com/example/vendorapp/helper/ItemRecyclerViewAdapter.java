package com.example.vendorapp.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.StrictMode;
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    private static final String TAG = "ItemRecycleViewAdapter";
    private List<Item> itemList;
    private Context context;
    private LayoutInflater mLayoutInflater;
    private
    URL imageURL = null;

    public ItemRecyclerViewAdapter(Context context, List<Item> data) {
        Log.d("ItemRecyclerViewAdapter", "constructor");
        this.context = context;
        this.itemList = data;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View recyclerViewItem = mLayoutInflater.inflate(R.layout.item_cardview, parent, false);

        Log.d("ItemRecyclerViewAdapter", "Here");
        recyclerViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRecyclerItemClick((RecyclerView) parent, v);
            }
        });
        return new ItemViewHolder(recyclerViewItem);

    }

    // handle recycle item click
    private void handleRecyclerItemClick(RecyclerView parent, View v) {
        int itemPosition = parent.getChildLayoutPosition(v);

        Item item = this.itemList.get(itemPosition);

        Toast.makeText(this.context, item.getName(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Log.d("ItemRecyclerViewAdapter", "render");

        Item item = this.itemList.get(position);

        holder.name.setText(item.getName());
        holder.price.setText(item.getPrice() + "");
        holder.vendorName.setText(item.getVendorID() + "");
        holder.category.setText(item.getCategory());
        try {
            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        imageURL = new URL(item.getImage());
                        Bitmap mIcon_val = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                        holder.image.setImageBitmap(mIcon_val);
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            });
            thread.start();
        } catch (Exception e) {
            holder.image.setImageResource(R.drawable.food);
            e.printStackTrace();
        }
        //TODO: Image and Button


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
class ItemViewHolder extends RecyclerView.ViewHolder {

    ImageView image;
    TextView name;
    TextView vendorName;
    TextView price;
    TextView category;
    Button addBtn;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);

        image = itemView.findViewById(R.id.itemImage);
        name = itemView.findViewById(R.id.itemName);
        vendorName = itemView.findViewById(R.id.itemVendorName);
        category = itemView.findViewById(R.id.itemCategory);
        price = itemView.findViewById(R.id.itemPrice);
        addBtn = itemView.findViewById(R.id.addItem);
    }
}