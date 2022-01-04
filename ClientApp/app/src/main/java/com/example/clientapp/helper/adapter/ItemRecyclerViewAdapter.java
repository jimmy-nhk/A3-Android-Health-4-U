package com.example.clientapp.helper.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clientapp.R;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clientapp.activity.MainActivity;
import com.example.clientapp.fragment.ItemDetailsFragment;
import com.example.clientapp.helper.viewModel.ItemViewModel;
import com.example.clientapp.model.Item;
import com.example.clientapp.model.Vendor;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemViewHolder>{

    private List<Item> itemList;
    private Context context;
    private LayoutInflater mLayoutInflater;
    private ItemViewModel viewModel;

    public ItemRecyclerViewAdapter(Context context, List<Item> data, ItemViewModel viewModel) {
        Log.d("ItemRecyclerViewAdapter" , "constructor");
        this.context = context;
        this.itemList = data;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View recyclerViewItem = mLayoutInflater.inflate(R.layout.item_cardview, parent, false);

//        Log.d("ItemRecyclerViewAdapter" , "Here");
        // Set item on click listener
        recyclerViewItem.setOnClickListener(v -> handleRecyclerItemClick((RecyclerView) parent, v));
        return new ItemViewHolder(recyclerViewItem);
    }

    // handle recycle item click
    private void handleRecyclerItemClick(RecyclerView parent, View v) {
        // Get item
        int itemPosition = parent.getChildLayoutPosition(v);
        Item item = this.itemList.get(itemPosition);

        // Put item in bundle to send to ItemDetails fragment
        Bundle bundle = new Bundle();
        bundle.putParcelable("item", item);

        // Get fragment
        Fragment fragment = new ItemDetailsFragment();
        fragment.setArguments(bundle);

        // Go to item detail fragment
        MainActivity mainActivity = (MainActivity) context;
        mainActivity.loadFragmentWithBackStack(fragment);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
//        Log.d("ItemRecyclerViewAdapter" , "render");
        Item item =  itemList.get(position);

        holder.name.setText((item.getName()));
        holder.price.setText((item.getPrice() + "$"));
        holder.vendorName.setText(("VendorID: " + item.getVendorID()));
        holder.category.setText(("Category: " + item.getCategory()));
        setItemImage(holder, item.getImage());

        // init final position for on click
        holder.addBtn.setOnClickListener(v -> {
            viewModel.addItem(item);
            Toast.makeText(v.getContext(), "Added item" + item.getName() + "to card", Toast.LENGTH_SHORT).show();
        });

        //TODO: Image and Button
        holder.image.setImageResource(R.drawable.food);
    }

    private void setItemImage(ItemViewHolder holder, String imageUrl) {
        try {
            if (imageUrl.length() > 0) {
                StorageReference mImageRef =
                        FirebaseStorage.getInstance().getReference(imageUrl);

                final long ONE_MEGABYTE = 1024 * 1024;
                // Handle any errors
                mImageRef.getBytes(ONE_MEGABYTE)
                        .addOnSuccessListener(bytes -> {
                            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            DisplayMetrics dm = new DisplayMetrics();
                            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);

                            holder.image.setMinimumHeight(dm.heightPixels);
                            holder.image.setMinimumWidth(dm.widthPixels);
                            holder.image.setImageBitmap(bm);
                        }).addOnFailureListener(Throwable::printStackTrace);
            }
        } catch (Exception e) {
//            .setImageResource(R.drawable.bun); //Set something else
            e.printStackTrace();
        }
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
//    Button addBtn;
    LinearLayout addBtn;

    @SuppressLint("ClickableViewAccessibility")
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