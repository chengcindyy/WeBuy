package com.csis4495_cmk.webuy.adapters.viewpager;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.csis4495_cmk.webuy.fragments.CustomerCartItemsFragment;
import com.csis4495_cmk.webuy.fragments.CustomerWishListItemsFragment;
import com.csis4495_cmk.webuy.models.Wishlist;
import com.csis4495_cmk.webuy.viewmodels.CustomerWishlistViewModel;

import java.util.List;

public class CustomerCartViewPagerAdapter extends FragmentStateAdapter {

    private List<Wishlist> wishlist;
    private CustomerWishlistViewModel wishlistViewModel;

    public CustomerCartViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, CustomerWishlistViewModel wishlistViewModel, List<Wishlist> wishlist) {
        super(fragmentActivity);
        this.wishlistViewModel = wishlistViewModel;
        this.wishlist = wishlist;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        Log.d("custCartTest","Tab Position: "+ position);
        switch (position) {
            case 0:
                //return CustomerCartItemsFragment.newInstance();
                return new CustomerCartItemsFragment();
            case 1:
                //return CustomerCartItemsFragment.newInstance();
                return new CustomerWishListItemsFragment();
            default:
                return new CustomerCartItemsFragment();
        }
    }

    public void updateData(List<Wishlist> newWishlist) {
        this.wishlist = newWishlist;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
