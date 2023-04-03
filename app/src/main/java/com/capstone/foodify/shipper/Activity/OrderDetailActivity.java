package com.capstone.foodify.shipper.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.capstone.foodify.shipper.R;

public class OrderDetailActivity extends AppCompatActivity {

    Button btn_shipping;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        btn_shipping = findViewById(R.id.btn_shipping);

        btn_shipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q=16.0230,108.2498&mode=l"));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });



    }
}