package com.csis4495_cmk.webuy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class GroupsDetailActivity extends AppCompatActivity {

    private Spinner filter_one, filter_two;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_detail);

        // TODO: filter adapter
        FilterAdapterHelper();
    }

    private void FilterAdapterHelper() {

        filter_one = findViewById(R.id.spi_filter_one);
        filter_two = findViewById(R.id.spi_filter_two);

        ArrayAdapter<CharSequence> filter_adapter_one = ArrayAdapter.createFromResource(this,
                R.array.arr_filter_one, android.R.layout.simple_spinner_item);

        ArrayAdapter<CharSequence> filter_adapter_two = ArrayAdapter.createFromResource(this,
                R.array.arr_filter_two, android.R.layout.simple_spinner_item);

        filter_adapter_one.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filter_adapter_two.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        filter_one.setAdapter(filter_adapter_one);
        filter_one.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        filter_two.setAdapter(filter_adapter_two);
        filter_two.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
}