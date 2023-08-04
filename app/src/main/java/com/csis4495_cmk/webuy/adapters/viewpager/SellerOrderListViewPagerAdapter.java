package com.csis4495_cmk.webuy.adapters.viewpager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.csis4495_cmk.webuy.fragments.SellerGroupClosedFragment;
import com.csis4495_cmk.webuy.fragments.SellerGroupClosingSoonFragment;
import com.csis4495_cmk.webuy.fragments.SellerGroupNotYetOpenedFragment;
import com.csis4495_cmk.webuy.fragments.SellerGroupOpeningFragment;
import com.csis4495_cmk.webuy.fragments.SellerOrderPendingFragment;

public class SellerOrderListViewPagerAdapter extends FragmentStateAdapter {

    public SellerOrderListViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public SellerOrderListViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public SellerOrderListViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new SellerOrderPendingFragment();

            case 1:
                return new SellerOrderPendingFragment();

            case 2:
                return new SellerOrderPendingFragment();

            case 3:
                return new SellerOrderPendingFragment();

            case 4:
                return new SellerOrderPendingFragment();

            case 5:
                return new SellerOrderPendingFragment();

            default:
                return new SellerOrderPendingFragment();

        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}
