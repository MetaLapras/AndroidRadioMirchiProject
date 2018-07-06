package in.co.ashclan.mirchithunder;

import android.Manifest;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;

import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;
//import in.co.ashclan.mirchithunder.utils.RuntimePermissionUtil;

public class QRCodeReaderActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String cameraPerm = Manifest.permission.CAMERA;

    private SurfaceView QRCodeReder;
    private QREader qrEader;
    Context mContext;


    boolean hasCamerPermission = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_reader);
      //  hasCamerPermission =RuntimePermissionUtil.checkPermissonGranted(mContext,cameraPerm);

        mInit();
        QRReader();
    }

    public void QRReader(){

    }


    private void mInit(){
        mContext = QRCodeReaderActivity.this;
        QRCodeReder = (SurfaceView)findViewById(R.id.qr_reader_view);
    }

    @Override
    public void onClick(View view) {

    }
}
