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
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
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
    CircleImageView img_btnSelect;

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
    TextView txt_payment,txt_gender,txt_booking;

    Boolean isImage = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        init();
        edtDateofBirth.setOnClickListener(this);
        btn_Select.setOnClickListener(this);
        btn_upload.setOnClickListener(this);
        img_btnSelect.setOnClickListener(this);

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
        img_btnSelect   = (CircleImageView) findViewById(R.id.img_btnSelect) ;

        calendar        = Calendar.getInstance();
        Year            = calendar.get(Calendar.YEAR) ;
        Month           = calendar.get(Calendar.MONTH);
        Day             = calendar.get(Calendar.DAY_OF_MONTH);
        txt_payment     = (TextView)findViewById(R.id.textView_payment);
        txt_gender      = (TextView)findViewById(R.id.textView_gender);
        txt_booking     = (TextView)findViewById(R.id.textView_booking);
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
                    if(utilsCheck())
                    {
                      uploadImage();// Upload all Data into the Firebase database
                    }
                break;
            case R.id.img_btnSelect:
                chooseImage();//Let user Choose the Image from Gallery
                break;
        }

    }
    private void uploadImage() {
        if(saveuri!=null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading Image .... ");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
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
                            uploadDatatoFireBase(uri);
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
                    long progress = (100 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Registering "+ progress + "%");
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
            Picasso.with(mContext).load(saveuri).into(img_btnSelect);
            isImage = true;
            btn_Select.setText("Image Selected !");
        }
    }
    private void uploadDatatoFireBase(Uri uri) {
        //Upload all data to Firebase
        // set value for new category if image upload and we can get download link
       /* if (edtRecipteId.getText().length()<=0){
            edtRecipteId.setError("Please enter Receipt Id");
            return;
        }else if (edtFirstName.getText().length()<=0){
            edtFirstName.setError("Please enter first Name");
            return;
        }
        else if (TextUtils.isEmpty(edtLastName.getText())){
            edtLastName.setError("Please enter first Name");
             return;
        }
        else if (TextUtils.isEmpty(edtDateofBirth.getText())&&!isValidDate(edtDateofBirth.getText().toString())){
            edtDateofBirth.setError("Please enter in Dateformat");
            return;
        }
        else if (TextUtils.isEmpty(edtEmailId.getText())&&!isEmailValid(edtEmailId.getText().toString())){
            edtEmailId.setError("Please enter in Dateformat");
             return;
        }
        else if (TextUtils.isEmpty(edtMobileNo.getText())||edtMobileNo.length()>13){
            edtRecipteId.setError("Please enter Valid Mobile No or prefix +91");
            return;
        }
        //Spinner Validation
        else if(spn_PaymentType.getSelectedItemPosition()==0){
            txt_payment.setError("Please Select Gender");
            return;
        }
        //Radio Button Validation
        else if(rdg_group.getCheckedRadioButtonId() == 0 )
        {
            Toast.makeText(mContext, "Please Select Category", Toast.LENGTH_SHORT).show();
             return;
        }
        else if(rdg_Gender.getCheckedRadioButtonId() == 0 )
        {
            Toast.makeText(mContext, "Please Select Gender", Toast.LENGTH_SHORT).show();
            return;
        }
    else
        {*/
            participantModel = new ParticipantModel();

            participantModel.setReceiptid(edtRecipteId.getText().toString());
            participantModel.setFirstname(edtFirstName.getText().toString());
            participantModel.setLastname(edtLastName.getText().toString());
            participantModel.setDob(edtDateofBirth.getText().toString());
            participantModel.setEmail(edtEmailId.getText().toString());
            participantModel.setMobile(edtMobileNo.getText().toString());

            if (rd_male.isChecked()) {
                participantModel.setGender(rd_male.getText().toString());
                PreferenceUtil.setGender(mContext, rd_male.getText().toString());
            } else {
                participantModel.setGender(rd_female.getText().toString());
                PreferenceUtil.setGender(mContext, rd_female.getText().toString());
            }
            if (rd_fun.isChecked()) {
                participantModel.setTickettype(rd_fun.getText().toString());
                PreferenceUtil.setTickettype(mContext, rd_fun.getText().toString());
            }   else {
                participantModel.setTickettype(rd_pro.getText().toString());
                PreferenceUtil.setTickettype(mContext, rd_pro.getText().toString());
            }
                participantModel.setPassword(edtFirstName.getText().toString());
                participantModel.setStatus("deactivate");
                participantModel.setPuid("XXXX");
                participantModel.setPaymenttype(spn_PaymentType.getSelectedItem().toString().trim());
                participantModel.setImage(uri.toString());

                imagesModel = new ImagesModel();
                imagesModel.setMobile(edtMobileNo.getText().toString());
                imagesModel.setBkid(edtRecipteId.getText().toString());
                imagesModel.setPuid("XXXX");

                ArrayList<String> strings = new ArrayList<>();
                strings.add(uri.toString());
                imagesModel.setImages(strings);

                if (participantModel != null)
                {
                    table_participant.child(edtMobileNo.getText().toString()).setValue(participantModel);
                    participantImages.child(edtMobileNo.getText().toString()).setValue(imagesModel);

                    //table_participant.push().setValue(participantModel);
                    //Snackbar.make(RootLayout, "Congratulations" + participantModel.getFirstname().toString() + " Registred successfully", Snackbar.LENGTH_SHORT).show();

                    PreferenceUtil.setReceiptid(mContext,edtRecipteId.getText().toString());
                    PreferenceUtil.setFirstname(mContext,edtFirstName.getText().toString());
                    PreferenceUtil.setLastname(mContext,edtLastName.getText().toString());
                    PreferenceUtil.setDob(mContext,edtDateofBirth.getText().toString());
                    PreferenceUtil.setEmailid(mContext,edtEmailId.getText().toString());
                    PreferenceUtil.setMobileNo(mContext,edtMobileNo.getText().toString());
                    PreferenceUtil.setPaymenttype(mContext,spn_PaymentType.getSelectedItem().toString().trim());

                    Toast toast = Toast.makeText(mContext, "Congratulations" + participantModel.getFirstname().toString() + " Registred successfully", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    Intent intent = new Intent(RegistrationActivity.this, Activity_DashBoard2.class);
                    startActivity(intent);
                    finish();

            }else
                {
                    Toast.makeText(mContext, "Somthing went Wrong! Please Try After some time", Toast.LENGTH_SHORT).show();
                }
        //}
    }
    //Check User VAlidataions
    public boolean utilsCheck(){

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(edtRecipteId.getText())){
            edtRecipteId.setError("Please enter Receipt Id");
            focusView=edtRecipteId;
            cancel=true;
        }
        if (TextUtils.isEmpty(edtFirstName.getText())){
            edtFirstName.setError("Please enter first Name");
            focusView=edtFirstName;
            cancel=true;
        }
        if (TextUtils.isEmpty(edtLastName.getText())){
            edtLastName.setError("Please enter first Name");
            focusView=edtLastName;
            cancel=true;
        }
        if (TextUtils.isEmpty(edtDateofBirth.getText())&&!isValidDate(edtDateofBirth.getText().toString())){
            edtDateofBirth.setError("Please enter in Dateformat");
            focusView=edtDateofBirth;
            cancel=true;
        }
        isValidEmail(edtEmailId.getText().toString());
        //isValidMobile(edtMobileNo.getText().toString());

        //Spinner Validation
        if(spn_PaymentType.getSelectedItemPosition()==0){
            txt_payment.setError("Please Select Gender");
            focusView=spn_PaymentType;
            cancel=true;
        }
        //Radio Button Validation
        if(rdg_group.getCheckedRadioButtonId() == 0 )
        {
            txt_booking.setError("Please Select Category");
            //Toast.makeText(mContext, "Please Select Category", Toast.LENGTH_SHORT).show();
            focusView=rdg_group;
            cancel=true;
        }

        if(rdg_Gender.getCheckedRadioButtonId() == 0 )
        {
            txt_gender.setError("Please Select Gender");
            //Toast.makeText(mContext, "Please Select Gender", Toast.LENGTH_SHORT).show();
            focusView=rdg_Gender;
            cancel=true;
        }
        if(isImage)
        {
            Toast toast = Toast.makeText(mContext,"Please Select profile Pic",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        return cancel;
    }
    //Date Validataion
    private boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }
    //Email Validataion
    private boolean isValidEmail(String e){
        boolean check;
        Pattern p;
        Matcher m;

        String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        p = Pattern.compile(EMAIL_STRING);

        m = p.matcher(e);
        check = m.matches();

        if(!check) {
            edtEmailId.setError("Not Valid Email");
        }
        return check;
//        return Patterns.EMAIL_ADDRESS.matcher(e).matches();
    }
    //Mobile No Validataion
    private boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }
}
