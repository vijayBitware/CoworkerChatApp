package com.bitware.coworker.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bitware.coworker.R;
import com.bitware.coworker.baseUtils.AsyncTaskCompleteListener;
import com.bitware.coworker.baseUtils.Const;
import com.bitware.coworker.baseUtils.PostHelper;
import com.bitware.coworker.baseUtils.SharedHelper;
import com.bitware.coworker.baseUtils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class PrivacyActivity extends AppCompatActivity {

//    TextView status, profile;
    TextView last_seen,head_text;
//    LinearLayout status_root, profile_root;
    LinearLayout last_root;
    String[] items;
    private boolean internet;
    public static Toolbar toolbar;
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
        setContentView(R.layout.activity_privacy);
        items = new String[]{getResources().getString(R.string.every), getResources().getString(R.string.my_con), getResources().getString(R.string.nobo)};
//        status = (TextView) findViewById(R.id.status);
//        profile = (TextView) findViewById(R.id.profile_photo);
        last_seen = (TextView) findViewById(R.id.last_seen);
        head_text = (TextView) findViewById(R.id.head_text);
        head_text.setTextColor(getPrimaryCOlor(PrivacyActivity.this));

//        status_root = (LinearLayout) findViewById(R.id.status_root);
//        profile_root = (LinearLayout) findViewById(R.id.profile_photo_rrot);
        last_root = (LinearLayout) findViewById(R.id.last_seen_rrot);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.privacy_settings));
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



        String sign = SharedHelper.getKey(this, "sign_settings_privacy");

        if (sign.equalsIgnoreCase("true")) {
            String s_value = validatevalues(SharedHelper.getKey(this, "status_privacy"));
            String p_value = validatevalues(SharedHelper.getKey(this, "profile_privacy"));
            String l_value = validatevalues(SharedHelper.getKey(this, "last_privacy"));
            Const.URI.status = s_value;
            Const.URI.profile_photo = p_value;
            Const.URI.last_seen = l_value;

//            status.setText(s_value);
//            profile.setText(p_value);
            last_seen.setText(l_value);
        } else {
            SharedHelper.putKey(this, "status_privacy", "c");
            SharedHelper.putKey(this, "profile_privacy", "c");
            SharedHelper.putKey(this, "last_privacy", "c");
            SharedHelper.putKey(this, "sign_settings_privacy", "true");
            String s_value = validatevalues(SharedHelper.getKey(this, "status_privacy"));
            String p_value = validatevalues(SharedHelper.getKey(this, "profile_privacy"));
            String l_value = validatevalues(SharedHelper.getKey(this, "last_privacy"));
            Const.URI.status = s_value;
            Const.URI.profile_photo = p_value;
            Const.URI.last_seen = l_value;
//            status.setText(s_value);
//            profile.setText(p_value);
            last_seen.setText(l_value);

        }


//        status_root.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                opendialog(3);
//            }
//        });
//        profile_root.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                opendialog(1);
//
//            }
//        });

        last_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opendialog(2);

            }
        });
    }

    private String validatevalues(String privacy) {
        String value = "";
        if (privacy.equalsIgnoreCase("e")) {
            value = getResources().getString(R.string.every);
        } else if (privacy.equalsIgnoreCase("n")) {
            value = getResources().getString(R.string.nobo);
        } else {
            value = getResources().getString(R.string.my_con);
        }
        return value;
    }

    private String invalidatevalues(String privacy) {
        String value = "";
        if (privacy.equalsIgnoreCase(getResources().getString(R.string.every))) {
            value = "e";
        } else if (privacy.equalsIgnoreCase(getResources().getString(R.string.nobo))) {
            value = "n";
        } else {
            value = "c";
        }
        return value;
    }

    private void opendialog(final int i) {

        String title;
        if (i == 1) {
            title = getResources().getString(R.string.profile_photo);
        } else if (i == 2) {
            title = getResources().getString(R.string.last_seen);
        } else {
            title = getResources().getString(R.string.status);
        }
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        alt_bld.setTitle(title);
        alt_bld.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("onClick: ", "which_values:" + which);
                if (i == 1) {
//                    profile.setText(items[which]);
                    hitapi("statusPrivacy", invalidatevalues(items[which]));
                    SharedHelper.putKey(PrivacyActivity.this,"profile_privacy",invalidatevalues(items[which]));
                }
                if (i == 2) {
                    last_seen.setText(items[which]);
                    hitapi("lastSeenPrivacy", invalidatevalues(items[which]));
                    SharedHelper.putKey(PrivacyActivity.this,"last_privacy",invalidatevalues(items[which]));


                }
                if (i == 3) {
//                    status.setText(items[which]);
                    hitapi("imagePrivacy", invalidatevalues(items[which]));
                    SharedHelper.putKey(PrivacyActivity.this,"status_privacy",invalidatevalues(items[which]));


                }
                dialog.dismiss();
            }
        });
        alt_bld.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }

    private void hitapi(String statusPrivacy, String invalidatevalues) {
        internet = Utils.isNetworkAvailable(PrivacyActivity.this);
        if (internet) {


            new updatePrivacy(statusPrivacy, invalidatevalues).execute();
        }
        else
        {
            Toast.makeText(this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
    }


    private class updatePrivacy extends AsyncTask<String, String, String> implements AsyncTaskCompleteListener {
        String result = "";
        String key;
        String value;

        public updatePrivacy(String statusPrivacy, String invalidatevalues) {
            key = statusPrivacy;
            value = invalidatevalues;
        }

        @Override
        protected String doInBackground(String... params) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", SharedHelper.getKey(getApplicationContext(), "id"));
                jsonObject.put("key", key);
                jsonObject.put("value", value);
//                Log.d("doInBackground: ","posting_values:"+jsonObject);

                if (internet) {
                    new PostHelper(Const.Methods.PRIVACY_SETTINGS, jsonObject.toString(), Const.ServiceCode.PARTICIPANTS_DETAILS, PrivacyActivity.this, this);
                } else {
                    result = "yes";
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }


        @Override
        public void onTaskCompleted(JSONObject response, int serviceCode) {

            Log.d("onTaskCompleted: ", "" + response);


            if (response.optString("error").equalsIgnoreCase("true"))
            {
                Toast.makeText(PrivacyActivity.this, getResources().getString(R.string.privacy_error), Toast.LENGTH_SHORT).show();
            }

        }
    }

}

