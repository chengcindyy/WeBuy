package com.csis4495_cmk.webuy.adapters.viewpager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.csis4495_cmk.webuy.fragments.SellerGroupAllocatedOrderFragment;
import com.csis4495_cmk.webuy.fragments.SellerGroupCanceledOrderFragment;
import com.csis4495_cmk.webuy.fragments.SellerGroupToAllocateOrderFragment;
import com.csis4495_cmk.webuy.fragments.SellerGroupProcessingOrderFragment;
import com.csis4495_cmk.webuy.fragments.SellerGroupReceivedOrderFragment;
import com.csis4495_cmk.webuy.fragments.SellerGroupToAllocateOrderFragment;

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
                return new SellerGroupToAllocateOrderFragment();

            case 1:
                return new SellerGroupAllocatedOrderFragment();

            case 2:
                return new SellerGroupProcessingOrderFragment();

            case 3:
                return new SellerGroupReceivedOrderFragment();

            case 4:
                return new SellerGroupCanceledOrderFragment();

            default:
                return new SellerGroupToAllocateOrderFragment();

        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
