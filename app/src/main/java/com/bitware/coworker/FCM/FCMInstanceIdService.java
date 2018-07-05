package com.bitware.coworker.FCM;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.bitware.coworker.baseUtils.SharedHelper;

/**
 * Created by KrishnaDev on 1/13/17.
 */

public class FCMInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFireBaseIDService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed_token: " + refreshedToken);
        SharedHelper.putKey(this, "token", refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Send any registration to your app's servers.

    }
}
