package in.co.ashclan.mirchithunder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.PeriodicSync;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.bumptech.glide.load.model.ResourceLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mindorks.paracamera.Camera;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import in.co.ashclan.mirchithunder.adapter.mGalleryAdapter;
import in.co.ashclan.mirchithunder.model.ImagesModel;
import in.co.ashclan.mirchithunder.model.ParticipantModel;
import in.co.ashclan.mirchithunder.utils.PreferenceUtil;
import in.co.ashclan.mirchithunder.utils.Utility;
import in.co.ashclan.mirchithunder.utils.util;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class MirchiSelfie extends AppCompatActivity implements View.OnClickListener {
    Context mContext;
    PhotoEditorView mPhotoEditorView;
    PhotoEditor mPhotoEditor;
    private int REQUEST_CAMERA = 52;
    public static final int REQUEST_PERMISSION = 200;
    FloatingActionButton fabCamera,fabSave;
    private Bitmap bitmapImage;
    private File destination;
    Uri saveUri;
    Uri selectedImage;
    private Uri imageUri;
    public static final int READ_WRITE_STORAGE = 52;
    RelativeLayout relativeLayout;
    ArrayList<String> arrayList;
    String mImageFileLocation = "";
    private String imageFilePath = "";

    private Uri mHighQualityImageUri = null;

    FirebaseDatabase database;
    DatabaseReference participantImages ;
    FirebaseStorage storage;
    StorageReference storageReference ;

    ImagesModel imagesModel;
    Bitmap icon;
    Uri uri;
    Integer[] frames = {
            R.drawable.filter_1,
            R.drawable.filter_2,
            R.drawable.filter_3,
            R.drawable.filter_4,
            R.drawable.filter_5,
            R.drawable.filter_6,
            R.drawable.filter_7
    };
    int selectFrame = 0;
    Camera camera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mirchi_selfie);
        inti();
        selectFrame = getIntent().getIntExtra("position",0);

      if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }

        camera = new Camera.Builder()
                .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
                .setTakePhotoRequestCode(1)
                .setDirectory("pics")
                .setName("ali_" + System.currentTimeMillis())
                .setImageFormat(Camera.IMAGE_JPEG)
                .setCompression(75)
                .setImageHeight(1000)// it will try to achieve this height as close as possible maintaining the aspect ratio;
                .build(this);
    }

    private void inti() {
        mContext = MirchiSelfie.this;
        mPhotoEditorView = (PhotoEditorView)findViewById(R.id.photoEditorView);
        fabCamera = (FloatingActionButton)findViewById(R.id.fab_camera);
        fabSave = (FloatingActionButton)findViewById(R.id.fab_camera_save);
        fabCamera.setOnClickListener(this);
        fabSave.setOnClickListener(this);

        relativeLayout = (RelativeLayout)findViewById(R.id.Relative_rootlayout);

        //InIt FireBase
        database            = FirebaseDatabase.getInstance();
        participantImages   = database.getReference("ParticipantImages"); //Linked to Participant table
        storage             = FirebaseStorage.getInstance();
        storageReference    = storage.getReference();

    }

    private void cameraIntent() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if(cameraIntent.resolveActivity(getPackageManager())!=null)
        {
            File PhotoFile = null;
            try
            {
                PhotoFile = createImageFile();
            }catch (IOException e)
            {
                e.printStackTrace();
                return;
            }
            Uri photoUri = FileProvider.getUriForFile(this,getPackageName()+".provider",PhotoFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
        }
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }

    Bitmap bitmap = null;

    private File createImageFile() throws IOException{

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imageFilePath = image.getAbsolutePath();

        return image;
    }
        @Override
    public void onClick(View view) {
            boolean result= Utility.checkPermission(mContext);

            if(view == fabCamera)
        {
            try {
                camera.takePicture();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if (view == fabSave)
        {
            saveSelfieImage();
        }
    }
    @SuppressLint("MissingPermission")
    private void saveSelfieImage() {
        if(requestPermissionStorage(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            String fileName=System.currentTimeMillis() + ".jpg";
            destination = new File(Environment.getExternalStorageDirectory(),
                    fileName);
            try {
                destination.createNewFile();
                mPhotoEditor.saveAsFile(destination.getAbsolutePath(), new PhotoEditor.OnSaveListener() {
                    @Override
                    public void onSuccess(@NonNull String imagePath) {
                        mPhotoEditorView.getSource().setImageURI(Uri.fromFile(new File(imagePath)));
                        saveUri = Uri.fromFile(new File(imagePath));
                        uploadImage(saveUri);
                    }
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });
            }catch (Exception e)
            {
                Log.e("-->",e.toString());
            }
        }
    }
    private void uploadImage(Uri saveUri) {
        if(saveUri!=null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading Image .... ");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();

            participantImages.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.e("-->img",dataSnapshot.toString());
                    if(dataSnapshot.child(PreferenceUtil.getMobileNo(mContext)).exists())
                    {
                        imagesModel = dataSnapshot.child(PreferenceUtil.getMobileNo(mContext)).getValue(ImagesModel.class);
                        Log.e("-->1234",imagesModel.toString());
                        util.CurrentimagesModel = imagesModel;
                        arrayList = imagesModel.getImages();
                        Log.e("-->s",arrayList.toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            String imageName = UUID.randomUUID().toString(); //set Image to an ID
            final StorageReference imageFolder = storageReference.child("images/"+imageName); // Create a folder in the Firebase with id reference
            // Add Image to the Folder at Firebase
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    //Download the refence image from the database
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // set value for new category if image upload and we can get download link
                            if(imagesModel!=null)
                            {
                                arrayList.add(uri.toString());
                                participantImages.child(PreferenceUtil.getMobileNo(mContext)).setValue(imagesModel);
                                finish();
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(mContext, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
    public Boolean requestPermissionStorage(String permission)
    {
        boolean isGranted = ContextCompat.checkSelfPermission(mContext,permission) == PackageManager.PERMISSION_GRANTED;
        if(!isGranted)
        {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{permission},
                    READ_WRITE_STORAGE
            );
        }
        return isGranted;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Camera.REQUEST_TAKE_PHOTO){
            Bitmap bitmap = camera.getCameraBitmap();
            if(bitmap != null) {
                mPhotoEditorView.getSource().setImageBitmap(bitmap);
                onCaptureImageResult();
            }else{
                Toast.makeText(this.getApplicationContext(),"Picture not taken!",Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Thanks for granting Permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onCaptureImageResult() {

        //mPhotoEditorView.getSource().setImageURI(Uri.parse(imageFilePath));
//        bitmapImage = (Bitmap) data.getExtras().get("data");
        //Toast.makeText(mContext,destination.getAbsolutePath(),Toast.LENGTH_LONG).show();
        //cropView.setUri(Uri.parse(destination.getAbsolutePath()));
    //    mPhotoEditorView.getSource().setImageURI(Uri.parse(imageFilePath));
        mPhotoEditor = new PhotoEditor.Builder(mContext,mPhotoEditorView).setPinchTextScalable(true).build();
        switch (selectFrame)
        {
            case 0 :
                 icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.filter_1);
                mPhotoEditor.addImage(icon);
                break;
            case 1 :
                 icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.filter_2);
                mPhotoEditor.addImage(icon);
                break;
            case 2 :
                icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.filter_3);
                mPhotoEditor.addImage(icon);
                break;
            case 3 :
                icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.filter_4);
                mPhotoEditor.addImage(icon);
                break;
            case 4 :
                icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.filter_5);
                mPhotoEditor.addImage(icon);
                break;
            case 5 :
                icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.filter_6);
                mPhotoEditor.addImage(icon);
                break;
            case 6 :
                icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.filter_7);
                mPhotoEditor.addImage(icon);
                break;
          /*  case 7 :
                icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.frame_9);
                mPhotoEditor.addImage(icon);
                break;
            case 8 :
                icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.frame_10);
                mPhotoEditor.addImage(icon);
                break;
            case 9 :
                icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.frame_11);
                mPhotoEditor.addImage(icon);
                break;
            case 10 :
                icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.frame_12);
                mPhotoEditor.addImage(icon);
                break;
            case 11 :
                icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.frame_13);
                mPhotoEditor.addImage(icon);
                break;
            case 12 :
                icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.frame_14);
                mPhotoEditor.addImage(icon);
                break;
            case 13 :
                icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.frame_15);
                mPhotoEditor.addImage(icon);
                break;
            case 14 :
                icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.frame_16);
                mPhotoEditor.addImage(icon);
                break;
            case 15 :
                icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.frame_17);
                mPhotoEditor.addImage(icon);
                break;
            case 16 :
                icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.frame_18);
                mPhotoEditor.addImage(icon);
                break;*/
        }

//        icon = BitmapFactory.decodeResource(getResources(),
//                R.drawable.back_img);
//        mPhotoEditor.addImage(icon);

        //        imageViewFingerPrint2.setImageBitmap(bitmapImage);
        //     memberDetails.setPhotoLocalPath(BitMapToString(bitmapImage));
        //setImagePath(destination.getAbsolutePath());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        camera.deleteImage();
    }
}
