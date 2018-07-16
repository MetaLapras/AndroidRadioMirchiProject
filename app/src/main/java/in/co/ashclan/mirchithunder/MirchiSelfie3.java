package in.co.ashclan.mirchithunder;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;
import in.co.ashclan.mirchithunder.model.ImagesModel;
import in.co.ashclan.mirchithunder.utils.PreferenceUtil;
import in.co.ashclan.mirchithunder.utils.RuntimePermissionUtil;
import in.co.ashclan.mirchithunder.utils.util;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class MirchiSelfie3 extends AppCompatActivity implements View.OnClickListener {
    Context mContext;

    boolean hasCameraPermission = false;
    private static final String cameraPerm = Manifest.permission.CAMERA;

    ImageLoaderConfiguration loaderConfiguration;
    ImageLoader imageLoader = ImageLoader.getInstance();
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    PhotoEditorView mPhotoEditorView;
    PhotoEditor mPhotoEditor;
    FloatingActionButton fabCamera, fabSave,fabpost;
    private Bitmap bitmapImage;
    private File destination;
    RelativeLayout relativeLayout;
    ImageView imageView;
    Uri saveUri;
    FirebaseDatabase database;
    DatabaseReference participantImages;
    FirebaseStorage storage;
    StorageReference storageReference;
    //************************
    SurfaceView mySurfaceView;
    QREader qrEader;
    TextView textView;
    //************************
    public static final int READ_WRITE_STORAGE = 52;
    Bitmap icon;
    // Create global camera reference in an activity or fragment
    Camera camera;
    int selectFrame = 0;
    ImagesModel imagesModel;
    ArrayList<String> arrayList;
    String imgURL;
    Uri uri;


    //**************************//
    //Create Target from Picasso
    com.squareup.picasso.Target target = new com.squareup.picasso.Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            //Create photo from bitmap
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();
            if(ShareDialog.canShow(SharePhotoContent.class))
            {
                SharePhotoContent content =  new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .setShareHashtag(new ShareHashtag.Builder().setHashtag("#RadioMirchi").build())
                        .build();
                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };
    //*****************************//

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mirchi_selfie);
        inti();
        selectFrame = getIntent().getIntExtra("position", 0);
        hasCameraPermission = RuntimePermissionUtil.checkPermissonGranted(this, cameraPerm);

        /***********************************/
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        //**********************************************//


        //chn******************************************************
        if (hasCameraPermission){
            setUpQREader();
        }else{
            RuntimePermissionUtil.requestPermission((Activity) mContext, cameraPerm, 100);
        }

