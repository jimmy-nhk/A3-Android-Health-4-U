package com.example.clientapp.helper.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clientapp.R;
import com.example.clientapp.model.Item;
import com.example.clientapp.model.Order;
import com.example.clientapp.model.Vendor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class BillingStoreRecycleViewAdapter extends RecyclerView.Adapter<BillingStoreRecycleViewHolder> {

    private static final String TAG = "BillingStoreRecycleViewAdapter";
    private List<Order> orderList;
    private Context context;
    private LayoutInflater mLayoutInflater;
    private FirebaseFirestore fireStore;
    private BillingStoreItemListViewAdapter mAdapter;

    //Params
    Order order;
    List<Item> itemList;
    List<Integer> quantityList;

    public BillingStoreRecycleViewAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
        this.mLayoutInflater = LayoutInflater.from(context);

        Log.d(TAG, "constructor");

    }

    @NonNull
    @Override
    public BillingStoreRecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View recyclerViewCart = mLayoutInflater.inflate(R.layout.billingstore_cardview, parent, false);

        Log.d(TAG, "onBindViewHolder: onCreateViewHolder");

        return new BillingStoreRecycleViewHolder(recyclerViewCart);
    }

    @SuppressLint({"SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull BillingStoreRecycleViewHolder holder, int position) {

        // take the cart
        order = orderList.get(position);

        itemList = order.getItemList();
        quantityList = order.getQuantity();

        getVendorById(holder, "1");

    }

    private void onBindViewHolder2(@NonNull BillingStoreRecycleViewHolder holder, Vendor v) {

        // set the value to the xml file
        holder.billingstoreName.setText(v.getStoreName());
        holder.billingstorePrice.setText(order.getPrice() + " đồng");
        mAdapter = new BillingStoreItemListViewAdapter(itemList, quantityList, context);

        // linear styles
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        holder.billingstoreRecycleView.setNestedScrollingEnabled(true);
        holder.billingstoreRecycleView.setAdapter(mAdapter);

        //Check processed status and set value to IsProcessing textview
        if (order.getIsCancelled()) {
            holder.billingstoreIsProccessing.setText("CANCELLED");
            holder.billingstoreIsProccessing.setTextColor(context.getColor(R.color.red));
        } else {
            if (order.getIsProcessed()) {
                holder.billingstoreIsProccessing.setText("PROCESSING");
                holder.billingstoreIsProccessing.setTextColor(context.getColor(R.color.white_transparent));
            } else {
                holder.billingstoreIsProccessing.setText("FINISHED");
                holder.billingstoreIsProccessing.setTextColor(context.getColor(R.color.green));
            }
        }
        //set image by URL
        try {
            if (v.getImage().length() > 0) {
                StorageReference mImageRef =
                        FirebaseStorage.getInstance().getReference(v.getImage());

                final long ONE_MEGABYTE = 1024 * 1024 *5;
                mImageRef.getBytes(ONE_MEGABYTE)
                        .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                DisplayMetrics dm = new DisplayMetrics();
                                ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);

                                holder.billingstoreImage.setMinimumHeight(dm.heightPixels);
                                holder.billingstoreImage.setMinimumWidth(dm.widthPixels);
                                holder.billingstoreImage.setImageBitmap(bm);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }
        } catch (Exception e) {
            holder.billingstoreImage.setImageResource(R.drawable.bun); //Set something else
            e.printStackTrace();
        }
        //TODO: Show order in the cart
        Log.d("HistoryRecycler", "onBindViewHolder: load data");

        //billingstoreRecycleView

    }

    private void getVendorById(@NonNull BillingStoreRecycleViewHolder holder, String s) {
        fireStore = FirebaseFirestore.getInstance();
        DocumentReference docRef = fireStore.collection("vendors").document(s);

        // load items
        docRef.addSnapshotListener((value, error) -> {
            if (value != null) {
                Log.d(TAG, "value " + s);
                Log.d(TAG, "value != null");
                Vendor v = value.toObject(Vendor.class);
                Log.d(TAG, v.toString());
                onBindViewHolder2(holder, v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}


class BillingStoreRecycleViewHolder extends RecyclerView.ViewHolder {

    TextView billingstoreIsProccessing;
    TextView billingstoreName;
    ImageView billingstoreImage;
    TextView billingstorePrice;
    ListView billingstoreRecycleView;

    public BillingStoreRecycleViewHolder(@NonNull View itemView) {
        super(itemView);

        billingstoreName = itemView.findViewById(R.id.billingstoreName);
        billingstoreImage = itemView.findViewById(R.id.billingstoreImage);
        billingstorePrice = itemView.findViewById(R.id.billingstorePrice);
        billingstoreRecycleView = itemView.findViewById(R.id.billingstoreListView);
        billingstoreIsProccessing = itemView.findViewById(R.id.billingstoreIsProccessing);

    }

}
