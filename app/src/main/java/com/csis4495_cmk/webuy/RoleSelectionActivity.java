package com.csis4495_cmk.webuy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class RoleSelectionActivity extends AppCompatActivity {

    private static final String TAG = RoleSelectionActivity.class.getSimpleName();
    private Button btnNext;
    private CardView selectCustomer, select_seller;
    private int selectedRole = 0;
    private final int CUSTOMER = 1;
    private final int SELLER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        selectCustomer = findViewById(R.id.card_customer);
        selectCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectCustomer.setBackgroundColor(getColor(R.color.cute_orange));
                selectedRole = CUSTOMER;
            }
        });

        select_seller = findViewById(R.id.card_seller);
        select_seller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedRole = SELLER;
            }
        });

        btnNext = findViewById(R.id.btn_register_to_next);
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
                        Toast.makeText(RoleSelectionActivity.this, "Please select one of the option", Toast.LENGTH_SHORT).show();
                        break;
                }
                // TODO: Send data
                if(selectedRole != 0){
                    Intent intent = new Intent(RoleSelectionActivity.this, RegistrationActivity.class);
                    intent.putExtra("role",selectedRole);
                    startActivity(intent);
                }
            }
        });
    }
}