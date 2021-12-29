package com.example.clientapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.clientapp.R;
import com.example.clientapp.activity.MainActivity;
import com.example.clientapp.helper.CategoryHomeAdapter;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    // Views
    private RecyclerView categoryRecycleView;
    private RecyclerView recyclerView;
    private CategoryHomeAdapter adapter;
    private String selectedCategory = "";

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getViews(view);
        initCategoryListAdapter(view);
    }

    private void initCategoryListAdapter(View view) {
        //This set list adapter for category
        ArrayList<String> listCategoryValue = new ArrayList<>();
        listCategoryValue.add("Rice");
        listCategoryValue.add("Noodles");
        listCategoryValue.add("Banh mi/Sticky rice");
        listCategoryValue.add("Salad");
        listCategoryValue.add("Snacks");
        listCategoryValue.add("Drinks");

        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        categoryRecycleView.setLayoutManager(horizontalLayoutManager);
        adapter = new CategoryHomeAdapter(view.getContext(), listCategoryValue);
        adapter.setClickListener((view1, position) -> {
            //Set category on Clicked category
            selectedCategory = listCategoryValue.get(position);
            redirectToItemListFragment(selectedCategory);
        });
        categoryRecycleView.setAdapter(adapter);
    }

    private void redirectToItemListFragment(String category) {
//        String backStateName = this.getClass().getName();
//        FragmentManager fragmentManager = getParentFragmentManager();
//        boolean fragmentPopped = fragmentManager.popBackStackImmediate (backStateName, 0);
//        fragmentManager.popBackStackImmediate();
//        Toast.makeText(getContext(), "fragmentManager.getBackStackEntryCount()=" + fragmentManager.getBackStackEntryCount(), Toast.LENGTH_SHORT).show();
//        Log.d("HomeFragment", "backstack=" + fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName());
//        Log.d("HomeFragment", "fragmentPopped=" + fragmentPopped);

        Fragment fragment = new ItemListFragment();
        loadFragment(fragment, category);
    }

    private void loadFragment(Fragment fragment, String category) {
        // load fragment
//        FragmentManager fragmentManager = getParentFragmentManager();
//        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//        transaction.show(fragmentManager.findFragmentByTag());
//        transaction.replace(R.id.fragment_container, fragment);
//        transaction.addToBackStack(null);
//        transaction.commit();

        Bundle bundle = new Bundle();
        bundle.putString("category", category);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void getViews(View view) {
        categoryRecycleView = view.findViewById(R.id.recyclerCategoryHome);
    }
}