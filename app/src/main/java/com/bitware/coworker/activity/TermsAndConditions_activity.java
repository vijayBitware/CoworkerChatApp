package com.bitware.coworker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.bitware.coworker.R;
import com.bitware.coworker.baseUtils.SharedHelper;

public class TermsAndConditions_activity extends AppCompatActivity {


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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_agree_activity);

        TextView terms_conditions = (TextView) findViewById(R.id.terms_label);
        String string_one = getResources().getString(R.string.terms_text);
        String string_two = "<font color='#1abc9c'> "+getResources().getString(R.string.terms_textt)+"</font>";
        //noinspection deprecation
        terms_conditions.setText(Html.fromHtml(string_one + string_two));

        findViewById(R.id.btn_terms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent get_phone = new Intent(TermsAndConditions_activity.this, GettingPhoneNumber_activity.class);
                get_phone.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(get_phone);
                finish();
            }
        });

       /* String token = FirebaseInstanceId.getInstance().getToken();
        Utils.showShortToast(token, getApplicationContext());*/

    }
}
