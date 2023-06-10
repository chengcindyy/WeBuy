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
    private Button btn_next;
    private CardView select_customer, select_seller;
    private int selected_role = 0;
    private final int CUSTOMER = 1;
    private final int SELLER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        select_customer = findViewById(R.id.card_customer);
        select_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_customer.setBackgroundColor(getColor(R.color.cute_orange));
                selected_role = CUSTOMER;
            }
        });

        select_seller = findViewById(R.id.card_seller);
        select_seller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_role = SELLER;
            }
        });

        btn_next = findViewById(R.id.btn_register_to_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (selected_role){
                    case 1:
                        selected_role = CUSTOMER;
                        break;
                    case 2:
                        selected_role = SELLER;
                        break;
                    default:
                        Toast.makeText(RoleSelectionActivity.this, "Please select one of the option", Toast.LENGTH_SHORT).show();
                        break;
                }
                // TODO: Send data
                if(selected_role != 0){
                    Intent intent = new Intent(RoleSelectionActivity.this, RegistrationActivity.class);
                    intent.putExtra("role",selected_role);
                    startActivity(intent);
                }
            }
        });
    }
}