package com.bitware.coworker.baseUtils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by KrishnaDev on 1/10/17.
 */

public class SharedHelper {

    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;

    public static void putKey(Context context, String Key, String Value) {
        sharedPreferences = context.getSharedPreferences("Cache", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(Key, Value);
        editor.commit();

    }


    public static String getKey(Context contextGetKey, String Key) {
        String Value="";
        try {
            sharedPreferences = contextGetKey.getSharedPreferences("Cache", Context.MODE_PRIVATE);
            Value = sharedPreferences.getString(Key, "");
        }catch (NullPointerException e)
        {
            e.printStackTrace();
        }
        return Value;

    }
    public static boolean putKey(Context mContext, String arrayName, boolean[] array) {

        SharedPreferences prefs = mContext.getSharedPreferences("preferencename", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(arrayName +"_size", array.length);

        for(int i=0;i<array.length;i++)
            editor.putBoolean(arrayName + "_" + i, array[i]);

        return editor.commit();
    }
    public static boolean[] getKey(String arrayName, Context mContext) {

        SharedPreferences prefs = mContext.getSharedPreferences("preferencename", 0);
        int size = prefs.getInt(arrayName + "_size", 0);
        boolean array[] = new boolean[size];
        for(int i=0;i<size;i++)
            array[i] = prefs.getBoolean(arrayName + "_" + i, false);

        return array;
    }

    public static void putHeader(Context context, String Key, String Value) {
        if (Value != null) {
            sharedPreferences = context.getSharedPreferences("Cache", Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.putString(Key, Value);
            editor.commit();
        } else {
            sharedPreferences = context.getSharedPreferences("Cache", Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.putString(Key, "0");
            editor.commit();
        }
    }

    public static String getHeader(Context contextGetKey, String Key) {
        sharedPreferences = contextGetKey.getSharedPreferences("Cache", Context.MODE_PRIVATE);
        String Value = sharedPreferences.getString(Key, "0");
        return Value;

    }

    public static void putInt(Context context, String Key, int Value) {
        sharedPreferences = context.getSharedPreferences("Cache", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(Key, String.valueOf(Value));
        editor.commit();

    }

    public static String getInt(Context contextGetKey, String Key) {
        sharedPreferences = contextGetKey.getSharedPreferences("Cache", Context.MODE_PRIVATE);
        String Value = sharedPreferences.getString(Key, "0");
        return Value;

    }


}
