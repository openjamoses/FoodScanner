package com.example.john.foodscanner.firebase;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import static com.example.john.foodscanner.Config.HOST_URL;
import static com.example.john.foodscanner.Config.URL_REGISTER_DEVICE;

/**
 * Created by john on 9/6/17.
 */
public class SendTokens {

    Context context;
    private static final String TAG = "SendTokens";
    public SendTokens(Context context){
        this.context = context;
    }
    //storing token to mysql server
    public void sendTokenToServer(final String token, final String imei) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HOST_URL+URL_REGISTER_DEVICE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e(TAG,response);
                            //JSONObject obj = new JSONObject(response);
                            //Toast.makeText(MainActivity_Doctor.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                            String token = new DeviceToken(context).token();
                            //String doctor_id = new Doctor_Details(context).getDoctor_id();

                            Log.e(TAG, "Firebase reg id: " + token);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG,"Error Occured: "+e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            Log.e(TAG,error+"");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("token", token);
                params.put("imei", imei);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }


}
