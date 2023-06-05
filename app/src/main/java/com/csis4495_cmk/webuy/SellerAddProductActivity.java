package com.csis4495_cmk.webuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SellerAddProductActivity extends AppCompatActivity {

    private Button btn_add_style_new_product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_add_product);

        btn_add_style_new_product = findViewById(R.id.btn_add_style_new_product);

        btn_add_style_new_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerAddProductActivity.this, SellerAddStyleActivity.class));
            }
        });
    }
}