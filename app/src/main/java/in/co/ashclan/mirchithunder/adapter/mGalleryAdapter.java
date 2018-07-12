package in.co.ashclan.mirchithunder.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import in.co.ashclan.mirchithunder.R;
import in.co.ashclan.mirchithunder.ViewHolders.mGalleryViewHolder;
import in.co.ashclan.mirchithunder.model.ImagesModel;
import in.co.ashclan.mirchithunder.utils.ItemClickListener;

public class mGalleryAdapter extends RecyclerView.Adapter<mGalleryViewHolder> {

    Context mContext;
    ArrayList<String> pictures;


    public mGalleryAdapter(Context mContext, ArrayList<String> pictures) {
        this.mContext = mContext;
        this.pictures = pictures;
    }

    @NonNull
    @Override
    public mGalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.gallery_single_item, parent, false);
        return new mGalleryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull mGalleryViewHolder holder, int position) {
        // holder.txtMenuName.setText(model.getName());
        Glide.with(mContext).load(pictures.get(position))
                .into(holder.Img_Menu);

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                      /*  Intent intent = new Intent(Home.this, FoodMenu.class);
                        intent.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(intent)*/;
                Toast.makeText(mContext,"this is clicked",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
