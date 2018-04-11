package com.example.john.foodscanner.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
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
import com.example.john.foodscanner.utils.MyConstants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;

import static com.example.john.foodscanner.Config.CONSUMER_ADDRESS;
import static com.example.john.foodscanner.Config.CONSUMER_NAME;
import static com.example.john.foodscanner.Config.CONSUMER_PHONE;
import static com.example.john.foodscanner.Config.CONSUMER_QUANTITY;
import static com.example.john.foodscanner.Config.HOST_URL;
import static com.example.john.foodscanner.Config.IS_ACTIVE;
import static com.example.john.foodscanner.Config.LATITUTE;
import static com.example.john.foodscanner.Config.LONGITUTE;
import static com.example.john.foodscanner.Config.URL_SAVE_CONSUMER;

/**
 * Created by john on 3/30/18.
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private Context context = this;
    private GoogleMap mMap;
    //todo:: Locations listener variables
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager locationManager;
    private LocationRequest mLocationRequest;
    double latitude = 0;
    double longitude = 0;
    private static final String TAG = "Googleplay Map";
    private String requestParams;
    AppSharedPreferences appSharedPreferences;
    private String address = "",lat = "",lng = "",userid= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_fragment);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        appSharedPreferences = new AppSharedPreferences(getApplicationContext());
        userid = appSharedPreferences.getStringPreferences(MyConstants.PREF_KEY_ID);

        try {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void submitDonation() {
        address = getIntent().getStringExtra("address");

        //if (isValidationSuccess()){
            doSubmitDonationTask();
       // }
    }

    private void doSubmitDonationTask() {
/*
        postParams = new HashMap<>();
        postParams.put("userid",userid);
        postParams.put("latitude",lat);
        postParams.put("longitude",lng);
        postParams.put("address", address);
*/

        JSONObject object = new JSONObject();
        try {
            object.put("consumerName", appSharedPreferences.getStringPreferences(MyConstants.PREF_KEY_NAME));
            object.put("consumerMobile", appSharedPreferences.getStringPreferences(MyConstants.PREF_KEY_MOBILE));
            //object.put("isVolunteer", String.valueOf(appSharedPreferences.getStringPreferences(MyConstants.PREF_KEY_IS_VOLUNTEER)));
            object.put("isActive", "true");
//            object.put("deviceId", String.valueOf(appSharedPreferences.getStringPreferences(MyConstants.PREF_KEY_DEVICE_ID)));
            object.put("quantity", "20");
            object.put("latitude", lat);
            object.put("longitude", lng);
            object.put("address", address);
//            object.put("deviceToken", "TestDeviceToken");
            requestParams = object.toString();
            Log.e("Map Request params","--->>> "+requestParams);
            send(appSharedPreferences.getStringPreferences(MyConstants.PREF_KEY_NAME),appSharedPreferences.getStringPreferences(MyConstants.PREF_KEY_MOBILE),
                    "true","20",lat,lng,address);
            //new doSubmitDonationAsyncTask().execute();
        } catch (Exception ex) {
            displayToast(getString(R.string.unable_to_connect));
        }
    }

    public void send(final String name, final String phone, final String is_active, final String quantity, final String latitute, final String longitute, final String address){
        ProgressDialog pDialog = null;

        try {
            // Showing progress dialog
            pDialog = new ProgressDialog(MapsActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
        final ProgressDialog finalPDialog = pDialog;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HOST_URL+URL_SAVE_CONSUMER,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG,"Results: "+response);
                        try{
                            if (finalPDialog.isShowing())
                                finalPDialog.dismiss();
                            Intent intent =new Intent(getApplicationContext(),ThankYouActivity.class);
                            startActivity(intent);
                            intent.putExtra(MyConstants.FROM_ACTIVITY,MyConstants.KEY_MAP_LOCATION);
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                            finish();
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
                            e.printStackTrace();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                int status = 1;
                Map<String, String> params = new Hashtable<String, String>();
                //Adding parameters
                params.put(CONSUMER_NAME, name);
                params.put(CONSUMER_PHONE, phone);
                params.put(IS_ACTIVE, is_active);
                params.put(CONSUMER_QUANTITY, quantity);
                params.put(LATITUTE, latitute);
                params.put(LONGITUTE, longitute);
                params.put(CONSUMER_ADDRESS, address);
                //returning parameters
                return params;
            }
        };
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        //Adding request to the queue
        requestQueue.add(stringRequest);
    }


    private void displayToast(String toastMsg) {
        Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startLocationUpdates();
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation == null) {
            startLocationUpdates();
        }
        if (mLocation != null) {
            latitude = mLocation.getLatitude();
            longitude = mLocation.getLongitude();
        } else {
            // Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mLocation != null) {
            latitude = mLocation.getLatitude();
            longitude = mLocation.getLongitude();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1)
                .setFastestInterval(2);
        // Request location updates

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d("reque", "--->>>>");
    }
}