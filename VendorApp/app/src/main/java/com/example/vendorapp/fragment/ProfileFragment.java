package com.example.vendorapp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.vendorapp.R;
import com.example.vendorapp.model.Vendor;

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
    private static final String ARG_VENDOR = "vendor";

    private EditText fullNameTxt, usernameTxt, emailTxt, phoneTxt, addressTxt
            , ratingTxt, totalSalesTxt;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Vendor vendor;

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
            vendor = getArguments().getParcelable(ARG_VENDOR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        getViews(view);

        // display vendor
        if (vendor != null) {
            updateUI();
        }

        // Inflate the layout for this fragment
        return view;
    }

    private void updateUI() {
        fullNameTxt.setText(vendor.getFullName());
        usernameTxt.setText(vendor.getUsername());
        emailTxt.setText(vendor.getEmail());
        phoneTxt.setText(vendor.getPhone());
        addressTxt.setText(vendor.getAddress());

        String ratingStr = String.valueOf(vendor.getRating());
        String totalSalesStr = String.valueOf(vendor.getTotalSale());
        ratingTxt.setText(ratingStr);
        totalSalesTxt.setText(totalSalesStr);
    }

    private void getViews(View view) {
        fullNameTxt = view.findViewById(R.id.editFullName);
        usernameTxt = view.findViewById(R.id.editUsername);
        emailTxt = view.findViewById(R.id.editEmail);
        phoneTxt = view.findViewById(R.id.editPhone);
        addressTxt = view.findViewById(R.id.editAddress);
        ratingTxt = view.findViewById(R.id.editRating);
        totalSalesTxt = view.findViewById(R.id.editTotalSales);
    }
}