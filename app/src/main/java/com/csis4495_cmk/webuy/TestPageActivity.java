package com.csis4495_cmk.webuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TestPageActivity extends AppCompatActivity {

    private Button btn_groups_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_page);

        btn_groups_detail = findViewById(R.id.btn_groups_detail);
        btn_groups_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TestPageActivity.this, GroupsDetailActivity.class));
            }
        });
    }
}