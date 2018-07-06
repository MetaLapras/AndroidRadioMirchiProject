package in.co.ashclan.mirchithunder.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtil {

    public static final String PREFERENCE_KEY = "church";


    public static final String PREFERENCE_KEY_INTERNET_ACCESS="internetAccess";

    private PreferenceUtil(){}

    public static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(PREFERENCE_KEY,Context.MODE_PRIVATE);
    }

    public static void setInternetAccess(Context context, boolean internetAccess){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(PREFERENCE_KEY_INTERNET_ACCESS,internetAccess).apply();
    }

    public static boolean getInternetAccess(Context context){
        return getSharedPreferences(context).getBoolean(PREFERENCE_KEY_INTERNET_ACCESS,false);
    }

}
