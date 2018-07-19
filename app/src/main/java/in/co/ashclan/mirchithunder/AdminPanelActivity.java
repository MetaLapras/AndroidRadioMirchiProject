package in.co.ashclan.mirchithunder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextClock;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.co.ashclan.mirchithunder.database.DataBaseHelper;
import in.co.ashclan.mirchithunder.model.BatchModel;
import in.co.ashclan.mirchithunder.model.ImagesModel;
import in.co.ashclan.mirchithunder.model.ParticipantModel;
import in.co.ashclan.mirchithunder.utils.PreferenceUtil;
import in.co.ashclan.mirchithunder.utils.util;

public class AdminPanelActivity extends AppCompatActivity implements View.OnClickListener {

    TextClock txt_Start_time;
    Spinner spn_Batch;
    Context mContext;
    DataBaseHelper dataBaseHelper;
    BatchModel batchModel;
    ArrayList<BatchModel> BatchArray;
    Button btn_StartRace;

    String batch,time,batchid ;

    //FirebaseDatabase
    FirebaseDatabase database;
    DatabaseReference table_batch ;
    DatabaseReference table_checkpoint ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
        mInit();
        btn_StartRace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(spn_Batch.getSelectedItem().toString().equals("Select Batch"))
                {
                    txt_Start_time.setError("Please Select a Batch");
                    Toast.makeText(mContext, "Please Select a batch", Toast.LENGTH_SHORT).show();
                }else
                {
                    batch    = spn_Batch.getSelectedItem().toString().trim();
                   // time     = txt_Start_time.getText().toString();
                    time     = getCheckPointTime();
                    for(int i = 0;i<BatchArray.size();i++)
                    {
                        if(batch == BatchArray.get(i).getBatchName())
                        {
                            batchid = BatchArray.get(i).getBatchid();
                        }
                    }
                    insertIntoFirebase(batchid,batch,time);
                    insertIntoSqlite(batchid,batch,time);
                    finish();
                }
            }
        });
    }

    private void mInit() {
        mContext = AdminPanelActivity.this;

        txt_Start_time = (TextClock)findViewById(R.id.txt_start_time);
        spn_Batch = (Spinner)findViewById(R.id.spn_batch);
        btn_StartRace = (Button)findViewById(R.id.btn_start_race);
        batchModel = new BatchModel();
        dataBaseHelper = new DataBaseHelper(mContext);
        loadSpinner();

        database = FirebaseDatabase.getInstance();
        table_batch = database.getReference("Batch");
        table_checkpoint = database.getReference("RunnerState");
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
        spn_Batch.setAdapter(adp1); // spn new table
    }
    @Override
    public void onClick(View view) {
    }

    private void insertIntoSqlite(String batchid, String batch, String time) {
        dataBaseHelper = new DataBaseHelper(mContext);
        dataBaseHelper.updateBatchTime(time,batchid);
    }

    private void insertIntoFirebase(final String batchid, final String batch, final String time) {
        if(util.isConnectedToInterNet(mContext))
        {
            /*final ProgressDialog mDialog = new ProgressDialog(mContext);
            mDialog.setMessage("Please Wait.....");
            mDialog.setCancelable(false);
            mDialog.show();*/

            table_batch.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.child(batchid).exists())
                    {
                        batchModel = new BatchModel();

                        batchModel.setBatchName(batch);
                        batchModel.setBatchid(batchid);
                        batchModel.setBatchStartTime(time);
                        batchModel.setBatchstatus("Allotted");
                        if(batchModel!=null)
                        {
                            table_batch.child(batch).setValue(batchModel);
                            Toast toast = Toast.makeText(mContext, "Congratulations Batch successfully", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();

                            /*PreferenceUtil.setBatchid(mContext,batchid);
                            PreferenceUtil.setBatchname(mContext,batch);*/
                            PreferenceUtil.setStarttime(mContext,time);

                        }else
                        {
                           // mDialog.dismiss();
                            Toast.makeText(mContext, "Somthing went Wrong! Please Try After some time", Toast.LENGTH_SHORT).show();
                        }
                    }else
                    {
                        //mDialog.dismiss();
                        Toast.makeText(mContext, "Batch Already has Started Please Select another Batch", Toast.LENGTH_SHORT).show();
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

    /////////////////////////database to spn
    /*
     List<String> labels = new ArrayList<String>();
            labels.add("Select New Table");
            for(int i = 0; i<emptyTableList.size(); i++)
            {
                labels.add(emptyTableList.get(i).getT_id().toString());
            }

            ArrayAdapter<String> adp1 = new ArrayAdapter<String>
                    (activity,R.layout.spinner_item,labels);
            adp1.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spnnewtable.setAdapter(adp1); // spn new table
     */

    private String getCheckPointTime() {
        DateFormat tf = new SimpleDateFormat(" HH:mm:ss");
        String time = tf.format(Calendar.getInstance().getTime());
        return time;
    }

}
