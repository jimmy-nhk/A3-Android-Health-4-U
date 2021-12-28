package com.example.clientapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

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
import com.example.clientapp.helper.categoryAdapter;
import com.example.clientapp.model.Item;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemListFragment extends Fragment {
    // components
    private SearchView searchTxt;
    private RecyclerView categoryRecycleView;
    private RecyclerView recyclerView;
    private Button cancelBtn;
    //
    private List<Item> itemList = new ArrayList<>();
    private String selectedCategory="";
    HashMap<String, Integer> categoryList = new HashMap<String, Integer>();
    private com.example.clientapp.helper.categoryAdapter categoryAdapter;
    private ItemRecyclerViewAdapter mAdapter;
    private static final String TAG = ItemListFragment.class.getSimpleName();
    private static final String ITEM_COLLECTION = "items";
    private FirebaseFirestore fireStore;
    private CollectionReference itemCollection;

    private ItemViewModel viewModel;


    public ItemListFragment() {
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
        getViews(view);
        initService(view);
        checkSearch(view);
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

    //This function check if search view value is changed
    private void checkSearch(View view) {
        //Check search value on changed and load value
        searchTxt.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(view.getContext(), "onQueryTextSubmit", Toast.LENGTH_SHORT).show();
                fetchItemsToListview(view);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fetchItemsToListview(view);
                return false;
            }
        });
    }

    private void initService(View view) {
        // init fireStore db
        fireStore = FirebaseFirestore.getInstance();
        itemCollection = fireStore.collection(ITEM_COLLECTION);

        //Fetch item from server
        fetchItemsToListview(view);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOnClick(v);
            }
        });
    }
    private void cancelOnClick(View view){
        selectedCategory="";
        searchTxt.setQuery("",false);
        fetchItemsToListview(view);
    };

    private void fetchItemsToListview(View view) {
        //Init condition
        String searchValue = searchTxt.getQuery().toString();
        String localCategory = this.selectedCategory;
        Log.d(TAG,"searchValue "+searchValue);
        Log.d(TAG,"selectedCategory "+selectedCategory);
        // load items
        itemCollection.addSnapshotListener((value, error) -> {

            // clear to list
            itemList = new ArrayList<>();//Reset value of item List
            categoryList= new HashMap<>();
            // validate no value in the list
            if (value == null || value.isEmpty()) {
                return;
            }

            // Cast to item object and add to item list
            for (int i = 0; i < value.size(); i++) {
                Item item = value.getDocuments().get(i).toObject(Item.class);
                //add value by conditions
                assert item != null;
                //Condition check if current item has match search key or match category
                if (item.getName().toLowerCase().contains(searchValue.toLowerCase())
                        && item.getCategory().toLowerCase().contains(localCategory.toLowerCase())) {
                    itemList.add(item);

                    //Check if current item category is conatained in hashmap
                    int count = categoryList.containsKey(item.getCategory()) ? categoryList.get(item.getCategory()) : 0;

                    //if category is not added to list yet, add to list and set value to 1
                    //if value is already existed, increase by 1
                    categoryList.put(item.getCategory(), count + 1);
                }
            }
            // sort again
            itemList.sort((o1, o2) -> {
                // reverse sort
                if (o1.getId() < o2.getId()) {
                    return 1; // normal will return -1
                } else if (o1.getId() > o2.getId()) {
                    return -1; // reverse
                }
                return 0;
            });


            // Initialize list adapter
            mAdapter = new ItemRecyclerViewAdapter(getActivity(), itemList, viewModel);

            // linear styles
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setNestedScrollingEnabled(true);
            recyclerView.setAdapter(mAdapter);
            Log.d(TAG, "searchStr: " + searchValue);

            //This set list adapter for category
            ArrayList<String> listCategoryValue = new ArrayList<>();
            for (Map.Entry<String, Integer> category : categoryList.entrySet()) {
                listCategoryValue.add(category.getKey() + '(' + category.getValue() + ')');
            }

            LinearLayoutManager horizontalLayoutManager
                    = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
            categoryRecycleView.setLayoutManager(horizontalLayoutManager);
            categoryAdapter = new categoryAdapter(view.getContext(), listCategoryValue);
            categoryAdapter.setClickListener(new categoryAdapter.ItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                    //Set category on Clicked category
                    String[] arrOfStr = listCategoryValue.get(position).split("\\(", 2);
                    selectedCategory=arrOfStr[0];
                    Toast.makeText(view.getContext(), "Clicked on " + selectedCategory, Toast.LENGTH_SHORT).show();

                    fetchItemsToListview(view);
                }
            });
            categoryRecycleView.setAdapter(categoryAdapter);
            // grid styles
//        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
//        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setAdapter(mAdapter);
//        recyclerView.setNestedScrollingEnabled(false);
        });
    }

    // attach components
    public void getViews(View view) {
        searchTxt = view.findViewById(R.id.searchView);
        // Get recycler view
        recyclerView = view.findViewById(R.id.recycler_view);
        categoryRecycleView = view.findViewById(R.id.categoryRecycleView);
        cancelBtn = view.findViewById(R.id.cancelSearch);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}