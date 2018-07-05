package com.bitware.coworker.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bitware.coworker.R;
import com.bitware.coworker.baseUtils.Const;
import com.bitware.coworker.baseUtils.SharedHelper;


public class AutoDownloadSettings extends AppCompatActivity {
    boolean[] data, test1;
    ImageView back;
    public static Toolbar toolbar;
    String photo, video, audio, docu;
    Boolean all;

    LinearLayout mobile, wi_fi, roaming;
    int selected_item ;
    Boolean selected_check;
    TextView mobile_data_val, roaming_data_val, wifi_data_val,head_text;
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
        String themevalue=SharedHelper.getKey(this,"theme_value");
        Setheme(themevalue);
        setContentView(R.layout.activity_auto_download_settings);

        mobile = (LinearLayout) findViewById(R.id.mobile_data);

        head_text = (TextView) findViewById(R.id.head_text);
        head_text.setTextColor(getPrimaryCOlor(AutoDownloadSettings.this));

        wi_fi = (LinearLayout) findViewById(R.id.wifi_data);
        roaming = (LinearLayout) findViewById(R.id.roaming_data);
        toolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mobile_data_val=(TextView)findViewById(R.id.mobile_data_name);
        roaming_data_val=(TextView)findViewById(R.id.roaming_data_name);
        wifi_data_val=(TextView)findViewById(R.id.wifi_data_name);

        final String sign = SharedHelper.getKey(this, "download_settings");
        if (sign.equalsIgnoreCase("true")) {
            test1 = SharedHelper.getKey("wifi_data", AutoDownloadSettings.this);
            String wifi_val = validatevalues(test1);
            test1 = SharedHelper.getKey("mobile_data", AutoDownloadSettings.this);
            String mob_val = validatevalues(test1);
            test1 = SharedHelper.getKey("roaming_data", AutoDownloadSettings.this);
            String roaming_val = validatevalues(test1);

            mobile_data_val.setText(mob_val);
            roaming_data_val.setText(roaming_val);
            wifi_data_val.setText(wifi_val);
        }
        else {
            data = new boolean[]{true, true, true, true};
            SharedHelper.putKey(AutoDownloadSettings.this, "download_settings", "true");
            SharedHelper.putKey(AutoDownloadSettings.this, "wifi_data", data);
            data = new boolean[]{false, false, false, false};
            SharedHelper.putKey(AutoDownloadSettings.this, "mobile_data", data);
            SharedHelper.putKey(AutoDownloadSettings.this, "roaming_data", data);
            test1 = SharedHelper.getKey("wifi_data", AutoDownloadSettings.this);
            String wifi_val = validatevalues(test1);
            test1 = SharedHelper.getKey("mobile_data", AutoDownloadSettings.this);
            String mob_val = validatevalues(test1);
            test1 = SharedHelper.getKey("roaming_data", AutoDownloadSettings.this);
            String roaming_val = validatevalues(test1);

            mobile_data_val.setText(mob_val);
            roaming_data_val.setText(roaming_val);
            wifi_data_val.setText(wifi_val);
        }

