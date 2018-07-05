package com.bitware.coworker.baseUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;
import com.bitware.coworker.DBHelper.DBHandler;

import java.io.IOException;

/**
 * Created by user on 27-05-2017.
 */

public class VideoRequestHandler extends RequestHandler {

    Context context;
    DBHandler dbHandler;
    public VideoRequestHandler(String msgId, Context context) {
        this.msgId = msgId;
        this.context=context;
        dbHandler=new DBHandler(context);
    }

    public String msgId;
    public String SCHEME_VIDEO="video";
    @Override
    public boolean canHandleRequest(Request data)
    {
        String scheme = data.uri.getScheme();
        return (SCHEME_VIDEO.equals(scheme));
    }

    @Override
    public Result load(Request data, int arg1) throws IOException
    {
        Bitmap bm = ThumbnailUtils.createVideoThumbnail(data.uri.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
        dbHandler.addImageBit(msgId, Utils.getBytes(bm));
        return new Result(bm, Picasso.LoadedFrom.DISK);
    }
}