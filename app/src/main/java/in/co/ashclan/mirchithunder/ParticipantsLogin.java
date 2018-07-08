package in.co.ashclan.mirchithunder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dmax.dialog.SpotsDialog;
import in.co.ashclan.mirchithunder.model.ParticipantModel;
import in.co.ashclan.mirchithunder.utils.util;
import info.hoang8f.widget.FButton;

public class ParticipantsLogin extends AppCompatActivity implements View.OnClickListener{

    FButton btn_facebook,btn_Gmail;
    FirebaseDatabase database;
    DatabaseReference table_participant ;
    Context mContext;
    public static final int REQUEST_CODE = 7171;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants_login);
        init();
        btn_Gmail.setOnClickListener(this);
        btn_facebook.setOnClickListener(this);
    }

    private void init() {
        mContext = ParticipantsLogin.this;
        btn_facebook = (FButton)findViewById(R.id.btn_facebook);
        btn_Gmail = (FButton)findViewById(R.id.btn_Gmail);

        //InIt FireBase
        database = FirebaseDatabase.getInstance();
        table_participant = database.getReference("Participant");//Linked to Participant table

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_facebook:
                startFacebookLogin();
                break;
            case R.id.btn_Gmail:
                startGmailLogin();
                break;
        }
    }

    private void startFacebookLogin() {

        Intent intent = new Intent(ParticipantsLogin.this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,configurationBuilder.build());
        startActivityForResult(intent,REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE)
        {
            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if(result.getError()!=null)
            {
                Toast.makeText(this, ""+result.getError().getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }else if(result.wasCancelled())
            {
                Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
                return;
            }
            else
            {
                if(result.getAccessToken()!=null)
                {
                    //Show Dialog
                    final AlertDialog watingDialog = new SpotsDialog(this);
                    watingDialog.show();
                    watingDialog.setMessage("Please Wait");
                    watingDialog.setCancelable(false);

                    //get Current Phone
                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(Account account) {
                            final String userphone = account.getPhoneNumber().toString();

                            //Check if User Exist on Firebase
                            table_participant.orderByKey().equalTo(userphone)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(!dataSnapshot.child(userphone).exists())//if Usernot Exist
                                            {
                                                ParticipantModel newUser = new ParticipantModel();
                                                newUser.setMobile(userphone);
                                                newUser.setName("");

                                                //add to fire base
                                                table_participant.child(userphone)
                                                        .setValue(newUser)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful())
                                                                    Toast.makeText(ParticipantsLogin.this, "User Register Successfully !", Toast.LENGTH_SHORT).show();

                                                                //Login
                                                                table_participant.child(userphone)
                                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                                                ParticipantModel localUser = dataSnapshot.getValue(ParticipantModel.class);
                                                                                startActivity(new Intent(ParticipantsLogin.this, QRCodeReaderActivity.class));
                                                                                util.currentParticipant = localUser;
                                                                                watingDialog.dismiss();
                                                                                finish();
                                                                            }
                                                                            @Override
                                                                            public void onCancelled(DatabaseError databaseError) {
                                                                            }
                                                                        });
                                                            }
                                                        });

                                            }else//if User Exist
                                            {
                                                table_participant.child(userphone)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                ParticipantModel localUser = dataSnapshot.getValue(ParticipantModel.class);
                                                                startActivity(new Intent(ParticipantsLogin.this, QRCodeReaderActivity.class));
                                                                util.currentParticipant = localUser;
                                                                watingDialog.dismiss();
                                                                finish();
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                        }
                        @Override
                        public void onError(AccountKitError accountKitError) {
                            Toast.makeText(ParticipantsLogin.this, ""+accountKitError.getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }

    private void startGmailLogin() {
    }
}
