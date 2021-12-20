package com.example.clientapp.helper;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clientapp.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clientapp.model.Item;

import java.util.List;

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemViewHolder>{

    private List<Item> itemList;
    private Context context;
    private LayoutInflater mLayoutInflater;
    private ItemViewModel viewModel;

    public ItemRecyclerViewAdapter(Context context, List<Item> datas , ItemViewModel viewModel ) {
        Log.d("ItemRecyclerViewAdapter" , "constructor");
        this.context = context;
        this.itemList = datas;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View recyclerViewItem = mLayoutInflater.inflate(R.layout.item_cardview, parent, false);

        Log.d("ItemRecyclerViewAdapter" , "Here");
        recyclerViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: Show Info intent of the item
//                handleRecyclerItemClick( (RecyclerView)parent, v);
            }
        });
        return new ItemViewHolder(recyclerViewItem);
        
    }

    // handle recycle item click
    private void handleRecyclerItemClick(RecyclerView parent, View v) {
        int itemPosition = parent.getChildLayoutPosition(v);

        Item item = this.itemList.get(itemPosition);

        viewModel.addItem(item);


        Toast.makeText(this.context, item.getName(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        Log.d("ItemRecyclerViewAdapter" , "render");

        Item item =  itemList.get(position);

        holder.name.setText(item.getName());
        holder.price.setText(item.getPrice() +"");
        holder.vendorName.setText(item.getVendorID() + "");
        holder.category.setText(item.getCategory());


        // init final position for on click
        int finalPosition = position;
        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Item item = itemList.get(finalPosition);

                viewModel.addItem(item);
            }
        });

        //TODO: Image and Button
        holder.image.setImageResource(R.drawable.avatar_foreground);

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