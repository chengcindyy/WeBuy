package com.csis4495_cmk.webuy.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.adapters.CustomerHomeViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;

public class CustomerHomeFragment extends Fragment {

    private final String[] locations = new String[]{"All", "Vancouver", "W. Vancouver", "N. Vancouver",
            "Burnaby", "Richmond", "New West", "Coquitlam", "Surrey", "Langley", "Delta",
            "Port Moody", "White Rock", "Maple Ridge"};

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    CustomerHomeViewPagerAdapter viewPagerAdapter;
    BottomNavigationView bottomNavigationView;
    AutoCompleteTextView input_location;
    TextInputLayout layout_location;
    private NavController navController;

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

        tabLayout = view.findViewById(R.id.home_tab_layout);
        viewPager2 = view.findViewById(R.id.home_view_pager);
        viewPagerAdapter = new CustomerHomeViewPagerAdapter(getActivity());
        viewPager2.setAdapter(viewPagerAdapter);
        input_location = view.findViewById(R.id.input_location);
        layout_location = view.findViewById(R.id.text_input_layout_location);

        //filter
        //location filter
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_location, locations);
        input_location.setAdapter(locationAdapter);
        input_location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                layout_location.setHint(parent.getItemAtPosition(position).toString());
                input_location.setText(null);
            }
        });
        //sort filter

        //tag filter


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


}