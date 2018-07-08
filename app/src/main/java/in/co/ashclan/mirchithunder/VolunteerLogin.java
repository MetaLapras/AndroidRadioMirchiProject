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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_login);
        inti();
        btnLogin.setOnClickListener(this);
    }

    private void inti() {
        mContext = VolunteerLogin.this;
        edtUserId = (MaterialEditText)findViewById(R.id.edt_userid);
        edtPassword = (MaterialEditText)findViewById(R.id.edt_password);
        btnLogin = (FButton)findViewById(R.id.btn_Login);

        //Fire Base
        database = FirebaseDatabase.getInstance();
        table_volunteer = database.getReference("Volunteer");
    }

    @Override
    public void onClick(View view) {
        if (util.isConnectedToInterNet(getBaseContext())){
            //Save username and password
//            if(chkRememberMe.isChecked())
//            {
//                Paper.book().write(Common.USER_KEY,edtPhone.getText().toString());
//                Paper.book().write(Common.PWD_KEY,edtPassword.getText().toString());
//            }
            final ProgressDialog mDialog = new ProgressDialog(VolunteerLogin.this);
            mDialog.setMessage("Please Wait.....");
            mDialog.show();

            table_volunteer.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Check if User doesnt exist in database
                    if (dataSnapshot.child(edtUserId.getText().toString()).exists()) {
                        mDialog.dismiss();

                        //get User Values
                        VolunteerModel user = dataSnapshot.child(edtUserId.getText().toString()).getValue(VolunteerModel.class);
                        user.setMobile(edtUserId.getText().toString());
                        if (user.getPassword().equals(edtPassword.getText().toString())) {
                            Toast.makeText(getApplicationContext(),"Successful",Toast.LENGTH_LONG).show();
//                            startActivity(new Intent(Sign_IN.this, Home.class));
//                            Common.currentUser = user;
//                            finish();

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
