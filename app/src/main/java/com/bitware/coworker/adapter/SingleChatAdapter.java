package com.bitware.coworker.adapter;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.squareup.picasso.Picasso;
import com.bitware.coworker.DBHelper.DBHandler;
import com.bitware.coworker.R;
import com.bitware.coworker.activity.ChatActivity;
import com.bitware.coworker.activity.ImageViewer_Activity;
import com.bitware.coworker.activity.WebViewActivity;
import com.bitware.coworker.baseUtils.Const;
import com.bitware.coworker.baseUtils.SharedHelper;
import com.bitware.coworker.baseUtils.Utils;
import com.bitware.coworker.baseUtils.VideoRequestHandler;
import com.bitware.coworker.models.ChatMessages;
import com.bitware.coworker.models.ChatType;
import com.bitware.coworker.models.ChatsModel;
import com.bitware.coworker.models.Status;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.fresco.processors.BlurPostprocessor;
import vc908.stickerfactory.StickersManager;

import static com.bitware.coworker.activity.ChatActivity.singleChatAdapter;

//import com.zybertron.zoechat.baseUtils.AndroidBmpUtil;

public class SingleChatAdapter extends RecyclerView.Adapter<SingleChatAdapter.ViewHolder> {
    public static boolean is_in_action_mode = false;
    public static ArrayList<String> selectRemoveItem;
    public static ArrayList<Integer> selectRemoveItemPosition;
    public static ArrayList<String> select_position = new ArrayList<>();
    public static int Scrollposition;
    public static JSONArray mMessages;
    public static Handler handler = new Handler();
    private static Runnable globalrunnable;
    public Boolean isFirstStarred = true;
    MediaPlayer mPlayer = new MediaPlayer();
    int position1;
    VideoRequestHandler videoRequestHandler;
    Picasso picassoInstance;
    AmazonS3Client s3Client = new AmazonS3Client(new BasicAWSCredentials(Const.accessKey, Const.secretKey));
    TransferUtility transferUtility;
    TransferObserver transferObserver;
    String thumbnail, thumb_url;
    Boolean chatflag = false;
    private Context mContext;
    private String zoeChatId = "", ChatRoomType = "", GroupID = "";
    private int selectedCount;
    private String TAG = SingleChatAdapter.class.getSimpleName();


    public SingleChatAdapter(Context context, JSONArray jsonArray, String zoeChatID, String localChatRoomType, String groupId) {
        mMessages = jsonArray;
        this.mContext = context;
        this.zoeChatId = zoeChatID;
        this.ChatRoomType = localChatRoomType;
        this.GroupID = groupId;
        selectRemoveItem = new ArrayList<>();
        selectRemoveItemPosition = new ArrayList<>();
//        s3Client.setRegion(Region.getRegion(Regions.US_EAST_1));

        s3Client.setEndpoint(Const.ENDPOINT);


        Log.d("messages", "" + mMessages);


    }

    public SingleChatAdapter() {

    }

    public static void runOnUiThread(Runnable runnable) {
        globalrunnable = runnable;
        handler.post(globalrunnable);
    }

