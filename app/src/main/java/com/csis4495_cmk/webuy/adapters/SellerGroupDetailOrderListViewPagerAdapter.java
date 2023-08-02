package com.csis4495_cmk.webuy.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.csis4495_cmk.webuy.fragments.SellerGroupAllocatedOrderFragment;
import com.csis4495_cmk.webuy.fragments.SellerGroupCanceledOrderFragment;
import com.csis4495_cmk.webuy.fragments.SellerGroupProcessedOrderFragment;
import com.csis4495_cmk.webuy.fragments.SellerGroupPendingOrderFragment;

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
                return new SellerGroupPendingOrderFragment();

            case 1:
                return new SellerGroupAllocatedOrderFragment();

            case 2:
                return new SellerGroupProcessedOrderFragment();

            case 3:
                return new SellerGroupCanceledOrderFragment();

            default:
                return new SellerGroupPendingOrderFragment();

        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
