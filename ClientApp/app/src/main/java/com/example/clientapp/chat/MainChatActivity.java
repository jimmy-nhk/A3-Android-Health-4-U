package com.example.clientapp.chat;

import com.bumptech.glide.Glide;
import com.example.clientapp.R;
import com.example.clientapp.activity.MainActivity;
import com.example.clientapp.chat.fragments.ChatsFragment;
import com.example.clientapp.chat.fragments.UsersFragment;
import com.example.clientapp.helper.viewModel.CartViewModel;
import com.example.clientapp.model.Client;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainChatActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;

    private FirebaseFirestore fireStore;
    private CollectionReference clientCollection;
    private final String CLIENT_COLLECTION = "clients";
    private Client currentClient;

    private ClientViewModel clientViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        // get the current client
        Intent intent = getIntent();
        currentClient = intent.getParcelableExtra("client");

        // set current client to the view model
        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
        clientViewModel.setValue(currentClient);

        // attach the component
        profile_image = findViewById(R.id.profile_image);
        username  = findViewById(R.id.usernameMainChat);

        username.setText("username: "+currentClient.getUsername());
        profile_image.setImageResource(R.mipmap.ic_launcher);
        //FIXME: if has profile please fill in
//                    Glide.with(MainChatActivity.this).load(currentClient.getImage()).into(profile_image);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");


        fireStore = FirebaseFirestore.getInstance();
        clientCollection = fireStore.collection(CLIENT_COLLECTION);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new ChatsFragment(), "Chats");
        viewPagerAdapter.addFragment(new UsersFragment(), "Users");

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);


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