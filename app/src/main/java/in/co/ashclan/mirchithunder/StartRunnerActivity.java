package in.co.ashclan.mirchithunder;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;
import in.co.ashclan.mirchithunder.database.DataBaseHelper;
import in.co.ashclan.mirchithunder.model.BatchModel;
import in.co.ashclan.mirchithunder.model.CheckPointModel;
import in.co.ashclan.mirchithunder.utils.PreferenceUtil;
import in.co.ashclan.mirchithunder.utils.RPResultListener;
import in.co.ashclan.mirchithunder.utils.RuntimePermissionUtil;
import in.co.ashclan.mirchithunder.utils.util;

public class StartRunnerActivity extends AppCompatActivity implements View.OnClickListener {
    boolean hasCameraPermission = false;
    private static final String cameraPerm = Manifest.permission.CAMERA;

    SurfaceView mySurfaceView;
    QREader qrEader;
    TextView textView,txtPuid;
    ImageView stateBtn,restartBtn;
    Context mContext;
    Spinner spn_batch;
    DataBaseHelper dataBaseHelper;
    BatchModel batchModel;
    ArrayList<BatchModel> BatchArray;

    FirebaseDatabase database;
    DatabaseReference table_checkPt ;
    DatabaseReference table_participant ;
    CheckPointModel checkPointModel;
    String puid,batch,starttime,checkpointtime,endpointtime;
    String strPoint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_start_runner);
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
        mContext = StartRunnerActivity.this;
        mySurfaceView = (SurfaceView)findViewById(R.id.camera_view_start);

        textView =(TextView) findViewById(R.id.code_info_start);
        stateBtn = (ImageView) findViewById(R.id.btn_start_stop_start);
        restartBtn = (ImageView) findViewById(R.id.btn_restart_activity_start);

        stateBtn.setOnClickListener(this);
        restartBtn.setOnClickListener(this);


        dataBaseHelper = new DataBaseHelper(mContext);

        database = FirebaseDatabase.getInstance();
        table_checkPt = database.getReference("RunnerState");
        table_participant   = database.getReference("Participant");
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
                        //showQRVerificationDialog(data);
                        //storeQrCheckPointData(data);
                            showQRVerificationDialog(data);
                    }
                });
            }
        }).facing(QREader.BACK_CAM)
                .enableAutofocus(true)
                .height(mySurfaceView.getHeight())
                .width(mySurfaceView.getWidth())
                .build();
    }

    private void showQRVerificationDialog(final String data) {

        final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(mContext);
        LayoutInflater layoutInflater = this.getLayoutInflater();

        qrEader.stop();

        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please Wait .... ");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        View layout = layoutInflater.inflate(R.layout.custom_startpoint_dialog,null);
        txtPuid = (TextView)layout.findViewById(R.id.txt_puid);
        spn_batch = (Spinner)layout.findViewById(R.id.spn_batch_dialog);
        alertDialog.setView(layout);
        txtPuid.setText(puid);

        loadSpinner();

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(data!=null)
                {
                    puid = data;
                    batch = spn_batch.getSelectedItem().toString().trim();

                    progressDialog.dismiss();

                    checkPointModel = new CheckPointModel();
                    checkPointModel.setPuid(puid);
                    checkPointModel.setBatch(batch);
                    checkPointModel.setStart_time("empty");
                    checkPointModel.setCheckpoint_time("empty");
                    checkPointModel.setEndPointTime("empty");
                    checkPointModel.setStartptImage("empty");
                    checkPointModel.setCheckptImage("empty");
                    checkPointModel.setEndptImage("empty");

                    insertIntoFirebase(puid,batch);

                 /*   if(!dataBaseHelper.isIdAvailable(puid)) //check if the PUID and BATCH PRESENT if NOT Then ADD
                    {
                        dataBaseHelper.onCheckpointAdd(checkPointModel);
                        PreferenceUtil.setPuid(mContext,puid);
                        PreferenceUtil.setBatchname(mContext,batch);
                        Toast.makeText(mContext, " Runner is Recorded !", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(mContext,MirchiSelfie4Activity.class);
                        intent.putExtra("position",0);
                        startActivity(intent);
                        finish();

                    }else
                    {
                        Toast.makeText(mContext, "Already Runner is Recorded !", Toast.LENGTH_SHORT).show();
                    }*/
                }else
                {
                    Toast.makeText(mContext, "Trouble !", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(mContext, "Operation Cancel by User", Toast.LENGTH_SHORT).show();
                qrEader.start();
                dialog.dismiss();
                progressDialog.dismiss();
            }
        });
        alertDialog.show();
    }
    private void insertIntoFirebase(final String data,final String batch) {

        if(util.isConnectedToInterNet(mContext))
        {
            table_checkPt.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(!dataSnapshot.child(data).exists())
                    {
                        checkPointModel = new CheckPointModel();
                        checkPointModel.setPuid(data);
                        checkPointModel.setBatch(batch);
                        checkPointModel.setStart_time("empty");
                        checkPointModel.setCheckpoint_time("empty");
                        checkPointModel.setEndPointTime("empty");
                        checkPointModel.setStatus("Allotted");
                        checkPointModel.setStartptImage("empty");
                        checkPointModel.setCheckptImage("empty");
                        checkPointModel.setEndPointTime("empty");

                        if(checkPointModel!=null)
                        {
                            table_checkPt.child(data).setValue(checkPointModel);
                            Toast toast = Toast.makeText(mContext, "Congratulations Batch successfully", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();

                            Intent intent = new Intent(mContext,MirchiSelfie4Activity.class);
                            intent.putExtra("position",0);
                            intent.putExtra("puid",puid);
                            startActivity(intent);
                            finish();

                        }else
                        {
                            Toast.makeText(mContext, "Somthing went Wrong! Please Try After some time", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(mContext, "You have Already Registred for this batch", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(mContext, "Somthing went Wrong! Please Try After some time", Toast.LENGTH_SHORT).show();
                }
            });
        }else
        {
            Toast.makeText(mContext, "Please Check your Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }
    private void storeQREndPointData(String data) {}
    private void storeQrCheckPointData(String data) {
      /*  final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please Wait .... ");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        qrEader.stop();

        if(data!=null)
        {
            puid = data;
            batch = PreferenceUtil.getBatchname(mContext);
            starttime = PreferenceUtil.getStarttime(mContext);
            checkpointtime = getCheckPointTime();
            endpointtime = "0";

            progressDialog.dismiss();

            checkPointModel = new CheckPointModel();
            checkPointModel.setPuid(puid);
            checkPointModel.setBatch(batch);
            checkPointModel.setStart_time(starttime);
            checkPointModel.setCheckpoint_time(checkpointtime);
            checkPointModel.setEndPointTime(endpointtime);

            if(!dataBaseHelper.isIdAvailable(puid,batch))
            {
                dataBaseHelper.onCheckpointAdd(checkPointModel);
                Toast.makeText(mContext, " Runner is Recorded !", Toast.LENGTH_SHORT).show();
                Log.e("-->qr", "puid : "+puid + " Batch :" + batch + " starttime : " + starttime  + " Checkpoint Time :" + checkpointtime + " endpoint :"+endpointtime);
            }else
            {
                Toast.makeText(mContext, "Already Runner is Recorded !", Toast.LENGTH_SHORT).show();
            }
        }else
        {
            Toast.makeText(mContext, "Trouble !", Toast.LENGTH_SHORT).show();
        }*/


    }
    private String getCheckPointTime() {
        DateFormat tf = new SimpleDateFormat(" HH:mm:ss");
        String time = tf.format(Calendar.getInstance().getTime());
        return time;
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
    private void loadSpinner() {
        BatchArray = (ArrayList<BatchModel>) dataBaseHelper.getAllBatchs();
        List<String> labels = new ArrayList<String>();
        labels.add("Select Batch");
        for(int i = 0; i<BatchArray.size(); i++)
        {
            labels.add(BatchArray.get(i).getBatchName().toString());
        }
        ArrayAdapter<String> adp1 = new ArrayAdapter<String>
                (mContext,android.R.layout.simple_list_item_1,labels);
        adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_batch.setAdapter(adp1); // spn new table
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
