package in.co.ashclan.mirchithunder.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.FileProvider;

public class PreferenceUtil {

    public static final String PREFERENCE_KEY = "church";


    public static final String PREFERENCE_KEY_INTERNET_ACCESS="internetAccess";
    public static final String MOBILE_NO="mobileNo";
    public static final String PUID="puid";
    public static final String PASSWORD="Password";
    public static final String EMAILID="emailid";
    public static final String FIRSTNAME="firstname";
    public static final String LASTNAME="lastname";
    public static final String IMAGE="image";
    public static final String PAYMENTTYPE="paymenttype";
    public static final String RECEIPTID="receiptId";
    public static final String STATUS="status";
    public static final String TICKETTYPE="tickettype";
    public static final String GENDER="Gender";
    public static final String DOB="dob";
    public static final String SIGN_IN="signIn";
    public static final String QRCODEID="qrcodeId";


    //BatchDetails
    public static final String BATCHID="batchid";
    public static final String BATCHNAME="batchname";
    public static final String BATCHSTARTTIME="batchstarttime";

    //QRDetails
    public static final String QRPUID="QRpuid";
    public static final String STARTTIME="starttime";
    public static final String CHECKPTTIME="checkpttime";
    public static final String ENDTIME="endtime";

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

    public static void setSignIn(Context context,boolean signIn) {
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

    public static void setPass(Context context,String pass) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PASSWORD,pass).apply();
    }
    public static String getPass(Context context){
        return getSharedPreferences(context).getString(PASSWORD,"");
    }
    public static void setEmailid(Context context,String puid){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(EMAILID,puid).apply();
    }
    public static String getEmailid(Context context){
        return getSharedPreferences(context).getString(EMAILID,"");
    }
    public static void setPuid(Context context,String puid){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PUID,puid).apply();
    }
    public static String getPuid(Context context){
        return getSharedPreferences(context).getString(PUID,"");
    }
    public static void setFirstname(Context context,String puid){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(FIRSTNAME,puid).apply();
    }
    public static String getFirstname(Context context){
        return getSharedPreferences(context).getString(FIRSTNAME,"");
    }
    public static void setLastname(Context context,String puid){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(LASTNAME,puid).apply();
    }
    public static String getLastname(Context context){
        return getSharedPreferences(context).getString(LASTNAME,"");
    }
    public static void setImage(Context context,String puid){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(IMAGE,puid).apply();
    }
    public static String getImage(Context context){
        return getSharedPreferences(context).getString(IMAGE,"");
    }
    public static void setPaymenttype(Context context,String puid){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PAYMENTTYPE,puid).apply();
    }
    public static String getPaymenttype(Context context){
        return getSharedPreferences(context).getString(PAYMENTTYPE,"");
    }
    public static void setReceiptid(Context context,String puid){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(RECEIPTID,puid).apply();
    }
    public static String getReceiptid(Context context){
        return getSharedPreferences(context).getString(RECEIPTID,"");
    }
    public static void setStatus(Context context,String puid){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(STATUS,puid).apply();
    }
    public static String getStatus(Context context){
        return getSharedPreferences(context).getString(STATUS,"");
    }
    public static void setTickettype(Context context,String puid){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(TICKETTYPE,puid).apply();
    }
    public static String getTickettype(Context context){
        return getSharedPreferences(context).getString(TICKETTYPE,"");
    }
    public static void setGender(Context context,String puid){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(GENDER,puid).apply();
    }
    public static String getGender(Context context){
        return getSharedPreferences(context).getString(GENDER,"");
    }
    public static void setDob(Context context,String puid){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(DOB,puid).apply();
    }
    public static String getDob(Context context){
        return getSharedPreferences(context).getString(DOB,"");
    }
    public static void setQrcodeid(Context context,String puid){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(QRCODEID,puid).apply();
    }
    public static String getQrcodeid(Context context){
        return getSharedPreferences(context).getString(QRCODEID,"");
    }


    public static void setBatchid(Context context,String batchid){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(BATCHID,batchid).apply();
    }
    public static String getBatchid(Context context){
        return getSharedPreferences(context).getString(BATCHID,"");
    }
    public static void setBatchname(Context context,String batchName){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(BATCHNAME,batchName).apply();
    }
    public static String getBatchname(Context context){
        return getSharedPreferences(context).getString(BATCHNAME,"");
    }
    public static void setBatchstarttime(Context context,String batchName){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(BATCHSTARTTIME,batchName).apply();
    }
    public static String getBatchstarttime(Context context){
        return getSharedPreferences(context).getString(BATCHSTARTTIME,"");
    }
    public static void setQrpuid(Context context,String qrpuid){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(QRPUID,qrpuid).apply();
    }
    public static String getQrpuid(Context context){
        return getSharedPreferences(context).getString(QRPUID,"");
    }
    public static void setStarttime(Context context,String string){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(STARTTIME,string).apply();
    }
    public static String getStarttime(Context context){
        return getSharedPreferences(context).getString(STARTTIME,"");
    }
    public static void setCheckpttime(Context context,String string){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(CHECKPTTIME,string).apply();
    }
    public static String getCheckpttime(Context context){
        return getSharedPreferences(context).getString(CHECKPTTIME,"");
    }
    public static void setEndtime(Context context,String string){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(ENDTIME,string).apply();
    }
    public static String getEndtime(Context context){
        return getSharedPreferences(context).getString(ENDTIME,"");
    }


}
