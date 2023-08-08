package com.csis4495_cmk.webuy.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csis4495_cmk.webuy.R;

public class CustomerOrderReceivedFragment extends Fragment {

    private TextView tvCountdown;
    private int countdownSeconds = 5;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_order_received, container, false);
        tvCountdown = view.findViewById(R.id.tvCountdown);

        // Start the countdown
        startCountdown();

        return view;
    }

    private void startCountdown() {
        new CountDownTimer(countdownSeconds * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                tvCountdown.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                // Redirect to other pages using Navigation component
                NavController navController = NavHostFragment.findNavController(CustomerOrderReceivedFragment.this);
                navController.navigate(R.id.action_customerOrderReceivedFragment_to_customerHomeGroupsFragment);
                // Close the current fragment
                //will cause loop
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        navController.popBackStack(); // Close the current fragment
//                    }
//                }, 2000);
            }
        }.start();
    }

}