package com.csis4495_cmk.webuy.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.csis4495_cmk.webuy.fragments.SellerGroupClosedFragment;
import com.csis4495_cmk.webuy.fragments.SellerGroupClosingSoonFragment;
import com.csis4495_cmk.webuy.fragments.SellerGroupNotYetOpenedFragment;
import com.csis4495_cmk.webuy.fragments.SellerGroupOpeningFragment;

public class SellerGroupListViewPagerAdapter extends FragmentStateAdapter {
    public SellerGroupListViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new SellerGroupOpeningFragment();

            case 1:
                return new SellerGroupClosingSoonFragment();

            case 2:
                return new SellerGroupNotYetOpenedFragment();

            case 3:
                return new SellerGroupClosedFragment();

            default:
                return new SellerGroupOpeningFragment();

        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
