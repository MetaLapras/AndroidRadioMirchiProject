package in.co.ashclan.mirchithunder;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {

    Context mcontext;
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ImageView image = findViewById(R.id.imgLogo);

        mInit();

        Log.e("--->MEMBER>>","Splash Screen");


        //for animation
       /* final Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
        final Animation animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        image.startAnimation(animationFadeIn);
        image.startAnimation(animationFadeOut);*/

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreen.this, LoginScreen.class);
                startActivity(i);
                overridePendingTransition(R.anim.fadein,R.anim.fadeout);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    private void overridePendingTransition(int fadein) {
    }


    private void mInit() {
        mcontext = SplashScreen.this;
    }
}
