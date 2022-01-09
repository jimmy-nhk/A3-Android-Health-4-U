package com.example.vendorapp.chat;

import static androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.vendorapp.R;
import com.example.vendorapp.activity.MainActivity;
import com.example.vendorapp.chat.fragments.ChatsFragment;
import com.example.vendorapp.chat.fragments.ClientsFragment;
import com.example.vendorapp.fragment.HomeFragment;
import com.example.vendorapp.fragment.ItemListFragment;
import com.example.vendorapp.fragment.OrderListFragment;
import com.example.vendorapp.model.Vendor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainChatActivity extends AppCompatActivity {

    // attributes
    CircleImageView profile_image;
    TextView username;

    private FirebaseFirestore fireStore;
    private CollectionReference vendorCollection;
    private final String VENDOR_COLLECTION = "vendors";
    private Vendor currentVendor;



    private VendorViewModel vendorViewModel;
    private final static String TAG= "MainChatActivity";

    private FragmentTransaction transaction;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        // get the current vendor
        Intent intent = getIntent();
        currentVendor = intent.getParcelableExtra("vendor");


        Log.d(TAG, "received vendor from MainChat: " + currentVendor.toString());
//         set current vendor to the view model
        vendorViewModel = new ViewModelProvider(this).get(VendorViewModel.class);
        vendorViewModel.setValue(currentVendor);

        // attach the component
        profile_image = findViewById(R.id.profile_image);
        username  = findViewById(R.id.usernameMainChat);
        username.setText("username: "+currentVendor.getUserName());
        profile_image.setImageResource(R.mipmap.ic_launcher);
//        //FIXME: if has profile please fill in
////                    Glide.with(getApplicationContext()).load(currentvendor.getImage()).into(profile_image);
//



        // init service
        fireStore = FirebaseFirestore.getInstance();
        vendorCollection = fireStore.collection(VENDOR_COLLECTION);


        // attach UI
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        bottomNavigationView = findViewById(R.id.bottom_navigation_container);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // attaching bottom sheet behaviour - hide / show on scroll
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());

        // init home fragment
        loadFragment(new ChatsFragment());

    }

    // bottom navigation
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        Fragment fragment;

        switch (item.getItemId()) {
            case R.id.chatNav:
                fragment = new ChatsFragment();
                loadFragment(fragment);
                return true;
            case R.id.clientNav:
                fragment = new ClientsFragment();
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

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        Intent i=new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
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