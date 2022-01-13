package com.example.vendorapp.helper.adapter;

import android.app.Activity;
import android.content.Context;
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

import com.example.vendorapp.R;
import com.example.vendorapp.model.MessageObject;
import com.example.vendorapp.model.Client;
import com.example.vendorapp.model.Vendor;
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


    // constructor
    public MessageAdapter(Context mContext, List<MessageObject> messageObjectList,Client currentClient, Vendor vendor){
        this.mContext = mContext;
        this.messageObjectList = messageObjectList;
        this.client = currentClient;
        this.vendor = vendor;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        // type right and left
        if (viewType == MSG_TYPE_RIGHT){
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);

        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);

        }
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // message object
        MessageObject messageObject = messageObjectList.get(position);

        // set message
        holder.show_message.setText(messageObject.getMessage().trim());
        setClientImage(holder, client.getImage());

        // check for the last message
        if (position == messageObjectList.size() - 1){

            // set text
            if (messageObject.getIsSeen()){
                holder.txt_seen.setText("Seen");
            } else {
                holder.txt_seen.setText("Delivered");
            }
        } else {
            holder.txt_seen.setVisibility(View.GONE);
        }
    }

    private void setClientImage(ViewHolder holder, String imageUrl) {
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
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return messageObjectList.size();
    }

    @Override
    public int getItemViewType(int position) {

        // set item left or right
        if (messageObjectList.get(position).getSender().equals(vendor.getUserName())){
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

