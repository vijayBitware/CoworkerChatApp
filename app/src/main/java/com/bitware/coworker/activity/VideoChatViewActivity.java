package com.bitware.coworker.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bitware.coworker.R;
import com.bitware.coworker.baseUtils.AsyncTaskCompleteListener;
import com.bitware.coworker.baseUtils.Const;
import com.bitware.coworker.baseUtils.PostHelper;
import com.bitware.coworker.baseUtils.SharedHelper;
import com.bitware.coworker.baseUtils.Utils;
import com.bitware.coworker.models.CallsEventModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

public class VideoChatViewActivity extends AppCompatActivity implements AsyncTaskCompleteListener {
    private  final String LOG_TAG = VideoChatViewActivity.class.getSimpleName();
    private  final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    private  final int PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1;
    public  Vibrator vibrator;
    public  boolean endVibration = false;
    public  Boolean isIncoming;
    public TextView timer_i_vo, timer_o_vo, timer_i_vi, timer_o_vi;
    public  RelativeLayout user_icon_o_vi;
    public  TextView user_name_i_vo, user_name_o_vo, user_name_i_vi, user_name_o_vi;
    public  SurfaceView ideo_preview;
    public  SurfaceView ideo_preview_2;
    public  FrameLayout container_pre;
    public long timeInMilliseconds = 0L;
    public long timeSwapBuff = 0L;
    public long updatedTime = 0L;
    public TextView timer;
    public MediaPlayer mPlayer;
    public RtcEngine mRtcEngine;// Tutorial Step 1
    public ImageView accept, userPhoto, user_icon_i_vo, user_icon_i_vi, user_icon_o_vo;
    public String callType = "", channelId = "", endCall = "";
    private Handler customHandler = new Handler();
    private  long startTime = 0L;
    //private static AudioManager mAudioMgr;
    public  CountDownTimer countDownTimer;
    public boolean isOn = false;
    public Runnable updateTimerThread = new Runnable() {

        public void run() {
            if (callType.equalsIgnoreCase("videoCall")) {
                ideo_preview_2.setVisibility(View.GONE);
               container_pre.setVisibility(View.VISIBLE);
                user_icon_o_vi.setVisibility(View.GONE);

            }
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            long seconds = timeInMilliseconds / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            //long days = hours / 24;
            @SuppressLint("DefaultLocale") String time = /*days + ":" +*/String.format("%02d", hours % 24) + ":" + String.format("%02d", minutes % 60) + ":" + String.format("%02d", seconds % 60);

            if (callType.equalsIgnoreCase("voiceCall")) {

                timer_i_vo.setText(time);
                timer_o_vo.setText(time);

            } else {

                timer_i_vi.setText(time);
                timer_o_vi.setText(time);

            }

            customHandler.postDelayed(this, 0);
        }

    };
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() { // Tutorial Step 1
        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) { // Tutorial Step 5
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupRemoteVideo(uid);
                }
            });
        }

        @Override
        public void onUserOffline(int uid, int reason) { // Tutorial Step 7
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserLeft();
                }
            });
        }

        @Override
        public void onUserMuteVideo(final int uid, final boolean muted) { // Tutorial Step 10
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserVideoMuted(uid, muted);
                }
            });
        }
    };
    String calltype;
    Camera camera;
    //set call duration
    LinearLayout voice_i, voice_o;
    FrameLayout video_i, video_o;
    private Camera.Size size;
    private Window wind;
    private Intent intent;
    private ImageView volume, speakerImgOn, speakerImgOff, muteOff, muteOn;
    private boolean previewing = false;

    private void acceptStatic(String channelId) {
        try {
            mRtcEngine.joinChannel(null, channelId, "Extra Optional Data", 0); // if you do not specify the uid, we will generate the uid for you
            mPlayer.stop();
            vibrator.cancel();
            endVibration = true;


//        accept.setVisibility(View.GONE);
            startTime = SystemClock.uptimeMillis();
            customHandler.postDelayed(updateTimerThread, 0);

            countDownTimer.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void endCall() {

        VideoChatViewActivity.this.finish();

    }

    private void Setheme(String themevalue) {
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

    public void onVolumeOff(View view) {
        ImageView iv = (ImageView) view;
        AudioManager mAudioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (isOn) {
            mAudioMgr.setMode(AudioManager.MODE_IN_CALL);
            mAudioMgr.setMode(AudioManager.MODE_NORMAL);
            mAudioMgr.setSpeakerphoneOn(isOn);
            iv.clearColorFilter();
            iv.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.MULTIPLY);
            isOn = false;
        } else {
            //Seems that this back and forth somehow resets the audio channel
            mAudioMgr.setMode(AudioManager.MODE_NORMAL);
            mAudioMgr.setMode(AudioManager.MODE_IN_CALL);
            mAudioMgr.setSpeakerphoneOn(isOn);
            iv.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
            isOn = true;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String themevalue = SharedHelper.getKey(this, "theme_value");
        Setheme(themevalue);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Uri notification = Uri.parse(SharedHelper.getKey(VideoChatViewActivity.this, "call_noti_tone"));
        Log.d("onCreate: ", "noti_call_value:" + SharedHelper.getKey(VideoChatViewActivity.this, "call_noti_tone"));

        mPlayer = MediaPlayer.create(getApplicationContext(), notification);
        vibrator = (Vibrator) VideoChatViewActivity.this.getSystemService(Context.VIBRATOR_SERVICE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        wind = this.getWindow();
        wind.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        wind.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        wind.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.voice_call_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        container_pre = (FrameLayout) findViewById(R.id.local_video_view_container);

        voice_i = (LinearLayout) findViewById(R.id.incoming_voice_call);
        voice_o = (LinearLayout) findViewById(R.id.outgoing_voice_call);
        video_i = (FrameLayout) findViewById(R.id.incoming_video_call);
        ideo_preview = (SurfaceView) findViewById(R.id.back_preview);
        ideo_preview_2 = (SurfaceView) findViewById(R.id.back_preview_2);
        video_o = (FrameLayout) findViewById(R.id.outgoing_video_call);
        timer_i_vo = (TextView) findViewById(R.id.ring_countDown_i_vo);
        timer_o_vo = (TextView) findViewById(R.id.ring_countDown_o_vo);
        timer_i_vi = (TextView) findViewById(R.id.ring_countDown_i_vi);
        timer_o_vi = (TextView) findViewById(R.id.ring_countDown_o_vi);
        speakerImgOff = (ImageView) findViewById(R.id.speakerImgOff);
        speakerImgOn = (ImageView) findViewById(R.id.speakerImgOn);
        muteOff = (ImageView) findViewById(R.id.muteOff);
        muteOn = (ImageView) findViewById(R.id.muteOn);

        final AudioManager mAudioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        Utils.enableStrictMode();

        accept = (ImageView) findViewById(R.id.accept_call);
        userPhoto = (ImageView) findViewById(R.id.image_icon);
        user_icon_i_vo = (ImageView) findViewById(R.id.user_icon_i_vo);
        user_icon_o_vi = (RelativeLayout) findViewById(R.id.user_icon_o_vi);

        timer = (TextView) findViewById(R.id.ring_countDown);


        speakerImgOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//

                speakerImgOff.setVisibility(View.GONE);
                speakerImgOn.setVisibility(View.VISIBLE);
                mAudioMgr.setMode(AudioManager.MODE_NORMAL);
                mAudioMgr.setMode(AudioManager.MODE_IN_CALL);
                mAudioMgr.setSpeakerphoneOn(true);
//


            }
        });


        speakerImgOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//

                speakerImgOff.setVisibility(View.VISIBLE);
                speakerImgOn.setVisibility(View.GONE);
                mAudioMgr.setMode(AudioManager.MODE_IN_CALL);
                mAudioMgr.setMode(AudioManager.MODE_NORMAL);
                mAudioMgr.setSpeakerphoneOn(false);

//


            }
        });


        muteOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                muteOn.setVisibility(View.VISIBLE);
                muteOff.setVisibility(View.GONE);


