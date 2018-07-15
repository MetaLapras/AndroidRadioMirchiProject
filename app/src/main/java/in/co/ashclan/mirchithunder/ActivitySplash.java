package in.co.ashclan.mirchithunder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        username = PreferenceUtil.getMobileNo(mcontext);
        pass     = PreferenceUtil.getPass(mcontext);

            new Handler().postDelayed(new Runnable() {

                                          /*
                                           * Showing splash screen with a timer. This will be useful when you
                                           * want to show case your app logo / company
                                           */
                                          @Override
                                          public void run() {
     // This method will be executed once the timer is over
     // Start your app main activity
     // close this activity
    //checkConnection();

    if (PreferenceUtil.getSignIn(mcontext)) {
        Intent intent = new Intent(ActivitySplash.this, Activity_DashBoard2.class);
          startActivity(intent);
         }
      else
     {
       Intent intent = new Intent(ActivitySplash.this, ParticipantsLogin.class);
       startActivity(intent);
      }
    finish();
    }
    }, SPLASH_TIME_OUT);
        }
    private void login(final String phone, final String pass) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("Participant");

        Log.d("-->",phone +" "+pass);

        if (util.isConnectedToInterNet(getBaseContext())) {

            final ProgressDialog mDialog = new ProgressDialog(mcontext);
            mDialog.setMessage("Please Wait.....");
            mDialog.show();

            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Check if User doesnt exist in database
                    if (dataSnapshot.child(phone).exists()) {
                        //mDialog.dismiss();
                        //get User Values
                        Log.d("-->123",dataSnapshot.toString());
                        ParticipantModel user = dataSnapshot.child(phone).getValue(ParticipantModel.class);
                        user.setMobile(phone);
                        Log.d("PoJo-->",user.toString());
                        if (user.getFirstname().equals(pass)) {
                            //Toast.makeText(getApplicationContext(),"Successful",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(mcontext, DashBoard.class));
                            util.currentParticipant = user;
                            finish();
                        } else {
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Wrong Password", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        mDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "User Doesnt Exist", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(mcontext, "Please Check Your Internet Connection!", Toast.LENGTH_SHORT).show();
        }

    }

}


