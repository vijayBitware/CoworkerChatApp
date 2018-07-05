package com.bitware.coworker.activity;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bitware.coworker.R;
import com.bitware.coworker.baseUtils.SharedHelper;

public class CustomizeNotification extends AppCompatActivity {
    RelativeLayout enable,sound,vibrate;
    LinearLayout led_sound;
    TextView h_1,h_2,sound_value,vib_value;
    AppCompatCheckBox enable_check;
    Boolean isEnabled=false;
    public static int getPrimaryCOlor (Context context) {
        final TypedValue value = new TypedValue ();
        context.getTheme ().resolveAttribute (R.attr.colorPrimary, value, true);
        return value.data;
    }
    public static int getPrimaryDark (Context context) {
        final TypedValue value = new TypedValue ();
        context.getTheme ().resolveAttribute (R.attr.colorPrimaryDark, value, true);
        return value.data;
    }

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
        String themevalue= SharedHelper.getKey(this,"theme_value");
        Setheme(themevalue);
        setContentView(R.layout.activity_customize_notification);
        h_1=(TextView)findViewById(R.id.head_1);
        h_2=(TextView)findViewById(R.id.head_2);
        sound_value=(TextView)findViewById(R.id.sound_value);
        vib_value=(TextView)findViewById(R.id.vibrate_value);

        sound_value.setTextColor(getPrimaryCOlor(CustomizeNotification.this));
        vib_value.setTextColor(getPrimaryCOlor(CustomizeNotification.this));
        enable=(RelativeLayout)findViewById(R.id.enable);
        sound=(RelativeLayout)findViewById(R.id.sound_lay);
        vibrate=(RelativeLayout)findViewById(R.id.vibrate_lay);
        led_sound=(LinearLayout)findViewById(R.id.led_lay);
        enable_check=(AppCompatCheckBox)findViewById(R.id.enable_check);
        Toolbar toolbar = (Toolbar) findViewById(R.id.group_toolbar);
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_enabled} //enabled
                },
                new int[] {getResources().getColor(R.color.colorPrimary) }
        );

        enable_check.setSupportButtonTintList(colorStateList);

        toolbar.setBackgroundColor(getPrimaryCOlor(CustomizeNotification.this));
        toolbar.setTitle("Custom Notification");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (isEnabled)
        {
           enabled();
        }
        else
        {
            disabled();
        }
        enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isEnabled)
                {
                    disabled();
                    isEnabled=false;
                }
                else
                {
                    enabled();
                    isEnabled=true;
                }
            }
        });
        enable_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    enabled();
                    isEnabled=true;
                }
                else
                {
                    disabled();
                    isEnabled=false;
                }
            }
        });
        enabled();
        disabled();
    }

    private void disabled() {
        enable_check.setChecked(false);
        sound.setAlpha((float) 0.5);
        vibrate.setAlpha((float) 0.5);
        led_sound.setAlpha((float) 0.5);
    }

    private void enabled() {
        enable_check.setChecked(true);

        sound.setAlpha(1);
        vibrate.setAlpha(1);
        led_sound.setAlpha(1);
    }


}
