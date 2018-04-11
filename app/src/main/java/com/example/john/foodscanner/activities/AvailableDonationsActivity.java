package com.example.john.foodscanner.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.john.foodscanner.R;
import com.example.john.foodscanner.adapters.DonationsListAdapter;
import com.example.john.foodscanner.utils.FoodObject;
import com.example.john.foodscanner.utils.MyConstants;
import com.example.john.foodscanner.utils.OnItemClickListener;
import com.example.john.foodscanner.utils.ServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import static com.example.john.foodscanner.Config.DEVICE_TOKEN;
import static com.example.john.foodscanner.Config.DISTANCE;
import static com.example.john.foodscanner.Config.DONATION_ADDRESS;
import static com.example.john.foodscanner.Config.DONATION_FOOD;
import static com.example.john.foodscanner.Config.DONATION_PHONE;
import static com.example.john.foodscanner.Config.DONATION_QUANTITY;
import static com.example.john.foodscanner.Config.HOST_URL;
import static com.example.john.foodscanner.Config.LATITUTE;
import static com.example.john.foodscanner.Config.LONGITUTE;
import static com.example.john.foodscanner.Config.OPERATION_DONATION;
import static com.example.john.foodscanner.Config.OPERATION_USER;
import static com.example.john.foodscanner.Config.URL_GET_DONATION;
import static com.example.john.foodscanner.Config.USER_ID;

public class AvailableDonationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DonationsListAdapter donationsListAdapter;
    private ArrayList<FoodObject> foodObjects;
    private OnItemClickListener onItemClickListener;
    private static final String TAG = "AvailableDonationsActivity";

    private Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_donations);
        //initView();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.mipmap.ic_launcher);
        ab.setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        onItemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Toast.makeText(getApplicationContext(),"clicked: "+position,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),AvailableDeliveryPlacesActivity.class);
                intent.putExtra("DonationObj",foodObjects.get(position));
                startActivity(intent);
            }
        };

        foodObjects = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        LinearLayoutManager llm = new LinearLayoutManager(AvailableDonationsActivity.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        donationsListAdapter = new DonationsListAdapter(getApplicationContext(),foodObjects);
        donationsListAdapter.setOnItemClickListener(onItemClickListener);
        recyclerView.setAdapter(donationsListAdapter);

        fetch();
       // loadLocations();

    }

    private void loadLocations() {
        new loadLocationsAsyncTask().execute();
    }

    private void displayToast(String toastMsg) {
        Toast.makeText(getApplicationContext(),toastMsg,Toast.LENGTH_SHORT).show();
    }

    /**
     * Async task class to get json by making HTTP call
     * */
    private class loadLocationsAsyncTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(AvailableDonationsActivity.this);
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
            String sUrl = MyConstants.URL_ROOT+"donate/distance";

            String jsonStr = serviceHandler.performGetCall(sUrl);

            Log.e("Response: ", "--->>> " + jsonStr);

            if (jsonStr != null) try {
                JSONArray jsonArray = new JSONArray(jsonStr);
                if (jsonArray != null && jsonArray.length() > 0) {
                    convertJsonAsObj(jsonArray);
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
            if (foodObjects.size()> 0){
                loadAdapter();
            }
        }
    }

    public void fetch(){
        ProgressDialog pDialog = null;
        try {
            // Showing progress dialog
            pDialog = new ProgressDialog(AvailableDonationsActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
        final ProgressDialog finalPDialog = pDialog;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HOST_URL+URL_GET_DONATION,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG,"Results: "+response);

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            convertJsonAsObj(jsonArray);

                            if (finalPDialog.isShowing())
                                finalPDialog.dismiss();
                            if (foodObjects.size()> 0){
                                loadAdapter();
                            }
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
                //params.put(DEVICE_TOKEN, token);

                //returning parameters
                return params;
            }
        };
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void convertJsonAsObj(JSONArray jsonArray) {
        try{
            for (int i = 0;i < jsonArray.length() ;i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                FoodObject foodObject = new FoodObject();
                if (!jsonObject.isNull(DONATION_PHONE))
                    foodObject.setMobile(jsonObject.getString(DONATION_PHONE));
                //foodObject.setId(jsonObject.getString("id"));
                if (!jsonObject.isNull(DONATION_FOOD))
                foodObject.setFoodtype(jsonObject.getString(DONATION_FOOD));

                if (!jsonObject.isNull(DONATION_QUANTITY))
                foodObject.setQuantity(jsonObject.getString(DONATION_QUANTITY));

                if (!jsonObject.isNull(DONATION_ADDRESS))
                foodObject.setAddress(jsonObject.getString(DONATION_ADDRESS));

                if (!jsonObject.isNull(LATITUTE))
                foodObject.setLat(jsonObject.getString(LATITUTE));

                if (!jsonObject.isNull(LONGITUTE))
                foodObject.setLng(jsonObject.getString(LONGITUTE));

                if (!jsonObject.isNull(DISTANCE))
                foodObject.setDistance(jsonObject.getString(DISTANCE));
                //foodObject.sets(jsonObject.getString("donationStatus"));
                foodObjects.add(foodObject);
            }
        }catch (Exception e){}
    }

    private void loadAdapter() {
        if (foodObjects.size() > 0) {
            donationsListAdapter = new DonationsListAdapter(getApplicationContext(), foodObjects);
            donationsListAdapter.setOnItemClickListener(onItemClickListener);
            recyclerView.setAdapter(donationsListAdapter);
            //donationsListAdapter.setOnItemClickListener(onItemClickListener);
        }
    }

}
