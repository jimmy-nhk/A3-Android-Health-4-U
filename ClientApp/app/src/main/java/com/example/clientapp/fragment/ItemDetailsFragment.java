package com.example.clientapp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clientapp.R;
import com.example.clientapp.model.Item;

public class ItemDetailsFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Views
    TextView itemNameTxt;

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

    private void displayItemDetail() {
        itemNameTxt.setText(item.toString());
    }

    private void getViews(View view) {
        itemNameTxt = view.findViewById(R.id.itemNameText);
    }
}