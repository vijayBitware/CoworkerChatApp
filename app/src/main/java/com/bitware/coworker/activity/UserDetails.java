package com.bitware.coworker.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.bitware.coworker.DBHelper.DBHandler;
import com.bitware.coworker.R;
import com.bitware.coworker.Service.ServiceClasss;
import com.bitware.coworker.adapter.ChannelParticipantAdapter;
import com.bitware.coworker.adapter.ParticipantAdapter;
import com.bitware.coworker.baseUtils.AsyncTaskCompleteListener;
import com.bitware.coworker.baseUtils.Const;
import com.bitware.coworker.baseUtils.MyCommon;
import com.bitware.coworker.baseUtils.PostHelper;
import com.bitware.coworker.baseUtils.SharedHelper;
import com.bitware.coworker.baseUtils.Utils;
import com.bitware.coworker.fragment.ChatFragment;
import com.bitware.coworker.models.ChannelParticiapntsModel;
import com.bitware.coworker.models.ChatMessages;
import com.bitware.coworker.models.ChatType;
import com.bitware.coworker.models.ChatsMessagesModel;
import com.bitware.coworker.models.ChatsModel;
import com.bitware.coworker.models.GroupParticiapntsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import io.socket.client.IO;
import io.socket.client.Socket;

public class UserDetails extends AppCompatActivity {

    public static RecyclerView recyclerView;
    public static CardView party_layout;
    public static RecyclerView parti_recycler;
    public static Socket socket;
    public static ParticipantAdapter participantAdapter;
    public static ChannelParticipantAdapter CparticipantAdapter;
    public static UserDetails userDetails;
    public static JSONArray particpants_array_val = new JSONArray();
    public static JSONArray particpants_array = new JSONArray();
    public static JSONObject group_details_val = new JSONObject();
    public static Handler handler = new Handler();
    public static Toolbar toolbar;
    public static String group_id, room_type, group_description;
    public static DBHandler dbHandler;
    public static String grp_id_val = "";
    public static String filePath = "";
    public static LinearLayout add_parti;
    ImageView userImage;
    String groupId_loc;
    private ArrayList<String> imageArray;
    private String ZoeChatID = "";
    private String Status = "";
    private ProgressBar progressBar;
    private Uri uri;
    private String image;
    private ImageView proName;

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

    private static JSONArray getarrayfromlist(List<GroupParticiapntsModel> models) throws JSONException {

        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < models.size(); i++) {
            GroupParticiapntsModel group = models.get(i);
            String user_id = group.getUser_id();
            String group_id = group.getGroup_id();
            String joined_at = group.getJoined_at();
            String added_by = group.getAdded_by();
            String isAdmin = group.getIsAdmin();

            JSONObject object = new JSONObject();
            object.put("groupId", group_id);
            object.put("joinedAt", joined_at);
            object.put("addedBy", added_by);
            object.put("participantId", user_id);
            object.put("isAdmin", isAdmin);

            jsonArray.put(i, object);
        }


