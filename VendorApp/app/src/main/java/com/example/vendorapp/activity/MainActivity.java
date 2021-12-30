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
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.vendorapp.R;
import com.example.vendorapp.fragment.ItemListFragment;
import com.example.vendorapp.fragment.HomeFragment;
import com.example.vendorapp.fragment.OrderListFragment;
import com.example.vendorapp.fragment.ProfileFragment;
import com.example.vendorapp.model.Vendor;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "MainActivity";
    private Vendor vendor;
    private FragmentTransaction transaction;

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
                    case R.id.orderNav:
                        fragment = new OrderListFragment(vendor.getId());
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
        } catch (Exception e){

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
        } catch (Exception e){

        }
        FragmentManager fm = getSupportFragmentManager();

        Log.i(TAG, "Fragment stack size : " + fm.getBackStackEntryCount());

        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // add item on click
    public void addItemOnClick(View view) {

        try {
            Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
            startActivityForResult(intent, R.integer.intentMainAdditem);
        } catch (Exception e){
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