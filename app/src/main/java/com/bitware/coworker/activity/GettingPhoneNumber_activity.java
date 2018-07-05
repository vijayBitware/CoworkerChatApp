package com.bitware.coworker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bitware.coworker.R;
import com.bitware.coworker.baseUtils.AsyncTaskCompleteListener;
import com.bitware.coworker.baseUtils.Const;
import com.bitware.coworker.baseUtils.PostHelper;
import com.bitware.coworker.baseUtils.ReadFiles;
import com.bitware.coworker.baseUtils.SharedHelper;
import com.bitware.coworker.baseUtils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


public class GettingPhoneNumber_activity extends AppCompatActivity implements AsyncTaskCompleteListener {

    String conCode;
    String phone;
    private Spinner countries;
    private TextView country_code_text;
    private ArrayList<String> phone_code;
    private EditText ph_number;
    //private ProgressBar progressBar;

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
        setContentView(R.layout.activity_getting_phone_number_activity);
        Utils.enableStrictMode();
        countries = (Spinner) findViewById(R.id.country_spinner);
        country_code_text = (TextView) findViewById(R.id.country_text_value);
        ph_number = (EditText) findViewById(R.id.edt_mobile_number);
        //progressBar = (ProgressBar) findViewById(R.id.progress);
        //progressBar.setVisibility(View.GONE);

        ArrayAdapter<String> countryCodeAdapter = new ArrayAdapter<String>(this, R.layout.spinner_text_view, parseCountryCodes());
        countries.setAdapter(countryCodeAdapter);
        countries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressWarnings("LoopStatementThatDoesntLoop")
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {

                //String str = adapterView.getSelectedItem().toString();
                for (int i = 0; i < phone_code.size(); i++) {
                    if (i == pos) {
                        country_code_text.setText(phone_code.get(i));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ph_number.getText().toString().matches("") || ph_number.getText().toString().length() < 5) {
                    Utils.showShortToast("Enter the valid mobile number", getApplicationContext());
                } else {
                    //progressBar.setVisibility(View.VISIBLE);
                    String con_code = country_code_text.getText().toString().trim();
                    phone = ph_number.getText().toString().trim();
                    conCode = con_code.replaceAll("[\\-\\+\\.\\^:,\\s+\\(\\)]", "");
                    SharedHelper.putKey(GettingPhoneNumber_activity.this, "country_code_default", conCode);
                    Register(conCode, phone);
                }

            }
        });

    }

    private void Register(String countryCode, String number) {


        if (!Utils.isNetworkAvailable(this)) {
            Utils.showShortToast(getResources().getString(R.string.no_internet), this);
            //progressBar.setVisibility(View.GONE);
            return;
        }
        Utils.showSimpleProgressDialog(GettingPhoneNumber_activity.this, "Please wait...", false);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Const.PHONE, number);
            jsonObject.put(Const.COUNTRY_CODE, countryCode);

            Log.e("getting_ph", jsonObject.toString());
            new PostHelper(Const.Methods.GET_OTP, jsonObject.toString(), Const.ServiceCode.GET_OTP, this, GettingPhoneNumber_activity.this);
        } catch (JSONException e) {
            e.printStackTrace();
            Utils.removeProgressDialog();
        }


    }

    public ArrayList<String> parseCountryCodes() {
        String response = "";
        ArrayList<String> list = new ArrayList<String>();
        phone_code = new ArrayList<String>();

        try {
            response = ReadFiles.readRawFileAsString(this,
                    R.raw.countrycodes);

            JSONArray array = new JSONArray(response);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                //list.add(object.getString("alpha-2") + " (" + object.getString("phone-code") + ")");
                list.add(object.getString("name"));
                phone_code.add(object.getString("phone-code"));

            }
            //Collections.sort(list);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void onTaskCompleted(JSONObject response, int serviceCode) {
        switch (serviceCode) {

            case Const.ServiceCode.GET_OTP:
                Log.e("response", "" + response);
                Utils.removeProgressDialog();
                if (response != null) {
                    if (response.optString("error").equalsIgnoreCase("false")) {
                        Utils.appLog("Result", response.toString());
                        Intent intent = new Intent(GettingPhoneNumber_activity.this, OTPVerfication_activity.class);
                        intent.putExtra("number", response.optString("mobileNumber"));
                        intent.putExtra("code", response.optString("countryCode"));
                        intent.putExtra("OTP", response.optString("otp"));
                        startActivity(intent);


                    } else {
//                        Utils.showLongToast(response.optString("message"), getApplicationContext());
                        Intent intent = new Intent(GettingPhoneNumber_activity.this, OTPVerfication_activity.class);
                        intent.putExtra("number", phone);
                        intent.putExtra("code", conCode);
                        intent.putExtra("OTP", response.optString("otp"));
                        startActivity(intent);
                    }
                } else {
                    Utils.showLongToast(getResources().getString(R.string.server_error), getApplicationContext());
                }

                break;
        }

    }


}
