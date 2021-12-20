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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clientapp.R;
import com.example.clientapp.model.Item;
import com.example.clientapp.model.Order;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.List;

public class CartItemRecyclerViewAdapter extends RecyclerView.Adapter<CartItemViewHolder> {
    private List<Item> itemList;
    private Context context;
    private LayoutInflater mLayoutInflater;
    private ItemViewModel viewModel;
//    final static String TAG = "CartItemRecyclerViewAdapter";

    public CartItemRecyclerViewAdapter(Context context, List<Item> data , ItemViewModel viewModel) {
        Log.d("ItemRecyclerViewAdapter" , "constructor");
        this.context = context;
        this.itemList = data;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cart_cardview, parent, false);
//        CartItemViewHolder cartItemViewHolder = ;
        return new CartItemViewHolder(view).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.name.setText(item.getName());
        holder.price.setText(("Price: " + item.getPrice()));
        holder.vendorName.setText(("VendorID: " + item.getVendorID()));
        holder.category.setText(("Category: " + item.getCategory()));

//        Log.d(CartItemRecyclerViewAdapter.class.getSimpleName(), itemList.get(position).toString());

        //TODO: Image and Button
        holder.image.setImageResource(R.drawable.avatar_foreground);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public void removeItemAt(int position) {
        itemList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, itemList.size());
    }
}

class CartItemViewHolder extends RecyclerView.ViewHolder {

    CartItemRecyclerViewAdapter adapter;
    ImageView image;
    TextView name;
    TextView vendorName;
    TextView price;
    TextView category;
    Button deleteBtn;

    public CartItemViewHolder(@NonNull View itemView) {
        super(itemView);

        image = itemView.findViewById(R.id.itemImage);
        name = itemView.findViewById(R.id.itemName);
        vendorName = itemView.findViewById(R.id.itemVendorName);
        category = itemView.findViewById(R.id.itemCategory);
        price = itemView.findViewById(R.id.itemPrice);
        deleteBtn = itemView.findViewById(R.id.deleteItem);

        deleteBtn.setOnClickListener(v -> handleDeleteCartItem(itemView));
    }

    private void handleDeleteCartItem(View itemView) {
        int position = getAdapterPosition();
        adapter.removeItemAt(position);
    }

    public CartItemViewHolder linkAdapter(CartItemRecyclerViewAdapter adapter) {
        this.adapter = adapter;
        return this;
    }
}