    public static Uri getImageContentUri(Context context, String filepath) {
        String filePath = filepath;
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, filePath);
            return context.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        }
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //doc

    public static void refresh(String msgId, String path) {
        Log.e("array_count", String.valueOf(mMessages.length()));
        for (int i = 0; i < mMessages.length(); i++) {
            String msgID = mMessages.optJSONObject(i).optString("msgId");
            if (msgId.equalsIgnoreCase(msgID)) {
                JSONObject jsonObject = mMessages.optJSONObject(i);
                jsonObject.remove("msg");
                try {
                    jsonObject.put("msg", path);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public static Bitmap getThumbnail(ContentResolver cr, String path) throws Exception {

        Cursor ca = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.MediaColumns._ID}, MediaStore.MediaColumns.DATA + "=?", new String[]{path}, null);
        if (ca != null && ca.moveToFirst()) {
            int id = ca.getInt(ca.getColumnIndex(MediaStore.MediaColumns._ID));
            ca.close();
            return MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
        }

        ca.close();
        return null;

    }

    public static int getPrimaryCOlor(Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, value, true);
        return value.data;
    }

    public static int getPrimaryDark(Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimaryDark, value, true);
        return value.data;
    }

    public static void customView(View v, int backgroundColor, int borderColor) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[]{50, 50, 50, 50, 50, 50, 50, 50});
        shape.setColor(backgroundColor);
        shape.setStroke(3, borderColor);
        v.setBackgroundDrawable(shape);
    }

    @SuppressLint("InflateParams")
    @SuppressWarnings("ConstantConditions")
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = null;
        if (viewType == ChatMessages.DATE_HEADER) {
            v = LayoutInflater.from(mContext).inflate(R.layout.chat_date_header, parent, false);
        }
        if (viewType == ChatMessages.SENDER_TEXT_MESSAGE) {
            v = LayoutInflater.from(mContext).inflate(R.layout.sender_text_item, parent, false);

        }
        if (viewType == ChatMessages.RECEIVER_TEXT__MESSAGE) {

            v = LayoutInflater.from(mContext).inflate(R.layout.receiver_text_item, parent, false);
        }
        if (viewType == ChatMessages.SENDER_IMAGE_MESSAGE) {
            v = LayoutInflater.from(mContext).inflate(R.layout.sender_image_item, null, false);


        }
        if (viewType == ChatMessages.RECEIVER_IMAGE_MESSAGE) {
            v = LayoutInflater.from(mContext).inflate(R.layout.receiver_image_item, parent, false);

        }
        if (viewType == ChatMessages.SENDER_LOCATION_MESSAGE) {
            v = LayoutInflater.from(mContext).inflate(R.layout.sender_map_item, parent, false);


        }
        if (viewType == ChatMessages.RECEIVER_LOCATION_MESSAGE) {
            v = LayoutInflater.from(mContext).inflate(R.layout.receiver_map_item, parent, false);

        }
        if (viewType == ChatMessages.SENDER_VIDEO_MESSAGE) {
            v = LayoutInflater.from(mContext).inflate(R.layout.sender_video_item, parent, false);


        }
        if (viewType == ChatMessages.RECEIVER_VIDEO_MESSAGE) {
            v = LayoutInflater.from(mContext).inflate(R.layout.receiver_video_item, parent, false);

        }
        if (viewType == ChatMessages.SENDER_CONTACT_MESSAGE) {
            v = LayoutInflater.from(mContext).inflate(R.layout.sender_contact_item, parent, false);


        }
        if (viewType == ChatMessages.RECEIVER_CONTACT_MESSAGE) {

            v = LayoutInflater.from(mContext).inflate(R.layout.receiver_contact_item, parent, false);
        }
        if (viewType == ChatMessages.CREATE_GROUP_MSG) {
            v = LayoutInflater.from(mContext).inflate(R.layout.chat_date_header, parent, false);


        }
        if (viewType == ChatMessages.SENDER_AUDIO_MESSAGE) {
            v = LayoutInflater.from(mContext).inflate(R.layout.sender_audio_item, parent, false);


        }
        if (viewType == ChatMessages.RECEIVER_AUDIO_MESSAGE) {
            v = LayoutInflater.from(mContext).inflate(R.layout.receiver_audio_item, parent, false);

        }
        if (viewType == ChatMessages.RECEIVER_DOC_MESSAGE) {
            v = LayoutInflater.from(mContext).inflate(R.layout.receiver_doc_item, parent, false);
        }
        if (viewType == ChatMessages.SENDER_DOC_MESSAGE) {
            v = LayoutInflater.from(mContext).inflate(R.layout.sender_doc_item, parent, false);

        }
        if (viewType == ChatMessages.RECEIVER_STICKER_MESSAGE) {
            v = LayoutInflater.from(mContext).inflate(R.layout.receiver_sticker_item, parent, false);
        }
        if (viewType == ChatMessages.SENDER_STICKER_MESSAGE) {
            v = LayoutInflater.from(mContext).inflate(R.layout.sender_sticker_item, parent, false);

        }
        v.setTag(viewType);


        return new ViewHolder(v);
    }

    private List<String> pullLinks(String text) {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "(http|ftp|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find()) {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }

        return containedUrls;
    }

    public void updateAdapter(JSONArray newlist) {
        mMessages = newlist;
        this.notifyDataSetChanged();
    }

    public void setIsUploaded(String s, String msgid) {
        for (int i = 0; i < mMessages.length(); i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject = mMessages.optJSONObject(i);
            if (jsonObject.optString("msgId").equalsIgnoreCase(msgid)) {
                try {
                    jsonObject.remove("upload");
                    jsonObject.put("upload", s);

                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressLint({"NewApi", "SetTextI18n"})
    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        final DBHandler dbHandler = new DBHandler(mContext);
        holder.setIsRecyclable(false);


        final JSONObject jsonObject = mMessages.optJSONObject(position);
        String selected = mMessages.optJSONObject(position).optString("isSelected");
        String starred = mMessages.optJSONObject(position).optString("isStarred");
        String shouldSign = mMessages.optJSONObject(position).optString("shouldSign");
        Log.d("onBindViewHolder: ", "jsonobjjjj:" + jsonObject);
        Log.d("onBindViewHolder: ", "selected:" + selected);
        int value = (int) holder.itemView.getTag();
        String type_val = "";
        if (value != ChatMessages.CREATE_GROUP_MSG) {
            if (value == ChatMessages.RECEIVER_AUDIO_MESSAGE || value == ChatMessages.RECEIVER_CONTACT_MESSAGE
                    || value == ChatMessages.RECEIVER_DOC_MESSAGE || value == ChatMessages.RECEIVER_VIDEO_MESSAGE || value == ChatMessages.RECEIVER_IMAGE_MESSAGE
                    || value == ChatMessages.RECEIVER_LOCATION_MESSAGE || value == ChatMessages.RECEIVER_TEXT__MESSAGE) {
                type_val = "receiver";
            } else {
                type_val = "sender";
            }
        }

        if (selected.equalsIgnoreCase("true")) {
            holder.itemView.setBackgroundResource(R.color.light_weight_gray);
        } else {
            holder.itemView.setBackgroundResource(0);
        }
        if (starred.equalsIgnoreCase("true")) {
            holder.star_image.setVisibility(View.VISIBLE);
        } else {
            holder.star_image.setVisibility(View.GONE);
        }


        String space = " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;" +
                "&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;";

        if (jsonObject.optString("userType").equalsIgnoreCase(ChatMessages.SENDER)) {
            try {
                double time = Double.valueOf(jsonObject.optString("sentTime"));
//                description= description.replaceAll("\\n", System.getProperty("line.separator"));

                holder.sender_txt.setText(jsonObject.optString("msg").replaceAll("\\n", System.getProperty("line.separator")) + Html.fromHtml(space));
                holder.sender_time_txt.setText(Utils.getDate((long) time, "hh:mm a"));
                setStatus(jsonObject, holder.sender_reply_status_image, mContext);
                Log.d("onBindViewHolder: ", "jsonObjectaswer:" + jsonObject.optString("showPreview"));
                if (jsonObject.optString("showPreview").equalsIgnoreCase("true") || jsonObject.optString("showPreview").equalsIgnoreCase("1")) {
                    holder.parent_layout.setVisibility(View.VISIBLE);
                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    final View content = inflater.inflate(
                            R.layout.link_preview, holder.parent_layout);
                    final LinearLayout infoWrap = (LinearLayout) content
                            .findViewById(R.id.info_wrap);


                    final ImageView imageSet = (ImageView) content
                            .findViewById(R.id.image_post_set);
                    final TextView titleTextView = (TextView) content
                            .findViewById(R.id.title);
                    final TextView urlTextView = (TextView) content
                            .findViewById(R.id.url);
                    final TextView descriptionTextView = (TextView) content
                            .findViewById(R.id.description);
                    urlTextView.setVisibility(View.GONE);

                    titleTextView.setText(jsonObject.optString("metaTitle"));
//                    urlTextView.setText(jsonObject.optString("metaDescription"));
                    Glide.with(mContext).load(jsonObject.optString("metaLogo")).into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            imageSet.setImageDrawable(resource);

                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            imageSet.setVisibility(View.GONE);
                            String logooo = jsonObject.optString("metaLogo");
                            if (logooo.length() > 0) {
                                if (logooo.substring(0, 3).equalsIgnoreCase("www")) {

                                } else {
                                    if (logooo.substring(0, 1).equalsIgnoreCase("/")) {

                                    } else {
                                        logooo = "/" + jsonObject.optString("metaLogo");
                                    }
                                }
                            }
                            String s = "";
                            try {
                                s = pullLinks(jsonObject.optString("msg")).get(0).toString() + logooo;
                            } catch (Exception e1) {

                            }
                            Glide.with(mContext).load(s).into(new SimpleTarget<GlideDrawable>() {
                                @Override
                                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                    imageSet.setVisibility(View.VISIBLE);
                                    imageSet.setImageDrawable(resource);
                                }

                                @Override
                                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                    super.onLoadFailed(e, errorDrawable);
                                    imageSet.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                    if (jsonObject.optString("metaDescription").equalsIgnoreCase("")) {
                        try {
                            descriptionTextView.setText(pullLinks(jsonObject.optString("msg")).get(0).toString());
                        } catch (Exception e) {

                        }
                    } else {
                        descriptionTextView.setText(jsonObject.optString("metaDescription"));
                    }
                } else {
                    holder.parent_layout.setVisibility(View.GONE);

                }

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        }

        if (jsonObject.optString("userType").equalsIgnoreCase(ChatMessages.RECEIVER)) {
            Log.d("binding_val", "onBindViewHolder: " + jsonObject.optString("msg") + "defualt:" + Html.fromHtml(jsonObject.optString("msg")));

            if (!jsonObject.optString("chatRoomType").equalsIgnoreCase("0")) {
                Log.d("onBindViewHolder: ", "" + jsonObject.optString("userName") + "--");

                String name = dbHandler.GetUserName(jsonObject.optString("userName"));
                holder.group_Receiver.setVisibility(View.VISIBLE);
                holder.group_Receiver.setText(name);
            } else {
                holder.group_Receiver.setVisibility(View.GONE);
            }

            try {
                double time = Double.valueOf(jsonObject.optString("sentTime"));
                holder.receiver_txt.setText(jsonObject.optString("msg").replaceAll("\\n", System.getProperty("line.separator")) + Html.fromHtml(space));

                holder.receiver_time_txt.setText(Utils.getDate((long) time, "hh:mm a"));

                if (jsonObject.optString("showPreview").equalsIgnoreCase("true") || jsonObject.optString("showPreview").equalsIgnoreCase("1")) {
                    holder.parent_layout.setVisibility(View.VISIBLE);
                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    JSONObject object = new JSONObject();
                    object.put("title", jsonObject.optString("metaTitle"));
                    object.put("description", jsonObject.optString("metaDescription"));
                    object.put("logo", jsonObject.optString("metaLogo"));
                    String url_db = "";
                    try {
                        url_db = pullLinks(jsonObject.optString("msg")).get(0).toString();
                    } catch (Exception e) {

                    }
                    object.put("url", url_db);

                    final View content = inflater.inflate(
                            R.layout.link_preview, holder.parent_layout);
                    final LinearLayout infoWrap = (LinearLayout) content
                            .findViewById(R.id.info_wrap);


                    final ImageView imageSet = (ImageView) content
                            .findViewById(R.id.image_post_set);
                    final TextView titleTextView = (TextView) content
                            .findViewById(R.id.title);
                    final TextView urlTextView = (TextView) content
                            .findViewById(R.id.url);
                    final TextView descriptionTextView = (TextView) content
                            .findViewById(R.id.description);
                    urlTextView.setVisibility(View.GONE);

                    titleTextView.setText(jsonObject.optString("metaTitle"));
//                    urlTextView.setText(jsonObject.optString("metaDescription"));
                    Glide.with(mContext).load(jsonObject.optString("metaLogo")).into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            imageSet.setImageDrawable(resource);

                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            imageSet.setVisibility(View.GONE);
                            String we = "";
                            try {
                                we = pullLinks(jsonObject.optString("msg")).get(0).toString() + jsonObject.optString("metaLogo");
                            } catch (Exception e1) {

                            }
                            Glide.with(mContext).load(we).into(new SimpleTarget<GlideDrawable>() {
                                @Override
                                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                    imageSet.setVisibility(View.VISIBLE);
                                    imageSet.setImageDrawable(resource);
                                }

                                @Override
                                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                    super.onLoadFailed(e, errorDrawable);
                                    imageSet.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                    if (jsonObject.optString("metaDescription").equalsIgnoreCase("")) {
                        descriptionTextView.setText(pullLinks(jsonObject.optString("msg")).get(0).toString());
                    } else {
                        descriptionTextView.setText(jsonObject.optString("metaDescription"));
                    }

                } else {
                    holder.parent_layout.setVisibility(View.GONE);

                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        if (jsonObject.optString("userType").equalsIgnoreCase(ChatMessages.SENDER_STICKER)) {
            StickersManager.with(mContext)
                    .loadSticker(jsonObject.optString("msg"))
                    .into((holder.sender_chat_sticker));
            try {
                double time = Double.valueOf(jsonObject.optString("sentTime"));
                holder.sender_time_txt.setText(Utils.getDate((long) time, "hh:mm a"));
                setStatus(jsonObject, holder.sender_reply_status_image, mContext);

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (jsonObject.optString("userType").equalsIgnoreCase(ChatMessages.RECEIVER_STICKER)) {


            StickersManager.with(mContext)
                    .loadSticker(jsonObject.optString("msg"))
                    .into((holder.receiver_chat_sticker));
            if (!jsonObject.optString("chatRoomType").equalsIgnoreCase("0")) {
                Log.d("onBindViewHolder: ", "" + jsonObject.optString("userName") + "--");

                String name = dbHandler.GetUserName(jsonObject.optString("userName"));
                holder.group_Receiver.setVisibility(View.VISIBLE);
                holder.group_Receiver.setText(name);
            } else {
                holder.group_Receiver.setVisibility(View.GONE);
            }

            try {
                double time = Double.valueOf(jsonObject.optString("sentTime"));
                holder.receiver_time_txt.setText(Utils.getDate((long) time, "hh:mm a"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        }


        //bind the video item
        if (jsonObject.optString("userType").equalsIgnoreCase(ChatMessages.SENDER_VIDEO)) {

            holder.upload_btn.setImageResource(R.drawable.ic_file_upload);

            try {
                double time = Double.valueOf(jsonObject.optString("sentTime"));
                holder.sender_time_txt.setText(Utils.getDate((long) time, "hh:mm a"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }


            final String url = jsonObject.optString("msg");

            if (dbHandler.getBitmap(jsonObject.optString("msgId")) != null) {
                Bitmap bitmap = Utils.getImage(dbHandler.getBitmap(jsonObject.optString("msgId")));
                holder.sender_video_image.setImageBitmap(bitmap);

            } else {
                videoRequestHandler = new VideoRequestHandler(jsonObject.optString("msgId"), mContext);
                picassoInstance = new Picasso.Builder(mContext.getApplicationContext())
                        .addRequestHandler(videoRequestHandler)
                        .build();

                picassoInstance.load(videoRequestHandler.SCHEME_VIDEO + ":" + url).into(holder.sender_video_image);
            }
            setStatus(jsonObject, holder.sender_reply_status_image, mContext);

            holder.sender_video_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File file = new File(url);
                    if (file.exists()) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri uri = Uri.fromFile(file);
                        intent.setDataAndType(uri, URLConnection.guessContentTypeFromName(uri.toString()));
                        mContext.startActivity(intent);
                    } else {
                        Toast.makeText(mContext, "Video file does not exist", Toast.LENGTH_LONG).show();
                    }

                }
            });


            String upload_status = jsonObject.optString("upload");

            if (upload_status.equalsIgnoreCase("uploading")) {
                holder.image_progress.setVisibility(View.VISIBLE);
                holder.upload_btn.setVisibility(View.GONE);
                holder.upload_cancel_btn.setVisibility(View.VISIBLE);
                holder.play_audio.setVisibility(View.GONE);

            } else if (upload_status.equalsIgnoreCase("uploaded")) {
                holder.image_progress.setVisibility(View.GONE);
                holder.upload_layout.setVisibility(View.VISIBLE);
                holder.play_audio.setVisibility(View.VISIBLE);
                holder.upload_btn.setVisibility(View.GONE);
                holder.upload_cancel_btn.setVisibility(View.GONE);


            } else if (upload_status.equalsIgnoreCase("uploadFailed")) {
                holder.image_progress.setVisibility(View.GONE);
                holder.upload_btn.setVisibility(View.VISIBLE);
                holder.upload_cancel_btn.setVisibility(View.GONE);
                holder.play_audio.setVisibility(View.GONE);

            }


            holder.upload_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    holder.image_progress.setVisibility(View.VISIBLE);
                    holder.upload_btn.setVisibility(View.GONE);
                    holder.upload_cancel_btn.setVisibility(View.VISIBLE);

                    UploadImages(jsonObject.optString("msg"), holder.image_progress, jsonObject.optString("msgId"), jsonObject.optString("userId"),
                            holder.upload_layout, holder.sender_reply_status_image, holder.upload_btn, holder.upload_cancel_btn, ChatType.image, jsonObject.optString("chatRoomType"), jsonObject.optString("groupId"));

                }
            });
            final String amz_id = dbHandler.getAmazonId(jsonObject.optString("msgId"));


            holder.upload_cancel_btn.setOnClickListener(new View.OnClickListener() {
                @SuppressWarnings("EqualsBetweenInconvertibleTypes")
                @Override
                public void onClick(View view) {

                    ChatActivity.transferUtility.cancel(Integer.parseInt(amz_id));
                    holder.image_progress.setVisibility(View.GONE);
                    holder.upload_btn.setVisibility(View.VISIBLE);
                    holder.upload_cancel_btn.setVisibility(View.GONE);
                }
            });

        }
        if (jsonObject.optString("userType").equalsIgnoreCase(ChatMessages.SENDER_AUDIO)) {

            holder.play_audio.setVisibility(View.GONE);

            try {
                double time = Double.valueOf(jsonObject.optString("sentTime"));
                holder.sender_time_txt.setText(Utils.getDate((long) time, "hh:mm a"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }


            final String url = jsonObject.optString("msg");
            Log.d("onBindViewHolder: ", "urlUpload:" + jsonObject.optString("msg"));

            String upload_status = jsonObject.optString("upload");

            Log.e(TAG, "onBindViewHoldestatus" + upload_status);

            if (upload_status.equalsIgnoreCase("uploading")) {
                holder.image_progress.setVisibility(View.VISIBLE);
                holder.upload_btn.setVisibility(View.GONE);
                holder.upload_cancel_btn.setVisibility(View.VISIBLE);
                holder.play_audio.setVisibility(View.GONE);
                holder.uploaded_audio.setVisibility(View.GONE);
                holder.upload_layout.setVisibility(View.VISIBLE);


            } else if (upload_status.equalsIgnoreCase("uploaded")) {
                try {
                    holder.image_progress.setVisibility(View.GONE);
                    holder.upload_layout.setVisibility(View.GONE);
                    holder.upload_btn.setVisibility(View.GONE);
                    holder.uploaded_audio.setVisibility(View.VISIBLE);
                    holder.play_audio.setVisibility(View.VISIBLE);

                    File file = new File(url);
                    final String mediaPath = Uri.parse(url).getPath();
                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    mmr.setDataSource(mediaPath);
                    String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    mmr.release();
                    holder.seek_bar.setMax(Integer.parseInt(duration));


                    if (jsonObject.optString("isPlaying").equalsIgnoreCase("0")) {
                        holder.seek_bar.setProgress(0);
                        holder.play_audio.setVisibility(View.VISIBLE);
                        holder.pause_audio.setVisibility(View.GONE);


                    } else if (jsonObject.optString("isPlaying").equalsIgnoreCase("2")) {
                        holder.play_audio.setVisibility(View.VISIBLE);
                        holder.pause_audio.setVisibility(View.GONE);
                        holder.seek_bar.setProgress(Integer.parseInt(jsonObject.optString("playingStatus")));

                    } else {
                        holder.play_audio.setVisibility(View.GONE);
                        holder.pause_audio.setVisibility(View.VISIBLE);
                        holder.seek_bar.setProgress(Integer.parseInt(jsonObject.optString("playingStatus")));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (upload_status.equalsIgnoreCase("uploadFailed")) {

                holder.image_progress.setVisibility(View.GONE);
                holder.upload_btn.setVisibility(View.VISIBLE);
                holder.upload_cancel_btn.setVisibility(View.GONE);
                holder.play_audio.setVisibility(View.GONE);
                holder.uploaded_audio.setVisibility(View.GONE);

            }


            final playerHandler playerHandler = new playerHandler();


            holder.play_audio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mediaPath = Uri.parse(url).getPath();
                    holder.play_audio.setVisibility(View.GONE);
                    holder.pause_audio.setVisibility(View.VISIBLE);
                    if (jsonObject.optString("isPlaying").equalsIgnoreCase("0")) {
                        playerHandler.stopplaying();
                        handler.removeCallbacksAndMessages(globalrunnable);
                        playerHandler.startPlaying(holder.seek_bar, mediaPath, position);

                    } else {
                        handler.removeCallbacksAndMessages(globalrunnable);
                        playerHandler.resumeplaying(holder.seek_bar, position, mediaPath);
                    }


                }
            });


            holder.pause_audio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.play_audio.setVisibility(View.VISIBLE);
                    holder.pause_audio.setVisibility(View.GONE);

                    handler.removeCallbacksAndMessages(globalrunnable);
                    playerHandler.pausePlaying(position, holder.seek_bar);


                }
            });
            setStatus(jsonObject, holder.sender_reply_status_image, mContext);


            holder.upload_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.image_progress.setVisibility(View.VISIBLE);
                    holder.upload_btn.setVisibility(View.GONE);
                    holder.upload_cancel_btn.setVisibility(View.VISIBLE);
                    holder.uploaded_audio.setVisibility(View.GONE);

                    UploadImages(jsonObject.optString("msg"), holder.image_progress, jsonObject.optString("msgId"), jsonObject.optString("userId"),
                            holder.upload_layout, holder.sender_reply_status_image, holder.upload_btn, holder.upload_cancel_btn, ChatType.audio, jsonObject.optString("chatRoomType"), jsonObject.optString("groupId"), holder.uploaded_audio);

                }
            });

            holder.upload_cancel_btn.setOnClickListener(new View.OnClickListener() {
                @SuppressWarnings("EqualsBetweenInconvertibleTypes")
                @Override
                public void onClick(View view) {
                    String amz_id = dbHandler.getAmazonId(jsonObject.optString("msgId"));

                    ChatActivity.transferUtility.cancel(Integer.parseInt(amz_id));

                    holder.image_progress.setVisibility(View.GONE);
                    holder.upload_btn.setVisibility(View.VISIBLE);
                    holder.upload_cancel_btn.setVisibility(View.GONE);
                    holder.uploaded_audio.setVisibility(View.GONE);

                }
            });


        }


        if (jsonObject.optString("userType").equalsIgnoreCase(ChatMessages.SENDER_DOC)) {

            holder.upload_btn.setImageResource(R.drawable.ic_file_upload);

            try {
                double time = Double.valueOf(jsonObject.optString("sentTime"));
                holder.sender_time_txt.setText(Utils.getDate((long) time, "hh:mm a"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }


            final String url = jsonObject.optString("msg");
            setStatus(jsonObject, holder.sender_reply_status_image, mContext);

            holder.view_document.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    File file = new File(getRealPathFromURI(mContext, Uri.parse(url)));
                    Log.d("onClick_doc: ", url);


                    Intent intent = new Intent(mContext, WebViewActivity.class);
                    intent.putExtra("doc_url", jsonObject.optString("msg"));
                    mContext.startActivity(intent);
//                    }

                }
            });

            String name = FilenameUtils.removeExtension(URLUtil.guessFileName(url, null, null));

            holder.sender_txt.setText(name);
            Log.d("onBindViewHolderdoc: ", "f_name_wext:" + name);


            String upload_status = jsonObject.optString("upload");

            if (upload_status.equalsIgnoreCase("uploading")) {
                String f_path = getRealPathFromURI(mContext, Uri.parse(url));
                File file = new File(f_path);
                name = file.getName();
                holder.sender_txt.setText(name);
                holder.image_progress.setVisibility(View.VISIBLE);
                holder.upload_btn.setVisibility(View.GONE);
                holder.upload_cancel_btn.setVisibility(View.VISIBLE);


            } else if (upload_status.equalsIgnoreCase("uploaded")) {
                holder.image_progress.setVisibility(View.GONE);
                holder.upload_layout.setVisibility(View.GONE);
                holder.upload_btn.setVisibility(View.VISIBLE);


            } else if (upload_status.equalsIgnoreCase("uploadFailed")) {
                String f_path = getRealPathFromURI(mContext, Uri.parse(url));
                File file = new File(f_path);
                name = file.getName();
                holder.sender_txt.setText(name);
                holder.image_progress.setVisibility(View.GONE);
                holder.upload_btn.setVisibility(View.VISIBLE);
                holder.upload_cancel_btn.setVisibility(View.GONE);

            }


            holder.upload_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    holder.image_progress.setVisibility(View.VISIBLE);
                    holder.upload_btn.setVisibility(View.GONE);
                    holder.upload_cancel_btn.setVisibility(View.VISIBLE);

                    UploadImages(jsonObject.optString("msg"), holder.image_progress, jsonObject.optString("msgId"), jsonObject.optString("userId"),
                            holder.upload_layout, holder.sender_reply_status_image, holder.upload_btn, holder.upload_cancel_btn, ChatType.document, jsonObject.optString("chatRoomType"), jsonObject.optString("groupId"));

                }
            });
            final String amz_id = dbHandler.getAmazonId(jsonObject.optString("msgId"));

            holder.upload_cancel_btn.setOnClickListener(new View.OnClickListener() {
                @SuppressWarnings("EqualsBetweenInconvertibleTypes")
                @Override
                public void onClick(View view) {
                    ChatActivity.transferUtility.cancel(Integer.parseInt(amz_id));
                    int id = transferObserver.getId();
                    transferUtility.cancel(id);
                    holder.image_progress.setVisibility(View.GONE);
                    holder.upload_btn.setVisibility(View.VISIBLE);
                    holder.upload_cancel_btn.setVisibility(View.GONE);


                }
            });


        }


        //bind the receiver video
        if (jsonObject.optString("userType").equalsIgnoreCase(ChatMessages.RECEIVER_VIDEO)) {

            if (!jsonObject.optString("chatRoomType").equalsIgnoreCase("0")) {
                String name = dbHandler.GetUserName(jsonObject.optString("userName"));
                holder.group_Receiver.setVisibility(View.VISIBLE);
                holder.group_Receiver.setText(name);
            } else {
                holder.group_Receiver.setVisibility(View.GONE);
            }

            try {
                double time1 = Double.valueOf(jsonObject.optString("sentTime"));
                holder.receiver_time_txt.setText(Utils.getDate((long) time1, "hh:mm a"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            double time1 = Double.valueOf(jsonObject.optString("sentTime"));
            holder.receiver_time_txt.setText(Utils.getDate((long) time1, "hh:mm a"));
            Log.d("onBindViewHolder: ", "valuesfromjson:" + jsonObject);

            final String url = jsonObject.optString("msg");

            final String downloadStatus = dbHandler.isDownloadStatus(jsonObject.optString("msgId"));
            if (downloadStatus.equalsIgnoreCase("1")) {
                holder.downLoad_Cancel.setVisibility(View.GONE);
                holder.download_btn.setVisibility(View.GONE);
                holder.download_layout.setVisibility(View.VISIBLE);
                holder.play_audio.setVisibility(View.VISIBLE);
                holder.image_progress.setVisibility(View.GONE);
                holder.play_layput.setVisibility(View.VISIBLE);
                Log.d("onBindViewHolder: ", "after_video:" + url);
                Bitmap bitmap = null;
                try {
                    bitmap = Utils.retriveVideoFrameFromVideo(url);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

                holder.receiver_video_image.setImageBitmap(bitmap);


            } else if (downloadStatus.equalsIgnoreCase("0")) {

                String in_type = Utils.getinternettype(mContext);
                if (in_type.equalsIgnoreCase("WiFi")) {
                    boolean[] booleen = SharedHelper.getKey("wifi_data", mContext);
                    boolean shouldDownload = booleen[2];
                    {
                        if (shouldDownload) {
                            holder.download_btn.setVisibility(View.GONE);
                            holder.image_progress.setVisibility(View.VISIBLE);
                            DownloadVideo(jsonObject.optString("msg"), jsonObject.optString("msgId"), holder.receiver_video_image,
                                    holder.image_progress, holder.download_layout, holder.download_btn, holder.downLoad_Cancel);
                        }
                    }
                } else {
                    if (Utils.isDataRoamingEnabled(mContext)) {
                        boolean[] booleen = SharedHelper.getKey("roaming_data", mContext);
                        boolean shouldDownload = booleen[2];
                        {
                            if (shouldDownload) {
                                holder.download_btn.setVisibility(View.GONE);
                                DownloadVideo(jsonObject.optString("msg"), jsonObject.optString("msgId"), holder.receiver_video_image,
                                        holder.image_progress, holder.download_layout, holder.download_btn, holder.downLoad_Cancel);
                            }
                        }
                    } else {
                        boolean[] booleen = SharedHelper.getKey("mobile_data", mContext);
                        boolean shouldDownload = booleen[2];
                        {
                            if (shouldDownload) {
                                holder.download_btn.setVisibility(View.GONE);
                                DownloadVideo(jsonObject.optString("msg"), jsonObject.optString("msgId"), holder.receiver_video_image,
                                        holder.image_progress, holder.download_layout, holder.download_btn, holder.downLoad_Cancel);
                            }
                        }
                    }
                }


                File file = new File(jsonObject.optString("msg"));
                final String f_name = FilenameUtils.removeExtension(file.getName());
                String fileName = Const.amazons3ServerImagePath + "thumb_" + f_name + ".jpg";
                Log.d("onBindViewHolder: ", "file_name:" + f_name + ",with:" + fileName);

                ResizeOptions options = new ResizeOptions(350, 350);

                BlurPostprocessor blurPostprocessor = new BlurPostprocessor(mContext);

                ImageRequest request = ImageRequestBuilder
                        .newBuilderWithSource(Uri.parse(fileName))
                        .setPostprocessor(blurPostprocessor)
                        .setProgressiveRenderingEnabled(true).setResizeOptions(options)
                        .build();
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(request)
                        .setOldController(holder.receiver_video_image.getController())
                        .build();
                holder.receiver_video_image.setController(controller);

                holder.play_layput.setVisibility(View.GONE);
                holder.play_audio.setVisibility(View.GONE);
                holder.downLoad_Cancel.setVisibility(View.GONE);
                holder.download_btn.setVisibility(View.VISIBLE);
                holder.image_progress.setVisibility(View.GONE);
            }


            holder.download_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("msg_Id", jsonObject.optString("msgId"));
                    DownloadVideo(jsonObject.optString("msg"), jsonObject.optString("msgId"), holder.receiver_video_image,
                            holder.image_progress, holder.download_layout, holder.download_btn, holder.downLoad_Cancel);
                }
            });
            holder.downLoad_Cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int id = transferObserver.getId();
                    transferUtility.cancel(id);
                    holder.downLoad_Cancel.setVisibility(View.GONE);
                    holder.download_btn.setVisibility(View.VISIBLE);
                    holder.image_progress.setVisibility(View.GONE);


                }
            });
            holder.receiver_video_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (downloadStatus.equalsIgnoreCase("1")) {
                        File file = new File(url);
                        if (file.exists()) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            Uri uri = Uri.fromFile(file);
                            intent.setDataAndType(uri, URLConnection.guessContentTypeFromName(uri.toString()));
                            mContext.startActivity(intent);
                        } else {
                            Toast.makeText(mContext, "Video file does not exist", Toast.LENGTH_LONG).show();
                        }

                    } else if (downloadStatus.equalsIgnoreCase("0")) {
                        DownloadVideo(jsonObject.optString("msg"), jsonObject.optString("msgId"), holder.receiver_video_image,
                                holder.image_progress, holder.download_layout, holder.download_btn, holder.downLoad_Cancel);
                        holder.download_btn.setVisibility(View.GONE);
                        holder.downLoad_Cancel.setVisibility(View.VISIBLE);
                    }
                }
            });

        }


        if (jsonObject.optString("userType").equalsIgnoreCase(ChatMessages.RECEIVER_AUDIO)) {
            try {
                Log.d("onBindViewHolder: ", "jsonobjjjj:" + jsonObject);

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    holder.image_progress.setIndeterminateTintList(ColorStateList.valueOf(getPrimaryCOlor(mContext)));
                }


                if (!jsonObject.optString("chatRoomType").equalsIgnoreCase("0")) {
                    String name = dbHandler.GetUserName(jsonObject.optString("userName"));
                    holder.group_Receiver.setVisibility(View.VISIBLE);
                    holder.group_Receiver.setText(name);
                } else {
                    holder.group_Receiver.setVisibility(View.GONE);
                }

                try {
                    double time1 = Double.valueOf(jsonObject.optString("sentTime"));
                    holder.receiver_time_txt.setText(Utils.getDate((long) time1, "hh:mm a"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                double time1 = Double.valueOf(jsonObject.optString("sentTime"));
                holder.receiver_time_txt.setText(Utils.getDate((long) time1, "hh:mm a"));

                final String url = jsonObject.optString("msg");
                final playerHandler playerHandler = new playerHandler();


                final String downloadStatus = dbHandler.isDownloadStatus(jsonObject.optString("msgId"));
                if (downloadStatus.equalsIgnoreCase("1")) {
                    holder.download_layout.setVisibility(View.GONE);
                    holder.downLoad_Cancel.setVisibility(View.GONE);
                    holder.download_btn.setVisibility(View.GONE);
                    holder.image_progress.setVisibility(View.GONE);
                    holder.downloaded_audio.setVisibility(View.VISIBLE);
                    final String mediaPath = Uri.parse(url).getPath();
                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    mmr.setDataSource(mediaPath);
                    String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    mmr.release();
                    try {
                        holder.seek_bar.setMax(Integer.parseInt(duration));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, "onBindViewHolder: " + Integer.parseInt(duration));
                    }
                    try {
                        if (jsonObject.optString("isPlaying").equalsIgnoreCase("0")) {
                            holder.seek_bar.setProgress(0);
                            holder.play_audio.setVisibility(View.VISIBLE);
                            holder.pause_audio.setVisibility(View.GONE);


                        } else if (jsonObject.optString("isPlaying").equalsIgnoreCase("2")) {
                            holder.play_audio.setVisibility(View.VISIBLE);
                            holder.pause_audio.setVisibility(View.GONE);
                            if (jsonObject.optString("playingStatus").trim().length() == 0) {
                                holder.seek_bar.setProgress(0);

                            } else {
                                holder.seek_bar.setProgress(Integer.parseInt(jsonObject.optString("playingStatus")));
                            }
                        } else {
                            holder.play_audio.setVisibility(View.GONE);
                            holder.pause_audio.setVisibility(View.VISIBLE);
                            holder.seek_bar.setProgress(Integer.parseInt(jsonObject.optString("playingStatus")));

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (downloadStatus.equalsIgnoreCase("0")) {
                    try {
                        holder.download_layout.setVisibility(View.VISIBLE);
                        holder.pause_audio.setVisibility(View.GONE);
                        holder.play_audio.setVisibility(View.GONE);
                        String in_type = Utils.getinternettype(mContext);
                        if (in_type.equalsIgnoreCase("WiFi")) {

                            boolean[] booleen = SharedHelper.getKey("wifi_data", mContext);
                            boolean shouldDownload = booleen[1];
                            {
                                if (shouldDownload) {
                                    holder.download_btn.setVisibility(View.GONE);
                                    DownloadAudio(jsonObject.optString("msg"), jsonObject.optString("msgId"),
                                            holder.image_progress, holder.download_layout, holder.download_btn, holder.downLoad_Cancel, holder.downloaded_audio);
                                } else {
                                    holder.downLoad_Cancel.setVisibility(View.GONE);
                                    holder.download_btn.setVisibility(View.VISIBLE);
                                    holder.image_progress.setVisibility(View.GONE);
                                    holder.downloaded_audio.setVisibility(View.GONE);

                                }
                            }
                        } else {
                            if (Utils.isDataRoamingEnabled(mContext)) {
                                boolean[] booleen = SharedHelper.getKey("roaming_data", mContext);
                                boolean shouldDownload = booleen[1];
                                {
                                    if (shouldDownload) {
                                        holder.download_btn.setVisibility(View.GONE);
                                        DownloadAudio(jsonObject.optString("msg"), jsonObject.optString("msgId"),
                                                holder.image_progress, holder.download_layout, holder.download_btn, holder.downLoad_Cancel, holder.downloaded_audio);
                                    } else {
                                        holder.downLoad_Cancel.setVisibility(View.GONE);
                                        holder.download_btn.setVisibility(View.VISIBLE);
                                        holder.image_progress.setVisibility(View.GONE);
                                        holder.downloaded_audio.setVisibility(View.GONE);

                                    }
                                }
                            } else {
                                boolean[] booleen = SharedHelper.getKey("mobile_data", mContext);
                                boolean shouldDownload = booleen[1];
                                {
                                    if (shouldDownload) {
                                        holder.download_btn.setVisibility(View.GONE);
                                        DownloadAudio(jsonObject.optString("msg"), jsonObject.optString("msgId"),
                                                holder.image_progress, holder.download_layout, holder.download_btn, holder.downLoad_Cancel, holder.downloaded_audio);
                                    } else {
                                        holder.downLoad_Cancel.setVisibility(View.GONE);
                                        holder.download_btn.setVisibility(View.VISIBLE);
                                        holder.image_progress.setVisibility(View.GONE);
                                        holder.downloaded_audio.setVisibility(View.GONE);

                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                holder.download_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("msg_Id", jsonObject.optString("msgId"));
                        DownloadAudio(jsonObject.optString("msg"), jsonObject.optString("msgId"),
                                holder.image_progress, holder.download_layout, holder.download_btn, holder.downLoad_Cancel, holder.downloaded_audio);
                    }
                });
                holder.downLoad_Cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int id = transferObserver.getId();
                        transferUtility.cancel(id);
                        holder.downLoad_Cancel.setVisibility(View.GONE);
                        holder.download_btn.setVisibility(View.VISIBLE);
                        holder.image_progress.setVisibility(View.GONE);


                    }
                });

                holder.play_audio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String mediaPath = Uri.parse(url).getPath();
                        holder.play_audio.setVisibility(View.GONE);
                        holder.pause_audio.setVisibility(View.VISIBLE);
                        if (jsonObject.optString("isPlaying").equalsIgnoreCase("0")) {
                            playerHandler.stopplaying();
                            handler.removeCallbacksAndMessages(globalrunnable);
                            playerHandler.startPlaying(holder.seek_bar, mediaPath, position);


                        } else {
                            handler.removeCallbacksAndMessages(globalrunnable);
                            playerHandler.resumeplaying(holder.seek_bar, position, mediaPath);
                        }


                    }
                });


                holder.pause_audio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.play_audio.setVisibility(View.VISIBLE);
                        holder.pause_audio.setVisibility(View.GONE);

                        handler.removeCallbacksAndMessages(globalrunnable);
                        playerHandler.pausePlaying(position, holder.seek_bar);


                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (jsonObject.optString("userType").equalsIgnoreCase(ChatMessages.RECEIVER_DOC)) {

            if (!jsonObject.optString("chatRoomType").equalsIgnoreCase("0")) {
                String name = dbHandler.GetUserName(jsonObject.optString("userName"));
                holder.group_Receiver.setVisibility(View.VISIBLE);
                holder.group_Receiver.setText(name);
            } else {
                holder.group_Receiver.setVisibility(View.GONE);
            }

            try {
                double time1 = Double.valueOf(jsonObject.optString("sentTime"));
                holder.receiver_time_txt.setText(Utils.getDate((long) time1, "hh:mm a"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            double time1 = Double.valueOf(jsonObject.optString("sentTime"));
            holder.receiver_time_txt.setText(Utils.getDate((long) time1, "hh:mm a"));

            final String url = jsonObject.optString("msg");
            Log.d("onBindViewHolderdoc: ", "url:" + url);


            Log.d("onBindViewHolderdoc: ", "f_name_ext:" + URLUtil.guessFileName(url, null, null));

            String name = FilenameUtils.removeExtension(URLUtil.guessFileName(url, null, null));

            holder.sender_txt.setText(name);
            Log.d("onBindViewHolderdoc: ", "f_name_wext:" + name);


            final String downloadStatus = dbHandler.isDownloadStatus(jsonObject.optString("msgId"));
            if (downloadStatus.equalsIgnoreCase("1")) {
                holder.downLoad_Cancel.setVisibility(View.GONE);
                holder.download_btn.setVisibility(View.GONE);
                holder.image_progress.setVisibility(View.GONE);
                holder.download_layout.setVisibility(View.GONE);

            } else if (downloadStatus.equalsIgnoreCase("0")) {
                String in_type = Utils.getinternettype(mContext);
                if (in_type.equalsIgnoreCase("WiFi")) {
                    boolean[] booleen = SharedHelper.getKey("wifi_data", mContext);
                    boolean shouldDownload = booleen[3];
                    {
                        if (shouldDownload) {
                            holder.download_btn.setVisibility(View.GONE);
                            DownloadDocument(jsonObject.optString("msg"), jsonObject.optString("msgId"),
                                    holder.image_progress, holder.download_layout, holder.download_btn, holder.downLoad_Cancel, holder.downloaded_audio);
                        }
                    }
                } else {
                    if (Utils.isDataRoamingEnabled(mContext)) {
                        boolean[] booleen = SharedHelper.getKey("roaming_data", mContext);
                        boolean shouldDownload = booleen[3];
                        {
                            if (shouldDownload) {
                                holder.download_btn.setVisibility(View.GONE);
                                DownloadDocument(jsonObject.optString("msg"), jsonObject.optString("msgId"),
                                        holder.image_progress, holder.download_layout, holder.download_btn, holder.downLoad_Cancel, holder.downloaded_audio);
                            }
                        }
                    } else {
                        boolean[] booleen = SharedHelper.getKey("mobile_data", mContext);
                        boolean shouldDownload = booleen[3];
                        {
                            if (shouldDownload) {
                                holder.download_btn.setVisibility(View.GONE);
                                DownloadDocument(jsonObject.optString("msg"), jsonObject.optString("msgId"),
                                        holder.image_progress, holder.download_layout, holder.download_btn, holder.downLoad_Cancel, holder.downloaded_audio);
                            }
                        }
                    }
                }


                holder.downLoad_Cancel.setVisibility(View.GONE);
                holder.download_btn.setVisibility(View.VISIBLE);
                holder.image_progress.setVisibility(View.GONE);

            }

            holder.download_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("msg_Id", jsonObject.optString("msgId"));
                    Log.d("onClick: ", "msg:docu:" + jsonObject.optString("msg"));
                    DownloadDocument(jsonObject.optString("msg"), jsonObject.optString("msgId"),
                            holder.image_progress, holder.download_layout, holder.download_btn, holder.downLoad_Cancel, holder.downloaded_audio);
                }
            });
            holder.downLoad_Cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int id = transferObserver.getId();
                    transferUtility.cancel(id);
                    holder.downLoad_Cancel.setVisibility(View.GONE);
                    holder.download_btn.setVisibility(View.VISIBLE);
                    holder.image_progress.setVisibility(View.GONE);


                }
            });
            holder.view_document.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    if (downloadStatus.equalsIgnoreCase("1")) {

                        Intent intent = new Intent(mContext, WebViewActivity.class);
                        intent.putExtra("doc_url", jsonObject.optString("msg"));
                        mContext.startActivity(intent);


                    } else if (downloadStatus.equalsIgnoreCase("0")) {
                        DownloadDocument(jsonObject.optString("msg"), jsonObject.optString("msgId"),
                                holder.image_progress, holder.download_layout, holder.download_btn, holder.downLoad_Cancel, holder.downloaded_audio);
                        holder.download_btn.setVisibility(View.GONE);
                        holder.downLoad_Cancel.setVisibility(View.VISIBLE);
                    }
                }
            });

        }

        //location layout field
        if (jsonObject.optString("userType").equalsIgnoreCase(ChatMessages.SENDER_LOCATION)) {
            try {
                double time = Double.valueOf(jsonObject.optString("sentTime"));
                Log.d("onBindViewHolder: ", "Locationimage:" + jsonObject.optString("msg"));
                if (jsonObject.optString("msg").equalsIgnoreCase(" ") || jsonObject.optString("msg").equalsIgnoreCase("")) {
//                    Picasso.with(mContext).load(R.drawable.ic_account_circle).error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.sender_map_image);
                    Glide.with(mContext).load(R.drawable.ic_account_circle).error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.sender_map_image);
                } else {
                    Glide.with(mContext).load(jsonObject.optString("msg")).error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.sender_map_image);
//                    Picasso.with(mContext).load(jsonObject.optString("msg")).into(holder.sender_map_image);
                }


                holder.sender_time_txt.setText(Utils.getDate((long) time, "hh:mm a"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            setStatus(jsonObject, holder.sender_reply_status_image, mContext);

            holder.sender_map_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String latitude = mMessages.optJSONObject(position).optString("lat");
                    String longitude = mMessages.optJSONObject(position).optString("lng");
                    Log.e("sender_lat_lng", "" + latitude + "-" + longitude);
                    String url = "http://maps.google.com/maps?daddr=" + latitude + "," + longitude;

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.valueOf(url)));
                    intent.setPackage("com.google.android.apps.maps");
                    try {
                        mContext.startActivity(intent);
                    } catch (ActivityNotFoundException ex) {

                        try {
                            Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.valueOf(url)));
                            mContext.startActivity(unrestrictedIntent);
                        } catch (ActivityNotFoundException innerEx) {
                            Toast.makeText(mContext, "Please install a maps application", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });

        }

        if (jsonObject.optString("userType").equalsIgnoreCase(ChatMessages.SENDER_IMAGE)) {

            try {
                double time = Double.valueOf(jsonObject.optString("sentTime"));
                String uri = jsonObject.optString("msg");
                File f = new File(uri);
                Log.d("onBindViewHolder: ", "uri:" + uri);

                if (uri.equalsIgnoreCase(" ") || uri.equalsIgnoreCase("")) {

                    Glide.with(mContext).load(R.drawable.ic_account_circle).error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.sender_chat_image);

                } else {

                    Glide.with(mContext).load(f).into(holder.sender_chat_image);

                }

                holder.sender_time_txt.setText(Utils.getDate((long) time, "hh:mm a"));
                setStatus(jsonObject, holder.sender_reply_status_image, mContext);
                String upload_status = jsonObject.optString("upload");

                Log.e(TAG, "onBindViewHoldestatus" + upload_status);

                if (upload_status.equalsIgnoreCase("uploading")) {
                    holder.image_progress.setVisibility(View.VISIBLE);
                    holder.upload_btn.setVisibility(View.GONE);
                    holder.upload_cancel_btn.setVisibility(View.VISIBLE);
                } else if (upload_status.equalsIgnoreCase("uploaded")) {
                    holder.image_progress.setVisibility(View.GONE);
                    holder.upload_layout.setVisibility(View.GONE);

                } else if (upload_status.equalsIgnoreCase("uploadFailed")) {
                    holder.image_progress.setVisibility(View.GONE);
                    holder.upload_btn.setVisibility(View.VISIBLE);
                    holder.upload_cancel_btn.setVisibility(View.GONE);
                }


                holder.upload_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.image_progress.setVisibility(View.VISIBLE);
                        holder.upload_btn.setVisibility(View.GONE);
                        holder.upload_cancel_btn.setVisibility(View.VISIBLE);


                        UploadImages(jsonObject.optString("msg"), holder.image_progress, jsonObject.optString("msgId"), jsonObject.optString("userId"),
                                holder.upload_layout, holder.sender_reply_status_image, holder.upload_btn, holder.upload_cancel_btn, ChatType.image, jsonObject.optString("chatRoomType"), jsonObject.optString("groupId"));

                    }
                });
                final String amz_id = dbHandler.getAmazonId(jsonObject.optString("msgId"));
                holder.upload_cancel_btn.setOnClickListener(new View.OnClickListener() {
                    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
                    @Override
                    public void onClick(View view) {


                        ChatActivity.transferUtility.cancel(Integer.parseInt(amz_id));
                        holder.image_progress.setVisibility(View.GONE);
                        holder.upload_btn.setVisibility(View.VISIBLE);
                        holder.upload_cancel_btn.setVisibility(View.GONE);


                    }
                });

                holder.sender_chat_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, ImageViewer_Activity.class);
                        String image_id = jsonObject.optString("msg");
                        Log.d("onClick: ", "msg:" + image_id);
                        String file = new File(image_id).getName();
                        intent.putExtra("select_file", file);
                        intent.putExtra("zoeChatId", zoeChatId);
                        intent.putExtra("groupId", GroupID);
                        intent.putExtra("chatRoomType", ChatRoomType);
                        mContext.startActivity(intent);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (jsonObject.optString("userType").equalsIgnoreCase(ChatMessages.RECEIVER_IMAGE)) {

            try {
                if (!jsonObject.optString("chatRoomType").equalsIgnoreCase("0")) {
                    String name = dbHandler.GetUserName(jsonObject.optString("userName"));
                    holder.group_Receiver.setVisibility(View.VISIBLE);
                    holder.group_Receiver.setText(name);
                } else {
                    holder.group_Receiver.setVisibility(View.GONE);
                }

                double time1 = Double.valueOf(jsonObject.optString("sentTime"));

                holder.receiver_time_txt.setText(Utils.getDate((long) time1, "hh:mm a"));
                final String downloadStatus = dbHandler.isDownloadStatus(jsonObject.optString("msgId"));
                if (downloadStatus.equalsIgnoreCase("0")) {

//                File file = new File(jsonObject.optString("msg"));
//                String fileName = Const.amazons3ServerImagePath + "thumb_" + file.getName();
//                ResizeOptions options = new ResizeOptions(350, 350);
//
//                BlurPostprocessor blurPostprocessor = new BlurPostprocessor(mContext);
//
//                ImageRequest request = ImageRequestBuilder
//                        .newBuilderWithSource(Uri.parse(fileName))
//                        .setPostprocessor(blurPostprocessor)
//                        .setProgressiveRenderingEnabled(true).setResizeOptions(options)
//                        .build();
//                DraweeController controller = Fresco.newDraweeControllerBuilder()
//                        .setImageRequest(request)
//                        .setOldController(holder.receiver_chat_image.getController())
//                        .build();
//                holder.receiver_chat_image.setController(controller);
                    Picasso.with(mContext).load(jsonObject.optString("msg")).error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).centerCrop().resize(50, 50).into(holder.receiver_chat_image);


                    String in_type = Utils.getinternettype(mContext);
                    if (in_type.equalsIgnoreCase("WiFi")) {
                        boolean[] booleen = SharedHelper.getKey("wifi_data", mContext);
                        boolean shouldDownload = booleen[0];
                        {
                            if (shouldDownload) {
                                holder.download_btn.setVisibility(View.GONE);
                                holder.downLoad_Cancel.setVisibility(View.VISIBLE);

                                DownloadImage(jsonObject.optString("msg"), jsonObject.optString("msgId"), holder.receiver_chat_image,
                                        holder.image_progress, holder.download_layout, holder.download_btn, holder.downLoad_Cancel, position);
                            }
                        }
                    } else {
                        if (Utils.isDataRoamingEnabled(mContext)) {
                            boolean[] booleen = SharedHelper.getKey("roaming_data", mContext);
                            boolean shouldDownload = booleen[0];
                            {
                                if (shouldDownload) {
                                    holder.download_btn.setVisibility(View.GONE);
                                    holder.downLoad_Cancel.setVisibility(View.VISIBLE);
                                    DownloadImage(jsonObject.optString("msg"), jsonObject.optString("msgId"), holder.receiver_chat_image,
                                            holder.image_progress, holder.download_layout, holder.download_btn, holder.downLoad_Cancel, position);
                                }
                            }
                        } else {
                            boolean[] booleen = SharedHelper.getKey("mobile_data", mContext);
                            boolean shouldDownload = booleen[0];
                            {
                                if (shouldDownload) {
                                    holder.download_btn.setVisibility(View.GONE);
                                    DownloadImage(jsonObject.optString("msg"), jsonObject.optString("msgId"), holder.receiver_chat_image,
                                            holder.image_progress, holder.download_layout, holder.download_btn, holder.downLoad_Cancel, position);
                                }
                            }
                        }
                    }


                    holder.download_layout.setVisibility(View.VISIBLE);
                    holder.download_btn.setVisibility(View.VISIBLE);
                    holder.downLoad_Cancel.setVisibility(View.GONE);


                } else if (downloadStatus.equalsIgnoreCase("1")) {
                    holder.download_layout.setVisibility(View.GONE);
                    Log.e("load_image_path", jsonObject.optString("msg"));

                    if (jsonObject.optString("msg").equalsIgnoreCase(" ") || jsonObject.optString("msg").equalsIgnoreCase("")) {
//                    Picasso.with(mContext).load(R.drawable.ic_account_circle)
//                            .error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.receiver_chat_image);
                        Glide.with(mContext).load(R.drawable.ic_account_circle).error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.receiver_chat_image);

                    } else {
                        Glide.with(mContext).load(new File(jsonObject.optString("msg"))).into(holder.receiver_chat_image);
//                    Picasso.with(mContext).load(new File(jsonObject.optString("msg"))).into(holder.receiver_chat_image);
                    }
                }

                holder.download_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("msg_Id", jsonObject.optString("msgId"));
                        DownloadImage(jsonObject.optString("msg"), jsonObject.optString("msgId"), holder.receiver_chat_image,
                                holder.image_progress, holder.download_layout, holder.download_btn, holder.downLoad_Cancel, position);
                    }
                });
                holder.downLoad_Cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int id = transferObserver.getId();
                        transferUtility.cancel(id);
                        holder.downLoad_Cancel.setVisibility(View.GONE);
                        holder.download_btn.setVisibility(View.VISIBLE);
                        holder.image_progress.setVisibility(View.GONE);


                    }
                });
                holder.receiver_chat_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String downloadStatus = dbHandler.isDownloadStatus(jsonObject.optString("msgId"));
                        if (downloadStatus.equalsIgnoreCase("1")) {
                            Intent intent = new Intent(mContext, ImageViewer_Activity.class);
                            String image_id = jsonObject.optString("msg");
                            String file = new File(image_id).getName();
                            Log.e("file", file);
                            intent.putExtra("select_file", file);
                            intent.putExtra("groupId", jsonObject.optString("groupId"));
                            intent.putExtra("chatRoomType", jsonObject.optString("chatRoomType"));
                            intent.putExtra("zoeChatId", jsonObject.optString("userId"));
                            mContext.startActivity(intent);
                        } else if (downloadStatus.equalsIgnoreCase("0")) {
                            DownloadImage(jsonObject.optString("msg"), jsonObject.optString("msgId"), holder.receiver_chat_image,
                                    holder.image_progress, holder.download_layout, holder.download_btn, holder.downLoad_Cancel, position);
                            holder.download_btn.setVisibility(View.GONE);
                            holder.downLoad_Cancel.setVisibility(View.VISIBLE);
                        }

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (jsonObject.optString("userType").equalsIgnoreCase(ChatMessages.RECEIVER_LOCATION)) {

            if (!jsonObject.optString("chatRoomType").equalsIgnoreCase("0")) {
                String name = dbHandler.GetUserName(jsonObject.optString("userName"));
                holder.group_Receiver.setVisibility(View.VISIBLE);
                holder.group_Receiver.setText(name);
            } else {
                holder.group_Receiver.setVisibility(View.GONE);
            }

            double time1 = Double.valueOf(jsonObject.optString("sentTime"));
            holder.receiver_time_txt.setText(Utils.getDate((long) time1, "hh:mm a"));
            if (jsonObject.optString("msg").equalsIgnoreCase(" ") || jsonObject.optString("msg").equalsIgnoreCase("")) {
                Glide.with(mContext).load(R.drawable.ic_account_circle).error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.receiver_map_image);
//                Picasso.with(mContext).load(R.drawable.ic_account_circle)
//                        .error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.receiver_map_image);

            } else {
                Glide.with(mContext).load(jsonObject.optString("msg")).into(holder.receiver_map_image);
//                Picasso.with(mContext).load(jsonObject.optString("msg")).into(holder.receiver_map_image);
            }

            holder.receiver_map_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String latitude = mMessages.optJSONObject(position).optString("lat");
                    String longitude = mMessages.optJSONObject(position).optString("lng");
                    Log.e("receiver_lat_lng", "" + latitude + "-" + longitude);
                    String url = "http://maps.google.com/maps?daddr=" + latitude + "," + longitude;

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.valueOf(url)));
                    intent.setPackage("com.google.android.apps.maps");
                    try {
                        mContext.startActivity(intent);
                    } catch (ActivityNotFoundException ex) {
                        try {
                            Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.valueOf(url)));
                            mContext.startActivity(unrestrictedIntent);
                        } catch (ActivityNotFoundException innerEx) {
                            Toast.makeText(mContext, "Please install a maps application", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }

        if (jsonObject.optString("userType").equalsIgnoreCase(ChatMessages.HEADER)) {

            customView(holder.date_header, getPrimaryCOlor(mContext), getPrimaryCOlor(mContext));
            holder.date_header.setText(jsonObject.optString("date"));

        }

        if (jsonObject.optString("userType").equalsIgnoreCase(ChatMessages.CREATE_GROUP)) {
            holder.date_header.setText(jsonObject.optString("msg"));
            customView(holder.date_header, getPrimaryCOlor(mContext), getPrimaryCOlor(mContext));

        }

        if (jsonObject.optString("userType").equalsIgnoreCase(ChatMessages.SENDER_CONTACT)) {
            holder.contact_name.setText(jsonObject.optString("cName"));
            double time1 = Double.valueOf(jsonObject.optString("sentTime"));
            holder.sender_time_txt.setText(Utils.getDate((long) time1, "hh:mm a"));
            setStatus(jsonObject, holder.sender_reply_status_image, mContext);
            if (dbHandler.GetUserImage(jsonObject.optString("cNumber")).equalsIgnoreCase(" ") || dbHandler.GetUserImage(jsonObject.optString("cNumber")).equalsIgnoreCase("")) {
//                Picasso.with(mContext).load(R.drawable.ic_account_circle)
//                        .error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.contact_image);
                Glide.with(mContext).load(R.drawable.ic_account_circle).error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.contact_image);
            } else {
                Glide.with(mContext).load(dbHandler.GetUserImage(jsonObject.optString("msg"))).error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.contact_image);
//                Picasso.with(mContext).load(dbHandler.GetUserImage(jsonObject.optString("cNumber"))).error(R.drawable.ic_account_circle).into(holder.contact_image);
            }

            holder.contact_number.setText(jsonObject.optString("cNumber"));

            Log.d("onBindViewHolder: ", "contact_sender" + mMessages.optJSONObject(position));
            Log.d("onBindViewHolder: ", "contact_sender_2" + jsonObject);
            holder.message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject object = dbHandler.GetUserInfo(jsonObject.optString("cNumber"));
                    Log.d("onBindViewHolder: ", "contact_click" + object);
                    SharedHelper.putKey(mContext, "single_chat_enable", "yes");

                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("user_name", object.optString("Name"));
                    intent.putExtra("image", object.optString("Image"));
                    intent.putExtra("zoeChatID", object.optString("ZoeChatId"));
                    intent.putExtra("groupId", "0");
                    intent.putExtra("chatRoomType", "0");
                    mContext.startActivity(intent);
                }
            });

        }
        if (jsonObject.optString("userType").equalsIgnoreCase(ChatMessages.RECEIVER_CONTACT)) {

            if (!jsonObject.optString("chatRoomType").equalsIgnoreCase("0")) {
                String name = dbHandler.GetUserName(jsonObject.optString("userName"));
                holder.group_Receiver.setVisibility(View.VISIBLE);
                holder.group_Receiver.setText(name);
            } else {
                holder.group_Receiver.setVisibility(View.GONE);
            }

            holder.message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject object = dbHandler.GetUserInfo(jsonObject.optString("cNumber"));
                    Log.d("onBindViewHolder: ", "contact_click" + object);
                    SharedHelper.putKey(mContext, "single_chat_enable", "yes");

                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("user_name", object.optString("Name"));
                    intent.putExtra("image", object.optString("Image"));
                    intent.putExtra("zoeChatID", object.optString("ZoeChatId"));
                    intent.putExtra("groupId", "0");
                    intent.putExtra("chatRoomType", "0");
                    mContext.startActivity(intent);
                }
            });


            holder.contact_name.setText(jsonObject.optString("cName"));
            double time1 = Double.valueOf(jsonObject.optString("sentTime"));
            holder.receiver_time_txt.setText(Utils.getDate((long) time1, "hh:mm a"));

            if (dbHandler.GetUserImage(jsonObject.optString("cNumber")).equalsIgnoreCase(" ") || dbHandler.GetUserImage(jsonObject.optString("cNumber")).equalsIgnoreCase("")) {
//                Picasso.with(mContext).load(R.drawable.ic_account_circle)
//                        .error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.contact_image);
                Glide.with(mContext).load(R.drawable.ic_account_circle).error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.contact_image);
            } else {
                Glide.with(mContext).load(dbHandler.GetUserImage(jsonObject.optString("msg"))).error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.contact_image);
