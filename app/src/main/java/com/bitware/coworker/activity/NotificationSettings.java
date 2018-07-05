package com.bitware.coworker.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bitware.coworker.R;
import com.bitware.coworker.baseUtils.Const;
import com.bitware.coworker.baseUtils.SharedHelper;
import com.bitware.coworker.baseUtils.Utils;

import org.apache.commons.io.FilenameUtils;

import java.util.Arrays;

public class NotificationSettings extends AppCompatActivity {

    public static TextView single_noti_text, group_noti_text, call_noti_text, single_vibrate_text, group_vibrate_text, call_vibrate_text, single_popup_text, group_popup_text, single_light_text, group_light_text;
    public static Toolbar toolbar;
    public static RelativeLayout back_layout;
    public ImageView back;
    TextView   message_noti,grp_noti,call_noti_tex;

    LinearLayout single_noti, group_noti, call_noti, single_vibrate, group_vibrate, call_vibrate, single_popup, group_popup, single_light, group_light;
    ImageView settings;
    CheckBox inmessage_sound;
    private String[] grpname;

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
        setContentView(R.layout.activity_notification_settings);

        toolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        back_layout = (RelativeLayout) findViewById(R.id.back_layout);

        back = (ImageView) back_layout.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });

        message_noti=(TextView)findViewById(R.id.message_noti);
        grp_noti=(TextView)findViewById(R.id.grp_noti);
        call_noti_tex=(TextView)findViewById(R.id.call_noti);

        message_noti.setTextColor(getPrimaryCOlor(NotificationSettings.this));
        grp_noti.setTextColor(getPrimaryCOlor(NotificationSettings.this));
        call_noti_tex.setTextColor(getPrimaryCOlor(NotificationSettings.this));

        single_noti = (LinearLayout) findViewById(R.id.single_notification);
        group_noti = (LinearLayout) findViewById(R.id.group_notification);
        call_noti = (LinearLayout) findViewById(R.id.call_notification);
        single_vibrate = (LinearLayout) findViewById(R.id.vibrate_single);
        group_vibrate = (LinearLayout) findViewById(R.id.vibrate_group);
        call_vibrate = (LinearLayout) findViewById(R.id.vibrate_call);
        single_light = (LinearLayout) findViewById(R.id.light_single);
        group_light = (LinearLayout) findViewById(R.id.light_group);
        single_noti_text = (TextView) findViewById(R.id.single_notification_name);
        group_noti_text = (TextView) findViewById(R.id.group_notification_name);
        call_noti_text = (TextView) findViewById(R.id.call_notification_name);
        single_vibrate_text = (TextView) findViewById(R.id.single_vibrate_name);
        group_vibrate_text = (TextView) findViewById(R.id.group_vibrate_name);
        call_vibrate_text = (TextView) findViewById(R.id.call_vibrate_name);
        single_light_text = (TextView) findViewById(R.id.single_light_name);
        group_light_text = (TextView) findViewById(R.id.group_light_name);
        single_popup = (LinearLayout) findViewById(R.id.pop_notfi_single);
        group_popup = (LinearLayout) findViewById(R.id.pop_notfi_group);
        single_popup_text = (TextView) findViewById(R.id.single_popup_name);
        group_popup_text = (TextView) findViewById(R.id.group_popup_name);
        settings = (ImageView) findViewById(R.id.settings);
        inmessage_sound = (CheckBox) findViewById(R.id.inmessage_sound);




        String sign = SharedHelper.getKey(this, "sign_settings");
        if (sign.equalsIgnoreCase("true")) {
            Ringtone r = RingtoneManager.getRingtone(this, Uri.parse(SharedHelper.getKey(this, "single_noti_tone")));

            single_noti_text.setText(r.getTitle(this));
            r = RingtoneManager.getRingtone(this, Uri.parse(SharedHelper.getKey(this, "group_noti_tone")));
            group_noti_text.setText(r.getTitle(this));
            r = RingtoneManager.getRingtone(this, Uri.parse(SharedHelper.getKey(this, "call_noti_tone")));
            call_noti_text.setText(r.getTitle(this));

            int single_value = Integer.parseInt(SharedHelper.getKey(NotificationSettings.this, "single_vib_value"));
            int grp_value = Integer.parseInt(SharedHelper.getKey(NotificationSettings.this, "group_vib_value"));
            int call_value = Integer.parseInt(SharedHelper.getKey(NotificationSettings.this, "call_vib_value"));

            String light = SharedHelper.getKey(NotificationSettings.this, "single_light_value");
            String g_light = SharedHelper.getKey(NotificationSettings.this, "group_light_value");
            String in_sound = SharedHelper.getKey(NotificationSettings.this, "play_sounds");

            if (in_sound.equalsIgnoreCase("yes")) {
                inmessage_sound.setChecked(true);
                Const.URI.inMessageTone = true;
            } else {
                inmessage_sound.setChecked(false);
                Const.URI.inMessageTone = false;
            }


            Const.URI.default_single_vibrate = new long[]{single_value, single_value, single_value, single_value};
            Const.URI.default_group_vibrate = new long[]{grp_value, grp_value, grp_value, grp_value};
            Const.URI.default_call_vibrate = new long[]{call_value, call_value, call_value, call_value};
            Const.URI.single_light = Integer.parseInt(light);
            Const.URI.group_light = Integer.parseInt(g_light);

            String name = getvaluefromvibrate(Const.URI.default_single_vibrate);
            single_vibrate_text.setText(name);
            name = getvaluefromvibrate(Const.URI.default_group_vibrate);
            group_vibrate_text.setText(name);
            name = getvaluefromvibrate(Const.URI.default_call_vibrate);
            call_vibrate_text.setText(name);

            single_light_text.setText(getlightname(Const.URI.single_light));
            group_light_text.setText(getlightname(Const.URI.group_light));


        } else {


            Ringtone r = RingtoneManager.getRingtone(this, Const.URI.default_single_message);
            single_noti_text.setText(r.getTitle(this));
            r = RingtoneManager.getRingtone(this, Const.URI.default_group_message);
            group_noti_text.setText(r.getTitle(this));
            r = RingtoneManager.getRingtone(this, Const.URI.default_call);
            call_noti_text.setText(r.getTitle(this));
            String name = getvaluefromvibrate(Const.URI.default_single_vibrate);
            single_vibrate_text.setText(name);
            name = getvaluefromvibrate(Const.URI.default_group_vibrate);
            group_vibrate_text.setText(name);
            name = getvaluefromvibrate(Const.URI.default_call_vibrate);
            call_vibrate_text.setText(name);
            single_light_text.setText(getlightname(Const.URI.single_light));
            group_light_text.setText(getlightname(Const.URI.group_light));
            inmessage_sound.setChecked(true);


            SharedHelper.putKey(NotificationSettings.this, "single_noti_tone", String.valueOf(Const.URI.default_single_message));
            SharedHelper.putKey(NotificationSettings.this, "group_noti_tone", String.valueOf(Const.URI.default_group_message));
            SharedHelper.putKey(NotificationSettings.this, "call_noti_tone", String.valueOf(Const.URI.default_call));
            SharedHelper.putKey(NotificationSettings.this, "single_vib_value", "300");
            SharedHelper.putKey(NotificationSettings.this, "group_vib_value", "300");
            SharedHelper.putKey(NotificationSettings.this, "call_vib_value", "300");
            SharedHelper.putKey(NotificationSettings.this, "play_sounds", "yes");
            SharedHelper.putKey(NotificationSettings.this, "sign_settings", "true");
            SharedHelper.putKey(NotificationSettings.this, "single_light_value", String.valueOf(Const.URI.single_light));
            SharedHelper.putKey(NotificationSettings.this, "group_light_value", String.valueOf(Const.URI.group_light));
        }


        inmessage_sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    inmessage_sound.setChecked(true);
                    Const.URI.inMessageTone = true;
                    SharedHelper.putKey(NotificationSettings.this, "play_sounds", "yes");
                } else {
                    inmessage_sound.setChecked(false);
                    Const.URI.inMessageTone = false;
                    SharedHelper.putKey(NotificationSettings.this, "play_sounds", "no");
                }
            }
        });


        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(NotificationSettings.this, settings);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.notifi_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
