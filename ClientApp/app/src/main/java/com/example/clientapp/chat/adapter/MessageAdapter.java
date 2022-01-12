package com.example.clientapp.chat.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.clientapp.R;
import com.example.clientapp.chat.MessageActivity;
import com.example.clientapp.chat.model.MessageObject;
import com.example.clientapp.model.Client;
import com.example.clientapp.model.Vendor;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Context mContext;
    private List<MessageObject> messageObjectList;
    private String imageUrl;

    private final static String TAG = "MessageAdapter";
    private Client client;
    private Vendor vendor;
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;


    public MessageAdapter(Context mContext, List<MessageObject> messageObjectList,Client currentClient, Vendor vendor){
        this.mContext = mContext;
        this.messageObjectList = messageObjectList;
        this.client = currentClient;
        this.imageUrl = imageUrl;
        this.vendor = vendor;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        // check the position
        if (viewType == MSG_TYPE_RIGHT){
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);

        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);

        }
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        MessageObject messageObject = messageObjectList.get(position);

        // set views
        holder.show_message.setText(messageObject.getMessage());

        setStoreImage(holder, vendor.getImage());
//        if (imageUrl.equals("default")){
//            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
//        } else {
//            Glide.with(mContext).load(imageUrl).into(holder.profile_image);
//        }

        //TODO: set up image here
//        holder.txt_seen.setVisibility(View.GONE);

        // check for the last message
        if (position == messageObjectList.size() - 1){

            // get is seen
            if (messageObject.getIsSeen()){
                holder.txt_seen.setText(("Seen"));
            } else {
                holder.txt_seen.setText(("Delivered"));
            }
        } else {
            holder.txt_seen.setVisibility(View.GONE);
        }
    }

    private void setStoreImage(ViewHolder holder, String imageUrl) {
        try {
            if (imageUrl!=null && imageUrl.length() > 0) {
//                Log.d("setStoreImage", imageUrl);
                StorageReference mImageRef =
                        FirebaseStorage.getInstance().getReference(imageUrl);

                final long ONE_MEGABYTE = 1024 * 1024 *5;
                // Handle any errors
                mImageRef.getBytes(ONE_MEGABYTE)
                        .addOnSuccessListener(bytes -> {
                            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            DisplayMetrics dm = new DisplayMetrics();
                            ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);

                            holder.profile_image.setMinimumHeight(dm.heightPixels);
                            holder.profile_image.setMinimumWidth(dm.widthPixels);
                            holder.profile_image.setImageBitmap(bm);
                        }).addOnFailureListener(Throwable::printStackTrace);
            }
        } catch (Exception e) {
//            .setImageResource(R.drawable.bun); //Set something else
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return messageObjectList.size();
    }

    @Override
    public int getItemViewType(int position) {

        // get item view type
        if (messageObjectList.get(position).getSender().equals(client.getUserName())){
            return MSG_TYPE_RIGHT;
        } else
            return MSG_TYPE_LEFT;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // attributes
        private TextView show_message;
        private ImageView profile_image;
        private TextView txt_seen;

        // constructor
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);

        }

    }
}

