package in.co.ashclan.mirchithunder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

public class ScanRunnerActivity extends AppCompatActivity implements View.OnClickListener {
    CardView cardStartPoint,cardCheckPoint,cardEndPoint;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_runner);
        mInit();

        cardStartPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,StartRunnerActivity.class);
                startActivity(intent);
                finish();
            }
        });

        cardEndPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,EndPointActivity.class);
                startActivity(intent);
                finish();
            }
        });
        cardCheckPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,CheckPointActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void mInit() {
        mContext = ScanRunnerActivity.this;

        cardStartPoint = (CardView) findViewById(R.id.card_StartPoint) ;
        cardCheckPoint = (CardView)findViewById(R.id.card_CheckPoint);
        cardEndPoint = (CardView)findViewById(R.id.card_EndPoint);
    }

    @Override
    public void onClick(View view) {
        if(view == cardStartPoint)
        {

        }else if(view == cardCheckPoint) {

        }else if(view == cardEndPoint) {

        }
    }
}