//                Picasso.with(mContext).load(dbHandler.GetUserImage(jsonObject.optString("cNumber"))).error(R.drawable.ic_account_circle).into(holder.contact_image);
            }
            holder.contact_number.setText(jsonObject.optString("cNumber"));
            JSONArray fullcontacts = dbHandler.GetAllUsers();


            for (int i = 0; i < fullcontacts.length(); i++) {

                if (fullcontacts.optJSONObject(i).optString("Mobile").equalsIgnoreCase(jsonObject.optString("cNumber"))) {
                    holder.contact_layout.setVisibility(View.GONE);
                    holder.contact_view_line.setVisibility(View.GONE);
                }
//                }else{
//                    holder.add_contact.setVisibility(View.VISIBLE);
//                }

            }
            holder.add_contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("Create a new contact or add to an existing contact?");
                    builder.setPositiveButton("New", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent add_contact = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
                            add_contact.putExtra(ContactsContract.Intents.Insert.NAME, jsonObject.optString("cName"));
                            add_contact.putExtra(ContactsContract.Intents.Insert.PHONE, jsonObject.optString("cNumber").substring(2, 12));
                            mContext.startActivity(add_contact);
                        }
                    });
                   /* builder.setNegativeButton("Existing", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            try {
                                ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

                                ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                                        .withSelection(ContactsContract.CommonDataKinds.Phone._ID + "=? AND " +
                                                        ContactsContract.Contacts.Data.MIMETYPE + "='" +
                                                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'",
                                                new String[]{"1"}).build());

                                ContentProviderResult[] result = mContext.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                            } catch (Exception e) {
                                Log.w("UpdateContact", e.getMessage() + "");
                                for (StackTraceElement ste : e.getStackTrace()) {
                                    Log.w("UpdateContact", "\t" + ste.toString());
                                }


                            }
                        }
                    });*/
                    builder.show();
                }
            });

        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (!mMessages.optJSONObject(position).optString("userType").equalsIgnoreCase("4")) {
                    try {
                        Log.d("onLongClick: ", "imagemessagevalues:" + mMessages.get(position));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("onClicktest: ", "multi_triggered");
                    Log.d("onClicktest: ", "position:" + position);
                    int value = (int) v.getTag();
                    String type_val;
                    if (value != ChatMessages.CREATE_GROUP_MSG) {
                        if (value == ChatMessages.RECEIVER_AUDIO_MESSAGE || value == ChatMessages.RECEIVER_CONTACT_MESSAGE
                                || value == ChatMessages.RECEIVER_DOC_MESSAGE || value == ChatMessages.RECEIVER_VIDEO_MESSAGE || value == ChatMessages.RECEIVER_IMAGE_MESSAGE
                                || value == ChatMessages.RECEIVER_LOCATION_MESSAGE || value == ChatMessages.RECEIVER_TEXT__MESSAGE) {
                            type_val = "receiver";
                        } else {
                            type_val = "sender";
                        }


                        Scrollposition = position;
                        try {
                            String starred = mMessages.optJSONObject(position).optString("isStarred");
                            String type = mMessages.optJSONObject(position).optString("chatType");
                            //String sender=
                            Log.e("onLongClick: ", " sender:" + mMessages.optJSONObject(position));
                            ChatActivity.toolbar.getMenu().clear();
                            ChatActivity.back_layout.setVisibility(View.GONE);


                            if (starred.equalsIgnoreCase("true")) {
                                if (type.equalsIgnoreCase("text")) {
                                    isFirstStarred = true;
                                    if (type_val.equalsIgnoreCase("receiver")) {
                                        ChatActivity.toolbar.inflateMenu(R.menu.action_single_chat_unstar_rec);
                                    } else {
                                        ChatActivity.toolbar.inflateMenu(R.menu.action_single_chat_unstar);
                                    }
                                } else {
                                    if (type_val.equalsIgnoreCase("receiver")) {
                                        ChatActivity.toolbar.inflateMenu(R.menu.action_single_no_copy_rec);

                                    } else {
                                        ChatActivity.toolbar.inflateMenu(R.menu.action_single_no_copy);
                                    }

                                }
                            } else {
                                if (type.equalsIgnoreCase("text")) {

                                    isFirstStarred = false;
                                    if (type_val.equalsIgnoreCase("receiver")) {
                                        ChatActivity.toolbar.inflateMenu(R.menu.action_single_chat_rec);

                                    } else {
                                        ChatActivity.toolbar.inflateMenu(R.menu.action_single_chat);
                                    }

                                } else {
                                    isFirstStarred = false;

                                    if (type_val.equalsIgnoreCase("receiver")) {
                                        ChatActivity.toolbar.inflateMenu(R.menu.action_single_chat_no_un_rec);

                                    } else {
                                        ChatActivity.toolbar.inflateMenu(R.menu.action_single_chat_no_un);
                                    }
                                }
                            }

                            Log.d("onLongClick: ", "valueheader:" + value);

                            Menu menu = ChatActivity.toolbar.getMenu();
                            for (int i = 0; i < menu.size(); i++) {
                                MenuItem menuItems = menu.getItem(i);
                                Drawable drawable = menuItems.getIcon();
                                if (drawable != null) {
                                    // If we don't mutate the drawable, then all drawable's with this id will have a color
                                    // filter applied to it.
                                    drawable.mutate();
                                    drawable.setColorFilter(mContext.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

                                }

                            }

                            ChatActivity.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
                            ChatActivity.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    is_in_action_mode = false;
                                    ChatActivity.clearActionM();
                                    for (int i = 0; i < mMessages.length(); i++) {
                                        try {
                                            JSONObject object = mMessages.optJSONObject(i);
                                            object.remove("isSelected");
                                            Log.d("onClick: ", "changing:");
                                            Log.d("onClick: ", "value:" + object.optString("isSelected"));
                                            object.put("isSelected", "false");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    ChatActivity.back_layout.setVisibility(View.VISIBLE);


                                }
                            });
                            is_in_action_mode = true;
                            JSONObject object = mMessages.optJSONObject(position);
                            Log.d("onLongClick: ", "" + object);
                            ChatActivity.read_time = object.optString("seenTime");
                            ChatActivity.delievered_time = object.optString("deliveredTime");

                            object.remove("isSelected");
                            object.put("isSelected", "true");
                            singleChatAdapter.notifyDataSetChanged();
                            notifyDataSetChanged();
                            ChatActivity.counter++;

                            updateCnt(ChatActivity.counter);
                            try {
                                holder.remove_view.setVisibility(View.VISIBLE);
                            } catch (Exception e) {

                            }
                            holder.itemView.setBackgroundResource(R.color.selected_blue);
                            String id = object.optString("msgId");
                            select_position.add(0, String.valueOf(position));
                            addItem(id, position);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return true;
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("onClicktest: ", "single_triggered");
                Log.d("onClicktest: ", "position:" + position);


                if (is_in_action_mode) {

                    if (!mMessages.optJSONObject(position).optString("userType").equalsIgnoreCase("4")) {

                        if (mMessages.optJSONObject(position).optString("isSelected").equalsIgnoreCase("true")) {
                            try {
                                Scrollposition = position;
                                Log.e("view_", "Visible");
//                                holder.remove_view.setVisibility(View.GONE);
                                holder.itemView.setBackgroundResource(0);
                                ChatActivity.counter--;
                                JSONObject object = mMessages.optJSONObject(position);
                                object.remove("isSelected");
                                object.put("isSelected", "false");
                                updateCnt(ChatActivity.counter);
                                String id = mMessages.optJSONObject(position).optString("msgId");
                                removeitem(id);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            try {
                                Scrollposition = position;
                                Log.e("view_", "UNVisible");
                                try {
                                    holder.remove_view.setVisibility(View.VISIBLE);

                                } catch (Exception e) {
                                }

                                holder.itemView.setBackgroundResource(R.color.selected_blue);
                                ChatActivity.counter++;
                                updateCnt(ChatActivity.counter);
                                JSONObject object = mMessages.optJSONObject(position);
                                object.remove("isSelected");
                                object.put("isSelected", "true");
                                String id = mMessages.optJSONObject(position).optString("msgId");
                                addItem(id, position);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        if (ChatActivity.counter > 1) {

                            if (isFirstStarred) {
                                ChatActivity.toolbar.getMenu().clear();
                                ChatActivity.back_layout.setVisibility(View.GONE);
                                ChatActivity.toolbar.inflateMenu(R.menu.action_single_chat_one_un);

                                ChatActivity.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
                                Menu menu = ChatActivity.toolbar.getMenu();
                                for (int i = 0; i < menu.size(); i++) {
                                    MenuItem menuItems = menu.getItem(i);
                                    Drawable drawable = menuItems.getIcon();
                                    if (drawable != null) {
                                        // If we don't mutate the drawable, then all drawable's with this id will have a color
                                        // filter applied to it.
                                        drawable.mutate();
                                        drawable.setColorFilter(mContext.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

                                    }

                                }
                                ChatActivity.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        position1 = ChatActivity.linearLayoutManager.findFirstCompletelyVisibleItemPosition();

                                        ChatActivity.clearActionM();
                                        for (int i = 0; i < mMessages.length(); i++) {
                                            try {
                                                JSONObject object = mMessages.optJSONObject(i);
                                                Log.d("onClick: ", "changing:");
                                                Log.d("onClick: ", "value:" + object.optString("isSelected"));
                                                object.remove("isSelected");
                                                object.put("isSelected", "false");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }


                                    }
                                });
                            } else {
                                position1 = ChatActivity.linearLayoutManager.findFirstCompletelyVisibleItemPosition();

                                ChatActivity.toolbar.getMenu().clear();
                                ChatActivity.back_layout.setVisibility(View.GONE);
                                ChatActivity.toolbar.inflateMenu(R.menu.action_single_chat_one);
                                ChatActivity.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
                                Menu menu = ChatActivity.toolbar.getMenu();
                                for (int i = 0; i < menu.size(); i++) {
                                    MenuItem menuItems = menu.getItem(i);
                                    Drawable drawable = menuItems.getIcon();
                                    if (drawable != null) {
                                        // If we don't mutate the drawable, then all drawable's with this id will have a color
                                        // filter applied to it.
                                        drawable.mutate();
                                        drawable.setColorFilter(mContext.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

                                    }

                                }
                                ChatActivity.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ChatActivity.clearActionM();
                                        for (int i = 0; i < mMessages.length(); i++) {
                                            try {
                                                JSONObject object = mMessages.optJSONObject(i);
                                                Log.d("onClick: ", "changing:");
                                                Log.d("onClick: ", "value:" + object.optString("isSelected"));
                                                object.remove("isSelected");
                                                object.put("isSelected", "false");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }


                                    }
                                });
                            }
                        }

                    }
                }
            }
        });

        selected = mMessages.optJSONObject(position).optString("isSelected");


        if (selected.equalsIgnoreCase("true")) {
            holder.itemView.setBackgroundResource(R.color.selected_blue);
        }

        try {

            if (type_val.equalsIgnoreCase("receiver")) {
                if (!jsonObject.optString("chatRoomType").equalsIgnoreCase("0")) {
                    if (shouldSign.equalsIgnoreCase("true") || shouldSign.equalsIgnoreCase("1") || shouldSign.equalsIgnoreCase("")) {
                        holder.group_Receiver.setVisibility(View.VISIBLE);
                    } else {
                        holder.group_Receiver.setVisibility(View.GONE);

                    }
                } else {
                    holder.group_Receiver.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        dbHandler.close();
    }

    private void setDefaultvalues(int position) throws Exception {

        for (int i = 0; i < mMessages.length(); i++) {
            if (i != position) {
                mMessages.optJSONObject(i).put("isPlaying", "0");
                mMessages.optJSONObject(i).put("playingStatus", "0");
            }
        }
        notifyDataSetChanged();


    }

    private void showHideImage(View image, View parent, boolean show) {
        if (show) {
            image.setVisibility(View.VISIBLE);
            parent.setPadding(5, 5, 5, 5);

        } else {
            image.setVisibility(View.GONE);
            parent.setPadding(5, 5, 5, 5);

        }
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    //video,
    private void DownloadVideo(String msg, final String msgId, final ImageView receiver_video_image, final ProgressBar image_progress,
                               final RelativeLayout download_layout, final ImageView download_btn, ImageView downLoad_cancel) {


        if (!msgId.matches("")) {
            download_btn.setVisibility(View.GONE);
            downLoad_cancel.setVisibility(View.VISIBLE);

            File videoFile = new File(msg);
            String videoFileName = videoFile.getName();

            File filepath = Environment.getExternalStorageDirectory();
            File zoeFolder = new File(filepath.getAbsolutePath(), mContext.getResources().getString(R.string.app_name)).getAbsoluteFile();
            if (!zoeFolder.exists()) {
                zoeFolder.mkdir();
            }
            File newFolder = new File(zoeFolder, mContext.getResources().getString(R.string.app_name) + " Video").getAbsoluteFile();
            if (!newFolder.exists()) {
                newFolder.mkdir();
            }
            try {
                ChatActivity.randomImageNo++;
                SharedHelper.putInt(mContext, "image_count", ChatActivity.randomImageNo);
                String date = Utils.getDate(System.currentTimeMillis(), "ddMMyyyy");
                final String saveFileName = "VID_" + date + "ZC00" + ChatActivity.randomImageNo;
                final File imageFile = File.createTempFile(saveFileName, ".mp4", newFolder);
                if (imageFile.exists()) {
                    imageFile.delete();
                    Log.e("file_deleted", "Yes");
                }
                transferUtility = new TransferUtility(s3Client, mContext);
                transferObserver = transferUtility.download(Const.bucket_name, videoFileName, imageFile);
                transferObserver.setTransferListener(new TransferListener() {

                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        Log.d("image_state", state.toString());
                        if (state.toString().equalsIgnoreCase("IN_PROGRESS")) {
                            image_progress.setVisibility(View.VISIBLE);

                        } else if (state.toString().equalsIgnoreCase("WAITING_FOR_NETWORK")) {
                            image_progress.setVisibility(View.VISIBLE);

                        } else if (state.toString().equalsIgnoreCase("COMPLETED")) {
                            download_layout.setVisibility(View.GONE);
                            DBHandler dbHandler = new DBHandler(mContext);
                            dbHandler.UpdateChatMsgStatus(msgId, imageFile.getAbsolutePath());
                            dbHandler.close();
                            Log.d("imageFile.Path", imageFile.getAbsolutePath());
                            if (imageFile.getAbsolutePath().equalsIgnoreCase(" ") || imageFile.getAbsolutePath().equalsIgnoreCase("")) {
                                Picasso.with(mContext).load(R.drawable.ic_account_circle).error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(receiver_video_image);
                            } else {
                                Picasso.with(mContext).load(imageFile.getAbsoluteFile()).into(receiver_video_image);
                            }

                            notifyDataSetChanged();

                            refresh(msgId, imageFile.getAbsoluteFile().toString());
                        }

                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        // int percentage = (int) (bytesCurrent / bytesTotal * 100);
                        //Log.d("image_state_percentage", String.valueOf(percentage));
                    }

                    @SuppressWarnings("deprecation")
                    @Override
                    public void onError(int id, Exception ex) {
                        Log.e("image_state_error", ex.getMessage());

                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void DownloadDocument(final String msg, final String msgId, final ProgressBar image_progress,
                                  final RelativeLayout download_layout, ImageView download_btn, ImageView downLoad_cancel, final RelativeLayout downloaded_lay) {


        if (!msgId.matches("")) {
            download_btn.setVisibility(View.GONE);
            downLoad_cancel.setVisibility(View.VISIBLE);

            File videoFile = new File(msg);
            String videoFileName = videoFile.getName();
            Log.d("DownloadDOCUMENT: ", "values:" + msg + "," + videoFileName);

            File filepath = Environment.getExternalStorageDirectory();
            File zoeFolder = new File(filepath.getAbsolutePath(), mContext.getResources().getString(R.string.app_name)).getAbsoluteFile();
            if (!zoeFolder.exists()) {
                zoeFolder.mkdir();
            }
            File newFolder = new File(zoeFolder, mContext.getResources().getString(R.string.app_name) + " Document").getAbsoluteFile();
            if (!newFolder.exists()) {
                newFolder.mkdir();
            }
            try {
                ChatActivity.randomImageNo++;
                SharedHelper.putInt(mContext, "image_count", ChatActivity.randomImageNo);
                String date = Utils.getDate(System.currentTimeMillis(), "ddMMyyyy");
                ContentResolver cR = mContext.getContentResolver();
                MimeTypeMap mime = MimeTypeMap.getSingleton();

//                String type = mime.getExtensionFromMimeType(cR.getType(Uri.fromFile(videoFile)));
                String type = getLastThree(videoFileName);


//                final String saveFileName = "DOC_" + date + "ZC00" + ChatActivity.randomImageNo + "." + type;
                final String saveFileName = "DOC_" + date + "ZC00" + ChatActivity.randomImageNo;
                Log.d("DownloadVideo: ", "save_file_name:" + saveFileName + ",r:" + ChatActivity.randomImageNo + "type:" + type);
                final File imageFile = File.createTempFile(saveFileName, "." + type, newFolder);
                if (imageFile.exists()) {
                    imageFile.delete();
                    Log.e("file_deleted", "Yes");
                }
                transferUtility = new TransferUtility(s3Client, mContext);
                transferObserver = transferUtility.download(Const.bucket_name, videoFileName, imageFile);
                transferObserver.setTransferListener(new TransferListener() {

                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        Log.d("image_state", state.toString());
                        if (state.toString().equalsIgnoreCase("IN_PROGRESS")) {
                            image_progress.setVisibility(View.VISIBLE);

                        } else if (state.toString().equalsIgnoreCase("WAITING_FOR_NETWORK")) {
                            image_progress.setVisibility(View.VISIBLE);

                        } else if (state.toString().equalsIgnoreCase("COMPLETED")) {
                            download_layout.setVisibility(View.GONE);
                            downloaded_lay.setVisibility(View.VISIBLE);
                            DBHandler dbHandler = new DBHandler(mContext);

//                            dbHandler.UpdateChatMsgStatus(msgId, String.valueOf(getImageContentUri(mContext, imageFile.getAbsolutePath())));
                            dbHandler.UpdateChatMsgStatus(msgId, msg);

                            dbHandler.UpdateMedia(msgId, imageFile.getAbsolutePath());


                            dbHandler.close();
                            Log.d("imageFile.Path", String.valueOf(getImageContentUri(mContext, imageFile.getAbsolutePath())));
                            notifyDataSetChanged();
//                            refresh(msgId, imageFile.getAbsoluteFile().toString());
                            refresh(msgId, msg);
                        }

                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        // int percentage = (int) (bytesCurrent / bytesTotal * 100);
                        //Log.d("image_state_percentage", String.valueOf(percentage));
                    }

                    @SuppressWarnings("deprecation")
                    @Override
                    public void onError(int id, Exception ex) {
                        Log.e("image_state_error", ex.getMessage());

                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getLastThree(String myString) {
        if (myString.length() > 3)
            return myString.substring(myString.length() - 3);
        else
            return myString;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void DownloadAudio(String msg, final String msgId, final ProgressBar image_progress,
                               final RelativeLayout download_layout, ImageView download_btn, ImageView downLoad_cancel, final RelativeLayout downloaded_lay) {


        if (!msgId.matches("")) {
            download_btn.setVisibility(View.GONE);
            downLoad_cancel.setVisibility(View.VISIBLE);
            File videoFile = new File(msg);
            String videoFileName = videoFile.getName();
            Log.d("DownloadAudio: ", "values:" + msg + "," + videoFileName);

            File filepath = Environment.getExternalStorageDirectory();
            File zoeFolder = new File(filepath.getAbsolutePath(), mContext.getResources().getString(R.string.app_name)).getAbsoluteFile();
            if (!zoeFolder.exists()) {
                zoeFolder.mkdir();
            }
            File newFolder = new File(zoeFolder, mContext.getResources().getString(R.string.app_name) + " Audio").getAbsoluteFile();
            if (!newFolder.exists()) {
                newFolder.mkdir();
            }
            try {
                ChatActivity.randomImageNo++;
                SharedHelper.putInt(mContext, "image_count", ChatActivity.randomImageNo);
                String date = Utils.getDate(System.currentTimeMillis(), "ddMMyyyy");
                final String saveFileName = "AUD_" + date + "ZC00" + ChatActivity.randomImageNo;

                final File imageFile = File.createTempFile(saveFileName, ".aac", newFolder);
                if (imageFile.exists()) {
                    imageFile.delete();
                    Log.e("file_deleted", "Yes");
                }
                transferUtility = new TransferUtility(s3Client, mContext);
                transferObserver = transferUtility.download(Const.bucket_name, videoFileName, imageFile);
                transferObserver.setTransferListener(new TransferListener() {

                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        Log.d("image_state", state.toString());
                        if (state.toString().equalsIgnoreCase("IN_PROGRESS")) {
                            image_progress.setVisibility(View.VISIBLE);
                        } else if (state.toString().equalsIgnoreCase("WAITING_FOR_NETWORK")) {
                            image_progress.setVisibility(View.VISIBLE);
                        } else if (state.toString().equalsIgnoreCase("COMPLETED")) {
                            download_layout.setVisibility(View.GONE);
                            downloaded_lay.setVisibility(View.VISIBLE);
                            DBHandler dbHandler = new DBHandler(mContext);
                            dbHandler.UpdateChatMsgStatus(msgId, imageFile.getAbsolutePath());
                            dbHandler.close();
                            Log.d("imageFile.Path", imageFile.getAbsolutePath());
                            notifyDataSetChanged();
                            refresh(msgId, imageFile.getAbsoluteFile().toString());
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        // int percentage = (int) (bytesCurrent / bytesTotal * 100);
                        //Log.d("image_state_percentage", String.valueOf(percentage));
                    }

                    @SuppressWarnings("deprecation")
                    @Override
                    public void onError(int id, Exception ex) {
                        Log.e("image_state_error", ex.getMessage());

                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void setStatus(JSONObject jsonObject, ImageView sender_reply_status_image, Context mContext) {
        DBHandler dbHandler = new DBHandler(mContext);

        if (jsonObject.optString("chatRoomType").equalsIgnoreCase("0")) {
            try {
                if (jsonObject.optString("contentStatus").equalsIgnoreCase("sent")) {
                    sender_reply_status_image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.sent));
                } else if (jsonObject.optString("contentStatus").equalsIgnoreCase("delivered")) {
                    String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                    Log.d("deleivered_chat", mydate);
                    dbHandler.UpdateDeliveredTime(jsonObject.optString("msgId"), mydate);
                    sender_reply_status_image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.delivered));
                } else if (jsonObject.optString("contentStatus").equalsIgnoreCase("read")) {
                    String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                    Log.d("read_chat", mydate);


                    dbHandler.UpdateSeenMsgTime(jsonObject.optString("msgId"), mydate);
                    sender_reply_status_image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.read));
                } else {

                    sender_reply_status_image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.sending_pending));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (jsonObject.optString("contentStatus").equalsIgnoreCase("sent")) {
                    sender_reply_status_image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.sent));
                } else if (jsonObject.optString("contentStatus").equalsIgnoreCase("delivered")) {
                    String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                    Log.d("deleivered_chat", mydate);
                    dbHandler.UpdateDeliveredTime(jsonObject.optString("msgId"), mydate);
                    sender_reply_status_image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.sent));
                } else if (jsonObject.optString("contentStatus").equalsIgnoreCase("read")) {
                    String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                    Log.d("read_chat", mydate);

                    dbHandler.UpdateSeenMsgTime(jsonObject.optString("msgId"), mydate);
                    sender_reply_status_image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.sent));
                } else {

                    sender_reply_status_image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.sending_pending));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    private void addItem(String msgId, int position) {
        selectRemoveItem.add(msgId);
        selectRemoveItemPosition.add(position);
        Log.e("items", selectRemoveItem.toString());
    }

    public void removeitem(String msgId) {

        for (int i = 0; i < selectRemoveItem.size(); i++) {
            String s1 = selectRemoveItem.get(i);
            if (s1.equalsIgnoreCase(msgId)) {
                selectRemoveItem.remove(i);
                selectRemoveItemPosition.remove(i);

            }
        }
    }

    private void updateCnt(int counter) {
        if (counter == 0) {
            ChatActivity.clearActionM();
            is_in_action_mode = false;
            selectRemoveItem.clear();
            selectRemoveItemPosition.clear();
            selectRemoveItemPosition.clear();
        } else {
            ChatActivity.toolbar.setTitleTextColor(Color.WHITE);
            ChatActivity.toolbar.setTitle(counter + "  selected");

        }
    }

    //image,video,document,location
    private void UploadImages(final String image_path, final ProgressBar image_progress,
                              final String msgId, final String userID, final RelativeLayout upload_lay,
                              ImageView sender_reply_status_image, final ImageView upload_btn, final ImageView upload_cancel_btn,
                              final ChatType chatType, final String chatRoomType, final String groupId) {
        final DBHandler dbHandler = new DBHandler(mContext);
        final String image_path_val;

        if (chatType == ChatType.document) {
            image_path_val = getRealPathFromURI(mContext, Uri.parse(image_path));
        } else {
            image_path_val = image_path;
        }

        final File file = new File(image_path_val);
        Log.e("upload_file", file.toString());
        Log.d("UploadImages: ", "imagepath" + image_path_val);

        transferUtility = new TransferUtility(s3Client, mContext);

        transferObserver = transferUtility.upload(Const.bucket_name, file.getName(), file);

        transferObserver.setTransferListener(new TransferListener() {

            @SuppressLint("WrongConstant")
            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.d("image_state", state.toString());
                if (state.toString().equalsIgnoreCase("IN_PROGRESS")) {
                    if (chatType == ChatType.document) {

                    }
                    image_progress.setVisibility(View.VISIBLE);
                    upload_btn.setVisibility(View.GONE);
                    upload_cancel_btn.setVisibility(View.VISIBLE);

                } else if (state.toString().equalsIgnoreCase("WAITING_FOR_NETWORK")) {
                    image_progress.setVisibility(View.VISIBLE);
                    upload_btn.setVisibility(View.GONE);
                    upload_cancel_btn.setVisibility(View.VISIBLE);
                } else if (state.toString().equalsIgnoreCase("COMPLETED")) {
                    dbHandler.UpdateIsUploaded(msgId, "uploaded");
                    setIsUploaded("uploaded", msgId);
                    image_progress.setVisibility(View.GONE);
                    upload_btn.setVisibility(View.GONE);
                    upload_lay.setVisibility(View.GONE);

                    Log.e(TAG, "onStateChanged: " + upload_btn.getVisibility());
                    Log.e(TAG, "onStateChanged: " + upload_lay.getVisibility());

                    if (chatType == ChatType.image) {

//                        new LongOperation(Const.amazons3ServerImagePath + file.getName(), file).execute();
                        Log.d("onStateChanged: ", "onsss:" + file.getName());
                    }
                    if (chatType == ChatType.video) {
                        thumbnail = "";
                    }


                    Log.d("upload_state", "completed");
                    Log.d("onStateChanged: ", "chattype:" + chatType);

                    if (chatRoomType.equalsIgnoreCase("0")) {
                        if (!dbHandler.DoesChatsUser(userID)) {
                            if (ChatType.document == chatType) {

                                dbHandler.InsertChats(new ChatsModel(userID, "0", ChatMessages.SENDER_IMAGE,
                                        Const.amazons3ServerImagePath + file.getName(), Status.SENT.toString(), String.valueOf(System.currentTimeMillis()), chatType.toString()
                                        , 0, "", "", "", "", "0", "0", "", "", "", "", "", "", "false", "1", ""));

                                Log.d("onStateChanged: ", "1");


                            } else {


                                dbHandler.InsertChats(new ChatsModel(userID, "0", ChatMessages.SENDER_IMAGE,
                                        image_path, Status.SENT.toString(), String.valueOf(System.currentTimeMillis()), chatType.toString()
                                        , 0, "", "", "", "", "0", "0", "", "", "", "", "", "", "false", "1", ""));
                                Log.d("onStateChanged: ", "2");

                            }
                        } else {
                            String isDeleted = dbHandler.getDeleteStatus(userID);
                            if (isDeleted.equalsIgnoreCase("1")) {
                                dbHandler.GroupDeleteUpdate("0", userID);
                            }
                            if (ChatType.document == chatType) {
                                dbHandler.UpdateLastMsg(userID, ChatMessages.SENDER_IMAGE, Const.amazons3ServerImagePath + file.getName(),
                                        Status.SENT.toString(), String.valueOf(System.currentTimeMillis()), chatType.toString(), 0);
                                Log.d("onStateChanged: ", "3");

                            } else {
                                Log.d("onStateChanged: ", "4");

                                dbHandler.UpdateLastMsg(userID, ChatMessages.SENDER_IMAGE, image_path,
                                        Status.SENT.toString(), String.valueOf(System.currentTimeMillis()), chatType.toString(), 0);
                            }
                        }
                    } else if (chatRoomType.equalsIgnoreCase("1")) {
                        if (!dbHandler.DoesChatsUser(groupId)) {
                            if (ChatType.document == chatType) {
                                Log.d("onStateChanged: ", "5");

                                dbHandler.InsertChats(new ChatsModel(groupId, "0", ChatMessages.SENDER_IMAGE,
                                        Const.amazons3ServerImagePath + file.getName(), Status.SENT.toString(), String.valueOf(System.currentTimeMillis()), chatType.toString()
                                        , 0, "", "", "", "", "0", "0", "", "", "", "", "", "", "false", "1", ""));
                            } else {
                                Log.d("onStateChanged: ", "6");

                                dbHandler.InsertChats(new ChatsModel(groupId, "0", ChatMessages.SENDER_IMAGE,
                                        image_path, Status.SENT.toString(), String.valueOf(System.currentTimeMillis()), chatType.toString()
                                        , 0, "", "", "", "", "0", "0", "", "", "", "", "", "", "false", "1", ""));
                            }


                        } else {
                            String isDeleted = dbHandler.getDeleteStatus(groupId);
                            if (isDeleted.equalsIgnoreCase("1")) {
                                dbHandler.GroupDeleteUpdate("0", groupId);
                            }
                            if (ChatType.document == chatType) {
                                Log.d("onStateChanged: ", "7");

                                dbHandler.UpdateLastMsg(groupId, ChatMessages.SENDER_IMAGE, Const.amazons3ServerImagePath + file.getName(),
                                        Status.SENT.toString(), String.valueOf(System.currentTimeMillis()), chatType.toString(), 0);
                            } else {
                                Log.d("onStateChanged: ", "8");

                                dbHandler.UpdateLastMsg(groupId, ChatMessages.SENDER_IMAGE, image_path,
                                        Status.SENT.toString(), String.valueOf(System.currentTimeMillis()), chatType.toString(), 0);
                            }
                        }

                    }

                    long currentTime = System.currentTimeMillis();
                    if (ChatType.document == chatType) {
                        dbHandler.UpdateChatMsgStatus(msgId, Const.amazons3ServerImagePath + file.getName());
                        dbHandler.UpdateMedia(msgId, image_path);

                        refresh(msgId, Const.amazons3ServerImagePath + file.getName());
                    }

                    if (chatType == ChatType.image || chatType == ChatType.video) {
                        ChatActivity.SendChatMsgwithThumb(Const.amazons3ServerImagePath + file.getName(), msgId,
                                zoeChatId, String.valueOf(currentTime), chatType, SharedHelper.getKey(mContext, "id"), groupId, chatRoomType, thumbnail, dbHandler.getNotifications(groupId));
                    } else {
                        ChatActivity.SendChatMsg(Const.amazons3ServerImagePath + file.getName(), msgId,
                                zoeChatId, String.valueOf(currentTime), chatType, SharedHelper.getKey(mContext, "id"), groupId, chatRoomType, dbHandler.getNotifications(groupId));
                    }

                }
            }


            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

            }

            @SuppressWarnings("deprecation")
            @Override
            public void onError(int id, Exception ex) {
                Log.e("image_state_error", ex.getMessage());
                image_progress.setVisibility(View.GONE);
                upload_cancel_btn.setVisibility(View.GONE);
                upload_lay.setVisibility(View.VISIBLE);
                upload_btn.setVisibility(View.VISIBLE);
            }
        });

        Log.e("image_state_error", String.valueOf(transferObserver.getBytesTransferred()));

        dbHandler.close();
    }

    //audio
    private void UploadImages(final String image_path, final ProgressBar image_progress,
                              final String msgId, final String userID, final RelativeLayout upload_lay,
                              ImageView sender_reply_status_image, final ImageView upload_btn, final ImageView upload_cancel_btn,
                              final ChatType chatType, final String chatRoomType, final String groupId, final RelativeLayout uploaded) {
        final DBHandler dbHandler = new DBHandler(mContext);
        final File file = new File(image_path);
        Log.e("upload_file", file.toString());

        transferUtility = new TransferUtility(s3Client, mContext);
        transferObserver = transferUtility.upload(Const.bucket_name, file.getName(), file);
        //image_progress.setMax(100);

        transferObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.d("image_state", state.toString());
                if (state.toString().equalsIgnoreCase("IN_PROGRESS")) {
                    image_progress.setVisibility(View.VISIBLE);
                    upload_btn.setVisibility(View.GONE);
                    upload_cancel_btn.setVisibility(View.VISIBLE);

                } else if (state.toString().equalsIgnoreCase("WAITING_FOR_NETWORK")) {
                    image_progress.setVisibility(View.GONE);
                    upload_lay.setVisibility(View.VISIBLE);
                    upload_btn.setVisibility(View.GONE);

                } else if (state.toString().equalsIgnoreCase("COMPLETED")) {

                    image_progress.setVisibility(View.GONE);
                    upload_lay.setVisibility(View.GONE);
                    uploaded.setVisibility(View.VISIBLE);
                    upload_btn.setVisibility(View.GONE);
                    upload_cancel_btn.setVisibility(View.GONE);
                    Log.d("upload_state", "completed");

                    dbHandler.UpdateIsUploaded(msgId, "uploaded");
                    if (chatRoomType.equalsIgnoreCase("0")) {
                        if (!dbHandler.DoesChatsUser(userID)) {
                            dbHandler.InsertChats(new ChatsModel(userID, "0", ChatMessages.SENDER_IMAGE,
                                    image_path, Status.SENT.toString(), String.valueOf(System.currentTimeMillis()), chatType.toString()
                                    , 0, "", "", "", "", "0", "0", "", "", "", "", "", "", "false", "1", ""));
                        } else {

                            String isDeleted = dbHandler.getDeleteStatus(userID);
                            if (isDeleted.equalsIgnoreCase("1")) {
                                dbHandler.GroupDeleteUpdate("0", userID);
                            }
                            dbHandler.UpdateLastMsg(userID, ChatMessages.SENDER_IMAGE, image_path,
                                    Status.SENT.toString(), String.valueOf(System.currentTimeMillis()), chatType.toString(), 0);
                        }
                    } else if (chatRoomType.equalsIgnoreCase("1")) {
                        if (!dbHandler.DoesChatsUser(groupId)) {
                            dbHandler.InsertChats(new ChatsModel(groupId, "0", ChatMessages.SENDER_IMAGE,
                                    image_path, Status.SENT.toString(), String.valueOf(System.currentTimeMillis()), chatType.toString()
                                    , 0, "", "", "", "", "0", "0", "", "", "", "", "", "", "false", "1", ""));
                        } else {

                            String isDeleted = dbHandler.getDeleteStatus(groupId);
                            if (isDeleted.equalsIgnoreCase("1")) {
                                dbHandler.GroupDeleteUpdate("0", groupId);
                            }
                            dbHandler.UpdateLastMsg(groupId, ChatMessages.SENDER_IMAGE, image_path,
                                    Status.SENT.toString(), String.valueOf(System.currentTimeMillis()), chatType.toString(), 0);
                        }
                    }

                    long currentTime = System.currentTimeMillis();
                    ChatActivity.SendChatMsg(Const.amazons3ServerImagePath + file.getName(), msgId,
                            zoeChatId, String.valueOf(currentTime), chatType, SharedHelper.getKey(mContext, "id"), groupId, chatRoomType, dbHandler.getNotifications(groupId));
                    //sender_reply_status_image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.sent));
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                //int percentage = (int) (bytesCurrent / bytesTotal * 100);
                //image_progress.setProgress(percentage);
                //Log.d("image_state_percentage", String.valueOf(percentage));
            }

            @SuppressWarnings("deprecation")
            @Override
            public void onError(int id, Exception ex) {
                Log.e("image_state_error", ex.getMessage());
                image_progress.setVisibility(View.GONE);
                upload_cancel_btn.setVisibility(View.GONE);
                upload_lay.setVisibility(View.VISIBLE);
                upload_btn.setVisibility(View.VISIBLE);
            }
        });

        Log.e("image_state_error", String.valueOf(transferObserver.getBytesTransferred()));

        dbHandler.close();
    }

    private void DownloadImage(String msg, final String msgId, final ImageView receiver_chat_image,
                               final ProgressBar image_progress, final RelativeLayout download_layout,
                               final ImageView downLoad_btn, final ImageView downLoad_cancel, final int position) {
        final DBHandler dbHandler = new DBHandler(mContext);
        if (!msgId.matches("")) {
            downLoad_btn.setVisibility(View.GONE);
            downLoad_cancel.setVisibility(View.VISIBLE);

            File file = new File(msg);
            String fileName = file.getName();
            Log.e("fileName", "" + fileName);

            File filepath = Environment.getExternalStorageDirectory();
            File zoeFolder = new File(filepath.getAbsolutePath(), mContext.getResources().getString(R.string.app_name)).getAbsoluteFile();
            if (!zoeFolder.exists()) {
                zoeFolder.mkdir();
            }
            File newFolder = new File(zoeFolder, mContext.getResources().getString(R.string.app_name) + " Image").getAbsoluteFile();
            if (!newFolder.exists()) {
                newFolder.mkdir();
            }
            try {
                ChatActivity.randomImageNo++;
                SharedHelper.putInt(mContext, "image_count", ChatActivity.randomImageNo);
                String date = Utils.getDate(System.currentTimeMillis(), "ddMMyyyy");
                final String saveFileName = "IMG_" + date + "ZC00" + ChatActivity.randomImageNo;
                final File imageFile = File.createTempFile(saveFileName, ".jpg", newFolder);
                if (imageFile.exists()) {
                    imageFile.delete();
                    Log.e("file_deleted", "Yes");
                }
                transferUtility = new TransferUtility(s3Client, mContext);
                transferObserver = transferUtility.download(Const.bucket_name, fileName, imageFile);
                transferObserver.setTransferListener(new TransferListener() {

                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        Log.d("image_state", state.toString());

                        if (state.toString().equalsIgnoreCase("IN_PROGRESS")) {
                            image_progress.setVisibility(View.VISIBLE);
                            downLoad_btn.setVisibility(View.GONE);
                            downLoad_cancel.setVisibility(View.VISIBLE);


                        } else if (state.toString().equalsIgnoreCase("WAITING_FOR_NETWORK")) {
                            image_progress.setVisibility(View.VISIBLE);

                        } else if (state.toString().equalsIgnoreCase("FAILED")) {
                            image_progress.setVisibility(View.GONE);
                            downLoad_cancel.setVisibility(View.GONE);
                            downLoad_btn.setVisibility(View.VISIBLE);

                        } else if (state.toString().equalsIgnoreCase("COMPLETED")) {
                            download_layout.setVisibility(View.GONE);
                            dbHandler.UpdateChatMsgStatus(msgId, imageFile.getAbsolutePath());
                            Log.d("imageFile.Path", imageFile.getAbsolutePath());

                            if (imageFile.getAbsolutePath().equalsIgnoreCase(" ") || imageFile.getAbsolutePath().equalsIgnoreCase("")) {
                                Picasso.with(mContext).load(R.drawable.ic_account_circle).error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(receiver_chat_image);
                            } else {

                                try {
                                    Bitmap bitmap = getThumbnail(mContext.getContentResolver(), imageFile.getAbsoluteFile().getPath());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                                Picasso.with(mContext).load(imageFile.getAbsoluteFile()).into(receiver_chat_image);
                            }

                            notifyDataSetChanged();
                            refresh(msgId, imageFile.getAbsoluteFile().toString());

                        }

                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        // int percentage = (int) (bytesCurrent / bytesTotal * 100);
                        //Log.d("image_state_percentage", String.valueOf(percentage));
                    }

                    @SuppressWarnings("deprecation")
                    @Override
                    public void onError(int id, Exception ex) {
                        Log.e("image_state_error", ex.getMessage());
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dbHandler.close();
    }

    @Override
    public int getItemCount() {
        return mMessages.length();
    }

    @Override
    public int getItemViewType(int position) {
        JSONObject object = mMessages.optJSONObject(position);
        Log.d("getItemViewType: ", "asdasd" + object);
        return Integer.parseInt(mMessages.optJSONObject(position).optString("userType"));
    }

    public void updateArray(JSONArray jsonArray) {
        mMessages = jsonArray;
        singleChatAdapter.notifyDataSetChanged();
    }

    public int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }

    public class playerHandler {

        public playerHandler() {

        }

        private void pausePlaying(int position, SeekBar seek_bar) {

            Log.d(TAG, "pausePlaying: " + mPlayer.getCurrentPosition());
            mPlayer.seekTo(mPlayer.getCurrentPosition());
            seek_bar.setProgress(mPlayer.getCurrentPosition());
            notifyDataSetChanged();

            try {
                mMessages.optJSONObject(position).put("isPlaying", "2");
                mMessages.optJSONObject(position).put("playingStatus", "" + mPlayer.getCurrentPosition());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mPlayer.pause();

        }

        private void resumeplaying(final SeekBar seek_bar, final int position, String mediaPath) {
            try {
                mPlayer = new MediaPlayer();
                mPlayer.setDataSource(mediaPath);
                mPlayer.prepare();
                seek_bar.setMax(mPlayer.getDuration());
                mPlayer.seekTo(Integer.parseInt(mMessages.optJSONObject(position).optString("playingStatus")));
                mPlayer.start();

                try {
                    mMessages.optJSONObject(position).put("isPlaying", "1");
                    notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (mPlayer != null) {

                        seek_bar.setProgress(mPlayer.getCurrentPosition());
                        try {
                            mMessages.optJSONObject(position).put("playingStatus", "" + mPlayer.getCurrentPosition());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        notifyDataSetChanged();

                        handler.postDelayed(this, 1000);

                    }
                }
            });

            seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                    mPlayer.seekTo(mPlayer.getCurrentPosition());

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Log.d("onProgressChanged: ", "progress");
                    if (mPlayer != null && fromUser) {
                        mPlayer.seekTo(mPlayer.getCurrentPosition());


                    }
                }
            });


            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopplaying();
                    try {
                        mMessages.optJSONObject(position).put("isPlaying", "0");
                        mMessages.optJSONObject(position).put("playingStatus", "0");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

        public void stopplaying() {

            if (mPlayer != null && mPlayer.isPlaying()) {
                mPlayer.stop();
                mPlayer.reset();
                mPlayer.release();
                handler.removeCallbacksAndMessages(globalrunnable);

                try {
                    mPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mPlayer = null;


            }

        }

        private void startPlaying(final SeekBar seek_bar, String mediaPath, final int position) {
            try {
                setDefaultvalues(position);
                mPlayer = new MediaPlayer();
                Log.d("startplaying: ", "path:" + mediaPath);
                mPlayer.setDataSource(mediaPath);
                mPlayer.prepare();
                mPlayer.start();
                seek_bar.setProgress(0);
                seek_bar.setMax(mPlayer.getDuration());

                try {
                    mMessages.optJSONObject(position).put("isPlaying", "1");
                    notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (mPlayer != null) {
                        seek_bar.setProgress(mPlayer.getCurrentPosition());

                        notifyDataSetChanged();

                        try {
                            mMessages.optJSONObject(position).put("playingStatus", "" + mPlayer.getCurrentPosition());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                    handler.postDelayed(this, 1000);
                }
            });


            seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    Log.d("onStartTrackingTouch: ", "released");


                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    Log.d("onStartTrackingTouch: ", "started");

                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (mPlayer != null && fromUser) {
                        Log.d("onProgressChanged: ", "progress");
                        mPlayer.seekTo(mPlayer.getCurrentPosition());
//                        notifyDataSetChanged();

                    }
                }
            });


            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    try {
                        mMessages.optJSONObject(position).put("isPlaying", "0");
                        mMessages.optJSONObject(position).put("playingStatus", "0");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    stopplaying();
                }
            });
        }

        public int getProgressPercentage(long currentDuration, long totalDuration) {
            Double percentage = (double) 0;

            long currentSeconds = (int) (currentDuration / 1000);
            long totalSeconds = (int) (totalDuration / 1000);

            // calculating percentage
            percentage = (((double) currentSeconds) / totalSeconds) * 100;

            // return percentage
            return percentage.intValue();
        }

        public int progressToTimer(int progress, int totalDuration) {
            int currentDuration = 0;
            totalDuration = (int) (totalDuration / 1000);
            currentDuration = (int) ((((double) progress) / 100) * totalDuration);

            // return current duration in milliseconds
            return currentDuration * 1000;
        }

    }

    private class LongOperation extends AsyncTask<String, Void, String> {
        String url;
        File file;

        LongOperation(final String url_val, final File file_val) {
            url = url_val;
            file = file_val;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Thread.sleep(5000); // no need for a loop
            } catch (InterruptedException e) {
                Log.e("LongOperation", "Interrupted", e);
                return "Interrupted";
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Bitmap bitmap = getBitmapFromURL(url);
                File filepath = Environment.getExternalStorageDirectory();
                FileOutputStream fOut = null;
                String date = Utils.getDate(System.currentTimeMillis(), "ddMMyyyy");
                File zoeFolder = new File(filepath.getAbsolutePath(), mContext.getResources().getString(R.string.app_name)).getAbsoluteFile();
                File newFolder = new File(zoeFolder, mContext.getResources().getString(R.string.app_name) + " Image").getAbsoluteFile();
                final String saveFileName = "thumb_IMG_" + date + "ZC00" + ChatActivity.randomImageNo;
                File imageFile = null;
                imageFile = File.createTempFile(saveFileName, ".jpg", newFolder);
                fOut = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 15, fOut);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                options.inSampleSize = 8;
                Bitmap value = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
                Bitmap blurred = Utils.blur(mContext, value);
                blurred.compress(Bitmap.CompressFormat.PNG, 50, fOut);


                transferUtility = new TransferUtility(s3Client, mContext);
                transferObserver = transferUtility.upload(Const.bucket_name, "thumb_" + file.getName(), imageFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            transferObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state.toString().equalsIgnoreCase("COMPLETED")) {
                        thumb_url = Const.amazons3ServerImagePath + "thumb_" + file.getName();
                        Log.d("onStateChangedthum: ", "thumb:" + thumb_url);
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {


                }

                @Override
                public void onError(int id, Exception ex) {

                }
            });
            thumbnail = thumb_url;

        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView remove_view;
        LinearLayout parent_layout;
        TextView contact_number;
        ImageView sender_chat_image;
        //download
        SeekBar seek_bar;
        //sender text message type
        private TextView sender_txt, sender_time_txt;
        private View rootview;
        private ImageView sender_reply_status_image, upload_btn, upload_cancel_btn;
        private SimpleDraweeView receiver_chat_image, receiver_video_image;
        //receiver text message type
        private TextView receiver_txt, receiver_time_txt;
        private ImageView downLoad_Cancel;
        private ImageView sender_chat_sticker, receiver_chat_sticker;
        //date header
        private TextView date_header;
        private ProgressBar image_progress;
        private RelativeLayout upload_layout, download_layout, downloaded_audio, uploaded_audio, view_document, play_layput;
        //contact chat
        private TextView contact_name, add_contact, message, group_Receiver;
        private CircleImageView contact_image;
        private ImageView download_btn, sender_map_image, receiver_map_image, sender_video_image, play_audio, pause_audio, star_image, play_button, dummy_audio_ico;
        private LinearLayout contact_layout;
        private View contact_view_line;

        private ViewHolder(final View itemView) {
            super(itemView);
            //sender text msg
            rootview = itemView;
            sender_txt = (TextView) itemView.findViewById(R.id.sender_message_text);
            sender_time_txt = (TextView) itemView.findViewById(R.id.sender_time_text);
            sender_reply_status_image = (ImageView) itemView.findViewById(R.id.sender_reply_status_image);
            star_image = (ImageView) itemView.findViewById(R.id.star_image);
            dummy_audio_ico = (ImageView) itemView.findViewById(R.id.dummy);

            sender_chat_sticker = (ImageView) itemView.findViewById(R.id.chat_sender_sticker);
            receiver_chat_sticker = (ImageView) itemView.findViewById(R.id.chat_receiver_sticker);
            parent_layout = (LinearLayout) itemView.findViewById(R.id.parent_layout);

            remove_view = (ImageView) itemView.findViewById(R.id.remove_view_check);


            //image
            sender_chat_image = (ImageView) itemView.findViewById(R.id.chat_image_sender);
            upload_btn = (ImageView) itemView.findViewById(R.id.upload_btn);
            upload_cancel_btn = (ImageView) itemView.findViewById(R.id.upload_cancel_btn);

            //receiver text msg
            receiver_txt = (TextView) itemView.findViewById(R.id.receiver_text_message);
            receiver_time_txt = (TextView) itemView.findViewById(R.id.receiver_time_txt);
            //image
            receiver_chat_image = (SimpleDraweeView) itemView.findViewById(R.id.receiver_chat_image);
            date_header = (TextView) itemView.findViewById(R.id.date_header);


            image_progress = (ProgressBar) itemView.findViewById(R.id.chat_image_progress);
            upload_layout = (RelativeLayout) itemView.findViewById(R.id.upload_layout);
            uploaded_audio = (RelativeLayout) itemView.findViewById(R.id.uploaded);
            play_audio = (ImageView) itemView.findViewById(R.id.play_button);
            pause_audio = (ImageView) itemView.findViewById(R.id.pause_button);


            download_layout = (RelativeLayout) itemView.findViewById(R.id.download_layout);
            downloaded_audio = (RelativeLayout) itemView.findViewById(R.id.downloaded_layout);
            view_document = (RelativeLayout) itemView.findViewById(R.id.view_doc);
            play_layput = (RelativeLayout) itemView.findViewById(R.id.play_layout);

            downLoad_Cancel = (ImageView) itemView.findViewById(R.id.download_cancel_btn);
            seek_bar = (SeekBar) itemView.findViewById(R.id.seek_bar);


            //download
            download_btn = (ImageView) itemView.findViewById(R.id.download_btn);


            sender_map_image = (ImageView) itemView.findViewById(R.id.show_map_location);
            receiver_map_image = (ImageView) itemView.findViewById(R.id.receiver_map);


            //video thumbnail
            sender_video_image = (ImageView) itemView.findViewById(R.id.show_video_thumbnail);
            receiver_video_image = (SimpleDraweeView) itemView.findViewById(R.id.receiver_video_thumbnail);


            //contact name
            contact_name = (TextView) itemView.findViewById(R.id.contact_name);
            contact_image = (CircleImageView) itemView.findViewById(R.id.contact_image);
            add_contact = (TextView) itemView.findViewById(R.id.add_contact);
            contact_layout = (LinearLayout) itemView.findViewById(R.id.contact_layout);
            contact_view_line = (View) itemView.findViewById(R.id.contact_view_line);
            message = (TextView) itemView.findViewById(R.id.msg_contact);
            group_Receiver = (TextView) itemView.findViewById(R.id.group_receiver);
            contact_number = (TextView) itemView.findViewById(R.id.contact_number);
        }

    }
}
