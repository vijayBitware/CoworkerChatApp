package com.bitware.coworker.baseUtils;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import io.socket.client.Socket;
import vc908.stickerfactory.StickersManager;
import vc908.stickerfactory.User;

//import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
//import cafe.adriel.androidaudioconverter.callback.ILoadCallback;

/**
 * Created by KrishnaDev on 1/10/17.
 */

public class AppController extends Application {

    public static Application application;
    public static Resources resources;
    private static Context mContext;
    private static Socket ChatSocket;



    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

    }



    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        mContext = this;

    }
    public static Context getContext(){
        return mContext;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        Fabric.with(this, new Crashlytics());
        StickersManager.initialize("c67495c61079060965c5c6b47064e452", this);
        Map<String, String> meta = new HashMap<>();
        meta.put(User.KEY_GENDER, User.GENDER_FEMALE);
        meta.put(User.KEY_AGE, "33");
        // Put your user id when you know it
        StickersManager.setUser(vc908.stickerfactory.utils.Utils.getDeviceId(this), meta);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        Log.d("Appcontroller", "start");
//        IO.Options opts = new IO.Options();
//        opts.forceNew = true;
//        opts.reconnection = true;
//        try {
//            ChatSocket = IO.socket(Const.chatSocketURL, opts);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//            Log.e("SOCKET.IO ", e.getMessage());
//        }

        //SocketSingleton.getInstance().SocketSingleton(this);

    }

//    public static Socket getSocket() {
//        return ChatSocket;
//    }



}
