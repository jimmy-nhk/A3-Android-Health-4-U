package com.example.clientapp.chat;

import com.example.clientapp.R;
import com.example.clientapp.activity.MainActivity;
import com.example.clientapp.chat.adapter.MessageAdapter;
import com.example.clientapp.chat.model.MessageObject;
import com.example.clientapp.model.Client;
import com.example.clientapp.model.Vendor;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.Nullable;
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
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {


    // components
    CircleImageView profile_image;
    TextView username;

    Client currentClient;
    Vendor currentVendor;

    ImageButton btn_send;
    EditText text_send;



    Intent intent;

    // message size
    private int messageSize ;
    // fireStore init
    private FirebaseFirestore fireStore;
    private CollectionReference messageCollection;

    private final String MESSAGE_COLLECTION = "messages";

    private final String TAG = "MessageActivity";

    // adapter
    MessageAdapter messageAdapter;
    List<MessageObject> messageObjectList;

    // recycler view
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // tool bar settings
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
//                startActivity(new Intent(MessageActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });


        // attach view
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
                // set notify to true

                // get the message
                String msg = text_send.getText().toString();
                if (!msg.equals("")){
                    sendMessage(currentClient.getUserName(), currentVendor.getUserName(), msg);
                }else {
                    Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }

                text_send.setText("");
            }
        });

        // init service
        fireStore = FirebaseFirestore.getInstance();
        messageCollection = fireStore.collection(MESSAGE_COLLECTION);

        // get the intent data from the previous activity
        intent = getIntent();
        currentVendor = intent.getParcelableExtra("vendor");
        currentClient = intent.getParcelableExtra("client");

        username.setText(currentVendor.getUserName());
        profile_image.setImageResource(R.mipmap.ic_launcher);
        readMessages();

//        seenMessage();
        //FIXME: fix image
//        Glide.with(getApplicationContext()).load(vendor.getImage()).into(holder.profile_image);

    }

//    // listen to change
//    private ListenerRegistration listenerRegistration;
//
//    private void seenMessage(){
//
//        listenerRegistration = messageCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//
//                MessageObject messageObject;
//                assert value != null;
//
//
//                for (DocumentChange dc : value.getDocumentChanges()){
//
//                    // check if added
//                    if (dc.getType() == DocumentChange.Type.ADDED){
//                        messageObject = dc.getDocument().toObject(MessageObject.class);
//
//                        // check the message is already read
//                        if (messageObject.getReceiver().equals(currentVendor.getUserName())
//                                && messageObject.getSender().equals(currentClient.getUserName())){
//
//
//                            // set the isSeen to true
//                            HashMap<String , Object> hashMap = new HashMap<>();
//                            hashMap.put("isSeen", true);
//
//                            messageObject.setIsSeen(true);
//                            // update db
//                            Log.d(TAG, "updated isSeen here");
//
//                            dc.getDocument().getReference().update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void unused) {
//                                    Log.d(TAG, "successfully update seen");
//
//                                }
//                            });
////                        messageCollection.document(messageObject.getId() + "")
////                                .set(messageObject.toMap())
////                                .addOnSuccessListener(new OnSuccessListener<Void>() {
////                                    @Override
////                                    public void onSuccess(Void unused) {
////                                        Log.d(TAG, "successfully update seen");
////                                    }
////                                });
//                    }
//
//                    }
//                }
//            }
//        });
//    }

    // send message to the other client
    private void sendMessage(String sender, String receiver, String message){

        messageSize++;
        // init message
        MessageObject messageObject = new MessageObject( messageSize,sender, receiver, message , false, true);

        // add message to db
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
        messageCollection.orderBy("id").addSnapshotListener((value, error) -> {
            messageObjectList = new ArrayList<>();

            // get size
            messageSize = value.size();

            // init obj
            MessageObject messageObject;


            //scan the value from db
            for (DocumentSnapshot ds: value
            ) {
                messageObject = ds.toObject(MessageObject.class);
                Log.d(TAG, "messageObj:  " +messageObject.toString());


                //validate the condition to add the message to the list
                if (messageObject.getReceiver().equals(currentClient.getUserName()) && messageObject.getSender().equals(currentVendor.getUserName()) ||
                        messageObject.getReceiver().equals(currentVendor.getUserName()) && messageObject.getSender().equals(currentClient.getUserName())) {

                    messageObjectList.add(messageObject);

                }
            }
            // set reverse the collection
//            Collections.reverse(messageObjectList);
            Log.d(TAG, "messageObjectList size:  " +messageObjectList.size());

            // set adapter
            messageAdapter = new MessageAdapter(MessageActivity.this, messageObjectList, currentClient , currentVendor);
            recyclerView.setAdapter(messageAdapter);

        });
    }

    @Override
    protected void onPause() {

        finish();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        finish();

        super.onBackPressed();
    }
}