package com.example.john.foodscanner.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;

import static com.example.john.foodscanner.Config.DEVICE_ID;
import static com.example.john.foodscanner.Config.DEVICE_TOKEN;
import static com.example.john.foodscanner.Config.HOST_URL;
import static com.example.john.foodscanner.Config.IS_VOLUNTEER;
import static com.example.john.foodscanner.Config.OPERATION_DONATION;
import static com.example.john.foodscanner.Config.OPERATION_USER;
import static com.example.john.foodscanner.Config.URL_SAVE_USER;
import static com.example.john.foodscanner.Config.USERNAME;
import static com.example.john.foodscanner.Config.USER_ID;
import static com.example.john.foodscanner.Config.USER_PHONE;

/**
 * Created by john on 3/10/18.
 */

public class FetchData {
    private Context context;
    private static final String TAG = "FetchData";
    AppSharedPreferences appSharedPreferences;
    public FetchData(Context context){
        this.context = context;
        appSharedPreferences = new AppSharedPreferences(context.getApplicationContext());
    }

    public void fetch(String url, final String token, final String operationUser){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HOST_URL+url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG,"Results: "+response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if (operationUser.equals(OPERATION_USER)){
                                    long id = jsonObject.getLong(USER_ID);
                                    appSharedPreferences.saveStringPreferences(MyConstants.PREF_KEY_ID, String.valueOf(id));
                                    appSharedPreferences.saveBooleanPreferences(MyConstants.PREF_KEY_IS_LOGGEDIN, true);
                                }else if (operationUser.equals(OPERATION_DONATION)){

                                }
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
                params.put(USER_PHONE, token);

                //returning parameters
                return params;
            }
        };
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

}
