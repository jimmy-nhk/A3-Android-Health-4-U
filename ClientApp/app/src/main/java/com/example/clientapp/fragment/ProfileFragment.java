package com.example.clientapp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.clientapp.R;
import com.example.clientapp.model.Client;
import com.example.clientapp.helper.ItemRecyclerViewAdapter;
import com.google.android.gms.common.api.Api;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "ProfileFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView fullNameTextView;
    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView phoneTextView;
    private TextView dobTextView;
    private TextView weightTextView;
    private TextView heightTextView;

    private String fullName;
    private String username;
    private String email;
    private String phone;
    private String dob;
    private double weight;
    private double height;

    private FirebaseFirestore fireStore;
    private Client client;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        if (getArguments() != null) {
            username = getArguments().getString("username");
        }

        getViews(view);
        initService(view);
//        getUserInfo();



        // Inflate the layout for this fragment
        return view;
    }

    private void initService(View view) {
        // init fireStore db
        fireStore = FirebaseFirestore.getInstance();
        DocumentReference docRef = fireStore.collection("clients").document(username);

        // load items
        docRef.addSnapshotListener((value, error) -> {
            if (value != null) {
                Log.d(TAG, "value != null");
                Client c = value.toObject(Client.class);

                Log.d(TAG, "c != null=" + (c != null));
                if (c != null) {
                    fullName = c.getFullName();
                    username = c.getUsername();
                    email = c.getEmail();
                    phone = c.getPhone();
                    dob = c.getDob();
                    weight = c.getWeight();
                    height = c.getHeight();

                    displayUserInfo();
                }
            }
        });
    }

    private void displayUserInfo() {
        fullNameTextView.setText(fullName);
        usernameTextView.setText(username);
        emailTextView.setText(email);
        phoneTextView.setText(phone);
        dobTextView.setText(dob);
        weightTextView.setText(String.valueOf(weight));
        heightTextView.setText(String.valueOf(height));
    }

    private void getViews(View view) {
        fullNameTextView = view.findViewById(R.id.fullNameTxt);
        usernameTextView = view.findViewById(R.id.usernameTxt);
        emailTextView = view.findViewById(R.id.emailTxt);
        phoneTextView = view.findViewById(R.id.phoneTxt);
        dobTextView = view.findViewById(R.id.dobTxt);
        weightTextView = view.findViewById(R.id.weightTxt);
        heightTextView = view.findViewById(R.id.heightTxt);
    }
}