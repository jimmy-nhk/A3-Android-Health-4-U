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
import android.widget.ListAdapter;
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
import com.google.firebase.firestore.DocumentSnapshot;
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

    //Params
    Order order;

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

        List<Item> itemList = order.getItemList();
        List<Integer> quantityList = order.getQuantity();

        getVendorById(holder, order.getVendorID() + "", itemList, quantityList);

    }

    private void onBindViewHolder2(@NonNull BillingStoreRecycleViewHolder holder, Vendor v,
                                   List<Item> itemList,
                                   List<Integer> quantityList) {

        // set the value to the xml file
        holder.billingstoreName.setText(v.getStoreName());
        holder.billingstorePrice.setText(order.getPrice() + " $");

        // linear styles
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        holder.billingstoreLinearView.setNestedScrollingEnabled(true);
        holder.billingstoreLinearView.setAdapter( new BillingStoreItemListViewAdapter(itemList, quantityList, context));
        holder.billingstoreLinearView.setDivider(null);

        setListViewHeightBasedOnChildren(holder.billingstoreLinearView);
        //Check processed status and set value to IsProcessing textview
        if (order.getIsCancelled()) {
            holder.billingstoreIsProccessing.setText("CANCELLED");
            holder.billingstoreIsProccessing.setTextColor(context.getColor(R.color.red));
        } else {
            if (order.getIsProcessed()) {
                holder.billingstoreIsProccessing.setText("FINISHED");
                holder.billingstoreIsProccessing.setTextColor(context.getColor(R.color.green));
            } else {
                holder.billingstoreIsProccessing.setText("PROCESSING");
                holder.billingstoreIsProccessing.setTextColor(context.getColor(R.color.grey));
            }
        }
        //set image by URL
        try {
            if (v.getImage()!=null && v.getImage().length() > 0) {
                StorageReference mImageRef =
                        FirebaseStorage.getInstance().getReference(v.getImage());

                final long ONE_MEGABYTE = 1024 * 1024 * 5;
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

        //billingstoreLinearView

    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
    private void getVendorById(@NonNull BillingStoreRecycleViewHolder holder, String s,
                               List<Item> itemList,
                               List<Integer> quantityList) {
        fireStore = FirebaseFirestore.getInstance();
        DocumentReference docRef = fireStore.collection("vendors").document(s);

        // load items
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null) {
                    Log.d(TAG, "value " + s);
                    Log.d(TAG, "value != null");
                    Vendor v = documentSnapshot.toObject(Vendor.class);
                    Log.d(TAG, v.toString());
                    onBindViewHolder2(holder, v, itemList, quantityList);
                }
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
    ListView billingstoreLinearView;

    public BillingStoreRecycleViewHolder(@NonNull View itemView) {
        super(itemView);

        billingstoreName = itemView.findViewById(R.id.billingstoreName);
        billingstoreImage = itemView.findViewById(R.id.billingstoreImage);
        billingstorePrice = itemView.findViewById(R.id.billingstorePrice);
        billingstoreLinearView = itemView.findViewById(R.id.billingstoreListView);
        billingstoreIsProccessing = itemView.findViewById(R.id.billingstoreIsProccessing);

    }

}
