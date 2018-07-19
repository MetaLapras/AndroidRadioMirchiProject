package in.co.ashclan.mirchithunder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import in.co.ashclan.mirchithunder.model.ParticipantModel;
import in.co.ashclan.mirchithunder.utils.PreferenceUtil;
import in.co.ashclan.mirchithunder.utils.util;
import me.anwarshahriar.calligrapher.Calligrapher;

public class UserProfile extends AppCompatActivity implements View.OnClickListener {
    Context mContext;
    CircleImageView imgProfile;
    ImageView imgEdit;
    TextView txtName,txtUserType,txtemailid,txtgender,txtMobileNo,txtPaymentType,txtReceiptId;
    String firtsName,lastName,Status;
    FirebaseDatabase database;
    DatabaseReference table_participant ;
    FirebaseStorage storage;
    StorageReference storageReference ;
    String phone ;
    ImageLoaderConfiguration loaderConfiguration;
    ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mInit();
        onLoad();
        imgEdit.setOnClickListener(this);
    }

    private void onLoad() {
        try
        {
            phone = PreferenceUtil.getMobileNo(mContext);
            if (util.isConnectedToInterNet(getBaseContext())) {

                final ProgressDialog mDialog = new ProgressDialog(UserProfile.this);
                mDialog.setMessage("Please Wait.....");
                mDialog.setCancelable(false);
                mDialog.show();

                table_participant.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Check if User doesnt exist in database
                        if (dataSnapshot.child(phone).exists()) {
                            mDialog.dismiss();
                            //get User Values
                            Log.d("-->123",dataSnapshot.toString());
                            ParticipantModel user = dataSnapshot.child(phone).getValue(ParticipantModel.class);
                            Log.d("PoJo-->",user.toString());

                            txtName.setText(user.getFirstname().toString()+" "+user.getLastname().toString());
                            txtemailid.setText(user.getEmail().toString());
                            txtUserType.setText(user.getcat().toString());
                            txtgender.setText(user.getGender().toString());
                            txtMobileNo.setText(user.getMobile().toString());
                            txtPaymentType.setText(user.getPaymenttype().toString());
                            txtReceiptId.setText(user.getReceiptid().toString());

                            if ( null != user.getImage()) {
                                //    "http://52.172.221.235:8983/uploads/"
                               // String imgURL = PreferenceUtils.getUrlUploadImage(context) + memberDetails.getPhotoURL();
                                String imgURL = user.getImage().toString();
                                try {
                                    imageLoader.displayImage(imgURL, imgProfile, new ImageLoadingListener() {
                                        @Override
                                        public void onLoadingStarted(String imageUri, View view) {
                                            //    imageLoader.displayImage("http://52.172.221.235:8983/uploads/" + defaultIcon, imageView);
                                        }
                                        @Override
                                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                            // profileImageView.setImageBitmap();
                                            imgProfile.setImageDrawable(getResources().getDrawable(R.drawable.man));
                                        }
                                        @Override
                                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                            Log.e("--->", imageUri);
                                            Log.e("--->", loadedImage.toString());
                                        }

                                        @Override
                                        public void onLoadingCancelled(String imageUri, View view) {

                                        }
                                    });
                                }catch (Exception ex){
                                    imgProfile.setImageResource(R.drawable.man);
                                    ex.printStackTrace();
                                }
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
                Toast.makeText(mContext, "Please Check Your Internet Connection!", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e)
        {
            Log.e("-->",e.toString());
            Toast.makeText(mContext, "Something went Wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    private void mInit() {
        mContext = UserProfile.this;
        txtName = (TextView)findViewById(R.id.txt_name);
        txtemailid = (TextView)findViewById(R.id.txt_email_id);
        txtUserType = (TextView)findViewById(R.id.txt_user_type);
        txtgender = (TextView)findViewById(R.id.txt_gender);
        txtMobileNo = (TextView)findViewById(R.id.txt_mobile_no);
        txtPaymentType = (TextView)findViewById(R.id.txt_payment_type);
        txtReceiptId = (TextView)findViewById(R.id.txt_receipt_id);

        imgEdit = (ImageView) findViewById(R.id.edit);

        //InIt FireBase
        database = FirebaseDatabase.getInstance();
        table_participant = database.getReference("Participant"); //Linked to Participant table
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        imgProfile = (CircleImageView)findViewById(R.id.profile);

//        Calligrapher calligrapher = new Calligrapher(this);
//        calligrapher.setFont(this, "calibri.ttf", true);

        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .showImageOnLoading(R.drawable.ic_person)
                .showImageForEmptyUri(R.drawable.ic_person)
                .showImageOnFail(R.drawable.ic_person)
                .build();

        loaderConfiguration = new ImageLoaderConfiguration.Builder(mContext)
                .defaultDisplayImageOptions(imageOptions).build();
        imageLoader.init(loaderConfiguration);
    }

    @Override
    public void onClick(View view) {
        if(view == imgEdit)
        {
            showChangePwdDialog();
        }

    }
    private void showChangePwdDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Change Password");
        alertDialog.setMessage("Please Fill all Information");

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View changePwdLayout = layoutInflater.inflate(R.layout.change_password_layout,null);

        final MaterialEditText edtOldPwd,edtNewPwd,edtConfPwd;
        edtOldPwd = (MaterialEditText)changePwdLayout.findViewById(R.id.edt_old_password);
        edtNewPwd = (MaterialEditText)changePwdLayout.findViewById(R.id.edt_new_password);
        edtConfPwd = (MaterialEditText)changePwdLayout.findViewById(R.id.edt_confirm_password);


        alertDialog.setView(changePwdLayout);


        //Set Buttons
        alertDialog.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final android.app.AlertDialog waiting_dialog = new SpotsDialog(mContext);
                waiting_dialog.show();
                //check old password
                if(edtOldPwd.getText().toString().equals(PreferenceUtil.getPass(mContext)))
                {
                    //check new password and new password
                    if(edtNewPwd.getText().toString().equals(edtConfPwd.getText().toString())){
                        Map<String,Object> passwordChange = new HashMap<>();
                        passwordChange.put("password",edtNewPwd.getText().toString());

                        //Update
                        table_participant.child(PreferenceUtil.getMobileNo(mContext)).updateChildren(passwordChange)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        waiting_dialog.dismiss();
                                        Toast.makeText(mContext, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(mContext, "New Password Doesn't Match", Toast.LENGTH_SHORT).show();
                    }
                }else
                {
                    waiting_dialog.dismiss();
                    Toast.makeText(mContext, "Worng Pasword Inputed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

}
