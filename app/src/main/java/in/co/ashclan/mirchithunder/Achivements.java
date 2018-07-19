package in.co.ashclan.mirchithunder;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alexfu.countdownview.CountDownView;

public class Achivements extends AppCompatActivity {

    Context mContext;
    CountDownView countDownView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achivements);
        mInit();

    }
    private void mInit() {
        mContext = Achivements.this;
    }
}
