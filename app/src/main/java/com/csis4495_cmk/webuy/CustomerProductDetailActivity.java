package com.csis4495_cmk.webuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

public class CustomerProductDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_product_detail);

        checkUserPermission();

        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 111);


    }

    private void checkUserPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 111);
            Toast.makeText(this,"permission asked",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,"add pic",Toast.LENGTH_SHORT).show();

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 111) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"permission approved, add pic",Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this,grantResults.length + "cm",Toast.LENGTH_SHORT).show();
                Toast.makeText(this,grantResults[0] + "?",Toast.LENGTH_SHORT).show();

                Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
            }
        }
    }
}