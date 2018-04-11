package com.example.john.foodscanner.firebase;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import static com.example.john.foodscanner.Config.HOST_URL;
import static com.example.john.foodscanner.Config.URL_MULTIPLE_USERS;
import static com.example.john.foodscanner.Config.URL_SINGLE;

/**
 * Created by john on 8/31/17.
 */

public class SendNotification {
    Context context;
    private final static String TAG = "SendNotification";
    public SendNotification(Context context){
        this.context = context;
    }
    public void sendSinglePush_Patient(final String title, final String message, final String image, final String imei) {

        //progressDialog.setMessage("Sending Push");
        //progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, HOST_URL+URL_SINGLE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                        Log.e(TAG,response+"\t"+imei);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG,error+"");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("message", message);
                //if(!TextUtils.isEmpty(image))
                params.put("image", image);
                params.put("imei", imei);
                return params;
            }
        };

        MyVolley.getInstance(context).addToRequestQueue(stringRequest);
    }



    public void sendMultiplePush(final String title, final String message, final String image, final String users) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HOST_URL+URL_MULTIPLE_USERS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        Log.e(TAG,response);
                       // Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG,error+"");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("message", message);
                params.put("users", users);

                if (!TextUtils.isEmpty(image))
                    params.put("image", image);
                return params;
            }
        };
        MyVolley.getInstance(context).addToRequestQueue(stringRequest);
    }


}
