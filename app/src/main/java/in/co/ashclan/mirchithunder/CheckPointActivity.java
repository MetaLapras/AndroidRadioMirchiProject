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
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;
import in.co.ashclan.mirchithunder.database.DataBaseHelper;
import in.co.ashclan.mirchithunder.model.BatchModel;
import in.co.ashclan.mirchithunder.model.CheckPointModel;
import in.co.ashclan.mirchithunder.utils.PreferenceUtil;
import in.co.ashclan.mirchithunder.utils.RPResultListener;
import in.co.ashclan.mirchithunder.utils.RuntimePermissionUtil;

public class CheckPointActivity extends AppCompatActivity implements View.OnClickListener {
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
    TextView txt_checkptTime;

    FirebaseDatabase database;
    DatabaseReference table_checkPt ;
    DatabaseReference table_participant ;
    DatabaseReference table_batch ;
    CheckPointModel checkPointModel;
    String puid,batch,starttime,checkpointtime,endpointtime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_check_point);
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
        mContext = CheckPointActivity.this;
        mySurfaceView = (SurfaceView)findViewById(R.id.camera_view_checkpt);

        textView =(TextView) findViewById(R.id.code_info_checkpt);
        stateBtn = (ImageView) findViewById(R.id.btn_start_stop_checkpt);
        restartBtn = (ImageView) findViewById(R.id.btn_restart_activity_checkpt);

        txt_checkptTime = (TextView)findViewById(R.id.txt_checkpt_time);

        stateBtn.setOnClickListener(this);
        restartBtn.setOnClickListener(this);

        database            = FirebaseDatabase.getInstance();
        table_checkPt   = database.getReference("RunnerState");
        table_participant   = database.getReference("Participant");
        table_batch   = database.getReference("Batch");

        dataBaseHelper = new DataBaseHelper(mContext);
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
                        storeQrCheckPointData(data);
                    }
                });
            }
        }).facing(QREader.BACK_CAM)
                .enableAutofocus(true)
                .height(mySurfaceView.getHeight())
                .width(mySurfaceView.getWidth())
                .build();
    }
    private void storeQrCheckPointData(final String data) {

        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please Wait .... ");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        qrEader.stop();

        if(data!=null)
        {
            puid = data;
            //batch = PreferenceUtil.getBatchname(mContext);
           // starttime = PreferenceUtil.getStarttime(mContext);
            checkpointtime = getCheckPointTime();

            //Get Batch Details
            table_batch.orderByValue().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Log.e("-->batch",dataSnapshot.toString());
                    if(dataSnapshot.exists())
                    {
                        BatchModel model = dataSnapshot.getValue(BatchModel.class);
                        //starttime = model.getBatchStartTime().toString();
                        Log.e("-->time",model.toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            progressDialog.dismiss();
                        Map<String,Object> startingTime = new HashMap<>();
                        startingTime.put("start_time",starttime);

                        Map<String,Object> checkpttime = new HashMap<>();
                        checkpttime.put("checkpoint_time",checkpointtime);

                        //table_checkPt.child(puid).updateChildren(startingTime);

                        table_checkPt.child(puid).updateChildren(checkpttime);

                        Intent intent = new Intent(mContext,MirchiSelfie4Activity.class);
                        intent.putExtra("position",1);
                        intent.putExtra("puid",puid);
                        startActivity(intent);
                        finish();

            /*ArrayList<CheckPointModel> checkPointModels = dataBaseHelper.getAllParticipant();
            for(int i = 0;i<checkPointModels.size();i++)
            {
                checkPointModel = new CheckPointModel();
                checkPointModel.setPuid(puid);
                checkPointModel.setStart_time(checkPointModels.get(i).getStart_time());
                checkPointModel.setCheckpoint_time(checkpointtime);
                checkPointModel.setEndPointTime(checkPointModels.get(i).getEndPointTime());
                checkPointModel.setStartptImage(checkPointModels.get(i).getStartptImage());
                checkPointModel.setCheckptImage(checkPointModels.get(i).getCheckptImage());
                checkPointModel.setEndptImage(checkPointModels.get(i).getEndptImage());

            if(!dataBaseHelper.isIdAvailable(puid)) //check if the PUID and BATCH PRESENT if NOT Then ADD
            {
                dataBaseHelper.onCheckpointAdd(checkPointModel);
                PreferenceUtil.setPuid(mContext,puid);
                PreferenceUtil.setBatchname(mContext,batch);
                Toast.makeText(mContext, " Runner is Recorded !", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(mContext,MirchiSelfie4Activity.class);
                intent.putExtra("position",1);
                intent.putExtra("puid",puid);
                startActivity(intent);
                finish();
            }else
            {
                Toast.makeText(mContext, "Runner is Not Available For This Batch !", Toast.LENGTH_SHORT).show();
                finish();
            }
            }*/
        }else
        {
            Toast.makeText(mContext, "Trouble !", Toast.LENGTH_SHORT).show();
        }
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