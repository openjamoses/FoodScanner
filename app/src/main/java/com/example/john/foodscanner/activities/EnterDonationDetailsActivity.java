package com.example.john.foodscanner.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.john.foodscanner.R;
import com.example.john.foodscanner.utils.AppSharedPreferences;
import com.example.john.foodscanner.utils.FetchData;
import com.example.john.foodscanner.utils.MyConstants;
import com.example.john.foodscanner.utils.ServiceHandler;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import static com.example.john.foodscanner.Config.DEVICE_ID;
import static com.example.john.foodscanner.Config.DEVICE_TOKEN;
import static com.example.john.foodscanner.Config.DONATION_ADDRESS;
import static com.example.john.foodscanner.Config.DONATION_FOOD;
import static com.example.john.foodscanner.Config.DONATION_PHONE;
import static com.example.john.foodscanner.Config.DONATION_QUANTITY;
import static com.example.john.foodscanner.Config.DONATION_STATUS;
import static com.example.john.foodscanner.Config.HOST_URL;
import static com.example.john.foodscanner.Config.IS_VOLUNTEER;
import static com.example.john.foodscanner.Config.LATITUTE;
import static com.example.john.foodscanner.Config.LONGITUTE;
import static com.example.john.foodscanner.Config.OPERATION_USER;
import static com.example.john.foodscanner.Config.URL_GET_USER;
import static com.example.john.foodscanner.Config.URL_SAVE_DONATION;
import static com.example.john.foodscanner.Config.URL_SAVE_USER;
import static com.example.john.foodscanner.Config.USERNAME;
import static com.example.john.foodscanner.Config.USER_PHONE;

public class EnterDonationDetailsActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {
    private Context context = this;

