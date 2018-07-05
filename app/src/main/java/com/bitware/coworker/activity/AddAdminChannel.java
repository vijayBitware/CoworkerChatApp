package com.bitware.coworker.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.bitware.coworker.DBHelper.DBHandler;
import com.bitware.coworker.R;
import com.bitware.coworker.Service.ServiceClasss;
import com.bitware.coworker.adapter.ChannelAdminAdapter;
import com.bitware.coworker.baseUtils.Const;
import com.bitware.coworker.baseUtils.SharedHelper;
import com.bitware.coworker.models.ChannelParticiapntsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;

public class AddAdminChannel extends AppCompatActivity {
    private static Handler handler=new Handler();
    String group_id;
    private List<JSONObject> grpparticipants_List = new ArrayList<>();
    public static ChannelAdminAdapter CparticipantAdapter;
    public static RecyclerView parti_recycler;
    public static JSONArray particpants_array;
    public static Socket socket;
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
//
//
//    };

    private DBHandler dbHandler;


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

    @Override
    protected void onResume() {
        super.onResume();

//        socket.on(Socket.EVENT_CONNECT, onConnect);
//        socket.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();

//        socket.disconnect();
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
        setContentView(R.layout.activity_add_admin_channel);
        IO.Options opts = new IO.Options();
        opts.forceNew = true;
        opts.reconnection = false;
        Toolbar toolbar = (Toolbar) findViewById(R.id.group_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getPrimaryCOlor(AddAdminChannel.this));
        toolbar.setTitle(getResources().getString(R.string.administr));
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        try {
            socket = IO.socket(Const.chatSocketURL, opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.e("SOCKET.IO ", e.getMessage());
        }

        Intent intent = getIntent();
        group_id = intent.getStringExtra("group_id");
        dbHandler=new DBHandler(AddAdminChannel.this);
        try {
            particpants_array=getarrayfromliste(dbHandler.GetUserFromChannel(group_id));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CparticipantAdapter = new ChannelAdminAdapter(AddAdminChannel.this, particpants_array);
        parti_recycler = (RecyclerView) findViewById(R.id.participant_list);
        parti_recycler.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AddAdminChannel.this);
        parti_recycler.setLayoutManager(linearLayoutManager);
        parti_recycler.setAdapter(CparticipantAdapter);
    }


    public static void makeadminChannel(final String part_id, final String grp_id, final Dialog dialog, final Context context, final String created_by, final String joined_at) throws JSONException {


        ServiceClasss.Emitters emitters=new ServiceClasss.Emitters(context);
       Boolean res = emitters.addAdminInChannel(  part_id,  grp_id,0,dialog,context,created_by,joined_at);

        if (res)
        {


        }

    }

    public static void trigger_makeadminChannel_0(final String part_id, final String grp_id, final Dialog dialog, final Context context, final String created_by, final String joined_at)
    {
        dialog.dismiss();
        final DBHandler dbHandler = new DBHandler(context);
        dbHandler.channelPartiUpdate(grp_id, joined_at, "admin", created_by, part_id);
        List<ChannelParticiapntsModel> models = new ArrayList<>();
        models = dbHandler.GetUserFromChannel(grp_id);

        try {
            particpants_array = getarrayfromliste(models);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        handler.post(new Runnable() {
            public void run() {
                CparticipantAdapter = new ChannelAdminAdapter(context, particpants_array);
                parti_recycler.setNestedScrollingEnabled(false);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                parti_recycler.setLayoutManager(linearLayoutManager);
                parti_recycler.setAdapter(CparticipantAdapter);
                CparticipantAdapter.notifyDataSetChanged();
                ChannelAdmin.refresh();

            }
        });
    }
    private static JSONArray getarrayfromliste(List<ChannelParticiapntsModel> models) throws JSONException {

        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < models.size(); i++) {
            ChannelParticiapntsModel group = models.get(i);
            String user_id = group.getUser_id();
            String group_id = group.getChannel_id();
            String joined_at = group.getJoined_at();
            String added_by = group.getAdded_by();
            String isAdmin = group.getParticipantRole();

            JSONObject object = new JSONObject();
            object.put("channelId", group_id);
            object.put("joinedAt", joined_at);
            object.put("addedBy", added_by);
            object.put("participantId", user_id);
            object.put("isAdmin", isAdmin);

            jsonArray.put(i, object);
        }


        return jsonArray;
    }

}
