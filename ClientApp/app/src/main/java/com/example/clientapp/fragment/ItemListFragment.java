package com.example.clientapp.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clientapp.R;
import com.example.clientapp.helper.adapter.ItemRecyclerViewAdapter;
import com.example.clientapp.helper.viewModel.ItemViewModel;
import com.example.clientapp.helper.adapter.CategoryAdapter;
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

    // Service
    private List<Item> itemList = new ArrayList<>();
    private String selectedCategory = "";
    HashMap<String, Integer> categoryList = new HashMap<>();
    private CategoryAdapter categoryAdapter;
    private ItemRecyclerViewAdapter mAdapter;
    private static final String TAG = ItemListFragment.class.getSimpleName();
    private static final String ITEM_COLLECTION = "items";
    private FirebaseFirestore fireStore;
    private CollectionReference itemCollection;

    // Views
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

        // Get arguments
        getArgs();

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

        return view;
    }

    private void getArgs() {
        try {
            if (getArguments() != null) {
                selectedCategory = getArguments().getString("category");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //This function check if search view value is changed
    private void checkSearch(View view) {
        //Check search value on changed and load value
        searchTxt.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                Toast.makeText(view.getContext(), "onQueryTextSubmit", Toast.LENGTH_SHORT).show();
                fetchItemsToListView(view);
                Log.d(TAG, "onQueryTextSubmit");

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fetchItemsToListView(view);
                Log.d(TAG, "onQueryTextChange");
                return false;
            }
        });
    }

    // init service
    private void initService(View view) {
        // init fireStore db
        fireStore = FirebaseFirestore.getInstance();
        itemCollection = fireStore.collection(ITEM_COLLECTION);

        //Fetch item from server
        fetchItemsToListView(view);
        cancelBtn.setOnClickListener(this::onCancelBtnClick);

    }

    // onCancel btn
    private void onCancelBtnClick(View view){
        selectedCategory = "";
        searchTxt.setQuery("",false);
        Log.d(TAG, "selectedCategory: onCancel: " + selectedCategory);
        fetchItemsToListView(view);
    }

    // fetch items to list view
    private void fetchItemsToListView(View view) {
        // Init conditions
        String searchValue = searchTxt.getQuery().toString();
        String localCategory = this.selectedCategory;
        Log.d(TAG, "localCategory: " + localCategory);
        Log.d(TAG, "selectedCategory: " + selectedCategory);

        // Load items from Firestore
        itemCollection.addSnapshotListener((value, error) -> {
            // clear to list
            itemList = new ArrayList<>();//Reset value of item List
            categoryList= new HashMap<>();
            String currentItemCategory;

            // validate if there is no value in the list
            if (value == null || value.isEmpty())
                return;

            // Cast to item object and add to item list
            for (int i = 0; i < value.size(); i++) {
                Item item = value.getDocuments().get(i).toObject(Item.class);
                // add value by conditions
                assert item != null;
                currentItemCategory = item.getCategory();

                // Filter items
                //----- for reference, please don't delete--------
//                if ((searchValue.isEmpty() && (item.getCategory()
//                        .equalsIgnoreCase(localCategory.toLowerCase())
//                        || localCategory.isEmpty())
//                || (!searchValue.isEmpty() && item.getName().toLowerCase().contains(searchValue.toLowerCase())
//                        && item.getCategory().toLowerCase().contains(localCategory.toLowerCase())))) {
//                    addItemToFilteredList(item);
//                }
                // If filter by category or view all
                if ((searchValue.isEmpty()
                        && (matchesCategory(currentItemCategory, localCategory) // by category
                        || localCategory.isEmpty()) // view all
                        //////////////////////////////////////////////////////////////////////
                        // If search with keyword
                        || (!searchValue.isEmpty() && matchesItemName(item.getName(), searchValue)
                        && containsCategory(currentItemCategory, localCategory)))) {
                    addItemToFilteredList(item);
                }

                //----- for reference, please don't delete--------
                //Condition check if current item has match search key or match category
//                else if (!searchValue.isEmpty() && item.getName().toLowerCase().contains(searchValue.toLowerCase())
//                        && item.getCategory().toLowerCase().contains(localCategory.toLowerCase())) {
//                    addItemToFilteredList(item);
//                }
            }

            // sort again
            sortItemList();

            // Initialize list adapter
            initListAdapter(view);
        });

    }

    //validate category
    private boolean matchesCategory(String itemCategory, String localCategory) {
        return itemCategory
                .equalsIgnoreCase(localCategory.toLowerCase());
    }

    //validate category
    private boolean containsCategory(String itemCategory, String localCategory) {
        return itemCategory.toLowerCase().contains(localCategory.toLowerCase());
    }

    // match name
    private boolean matchesItemName(String itemName, String searchValue) {
        return itemName.toLowerCase().contains(searchValue.toLowerCase());
    }

    private void addItemToFilteredList(Item item) {
        itemList.add(item);
        //Check if current item category is contained in hashmap
        int count = categoryList.containsKey(item.getCategory()) ?
                categoryList.get(item.getCategory()) : 0;

        //if category is not added to list yet, add to list and set value to 1
        //if value is already existed, increase by 1
        categoryList.put(item.getCategory(), count + 1);
    }

    private void sortItemList() {
        itemList.sort((o1, o2) -> {
            // reverse sort
            if (o1.getId() < o2.getId()) {
                return 1; // normal will return -1
            } else if (o1.getId() > o2.getId()) {
                return -1; // reverse
            }
            return 0;
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initListAdapter(View view) {
        if (isAdded()){
            mAdapter = new ItemRecyclerViewAdapter(getActivity(), itemList, viewModel);

            // linear styles
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setNestedScrollingEnabled(true);
            recyclerView.setAdapter(mAdapter);
//            Log.d(TAG, "searchStr: " + searchValue);

            mAdapter.notifyDataSetChanged();
        }


        //This set list adapter for category
        ArrayList<String> listCategoryValue = new ArrayList<>();
        for (Map.Entry<String, Integer> category : categoryList.entrySet()) {
            listCategoryValue.add(category.getKey() + '(' + category.getValue() + ')');
        }

        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        categoryRecycleView.setLayoutManager(horizontalLayoutManager);
        categoryAdapter = new CategoryAdapter(view.getContext(), listCategoryValue);
        categoryAdapter.setClickListener((view1, position) -> {
            //Set category on Clicked category
            String[] arrOfStr = listCategoryValue.get(position).split("\\(", 2);
            selectedCategory = arrOfStr[0];
//            Toast.makeText(view1.getContext(), "Clicked on " + selectedCategory, Toast.LENGTH_SHORT).show();

            Log.d(TAG, "selectedCategory in adapter: " + selectedCategory);
            fetchItemsToListView(view1);
        });
        categoryRecycleView.setAdapter(categoryAdapter);
        categoryAdapter.notifyDataSetChanged();


    }

    // Attach components
    public void getViews(View view) {
        searchTxt = view.findViewById(R.id.searchView);
        // Get recycler view
        recyclerView = view.findViewById(R.id.recycler_view);
        categoryRecycleView = view.findViewById(R.id.categoryRecycleView);
        cancelBtn = view.findViewById(R.id.cancelSearch);


    }
}