package com.csis4495_cmk.webuy;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.csis4495_cmk.webuy.fragments.CustomerHomePageFoodFragment;
import com.csis4495_cmk.webuy.fragments.CustomerHomePageNewFragment;

public class HomeViewPagerAdapter extends FragmentStateAdapter {
    public HomeViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new CustomerHomePageNewFragment();
            case 1:
                return new CustomerHomePageFoodFragment();
            default:
                return new CustomerHomePageNewFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5; //tabs amount
    }
}
