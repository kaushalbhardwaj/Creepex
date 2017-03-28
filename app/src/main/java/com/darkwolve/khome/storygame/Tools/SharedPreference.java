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
    public static final String PlayerNamePreference = "playername";
    public static final String PlayerKey = "playerKey";

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

    public static boolean putPlayerName(Context con, String s )
    {
        sharedpreference = con.getSharedPreferences(PlayerNamePreference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreference.edit();
        editor.putString(PlayerKey, s);
        editor.commit();
        return true;
    }

    public static String getPlayerName(Context con)
    {

        sharedpreference = con.getSharedPreferences(PlayerNamePreference, Context.MODE_PRIVATE);
        return (sharedpreference.getString(PlayerKey, null));
    }


}
