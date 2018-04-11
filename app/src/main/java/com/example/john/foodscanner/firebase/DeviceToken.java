package com.example.john.foodscanner.firebase;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.john.foodscanner.Config;

import static com.example.john.foodscanner.Config.KEY_TOKEN;

/**
 * Created by john on 8/31/17.
 */

public class DeviceToken {
    Context context;
    public DeviceToken(Context context){
        this.context = context;
    }
    public String token(){
        SharedPreferences pref = context.getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString(KEY_TOKEN, null);
        return regId;
    }

}
