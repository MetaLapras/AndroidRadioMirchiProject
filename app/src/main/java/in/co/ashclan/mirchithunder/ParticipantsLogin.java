package in.co.ashclan.mirchithunder;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.UUID;

import dmax.dialog.SpotsDialog;
import in.co.ashclan.mirchithunder.model.ParticipantModel;
import in.co.ashclan.mirchithunder.utils.util;
import info.hoang8f.widget.FButton;
import in.co.ashclan.mirchithunder.utils.PreferenceUtil;
import io.paperdb.Paper;


public class ParticipantsLogin extends AppCompatActivity
        implements View.OnClickListener{

    FButton btn_facebook,btn_Gmail,btn_submit;
    FirebaseDatabase database;
    DatabaseReference table_participant ;
    FirebaseStorage storage;
    Context mContext;

    //Get GMail Data from login Account
    public String personName,personFirstName,personLastName,personEmail,personId;
    Uri personPhoto ;

    //Root Layout
    LinearLayout linearLayout;

    //Participant Pojo
    ParticipantModel participantModel;

    //FireBase to Facebook
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "GoogleSignIn";

    //FireBase to Google
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    public static final int REQUEST_CODE = 7171;

    MaterialEditText edtUserName,edtPassword;
    String phone,pass;

    //Alert Dialog View
    MaterialEditText edtMobileNo;
    RadioButton rd_male,rd_female;

    //Upload Images
    Uri saveuri;
    StorageReference storageReference ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants_login);
        mInit();
        btn_Gmail.setOnClickListener(this);
        btn_facebook.setOnClickListener(this);
        btn_submit.setOnClickListener(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    //Initialisation
    private void mInit()
    {
        mContext = ParticipantsLogin.this;
        btn_facebook = (FButton)findViewById(R.id.btn_facebook);
        btn_Gmail = (FButton)findViewById(R.id.btn_Gmail);

        //InIt FireBase
        database = FirebaseDatabase.getInstance();
        table_participant = database.getReference("Participant"); //Linked to Participant table
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Init ROOT LAYOUT
        linearLayout = (LinearLayout)findViewById(R.id.root_layout);
        participantModel = new ParticipantModel();

        //Submit Button
        btn_submit = (FButton)findViewById(R.id.btn_submit);
        edtUserName = (MaterialEditText)findViewById(R.id.edt_User_id);
        edtPassword = (MaterialEditText)findViewById(R.id.edt_User_password);

    }

    //OnclickListner
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_facebook:
                startFacebookLogin();
                break;
            case R.id.btn_Gmail:
                startGmailLogin();
                break;
            case R.id.btn_submit:
                        //Save username and password
                            phone = "+91"+ edtUserName.getText().toString();
                            pass = edtPassword.getText().toString();

                            if(edtUserName.getText().length()==0)
                            {
                                edtUserName.setError("Please Enter Your Mobile No");
                            } else if (edtPassword.getText().length()==0) {
                                edtPassword.setError("Please enter correct password");
                            }else {
                                login(phone, pass);
                            }
                        //startActivity(new Intent(mContext,RegistrationActivity.class));
                   break;
                }
        }

    //Methods
    private void startFacebookLogin()
    {
        Intent intent = new Intent(ParticipantsLogin.this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,configurationBuilder.build());
        startActivityForResult(intent,REQUEST_CODE);
    }
    private void startGmailLogin()
    {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            //Activity result set for facebook login
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
                    if(result.getAccessToken() != null)
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
                                String phone  = account.getPhoneNumber().toString();
                                final String userphone = phone.substring(3);
                                Log.e("fbno-->",userphone);

                                //Check if User Exist on Firebase if not then add it
                                table_participant.orderByKey().equalTo(userphone)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                if(!dataSnapshot.child(userphone).exists())//if User not Exist
                                                {
                                                    Intent intent = new Intent(ParticipantsLogin.this,RegistrationActivity.class);
                                                    intent.putExtra("mobilno",userphone);
                                                    startActivity(intent);
                                                    watingDialog.dismiss();
                                                    finish();

                                                }else //if User Exist
                                                {
                                                    startActivity(new Intent(ParticipantsLogin.this, Activity_DashBoard2.class));
                                                   /* table_participant.child(userphone)
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                                    Log.e("-->123",dataSnapshot.toString());

                                                                    ParticipantModel localUser = dataSnapshot.getValue(ParticipantModel.class);
                                                                    util.currentParticipant = localUser;
                                                                    startActivity(new Intent(ParticipantsLogin.this, Activity_DashBoard2.class));
                                                                    watingDialog.dismiss();
                                                                    finish();
                                                                }
                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {
                                                                }
                                                            });*/
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
        }catch (Exception e)
        {
            Log.e("-->FbError",e.toString());
        }


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }

        //Result returned from launching FirebaseDialog
        if(requestCode== util.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null)
        {
           // saveuri = data.getData();
            //btn_Select.setText("Image Selected !");
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct)
    {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        //Show Dialog
        final AlertDialog watingDialog = new SpotsDialog(this);
        watingDialog.show();
        watingDialog.setMessage("Please Wait");
        watingDialog.setCancelable(false);

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            //showFireBaseDialog();
                            watingDialog.dismiss();
                            //If user is not registred then register it
                           // startActivity(new Intent(ParticipantsLogin.this,RegistrationActivity.class));
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                           // Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
    //User Login
    private void login(final String phone, final String pass)
    {

        try
        {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference table_user = database.getReference("Participant");

            Log.d("-->",phone +" "+pass);

            if (util.isConnectedToInterNet(getBaseContext())) {

                final ProgressDialog mDialog = new ProgressDialog(ParticipantsLogin.this);
                mDialog.setMessage("Please Wait.....");
                mDialog.show();

                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Check if User doesnt exist in database
                        if (dataSnapshot.child(phone).exists()) {
                            //mDialog.dismiss();
                            //get User Values
                            Log.d("-->123",dataSnapshot.toString());
                            ParticipantModel user = dataSnapshot.child(phone).getValue(ParticipantModel.class);
                            user.setMobile(phone);
                            Log.d("PoJo-->",user.toString());
                            if (user.getFirstname().equals(pass)) {
                                //Toast.makeText(getApplicationContext(),"Successful",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(ParticipantsLogin.this, Activity_DashBoard2.class));
                                util.currentParticipant = user;

                                PreferenceUtil.setSignIn(ParticipantsLogin.this,true);
                                PreferenceUtil.setMobileNo(mContext,phone);
                                PreferenceUtil.setPass(mContext,pass);
                                mDialog.dismiss();
                                finish();
                            } else {
                                mDialog.dismiss();
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
                Toast.makeText(ParticipantsLogin.this, "Please Check Your Internet Connection!", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e)
        {
            Log.e("-->",e.toString());
            Toast.makeText(mContext, "Something went Wrong!", Toast.LENGTH_SHORT).show();
        }
    }
    private void updateUI(FirebaseUser user)
    {
        if (user != null) {
            // User is signed in
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
            if (acct != null) {
                personName = acct.getDisplayName();
                personFirstName = acct.getGivenName();
                personLastName = acct.getFamilyName();
                personEmail = acct.getEmail();
                personId = acct.getId();
                personPhoto = acct.getPhotoUrl();

           /*     Toast.makeText(ParticipantsLogin.this, "" + personName + "\n" +
                        personFirstName + "\n" +
                        personLastName + "\n" +
                        personEmail + "\n", Toast.LENGTH_LONG).show();*/
                PreferenceUtil.setSignIn(ParticipantsLogin.this,true);
            } else {
                // No user is signed in
            }
            //Show Dialog
            android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(ParticipantsLogin.this);
            alertDialog.setTitle("One More Step... ");
            alertDialog.setMessage("Please Enter Your Registred Mobile");

            LayoutInflater layoutInflater = this.getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.custom_mobileno_dialog, null);

            edtMobileNo = (MaterialEditText) view.findViewById(R.id.edt_gm_MobileNo);

            alertDialog.setView(view);
            alertDialog.setIcon(R.drawable.ic_person);

            //Set Buttons
            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final String phone = "+91"+edtMobileNo.getText().toString();
                    final AlertDialog watingDialog = new SpotsDialog(ParticipantsLogin.this);
                    watingDialog.show();
                    watingDialog.setMessage("Please Wait");
                    watingDialog.setCancelable(false);

                    //Check if User Exist on Firebase if not then add it
                    table_participant.orderByKey().equalTo(phone)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.child(phone).exists())//if User not Exist
                                    {
                                        Intent intent = new Intent(ParticipantsLogin.this, RegistrationActivity.class);
                                        intent.putExtra("email", personEmail);
                                        intent.putExtra("firstname", personFirstName);
                                        intent.putExtra("lastname", personLastName);
                                        intent.putExtra("mobilno", phone);

                                        PreferenceUtil.setMobileNo(mContext,phone);
                                        PreferenceUtil.setEmailid(mContext,personEmail);
                                        PreferenceUtil.setLastname(mContext,personLastName);
                                        PreferenceUtil.setFirstname(mContext,personFirstName);
                                        PreferenceUtil.setPass(mContext,personFirstName);

                                        startActivity(intent);
                                        watingDialog.dismiss();
                                        finish();

                                    } else//if User Exist
                                    {
                                        table_participant.child(phone)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        ParticipantModel localUser = dataSnapshot.getValue(ParticipantModel.class);
                                                        startActivity(new Intent(ParticipantsLogin.this, Activity_DashBoard2.class));
                                                        util.currentParticipant = localUser;

                                                        PreferenceUtil.setMobileNo(mContext,phone);
                                                        PreferenceUtil.setEmailid(mContext,personEmail);
                                                        PreferenceUtil.setLastname(mContext,personLastName);
                                                        PreferenceUtil.setFirstname(mContext,personFirstName);
                                                        PreferenceUtil.setPass(mContext,personFirstName);

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
            });
            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();
        }
    }
    //Check User VAlidataions
    public boolean utilsCheck()
    {
        boolean cancel = false;
        View focusView = null;
        if (TextUtils.isEmpty(edtUserName.getText())){
            edtUserName.setError("Please enter first Name");
            focusView=edtUserName;
            cancel=true;
        }
        if (TextUtils.isEmpty(edtPassword.getText())){
            edtPassword.setError("Please enter Password");
            focusView=edtPassword;
            cancel=true;
        }
        return cancel;
    }

}
