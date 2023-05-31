package com.csis4495_cmk.webuy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.csis4495_cmk.webuy.adapters.SellerProductListRecyclerAdapter;

public class SellerProductListActivity extends AppCompatActivity {

    private Button btn_add_seller_product_list;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    SellerProductListRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_product_list);

        btn_add_seller_product_list = findViewById(R.id.btn_add_seller_product_list);

        recyclerView = findViewById(R.id.recyclerView_seller_product_list);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SellerProductListRecyclerAdapter();
        recyclerView.setAdapter(adapter);


        btn_add_seller_product_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerProductListActivity.this, SellerAddProductActivity.class));
            }
        });
    }
}