package in.co.ashclan.mirchithunder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.accountkit.AccountKit;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import in.co.ashclan.mirchithunder.utils.PreferenceUtil;
import io.github.kobakei.materialfabspeeddial.FabSpeedDial;
import io.github.kobakei.materialfabspeeddial.FabSpeedDialMenu;
import io.paperdb.Paper;

public class Activity_DashBoard2 extends AppCompatActivity{

    Context mContext;
    CardView cardView_Gallery,cardView_Selfie,cardView_Achivements,cardView_logout;
    GoogleSignInClient mGoogleSignInClient;
    FabSpeedDial fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board2);
        init();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        fab = (FabSpeedDial)findViewById(R.id.fab);
        FabSpeedDialMenu menu = new FabSpeedDialMenu(this);
        menu.add("QRScan").setIcon(R.drawable.ic_fullscreen);
        menu.add("Support").setIcon(R.drawable.ic_headset);
        fab.setMenu(menu);

        fab.addOnStateChangeListener(new FabSpeedDial.OnStateChangeListener() {
            @Override
            public void onStateChange(boolean open) {
                // do something
            }
        });

        fab.addOnMenuItemClickListener(new FabSpeedDial.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(FloatingActionButton fab, TextView textView, int itemId) {
                // do somethin
                Log.e("-->Tag",itemId+"");
                if(itemId==1){
                    startActivity(new Intent(mContext,QRCodeReaderActivity.class));
                }else if(itemId == 2)
                {
                    Toast.makeText(mContext, "pasistence@mirchilive.app", Toast.LENGTH_LONG).show();
                }
            }
        });
        cardView_Gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext,MyGallery.class));
            }
        });
        cardView_Achivements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext,Achivements.class));
            }
        });
        cardView_Selfie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext,FramesActivity.class));
            }
        });
        cardView_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signoutFacebook();
                signOutRegular();
                signOutGmail();
                //Paper.book().destroy();
            }
        });

    }
    private void init() {
        mContext = Activity_DashBoard2.this;
        //Cardviews
        cardView_Gallery = (CardView)findViewById(R.id.card_Gallery_1);
        cardView_Selfie = (CardView)findViewById(R.id.card_selfie_1);
        cardView_Achivements = (CardView)findViewById(R.id.card_achivements_1);
        cardView_logout = (CardView)findViewById(R.id.card_logout_1);
        //   txtUserName = (TextView)findViewById(R.id.txt_user_name);
    }
    private void signoutFacebook() {
        //FaceBook LogOut
        AccountKit.logOut();
        finish();
    }
    private void signOutRegular() {

        Intent signin = new Intent(Activity_DashBoard2.this,ParticipantsLogin.class);
        signin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(signin);
        //  Toast.makeText(DashBoard.this, "Logout", Toast.LENGTH_SHORT).show();
        finish();
    }
    private void signOutGmail() {
        //Google Logout
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(Activity_DashBoard2.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        PreferenceUtil.setSignIn(mContext,false);
                    }
                });
        finish();
    }

}
