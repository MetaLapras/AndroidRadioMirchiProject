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
public class ParticipantsLogin extends AppCompatActivity
        implements View.OnClickListener,DatePickerDialog.OnDateSetListener{

    FButton btn_facebook,btn_Gmail;
    FirebaseDatabase database;
    DatabaseReference table_participant ;
    FirebaseStorage storage;
    Context mContext;

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

    //Alert Dialog View
    MaterialEditText edtFirstName,edtLastName,edtEmailId,edtMobileNo,edtDateofBirth;
    RadioGroup rdg_Gender;
    RadioButton rd_male,rd_female;
    Button btn_Select,btn_upload;

    //Dialog Datetime Picker
    Calendar calendar ;
    DatePickerDialog datePickerDialog ;
    int Year, Month, Day ;

    //Upload Images
    Uri saveuri;
    StorageReference storageReference ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants_login);
        init();
        btn_Gmail.setOnClickListener(this);
        btn_facebook.setOnClickListener(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    private void init() {
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
    private void startGmailLogin() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
                            /*Intent intent = new Intent(ParticipantsLogin.this,RegistrationActivity.class);
                            intent.putExtra("mobilno",userphone);
                            startActivity(intent);
                            watingDialog.dismiss();*/

                            //Check if User Exist on Firebase if not then add it
                            table_participant.orderByKey().equalTo(userphone)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(!dataSnapshot.child(userphone).exists())//if Usernot Exist
                                            {
                                                Intent intent = new Intent(ParticipantsLogin.this,RegistrationActivity.class);
                                                intent.putExtra("mobilno",userphone);
                                                startActivity(intent);
                                                watingDialog.dismiss();

                                            }else//if User Exist
                                            {
                                                table_participant.child(userphone)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                ParticipantModel localUser = dataSnapshot.getValue(ParticipantModel.class);
                                                                startActivity(new Intent(ParticipantsLogin.this, DashBoard.class));
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
            saveuri = data.getData();
            btn_Select.setText("Image Selected !");
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
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
                            startActivity(new Intent(ParticipantsLogin.this,RegistrationActivity.class));
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                           // Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                    private void updateUI(FirebaseUser user) {
                    }
                });
    }
    private void showFireBaseDialog() {
        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(ParticipantsLogin.this);
        alertDialog.setTitle("One More Step... ");
        alertDialog.setMessage("Please Fill all Information");

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.custom_register_dialog,null);

        edtFirstName    = (MaterialEditText) view.findViewById(R.id.edt_FirstName);
        edtLastName     = (MaterialEditText) view.findViewById(R.id.edt_LastName);
        edtMobileNo     = (MaterialEditText) view.findViewById(R.id.edt_MobileNo);
        edtEmailId      = (MaterialEditText) view.findViewById(R.id.edt_EmailId);
        edtDateofBirth  = (MaterialEditText)view.findViewById(R.id.edt_DateOfBirth);

        btn_Select      = (Button)view.findViewById(R.id.btnSelect);
        btn_upload      = (Button)view.findViewById(R.id.btnUpload);

        rd_male         = (RadioButton)view.findViewById(R.id.rd_male);
        rd_female       = (RadioButton)view.findViewById(R.id.rd_female);

        rdg_Gender = (RadioGroup)view.findViewById(R.id.rdg_group);

        alertDialog.setView(view);
        alertDialog.setIcon(R.drawable.ic_person);

        calendar = Calendar.getInstance();

        Year = calendar.get(Calendar.YEAR) ;
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);

        //Event for Material Edit text
        edtDateofBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                datePickerDialog = DatePickerDialog.newInstance((DatePickerDialog.OnDateSetListener) mContext, Year, Month, Day);
                datePickerDialog.setThemeDark(false);
                datePickerDialog.showYearPickerFirst(false);
                datePickerDialog.setAccentColor(Color.parseColor("#009688"));
                datePickerDialog.setTitle("Select Date From DatePickerDialog");
                datePickerDialog.show(getFragmentManager(), "DatePickerDialog");

            }
        });

        //Event for Buttons
        btn_Select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();//Let user Choose the Image from Gallery
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();// Upload all Data into the Firebase database
            }
        });

        //Set Buttons
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
               if(participantModel!=null)
                {
                    table_participant.push().setValue(participantModel);
                   // Snackbar.make(linearLayout, "Congratulations"+participantModel.getFirstname().toString()+" Registred successfully", Snackbar.LENGTH_SHORT).show();
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
    private void uploadImage() {
        if(saveuri!=null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading Image .... ");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();

            String imageName = UUID.randomUUID().toString(); //set Image to an ID
            final StorageReference imageFolder = storageReference.child("images/"+imageName); // Create a folder in the Firebase with id reference
            // Add Image to the Folder at Firebase
            imageFolder.putFile(saveuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    //Download the refence image from the database
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            // set value for new category if image upload and we can get download link
                            participantModel = new ParticipantModel();
                            participantModel.setFirstname(edtFirstName.getText().toString());
                            participantModel.setLastname(edtLastName.getText().toString());
                            participantModel.setMobile(edtMobileNo.getText().toString());
                            participantModel.setDob(edtDateofBirth.getText().toString());

                            rdg_Gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                    if(rd_male.isChecked())
                                        participantModel.setGender(rd_male.getText().toString());
                                    else
                                        participantModel.setGender(rd_female.getText().toString());
                                }
                            });
                            participantModel.setEmail(edtEmailId.getText().toString());
                            participantModel.setPassword("puid"+ edtMobileNo.getText().toString());
                            participantModel.setStatus("Active");
                            participantModel.setImage(uri.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(ParticipantsLogin.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploading "+ progress + "%");
                }
            });
        }
    }
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),util.PICK_IMAGE_REQUEST);
    }
    @Override
    public void onDateSet(DatePickerDialog view, int Year, int Month, int Day) {
        String date = "" + Day + "/" + Month + "/" + Year;
        edtDateofBirth.setText(date);
    }

    //User Login
    private void login(final String phone, final String pass) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("Participant");

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
                        ParticipantModel user = dataSnapshot.child(phone).getValue(ParticipantModel.class);
                        user.setMobile(phone);
                        if (user.getPassword().equals(pass)) {
                            //Toast.makeText(getApplicationContext(),"Successful",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(ParticipantsLogin.this, DashBoard.class));
                            util.currentParticipant = user;
                            finish();
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
            Toast.makeText(ParticipantsLogin.this, "Please Check Your Internet Connection!", Toast.LENGTH_SHORT).show();
        }

    }

}
