package in.co.ashclan.mirchithunder.ViewHolders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import in.co.ashclan.mirchithunder.R;
import in.co.ashclan.mirchithunder.utils.ItemClickListener;

public class mGalleryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

    public TextView txtMenuName;
    public ImageView Img_Menu;

    private ItemClickListener itemClickListener;

    public mGalleryViewHolder(@NonNull View itemView) {
        super(itemView);

        Img_Menu = (ImageView)itemView.findViewById(R.id.menu_image);
        txtMenuName = (TextView)itemView.findViewById(R.id.txtMenu_name);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}
