package in.co.ashclan.mirchithunder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.co.ashclan.mirchithunder.database.DataBaseHelper;
import in.co.ashclan.mirchithunder.model.BatchModel;
import in.co.ashclan.mirchithunder.model.CheckPointModel;
import in.co.ashclan.mirchithunder.model.ParticipantModel;
import in.co.ashclan.mirchithunder.model.RankingPojo;

public class RankingActivity extends AppCompatActivity implements View.OnClickListener {
    ListView myRankingGridView;
    Context mContext;
    DataBaseHelper dataBaseHelper;
    public static ArrayList<String> ArrayofName = new ArrayList<String>();
    //FirebaseDatabase
    FirebaseDatabase database;
    DatabaseReference table_batch ;
    DatabaseReference table_checkPt ;
    DatabaseReference table_participant ;
    RankingPojo rankingPojo ;

    private static HashMap<String, BatchModel> modelHashMap = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        mInit();

      /*  ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, ArrayofName);

        myRankingGridView.setAdapter(adapter);

        myRankingGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(),
                        ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
            }
        });*/


    }

    private void mInit() {
        mContext = RankingActivity.this;
        myRankingGridView = (ListView) findViewById(R.id.myRankingGridview);
        dataBaseHelper = new DataBaseHelper(mContext);

        database            = FirebaseDatabase.getInstance();
        table_checkPt       = database.getReference("RunnerState");
        table_participant   = database.getReference("Participant");
        table_batch         = database.getReference("Batch");

        getDatafromFirebase();
    }

    @Override
    public void onClick(View view) {
    }
    public void getDatafromFirebase()
    {
        final RankingPojo rankingPojo = new RankingPojo();

        table_batch.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    BatchModel model = snapshot.getValue(BatchModel.class);

                    rankingPojo.setBatch(model.getBatchName().toString());
                    rankingPojo.setStarttime(model.getBatchStartTime().toString());

                    Log.e("-->batch", model.toString());
                    Log.e("-->rank", rankingPojo.getBatch().toString());
                    Log.e("-->rank", rankingPojo.getStarttime().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        table_checkPt.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    CheckPointModel checkPointModel = snapshot.getValue(CheckPointModel.class);
                    rankingPojo.setPuid(checkPointModel.getPuid().toString());
                    rankingPojo.setEndtime(checkPointModel.getEndPointTime().toString());

                    Log.e("-->chkpt", checkPointModel.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        table_participant.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    ParticipantModel participantModel = snapshot.getValue(ParticipantModel.class);

                    if(participantModel.getPuid().toString()==rankingPojo.getPuid().toString())
                    {
                        rankingPojo.getName().toString();
                    }

                    Log.e("-->participant", participantModel.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Log.e("-->ranking", rankingPojo.toString());

    }

}
