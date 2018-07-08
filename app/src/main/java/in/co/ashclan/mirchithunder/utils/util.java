package in.co.ashclan.mirchithunder.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import in.co.ashclan.mirchithunder.ParticipantsLogin;
import in.co.ashclan.mirchithunder.model.ParticipantModel;
import in.co.ashclan.mirchithunder.model.VolunteerModel;

public class util {
    public static ParticipantModel currentParticipant;
    public static VolunteerModel currentvolunteer;

    public static boolean isConnectedToInterNet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null)
        {
            NetworkInfo[] infos = connectivityManager.getAllNetworkInfo();
            if(infos != null)
            {
                for(int i = 0;i<infos.length;i++)
                {
                    if(infos[i].getState()==NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }
}