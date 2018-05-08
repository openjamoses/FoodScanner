package com.example.john.foodscanner;

/**
 * Created by john on 8/31/17.
 */

public class Config {

    public static final String URL_PHONE = "http://192.168.43.18/";
    public static final String URL_MODEM = "http://10.127.181.234/";
    public static final String URL_CAMTECH = "http://192.168.1.110/";
    public static final String URL_EMULATOR = "http://10.0.2.2/";
    public static final String URL_LOCAL = "http://127.0.0.1/";
    public static final String URL_SERVER = "http://173.255.219.164/";
    public static final String HOST_URL = URL_PHONE+"FoodScanning/pages/mobile_connectivity/";

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";
    public static final String KEY_TOKEN = "regId";
    //URL to RegisterDevice.php
    public static final String URL_REGISTER_DEVICE = "firebase/RegisterDevice.php";
     public static final String URL_SINGLE = "firebase/sendSinglePush.php";
    public static final String URL_MULTIPLE_USERS = "firebase/sendMultiplePush.php";

    //TODO:: USERS CONSTANTS
    public static final String USER_ID = "nfw_user_id";
    public static final String USERNAME = "nfw_user_name";
    public static final String USER_PHONE = "nfw_user_phone_number";
    public static final String IS_VOLUNTEER = "is_volunteer";
    public static final String DEVICE_ID = "device_id";
    public static final String DEVICE_TOKEN = "device_token";

    //TODO:: CONSUMER
    public static final String CONSUMER_ID = "consumer_id";
    public static final String CONSUMER_NAME = "consumer_name";
    public static final String CONSUMER_PHONE = "consumer_phone_number";
    public static final String CONSUMER_QUANTITY = "consumer_quantity";
    public static final String CONSUMER_ADDRESS = "consumer_address";
    public static final String LATITUTE = "lat";
    public static final String LONGITUTE = "longi";
    public static final String IS_ACTIVE = "is_active";

    //TODO:: DONATION
    public static final String DONATION_ID = "nfw_donation_id";
    public static final String DONATION_PHONE = "nfw_donor_phone_number";
    public static final String DONATION_FOOD = "donation_food_type";
    public static final String DONATION_QUANTITY = "donation_quantity";
    public static final String DONATION_ADDRESS = "donation_address";;
    public static final String DONATION_STATUS = "donation_status";
    public static final String DISTANCE = "distance";

    //TODO:: URL
    public static final String SYNC_FOLDER = "sync_calls/";
    public static final String URL_SAVE_USER = SYNC_FOLDER+"save_User.php";
    public static final String URL_SAVE_DONATION = SYNC_FOLDER+"save_Donation.php";
    public static final String URL_SAVE_CONSUMER = SYNC_FOLDER+"saveConsumer.php";
    public static final String URL_GET_USER = SYNC_FOLDER+"getUsers.php";;
    public static final String URL_GET_DONATION = SYNC_FOLDER+"getDonation.php";
    public static final String URL_GET_CONSUMER = SYNC_FOLDER+"getConsumers.php";

    //TODO:: OPERATION
    public static final String OPERATION_USER = "user";
    public static final String OPERATION_DONATION = "donation";
    public static final String OPERATION_CONSUMER = "consumer";

}
