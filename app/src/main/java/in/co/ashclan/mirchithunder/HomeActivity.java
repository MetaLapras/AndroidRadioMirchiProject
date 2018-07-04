package in.co.ashclan.mirchithunder;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mInit();
        Log.e("--->MEMBER>> ","");
        Toast.makeText(mContext,"",Toast.LENGTH_LONG).show();
    }


    private void mInit(){
        mContext = HomeActivity.this;

    }
}
