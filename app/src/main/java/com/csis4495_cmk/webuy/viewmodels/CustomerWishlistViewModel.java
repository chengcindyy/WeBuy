package com.csis4495_cmk.webuy.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.csis4495_cmk.webuy.models.Wishlist;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Iterator;

public class CustomerWishlistViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<Wishlist>> wishlistObject = new MutableLiveData<>();

    public CustomerWishlistViewModel() {
        // Initialize the ViewModel from Firebase
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference("Customer");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference wishlistRef = customerRef.child(userId).child("wishlist");

        wishlistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Wishlist> currentWishlist = new ArrayList<>();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()) {
                    Wishlist wishlistItem = itemSnapshot.getValue(Wishlist.class);
                    currentWishlist.add(wishlistItem);
                }
                wishlistObject.setValue(currentWishlist);
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
    }

    public void addToWishlist(Wishlist wishlist, String userId) {
        ArrayList<Wishlist> currentWishlist = wishlistObject.getValue();
        if (currentWishlist == null) {
            currentWishlist = new ArrayList<>();
        }
        currentWishlist.add(wishlist);
        wishlistObject.setValue(currentWishlist);
        Log.d("Test viewModel", "wishlist size: "+ currentWishlist.size());

        // Update Firebase
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference("Customer");
        DatabaseReference wishlistRef = customerRef.child(userId).child("wishlist");
        wishlistRef.child(wishlist.getGroupId()).setValue(wishlist)
                .addOnCompleteListener(task -> Log.d("Test wishlist", "Saved to firebase"))
                .addOnFailureListener(e -> Log.d("Test wishlist", "Cannot save to firebase because " + e.getMessage()));
    }

    public void removeFromWishlist(Wishlist wishlist, String userId) {
        ArrayList<Wishlist> currentWishlist = wishlistObject.getValue();
        if (currentWishlist != null) {
            for (Iterator<Wishlist> iterator = currentWishlist.iterator(); iterator.hasNext();) {
                Wishlist currentWishlistItem = iterator.next();
                if (currentWishlistItem.getGroupId().equals(wishlist.getGroupId())) {
                    iterator.remove();
                    break;
                }
            }
            wishlistObject.setValue(currentWishlist);
            Log.d("Test viewModel", "wishlist size: "+ currentWishlist.size());

            // Update Firebase
            DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference("Customer");
            DatabaseReference wishlistRef = customerRef.child(userId).child("wishlist");
            wishlistRef.child(wishlist.getGroupId()).removeValue()
                    .addOnCompleteListener(task -> Log.d("Test wishlist", "Removed from firebase"))
                    .addOnFailureListener(e -> Log.d("Test wishlist", "Cannot remove from firebase because " + e.getMessage()));
        }
    }

    public LiveData<ArrayList<Wishlist>> getWishlistObject() {
        return wishlistObject;
    }

}

