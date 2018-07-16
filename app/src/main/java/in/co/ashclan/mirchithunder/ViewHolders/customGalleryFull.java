package in.co.ashclan.mirchithunder.ViewHolders;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.squareup.picasso.Picasso;

import in.co.ashclan.mirchithunder.DashBoard;
import in.co.ashclan.mirchithunder.ParticipantsLogin;
import in.co.ashclan.mirchithunder.R;



public class customGalleryFull extends AppCompatActivity {
    ImageView imgFull;
    FloatingActionButton fab_share,fab_post;
    ImageLoaderConfiguration loaderConfiguration;
    ImageLoader imageLoader = ImageLoader.getInstance();
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    String imgURL;
    String Hashtag;
    Context mContext;
    ProgressDialog mDialog;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.custom_gallery_full);
        mInit();

       // final String img = getIntent().getStringExtra(DashBoard.EXTRA_IMAGE);

      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            imgFull.setTransitionName(DashBoard.EXTRA_TRANSITION_IMAGE);
        GlideApp.with(this)
                .load(img)
                .into(imgFull);*/

        //Init Facebook
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .showImageOnLoading(R.drawable.ic_default)
                .showImageForEmptyUri(R.drawable.ic_default)
                .showImageOnFail(R.drawable.ic_default)
                .build();


        loaderConfiguration = new ImageLoaderConfiguration.Builder(customGalleryFull.this)
                    .defaultDisplayImageOptions(imageOptions).build();
            imageLoader.init(loaderConfiguration);
            imgURL= getIntent().getStringExtra(DashBoard.EXTRA_IMAGE);
            try {
                imageLoader.displayImage(imgURL, imgFull, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    //    imageLoader.displayImage("http://52.172.221.235:8983/uploads/" + defaultIcon, imageView);
                }
                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                }
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                }
                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                }
            });
        }catch (Exception e){
            imgFull.setImageResource(R.drawable.ic_default);
            e.printStackTrace();
        }


        fab_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                Uri screenshotUri = Uri.parse(img);
                try {
                    InputStream stream = getContentResolver().openInputStream(screenshotUri);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                sharingIntent.setType("image/jpeg");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                startActivity(Intent.createChooser(sharingIntent, "Share image using"));*/

                Drawable mDrawable = imgFull.getDrawable();
                Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap();

                String path = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "Image Description", null);
                Uri uri = Uri.parse(path);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(intent, "Share Image"));
            }
        });

        fab_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.show();
                shareOnFacebook();
            }
        });
    }

    private void mInit() {
        mContext = customGalleryFull.this;
        imgFull =findViewById(R.id.img_Full);
        fab_share = findViewById(R.id.fab_Share);
        fab_post = findViewById(R.id.fab_post);
        mDialog = new ProgressDialog(mContext);
    }

    private void shareOnFacebook() {
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(customGalleryFull.this,"Share Successful !",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancel() {
                Toast.makeText(customGalleryFull.this,"Share Cancel !",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(customGalleryFull.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        Picasso.with(this).load(imgURL).into(target);
        mDialog.dismiss();
    }


}
