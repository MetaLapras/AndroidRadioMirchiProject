package in.co.ashclan.mirchithunder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import in.co.ashclan.mirchithunder.database.DataBaseHelper;
import in.co.ashclan.mirchithunder.model.BatchModel;
import in.co.ashclan.mirchithunder.model.VolunteerModel;
import in.co.ashclan.mirchithunder.utils.util;
import info.hoang8f.widget.FButton;
import mehdi.sakout.fancybuttons.Utils;

public class VolunteerLogin extends AppCompatActivity implements View.OnClickListener {

    MaterialEditText edtUserId,edtPassword;
    FButton btnLogin;
    Context mContext;

    FirebaseDatabase database;
    DatabaseReference table_volunteer ;
    String userid,pass;
    DataBaseHelper dataBaseHelper ;

    //Model class
    BatchModel batchModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_login);
        mInti();
        btnLogin.setOnClickListener(this);
    }

    private void mInti() {
        mContext = VolunteerLogin.this;
        edtUserId = (MaterialEditText)findViewById(R.id.edt_userid);
        edtPassword = (MaterialEditText)findViewById(R.id.edt_password);
        btnLogin = (FButton)findViewById(R.id.btn_Login);

        //Fire Base
        database = FirebaseDatabase.getInstance();
        table_volunteer = database.getReference("Volunteer");
        batchModel = new BatchModel();

        //SqlLite Database
        dataBaseHelper = new DataBaseHelper(mContext);
        final Boolean isExist = dataBaseHelper.checkBatchExist();
                if(!isExist) //if not exist create table with data
                {
                    for(int i = 1 ; i<=30;i++)
                    {
                        String Batch = "batch "+ i;
                        batchModel.setBatchName(Batch);
                        batchModel.setBatchstatus("NotAllotted");
                        dataBaseHelper.onBatchAdd(batchModel);
                    }
                }
    }

    @Override
    public void onClick(View view) {
        if(view == btnLogin)
        {
            userid = edtUserId.getText().toString();
            pass = edtPassword.getText().toString();

            if(edtUserId.getText().length()==0)
            {
                edtUserId.setError("Please Enter Your Volunteer Id");
            } else if (edtPassword.getText().length()==0) {
                edtPassword.setError("Please enter correct password");
            }else {
               userid = edtUserId.getText().toString();
                edtPassword.getText().toString();
                login(userid, pass);
             startActivity(new Intent(mContext,VolunteerDashBoard_Activity.class));
            }
        }
    }

    private void login(String phone, final String pass) {
        if (util.isConnectedToInterNet(getBaseContext())){

            final ProgressDialog mDialog = new ProgressDialog(VolunteerLogin.this);
            mDialog.setMessage("Please Wait.....");
            mDialog.show();

            table_volunteer.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Check if User doesnt exist in database
                    if (dataSnapshot.child(userid).exists()) {
                        mDialog.dismiss();

                        //get User Values
                        VolunteerModel user = dataSnapshot.child(edtUserId.getText().toString()).getValue(VolunteerModel.class);
                        user.setMobile(edtUserId.getText().toString());//set user id
                        if (user.getPassword().equals(pass)) {
                            Toast.makeText(getApplicationContext(),"Successful",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(VolunteerLogin.this, VolunteerDashBoard_Activity.class));
                            finish();
                            table_volunteer.removeEventListener(this);

                        } else {
                            Toast.makeText(getApplicationContext(), "Wrong Password", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        mDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "User Doesnt Exist", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(VolunteerLogin.this, "Please Check Your Internet Connection!", Toast.LENGTH_SHORT).show();
        }
    }
}
