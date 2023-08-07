package com.csis4495_cmk.webuy.adapters.viewpager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.csis4495_cmk.webuy.fragments.SellerAllocatedOrderListFragment;
import com.csis4495_cmk.webuy.fragments.SellerCanceledOrderListFragment;
import com.csis4495_cmk.webuy.fragments.SellerPendingOrderListFragment;
import com.csis4495_cmk.webuy.fragments.SellerProcessingOrderListFragment;
import com.csis4495_cmk.webuy.fragments.SellerReceivedOrderListFragment;
import com.csis4495_cmk.webuy.fragments.SellerToAllocateOrderListFragment;

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
                return new SellerPendingOrderListFragment();
            case 1:
                return new SellerToAllocateOrderListFragment();
            case 2:
                return new SellerAllocatedOrderListFragment();
            case 3:
                return new SellerProcessingOrderListFragment();
            case 4:
                return new SellerReceivedOrderListFragment();
            case 5:
                return new SellerCanceledOrderListFragment();

            default:
                return new SellerToAllocateOrderListFragment();

        }
    }

    @Override
    public int getItemCount() {
        return 6;
    }
}
