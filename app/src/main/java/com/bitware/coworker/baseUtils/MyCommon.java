package com.bitware.coworker.baseUtils;

/**
 * Created by User on 03-08-2016.
 */
public class MyCommon {
    private static MyCommon ourInstance = new MyCommon();

    public Boolean chatActivity = false;
    public Boolean mainActivity = false;
    public Boolean setGroupCreateName = false;
    public Boolean network = false;
    public Boolean isConnected = true;

    public static MyCommon getInstance() {
        return ourInstance;
    }

}
