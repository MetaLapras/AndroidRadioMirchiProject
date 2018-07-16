package in.co.ashclan.mirchithunder;

import android.database.Cursor;
import android.graphics.Camera;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

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
import in.co.ashclan.mirchithunder.adapter.mSelfieAdapter;
import in.co.ashclan.mirchithunder.model.ImagesModel;
import in.co.ashclan.mirchithunder.model.ParticipantModel;
import in.co.ashclan.mirchithunder.utils.PreferenceUtil;
import in.co.ashclan.mirchithunder.utils.util;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;


public class MirchiSelfi2 extends AppCompatActivity implements View.OnClickListener {
    Context mContext;
    PhotoEditorView mPhotoEditorView;
    PhotoEditor mPhotoEditor;
    private int REQUEST_CAMERA = 52, SELECT_FILE = 1, CROP_IMAGE = 2;

    FloatingActionButton fabCamera, fabSave;
    private Bitmap bitmapImage;
    private File destination;
    Uri saveUri;
    Uri selectedImage;
    private Uri imageUri;
    public static final int READ_WRITE_STORAGE = 52;
    RelativeLayout relativeLayout;
    ArrayList<String> arrayList;
    String mImageFileLocation = "";


    private Uri mHighQualityImageUri = null;

    FirebaseDatabase database;
    DatabaseReference participantImages;
    FirebaseStorage storage;
    StorageReference storageReference;
    ImagesModel imagesModel;

    public static final int REQUEST_PERMISSION = 200;
    int selectframe;
    Bitmap icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mirchi_selfie);
        inti();
        selectframe = getIntent().getIntExtra("position", 0);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }
    }

    private void inti() {
        mContext = MirchiSelfi2.this;
        mPhotoEditorView = (PhotoEditorView) findViewById(R.id.photoEditorView);
        fabCamera = (FloatingActionButton) findViewById(R.id.fab_camera);
        fabSave = (FloatingActionButton) findViewById(R.id.fab_camera_save);
        fabCamera.setOnClickListener(this);
        fabSave.setOnClickListener(this);

        relativeLayout = (RelativeLayout) findViewById(R.id.Relative_rootlayout);

        //InIt FireBase
        database = FirebaseDatabase.getInstance();
        participantImages = database.getReference("ParticipantImages"); //Linked to Participant table
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

    }

    private void cameraIntent() {

    /*
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        File photofile = null;
        try{
            photofile = createImageFile();

        }catch (IOException e) {
            e.printStackTrace();
            Log.e("-->",e.toString());
        }catch(Exception e){
            e.printStackTrace();
            Log.e("-->",e.toString());
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photofile));
        startActivityForResult(intent,REQUEST_CAMERA);
*/
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }

    @Override
    public void onClick(View view) {
        if (view == fabCamera) {
            //galleryIntent();
            try{
                cameraIntent();
            }catch (Exception e)
            {
                e.printStackTrace();
                Toast.makeText(mContext, "Please Provide Manual permission! Settings -> Permission -> Camera -> MirchiLive",Toast.LENGTH_LONG).show();
            }

        }
        if (view == fabSave) {
            saveSelfieImage();
        }
    }

    //  @SuppressLint("MissingPermission")
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
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CAMERA) {

//                Uri uri = data.getData();
//                bitmapImage = (Bitmap) data.getExtras().get("data");
//                mPhotoEditorView.getSource().setImageBitmap(bitmapImage);
                onCaptureImageResult(data);
                //imageView.setImageBitmap(photo);
       //     caputerImage(data);
        }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void caputerImage(Intent data) {
//        Bundle extras = data.getExtras();
//        Bitmap bitmap = (Bitmap)extras.get("data");
//        mPhotoEditorView.getSource().setImageBitmap(bitmap);
        Bitmap mBitmap = BitmapFactory.decodeFile(mImageFileLocation);
        mPhotoEditorView.getSource().setImageBitmap(mBitmap);
    }
    public File createImageFile() throws IOException
    {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String ImageFileName = "Image_"+timestamp+"_" ;
        File storageDictionary = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(ImageFileName,".jpg",storageDictionary);
        mImageFileLocation = image.getAbsolutePath();

        return image;

    }
    private void onCaptureImageResult(Intent data) {
        bitmapImage = (Bitmap) data.getExtras().get("data");
        //Toast.makeText(mContext,destination.getAbsolutePath(),Toast.LENGTH_LONG).show();
        //cropView.setUri(Uri.parse(destination.getAbsolutePath()));
        mPhotoEditorView.getSource().setImageBitmap(bitmapImage);
        mPhotoEditor = new PhotoEditor.Builder(mContext,mPhotoEditorView).setPinchTextScalable(true).build();
        icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.demo_back);
        mPhotoEditor.addImage(icon);

        switch (selectframe)
        {
            case 0 :
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
            case 3 :
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
        }

        //        imageViewFingerPrint2.setImageBitmap(bitmapImage);
        //     memberDetails.setPhotoLocalPath(BitMapToString(bitmapImage));
        //setImagePath(destination.getAbsolutePath());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }


    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        bitmapImage=null;
        String path ="";
        if (data != null) {
            try {
                bitmapImage = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());

                Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
                cursor.moveToFirst();
                String document_id = cursor.getString(0);
                document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
                cursor.close();

                cursor = getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);

                cursor.moveToFirst();
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                cursor.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
       // Toast.makeText(mContext,path,Toast.LENGTH_LONG).show();
//        bitmapImage = (Bitmap) data.getExtras().get("data");
        //Toast.makeText(mContext,destination.getAbsolutePath(),Toast.LENGTH_LONG).show();
        //cropView.setUri(Uri.parse(destination.getAbsolutePath()));
        mPhotoEditorView.getSource().setImageBitmap(bitmapImage);
        mPhotoEditor = new PhotoEditor.Builder(mContext,mPhotoEditorView).setPinchTextScalable(true).build();
//        icon = BitmapFactory.decodeResource(getResources(),
//                R.drawable.back_img);
//        mPhotoEditor.addImage(icon);

        switch (selectframe)
        {
            case 0 :
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
            case 3 :
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
        }
    }


}

