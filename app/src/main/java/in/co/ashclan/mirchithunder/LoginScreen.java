package in.co.ashclan.mirchithunder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.support.v7.widget.CardView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

public class LoginScreen extends AppCompatActivity {
   LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        linearLayout = findViewById(R.id.linear);


        final Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
       linearLayout.startAnimation(animationFadeIn);
    }
}
