package com.example.john.foodscanner.firebase;

/**
 * Created by john on 8/31/17.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.john.foodscanner.Config;
import com.example.john.foodscanner.R;
import com.example.john.foodscanner.activities.LoginActivity;
import com.example.john.foodscanner.activities.MainActivity;
import com.example.john.foodscanner.activities.MessageActivity;
import com.example.john.foodscanner.utils.Get_CurrentDateTime;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.john.foodscanner.Config.NOTIFICATION_ID;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;
    private Context context = this;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            Log.e(TAG, "From: " + remoteMessage.getFrom());

            if (remoteMessage == null)
                return;

            // Check if message contains a notification payload.
            if (remoteMessage.getNotification() != null) {
                Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
                handleNotification(remoteMessage.getNotification().getBody());
            }

            // Check if message contains a data payload.
            if (remoteMessage.getData().size() > 0) {
                Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

                try {
                    JSONObject json = new JSONObject(remoteMessage.getData().toString());
                    handleDataMessage(json);
                } catch (Exception e) {
                    Log.e(TAG, "Exception: " + e.getMessage());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);

        String datas = intent.getStringExtra("data");
        if (datas!=null){
            Log.e(TAG+": handleIntent", "Result: " + datas.toString());
            try{
                JSONObject jsonObject = new JSONObject(datas);
                final String title = jsonObject.getString("title");
                final String message = jsonObject.getString("message");
                //boolean isBackground = data.getBoolean("is_background");
                final String imageUrl = jsonObject.getString("image");
                final String timestamp = new Get_CurrentDateTime().getCurrentDate()+","+new Get_CurrentDateTime().getCurrentTime();
                //JSONObject payload = data.getJSONObject("payload");
                Log.e(TAG, "title: " + title);
                Log.e(TAG, "message: " + message);
                //// Log.e(TAG, "isBackground: " + isBackground);
                ////Log.e(TAG, "payload: " + payload.toString());
                Log.e(TAG, "imageUrl: " + imageUrl);


                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {

                        if (NotificationUtils.isAppIsInBackground(getApplicationContext())) {


                            // play notification sound
                            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                            notificationUtils.playNotificationSound();




                            Intent resultIntent = new Intent(getApplicationContext(), LoginActivity.class);
                            resultIntent.putExtra("message", message);

                            //// check for image attachment
                            if (TextUtils.isEmpty(imageUrl)) {
                                showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                                // new Custom(context).createNotify(splits[1],message);
                            } else {

                                // image is present, show notification with image
                                showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                            }


                        }else {
                            // play notification sound
                            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                            notificationUtils.playNotificationSound();




                            Intent resultIntent = new Intent(getApplicationContext(), LoginActivity.class);
                            resultIntent.putExtra("message", message);

                            //// check for image attachment
                            if (TextUtils.isEmpty(imageUrl)) {
                                showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                                // new Custom(context).createNotify(splits[1],message);
                            } else {

                                // image is present, show notification with image
                                showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                            }
                        }


                    }
                });



            }catch (Exception e){
                e.printStackTrace();
            }

        }else {
            Log.e(TAG+": handleIntent", "Empty " + datas);
        }
        // you can get ur data here
        //intent.getExtras().get("your_data_key")
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        }else{
            // If the app is in background, firebase itself handles the notification
        }
    }
    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());
        try {
            JSONObject data = json.getJSONObject("data");
            final String title = data.getString("title");
            final String message = data.getString("message");
            //boolean isBackground = data.getBoolean("is_background");
            final String imageUrl = data.getString("image");
            final String timestamp = new Get_CurrentDateTime().getCurrentDate()+","+new Get_CurrentDateTime().getCurrentTime();
            //JSONObject payload = data.getJSONObject("payload");
            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);

            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                public void run() {
                    NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                    notificationUtils.playNotificationSound();

                    Toast.makeText(context, message,Toast.LENGTH_SHORT).show();
                    showNotifications(title,message);
                    Intent resultIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    resultIntent.putExtra("message", message);
                    if (TextUtils.isEmpty(imageUrl)) {
                        showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                        //new Custom(context).createNotify(splits[1],message);
                    } else {
                        // image is present, show notification with image
                        showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                    }
                    //Toast.makeText(context, "Your message to main thread", Toast.LENGTH_SHORT).show();
                }
            });



        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * ////Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }

    private void showNotifications(String title, String msg) {
        Intent i = new Intent(this, MessageActivity.class);
        i.putExtra("title",title);
        i.putExtra("message",msg);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 2,
                i, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentText(msg)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .build();

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, notification);
    }

}