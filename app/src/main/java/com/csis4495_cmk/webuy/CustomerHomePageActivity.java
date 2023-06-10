package com.csis4495_cmk.webuy;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.csis4495_cmk.webuy.adapters.HomeViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class CustomerHomePageActivity extends AppCompatActivity {

    private final String[] locations = new String[]{"All", "Vancouver", "W. Vancouver", "N. Vancouver",
            "Burnaby", "Richmond", "New West", "Coquitlam", "Surrey", "Langley", "Delta",
            "Port Moody", "White Rock", "Maple Ridge"};
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    HomeViewPagerAdapter viewPagerAdapter;
    BottomNavigationView bottomNavigationView;
    AutoCompleteTextView input_location;
    TextInputLayout layout_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home_page);

        tabLayout = findViewById(R.id.home_tab_layout);
        viewPager2 = findViewById(R.id.home_view_pager);
        viewPagerAdapter = new HomeViewPagerAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);
        input_location = findViewById(R.id.input_location);
        layout_location = findViewById(R.id.text_input_layout_location);

        //filter
        //location filter
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, R.layout.list_location, locations);
        input_location.setAdapter(locationAdapter);
        input_location.setOnItemClickListener((parent, view, position, id) -> {
            //Toast.makeText(this, parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
            layout_location.setHint(parent.getItemAtPosition(position).toString());
            input_location.setText(null);
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

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        buttonNavViewFunction(bottomNavigationView);
    }

    private void buttonNavViewFunction(BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.customer_home:

                        return true;
                    case R.id.customer_profile:
                        startActivity(new Intent(CustomerHomePageActivity.this, CustomerProfileActivity.class));
                        return true;
                    case R.id.customer_cart:

                        return true;
                    case R.id.customer_settings:
                        return true;
                }
                return false;
            }
        });

    }
}