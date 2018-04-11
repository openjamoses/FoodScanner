package com.example.john.foodscanner.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.john.foodscanner.Phone;
import com.example.john.foodscanner.R;
import com.example.john.foodscanner.firebase.SendNotification;

/**
 * Created by john on 2/18/18.
 */

public class Share_Activity extends AppCompatActivity {
    private Button btn_chose, btn_share;
    private EditText edit_name, edit_title, edit_body;
    private Context context = this;
    private ImageView imageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        edit_name = (EditText) findViewById(R.id.edt_name);
        edit_title = (EditText) findViewById(R.id.edt_title);
        edit_body = (EditText) findViewById(R.id.edt_body);
        btn_chose = (Button) findViewById(R.id.btn_chose);
        btn_share = (Button) findViewById(R.id.btn_share);
        imageView = (ImageView) findViewById(R.id.imageView);

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edit_name.getText().toString().trim();
                String title = edit_title.getText().toString().trim();
                String body = edit_body.getText().toString().trim();

                String imei = Phone.getIMEI(context);

                String combine = "content:"+name+":"+title+":"+imei;
                new SendNotification(context).sendMultiplePush("share_all",body+"/"+ combine, "", imei);
                Toast.makeText(context, "Your Messages has been Shared..!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        btn_chose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });
    }

    private void openCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            Bitmap bp = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bp);
        }
    }
}
