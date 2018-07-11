package in.co.ashclan.mirchithunder;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import in.co.ashclan.mirchithunder.DashBoard;
import in.co.ashclan.mirchithunder.R;

public class FullScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        ImageView imgFull=findViewById(R.id.imgFull);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            imgFull.setTransitionName(DashBoard.EXTRA_TRANSITION_IMAGE);
        GlideApp.with(this)
                .load(getIntent().getIntExtra(DashBoard.EXTRA_IMAGE,0))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        supportStartPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        supportStartPostponedEnterTransition();
                        return false;
                    }
                })
                .into(imgFull);
    }
}
