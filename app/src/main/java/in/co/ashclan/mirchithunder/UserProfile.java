package in.co.ashclan.mirchithunder;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;
import me.anwarshahriar.calligrapher.Calligrapher;

public class UserProfile extends AppCompatActivity {
    Context mcontext;
    CircleImageView imgProfile;
    ImageView imgEdit;
    TextView txtName,txtUserType,txtemailid,txtgender,txtMobileNo,txtPaymentType,txtReceiptId;
    String firtsName,lastName,Status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mInit();
        onLoad();
    }

    private void onLoad() {

    }

    private void mInit() {
        mcontext = UserProfile.this;
        txtName = (TextView)findViewById(R.id.txt_name);
        txtemailid = (TextView)findViewById(R.id.txt_email_id);
        txtUserType = (TextView)findViewById(R.id.txt_user_type);
        txtgender = (TextView)findViewById(R.id.txt_gender);
        txtMobileNo = (TextView)findViewById(R.id.txt_mobile_no);
        txtPaymentType = (TextView)findViewById(R.id.txt_payment_type);
        txtReceiptId = (TextView)findViewById(R.id.txt_receipt_id);

        imgEdit = (CircleImageView)findViewById(R.id.profile);

//        Calligrapher calligrapher = new Calligrapher(this);
//        calligrapher.setFont(this, "calibri.ttf", true);
    }

}
