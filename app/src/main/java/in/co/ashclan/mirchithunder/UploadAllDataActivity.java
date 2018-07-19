package in.co.ashclan.mirchithunder;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.co.ashclan.mirchithunder.database.DataBaseHelper;
import in.co.ashclan.mirchithunder.model.BatchModel;
import in.co.ashclan.mirchithunder.model.CheckPointModel;
import in.co.ashclan.mirchithunder.utils.PreferenceUtil;
import in.co.ashclan.mirchithunder.utils.util;

public class UploadAllDataActivity extends AppCompatActivity {
    ImageView img_upload;
    FirebaseDatabase database;
    DatabaseReference table_checkPt ;
    DatabaseReference table_batch ;
    ArrayList<CheckPointModel> list ;
    String starttime;
    Context mContext;

    DataBaseHelper dataBaseHelper ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_all_data);
        img_upload = (ImageView)findViewById(R.id.img_upload_data);
        database = FirebaseDatabase.getInstance();
        table_checkPt   = database.getReference("RunnerState");
        table_batch   = database.getReference("Batch");
        mContext =UploadAllDataActivity.this;


        dataBaseHelper = new DataBaseHelper(UploadAllDataActivity.this);
        list = dataBaseHelper.getAllParticipant();

        table_batch.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    BatchModel model = dataSnapshot.getValue(BatchModel.class);
                    starttime = model.getBatchStartTime();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        img_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertintoFireBase();
            }
        });
    }
    private void insertintoFireBase() {

     if(util.isConnectedToInterNet(mContext))
            {
            final ProgressDialog mDialog = new ProgressDialog(mContext);
            mDialog.setMessage("Please Wait.....");
            mDialog.setCancelable(false);
            mDialog.show();

                table_checkPt.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(int i = 0;i<list.size();i++) {

                            final int finalI = i;

                            CheckPointModel checkPointModel = new CheckPointModel();
                            checkPointModel.setId(list.get(finalI).getId().toString());
                            checkPointModel.setPuid(list.get(finalI).getPuid().toString());
                            checkPointModel.setBatch(list.get(finalI).getBatch().toString());
                            checkPointModel.setStart_time(starttime);
                            checkPointModel.setCheckpoint_time(list.get(finalI).getCheckpoint_time().toString());
                            checkPointModel.setEndPointTime(list.get(finalI).getEndPointTime().toString());
                            checkPointModel.setStartptImage(list.get(finalI).getStartptImage().toString());
                            checkPointModel.setCheckptImage(list.get(finalI).getCheckptImage().toString());
                            checkPointModel.setEndptImage(list.get(finalI).getEndptImage().toString());

                            if (checkPointModel != null)
                            {
                                table_checkPt.child(list.get(finalI).getPuid().toString()).setValue(checkPointModel);
                                Toast.makeText(mContext, "Data Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                mDialog.dismiss();
                            }else
                            {
                                 mDialog.dismiss();
                                Toast.makeText(mContext, "Somthing went Wrong! Please Try After some time", Toast.LENGTH_SHORT).show();
                            }

                        }
                        }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(mContext, "Somthing went Wrong! Please Try After some time", Toast.LENGTH_SHORT).show();
                        // mDialog.dismiss();
                    }
                });
            }else
            {
                Toast.makeText(mContext, "Please Check your Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }
}
