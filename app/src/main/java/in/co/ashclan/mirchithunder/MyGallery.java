package in.co.ashclan.mirchithunder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import in.co.ashclan.mirchithunder.ViewHolders.mGalleryViewHolder;
import in.co.ashclan.mirchithunder.model.ImagesModel;
import in.co.ashclan.mirchithunder.utils.ItemClickListener;
import in.co.ashclan.mirchithunder.utils.util;
import io.paperdb.Paper;

public class MyGallery extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference participantImages ;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager ;
    Context mContext;
    SwipeRefreshLayout refreshLayout;
    FirebaseRecyclerAdapter<ImagesModel,mGalleryViewHolder> adapter;
    String phoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gallery);
        intit();
      //  imagesbyParticipant(phoneNo);
        imagesbyParticipant("+919552913501");

        //Init
        Paper.init(this);
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //to Load menu from Firebase
                if(util.isConnectedToInterNet(getBaseContext())) {
                    loadMenu();
                }else
                {
                    Toast.makeText(getBaseContext(), "Check Your Internet Connection ! ", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        //Default Load
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                //to Load menu from Firebase
                if(util.isConnectedToInterNet(getBaseContext())) {
                    loadMenu();
                }else
                {
                    Toast.makeText(getBaseContext(), "Check Your Internet Connection ! ", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
    }

    private void imagesbyParticipant(String phoneNo) {

        final Query byParticipantImages = participantImages.orderByKey().equalTo(phoneNo);
        FirebaseRecyclerOptions<ImagesModel> options = new FirebaseRecyclerOptions.Builder<ImagesModel>()
                .setQuery(byParticipantImages,ImagesModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<ImagesModel, mGalleryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull mGalleryViewHolder holder, int position, @NonNull ImagesModel model) {

               // holder.txtMenuName.setText(model.getName());
                Glide.with(getBaseContext()).load(model.getImage())
                        .into(holder.Img_Menu);

                final ImagesModel clickitem = model;

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
            @NonNull
            @Override
            public mGalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.gallery_single_item,parent,false);
                return new mGalleryViewHolder(itemView);

            }
        };
    }

    private void intit() {
        mContext = MyGallery.this;
        recyclerView = (RecyclerView)findViewById(R.id.mGallery_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(),
                R.anim.layout_fall_animation);
        recyclerView.setLayoutAnimation(controller);

        //init Fire base
        database = FirebaseDatabase.getInstance();
        participantImages = database.getReference("ParticipantImages");
    }

    private void loadMenu() {
        adapter.notifyDataSetChanged();
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        recyclerView.scheduleLayoutAnimation();
        refreshLayout.setRefreshing(false);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null)
            adapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


}
