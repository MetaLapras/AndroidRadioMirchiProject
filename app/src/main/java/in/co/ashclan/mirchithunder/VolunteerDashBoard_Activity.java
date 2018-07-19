package in.co.ashclan.mirchithunder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

public class VolunteerDashBoard_Activity extends AppCompatActivity implements View.OnClickListener {
    CardView cardRegisterUser,cardCheckPoint,cardScanUser,cardLogOut;
    Context mContext;
    //Dialog Interface
    MaterialEditText edtadminUserid,edtadminPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_dash_board);
        mInit();

        cardRegisterUser.setOnClickListener(this);
        cardCheckPoint.setOnClickListener(this);
        cardScanUser.setOnClickListener(this);
        cardLogOut.setOnClickListener(this);
    }

    private void mInit() {
        mContext = VolunteerDashBoard_Activity.this;
        cardRegisterUser = findViewById(R.id.card_register_user);
        cardScanUser = findViewById(R.id.card_scan_user);
        cardCheckPoint = findViewById(R.id.card_check_point);
        cardLogOut = findViewById(R.id.card_logout_1);
    }

    @Override
    public void onClick(View view) {
       if(view == cardCheckPoint)
       {
           //startActivity(new Intent(mContext,CheckPointActivity.class));
           //startActivity(new Intent(mContext,UploadAllDataActivity.class));
           startActivity(new Intent(mContext,RankingActivity.class));

       }else if(view == cardRegisterUser)
       {
         /*  if(edtadminUserid.getText().length()==0)
           {
               edtadminUserid.setError("Please Valid User ID");
           } else if (edtadminPassword.getText().length()==0) {
               edtadminPassword.setError("Please Valid Password");
           }else {
               showAdminLogin();
           }*/
           showAdminLogin();

       }else if(view == cardScanUser)
       {
           startActivity(new Intent(mContext,ScanRunnerActivity.class));
           
       }else if(view == cardLogOut)
       {
           startActivity(new Intent(mContext,VolunteerLogin.class));
           finish();
       }
    }
    private void showAdminLogin() {

        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(mContext);
        LayoutInflater layoutInflater = this.getLayoutInflater();

        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please Wait .... ");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        View add_menu_layout = layoutInflater.inflate(R.layout.custom_login_dialog,null);

        edtadminUserid = (MaterialEditText) add_menu_layout.findViewById(R.id.edt_admin_userid);
        edtadminPassword = (MaterialEditText) add_menu_layout.findViewById(R.id.edt_admin_password);

        alertDialog.setView(add_menu_layout);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (edtadminUserid.getText().length() == 0) {
                    Toast.makeText(mContext, "Please Enter Valid User ID", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else if (edtadminPassword.getText().length() == 0) {
                    Toast.makeText(mContext, "Please Enter Valid Password", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {

                    if (edtadminUserid.getText().toString().equals("admin") && edtadminPassword.getText().toString().equals("pass@123")) {
                        startActivity(new Intent(mContext, AdminPanelActivity.class));
                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(mContext, "Invalid User Id or Password", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        dialog.dismiss();
                    }
                }
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(mContext, "Operation Cancel by User", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                progressDialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
