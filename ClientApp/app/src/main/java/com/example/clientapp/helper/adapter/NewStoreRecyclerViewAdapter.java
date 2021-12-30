package com.example.clientapp.helper.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clientapp.R;
import com.example.clientapp.model.Vendor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class NewStoreRecyclerViewAdapter extends
        RecyclerView.Adapter<NewStoreRecyclerViewAdapter.ViewHolder> {
    private ArrayList<Vendor> vendorList;
    private LayoutInflater mInflater;
    private Context context;

    public NewStoreRecyclerViewAdapter(Context context, ArrayList<Vendor> vendorList) {
        this.mInflater = LayoutInflater.from(context);
        this.vendorList = vendorList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_store_cardview, parent, false);
//        context = layoutView.getContext();
        return new ViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Vendor vendor = vendorList.get(position);
        Log.d("onBindViewHolder", vendor.toString());
        holder.newStoreNameText.setText(vendor.getStoreName());
        holder.newStoreRatingBar.setRating(vendor.getRating());

        // Set image by URL
        setStoreImage(holder, vendor);
    }

    private void setStoreImage(ViewHolder holder, Vendor vendor) {
        try {
            if (vendor.getImage() == null) return;

            if (vendor.getImage().length() > 0) {
                StorageReference mImageRef =
                        FirebaseStorage.getInstance().getReference(vendor.getImage());

                final long ONE_MEGABYTE = 1024 * 1024;
                mImageRef.getBytes(ONE_MEGABYTE)
                        .addOnSuccessListener(bytes -> {
                            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            DisplayMetrics dm = new DisplayMetrics();
                            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);

                            holder.newStoreImage.setMinimumHeight(dm.heightPixels);
                            holder.newStoreImage.setMinimumWidth(dm.widthPixels);
                            holder.newStoreImage.setImageBitmap(bm);
                        }).addOnFailureListener(exception -> {
                    // Handle any errors
                    exception.printStackTrace();
                });
            }
        } catch (Exception e) {
//            .setImageResource(R.drawable.bun); //Set something else
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return vendorList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView newStoreImage;
        private TextView newStoreNameText;
        private RatingBar newStoreRatingBar;

        public ViewHolder(@NonNull View view) {
            super(view);
            newStoreImage = view.findViewById(R.id.newStoreImage);
            newStoreNameText = view.findViewById(R.id.newStoreNameText);
            newStoreRatingBar = view.findViewById(R.id.newStoreRatingBar);
        }
    }
}
