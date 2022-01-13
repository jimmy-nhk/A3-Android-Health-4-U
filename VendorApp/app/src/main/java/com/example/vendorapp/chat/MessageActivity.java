package com.example.vendorapp.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vendorapp.R;
import com.example.vendorapp.helper.adapter.MessageAdapter;
import com.example.vendorapp.model.MessageObject;
import com.example.vendorapp.model.Client;
import com.example.vendorapp.model.Vendor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    // attributes
    CircleImageView profile_image;
    TextView username;

    Client currentClient;
    Vendor currentVendor;

    ImageButton btn_send;
    EditText text_send;


    Intent intent;

    private int messageSize;
    private FirebaseFirestore fireStore;
    private CollectionReference messageCollection;
    private CollectionReference vendorCollection;

    private final String VENDOR_COLLECTION = "vendors";
    private final String MESSAGE_COLLECTION = "messages";
    private final String TAG = "MessageActivity";

    MessageAdapter messageAdapter;
    List<MessageObject> messageObjectList;

    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        getViews();

        readMessages();

//        seenMessage();

    }

    // get views
    private void getViews() {
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
                if (!msg.equals("")) {
                    sendMessage(currentVendor.getUserName(), currentClient.getUserName(), msg);
                } else {
//                    Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }

                text_send.setText("");
            }
        });

        // init service
        fireStore = FirebaseFirestore.getInstance();
        messageCollection = fireStore.collection(MESSAGE_COLLECTION);
        vendorCollection = fireStore.collection(VENDOR_COLLECTION);


        intent = getIntent();
        currentVendor = intent.getParcelableExtra("vendor");
        currentClient = intent.getParcelableExtra("client");

        username.setText(currentClient.getUserName());
        profile_image.setImageResource(R.mipmap.ic_launcher);
        //FIXME: fix image
//        Glide.with(getApplicationContext()).load(vendor.getImage()).into(holder.profile_image);
    }

    private void sendMessage(String sender, String receiver, String message) {

        messageSize++;
        // init message
        MessageObject messageObject = new MessageObject(messageSize, sender, receiver, message, false, true);

        // add message to db
        messageCollection.document(messageObject.getId() + "")
                .set(messageObject.toMap())
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "Successfully added Message to FireStore: " + messageObject.toString());


                })
                .addOnFailureListener(e -> Log.d(TAG, "Fail to add Message to FireStore: " + messageObject.toString()));

    }

    // read message
    private void readMessages() {


        // load items
        messageCollection.orderBy("id").addSnapshotListener((value, error) -> {
            messageObjectList = new ArrayList<>();

            messageSize = value.size();
            Log.d(TAG, "messageSize:  " + messageSize);


            MessageObject messageObject;
            //scan the value from db
            for (DocumentSnapshot ds : value
            ) {
                messageObject = ds.toObject(MessageObject.class);
                Log.d(TAG, "messageObj:  " + messageObject.toString());


                if (messageObject.getReceiver().equals(currentClient.getUserName()) && messageObject.getSender().equals(currentVendor.getUserName()) ||
                        messageObject.getReceiver().equals(currentVendor.getUserName()) && messageObject.getSender().equals(currentClient.getUserName())) {
                    messageObjectList.add(messageObject);
                }


            }
            // set reverse the collection
//            Collections.reverse(messageObjectList);
            Log.d(TAG, "messageObjectList size:  " + messageObjectList.size());

            messageAdapter = new MessageAdapter(MessageActivity.this, messageObjectList, currentClient, currentVendor);
            recyclerView.setAdapter(messageAdapter);

        });
    }

    // toggle
    private void toggleStatus(String status) {

        // update vendor
        vendorCollection.document(currentVendor.getId() + "")
                .update("status", status)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {
                        Log.d(TAG, "DocumentSnapshot successfully updated status!");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "DocumentSnapshot fail updated status!");

                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        toggleStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        toggleStatus("offline");
    }

}