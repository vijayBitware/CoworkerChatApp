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
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.bumptech.glide.Glide;
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
import com.bitware.coworker.activity.StarredMessages;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.fresco.processors.BlurPostprocessor;
import vc908.stickerfactory.StickersManager;

//import com.zybertron.zoechat.baseUtils.AndroidBmpUtil;


public class StarChatAdapter extends RecyclerView.Adapter<StarChatAdapter.ViewHolder> {
    public static boolean is_in_action_mode = false;
    public static ArrayList<String> selectRemoveItem;
    public static ArrayList<Integer> selectRemoveItemPosition;
    public static ArrayList<String> select_position = new ArrayList<>();

    public static int Scrollposition;
    public static JSONArray mMessages;
    public Boolean isFirstStarred = true;
    VideoRequestHandler videoRequestHandler;
    Picasso picassoInstance;
    AmazonS3Client s3Client ;
    TransferUtility transferUtility;
    TransferObserver transferObserver;
    String thumbnail, thumb_url;
    private Context mContext;
    public static Handler handler = new Handler();
    private static Runnable globalrunnable;
    MediaPlayer mPlayer = new MediaPlayer();
    private int selectedCount;
    private String TAG=StarChatAdapter.class.getSimpleName();


    public StarChatAdapter(Context context, JSONArray jsonArray) {
        mMessages = jsonArray;
        this.mContext = context;

        selectRemoveItem = new ArrayList<>();
        selectRemoveItemPosition = new ArrayList<>();
//        s3Client.setRegion(Const.Regionss3);
//        s3Client.setEndpoint(Const.ENDPOINT);
        System.setProperty(SDKGlobalConfiguration.ENFORCE_S3_SIGV4_SYSTEM_PROPERTY, "true");
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(mContext, Const.IDENTIY_POOL_ID, Const.cognitoRegion);
        s3Client = new AmazonS3Client(credentialsProvider);
        s3Client.setRegion(Const.REGIONS);
        s3Client.setEndpoint(Const.ENDPOINT);


        Log.d("messages", "" + mMessages);
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


    public static int getPrimaryCOlor(Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, value, true);
        return value.data;
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

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
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

        }   if (viewType == ChatMessages.RECEIVER_STICKER_MESSAGE) {
            v = LayoutInflater.from(mContext).inflate(R.layout.receiver_sticker_item, parent, false);
        }
        if (viewType == ChatMessages.SENDER_STICKER_MESSAGE) {
            v = LayoutInflater.from(mContext).inflate(R.layout.sender_sticker_item, parent, false);

        }
        v.setTag(viewType);
        setMargins(v, 0, 15, 0, 50);


        return new ViewHolder(v);
    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
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

        int value = StarredMessages.starChatAdapter.getItemViewType(position);
        String type_val;


        String date = getDate(Long.parseLong(jsonObject.optString("sentTime")), "dd/MM/yyyy");

        holder.date_star.setText(date);

        if (value == ChatMessages.RECEIVER_AUDIO_MESSAGE || value == ChatMessages.RECEIVER_CONTACT_MESSAGE
                || value == ChatMessages.RECEIVER_DOC_MESSAGE || value == ChatMessages.RECEIVER_VIDEO_MESSAGE || value == ChatMessages.RECEIVER_IMAGE_MESSAGE
                || value == ChatMessages.RECEIVER_LOCATION_MESSAGE || value == ChatMessages.RECEIVER_TEXT__MESSAGE) {
            type_val = "receiver";
        } else {
            type_val = "sender";
        }


        String user_type = mMessages.optJSONObject(position).optString("chatRoomType");

        if (type_val.equalsIgnoreCase("sender")) {
            if (user_type.equalsIgnoreCase("0"))//personal
            {
                holder.sender_star.setText(mContext.getResources().getString(R.string.you));
                holder.receiver_star.setText(jsonObject.optString("userName"));
            } else//group
            {
                holder.sender_star.setText(mContext.getResources().getString(R.string.you));
                holder.receiver_star.setText(dbHandler.GetGroupName(jsonObject.optString("groupId")));
            }
        } else {
            if (user_type.equalsIgnoreCase("0"))//personal
            {
                holder.receiver_star.setText(mContext.getResources().getString(R.string.you));
                holder.sender_star.setText(jsonObject.optString("userName"));
            } else//group
            {
                holder.sender_star.setText(jsonObject.optString("userName"));
                holder.receiver_star.setText(dbHandler.GetGroupName(jsonObject.optString("groupId")));

            }

        }


        Log.d("onBindViewHolder: ", "selected:" + selected);
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

        Log.d("onBindViewHolder: ", "json_array_values:" + mMessages.toString());


        String space = " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;" +
                "&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;";


