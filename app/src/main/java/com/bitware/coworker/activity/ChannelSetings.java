package com.bitware.coworker.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bitware.coworker.DBHelper.DBHandler;
import com.bitware.coworker.R;
import com.bitware.coworker.Service.ServiceClasss;
import com.bitware.coworker.baseUtils.Const;
import com.bitware.coworker.baseUtils.MyCommon;
import com.bitware.coworker.baseUtils.SharedHelper;
import com.bitware.coworker.fragment.ChatFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.UUID;

import io.socket.client.IO;
import io.socket.client.Socket;

public class ChannelSetings extends AppCompatActivity {
    public static Socket socket;
    String c_type = "public";
    LinearLayout public_c, private_c;
    ImageView public_c_i, private_c_i;
    private DBHandler dbHandler;
//    private Emitter.Listener onConnect = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            /*ChatActivity.this.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {*/
//            try {
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("id", SharedHelper.getKey(getApplicationContext(), "id"));
//                Log.e("connect", "online" + jsonObject);
//                socket.emit("online", jsonObject);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//             /*   }
//            });*/
//
//        }
//    };

    public static int getPrimaryCOlor(Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, value, true);
        return value.data;
    }

    public static int getPrimaryDark(Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimaryDark, value, true);
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

    public void setIconColour(Drawable drawable, int primaryCOlor) {

        drawable.setColorFilter(new PorterDuffColorFilter(getPrimaryCOlor(ChannelSetings.this), PorterDuff.Mode.SRC_IN));

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String themevalue = SharedHelper.getKey(this, "theme_value");
        Setheme(themevalue);
        setContentView(R.layout.activity_channel_setings);
        dbHandler = new DBHandler(ChannelSetings.this);
        public_c = (LinearLayout) findViewById(R.id.public_channel);
        private_c = (LinearLayout) findViewById(R.id.private_channel);
        public_c_i = (ImageView) findViewById(R.id.public_i);
        private_c_i = (ImageView) findViewById(R.id.private_i);
        DrawableCompat.setTint(public_c_i.getDrawable(), getPrimaryCOlor(ChannelSetings.this));
        DrawableCompat.setTint(private_c_i.getDrawable(), getPrimaryCOlor(ChannelSetings.this));
        IO.Options opts = new IO.Options();
        opts.forceNew = true;
        opts.reconnection = false;
        try {
            socket = IO.socket(Const.chatSocketURL, opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.e("SOCKET.IO ", e.getMessage());
        }
//        socket.connect();


        Toolbar toolbar = (Toolbar) findViewById(R.id.group_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getPrimaryCOlor(ChannelSetings.this));
        toolbar.setTitle(getResources().getString(R.string.settiings));
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        public_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c_type = "public";
                public_c_i.setImageResource(R.drawable.checked);
                private_c_i.setImageResource(R.drawable.unchecked);
                DrawableCompat.setTint(public_c_i.getDrawable(), getPrimaryCOlor(ChannelSetings.this));
                DrawableCompat.setTint(private_c_i.getDrawable(), getPrimaryCOlor(ChannelSetings.this));


            }
        });

        private_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c_type = "private";

                public_c_i.setImageResource(R.drawable.unchecked);
                private_c_i.setImageResource(R.drawable.checked);

                DrawableCompat.setTint(public_c_i.getDrawable(), getPrimaryCOlor(ChannelSetings.this));
                DrawableCompat.setTint(private_c_i.getDrawable(), getPrimaryCOlor(ChannelSetings.this));

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.tick_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        MyCommon.getInstance().setGroupCreateName = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("onPause: ", "socket_stopped");
        MyCommon.getInstance().setGroupCreateName = false;
//        socket.disconnect();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                createchannel();

                break;
            default:
                return false;
        }
        return true;
    }

    private void createchannel() {
        try {

            //Utils.showSimpleProgressDialog(SetGroupName_activity.this, "Please wait...", true);
            JSONObject jsonObject = new JSONObject();
            JSONObject my_id = new JSONObject();
            JSONArray post_select_array = new JSONArray(SetGroupName_activity.select_list);
            jsonObject.put("from", SharedHelper.getKey(ChannelSetings.this, "id"));
            jsonObject.put("channelName", SetGroupName_activity.cName);
            if (!SetGroupName_activity.cImage.isEmpty()) {
                jsonObject.put("channelImage", Const.amazons3ServerImagePath + SetGroupName_activity.cImage);
            } else {
                jsonObject.put("channelImage", " ");
            }
            String c_id = UUID.randomUUID().toString();
            jsonObject.put("channelId", c_id);
            jsonObject.put("channelType", c_type);
            jsonObject.put("channelDescription", SetGroupName_activity.cDescription);
            my_id.put("participantId", SharedHelper.getKey(ChannelSetings.this, "id"));
            my_id.put("participantType", "owner");
            post_select_array.put(my_id);
            jsonObject.put("participants", post_select_array.toString());

            Log.d("createChannel", "" + jsonObject);

            ServiceClasss.Emitters emitters = new ServiceClasss.Emitters(ChannelSetings.this);
            Boolean res = emitters.createChannel(jsonObject);



            if (res) {

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void createchanneltrigger(Context context)
    {
        ChatFragment.mainActivity.refresh();
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

    }
}
