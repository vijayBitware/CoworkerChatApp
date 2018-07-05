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
import android.view.View;
import android.widget.LinearLayout;

import com.bitware.coworker.DBHelper.DBHandler;
import com.bitware.coworker.R;
import com.bitware.coworker.adapter.ChannelParticipantAdapter;
import com.bitware.coworker.models.ChannelParticiapntsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ChannelAdmin extends AppCompatActivity {
    public static String group_id;
    public static ChannelParticipantAdapter CparticipantAdapter;
    public static RecyclerView parti_recycler;
    public static JSONArray particpants_array;
    public static DBHandler dbHandler;
    LinearLayout add_admin;

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

    public static void refresh() {

        try {
            particpants_array = getarrayfromliste(dbHandler.GetAdminFromChannel(group_id));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ManageChannel.refresh();

        CparticipantAdapter.notifyDataSetChanged();

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

    @Override
    protected void onResume() {
        super.onResume();
        dbHandler = new DBHandler(ChannelAdmin.this);
        try {
            particpants_array = getarrayfromliste(dbHandler.GetAdminFromChannel(group_id));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CparticipantAdapter = new ChannelParticipantAdapter(ChannelAdmin.this, particpants_array);
        parti_recycler = (RecyclerView) findViewById(R.id.admin_lists);
        parti_recycler.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChannelAdmin.this);
        parti_recycler.setLayoutManager(linearLayoutManager);
        parti_recycler.setAdapter(CparticipantAdapter);
        CparticipantAdapter.notifyDataSetChanged();

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
        setContentView(R.layout.activity_channel_admin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.group_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getPrimaryCOlor(ChannelAdmin.this));
        toolbar.setTitle(getResources().getString(R.string.admini));

        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Intent intent = getIntent();
        group_id = intent.getStringExtra("group_id");
        dbHandler = new DBHandler(ChannelAdmin.this);
        try {
            particpants_array = getarrayfromliste(dbHandler.GetAdminFromChannel(group_id));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CparticipantAdapter = new ChannelParticipantAdapter(ChannelAdmin.this, particpants_array);
        parti_recycler = (RecyclerView) findViewById(R.id.admin_lists);
        add_admin = (LinearLayout) findViewById(R.id.add_admin);
        add_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent admin = new Intent(ChannelAdmin.this, AddAdminChannel.class);
                admin.putExtra("group_id", group_id);
                startActivity(admin);

            }
        });
        parti_recycler.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChannelAdmin.this);
        parti_recycler.setLayoutManager(linearLayoutManager);
        parti_recycler.setAdapter(CparticipantAdapter);
    }
}
