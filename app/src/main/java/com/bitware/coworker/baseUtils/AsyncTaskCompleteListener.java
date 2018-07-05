package com.bitware.coworker.baseUtils;

import org.json.JSONObject;

/**
 * Created by KrishnaDev on 1/10/17.
 */
public interface AsyncTaskCompleteListener {
    void onTaskCompleted(JSONObject response, int serviceCode);
}
