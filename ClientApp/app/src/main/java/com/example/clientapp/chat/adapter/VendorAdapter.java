package com.example.clientapp.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clientapp.R;
import com.example.clientapp.chat.MessageActivity;
import com.example.clientapp.model.Client;
import com.example.clientapp.model.Vendor;

import java.util.List;

public class VendorAdapter extends RecyclerView.Adapter<VendorAdapter.ViewHolder> {

    private Context mContext;
    private List<Vendor> mVendors;
    private final static String TAG = "UserAdapter";
    private Client client;
    private boolean isChat;

    public VendorAdapter(Context mContext, List<Vendor> vendors, Client currentClient, boolean isChat){
        this.mContext = mContext;
        this.mVendors = vendors;
        this.client = currentClient;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public VendorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new VendorAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Vendor vendor = mVendors.get(position);
        holder.username.setText("vendor name: " + vendor.getUserName());
        holder.profile_image.setImageResource(R.mipmap.ic_launcher);
//        Glide.with(mContext).load(vendor.getImage()).into(holder.profile_image);

        // check if chat is callable
        if (isChat){
            if (vendor.getStatus().equals("online")){
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            }else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }

            // if chat is not callable, set gone to status
        } else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("vendor" , vendor);
                intent.putExtra("client", client);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mVendors.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView username;
        ImageView profile_image;
        ImageView img_on;
        ImageView img_off;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
        }
    }
}