    private GoogleApiClient mGoogleApiClient;
    private int PLACE_PICKER_REQUEST = 1;
    private RadioButton radioBreakfast, radioLunch, radioDinner;
    private EditText edtQuantity, edtAddress;
    private String foodType = "dinner", quantity = "", address = "", lat = "20.00", lng = "120.19276", userid = "";
    //private HashMap<String ,String> postParams;
    private static final String TAG = "EnterDonationDetailsActivity";
    private String requestParams;
    AppSharedPreferences appSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donations_details_entry);
        //initView();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appSharedPreferences = new AppSharedPreferences(getApplicationContext());
        userid = appSharedPreferences.getStringPreferences(MyConstants.PREF_KEY_ID);

        radioBreakfast = (RadioButton) findViewById(R.id.radio_breakfast);
        radioLunch = (RadioButton) findViewById(R.id.radio_lunch);
        radioDinner = (RadioButton) findViewById(R.id.radio_dinner);

        edtQuantity = (EditText) findViewById(R.id.edt_quantity);
        edtAddress = (EditText) findViewById(R.id.edt_address);


        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.mipma);
        ab.setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.txt_select_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPlacePicker();
                // displayMap();
            }
        });

        radioBreakfast.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    foodType = "breakfast";
                }
            }
        });
        radioLunch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    foodType = "lunch";
                }
            }
        });
        radioDinner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    foodType = "dinner";
                }
            }
        });

        findViewById(R.id.btn_donate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitDonation();
            }
        });

    }

    private void submitDonation() {
        quantity = edtQuantity.getText().toString().trim();
        address = edtAddress.getText().toString().trim();
        if (isValidationSuccess()) {
            //TODO::doSubmitDonationTask();
            submit();
        }
    }

    private void doSubmitDonationTask() {
        /*postParams = new HashMap<>();
        postParams.put("userid",userid);
        postParams.put("foodtype",foodType);
        postParams.put("quantity",quantity);
        postParams.put("latitude",lat);
        postParams.put("longitude",lng);
        postParams.put("address", address);*/
        /*{ "donorMobile": "9944775657", "donationStatus": "open",
                "foodType":"lunch", "quantity":"10",
                "latitude":"102.30", "longitude":"233.dd", "address":"Some text" }*/
        JSONObject object = new JSONObject();
        try {
            //object.put("consumerName", appSharedPreferences.getStringPreferences(MyConstants.PREF_KEY_NAME));
            //object.put("isVolunteer", String.valueOf(appSharedPreferences.getStringPreferences(MyConstants.PREF_KEY_IS_VOLUNTEER)));
            //object.put("deviceId", String.valueOf(appSharedPreferences.getStringPreferences(MyConstants.PREF_KEY_DEVICE_ID)));
            object.put("address", address);
            object.put("donorMobile", appSharedPreferences.getStringPreferences(MyConstants.PREF_KEY_MOBILE));
            object.put("foodType", foodType);
            object.put("longitude", lng);
            object.put("quantity", quantity);
            object.put("latitude", lat);
            object.put("donationStatus", "open");
            //object.put("deviceToken", "TestDeviceToken");
            requestParams = object.toString();
            Log.e("Params", "--->>> " + requestParams);
            new doSubmitDonationAsyncTask().execute();


        } catch (Exception ex) {
            displayToast(getString(R.string.unable_to_connect));
        }
    }

    private void submit() {
        try {
            Log.e("Params", "--->>> " + requestParams);
            send(address, appSharedPreferences.getStringPreferences(MyConstants.PREF_KEY_MOBILE), foodType, lat, lng, quantity, "open");
        } catch (Exception ex) {
            displayToast(getString(R.string.unable_to_connect));
        }
    }

    public void send(final String address, final String phone, final String food_type, final String latitude, final String longitude, final String quantity, final String donationStatus) {
        ProgressDialog pDialog = null;
        try {

            pDialog = new ProgressDialog(EnterDonationDetailsActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final ProgressDialog finalPDialog = pDialog;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HOST_URL + URL_SAVE_DONATION,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "Results: " + response);
                        try {
                            if (finalPDialog.isShowing())
                                finalPDialog.dismiss();
                            Intent intent = new Intent(getApplicationContext(), ThankYouActivity.class);
                            startActivity(intent);
                            intent.putExtra(MyConstants.FROM_ACTIVITY, MyConstants.KEY_DONOR);
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                            finish();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG, response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        try {
                            Log.e(TAG, volleyError.getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                int status = 1;
                Map<String, String> params = new Hashtable<String, String>();
                //Adding parameters
                params.put(DONATION_ADDRESS, address);
                params.put(DONATION_PHONE, phone);
                params.put(DONATION_FOOD, food_type);
                params.put(DONATION_QUANTITY, quantity);
                params.put(LATITUTE, latitude);
                params.put(LONGITUTE, longitude);
                params.put(DONATION_STATUS, donationStatus);
                //returning parameters
                return params;
            }
        };
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        //Adding request to the queue
        requestQueue.add(stringRequest);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            //mAdapter.setGoogleApiClient( null );
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    private void displayMap() {
        startActivity(new Intent(context, MapsActivity.class));
    }

    private void displayPlacePicker() {
        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
            Log.e("Map Error", "Googlemap not connected");
        } else {
            //  return;

            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

            try {
                startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                Log.d("PlacesAPI Demo", "GooglePlayServicesRepairableException thrown");
            } catch (GooglePlayServicesNotAvailableException e) {
                Log.d("PlacesAPI Demo", "GooglePlayServicesNotAvailableException thrown");
            }
        }
    }

    private void guessCurrentPlace() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback( new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult( PlaceLikelihoodBuffer likelyPlaces ) {

                PlaceLikelihood placeLikelihood = likelyPlaces.get( 0 );
                String content = "";
                if( placeLikelihood != null && placeLikelihood.getPlace() != null && !TextUtils.isEmpty( placeLikelihood.getPlace().getName() ) )
                    content = "Most likely place: " + placeLikelihood.getPlace().getName() + "\n";
                if( placeLikelihood != null )
                    content += "Percent change of being there: " + (int) ( placeLikelihood.getLikelihood() * 100 ) + "%";
                //mTextView.setText( content );

                likelyPlaces.release();
            }
        });
    }

    /**
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //checkPermissionOnActivityResult(requestCode, resultCode, data);

        String content = "";
        //if( !TextUtils.isEmpty(place.getName()) ) {
        //    content += "Name: " + place.getName() + "\n";
        //}


        if (resultCode == RESULT_OK) {
            if (requestCode == PLACE_PICKER_REQUEST ){
                Place place = PlacePicker.getPlace(this, data);

                if (place != null){
                    if( !TextUtils.isEmpty(place.getAddress()) ) {
                        content += place.getAddress();
                    }
                    String placeName = String.format("Place: %s", place.getName());
                    lat = String.valueOf(place.getLatLng().latitude);
                    lng  = String.valueOf(place.getLatLng().longitude);

                    if (content != null && !content.equals("")){
                        edtAddress.setText(content);
                    }else {
                        edtAddress.setText("");
                    }
                }else {
                 Log.e("Map Failure..", "Place is Empty..");
                }

            }
        }
    }

    **/
   protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        if( requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK ) {
            displayPlace( PlacePicker.getPlace( data, this ) );
        }
    }


    private void displayPlace( Place place ) {
        if( place == null )
            return;

        //DisplayLog.displayNormalLog("MainActivity","Place",place);

        String content = "";
        //if( !TextUtils.isEmpty(place.getName()) ) {
        //    content += "Name: " + place.getName() + "\n";
        //}
        if( !TextUtils.isEmpty(place.getAddress()) ) {
            content += place.getAddress();
        }
        //if( !TextUtils.isEmpty( place.getPhoneNumber() ) ) {
        //    content += "Phone: " + place.getPhoneNumber();
        //}

        if( !TextUtils.isEmpty( String.valueOf(place.getLatLng()) ) ) {
            Log.e("MainActivity", "Latlong: "+String.valueOf(place.getLatLng()));
            LatLng mLatLng = place.getLatLng();
            //isplayLog.displayNormalLog("MainActivity", "Latlong Points", mLatLng.latitude+" - "+mLatLng.longitude);
            lat = String.valueOf(mLatLng.latitude);
            lng = String.valueOf(mLatLng.longitude);

        }
        Log.e("Main ac","address: "+content);

        if (content != null && !content.equals("")){
            edtAddress.setText(content);
        }else {
            edtAddress.setText("");
        }
        //mTextView.setText( content );
       // medtAddress.setText(content);
    }

    private boolean isValidationSuccess(){
        boolean isSuccess = true;
        if (quantity.equals("")){
            displayToast("Please enter the quantity");
            isSuccess = false;
        }else if (address.equals("") || address.length() < 5){
            displayToast("Please enter the correct address");
            isSuccess = false;
        }else if (lat.equals("") || lng.equals("")){
            displayToast("Please select the location");
            //isSuccess = false;
        }
        return isSuccess;
    }

    private void displayToast(String toastMsg) {
        Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Async task class to get json by making HTTP call
     * */
    private class doSubmitDonationAsyncTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(EnterDonationDetailsActivity.this);
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
            String sUrl = MyConstants.URL_ROOT+"donate/create";

            String jsonStr = serviceHandler.performPostCall(sUrl, requestParams);

            Log.e("Response: ", "--->>> " + jsonStr);

            if (jsonStr != null) try {
                JSONObject jsonObj = new JSONObject(jsonStr);
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
            Intent intent =new Intent(getApplicationContext(),ThankYouActivity.class);
            startActivity(intent);
            intent.putExtra(MyConstants.FROM_ACTIVITY,MyConstants.KEY_DONOR);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            finish();
        }

    }


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
