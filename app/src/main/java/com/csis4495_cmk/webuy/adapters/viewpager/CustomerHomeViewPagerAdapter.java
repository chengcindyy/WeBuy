package com.csis4495_cmk.webuy.adapters.viewpager;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.csis4495_cmk.webuy.fragments.CustomerHomeGroupsFragment;

public class CustomerHomeViewPagerAdapter extends FragmentStateAdapter {
    public CustomerHomeViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.d("custTest","Tab Position: "+ position);
        switch (position) {
            case 0:
                return new CustomerHomeGroupsFragment();
            case 1:
                return new CustomerHomeGroupsFragment("Food");
            case 2:
                return new CustomerHomeGroupsFragment("Home Appliance");
            case 3:
                return new CustomerHomeGroupsFragment("Activity");
            case 4:
                return new CustomerHomeGroupsFragment("Service");
            case 5:
                return new CustomerHomeGroupsFragment("Other");
            default:
                return new CustomerHomeGroupsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 6; //tabs amount
    }
}