///***************************************************************************
            // Build the camera
            camera = new Camera.Builder()
                    .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
                    .setTakePhotoRequestCode(1)
                    .setDirectory("pics")
                    .setName("ali_" + System.currentTimeMillis())
                    .setImageFormat(Camera.IMAGE_JPEG)
                    .setCompression(75)
                    .setImageHeight(1000)// it will try to achieve this height as close as possible maintaining the aspect ratio;
                    .build(this);

            fabCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        camera.takePicture();
                        imageView.setVisibility(View.INVISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
             fabSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            saveSelfieImage();
                       }
            });
             fabpost.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     saveFacebookImage();
                 }
             });

            // Call the camera takePicture method to open the existing camera
    }

    // Get the bitmap and image path onActivityResult of an activity or fragment
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Camera.REQUEST_TAKE_PHOTO) {
            bitmapImage = camera.getCameraBitmap();
            if (bitmapImage != null) {
                    onCaptureImageResult(bitmapImage, data);
            } else {
                Toast.makeText(this.getApplicationContext(), "Picture not taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void inti() {
        mContext = MirchiSelfie3.this;

        mySurfaceView = (SurfaceView)findViewById(R.id.camera_view);
        textView =(TextView) findViewById(R.id.code_info);

        mPhotoEditorView = (PhotoEditorView) findViewById(R.id.photoEditorView);
        mPhotoEditor = new PhotoEditor.Builder(mContext, mPhotoEditorView).setPinchTextScalable(true).build();
        fabCamera = (FloatingActionButton) findViewById(R.id.fab_camera);
        fabSave = (FloatingActionButton) findViewById(R.id.fab_camera_save);
        fabpost = (FloatingActionButton) findViewById(R.id.fab_post);
        fabCamera.setOnClickListener(this);
        fabSave.setOnClickListener(this);

        relativeLayout = (RelativeLayout) findViewById(R.id.Relative_rootlayout);

        //InIt FireBase
        database = FirebaseDatabase.getInstance();
        participantImages = database.getReference("ParticipantImages"); //Linked to Participant table
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        imageView = (ImageView)findViewById(R.id.pasistence_logo);
    }
    @Override
    public void onClick(View view) {
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        camera.deleteImage();
    }
    private void onCaptureImageResult(Bitmap bitmapImage, Intent data) {
        mPhotoEditorView.getSource().setImageBitmap(bitmapImage);
        //mPhotoEditorView.getSource().setImageURI(Uri.parse(imageFilePath));
//        bitmapImage = (Bitmap) data.getExtras().get("data");
        //Toast.makeText(mContext,destination.getAbsolutePath(),Toast.LENGTH_LONG).show();
        //cropView.setUri(Uri.parse(destination.getAbsolutePath()));
        //    mPhotoEditorView.getSource().setImageURI(Uri.parse(imageFilePath));
        mPhotoEditor.clearAllViews();
        switch (selectFrame) {
            case 0:
                icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.filter_1);
                mPhotoEditor.addImage(icon);
                break;
            case 1:
                icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.filter_2);
                mPhotoEditor.addImage(icon);
                break;
            case 2:
                icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.filter_3);
                mPhotoEditor.addImage(icon);
                break;
            case 3:
                icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.filter_4);
                mPhotoEditor.addImage(icon);
                break;
            case 4:
                icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.filter_5);
                mPhotoEditor.addImage(icon);
                break;
            case 5:
                icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.filter_6);
                mPhotoEditor.addImage(icon);
                break;
            case 6:
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
    private void saveSelfieImage() {
        if (requestPermissionStorage(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            String fileName = System.currentTimeMillis() + ".jpg";
            destination = new File(Environment.getExternalStorageDirectory(),
                    fileName);
            try {
                destination.createNewFile();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
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
    private void saveFacebookImage() {
        if (requestPermissionStorage(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            String fileName = System.currentTimeMillis() + ".jpg";
            destination = new File(Environment.getExternalStorageDirectory(),
                    fileName);
            try {
                destination.createNewFile();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mPhotoEditor.saveAsFile(destination.getAbsolutePath(), new PhotoEditor.OnSaveListener() {
                    @Override
                    public void onSuccess(@NonNull String imagePath) {
                        mPhotoEditorView.getSource().setImageURI(Uri.fromFile(new File(imagePath)));
                        saveUri = Uri.fromFile(new File(imagePath));
                        shareOnFacebook(saveUri.toString());
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
            progressDialog.setCancelable(false);
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
                                startActivity(new Intent(MirchiSelfie3.this,MyGallery.class));
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
    private void shareOnFacebook(String uri) {
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(mContext,"Share Successful !",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancel() {
                Toast.makeText(mContext,"Share Cancel !",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(mContext,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        Picasso.with(this).load(uri).into(target);
    }
    /*/***/
    public void setUpQREader(){
        qrEader = new QREader.Builder(mContext, mySurfaceView, new QRDataListener() {
            @Override
            public void onDetected(final String data) {
                Log.d("QREader", "Value : " + data);
                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        //textView.setText(data);
                        //**********************************
                        // Dialog box
                        /*******************************************/
                       // showQRVerificationDialog(data);
                    }
                });
            }
        }).facing(QREader.BACK_CAM)
                .enableAutofocus(true)
                .height(mySurfaceView.getHeight())
                .width(mySurfaceView.getWidth())
                .build();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (hasCameraPermission){
            qrEader.releaseAndCleanup();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (hasCameraPermission){
            qrEader.initAndStart(mySurfaceView);
        }
    }

}
