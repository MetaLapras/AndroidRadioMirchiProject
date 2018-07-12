package in.co.ashclan.mirchithunder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import in.co.ashclan.mirchithunder.adapter.mGalleryAdapter;
import in.co.ashclan.mirchithunder.model.ImagesModel;
import in.co.ashclan.mirchithunder.utils.util;
import io.paperdb.Paper;

public class MyGallery extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference participantImages ;

    GridView gridView;
    Context mContext;
    SwipeRefreshLayout refreshLayout;
    mGalleryAdapter adapter;
    String phoneNo;
    ArrayList<String>arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gallery);
        intit();
      //  imagesbyParticipant(phoneNo);
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
                    loadPictures();
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
                    loadPictures();
                }else
                {
                    Toast.makeText(getBaseContext(), "Check Your Internet Connection ! ", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
    }
    private void intit() {
        mContext = MyGallery.this;
        gridView = (GridView) findViewById(R.id.mgallery_grid_view);

        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(gridView.getContext(),
                R.anim.layout_fall_animation);
        gridView.setLayoutAnimation(controller);

        //init Fire base
        database = FirebaseDatabase.getInstance();
        participantImages = database.getReference("ParticipantImages");
    }

    private void loadPictures() {
        phoneNo = "+917378797289";
        participantImages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("-->123",dataSnapshot.toString());
                if(dataSnapshot.child(phoneNo).exists())
                {
                    ImagesModel imagesModel = dataSnapshot.child(phoneNo).getValue(ImagesModel.class);
                    Log.e("-->1234",imagesModel.toString());
                    util.CurrentimagesModel = imagesModel;

                    arrayList = imagesModel.getImages();
                    Log.e("-->s",arrayList.toString());
                    adapter = new mGalleryAdapter(mContext,arrayList);
                    gridView.setAdapter(adapter);
                    gridView.scheduleLayoutAnimation();
                    adapter.notifyDataSetChanged();
                    refreshLayout.setRefreshing(false);

                    for(int i =0;i<arrayList.size();i++)
                    {
                        Log.e("-->ss", arrayList.get(i));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }


}
