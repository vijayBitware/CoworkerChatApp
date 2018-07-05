package com.bitware.coworker.FCM;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.bitware.coworker.DBHelper.DBHandler;
import com.bitware.coworker.R;
import com.bitware.coworker.activity.MainActivity;
import com.bitware.coworker.activity.VideoChatViewActivity;
import com.bitware.coworker.baseUtils.Const;
import com.bitware.coworker.baseUtils.SharedHelper;
import com.bitware.coworker.models.CallsEventModel;
import com.bitware.coworker.models.CallsModel;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by KrishnaDev on 1/13/17.
 */

public class FCMMsgService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    public static String current_id, check_id;
    DBHandler dbHandler = new DBHandler(this);
    String Notifytime;
    private Handler handler = new Handler();
    private int value = 0;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Create and show notification
        Log.e("Notification_msg", remoteMessage.getFrom());
        //Log.e("Notification_msg_title", remoteMessage.getNotification().getTitle());
        JSONObject jsonObject = new JSONObject(remoteMessage.getData());
        try {
            sendNotification(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    @SuppressWarnings("deprecation")
    private void sendNotification(JSONObject jsonObject) throws JSONException {


        Log.e("sendNotify_method", jsonObject.toString());
        String zoeChatId = jsonObject.optString("fromId");
        String channel_id = jsonObject.optString("channel_id");


        String name = dbHandler.GetUserName(zoeChatId);
        String image = dbHandler.GetUserImage(zoeChatId);

        long current = System.currentTimeMillis();
        try {
            JSONObject values1 = new JSONObject(jsonObject.optString("payload"));
            String channelId = values1.optString("channelId");
            Notifytime = dbHandler.GettimedNotifications(channelId);
            long noti = Long.parseLong(Notifytime);
            if (Notifytime.equalsIgnoreCase("1")) {
                Notifytime = "1";
            } else if (Notifytime.equalsIgnoreCase("0")) {
                Notifytime = "0";
            } else {
                if (current >= noti) {
                    Notifytime = "0";
                    dbHandler.UpdateNotifications(channelId, "0");
                } else {
                    Notifytime = "0";
                }
            }
        } catch (Exception e) {

        }

        if (jsonObject.optString("pushType").equalsIgnoreCase("voicecall")) {
            String call_msg = name + " " + "Voice Calling...";
            String type = "voiceCall";
            dbHandler.InsertCalls(new CallsModel(zoeChatId, name, "", System.currentTimeMillis(), type, "1"));
            Intent call = new Intent(this, VideoChatViewActivity.class);
            call.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            call.putExtra("call", type);
            call.putExtra("zoeChatID", zoeChatId);
            call.putExtra("user_name", name);
            call.putExtra("image", image);
            call.putExtra("channelId", channel_id);
            call.putExtra("receive", "yes");
            this.startActivity(call);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setVibrate(Const.URI.default_call_vibrate)
                    .setColor(this.getResources().getColor(R.color.colorPrimaryDark))
                    .setContentTitle(name)
                    .setContentText(call_msg)

                    .setAutoCancel(true)
                    .setSound(defaultSoundUri);


            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        } else if (jsonObject.optString("pushType").equalsIgnoreCase("videocall")) {
            String call_msg = name + " " + "Video Calling...";
            String type = "videoCall";
            dbHandler.InsertCalls(new CallsModel(zoeChatId, name, "", System.currentTimeMillis(), type, "1"));
            Intent call = new Intent(this, VideoChatViewActivity.class);
            call.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            call.putExtra("call", type);
            call.putExtra("zoeChatID", zoeChatId);
            call.putExtra("user_name", name);
            call.putExtra("image", image);
            call.putExtra("channelId", channel_id);
            call.putExtra("receive", "yes");
            this.startActivity(call);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setColor(this.getResources().getColor(R.color.colorPrimaryDark))
                    .setContentTitle(name)
                    .setContentText(call_msg)
                    .setVibrate(Const.URI.default_call_vibrate)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri);


            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }

        if (!jsonObject.optString("message").isEmpty()) {


            int single_value = Integer.parseInt(SharedHelper.getKey(getApplicationContext(), "single_vib_value"));
            int grp_value = Integer.parseInt(SharedHelper.getKey(getApplicationContext(), "group_vib_value"));
            int call_value = Integer.parseInt(SharedHelper.getKey(getApplicationContext(), "call_vib_value"));
            String light = SharedHelper.getKey(getApplicationContext(), "single_light_value");
            String g_light = SharedHelper.getKey(getApplicationContext(), "group_light_value");
            String in_sound = SharedHelper.getKey(getApplicationContext(), "play_sounds");


            Const.URI.default_single_vibrate = new long[]{single_value, single_value, single_value, single_value};
            Const.URI.default_group_vibrate = new long[]{grp_value, grp_value, grp_value, grp_value};
            Const.URI.default_call_vibrate = new long[]{call_value, call_value, call_value, call_value};
            Const.URI.single_light = Integer.parseInt(light);
            Const.URI.group_light = Integer.parseInt(g_light);
            Const.URI.inMessageTone = in_sound.equalsIgnoreCase("yes");
            Const.URI.default_single_message = Uri.parse(SharedHelper.getKey(this, "single_noti_tone"));
            Const.URI.default_group_message = Uri.parse(SharedHelper.getKey(this, "group_noti_tone"));


            Uri defaultSoundUri;
            long[] vib_val;
            int led_light;


            Log.d(TAG, "sendNotification: values:" + jsonObject.optString("chatRoomType"));

            JSONObject values = new JSONObject(jsonObject.optString("payload"));
            Log.d(TAG, "sendNotification: chat:" + values);
            if (values.optString("chatRoomType").equalsIgnoreCase("0")) {
                check_id = jsonObject.optString("fromId");
                defaultSoundUri = Const.URI.default_single_message;
                vib_val = Const.URI.default_single_vibrate;
                led_light = Const.URI.single_light;
            } else {
                check_id = values.optString("groupId");
                defaultSoundUri = Const.URI.default_group_message;
                vib_val = Const.URI.default_group_vibrate;
                led_light = Const.URI.group_light;

            }
            Log.e(TAG, "sendNotificationout: " + check_id + "," + current_id);


            if (Notifytime.equalsIgnoreCase("0") || Notifytime.equalsIgnoreCase("")) {
                long currenttime = System.currentTimeMillis();
                value = value + 1;
                if (!check_id.equalsIgnoreCase(current_id) || current_id.equalsIgnoreCase("")) {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                            PendingIntent.FLAG_ONE_SHOT);
                    NotificationCompat.InboxStyle inboxStyle =
                            new NotificationCompat.InboxStyle();
                    // Sets a title for the Inbox in expanded layout
                    inboxStyle.setBigContentTitle("Title - Notification");
                    inboxStyle.setSummaryText("You have " + value + " Notifications.");


                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setColor(this.getResources().getColor(R.color.colorPrimaryDark))
                            .setContentTitle(name)
                            .setLights(led_light, 500, 500)
                            .setContentText(jsonObject.optString("message"))
                            .setAutoCancel(true)
                            .setVibrate(vib_val)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);

                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                }
            }


        }
        if (jsonObject.optString("pushType").equalsIgnoreCase("acceptCall")) {

            EventBus.getDefault().post(new CallsEventModel("acceptCall",channel_id));

        }
        if (jsonObject.optString("pushType").equalsIgnoreCase("out")) {


            EventBus.getDefault().post(new CallsEventModel("endCall"));


        } else if (jsonObject.optString("pushType").equalsIgnoreCase("inc")) {


            EventBus.getDefault().post(new CallsEventModel("endCall"));


        }


        if (jsonObject.optString("pushType").equalsIgnoreCase("GroupAdd")) {

            String createdBy = dbHandler.GetUserName(jsonObject.optString("createdBy"));
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = Const.URI.default_group_message;
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setColor(this.getResources().getColor(R.color.colorPrimaryDark))
                    .setContentTitle(jsonObject.optString("groupName"))
                    .setContentText(createdBy + " added you")
                    .setAutoCancel(true)
                    .setVibrate(Const.URI.default_group_vibrate)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());


        }
    }


}
