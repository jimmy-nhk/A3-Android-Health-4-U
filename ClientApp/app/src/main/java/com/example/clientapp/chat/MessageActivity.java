package com.example.clientapp.chat;

import com.example.clientapp.R;
import com.example.clientapp.chat.adapter.MessageAdapter;
import com.example.clientapp.chat.model.MessageObject;
import com.example.clientapp.model.Client;
import com.example.clientapp.model.Order;
import com.example.clientapp.model.Vendor;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;

    Client currentClient;
    Vendor currentVendor;

    ImageButton btn_send;
    EditText text_send;

    Intent intent;

    private int messageSize ;
    private FirebaseFirestore fireStore;
    private CollectionReference messageCollection;
    private final String MESSAGE_COLLECTION = "messages";
    private final String TAG = "MessageActivity";

    MessageAdapter messageAdapter;
    List<MessageObject> messageObjectList;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.usernameMainChat);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        recyclerView = findViewById(R.id.recycler_view_message);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = text_send.getText().toString();
                if (!msg.equals("")){
                    sendMessage(currentClient.getUsername(), currentVendor.getUsername(), msg);
                }else {
                    Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }

                text_send.setText("");
            }
        });
        fireStore = FirebaseFirestore.getInstance();
        messageCollection = fireStore.collection(MESSAGE_COLLECTION);



        intent = getIntent();
        currentVendor = intent.getParcelableExtra("vendor");
        currentClient = intent.getParcelableExtra("client");

        username.setText("username: " + currentVendor.getUsername());
        profile_image.setImageResource(R.mipmap.ic_launcher);
        readMessages();

        //FIXME: fix image
//        Glide.with(mContext).load(vendor.getImage()).into(holder.profile_image);

    }

    private void sendMessage(String sender, String receiver, String message){

        messageSize++;
        MessageObject messageObject = new MessageObject( messageSize,sender, receiver, message);



        messageCollection.document(messageObject.getId() + "")
                .set(messageObject.toMap())
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "Successfully added Message to FireStore: " + messageObject.toString());


                })
                .addOnFailureListener(e -> Log.d(TAG, "Fail to add Message to FireStore: " + messageObject.toString()));

    }

    // read message
    private void readMessages(){


        // load items
        messageCollection.addSnapshotListener((value, error) -> {
            messageObjectList = new ArrayList<>();

            messageSize = value.size();

            MessageObject messageObject;
            //scan the value from db
            for (int i = value.size() - 1 ; i >= 0; i--){
                messageObject = value.getDocuments().get(i).toObject(MessageObject.class);


                //validate the condition to add the message to the list
                if (messageObject.getReceiver().equals(currentClient.getUsername()) && messageObject.getSender().equals(currentVendor.getUsername()) ||
                    messageObject.getReceiver().equals(currentVendor.getUsername()) && messageObject.getSender().equals(currentClient.getUsername())){

                    // add message
                    messageObjectList.add(messageObject);
                }


            }
            // set reverse the collection
            Collections.reverse(messageObjectList);
            messageAdapter = new MessageAdapter(MessageActivity.this, messageObjectList, currentClient , currentVendor);
            recyclerView.setAdapter(messageAdapter);

        });
    }
}