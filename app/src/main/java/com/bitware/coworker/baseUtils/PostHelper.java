package com.bitware.coworker.baseUtils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.bitware.coworker.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by User on 13-06-2016.
 */
public class PostHelper {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();

    public PostHelper(String url, String jsonObject, int service_code,
                      Context context,
                      AsyncTaskCompleteListener asyncTaskCompleteListener) {
        postMethod(url, jsonObject, service_code, asyncTaskCompleteListener, context);
    }

    Context mContext;

    public PostHelper(Context context) {
        this.mContext = context;
    }


    @SuppressLint("NewApi")
    private void postMethod(String url, String object, int serviceCode,
                            AsyncTaskCompleteListener asyncTaskCompleteListener,
                            Context context) {

        System.out.println("*******url******"+url+"****param****"+object.toString());
        RequestBody requestBody = RequestBody.create(JSON, object);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        try (Response response = client.newCall(request).execute()) {
            System.out.println("****Ress*******"+response.toString());
            JSONObject jsonObject = new JSONObject(response.body().string());
            asyncTaskCompleteListener.onTaskCompleted(jsonObject, serviceCode);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            System.out.println("*******e*******"+e.toString());
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("message", context.getResources().getString(R.string.server_error));
                asyncTaskCompleteListener.onTaskCompleted(jsonObject, serviceCode);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            //Utils.showShortToast(context.getResources().getString(R.string.server_error), context);
        }

    }


    @SuppressLint("NewApi")
    public JSONObject Post(String url, String json) throws IOException, JSONException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();
        try (Response response = client.newCall(request).execute()) {
            return new JSONObject(response.body().string());
        }
    }


}
