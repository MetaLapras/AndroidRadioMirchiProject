package in.co.ashclan.mirchithunder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
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
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;
import io.paperdb.Paper;
import me.anwarshahriar.calligrapher.Calligrapher;

public class Activity_DashBoard2 extends AppCompatActivity{

    Context mContext;
    CardView cardView_Gallery,cardView_Selfie,cardView_Achivements,cardView_logout;
    GoogleSignInClient mGoogleSignInClient;
    io.github.yavski.fabspeeddial.FabSpeedDial fabSpeedDial;


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

       /* fab = (FabSpeedDial)findViewById(R.id.fab);
        FabSpeedDialMenu menu = new FabSpeedDialMenu(this);
        menu.add("QRScan").setIcon(R.drawable.ic_fullscreen);
        menu.add("Support").setIcon(R.drawable.ic_headset);
        menu.add("Profile Image").setIcon(R.drawable.ic_profile);
        fab.setMenu(menu);

        fab.addOnStateChangeListener(new FabSpeedDial.OnStateChangeListener() {
            @Override
            public void onStateChange(boolean open) {
            }
        });

        fab.addOnMenuItemClickListener(new FabSpeedDial.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(FloatingActionButton fab, TextView textView, int itemId) {
                // do somethi
                Log.e("-->Tag",itemId+"");
                if(itemId == 1){
                    startActivity(new Intent(mContext,QRCodeReaderActivity.class));
                }else if(itemId == 2)
                {
                    Toast.makeText(mContext, "pasistence@mirchilive.app", Toast.LENGTH_LONG).show();
                }else if(itemId == 3)
                {
                    startActivity(new Intent(mContext,UserProfile.class));
                }
            }
        });*/

       fabSpeedDial = (io.github.yavski.fabspeeddial.FabSpeedDial) findViewById(R.id.fab_speed_dial);
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                //TODO: Start some activity
                switch (menuItem.getItemId())
                {
                    case R.id.QR_Code_Scanner :
                        startActivity(new Intent(mContext,QRCodeReaderActivity.class));
                        break;
                    case R.id.support:
                        sendEmail();
                        break;
                    case R.id.profile:
                        startActivity(new Intent(mContext,UserProfile.class));
                        break;
                }
                return false;
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

    private void emailIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_EMAIL, "emailaddress@emailaddress.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        intent.putExtra(Intent.EXTRA_TEXT, "I'm email body.");

        startActivity(Intent.createChooser(intent, "Send Email"));
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

    @SuppressLint("LongLogTag")
    protected void sendEmail() {
        Log.i("Send email", "");
        String[] TO = {"pasistence@mirchilive.app"};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Require Help");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
          //  Log.i("Finished sending email...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(mContext, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
