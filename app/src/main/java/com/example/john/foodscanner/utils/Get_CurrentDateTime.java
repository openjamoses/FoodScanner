package com.example.john.foodscanner.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by john on 7/9/17.
 */

public class Get_CurrentDateTime {
    public String getCurrentDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sdf.format(c.getTime());
        return strDate;
    }
    public String getCurrentTime(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String strTime = sdf.format(c.getTime());
        return strTime;
    }
    public static int getCurrentDay(){
        String daysArray[] = {"Sunday","Monday","Tuesday", "Wednesday","Thursday","Friday", "Saturday"};
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        return day;
    }

    public static int getDate_By(String date){
        int day = 0;
        if (date.equals("Sunday")){
            day = 1;
        }else if (date.equals("Monday")){
            day = 2;
        }
        else if (date.equals("Tuesday")){
            day = 3;
        }
        else if (date.equals("Wednesday")){
            day = 4;
        }
        else if (date.equals("Thursday")){
            day = 5;
        }
        else if (date.equals("Friday")){
            day = 6;
        }
        else if (date.equals("Saturday")){
            day = 7;
        }
        return day;
    }
}
