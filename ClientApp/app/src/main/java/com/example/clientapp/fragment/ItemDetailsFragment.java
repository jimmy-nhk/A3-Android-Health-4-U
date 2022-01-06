package com.example.clientapp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.clientapp.R;
import com.example.clientapp.helper.viewModel.ItemViewModel;
import com.example.clientapp.model.Item;
import com.example.clientapp.model.Vendor;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ItemDetailsFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Views
    TextView itemNameTxt;
    TextView itemPriceTxt;
    TextView itemDescriptionTxt;
    TextView itemCategoryTxt;
    TextView itemCaloriesTxt;
    TextView storeNameTxt;
    TextView itemExpirationDateTxt;
    Button addToCardButton;

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

        try {
            // Go to item detail fragment
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }catch (Exception e){
            return;
        }
    }

    private void getStoreName(String vendorID) {
        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        final DocumentReference docRef = fireStore.collection("vendors").document(vendorID);
        docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w("ItemDetailsFragment", "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
//                    Log.d("ItemDetailsFragment", "Current data: " + snapshot.getData());
                try {
                    Vendor vendor = snapshot.toObject(Vendor.class);
                    storeNameTxt.setText(vendor != null ? vendor.getStoreName() : "");
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            } else {
                Log.d("ItemDetailsFragment", "Current data: null");
            }
        });

    }

    private void displayItemDetail() {
        itemNameTxt.setText(item.getName());
        itemPriceTxt.setText(("$ " + item.getPrice()));
        itemDescriptionTxt.setText(item.getDescription());
        itemCategoryTxt.setText(item.getCategory());
        itemCaloriesTxt.setText((item.getCalories() + " cal"));
        itemExpirationDateTxt.setText(item.getExpireDate());
        getStoreName(item.getVendorID() + "");
    }

    private void getViews(View view) {
        itemNameTxt = view.findViewById(R.id.itemNameText);
        storeNameTxt = view.findViewById(R.id.storeNameText);
        itemPriceTxt = view.findViewById(R.id.itemPriceText);
        itemDescriptionTxt = view.findViewById(R.id.itemDescriptionText);
        itemCaloriesTxt = view.findViewById(R.id.itemCaloriesText);
        itemCategoryTxt = view.findViewById(R.id.itemCategoryText);
        itemExpirationDateTxt = view.findViewById(R.id.itemExpirationDateText);
        addToCardButton = view.findViewById(R.id.addToCartBtn);

        // addToCardButton onClick
        addToCardButton.setOnClickListener(v -> handleAddItemToCardClick(item));

        // Set store on click listener
        storeNameTxt.setOnClickListener(v -> handleStoreClick());
    }

    private void handleAddItemToCardClick(Item item) {
        if (item == null) return;
        ItemViewModel viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        viewModel.addItem(item);
    }
}