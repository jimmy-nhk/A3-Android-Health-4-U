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

import com.bumptech.glide.Glide;
import com.example.clientapp.R;
import com.example.clientapp.chat.MessageActivity;
import com.example.clientapp.chat.fragments.UsersFragment;
import com.example.clientapp.model.Client;
import com.example.clientapp.model.Vendor;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<Vendor> mVendors;
    private final static String TAG = "UserAdapter";
    private Client client;

    public UserAdapter(Context mContext, List<Vendor> vendors, Client currentClient){
        this.mContext = mContext;
        this.mVendors = vendors;
        this.client = currentClient;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Vendor vendor = mVendors.get(position);
        holder.username.setText("vendor name: " + vendor.getUsername());
        holder.profile_image.setImageResource(R.mipmap.ic_launcher);
//        Glide.with(mContext).load(vendor.getImage()).into(holder.profile_image);
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
        }
    }
}
