package com.csis4495_cmk.webuy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class SellerAddStyleActivity extends AppCompatActivity {

    private Button moreStyle;
    private LinearLayout mainLayout;
    private LinearLayout defaultStyleLayout;

    List<View> views = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_add_style);

        moreStyle = findViewById(R.id.btn_more_add_style);

        mainLayout = findViewById(R.id.linearLayout_main_add_style);

        defaultStyleLayout = findViewById(R.id.linearLayout_default_style_add_style);

        views.add(defaultStyleLayout);

        moreStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View newStyleLayout = LayoutInflater.from(SellerAddStyleActivity.this).inflate(R.layout.seller_edit_style_card_layout, mainLayout, false);
                mainLayout.addView(newStyleLayout, mainLayout.indexOfChild(moreStyle) );
                views.add(newStyleLayout);

                AppCompatButton clearBtn = newStyleLayout.findViewById(R.id.btn_clear_style_list);
                clearBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        views.remove(newStyleLayout);
                        mainLayout.removeView(newStyleLayout);
                    }
                });

            }
        });
    }
}