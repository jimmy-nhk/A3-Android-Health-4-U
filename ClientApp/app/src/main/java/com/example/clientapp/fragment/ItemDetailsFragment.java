package com.example.clientapp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.clientapp.R;
import com.example.clientapp.activity.MainActivity;
import com.example.clientapp.model.Item;

public class ItemDetailsFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Views
    TextView itemNameTxt;
    TextView storeNameTxt;

    private String mParam1;
    private String mParam2;
    private Item item;

    public ItemDetailsFragment() {
        // Required empty public constructor
    }

    public static ItemDetailsFragment newInstance(String param1, String param2) {
        ItemDetailsFragment fragment = new ItemDetailsFragment();
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
            item = getArguments().getParcelable("item");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_details, container, false);

        // Get views
        getViews(view);
        // Render item detail
        displayItemDetail();

        // Inflate the layout for this fragment
        return view;
    }

    // On store name click => go to Store profile page
    private void handleStoreClick() {
        // Put vendorID in bundle to send to StoreDetails fragment
        Bundle bundle = new Bundle();
        bundle.putInt("vendorID", item.getVendorID());

        // Get fragment
        Fragment fragment = new StoreDetailsFragment();
        fragment.setArguments(bundle);

        // Go to item detail fragment
        FragmentTransaction transaction;
        if (getActivity() != null) {
            transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private void displayItemDetail() {
        itemNameTxt.setText(item.toString());
        storeNameTxt.setText(("Click here to go to Store profile: " + (item.getVendorID())));
    }

    private void getViews(View view) {
        itemNameTxt = view.findViewById(R.id.itemNameText);
        storeNameTxt = view.findViewById(R.id.storeNameText);

        // Set store on click listener
        storeNameTxt.setOnClickListener(v -> handleStoreClick());
    }
}