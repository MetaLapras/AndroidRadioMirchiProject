package in.co.ashclan.mirchithunder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import in.co.ashclan.mirchithunder.model.ParticipantModel;
import in.co.ashclan.mirchithunder.utils.PreferenceUtil;
import in.co.ashclan.mirchithunder.utils.util;

public class SplashScreen extends AppCompatActivity {

    Context mcontext;
    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //checkConnection();
        if (PreferenceUtil.getSignIn(this)) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreen.this, DashBoard.class);
                    startActivity(intent);
                    finish();
                    //        layout.setVisibility(View.VISIBLE);
                    //login(PreferenceUtil.getMobileNo(mcontext).toString(),PreferenceUtil.getPass(mcontext).toString());

                }
            }, 1500);
        } else {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreen.this, ParticipantsLogin.class);
                    startActivity(intent);
                    finish();
                }
            }, 1500);
        }
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
