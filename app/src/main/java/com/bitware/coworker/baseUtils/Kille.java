package com.bitware.coworker.baseUtils;

import android.app.Activity;

/**
 * Created by user on 18-09-2017.
 */

public class Kille {
    public static void activityKiller(Activity activity)
    {
        if(activity != null)
        {
            activity.finish();
        }
    }
}
