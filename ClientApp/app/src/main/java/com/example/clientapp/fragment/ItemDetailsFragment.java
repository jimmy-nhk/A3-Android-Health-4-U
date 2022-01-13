package com.example.clientapp.fragment;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clientapp.R;
import com.example.clientapp.helper.viewModel.ItemViewModel;
import com.example.clientapp.model.Item;
import com.example.clientapp.model.Vendor;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ItemDetailsFragment extends Fragment {



    // Views
    TextView itemNameTxt;
    TextView itemPriceTxt;
    TextView itemDescriptionTxt;
    TextView itemCategoryTxt;
    TextView itemCaloriesTxt;
    TextView storeNameTxt;
    TextView itemExpirationDateTxt;
    CardView backBtnV;
//    ImageView backBtnV;
    Button addToCardButton;

    private Item item;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
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
        //Init back btn
        initBackBtnHandler();
        // Inflate the layout for this fragment
        return view;
    }

    private void initBackBtnHandler() {
        try {
            backBtnV.setOnClickListener(v -> {
                if (requireActivity().getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    requireActivity().finish();
                } else {
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    //get store name
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

    // display item details
    private void displayItemDetail() {
        itemNameTxt.setText(item.getName());
        itemPriceTxt.setText(("$ " + item.getPrice()));
        itemDescriptionTxt.setText(item.getDescription());
        itemCategoryTxt.setText(item.getCategory());
        itemCaloriesTxt.setText((item.getCalories() + " cal"));
        itemExpirationDateTxt.setText(item.getExpireDate());
        getStoreName(item.getVendorID() + "");
    }

    // get views
    private void getViews(View view) {
        itemNameTxt = view.findViewById(R.id.itemNameText);
        storeNameTxt = view.findViewById(R.id.storeNameText);
        itemPriceTxt = view.findViewById(R.id.itemPriceText);
        itemDescriptionTxt = view.findViewById(R.id.itemDescriptionText);
        itemCaloriesTxt = view.findViewById(R.id.itemCaloriesText);
        itemCategoryTxt = view.findViewById(R.id.itemCategoryText);
        itemExpirationDateTxt = view.findViewById(R.id.itemExpirationDateText);
        addToCardButton = view.findViewById(R.id.addToCartBtn);
        backBtnV = view.findViewById(R.id.backBtn);

        // addToCardButton onClick
        addToCardButton.setOnClickListener(v -> handleAddItemToCardClick(item));

        // Set store on click listener
        storeNameTxt.setOnClickListener(v -> handleStoreClick());
    }

    private void handleAddItemToCardClick(Item item) {
        if (item == null) return;
        ItemViewModel viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        viewModel.addItem(item);
        Toast.makeText(requireContext(), "Added item " + item.getName() + " to cart", Toast.LENGTH_SHORT).show();
    }
}