        wi_fi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sign.equalsIgnoreCase("true")) {
                    data = SharedHelper.getKey("wifi_data", AutoDownloadSettings.this);
                    Const.URI.wifi = data;
                }
                else {
                    data = new boolean[]{true, true, true, true};
                }

                opendialog(2);
            }
        });

        roaming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sign.equalsIgnoreCase("true")) {
                    data = SharedHelper.getKey("roaming_data", AutoDownloadSettings.this);
                    Const.URI.roaming_data = data;
                } else {
                    data = new boolean[]{false, false, false, false};
                }
                opendialog(3);
            }
        });

        mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sign.equalsIgnoreCase("true")) {
                    data = SharedHelper.getKey("mobile_data", AutoDownloadSettings.this);
                    Const.URI.mobile_data = data;
                } else {
                    data = new boolean[]{false, false, false, false};
                }
                opendialog(1);
            }
        });


    }

    private String validatevalues(boolean[] test1) {
        String ret;
        if (test1[0]) {
            photo = "true";
        } else {
            photo = "false";
        }
        if (test1[1]) {
            audio = "true";
        } else {
            audio = "false";
        }
        if (test1[2]) {
            video = "true";
        } else {
            video = "false";
        }
        if (test1[3]) {
            docu = "true";
        } else {
            docu = "false";
        }

        if (photo.equalsIgnoreCase("true")&&video.equalsIgnoreCase("true")&&audio.equalsIgnoreCase("true")&&docu.equalsIgnoreCase("true"))
        {
            ret=getResources().getString(R.string.all_media);
        } else if (photo.equalsIgnoreCase("true")&&video.equalsIgnoreCase("false")&&audio.equalsIgnoreCase("false")&&docu.equalsIgnoreCase("false"))
        {
            ret=getResources().getString(R.string.photo);

        }
        else if (photo.equalsIgnoreCase("false")&&video.equalsIgnoreCase("false")&&audio.equalsIgnoreCase("false")&&docu.equalsIgnoreCase("true"))
        {

            ret=getResources().getString(R.string.document);

        }
        else if (photo.equalsIgnoreCase("false")&&video.equalsIgnoreCase("true")&&audio.equalsIgnoreCase("false")&&docu.equalsIgnoreCase("false"))
        {
            ret=getResources().getString(R.string.video);

        } else if (photo.equalsIgnoreCase("false")&&video.equalsIgnoreCase("false")&&audio.equalsIgnoreCase("true")&&docu.equalsIgnoreCase("false"))
        {
            ret=getResources().getString(R.string.audio);
        }

        else if (photo.equalsIgnoreCase("true")&&video.equalsIgnoreCase("true")&&audio.equalsIgnoreCase("false")&&docu.equalsIgnoreCase("false"))
        {
            ret=getResources().getString(R.string.photo)+","+getResources().getString(R.string.video);

        }
        else if (photo.equalsIgnoreCase("false")&&video.equalsIgnoreCase("true")&&audio.equalsIgnoreCase("false")&&docu.equalsIgnoreCase("true"))
        {
            ret=getResources().getString(R.string.document)+","+getResources().getString(R.string.video);

        }
        else if (photo.equalsIgnoreCase("false")&&video.equalsIgnoreCase("true")&&audio.equalsIgnoreCase("true")&&docu.equalsIgnoreCase("false"))
        {
            ret=getResources().getString(R.string.audio)+","+getResources().getString(R.string.video);

        }
        else if (photo.equalsIgnoreCase("true")&&video.equalsIgnoreCase("false")&&audio.equalsIgnoreCase("true")&&docu.equalsIgnoreCase("false"))
        {
            ret=getResources().getString(R.string.audio)+","+getResources().getString(R.string.photo);

        } else if (photo.equalsIgnoreCase("true")&&video.equalsIgnoreCase("false")&&audio.equalsIgnoreCase("false")&&docu.equalsIgnoreCase("true"))
        {
            ret=getResources().getString(R.string.document)+","+getResources().getString(R.string.photo);

        }
        else if (photo.equalsIgnoreCase("false")&&video.equalsIgnoreCase("false")&&audio.equalsIgnoreCase("true")&&docu.equalsIgnoreCase("true"))
        {
            ret=getResources().getString(R.string.audio)+","+getResources().getString(R.string.document);

        }

        else if (photo.equalsIgnoreCase("true")&&video.equalsIgnoreCase("true")&&audio.equalsIgnoreCase("true")&&docu.equalsIgnoreCase("false"))
        {
            ret=getResources().getString(R.string.photo)+","+getResources().getString(R.string.video)+","+getResources().getString(R.string.audio);

        }
        else if (photo.equalsIgnoreCase("true")&&video.equalsIgnoreCase("true")&&audio.equalsIgnoreCase("false")&&docu.equalsIgnoreCase("true"))
        {
            ret=getResources().getString(R.string.photo)+","+getResources().getString(R.string.video)+","+getResources().getString(R.string.document);

        }

        else if (photo.equalsIgnoreCase("false")&&video.equalsIgnoreCase("true")&&audio.equalsIgnoreCase("true")&&docu.equalsIgnoreCase("true"))
        {
            ret=getResources().getString(R.string.audio)+","+getResources().getString(R.string.document)+","+getResources().getString(R.string.video);


        }
        else if (photo.equalsIgnoreCase("true")&&video.equalsIgnoreCase("false")&&audio.equalsIgnoreCase("true")&&docu.equalsIgnoreCase("true"))
        {
            ret=getResources().getString(R.string.photo)+","+getResources().getString(R.string.document)+","+getResources().getString(R.string.audio);

        }
        else
        {
            ret=getResources().getString(R.string.no_media);
        }


        return ret;



    }


    private void opendialog(final int i) {

        AlertDialog.Builder alt_bld = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        //alt_bld.setIcon(R.drawable.icon);
        if (i==1)
        {
            alt_bld.setTitle(getResources().getString(R.string.using_mobile));
        }
        else if (i==2)
        {
            alt_bld.setTitle(getResources().getString(R.string.connected_to));

        }else {
            alt_bld.setTitle(getResources().getString(R.string.when_roaming));

        }

        String item[] = {getResources().getString(R.string.photo), getResources().getString(R.string.audio), getResources().getString(R.string.video), getResources().getString(R.string.document)};
        alt_bld.setMultiChoiceItems(item, data, new DialogInterface
                .OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialog, int item, boolean isChecked) {
                selected_item = item;
                selected_check = isChecked;

            }
        });
        alt_bld.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        alt_bld.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                update(i);
                dialog.dismiss();
            }
        });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }

    private void update(int i) {

        if (i == 1) {
            try {
                Const.URI.mobile_data[selected_item] = selected_check;
            }
            catch (NullPointerException e)
            {
                e.printStackTrace();
            }
            SharedHelper.putKey(AutoDownloadSettings.this, "mobile_data", Const.URI.mobile_data);
            test1 = SharedHelper.getKey("mobile_data", AutoDownloadSettings.this);
            String mob_val = validatevalues(test1);
            mobile_data_val.setText(mob_val);
        } else if (i == 2) {
            try {
                Const.URI.wifi[selected_item] = selected_check;
            }
            catch (NullPointerException e)
            {
                e.printStackTrace();
            }
            SharedHelper.putKey(AutoDownloadSettings.this, "wifi_data", Const.URI.wifi);
            test1 = SharedHelper.getKey("wifi_data", AutoDownloadSettings.this);
            String wifi_val = validatevalues(test1);
            wifi_data_val.setText(wifi_val);
        } else {
            try {
                Const.URI.roaming_data[selected_item] = selected_check;
            }
            catch (NullPointerException e)
            {
                e.printStackTrace();
            }
            SharedHelper.putKey(AutoDownloadSettings.this, "roaming_data", Const.URI.roaming_data);
            test1 = SharedHelper.getKey("roaming_data", AutoDownloadSettings.this);
            String roaming_val = validatevalues(test1);
            roaming_data_val.setText(roaming_val);

        }

    }

}

