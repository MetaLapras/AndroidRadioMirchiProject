package in.co.ashclan.mirchithunder;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;
import in.co.ashclan.mirchithunder.utils.PreferenceUtil;
import in.co.ashclan.mirchithunder.utils.RPResultListener;
import in.co.ashclan.mirchithunder.utils.RuntimePermissionUtil;

public class QRCodeReaderActivity extends AppCompatActivity implements View.OnClickListener {
    boolean hasCameraPermission = false;
    private static final String cameraPerm = Manifest.permission.CAMERA;

    SurfaceView mySurfaceView;
    QREader qrEader;
    TextView textView;
    ImageView stateBtn,restartBtn;
    MaterialEditText edtQRCodeVerifcation;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_reader2);

        mIinit();
        hasCameraPermission = RuntimePermissionUtil.checkPermissonGranted(this, cameraPerm);
        stateBtn.setVisibility(View.VISIBLE);

        //Setup SurfaceView

        if (hasCameraPermission){
         setUpQREader();
        }else{
            RuntimePermissionUtil.requestPermission((Activity) mContext, cameraPerm, 100);
        }


    }

    private void mIinit(){
        mContext = QRCodeReaderActivity.this;
        mySurfaceView = (SurfaceView)findViewById(R.id.camera_view);

        textView =(TextView) findViewById(R.id.code_info);
        stateBtn = (ImageView) findViewById(R.id.btn_start_stop);
        restartBtn = (ImageView) findViewById(R.id.btn_restart_activity);

        stateBtn.setOnClickListener(this);
        restartBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view==stateBtn){
            if (qrEader.isCameraRunning()){
                //stateBtn.setText("Start QREader");
                stateBtn.setImageDrawable(getResources().getDrawable(R.drawable.aar_ic_play));
                qrEader.stop();
            }else{
               // stateBtn.setText("Stop QREader");
                stateBtn.setImageDrawable(getResources().getDrawable(R.drawable.aar_ic_stop));
                qrEader.start();
            }
        }else if (view==restartBtn){
            restartActivity();
        }
    }

    public void restartActivity() {
        startActivity(new Intent(mContext, QRCodeReaderActivity.class));
        finish();
    }

    public void setUpQREader(){
        qrEader = new QREader.Builder(mContext, mySurfaceView, new QRDataListener() {
            @Override
            public void onDetected(final String data) {
                Log.d("QREader", "Value : " + data);
                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(data);
                        //**********************************
                       // Dialog box
                        /*******************************************/
                       // showQRVerificationDialog(data);
                    }
                });
            }
        }).facing(QREader.BACK_CAM)
                .enableAutofocus(true)
                .height(mySurfaceView.getHeight())
                .width(mySurfaceView.getWidth())
                .build();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (hasCameraPermission){
            qrEader.releaseAndCleanup();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasCameraPermission){
            qrEader.initAndStart(mySurfaceView);
        }
    }

    private void showQRVerificationDialog(final String data) {
        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(mContext);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View add_menu_layout = layoutInflater.inflate(R.layout.custom_qrverification,null);

        edtQRCodeVerifcation = (MaterialEditText) add_menu_layout.findViewById(R.id.edt_QrCOdeID);
        alertDialog.setView(add_menu_layout);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(edtQRCodeVerifcation.getText().toString().equals(data))
                {
                    PreferenceUtil.setQrcodeid(mContext,edtQRCodeVerifcation.getText().toString());
                    Toast.makeText(mContext, "Verification Successful", Toast.LENGTH_SHORT).show();
                    finish();
                }else
                {
                    Toast.makeText(mContext, "Invalid QR Code", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        if (requestCode == 100) {
            RuntimePermissionUtil.onRequestPermissionsResult(grantResults, new RPResultListener() {
                @Override
                public void onPermissionGranted() {
                    if ( RuntimePermissionUtil.checkPermissonGranted(mContext, cameraPerm)) {
                        restartActivity();
                    }
                }

                @Override
                public void onPermissionDenied() {
                    // do nothing
                }
            });
        }
    }



}
