package in.co.ashclan.mirchithunder.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtil {

    public static final String PREFERENCE_KEY = "church";


    public static final String PREFERENCE_KEY_INTERNET_ACCESS="internetAccess";
    public static final String MOBILE_NO="mobileNo";
    public static final String PASS="Pass";
    public static final String SIGN_IN="signIn";


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

    public static void setSignIn(Context context,boolean signIn)
    {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(SIGN_IN,signIn).apply();
    }

    public static boolean getSignIn(Context context){
        return getSharedPreferences(context).getBoolean(SIGN_IN,false);
    }

    public static void setMobileNo(Context context,String mobileNo){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(MOBILE_NO,mobileNo).apply();
    }

    public static String getMobileNo(Context context){
        return getSharedPreferences(context).getString(MOBILE_NO,"");
    }

    public static void setPass(Context context,String pass)
    {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PASS,pass).apply();
    }
    public static String getPass(Context context){
        return getSharedPreferences(context).getString(PASS,"");
    }



}
