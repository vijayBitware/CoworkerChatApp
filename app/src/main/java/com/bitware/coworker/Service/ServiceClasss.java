package com.bitware.coworker.Service;

/**
 * Created by user on 25-07-2017.
 */

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bitware.coworker.DBHelper.DBHandler;
import com.bitware.coworker.FCM.FCMMsgService;
import com.bitware.coworker.R;
import com.bitware.coworker.activity.AddAdminChannel;
import com.bitware.coworker.activity.ChangeGrpName;
import com.bitware.coworker.activity.ChannelEdit;
import com.bitware.coworker.activity.ChannelSetings;
import com.bitware.coworker.activity.ChatActivity;
import com.bitware.coworker.activity.MainActivity;
import com.bitware.coworker.activity.NewGroup_activity;
import com.bitware.coworker.activity.SetGroupName_activity;
import com.bitware.coworker.activity.UserDetails;
import com.bitware.coworker.baseUtils.AsyncTaskCompleteListener;
import com.bitware.coworker.baseUtils.Const;
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
import com.bitware.coworker.models.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.bitware.coworker.models.Status.SENDING;

public class ServiceClasss extends Service {
    public static int value = 0;
    public static int unreadCount = 0;
    public static Socket socket;
    public static String current_id = "";
    public static PowerManager.WakeLock wl;
    Handler handler = new Handler();
    Boolean UpdatedOff = false;
    private String my_id;
    private String TAG = ServiceClasss.class.getSimpleName();
    private String groupId_loc;
    private MediaPlayer mPlayer;
    private Handler handleronline = new Handler();
    private Handler handleroffline = new Handler();
    private boolean Updatedon = false;
    private Runnable statusCheck = new Runnable() {
        @Override
        public void run() {
            Boolean isBacgkround = isAppIsInBackground(ServiceClasss.this);
            if (isBacgkround) {
                if (!UpdatedOff) {
                    Emitters emitters = new Emitters(ServiceClasss.this);
                    emitters.emitoffline();
                    UpdatedOff = true;
                    Updatedon = false;
                }
            } else {
                if (!Updatedon) {
                    Emitters emitters = new Emitters(ServiceClasss.this);
                    emitters.emitonline();
                    UpdatedOff = false;
                    Updatedon = true;
                }
            }
            handleroffline.postDelayed(this, 1000);
        }

    };


    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            handler.post(statusCheck);

//            Boolean isBackground = isAppIsInBackground(ServiceClasss.this);
//            if (!isBackground) {
//                if (!isConnected) {
//                    Emitters emitters = new Emitters(ServiceClasss.this);
//                    emitters.emitonline();
//                    isConnected=true;
//
//                }
//            }
//            handleroffline.post(statusCheck);


        }

    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    @CallSuper
    public void onCreate() {
        super.onCreate();


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Log.d("onStartCommand: ", "ServiceRunning SUccessfully");

        my_id = SharedHelper.getKey(ServiceClasss.this, "id");


        IO.Options opts = new IO.Options();
        opts.forceNew = true;
        opts.reconnection = true;
        try {
            socket = IO.socket(Const.chatSocketURL, opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.e("SOCKET.IO ", e.getMessage());
        }

//        socket= AppController.getSocket();
        socket.connect();
        try {
            socket.on(Socket.EVENT_CONNECT, onConnect);
//            socket.on(Socket.EVENT_RECONNECT, reonConnect);
        } catch (Exception e) {

        }
        Log.d("onStartCommand: ", "asdasdasd:" + SharedHelper.getKey(ServiceClasss.this, "id"));


//        socket.on("heartbeat", new Emitter.Listener() {
//            @Override
//            public void call(final Object... args) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        JSONObject data = (JSONObject) args[0];
//                        String ping;
//                        Log.e("SOCKETPING", "RECEIVED! "+data);
//
//                        try {
//                            ping = data.getString("ping");
//                        } catch (JSONException e) {
//                            return;
//                        }
//                        if(ping.equals("1")){
//                           socket.emit("pong", "pong");
//                        }
//                        Log.e("SOCKETPING", "RECEIVED PING! ");
//                    }
//                });
//            }
//        });

        socket.on(SharedHelper.getKey(ServiceClasss.this, "id") + ":receiveMessage", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(
                        new Runnable() {
                            @Override
                            public void run() {

                                JSONObject data = (JSONObject) args[0];
                                Log.e("Main_receive_data", data.toString());
                                Log.d("main_active", "true");
//                                try {
//                                    Emitters emitters=new Emitters(ServiceClasss.this);
//                                    emitters.sendack(data.optString("uniqueid"));
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
                                long deliveredTime = System.currentTimeMillis();
                                String groupName = "", create_by = "", sent_by = "";

                                if (data.optString("chatRoomType").equalsIgnoreCase("1") || data.optString("chatRoomType").equalsIgnoreCase("3")) {
                                    sent_by = data.optString("from");
                                }

                                DBHandler dbHandler = new DBHandler(ServiceClasss.this);
                                if (data.optString("chatRoomType").equalsIgnoreCase("1")) {
                                    String isDeleted = dbHandler.getDeleteStatus(data.optString("groupId"));
                                    if (isDeleted.equalsIgnoreCase("1")) {
                                        dbHandler.GroupDeleteUpdate("0", data.optString("groupId"));
                                    }
                                } else {
                                    String isDeleted = dbHandler.getDeleteStatus(data.optString("from"));
                                    if (isDeleted.equalsIgnoreCase("1")) {
                                        dbHandler.GroupDeleteUpdate("0", data.optString("from"));
                                    }

                                }
                                boolean show = data.optBoolean("showPreview");
                                String tit, desc, log;
                                if (show) {
                                    tit = data.optString("metaTitle");
                                    desc = data.optString("metaDescription");
                                    log = data.optString("metaLogo");
                                } else {
                                    tit = "";
                                    desc = "";
                                    log = "";
                                }
                                if (data.optString("chatRoomType").equalsIgnoreCase("3")) {


                                    ReceiveMessage(data.optString("contentType"), data.optString("messageId"), data.optString("from"), data.optString("content"),
                                            Status.DELIVERED, data.optString("time"), String.valueOf(deliveredTime), "0", 0, 0, data.optString("to"), data.optString("latitude"),
                                            data.optString("longitude"), groupName, create_by, sent_by, data.optString("roomId"), data.optString("chatRoomType"), data.optString("contactName"), data.optString("contactNumber"), show, tit, desc, log, data.optString("shouldSign"));

                                } else {
                                    ReceiveMessage(data.optString("contentType"), data.optString("messageId"), data.optString("from"), data.optString("content"),
                                            Status.DELIVERED, data.optString("time"), String.valueOf(deliveredTime), "0", 0, 0, data.optString("to"), data.optString("latitude"),
                                            data.optString("longitude"), groupName, create_by, sent_by, data.optString("groupId"), data.optString("chatRoomType"), data.optString("contactName"), data.optString("contactNumber"), show, tit, desc, log, "true");

                                }


//                                Toast.makeText(ServiceClasss.this, "Receved Message", Toast.LENGTH_SHORT).show();
                            }
                        }
                );


            }

        });


        socket.on(SharedHelper.getKey(ServiceClasss.this, "id") + ":typing", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                Log.e("Listen", "typing...");


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = (JSONObject) args[0];
                        Log.e("Listen_jsonObject", "typing..." + jsonObject);
                        if (jsonObject.optString("status").equalsIgnoreCase("start")) {
                            if (ChatActivity.zoeChatID.equals(jsonObject.optString("from"))) {
                                ChatActivity.sub_user_title.setText(getResources().getString(R.string.typing));
                            }
                        } else if (jsonObject.optString("status").equalsIgnoreCase("stop")) {
                            try {
                                JSONObject jsonObject1 = new JSONObject();
                                jsonObject1.put("id", ChatActivity.zoeChatID);
                                jsonObject1.put("from", MainActivity.my_id);
                                socket.emit("getOnline", jsonObject1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });


            }
        });

        socket.on(SharedHelper.getKey(ServiceClasss.this, "id") + ":onlineStatus", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                Log.e("Listen", "online_status..." + args[0]);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
//                            try {
//                                Emitters emitters=new Emitters(ServiceClasss.this);
//                                emitters.sendack(data.optString("uniqueid"));
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
                        if (data.optString("status").equalsIgnoreCase("1")) {
                            ChatActivity.sub_user_title.setText(getResources().getString(R.string.online));
                        } else {
                            try {
                                double time = Double.parseDouble(data.optString("lastSeen"));
                                String date = Utils.getDate((long) time, "dd/MM/yyyy");
                                String chat_time = Utils.getDate((long) time, "hh:mm a");
                                String sortDate = Utils.formatToYesterdayOrToday(date);
                                ChatActivity.sub_user_title.setText(sortDate + " " + chat_time);
                            } catch (ParseException | NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });


            }
        });


        socket.on(SharedHelper.getKey(ServiceClasss.this, "id") + ":ack", new Emitter.Listener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void call(final Object... args) {

                DBHandler dbHandler = new DBHandler(ServiceClasss.this);
                Log.e("main_active", "true");
                JSONObject data = (JSONObject) args[0];
                Log.e("main_ack", data.toString());

                final String id = data.optString("messageid");
                final String status = data.optString("status");
                final String msg = data.optString("content");
                final String to = data.optString("to");
                if (status.equalsIgnoreCase("sent")) {
                    dbHandler.UpdateContentStatus(id, status);
                    dbHandler.UpdateLastMsg_Status(to, status);
                    dbHandler.close();
                } else if (status.equalsIgnoreCase("delivered")) {
                    try {
                        Emitters emitters = new Emitters(ServiceClasss.this);
                        emitters.sendack(data.optString("uniqueid"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dbHandler.UpdateContentStatus(id, status);
                    dbHandler.UpdateLastMsg_Status(to, status);
                    dbHandler.close();
                } else if (status.equalsIgnoreCase("read")) {
                    try {
                        Emitters emitters = new Emitters(ServiceClasss.this);
                        emitters.sendack(data.optString("uniqueid"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dbHandler.UpdateContentStatus(id, status);
                    dbHandler.UpdateLastMsg_Status(to, status);
                    dbHandler.close();
                }
//                Log.e("chatActivity_ack", data.toString());
                final String message_id = data.optString("messageid");
                if (status.equalsIgnoreCase("sent")) {
                    dbHandler.UpdateContentStatus(id, status);
                    dbHandler.UpdateLastMsg_Status(to, status);
                    if (Const.URI.inMessageTone) {
                        mPlayer = MediaPlayer.create(ServiceClasss.this, R.raw.receive_msg_tone);
                        mPlayer.start();
                    }
                } else if (status.equalsIgnoreCase("delivered")) {
                    JSONObject object = new JSONObject();
                    String mydate = "";
                    try {
                        dbHandler.UpdateContentStatus(id, status);
                        dbHandler.UpdateLastMsg_Status(to, status);
                        mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                        dbHandler.UpdateDeliveredTime(id, mydate);

                    } catch (Exception e) {

                    }
                    Log.d("deleivered_chat", mydate);
                } else if (status.equalsIgnoreCase("read")) {
                    String mydate = "";
                    try {
                        dbHandler.UpdateContentStatus(id, status);
                        dbHandler.UpdateLastMsg_Status(to, status);
                        mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                        dbHandler.UpdateSeenMsgTime(id, mydate);

                    } catch (Exception e) {
                    }
                    Log.d("read_chat", mydate);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int i = 0; i < ChatActivity.jsonArray.length(); i++) {
                                String msgId = ChatActivity.jsonArray.optJSONObject(i).optString("msgId");
                                if (msgId.equalsIgnoreCase(id)) {
                                    try {
                                        ChatActivity.jsonArray.optJSONObject(i).remove("contentStatus");
                                        ChatActivity.jsonArray.optJSONObject(i).put("contentStatus", status);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                }
                            }
                            ChatActivity.refresh();
                        } catch (Exception e) {
                        }
                    }
                });
            }
        });


        socket.on(SharedHelper.getKey(ServiceClasss.this, "id") + ":group", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                Log.e("Listen", "typing...");


                ServiceClasss.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        JSONObject jsonObject = (JSONObject) args[0];
                        Log.e("Listen_create_group", "typing..." + jsonObject);
                        DBHandler dbHandler = new DBHandler(ServiceClasss.this);
                        String uniqueID = UUID.randomUUID().toString();
                        try {
                            Emitters emitters = new Emitters(ServiceClasss.this);
                            emitters.sendack(jsonObject.optString("uniqueid"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("contenttype: ", "group:" + jsonObject.optString("contentType"));

                        if (SharedHelper.getKey(ServiceClasss.this, "single_chat_enable").equalsIgnoreCase("yes")) {

                            if (Const.URI.inMessageTone) {

                                if (!isAppIsInBackground(ServiceClasss.this)) {
                                    mPlayer = MediaPlayer.create(ServiceClasss.this, R.raw.send_audio);
                                    mPlayer.start();
                                }
                            }
                        }

                        if (jsonObject.optString("contentType").equalsIgnoreCase("GroupAdd")) {

//                                dbHandler.AddGroupParticipants(new GroupParticiapntsModel(jsonObject.optString("participantId"),jsonObject.optString("groupId"),jsonObject.optString("time"),jsonObject.optString("addedBy"),"0"));

                            groupId_loc = jsonObject.optString("groupId");

                            new getGrpdetails().execute();
                            dbHandler.InsertChats(new ChatsModel(jsonObject.optString("groupId"), "1", ChatMessages.GROUP,
                                    jsonObject.optString("content"), Status.SENDING.toString(), jsonObject.optString("time"),
                                    jsonObject.optString("contentType"), 0, jsonObject.optString("groupName"), jsonObject.optString("createdBy"), "", jsonObject.optString("groupImage"), "0", "0", "", "", "", "", "", "", "true", "1", ""));

                            if (jsonObject.optString("createdBy").equalsIgnoreCase(SharedHelper.getKey(ServiceClasss.this, "id"))) {
                                String msg = "You created group " + jsonObject.optString("groupName");
                                dbHandler.InsertChatMsg(new ChatsMessagesModel(uniqueID.trim(), SharedHelper.getKey(ServiceClasss.this, "id"), msg, ChatType.createGroup.toString(), Status.SENDING.toString(), ChatMessages.CREATE_GROUP, jsonObject.optString("groupId"), jsonObject.optString("time"), "0", "0", 0, 0, 0, "0.0", "0.0", "", "", "1", "false", "", "", 0, "", "", "", ""));
                                dbHandler.UpdateLastMsg(jsonObject.optString("groupId"), jsonObject.optString("from"), msg, "", jsonObject.optString("time"), ChatMessages.CREATE_GROUP, unreadCount);

                            } else {
                                String msg = dbHandler.GetUserName(jsonObject.optString("createdBy")) + " created group " + jsonObject.optString("groupName");
                                dbHandler.InsertChatMsg(new ChatsMessagesModel(uniqueID.trim(), SharedHelper.getKey(ServiceClasss.this, "id"), msg, ChatType.createGroup.toString(), Status.SENDING.toString(), ChatMessages.CREATE_GROUP, jsonObject.optString("groupId"), jsonObject.optString("time"), "0", "0", 0, 0, 0, "0.0", "0.0", "", "", "1", "false", "", "", 0, "", "", "", ""));
                                dbHandler.UpdateLastMsg(jsonObject.optString("groupId"), jsonObject.optString("from"), msg, "", jsonObject.optString("time"), ChatMessages.CREATE_GROUP, unreadCount);

                            }

                            try {
                                ChatFragment.refreshLay();
                                ChatActivity.singleChatAdapter.notifyDataSetChanged();
                                ChatFragment.mainActivity.refresh();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        } else if (jsonObject.optString("contentType").equalsIgnoreCase("GroupNameEdit")) {
//                                try {
                            try {

                                ChatActivity.user_title.setText(jsonObject.optString("groupName"));
                            } catch (Exception e) {

                            }

                            String msg = "";
                            if (jsonObject.optString("from").equalsIgnoreCase(SharedHelper.getKey(ServiceClasss.this, "id"))) {
                                msg = "You changed group Name from " + jsonObject.optString("oldName") + " to " + jsonObject.optString("groupName");
                                dbHandler.InsertChatMsg(new ChatsMessagesModel(uniqueID.trim(), SharedHelper.getKey(ServiceClasss.this, "id"), msg, ChatType.editGroupName.toString(), Status.SENDING.toString(), ChatMessages.CREATE_GROUP, jsonObject.optString("groupId"), jsonObject.optString("time"), "0", "0", 0, 0, 0, "0.0", "0.0", "", "", "1", "false", "", "", 0, "", "", "", ""));
                                dbHandler.UpdateLastMsg(jsonObject.optString("groupId"), jsonObject.optString("from"), msg, "", jsonObject.optString("time"), ChatMessages.CREATE_GROUP, unreadCount);
                                dbHandler.GroupNameUpdate(jsonObject.optString("groupName"), jsonObject.optString("groupId"));

                                ChatFragment.refreshLay();
                                try {
                                    ChatFragment.refreshLay();
                                    ChatActivity.singleChatAdapter.notifyDataSetChanged();
                                    ChatFragment.mainActivity.refresh();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                msg = dbHandler.GetUserName(jsonObject.optString("from")) + " changed group Name from " + jsonObject.optString("oldName") + " to " + jsonObject.optString("groupName");
                                dbHandler.InsertChatMsg(new ChatsMessagesModel(uniqueID.trim(), SharedHelper.getKey(ServiceClasss.this, "id"), msg, ChatType.editGroupName.toString(), Status.SENDING.toString(), ChatMessages.CREATE_GROUP, jsonObject.optString("groupId"), jsonObject.optString("time"), "0", "0", 0, 0, 0, "0.0", "0.0", "", "", "1", "false", "", "", 0, "", "", "", ""));
                                dbHandler.UpdateLastMsg(jsonObject.optString("groupId"), jsonObject.optString("from"), msg, "", jsonObject.optString("time"), ChatMessages.CREATE_GROUP, unreadCount);
                                dbHandler.GroupNameUpdate(jsonObject.optString("groupName"), jsonObject.optString("groupId"));

                                try {
                                    ChatFragment.refreshLay();
                                    ChatActivity.singleChatAdapter.notifyDataSetChanged();
                                    ChatFragment.mainActivity.refresh();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                            addHeaderReceiveMsgs(jsonObject.optString("from"), ChatMessages.HEADER, jsonObject.optString("time"), String.valueOf(System.currentTimeMillis()), jsonObject.optString("groupId"), "1", msg);


                        } else if (jsonObject.optString("contentType").equalsIgnoreCase("GroupImageEdit")) {
                            String msg = "";
                            if (jsonObject.optString("from").equalsIgnoreCase(SharedHelper.getKey(ServiceClasss.this, "id"))) {
                                msg = "You changed group Icon ";
                                dbHandler.InsertChatMsg(new ChatsMessagesModel(uniqueID.trim(), SharedHelper.getKey(ServiceClasss.this, "id"), msg, ChatType.editGroupImage.toString(), Status.SENDING.toString(), ChatMessages.CREATE_GROUP, jsonObject.optString("groupId"), jsonObject.optString("time"), "0", "0", 0, 0, 0, "0.0", "0.0", "", "", "1", "false", "", "", 0, "", "", "0", ""));
                                dbHandler.UpdateLastMsg(jsonObject.optString("groupId"), jsonObject.optString("from"), msg, "", jsonObject.optString("time"), ChatMessages.CREATE_GROUP, unreadCount);
                                dbHandler.GrpImageUpdate(jsonObject.optString("groupImage"), jsonObject.optString("groupId"));
                                addHeaderReceiveMsgs(jsonObject.optString("addedBy"), ChatMessages.HEADER, jsonObject.optString("time"), String.valueOf(System.currentTimeMillis()), jsonObject.optString("groupId"), "1", msg);

                                try {
                                    ChatFragment.refreshLay();
                                    ChatActivity.singleChatAdapter.notifyDataSetChanged();
                                    ChatFragment.mainActivity.refresh();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else {
                                msg = dbHandler.GetUserName(jsonObject.optString("from")) + " changed group Icon";
                                dbHandler.InsertChatMsg(new ChatsMessagesModel(uniqueID.trim(), SharedHelper.getKey(ServiceClasss.this, "id"), msg, ChatType.editGroupImage.toString(), Status.SENDING.toString(), ChatMessages.CREATE_GROUP, jsonObject.optString("groupId"), jsonObject.optString("time"), "0", "0", 0, 0, 0, "0.0", "0.0", "", "", "1", "false", "", "", 0, "", "", "", ""));
                                dbHandler.UpdateLastMsg(jsonObject.optString("groupId"), jsonObject.optString("from"), msg, "", jsonObject.optString("time"), ChatMessages.CREATE_GROUP, unreadCount);
                                dbHandler.GrpImageUpdate(jsonObject.optString("groupImage"), jsonObject.optString("groupId"));
                                addHeaderReceiveMsgs(jsonObject.optString("addedBy"), ChatMessages.HEADER, jsonObject.optString("time"), String.valueOf(System.currentTimeMillis()), jsonObject.optString("groupId"), "1", msg);

                                try {
                                    ChatFragment.refreshLay();
                                    ChatActivity.singleChatAdapter.notifyDataSetChanged();
                                    ChatFragment.mainActivity.refresh();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            addHeaderReceiveMsgs(jsonObject.optString("from"), ChatMessages.HEADER, jsonObject.optString("time"), String.valueOf(System.currentTimeMillis()), jsonObject.optString("groupId"), "1", msg);

                        } else if (jsonObject.optString("contentType").equalsIgnoreCase("GroupExit")) {
                            groupId_loc = jsonObject.optString("groupId");
                            new getGrpdetails().execute();
                            dbHandler.DeleteGroupParticipants(jsonObject.optString("from"), jsonObject.optString("groupId"));
                            String msg = dbHandler.GetUserName(jsonObject.optString("from")) + " left";
                            dbHandler.InsertChatMsg(new ChatsMessagesModel(uniqueID.trim(), SharedHelper.getKey(ServiceClasss.this, "id"), msg, ChatType.exitGroup.toString(), Status.SENDING.toString(), ChatMessages.CREATE_GROUP, jsonObject.optString("groupId"), jsonObject.optString("time"), "0", "0", 0, 0, 0, "0.0", "0.0", "", "", "1", "false", "", "", 0, "", "", "", ""));
                            dbHandler.UpdateLastMsg(jsonObject.optString("groupId"), jsonObject.optString("from"), msg, "", jsonObject.optString("time"), ChatMessages.HEADER, unreadCount);
                            addHeaderReceiveMsgs(jsonObject.optString("addedBy"), ChatMessages.HEADER, jsonObject.optString("time"), String.valueOf(System.currentTimeMillis()), jsonObject.optString("groupId"), "1", msg);

                            try {
                                ChatFragment.refreshLay();
                                ChatActivity.singleChatAdapter.notifyDataSetChanged();
                                ChatFragment.mainActivity.refresh();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            addHeaderReceiveMsgs(jsonObject.optString("from"), ChatMessages.HEADER, jsonObject.optString("time"), String.valueOf(System.currentTimeMillis()), jsonObject.optString("groupId"), "1", msg);

// }
                        } else if ((jsonObject.optString("contentType")).equalsIgnoreCase("PartAdd")) {
                            groupId_loc = jsonObject.optString("groupId");
                            new getGrpdetails().execute();
                            if (jsonObject.optString("addedBy").equalsIgnoreCase(SharedHelper.getKey(ServiceClasss.this, "id"))) {
                                dbHandler.AddGroupParticipants(new GroupParticiapntsModel(jsonObject.optString("participantId"), jsonObject.optString("groupId"), jsonObject.optString("time"), jsonObject.optString("addedBy"), "0"));

                                String username = dbHandler.GetUserName(jsonObject.optString("participantId"));
                                String msg = "You added " + username;
                                dbHandler.InsertChatMsg(new ChatsMessagesModel(uniqueID.trim(), SharedHelper.getKey(ServiceClasss.this, "id"), msg, ChatType.editGroupImage.toString(), Status.SENDING.toString(), ChatMessages.CREATE_GROUP, jsonObject.optString("groupId"), jsonObject.optString("time"), "0", "0", 0, 0, 0, "0.0", "0.0", "", "", "1", "false", "", "", 0, "", "", "", ""));
                                dbHandler.UpdateLastMsg(jsonObject.optString("groupId"), jsonObject.optString("from"), msg, "", jsonObject.optString("time"), ChatMessages.CREATE_GROUP, unreadCount);
                                dbHandler.GrpImageUpdate(jsonObject.optString("groupImage"), jsonObject.optString("groupId"));

                                addHeaderReceiveMsgs(jsonObject.optString("addedBy"), ChatMessages.HEADER, jsonObject.optString("time"), String.valueOf(System.currentTimeMillis()), jsonObject.optString("groupId"), "1", msg);

                                try {
                                    ChatFragment.refreshLay();
                                    ChatActivity.singleChatAdapter.notifyDataSetChanged();
                                    ChatFragment.mainActivity.refresh();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else {
                                String username = dbHandler.GetUserName(jsonObject.optString("participantId"));
                                String msg = dbHandler.GetUserName(jsonObject.optString("addedBy")) + " added " + username;
                                dbHandler.InsertChatMsg(new ChatsMessagesModel(uniqueID.trim(), SharedHelper.getKey(ServiceClasss.this, "id"), msg, ChatType.editGroupImage.toString(), Status.SENDING.toString(), ChatMessages.CREATE_GROUP, jsonObject.optString("groupId"), jsonObject.optString("time"), "0", "0", 0, 0, 0, "0.0", "0.0", "", "", "1", "false", "", "", 0, "", "", "", ""));
                                dbHandler.UpdateLastMsg(jsonObject.optString("groupId"), jsonObject.optString("from"), msg, "", jsonObject.optString("time"), ChatMessages.CREATE_GROUP, unreadCount);
                                dbHandler.GrpImageUpdate(jsonObject.optString("groupImage"), jsonObject.optString("groupId"));
                                addHeaderReceiveMsgs(jsonObject.optString("addedBy"), ChatMessages.HEADER, jsonObject.optString("time"), String.valueOf(System.currentTimeMillis()), jsonObject.optString("groupId"), "1", msg);

                                try {
                                    ChatFragment.refreshLay();
                                    ChatActivity.singleChatAdapter.notifyDataSetChanged();
                                    ChatFragment.mainActivity.refresh();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                        } else if ((jsonObject.optString("contentType")).equalsIgnoreCase("GroupRemoveParticipant")) {
                            if (jsonObject.optString("removedId").equalsIgnoreCase(SharedHelper.getKey(ServiceClasss.this, "id"))) {
                                String grpId = jsonObject.optString("groupId");
                                dbHandler.DeleteChatsGrpComplete(grpId);
                                dbHandler.DeleteGrpMsg(grpId);
                                dbHandler.DeleteChats(grpId);
                                try {
                                    ChatFragment.refreshLay();
                                    ChatActivity.singleChatAdapter.notifyDataSetChanged();
                                    ChatFragment.mainActivity.refresh();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (jsonObject.optString("from").equalsIgnoreCase(SharedHelper.getKey(ServiceClasss.this, "id"))) {
                                    dbHandler.AddGroupParticipants(new GroupParticiapntsModel(jsonObject.optString("participantId"), jsonObject.optString("groupId"), jsonObject.optString("time"), jsonObject.optString("addedBy"), "0"));

                                    String username = dbHandler.GetUserName(jsonObject.optString("removedId"));
                                    String msg = "You removed " + username;
                                    dbHandler.InsertChatMsg(new ChatsMessagesModel(uniqueID.trim(), SharedHelper.getKey(ServiceClasss.this, "id"), msg, ChatType.editGroupImage.toString(), Status.SENDING.toString(), ChatMessages.CREATE_GROUP, jsonObject.optString("groupId"), jsonObject.optString("time"), "0", "0", 0, 0, 0, "0.0", "0.0", "", "", "1", "false", "", "", 0, "", "", "", ""));
                                    dbHandler.UpdateLastMsg(jsonObject.optString("groupId"), jsonObject.optString("from"), msg, "", jsonObject.optString("time"), ChatMessages.CREATE_GROUP, unreadCount);
                                    dbHandler.PartiDelete(jsonObject.optString("removedId"), jsonObject.optString("groupId"));
                                    addHeaderReceiveMsgs(jsonObject.optString("from"), ChatMessages.HEADER, jsonObject.optString("time"), String.valueOf(System.currentTimeMillis()), jsonObject.optString("groupId"), "1", msg);

                                    try {
                                        ChatFragment.refreshLay();
                                        ChatActivity.singleChatAdapter.notifyDataSetChanged();
                                        ChatFragment.mainActivity.refresh();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    String username = dbHandler.GetUserName(jsonObject.optString("removedId"));
                                    String msg = dbHandler.GetUserName(jsonObject.optString("from")) + " removed " + username;
                                    dbHandler.InsertChatMsg(new ChatsMessagesModel(uniqueID.trim(), SharedHelper.getKey(ServiceClasss.this, "id"), msg, ChatType.editGroupImage.toString(), Status.SENDING.toString(), ChatMessages.CREATE_GROUP, jsonObject.optString("groupId"), jsonObject.optString("time"), "0", "0", 0, 0, 0, "0.0", "0.0", "", "", "1", "false", "", "", 0, "", "", "", ""));
                                    dbHandler.UpdateLastMsg(jsonObject.optString("groupId"), jsonObject.optString("from"), msg, "", jsonObject.optString("time"), ChatMessages.CREATE_GROUP, unreadCount);
                                    dbHandler.PartiDelete(jsonObject.optString("removedId"), jsonObject.optString("groupId"));
                                    addHeaderReceiveMsgs(jsonObject.optString("from"), ChatMessages.HEADER, jsonObject.optString("time"), String.valueOf(System.currentTimeMillis()), jsonObject.optString("groupId"), "1", msg);

                                    try {
                                        ChatFragment.refreshLay();
                                        ChatActivity.singleChatAdapter.notifyDataSetChanged();
                                        ChatFragment.mainActivity.refresh();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }


                        }
                        dbHandler.close();
                        ChatFragment.mainActivity.refresh();

                    }
                });

            }
        });


        socket.on(SharedHelper.getKey(ServiceClasss.this, "id") + ":channel", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                JSONObject jsonObject = (JSONObject) args[0];
                Log.e("Listen_create_channel", "ChannelSettings" + jsonObject);
                DBHandler dbHandler = new DBHandler(ServiceClasss.this);
                try {
                    Emitters emitters = new Emitters(ServiceClasss.this);
                    emitters.sendack(jsonObject.optString("uniqueid"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("contenttype: ", "group:" + jsonObject.optString("contentType"));
                if (jsonObject.optString("contentType").equalsIgnoreCase("ChannelEdit")) {
                    String group_id = jsonObject.optString("channelId");
                    String time = jsonObject.optString("time");

                    try {
                        JSONObject ed_obj = new JSONObject(jsonObject.optString("editedFields"));
                        String image = ed_obj.optString("channelImage");
                        String name = ed_obj.optString("channelName");
                        String descrip = ed_obj.optString("channelDescription");
                        String las_msg = "";


                        if (!image.equalsIgnoreCase("false")) {
                            dbHandler.GrpImageUpdate(image, group_id);
                            las_msg = "Channel Image Changed";
                            String uniqueID = UUID.randomUUID().toString();
                            dbHandler.InsertChatMsg(new ChatsMessagesModel(uniqueID.trim(), SharedHelper.getKey(ServiceClasss.this, "id"), las_msg, ChatType.editGroupName.toString(), SENDING.toString(), ChatMessages.CREATE_GROUP, group_id, "" + time, "0", "0", 0, 0, 0, "0.0", "0.0", "", "", "1", "false", "", "", 0, "", "", "", ""));

                        }
                        if (!name.equalsIgnoreCase("false")) {
                            dbHandler.GroupNameUpdate(name, group_id);
                            las_msg = "Channel Name Changed to " + name;
                            String uniqueID = UUID.randomUUID().toString();
                            dbHandler.InsertChatMsg(new ChatsMessagesModel(uniqueID.trim(), SharedHelper.getKey(ServiceClasss.this, "id"), las_msg, ChatType.editGroupName.toString(), SENDING.toString(), ChatMessages.CREATE_GROUP, group_id, "" + time, "0", "0", 0, 0, 0, "0.0", "0.0", "", "", "1", "false", "", "", 0, "", "", "", ""));

                        }
                        if (!descrip.equalsIgnoreCase("false")) {
                            dbHandler.GroupDescUpdate(descrip, group_id);
                            las_msg = "Channel Description Changed " + descrip;
                            String uniqueID = UUID.randomUUID().toString();
                            dbHandler.InsertChatMsg(new ChatsMessagesModel(uniqueID.trim(), SharedHelper.getKey(ServiceClasss.this, "id"), las_msg, ChatType.editGroupName.toString(), SENDING.toString(), ChatMessages.CREATE_GROUP, group_id, "" + time, "0", "0", 0, 0, 0, "0.0", "0.0", "", "", "1", "false", "", "", 0, "", "", "", ""));


                        }
                        dbHandler.GroupSignUpdate(jsonObject.optString("signAdmin"), group_id);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else if (jsonObject.optString("contentType").equalsIgnoreCase("ChannelAdd")) {
                    String uniqueID = UUID.randomUUID().toString();
                    String msg = "Channel Created";
                    groupId_loc = jsonObject.optString("channelId");
                    new getChanneldetails().execute();
                    dbHandler.InsertChats(new ChatsModel(jsonObject.optString("channelId"), "3", ChatMessages.GROUP,
                            jsonObject.optString("content"), Status.SENDING.toString(), jsonObject.optString("time"),
                            jsonObject.optString("contentType"), 0, jsonObject.optString("channelName"), jsonObject.optString("createdBy"), "", jsonObject.optString("channelImage"), "0", "0", "", "", "", "", jsonObject.optString("channelType"), jsonObject.optString("channelDescription"), "false", "1", ""));
                    dbHandler.InsertChatMsg(new ChatsMessagesModel(uniqueID.trim(), SharedHelper.getKey(ServiceClasss.this, "id"), msg, ChatType.createGroup.toString(), Status.SENDING.toString(), ChatMessages.CREATE_GROUP, jsonObject.optString("channelId"), jsonObject.optString("time"), "0", "0", 0, 0, 0, "0.0", "0.0", "", "", "1", "false", "", "", 0, "", "", "", ""));
                    dbHandler.UpdateLastMsg(jsonObject.optString("groupId"), jsonObject.optString("from"), msg, "", jsonObject.optString("time"), ChatMessages.CREATE_GROUP, unreadCount);
                } else if (jsonObject.optString("contentType").equalsIgnoreCase("ChannelAddAdmin")) {
                    dbHandler.channelPartiUpdateOnly(jsonObject.optString("channelId"), "admin", my_id);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ChatActivity.member_layout.setVisibility(View.GONE);

                        }
                    });

                }
                ChatFragment.mainActivity.refresh();

            }
        });


        return START_STICKY;
    }

    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
                for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (String activeProcess : processInfo.pkgList) {
                            if (activeProcess.equals(context.getPackageName())) {
                                isInBackground = false;
                            }
                        }
                    }
                }
            } else {
                List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
                ComponentName componentInfo = taskInfo.get(0).topActivity;
                if (componentInfo.getPackageName().equals(context.getPackageName())) {
                    isInBackground = false;
                }
            }
        } catch (Exception e) {

        }

        if(isInBackground){
            FCMMsgService.current_id = "";
        }

//        Log.d(TAG, "isAppIsInBackground: " + isInBackground);
        return isInBackground;

    }

    private void addHeaderReceiveMsgs(String userId, String header, String currentTime, String time, String groupId, String chatRoomType, String msg) {

        DBHandler dbHandler = new DBHandler(ServiceClasss.this);
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userType", header);
            jsonObject.put("date", msg);
            ChatActivity.jsonArray.put(jsonObject);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ChatActivity.singleChatAdapter.notifyDataSetChanged();

                    } catch (Exception e) {

                    }
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
        dbHandler.close();
//        }

    }

    private void addHeaderReceiveMsg(String userId, String header, String currentTime, String msgId, String groupId, String chatRoomType, String shouldSign) {
        DBHandler dbHandler = new DBHandler(ServiceClasss.this);
        dbHandler.InsertChatMsg(new ChatsMessagesModel(msgId, userId, "0", "0", "0", header, groupId, String.valueOf(currentTime), "0", "0", 0, 0, 0, "", "", "", "", chatRoomType, "false", "", "", 0, "", "", "", shouldSign));
        dbHandler.close();
    }

    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    private void ReceiveMessage(String chatType, final String messageId, final String userId, String msg, Status delivered, String sentTime,
                                final String deliveredTime, final String seenTime, int caption, int download, final String to, String lat,
                                String lng, String groupName, String create_by, String sent_by, final String groupId, String chatRoomType, String cName, String cNumber, boolean show, String tit, String desc, String log, String shouldSign) {


        Log.e("count", String.valueOf(unreadCount));
        sendDelivered(messageId, userId, to, deliveredTime, chatRoomType);
        try {


            if (ChatActivity.localChatRoomType.equalsIgnoreCase(chatRoomType)) {
                Log.d("ReceiveMessage: ", "userid:" + userId + ",zoe:" + ChatActivity.zoeChatID);

                if (userId.equalsIgnoreCase(SharedHelper.getKey(ServiceClasss.this, "id"))) {
                    final JSONObject jsonObject = new JSONObject();
                    jsonObject.put("msg", msg);
                    if (chatType.equalsIgnoreCase(ChatType.text.toString())) {
                        jsonObject.put("userType", ChatMessages.SENDER);
                    } else if (chatType.equalsIgnoreCase(ChatType.image.toString())) {
                        jsonObject.put("userType", ChatMessages.SENDER_IMAGE);
                    } else if (chatType.equalsIgnoreCase(ChatType.location.toString())) {
                        jsonObject.put("userType", ChatMessages.SENDER_LOCATION);
                    } else if (chatType.equalsIgnoreCase(ChatType.video.toString())) {
                        jsonObject.put("userType", ChatMessages.SENDER_VIDEO);
                    } else if (chatType.equalsIgnoreCase(ChatType.contact.toString())) {
                        jsonObject.put("userType", ChatMessages.SENDER_CONTACT);
                    } else if (chatType.equalsIgnoreCase(ChatType.audio.toString())) {
                        jsonObject.put("isPlaying","0");
                        jsonObject.put("playingStatus","0");
                        jsonObject.put("userType", ChatMessages.SENDER_AUDIO);
                    } else if (chatType.equalsIgnoreCase(ChatType.document.toString())) {
                        jsonObject.put("userType", ChatMessages.SENDER_DOC);
                    } else if (chatType.equalsIgnoreCase(ChatType.sticker.toString())) {
                        jsonObject.put("userType", ChatMessages.SENDER_STICKER);
                    }

                    jsonObject.put("contentStatus", Status.DELIVERED);
                    jsonObject.put("chatType", chatType);
                    if (show) {
                        jsonObject.put("showPreview", true);
                        jsonObject.put("metaTitle", tit);
                        jsonObject.put("metaDescription", desc);
                        jsonObject.put("metaLogo", log);

                    } else {
                        jsonObject.put("showPreview", false);
                    }
                    jsonObject.put("contentType", ChatType.text);
                    jsonObject.put("lat", lat);
                    jsonObject.put("lng", lng);
                    jsonObject.put("chatRoomType", chatRoomType);
                    jsonObject.put("sentTime", sentTime);
                    jsonObject.put("cName", cName);
                    jsonObject.put("cNumber", cNumber);
                    jsonObject.put("msgId", messageId);
                    jsonObject.put("userName", sent_by);
                    jsonObject.put("shouldSign", shouldSign);
                    ChatActivity.jsonArray.put(jsonObject);
                    Log.d("ReceiveMessage: ", "1name:" + cName + ",type:" + chatRoomType);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            ChatActivity.singleChatAdapter.notifyDataSetChanged();
                            ChatActivity.scrollToBottom();
                            if (SharedHelper.getKey(ServiceClasss.this, "single_chat_enable").equalsIgnoreCase("yes")) {
                                sendSeen(messageId, userId, to);
                                if (Const.URI.inMessageTone) {
                                    if (!isAppIsInBackground(ServiceClasss.this)) {
                                        mPlayer = MediaPlayer.create(ServiceClasss.this, R.raw.send_audio);
                                        mPlayer.start();
                                    }
                                }
                            }
                        }
                    });

                }
                //singlechat receiver
                else if (userId.equalsIgnoreCase(ChatActivity.zoeChatID)) {
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
                        jsonObject.put("isPlaying","0");
                        jsonObject.put("playingStatus","0");
                    } else if (chatType.equalsIgnoreCase(ChatType.document.toString())) {
                        jsonObject.put("userType", ChatMessages.RECEIVER_DOC);
                    } else if (chatType.equalsIgnoreCase(ChatType.sticker.toString())) {
                        jsonObject.put("userType", ChatMessages.RECEIVER_STICKER);
                    }
                    if (show) {
                        jsonObject.put("showPreview", true);
                        jsonObject.put("metaTitle", tit);
                        jsonObject.put("metaDescription", desc);
                        jsonObject.put("metaLogo", log);

                    } else {
                        jsonObject.put("showPreview", false);
                    }
                    jsonObject.put("contentStatus", Status.DELIVERED);
                    jsonObject.put("chatType", chatType);
                    jsonObject.put("lat", lat);
                    jsonObject.put("lng", lng);
                    jsonObject.put("chatRoomType", chatRoomType);
                    jsonObject.put("sentTime", sentTime);
                    jsonObject.put("cName", cName);
                    jsonObject.put("cNumber", cNumber);
                    jsonObject.put("msgId", messageId);
                    jsonObject.put("userName", sent_by);
                    jsonObject.put("shouldSign", shouldSign);

                    ChatActivity.jsonArray.put(jsonObject);
                    Log.d("ReceiveMessage: ", "1name:" + cName + ",type:" + chatRoomType);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            ChatActivity.singleChatAdapter.notifyDataSetChanged();
                            ChatActivity.scrollToBottom();
                            if (SharedHelper.getKey(ServiceClasss.this, "single_chat_enable").equalsIgnoreCase("yes")) {
                                if (SharedHelper.getKey(ServiceClasss.this, "single_chat_enable").equalsIgnoreCase("yes")) {
                                    sendSeen(messageId, userId, to);
                                    if (Const.URI.inMessageTone) {
                                        if (!isAppIsInBackground(ServiceClasss.this)) {
                                            mPlayer = MediaPlayer.create(ServiceClasss.this, R.raw.send_audio);
                                            mPlayer.start();
                                        }
                                    }
                                }
                            }
                        }
                    });

                }
                //groupchat receiver
                else {
                    Log.d("ReceiveMessage: ", "2name:" + cName + ",type:" + chatRoomType);
                    Log.d("ReceiveMessage: ", "userid:" + ChatActivity.groupId_loc + ",grp:" + groupId);
                    if (groupId.equalsIgnoreCase(ChatActivity.groupId_loc)) {
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
                            jsonObject.put("isPlaying","0");
                            jsonObject.put("playingStatus","0");
                        } else if (chatType.equalsIgnoreCase(ChatType.document.toString())) {
                            jsonObject.put("userType", ChatMessages.RECEIVER_DOC);
                        } else if (chatType.equalsIgnoreCase(ChatType.sticker.toString())) {
                            jsonObject.put("userType", ChatMessages.RECEIVER_STICKER);
                        }
                        if (show) {
                            jsonObject.put("showPreview", true);
                            jsonObject.put("metaTitle", tit);
                            jsonObject.put("metaDescription", desc);
                            jsonObject.put("metaLogo", log);

                        } else {
                            jsonObject.put("showPreview", false);
                        }
                        jsonObject.put("contentStatus", Status.DELIVERED);
                        jsonObject.put("chatType", chatType);
                        jsonObject.put("lat", lat);
                        jsonObject.put("lng", lng);
                        jsonObject.put("chatRoomType", chatRoomType);
                        jsonObject.put("sentTime", sentTime);
                        jsonObject.put("userName", sent_by);
                        jsonObject.put("msgId", messageId);
                        jsonObject.put("shouldSign", shouldSign);

                        ChatActivity.jsonArray.put(jsonObject);

                        runOnUiThread(new Runnable() {
                            public void run() {
                                ChatActivity.singleChatAdapter.notifyDataSetChanged();
                                ChatActivity.scrollToBottom();
                                if (SharedHelper.getKey(ServiceClasss.this, "single_chat_enable").equalsIgnoreCase("yes")) {
                                    sendSeen(messageId, groupId, to);
                                    if (Const.URI.inMessageTone) {
                                        if (!isAppIsInBackground(ServiceClasss.this)) {
                                            mPlayer = MediaPlayer.create(ServiceClasss.this, R.raw.send_audio);
                                            mPlayer.start();
                                        }
                                    }
                                }
                            }
                        });

                    }

                }

            }


        } catch (Exception e) {

        }


        //long time = dbHandler.GetLastRow(userId);
        try {
            String times = SharedHelper.getHeader(ServiceClasss.this, "header_time");
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
                SharedHelper.putHeader(ServiceClasss.this, "header_time", sentTime);
                addHeaderReceiveMsg(userId, ChatMessages.HEADER, sentTime, String.valueOf(System.currentTimeMillis()), groupId, chatRoomType, shouldSign);

                addDBReceiveMsg(chatType, messageId, userId, msg, delivered, sentTime, deliveredTime, seenTime, caption, download, to, lat, lng, groupName, create_by, sent_by, groupId, chatRoomType, cName, cNumber, to, show, tit, desc, log, shouldSign);

            } else if (chatTime.equalsIgnoreCase("Today")) {
                if (value == 0) {
                    value++;
                    SharedHelper.putInt(ServiceClasss.this, "date", value);
                }
                SharedHelper.putHeader(ServiceClasss.this, "header_time", sentTime);
                value = Integer.parseInt(SharedHelper.getInt(ServiceClasss.this, "date"));
                addDBReceiveMsg(chatType, messageId, userId, msg, delivered, sentTime, deliveredTime, seenTime, caption, download, to, lat, lng, groupName, create_by, sent_by, groupId, chatRoomType, cName, cNumber, to, show, tit, desc, log, shouldSign);
            } else {
                SharedHelper.putHeader(ServiceClasss.this, "header_time", sentTime);
                addHeaderReceiveMsg(userId, ChatMessages.HEADER, sentTime, String.valueOf(System.currentTimeMillis()), groupId, chatRoomType, shouldSign);
                addDBReceiveMsg(chatType, messageId, userId, msg, delivered, sentTime, deliveredTime, seenTime, caption, download, to, lat, lng, groupName, create_by, sent_by, groupId, chatRoomType, cName, cNumber, to, show, tit, desc, log, shouldSign);
                value = 0;
            }
            try {

                ChatFragment.mainActivity.refresh();
            } catch (Exception e) {

            }

        } catch (ParseException | NumberFormatException e) {
            e.printStackTrace();
        }


    }

    private void sendSeen(String messageId, String userId, String to) {
        if (!userId.equalsIgnoreCase(SharedHelper.getKey(ServiceClasss.this, "id"))) {

            MainActivity.unreadCount = 0;
            unreadCount = 0;
            ChatActivity.counter = 0;
            ChatFragment.refreshLay();
            DBHandler dbHandler = new DBHandler(ServiceClasss.this);
            JSONObject jsonObject = new JSONObject();
            long seenTime = System.currentTimeMillis();
            dbHandler.UpdateSeenTime(messageId, String.valueOf(seenTime));
            dbHandler.UpdateContentStatus(messageId, Status.READ.toString());
            dbHandler.close();
            try {
                jsonObject.put("messageId", messageId);
                jsonObject.put("from", userId);
                jsonObject.put("to", to);
                jsonObject.put("time", String.valueOf(seenTime));
                jsonObject.put("chatRoomType", ChatActivity.localChatRoomType);
                socket.emit("sendSeen", jsonObject);
                Log.d("sendSeen", "values"+jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void addDBReceiveMsg(final String chatType, final String messageId, final String userIdq,
                                 final String msg, final Status delivered, final String sentTime, final String deliveredTime,
                                 final String seenTime, final int caption, final int download, final String to, final String lat, final String lng, final String groupName,
                                 final String create_by, final String sent_by, final String groupId, final String chatRoomType, final String cName, final String cNumber, String s, final boolean show, final String metatitle, final String metadesc, final String metalogo, final String shouldSign) {


        final DBHandler dbHandler = new DBHandler(ServiceClasss.this);

        String chatTypeLayout = "";
        String userId = userIdq;
        String checkvalue=userIdq;


        if (userId.equalsIgnoreCase(SharedHelper.getKey(ServiceClasss.this, "id"))) {

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

                if (!current_id.equalsIgnoreCase(userId)) {

                    if (chatRoomType.equalsIgnoreCase("0")) {
                        unreadCount = dbHandler.GetUnReadCount(userId);
                        unreadCount++;
                    } else {
                        unreadCount = dbHandler.GetUnReadCount(groupId);
                        unreadCount++;
                    }
                }
            } else {
                if (!current_id.equalsIgnoreCase(groupId)) {

                    if (chatRoomType.equalsIgnoreCase("0")) {
                        unreadCount = dbHandler.GetUnReadCount(userId);
                        unreadCount++;
                    } else {
                        unreadCount = dbHandler.GetUnReadCount(groupId);
                        unreadCount++;
                    }
                }

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
        if (checkvalue.equalsIgnoreCase(SharedHelper.getKey(ServiceClasss.this,"id"))) {
            dbHandler.InsertChatMsg(new ChatsMessagesModel(messageId.trim(), userId, msg,
                    chatType, delivered.toString(), chatTypeLayout, groupId,
                    sentTime, String.valueOf(deliveredTime), seenTime, caption, 1, download, lat, lng, cName, cNumber, chatRoomType, "false", msg, "", showPre, metatitle, metadesc, metalogo, shouldSign));

        }
        else {
            dbHandler.InsertChatMsg(new ChatsMessagesModel(messageId.trim(), userId, msg,
                    chatType, delivered.toString(), chatTypeLayout, groupId,
                    sentTime, String.valueOf(deliveredTime), seenTime, caption, 0, download, lat, lng, cName, cNumber, chatRoomType, "false", msg, "", showPre, metatitle, metadesc, metalogo, shouldSign));
        }
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

    private void sendDelivered(String messageId, String from, String to, String deliveredTime, String localChatRoomType) {
        DBHandler dbHandler = new DBHandler(ServiceClasss.this);
        dbHandler.UpdateDeliveredTime(messageId, String.valueOf(deliveredTime));
        dbHandler.close();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("messageId", messageId);
            jsonObject.put("from", from);
            jsonObject.put("to", to);
            jsonObject.put("userid", SharedHelper.getKey(ServiceClasss.this, "id"));
            jsonObject.put("time", String.valueOf(deliveredTime));
            jsonObject.put("chatRoomType", localChatRoomType);
            socket.emit("sendDelivered", jsonObject);
            Log.d("sendDelivered", "true");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void validateincomingvalues(JSONArray particpants_array) {
        DBHandler dbHandler = new DBHandler(ServiceClasss.this);
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
        DBHandler dbHandler = new DBHandler(ServiceClasss.this);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
//        startService(new Intent(this, ServiceClasss.class));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", SharedHelper.getKey(ServiceClasss.this, "id"));
            socket.emit("offline", jsonObject);
            Log.e("offlines", "" + jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        socket.disconnect();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();

    }

    public static class Emitters {
        Context context;
        Boolean boolean_response = false;


        private DBHandler dbHandler;

        public Emitters(Context context) {
            this.context = context;

        }

        public void SendChatMsgunsent(String image_path, String msgId, String userID,
                                      String currentTime, String chatType, String from, String groupId, String chatRoomType, String notii, String title, String description, String logo, boolean showPreview, AsyncTask<Void, Void, Void> object) throws JSONException {
            JSONObject jsonObject = new JSONObject();
            dbHandler = new DBHandler(context);

            try {
                jsonObject.put("from", from);

                if (chatRoomType.equalsIgnoreCase("0")) {
                    jsonObject.put("to", userID);
                    jsonObject.put("shouldSign", dbHandler.GetSign(userID));
                } else if (chatRoomType.equalsIgnoreCase("1")) {
                    jsonObject.put("shouldSign", dbHandler.GetSign(groupId));
                    jsonObject.put("to", groupId);
                } else if (chatRoomType.equalsIgnoreCase("3")) {
                    jsonObject.put("to", groupId);
                    jsonObject.put("shouldSign", dbHandler.GetSign(groupId));

                }
                jsonObject.put("messageId", msgId);
                jsonObject.put("chatRoomType", chatRoomType);
                if (!chatRoomType.equalsIgnoreCase("3")) {
                    jsonObject.put("groupId", groupId);
                }
                jsonObject.put("time", currentTime);
                jsonObject.put("content", image_path);
                jsonObject.put("contentType", chatType);


                Log.e("sendMsg_from_adapter", jsonObject.toString());
                if (chatRoomType.equalsIgnoreCase("3")) {
                    jsonObject.put("notify", notii);
                    socket.emit("sendMessageInChannel", jsonObject);

                    if (object != null) {
                        object.cancel(true);
                    }


                } else {
                    socket.emit("sendMessage", jsonObject);

                    if (object != null) {
                        object.cancel(true);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        public void SendChatMsg(String image_path, String msgId, String userID,
                                String currentTime, ChatType chatType, String from, String groupId, String chatRoomType, String notifications, String title, String description, String logo, boolean showPreview, AsyncTask<Void, Void, Void> object) throws JSONException {
            JSONObject jsonObject = new JSONObject();
            dbHandler = new DBHandler(context);


            if (!chatRoomType.equalsIgnoreCase("2")) {

                try {
                    jsonObject.put("from", from);

                    if (chatRoomType.equalsIgnoreCase("0")) {
                        jsonObject.put("to", userID);
                        jsonObject.put("shouldSign", dbHandler.GetSign(userID));
                    } else if (chatRoomType.equalsIgnoreCase("1")) {
                        jsonObject.put("shouldSign", dbHandler.GetSign(groupId));
                        jsonObject.put("to", groupId);
                    } else if (chatRoomType.equalsIgnoreCase("3")) {
                        jsonObject.put("to", groupId);
                        jsonObject.put("shouldSign", dbHandler.GetSign(groupId));

                    }
                    jsonObject.put("messageId", msgId);
                    jsonObject.put("chatRoomType", chatRoomType);
                    if (!chatRoomType.equalsIgnoreCase("3")) {
                        jsonObject.put("groupId", groupId);
                    }
                    jsonObject.put("time", currentTime);
                    jsonObject.put("content", image_path);

                    if (chatType.equals(ChatType.image)) {
                        jsonObject.put("contentType", ChatType.image);
                    } else if (chatType.equals(ChatType.video)) {
                        jsonObject.put("contentType", ChatType.video);
                    } else if (chatType.equals(ChatType.audio)) {
                        jsonObject.put("contentType", ChatType.audio);
                    } else if (chatType.equals(ChatType.document)) {
                        jsonObject.put("contentType", ChatType.document);
                    }
                    Log.e("sendMsg_from_adapter", jsonObject.toString());

                    if (chatRoomType.equalsIgnoreCase("3")) {
                        jsonObject.put("notify", notifications);
                        socket.emit("sendMessageInChannel", jsonObject);

                        if (object != null) {
                            object.cancel(true);
                        }
                        ChatActivity.title = "";
                        ChatActivity.description = "";
                        ChatActivity.logo = "";
                        ChatActivity.showPreview = false;


                    } else {
                        socket.emit("sendMessage", jsonObject);

                        if (object != null) {
                            object.cancel(true);
                        }
                        ChatActivity.title = "";
                        ChatActivity.description = "";
                        ChatActivity.logo = "";
                        ChatActivity.showPreview = false;

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                JSONArray fullarray = new JSONArray();
                try {
                    for (int i = 0; i < ChatActivity.participan_model.size(); i++) {
                        JSONObject part = new JSONObject();

                        GroupParticiapntsModel values = ChatActivity.participan_model.get(i);
                        String part_id = values.getUser_id();
                        part.put("participantId", part_id);
                        fullarray.put(part);
                    }


                    jsonObject.put("from", from);
                    jsonObject.put("participants", fullarray.toString());
                    jsonObject.put("messageId", msgId);
                    jsonObject.put("chatRoomType", "0");
                    jsonObject.put("groupId", groupId);
                    jsonObject.put("time", currentTime);
                    jsonObject.put("content", image_path);

                    if (chatType.equals(ChatType.image)) {
                        jsonObject.put("contentType", ChatType.image);
                    } else if (chatType.equals(ChatType.video)) {
                        jsonObject.put("contentType", ChatType.video);
                    } else if (chatType.equals(ChatType.audio)) {
                        jsonObject.put("contentType", ChatType.audio);
                    } else if (chatType.equals(ChatType.document)) {
                        jsonObject.put("contentType", ChatType.document);
                    }
                    Log.e("sendMsg_from_adapter", jsonObject.toString());
                    socket.emit("sendBroadcast", jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }


        public void SendChatMsgwithThumb(String image_path, String msgId, String userID,
                                         String currentTime, ChatType chatType, String from, String groupId, String chatRoomType, String thumb, String notifications, String title, String description, String logo, boolean showPreview, AsyncTask<Void, Void, Void> object) throws JSONException {
            JSONObject jsonObject = new JSONObject();
            dbHandler = new DBHandler(context);


            if (!chatRoomType.equalsIgnoreCase("2")) {

                try {
                    jsonObject.put("from", from);

                    if (chatRoomType.equalsIgnoreCase("0")) {
                        jsonObject.put("to", userID);
                        jsonObject.put("shouldSign", dbHandler.GetSign(userID));
                    } else if (chatRoomType.equalsIgnoreCase("1")) {
                        jsonObject.put("shouldSign", dbHandler.GetSign(groupId));
                        jsonObject.put("to", groupId);
                    } else if (chatRoomType.equalsIgnoreCase("3")) {
                        jsonObject.put("to", groupId);
                        jsonObject.put("shouldSign", dbHandler.GetSign(groupId));

                    }
                    jsonObject.put("messageId", msgId);
                    jsonObject.put("chatRoomType", chatRoomType);
                    if (!chatRoomType.equalsIgnoreCase("3")) {
                        jsonObject.put("groupId", groupId);
                    }
                    jsonObject.put("time", currentTime);
                    jsonObject.put("content", image_path);

                    if (chatType.equals(ChatType.image)) {
                        jsonObject.put("contentType", ChatType.image);
                    } else if (chatType.equals(ChatType.video)) {
                        jsonObject.put("contentType", ChatType.video);
                    } else if (chatType.equals(ChatType.audio)) {
                        jsonObject.put("contentType", ChatType.audio);
                    } else if (chatType.equals(ChatType.document)) {
                        jsonObject.put("contentType", ChatType.document);
                    }
                    Log.e("sendMsg_from_adapter", jsonObject.toString());

                    if (chatRoomType.equalsIgnoreCase("3")) {
                        jsonObject.put("notify", notifications);
                        socket.emit("sendMessageInChannel", jsonObject);

                        if (object != null) {
                            object.cancel(true);
                        }
                        ChatActivity.title = "";
                        ChatActivity.description = "";
                        ChatActivity.logo = "";
                        ChatActivity.showPreview = false;


                    } else {
                        socket.emit("sendMessage", jsonObject);

                        if (object != null) {
                            object.cancel(true);
                        }
                        ChatActivity.title = "";
                        ChatActivity.description = "";
                        ChatActivity.logo = "";
                        ChatActivity.showPreview = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                JSONArray fullarray = new JSONArray();
                try {
                    for (int i = 0; i < ChatActivity.participan_model.size(); i++) {
                        JSONObject part = new JSONObject();

                        GroupParticiapntsModel values = ChatActivity.participan_model.get(i);
                        String part_id = values.getUser_id();
                        part.put("participantId", part_id);
                        fullarray.put(part);
                    }


                    jsonObject.put("from", from);
                    jsonObject.put("participants", fullarray.toString());
                    jsonObject.put("messageId", msgId);
                    jsonObject.put("chatRoomType", "0");
                    jsonObject.put("groupId", groupId);
                    jsonObject.put("time", currentTime);
                    jsonObject.put("content", image_path);

                    if (chatType.equals(ChatType.image)) {
                        jsonObject.put("contentType", ChatType.image);
                    } else if (chatType.equals(ChatType.video)) {
                        jsonObject.put("contentType", ChatType.video);
                    } else if (chatType.equals(ChatType.audio)) {
                        jsonObject.put("contentType", ChatType.audio);
                    } else if (chatType.equals(ChatType.document)) {
                        jsonObject.put("contentType", ChatType.document);
                    }
                    Log.e("sendMsg_from_adapter", jsonObject.toString());
                    socket.emit("sendBroadcast", jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }


        public void sendChat(String userID, String msg, String uniqueID, String currentTime, ChatType chatType, String chatRoomType, String groupId, String cName, String cNumber, String shouldSign, String title, String description, String logo, boolean showPreview, AsyncTask<Void, Void, Void> object, double lat, double lng) {
            JSONObject jsonObject = new JSONObject();

            dbHandler = new DBHandler(context);

            if (!chatRoomType.equalsIgnoreCase("2")) {
                try {
                    jsonObject.put("from", SharedHelper.getKey(context, "id"));
                    if (chatRoomType.equalsIgnoreCase("0")) {
                        jsonObject.put("to", userID);
                        jsonObject.put("shouldSign", "false");
                    } else if (chatRoomType.equalsIgnoreCase("1")) {
                        jsonObject.put("to", groupId);
                        jsonObject.put("shouldSign", "true");
                    } else if (chatRoomType.equalsIgnoreCase("3")) {
                        jsonObject.put("to", groupId);
                        jsonObject.put("shouldSign", shouldSign);

                    }

                    jsonObject.put("messageId", uniqueID);
                    jsonObject.put("chatRoomType", chatRoomType);
                    if (!chatRoomType.equalsIgnoreCase("3")) {
                        jsonObject.put("groupId", groupId);
                    }

                    jsonObject.put("time", String.valueOf(currentTime));
                    jsonObject.put("content", msg);


                    if (chatType.equals(ChatType.text)) {
                        if (showPreview) {
                            jsonObject.put("showPreview", true);
                            jsonObject.put("metaTitle", title);
                            jsonObject.put("metaDescription", description);
                            jsonObject.put("metaLogo", logo);

                        } else {
                            jsonObject.put("showPreview", false);
                        }
                        jsonObject.put("contentType", ChatType.text);
                    } else if (chatType.equals(ChatType.contact)) {
                        jsonObject.put("contentType", ChatType.contact);
                        jsonObject.put("contactName", cName);
                        jsonObject.put("contactNumber", cNumber);
                    } else if (chatType.equals(ChatType.location)) {
                        jsonObject.put("contentType", ChatType.location);
                        jsonObject.put("latitude", lat);
                        jsonObject.put("longitude", lng);
                    } else if (chatType.equals(ChatType.sticker)) {
                        jsonObject.put("contentType", ChatType.sticker);
                    }
                    Log.e("sendMsg", jsonObject.toString());
                    if (chatRoomType.equalsIgnoreCase("3")) {
                        jsonObject.put("notify", dbHandler.getNotifications(groupId));
                        socket.emit("sendMessageInChannel", jsonObject);

                    } else {
                        socket.emit("sendMessage", jsonObject);
                        Log.d("sendChat: ", "ASdasd");
                    }

                    if (object != null) {
                        if (object != null) {
                            object.cancel(true);
                        }
                    }
                    ChatActivity.title = "";
                    ChatActivity.description = "";
                    ChatActivity.logo = "";
                    ChatActivity.showPreview = false;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    JSONArray fullarray = new JSONArray();

                    for (int i = 0; i < ChatActivity.participan_model.size(); i++) {
                        JSONObject part = new JSONObject();

                        GroupParticiapntsModel values = ChatActivity.participan_model.get(i);
                        String part_id = values.getUser_id();
                        part.put("participantId", part_id);

                        fullarray.put(i, part);
                    }

                    jsonObject.put("from", SharedHelper.getKey(context, "id"));

                    jsonObject.put("participants", fullarray.toString());
                    jsonObject.put("messageId", uniqueID);
                    jsonObject.put("chatRoomType", "0");
                    jsonObject.put("groupId", "");
                    jsonObject.put("time", String.valueOf(currentTime));
                    jsonObject.put("content", msg);
                    if (chatType.equals(ChatType.text)) {
                        if (showPreview) {
                            jsonObject.put("showPreview", true);
                            jsonObject.put("metaTitle", title);
                            jsonObject.put("metaDescription", description);
                            jsonObject.put("metaLogo", logo);

                        } else {
                            jsonObject.put("showPreview", false);
                        }
                        jsonObject.put("contentType", ChatType.text);
                    } else if (chatType.equals(ChatType.contact)) {
                        jsonObject.put("contentType", ChatType.contact);
                        jsonObject.put("contactName", cName);
                        jsonObject.put("contactNumber", cNumber);
                    } else if (chatType.equals(ChatType.location)) {
                        jsonObject.put("contentType", ChatType.location);
                        jsonObject.put("latitude", lat);
                        jsonObject.put("longitude", lng);
                    } else if (chatType.equals(ChatType.sticker)) {
                        jsonObject.put("contentType", ChatType.sticker);
                    }
                    Log.e("sendMsg", jsonObject.toString());
                    socket.emit("sendBroadcast", jsonObject);


                    if (object != null) {
                        object.cancel(true);
                    }
                    ChatActivity.title = "";
                    ChatActivity.description = "";
                    ChatActivity.logo = "";
                    ChatActivity.showPreview = false;


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void sendseen(String messageId, String userId, String to, String localChatRoomType) {
            JSONObject jsonObject = new JSONObject();
            dbHandler = new DBHandler(context);
            long seenTime = System.currentTimeMillis();
            dbHandler.UpdateSeenTime(messageId, String.valueOf(seenTime));
            dbHandler.UpdateContentStatus(messageId, Status.READ.toString());
            dbHandler.close();
            try {
                jsonObject.put("messageId", messageId);
                jsonObject.put("from", userId);
                jsonObject.put("to", to);
                jsonObject.put("time", String.valueOf(seenTime));
                jsonObject.put("chatRoomType", localChatRoomType);
                socket.emit("sendSeen", jsonObject);
                Log.d("sendSeen", "values:"+jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        public void sendDelivered(String messageId, String from, String to, String deliveredTime, String localChatRoomType) {
            if (!from.equalsIgnoreCase(SharedHelper.getKey(context, "id"))) {

                JSONObject jsonObject = new JSONObject();
                dbHandler = new DBHandler(context);
                dbHandler.UpdateDeliveredTime(messageId, String.valueOf(deliveredTime));
                dbHandler.close();

                try {
                    jsonObject.put("messageId", messageId);
                    jsonObject.put("from", from);
                    jsonObject.put("to", to);
                    jsonObject.put("time", String.valueOf(deliveredTime));
                    jsonObject.put("chatRoomType", localChatRoomType);
                    socket.emit("sendDelivered", jsonObject);
                    Log.d("sendDelivered", "true");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        public void sendtyping(String from, String to, String status) throws Exception {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("from", SharedHelper.getKey(context, "id"));
            jsonObject.put("to", to);
            jsonObject.put("status", "start");
            socket.emit("typing", jsonObject);

        }

        public void sendack(String msgId) throws JSONException {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userid", SharedHelper.getKey(context, "id"));
            jsonObject.put("uniqueid", msgId);
            socket.emit("ack", jsonObject);

        }


        public void getOnline(JSONObject object) {

            try {
                socket.emit("getOnline", object);
            } catch (Exception e) {

            }

        }


        public Boolean addAdminInChannel(final String part_id, final String grp_id, final int i, final Dialog dialog, final Context context, final String created_by, final String joined_at) throws JSONException {
            JSONObject jsonObject = new JSONObject();
            boolean_response = true;


            String from = SharedHelper.getKey(this.context, "my_zoe_id");

            jsonObject.put("from", from);
            jsonObject.put("channelId", grp_id);
            jsonObject.put("participantId", part_id);
            Log.d("addgroup: ", "" + jsonObject);


            socket.emit("addAdminInChannel", jsonObject, new Ack() {
                @Override
                public void call(Object... args) {
                    String msg = String.valueOf(args[0]);
                    if (msg.equalsIgnoreCase("true")) {

                        if (i == 0) {

                            AddAdminChannel.trigger_makeadminChannel_0(part_id, grp_id, dialog, context, created_by, joined_at);
                        } else {
                            UserDetails.trigger_makeadminChannel_1(part_id, grp_id, dialog, context, created_by, joined_at);

                        }


                    } else {
                        boolean_response = false;

                    }

                }
            });


            return boolean_response;

        }

        public Boolean editgroupname(final String grp_id, final String from_id, final String g_name, final String old_id) throws JSONException {
            JSONObject jsonObject = new JSONObject();
            boolean_response = true;

            String from = SharedHelper.getKey(context, "my_zoe_id");
            jsonObject.put("from", from);
            jsonObject.put("groupId", grp_id);
            jsonObject.put("groupName", g_name);
            jsonObject.put("oldName", old_id);
            Log.d("change_name: ", "" + jsonObject);

            socket.emit("editGroupName", jsonObject, new Ack() {
                @Override
                public void call(Object... args) {
                    String msg = String.valueOf(args[0]);

                    if (msg.equalsIgnoreCase("true")) {
                        ChangeGrpName.trigger_editname(grp_id, from_id, g_name, old_id, context);
                    } else {
                        boolean_response = false;

                    }

                }
            });
            return boolean_response;


        }

        public Boolean editChannel(final JSONObject object) throws JSONException {

            boolean_response = true;


            socket.emit("editChannel", object, new Ack() {
                @Override
                public void call(Object... args) {
                    String msg = String.valueOf(args[0]);

                    if (msg.equalsIgnoreCase("true")) {
                        ChannelEdit.editchanneltrigger(object, context);
                    } else {
                        boolean_response = false;

                    }

                }
            });
            return boolean_response;


        }


        public Boolean exitChannel(JSONObject object, final int i) {
            boolean_response = true;

            socket.emit("exitChannel", object, new Ack() {
                @Override
                public void call(Object... args) {
                    String msg = String.valueOf(args[0]);

                    if (msg.equalsIgnoreCase("true")) {
                        boolean_response = true;
                        if (i == 0) {
                            ChannelEdit.exitchanneltrigger0(context);
                        } else {
                            UserDetails.exitchanneltrigger1(context);

                        }
                    } else {
                        boolean_response = false;

                    }

                }
            });
            return boolean_response;

        }

        public Boolean createChannel(JSONObject jsonObject) {
            boolean_response = true;

            socket.emit("createChannel", jsonObject, new Ack() {
                @Override
                public void call(Object... args) {
                    String msg = String.valueOf(args[0]);

                    if (msg.equalsIgnoreCase("true")) {
                        boolean_response = true;

                        ChannelSetings.createchanneltrigger(context);
                    } else {
                        boolean_response = false;

                    }

                }
            });
            return boolean_response;

        }

        public Boolean addParticipants(final JSONObject jsonObject) {
            boolean_response = true;

            socket.emit("addParticipants", jsonObject, new Ack() {
                @Override
                public void call(Object... args) {
                    String msg = String.valueOf(args[0]);

                    if (msg.equalsIgnoreCase("true")) {
                        NewGroup_activity.addgrouptrigger(jsonObject, context);
                    } else {
                        boolean_response = false;

                    }

                }
            });
            return boolean_response;
        }

        public Boolean createGroup(final JSONObject jsonObject) {
            boolean_response = true;
            boolean_response = true;

            socket.emit("createGroup", jsonObject, new Ack() {
                @Override
                public void call(Object... args) {
                    String msg = String.valueOf(args[0]);
                    if (msg.equalsIgnoreCase("true")) {

                        SetGroupName_activity.createGrouptrigger(context, jsonObject.optString("groupId"));
                        boolean_response = true;
                        return;
                    } else {
                        boolean_response = false;
                        return;
                    }

                }
            });

            return boolean_response;
        }

        public Boolean makeAdmin(final JSONObject jsonObject, final String created_by, final String joined_at) {
            boolean_response = true;

            socket.emit("makeAdmin", jsonObject, new Ack() {
                @Override
                public void call(Object... args) {
                    String msg = String.valueOf(args[0]);

                    if (msg.equalsIgnoreCase("true")) {
                        UserDetails.makeadmintrigger(context, jsonObject, created_by, joined_at);
                        boolean_response = true;
                    } else {
                        boolean_response = false;

                    }

                }
            });
            return boolean_response;
        }

        public Boolean removeParticipant(final JSONObject jsonObject) {
            boolean_response = true;

            socket.emit("removeParticipant", jsonObject, new Ack() {
                @Override
                public void call(Object... args) {
                    String msg = String.valueOf(args[0]);

                    if (msg.equalsIgnoreCase("true")) {
                        boolean_response = true;
                        UserDetails.removepartitrigger(context, jsonObject);
                    } else {
                        boolean_response = false;

                    }

                }
            });
            return boolean_response;
        }

        public Boolean removeAdminInChannel(final JSONObject jsonObject, final String joined_at, final String created_by) {
            socket.emit("removeAdminInChannel", jsonObject, new Ack() {
                @Override
                public void call(Object... args) {
                    String msg = String.valueOf(args[0]);

                    if (msg.equalsIgnoreCase("true")) {
                        UserDetails.removeadmintrigger(context, jsonObject, joined_at, created_by);

                        boolean_response = true;
                    } else {
                        boolean_response = true;

                    }

                }
            });
            return boolean_response;
        }

        public Boolean editGroupImage(JSONObject jsonObject) {
            boolean_response = true;

            socket.emit("editGroupImage", jsonObject, new Ack() {
                @Override
                public void call(Object... args) {
                    String msg = String.valueOf(args[0]);

                    if (msg.equalsIgnoreCase("true")) {
                        UserDetails.editgrpimagetrigger(context);
                        boolean_response = true;
                    } else {
                        boolean_response = true;

                    }

                }
            });
            return boolean_response;
        }

        public Boolean exitGroup(final JSONObject jsonObject) {
            boolean_response = true;

            socket.emit("exitGroup", jsonObject, new Ack() {
                @Override
                public void call(Object... args) {
                    String msg = String.valueOf(args[0]);

                    if (msg.equalsIgnoreCase("true")) {
                        UserDetails.leavetrigger(jsonObject, context);
                        boolean_response = true;
                    } else {
                        boolean_response = true;

                    }

                }
            });
            return boolean_response;
        }

        public void emitoffline() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", SharedHelper.getKey(context, "id"));
                socket.emit("offline", jsonObject);
                Log.e("connect", "emitoffline" + jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void emitonline() {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", SharedHelper.getKey(context, "id"));
                Log.e("connect", "emitonline" + jsonObject);
                socket.emit("online", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();

            }
        }

        public void qridemit(JSONObject jsonObject) {
            socket.emit("qridemit", jsonObject);


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

                new PostHelper(Const.Methods.CHANNEL_PARTICIPANTS_DETAILS, jsonObject.toString(), Const.ServiceCode.CHANNEL_PARTICIPANTS_DETAILS, ServiceClasss.this, this);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }


        @Override
        public void onTaskCompleted(JSONObject response, int serviceCode) {

            DBHandler dbHandler = new DBHandler(ServiceClasss.this);
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


    private class getGrpdetails extends AsyncTask<String, String, String> implements AsyncTaskCompleteListener {
        String result = "";
        private JSONArray particpants_array = new JSONArray();
        private JSONObject group_details = new JSONObject();

        @Override
        protected String doInBackground(String... params) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("groupId", groupId_loc);

                new PostHelper(Const.Methods.PARTICIPANTS_DETAILS, jsonObject.toString(), Const.ServiceCode.PARTICIPANTS_DETAILS, ServiceClasss.this, this);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }


        @Override
        public void onTaskCompleted(JSONObject response, int serviceCode) {

            DBHandler dbHandler = new DBHandler(ServiceClasss.this);
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
}
