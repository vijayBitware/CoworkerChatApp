package com.bitware.coworker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.bitware.coworker.R;
import com.bitware.coworker.baseUtils.Const;
import com.bitware.coworker.baseUtils.SharedHelper;

public class Splash_activity extends AppCompatActivity {
    String path;
    private Handler handler;
    private String TAG=Splash_activity.class.getSimpleName();

    private void Setheme(String themevalue) {
        switch (themevalue)
        {
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String themevalue=SharedHelper.getKey(this,"theme_value");
        Setheme(themevalue);
//        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_activity);



        String sign = SharedHelper.getKey(this, "sign");
        if (sign.equalsIgnoreCase("true")) {
            Intent mainIntent = new Intent(Splash_activity.this, MainActivity.class);
//            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
//            finish();
        } else {
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    boolean[] data;
                    data = new boolean[]{true, true, true, true};
                    SharedHelper.putKey(Splash_activity.this, "wifi_data", data);
                    data = new boolean[]{false, false, false, false};
                    SharedHelper.putKey(Splash_activity.this, "mobile_data", data);
                    SharedHelper.putKey(Splash_activity.this, "roaming_data", data);
                    SharedHelper.putKey(Splash_activity.this, "single_noti_tone", String.valueOf(Const.URI.default_single_message));
                    SharedHelper.putKey(Splash_activity.this, "group_noti_tone", String.valueOf(Const.URI.default_group_message));
                    SharedHelper.putKey(Splash_activity.this, "call_noti_tone", String.valueOf(Const.URI.default_call));
                    SharedHelper.putKey(Splash_activity.this, "single_vib_value", "300");
                    SharedHelper.putKey(Splash_activity.this, "group_vib_value", "300");
                    SharedHelper.putKey(Splash_activity.this, "call_vib_value", "300");
                    SharedHelper.putKey(Splash_activity.this, "play_sounds", "yes");
                    SharedHelper.putKey(Splash_activity.this, "sign_settings", "true");
                    SharedHelper.putKey(Splash_activity.this,"single_light_value",""+Const.URI.single_light);
                    SharedHelper.putKey(Splash_activity.this,"group_light_value",""+Const.URI.group_light);
                    SharedHelper.putKey(Splash_activity.this,"theme_value","1");
                    SharedHelper.putKey(Splash_activity.this,"enter_key","false");




                    Intent mainIntent = new Intent(Splash_activity.this, TermsAndConditions_activity.class);
//                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);



                }
            }, 2000);
        }


    }



}
