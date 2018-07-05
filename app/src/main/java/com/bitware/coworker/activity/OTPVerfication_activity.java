package com.bitware.coworker.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bitware.coworker.R;
import com.bitware.coworker.baseUtils.SharedHelper;
import com.bitware.coworker.baseUtils.Utils;

public class OTPVerfication_activity extends AppCompatActivity {

    private String number = "", code = "";

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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String themevalue= SharedHelper.getKey(this,"theme_value");
        Setheme(themevalue);
        setContentView(R.layout.activity_otpverfication_activity);

        Utils.enableStrictMode();

        TextView verify_number = (TextView) findViewById(R.id.verify_number);
        TextView verify_number_label = (TextView) findViewById(R.id.verify_number_lbl);
        final EditText otp_number = (EditText) findViewById(R.id.edt_otp_number);

        final Intent intent = getIntent();
        if (intent != null) {
            number = intent.getStringExtra("number");
            code = intent.getStringExtra("code");
            verify_number.setText(getResources().getString(R.string.verify) + code + " " + number);
            String wrong = "<font color='#1abc9c'>"+getResources().getString(R.string.wrong)+" </font>";
            //noinspection deprecation
            verify_number_label.setText(Html.fromHtml(getText(R.string.verify_number) + "\n" + intent.getStringExtra("number") + " " + wrong));
            otp_number.setText(intent.getStringExtra("OTP"));

        }

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OTPVerfication_activity.this, GettingPhoneNumber_activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        verify_number_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        findViewById(R.id.otp_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (otp_number.getText().toString().matches("") || otp_number.getText().toString().length() < 6||!otp_number.getText().toString().equalsIgnoreCase(intent.getStringExtra("OTP"))) {
                    Utils.showShortToast(getResources().getString(R.string.valid_otp), getApplicationContext());
                } else {
                    Intent intent = new Intent(OTPVerfication_activity.this, InitProfile_activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("number", number);
                    intent.putExtra("code", code);
                    startActivity(intent);
                    finish();

                }

            }
        });
    }
}
