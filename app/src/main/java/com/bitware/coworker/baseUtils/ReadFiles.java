package com.bitware.coworker.baseUtils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by KrishnaDev on 12/30/16.
 */
public class ReadFiles {

    public ReadFiles() {
        // TODO Auto-generated constructor stub
    }

    public static String readRawFileAsString(Context context, int rawFile)
            throws java.io.IOException {
        InputStream inputStream = context.getResources().openRawResource(
                rawFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream));
        StringBuffer result = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        reader.close();
        return result.toString();
    }
}