        return jsonArray;
    }

    private static JSONArray getCarrayfromlist(List<ChannelParticiapntsModel> models) throws JSONException {

        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < models.size(); i++) {
            ChannelParticiapntsModel group = models.get(i);
            String user_id = group.getUser_id();
            String group_id = group.getChannel_id();
            String joined_at = group.getJoined_at();
            String added_by = group.getAdded_by();
            String isAdmin = group.getParticipantRole();

            JSONObject object = new JSONObject();
            object.put("groupId", group_id);
            object.put("joinedAt", joined_at);
            object.put("addedBy", added_by);
            object.put("participantId", user_id);
            object.put("isAdmin", isAdmin);

            jsonArray.put(i, object);
        }


        return jsonArray;
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

    public static void makeadmin(final String part_id, final String grp_id, final Context context, final Dialog dialog, final String created_by, final String joined_at) throws JSONException {

        JSONObject jsonObject = new JSONObject();

        String from = SharedHelper.getKey(context, "my_zoe_id");

        jsonObject.put("from", from);
        jsonObject.put("groupId", grp_id);
        jsonObject.put("participantId", part_id);
        Log.d("addgroup: ", "" + jsonObject);

        ServiceClasss.Emitters emitters = new ServiceClasss.Emitters(context);
        Boolean res = emitters.makeAdmin(jsonObject, created_by, joined_at);

        if (res) {


        }

//        socket.emit("makeAdmin", jsonObject, new Ack() {
//            @Override
//            public void call(Object... args) {
//                String msg = String.valueOf(args[0]);
//                Log.d("leave_grp: ", "message:" + args[0]);
//                if (msg.equalsIgnoreCase("true")) {
//
//                }
//
//            }
//        });


    }

    public static void makeadmintrigger(final Context context, JSONObject jsonObject, String created_by, String joined_at) {

        ParticipantAdapter.dialog.dismiss();
        DBHandler dbHandler = new DBHandler(context);
        dbHandler.groupPartiUpdate(jsonObject.optString("groupId"), joined_at, "1", created_by, jsonObject.optString("participantId"));
        List<GroupParticiapntsModel> models = new ArrayList<>();
        models = dbHandler.GetPartiFromGrp(jsonObject.optString("groupId"));

        try {
            particpants_array = getarrayfromlist(models);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        handler.post(new Runnable() {
            public void run() {
                participantAdapter = new ParticipantAdapter(context, particpants_array);
                parti_recycler.setNestedScrollingEnabled(false);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                parti_recycler.setLayoutManager(linearLayoutManager);
                parti_recycler.setAdapter(participantAdapter);
                participantAdapter.notifyDataSetChanged();
            }
        });
        ;
    }

    public static void makeadminChannel(final String part_id, final String grp_id, final Dialog dialog, final Context context, final String created_by, final String joined_at) throws JSONException {


        ServiceClasss.Emitters emitters = new ServiceClasss.Emitters(context);
        Boolean res = emitters.addAdminInChannel(part_id, grp_id, 1, dialog, context, created_by, joined_at);

    }

    public static void trigger_makeadminChannel_1(final String part_id, final String grp_id, final Dialog dialog, final Context context, final String created_by, final String joined_at) {
        dialog.dismiss();
        final DBHandler dbHandler = new DBHandler(context);
        dbHandler.channelPartiUpdate(grp_id, joined_at, "admin", created_by, part_id);
        List<ChannelParticiapntsModel> models = new ArrayList<>();
        models = dbHandler.GetPartiFromChannel(grp_id);

        try {
            particpants_array = getarrayfromliste(models);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        handler.post(new Runnable() {
            public void run() {
                CparticipantAdapter = new ChannelParticipantAdapter(context, particpants_array);
                parti_recycler.setNestedScrollingEnabled(false);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                parti_recycler.setLayoutManager(linearLayoutManager);
                parti_recycler.setAdapter(CparticipantAdapter);
                CparticipantAdapter.notifyDataSetChanged();
                String role = dbHandler.GetChannelMember(ChatActivity.groupId_loc, MainActivity.my_id);
                if (role.equalsIgnoreCase("user")) {
                    party_layout.setVisibility(View.GONE);
                }
            }
        });
    }

    public static void removefromgroup(final String part_id, final String grp_id, final Context context) throws JSONException {

        JSONObject jsonObject = new JSONObject();


        jsonObject.put("groupId", grp_id);
        jsonObject.put("participantId", part_id);
        jsonObject.put("from", SharedHelper.getKey(context, "id"));
        Log.d("addgroup: ", "" + jsonObject);


        ServiceClasss.Emitters emitters = new ServiceClasss.Emitters(context);
        Boolean res = emitters.removeParticipant(jsonObject);

        if (res) {


        }


//        socket.emit("removeParticipant", jsonObject, new Ack() {
//            @Override
//            public void call(Object... args) {
//                String msg = String.valueOf(args[0]);
//                Log.d("leave_grp: ", "message:" + args[0]);
//                if (msg.equalsIgnoreCase("true")) {
//
//                }
//
//            }
//        });


    }

    public static void removepartitrigger(final Context context, JSONObject jsonObject) {
        ParticipantAdapter.dialog.dismiss();
        DBHandler dbHandler = new DBHandler(context);
        dbHandler.PartiDelete(jsonObject.optString("participantId"), jsonObject.optString("groupId"));
        List<GroupParticiapntsModel> models = new ArrayList<>();
        models = dbHandler.GetPartiFromGrp(jsonObject.optString("groupId"));

        try {
            particpants_array = getarrayfromlist(models);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        handler.post(new Runnable() {
            public void run() {
                participantAdapter = new ParticipantAdapter(context, particpants_array);
                parti_recycler.setNestedScrollingEnabled(false);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                parti_recycler.setLayoutManager(linearLayoutManager);
                parti_recycler.setAdapter(participantAdapter);
                participantAdapter.notifyDataSetChanged();
            }
        });
        ;

    }

    public static void removefromadminchannel(final String part_id, final String grp_id, final Context context, final String joined_at, final String created_by) throws JSONException {

        JSONObject jsonObject = new JSONObject();


        jsonObject.put("groupId", grp_id);
        jsonObject.put("participantId", part_id);
        Log.d("addgroup: ", "" + jsonObject);


        ServiceClasss.Emitters emitters = new ServiceClasss.Emitters(context);
        Boolean res = emitters.removeAdminInChannel(jsonObject, joined_at, created_by);

        if (res) {

            ;
        }

//        socket.emit("removeAdminInChannel", jsonObject, new Ack() {
//            @Override
//            public void call(Object... args) {
//                String msg = String.valueOf(args[0]);
//                Log.d("leave_grp: ", "message:" + args[0]);
//                if (msg.equalsIgnoreCase("true")) {
//
//
//                }
//
//            }
//        });


    }

    public static void removeadmintrigger(final Context context, JSONObject jsonObject, String joined_at, String created_by) {
        ParticipantAdapter.dialog.dismiss();
        DBHandler dbHandler = new DBHandler(context);
        dbHandler.channelPartiUpdate(jsonObject.optString("groupId"), joined_at, "admin", created_by, jsonObject.optString("participantId"));
        List<GroupParticiapntsModel> models = new ArrayList<>();
        models = dbHandler.GetPartiFromGrp(jsonObject.optString("groupId"));

        try {
            particpants_array = getarrayfromlist(models);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        handler.post(new Runnable() {
            public void run() {
                participantAdapter = new ParticipantAdapter(context, particpants_array);
                parti_recycler.setNestedScrollingEnabled(false);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                parti_recycler.setLayoutManager(linearLayoutManager);
                parti_recycler.setAdapter(participantAdapter);
                participantAdapter.notifyDataSetChanged();
            }
        });
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
    }

    public static void editgrpimagetrigger(Context context) {
        final File file = new File(filePath);
        final String from = SharedHelper.getKey(context, "my_zoe_id");

        long millis = System.currentTimeMillis();
        String msg_value = "You changed group Icon";
        dbHandler.GrpImageUpdate(Const.amazons3ServerImagePath + file.getName(), group_id);
        String uniqueID = UUID.randomUUID().toString();
        dbHandler.InsertChatMsg(new ChatsMessagesModel(uniqueID.trim(), SharedHelper.getKey(context, "id"), msg_value, ChatType.editGroupImage.toString(), com.bitware.coworker.models.Status.SENDING.toString(), ChatMessages.CREATE_GROUP, ChatActivity.groupId_loc, String.valueOf(millis), "0", "0", 0, 0, 0, "0.0", "0.0", "", "", "1", "false", "", "", 0, "", "", "0", ""));
        dbHandler.UpdateLastMsg(ChatActivity.groupId_loc, from, msg_value, "", String.valueOf(millis), ChatMessages.CREATE_GROUP, MainActivity.unreadCount);


        ChatFragment.refreshLay();
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void exitchanneltrigger1(Context context) {
        DBHandler dbHandler = new DBHandler(context);

        dbHandler.DeleteChatsGrpComplete(group_id);
        dbHandler.DeleteChats(group_id);
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void leavetrigger(JSONObject jsonObject, Context context) {
        dbHandler.DeleteChats(jsonObject.optString("groupId"));
        dbHandler.DeleteGrpMsg(jsonObject.optString("groupId"));
        ChatFragment.refreshLay();
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
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

    private List<Integer> getAllMaterialColors() throws IOException, XmlPullParserException {
        XmlResourceParser xrp = UserDetails.this.getResources().getXml(R.xml.select_color);
        List<Integer> allColors = new ArrayList<>();
        int nextEvent;
        while ((nextEvent = xrp.next()) != XmlResourceParser.END_DOCUMENT) {
            String s = xrp.getName();
            if ("color".equals(s)) {
                String color = xrp.nextText();
                allColors.add(Color.parseColor(color));
            }
        }
        return allColors;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        room_type = intent.getStringExtra("room_type");
        IO.Options opts = new IO.Options();
        opts.forceNew = true;
        opts.reconnection = false;

        try {
            socket = IO.socket(Const.chatSocketURL, opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.e("SOCKET.IO ", e.getMessage());
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        if (room_type.equalsIgnoreCase("0")) {
            String themevalue = SharedHelper.getKey(this, "theme_value");
            Setheme(themevalue);
            setContentView(R.layout.activity_scrolling);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            proName = (ImageView) findViewById(R.id.text_name_per);
            JSONObject object;

            String our_id = SharedHelper.getKey(getApplicationContext(), "id");

            ImageView userImage = (ImageView) findViewById(R.id.user_image);


            dbHandler = new DBHandler(UserDetails.this);


            TextView mobile = (TextView) findViewById(R.id.mobile_no);
            TextView status = (TextView) findViewById(R.id.user_status);
            recyclerView = (RecyclerView) findViewById(R.id.media_list);
            CardView cardView = (CardView) findViewById(R.id.media_layout);
            TextView media_text = (TextView) findViewById(R.id.media_text);
            TextView phone = (TextView) findViewById(R.id.status_and_phone);
            phone.setTextColor(getPrimaryCOlor(UserDetails.this));
            media_text.setTextColor(getPrimaryCOlor(UserDetails.this));
            if (intent != null) {
                imageArray = new ArrayList<>();

                image = intent.getStringExtra("image");//userImage


                final String f_let = String.valueOf(intent.getStringExtra("user_name").charAt(0));
                String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                Log.d("onBindViewHolder: ", "f_let" + f_let);
                int color = alphabet.indexOf(f_let.toUpperCase());
                Log.d("onBindViewHolder: ", "color" + color);
                if (color == -1) {
                    String number = "0123456789";
                    color = number.indexOf(f_let);
                    if (color == -1) {
                        String regExSpecialChars = "<([{\\^-=$!|]})?*+.>";
                        color = regExSpecialChars.indexOf(f_let);
                    }
                }

                List<Integer> finalAllColors = null;

                try {
                    finalAllColors = getAllMaterialColors();
                } catch (IOException | XmlPullParserException e) {
                    e.printStackTrace();
                }


                int randomColor = finalAllColors.get(color);
                if (image.equalsIgnoreCase(" ") || image.equalsIgnoreCase("")) {

                    Log.d("onBindViewHolder: ", "ccc:" + randomColor + " " + finalAllColors.get(5));
                    userImage.setImageDrawable(new ColorDrawable(randomColor));
//                    proName.setText(f_let.toUpperCase());
                    proName.setVisibility(View.VISIBLE);

                } else {
                    proName.setVisibility(View.GONE);
                    Picasso.with(UserDetails.this).load(image).placeholder(new ColorDrawable(randomColor)).error(new ColorDrawable(randomColor)).into(userImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
//                            proName.setText(f_let.toUpperCase());
                            proName.setVisibility(View.VISIBLE);

                        }
                    });
                }

                toolbar.setTitle(intent.getStringExtra("user_name"));
                ZoeChatID = intent.getStringExtra("zoeChatID");
                Status = dbHandler.GetUserStatus(ZoeChatID);
                Log.d("onCreate: ", "");
                mobile.setText(ZoeChatID);
                status.setText(Status);
                JSONArray jsonArray = dbHandler.GetChatsMessages(intent.getStringExtra("zoeChatID"));

                for (int i = 0; i < jsonArray.length(); i++) {
                    String type = jsonArray.optJSONObject(i).optString("userType");
                    if (type.equalsIgnoreCase(ChatMessages.SENDER_IMAGE)) {
                        String msg = jsonArray.optJSONObject(i).optString("msg");
                        imageArray.add(msg);
                    } else if (type.equalsIgnoreCase(ChatMessages.RECEIVER_IMAGE)) {
                        if (jsonArray.optJSONObject(i).optString("download").equalsIgnoreCase("1")) {
                            String msg = jsonArray.optJSONObject(i).optString("msg");
                            imageArray.add(msg);
                        }
                    }
                }
                Log.e("media_array", "" + imageArray);

                if (imageArray.size() > 0) {
                    MediaImgAdapter mediaImageAdapter = new MediaImgAdapter(UserDetails.this, imageArray);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                    linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(mediaImageAdapter);
                } else {
                    cardView.setVisibility(View.GONE);
                }


            }
            setSupportActionBar(toolbar);

            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        } else if (room_type.equalsIgnoreCase("3")) {

            String themevalue = SharedHelper.getKey(this, "theme_value");
            Setheme(themevalue);
            setContentView(R.layout.activity_scrolling_channel);
            party_layout = (CardView) findViewById(R.id.party_layout);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            TextView media_text = (TextView) findViewById(R.id.media_text);
            LinearLayout noti_lay = (LinearLayout) findViewById(R.id.notification_layout);
            LinearLayout invite_via = (LinearLayout) findViewById(R.id.invite_via);
            noti_lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openalert();
                }
            });
            invite_via.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(UserDetails.this, ChannelLink.class);
                    intent1.putExtra("channel_link", "qwertasdfzxcv");
                    startActivity(intent1);
                }
            });
            TextView desription_text = (TextView) findViewById(R.id.channel_description);
            media_text.setTextColor(getPrimaryCOlor(UserDetails.this));
            dbHandler = new DBHandler(UserDetails.this);
            progressBar = (ProgressBar) findViewById(R.id.profile_image_progress);
            progressBar.setVisibility(View.GONE);
            imageArray = new ArrayList<>();
            group_id = intent.getStringExtra("group_id");
            group_description = dbHandler.GetGroupDesc(group_id);
            desription_text.setText(group_description);
            List<ChannelParticiapntsModel> models = new ArrayList<>();
            models = dbHandler.GetPartiFromChannel(group_id);
            String group_name = dbHandler.GetGroupName(group_id);
            toolbar.setTitle(group_name);

            try {
                particpants_array = getarrayfromliste(models);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONObject group_details = new JSONObject();
            try {
                group_details = new JSONObject(intent.getStringExtra("grp_details"));
                if (group_details.length() == 0) {
                    group_details = dbHandler.GetGroupdetails(ChatActivity.groupId_loc);
                }
                Log.d("onCreate: ", "grp_details_val:" + group_details);
                Log.d("onCreate: ", "part_details_val:" + particpants_array);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            particpants_array_val = particpants_array;
            group_details_val = group_details;
            Button exit_group;

            dbHandler = new DBHandler(UserDetails.this);


            exit_group = (Button) findViewById(R.id.exit_group);
            userImage = (ImageView) findViewById(R.id.user_image);
            TextView participants_count = (TextView) findViewById(R.id.participants_count);
            participants_count.setTextColor(getPrimaryCOlor(UserDetails.this));
            image = dbHandler.GetGroupImage(ChatActivity.groupId_loc);//userImage

            exit_group.setText("Exit the Channel");
            exit_group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String grp_id = group_id;

                    showalert(grp_id, "Are you sure you want to exit ?");

                }
            });
            if (image.equalsIgnoreCase("") || image.equalsIgnoreCase(" ")) {
                Picasso.with(UserDetails.this).load(R.drawable.ic_channel)
                        .error(UserDetails.this.getResources().getDrawable(R.drawable.ic_channel)).into(userImage);
            } else {

                Picasso.with(UserDetails.this).load(image).error(R.drawable.ic_channel).into(userImage);
            }

            userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (room_type.equalsIgnoreCase("2")) {

                    } else {
                        Intent intent1 = new Intent(UserDetails.this, ManageChannel.class);
                        intent1.putExtra("group_id", group_id);
                        startActivity(intent1);

                    }

                }
            });


            final JSONArray finalParticpants_array = particpants_array;
            final JSONObject finalGroup_details = group_details;
            particpants_array_val = particpants_array;
            group_details_val = group_details;

            add_parti = (LinearLayout) findViewById(R.id.add_participants);


            add_parti.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(UserDetails.this, NewGroup_activity.class);
                    intent1.putExtra("particpants_array_val", String.valueOf(finalParticpants_array));
                    intent1.putExtra("group_details", String.valueOf(finalGroup_details));
                    intent1.putExtra("forward_msg", "false");
                    intent1.putExtra("room_type", room_type);

                    ChatFragment.isGroup = "true";
                    startActivity(intent1);
                }
            });
            CparticipantAdapter = new ChannelParticipantAdapter(UserDetails.this, particpants_array);
            String count = particpants_array.length() + " Participants";
            participants_count.setText(count);


            parti_recycler = (RecyclerView) findViewById(R.id.participant_list);
            parti_recycler.setNestedScrollingEnabled(false);
            CardView cardView = (CardView) findViewById(R.id.media_layout);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(UserDetails.this);
            parti_recycler.setLayoutManager(linearLayoutManager);
            parti_recycler.setAdapter(CparticipantAdapter);
            String role = dbHandler.GetChannelMember(ChatActivity.groupId_loc, MainActivity.my_id);
            if (role.equalsIgnoreCase("user")) {
                party_layout.setVisibility(View.GONE);
            }
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

            JSONArray jsonArray = dbHandler.GetGroupChatsMessages(finalGroup_details.optString("groupId"));

            for (int i = 0; i < jsonArray.length(); i++) {
                String type = jsonArray.optJSONObject(i).optString("userType");
                if (type.equalsIgnoreCase(ChatMessages.SENDER_IMAGE)) {
                    String msg = jsonArray.optJSONObject(i).optString("msg");
                    imageArray.add(msg);
                } else if (type.equalsIgnoreCase(ChatMessages.RECEIVER_IMAGE)) {
                    if (jsonArray.optJSONObject(i).optString("download").equalsIgnoreCase("1")) {
                        String msg = jsonArray.optJSONObject(i).optString("msg");
                        imageArray.add(msg);
                    }
                }
            }
            Log.e("media_array", "" + imageArray);

            if (imageArray.size() > 0) {
                MediaImgAdapter mediaImageAdapter = new MediaImgAdapter(UserDetails.this, imageArray);
                RecyclerView recyclerViewmedia = (RecyclerView) findViewById(R.id.media_list);
                linearLayoutManager = new LinearLayoutManager(this);
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                recyclerViewmedia.setLayoutManager(linearLayoutManager);
                recyclerViewmedia.setAdapter(mediaImageAdapter);
            } else {
                cardView.setVisibility(View.GONE);
            }


        } else if (room_type.equalsIgnoreCase("2")) {
            String themevalue = SharedHelper.getKey(this, "theme_value");
            Setheme(themevalue);
            setContentView(R.layout.activity_scrolling_group);
            TextView media_text = (TextView) findViewById(R.id.media_text);
            media_text.setTextColor(getPrimaryCOlor(UserDetails.this));
            dbHandler = new DBHandler(UserDetails.this);
            progressBar = (ProgressBar) findViewById(R.id.profile_image_progress);
            progressBar.setVisibility(View.GONE);
            imageArray = new ArrayList<>();
            group_id = intent.getStringExtra("group_id");

            Log.d("onCreate: ", "group_id:" + group_id);
            intent = getIntent();

            List<GroupParticiapntsModel> models = new ArrayList<>();
            models = dbHandler.GetPartiFromGrp(group_id);


            try {
                particpants_array = getarrayfromlist(models);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final JSONArray finalParticpants_array = particpants_array;
            particpants_array_val = particpants_array;
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.inflateMenu(R.menu.group_menu);
            String image = dbHandler.GetGroupImage(group_id);
            String name = dbHandler.GetGroupName(group_id);

            final JSONObject finalGroup_details = new JSONObject();
            try {
                finalGroup_details.put("name", name);
                finalGroup_details.put("image", image);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {


                    int id = menuItem.getItemId();

                    if (!room_type.equalsIgnoreCase("0")) {
                        if (id == R.id.add_contact) {
                            Intent intent1 = new Intent(UserDetails.this, NewGroup_activity.class);
                            intent1.putExtra("particpants_array_val", String.valueOf(particpants_array_val));
                            intent1.putExtra("group_details", String.valueOf(group_details_val));
                            intent1.putExtra("forward_msg", "false");
                            intent1.putExtra("room_type", room_type);

                            ChatFragment.isGroup = "true";
                            startActivity(intent1);

                            return true;
                        }

                        if (id == R.id.edit_grp) {

                            String from = SharedHelper.getKey(UserDetails.this, "my_zoe_id");
                            Intent intent1 = new Intent(UserDetails.this, ChangeGrpName.class);
                            intent1.putExtra("grop_id", ChatActivity.groupId_loc);
                            intent1.putExtra("from_id", from);
                            intent1.putExtra("old_name", group_details_val.optString("name"));
                            if (ChatActivity.localChatRoomType.equalsIgnoreCase("2")) {
                                intent1.putExtra("broadcast", "yes");
                            } else {
                                intent1.putExtra("broadcast", "no");
                            }
                            startActivity(intent1);

                            return true;
                        }
                    }
                    return true;
                }
            });


//            TextView grp_name, grp_details;
//            ImageView back_button, edit_button;
            Button exit_group;

            dbHandler = new DBHandler(UserDetails.this);

//            Log.d("onCreate: ", "grpoup_detials" + group_details);
//            grp_name = (TextView) findViewById(R.id.grp_name);
//            grp_details = (TextView) findViewById(R.id.grp_created_details);
//            back_button = (ImageView) findViewById(R.id.grp_tool_back);
//            edit_button = (ImageView) findViewById(R.id.grp_name_edit);
            String c_name = "";
            exit_group = (Button) findViewById(R.id.exit_group);
            userImage = (ImageView) findViewById(R.id.user_image);
            TextView participants_count = (TextView) findViewById(R.id.participants_count);
            participants_count.setTextColor(getPrimaryCOlor(UserDetails.this));

            if (image.equalsIgnoreCase("") || image.equalsIgnoreCase(" ")) {
                Picasso.with(UserDetails.this).load(R.drawable.ic_broad)
                        .error(UserDetails.this.getResources().getDrawable(R.drawable.ic_broad)).into(userImage);
            } else {

                Picasso.with(UserDetails.this).load(image).error(R.drawable.ic_broad).into(userImage);
            }
            userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (room_type.equalsIgnoreCase("2")) {

                    } else {
                        showPictureDialog();
                    }

                }
            });


            if (room_type.equalsIgnoreCase("2")) {
                exit_group.setText("Delete Broadcast list");
            }

            String group_name = dbHandler.GetGroupName(group_id);
            add_parti = (LinearLayout) findViewById(R.id.add_participants);


            add_parti.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(UserDetails.this, NewGroup_activity.class);
                    intent1.putExtra("particpants_array_val", String.valueOf(finalParticpants_array));

                    intent1.putExtra("group_details", String.valueOf(finalGroup_details));
                    intent1.putExtra("forward_msg", "false");
                    intent1.putExtra("room_type", room_type);

                    ChatFragment.isGroup = "true";
                    startActivity(intent1);
                }
            });

            participantAdapter = new ParticipantAdapter(UserDetails.this, particpants_array);
            String count = particpants_array.length() + " Participants";
            participants_count.setText(count);
            parti_recycler = (RecyclerView) findViewById(R.id.participant_list);
            parti_recycler.setNestedScrollingEnabled(false);
            CardView cardView = (CardView) findViewById(R.id.media_layout);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(UserDetails.this);
            parti_recycler.setLayoutManager(linearLayoutManager);
            parti_recycler.setAdapter(participantAdapter);
            toolbar.setTitle(group_name);

            JSONArray jsonArray = dbHandler.GetGroupChatsMessages(group_id);

            for (int i = 0; i < jsonArray.length(); i++) {
                String type = jsonArray.optJSONObject(i).optString("userType");
                if (type.equalsIgnoreCase(ChatMessages.SENDER_IMAGE)) {
                    String msg = jsonArray.optJSONObject(i).optString("msg");
                    imageArray.add(msg);
                } else if (type.equalsIgnoreCase(ChatMessages.RECEIVER_IMAGE)) {
                    if (jsonArray.optJSONObject(i).optString("download").equalsIgnoreCase("1")) {
                        String msg = jsonArray.optJSONObject(i).optString("msg");
                        imageArray.add(msg);
                    }
                }
            }
            Log.e("media_array", "" + imageArray);

            if (imageArray.size() > 0) {
                MediaImgAdapter mediaImageAdapter = new MediaImgAdapter(UserDetails.this, imageArray);
                RecyclerView recyclerViewmedia = (RecyclerView) findViewById(R.id.media_list);
                linearLayoutManager = new LinearLayoutManager(this);
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                recyclerViewmedia.setLayoutManager(linearLayoutManager);
                recyclerViewmedia.setAdapter(mediaImageAdapter);
            } else {
                cardView.setVisibility(View.GONE);
            }

            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        } else {
            String themevalue = SharedHelper.getKey(this, "theme_value");
            Setheme(themevalue);
            setContentView(R.layout.activity_scrolling_group);
            TextView media_text = (TextView) findViewById(R.id.media_text);
            media_text.setTextColor(getPrimaryCOlor(UserDetails.this));
            dbHandler = new DBHandler(UserDetails.this);
            progressBar = (ProgressBar) findViewById(R.id.profile_image_progress);
            progressBar.setVisibility(View.GONE);
            imageArray = new ArrayList<>();
            group_id = intent.getStringExtra("group_id");
            Log.d("onCreate: ", "group_id:" + group_id);
            intent = getIntent();

            List<GroupParticiapntsModel> models = new ArrayList<>();
            models = dbHandler.GetPartiFromGrp(group_id);


            try {
                particpants_array = getarrayfromlist(models);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONObject group_details = new JSONObject();
            try {
                group_details = new JSONObject(intent.getStringExtra("grp_details"));
                Log.d("onCreate: ", "grp_details_val:" + group_details);
                Log.d("onCreate: ", "part_details_val:" + particpants_array);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            final JSONArray finalParticpants_array = particpants_array;
            final JSONObject finalGroup_details = group_details;
            particpants_array_val = particpants_array;
            group_details_val = group_details;
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.inflateMenu(R.menu.group_menu);

            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {


                    int id = menuItem.getItemId();

                    if (!room_type.equalsIgnoreCase("0")) {
                        if (id == R.id.add_contact) {
                            Intent intent1 = new Intent(UserDetails.this, NewGroup_activity.class);
                            intent1.putExtra("particpants_array_val", String.valueOf(particpants_array_val));
                            intent1.putExtra("group_details", String.valueOf(group_details_val));
                            intent1.putExtra("forward_msg", "false");
                            intent1.putExtra("room_type", room_type);

                            ChatFragment.isGroup = "true";
                            startActivity(intent1);

                            return true;
                        }

                        if (id == R.id.edit_grp) {

                            String from = SharedHelper.getKey(UserDetails.this, "my_zoe_id");
                            Intent intent1 = new Intent(UserDetails.this, ChangeGrpName.class);
                            intent1.putExtra("grop_id", ChatActivity.groupId_loc);
                            intent1.putExtra("from_id", from);
                            intent1.putExtra("old_name", group_details_val.optString("name"));
                            if (ChatActivity.localChatRoomType.equalsIgnoreCase("2")) {
                                intent1.putExtra("broadcast", "yes");
                            } else {
                                intent1.putExtra("broadcast", "no");
                            }
                            startActivity(intent1);

                            return true;
                        }
                    }
                    return true;
                }
            });


//            TextView grp_name, grp_details;
//            ImageView back_button, edit_button;
            Button exit_group;

            dbHandler = new DBHandler(UserDetails.this);

//            Log.d("onCreate: ", "grpoup_detials" + group_details);
//            grp_name = (TextView) findViewById(R.id.grp_name);
//            grp_details = (TextView) findViewById(R.id.grp_created_details);
//            back_button = (ImageView) findViewById(R.id.grp_tool_back);
//            edit_button = (ImageView) findViewById(R.id.grp_name_edit);
            String c_name = "";
            exit_group = (Button) findViewById(R.id.exit_group);
            userImage = (ImageView) findViewById(R.id.user_image);
            TextView participants_count = (TextView) findViewById(R.id.participants_count);
            participants_count.setTextColor(getPrimaryCOlor(UserDetails.this));

            if (finalGroup_details.optString("image").equalsIgnoreCase("") || finalGroup_details.optString("image").equalsIgnoreCase(" ")) {
                Picasso.with(UserDetails.this).load(R.drawable.ic_profile_group)
                        .error(UserDetails.this.getResources().getDrawable(R.drawable.ic_profile_group)).into(userImage);
            } else {

                Picasso.with(UserDetails.this).load(finalGroup_details.optString("image")).error(R.drawable.ic_profile_group).into(userImage);
            }
            userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (room_type.equalsIgnoreCase("2")) {

                    } else {
                        showPictureDialog();
                    }

                }
            });


            add_parti = (LinearLayout) findViewById(R.id.add_participants);
            if (room_type.equalsIgnoreCase("2")) {
                exit_group.setText("Delete Broadcast list");
            }
            exit_group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String grp_id = finalGroup_details.optString("groupId");
                    if (!room_type.equalsIgnoreCase("2")) {
                        showalert(grp_id, "Are you sure you want to leave ?");
                    } else {
                        showalert(grp_id, "Are you sure you want to delete ?");
                    }
                }
            });
            String group_name = dbHandler.GetGroupName(group_id);

            add_parti.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(UserDetails.this, NewGroup_activity.class);
                    intent1.putExtra("particpants_array_val", String.valueOf(finalParticpants_array));
                    intent1.putExtra("group_details", String.valueOf(finalGroup_details));
                    intent1.putExtra("forward_msg", "false");
                    intent1.putExtra("room_type", room_type);

                    ChatFragment.isGroup = "true";
                    startActivity(intent1);
                }
            });

            participantAdapter = new ParticipantAdapter(UserDetails.this, particpants_array);
            String count = particpants_array.length() + " Participants";
            participants_count.setText(count);
            parti_recycler = (RecyclerView) findViewById(R.id.participant_list);
            parti_recycler.setNestedScrollingEnabled(false);
            CardView cardView = (CardView) findViewById(R.id.media_layout);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(UserDetails.this);
            parti_recycler.setLayoutManager(linearLayoutManager);
            parti_recycler.setAdapter(participantAdapter);
            toolbar.setTitle(group_name);

            JSONArray jsonArray = dbHandler.GetGroupChatsMessages(finalGroup_details.optString("groupId"));

            for (int i = 0; i < jsonArray.length(); i++) {
                String type = jsonArray.optJSONObject(i).optString("userType");
                if (type.equalsIgnoreCase(ChatMessages.SENDER_IMAGE)) {
                    String msg = jsonArray.optJSONObject(i).optString("msg");
                    imageArray.add(msg);
                } else if (type.equalsIgnoreCase(ChatMessages.RECEIVER_IMAGE)) {
                    if (jsonArray.optJSONObject(i).optString("download").equalsIgnoreCase("1")) {
                        String msg = jsonArray.optJSONObject(i).optString("msg");
                        imageArray.add(msg);
                    }
                }
            }
            Log.e("media_array", "" + imageArray);

            if (imageArray.size() > 0) {
                MediaImgAdapter mediaImageAdapter = new MediaImgAdapter(UserDetails.this, imageArray);
                RecyclerView recyclerViewmedia = (RecyclerView) findViewById(R.id.media_list);
                linearLayoutManager = new LinearLayoutManager(this);
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                recyclerViewmedia.setLayoutManager(linearLayoutManager);
                recyclerViewmedia.setAdapter(mediaImageAdapter);
            } else {
                cardView.setVisibility(View.GONE);
            }

            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }
    }

    private void openalert() {
        final Dialog dialog = new Dialog(UserDetails.this);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.notifiation_dialog);

        LinearLayout turn_on, mute_hour, mute_days, customize, turn_off;

        turn_on = (LinearLayout) dialog.findViewById(R.id.turn_on_lay);
        turn_off = (LinearLayout) dialog.findViewById(R.id.turn_off_lay);
        mute_hour = (LinearLayout) dialog.findViewById(R.id.mute_lay_1);
        mute_days = (LinearLayout) dialog.findViewById(R.id.mute_lay_2);
        customize = (LinearLayout) dialog.findViewById(R.id.customize_lay);
        turn_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHandler.UpdatetimedNotifications(ChatActivity.groupId_loc, "0");
                dialog.dismiss();
            }
        });
        turn_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHandler.UpdatetimedNotifications(ChatActivity.groupId_loc, "1");
                dialog.dismiss();
            }
        });
        mute_hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long current_time = System.currentTimeMillis() + 3600000;
                dbHandler.UpdatetimedNotifications(ChatActivity.groupId_loc, String.valueOf(current_time));
                dialog.dismiss();
            }
        });
        mute_days.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long current_time = System.currentTimeMillis() + 172800000;
                dbHandler.UpdatetimedNotifications(ChatActivity.groupId_loc, String.valueOf(current_time));
                dialog.dismiss();
            }
        });
        customize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent custom = new Intent(UserDetails.this, CustomizeNotification.class);
                startActivity(custom);
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        dialog.show();
    }

    private void ReceiveMessage(String chatType, final String messageId, final String userId, String msg, com.bitware.coworker.models.Status delivered, String sentTime,
                                final String deliveredTime, final String seenTime, int caption, int download, final String to, String lat,
                                String lng, String groupName, String create_by, String sent_by, String groupId, String chatRoomType, String cName, String cNumber, boolean show, String tit, String desc, String log, String shouldSign) throws JSONException {

        //long time = dbHandler.GetLastRow(userId);
        try {
            String times = SharedHelper.getHeader(UserDetails.this, "header_time");
            long time;
            if (times.isEmpty()) {
                time = 0;
            } else {
                time = Long.parseLong(deliveredTime);
            }

            Log.e("lastRowTime", String.valueOf(+time));
            Log.e("main_activity_insert", "type:" + chatRoomType + ",grp_id:" + groupId + ",name:" + groupName);
            String chatTime = Utils.formatToYesterdayOrToday(Utils.getDate(time, "dd/MM/yyyy hh:mm:ss.SSS"));
            Log.e("chatTime", chatTime);

            if (time == 0) {
                SharedHelper.putHeader(UserDetails.this, "header_time", sentTime);
                addHeaderReceiveMsg(userId, ChatMessages.HEADER, sentTime, String.valueOf(System.currentTimeMillis()), groupId, chatRoomType);
                addDBReceiveMsg(chatType, messageId, userId, msg, delivered, sentTime, deliveredTime, seenTime, caption, download, to, lat, lng, groupName, create_by, sent_by, groupId, chatRoomType, cName, cNumber, show, tit, desc, log, shouldSign);

            } else if (chatTime.equalsIgnoreCase("Today")) {
                if (MainActivity.value == 0) {
                    MainActivity.value++;
                    SharedHelper.putInt(UserDetails.this, "date", MainActivity.value);
                }
                SharedHelper.putHeader(UserDetails.this, "header_time", sentTime);
                MainActivity.value = Integer.parseInt(SharedHelper.getInt(UserDetails.this, "date"));
                addDBReceiveMsg(chatType, messageId, userId, msg, delivered, sentTime, deliveredTime, seenTime, caption, download, to, lat, lng, groupName, create_by, sent_by, groupId, chatRoomType, cName, cNumber, show, tit, desc, log, shouldSign);
            } else {
                SharedHelper.putHeader(UserDetails.this, "header_time", sentTime);
                addHeaderReceiveMsg(userId, ChatMessages.HEADER, sentTime, String.valueOf(System.currentTimeMillis()), groupId, chatRoomType);
                addDBReceiveMsg(chatType, messageId, userId, msg, delivered, sentTime, deliveredTime, seenTime, caption, download, to, lat, lng, groupName, create_by, sent_by, groupId, chatRoomType, cName, cNumber, show, tit, desc, log, shouldSign);
                MainActivity.value = 0;
            }
        } catch (ParseException | NumberFormatException e) {
            e.printStackTrace();
        }

        if (room_type.equalsIgnoreCase(chatRoomType)) {
            String zoeChatID = SharedHelper.getKey(UserDetails.this, "details_zoe_chat_id");
            String groupId_loc = SharedHelper.getKey(UserDetails.this, "details_grp_chat_id");
            if (userId.equalsIgnoreCase(zoeChatID)) {
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("msg", msg);
                if (chatType.equalsIgnoreCase(ChatType.text.toString())) {
                    jsonObject.put("userType", ChatMessages.RECEIVER);
                } else if (chatType.equalsIgnoreCase(ChatType.image.toString())) {
                    jsonObject.put("userType", ChatMessages.RECEIVER_IMAGE);
                } else if (chatType.equalsIgnoreCase(ChatType.location.toString())) {
                    jsonObject.put("userType", ChatMessages.RECEIVER_LOCATION);
                } else if (chatType.equalsIgnoreCase(ChatType.video.toString())) {
                    jsonObject.put("userType", ChatMessages.RECEIVER_VIDEO);
                } else if (chatType.equalsIgnoreCase(ChatType.contact.toString())) {
                    jsonObject.put("userType", ChatMessages.RECEIVER_CONTACT);
                } else if (chatType.equalsIgnoreCase(ChatType.audio.toString())) {
                    jsonObject.put("userType", ChatMessages.RECEIVER_AUDIO);
                } else if (chatType.equalsIgnoreCase(ChatType.document.toString())) {
                    jsonObject.put("userType", ChatMessages.RECEIVER_DOC);
                }

                jsonObject.put("contentStatus", com.bitware.coworker.models.Status.DELIVERED);
                jsonObject.put("chatType", chatType);
                jsonObject.put("lat", lat);
                jsonObject.put("lng", lng);
                jsonObject.put("chatRoomType", chatRoomType);
                jsonObject.put("sentTime", sentTime);
                jsonObject.put("cName", cName);
                jsonObject.put("cNumber", cNumber);
                jsonObject.put("msgId", messageId);
                jsonObject.put("userName", sent_by);
                ChatActivity.jsonArray.put(jsonObject);
                Log.d("ReceiveMessage: ", "1name:" + cName + ",type:" + chatRoomType);
                UserDetails.this.runOnUiThread(new Runnable() {
                    public void run() {
                        ChatActivity.singleChatAdapter.notifyDataSetChanged();
                    }
                });


                ChatFragment.mainActivity.refresh();
            } else {
                Log.d("ReceiveMessage: ", "2name:" + cName + ",type:" + chatRoomType);
                Log.d("ReceiveMessage: ", "userid:" + groupId_loc + ",grp:" + groupId + "loc:");
                if (groupId.equalsIgnoreCase(groupId_loc)) {
                    final JSONObject jsonObject = new JSONObject();
                    jsonObject.put("msg", msg);
                    if (chatType.equalsIgnoreCase(ChatType.text.toString())) {
                        jsonObject.put("userType", ChatMessages.RECEIVER);
                    } else if (chatType.equalsIgnoreCase(ChatType.image.toString())) {
                        jsonObject.put("userType", ChatMessages.RECEIVER_IMAGE);
                    } else if (chatType.equalsIgnoreCase(ChatType.location.toString())) {
                        jsonObject.put("userType", ChatMessages.RECEIVER_LOCATION);
                    } else if (chatType.equalsIgnoreCase(ChatType.video.toString())) {
                        jsonObject.put("userType", ChatMessages.RECEIVER_VIDEO);
                    } else if (chatType.equalsIgnoreCase(ChatType.audio.toString())) {
                        jsonObject.put("userType", ChatMessages.RECEIVER_AUDIO);
                    } else if (chatType.equalsIgnoreCase(ChatType.document.toString())) {
                        jsonObject.put("userType", ChatMessages.RECEIVER_DOC);
                    }
                    jsonObject.put("contentStatus", com.bitware.coworker.models.Status.DELIVERED);
                    jsonObject.put("chatType", chatType);
                    jsonObject.put("lat", lat);
                    jsonObject.put("lng", lng);
                    jsonObject.put("chatRoomType", chatRoomType);
                    jsonObject.put("sentTime", sentTime);
                    jsonObject.put("userName", userId);
                    jsonObject.put("msgId", messageId);
                    Log.d("ReceiveMessage: ", "jsonvaluesssss:" + jsonObject);
                    ChatActivity.jsonArray.put(jsonObject);

                    UserDetails.this.runOnUiThread(new Runnable() {
                        public void run() {
                            ChatActivity.singleChatAdapter.notifyDataSetChanged();

                        }
                    });

                    ChatFragment.mainActivity.refresh();
                }

            }

        }


        Log.e("count", String.valueOf(MainActivity.unreadCount));
        sendDelivered(messageId, userId, to, deliveredTime, chatRoomType);


        ChatFragment.mainActivity.refresh();

    }

    private void addHeaderReceiveMsg(String userId, String header, String currentTime, String msgId, String groupId, String chatRoomType) {
        DBHandler dbHandler = new DBHandler(UserDetails.this);
        dbHandler.InsertChatMsg(new ChatsMessagesModel(msgId, userId, "0", "0", "0", header, groupId, String.valueOf(currentTime), "0", "0", 0, 0, 0, "", "", "", "", chatRoomType, "false", "", "", 0, "", "", "", ""));
        dbHandler.close();
    }

    private void addDBReceiveMsg(String chatType, final String messageId, String userId,
                                 final String msg, final com.bitware.coworker.models.Status delivered, String sentTime, final String deliveredTime,
                                 final String seenTime, int caption, int download, final String to, String lat, String lng, String groupName,
                                 String create_by, String sent_by, String groupId, String chatRoomType, String cName, String cNumber, boolean showpre, String metatitle, String metadesc, String metalogo, String shouldSign) {
        DBHandler dbHandler = new DBHandler(UserDetails.this);


        String chatTypeLayout = "";


        if (userId.equalsIgnoreCase(SharedHelper.getKey(UserDetails.this, "id"))) {
//            if (chatRoomType.equalsIgnoreCase("0")) {
//                MainActivity.unreadCount = dbHandler.GetUnReadCount(to);
//                MainActivity.unreadCount++;
//            } else {
//                MainActivity.unreadCount = dbHandler.GetUnReadCount(groupId);
//                MainActivity.unreadCount++;
//            }

            if (chatType.equalsIgnoreCase(ChatType.text.toString())) {
                chatTypeLayout = ChatMessages.SENDER;
            } else if (chatType.equalsIgnoreCase(ChatType.image.toString())) {
                chatTypeLayout = ChatMessages.SENDER_IMAGE;
            } else if (chatType.equalsIgnoreCase(ChatType.location.toString())) {
                chatTypeLayout = ChatMessages.SENDER_LOCATION;
            } else if (chatType.equalsIgnoreCase(ChatType.video.toString())) {
                chatTypeLayout = ChatMessages.SENDER_VIDEO;
            } else if (chatType.equalsIgnoreCase(ChatType.contact.toString())) {
                chatTypeLayout = ChatMessages.SENDER_CONTACT;
            } else if (chatType.equalsIgnoreCase(ChatType.audio.toString())) {
                chatTypeLayout = ChatMessages.SENDER_AUDIO;
            } else if (chatType.equalsIgnoreCase(ChatType.document.toString())) {
                chatTypeLayout = ChatMessages.SENDER_DOC;
            } else if (chatType.equalsIgnoreCase(ChatType.sticker.toString())) {
                chatTypeLayout = ChatMessages.SENDER_STICKER;
            }
            userId = to;
        } else {
            if (chatRoomType.equalsIgnoreCase("0")) {
                MainActivity.unreadCount = dbHandler.GetUnReadCount(userId);
                MainActivity.unreadCount++;
            } else {
                MainActivity.unreadCount = dbHandler.GetUnReadCount(groupId);
                MainActivity.unreadCount++;
            }
            if (chatType.equalsIgnoreCase(ChatType.text.toString())) {
                chatTypeLayout = ChatMessages.RECEIVER;
            } else if (chatType.equalsIgnoreCase(ChatType.image.toString())) {
                chatTypeLayout = ChatMessages.RECEIVER_IMAGE;
            } else if (chatType.equalsIgnoreCase(ChatType.location.toString())) {
                chatTypeLayout = ChatMessages.RECEIVER_LOCATION;
            } else if (chatType.equalsIgnoreCase(ChatType.video.toString())) {
                chatTypeLayout = ChatMessages.RECEIVER_VIDEO;
            } else if (chatType.equalsIgnoreCase(ChatType.contact.toString())) {
                chatTypeLayout = ChatMessages.RECEIVER_CONTACT;
            } else if (chatType.equalsIgnoreCase(ChatType.audio.toString())) {
                chatTypeLayout = ChatMessages.RECEIVER_AUDIO;
            } else if (chatType.equalsIgnoreCase(ChatType.document.toString())) {
                chatTypeLayout = ChatMessages.RECEIVER_DOC;
            } else if (chatType.equalsIgnoreCase(ChatType.sticker.toString())) {
                chatTypeLayout = ChatMessages.RECEIVER_STICKER;
            }


        }
        int showp = showpre ? 1 : 0;
        dbHandler.InsertChatMsg(new ChatsMessagesModel(messageId.trim(), userId, msg,
                chatType, delivered.toString(), chatTypeLayout, groupId,
                sentTime, String.valueOf(deliveredTime), seenTime, caption, 0, download, lat, lng, cName, cNumber, chatRoomType, "false", msg, "", showp, metatitle, metadesc, metalogo, shouldSign));


        if (!dbHandler.DoesChatsUser(userId)) {
            dbHandler.InsertChats(new ChatsModel(userId, "0", chatTypeLayout, msg, delivered.toString(), deliveredTime, chatType, MainActivity.unreadCount, groupName, create_by, sent_by, "", "0", "0", "", "", "", "", "", "", "false", "1", ""));
        } else {
            if (chatRoomType.equalsIgnoreCase("1")) {
                dbHandler.UpdateLastMsg(groupId, chatTypeLayout, msg, delivered.toString(), deliveredTime, chatType, MainActivity.unreadCount);
            } else {
                dbHandler.UpdateLastMsg(userId, chatTypeLayout, msg, delivered.toString(), deliveredTime, chatType, MainActivity.unreadCount);

            }
        }
        dbHandler.close();
    }

    private void sendDelivered(String messageId, String from, String to, String deliveredTime, String chatRoomType) {
        DBHandler dbHandler = new DBHandler(UserDetails.this);
        dbHandler.UpdateDeliveredTime(from, String.valueOf(deliveredTime));
        dbHandler.close();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("messageId", messageId);
            jsonObject.put("from", from);
            jsonObject.put("to", to);
            jsonObject.put("time", deliveredTime);
            jsonObject.put("chatRoomType", chatRoomType);
            socket.emit("sendDelivered", jsonObject);
            Log.d("sendDelivered", "true");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


        if (room_type.equalsIgnoreCase("1") || room_type.equalsIgnoreCase("2")) {
            getMenuInflater().inflate(R.menu.group_menu, menu);
        } else if (room_type.equalsIgnoreCase("3")) {
            String role = dbHandler.GetChannelMember(ChatActivity.groupId_loc, MainActivity.my_id);
            if (role.equalsIgnoreCase("user")) {

            } else {
                getMenuInflater().inflate(R.menu.channel_menu, menu);

            }

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (!room_type.equalsIgnoreCase("0")) {
            if (id == R.id.add_contact) {
                Intent intent1 = new Intent(UserDetails.this, NewGroup_activity.class);
                intent1.putExtra("particpants_array_val", String.valueOf(particpants_array_val));
                intent1.putExtra("group_details", String.valueOf(group_details_val));
                intent1.putExtra("forward_msg", "false");
                intent1.putExtra("room_type", room_type);

                ChatFragment.isGroup = "true";
                startActivity(intent1);

                return true;
            }

            if (id == R.id.edit_grp) {

                String from = SharedHelper.getKey(UserDetails.this, "my_zoe_id");
                Intent intent1 = new Intent(UserDetails.this, ChangeGrpName.class);
                intent1.putExtra("grop_id", ChatActivity.groupId_loc);
                intent1.putExtra("from_id", from);
                if (ChatActivity.localChatRoomType.equalsIgnoreCase("2")) {
                    intent1.putExtra("broadcast", "yes");
                } else {
                    intent1.putExtra("broadcast", "no");
                }
                startActivity(intent1);

                return true;
            }
        }
        if (id == R.id.action_manage) {

            Intent intent1 = new Intent(UserDetails.this, ManageChannel.class);
            intent1.putExtra("group_id", group_id);
            startActivity(intent1);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showPictureDialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Choose your option");
        String[] items = {"Gallery", "Camera"};

        dialog.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                switch (which) {
                    case 0:
                        choosePhotoFromGallary();
                        break;
                    case 1:
                        takePhotoFromCamera();
                        break;

                }
            }
        });
        dialog.show();
    }

    private void choosePhotoFromGallary() {
        Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, 1);

    }

    private void validateincomingvalues(JSONArray particpants_array) {
        DBHandler dbHandler = new DBHandler(UserDetails.this);
        for (int i = 0; i < particpants_array.length(); i++) {
            String id = particpants_array.optJSONObject(i).optString("participantId");
            String grp_id = particpants_array.optJSONObject(i).optString("groupId");
            Boolean exist = dbHandler.CheckParticipantAlreadyInDBorNot(id, grp_id);
            if (exist) {
                String isAdmin = particpants_array.optJSONObject(i).optString("isAdmin");
                if (isAdmin.equalsIgnoreCase("")) {
                    isAdmin = "0";
                }
                dbHandler.groupPartiUpdate(grp_id, particpants_array.optJSONObject(i).optString("joinedAt"), isAdmin, particpants_array.optJSONObject(i).optString("addedBy"), id);
            } else {
                String isAdmin = particpants_array.optJSONObject(i).optString("isAdmin");
                if (isAdmin.equalsIgnoreCase("")) {
                    isAdmin = "0";
                }
                dbHandler.AddGroupParticipants(new GroupParticiapntsModel(id, grp_id, particpants_array.optJSONObject(i).optString("joinedAt"), particpants_array.optJSONObject(i).optString("addedBy"), isAdmin));
            }

        }


    }

    private void cvalidateincomingvalues(JSONArray particpants_array) {
        DBHandler dbHandler = new DBHandler(UserDetails.this);
        for (int i = 0; i < particpants_array.length(); i++) {
            String id = particpants_array.optJSONObject(i).optString("participantId");
            String grp_id = particpants_array.optJSONObject(i).optString("channelId");
            Boolean exist = dbHandler.CheckChanneParticipantAlreadyInDBorNot(id, grp_id);
            if (exist) {
                String isAdmin = particpants_array.optJSONObject(i).optString("isAdmin");
                if (isAdmin.equalsIgnoreCase("")) {
                    isAdmin = "user";
                }
                dbHandler.channelPartiUpdate(grp_id, particpants_array.optJSONObject(i).optString("joinedAt"), isAdmin, particpants_array.optJSONObject(i).optString("addedBy"), id);
            } else {
                String isAdmin = particpants_array.optJSONObject(i).optString("isAdmin");
                if (isAdmin.equalsIgnoreCase("")) {
                    isAdmin = "user";
                }
                dbHandler.AddChannelParticipants(new ChannelParticiapntsModel(id, grp_id, particpants_array.optJSONObject(i).optString("joinedAt"), particpants_array.optJSONObject(i).optString("addedBy"), isAdmin));
            }

        }


    }

    private void validatelocalvalues(List<GroupParticiapntsModel> models, JSONArray particpants_array) {
        DBHandler dbHandler = new DBHandler(this);
        for (int i = 0; i < models.size(); i++) {
            GroupParticiapntsModel values = models.get(i);
            String id = values.getUser_id();
            Boolean exist = value(particpants_array, id);
            if (!exist) {
                dbHandler.DeleteGroupParticipants(id, groupId_loc);
            }
        }

    }

    private boolean value(JSONArray jsonArray, String part_id) {
        return jsonArray.toString().contains("\"participantId\":\"" + part_id + "\"");
    }

    private void cvalidatelocalvalues(List<ChannelParticiapntsModel> models, JSONArray particpants_array) {
        DBHandler dbHandler = new DBHandler(this);
        for (int i = 0; i < models.size(); i++) {
            ChannelParticiapntsModel values = models.get(i);
            String id = values.getUser_id();
            Boolean exist = value(particpants_array, id);
            if (!exist) {
                dbHandler.DeleteChannelParticipants(id, groupId_loc);
            }
        }

    }

    private void takePhotoFromCamera() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
        } else {


            String register_id = SharedHelper.getKey(UserDetails.this, "id");
            File file = new File(Environment.getExternalStorageDirectory(), register_id + ".jpg");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {

                file.delete();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            uri = Uri.fromFile(file);
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(i, 2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Activity_Res", "" + requestCode);
        switch (requestCode) {
            case 1:
                if (data != null) {

                    uri = data.getData();
                    if (uri != null) {
                        beginCrop(uri);

                    } else {
                        Utils.showShortToast("Unable to select image", getApplicationContext());
                    }
                }
                break;
            case 2:
                if (uri != null) {
                    beginCrop(uri);

                } else {
                    Utils.showShortToast("Unable to select image", getApplicationContext());
                }
                break;
            case Crop.REQUEST_CROP:

                if (data != null)
                    try {
                        handleCrop(resultCode, data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    //Toast.makeText(SignInActivity.this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
                    String register_id = SharedHelper.getKey(UserDetails.this, "id");
                    File file = new File(Environment.getExternalStorageDirectory(), register_id + ".jpg");
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {

                        file.delete();
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    uri = Uri.fromFile(file);
                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(i, 2);
                } else {
                    Toast.makeText(this, "You need to give permission to open camera", Toast.LENGTH_SHORT).show();
                }
                break;


            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void uploadImage() throws JSONException {
        if (!filePath.isEmpty()) {
            final File file = new File(filePath);
//            AmazonS3Client s3Client = new AmazonS3Client(new BasicAWSCredentials(Const.accessKey, Const.secretKey));
//            s3Client.setRegion(Const.Regionss3);
//            s3Client.setEndpoint(Const.Endpoint);
            System.setProperty(SDKGlobalConfiguration.ENFORCE_S3_SIGV4_SYSTEM_PROPERTY, "true");
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(UserDetails.this, Const.IDENTIY_POOL_ID, Const.cognitoRegion);
            AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider);
            s3Client.setEndpoint(Const.ENDPOINT);


            TransferUtility transferUtility = new TransferUtility(s3Client, UserDetails.this);

            TransferObserver transferObserver = transferUtility.upload(Const.bucket_name, file.getName(), file);
            transferObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    Log.e("image_state", state.toString());
                    if (state.toString().equalsIgnoreCase("IN_PROGRESS")) {
                        progressBar.setVisibility(View.VISIBLE);
                    } else if (state.toString().equalsIgnoreCase("COMPLETED")) {
                        progressBar.setVisibility(View.GONE);
                        //uploadStatus = true;
                        try {
                            upDate();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    int percentage = (int) (bytesCurrent / bytesTotal * 100);
                    Log.e("image_state_percentage", String.valueOf(percentage));
                }

                @Override
                public void onError(int id, Exception ex) {
                    Log.e("image_state_error", ex.getMessage());
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            upDate();
        }

    }

    //socket code for image
    private void upDate() throws JSONException {

        JSONObject jsonObject = new JSONObject();
        final File file = new File(filePath);
        final String from = SharedHelper.getKey(UserDetails.this, "my_zoe_id");
        final String valuess = Const.amazons3ServerImagePath + file.getName();
        jsonObject.put("from", from);
        jsonObject.put("groupId", group_id);
        jsonObject.put("groupImage", Const.amazons3ServerImagePath + file.getName());
        Log.d("editgrp_image: ", "" + jsonObject);
        ServiceClasss.Emitters emitters = new ServiceClasss.Emitters(UserDetails.this);
        emitters.editGroupImage(jsonObject);


//        socket.emit("editGroupImage", jsonObject, new Ack() {
//            @Override
//            public void call(Object... args) {
//                String msg = String.valueOf(args[0]);
//                Log.d("editgrp_image: ", "message:" + args[0]);
//                if (msg.equalsIgnoreCase("true")) {
//
//                }
//            }
//        });


    }

    private void beginCrop(Uri source) {
        //String register_id = code + number;
        Uri outputUri = Uri.fromFile(new File(Environment
                .getExternalStorageDirectory(), Calendar.getInstance().getTimeInMillis() + ".jpg"));
        Crop.of(source, outputUri).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) throws JSONException {

        if (resultCode == RESULT_OK) {
            filePath = getRealPathFromURI(Crop.getOutput(result));
            //profileImage.setImageURI(Crop.getOutput(result));
            Log.e("image_path", "" + filePath);
            Picasso.with(this).load(Crop.getOutput(result)).into(userImage);
            uploadImage();
        } else if (resultCode == Crop.RESULT_ERROR) {
            Utils.showShortToast("Unable to select image", getApplicationContext());
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null,
                null, null, null);

        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor
                    .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private void showalert(final String grp_id, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserDetails.this, R.style.AlertDialogCustom);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>OK</font>"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        try {
                            if (room_type.equalsIgnoreCase("2")) {
                                dbHandler.DeleteChats(group_id);
                                Intent intent = new Intent(UserDetails.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else if (room_type.equalsIgnoreCase("3")) {
                                dbHandler.DeleteChats(grp_id);
                                leavechannel(grp_id);
                            } else {
                                leavegroup(grp_id);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void leavechannel(String grp_id) throws JSONException {


        JSONObject object = new JSONObject();
        object.put("from", MainActivity.my_id);
        object.put("channelId", grp_id);
        Log.d("leavechannel: ", "obj" + object);

        ServiceClasss.Emitters emitters = new ServiceClasss.Emitters(UserDetails.this);
        Boolean res = emitters.exitChannel(object, 1);

        if (res) {


        }
//        socket.emit("exitChannel", new Ack() {
//                @Override
//                public void call(Object... args) {
//                    String msg = String.valueOf(args[0]);
//                    Log.d("editgrp_image: ", "message:" + args[0]);
//                    if (msg.equalsIgnoreCase("true"))
//                    {
//
//                    }
//
//                }
//            });


    }

    private void leavegroup(final String grp_id) throws JSONException {

        JSONObject jsonObject = new JSONObject();

        String from = SharedHelper.getKey(UserDetails.this, "my_zoe_id");

        jsonObject.put("from", from);
        jsonObject.put("groupId", grp_id);
        Log.d("addgroup: ", "" + jsonObject);
        ServiceClasss.Emitters emitters = new ServiceClasss.Emitters(UserDetails.this);
        emitters.exitGroup(jsonObject);
    }

    public String getDateCurrentTimeZone(long timestamp) {
        try {
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currenTimeZone = calendar.getTime();
            return sdf.format(currenTimeZone);
        } catch (Exception e) {
        }
        return "";
    }

    @Override
    protected void onResume() {
        super.onResume();


        MyCommon.getInstance().mainActivity = true;
//        socket.on(Socket.EVENT_CONNECT, onConnect);
//        socket.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        socket.disconnect();
    }

    private class getGrpdetails extends AsyncTask<String, String, String> implements AsyncTaskCompleteListener {
        String result = "";
        private JSONArray particpants_array = new JSONArray();
        private JSONObject group_details = new JSONObject();

        @Override
        protected String doInBackground(String... params) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("groupId", groupId_loc);

                new PostHelper(Const.Methods.PARTICIPANTS_DETAILS, jsonObject.toString(), Const.ServiceCode.PARTICIPANTS_DETAILS, UserDetails.this, this);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }


        @Override
        public void onTaskCompleted(JSONObject response, int serviceCode) {

            DBHandler dbHandler = new DBHandler(UserDetails.this);
            particpants_array = response.optJSONArray("participants");
            group_details = response.optJSONObject("groupDetails");


            List<GroupParticiapntsModel> models = new ArrayList<>();

            if (dbHandler.DoesChatExist(groupId_loc)) {
                models = dbHandler.GetPartiFromGrp(groupId_loc);

                validatelocalvalues(models, particpants_array);
                validateincomingvalues(particpants_array);
            } else {
                for (int i = 0; i < particpants_array.length(); i++) {
                    String id = particpants_array.optJSONObject(i).optString("participantId");
                    String grp_id = particpants_array.optJSONObject(i).optString("groupId");
                    String isAdmin = particpants_array.optJSONObject(i).optString("isAdmin");
                    if (isAdmin.equalsIgnoreCase("")) {
                        isAdmin = "0";
                    }

                    dbHandler.InsertGroup_details(grp_id, group_details.optString("createdBy"), group_details.optString("createdAt"), group_details.optString("image"), group_details.optString("name"));

                    dbHandler.AddGroupParticipants(new GroupParticiapntsModel(id, grp_id, particpants_array.optJSONObject(i).optString("joinedAt"), particpants_array.optJSONObject(i).optString("addedBy"), isAdmin));

                }
            }
            Log.d("onTaskCompleted: ", "" + response);

        }
    }

    private class getChanneldetails extends AsyncTask<String, String, String> implements AsyncTaskCompleteListener {
        String result = "";
        private JSONArray particpants_array = new JSONArray();
        private JSONObject group_details = new JSONObject();

        @Override
        protected String doInBackground(String... params) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("channelId", groupId_loc);

                new PostHelper(Const.Methods.CHANNEL_PARTICIPANTS_DETAILS, jsonObject.toString(), Const.ServiceCode.CHANNEL_PARTICIPANTS_DETAILS, UserDetails.this, this);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }


        @Override
        public void onTaskCompleted(JSONObject response, int serviceCode) {

            DBHandler dbHandler = new DBHandler(UserDetails.this);
            particpants_array = response.optJSONArray("participants");
            group_details = response.optJSONObject("groupDetails");


            List<ChannelParticiapntsModel> models = new ArrayList<>();

            if (dbHandler.DoesChatExist(groupId_loc)) {
                models = dbHandler.GetPartiFromChannel(groupId_loc);

                cvalidatelocalvalues(models, particpants_array);
                cvalidateincomingvalues(particpants_array);
            } else {
                for (int i = 0; i < particpants_array.length(); i++) {
                    String id = particpants_array.optJSONObject(i).optString("participantId");
                    String grp_id = particpants_array.optJSONObject(i).optString("groupId");
                    String isAdmin = particpants_array.optJSONObject(i).optString("isAdmin");
                    if (isAdmin.equalsIgnoreCase("")) {
                        isAdmin = "user";
                    }
                    dbHandler.AddChannelParticipants(new ChannelParticiapntsModel(id, grp_id, particpants_array.optJSONObject(i).optString("joinedAt"), particpants_array.optJSONObject(i).optString("addedBy"), isAdmin));
                }
            }
            Log.d("onTaskCompleted: ", "" + response);

        }
    }

    private class MediaImgAdapter extends RecyclerView.Adapter<MediaImgAdapter.ViewHolder> {
        private ArrayList<String> image_array;
        private Context mContext;

        public MediaImgAdapter(Context context, ArrayList<String> imageArray) {
            this.image_array = imageArray;
            this.mContext = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.media_image_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            if (image_array.get(position).equalsIgnoreCase("") || image_array.get(position).equalsIgnoreCase(" ")) {
                Picasso.with(mContext).load(R.drawable.ic_account_circle)
                        .error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.show_image);
            } else {
                File f = new File(image_array.get(position));
                Picasso.with(mContext).load(f).into(holder.show_image);
            }

            holder.show_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ImageViewer_Activity.class);
                    String image_id = image_array.get(position);
                    String file = new File(image_id).getName();
                    intent.putExtra("select_file", file);
                    intent.putExtra("zoeChatId", ZoeChatID);
                    intent.putExtra("groupId", group_id);
                    intent.putExtra("chatRoomType", room_type);
                    mContext.startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return image_array.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView show_image;

            public ViewHolder(View itemView) {
                super(itemView);
                show_image = (ImageView) itemView.findViewById(R.id.media_image);
            }
        }
    }


}
