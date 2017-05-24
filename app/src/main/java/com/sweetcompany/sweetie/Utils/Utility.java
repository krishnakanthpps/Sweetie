package com.sweetcompany.sweetie.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;

import java.util.Date;

/**
 * Created by lucas on 22/05/2017.
 */

public class Utility {

    // Key strings for get a shared preference
    public static final String MAIL = "mail";
    public static final String USERNAME = "Username";
    public static final String PHONENUMBER = "phoneNumber";
    public static final String GENDER = "gender";

    //Method for saving a shared preference
    //How to use: pass the context, a key string and the data string; returns true if successfully saved
    //
     static public boolean saveStringPreference(Context context, String key, String data){
        SharedPreferences settings = context.getSharedPreferences(key, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key,data);
        return editor.commit();
    }

    //Method for getting a string from shared preferences
    //How to use: pass the context and one of key strings above
    //if the result string doesn't exist it returns the string "error"
    static public String getStringPreference(Context context,String key){
        SharedPreferences setting = context.getSharedPreferences(key, 0);
        return setting.getString(key,"error");
    }

    // TODO
    // Give dateTime
    // return data formatted for Actions
    // es: Give (24/05/17 22:00:01)
    //     if date of today             return "22:00"
    //     if date of yesterday         return "TOMORROW/IERI"
    //     if date past                 return "24/05/17"
    public String getDateFormattedForActions(String data){
        String mData = data;

        return mData;
    }
}
