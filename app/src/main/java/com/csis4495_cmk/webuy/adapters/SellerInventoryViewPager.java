package com.csis4495_cmk.webuy.adapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.csis4495_cmk.webuy.fragments.SellerInventoryFragment;

public class SellerInventoryViewPager extends FragmentStateAdapter {

    public SellerInventoryViewPager(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.d("inventory view pagers","Tab Position: "+ position);
        switch (position){
            case 1:
                return new SellerInventoryFragment("In-stock");
            case 2:
                return new SellerInventoryFragment("Pre-order");
            default:
                return new SellerInventoryFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
