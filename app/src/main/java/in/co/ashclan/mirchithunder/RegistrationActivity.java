package in.co.ashclan.mirchithunder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import in.co.ashclan.mirchithunder.model.ImagesModel;
import in.co.ashclan.mirchithunder.model.ParticipantModel;
import in.co.ashclan.mirchithunder.utils.PreferenceUtil;
import in.co.ashclan.mirchithunder.utils.util;

public class RegistrationActivity extends AppCompatActivity
        implements
        View.OnClickListener,
        DatePickerDialog.OnDateSetListener{

    //Participant Pojo
    ParticipantModel participantModel;
    ImagesModel imagesModel;
    FirebaseDatabase database;
    DatabaseReference table_participant ;
    DatabaseReference participantImages;
    FirebaseStorage storage;
    Context mContext;

    //Alert Dialog View
    MaterialEditText edtFirstName,edtLastName,edtEmailId,edtMobileNo,edtDateofBirth,edtQRCodeId,edtRecipteId;
    RadioGroup rdg_Gender,rdg_group;
    RadioButton rd_male,rd_female,rd_pro,rd_fun;
    Button btn_Select,btn_upload;
    Spinner spn_PaymentType;

    //Dialog Datetime Picker
    Calendar calendar ;
    DatePickerDialog datePickerDialog ;
    int Year, Month, Day ;

    //Upload Images
    Uri saveuri;
    StorageReference storageReference ;
    CardView RootLayout;
    String fbNumber,gmFirstName,gmLastName,gmEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        init();
        edtDateofBirth.setOnClickListener(this);
        btn_Select.setOnClickListener(this);
        btn_upload.setOnClickListener(this);

        if(getIntent()!=null)
        {
                fbNumber =getIntent().getStringExtra("mobilno");
                gmEmail = getIntent().getStringExtra("email");
                gmFirstName = getIntent().getStringExtra("firstname");
                gmLastName = getIntent().getStringExtra("lastname");

                edtMobileNo.setText(fbNumber);
                edtEmailId.setText(gmEmail);
                edtFirstName.setText(gmFirstName);
                edtLastName.setText(gmLastName);
        }
    }
    private void init() {
        mContext = RegistrationActivity.this;

        RootLayout      = (CardView) findViewById(R.id.root_layout);
        edtFirstName    = (MaterialEditText) findViewById(R.id.edt_FirstName);
        edtLastName     = (MaterialEditText) findViewById(R.id.edt_LastName);
        edtMobileNo     = (MaterialEditText) findViewById(R.id.edt_MobileNo);
        edtEmailId      = (MaterialEditText) findViewById(R.id.edt_EmailId);
        edtDateofBirth  = (MaterialEditText) findViewById(R.id.edt_DateOfBirth);
        edtQRCodeId     = (MaterialEditText) findViewById(R.id.edt_QrCOdeID);
        edtRecipteId    = (MaterialEditText) findViewById(R.id.edt_RecipteId);

        //InIt FireBase
        database            = FirebaseDatabase.getInstance();
        table_participant   = database.getReference("Participant"); //Linked to Participant table
        participantImages   = database.getReference("ParticipantImages"); //Linked to Participant table
        storage             = FirebaseStorage.getInstance();
        storageReference    = storage.getReference();

        btn_Select      = (Button)findViewById(R.id.btnSelect);
        btn_upload      = (Button) findViewById(R.id.btnUpload);
        rd_male         = (RadioButton) findViewById(R.id.rd_male);
        rd_female       = (RadioButton) findViewById(R.id.rd_female);
        rd_fun          = (RadioButton) findViewById(R.id.rd_fun);
        rd_pro          = (RadioButton) findViewById(R.id.rd_pro);
        rdg_Gender      = (RadioGroup) findViewById(R.id.rdg_group);
        rdg_Gender      = (RadioGroup) findViewById(R.id.rdg_group);
        rdg_group       = (RadioGroup) findViewById(R.id.rdg_group_type);
        spn_PaymentType = (Spinner)findViewById(R.id.spn_PaymentType) ;

        calendar        = Calendar.getInstance();
        Year            = calendar.get(Calendar.YEAR) ;
        Month           = calendar.get(Calendar.MONTH);
        Day             = calendar.get(Calendar.DAY_OF_MONTH);

    }
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.edt_DateOfBirth :
                                datePickerDialog = DatePickerDialog.newInstance((DatePickerDialog.OnDateSetListener) mContext, Year, Month, Day);
                                datePickerDialog.setThemeDark(false);
                                datePickerDialog.showYearPickerFirst(false);
                                datePickerDialog.setAccentColor(Color.parseColor("#009688"));
                                datePickerDialog.setTitle("Select Date From DatePickerDialog");
                                datePickerDialog.show(getFragmentManager(), "DatePickerDialog");
                                break;
            case R.id.btnSelect :
                chooseImage();//Let user Choose the Image from Gallery
                break;
            case R.id.btnUpload:
                uploadImage();// Upload all Data into the Firebase database
                break;
        }

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
                            if (edtFirstName.getText().toString().equals(null) || edtFirstName.getText().toString().equals("")) {
                                edtFirstName.setError("Please Enter FirstName");
                            } else if (edtLastName.getText().toString().equals(null) || edtLastName.getText().toString().equals("")) {
                                edtLastName.setError("Please Enter LastName");
                            } else if (edtMobileNo.getText().toString().equals(null) || edtMobileNo.getText().toString().equals("")) {
                                edtMobileNo.setError("Please Enter Mobile");
                            } else if (edtDateofBirth.getText().toString().equals(null) || edtDateofBirth.getText().toString().equals("")) {
                                edtDateofBirth.setError("Please Enter Date of Birth");
                            } else if (edtEmailId.getText().toString().equals(null) || edtFirstName.getText().toString().equals("")) {
                                edtEmailId.setError("Please Enter Valid Email Address ");
                            } else if (edtMobileNo.getText().toString().equals(null) || edtMobileNo.getText().toString().equals("")) {
                                edtEmailId.setError("Please Enter Valid Mobile No Address ");
                            } else {


                                participantModel = new ParticipantModel();
                                participantModel.setFirstname(edtFirstName.getText().toString());
                                participantModel.setLastname(edtLastName.getText().toString());
                                participantModel.setMobile(edtMobileNo.getText().toString());
                                participantModel.setDob(edtDateofBirth.getText().toString());
                                if (rd_male.isChecked())
                                    participantModel.setGender(rd_male.getText().toString());
                                else
                                    participantModel.setGender(rd_female.getText().toString());
                                participantModel.setEmail(edtEmailId.getText().toString());
                                participantModel.setPassword(edtMobileNo.getText().toString());
                                participantModel.setStatus("Active");
                                if (rd_fun.isChecked())
                                    participantModel.setGender(rd_fun.getText().toString());
                                else
                                    participantModel.setGender(rd_pro.getText().toString());
                                participantModel.setPuid(edtQRCodeId.getText().toString());
                                participantModel.setReceiptid(edtRecipteId.getText().toString());
                                participantModel.setPaymenttype(spn_PaymentType.getSelectedItem().toString().trim());
                                participantModel.setImage(uri.toString());

                                imagesModel = new ImagesModel();
                                imagesModel.setMobile(edtMobileNo.getText().toString());
                                imagesModel.setBkid(edtRecipteId.getText().toString());
                                imagesModel.setPuid(edtQRCodeId.getText().toString());
                                ArrayList<String> strings = new ArrayList<>();
                                strings.add(uri.toString());
                                imagesModel.setImages(strings);


                                if (participantModel != null) {
                                    table_participant.child(edtMobileNo.getText().toString()).setValue(participantModel);
                                    participantImages.child(edtMobileNo.getText().toString()).setValue(imagesModel);
                                    //table_participant.push().setValue(participantModel);
                                    Snackbar.make(RootLayout, "Congratulations" + participantModel.getFirstname().toString() + " Registred successfully", Snackbar.LENGTH_SHORT).show();

                                    Intent intent = new Intent(RegistrationActivity.this, Activity_DashBoard2.class);
                                    PreferenceUtil.setMobileNo(mContext, edtMobileNo.getText().toString());
                                    PreferenceUtil.setPass(mContext, edtFirstName.getText().toString());
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }

                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(RegistrationActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), util.PICK_IMAGE_REQUEST);
    }
    @Override
    public void onDateSet(DatePickerDialog view, int Year, int Month, int Day) {
        String date = "" + Day + "/" + Month + "/" + Year;
        edtDateofBirth.setText(date);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == util.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null)
        {
            saveuri = data.getData();
            btn_Select.setText("Image Selected !");
        }
    }
}
