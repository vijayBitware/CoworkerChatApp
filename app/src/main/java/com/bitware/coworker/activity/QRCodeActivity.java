package com.bitware.coworker.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import io.socket.emitter.Emitter;
import io.socket.client.IO;
import io.socket.client.Socket;

import com.google.zxing.Result;
import com.bitware.coworker.R;
import com.bitware.coworker.Service.ServiceClasss;
import com.bitware.coworker.baseUtils.Const;
import com.bitware.coworker.baseUtils.SharedHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class QRCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler  {
    public static Socket socket;
    ViewGroup contentFrame;
    RelativeLayout line;
    //
//    private SurfaceView mySurfaceView;
//    private QREader qrEader;
    ImageView back;
    public static Toolbar toolbar;
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            /*ChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {*/
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", SharedHelper.getKey(getApplicationContext(), "id"));
                Log.e("connect", "online" + jsonObject);
                socket.emit("online", jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
             /*   }
            });*/

        }


    };
        private ZXingScannerView mScannerView;
    private String groupId_loc;
    private SurfaceView mySurfaceView;
//    private QRCodeReaderView qrCodeReaderView;

    private void Setheme(String themevalue) {
        Log.d("Setheme: ", "themevalues:" + themevalue);
        switch (themevalue) {
            case "1":
                setTheme(R.style.AppThemeGreen);
                break;
            case "2":
                setTheme(R.style.AppThemeBlue);
                break;
            case "3":
                setTheme(R.style.AppThemeIndigo);
                break;
            case "4":
                setTheme(R.style.AppThemeGrey);
                break;
            case "5":
                setTheme(R.style.AppThemeYellow);
                break;
            case "6":
                setTheme(R.style.AppThemeOrange);
                break;
            case "7":
                setTheme(R.style.AppThemePurple);
                break;
            case "8":
                setTheme(R.style.AppThemePaleGreen);
                break;
            case "9":
                setTheme(R.style.AppThemelightBlue);
                break;
            case "10":
                setTheme(R.style.AppThemePink);
                break;
            case "11":
                setTheme(R.style.AppThemelightGreen);
                break;
            case "12":
                setTheme(R.style.AppThemelightRed);
                break;
            default:
                setTheme(R.style.AppThemeGreen);
                break;
        }
    }

    public static int getPrimaryCOlor(Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, value, true);
        return value.data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String themevalue=SharedHelper.getKey(this,"theme_value");
        Setheme(themevalue);
        setContentView(R.layout.activity_qrcode);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
        }
        IO.Options opts = new IO.Options();
        opts.forceNew = true;
        opts.reconnection = false;

        line=(RelativeLayout)findViewById(R.id.line);
        Animation emerge = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        line.startAnimation(emerge);


        try {
            socket = IO.socket(Const.chatSocketURL, opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.e("SOCKET.IO ", e.getMessage());
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getPrimaryCOlor(QRCodeActivity.this));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
        }
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

//        qrCodeReaderView = (QRCodeReaderView) findViewById(R.id.camera_view);
//        qrCodeReaderView.setOnQRCodeReadListener(this);

        // Use this function to enable/disable decoding
//        qrCodeReaderView.setQRDecodingEnabled(true);


//        mySurfaceView = (SurfaceView) findViewById(R.id.camera_view);
        // Init QREader
//        qrEader = new QREader.Builder(this, mySurfaceView, new QRDataListener() {
//            @Override
//            public void onDetected(final String data) {
//                Log.d("QREader", "Value : " + data);
//
//            }
//        }).facing(QREader.BACK_CAM)
//                .enableAutofocus(true)
//                .height(mySurfaceView.getHeight())
//                .width(mySurfaceView.getWidth())
//                .build();
//        qrEader.initAndStart(mySurfaceView);


        contentFrame = (ViewGroup) findViewById(R.id.camera_view);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);


    }


    @Override
    protected void onResume() {
        super.onResume();

        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
//        qrCodeReaderView.startCamera();
//        qrCodeReaderView.setQRDecodingEnabled(true);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mScannerView.stopCamera();
//        qrCodeReaderView.stopCamera();// Stop camera on pause
    }

    @Override
    protected void onStop() {
        super.onStop();
        mScannerView.stopCamera();
//        qrCodeReaderView.stopCamera();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                try{
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                        mScannerView = new ZXingScannerView(this);
//                        contentFrame.addView(mScannerView);
                    }
                    else {
                        Toast.makeText(this, "You need to give camera permission to use this feature", Toast.LENGTH_SHORT).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                        }
                    }
                }
                catch (Exception e)
                {

                }
                break;


            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }





    @Override
    protected void onPause() {
        super.onPause();
//        qrCodeReaderView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {


        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.

        //QRidemit
        Log.d("handleResult: ",rawResult.getText());
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("from", SharedHelper.getKey(QRCodeActivity.this, "id"));
            jsonObject.put("id", rawResult.getText());
            ServiceClasss.Emitters emitters=new ServiceClasss.Emitters(QRCodeActivity.this);
            emitters.qridemit(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(QRCodeActivity.this);
            }
        }, 5000);

        onBackPressed();

    }


//    @Override
//    public void onQRCodeRead(String text, PointF[] points) {
//        Toast.makeText(this, "values:"+text, Toast.LENGTH_SHORT).show();
//        Log.d("onQRCodeRead: ","asdsad:"+text   );
//        try {
//            qrCodeReaderView.setQRDecodingEnabled(false);
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("from", SharedHelper.getKey(QRCodeActivity.this, "id"));
//            jsonObject.put("id", text);
//            ServiceClasss.Emitters emitters=new ServiceClasss.Emitters(QRCodeActivity.this);
//            emitters.qridemit(jsonObject);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        onBackPressed();
//    }
}

