package com.csis4495_cmk.webuy.adapters.viewpager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.csis4495_cmk.webuy.fragments.SellerGroupAllocatedOrderFragment;
import com.csis4495_cmk.webuy.fragments.SellerGroupCanceledOrderFragment;
import com.csis4495_cmk.webuy.fragments.SellerPendingOrderListFragment;
import com.csis4495_cmk.webuy.fragments.SellerGroupProcessingOrderFragment;
import com.csis4495_cmk.webuy.fragments.SellerGroupReceivedOrderFragment;
import com.csis4495_cmk.webuy.fragments.SellerGroupToAllocateOrderFragment;

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
                return new SellerGroupToAllocateOrderFragment();

            case 2:
                return new SellerGroupAllocatedOrderFragment();

            case 3:
                return new SellerGroupProcessingOrderFragment();

            case 4:
                return new SellerGroupReceivedOrderFragment();

            case 5:
                return new SellerGroupCanceledOrderFragment();

            default:
                return new SellerPendingOrderListFragment();

        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}
