package com.bitware.coworker.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bitware.coworker.DBHelper.DBHandler;
import com.bitware.coworker.R;
import com.bitware.coworker.Service.ServiceClasss;
import com.bitware.coworker.adapter.ChatsAdapter;
import com.bitware.coworker.baseUtils.AsyncTaskCompleteListener;
import com.bitware.coworker.baseUtils.Const;
import com.bitware.coworker.baseUtils.MyCommon;
import com.bitware.coworker.baseUtils.PostHelper;
import com.bitware.coworker.baseUtils.SharedHelper;
import com.bitware.coworker.baseUtils.Utils;
import com.bitware.coworker.fragment.CallsFragment;
import com.bitware.coworker.fragment.ChatFragment;
import com.bitware.coworker.fragment.ContactFragment;
import com.bitware.coworker.models.ChannelParticiapntsModel;
import com.bitware.coworker.models.ChatMessages;
import com.bitware.coworker.models.ChatType;
import com.bitware.coworker.models.ChatsMessagesModel;
import com.bitware.coworker.models.ChatsModel;
import com.bitware.coworker.models.GroupParticiapntsModel;
import com.bitware.coworker.models.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    public static MainActivity mainActivity;
    public static int unreadCount = 0;
    public static Socket socket;
    public static int value = 0;
    public static Boolean isSending = false;
    public static String my_id;
    public static String my_country_code;
    public String groupId_loc;
    public ProgressBar progressBar;
    public TextView counterTextView;
    public Toolbar toolbar;
    Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", SharedHelper.getKey(getApplicationContext(), "id"));
                socket.emit("offline", jsonObject);
                Log.e("offline", "" + jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            socket.disconnect();
        }
    };
    private Handler handler1;
    //private DBHandler dbHandler;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private List<Fragment> FragmentList = new ArrayList();
    private List<String> FragmentTitle = new ArrayList();
    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject jsonObject = (JSONObject) args[0];
            Log.e("MainActivity", "Socket_Error" + jsonObject);
        }
    };
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
    private Runnable internetCheck = new Runnable() {
        @Override
        public void run() {

            Boolean isAvailable = isNetworkAvailable(getApplicationContext());

            if (isAvailable) {


                if (!isSending) {


                    isSending = true;
//                    sendingmessage();
                }
            }

            handler1.postDelayed(this, 5000);
        }

    };

    private void Setheme(String themevalue) {
        Log.d("Setheme: ", "themevalues:" + themevalue);
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

//    public static void SendChatMsgunsent(String image_path, String msgId, String userID,
//                                         String currentTime, String chatType, String from, String groupId, String chatRoomType) {
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("from", from);
//
//            if (chatRoomType.equalsIgnoreCase("0")) {
//                jsonObject.put("to", userID);
//            } else if (chatRoomType.equalsIgnoreCase("1")) {
//                jsonObject.put("to", groupId);
//            }
//            jsonObject.put("messageId", msgId);
//            jsonObject.put("chatRoomType", chatRoomType);
//            jsonObject.put("groupId", groupId);
//            jsonObject.put("time", currentTime);
//            jsonObject.put("content", image_path);
//
//            jsonObject.put("contentType", chatType);
//
//            Log.e("sendMsg_from_adapter", jsonObject.toString());
//            socket.emit("sendMessage", jsonObject);
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String themevalue = SharedHelper.getKey(this, "theme_value");
        Setheme(themevalue);
        setContentView(R.layout.activity_main);
        my_id = SharedHelper.getKey(MainActivity.this, "id");
        my_country_code = SharedHelper.getKey(MainActivity.this, "country_code_default");
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        Utils.enableStrictMode();

        IO.Options opts = new IO.Options();
        opts.forceNew = true;
        opts.reconnection = false;
        handler1 = new Handler();

        Log.d("onCreate: ", "MainActivity");
        try {
            socket = IO.socket(Const.chatSocketURL, opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.e("SOCKET.IO ", e.getMessage());
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.action_menu));
        progressBar = (ProgressBar) toolbar.findViewById(R.id.progress);
        counterTextView = (TextView) toolbar.findViewById(R.id.context_menu);
        setSupportActionBar(toolbar);
        mainActivity = MainActivity.this;

        if (!isMyServiceRunning(ServiceClasss.class)) {
            MainActivity.this.startService(new Intent(MainActivity.this, ServiceClasss.class));
        }


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.d("onPageScrolled: ", "position:" + position);

            }

            @Override
            public void onPageSelected(int position) {
                Log.d("onPageSelected: ", "position:" + position);
//                if (position == 0) {
//                    ContactFragment.refresh.clearActionM();
//                    CallsFragment.mainActivity.clearActionM();
//                } else if (position == 1) {
//                    ChatFragment.mainActivity.clearActionM();
//                    CallsFragment.mainActivity.clearActionM();
//                } else {
//                    ChatFragment.mainActivity.clearActionM();
//                    ContactFragment.refresh.clearActionM();
//                }


//                if (position == 0) {
//                  toolbar.getMenu().clear();
//                toolbar.inflateMenu(R.menu.chat_main);
//                    toolbar.setNavigationIcon(null);
//                    counterTextView.setText(R.string.app_name);
//                    try {
//                        new AsyncTask<String, String, String>() {
//                            @Override
//                            protected String doInBackground(String... params) {
//                                ContactFragment.refresh.Refresgfrommain();
//                                CallsFragment.mainActivity.Refreshfrommain();
//                                return "yes";
//                            }
//                        }.execute();
//                    } catch (Exception e) {
//
//                    }
//
//                } else if (position == 1) {
//                   toolbar.getMenu().clear();
//                    toolbar.inflateMenu(R.menu.contact_main);
//                   toolbar.setNavigationIcon(null);
//                    counterTextView.setText(R.string.app_name);
//
//                    try {
//                        new AsyncTask<String, String, String>() {
//                            @Override
//                            protected String doInBackground(String... params) {
//
//                                ChatFragment.mainActivity.Refreshfrommain();
//                                CallsFragment.mainActivity.Refreshfrommain();
//                                return "yes";
//                            }
//                        }.execute();
//                    } catch (Exception e) {
//
//                    }
//
//
//                } else {
//                   toolbar.getMenu().clear();
//                  toolbar.inflateMenu(R.menu.contact_main);
//                   toolbar.setNavigationIcon(null);
//                    counterTextView.setText(R.string.app_name);
//
//                    try {
//                        new AsyncTask<String, String, String>() {
//                            @Override
//                            protected String doInBackground(String... params) {
//                                ChatFragment.mainActivity.Refreshfrommain();
//                                ContactFragment.refresh.Refresgfrommain();
//                                return "yes";
//                            }
//                        }.execute();
//                    } catch (Exception e) {
//
//                    }
//                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.addFragment(new ChatFragment(), getResources().getString(R.string.chats));
        mSectionsPagerAdapter.addFragment(new ContactFragment(), getResources().getString(R.string.contacts));
        mSectionsPagerAdapter.addFragment(new CallsFragment(), getResources().getString(R.string.calls));

        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        //tabLayout.setTabTextColors(getResources().getColor(R.color.nav_bg), getResources().getColor(R.color.colorAccent));
        //tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorAccent));
        tabLayout.setupWithViewPager(mViewPager);

    }


    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public void sendChatFailed(String userID, String msg, String uniqueID, String currentTime, ChatType chatType, String chatRoomType, String groupId, String cName, String cNumber, String shouldSign,String mtitle,String mdesc,String mlogo,String shoprev,String lati,String lngi) {
        Boolean prr;
        AsyncTask<Void, Void, Void> asynckobj = null;

        if (shoprev.equalsIgnoreCase("true"))
        {
            prr=true;
        }
        else {
            prr=false;
        }
        ServiceClasss.Emitters emitters = new ServiceClasss.Emitters(MainActivity.this);
        emitters.sendChat(userID, msg, uniqueID, currentTime, chatType, chatRoomType, groupId, cName, cNumber, shouldSign, mtitle, mdesc, mlogo, prr, asynckobj, Double.parseDouble(lati), Double.parseDouble(lngi));


    }

    public void sendingmessage() {
        DBHandler dbHandler = new DBHandler(this);
        JSONArray messages = dbHandler.GetChatsMessagesUnSent();
        if (messages.length() > 0) {
            for (int i = 0; i < messages.length(); i++) {
                JSONObject object = messages.optJSONObject(i);
                String msg = object.optString("msg");
                String msg_id = object.optString("msgId");
                String user_id = object.optString("userId");
                String time = String.valueOf(System.currentTimeMillis());
                String chatType = object.optString("chatType");

                ChatType type = null;

                if ("text".equalsIgnoreCase(chatType)) {
                    type=ChatType.text;
                } else if ("image".equalsIgnoreCase(chatType)) {
                    type=ChatType.image;
                } else if (ChatType.location.equals(chatType)) {
                    type= ChatType.location;
                } else if (ChatType.contact.equals(chatType)) {
                    type=ChatType.contact;
                } else if (ChatType.video.equals(chatType)) {
                    type=ChatType.video;
                }

                String from = SharedHelper.getKey(getApplicationContext(), "id");
                String grp_id = object.optString("groupId");
                String chatrromtype = object.optString("chatRoomType");
                String showpreview=object.optString("showPreview");
                String title = "",description = "",logo = "",lat="",longg="",c_name="",c_number="";

                if (showpreview.equalsIgnoreCase("true"))
                {
                    title=object.optString("metaTitle");
                    description=object.optString("metaDescription");
                    logo=object.optString("metaLogo");
                }
                if (chatType.equals(ChatType.location)) {
                    lat=object.optString("lat");
                    longg=object.optString("lng");
                } else if (chatType.equals(ChatType.contact)) {
                    c_name=object.optString("cName");
                    c_number=object.optString("cNumber");
                }




                if (i == messages.length()) {
                    isSending = false;
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("run: ", "notifying_adapter");
                            ChatActivity.singleChatAdapter.notifyDataSetChanged();
                        }
                    });
                }
                String upload = object.optString("upload");

                if (upload.equalsIgnoreCase("3")) {

                    String sign = dbHandler.GetSign(groupId_loc);
                    sendChatFailed(user_id,msg, msg_id, time, type, chatrromtype, grp_id,c_name,c_number,sign,title,description,logo,showpreview,lat,longg);
                }
                else {

                    sendChatFailed(user_id,msg, msg_id, time, type, chatrromtype, grp_id,c_name,c_number,"true",title,description,logo,showpreview,lat,longg);
                }

            }
        }
        }
        //imgpath,msgid,userId,currentime,chattype,from,groupid,chatroomtype





    private void ReceiveMessage(String chatType, final String messageId, final String userId, String msg, Status delivered, String sentTime,
                                final String deliveredTime, final String seenTime, int caption, int download, final String to, String lat,
                                String lng, String groupName, String create_by, String sent_by, String groupId, String chatRoomType, String cName, String cNumber, boolean show, String tit, String desc, String log, String shouldSign) {

        //long time = dbHandler.GetLastRow(userId);
        try {
            String times = SharedHelper.getHeader(MainActivity.this, "header_time");
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
                SharedHelper.putHeader(MainActivity.this, "header_time", sentTime);
                addHeaderReceiveMsg(userId, ChatMessages.HEADER, sentTime, String.valueOf(System.currentTimeMillis()), groupId, chatRoomType, shouldSign);

                addDBReceiveMsg(chatType, messageId, userId, msg, delivered, sentTime, deliveredTime, seenTime, caption, download, to, lat, lng, groupName, create_by, sent_by, groupId, chatRoomType, cName, cNumber, to, show, tit, desc, log, shouldSign);

            } else if (chatTime.equalsIgnoreCase("Today")) {
                if (value == 0) {
                    value++;
                    SharedHelper.putInt(MainActivity.this, "date", value);
                }
                SharedHelper.putHeader(MainActivity.this, "header_time", sentTime);
                value = Integer.parseInt(SharedHelper.getInt(MainActivity.this, "date"));
                addDBReceiveMsg(chatType, messageId, userId, msg, delivered, sentTime, deliveredTime, seenTime, caption, download, to, lat, lng, groupName, create_by, sent_by, groupId, chatRoomType, cName, cNumber, to, show, tit, desc, log, shouldSign);
            } else {
                SharedHelper.putHeader(MainActivity.this, "header_time", sentTime);
                addHeaderReceiveMsg(userId, ChatMessages.HEADER, sentTime, String.valueOf(System.currentTimeMillis()), groupId, chatRoomType, shouldSign);
                addDBReceiveMsg(chatType, messageId, userId, msg, delivered, sentTime, deliveredTime, seenTime, caption, download, to, lat, lng, groupName, create_by, sent_by, groupId, chatRoomType, cName, cNumber, to, show, tit, desc, log, shouldSign);
                value = 0;
            }
        } catch (ParseException | NumberFormatException e) {
            e.printStackTrace();
        }

        Log.e("count", String.valueOf(unreadCount));
        sendDelivered(messageId, userId, to, deliveredTime, chatRoomType);
        ChatFragment.mainActivity.refresh();

    }

    private void addHeaderReceiveMsg(String userId, String header, String currentTime, String msgId, String groupId, String chatRoomType, String shouldSign) {
        DBHandler dbHandler = new DBHandler(MainActivity.this);
        dbHandler.InsertChatMsg(new ChatsMessagesModel(msgId, userId, "0", "0", "0", header, groupId, String.valueOf(currentTime), "0", "0", 0, 0, 0, "", "", "", "", chatRoomType, "false", "", "", 0, "", "", "", shouldSign));
        dbHandler.close();
    }

    private void addDBReceiveMsg(String chatType, final String messageId, String userId,
                                 final String msg, final Status delivered, String sentTime, final String deliveredTime,
                                 final String seenTime, int caption, int download, final String to, String lat, String lng, String groupName,
                                 String create_by, String sent_by, String groupId, String chatRoomType, String cName, String cNumber, String s, boolean show, String metatitle, String metadesc, String metalogo, String shouldSign) {
        DBHandler dbHandler = new DBHandler(MainActivity.this);


        String chatTypeLayout = "";


        if (userId.equalsIgnoreCase(SharedHelper.getKey(MainActivity.this, "id"))) {
//            if (chatRoomType.equalsIgnoreCase("0")) {
//                unreadCount = dbHandler.GetUnReadCount(to);
//                unreadCount++;
//            } else {
//                unreadCount = dbHandler.GetUnReadCount(groupId);
//                unreadCount++;
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
                unreadCount = dbHandler.GetUnReadCount(userId);
                unreadCount++;
            } else {
                unreadCount = dbHandler.GetUnReadCount(groupId);
                unreadCount++;
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

        int showPre = (show) ? 1 : 0;
        dbHandler.InsertChatMsg(new ChatsMessagesModel(messageId.trim(), userId, msg,
                chatType, delivered.toString(), chatTypeLayout, groupId,
                sentTime, String.valueOf(deliveredTime), seenTime, caption, 0, download, lat, lng, cName, cNumber, chatRoomType, "false", msg, "", showPre, metatitle, metadesc, metalogo, shouldSign));


        if (!dbHandler.DoesChatsUser(userId)) {
            dbHandler.InsertChats(new ChatsModel(userId, "0", chatTypeLayout, msg, delivered.toString(), deliveredTime, chatType, unreadCount, groupName, create_by, sent_by, "", "0", "0", "", "", "", "", "", "", "false", "1", ""));
        } else {
            if (chatRoomType.equalsIgnoreCase("1") || chatRoomType.equalsIgnoreCase("3")) {
                dbHandler.UpdateLastMsg(groupId, chatTypeLayout, msg, delivered.toString(), deliveredTime, chatType, unreadCount);
            } else {
                dbHandler.UpdateLastMsg(userId, chatTypeLayout, msg, delivered.toString(), deliveredTime, chatType, unreadCount);

            }
        }
        dbHandler.close();
    }

    private void sendDelivered(String messageId, String from, String to, String deliveredTime, String chatRoomType) {
        DBHandler dbHandler = new DBHandler(MainActivity.this);
        dbHandler.UpdateDeliveredTime(from, String.valueOf(deliveredTime));
        dbHandler.close();

        ServiceClasss.Emitters emitters = new ServiceClasss.Emitters(MainActivity.this);
        emitters.sendDelivered(messageId, from, to, deliveredTime, chatRoomType);


    }

    @Override
    protected void onResume() {
        super.onResume();
        isSending = false;
        handler1.post(internetCheck);
        if (!isMyServiceRunning(ServiceClasss.class)) {
            MainActivity.this.startService(new Intent(MainActivity.this, ServiceClasss.class));
        }
        MyCommon.getInstance().mainActivity = true;
        /*if (!SocketSingleton.getInstance().socket.connected()) {
            SocketSingleton.getInstance().onConnectSocket();
            Log.e("Scoket", "Connect");
        } else {
            Log.e("Scoket", "notConnect");
        }*/

        Log.d("onResume: ", "MainActivity");
        ChatFragment.refreshLay();
        ChatActivity.zoeChatID = "";
        ChatActivity.groupId_loc = "";

//        socket.connect();
//        socket.on(Socket.EVENT_CONNECT, onConnect);


    }

    @Override
    protected void onPause() {
        super.onPause();
//        handler1.removeCallbacks(internetCheck);
        isSending = false;
        MyCommon.getInstance().mainActivity = false;
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("id", SharedHelper.getKey(getApplicationContext(), "id"));
//            socket.emit("offline", jsonObject);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        socket.disconnect();
    }


    @Override
    public void onBackPressed() {


        if (ChatsAdapter.is_in_action_mode) {
            ChatFragment.mainActivity.clearActionM();
            CallsFragment.mainActivity.clearActionM();

        } else {
            super.onBackPressed();
        }
    }

    private void validateincomingvalues(JSONArray particpants_array) {
        DBHandler dbHandler = new DBHandler(MainActivity.this);
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


    private void cvalidateincomingvalues(JSONArray particpants_array) {
        DBHandler dbHandler = new DBHandler(MainActivity.this);
        for (int i = 0; i < particpants_array.length(); i++) {
            String id = particpants_array.optJSONObject(i).optString("participantId");
            String grp_id = particpants_array.optJSONObject(i).optString("groupId");
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

    private boolean value(JSONArray jsonArray, String part_id) {
        return jsonArray.toString().contains("\"participantId\":\"" + part_id + "\"");
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);

        }

        public void addFragment(Fragment fragment, String title) {
            FragmentList.add(fragment);
            FragmentTitle.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return FragmentList.get(position);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return FragmentTitle.get(position);
        }
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

                new PostHelper(Const.Methods.PARTICIPANTS_DETAILS, jsonObject.toString(), Const.ServiceCode.PARTICIPANTS_DETAILS, MainActivity.this, this);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }


        @Override
        public void onTaskCompleted(JSONObject response, int serviceCode) {

            DBHandler dbHandler = new DBHandler(MainActivity.this);
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

                new PostHelper(Const.Methods.CHANNEL_PARTICIPANTS_DETAILS, jsonObject.toString(), Const.ServiceCode.CHANNEL_PARTICIPANTS_DETAILS, MainActivity.this, this);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }


        @Override
        public void onTaskCompleted(JSONObject response, int serviceCode) {

            DBHandler dbHandler = new DBHandler(MainActivity.this);
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


}