//                if (iv.isSelected()) {
//                    iv.setSelected(false);
//                    iv.clearColorFilter();
//                } else {
//                    iv.setSelected(true);
//                    iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
//                }

                mRtcEngine.muteLocalVideoStream(true);

//                FrameLayout container = (FrameLayout) findViewById(R.id.local_video_view_container);
//                SurfaceView surfaceView = (SurfaceView) container.getChildAt(0);
//                surfaceView.setZOrderMediaOverlay(!iv.isSelected());
//                surfaceView.setVisibility(iv.isSelected() ? View.GONE : View.VISIBLE);
            }
        });


        muteOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                muteOff.setVisibility(View.VISIBLE);
                muteOn.setVisibility(View.GONE);
                mRtcEngine.muteLocalVideoStream(false);
            }
        });


        intent = getIntent();
        if (intent != null) {
            channelId = intent.getStringExtra("channelId");
            callType = intent.getStringExtra("call");
            if (intent.getStringExtra("receive").equalsIgnoreCase("no")) {

                callType = intent.getStringExtra("call");
                Log.e("onCreate: ", "asdas:" + callType);
                if (callType.equalsIgnoreCase("voiceCall")) {

                    Glide.with(VideoChatViewActivity.this).load(intent.getStringExtra("image")).error(R.drawable.ic_person).into(userPhoto);


                    isIncoming = false;
                    voice_o.setVisibility(View.VISIBLE);
                    timer_i_vi.setText("INCOMING CALL");

                    try {
//                    AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//                    am.setMode(AudioManager.MODE_IN_CALL);
//                    am.setSpeakerphoneOn(false);
                        mPlayer = new MediaPlayer();
                        Uri tone = Uri.parse("android.resource://com.zybertron.zoechat/" + R.raw.ringsound);
                        mPlayer.setDataSource(VideoChatViewActivity.this, tone);
                        mPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                        mPlayer.setLooping(true);
                        mPlayer.prepare();
                        mPlayer.start();
                        endCall = "no";
//                        accept.setVisibility(View.GONE);
                        notificationClear();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    video_o.setVisibility(View.VISIBLE);
                    final SurfaceHolder surfaceHolder;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    surfaceHolder = ideo_preview_2.getHolder();
                    surfaceHolder.addCallback(new SurfaceHolder.Callback() {
                        @Override
                        public void surfaceCreated(SurfaceHolder holder) {
                            camera = openFrontFacingCameraGingerbread();
                            holder = ideo_preview_2.getHolder();
                            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

                        }

                        @Override
                        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                            if (previewing) {
                                camera.stopPreview();
                                previewing = false;
                            }
                            if (camera != null) {
                                try {

                                    Camera.Parameters myParameters = camera.getParameters();
                                    Camera.Size myBestSize = getBestPreviewSize(camera.getParameters().getSupportedPreviewSizes(), width, height);


                                    myParameters.setPreviewSize(myBestSize.width, myBestSize.height);
                                    camera.setParameters(myParameters);
                                    camera.setPreviewDisplay(surfaceHolder);
                                    camera.setDisplayOrientation(90);
                                    camera.startPreview();
                                    previewing = true;

                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }

                        }

                        @Override
                        public void surfaceDestroyed(SurfaceHolder holder) {

                            try {
                                camera.stopPreview();
                                camera.release();
                                camera = null;
                                previewing = false;

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

                    isIncoming = false;
                    mPlayer = MediaPlayer.create(VideoChatViewActivity.this, R.raw.ringsound);
                    mPlayer.start();
                    endCall = "no";
//                    accept.setVisibility(View.GONE);
                    notificationClear();


                }
            } else if (intent.getStringExtra("receive").equalsIgnoreCase("yes")) {
//               playringtone();
                Log.e("onCreate: ", "asdas:" + callType);
                if (callType.equalsIgnoreCase("voiceCall")) {
                    voice_i.setVisibility(View.VISIBLE);

                } else {

                    video_i.setVisibility(View.VISIBLE);
                    final SurfaceHolder surfaceHolder;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    surfaceHolder = ideo_preview.getHolder();
                    surfaceHolder.addCallback(new SurfaceHolder.Callback() {
                        @Override
                        public void surfaceCreated(SurfaceHolder holder) {
                            camera = openFrontFacingCameraGingerbread();
                            holder = ideo_preview.getHolder();
                            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

                        }

                        @Override
                        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                            if (previewing) {
                                camera.stopPreview();
                                previewing = false;
                            }
                            if (camera != null) {
                                try {

                                    Camera.Parameters myParameters = camera.getParameters();
                                    Camera.Size myBestSize = getBestPreviewSize(camera.getParameters().getSupportedPreviewSizes(), width, height);


                                    myParameters.setPreviewSize(myBestSize.width, myBestSize.height);
                                    camera.setParameters(myParameters);
                                    camera.setPreviewDisplay(surfaceHolder);
                                    camera.setDisplayOrientation(90);
                                    camera.startPreview();
                                    previewing = true;

                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }

                        }

                        @Override
                        public void surfaceDestroyed(SurfaceHolder holder) {
                            try {
                                camera.stopPreview();
                                camera.release();
                                camera = null;
                                previewing = false;

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


                }

            }

            callType = intent.getStringExtra("call");

            if (intent.getStringExtra("receive").equalsIgnoreCase("yes")) {
                Glide.with(VideoChatViewActivity.this).load(intent.getStringExtra("image")).error(R.drawable.ic_person).into(user_icon_i_vo);
            } else {

                Glide.with(VideoChatViewActivity.this).load(intent.getStringExtra("image")).error(R.drawable.ic_person).into(userPhoto);
            }

            if (callType.equalsIgnoreCase("voiceCall")) {
                if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO) && checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA)) {
                    initAgoraEngineAndJoinChannel();
                }
                userPhoto.setVisibility(View.VISIBLE);
                notificationClear();
                isOn = false;
            } else if (callType.equalsIgnoreCase("videoCall")) {
                if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO) && checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA)) {
                    initAgoraEngineAndJoinChannel();
                }
                userPhoto.setVisibility(View.GONE);
                notificationClear();

            }
//            TextView userName = (TextView) findViewById(R.id.username);
//            TextView userNumber = (TextView) findViewById(R.id.userNumber);
//            userName.setText(intent.getStringExtra("user_name"));

            user_name_i_vi = (TextView) findViewById(R.id.username_incoming);
            user_name_o_vi = (TextView) findViewById(R.id.username_outgoing_video);
            user_name_i_vo = (TextView) findViewById(R.id.username_incoming_video);
            user_name_o_vo = (TextView) findViewById(R.id.username_outgoing_voice);

            user_name_i_vi.setText(intent.getStringExtra("user_name"));
            user_name_i_vo.setText(intent.getStringExtra("user_name"));
            user_name_o_vi.setText(intent.getStringExtra("user_name"));
            user_name_o_vo.setText(intent.getStringExtra("user_name"));
//            userNumber.setText(intent.getStringExtra("zoeChatID"));


            countDownTimer = new CountDownTimer(45000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    finish();
                }
            }.start();


        }
    }


    private Camera.Size getBestPreviewSize(List<Camera.Size> sizes, int w, int h) {
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double ratio = (double) h / w;
        double minDiff = Double.MAX_VALUE;
        double newDiff;
        for (Camera.Size size : sizes) {
            newDiff = Math.abs((double) size.width / size.height - ratio);
            if (newDiff < minDiff) {
                optimalSize = size;
                minDiff = newDiff;
            }
        }
        return optimalSize;
    }


    private Camera openFrontFacingCameraGingerbread() {
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.e("asdasd", "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }

        return cam;
    }

    private void playringtone() {
        Log.d("onCreate:video ", intent.getStringExtra("receive"));

        Log.d("onCreate: ", "noti:" + mPlayer.isPlaying());
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
            Uri notification = Uri.parse(SharedHelper.getKey(VideoChatViewActivity.this, "call_noti_tone"));
            mPlayer = MediaPlayer.create(getApplicationContext(), notification);

        } else {
            Uri notification = Uri.parse(SharedHelper.getKey(VideoChatViewActivity.this, "call_noti_tone"));
            mPlayer = MediaPlayer.create(getApplicationContext(), notification);
            mPlayer.start();
        }

        endCall = "yes";
        accept.setVisibility(View.VISIBLE);
        notificationClear();
    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (intent.getStringExtra("receive").equalsIgnoreCase("yes")) {

                playringtone();
                endVibration = false;
                playvibration();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


    }

    private void playvibration() {
        final long[] vib_val = Const.URI.default_call_vibrate;


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (endVibration) {
//                    handler.postDelayed(this, 1000);
                } else {
                    vibrator.vibrate(vib_val, 1);
                }
            }
        }, 2000);


    }

    private void notificationClear() {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    private void initAgoraEngineAndJoinChannel() {
        try {

            initializeAgoraEngine();     // Tutorial Step 1
            if (callType.equalsIgnoreCase("voiceCall")) {
                FrameLayout container = (FrameLayout) findViewById(R.id.local_video_view_container);
                container.setVisibility(View.INVISIBLE);
                findViewById(R.id.switch_camera).setVisibility(View.GONE);
//            findViewById(R.id.camera_off).setVisibility(View.GONE);
                //volume.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
                isOn = false;
            } else {
                setupVideoProfile();         // Tutorial Step 2
                setupLocalVideo();  // Tutorial Step 3

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //joinChannel();               // Tutorial Step 4
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            countDownTimer.cancel();
            mPlayer.stop();
            vibrator.cancel();
            endVibration = true;
//            leaveChannel();
            try {
                mRtcEngine.destroy();
            } catch (Exception e) {

            }

            RtcEngine.destroy();
            mRtcEngine = null;

        } catch (Exception e) {

        }

        customHandler.removeCallbacks(updateTimerThread);
    }

    public boolean checkSelfPermission(String permission, int requestCode) {
        Log.i(LOG_TAG, "checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.i(LOG_TAG, "onRequestPermissionsResult " + grantResults[0] + " " + requestCode);

        switch (requestCode) {
            case PERMISSION_REQ_ID_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA);
                } else {
                    showLongToast("No permission for " + Manifest.permission.RECORD_AUDIO);
                    finish();
                }
                break;
            }
            case PERMISSION_REQ_ID_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initAgoraEngineAndJoinChannel();
                } else {
                    showLongToast("No permission for " + Manifest.permission.CAMERA);
                    finish();
                }
                break;
            }
        }
    }

    public final void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        leaveChannel();
        try {
            mRtcEngine.destroy();
        } catch (Exception e) {

        }
        RtcEngine.destroy();


        mRtcEngine = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        leaveChannel();
        RtcEngine.destroy();

        try {
            mRtcEngine.destroy();
        } catch (Exception e) {

        }

        mRtcEngine = null;

        EventBus.getDefault().unregister(this);
    }

    // Tutorial Step 10
    public void onLocalVideoMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }

        mRtcEngine.muteLocalVideoStream(iv.isSelected());

        FrameLayout container = (FrameLayout) findViewById(R.id.local_video_view_container);
        SurfaceView surfaceView = (SurfaceView) container.getChildAt(0);
        surfaceView.setZOrderMediaOverlay(!iv.isSelected());
        surfaceView.setVisibility(iv.isSelected() ? View.GONE : View.VISIBLE);
    }

    // Tutorial Step 9
    public void onLocalAudioMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }

        mRtcEngine.muteLocalAudioStream(iv.isSelected());
    }

    // Tutorial Step 8
    public void onSwitchCameraClicked(View view) {
        mRtcEngine.switchCamera();
    }

    // Tutorial Step 6
    public void onEncCallClicked(View view) {
        customHandler.removeCallbacks(updateTimerThread);

        if (endCall.equalsIgnoreCase("no")) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("from", SharedHelper.getKey(VideoChatViewActivity.this, "id"));
                jsonObject.put("to", intent.getStringExtra("zoeChatID"));
                jsonObject.put("pushType", "out");
                new PostHelper(Const.Methods.END_CALL, jsonObject.toString(), Const.ServiceCode.END_CALL, VideoChatViewActivity.this, VideoChatViewActivity.this);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (endCall.equalsIgnoreCase("yes")) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("from", SharedHelper.getKey(VideoChatViewActivity.this, "id"));
                jsonObject.put("to", intent.getStringExtra("zoeChatID"));
                jsonObject.put("pushType", "inc");

                new PostHelper(Const.Methods.END_CALL, jsonObject.toString(), Const.ServiceCode.END_CALL, VideoChatViewActivity.this, VideoChatViewActivity.this);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        finish();


    }

    public void onAcceptCall(View view) {
        try {
            mPlayer.stop();
            vibrator.cancel();
            endVibration = true;
            countDownTimer.cancel();
            isIncoming = true;
            startTime = SystemClock.uptimeMillis();
            customHandler.postDelayed(updateTimerThread, 0);

//        findViewById(R.id.accept_call).setVisibility(View.GONE);
            voice_i.setVisibility(View.GONE);

            Glide.with(VideoChatViewActivity.this).load(intent.getStringExtra("image")).error(R.drawable.ic_person).into(userPhoto);


            if (callType.equalsIgnoreCase("voicecall")) {
                voice_o.setVisibility(View.VISIBLE);
            } else {

                video_i.setVisibility(View.GONE);
                video_o.setVisibility(View.VISIBLE);
                user_icon_o_vi.setVisibility(View.GONE);
                ideo_preview.setVisibility(View.GONE);
            }


            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("from", SharedHelper.getKey(VideoChatViewActivity.this, "id"));
                jsonObject.put("to", intent.getStringExtra("zoeChatID"));
                String channel = intent.getStringExtra("zoeChatID");
                jsonObject.put("channelId", channel);

                new PostHelper(Const.Methods.ACCEPT_CALL, jsonObject.toString(), Const.ServiceCode.CALL_CODE, VideoChatViewActivity.this, VideoChatViewActivity.this);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Tutorial Step 1
    private void initializeAgoraEngine() {
        try {
            mRtcEngine.destroy();
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.private_app_id), mRtcEventHandler);
            if (callType.equalsIgnoreCase("voiceCall")) {
                mRtcEngine.setEnableSpeakerphone(false);
                isOn = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Tutorial Step 2
    private void setupVideoProfile() {

        mRtcEngine.enableVideo();
        mRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_360P, false);
    }

    // Tutorial Step 3
    private void setupLocalVideo() {
        FrameLayout container = (FrameLayout) findViewById(R.id.local_video_view_container);
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_ADAPTIVE, 0));

    }

    // Tutorial Step 4
    private void joinChannel() {
        mRtcEngine.joinChannel(null, channelId, "Extra Optional Data", 0); // if you do not specify the uid, we will generate the uid for you
        mPlayer.stop();
        endVibration = true;
        vibrator.cancel();
        countDownTimer.cancel();
        isOn = true;

    }

    // Tutorial Step 5
    private void setupRemoteVideo(int uid) {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);

        if (container.getChildCount() >= 1) {
            return;
        }

        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        container.addView(surfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_ADAPTIVE, uid));

        surfaceView.setTag(uid); // for mark purpose
