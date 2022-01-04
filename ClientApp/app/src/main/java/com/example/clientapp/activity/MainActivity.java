package com.example.clientapp.activity;
import static androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

import com.example.clientapp.R;
import com.example.clientapp.chat.MainChatActivity;
import com.example.clientapp.fragment.CartFragment;
import com.example.clientapp.fragment.HistoryFragment;
import com.example.clientapp.fragment.ItemListFragment;
import com.example.clientapp.fragment.HomeFragment;
import com.example.clientapp.fragment.ProfileFragment;
import com.example.clientapp.helper.broadcast.NotificationService;
import com.example.clientapp.helper.viewModel.CartViewModel;
import com.example.clientapp.helper.viewModel.ItemViewModel;
import com.example.clientapp.helper.broadcast.NotificationReceiver;
import com.example.clientapp.model.Cart;
import com.example.clientapp.model.Client;
import com.example.clientapp.model.Item;
import com.example.clientapp.model.Order;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private final String ORDER_COLLECTION = "orders";
    private final String TAG = MainActivity.class.getSimpleName();
    private FragmentTransaction transaction;

    // the item model list
    private ItemViewModel viewModel;
    private CartViewModel cartViewModel;

    private FirebaseFirestore fireStore;
    private CollectionReference orderCollection;
    private BottomNavigationView bottomNavigationView;

    private int orderSize;
    private Client client;
    private String selectedCategory;
    private List<Cart> cartList;


    public static final String CANCEL_NOTIFICATION = "Your order is cancelled!\nCheck it out!";
    public static final String ORDER_NOTIFICATION = "Successfully checked out!\nWaiting for processing!";
    public static final String PROCESS_NOTIFICATION = "Your order is successfully processed!\nCheck it out!";

    private NotificationReceiver notificationReceiver;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        viewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        viewModel.getSelectedItem().observe(this, item -> {
            // Perform an action with the latest item data
        });
        selectedCategory = "";

        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);


        registerService();
        bottomNavigationView = findViewById(R.id.bottom_navigation_container);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // attaching bottom sheet behaviour - hide / show on scroll
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());


        // init home fragment
        loadFragment(new HomeFragment());

        Intent intent = getIntent();
        if (intent != null) {
            client = intent.getParcelableExtra("client");
            loadOrderList();
            Log.d(TAG, "onCreate: client=" + client);
        }
    }

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
                        // Put item in bundle to send to ItemDetails fragment
                        // send the string to ItemList Fragment
                        try {
                            Bundle bundle = new Bundle();
                            bundle.putString("category", selectedCategory);
                            fragment.setArguments(bundle);
                            loadFragment(fragment);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return true;
                    case R.id.cartNav:
                        fragment = new CartFragment();
                        loadFragment(fragment);
                        return true;
                    case R.id.historyNav:
                        fragment = new HistoryFragment(client.getId());
                        loadFragment(fragment);
                        return true;
                }
                return false;
            };


    public void loadFragment(Fragment fragment) {
        try {
            FragmentManager fm = getSupportFragmentManager();
            Log.i(TAG, "Fragment stack size : " + fm.getBackStackEntryCount());

            for(int entry = 0; entry<fm.getBackStackEntryCount(); entry++){
                Log.i(TAG, "Found fragment: " + fm.getBackStackEntryAt(entry).getId());
                fm.popBackStackImmediate( null, POP_BACK_STACK_INCLUSIVE);
                Log.i(TAG, "Pop successfully : " + fm.getBackStackEntryAt(entry).getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // load fragment
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
//        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void loadFragmentWithBackStack(Fragment fragment){
        try {
            FragmentManager fm = getSupportFragmentManager();
            Log.i(TAG, "Fragment stack size : " + fm.getBackStackEntryCount());

            for(int entry = 0; entry<fm.getBackStackEntryCount(); entry++){
                Log.i(TAG, "Found fragment: " + fm.getBackStackEntryAt(entry).getId());
                fm.popBackStackImmediate( null, POP_BACK_STACK_INCLUSIVE);
                Log.i(TAG, "Pop successfully : " + fm.getBackStackEntryAt(entry).getId());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fm = getSupportFragmentManager();

        Log.i(TAG, "Fragment stack size : " + fm.getBackStackEntryCount());

        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
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
            super.onBackPressed();
//        }
    }

    public void onProfileBtnClick(View view) {
        Fragment fragment = new ProfileFragment();
        if (client != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("client", client);
            Log.d(TAG, "onProfileBtnClick: client=" + client);
            fragment.setArguments(bundle);
        }
        loadFragmentWithBackStack(fragment);
    }


    public void onChatBtnClick(View view){

        Intent intent = new Intent(this, MainChatActivity.class);
        intent.putExtra("client", client);
        startActivity(intent);
    }

    // order btn
    public void onOrderBtnClick(View view) {
        List<Item> cartList = viewModel.getListItem();

        // sort the list
        Collections.sort(cartList);

        // create multimap and store the value of list
        Map<Integer, List<Item> >
                multimap = cartList
                .stream()
                .collect(
                        Collectors
                                .groupingBy(
                                        Item::getVendorID,
                                        Collectors
                                                .toList()));


        // init the necessary list
        List<Item> itemOrder = new ArrayList<>();
        List<Integer> quantity = new ArrayList<>();
        Order order;
        int occurrences;

        double price = 0;
        // iterate through the list of the cart
        for (List<Item> list: multimap.values()){

            // iterate through the item in the list
            for (int i = 0; i < list.size(); i++ ){
                // take the frequency
                occurrences = Collections.frequency(list, list.get(i));
                // add to the list
                itemOrder.add(list.get(i));
                quantity.add(occurrences);
                price += list.get(i).getPrice() * occurrences;

                // skip to occurrence
                i += occurrences - 1;

            }
            orderSize++;
            order = new Order(orderSize, filterDateOrder(LocalDateTime.now().toString()), false, itemOrder, quantity , list.get(0).getVendorID(), client.getId(), price);

            Log.d(TAG, "order: orderDATE: " + LocalDateTime.now().toString());
            Order finalOrder = order;
            orderCollection.document(order.getId() + "")
                .set(order.toMap())
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "Successfully added Order to FireStore: " + finalOrder.toString());

                    // reset the cart
                    viewModel.resetMutableItemList();

                    //TODO: add notification here (use broadcast)
                    Intent intent = new Intent(ORDER_NOTIFICATION);
                    sendBroadcast(intent);

                })
                .addOnFailureListener(e -> Log.d(TAG, "Fail to add order to FireStore: " + finalOrder.toString()));


            // re-init the variables
            itemOrder = new ArrayList<>();
            quantity = new ArrayList<>();
            price = 0;
        }

    }

    private void registerService(){
        notificationReceiver = new NotificationReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(ORDER_NOTIFICATION);
        intentFilter.addAction(CANCEL_NOTIFICATION);
        intentFilter.addAction(PROCESS_NOTIFICATION);
        this.registerReceiver(notificationReceiver, intentFilter);
    }

    // filter the string date
    public String filterDateOrder (String rawString){

        // initialize the new string
        char [] filterString = new char[rawString.length()];


        // iterate through each character in the string
        for (int i = 0 ; i < rawString.length(); i++){

            // check if the character is T then replace it with T
            if (rawString.charAt(i) == 'T'){
                filterString[i] = ' ';
                continue;
            }

            // check if the character is :
            if(rawString.charAt(i) == '.'){
                return String.valueOf(filterString).trim();
            }

            filterString[i] = rawString.charAt(i);
        }

        return null;
    }


    private void loadOrderList() {
        // init fireStore db
        fireStore = FirebaseFirestore.getInstance();

        // setting to keep the fireStore fetching data without the internet
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        fireStore.setFirestoreSettings(settings);

        orderCollection = fireStore.collection(ORDER_COLLECTION);


        // load items
        orderCollection.addSnapshotListener((value, error) -> {

            try {
                orderSize = value.size();

            } catch (Exception e){
                orderSize = 1;
            }

        });

        cartList = new ArrayList<>(); //Reset value of cart List
        List<Order> orderList = new ArrayList<>();


        // load orders
        orderCollection.whereEqualTo("clientID" , client.getId())
                .addSnapshotListener((value, error) -> {


                    Log.d(TAG, "loadCart: value.getDocumentChanges size: " + value.getDocumentChanges().size());

                    Order orderModified = null;
                    for (DocumentChange documentChange: value.getDocumentChanges()){
                            if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                                orderModified =  documentChange.getDocument().toObject(Order.class);
                                Log.d(TAG, "order changed: " + orderModified.toString());
                                break;
                            }
                    }

                    // clear to list
                    cartList = new ArrayList<>();
                    cartViewModel.resetMutableCartList();

                    // init necessary variable for doing logic
                    int countCart = 0;
                    Cart currentCart;
                    int countProcessed;
                    int countCancel;
                    boolean isModified;

                    assert value != null;
                    Log.d(TAG, "loadCart: value size: " + value.getDocuments().size());

                    //scan the value from db
                    for (int i = value.size() - 1 ; i >= 0; i--){
                        orderList.add(value.getDocuments().get(i).toObject(Order.class));
                    }

                    // sort reverse way
                    orderList.sort((o1, o2) -> {
                        // reverse sort
                        if (o1.getId() < o2.getId()){
                            return 1; // normal will return -1
                        } else if (o1.getId() > o2.getId()){
                            return -1; // reverse
                        }
                        return 0;
                    });

                    Log.d(TAG, "loadCart: orderList.size(): " + orderList.size());

                    for (int i = 0 ; i < orderList.size(); i++){

                        Log.d(TAG, "loadCart: order: " + orderList.get(i).toString());
                        Log.d(TAG, "loadCart: iTh: " + i);


                        String time = filterDate(orderList.get(i).getDate());
                        Log.d(TAG, "loadCart: time: " + time);

                        // take the list of order in the same time
                        List<Order> orderByDate = orderList.stream().filter(order -> {
//                            Log.d(TAG, "filter: " + order.getDate().trim().equals(time));
//                            Log.d(TAG, "filter: isProcessed: " + order.getIsProcessed());
//                            Log.d(TAG, "filter: object " + order.toString() + " orderList size: " + orderList.size());
                            return order.getDate().trim().equals(time) ;
                        }).collect(Collectors.toList());

                        Log.d(TAG, "loadCart: orderByDate size: " + orderByDate.size());


                        // create cart object
                        currentCart = new Cart(countCart, time ,orderByDate );


                        // init count processed
                        countProcessed = 0;
                        countCancel = 0;
                        isModified = false;
                        for (Order order: orderByDate){

                            // validate if the orderModified is null
                            try {

                                if (orderModified.getId() == order.getId()){
                                    isModified = true;
                                    Log.d(TAG, "loadCart: orderModified : " + orderModified.toString());
                                }

                            }catch (Exception e){
                                e.printStackTrace();
                                isModified = false;
                            }

                            // validate if the order is cancel
                            if (order.getIsCancelled()){
                                countCancel++;
                                continue;
                            }

//                            Log.d(TAG, "filter-condition: isProcess: " + order.getIsProcessed());
                            // check if processed yet ?
                            if (order.getIsProcessed()){
                                countProcessed ++;
                            }
                        }
//                        Log.d(TAG, "loadCart: orderByDate size: " +orderByDate.size());
//                        Log.d(TAG, "loadCart: countProcessed : " +countProcessed);


                        // validate if the order is already processed.
                        if ((countProcessed + countCancel) == orderByDate.size()){
                            currentCart.setIsFinished(true);
                        }

                        countCart++;

                        // validate if the order is changed
                        if (isModified){

                            Log.d(TAG, "isModified: currentCart : " + currentCart.toString());
                            Log.d(TAG, "isModified: orderModified.getIsProcessed() : " + orderModified.getIsProcessed());
                            Log.d(TAG, "isModified: orderModified.getIsCancelled() : " + orderModified.getIsCancelled());

                            // add notification if the order is cancel
                            //        id = in.readInt();
                            //        date = in.readString();
                            //        orderList = in.createTypedArrayList(Order.CREATOR);
                            //        price = in.readDouble();
                            //        isFinished = in.readByte() != 0;
//                            Intent intent = new Intent(orderModified.getIsProcessed()? PROCESS_NOTIFICATION : orderModified.getIsCancelled()? CANCEL_NOTIFICATION : null);
//
//                            intent.putExtra("client", client);
//                            intent.putExtra("cart", currentCart);
//                            sendBroadcast(intent);

                            /** Using service*/
                            if (orderModified.getIsProcessed() || orderModified.getIsCancelled()){
                                Intent intent = new Intent(this, NotificationService.class);
                                intent.putExtra("message",orderModified.getIsProcessed()? PROCESS_NOTIFICATION : CANCEL_NOTIFICATION  );
                                intent.putExtra("client", client);
                                intent.putExtra("cart", currentCart);
                                intent.setPackage(this.getPackageName());
                                startService( intent) ;
                            }


                            isModified = false;
                        }



                        // add cart to cartList
                        cartList.add(currentCart);


                        i += orderByDate.size() - 1;


                    }


                    // reset the list
                    orderList.clear();
//                    Log.d(TAG, "loadCart: cardList size: " +cartList.size());


                    boolean successAddCart = cartViewModel.addListCarts(cartList);
                    Log.d(TAG, "loadCart: add successfully ? " +successAddCart);
                    Log.d(TAG, "loadCart: cartViewModel size:  " +cartViewModel.getListCart().size());

                });
    }

    // filter the string date
    public String filterDate (String rawString){

        // initialize the new string
        char [] filterString = new char[rawString.length()];

        int countColon = 0;

        // iterate through each character in the string
        for (int i = 0 ; i < rawString.length(); i++){


            // check if the character is :
            if(rawString.charAt(i) == ':'){
                countColon++;
                if (countColon == 2){
                    filterString[i] = rawString.charAt(i);
                    filterString[i+1] = rawString.charAt(i+1);
                    filterString[i+2] = rawString.charAt(i+2);
                    return String.valueOf(filterString).trim();
                }

            }

            filterString[i] = rawString.charAt(i);
        }

        return null;
    }


    public String getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(String selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public BottomNavigationView getBottomNavigationView() {
        return bottomNavigationView;
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