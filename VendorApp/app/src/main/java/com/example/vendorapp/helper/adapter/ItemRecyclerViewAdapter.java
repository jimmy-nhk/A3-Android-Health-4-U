package com.example.vendorapp.helper.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
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
import com.example.vendorapp.model.Order;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    private static final String TAG = "ItemRecycleViewAdapter";
    private List<Item> itemList;
    private Context context;
    private LayoutInflater mLayoutInflater;
    private URL imageURL = null;

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

//        new FetchImageTask(item.getImage()) {
//            @Override
//            protected void onPostExecute(Bitmap result) {
//                if (result != null) {
//                    holder.image.setImageBitmap(result);
//                }
//            }
//        }.execute("IMAGE_URL");

        try {
            if (item.getImage().length() > 0) {
                try {
                    StorageReference mImageRef =
                            FirebaseStorage.getInstance().getReference(item.getImage());
                    final long ONE_MEGABYTE = 1024 * 1024;
                    mImageRef.getBytes(ONE_MEGABYTE)
                            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    DisplayMetrics dm = new DisplayMetrics();
                                    ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);

                                    holder.image.setMinimumHeight(dm.heightPixels);
                                    holder.image.setMinimumWidth(dm.widthPixels);
                                    holder.image.setImageBitmap(bm);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                } catch (Exception e){

                }


            }
        } catch (Exception e){
            e.printStackTrace();
        }


        holder.name.setText(item.getName());
        holder.price.setText((item.getPrice() + " $"));
//        holder.vendorName.setText(item.getVendorID() + "");
        holder.category.setText(item.getCategory());

        holder.deleteBtn.setOnClickListener(v -> {
            initDeleteItemDialog(context, item, position);
//            deleteItem(item.getId(), position);
        });

//        //TODO: Implement service
//        Intent intent = new Intent(context, LoadImageIntentService.class);
//        intent.putExtra("imageUrl", item.getImage());
//
//
//        context.startService(intent);

//        try {
//            Thread thread = new Thread(new Runnable(){
//                @Override
//                public void run() {
//                    try {
//                        imageURL = new URL(item.getImage());
//                        Bitmap mIcon_val = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
//                        holder.image.setImageBitmap(mIcon_val);
//                    } catch (Exception e) {
//                        Log.e(TAG, e.getMessage());
//                    }
//                }
//            });
//            thread.start();
//        } catch (Exception e) {
//            holder.image.setImageResource(R.drawable.food);
//            e.printStackTrace();
//        }
        //TODO: Image and Button


    }

    private void initDeleteItemDialog(Context context, Item item, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                context);
//        builder.setIcon(context.getResources().getDrawable(
//                R.drawable.ic_launcher_foreground));
        builder.setTitle("Delete item");
        builder.setMessage("Are you sure you want to delete this item?");
        builder.setPositiveButton("Yes",
                (dialog, which) -> deleteItem(item.getId(), position));
        builder.setNegativeButton("No",
                (dialog, which) -> dialog.dismiss());
        builder.setCancelable(false);
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void deleteItem(int itemId, int position) {
        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        fireStore.collection("items").document(itemId + "")
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot ITEM successfully deleted!");
                    Toast.makeText(context,
                            "Deleted item " + itemList.get(position).getName(),
                            Toast.LENGTH_SHORT).show();
                    removeAt(position);
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting ITEM document", e));
    }

    private class FetchImageTask extends AsyncTask<String, Integer, Bitmap> {
        private String imageUrl;

        public FetchImageTask(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        @Override
        protected Bitmap doInBackground(String... arg0) {
            Bitmap mIcon_val = null;
            try {
                imageURL = new URL(imageUrl);
                mIcon_val = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return mIcon_val;
        }
    }

    private void removeAt(int position) {
        itemList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, itemList.size());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}

class ItemViewHolder extends RecyclerView.ViewHolder {
    ImageView image;
    TextView name;
//    TextView vendorName;
    TextView price;
    TextView category;
    Button deleteBtn;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);

        image = itemView.findViewById(R.id.itemImage);
        name = itemView.findViewById(R.id.itemName);
//        vendorName = itemView.findViewById(R.id.itemVendorName);
        category = itemView.findViewById(R.id.itemCategory);
        price = itemView.findViewById(R.id.itemPrice);
        deleteBtn = itemView.findViewById(R.id.deleteItem);
    }
}