package com.example.john.foodscanner.activities;

import android.app.ProgressDialog;
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
import com.example.john.foodscanner.utils.AppSharedPreferences;
import com.example.john.foodscanner.utils.MyConstants;

import net.rimoto.intlphoneinput.IntlPhoneInput;

/**
 * Created by john on 2/18/18.
 */

public class Share_Activity extends AppCompatActivity {
    private Button btn_chose, btn_share;
    private EditText edit_name, edit_title, edit_body;
    private Context context = this;
    private ImageView imageView;
    private IntlPhoneInput my_phone_input;
    AppSharedPreferences appSharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        edit_name = (EditText) findViewById(R.id.edt_name);
        edit_title = (EditText) findViewById(R.id.edt_title);
        my_phone_input = (IntlPhoneInput) findViewById(R.id.my_phone_input);
        edit_body = (EditText) findViewById(R.id.edt_body);
        btn_chose = (Button) findViewById(R.id.btn_chose);
        btn_share = (Button) findViewById(R.id.btn_share);
        imageView = (ImageView) findViewById(R.id.imageView);
        appSharedPreferences = new AppSharedPreferences(getApplicationContext());
        try{
            edit_name.setText(appSharedPreferences.getStringPreferences(MyConstants.PREF_KEY_NAME));
            edit_name.setEnabled(false);
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            my_phone_input.setNumber(appSharedPreferences.getStringPreferences(MyConstants.PREF_KEY_MOBILE));
        }catch (Exception e){
            e.printStackTrace();
        }
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = appSharedPreferences.getStringPreferences(MyConstants.PREF_KEY_NAME);
                String title = edit_title.getText().toString().trim();
                String body = edit_body.getText().toString().trim();
                String imei = Phone.getIMEI(context);
                String combine = "content:"+name+":"+title+":"+imei;

                ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("sharing....");

                if (my_phone_input.isValid()) {
                    if (!title.equals("") && !body.equals("")) {
                        progressDialog.show();
                        new SendNotification(context).sendMultiplePush(name+" - "+appSharedPreferences.getStringPreferences(MyConstants.PREF_KEY_MOBILE), body , "", imei, progressDialog);
                        Toast.makeText(context, "Your Messages has been Shared..!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(context, "Invalid inputs..!", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, "Invalid Phone ."+my_phone_input.getNumber(), Toast.LENGTH_SHORT).show();
                }
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
