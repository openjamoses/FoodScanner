package com.example.john.foodscanner.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.john.foodscanner.firebase.DeviceToken;
import com.example.john.foodscanner.Phone;
import com.example.john.foodscanner.R;
import com.example.john.foodscanner.firebase.SendTokens;
import com.example.john.foodscanner.utils.AppSharedPreferences;
import com.example.john.foodscanner.utils.MyConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.GET_ACCOUNTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.VIBRATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class DashBoardActivity extends AppCompatActivity {

    private EditText edtName,edtPhone;
    private RelativeLayout layoutVolunteer, layout_share;
    private boolean isVolunteer;
    AppSharedPreferences appSharedPreferences;
    private Context context = this;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private static int SPLASH_TIME_OUT = 2000;
    public static final int RequestPermissionCode = 1;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        //initView();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        layoutVolunteer = (RelativeLayout)findViewById(R.id.layout_volunteer);
        layout_share = (RelativeLayout) findViewById(R.id.layout_share);
        appSharedPreferences = new AppSharedPreferences(getApplicationContext());
        isVolunteer = appSharedPreferences.getBooleanPreferences(MyConstants.PREF_KEY_IS_VOLUNTEER);
        if (isVolunteer){
            layoutVolunteer.setVisibility(View.VISIBLE);
        }else {
            layoutVolunteer.setVisibility(View.GONE);
        }

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_launcher);
        ab.setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.layout_donate_food).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                donationBtnClick();
            }
        });
        layoutVolunteer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volunteerBtnClick();
            }
        });
        findViewById(R.id.layout_map_a_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapLocationBtnClick();
            }
        });

        layout_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, Share_Activity.class));
            }
        });
        sendToken();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_sign_out) {
            appSharedPreferences.signOut();
            finish();
            // After logout redirect user to Loing Activity
            Intent i = new Intent(context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Staring Login Activity
            startActivity(i);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void donationBtnClick(){
        Intent intent = new Intent(getApplicationContext(),EnterDonationDetailsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    private void volunteerBtnClick(){
        Intent intent = new Intent(getApplicationContext(),AvailableDonationsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    private void mapLocationBtnClick(){
        Intent intent = new Intent(getApplicationContext(),MapALocationActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public void sendToken(){
        try {
            String token = new DeviceToken(context).token();
            String imei = Phone.getIMEI(context);
            if (token != null) {
                new SendTokens(context).sendTokenToServer(token, imei);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /*private void doLogin() {
        String name = edtName.getText().toString().trim();
        String mobile = edtPhone.getText().toString().trim();
        if (isValidationSuccess(name,mobile)){
            doLoginTask(name,mobile);
        }else {
            //displayToast();
        }
    }

    private void doLoginTask(String name, String mobile) {
        Intent intent = new Intent(getApplicationContext(),)
    }

    private void displayToast(String toastMsg) {
        Toast.makeText(getApplicationContext(),toastMsg,Toast.LENGTH_SHORT).show();
    }

    private boolean isValidationSuccess(String name, String mobile) {
        boolean isSuccess = true;

        if (name.length() > 3){
            isSuccess = false;
        }else if(!mobile.matches(MyConstants.REG_EXP_MOBILE)){
            isSuccess = false;
        }
        return isSuccess;
    }

    private void initView() {
        edtName = (EditText)findViewById(R.id.edt_name);
        edtPhone = (EditText)findViewById(R.id.edt_phone);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/


    ///// TODO: 10/13/17   permission requests...
    private  boolean checkAndRequestPermissions() {
        int camerapermission = ContextCompat.checkSelfPermission(this, CAMERA);
        int writepermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readpermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionNetworkstate = ContextCompat.checkSelfPermission(this, ACCESS_NETWORK_STATE);
        int permissionVibrate = ContextCompat.checkSelfPermission(this, VIBRATE);

        int getAccountpermission = ContextCompat.checkSelfPermission(this, GET_ACCOUNTS);
        int phonestatepermission = ContextCompat.checkSelfPermission(this, READ_PHONE_STATE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (camerapermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(CAMERA);
        }
        if (writepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionNetworkstate != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(ACCESS_NETWORK_STATE);
        }
        if (readpermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (getAccountpermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(GET_ACCOUNTS);
        }
        if (permissionVibrate != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(VIBRATE);
        }
        if (phonestatepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(READ_PHONE_STATE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(ACCESS_NETWORK_STATE, PackageManager.PERMISSION_GRANTED);

                perms.put(VIBRATE, PackageManager.PERMISSION_GRANTED);
                perms.put(GET_ACCOUNTS, PackageManager.PERMISSION_GRANTED);
                perms.put(READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);

                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(VIBRATE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED
                            && perms.get(READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED ) {
                        Log.d(TAG, "All permissions services permission granted");
                        // process the normal flow
                        //Intent i = new Intent(MainActivity.this, WelcomeActivity.class);
                        //startActivity(i);
                        //finish();
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, CAMERA)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_NETWORK_STATE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, VIBRATE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, GET_ACCOUNTS)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE) ) {
                            showDialogOK("Service Permissions are required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    finish();
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            explain("You need to give some mandatory permissions to continue. Do you want to go to app settings?");
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }
    private void explain(String msg){
        final android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(this);
        dialog.setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        //  permissionsclass.requestPermission(type,code);
                        startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.exampledemo.parsaniahardik.marshmallowpermission")));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                });
        dialog.setCancelable(false);
        dialog.show();

    }
    private void requestPermission() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        ActivityCompat.requestPermissions(DashBoardActivity.this, new String[]
                {
                        CAMERA,
                        READ_PHONE_STATE,
                        VIBRATE,
                        ACCESS_NETWORK_STATE,
                        WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE,
                        GET_ACCOUNTS
                }, RequestPermissionCode);

    }

}
