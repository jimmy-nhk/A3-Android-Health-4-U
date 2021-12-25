package com.example.clientapp.activity;
import com.example.clientapp.R;
import com.example.clientapp.fragment.CartFragment;
import com.example.clientapp.fragment.HistoryFragment;
import com.example.clientapp.fragment.ItemListFragment;
import com.example.clientapp.fragment.HomeFragment;
import com.example.clientapp.fragment.ProfileFragment;
import com.example.clientapp.helper.ItemViewModel;
import com.example.clientapp.model.Client;
import com.example.clientapp.model.Item;
import com.example.clientapp.model.Order;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
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

public class MainActivity extends AppCompatActivity{
    private final String ORDER_COLLECTION = "orders";
    private final String TAG = MainActivity.class.getSimpleName();

    // the item model list
    private ItemViewModel viewModel;

    private FirebaseFirestore fireStore;
    private CollectionReference orderCollection;
    private BottomNavigationView bottomNavigationView;

    private int orderSize;
    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        loadOrderList();
        viewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        viewModel.getSelectedItem().observe(this, item -> {
            // Perform an action with the latest item data
        });

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
                        loadFragment(fragment);
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

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void onProfileBtnClick(View view) {
        Fragment fragment = new ProfileFragment();
        if (client != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("client", client);
            Log.d(TAG, "onProfileBtnClick: client=" + client);
            fragment.setArguments(bundle);
        }
        loadFragment(fragment);
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
                price = list.get(i).getPrice() * occurrences;

                // skip to occurrence
                i += occurrences - 1;

            }

            //TODO: add date checkout
            order = new Order(orderSize, filterDate(LocalDateTime.now().toString()), false, itemOrder, quantity , list.get(0).getVendorID(), client.getId(), price);

            Log.d(TAG, "order: orderDATE: " + LocalDateTime.now().toString());
            Order finalOrder = order;
            orderCollection.document(order.getId() + "")
                .set(order.toMap())
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "Successfully added Order to FireStore: " + finalOrder.toString());

                    // reset the cart
                    viewModel.resetMutableItemList();

                    //TODO: add notification here (use broadcast)
                })
                .addOnFailureListener(e -> Log.d(TAG, "Fail to add order to FireStore: " + finalOrder.toString()));


            // re-init the variables
            itemOrder = new ArrayList<>();
            quantity = new ArrayList<>();
            orderSize++;
        }

    }

    // filter the string date
    public String filterDate (String rawString){

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
        orderCollection = fireStore.collection(ORDER_COLLECTION);



        // load items
        orderCollection.addSnapshotListener((value, error) -> {

            try {
                orderSize = value.size();

            } catch (Exception e){
                orderSize = 1;
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