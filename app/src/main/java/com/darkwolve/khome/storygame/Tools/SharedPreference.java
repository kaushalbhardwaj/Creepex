package com.darkwolve.khome.storygame.Tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Keep;


/**
 * Created by khome on 26/3/17.
 */

public class SharedPreference {
    public static final String CurrentLocationPreference = "CurrentLocation" ;
    public static final String PositionKey = "positionKey";
    public static final String FirstTimePreference = "FirstTime";
    public static final String FirstKey = "firstKey";

    public static SharedPreferences sharedpreference;

    public static boolean putCurrentPosition(Context con, int id)
    {
        sharedpreference = con.getSharedPreferences(CurrentLocationPreference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreference.edit();
        editor.putInt(PositionKey, id);
        editor.commit();
        return true;
    }

    public static int getCurrentPosition(Context con)
    {

        sharedpreference = con.getSharedPreferences(CurrentLocationPreference, Context.MODE_PRIVATE);
        return (sharedpreference.getInt(PositionKey, -1));
    }

    public static boolean putFirstTimeStatus(Context con, int id)
    {
        sharedpreference = con.getSharedPreferences(FirstTimePreference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreference.edit();
        editor.putInt(FirstKey, id);
        editor.commit();
        return true;
    }

    public static int getFirstTimeStatus(Context con)
    {

        sharedpreference = con.getSharedPreferences(FirstTimePreference, Context.MODE_PRIVATE);
        return (sharedpreference.getInt(FirstKey, -1));
    }


}
