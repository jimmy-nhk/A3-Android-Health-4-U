package com.example.clientapp.helper.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clientapp.R;
import com.example.clientapp.activity.BillingActivity;
import com.example.clientapp.model.Cart;
import com.example.clientapp.model.Order;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryViewHolder> {

    private static final String TAG = "HistoryRecyclerViewAdapter";
    private List<Cart> cartList;
    private Context context;
    private LayoutInflater mLayoutInflater;

    // constructor
    public HistoryRecyclerViewAdapter(List<Cart> cartList, Context context) {
        this.cartList = cartList;
        this.context = context;
        this.mLayoutInflater = LayoutInflater.from(context);

        Log.d("HistoryRecycler", "constructor");

    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View recyclerViewCart = mLayoutInflater.inflate(R.layout.history_cardview, parent, false);

        Log.d("HistoryRecycler" , "onBindViewHolder: onCreateViewHolder");

        return new HistoryViewHolder(recyclerViewCart);
    }

    @SuppressLint({"SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {

        // take the cart
        Cart cart = cartList.get(position);

        // orderList
        List<Order> orderList = cart.getOrderList();

        // idString
        String idString = String.valueOf(orderList.get(0).getId());
//        String idString = "";
//        for (Order order: orderList){
//            String id = idString + " " + order.getId();
//            idString = id;
//        }

//        setVendorImage()
//        getVendor(orderList.get(0).getVendorID());

        // set the value to the xml file
        holder.historyId.setText("Order ID: " + idString);
        holder.historyDate.setText("Date: " + cart.getDate());
        holder.cartPrice.setText(cart.getPrice() + "$");

        holder.isProcessing.setText(cart.getIsFinished() ? "Finished" : "isProcessing");
        holder.isProcessing.setTextColor(cart.getIsFinished()
                ? ContextCompat.getColor(context, R.color.green)
                : ContextCompat.getColor(context, R.color.yellow));

        //TODO: Show order in the cart

        Log.d("HistoryRecycler" , "onBindViewHolder: load data");
        holder.detailBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, BillingActivity.class);
            intent.putExtra("cart", cart);
            context.startActivity(intent);

        });
    }

//    private void getVendor(int vendorID) {
//
//    }
//
//    private void setVendorImage(HistoryViewHolder holder, String imageUrl) {
//        try {
//            if (imageUrl.length() > 0) {
//                StorageReference mImageRef =
//                        FirebaseStorage.getInstance().getReference(imageUrl);
//
//                final long ONE_MEGABYTE = 1024 * 1024;
//                // Handle any errors
//                mImageRef.getBytes(ONE_MEGABYTE)
//                        .addOnSuccessListener(bytes -> {
//                            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                            DisplayMetrics dm = new DisplayMetrics();
//                            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
//
//                            holder.image.setMinimumHeight(dm.heightPixels);
//                            holder.image.setMinimumWidth(dm.widthPixels);
//                            holder.image.setImageBitmap(bm);
//                        }).addOnFailureListener(Throwable::printStackTrace);
//            }
//        } catch (Exception e) {
////            .setImageResource(R.drawable.bun); //Set something else
//            e.printStackTrace();
//        }
//    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }
}


class HistoryViewHolder extends RecyclerView.ViewHolder {
    TextView historyDate;
    TextView historyId;
    TextView cartPrice;
    TextView isProcessing;
    Button detailBtn;
    ImageView image;

    public HistoryViewHolder(@NonNull View itemView) {
        super(itemView);

        historyDate = itemView.findViewById(R.id.historyDate);
        historyId = itemView.findViewById(R.id.historyId);
        cartPrice = itemView.findViewById(R.id.cartPrice);
        detailBtn = itemView.findViewById(R.id.detailBtn);
        isProcessing = itemView.findViewById(R.id.isProcessingTxt);

    }

}
