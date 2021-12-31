package com.example.clientapp.activity;

import com.example.clientapp.R;
import com.example.clientapp.model.Cart;
import com.example.clientapp.model.Order;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class BillingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);
        String date = intent.getStringExtra("date");
        List<Order> orderList = intent.getParcelableArrayListExtra("orderList");
        double price = intent.getDoubleExtra("double", 0);
        boolean isFinished = intent.getBooleanExtra("isFinished" , false);

//        Cart cart = new Cart(id,date,orderList,price,isFinished);
        Cart cart = intent.getParcelableExtra("cart");
        TextView cartText = findViewById(R.id.billingText);
        cartText.setText(cart.toString());
    }
}