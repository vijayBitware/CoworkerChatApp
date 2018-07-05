package com.bitware.coworker.baseUtils;

import android.content.Context;
import android.util.Log;

import io.socket.emitter.Emitter;
import io.socket.client.IO;
import io.socket.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

/**
 * Created by krishnadev o
 * n 14/05/17.
 */

public class SocketSingleton {

    private Context mContext;
    public Socket socket;

    public void SocketSingleton(Context context) {
        this.mContext = context;

        onConnectSocket();

    }

    public static SocketSingleton socketInstance = new SocketSingleton();

    public static SocketSingleton getInstance() {
        return socketInstance;
    }

    public void onConnectSocket() {
        IO.Options opts = new IO.Options();
        opts.forceNew = true;
        opts.reconnection = false;

        try {
            socket = IO.socket(Const.chatSocketURL, opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.e("SOCKET.IO ", e.getMessage());
        }
        if (socket.connected()) {
            Log.e("SingleTon", "socket_connect");
        } else {
            socket.on(Socket.EVENT_CONNECT, onConnect);
            socket.connect();
        }

        //onReceiveMsg();

    }

   /* private void onReceiveMsg() {
        socket.on(SharedHelper.getKey(mContext, "id") + ":receiveMessage", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                if (MyCommon.getInstance().mainActivity) {
                    JSONObject data = (JSONObject) args[0];
                    Log.e("Main_receive_data", data.toString());
                    Log.d("main_active", "true");
                    long deliveredTime = System.currentTimeMillis();
                    String groupName = "", create_by = "", sent_by = "";
//                    ReceiveMessage(data.optString("contentType"), data.optString("messageId"), data.optString("from"), data.optString("content"),
//                            Status.DELIVERED, data.optString("time"), String.valueOf(deliveredTime), "0", 0, 0, data.optString("to"), data.optString("latitude"), data.optString("longitude"), groupName, create_by, sent_by, data.optString("groupId"), data.optString("chatRoomType"));
                } else if (MyCommon.getInstance().chatActivity) {
                    Log.e("main_active", "false");
                }
            }
        });

    }
*/
    public void disconnectSocket() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", SharedHelper.getKey(mContext, "id"));
            socket.emit("offline", jsonObject);
            Log.e("offline", "" + jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        socket.disconnect();
    }


    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            /*MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {*/
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", SharedHelper.getKey(mContext, "id"));
                socket.emit("online", jsonObject);
                Log.e("singleTOn", "connect");

            } catch (JSONException e) {
                e.printStackTrace();
            }
           /*     }
            });*/

        }
    };

}