//                        Toast.makeText(NotificationSettings.this,"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();

                        Utils.resetallnoti(NotificationSettings.this);


                        Ringtone r = RingtoneManager.getRingtone(NotificationSettings.this, Const.URI.default_single_message);
                        single_noti_text.setText(r.getTitle(NotificationSettings.this));
                        r = RingtoneManager.getRingtone(NotificationSettings.this, Const.URI.default_group_message);
                        group_noti_text.setText(r.getTitle(NotificationSettings.this));
                        r = RingtoneManager.getRingtone(NotificationSettings.this, Const.URI.default_call);
                        call_noti_text.setText(r.getTitle(NotificationSettings.this));
                        String name = getvaluefromvibrate(Const.URI.default_single_vibrate);
                        single_vibrate_text.setText(name);
                        name = getvaluefromvibrate(Const.URI.default_group_vibrate);
                        group_vibrate_text.setText(name);
                        name = getvaluefromvibrate(Const.URI.default_call_vibrate);
                        call_vibrate_text.setText(name);
                        single_light_text.setText(getlightname(Const.URI.single_light));
                        group_light_text.setText(getlightname(Const.URI.group_light));
                        inmessage_sound.setChecked(true);
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });

        single_noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectringtone(1);
            }
        });

        call_noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectringtone(2);
            }
        });
        group_noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectringtone(3);
            }
        });

        single_vibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opendialog(1, 1);
            }
        });

        group_vibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opendialog(1, 2);
            }
        });
        call_vibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opendialog(1, 3);
            }
        });

        single_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opendialog(2, 4);
            }
        });
        group_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opendialog(2, 5);
            }
        });

        single_light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opendialog(3, 6);
            }
        });
        group_light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opendialog(3, 7);
            }
        });


    }

    private String getlightname(int single_light) {
        String color_name;

        if (single_light == 0xFFFFFF) {
            color_name = "White";
        } else if (single_light == 0xFF0000) {
            color_name = "Red";
        } else if (single_light == 0xFFFF00) {
            color_name = "Yellow";
        } else if (single_light == 0x008000) {
            color_name = "Green";
        } else if (single_light == 0x00FFFF) {
            color_name = "Cyan";
        } else if (single_light == 0x0000FF) {
            color_name = "Blue";
        } else if (single_light == 0x800080) {
            color_name = "Purple";
        } else {
            color_name = "None";
        }
        return color_name;

    }
    private int getlightindex(int single_light) {
        int color_name;

        if (single_light == 0xFFFFFF) {
            color_name = 0;
        } else if (single_light == 0xFF0000) {
            color_name = 1;
        } else if (single_light == 0xFFFF00) {
            color_name = 2;
        } else if (single_light == 0x008000) {
            color_name = 3;
        } else if (single_light == 0x00FFFF) {
            color_name = 4;
        } else if (single_light == 0x0000FF) {
            color_name = 5;
        } else if (single_light == 0x800080) {
            color_name = 6;
        } else {
            color_name = 7;
        }
        return color_name+1;

    }

    private String getvaluefromvibrate(long[] default_single_vibrate) {
        String name;
        long no_vib[] = {0, 0, 0, 0};
        long defrault_vib[] = {300, 300, 300, 300};
        long short_vib[] = {500, 500, 500, 500};

        if (Arrays.equals(no_vib, default_single_vibrate)) {
            name = getResources().getString(R.string.off);
            return name;
        } else if (Arrays.equals(defrault_vib, default_single_vibrate)) {
            name = getResources().getString(R.string.defaulte);
            return name;
        } else if (Arrays.equals(short_vib, default_single_vibrate)) {
            name = getResources().getString(R.string.shortt);
            return name;
        } else {
            name = getResources().getString(R.string.longg);
            return name;
        }

    }


    private int getindexfromvibrate(long[] default_single_vibrate) {
        int name;
        long no_vib[] = {0, 0, 0, 0};
        long defrault_vib[] = {300, 300, 300, 300};
        long short_vib[] = {500, 500, 500, 500};

        if (Arrays.equals(no_vib, default_single_vibrate)) {
            name = 0;
            return name;
        } else if (Arrays.equals(defrault_vib, default_single_vibrate)) {
            name = 1;
            return name;
        } else if (Arrays.equals(short_vib, default_single_vibrate)) {
            name = 2;
            return name;
        } else {
            name =3;
            return name;
        }

    }



    private void opendialog(final int i, final int i1) {
        int selected=1;
        if (i == 1) {
            grpname = new String[4];
            grpname[0] = getResources().getString(R.string.off);
            grpname[1] = getResources().getString(R.string.defaulte);
            grpname[2] = getResources().getString(R.string.shortt);
            grpname[3] = getResources().getString(R.string.longg);


            if (i1==1)
            {

                selected=getindexfromvibrate(Const.URI.default_single_vibrate);
            }
            else if (i1==2)
            {
                selected=getindexfromvibrate(Const.URI.default_group_vibrate);

            }
            else
            {
                selected=getindexfromvibrate(Const.URI.default_call_vibrate);

            }

        } else if (i == 2) {
            grpname = new String[4];
            grpname[0] = getResources().getString(R.string.no_pop);
            grpname[1] = getResources().getString(R.string.only_when);
            grpname[2] = getResources().getString(R.string.only_when_off);
            grpname[3] = getResources().getString(R.string.alwayss);
        } else if (i == 3) {
            grpname = new String[8];
            grpname[0] = "None";
            grpname[1] = "White";
            grpname[2] = "Red";
            grpname[3] = "Yellow";
            grpname[4] = "Green";
            grpname[5] = "Cyan";
            grpname[6] = "BLue";
            grpname[7] = "Purple";

            if (i1==6)
            {
                selected=getlightindex(Const.URI.single_light);
            }
            else
            {
                selected=getlightindex(Const.URI.group_light);

            }
        }

        AlertDialog.Builder alt_bld = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        //alt_bld.setIcon(R.drawable.icon);
        alt_bld.setTitle(getResources().getString(R.string.vibrate));


        alt_bld.setSingleChoiceItems(grpname, selected, new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                if (i1 == 1) {

                    single_vibrate_text.setText(grpname[item]);
                    if (item == 0) {
                        Const.URI.default_single_vibrate = new long[]{0, 0, 0, 0};
                        SharedHelper.putKey(NotificationSettings.this, "single_vib_value", "0");
                    } else if (item == 1) {
                        Const.URI.default_single_vibrate = new long[]{300, 300, 300, 300};
                        SharedHelper.putKey(NotificationSettings.this, "single_vib_value", "300");


                    } else if (item == 2) {
                        Const.URI.default_single_vibrate = new long[]{500, 500, 500, 500};
                        SharedHelper.putKey(NotificationSettings.this, "single_vib_value", "500");


                    } else if (item == 3) {
                        Const.URI.default_single_vibrate = new long[]{700, 700, 700, 700};
                        SharedHelper.putKey(NotificationSettings.this, "single_vib_value", "700");


                    }
                }
                if (i1 == 2) {
                    group_vibrate_text.setText(grpname[item]);
                    if (item == 0) {
                        Const.URI.default_group_vibrate = new long[]{0, 0};
                        SharedHelper.putKey(NotificationSettings.this, "group_vib_value", "0");

                    } else if (item == 1) {
                        Const.URI.default_group_vibrate = new long[]{300, 300, 300, 300};
                        SharedHelper.putKey(NotificationSettings.this, "group_vib_value", "300");


                    } else if (item == 2) {
                        Const.URI.default_group_vibrate = new long[]{500, 500, 500, 500};
                        SharedHelper.putKey(NotificationSettings.this, "group_vib_value", "500");


                    } else if (item == 3) {
                        Const.URI.default_group_vibrate = new long[]{700, 700, 700, 700};
                        SharedHelper.putKey(NotificationSettings.this, "group_vib_value", "700");


                    }

                }
                if (i1 == 3) {
                    call_vibrate_text.setText(grpname[item]);
                    if (item == 0) {
                        Const.URI.default_call_vibrate = new long[]{0, 0};
                        SharedHelper.putKey(NotificationSettings.this, "call_vib_value", "0");

                    } else if (item == 1) {
                        Const.URI.default_call_vibrate = new long[]{100, 100};
                        SharedHelper.putKey(NotificationSettings.this, "call_vib_value", "300");


                    } else if (item == 2) {
                        Const.URI.default_call_vibrate = new long[]{200, 200};
                        SharedHelper.putKey(NotificationSettings.this, "call_vib_value", "500");


                    } else if (item == 3) {
                        Const.URI.default_call_vibrate = new long[]{300, 300};
                        SharedHelper.putKey(NotificationSettings.this, "call_vib_value", "700");


                    }

                }
                if (i1 == 4) {
                    single_popup_text.setText(grpname[item]);
                }
                if (i1 == 5) {
                    group_popup_text.setText(grpname[item]);

                }
                if (i1 == 6) {
                    single_light_text.setText(grpname[item]);

                    if (item == 0) {
                        Const.URI.single_light = 0;
                    } else if (item == 1) {
                        Const.URI.single_light = 0xFFFFFF;
                    } else if (item == 2) {
                        Const.URI.single_light = 0xFF0000;
                    } else if (item == 3) {
                        Const.URI.single_light = 0xFFFF00;
                    } else if (item == 4) {
                        Const.URI.single_light = 0x008000;
                    } else if (item == 5) {
                        Const.URI.single_light = 0x00FFFF;
                    } else if (item == 6) {
                        Const.URI.single_light = 0x0000FF;
                    } else if (item == 7) {
                        Const.URI.single_light = 0x800080;
                    }
                    SharedHelper.putKey(NotificationSettings.this, "single_light_value", String.valueOf(Const.URI.single_light));

                }
                if (i1 == 7) {
                    group_light_text.setText(grpname[item]);


                    if (item == 0) {
                        Const.URI.group_light = 0;
                    } else if (item == 1) {
                        Const.URI.group_light = 0xFFFFFF;
                    } else if (item == 2) {
                        Const.URI.group_light = 0xFF0000;
                    } else if (item == 3) {
                        Const.URI.group_light = 0xFFFF00;
                    } else if (item == 4) {
                        Const.URI.group_light = 0x008000;
                    } else if (item == 5) {
                        Const.URI.group_light = 0x00FFFF;
                    } else if (item == 6) {
                        Const.URI.group_light = 0x0000FF;
                    } else if (item == 7) {
                        Const.URI.group_light = 0x800080;
                    }

                    SharedHelper.putKey(NotificationSettings.this, "group_light_value", String.valueOf(Const.URI.single_light));

                }

                dialog.dismiss();// dismiss the alertbox after chose option

            }
        });
        alt_bld.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }

    private void selectringtone(int i) {
        Uri currentTone;
        if (i==1)
        {
            currentTone=Const.URI.default_single_message;
        } else  if (i==2)
        {
            currentTone=Const.URI.default_call;
        }
        else
        {
            currentTone=Const.URI.default_group_message;


        }
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getResources().getString(R.string.select_tome));
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentTone);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        startActivityForResult(intent, i);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String f_name;
            switch (requestCode) {
                case 1:
                    Uri ringtone;

                    ringtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
//                    String defaultPath = Settings.System.DEFAULT_RINGTONE_URI.getPath();
                    if(ringtone.equals(Settings.System.DEFAULT_RINGTONE_URI))
                    {
                        Log.d("onActivityResult: ","Default");
                        single_noti_text.setText(getResources().getString(R.string.defaulte));
                        Const.URI.default_single_message = ringtone;
                        SharedHelper.putKey(NotificationSettings.this, "single_noti_tone", String.valueOf(ringtone));

                    }
                    else {
//                    Log.d("onActivityResultring: ", "values:" + ringtone + "name:" + Utils.getFileNamefromURI(ringtone, NotificationSettings.this));
                        f_name = FilenameUtils.removeExtension(Utils.getFileNamefromURI(ringtone, NotificationSettings.this));
                        single_noti_text.setText(f_name);
                        Const.URI.default_single_message = ringtone;
                        SharedHelper.putKey(NotificationSettings.this, "single_noti_tone", String.valueOf(ringtone));
                    }
                    break;

                case 2:
                    ringtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);


                    if(ringtone.equals(Settings.System.DEFAULT_RINGTONE_URI))
                    {
                        Log.d("onActivityResult: ","Default");
                        call_noti_text.setText(getResources().getString(R.string.defaulte));
                        Const.URI.default_call = ringtone;
                        SharedHelper.putKey(NotificationSettings.this, "call_noti_tone", String.valueOf(ringtone));

                    }
                    else {
                        Log.d("onActivityResultring: ", "values:" + ringtone + "name:" + Utils.getFileNamefromURI(ringtone, NotificationSettings.this));
                        f_name = FilenameUtils.removeExtension(Utils.getFileNamefromURI(ringtone, NotificationSettings.this));
                        call_noti_text.setText(f_name);
                        Const.URI.default_call = ringtone;
                        SharedHelper.putKey(NotificationSettings.this, "call_noti_tone", String.valueOf(ringtone));
                    }

                    break;

                case 3:
                    ringtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    if(ringtone.equals(Settings.System.DEFAULT_RINGTONE_URI))
                    {
                        Log.d("onActivityResult: ","Default");
                        group_noti_text.setText(getResources().getString(R.string.defaulte));
                        Const.URI.default_group_message = ringtone;
                        SharedHelper.putKey(NotificationSettings.this, "group_noti_tone", String.valueOf(ringtone));

                    }
                    else {
                        Log.d("onActivityResultring: ", "values:" + ringtone + "name:" + Utils.getFileNamefromURI(ringtone, NotificationSettings.this));
                        f_name = FilenameUtils.removeExtension(Utils.getFileNamefromURI(ringtone, NotificationSettings.this));
                        group_noti_text.setText(f_name);
                        Const.URI.default_group_message = ringtone;
                        SharedHelper.putKey(NotificationSettings.this, "group_noti_tone", String.valueOf(ringtone));
                    }


                    break;

                default:
                    break;
            }
        }
    }


}
