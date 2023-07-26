package com.csis4495_cmk.webuy.adapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.csis4495_cmk.webuy.fragments.CustomerCartItemsFragment;
import com.csis4495_cmk.webuy.fragments.CustomerHomeGroupsFragment;

public class CustomerCartViewPagerAdapter extends FragmentStateAdapter {


    public CustomerCartViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        Log.d("custCartTest","Tab Position: "+ position);
        switch (position) {
            case 0:
                //return CustomerCartItemsFragment.newInstance();
            case 1:
                //return CustomerCartItemsFragment.newInstance();
            default:
                return new CustomerHomeGroupsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
