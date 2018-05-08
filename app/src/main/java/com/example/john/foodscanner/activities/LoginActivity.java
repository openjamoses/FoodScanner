package com.example.john.foodscanner.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.john.foodscanner.R;
import com.example.john.foodscanner.firebase.DeviceToken;
import com.example.john.foodscanner.utils.AppSharedPreferences;
import com.example.john.foodscanner.utils.FetchData;
import com.example.john.foodscanner.utils.MyConstants;
import com.example.john.foodscanner.utils.ServiceHandler;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.GET_ACCOUNTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.VIBRATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.example.john.foodscanner.Config.DEVICE_ID;
import static com.example.john.foodscanner.Config.DEVICE_TOKEN;
import static com.example.john.foodscanner.Config.HOST_URL;
import static com.example.john.foodscanner.Config.IS_VOLUNTEER;
import static com.example.john.foodscanner.Config.OPERATION_USER;
import static com.example.john.foodscanner.Config.URL_GET_USER;
import static com.example.john.foodscanner.Config.URL_SAVE_USER;
import static com.example.john.foodscanner.Config.USERNAME;
import static com.example.john.foodscanner.Config.USER_PHONE;

public class LoginActivity extends AppCompatActivity {

    private EditText edtName;
    private boolean isVolunteer;
    private AppCompatCheckBox chkIsVolunteer;
    private String name,mobile;
    AppSharedPreferences appSharedPreferences;
    private HashMap<String ,String> postParams;
    private String deviceId;
    String requestParams;
    private final static String TAG = "LoginActivity";
    private Context context = this;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private static int SPLASH_TIME_OUT = 2000;
    public static final int RequestPermissionCode = 1;
    private IntlPhoneInput my_phone_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_launcher);
        ab.setDisplayHomeAsUpEnabled(true);

        deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        appSharedPreferences = new AppSharedPreferences(getApplicationContext());
        appSharedPreferences.saveStringPreferences(MyConstants.PREF_KEY_DEVICE_ID,deviceId);
        my_phone_input = (IntlPhoneInput) findViewById(R.id.my_phone_input);

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });

        chkIsVolunteer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    isVolunteer = true;
                }else{
                    isVolunteer = true;
                }
            }
        });
        checkAndRequestPermissions();
    }

    private void doLogin() {
        name = edtName.getText().toString().trim();
        mobile = my_phone_input.getNumber();
        if (my_phone_input.isValid()){
            if (!name.equals("")){
                doLoginTask2();
                savePreferences();
            }
        }else {
            Toast.makeText(context,"Invalid phone number: "+my_phone_input.getNumber(),Toast.LENGTH_SHORT).show();
        }
    }

    private void savePreferences() {
        appSharedPreferences.saveStringPreferences(MyConstants.PREF_KEY_NAME,name);
        appSharedPreferences.saveStringPreferences(MyConstants.PREF_KEY_MOBILE,mobile);
        appSharedPreferences.saveBooleanPreferences(MyConstants.PREF_KEY_IS_VOLUNTEER, isVolunteer);
        appSharedPreferences.saveBooleanPreferences(MyConstants.PREF_KEY_IS_LOGGEDIN, true);
    }

    private void doLoginTask() {

        JSONObject object = new JSONObject();
        try {
            object.put("username", name);
            object.put("mobileNumber", mobile);
            object.put("isVolunteer", String.valueOf(isVolunteer));
            object.put("deviceId", String.valueOf(deviceId));
            object.put("deviceToken", "TestDeviceToken");
            requestParams = object.toString();
            Log.e("login","--->>> "+requestParams);
            //new doLoginAsyncTask().execute();
        } catch (Exception ex) {
            displayToast(getString(R.string.unable_to_connect));
        }
    }

    private void doLoginTask2() {

        JSONObject object = new JSONObject();
        try {
            String token = new DeviceToken(context).token();
            send(name,mobile,String.valueOf(isVolunteer),String.valueOf(deviceId),token);
        } catch (Exception ex) {
            displayToast(getString(R.string.unable_to_connect));
        }
    }

    private void displayToast(String toastMsg) {
        Toast.makeText(getApplicationContext(),toastMsg,Toast.LENGTH_SHORT).show();
    }

    private boolean isValidationSuccess() {
        boolean isSuccess = true;

        if (name.length() < 3){
            displayToast(getString(R.string.valid_name));
            isSuccess = false;
        }else if(!mobile.matches(MyConstants.REG_EXP_MOBILE)){
            displayToast(getString(R.string.valid_mobile));
            //isSuccess = false;
        }
        return isSuccess;
    }

    private void initView() {
        edtName = (EditText)findViewById(R.id.edt_name);
        chkIsVolunteer = (AppCompatCheckBox)findViewById(R.id.chk_is_volunteer);
    }

    /*@Override
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
    /**
     * Async task class to get json by making HTTP call
     * */
    private class doLoginAsyncTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler serviceHandler = new ServiceHandler();


            // Making a request to url and getting response
            //String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
            String sUrl = MyConstants.URL_ROOT+"user/create";

            String jsonStr = serviceHandler.performPostCall(sUrl, requestParams);

            Log.e("Response: ", "--->>> " + jsonStr);

            int i = 1;
            try {
                appSharedPreferences.saveStringPreferences(MyConstants.PREF_KEY_ID, i + "");
                appSharedPreferences.saveBooleanPreferences(MyConstants.PREF_KEY_IS_LOGGEDIN, true);
            }catch (Exception e){
                e.printStackTrace();
            }


            if (jsonStr != null) try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                if (jsonObj != null) {
                    if (!jsonObj.isNull("id")){
                        if (!jsonObj.getString("id").equals("")){
                            appSharedPreferences.saveStringPreferences(MyConstants.PREF_KEY_ID, jsonObj.getString("id"));
                            appSharedPreferences.saveBooleanPreferences(MyConstants.PREF_KEY_IS_LOGGEDIN, true);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            if (!appSharedPreferences.getStringPreferences(MyConstants.PREF_KEY_ID).equals("")) {
                Intent intent = new Intent(getApplicationContext(), DashBoardActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }else{
                displayToast(getString(R.string.unable_to_connect));
            }
        }

    }

    public void send(final String username, final String phone, final String is_volunteer, final String device_id, final String token){
        ProgressDialog pDialog = null;
        try {
            // Showing progress dialog
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
        final ProgressDialog finalPDialog = pDialog;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HOST_URL+URL_SAVE_USER,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG,"Results: "+response);
                        int status = 0;
                        if (response.equals("Success")){
                            status = 1;
                            new FetchData(context).fetch(URL_GET_USER,token,OPERATION_USER);
                        }
                        try{
                            if (finalPDialog.isShowing())
                                finalPDialog.dismiss();
                           // if (!appSharedPreferences.getStringPreferences(MyConstants.PREF_KEY_ID).equals("")) {
                                Intent intent = new Intent(getApplicationContext(), DashBoardActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                           // }else{
                                displayToast(getString(R.string.unable_to_connect));
                           // }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        Log.e(TAG,response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        try {
                            Log.e(TAG, volleyError.getMessage());
                        }catch (Exception e){
                            volleyError.getMessage();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                int status = 1;
                Map<String, String> params = new Hashtable<String, String>();
                //Adding parameters
                params.put(USER_PHONE, phone);
                params.put(USERNAME, username);
                params.put(IS_VOLUNTEER, is_volunteer);
                params.put(DEVICE_ID, device_id);
                params.put(DEVICE_TOKEN, "");
                //returning parameters
                return params;
            }
        };
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        //Adding request to the queue
        requestQueue.add(stringRequest);
    }


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
        ActivityCompat.requestPermissions(LoginActivity.this, new String[]
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
