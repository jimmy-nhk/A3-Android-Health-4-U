package com.example.vendorapp.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vendorapp.R;
import com.example.vendorapp.chat.MessageActivity;

import com.example.vendorapp.model.Client;
import com.example.vendorapp.model.Vendor;


import java.util.List;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ViewHolder> {

    private Context mContext;
    private List<Client> mClients;
    private final static String TAG = "UserAdapter";
    private Vendor vendor;
    private boolean isChat;

    public ClientAdapter(Context mContext, List<Client> mClients, Vendor vendor, boolean isChat) {
        this.mContext = mContext;
        this.mClients = mClients;
        this.vendor = vendor;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Client client = mClients.get(position);
        holder.username.setText("client name: " + client.getUserName());
        holder.profile_image.setImageResource(R.mipmap.ic_launcher);
//        Glide.with(mContext).load(vendor.getImage()).into(holder.profile_image);

        // check if chat is callable
        if (isChat){
            if (client.getStatus().equals("online")){
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
        return mClients.size();
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
