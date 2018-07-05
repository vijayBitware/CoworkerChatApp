package com.bitware.coworker.activity;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bitware.coworker.R;
import com.bitware.coworker.baseUtils.Const;
import com.bitware.coworker.baseUtils.SharedHelper;


public class ChatSettings extends AppCompatActivity {
    public static RelativeLayout back_layout;
    public ImageView back;
    CheckBox sendChat;
    TextView desc_text,heading;

    public static int getPrimaryCOlor(Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, value, true);
        return value.data;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String themevalue = SharedHelper.getKey(this, "theme_value");
        Setheme(themevalue);
        setContentView(R.layout.activity_chat_settings);
        sendChat = (CheckBox) findViewById(R.id.enter_key);
        desc_text = (TextView) findViewById(R.id.desc_text);
        heading = (TextView) findViewById(R.id.heading);
        heading.setTextColor(getPrimaryCOlor(ChatSettings.this));

        back_layout = (RelativeLayout) findViewById(R.id.back_layout);

        back = (ImageView) back_layout.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sendChat.setButtonTintList(ColorStateList.valueOf(getPrimaryCOlor(ChatSettings.this)));
        }
        String enter_key = SharedHelper.getKey(ChatSettings.this, "enter_key");
        if (enter_key.equalsIgnoreCase("yes")) {
            sendChat.setChecked(true);
            Const.URI.enterKey = true;
            desc_text.setText(getResources().getString(R.string.add_new_message));


        } else {
            sendChat.setChecked(false);
            Const.URI.enterKey = false;
            desc_text.setText(getResources().getString(R.string.add_new_line));


        }


        sendChat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sendChat.setChecked(true);
                    Const.URI.enterKey = true;
                    desc_text.setText(getResources().getString(R.string.add_new_message));

                    SharedHelper.putKey(ChatSettings.this, "enter_key", "yes");
                } else {
                    sendChat.setChecked(false);
                    Const.URI.enterKey = false;
                    desc_text.setText(getResources().getString(R.string.add_new_line));

                    SharedHelper.putKey(ChatSettings.this, "enter_key", "no");
                }
            }
        });

    }
}