        if (jsonObject.optString("userType").equalsIgnoreCase(ChatMessages.SENDER_STICKER))
        {
            StickersManager.with(mContext)
                    .loadSticker(jsonObject.optString("msg"))
                    .into((holder.sender_chat_sticker));
            try {
                double time = Double.valueOf(jsonObject.optString("sentTime"));
                holder.sender_time_txt.setText(Utils.getDate((long) time, "hh:mm a"));
                setStatus(jsonObject, holder.sender_reply_status_image,mContext);

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (jsonObject.optString("userType").equalsIgnoreCase(ChatMessages.RECEIVER_STICKER))
        {
            StickersManager.with(mContext)
                    .loadSticker(jsonObject.optString("msg"))
                    .into((holder.receiver_chat_sticker));
            try {
                double time = Double.valueOf(jsonObject.optString("sentTime"));
                holder.receiver_time_txt.setText(Utils.getDate((long) time, "hh:mm a"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        }

        if (jsonObject.optString("userType").equalsIgnoreCase(ChatMessages.SENDER)) {
            try {
                double time = Double.valueOf(jsonObject.optString("sentTime"));
                holder.sender_txt.setText(jsonObject.optString("msg").replaceAll("\\n", System.getProperty("line.separator")) + Html.fromHtml(space));

                holder.sender_time_txt.setText(Utils.getDate((long) time, "hh:mm a"));
                setStatus(jsonObject, holder.sender_reply_status_image,mContext);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        }

        if (jsonObject.optString("userType").equalsIgnoreCase(ChatMessages.RECEIVER)) {

            Log.d("onBindViewHolder: ", "" + jsonObject.optString("userName") + "--");

            String name = dbHandler.GetUserName(jsonObject.optString("userName"));
            holder.group_Receiver.setVisibility(View.GONE);
            holder.group_Receiver.setText(name);

            try {
                double time = Double.valueOf(jsonObject.optString("sentTime"));
                holder.receiver_txt.setText(jsonObject.optString("msg").replaceAll("\\n", System.getProperty("line.separator")) + Html.fromHtml(space));

                holder.receiver_time_txt.setText(Utils.getDate((long) time, "hh:mm a"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        }
//        holder.rootview.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                ChatActivity.toolbar.getMenu().clear();
//                ChatActivity.toolbar.inflateMenu(R.menu.action_single_chat);
//                ChatActivity.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
//                holder.rootview.setBackgroundResource(R.color.light_weight_gray);
//                ChatActivity.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        ChatFragment.mainActivity.clearActionM();
//                    }
//                });
//
//                is_in_action_mode = true;
//                notifyDataSetChanged();
//
//                holder.rootview.setBackgroundResource(R.color.light_weight_gray);
//
//                String id = jsonObject.optString("msgId");
//                addItem(id);
//
//                return true;
//            }
//        });

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


            if (dbHandler.getBitmap(jsonObject.optString("msgId")) != null)
            {
                Bitmap bitmap=Utils.getImage(dbHandler.getBitmap(jsonObject.optString("msgId")));
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Glide.with(mContext)
                        .load(stream.toByteArray())
                        .asBitmap()
                        .into(holder.sender_video_image);

            }
            else {
                videoRequestHandler = new VideoRequestHandler(jsonObject.optString("msgId"), mContext);
                picassoInstance = new Picasso.Builder(mContext.getApplicationContext())
                        .addRequestHandler(videoRequestHandler)
                        .build();

                picassoInstance.load(videoRequestHandler.SCHEME_VIDEO + ":" + url).into(holder.sender_video_image);
            }    setStatus(jsonObject, holder.sender_reply_status_image,mContext);

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

            String upload_status = dbHandler.isUploadStatus(jsonObject.optString("msgId"));
            if (upload_status.equalsIgnoreCase("0")) {

                UploadImages(jsonObject.optString("msg"), holder.image_progress, jsonObject.optString("msgId"), jsonObject.optString("userId"),
                        holder.upload_layout, holder.sender_reply_status_image, holder.upload_btn, holder.upload_cancel_btn, ChatType.video, jsonObject.optString("chatRoomType"), jsonObject.optString("groupId"));
                Log.e("test", "test");

            } else if (upload_status.equalsIgnoreCase("1")) {
                holder.image_progress.setVisibility(View.GONE);
                holder.upload_layout.setVisibility(View.GONE);

            }

            holder.upload_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    holder.image_progress.setVisibility(View.VISIBLE);
                    holder.upload_btn.setVisibility(View.GONE);
                    holder.upload_cancel_btn.setVisibility(View.GONE);

                    UploadImages(jsonObject.optString("msg"), holder.image_progress, jsonObject.optString("msgId"), jsonObject.optString("userId"),
                            holder.upload_layout, holder.sender_reply_status_image, holder.upload_btn, holder.upload_cancel_btn, ChatType.image, jsonObject.optString("chatRoomType"), jsonObject.optString("groupId"));

                }
            });
            holder.upload_cancel_btn.setOnClickListener(new View.OnClickListener() {
                @SuppressWarnings("EqualsBetweenInconvertibleTypes")
                @Override
                public void onClick(View view) {

                    int id = transferObserver.getId();
                    transferUtility.cancel(id);
                    holder.image_progress.setVisibility(View.GONE);
                    holder.upload_btn.setVisibility(View.VISIBLE);
                    holder.upload_cancel_btn.setVisibility(View.GONE);
                }
            });

            /*holder.sender_video_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ImageViewer_Activity.class);
                    String image_id = jsonObject.optString("msg");
                    String file = new File(image_id).getName();
                    intent.putExtra("select_file", file);
                    intent.putExtra("zoeChatId", zoeChatId);
                    mContext.startActivity(intent);
                }
            });*/
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

            if (upload_status.equalsIgnoreCase("uploading")) {
                holder.image_progress.setVisibility(View.VISIBLE);
                holder.upload_btn.setVisibility(View.GONE);
                holder.upload_cancel_btn.setVisibility(View.VISIBLE);
                holder.play_audio.setVisibility(View.GONE);
                holder.uploaded_audio.setVisibility(View.GONE);
                holder.upload_layout.setVisibility(View.VISIBLE);


            } else if (upload_status.equalsIgnoreCase("uploaded")) {
                holder.image_progress.setVisibility(View.GONE);
                holder.upload_layout.setVisibility(View.GONE);
                holder.upload_btn.setVisibility(View.VISIBLE);
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
            setStatus(jsonObject, holder.sender_reply_status_image,mContext);

            holder.view_document.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    File file = new File(getRealPathFromURI(mContext, Uri.parse(url)));
                    Log.d("onClick_doc: ", url);
//                    if (file.exists()) {
//                        MimeTypeMap myMime = MimeTypeMap.getSingleton();
//                        Intent newIntent = new Intent(Intent.ACTION_VIEW);
//                        ContentResolver cr = mContext.getContentResolver();
//                        String mime = cr.getType(Uri.parse(url));
//                        Log.d("onClick: ", "values:" + mime + "," + Uri.parse(url) + "," + Uri.fromFile(file));
//
////                            String mimeType = myMime.getMimeTypeFromExtension(fileExt(url).substring(1));
////                            Log.d( "onClick: ",""+fileExt(url).substring(1));
////                            Log.d("onClick: ","mime_type:"+mimeType);
//                        newIntent.setDataAndType(Uri.fromFile(file), mime);
//                        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        try {
//                            mContext.startActivity(newIntent);
//                        } catch (ActivityNotFoundException e) {
//                            Toast.makeText(mContext, "No handler for this type of file.", Toast.LENGTH_LONG).show();
//                        }
//                    } else {
//                        Toast.makeText(mContext, "Doc file does not exist", Toast.LENGTH_LONG).show();


                    Intent intent = new Intent(mContext, WebViewActivity.class);
                    intent.putExtra("doc_url", jsonObject.optString("msg"));
                    mContext.startActivity(intent);
//                    }

                }
            });

            String name = FilenameUtils.removeExtension(URLUtil.guessFileName(url, null, null));

            holder.sender_txt.setText(name);
            Log.d("onBindViewHolderdoc: ", "f_name_wext:" + name);

            String upload_status = dbHandler.isUploadStatus(jsonObject.optString("msgId"));
            if (upload_status.equalsIgnoreCase("0")) {

                Log.d("onBindViewHolderdoc: ", "url:" + url);


                Log.d("onBindViewHolderdoc: ", "f_name_ext:" + URLUtil.guessFileName(url, null, null));
                String f_path = getRealPathFromURI(mContext, Uri.parse(url));
                File file = new File(f_path);
                name = file.getName();
//                String name = FilenameUtils.removeExtension(URLUtil.guessFileName(url, null, null));
                Log.d("onBindViewHolderdoc: ", "f_name_wext:" + name);

                holder.sender_txt.setText(name);

                UploadImages(jsonObject.optString("msg"), holder.image_progress, jsonObject.optString("msgId"), jsonObject.optString("userId"),
                        holder.upload_layout, holder.sender_reply_status_image, holder.upload_btn, holder.upload_cancel_btn, ChatType.document, jsonObject.optString("chatRoomType"), jsonObject.optString("groupId"));

                Log.e("test", "test");

            } else if (upload_status.equalsIgnoreCase("1")) {
                holder.image_progress.setVisibility(View.GONE);
                holder.upload_layout.setVisibility(View.GONE);

            }


            holder.upload_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    holder.image_progress.setVisibility(View.VISIBLE);
                    holder.upload_btn.setVisibility(View.GONE);
                    holder.upload_cancel_btn.setVisibility(View.GONE);

                    UploadImages(jsonObject.optString("msg"), holder.image_progress, jsonObject.optString("msgId"), jsonObject.optString("userId"),
                            holder.upload_layout, holder.sender_reply_status_image, holder.upload_btn, holder.upload_cancel_btn, ChatType.document, jsonObject.optString("chatRoomType"), jsonObject.optString("groupId"));

                }
            });
            holder.upload_cancel_btn.setOnClickListener(new View.OnClickListener() {
                @SuppressWarnings("EqualsBetweenInconvertibleTypes")
                @Override
                public void onClick(View view) {

                    int id = transferObserver.getId();
                    transferUtility.cancel(id);
                    holder.image_progress.setVisibility(View.GONE);
                    holder.upload_btn.setVisibility(View.VISIBLE);
                    holder.upload_cancel_btn.setVisibility(View.GONE);


                }
            });

            /*holder.sender_video_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ImageViewer_Activity.class);
                    String image_id = jsonObject.optString("msg");
                    String file = new File(image_id).getName();
                    intent.putExtra("select_file", file);
                    intent.putExtra("zoeChatId", zoeChatId);
                    mContext.startActivity(intent);
                }
            });*/
        }


