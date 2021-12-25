package com.example.clientapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clientapp.R;
import com.example.clientapp.helper.ItemRecyclerViewAdapter;
import com.example.clientapp.helper.ItemViewModel;
import com.example.clientapp.model.Item;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ItemListFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<Item> itemList;
    private ItemRecyclerViewAdapter mAdapter;
    private static final String TAG = ItemListFragment.class.getSimpleName();
    private static final String ITEM_COLLECTION = "items";
    private FirebaseFirestore fireStore;
    private CollectionReference itemCollection;

    private ItemViewModel viewModel;


    public ItemListFragment(){

        Log.d(TAG, "FoodListFragment: onCreate");
    }

    public static ItemListFragment newInstance(String param1, String param2) {
        ItemListFragment fragment = new ItemListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);

        initService(view);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        //TODO: TESTING. Remember to turn on
//        initService(view);

        Log.d(TAG, "FoodListFragment: onCreateView");


        return view;
    }

    private void initService(View view) {
        // init fireStore db
        fireStore = FirebaseFirestore.getInstance();
        itemCollection = fireStore.collection(ITEM_COLLECTION);

        //Fetch item from server
        itemList = new ArrayList<>(); //Reset value of item List

        // load items
        itemCollection.addSnapshotListener((value, error) -> {

            // clear to list
            itemList = new ArrayList<>();

            // validate no value in the list
            if (value.isEmpty()){
                return;
            }


            for (int i = 0 ; i < value.size(); i++){

                itemList.add(value.getDocuments().get(i).toObject(Item.class));
            }

            // sort again
            itemList.sort((o1, o2) -> {
                // reverse sort
                if (o1.getId() < o2.getId()){
                    return 1; // normal will return -1
                } else if (o1.getId() > o2.getId()){
                    return -1; // reverse
                }
                return 0;
            });

            recyclerView = view.findViewById(R.id.recycler_view);

            mAdapter = new ItemRecyclerViewAdapter(getActivity(), itemList, viewModel);


            // linear styles
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setNestedScrollingEnabled(true);
            recyclerView.setAdapter(mAdapter);


            // grid styles
//        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
//        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setAdapter(mAdapter);
//        recyclerView.setNestedScrollingEnabled(false);
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}