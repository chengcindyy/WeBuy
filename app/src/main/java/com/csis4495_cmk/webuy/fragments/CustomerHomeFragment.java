package com.csis4495_cmk.webuy.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.PopupWindow;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.adapters.viewpager.CustomerHomeViewPagerAdapter;
import com.csis4495_cmk.webuy.viewmodels.CustomerHomeFilterViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;

public class CustomerHomeFragment extends Fragment {
    private static final int STORE_STATUS_NOT_YET_OPEN = 0;
    private static final int STORE_STATUS_OPENING = 1;
    private static final int GROUP_TYPE_IN_STOCK = 0;
    private static final int GROUP_TYPE_PRE_ORDER = 1;
    private final String[] locations = new String[]{"All", "Vancouver", "W. Vancouver", "N. Vancouver",
            "Burnaby", "Richmond", "New West", "Coquitlam", "Surrey", "Langley", "Delta",
            "Port Moody", "White Rock", "Maple Ridge"};
    private final String[] sort = new String[]{"Price low to high", "Price high to low", "Time nearest to furthest", "Time furthest to nearest"};
    private androidx.appcompat.widget.SearchView searchView;
    private CustomerHomeFilterViewModel model;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private CustomerHomeViewPagerAdapter viewPagerAdapter;
    private BottomNavigationView bottomNavigationView;
    private AutoCompleteTextView inputLocation, inputSort, inputTag;
    private TextInputLayout layoutLocation;
    private NavController navController;
    private int storeStatus = -1;
    private int groupTypes = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set navigation controller, and if you want to navigate to other fragment can call this to navigate
        navController = NavHostFragment.findNavController(CustomerHomeFragment.this);
        // TabLayout
        tabLayout = view.findViewById(R.id.home_tab_layout);
        // ViewPager
        viewPager2 = view.findViewById(R.id.home_view_pager);
        viewPagerAdapter = new CustomerHomeViewPagerAdapter(getActivity());
        viewPager2.setAdapter(viewPagerAdapter);
        // Search
        model = new ViewModelProvider(requireActivity()).get(CustomerHomeFilterViewModel.class);
        searchView = view.findViewById(R.id.search_group);
        doSearchByKeyWords(searchView);
        //filter
        //location filter
        inputLocation = view.findViewById(R.id.input_location);
        layoutLocation = view.findViewById(R.id.text_input_layout_location);
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_filter, locations);
        inputLocation.setAdapter(locationAdapter);
        inputLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                String selectedLocation = parent.getItemAtPosition(position).toString();
                layoutLocation.setHint(parent.getItemAtPosition(position).toString());
                inputLocation.setText(null);
                doFilterByLocation(selectedLocation);
            }
        });
        //sorting filter
        inputSort = view.findViewById(R.id.input_sort_price);
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_filter, sort);
        inputSort.setAdapter(sortAdapter);
        inputSort.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedCondition = adapterView.getItemAtPosition(i).toString();
                inputSort.setHint(adapterView.getItemAtPosition(i).toString());
                inputSort.setText(null);
                if (selectedCondition.equals("Price low to high") || selectedCondition.equals("Price high to low"))
                    doSortingByPrice(selectedCondition);
                else
                    doSortingByTime(selectedCondition);
            }
        });

        //popup window
        // Get screen width
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View popupView = inflater.inflate(R.layout.customer_tag_filter_popup_window, null);

        PopupWindow popupWindow = new PopupWindow(popupView, screenWidth, WindowManager.LayoutParams.WRAP_CONTENT, true);
        setPopupMenuOptions(popupView, popupWindow);

        //tag filter
        inputTag = view.findViewById(R.id.input_tag);
        inputTag.setOnClickListener(v -> popupWindow.showAsDropDown(v));

        //tab and view pager
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });
    }

    private void doSortingByTime(String selectedCondition) {
        if(selectedCondition != ""){
            model.selectedTimeRange(selectedCondition);
        }
    }

    private void doSortingByPrice(String selectedCondition) {
        if(selectedCondition != ""){
            model.selectPriceRange(selectedCondition);
        }
    }

    private void setPopupMenuOptions(View popupView, PopupWindow popupWindow) {
        // Store status
        Chip chipNotOpened = popupView.findViewById(R.id.chip_group_not_opened);
        Chip chipOpening = popupView.findViewById(R.id.chip_group_opening);

        chipNotOpened.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                chipOpening.setChecked(false);
                storeStatus = STORE_STATUS_NOT_YET_OPEN;
            }
        });
        chipOpening.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                chipNotOpened.setChecked(false);
                storeStatus = STORE_STATUS_OPENING;
            }
        });

        // Group types
        Chip chipInStock = popupView.findViewById(R.id.chip_group_in_stock);
        Chip chipPreOrder = popupView.findViewById(R.id.chip_group_pre_order);

        chipInStock.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                chipPreOrder.setChecked(false);
                groupTypes = GROUP_TYPE_IN_STOCK;
            }
        });
        chipPreOrder.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                chipInStock.setChecked(false);
                groupTypes = GROUP_TYPE_PRE_ORDER;
            }
        });

        // chip confirm button
        Button confirm = popupView.findViewById(R.id.btn_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(storeStatus != -1){
                    Log.d("Test store status", "store status from: "+storeStatus);
                    model.selectedStatus(storeStatus);
                } else if (groupTypes != -1) {
                    Log.d("Test groupTypes", "store groupTypes: "+groupTypes);
                    model.selectedGroupType(groupTypes);
                }
                popupWindow.dismiss();
            }
        });
        Button btnClear = popupView.findViewById(R.id.btn_clear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chipNotOpened.setChecked(false);
                chipOpening.setChecked(false);
                chipInStock.setChecked(false);
                chipPreOrder.setChecked(false);
            }
        });
    }

    private void doFilterByLocation(String selectedLocation) {
        if(selectedLocation != ""){
            model.selectLocation(selectedLocation);
        }
    }

    private void doSearchByKeyWords(SearchView searchView) {
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String str) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String str) {
                    model.enterKeywords(str);
                    return false;
                }
            });
        }
    }
}