        //bind the receiver video
        if (jsonObject.optString("userType").equalsIgnoreCase(ChatMessages.RECEIVER_VIDEO)) {


            holder.group_Receiver.setVisibility(View.GONE);
            holder.group_Receiver.setText(jsonObject.optString("userName"));

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
//            BitmapPool bitmapPool = Picasso.get(mContext).getBitmapPool();
//            int microSecond = 6000000;// 6th second as an example
//            VideoBitmapDecoder videoBitmapDecoder = new VideoBitmapDecoder(microSecond);
//            FileDescriptorBitmapDecoder fileDescriptorBitmapDecoder = new FileDescriptorBitmapDecoder(videoBitmapDecoder, bitmapPool, DecodeFormat.PREFER_ARGB_8888);

//            Picasso.with(mContext).load(fileName).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.receiver_video_image);


            final String downloadStatus = dbHandler.isDownloadStatus(jsonObject.optString("msgId"));
            if (downloadStatus.equalsIgnoreCase("1")) {
                holder.downLoad_Cancel.setVisibility(View.GONE);
                holder.download_btn.setVisibility(View.GONE);
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
//                Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(url, MediaStore.Video.Thumbnails.MINI_KIND);

//                videoRequestHandler = new VideoRequestHandler();
//                picassoInstance = new Picasso.Builder(mContext.getApplicationContext())
//                        .addRequestHandler(videoRequestHandler)
//                        .build();
//
//                picassoInstance.load(videoRequestHandler.SCHEME_VIDEO + ":" + url).into(holder.receiver_video_image);
                holder.receiver_video_image.setImageBitmap(bitmap);
////
//                Picasso.with(mContext)
//                        .load(url)
//                        .into(holder.receiver_video_image);


            } else if (downloadStatus.equalsIgnoreCase("0")) {
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

            /*holder.receiver_video_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File file = new File(url);
                    if (file.exists()) {
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                        Uri uri = Uri.fromFile(file);
                        intent.setDataAndType(uri, URLConnection.guessContentTypeFromName(uri.toString()));
                        mContext.startActivity(intent);
                    } else {
                        Toast.makeText(mContext, "Video file does not exist", Toast.LENGTH_LONG).show();
                    }

                }
            });*/

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
                holder.downLoad_Cancel.setVisibility(View.GONE);
                holder.download_btn.setVisibility(View.GONE);
                holder.image_progress.setVisibility(View.GONE);
                holder.downloaded_audio.setVisibility(View.VISIBLE);
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

            } else if (downloadStatus.equalsIgnoreCase("0")) {
                holder.download_layout.setVisibility(View.VISIBLE);
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
        }
        if (jsonObject.optString("userType").equalsIgnoreCase(ChatMessages.RECEIVER_DOC)) {


            holder.group_Receiver.setVisibility(View.GONE);
            holder.group_Receiver.setText(dbHandler.GetUserName(jsonObject.optString("userName")));


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
                holder.downLoad_Cancel.setVisibility(View.GONE);
                holder.download_btn.setVisibility(View.VISIBLE);
                holder.image_progress.setVisibility(View.GONE);

            }

            /*holder.receiver_video_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File file = new File(url);
                    if (file.exists()) {
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                        Uri uri = Uri.fromFile(file);
                        intent.setDataAndType(uri, URLConnection.guessContentTypeFromName(uri.toString()));
                        mContext.startActivity(intent);
                    } else {
                        Toast.makeText(mContext, "Video file does not exist", Toast.LENGTH_LONG).show();
                    }

                }
            });*/

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

//                        File file = new File(getRealPathFromURI(mContext, Uri.parse(url)));
//                        Log.d("onClick_doc: ", url);
//                        if (file.exists()) {
//                            MimeTypeMap myMime = MimeTypeMap.getSingleton();
//                            Intent newIntent = new Intent(Intent.ACTION_VIEW);
//                            ContentResolver cr = mContext.getContentResolver();
//                            String mime = cr.getType(Uri.parse(url));
//                            Log.d("onClick: ", "values:" + mime + "," + Uri.parse(url));
////                            String mimeType = myMime.getMimeTypeFromExtension(fileExt(url).substring(1));
////                            Log.d( "onClick: ",""+fileExt(url).substring(1));
////                            Log.d("onClick: ","mime_type:"+mimeType);
//                            newIntent.setDataAndType(Uri.parse(url), mime);
//                            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            try {
//                                mContext.startActivity(newIntent);
//                            } catch (ActivityNotFoundException e) {
//                                Toast.makeText(mContext, "No handler for this type of file.", Toast.LENGTH_LONG).show();
//                            }
//                            try {
//                                mContext.startActivity(newIntent);
//                            } catch (ActivityNotFoundException e) {
//                                Toast.makeText(mContext, "No handler for this type of file.", Toast.LENGTH_LONG).show();
//                            }
//                        } else {
//                            Toast.makeText(mContext, "Doc file does not exist", Toast.LENGTH_LONG).show();
//                        }


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
                    Picasso.with(mContext).load(R.drawable.ic_account_circle).error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.sender_map_image);
                } else {
                    Picasso.with(mContext).load(jsonObject.optString("msg")).into(holder.sender_map_image);
                }


                holder.sender_time_txt.setText(Utils.getDate((long) time, "hh:mm a"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            setStatus(jsonObject, holder.sender_reply_status_image,mContext);

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

            /*holder.upload_cancel_btn.setVisibility(View.GONE);
            holder.upload_btn.setVisibility(View.GONE);
            holder.image_progress.setVisibility(View.GONE);*/
            Log.d("onBindViewHolder: ","jsonvaluessatar:"+jsonObject);
            double time = Double.valueOf(jsonObject.optString("sentTime"));
            if (jsonObject.optString("msg").equalsIgnoreCase(" ") || jsonObject.optString("msg").equalsIgnoreCase("")) {
                Picasso.with(mContext).load(R.drawable.ic_account_circle)
                        .error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.sender_chat_image);

            } else {
                Picasso.with(mContext).load(new File(jsonObject.optString("msg"))).into(holder.sender_chat_image);
            }


            Bitmap myBitmap = BitmapFactory.decodeFile(jsonObject.optString("msg"));
//
            holder.sender_chat_image.setImageBitmap(myBitmap);

//            holder.sender_chat_image.setIma
            holder.sender_time_txt.setText(Utils.getDate((long) time, "hh:mm a"));
            setStatus(jsonObject, holder.sender_reply_status_image,mContext);
            String upload_status = dbHandler.isUploadStatus(jsonObject.optString("msgId"));

            Log.d("onBindViewHolder: ", "messageL:" + jsonObject.optString("msg"));

            if (upload_status.equalsIgnoreCase("0")) {

                UploadImages(jsonObject.optString("msg"), holder.image_progress, jsonObject.optString("msgId"), jsonObject.optString("userId"),
                        holder.upload_layout, holder.sender_reply_status_image, holder.upload_btn, holder.upload_cancel_btn, ChatType.image, jsonObject.optString("chatRoomType"), jsonObject.optString("groupId"));
                Log.e("test", "test");

            } else if (upload_status.equalsIgnoreCase("1")) {
                holder.image_progress.setVisibility(View.GONE);
                holder.upload_layout.setVisibility(View.GONE);

            }

            holder.upload_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.image_progress.setVisibility(View.VISIBLE);
                    holder.upload_btn.setVisibility(View.GONE);
                    holder.upload_cancel_btn.setVisibility(View.GONE);


                    UploadImages(jsonObject.optString("msg"), holder.image_progress, jsonObject.optString("msgId"), jsonObject.optString("userId"),
                            holder.upload_layout, holder.sender_reply_status_image, holder.upload_btn, holder.upload_cancel_btn, ChatType.image, jsonObject.optString("chatRoomType"), jsonObject.optString("groupId"));

                }
            });
            holder.upload_cancel_btn.setOnClickListener(new View.OnClickListener() {
                @SuppressWarnings("EqualsBetweenInconvertibleTypes")
                @Override
                public void onClick(View view) {

                    int id = transferObserver.getId();
                    transferUtility.cancel(id);
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
                    intent.putExtra("zoeChatId", jsonObject.optString("userId"));
                    intent.putExtra("groupId", jsonObject.optString("groupId"));
                    intent.putExtra("chatRoomType", jsonObject.optString("chatRoomType"));
                    mContext.startActivity(intent);
                }
            });
        }
        if (jsonObject.optString("userType").equalsIgnoreCase(ChatMessages.RECEIVER_IMAGE)) {


            holder.group_Receiver.setVisibility(View.GONE);
            holder.group_Receiver.setText(jsonObject.optString("userName"));


            double time1 = Double.valueOf(jsonObject.optString("sentTime"));

            holder.receiver_time_txt.setText(Utils.getDate((long) time1, "hh:mm a"));
            final String downloadStatus = dbHandler.isDownloadStatus(jsonObject.optString("msgId"));
            if (downloadStatus.equalsIgnoreCase("0")) {
                holder.download_layout.setVisibility(View.VISIBLE);
                holder.download_btn.setVisibility(View.VISIBLE);
                holder.downLoad_Cancel.setVisibility(View.GONE);
                File file = new File(jsonObject.optString("msg"));
                String fileName = Const.amazons3ServerImagePath + "thumb_" + file.getName();
                ResizeOptions options = new ResizeOptions(350, 350);

                BlurPostprocessor blurPostprocessor = new BlurPostprocessor(mContext);

                ImageRequest request = ImageRequestBuilder
                        .newBuilderWithSource(Uri.parse(fileName))
                        .setPostprocessor(blurPostprocessor)
                        .setProgressiveRenderingEnabled(true).setResizeOptions(options)
                        .build();
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(request)
                        .setOldController(holder.receiver_chat_image.getController())
                        .build();
                holder.receiver_chat_image.setController(controller);

            } else if (downloadStatus.equalsIgnoreCase("1")) {
                holder.download_layout.setVisibility(View.GONE);
                Log.e("load_image_path", jsonObject.optString("msg"));

                if (jsonObject.optString("msg").equalsIgnoreCase(" ") || jsonObject.optString("msg").equalsIgnoreCase("")) {
                    Picasso.with(mContext).load(R.drawable.ic_account_circle)
                            .error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.receiver_chat_image);

                } else {

                    Picasso.with(mContext).load(new File(jsonObject.optString("msg"))).into(holder.receiver_chat_image);
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

        }

        if (jsonObject.optString("userType").equalsIgnoreCase(ChatMessages.RECEIVER_LOCATION)) {

            holder.group_Receiver.setVisibility(View.GONE);
            holder.group_Receiver.setText(jsonObject.optString("userName"));

            double time1 = Double.valueOf(jsonObject.optString("sentTime"));
            holder.receiver_time_txt.setText(Utils.getDate((long) time1, "hh:mm a"));
            if (jsonObject.optString("msg").equalsIgnoreCase(" ") || jsonObject.optString("msg").equalsIgnoreCase("")) {
                Picasso.with(mContext).load(R.drawable.ic_account_circle)
                        .error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.receiver_map_image);

            } else {

                Picasso.with(mContext).load(jsonObject.optString("msg")).into(holder.receiver_map_image);
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
            holder.date_header.setText(jsonObject.optString("date"));
        }
        if (jsonObject.optString("userType").equalsIgnoreCase(ChatMessages.CREATE_GROUP)) {
            holder.date_header.setText(jsonObject.optString("msg"));
        }

        if (jsonObject.optString("userType").equalsIgnoreCase(ChatMessages.SENDER_CONTACT)) {
            holder.contact_name.setText(jsonObject.optString("cName"));
            double time1 = Double.valueOf(jsonObject.optString("sentTime"));
            holder.sender_time_txt.setText(Utils.getDate((long) time1, "hh:mm a"));
            setStatus(jsonObject, holder.sender_reply_status_image,mContext);
            if (dbHandler.GetUserImage(jsonObject.optString("cNumber")).equalsIgnoreCase(" ") || dbHandler.GetUserImage(jsonObject.optString("cNumber")).equalsIgnoreCase("")) {
                Picasso.with(mContext).load(R.drawable.ic_account_circle)
                        .error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.contact_image);
            } else {

                Picasso.with(mContext).load(dbHandler.GetUserImage(jsonObject.optString("cNumber"))).error(R.drawable.ic_account_circle).into(holder.contact_image);
            }

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


            holder.group_Receiver.setVisibility(View.GONE);
            holder.group_Receiver.setText(jsonObject.optString("userName"));

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
                Picasso.with(mContext).load(R.drawable.ic_account_circle)
                        .error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.contact_image);
            } else {

                Picasso.with(mContext).load(dbHandler.GetUserImage(jsonObject.optString("cNumber"))).error(R.drawable.ic_account_circle).into(holder.contact_image);
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
                            add_contact.putExtra(ContactsContract.Intents.Insert.PHONE, jsonObject.optString("cNumber"));
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
                        StarredMessages.toolbar.getMenu().clear();
                        StarredMessages.back_layout.setVisibility(View.GONE);


                        if (type.equalsIgnoreCase("text")) {
                            isFirstStarred = true;
                            if (type_val.equalsIgnoreCase("receiver")) {
                                StarredMessages.toolbar.inflateMenu(R.menu.action_single_chat_unstar_rec);
                            } else {
                                StarredMessages.toolbar.inflateMenu(R.menu.action_single_chat_unstar);
                            }
                        } else {
                            if (type_val.equalsIgnoreCase("receiver")) {
                                StarredMessages.toolbar.inflateMenu(R.menu.action_single_no_copy_rec);

                            } else {
                                StarredMessages.toolbar.inflateMenu(R.menu.action_single_no_copy);
                            }

                        }


                        Log.d("onLongClick: ", "valueheader:" + value);

                        StarredMessages.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
                        StarredMessages.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                StarredMessages.clearActionM();
                                Log.d("onClick: ", "value:" + mMessages.length());
                                Log.d("onClick: ", "one");

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
                                selectRemoveItem.clear();selectRemoveItemPosition.clear();
                                StarredMessages.counter=0;
                                StarredMessages.back_layout.setVisibility(View.VISIBLE);

                            }
                        });
                        is_in_action_mode = true;
                        JSONObject object = mMessages.optJSONObject(position);
                        Log.d("onLongClick: ", "" + object);
                        StarredMessages.read_time = object.optString("seenTime");
                        StarredMessages.delievered_time = object.optString("deliveredTime");

                        object.remove("isSelected");
                        object.put("isSelected", "true");
                        notifyDataSetChanged();
                        StarredMessages.counter++;

                        updateCnt(StarredMessages.counter);
                        try {
                            holder.remove_view.setVisibility(View.VISIBLE);
                        }
                        catch ( Exception e)
                        {

                        }
                        holder.itemView.setBackgroundResource(R.color.selected_blue);
                        String id = object.optString("msgId");
                        select_position.add(0, String.valueOf(position));
                        addItem(id,position);
