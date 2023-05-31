package com.csis4495_cmk.webuy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TestPageActivity extends AppCompatActivity {

    //TODO: Step 1: Declare Button
    private Button btn_groups_detail, btn_customer_homepage, btn_customer_product_detail;

    private Button btn_goSellerHome;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_page);

        //TODO: Step 2: GoToTestActivity(btnName, "btnID", testActvity.class)
        GoToTestActivity(btn_groups_detail, "btn_groups_detail", GroupsDetailActivity.class);
        GoToTestActivity(btn_customer_homepage, "btn_customer_homepage", CustomerHomePageActivity.class);
        GoToTestActivity(btn_customer_product_detail, "btn_customer_product_detail", CustomerProductDetailActivity.class);

        btn_goSellerHome = findViewById(R.id.btn_go_seller_home);

        fakeData("Group", "GroupName");
    }

    public void GoToTestActivity(Button btnClicked, String strBtnId, Class<?> testActivity) {
        int resourceId = this.getResources().
                getIdentifier(strBtnId, "id", this.getPackageName());
        btnClicked = findViewById(resourceId);
        btnClicked.setOnClickListener(v -> {
            startActivity(new Intent(TestPageActivity.this, testActivity));
        });
    }
    public void fakeData(String dbRef, String item) {
        databaseReference = firebaseDatabase.getReference(dbRef);

    }

    public void goSellerHome(View view) {
        startActivity(new Intent(TestPageActivity.this, SellerHomePageActivity.class));

    }
}