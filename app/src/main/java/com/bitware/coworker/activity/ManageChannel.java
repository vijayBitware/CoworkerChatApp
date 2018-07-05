package com.bitware.coworker.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.bitware.coworker.DBHelper.DBHandler;
import com.bitware.coworker.R;
import com.bitware.coworker.adapter.ChannelParticipantAdapter;
import com.bitware.coworker.baseUtils.SharedHelper;
import com.bitware.coworker.models.ChannelParticiapntsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ManageChannel extends AppCompatActivity {

    private static DBHandler dbHandler;
    LinearLayout info, actions, admins;
    public  static String group_id;
    public static ChannelParticipantAdapter CparticipantAdapter;
    private RecyclerView parti_recycler;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String themevalue = SharedHelper.getKey(this, "theme_value");
        Setheme(themevalue);
        setContentView(R.layout.activity_manage_channel);
        Intent intent = getIntent();
        group_id = intent.getStringExtra("group_id");
        info = (LinearLayout) findViewById(R.id.channel_info);
        actions = (LinearLayout) findViewById(R.id.recent_action);
        admins = (LinearLayout) findViewById(R.id.admin);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageChannel.this, ChannelEdit.class);
                intent.putExtra("group_id", group_id);
                startActivity(intent);
            }
        });

        actions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(ManageChannel.this,ChannelEdit.class);
//                startActivity(intent);
            }
        });

        admins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageChannel.this, ChannelAdmin.class);
                intent.putExtra("group_id", group_id);
                startActivity(intent);
            }
        });

dbHandler=new DBHandler(ManageChannel.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.group_toolbar);
        toolbar.setBackgroundColor(getPrimaryCOlor(ManageChannel.this));
        toolbar.setTitle("Manage Channel");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        CparticipantAdapter = new ChannelParticipantAdapter(ManageChannel.this, UserDetails.particpants_array);
        parti_recycler = (RecyclerView) findViewById(R.id.participant_list);
        parti_recycler.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ManageChannel.this);
        parti_recycler.setLayoutManager(linearLayoutManager);
        parti_recycler.setAdapter(CparticipantAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.tick_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:


                break;
            default:
                return false;
        }
        return true;
    }

    public static void refresh() {
        List<ChannelParticiapntsModel> models = new ArrayList<>();
        models = dbHandler.GetPartiFromChannel(group_id);

        try {
            UserDetails.particpants_array = getarrayfromliste(models);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        CparticipantAdapter.notifyDataSetChanged();
        UserDetails.refresh();

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
