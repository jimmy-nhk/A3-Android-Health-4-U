package com.example.vendorapp.activity;

import com.example.vendorapp.R;
import com.example.vendorapp.model.Order;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class OrderDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        Order order = getIntent().getParcelableExtra("order");

        TextView orderText = findViewById(R.id.orderText);
        orderText.setText(order.toString());
    }
}