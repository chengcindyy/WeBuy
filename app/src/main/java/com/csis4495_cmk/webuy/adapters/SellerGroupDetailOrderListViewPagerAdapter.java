package com.csis4495_cmk.webuy.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.csis4495_cmk.webuy.fragments.SellerGroupClosedFragment;
import com.csis4495_cmk.webuy.fragments.SellerGroupClosingSoonFragment;
import com.csis4495_cmk.webuy.fragments.SellerGroupFulfilledFragment;
import com.csis4495_cmk.webuy.fragments.SellerGroupNotFulfilledFragment;
import com.csis4495_cmk.webuy.fragments.SellerGroupNotYetOpenedFragment;
import com.csis4495_cmk.webuy.fragments.SellerGroupOpeningFragment;
import com.csis4495_cmk.webuy.fragments.SellerGroupOrderFragment;

public class SellerGroupDetailOrderListViewPagerAdapter extends FragmentStateAdapter {

    public SellerGroupDetailOrderListViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }


    public SellerGroupDetailOrderListViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new SellerGroupOrderFragment();

            case 1:
                return new SellerGroupFulfilledFragment();

            case 2:
                return new SellerGroupNotFulfilledFragment();

            default:
                return new SellerGroupOrderFragment();

        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
