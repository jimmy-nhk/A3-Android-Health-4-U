package com.example.vendorapp.activity;

//TODO: List of works needing doing
// Search (query) item -> finished
// HomePage (2 app) -> reminder drinking water
// Billing Page -> Download Bill
// Cart Detail Page -> see the list of bought items
// Item details in Vendor app
// UI


import static androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.vendorapp.R;
import com.example.vendorapp.chat.MainChatActivity;
import com.example.vendorapp.chat.model.MessageObject;
import com.example.vendorapp.fragment.ItemListFragment;
import com.example.vendorapp.fragment.HomeFragment;
import com.example.vendorapp.fragment.OrderListFragment;
import com.example.vendorapp.fragment.ProfileFragment;
import com.example.vendorapp.helper.NotificationReceiver;
import com.example.vendorapp.helper.NotificationService;
import com.example.vendorapp.helper.OrderViewModel;
import com.example.vendorapp.model.Client;
import com.example.vendorapp.model.Order;
import com.example.vendorapp.model.Vendor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NotificationReceiver notificationReceiver;
    private IntentFilter intentFilter;
    private static final String TAG = "MainActivity";
    public static final String ORDER_COMING = "New order needs processing!";
    public static final String NEW_MESSAGE = "New message is coming";
    private Vendor vendor;
    private FragmentTransaction transaction;
    private static final String ORDER_COLLECTION = "orders";
    private static final String VENDOR_COLLECTION = "vendors";
    private List<Order> orderList;

    // init firestore
    private FirebaseFirestore fireStore;
    private CollectionReference orderCollection;
    private CollectionReference vendorCollection;

    private CollectionReference messageCollection;
    private final String MESSAGE_COLLECTION = "messages";
    private CollectionReference clientCollection;
    private final String CLIENT_COLLECTION = "clients";

    private OrderViewModel orderViewModel;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        bottomNavigationView = findViewById(R.id.bottom_navigation_container);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // attaching bottom sheet behaviour - hide / show on scroll
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());

        // init home fragment
        loadFragment(new HomeFragment());

        Intent intent = getIntent();
        if (intent != null) {
            vendor = (Vendor) intent.getParcelableExtra("vendor");
        }
        Log.d(TAG, "vendor passing from Login: " + vendor.toString());

        initService();
        listenMessage();
    }

    private void listenMessage() {

        messageCollection = fireStore.collection(MESSAGE_COLLECTION);
        clientCollection = fireStore.collection(CLIENT_COLLECTION);

        // listen for messages
        messageCollection
                .orderBy("id")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        // check not null

//                        for (DocumentSnapshot ds: value.getDocuments()
//                             ) {
//                            MessageObject messageObject = ds.toObject(MessageObject.class);
//                            Log.d(TAG, "message newest: " + messageObject.toString());
//
//                        }

                        try {
                            DocumentChange dc = value.getDocumentChanges().get(value.getDocumentChanges().size() - 1);

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        try {

                            Log.d(TAG, "message size: " + value.getDocuments().size());
                            int size = value.getDocuments().size() - 1;

                            DocumentSnapshot ds = value.getDocuments().get(size);
                            DocumentChange dc = value.getDocumentChanges().get(value.getDocumentChanges().size() - 1);

                            if (dc.getType() == DocumentChange.Type.ADDED){

                            } else {
                                return;
                            }

                            if (ds != null) {
//                                MessageObject messageObject = ds.toObject(MessageObject.class);

                                MessageObject messageObject = dc.getDocument().toObject(MessageObject.class);
                                Log.d(TAG, "dc type " + messageObject.toString());

                                Log.d(TAG, "message newest: " + messageObject.toString());

                                try {
                                    if (messageObject.isNewestMessage()) {

                                        // get the vendor object
                                        clientCollection.whereEqualTo("username", messageObject.getSender() + "")
                                                .get()
                                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        try {
                                                            Client client = queryDocumentSnapshots.getDocuments().get(0).toObject(Client.class);

                                                            Log.d(TAG, "client who just sent message: " + client.toString());
                                                            // TODO: send notification here
                                                            Log.d(TAG, "New noti");

                                                            Intent intent = new Intent(NEW_MESSAGE);
                                                            intent.putExtra("message", messageObject.getMessage());
                                                            intent.putExtra("client", client);
                                                            intent.putExtra("vendor", vendor);
                                                            sendBroadcast(intent);
                                                            return;
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });


                                    }

                                    // validate the error
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                });
    }
    @Override
    protected void onResume() {
        toggleStatus("online");
        super.onResume();
    }

    @Override
    public void onBackPressed() {

        try {
            FragmentManager fm = getSupportFragmentManager();

            Log.i(TAG, "Fragment stack size : " + fm.getBackStackEntryCount());


        } catch (Exception e){

        }
//        // validate the back button in the device
//        if (getSupportFragmentManager().getBackStackEntryCount() == 1){
//            finish();
//        } else {
        toggleStatus("offline");
        super.onBackPressed();
//        }
    }
        // on chat btn
    public void onChatBtnClick(View view){

        // pass to new intent
        Intent intent = new Intent(MainActivity.this, MainChatActivity.class);
        intent.putExtra("vendor", vendor);
        startActivity(intent);
    }


    // bottom navigation
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        Fragment fragment;

        switch (item.getItemId()) {
            case R.id.homePageNav:
                fragment = new HomeFragment();
                loadFragment(fragment);
                return true;
            case R.id.itemsNav:
                fragment = new ItemListFragment();
                loadFragment(fragment);
                return true;
            case R.id.orderNav:
                Log.d(TAG, "vendor: " + vendor.toString());
                fragment = new OrderListFragment(vendor.getId());
                loadFragment(fragment);
                return true;

        }
        return false;
    };

    // load fragment
    public void loadFragment(Fragment fragment) {
        try {
            FragmentManager fm = getSupportFragmentManager();

            Log.i(TAG, "Fragment stack size : " + fm.getBackStackEntryCount());

            for (int entry = 0; entry < fm.getBackStackEntryCount(); entry++) {
                Log.i(TAG, "Found fragment: " + fm.getBackStackEntryAt(entry).getId());
                fm.popBackStackImmediate(null, POP_BACK_STACK_INCLUSIVE);
                Log.i(TAG, "Pop successfully : " + fm.getBackStackEntryAt(entry).getId());

            }
        } catch (Exception e) {

        }

        // load fragment
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
//        transaction.addToBackStack(null);
        transaction.commit();


    }

    // load fragment with backstack
    public void loadFragmentWithBackStack(Fragment fragment) {
        try {
            FragmentManager fm = getSupportFragmentManager();

            Log.i(TAG, "Fragment stack size : " + fm.getBackStackEntryCount());

            for (int entry = 0; entry < fm.getBackStackEntryCount(); entry++) {
                Log.i(TAG, "Found fragment: " + fm.getBackStackEntryAt(entry).getId());
                fm.popBackStackImmediate(null, POP_BACK_STACK_INCLUSIVE);
                Log.i(TAG, "Pop successfully : " + fm.getBackStackEntryAt(entry).getId());

            }
        } catch (Exception e) {

        }
        FragmentManager fm = getSupportFragmentManager();

        Log.i(TAG, "Fragment stack size : " + fm.getBackStackEntryCount());

        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }



    private void registerService(){
        notificationReceiver = new NotificationReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(ORDER_COMING);
        intentFilter.addAction(NEW_MESSAGE);
        this.registerReceiver(notificationReceiver, intentFilter);
    }

    private Order orderPassed;

    public Order getOrderPassed() {
        return orderPassed;
    }

    public void setOrderPassed(Order orderPassed) {
        this.orderPassed = orderPassed;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    // init service
    public void initService() {
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        // init fireStore db
        fireStore = FirebaseFirestore.getInstance();
        orderCollection = fireStore.collection(ORDER_COLLECTION);
        vendorCollection = fireStore.collection(VENDOR_COLLECTION);
        Log.d(TAG, "initService: vendorId: " + vendor.getId());

        // query
        orderCollection.whereEqualTo("vendorID", vendor.getId())
                .addSnapshotListener((value, error) -> {

                    orderList = new ArrayList<>();
                    orderViewModel.resetMutableOrderList();
//                    Log.d(TAG, "orderCollectionLoadDb: listSize: " + value.size());

                    // validate 0 case
                    assert value != null;
                    if (value.getDocuments().size() == 0) {
                        return;
                    }

                    for (DocumentSnapshot ds: value.getDocuments()
                             ) {
                            Order messageObject = ds.toObject(Order.class);
                            Log.d(TAG, "order newest: " + messageObject.toString());

                        }


                    int valueSize = value.getDocuments().size();
                    Log.d(TAG, "orderList size: " + valueSize);

                    Order orderModified = null;
                    String currentTime = filterDateOrder(LocalDateTime.now().toString()).substring(0, filterDateOrder(LocalDateTime.now().toString()).length() - 3);
                    Log.d(TAG, "current time changed: " + currentTime);

                    // Check if modified
                    for (DocumentChange dc : value.getDocumentChanges()) {

                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            orderModified = dc.getDocument().toObject(Order.class);

                            //FIXME: Cannot receive noti here
                            Log.d(TAG, "order time: " + orderModified.getDate());

                            if (orderModified.isNewestOrder()) {

                                orderPassed = orderModified;
                                Intent intent = new Intent(ORDER_COMING);
                                intent.putExtra("message", ORDER_COMING + "");
                                intent.putExtra("order", orderModified);
                                intent.putExtra("vendor",vendor);
                                sendBroadcast(intent);


                                /**Service*/
//                                Log.d(TAG, "order changed: " + orderModified.toString());
//                                Intent intent = new Intent(this, NotificationService.class);
//                                intent.putExtra("message", ORDER_COMING + "");
//                                intent.putExtra("order", orderModified);
//                                intent.putExtra("vendor",vendor);
//                                intent.setPackage(this.getPackageName());
//                                startService(intent);
                                break;
                            }


                        }
                    }

                    //reverse way (newest show first)
                    for (int i = value.size() - 1; i >= 0; i--) {

                        Order order = value.getDocuments().get(i).toObject(Order.class);
//                        Log.d(TAG, "orderCollectionLoadDb: order from db: " + order.toString());
                        orderList.add(order);
                    }

                    orderList.sort((o1, o2) -> {
                        // reverse sort
                        if (o1.getId() < o2.getId()) {
                            return 1; // normal will return -1
                        } else if (o1.getId() > o2.getId()) {
                            return -1; // reverse
                        }
                        return 0;
                    });


                    boolean successAddOrder = orderViewModel.addListOrders(orderList);
                    Log.d(TAG, "loadCart: add successfully ? " + successAddOrder);
                    Log.d(TAG, "loadCart: cartViewModel size:  " + orderViewModel.getListOrder().size());


                });

        registerService();
    }

    // onStart
    @Override
    protected void onStart() {

        toggleStatus("online");
        Log.d(TAG, "onStart");
        super.onStart();
    }


    // toggle status
    private void toggleStatus(String status){

        vendorCollection.document(vendor.getId() + "")
                .update("status", status)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {
                        Log.d(TAG, "DocumentSnapshot successfully updated status! " + status);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "DocumentSnapshot fail updated status!");

                    }
                });
    }

    // filter the string date
    public String filterDateOrder(String rawString) {

        // initialize the new string
        char[] filterString = new char[rawString.length()];


        // iterate through each character in the string
        for (int i = 0; i < rawString.length(); i++) {

            // check if the character is T then replace it with T
            if (rawString.charAt(i) == 'T') {
                filterString[i] = ' ';
                continue;
            }

            // check if the character is :
            if (rawString.charAt(i) == '.') {
                Log.d(TAG, "time: " + String.valueOf(filterString).trim());
                return String.valueOf(filterString).trim();
            }

            filterString[i] = rawString.charAt(i);
        }

        return null;
    }

    // add item on click
    public void addItemOnClick(View view) {

        try {
            Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
            startActivityForResult(intent, R.integer.intentMainAdditem);
        } catch (Exception e) {
            Log.d(TAG, "Cannot change to Add Item Activity");
        }
    }

    public void onProfileBtnClick(View view) {
        Fragment fragment = new ProfileFragment();
        if (vendor != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("vendor", vendor);
            fragment.setArguments(bundle);
        }
        loadFragmentWithBackStack(fragment);
    }

    // In your activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    // sign out btn
    public void onSignOut(View view) {


        vendorCollection.document(vendor.getId() + "")
                .update("status", "offline")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {
                        Log.d(TAG, "DocumentSnapshot successfully updated offline status! " );
                        FirebaseAuth.getInstance().signOut();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "DocumentSnapshot fail updated status!");

                    }
                });
    }
}

class BottomNavigationBehavior extends CoordinatorLayout.Behavior<BottomNavigationView> {

    public BottomNavigationBehavior() {
        super();
    }

    public BottomNavigationBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, BottomNavigationView child, View dependency) {
        boolean dependsOn = dependency instanceof FrameLayout;
        return dependsOn;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, BottomNavigationView child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, BottomNavigationView child, View target, int dx, int dy, int[] consumed) {
        if (dy < 0) {
            showBottomNavigationView(child);
        } else if (dy > 0) {
            hideBottomNavigationView(child);
        }
    }

    private void hideBottomNavigationView(BottomNavigationView view) {
        view.animate().translationY(view.getHeight());
    }

    private void showBottomNavigationView(BottomNavigationView view) {
        view.animate().translationY(0);
    }
}