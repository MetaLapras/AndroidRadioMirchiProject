package in.co.ashclan.mirchithunder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import in.co.ashclan.mirchithunder.model.ParticipantModel;
import in.co.ashclan.mirchithunder.utils.PreferenceUtil;
import in.co.ashclan.mirchithunder.utils.util;

public class ActivitySplash extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    String username,pass;
    Context mcontext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mcontext = ActivitySplash.this;
        printkeyhash();

       /* try {
            if (PreferenceUtil.getSignIn(this)) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(ActivitySplash.this, Activity_DashBoard2.class);
                        startActivity(intent);
                        finish();
                        //layout.setVisibility(View.VISIBLE);
                        //login(PreferenceUtil.getMobileNo(mcontext).toString(),PreferenceUtil.getPass(mcontext).toString());

                    }
                }, 3000);
            } else {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(ActivitySplash.this, ParticipantsLogin.class);
                        startActivity(intent);
                        finish();
                    }
                }, 3000);
            }

        }catch (Exception e)
        {
            Log.e("-->splashexp",e.toString());
        }*/

          new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(ActivitySplash.this, LoginScreen.class);
                    startActivity(intent);
                    /*if (PreferenceUtil.getSignIn(mcontext)) {
                        Intent intent = new Intent(ActivitySplash.this, LoginScreen.class);
                        startActivity(intent);
                    }else{
                       Intent intent = new Intent(ActivitySplash.this, ParticipantsLogin.class);
                       startActivity(intent);
                    }*/
                    finish();
                }}, SPLASH_TIME_OUT);
        }

    private void printkeyhash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("in.co.ashclan.mirchithunder",
                    PackageManager.GET_SIGNATURES);

            for (Signature signature :info.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


}


