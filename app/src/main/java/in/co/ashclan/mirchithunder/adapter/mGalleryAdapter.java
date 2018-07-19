package in.co.ashclan.mirchithunder.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import in.co.ashclan.mirchithunder.R;
import in.co.ashclan.mirchithunder.utils.ItemClickListener;

public class mGalleryAdapter extends BaseAdapter implements View.OnClickListener,
View.OnCreateContextMenuListener{

    Context mContext;
    ArrayList<String> pictures;
    ImageLoaderConfiguration loaderConfiguration;
    ImageLoader imageLoader = ImageLoader.getInstance();
    private ItemClickListener itemClickListener;
    int position;

    public mGalleryAdapter(Context mContext, ArrayList<String> pictures) {
        this.mContext = mContext;
        this.pictures = pictures;
    }

    @Override
    public int getCount() {
        return pictures.size();
    }

    @Override
    public Object getItem(int i) {
        return pictures.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View vList;
        position = i ;
        if (view == null){
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vList = new View(mContext);
            vList = inflater.inflate(R.layout.gallery_single_item,null);
        }else {
            vList = (View)view;
        }
        ImageView singleImage = (ImageView) vList.findViewById(R.id.single_image);

        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .showImageOnLoading(R.drawable.ic_default)
                .showImageForEmptyUri(R.drawable.ic_default)
                .showImageOnFail(R.drawable.ic_default)
                .build();

        loaderConfiguration = new ImageLoaderConfiguration.Builder(mContext)
                .defaultDisplayImageOptions(imageOptions).build();
        imageLoader.init(loaderConfiguration);

        String imgURL= pictures.get(i).toString();
        try {
            imageLoader.displayImage(imgURL, singleImage, new ImageLoadingListener() {
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
            singleImage.setImageResource(R.drawable.ic_default);
            e.printStackTrace();
        }
        return vList;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,position,false);
    }
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select the Action");
        menu.add(0,1,position, "DELETE");
    }
}
