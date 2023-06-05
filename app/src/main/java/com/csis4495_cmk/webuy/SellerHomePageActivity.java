package com.csis4495_cmk.webuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class SellerHomePageActivity extends AppCompatActivity {

    private Button btn_sellerProducts, btn_sellerProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home_page);

        goTestSeller(btn_sellerProducts, "btn_sellerProducts", SellerProductListActivity.class);
        goTestSeller(btn_sellerProfile, "btn_sellerProfile", SellerUploadProfile.class);

    }

    public void goTestSeller(Button btnClicked, String strBtnId, Class<?> testActivity) {
        int resourceId = this.getResources().
                getIdentifier(strBtnId, "id", this.getPackageName());
        btnClicked = findViewById(resourceId);
        btnClicked.setOnClickListener(v -> {
            startActivity(new Intent(SellerHomePageActivity.this, testActivity));
        });
    }

}