//        View tipMsg = findViewById(R.id.quick_tips_when_use_agora_sdk); // optional UI
//        tipMsg.setVisibility(View.GONE);
    }

    // Tutorial Step 6
    private void leaveChannel() {
        if (mRtcEngine != null) {
            mRtcEngine.leaveChannel();
        }
        customHandler.removeCallbacks(updateTimerThread);

    }

    // Tutorial Step 7
    private void onRemoteUserLeft() {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);
        container.removeAllViews();
        finish();
//        View tipMsg = findViewById(R.id.quick_tips_when_use_agora_sdk); // optional UI
//        tipMsg.setVisibility(View.VISIBLE);
    }


    // Tutorial Step 10
    private void onRemoteUserVideoMuted(int uid, boolean muted) {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);

        SurfaceView surfaceView = (SurfaceView) container.getChildAt(0);

        Object tag = surfaceView.getTag();
        if (tag != null && (Integer) tag == uid) {
            surfaceView.setVisibility(muted ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onTaskCompleted(JSONObject response, int serviceCode) {
        Log.d("notification", "" + response);
        joinChannel();
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CallsEventModel event) {

        if (event.getType().equalsIgnoreCase("endCall")) {
            endCall();
        } else if (event.getType().equalsIgnoreCase("acceptCall")) {
            acceptStatic(event.getChannelId());
        }

    }
}
