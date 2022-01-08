package com.example.clientapp.fragment;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clientapp.R;
import com.example.clientapp.helper.broadcast.HydrationReminderReceiver;
import com.example.clientapp.helper.viewModel.ItemViewModel;
import com.example.clientapp.helper.adapter.CategoryHomeAdapter;
import com.example.clientapp.helper.adapter.ItemRecyclerViewAdapter;
import com.example.clientapp.helper.adapter.NewStoreRecyclerViewAdapter;
import com.example.clientapp.model.Item;
import com.example.clientapp.model.Vendor;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.Calendar;

public class HomeFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final int ALARM_REQUEST_CODE = 100;

    private static final String TAG = HomeFragment.class.getSimpleName();

    // Views & Adapters
    private ItemViewModel viewModel;
    private ItemRecyclerViewAdapter mAdapter;
    private RecyclerView categoryRecyclerView;
    private RecyclerView newStoreRecyclerView;
    private RecyclerView newItemRecyclerView;
    private CategoryHomeAdapter categoryHomeAdapter;
    private NewStoreRecyclerViewAdapter newStoresAdapter;
    private ItemRecyclerViewAdapter newItemsAdapter;
    private SwitchCompat isRemindButton;
    private TextView remindIntervalTxt;
    private LinearLayout isDrinkingLayout;
    // List
    private String selectedCategory = "";
    private Vendor selectedStore;
    private boolean isRemind = false;
    private int remindInterval = 1;

    // Firestore
    private static final String ITEM_COLLECTION = "items";
    private static final String VENDOR_COLLECTION = "vendors";
    private FirebaseFirestore fireStore;
    private CollectionReference storeCollection;
    private CollectionReference itemCollection;
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        getViews(view);
        initService(view);
        initCategoryListAdapter(view);
        initHydrationReminder(view);
    }

    //Init button on click
    private void initHydrationReminder(View view) {
        isRemindButton.setChecked(false);

        isRemindButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { // If ischecked, run the alarm intent and show the hydration layout
                    runAlarm(remindInterval);
                    isDrinkingLayout.setVisibility(View.VISIBLE); //show the hydration layout
                } else {
                    cancelAlarm(); // If ! ischecked, cancel the alarm intent and hide the hydration layout
                    isDrinkingLayout.setVisibility(View.GONE); //hide the hydration layout

                }
            }
        });
        remindIntervalTxt.setOnClickListener(v -> { //Check if the interval text is clicked, if yes, show the dialog
            showIntervalDialog();
        });
    }

    //Function show dialog to set interval
    private void showIntervalDialog() {
        //Setup the Number picker in the dialog
        final AlertDialog.Builder d = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.number_picker_dialog, null);
        d.setView(dialogView);

        //Configure Numberpicker
        final NumberPicker numberPicker = (NumberPicker) dialogView.findViewById(R.id.dialog_number_picker);
        numberPicker.setValue(20); //set default value as 20
        numberPicker.setMaxValue(60); //set max value as 60
        numberPicker.setMinValue(1); //set min value as 1
        numberPicker.setWrapSelectorWheel(true);

        // Check if any value change when numberPicker dialog is popup.
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                remindInterval = numberPicker.getValue(); // on value change, set reminder interval to selected number
            }
        });
        // Check if the dialog is dismiss
        d.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                remindIntervalTxt.setText(remindInterval + " minutes"); // Set text view of interval equals to selected number
                runAlarm(remindInterval); // Run alarm with selected interval, from the dismissed time
            }
        });

        // alert dialog
        AlertDialog alertDialog = d.create(); // create AlertDialog from builder
        alertDialog.setCanceledOnTouchOutside(true); // Dismiss the dialog when touch outside the dialog
        alertDialog.show(); // Show the dialog
    }

    // Run the alarm by interval
    private void runAlarm(int remindInterval) {
        Log.d("runAlarm", remindInterval + "");

        // cancel the previous alarm first
        cancelAlarm();

        //Configure alarm
        Intent intent = new Intent(getContext(), HydrationReminderReceiver.class); // set the broadcast HydrationReminderReceiver to be receiver
        alarmIntent = PendingIntent.getBroadcast(
                getContext(), ALARM_REQUEST_CODE, intent, 0); // Set intent with broadcast
        getContext();
        alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE); // Initialize alarm Manager
        // Below line set the alarm to the alarm manager, which is elapsed by interval. The first alarm will be triggered after the interval, and repeated forever.
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, (long) SystemClock.elapsedRealtime() + (long) remindInterval * 60 * 1000, (long) remindInterval * 60 * 1000, alarmIntent);
    }

    //Cancel set alarm if existed
    private void cancelAlarm() {
        if (alarmManager != null) { // if alarmManager is not null, it is used to set alarm
            alarmManager.cancel(alarmIntent); // cancel set alarm in this alarm manager by alarm intent, which is the code of set alarm
            Log.d(TAG, "cancel alarm: " + remindInterval);

        }
    }

    // init category list
    private void initCategoryListAdapter(View view) {
        //This set list adapter for category
        ArrayList<String> listCategoryValue = new ArrayList<>();
        listCategoryValue.add("Rice");
        listCategoryValue.add("Noodles");
        listCategoryValue.add("Banh mi/Sticky rice");
        listCategoryValue.add("Salad");
        listCategoryValue.add("Snacks");
        listCategoryValue.add("Drinks");

        // set linear layout
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        categoryRecyclerView.setLayoutManager(horizontalLayoutManager);
        categoryHomeAdapter = new CategoryHomeAdapter(view.getContext(), listCategoryValue);
        categoryRecyclerView.setAdapter(categoryHomeAdapter);
    }

    private void initNewStoreListAdapter(View view, ArrayList<Vendor> newStoreList) {
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        newStoreRecyclerView.setLayoutManager(horizontalLayoutManager);

        if (isAdded()){
            newStoresAdapter = new NewStoreRecyclerViewAdapter(view.getContext(), newStoreList);
            newStoreRecyclerView.setAdapter(newStoresAdapter);
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private void initNewItemListAdapter(View view, ArrayList<Item> newItemList) {
        // sort again
        newItemList.sort((o1, o2) -> {
            // reverse sort
            if (o1.getId() < o2.getId()) {
                return 1; // normal will return -1
            } else if (o1.getId() > o2.getId()) {
                return -1; // reverse
            }
            return 0;
        });

        // Get recycler view
        newItemRecyclerView = view.findViewById(R.id.recyclerNewItems);

        // Initialize list adapter
        if (isAdded())
        {
            mAdapter = new ItemRecyclerViewAdapter(getActivity(), newItemList, viewModel);
            mAdapter.notifyDataSetChanged();
            // linear styles
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            newItemRecyclerView.setLayoutManager(linearLayoutManager);
            newItemRecyclerView.setItemAnimator(new DefaultItemAnimator());
            newItemRecyclerView.setHasFixedSize(true);
            newItemRecyclerView.setAdapter(mAdapter);
        }



    }


    private void loadNewStoreList(View view) {
        try {
            ArrayList<Vendor> storeList = new ArrayList<>();

            storeCollection.addSnapshotListener((value, error) -> {
                if (value == null || value.isEmpty())
                    return;

                int size = value.size();
                int maxListSize = Math.min(size, 8);

                for (int i = size - 1, j = 0; j < maxListSize; i--, j++)
                    storeList.add(value.getDocuments().get(i).toObject(Vendor.class));

                // Load
                initNewStoreListAdapter(view, storeList);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadNewItemList(View view) {
        try {
            ArrayList<Item> itemList = new ArrayList<>();

            itemCollection.addSnapshotListener((value, error) -> {
                if (value == null || value.isEmpty())
                    return;

                int size = value.size();
                int maxListSize = Math.min(size, 10);

                for (int i = size - 1, j = 0; j < maxListSize; i--, j++) {
                    itemList.add(value.getDocuments().get(i).toObject(Item.class));
                    Log.d("HomeFragment", "item=" + itemList.get(j).toString());
                }

                // Load
                initNewItemListAdapter(view, itemList);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initService(View view) {
        // init fireStore db
        fireStore = FirebaseFirestore.getInstance();
        storeCollection = fireStore.collection(VENDOR_COLLECTION);
        itemCollection = fireStore.collection(ITEM_COLLECTION);

        // Load data from Firestore
        loadNewStoreList(view);
        loadNewItemList(view);
    }

    private void getViews(View view) {
        categoryRecyclerView = view.findViewById(R.id.recyclerCategoryHome);
        newStoreRecyclerView = view.findViewById(R.id.recyclerNewStores);
        isRemindButton = view.findViewById(R.id.hydrationSwitch);
        remindIntervalTxt = view.findViewById(R.id.hydrationInterval);
        isDrinkingLayout = view.findViewById(R.id.isDrinking);
    }
}