package in.co.ashclan.mirchithunder.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import in.co.ashclan.mirchithunder.GlideApp;
import in.co.ashclan.mirchithunder.R;

public class mSelfieAdapter extends BaseAdapter{
    private final Activity context;
    private final Integer[] imageId;

    public mSelfieAdapter(Activity context, Integer[] imageId) {
        this.context = context;
        this.imageId = imageId;
    }

    @Override
    public int getCount() {
        return imageId.length;
    }

    @Override
    public Object getItem(int i) {
        return imageId;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.single_frame_item, null, true);

        Log.e("-->arr",imageId[i]+"");
        ImageView imageView = (ImageView) rowView.findViewById(R.id.single_image);
//        Bitmap icon = BitmapFactory.decodeResource(Resources.getSystem(), imageId[i]);
//        imageView.setImageBitmap(icon);
        //imageView.setImageDrawable(Resources.getSystem().getDrawable(imageId[i]));
        //imageView.setImageResource(imageId.);

        GlideApp.with(context)
                .load(imageId[i])
                .into(imageView);


        return rowView;
    }
}
