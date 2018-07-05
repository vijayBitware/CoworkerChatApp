package com.bitware.coworker.baseUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bitware.coworker.activity.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by KrishnaDev on 2/18/17.
 */

public class NetworkReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {


        if (Utils.isNetworkAvailable(context)) {
            MyCommon.getInstance().network = true;
            Log.d("network", MyCommon.getInstance().network.toString());
//            if (MyCommon.getInstance().mainActivity) {
//                reConnectSocket(context);
//            } else if (MyCommon.getInstance().chatActivity) {
//                reConnectSocket(context);
//            }
        } else {
            MyCommon.getInstance().network = false;
            Log.d("network", MyCommon.getInstance().network.toString());

        }
    }

    private void reConnectSocket(Context context) {
        if (MainActivity.socket.connected()) {
            Log.d("socket_while_internet", "connect");
        } else {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", SharedHelper.getKey(context, "id"));
                MainActivity.socket.emit("online", jsonObject);
                MainActivity.socket.connect();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("socket_while_internet", "u want to connect");
        }
    }
}



