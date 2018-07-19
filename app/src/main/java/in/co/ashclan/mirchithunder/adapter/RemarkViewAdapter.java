package in.co.ashclan.mirchithunder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

import in.co.ashclan.mirchithunder.R;
import in.co.ashclan.mirchithunder.model.CheckPointModel;
import in.co.ashclan.mirchithunder.utils.ItemClickListener;

public class RemarkViewAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<CheckPointModel> remarks = new ArrayList<>();
    ImageLoaderConfiguration loaderConfiguration;
    ImageLoader imageLoader = ImageLoader.getInstance();
    private ItemClickListener itemClickListener;
    int position;
    TextView txt_puid,txt_time;

    CheckPointModel checkPointModel ;

    public RemarkViewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public RemarkViewAdapter() {
    }

    @Override
    public int getCount() {
        return remarks.size();
    }

    @Override
    public Object getItem(int i) {
        return remarks.get(i);
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
            vList = inflater.inflate(R.layout.custom_remark_view,null);
        }else {
            vList = (View)view;
        }
     return view;
    }
}
