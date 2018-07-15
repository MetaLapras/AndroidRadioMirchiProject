package in.co.ashclan.mirchithunder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import in.co.ashclan.mirchithunder.adapter.mSelfieAdapter;

public class FramesActivity extends AppCompatActivity {
    Context mContext;
    GridView mListView;
    Integer[] frames = {
            R.drawable.filter_1,
            R.drawable.filter_2,
            R.drawable.filter_3,
            R.drawable.filter_4,
            R.drawable.filter_5,
            R.drawable.filter_6,
            R.drawable.filter_7
    };
    mSelfieAdapter selfieAdapter;
    int selectFrame = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frames);
        init();
        selfieAdapter = new mSelfieAdapter(FramesActivity.this,frames);
        mListView.setAdapter(selfieAdapter);
        selfieAdapter.notifyDataSetChanged();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectFrame = i;
                Intent intent = new Intent(FramesActivity.this,MirchiSelfie3.class);
                intent.putExtra("position",selectFrame);
                startActivity(intent);
                Log.e("-->",selectFrame+"");
            }
        });
    }

    private void init() {
        mContext = FramesActivity.this;
        mListView = (GridView) findViewById(R.id.mSelfie_gridview);
    }
}
