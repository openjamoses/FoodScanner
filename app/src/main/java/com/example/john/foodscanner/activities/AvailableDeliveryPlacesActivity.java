package com.example.john.foodscanner.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.john.foodscanner.R;
import com.example.john.foodscanner.adapters.DeliveryListAdapter;
import com.example.john.foodscanner.utils.AlertMagnaticInterface;
import com.example.john.foodscanner.utils.FoodObject;
import com.example.john.foodscanner.utils.MyConstants;
import com.example.john.foodscanner.utils.OnItemClickListener;
import com.example.john.foodscanner.utils.ServiceHandler;
import com.example.john.foodscanner.utils.ShowAlert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import static com.example.john.foodscanner.Config.CONSUMER_ADDRESS;
import static com.example.john.foodscanner.Config.CONSUMER_NAME;
import static com.example.john.foodscanner.Config.CONSUMER_PHONE;
import static com.example.john.foodscanner.Config.CONSUMER_QUANTITY;
import static com.example.john.foodscanner.Config.DISTANCE;
import static com.example.john.foodscanner.Config.HOST_URL;
import static com.example.john.foodscanner.Config.LATITUTE;
import static com.example.john.foodscanner.Config.LONGITUTE;
import static com.example.john.foodscanner.Config.URL_GET_CONSUMER;
import static com.example.john.foodscanner.Config.URL_GET_DONATION;

public class AvailableDeliveryPlacesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DeliveryListAdapter deliveryListAdapter;
    private ArrayList<FoodObject> foodObjects;
    private OnItemClickListener onItemClickListener;
    private FoodObject donorFoodObj;
    private static final String TAG = "AvailableDeliveryPlacesActivity";
    private Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_delivery_places);
        //initView();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.mipmap.ic_launcher);
        ab.setDisplayHomeAsUpEnabled(true);

        try{
            Intent intent = getIntent();
            donorFoodObj = (FoodObject)intent.getSerializableExtra("DonationObj");
        }catch (Exception e){

        }

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
                /*Intent intent = new Intent(getApplicationContext(),ThankYouActivity.class);
                intent.putExtra("DonationObj",donorFoodObj);
                intent.putExtra("DeliveryObj",foodObjects.get(position));
                startActivity(intent);
                finish();*/
                final int mPostion = position;
                ShowAlert.getConfirmDialog(AvailableDeliveryPlacesActivity.this, "Confirm", getString(R.string.alert_msg), "Yes", "No", true, new AlertMagnaticInterface() {
                    @Override
                    public void PositiveMethod(DialogInterface dialog, int id) {
                        callThankYouScreen(mPostion);
                    }

                    @Override
                    public void NegativeMethod(DialogInterface dialog, int id) {

                    }
                });
            }
        };

        foodObjects = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        LinearLayoutManager llm = new LinearLayoutManager(AvailableDeliveryPlacesActivity.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        deliveryListAdapter = new DeliveryListAdapter(getApplicationContext(),foodObjects);
        deliveryListAdapter.setOnItemClickListener(onItemClickListener);
        recyclerView.setAdapter(deliveryListAdapter);

        fetch();
        //loadLocations();

    }

    private void callThankYouScreen(int position) {
        Intent intent = new Intent(getApplicationContext(),ThankYouActivity.class);
        intent.putExtra("DonationObj",donorFoodObj);
        intent.putExtra("DeliveryObj",foodObjects.get(position));
        startActivity(intent);
        finish();
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
            pDialog = new ProgressDialog(AvailableDeliveryPlacesActivity.this);
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
            String sUrl = MyConstants.URL_ROOT+"consumer";

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
            pDialog = new ProgressDialog(AvailableDeliveryPlacesActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
        final ProgressDialog finalPDialog = pDialog;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HOST_URL+URL_GET_CONSUMER,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG,"Results: "+response);

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if (jsonArray != null && jsonArray.length() > 0) {
                                convertJsonAsObj(jsonArray);
                            }

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
               // foodObject.setId(jsonObject.getString("id"));

                if (!jsonObject.isNull(CONSUMER_NAME))
                    foodObject.setId(jsonObject.getString(CONSUMER_NAME));

                if (!jsonObject.isNull(CONSUMER_PHONE))
                foodObject.setMobile(jsonObject.getString(CONSUMER_PHONE));
               // foodObject.setFoodtype(jsonObject.getString("foodType"));
                if (!jsonObject.isNull(CONSUMER_QUANTITY))
                foodObject.setQuantity(jsonObject.getString(CONSUMER_QUANTITY));

                if (!jsonObject.isNull(CONSUMER_ADDRESS))
                foodObject.setAddress(jsonObject.getString(CONSUMER_ADDRESS));

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

            deliveryListAdapter = new DeliveryListAdapter(getApplicationContext(), foodObjects);
            deliveryListAdapter.setOnItemClickListener(onItemClickListener);
            recyclerView.setAdapter(deliveryListAdapter);
            //donationsListAdapter.setOnItemClickListener(onItemClickListener);
        }
    }

}
