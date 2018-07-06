package in.co.ashclan.mirchithunder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class LoginScreen extends AppCompatActivity implements View.OnClickListener{
   LinearLayout linearLayout;
   ImageView thunder_logo;
   Context mContext;
   Button participants, volunteer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        init();

        participants = findViewById(R.id.btn_Participant);
        volunteer = findViewById(R.id.btn_volunteer);

        volunteer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginScreen.this,VolunteerLogin.class);
                startActivity(i);
                overridePendingTransition(R.anim.fadein,R.anim.fadeout);
            }
        });

        participants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginScreen.this,ParticipantsLogin.class);
                startActivity(i);
                overridePendingTransition(R.anim.fadein,R.anim.fadeout);
            }
        });

    }

    private void init() {
        mContext = LoginScreen.this;
        thunder_logo = (ImageView)findViewById(R.id.thudner_logo);
        thunder_logo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(mContext,QRCodeReaderActivity.class);
        startActivity(intent);
      //  startActivity(new Intent(mContext,QRCodeReaderActivity.class));
    }

}
