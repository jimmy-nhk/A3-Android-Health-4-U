package com.example.vendorapp.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.vendorapp.R;
import com.example.vendorapp.chat.fragments.ChatsFragment;
import com.example.vendorapp.chat.fragments.ClientsFragment;
import com.example.vendorapp.model.Vendor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainChatActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;

    private FirebaseFirestore fireStore;
    private CollectionReference vendorCollection;
    private final String VENDOR_COLLECTION = "vendors";
    private Vendor currentVendor;



    private VendorViewModel vendorViewModel;
    private final static String TAG= "MainChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        // get the current vendor
        Intent intent = getIntent();
        currentVendor = intent.getParcelableExtra("vendor");

        // set current vendor to the view model
        vendorViewModel = new ViewModelProvider(this).get(VendorViewModel.class);
        vendorViewModel.setValue(currentVendor);

        // attach the component
        profile_image = findViewById(R.id.profile_image);
        username  = findViewById(R.id.usernameMainChat);

        username.setText("username: "+currentVendor.getUserName());
        profile_image.setImageResource(R.mipmap.ic_launcher);
        //FIXME: if has profile please fill in
//                    Glide.with(getApplicationContext()).load(currentvendor.getImage()).into(profile_image);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");



        fireStore = FirebaseFirestore.getInstance();
        vendorCollection = fireStore.collection(VENDOR_COLLECTION);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new ChatsFragment(), "Chats");
        viewPagerAdapter.addFragment(new ClientsFragment(), "Users");

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);


    }

    private void toggleStatus(String status){

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
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

    }

    @Override
    protected void onResume() {
        super.onResume();
        toggleStatus("online");
        Log.d(TAG, "onResume");

    }

    @Override
    protected void onPause() {
        super.onPause();
        toggleStatus("offline");
        Log.d(TAG, "onPause");

    }

    class ViewPagerAdapter extends FragmentPagerAdapter{

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;


        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }


}