package com.example.john.foodscanner.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.john.foodscanner.R;

/**
 * Created by john on 4/19/18.
 */

public class MessageActivity extends AppCompatActivity {
    private TextView titleText,txt_thanks_msg;
    private String title, message;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_layout);

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        }catch (Exception e){
            e.printStackTrace();
        }
        title = getIntent().getStringExtra("title");
        message = getIntent().getStringExtra("message");
        titleText = (TextView) findViewById(R.id.titleText);
        txt_thanks_msg = (TextView) findViewById(R.id.txt_thanks_msg);

        titleText.setText(title);
        txt_thanks_msg.setText(message);

    }
}
