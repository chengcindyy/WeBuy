package com.csis4495_cmk.webuy.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.csis4495_cmk.webuy.activities.MainActivity;
import com.csis4495_cmk.webuy.R;

public class UserRoleSelectionFragment extends Fragment {

    private static final String TAG = "UserRoleSelectionFragment";
    private Button btnNext;
    private CardView selectCustomer, selectSeller;
    private int selectedRole = 0;
    private final int CUSTOMER = 1;
    private final int SELLER = 2;
    private NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_role_selection, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set navigation controller, and if you want to navigate to other fragment can call this to navigate
        navController = NavHostFragment.findNavController(UserRoleSelectionFragment.this);

        selectCustomer = view.findViewById(R.id.card_customer);
        selectCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    selectCustomer.setBackgroundColor(getResources().getColor(R.color.cute_orange, getActivity().getTheme()));
                } else {
                    selectCustomer.setBackgroundColor(getResources().getColor(R.color.cute_orange));
                }
                selectedRole = CUSTOMER;
            }
        });


        selectSeller = view.findViewById(R.id.card_seller);
        selectSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedRole = SELLER;
            }
        });



        btnNext = view.findViewById(R.id.btn_register_to_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (selectedRole){
                    case 1:
                        selectedRole = CUSTOMER;
                        break;
                    case 2:
                        selectedRole = SELLER;
                        break;
                    default:
                        Toast.makeText(getActivity(), "Please select one of the option", Toast.LENGTH_SHORT).show();
                        break;
                }
                // TODO: Send data
                if(selectedRole != 0){
                    navController.navigate(R.id.action_roleSelectionFragment_to_userRegistrationFragment);
                    MainActivity activity = (MainActivity) getActivity();
                    activity.saveData(selectedRole);
                }
            }
        });
    }
}