//                    Log.d("onLongClick:", "" + holder.remove_view.getVisibility() + "," + View.VISIBLE);


                    } catch (JSONException e) {
                        e.printStackTrace();
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
                    if (mMessages.optJSONObject(position).optString("isSelected").equalsIgnoreCase("true")) {
                        try {
                            Scrollposition = position;
                            Log.e("view_", "Visible");
                            holder.remove_view.setVisibility(View.GONE);
                            holder.itemView.setBackgroundResource(0);
                            StarredMessages.counter--;
                            JSONObject object = mMessages.optJSONObject(position);
                            object.remove("isSelected");
                            object.put("isSelected", "false");
                            updateCnt(StarredMessages.counter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        try {
                            Scrollposition = position;
                            Log.e("view_", "UNVisible");
//                            holder.remove_view.setVisibility(View.VISIBLE);
                            holder.itemView.setBackgroundResource(R.color.selected_blue);
                            StarredMessages.counter++;
                            updateCnt(StarredMessages.counter);
                            JSONObject object = mMessages.optJSONObject(position);
                            object.remove("isSelected");
                            object.put("isSelected", "true");
                            String id = mMessages.optJSONObject(position).optString("msgId");
                            addItem(id,position);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    if (ChatActivity.counter > 1) {


                        StarredMessages.toolbar.getMenu().clear();
                        StarredMessages.back_layout.setVisibility(View.GONE);
                        StarredMessages.toolbar.inflateMenu(R.menu.action_single_chat_one_un);
                        StarredMessages.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
                        StarredMessages.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                StarredMessages.clearActionM();
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
                                StarredMessages.back_layout.setVisibility(View.VISIBLE);
                                selectRemoveItem.clear();selectRemoveItemPosition.clear();
                                StarredMessages.counter=0;

                            }
                        });

                    }

                }
            }
        });

        selected = mMessages.optJSONObject(position).optString("isSelected");


        if (selected.equalsIgnoreCase("true")) {
            holder.itemView.setBackgroundResource(R.color.selected_blue);
        }

        dbHandler.close();
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
            File newFolder = new File(zoeFolder, mContext.getResources().getString(R.string.app_name)+" Video").getAbsoluteFile();
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
            File newFolder = new File(zoeFolder, mContext.getResources().getString(R.string.app_name)+" Document").getAbsoluteFile();
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
            File newFolder = new File(zoeFolder, mContext.getResources().getString(R.string.app_name)+" Audio").getAbsoluteFile();
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
        try {
            DBHandler dbHandler = new DBHandler(mContext);

            if (jsonObject.optString("chatRoomType").equalsIgnoreCase("0")) {
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
            } else {
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
                notifyDataSetChanged();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void addItem(String msgId,int position) {
        selectRemoveItem.add(msgId);
        selectRemoveItemPosition.add(position);
        Log.e("items", selectRemoveItem.toString());
    }

    private void updateCnt(int counter) {
        if (counter == 0) {
            StarredMessages.clearActionM();
            is_in_action_mode = false;
            selectRemoveItem.clear();selectRemoveItemPosition.clear();
        } else {
            StarredMessages.toolbar.setTitleTextColor(Color.WHITE);
            StarredMessages.toolbar.setTitle(counter + "  selected");
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


        ///for maincontent
        transferObserver = transferUtility.upload(Const.bucket_name, file.getName(), file);
        //image_progress.setMax(100);


        transferObserver.setTransferListener(new TransferListener() {

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
                    image_progress.setVisibility(View.GONE);
                    upload_lay.setVisibility(View.VISIBLE);
                    upload_btn.setVisibility(View.GONE);
                } else if (state.toString().equalsIgnoreCase("COMPLETED")) {

                    if (chatType == ChatType.image) {
                        try {
                            thumbnail = getthumnailfromfile(Const.amazons3ServerImagePath + file.getName(), file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (chatType == ChatType.video) {
                        try {
                            thumbnail = getVideothumbnail(image_path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }


                    image_progress.setVisibility(View.GONE);
                    upload_lay.setVisibility(View.GONE);
                    Log.d("upload_state", "completed");
                    Log.d("onStateChanged: ", "chattype:" + chatType);
                    dbHandler.UpdateIsUploaded(msgId,"0");
                    if (chatRoomType.equalsIgnoreCase("0")) {
                        if (!dbHandler.DoesChatsUser(userID)) {
                            if (ChatType.document == chatType) {
                                dbHandler.InsertChats(new ChatsModel(userID, "0", ChatMessages.SENDER_IMAGE,
                                        Const.amazons3ServerImagePath + file.getName(), Status.SENT.toString(), String.valueOf(System.currentTimeMillis()), chatType.toString()
                                        , 0, "", "", "", "", "0","0","", "","","","","","false","1",""));
                                Log.d("onStateChanged: ", "1");


                            } else {
                                dbHandler.InsertChats(new ChatsModel(userID, "0", ChatMessages.SENDER_IMAGE,
                                        image_path, Status.SENT.toString(), String.valueOf(System.currentTimeMillis()), chatType.toString()
                                        , 0, "", "", "", "", "0","","0", "","","","","","false","1",""));
                                Log.d("onStateChanged: ", "2");

                            }
                        } else {
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
                                        , 0, "", "", "", "", "0","0","", "","","","","","false","1",""));
                            } else {
                                Log.d("onStateChanged: ", "6");

                                dbHandler.InsertChats(new ChatsModel(groupId, "0", ChatMessages.SENDER_IMAGE,
                                        image_path, Status.SENT.toString(), String.valueOf(System.currentTimeMillis()), chatType.toString()
                                        , 0, "", "", "", "", "0","0","", "","","","","","false","1",""));
                            }


                        } else {
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
                        refresh(msgId, Const.amazons3ServerImagePath + file.getName());
                    }

                    if (chatType == ChatType.image || chatType == ChatType.video) {
                        ChatActivity.SendChatMsgwithThumb(Const.amazons3ServerImagePath + file.getName(), msgId,
                                dbHandler.getZoechatId(msgId), String.valueOf(currentTime), chatType, SharedHelper.getKey(mContext, "id"), groupId, chatRoomType, thumbnail, dbHandler.getNotifications(groupId));
                    } else {
                        ChatActivity.SendChatMsg(Const.amazons3ServerImagePath + file.getName(), msgId,
                                dbHandler.getZoechatId(msgId), String.valueOf(currentTime), chatType, SharedHelper.getKey(mContext, "id"), groupId, chatRoomType, dbHandler.getNotifications(groupId));
                    }


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

    private String getVideothumbnail(String image_path) throws IOException {
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(image_path, MediaStore.Video.Thumbnails.MINI_KIND);
        File filepath = Environment.getExternalStorageDirectory();
        FileOutputStream fOut = null;
        final File imagename = new File(image_path);
        String date = Utils.getDate(System.currentTimeMillis(), "ddMMyyyy");
        File zoeFolder = new File(filepath.getAbsolutePath(), mContext.getResources().getString(R.string.app_name)).getAbsoluteFile();
        File newFolder = new File(zoeFolder, mContext.getResources().getString(R.string.app_name)+" Image").getAbsoluteFile();
        final String saveFileName = "thumb_IMG_" + date + "ZC00" + ChatActivity.randomImageNo;
        final File imageFile = File.createTempFile(saveFileName, ".jpg", newFolder);
        fOut = new FileOutputStream(imageFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        final String f_name = FilenameUtils.removeExtension(imagename.getName());

        fOut.flush();
        fOut.close();
        transferUtility = new TransferUtility(s3Client, mContext);
//        File zoeFolder = new File(filepath.getAbsolutePath(), "ZoeChat").getAbsoluteFile();
//        String date = Utils.getDate(System.currentTimeMillis(), "ddMMyyyy");
//        File newFolder = new File(zoeFolder, "ZoeChat_Image").getAbsoluteFile();
//        final String saveFileName = "thumb_" + date + "ZC00" + ChatActivity.randomImageNo;
//        final File imageFile = File.createTempFile(saveFileName, ".jpg", newFolder);
        //for_thumbnail

        transferObserver = transferUtility.upload(Const.bucket_name, "thumb_" + f_name + ".jpg", imageFile);

        transferObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state.toString().equalsIgnoreCase("COMPLETED")) {
                    thumb_url = Const.amazons3ServerImagePath + "thumb_" + f_name + ".jpg";
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


        return thumb_url;

    }


    private String getthumnailfromfile(final String url, final File file) throws IOException {

        Bitmap bitmap = getBitmapFromURL(url);
//        Bitmap compressed = getCompressbitmap(bitmap);
//        Bitmap blurred = createBitmap_ScriptIntrinsicBlur(bitmap,80);

//        Bitmap compressed =  ThumbnailUtils.createVideoThumbnail(url, MediaStore.Images.Thumbnails.MICRO_KIND);
        File filepath = Environment.getExternalStorageDirectory();
        FileOutputStream fOut = null;
        String date = Utils.getDate(System.currentTimeMillis(), "ddMMyyyy");
        File zoeFolder = new File(filepath.getAbsolutePath(), mContext.getResources().getString(R.string.app_name)).getAbsoluteFile();
        File newFolder = new File(zoeFolder, mContext.getResources().getString(R.string.app_name)+" Image").getAbsoluteFile();
        final String saveFileName = "thumb_IMG_" + date + "ZC00" + ChatActivity.randomImageNo;
        final File imageFile = File.createTempFile(saveFileName, ".jpg", newFolder);
        fOut = new FileOutputStream(imageFile);
//        InputStream inputStream=new BufferedInputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 15, fOut);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap value = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
        Bitmap blurred = Utils.blur(mContext, value);
//        AndroidBmpUtil androidBmpUtil = new AndroidBmpUtil();
//        androidBmpUtil.save(blurred,imageFile.getAbsolutePath());
        blurred.compress(Bitmap.CompressFormat.PNG, 100, fOut);

        fOut.flush();
        fOut.close();


        transferUtility = new TransferUtility(s3Client, mContext);
//        File zoeFolder = new File(filepath.getAbsolutePath(), "ZoeChat").getAbsoluteFile();
//        String date = Utils.getDate(System.currentTimeMillis(), "ddMMyyyy");
//        File newFolder = new File(zoeFolder, "ZoeChat_Image").getAbsoluteFile();
//        final String saveFileName = "thumb_" + date + "ZC00" + ChatActivity.randomImageNo;
//        final File imageFile = File.createTempFile(saveFileName, ".jpg", newFolder);
        //for_thumbnail

        transferObserver = transferUtility.upload(Const.bucket_name, "thumb_" + file.getName(), imageFile);

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


        return thumb_url;
    }

    private Bitmap createBitmap_ScriptIntrinsicBlur(Bitmap src, float r) {

        //Radius range (0 < r <= 25)
        if (r <= 0) {
            r = 0.1f;
        } else if (r > 25) {
            r = 25.0f;
        }

        Bitmap bitmap = Bitmap.createBitmap(
                src.getWidth(), src.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(mContext);

        Allocation blurInput = Allocation.createFromBitmap(renderScript, src);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
                Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(r);
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();
        return bitmap;
    }


    private String fileExt(String url) {
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
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
                    Log.d("upload_state", "completed");

                    dbHandler.UpdateIsUploaded(msgId,"0");
                    if (chatRoomType.equalsIgnoreCase("0")) {
                        if (!dbHandler.DoesChatsUser(userID)) {
                            dbHandler.InsertChats(new ChatsModel(userID, "0", ChatMessages.SENDER_IMAGE,
                                    image_path, Status.SENT.toString(), String.valueOf(System.currentTimeMillis()), chatType.toString()
                                    , 0, "", "", "", "", "0","0","", "","","","","","false","1",""));
                            dbHandler.UpdateLastMsg(userID, ChatMessages.SENDER_IMAGE, image_path,
                                    Status.SENT.toString(), String.valueOf(System.currentTimeMillis()), chatType.toString(), 0);
                        }
                    } else if (chatRoomType.equalsIgnoreCase("1")) {
                        if (!dbHandler.DoesChatsUser(groupId)) {
                            dbHandler.InsertChats(new ChatsModel(groupId, "0", ChatMessages.SENDER_IMAGE,
                                    image_path, Status.SENT.toString(), String.valueOf(System.currentTimeMillis()), chatType.toString()
                                    , 0, "", "", "", "", "0","0","", "","","","","","false","1",""));
                        } else {
                            dbHandler.UpdateLastMsg(groupId, ChatMessages.SENDER_IMAGE, image_path,
                                    Status.SENT.toString(), String.valueOf(System.currentTimeMillis()), chatType.toString(), 0);
                        }
                    }

                    long currentTime = System.currentTimeMillis();
                    ChatActivity.SendChatMsg(Const.amazons3ServerImagePath + file.getName(), msgId,
                            dbHandler.getChatRoomType(msgId), String.valueOf(currentTime), chatType, SharedHelper.getKey(mContext, "id"), groupId, chatRoomType, dbHandler.getNotifications(groupId));
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
            File newFolder = new File(zoeFolder, mContext.getResources().getString(R.string.app_name)+" Image").getAbsoluteFile();
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

    private void refresh(String msgId, String path) {
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

        private void stopplaying() {

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

        private void setDefaultvalues(int position) throws Exception {

            for (int i = 0; i < mMessages.length(); i++) {
                if (i != position) {
                    mMessages.optJSONObject(i).put("isPlaying", "0");
                    mMessages.optJSONObject(i).put("playingStatus", "0");
                }
            }
            notifyDataSetChanged();


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


    @Override
    public int getItemCount() {
        return mMessages.length();
    }

    @Override
    public int getItemViewType(int position) {
        return Integer.parseInt(mMessages.optJSONObject(position).optString("userType"));
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView remove_view;
        TextView sender_star, receiver_star, date_star;
        //sender text message type
        private TextView sender_txt, sender_time_txt;
        private View rootview;
        private ImageView sender_reply_status_image, upload_btn, upload_cancel_btn;
        ImageView sender_chat_image;
        private SimpleDraweeView receiver_chat_image, receiver_video_image;
        //receiver text message type
        private TextView receiver_txt, receiver_time_txt;
        private ImageView downLoad_Cancel;
        //date header
        private TextView date_header;
        private ProgressBar image_progress;
        private RelativeLayout upload_layout, download_layout, downloaded_audio, uploaded_audio, view_document, play_layput, star_header;
        //contact chat
        private TextView contact_name, add_contact, message, group_Receiver;
        private CircleImageView contact_image;
        private ImageView sender_chat_sticker,receiver_chat_sticker;

        //download
        SeekBar seek_bar;

        private ImageView download_btn, sender_map_image, receiver_map_image, sender_video_image, play_audio, pause_audio, star_image, play_button, dummy_audio_ico;


        private ViewHolder(final View itemView) {
            super(itemView);
            //sender text msg
            rootview = itemView;
            sender_txt = (TextView) itemView.findViewById(R.id.sender_message_text);
            sender_time_txt = (TextView) itemView.findViewById(R.id.sender_time_text);
            sender_reply_status_image = (ImageView) itemView.findViewById(R.id.sender_reply_status_image);
            star_image = (ImageView) itemView.findViewById(R.id.star_image);

            remove_view = (ImageView) itemView.findViewById(R.id.remove_view_check);


            sender_chat_sticker=(ImageView)itemView.findViewById(R.id.chat_sender_sticker);
            receiver_chat_sticker=(ImageView)itemView.findViewById(R.id.chat_receiver_sticker);

            sender_star = (TextView) itemView.findViewById(R.id.sender_star_text);
            receiver_star = (TextView) itemView.findViewById(R.id.receiver_star_text);
            date_star = (TextView) itemView.findViewById(R.id.date_star);


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


            //download
            download_btn = (ImageView) itemView.findViewById(R.id.download_btn);
            star_header = (RelativeLayout) itemView.findViewById(R.id.star_header);
            star_header.setVisibility(View.VISIBLE);


            sender_map_image = (ImageView) itemView.findViewById(R.id.show_map_location);
            receiver_map_image = (ImageView) itemView.findViewById(R.id.receiver_map);

            //video thumbnail
            sender_video_image = (ImageView) itemView.findViewById(R.id.show_video_thumbnail);
            receiver_video_image = (SimpleDraweeView) itemView.findViewById(R.id.receiver_video_thumbnail);
            seek_bar = (SeekBar) itemView.findViewById(R.id.seek_bar);


            //contact name
            contact_name = (TextView) itemView.findViewById(R.id.contact_name);
            contact_image = (CircleImageView) itemView.findViewById(R.id.contact_image);
            add_contact = (TextView) itemView.findViewById(R.id.add_contact);
            message = (TextView) itemView.findViewById(R.id.msg_contact);
            group_Receiver = (TextView) itemView.findViewById(R.id.group_receiver);


        }

    }
}
