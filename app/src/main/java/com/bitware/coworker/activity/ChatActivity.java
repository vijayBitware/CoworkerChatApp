package com.bitware.coworker.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.emoji.Emoji;
import com.vanniktech.emoji.listeners.OnEmojiBackspaceClickListener;
import com.vanniktech.emoji.listeners.OnEmojiClickedListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupDismissListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupShownListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardCloseListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardOpenListener;
import com.bitware.coworker.DBHelper.DBHandler;
import com.bitware.coworker.FCM.FCMMsgService;
import com.bitware.coworker.R;
import com.bitware.coworker.Service.ServiceClasss;
import com.bitware.coworker.adapter.SingleChatAdapter;
import com.bitware.coworker.baseUtils.AppController;
import com.bitware.coworker.baseUtils.AsyncTaskCompleteListener;
import com.bitware.coworker.baseUtils.Const;
import com.bitware.coworker.baseUtils.CustomDialog;
import com.bitware.coworker.baseUtils.MyCommon;
import com.bitware.coworker.baseUtils.PostHelper;
import com.bitware.coworker.baseUtils.SharedHelper;
import com.bitware.coworker.baseUtils.Utils;
import com.bitware.coworker.fragment.ChatFragment;
import com.bitware.coworker.models.CallsModel;
import com.bitware.coworker.models.ChannelParticiapntsModel;
import com.bitware.coworker.models.ChatMessages;
import com.bitware.coworker.models.ChatType;
import com.bitware.coworker.models.ChatsMessagesModel;
import com.bitware.coworker.models.ChatsModel;
import com.bitware.coworker.models.GroupParticiapntsModel;
import com.bitware.coworker.models.Status;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;

import static android.view.View.GONE;

//import android.view.ViewAnimationUtils;


public class ChatActivity extends AppCompatActivity implements View.OnClickListener, AsyncTaskCompleteListener {
    private static final String TAG = ChatActivity.class.getSimpleName();
    public static SingleChatAdapter singleChatAdapter;
    public static ChatFragment mainActivity;
    public static String zoeChatID = "", name = "", sendChatValue = "",
            image = "", groupId_loc = "", groupName = "", create_by = "", sent_by = "", group_image = " ";
    public static JSONArray jsonArray = new JSONArray();
    public static ImageView userIcon;
    public static int randomImageNo;
    public static String localChatRoomType = "";
    public static int counter = 0;
    public static JSONArray amazonIds = new JSONArray();
    public static String read_time, delievered_time;
    public static Context context;
    public static Toolbar toolbar;
    public static Bitmap doodle_bitmap;
    public static LinearLayout back_layout;
    public static RelativeLayout parent_layout;
    public static RelativeLayout input_layout;
    public static FrameLayout move_layout;
    public static ImageView back, add_attachment;
    public static TextView proName;
    public static int unreadCount = 0;
    public static JSONArray particpants_array = new JSONArray();
    public static List<GroupParticiapntsModel> participan_model = new ArrayList<>();
    public static List<ChannelParticiapntsModel> participan_model_c = new ArrayList<>();
    public static JSONObject group_details = new JSONObject();
    public static Boolean isSending = false;
    public static LinearLayoutManager linearLayoutManager;
    public static DBHandler dbHandler;
    public static AsyncTask<Void, Void, Void> object;
    public static boolean showPreview;
    public static String title;
    public static String description;
    public static String logo;
    public static RelativeLayout member_layout;
    public static TextView user_title;
    public static TextView sub_user_title;
    public static TransferUtility transferUtility;
    public static TransferObserver transferObserver;
    ChatType resultChattype = null;
    String resultMmessageType = "";
    int amazonIdVal;
    boolean hasResponseCome = true;
    ImageView hide_key;
    int bottompos = 0;
    int i = 1;
    String path_of_record;
    Uri uri;
    Boolean typing = false;
    Vibrator vibrate;
    RelativeLayout recording_lay, linkPreviewLayout;
    ImageView mic, notifications;
    LinearLayout cancelPreview;
    Chronometer timer;
    BottomSheetDialog bottomSheetDialog;
    BottomSheetBehavior bottomSheetBehavior;
    View bottomSheetView;
    AmazonS3Client s3Client;
    private int page = 0;
    private boolean internet;
    //private ChatListAdapter listAdapter;
    public static RecyclerView chatListView;
    private EmojiEditText chat_text;
    private ViewGroup rootView;
    private TextView metaTitle, metaDescription;
    private ImageView emojiButton, scrollDown, metaLogo;
    private EmojiPopup emojiPopup;
    private RevealFrameLayout mRevealView;
    private CardView circular_reveal;
    private boolean check = true;
    private int value = 0;
    private Handler handler;
    private Handler handler1;
    private double lat = 0.0, lng = 0.0;
    private MediaPlayer mPlayer;
    private int w, h, endRadius, cx, cy;
    private Runnable checkOnline = new Runnable() {
        @Override
        public void run() {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", zoeChatID);
                jsonObject.put("from", MainActivity.my_id);
                ServiceClasss.Emitters emitters = new ServiceClasss.Emitters(context);
                emitters.getOnline(jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            handler.postDelayed(this, 5000);
        }

    };
    private Runnable internetCheck = new Runnable() {
        @Override
        public void run() {

            Boolean isAvailable = isNetworkAvailable(getApplicationContext());

            if (isAvailable) {

                if (!isSending) {

                    isSending = true;
//                    sendingmessage();
                }
            }

            handler1.postDelayed(this, 5000);
        }

    };
    private MediaRecorder myAudioRecorder;
    private Timer typetimer;
    private boolean isTyping = false;
    private Animator revealAnimator;
    private Animator anim;
    private String final_filePath = "";
    private Intent intent;
    private RelativeLayout header;
    private ImageButton sendChat;
    private ImageButton sendaudio;
    private ImageView camera_photo;
    private int status;
    private int START_DRAGGING = 1;
    private float dX;
    private float dY;
    private int lastAction;
    private float homeX, homeY;
    private int[] homeLocation;
    private int homeLeft, homeRight, homeTop, homeBottom;
    private boolean firstTime = true;
    private boolean recStopped;
    private RelativeLayout.LayoutParams defaultParams;
    private ViewGroup.LayoutParams defaultParamsRecord;
    private int check_value = 7;
    private Context mContext = ChatActivity.this;
    private boolean checkOnce = true;
    private boolean isRight = false;

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
        shape.setCornerRadii(new float[]{100, 100, 100, 100, 100, 100, 100, 100});
        shape.setColor(backgroundColor);
        shape.setStroke(3, borderColor);
        v.setBackgroundDrawable(shape);
    }

    public static void SendChatMsgunsent(String image_path, String msgId, String userID,
                                         String currentTime, String chatType, String from, String groupId, String chatRoomType, String notii, Context context, String showpreview, String title, String description, String logo) {


        ServiceClasss.Emitters emitters = new ServiceClasss.Emitters(context);
        try {
            emitters.SendChatMsgunsent(image_path, msgId, userID,
                    currentTime, chatType, from, groupId, chatRoomType, notii, ChatActivity.title, ChatActivity.description, ChatActivity.logo, showPreview, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void SendChatMsg(String image_path, String msgId, String userID,
                                   String currentTime, ChatType chatType, String from, String groupId, String chatRoomType, String notifications) {

        ServiceClasss.Emitters emitters = new ServiceClasss.Emitters(context);
        try {
            emitters.SendChatMsg(image_path, msgId, userID,
                    currentTime, chatType, from, groupId, chatRoomType, notifications, title, description, logo, showPreview, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public static void SendChatMsgwithThumb(String image_path, String msgId, String userID,
                                            String currentTime, ChatType chatType, String from, String groupId, String chatRoomType, String thumb, String notifications) {


        ServiceClasss.Emitters emitters = new ServiceClasss.Emitters(context);
        try {
            emitters.SendChatMsgwithThumb(image_path, msgId, userID,
                    currentTime, chatType, from, groupId, chatRoomType, thumb, notifications, title, description, logo, showPreview, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void clearActionM() {

        toolbar.getMenu().clear();
        if (localChatRoomType.equalsIgnoreCase("0")) {
            toolbar.inflateMenu(R.menu.single_chat_menu);
        } else {
            toolbar.inflateMenu(R.menu.group_chat_menu);
        }
        toolbar.setNavigationIcon(null);
        counter = 0;
        Log.d("clearActionM: ", "" + counter);
        ChatActivity.back_layout.setVisibility(View.VISIBLE);

        singleChatAdapter.notifyDataSetChanged();


    }

    public static void refresh() {
        try {

            singleChatAdapter.notifyDataSetChanged();
        } catch (Exception e) {

        }
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

    private static String getStringFromBitmap(Bitmap bitmapPicture) {
 /*
 * This functions converts Bitmap picture to a string which can be
 * JSONified.
 * */
        final int COMPRESSION_QUALITY = 100;
        String encodedImage;
        java.io.ByteArrayOutputStream byteArrayBitmapStream = new java.io.ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
                byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

    public static List<String> extractUrls(String text) {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https?):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find()) {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }

        return containedUrls;
    }

    public static String escapeJavaString(String st) {
        StringBuilder builder = new StringBuilder();
        try {
            for (int i = 0; i < st.length(); i++) {
                char c = st.charAt(i);
                if (!Character.isLetterOrDigit(c) && !Character.isSpaceChar(c) && !Character.isWhitespace(c)) {
                    String unicode = String.valueOf(c);
                    int code = (int) c;
                    if (!(code >= 0 && code <= 255)) {
                        unicode = "\"\\\\u" + Integer.toHexString(c) + "\"";
                    }
                    builder.append(unicode);
                } else {
                    builder.append(c);
                }
            }
            Log.i("Unicode Block", builder.toString());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return builder.toString();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String themevalue = SharedHelper.getKey(this, "theme_value");
        Setheme(themevalue);
        setContentView(R.layout.activity_chat);
        initviews();
        initvalues();
        initbottomsheet();
        try {
            getWindow().setBackgroundDrawableResource(R.drawable.wallpaper);
        } catch (Exception e) {

        }
        Utils.enableStrictMode();
        cancelPreview.setClickable(true);
        cancelPreview.setOnClickListener(this);

        setSupportActionBar(toolbar);

        if (localChatRoomType.equalsIgnoreCase("0")) {
            ServiceClasss.current_id = zoeChatID;
        } else {
            ServiceClasss.current_id = groupId_loc;
        }


        findViewById(R.id.back_layout).findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FCMMsgService.current_id = "";
                onBackPressed();
            }
        });


        add_attachment.setOnClickListener(this);


        intent = getIntent();
        if (intent != null) {
            Log.d("onCreate: ", "values:" + intent);


            if (intent.getStringExtra("user_name").equalsIgnoreCase("34654745647")) {
                user_title.setText("Support Helpline");
                userIcon.setBackgroundResource(R.drawable.ic_helpline);

            } else {
                user_title.setText(intent.getStringExtra("user_name"));
            }

            name = intent.getStringExtra("user_name");
            zoeChatID = intent.getStringExtra("zoeChatID");
            create_by = intent.getStringExtra("create_by");
            groupId_loc = intent.getStringExtra("groupId");
            localChatRoomType = intent.getStringExtra("chatRoomType");
            groupName = intent.getStringExtra("groupName");
            group_image = intent.getStringExtra("grp_image");
            image = intent.getStringExtra("image");


            SharedHelper.putKey(ChatActivity.this, "details_zoe_chat_id", zoeChatID);
            SharedHelper.putKey(ChatActivity.this, "details_grp_chat_id", groupId_loc);
            final_filePath = "";

            ChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (localChatRoomType.equalsIgnoreCase("0")) {
                        handlePersonal();


                    } else if (localChatRoomType.equalsIgnoreCase("1")) {
                        handleGroup();

                    } else if (localChatRoomType.equalsIgnoreCase("2")) {
                        handlebroadcast();

                    } else if (localChatRoomType.equalsIgnoreCase("3")) {
                        handlechannel();

                    }


                }
            });


            if (!final_filePath.equalsIgnoreCase("")) {


                if (!final_filePath.equalsIgnoreCase("no")) {
                    String c = final_filePath.substring(0, 3);


                    if (c.equalsIgnoreCase("val")) {
                        String rem = final_filePath.substring(3, final_filePath.length());
                        int val = Integer.parseInt(rem);
                        ColorDrawable d = new ColorDrawable(val);
                        getWindow().setBackgroundDrawable(d);
                    } else {
                        Drawable d = Drawable.createFromPath(final_filePath);
                        getWindow().setBackgroundDrawable(d);
                    }

                } else {
                    if (final_filePath.equalsIgnoreCase("default")) {
                        getWindow().setBackgroundDrawableResource(R.drawable.wallpaper);
                    } else {
                        getWindow().setBackgroundDrawableResource(R.drawable.no_wallpaper);
                    }

                }

            }


            ImageView photo = (ImageView) findViewById(R.id.photo);
            ImageView doodle = (ImageView) findViewById(R.id.doodle);
            ImageView camera = (ImageView) findViewById(R.id.video);
            ImageView audio = (ImageView) findViewById(R.id.audio);
            ImageView map = (ImageView) findViewById(R.id.location);
            ImageView contacts = (ImageView) findViewById(R.id.contact);
            ImageView documents = (ImageView) findViewById(R.id.document);
            cx = (int) (mRevealView.getWidth() / 1.7);
            mRevealView.setOnClickListener(this);

            photo.setOnClickListener(this);
            doodle.setOnClickListener(this);
            camera.setOnClickListener(this);
            audio.setOnClickListener(this);
            map.setOnClickListener(this);
            contacts.setOnClickListener(this);
            documents.setOnClickListener(this);

            linearLayoutManager = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, true);
            linearLayoutManager.setStackFromEnd(true);


            mRevealView.setVisibility(View.INVISIBLE);

            mRevealView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        revealShow(check);
                    }
                    return true;
                }
            });
            //chatMessages = new ArrayList<>();


            scrollDown = (ImageView) findViewById(R.id.scroll_down);
            scrollDown.setVisibility(GONE);

            if (jsonArray.length() == 0) {
                SharedHelper.putHeader(ChatActivity.this, "header_time", "0");
            }

            singleChatAdapter = new SingleChatAdapter(ChatActivity.this, jsonArray, zoeChatID, localChatRoomType, groupId_loc);
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false);
            linearLayoutManager.setAutoMeasureEnabled(true);
            chatListView.setLayoutManager(linearLayoutManager);
            chatListView.setHasFixedSize(false);
            chatListView.setAdapter(singleChatAdapter);
            singleChatAdapter.notifyDataSetChanged();
            check_value = jsonArray.length() - 50;

            chatListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    Log.d(TAG, "onScrollStateChanged: " + linearLayoutManager.findFirstVisibleItemPosition());
                    if (linearLayoutManager.findFirstVisibleItemPosition() < check_value) {
                        if (firstTime) {
                            firstTime = false;
                            JSONArray newJsonArray = dbHandler.GetRemChatsMessages(zoeChatID, "", "");
                            for (int i = newJsonArray.length() - 1; i >= 0; i--) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject = newJsonArray.optJSONObject(i);
                                try {
                                    addToPos(0, jsonObject, jsonArray);
//                                    jsonArray.put(0,jsonObject);
                                    singleChatAdapter.notifyItemInserted(0);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
//                            Toast.makeText(ChatActivity.this, "New Items Added", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            });

//
//            chatListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                @Override
//                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                    super.onScrollStateChanged(recyclerView, newState);
//                }
//
//                @Override
//                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                    super.onScrolled(recyclerView, dx, dy);
//                    if (dy < 0) {
//                        // Recycle view scrolling up...
//                        scrollDown.setVisibility(View.VISIBLE);
//                    } else if (dy > 0) {
//                        // Recycle view scrolling down...
//                        scrollDown.setVisibility(GONE);
//                    }
//
//                }
//            });

            scrollToBottom();
            updateUnReadCount();
            scrollDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    scrollToBottom();
                    scrollDown.setVisibility(GONE);
                }
            });
            header.setOnClickListener(this);

            if (!typing) {
                recording_lay.setVisibility(GONE);
                sendaudio.setVisibility(View.VISIBLE);
                sendChat.setVisibility(GONE);
            } else {
                recording_lay.setVisibility(GONE);
                sendaudio.setVisibility(GONE);
                sendChat.setVisibility(View.VISIBLE);
            }


            chat_text.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (chatListView.getLayoutManager() != null) {
                        bottompos = linearLayoutManager.findLastVisibleItemPosition();
                    }
                    Log.e(TAG, "onTouch: " + bottompos + "  " + singleChatAdapter.getItemCount());

                    return false;
                }
            });

            if (SharedHelper.getKey(ChatActivity.this, "enter_key").equalsIgnoreCase("yes")) {
                chat_text.setSingleLine(true);
                chat_text.setImeOptions(EditorInfo.IME_ACTION_SEND);
            } else {
                chat_text.setSingleLine(false);
                chat_text.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            }


            chat_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEND) {
                        if (!chat_text.getText().toString().matches("")) {
                            String uniqueID;
                            uniqueID = UUID.randomUUID().toString();
                            Log.e("uniqueID", uniqueID);
                            long currentTime = System.currentTimeMillis();
                            Log.e("getDateMillies", "" + currentTime);

                            Log.e("getDate", Utils.getDate(currentTime, "dd/MM/yyyy hh:mm:ss.SSS"));

                            sendChatValue = chat_text.getText().toString().trim();

                            Log.d(TAG, "onClickemoji: " + escapeJavaString(sendChatValue));

                            linkPreviewLayout.setVisibility(GONE);

                            sendMsg(zoeChatID, sendChatValue, uniqueID, String.valueOf(currentTime), ChatType.text, ChatMessages.SENDER, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));

                            chat_text.setText("");

                        } else {
                            //Toast.makeText(ChatActivity.this, "Hold to record, release to send", Toast.LENGTH_LONG).show();
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
            });


            chatListView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

                    Log.e(TAG, "onLayoutChange: " + " " + top + " " + bottom + " " + oldTop + " " + scrollToBottm());
                    try {
                        if (bottom < oldBottom) {
                            if (bottompos == scrollToBottm()) {
                                chatListView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        chatListView.scrollToPosition(scrollToBottm());
                                    }
                                }, 10);
                            }
                        }
                    } catch (Exception e) {

                    }
                }
            });

//backhere

            camera_photo.setOnClickListener(this);

            emojiButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    emojiPopup.toggle();
                }
            });


            //handling forward messages//
            try {
                if (intent.getStringExtra("send_message").equalsIgnoreCase("yes")) {

                    String message_type = intent.getStringExtra("message_type");

                    Log.d("onCreate: ", "message:" + message_type + ",message_type:" + intent.getStringExtra("message"));

                    String prev;
                    prev = intent.getStringExtra("preview");
                    if (prev.equalsIgnoreCase("false") || prev.equalsIgnoreCase("0")) {
                        showPreview = false;
                    } else {
                        showPreview = true;
                    }
                    title = intent.getStringExtra("title");
                    logo = intent.getStringExtra("logo");
                    description = intent.getStringExtra("description");

                    if (message_type.equalsIgnoreCase(String.valueOf(ChatType.image.toString()))) {
                        String message = intent.getStringExtra("message");
                        uploadimage(message);
                    } else if (message_type.equalsIgnoreCase(String.valueOf(ChatType.audio.toString()))) {
                        String message = intent.getStringExtra("message");
                        uploadaudio(message);
                    } else if (message_type.equalsIgnoreCase(String.valueOf(ChatType.video.toString()))) {
                        String message = intent.getStringExtra("message");
                        uploadvideo(message);

                    } else if (message_type.equalsIgnoreCase(String.valueOf(ChatType.contact.toString()))) {
                        String message = intent.getStringExtra("message");
                        String c_name = intent.getStringExtra("c_name");
                        String c_id = intent.getStringExtra("c_id");
                        uploadcontact(message, c_name, c_id);
                    } else if (message_type.equalsIgnoreCase(String.valueOf(ChatType.location.toString()))) {
                        String message = intent.getStringExtra("message");
                        uploadlocation(message);
                    } else if (message_type.equalsIgnoreCase(String.valueOf(ChatType.sticker.toString()))) {
                        String message = intent.getStringExtra("message");
                        uploadsticker(message);
                    } else if ((message_type.equalsIgnoreCase(String.valueOf(ChatType.document.toString())))) {
                        String media = intent.getStringExtra("media");
                        uploaddocument(media, intent.getStringExtra("send_message_ac"));
                    } else if (message_type.equalsIgnoreCase(String.valueOf(ChatType.text.toString()))) {
                        String message = intent.getStringExtra("message");
                        Log.d("onCreate: ", "came_in:" + message);
                        String uniqueID;
                        uniqueID = UUID.randomUUID().toString();
                        Log.e("uniqueID", uniqueID);
                        long currentTime = System.currentTimeMillis();
                        Log.e("getDateMillies", "" + currentTime);
                        Log.e("getDate", Utils.getDate(currentTime, "dd/MM/yyyy hh:mm:ss.SSS"));
                        sendMsg(zoeChatID, message, uniqueID, String.valueOf(currentTime), ChatType.text, ChatMessages.SENDER, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));
                        //                    chat_text.setText(message);
                        sendaudio.setVisibility(GONE);
                        sendChat.setVisibility(View.VISIBLE);
                    }


                }
            } catch (NullPointerException | ParseException | IOException e) {
                e.printStackTrace();
            }

            //handling forward messages//

            sendChat.setOnClickListener(this);

            sendaudio.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {


                    switch (event.getAction() & MotionEvent.ACTION_MASK) {

                        case MotionEvent.ACTION_DOWN:
                            recStopped = false;
                            dX = move_layout.getX() - event.getRawX();
                            dY = move_layout.getY() - event.getRawY();


                            homeX = move_layout.getX();
                            homeY = move_layout.getY();

                            lastAction = MotionEvent.ACTION_DOWN;
                            vibrate.vibrate(200);
                            slideToLeft(recording_lay);

                            // Start action ...
                            Log.d("onTouch: ", "startted");


                            try {
                                myAudioRecorder = new MediaRecorder();
                                myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                                myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                                myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                                myAudioRecorder.setAudioChannels(1);
                                myAudioRecorder.setAudioSamplingRate(44100);
                                myAudioRecorder.setAudioEncodingBitRate(96000);
                                File filepath = Environment.getExternalStorageDirectory();
                                File zoeFolder = new File(filepath.getAbsolutePath(), getResources().getString(R.string.app_name)).getAbsoluteFile();
                                if (!zoeFolder.exists()) {
                                    zoeFolder.mkdir();
                                }
                                File newFolder = new File(zoeFolder, getResources().getString(R.string.app_name) + " Record").getAbsoluteFile();
                                if (!newFolder.exists()) {
                                    newFolder.mkdir();
                                }
                                String foldername = newFolder.getAbsolutePath();
                                path_of_record = foldername + "/REC_" + "" + ChatActivity.randomImageNo++ + ".aac";
                                myAudioRecorder.setOutputFile(path_of_record);
                                myAudioRecorder.prepare();
                                myAudioRecorder.start();


                            } catch (IOException | IllegalStateException e) {
                                e.printStackTrace();
                            }

                            break;
                        case MotionEvent.ACTION_UP:

                        case MotionEvent.ACTION_OUTSIDE:

                        case MotionEvent.ACTION_CANCEL:

                            checkOnce = true;

                            if (!isRight) {
                                vibrate.vibrate(200);
                                slideToRight(recording_lay);
                            }
                            try {
                                Log.d("onTouch: ", "cancel");

                                myAudioRecorder.stop();
                                myAudioRecorder.reset();
                                myAudioRecorder.release();
                                myAudioRecorder = null;


                                setAudioDefaultpos();

                                if (!recStopped) {
                                    randomImageNo = Integer.parseInt(SharedHelper.getInt(ChatActivity.this, "image_count"));
                                    String date1 = Utils.getDate(System.currentTimeMillis(), "dd/MM/yyyy");
                                    if (Utils.formatToYesterdayOrToday(date1).equalsIgnoreCase("Today")) {
                                        randomImageNo++;
                                        SharedHelper.putInt(ChatActivity.this, "image_count", randomImageNo);
                                    } else {
                                        randomImageNo = 0;
                                    }
                                    String msgId = UUID.randomUUID().toString();
                                    beginUpload(path_of_record, msgId, 2);

                                    recStopped = false;
                                }
                            } catch (Exception e) {

                            }

                            break;

                        case MotionEvent.ACTION_POINTER_DOWN:
                            break;
                        case MotionEvent.ACTION_POINTER_UP:
                            break;
                        case MotionEvent.ACTION_MOVE:

//                            DisplayMetrics displaymetrics = new DisplayMetrics();
//                            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//                            int windowWidth = displaymetrics.widthPixels;
//                            int windowHeight = displaymetrics.heightPixels;
//                            float bFrx = move_layout.getTop();
//                            float finalVal = event.getRawX() + dX;
//
//
//                            Log.d(TAG, "onTouch: " + finalVal);
//
//                            if (finalVal > 600) {
//
//                                if ((finalVal <= 0 || finalVal + move_layout.getWidth() + 10 >= windowWidth)) {
//                                    checkOnce=false;
//                                    slideToRight(recording_lay);
//
//                                    lastAction = MotionEvent.ACTION_MOVE;
//                                    recStopped = true;
//                                    break;
//                                }
//
//
//                                move_layout.setY(bFrx);
//                                move_layout.setX(finalVal);
//                                int margin_val = (int) ((10 - (finalVal / 100)) * 11);
//                                Log.d(TAG, "onTouchmargin: " + margin_val);
//                                if (margin_val > 30) {
//                                    margin_val = margin_val + 50;
//                                }
//
//                                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) input_layout.getLayoutParams();
//                                params.setMargins(0, 0, margin_val + 15, 0);
//                                input_layout.setLayoutParams(params);
//
//                                lastAction = MotionEvent.ACTION_MOVE;
//
//                            } else {
//                                    if(checkOnce) {
//                                        checkOnce=false;
//                                        lastAction = MotionEvent.ACTION_MOVE;
//                                        recStopped = true;
//                                        slideToRight(recording_lay);
//                                        setAudioDefaultpos();
//                                        break;
//                                    }
//
//                            }

                            break;
                    }

                    return true;
                }
            });


            chat_text.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_DEL || keyCode == KeyEvent.KEYCODE_BACK) {
                        if (chat_text.getText().toString().length() == 0) {
                            sendaudio.setVisibility(View.VISIBLE);
                            sendChat.setVisibility(GONE);
                        } else {
                            sendaudio.setVisibility(View.GONE);
                            sendChat.setVisibility(View.VISIBLE);
                        }
                    }
                    return false;
                }
            });


            chat_text.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, final int start, int before, final int count) {
                    Log.e("Typing...", "start");


                    ChatActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.d(TAG, "run:countt:" + count);

                                if (chat_text.getText().toString().length() == 0) {

                                    sendaudio.setVisibility(View.VISIBLE);
                                    sendChat.setVisibility(GONE);
                                    if (localChatRoomType.equalsIgnoreCase("3")) {
                                        notifications.setVisibility(View.VISIBLE);
                                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) chat_text.getLayoutParams();
                                        params.addRule(RelativeLayout.LEFT_OF, R.id.add_notifications);

                                        chat_text.setLayoutParams(params);
                                    }
                                } else {
                                    sendaudio.setVisibility(GONE);
                                    sendChat.setVisibility(View.VISIBLE);
//                                    camera_photo.setVisibility(GONE);
                                    notifications.setVisibility(View.GONE);
                                    if (localChatRoomType.equalsIgnoreCase("3")) {
                                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) chat_text.getLayoutParams();
                                        params.addRule(RelativeLayout.LEFT_OF, R.id.camera_photo);

                                        chat_text.setLayoutParams(params);
                                    }

                                    typing = true;
                                }

                                if (localChatRoomType.equalsIgnoreCase("0")) {

                                    ServiceClasss.Emitters emitters = new ServiceClasss.Emitters(context);
                                    emitters.sendtyping(SharedHelper.getKey(ChatActivity.this, "id"), zoeChatID, "start");


                                } else {
                                    ServiceClasss.Emitters emitters = new ServiceClasss.Emitters(context);
                                    emitters.sendtyping(SharedHelper.getKey(ChatActivity.this, "id"), groupId_loc, "start");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                @Override
                public void afterTextChanged(final Editable s) {

                    Log.i("t_text", s.toString());
                    if (!isTyping) {
                        isTyping = true;
                        linkPreviewLayout.setVisibility(GONE);
                        Log.i("t_type", "Typing");
                    }
                    typetimer.cancel();
                    typetimer = new Timer();
                    typetimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            android.os.Handler handler = new

                                    android.os.Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i("t_type", "Type Finish");

                                    isTyping = false;
                                    List<String> extractedUrls = pullLinks(s.toString());

                                    for (String url : extractedUrls) {
                                        System.out.println(url);
                                        if (!dbHandler.CheckLinkAvalible(url)) {
                                            if (hasResponseCome) {
                                                System.out.println("After Text Changed" + s.toString());
                                                hasResponseCome = false;
                                                linkPreviewLayout.setVisibility(GONE);
                                                Log.d(TAG, "run:url: " + url);
                                                if (object != null) {
                                                    if (object != null) {
                                                        object.cancel(true);
                                                    }
                                                }

                                                object = new MetaData(url, new MetaData.AsyncResponse() {
                                                    @Override
                                                    public void processFinish(JSONObject output) {
//                                                    Log.d(TAG, "processFinish: "+output);
                                                        hasResponseCome = true;
                                                        try {

                                                            if (output.optBoolean("showPreview")) {
                                                                linkPreviewLayout.setVisibility(View.VISIBLE);
                                                                metaTitle.setTypeface(Typeface.DEFAULT_BOLD);
                                                                metaTitle.setText(output.optString("title"));
                                                                metaDescription.setText(output.optString("description"));
                                                                Glide.with(ChatActivity.this).load(output.optString("logo")).into(new SimpleTarget<GlideDrawable>() {
                                                                    @Override
                                                                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                                                        metaLogo.setImageDrawable(resource);
                                                                    }

                                                                    @Override
                                                                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                                                        super.onLoadFailed(e, errorDrawable);
                                                                        metaLogo.setVisibility(GONE);
                                                                    }
                                                                });

                                                            }
                                                            System.out.println(output);

                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                                object.execute();
                                            }

                                        } else {

                                            JSONObject output = new JSONObject();
                                            try {
                                                output = dbHandler.getLinks(url);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            title = output.optString("title");
                                            description = output.optString("description");
                                            logo = output.optString("logo");
                                            final String b_val = output.optString("url");
                                            linkPreviewLayout.setVisibility(View.VISIBLE);
                                            metaTitle.setTypeface(Typeface.DEFAULT_BOLD);
                                            showPreview = true;
                                            metaTitle.setText(output.optString("title"));
                                            if (description.equalsIgnoreCase("")) {
                                                metaDescription.setText(output.optString("url"));

                                            } else {
                                                metaDescription.setText(output.optString("description"));
                                            }
                                            Glide.with(ChatActivity.this).load(output.optString("logo")).into(new SimpleTarget<GlideDrawable>() {
                                                @Override
                                                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                                    metaLogo.setImageDrawable(resource);
                                                }

                                                @Override
                                                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                                    super.onLoadFailed(e, errorDrawable);
                                                    metaLogo.setVisibility(GONE);
                                                    Glide.with(ChatActivity.this).load(b_val + logo).into(new SimpleTarget<GlideDrawable>() {
                                                        @Override
                                                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                                            metaLogo.setVisibility(View.VISIBLE);
                                                            metaLogo.setImageDrawable(resource);
                                                        }

                                                        @Override
                                                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                                            super.onLoadFailed(e, errorDrawable);
                                                            metaLogo.setVisibility(GONE);

                                                        }
                                                    });

                                                }
                                            });
                                        }
                                    }

                                }
                            });
                        }
                    }, 800);

                }
            });

        }

        getWindow().getDecorView().getRootView().post(new Runnable() {
            @Override
            public void run() {
//                if (Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP) {

                revealAnimator = ViewAnimationUtils.createCircularReveal(circular_reveal, cx, cy, 0, endRadius);
                revealAnimator.setDuration(300);
//                }


            }
        });


    }

    private void setAudioDefaultpos() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) input_layout.getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        input_layout.setLayoutParams(params);
        recording_lay.clearAnimation();
        findViewById(R.id.recording_layout).setVisibility(GONE);
        move_layout.setX(homeX);
        move_layout.setY(homeY);


    }

    private void handlechannel() {
        final_filePath = dbHandler.GetWallpaper(groupId_loc);
        String role = dbHandler.GetChannelMember(groupId_loc, MainActivity.my_id);
        if (role.equalsIgnoreCase("user")) {
            member_layout.setVisibility(View.VISIBLE);
        } else {
            member_layout.setVisibility(View.GONE);

        }

        new getChanneldetails().execute();

        notifications.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) chat_text.getLayoutParams();
        params.addRule(RelativeLayout.LEFT_OF, R.id.add_notifications);
        chat_text.setLayoutParams(params);


        if (dbHandler.getNotifications(groupId_loc).equalsIgnoreCase("1")) {
            notifications.setBackgroundResource(R.drawable.ic_notifications_grey);
        } else {
            notifications.setBackgroundResource(R.drawable.ic_notifications_off_grey);

        }

        notifications.setOnClickListener(this);
        participan_model_c = dbHandler.GetPartiFromChannel(groupId_loc);

        if (group_image.equalsIgnoreCase("") || group_image.equalsIgnoreCase(" ")) {
            Picasso.with(context).load(R.drawable.ic_channel).error(context.getResources().getDrawable(R.drawable.ic_channel)).into(userIcon);

        } else {
            Picasso.with(ChatActivity.this).load(group_image).error(getResources().getDrawable(R.drawable.ic_channel)).into(userIcon);
        }
        sub_user_title.setVisibility(GONE);
        jsonArray = dbHandler.GetGroupChatsMessages(groupId_loc);
        dbHandler.close();

    }

    private void handlebroadcast() {
        final_filePath = dbHandler.GetWallpaper(groupId_loc);
        group_image = intent.getStringExtra("grp_image");
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) chat_text.getLayoutParams();

        params.addRule(RelativeLayout.LEFT_OF, R.id.add_attachment);

        chat_text.setLayoutParams(params);

        participan_model = dbHandler.GetPartiFromGrp(groupId_loc);

        if (group_image.equalsIgnoreCase("") || group_image.equalsIgnoreCase(" ")) {
            Picasso.with(context).load(R.drawable.ic_broad).error(context.getResources().getDrawable(R.drawable.ic_broad)).into(userIcon);

        } else {
            Picasso.with(ChatActivity.this).load(group_image).error(getResources().getDrawable(R.drawable.ic_broad)).into(userIcon);
        }
        Log.d("onCreate: ", "fetching_grp");
        sub_user_title.setVisibility(GONE);
        jsonArray = dbHandler.GetGroupChatsMessages(groupId_loc);
        dbHandler.close();

    }

    private void handleGroup() {
        final_filePath = dbHandler.GetWallpaper(groupId_loc);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) chat_text.getLayoutParams();
        params.addRule(RelativeLayout.LEFT_OF, R.id.add_attachment);
        chat_text.setLayoutParams(params);
        new getGrpdetails().execute();

        if (group_image.equalsIgnoreCase("") || group_image.equalsIgnoreCase(" ")) {
            Picasso.with(context).load(R.drawable.ic_profile_group).error(context.getResources().getDrawable(R.drawable.ic_profile_group)).into(userIcon);

        } else {
            Picasso.with(ChatActivity.this).load(group_image).error(getResources().getDrawable(R.drawable.ic_profile_group)).into(userIcon);
        }

        sub_user_title.setVisibility(GONE);

        jsonArray = dbHandler.GetGroupChatsMessages(groupId_loc);
        dbHandler.close();


    }

    private void handlePersonal() {
        final_filePath = dbHandler.GetWallpaper(zoeChatID);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) chat_text.getLayoutParams();
        params.addRule(RelativeLayout.LEFT_OF, R.id.add_attachment);
        chat_text.setLayoutParams(params);

        if (image.equalsIgnoreCase(" ") || image.equalsIgnoreCase("")) {
            Glide.with(ChatActivity.this).load(R.drawable.ic_account_circle).into(userIcon);

            proName.setVisibility(View.GONE);

        } else {
            proName.setVisibility(GONE);
            Picasso.with(context).load(image).placeholder(R.drawable.ic_account_circle).error(R.drawable.ic_account_circle).into(userIcon, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                    proName.setVisibility(View.GONE);
                    proName.setVisibility(View.GONE);

                }
            });
        }


        jsonArray = dbHandler.GetLimitChatsMessages(zoeChatID, "", "");
        dbHandler.close();

    }

    private void initbottomsheet() {

        bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_dialog, null);

        LinearLayout gallery = (LinearLayout) bottomSheetView.findViewById(R.id.wallpaper);
        LinearLayout d_wallpaper = (LinearLayout) bottomSheetView.findViewById(R.id.default_wallpaper);
        LinearLayout no_wallpaper = (LinearLayout) bottomSheetView.findViewById(R.id.no_wallpaper);
        RelativeLayout solid_colors = (RelativeLayout) bottomSheetView.findViewById(R.id.solid_color);

        no_wallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setBackgroundDrawableResource(R.drawable.no_wallpaper);
                if (localChatRoomType.equalsIgnoreCase("0")) {
                    dbHandler.UpdateWallpaper(zoeChatID, "no");
                } else {
                    dbHandler.UpdateWallpaper(groupId_loc, "no");
                }

                bottomSheetDialog.dismiss();
            }
        });

        d_wallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setBackgroundDrawableResource(R.drawable.wallpaper);
                if (localChatRoomType.equalsIgnoreCase("0")) {
                    dbHandler.UpdateWallpaper(zoeChatID, "default");
                } else {
                    dbHandler.UpdateWallpaper(groupId_loc, "default");
                }
                bottomSheetDialog.dismiss();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 33);
                bottomSheetDialog.dismiss();

            }
        });

        solid_colors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, ColorPicker.class);
                startActivityForResult(intent, 34);
                bottomSheetDialog.dismiss();

            }
        });

        bottomSheetDialog = new BottomSheetDialog(ChatActivity.this);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                Log.d(TAG, "onStateChanged:" + newState);
                if (newState == 5)

                {
                    bottomSheetDialog.dismiss();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

    }

    private void initvalues() {
        handler = new Handler();
        handler1 = new Handler();
        dbHandler = new DBHandler(ChatActivity.this);
        typetimer = new Timer();
        context = ChatActivity.this;
        internet = Utils.isNetworkAvailable(ChatActivity.this);
        vibrate = (Vibrator) ChatActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
        w = circular_reveal.getWidth();
        h = circular_reveal.getHeight();
        endRadius = (int) Math.hypot(w, h) + 30;
        cy = (int) (circular_reveal.getHeight());

    }

    private void initviews() {
        linkPreviewLayout = (RelativeLayout) findViewById(R.id.linkPreviewLayout);
        cancelPreview = (LinearLayout) findViewById(R.id.cancelPreview);
        parent_layout = (RelativeLayout) findViewById(R.id.parent_layout);
        input_layout = (RelativeLayout) findViewById(R.id.input_layout);
        move_layout = (FrameLayout) findViewById(R.id.move_layout);
        notifications = (ImageView) findViewById(R.id.add_notifications);
        member_layout = (RelativeLayout) findViewById(R.id.member_lay);
        metaTitle = (TextView) findViewById(R.id.metaTitle);
        metaDescription = (TextView) findViewById(R.id.metaDescription);
        metaLogo = (ImageView) findViewById(R.id.metaLogo);
        toolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        add_attachment = (ImageView) findViewById(R.id.add_attachment);
        circular_reveal = (CardView) findViewById(R.id.circular_reveal);
        chat_text = (EmojiEditText) findViewById(R.id.edittext);
        back_layout = (LinearLayout) findViewById(R.id.back_layout);
        proName = (TextView) findViewById(R.id.text_name_per);
        recording_lay = (RelativeLayout) findViewById(R.id.recording_layout);
        mic = (ImageView) findViewById(R.id.mic_icon);
        timer = (Chronometer) findViewById(R.id.timer);
        hide_key = (ImageView) findViewById(R.id.hide_btn);
        userIcon = (ImageView) findViewById(R.id.user_icon);
        user_title = (TextView) findViewById(R.id.txt_title);
        sub_user_title = (TextView) findViewById(R.id.txt_sub_title);
        mRevealView = (RevealFrameLayout) findViewById(R.id.attachment_layout);
        chatListView = (RecyclerView) findViewById(R.id.chat_list_view);
        header = (RelativeLayout) findViewById(R.id.header_layout);
        sendChat = (ImageButton) findViewById(R.id.send_chat);
        sendaudio = (ImageButton) findViewById(R.id.send_audio);
        rootView = (ViewGroup) findViewById(R.id.chat_layout);
        setUpEmojiPopup();
        camera_photo = (ImageView) findViewById(R.id.camera_photo);
        emojiButton = (ImageView) findViewById(R.id.emojiButton);
        emojiButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_mood));
        customView(sendChat, getPrimaryCOlor(ChatActivity.this), getPrimaryCOlor(ChatActivity.this));
        customView(sendaudio, getPrimaryCOlor(ChatActivity.this), getPrimaryCOlor(ChatActivity.this));
        toolbar.setBackgroundColor(getPrimaryCOlor(ChatActivity.this));
        linkPreviewLayout.setVisibility(GONE);
        defaultParams = (RelativeLayout.LayoutParams) input_layout.getLayoutParams();


        System.setProperty(SDKGlobalConfiguration.ENFORCE_S3_SIGV4_SYSTEM_PROPERTY, "true");
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(ChatActivity.this, Const.IDENTIY_POOL_ID, Const.cognitoRegion);
        s3Client = new AmazonS3Client(credentialsProvider);
        s3Client.setEndpoint(Const.ENDPOINT);


    }

    private List<Integer> getAllMaterialColorss() throws IOException, XmlPullParserException {
        XmlResourceParser xrp = ChatActivity.this.getResources().getXml(R.xml.back_theme_color);
        List<Integer> allColors = new ArrayList<>();
        int nextEvent;
        while ((nextEvent = xrp.next()) != XmlResourceParser.END_DOCUMENT) {
            String s = xrp.getName();
            if ("color".equals(s)) {
                String color = xrp.nextText();
                Log.d("getAllMaterialColors: ", "color_values");
                allColors.add(Color.parseColor(color));
            }
        }
        return allColors;
    }

    private void Setheme(String themevalue) {
        switch (themevalue) {
            case "1":
                setTheme(R.style.AppThemeGreen);
                break;
            case "2":
                setTheme(R.style.AppThemeBlue);
                break;
            case "3":
                setTheme(R.style.AppThemeIndigo);
                break;
            case "4":
                setTheme(R.style.AppThemeGrey);
                break;
            case "5":
                setTheme(R.style.AppThemeYellow);
                break;
            case "6":
                setTheme(R.style.AppThemeOrange);
                break;
            case "7":
                setTheme(R.style.AppThemePurple);
                break;
            case "8":
                setTheme(R.style.AppThemePaleGreen);
                break;
            case "9":
                setTheme(R.style.AppThemelightBlue);
                break;
            case "10":
                setTheme(R.style.AppThemePink);
                break;
            case "11":
                setTheme(R.style.AppThemelightGreen);
                break;
            case "12":
                setTheme(R.style.AppThemelightRed);
                break;
            default:
                setTheme(R.style.AppThemeGreen);
                break;
        }
    }

    private List<Integer> getAllMaterialColors() throws IOException, XmlPullParserException {
        XmlResourceParser xrp = ChatActivity.this.getResources().getXml(R.xml.select_color);
        List<Integer> allColors = new ArrayList<>();
        int nextEvent;
        while ((nextEvent = xrp.next()) != XmlResourceParser.END_DOCUMENT) {
            String s = xrp.getName();
            if ("color".equals(s)) {
                String color = xrp.nextText();
                allColors.add(Color.parseColor(color));
            }
        }
        return allColors;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void revealShow(boolean b) {

        if (b) {

            mRevealView.setVisibility(View.VISIBLE);
            circular_reveal.setVisibility(View.VISIBLE);
            check = false;

//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            revealAnimator.start();
            w = circular_reveal.getWidth();
            h = circular_reveal.getHeight();

            endRadius = (int) Math.hypot(w, h) + 30;
            cx = (int) (mRevealView.getWidth() / 1.7);
            cy = (int) (circular_reveal.getHeight());

            revealAnimator = ViewAnimationUtils.createCircularReveal(circular_reveal, cx, cy, 0, endRadius);
            revealAnimator.setDuration(300);


            anim = ViewAnimationUtils.createCircularReveal(circular_reveal, cx, cy, endRadius, 0);
            anim.setDuration(300);
//            }

        } else {


//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    circular_reveal.setVisibility(View.INVISIBLE);
                    mRevealView.setVisibility(View.INVISIBLE);
                    w = circular_reveal.getWidth();
                    h = circular_reveal.getHeight();
                    endRadius = (int) Math.hypot(w, h) + 30;
                    cx = (int) (mRevealView.getWidth() / 1.7);
                    cy = (int) (circular_reveal.getHeight());
                    revealAnimator = ViewAnimationUtils.createCircularReveal(circular_reveal, cx, cy, 0, endRadius);
                    revealAnimator.setDuration(300);
                    anim = ViewAnimationUtils.createCircularReveal(circular_reveal, cx, cy, endRadius, 0);
                    anim.setDuration(300);

                }
            });
            anim.start();
//        }
//        else {
//                circular_reveal.setVisibility(View.INVISIBLE);
//                mRevealView.setVisibility(View.INVISIBLE);
//            }
            check = true;


        }

    }

    private void uploaddocument(String media, String send_message_ac) throws ParseException, IOException {

        ContentResolver cR = ChatActivity.this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        Uri typef = Uri.parse(media);
        String uri = typef.toString();
        Log.d("onActivityResult: ", "file:tpe" + typef);
        int i = uri.lastIndexOf('.');
        String fileExtension = uri.substring(i + 1);

        String type = mime.getExtensionFromMimeType(cR.getType(typef));

        String date1 = Utils.getDate(System.currentTimeMillis(), "dd/MM/yyyy");
        if (Utils.formatToYesterdayOrToday(date1).equalsIgnoreCase("Today")) {
            randomImageNo++;
            SharedHelper.putInt(ChatActivity.this, "image_count", randomImageNo);
        } else {
            randomImageNo = 0;
        }

        String date = Utils.getDate(System.currentTimeMillis(), "ddMMyyyy");
        File filepath = Environment.getExternalStorageDirectory();
        final File zoeFolder = new File(filepath.getAbsolutePath(), getResources().getString(R.string.app_name)).getAbsoluteFile();
        if (!zoeFolder.exists()) {
            zoeFolder.mkdir();
        }
        File newFolder = new File(zoeFolder, getResources().getString(R.string.app_name) + " Documents").getAbsoluteFile();
        if (!newFolder.exists()) {
            newFolder.mkdir();
        }

        String newFileName = String.valueOf("DOC" + date + "_ZC00" + randomImageNo + "." + fileExtension);
        final File audiofile = new File(newFolder, newFileName);


        String videoPath = audiofile.getAbsolutePath();
        Log.d("uploaddocument: ", "docpath:" + videoPath);

        if (send_message_ac.equalsIgnoreCase("no")) {
            if (localChatRoomType.equalsIgnoreCase("0")) {
                sendMsg(zoeChatID, media, UUID.randomUUID().toString(), String.valueOf(System.currentTimeMillis()), ChatType.document, ChatMessages.SENDER_DOC, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));
            } else {
                sendMsg(groupId_loc, media, UUID.randomUUID().toString(), String.valueOf(System.currentTimeMillis()), ChatType.document, ChatMessages.SENDER_DOC, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));
            }
        } else {
            if (localChatRoomType.equalsIgnoreCase("0")) {
                Log.d("onActivityResult: ", "documentMess:" + getImageContentUri(ChatActivity.this, videoPath));
                sendMsg(zoeChatID, String.valueOf(getImageContentUri(ChatActivity.this, videoPath)), UUID.randomUUID().toString(), String.valueOf(System.currentTimeMillis()), ChatType.document, ChatMessages.SENDER_DOC, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));
            } else if (localChatRoomType.equalsIgnoreCase("1")) {
                Log.d("onActivityResult: ", "documentMess:" + getImageContentUri(ChatActivity.this, videoPath));
                sendMsg(groupId_loc, String.valueOf(getImageContentUri(ChatActivity.this, videoPath)), UUID.randomUUID().toString(), String.valueOf(System.currentTimeMillis()), ChatType.document, ChatMessages.SENDER_DOC, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));
            }

        }

    }

    private void uploadlocation(String message) {
        if (localChatRoomType.equalsIgnoreCase("0")) {
            sendMsg(zoeChatID, message, UUID.randomUUID().toString(),
                    String.valueOf(System.currentTimeMillis()), ChatType.location, ChatMessages.SENDER_LOCATION, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));
        } else {
            sendMsg(groupId_loc, message, UUID.randomUUID().toString(),
                    String.valueOf(System.currentTimeMillis()), ChatType.location, ChatMessages.SENDER_LOCATION, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));
        }
        scrollToBottom();
    }

    private void uploadsticker(String message) {
        if (localChatRoomType.equalsIgnoreCase("0")) {
            sendMsg(zoeChatID, message, UUID.randomUUID().toString(),
                    String.valueOf(System.currentTimeMillis()), ChatType.sticker, ChatMessages.SENDER_STICKER, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));
        } else {
            sendMsg(groupId_loc, message, UUID.randomUUID().toString(),
                    String.valueOf(System.currentTimeMillis()), ChatType.sticker, ChatMessages.SENDER_STICKER, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));
        }
        scrollToBottom();
    }

    private void uploadcontact(String message, String c_name, String c_id) {
        if (localChatRoomType.equalsIgnoreCase("0")) {
            sendMsg(zoeChatID, message, UUID.randomUUID().toString(),
                    String.valueOf(System.currentTimeMillis()), ChatType.contact, ChatMessages.SENDER_CONTACT, groupName, groupId_loc, create_by, sent_by, localChatRoomType, c_name, c_id, dbHandler.GetSign(groupId_loc));
        } else {
            sendMsg(groupId_loc, message, UUID.randomUUID().toString(),
                    String.valueOf(System.currentTimeMillis()), ChatType.contact, ChatMessages.SENDER_CONTACT, groupName, groupId_loc, create_by, sent_by, localChatRoomType, c_name, c_id, dbHandler.GetSign(groupId_loc));
        }
        scrollToBottom();
    }

    private void uploadvideo(String message) throws ParseException, IOException {

        String date1 = Utils.getDate(System.currentTimeMillis(), "dd/MM/yyyy");
        if (Utils.formatToYesterdayOrToday(date1).equalsIgnoreCase("Today")) {
            randomImageNo++;
            SharedHelper.putInt(ChatActivity.this, "image_count", randomImageNo);
        } else {
            randomImageNo = 0;
        }

        String date = Utils.getDate(System.currentTimeMillis(), "ddMMyyyy");
        File filepath = Environment.getExternalStorageDirectory();
        final File zoeFolder = new File(filepath.getAbsolutePath(), getResources().getString(R.string.app_name)).getAbsoluteFile();
        if (!zoeFolder.exists()) {
            zoeFolder.mkdir();
        }
        File newFolder = new File(zoeFolder, getResources().getString(R.string.app_name) + " Video").getAbsoluteFile();
        if (!newFolder.exists()) {
            newFolder.mkdir();
        }

        String newFileName = String.valueOf("VID" + date + "_ZC00" + randomImageNo);
        final File videoFile = new File(newFolder, newFileName + ".mp4");

        File sourceFile = new File(message);
        FileUtils.copyFile(sourceFile, videoFile);
        String videoPath = videoFile.getAbsolutePath();

        if (localChatRoomType.equalsIgnoreCase("0")) {
            sendMsg(zoeChatID, videoPath, UUID.randomUUID().toString(), String.valueOf(System.currentTimeMillis()),
                    ChatType.video, ChatMessages.SENDER_VIDEO, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));
        } else {
            sendMsg(groupId_loc, videoPath, UUID.randomUUID().toString(), String.valueOf(System.currentTimeMillis()),
                    ChatType.video, ChatMessages.SENDER_VIDEO, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));
        }


    }

    public void slideToRight(View view) {
        isRight = true;
        TranslateAnimation animate = new TranslateAnimation(0, view.getWidth(), 0, 0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);

        view.setVisibility(GONE);
        timer.setBase(SystemClock.elapsedRealtime());
        timer.stop();
        blink(mic, "2");
//        recording_lay.setLayoutParams(defaultParamsRecord);

    }

    public void slideToLeft(View view) {
        isRight = false;
        TranslateAnimation animate = new TranslateAnimation(view.getWidth(), 0, 0, 0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.VISIBLE);
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
        blink(mic, "1");


    }

    public void blink(View view, String value) {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        if (value.equalsIgnoreCase("1")) {
            view.startAnimation(anim);
        } else {
            view.clearAnimation();
        }

    }

    private void uploadaudio(String message) throws ParseException, IOException {

        String date1 = Utils.getDate(System.currentTimeMillis(), "dd/MM/yyyy");
        if (Utils.formatToYesterdayOrToday(date1).equalsIgnoreCase("Today")) {
            randomImageNo++;
            SharedHelper.putInt(ChatActivity.this, "image_count", randomImageNo);
        } else {
            randomImageNo = 0;
        }

        String date = Utils.getDate(System.currentTimeMillis(), "ddMMyyyy");
        File filepath = Environment.getExternalStorageDirectory();
        final File zoeFolder = new File(filepath.getAbsolutePath(), getResources().getString(R.string.app_name)).getAbsoluteFile();
        if (!zoeFolder.exists()) {
            zoeFolder.mkdir();
        }
        File newFolder = new File(zoeFolder, getResources().getString(R.string.app_name) + " Audio").getAbsoluteFile();
        if (!newFolder.exists()) {
            newFolder.mkdir();
        }

        String newFileName = String.valueOf("AUD" + date + "_ZC00" + randomImageNo);
        final File audiofile = new File(newFolder, newFileName + ".mp3");

        File sourceFile = new File(message);
        FileUtils.copyFile(sourceFile, audiofile);
        String videoPath = audiofile.getAbsolutePath();

        if (localChatRoomType.equalsIgnoreCase("0")) {
            sendMsg(zoeChatID, videoPath, UUID.randomUUID().toString(), String.valueOf(System.currentTimeMillis()), ChatType.audio, ChatMessages.SENDER_AUDIO, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));
        } else {
            sendMsg(groupId_loc, videoPath, UUID.randomUUID().toString(), String.valueOf(System.currentTimeMillis()), ChatType.audio, ChatMessages.SENDER_AUDIO, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));
        }

    }

    private int scrollToBottm() {
        return (singleChatAdapter.getItemCount() - 1);
    }

    private void uploadimage(String message) throws ParseException {


        String date1 = Utils.getDate(System.currentTimeMillis(), "dd/MM/yyyy");
        if (Utils.formatToYesterdayOrToday(date1).equalsIgnoreCase("Today")) {
            randomImageNo++;
            SharedHelper.putInt(ChatActivity.this, "image_count", randomImageNo);
        } else {
            randomImageNo = 0;
        }

        File sourceFile = new File(message);

        String date = Utils.getDate(System.currentTimeMillis(), "ddMMyyyy");
        File filepath = Environment.getExternalStorageDirectory();
        final File zoeFolder = new File(filepath.getAbsolutePath(), getResources().getString(R.string.app_name)).getAbsoluteFile();
        if (!zoeFolder.exists()) {
            zoeFolder.mkdir();
        }
        File newFolder = new File(zoeFolder, getResources().getString(R.string.app_name) + " Image").getAbsoluteFile();
        if (!newFolder.exists()) {
            newFolder.mkdir();
        }

        String newFileName = String.valueOf("IMG_" + date + "_ZC00F" + randomImageNo);
        Log.e("newFileName", "" + newFileName);
        final File file = new File(newFolder, newFileName + ".jpg");


        try {
            FileUtils.copyFile(sourceFile, file);
            ChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String final_filePath = file.getAbsolutePath();
                    Log.e("final_filePath", "" + final_filePath);
                    if (localChatRoomType.equalsIgnoreCase("0")) {
                        sendMsg(zoeChatID, final_filePath, UUID.randomUUID().toString(), String.valueOf(System.currentTimeMillis()), ChatType.image, ChatMessages.SENDER_IMAGE, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));
                    } else {
                        sendMsg(groupId_loc, final_filePath, UUID.randomUUID().toString(), String.valueOf(System.currentTimeMillis()), ChatType.image, ChatMessages.SENDER_IMAGE, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));
                    }
                    scrollToBottom();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateUnReadCount() {
        DBHandler dbHandler = new DBHandler(ChatActivity.this);
        if (localChatRoomType.equalsIgnoreCase("0")) {
            dbHandler.removeUnreadCount(zoeChatID);
        } else {
            dbHandler.removeUnreadCount(groupId_loc);

        }
        MainActivity.unreadCount = 0;
        ServiceClasss.unreadCount = 0;

        //dbHandler.UpdateLastMsg_Status(zoeChatID, Status.READ.toString());
        for (int i = 0; i < jsonArray.length(); i++) {
            String status = jsonArray.optJSONObject(i).optString("contentStatus");
            if (status.equalsIgnoreCase("DELIVERED")) {
                String msgId = jsonArray.optJSONObject(i).optString("msgId");
                String userId;
                if (localChatRoomType.equalsIgnoreCase("0")) {
                    userId = jsonArray.optJSONObject(i).optString("userId");
                } else {
                    userId = groupId_loc;


                }

                //String msg = jsonArray.optJSONObject(i).optString("msg");
                //long deliveredTime = System.currentTimeMillis();

                sendSeen(msgId, userId, SharedHelper.getKey(ChatActivity.this, "id"));
            }

        }
        dbHandler.close();
    }

    protected void makeRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        clearActionM();
        SingleChatAdapter.is_in_action_mode = false;
        ChatActivity.clearActionM();
        for (int i = 0; i < SingleChatAdapter.mMessages.length(); i++) {
            try {
                JSONObject object = SingleChatAdapter.mMessages.optJSONObject(i);
                object.remove("isSelected");
                Log.d("onClick: ", "changing:");
                Log.d("onClick: ", "value:" + object.optString("isSelected"));
                object.put("isSelected", "false");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        back_layout.setVisibility(View.VISIBLE);
        FCMMsgService.current_id = null;
        SharedHelper.putKey(ChatActivity.this, "single_chat_enable", "no");
        if (emojiPopup != null && emojiPopup.isShowing()) {
            emojiPopup.dismiss();
        }
        SharedHelper.putKey(ChatActivity.this, "single_chat_enable", "no");
        FCMMsgService.current_id = null;
        ChatFragment.refreshLay();
        finish();


    }

    private void setUpEmojiPopup() {
        emojiPopup = EmojiPopup.Builder.fromRootView(rootView).setOnEmojiBackspaceClickListener(new OnEmojiBackspaceClickListener() {
            @Override
            public void onEmojiBackspaceClicked(final View v) {
                Log.d("MainActivity", "Clicked on Backspace");
            }
        }).setOnEmojiClickedListener(new OnEmojiClickedListener() {
            @Override
            public void onEmojiClicked(final Emoji emoji) {
                Log.d("MainActivity", "Clicked on emoji");
            }
        }).setOnEmojiPopupShownListener(new OnEmojiPopupShownListener() {
            @Override
            public void onEmojiPopupShown() {
                emojiButton.setImageResource(R.drawable.ic_keyboard_hide);
            }
        }).setOnSoftKeyboardOpenListener(new OnSoftKeyboardOpenListener() {
            @Override
            public void onKeyboardOpen(final int keyBoardHeight) {
                Log.d("MainActivity", "Opened soft keyboard");
            }
        }).setOnEmojiPopupDismissListener(new OnEmojiPopupDismissListener() {
            @Override
            public void onEmojiPopupDismiss() {
                emojiButton.setImageResource(R.drawable.emoji_people);
            }
        }).setOnSoftKeyboardCloseListener(new OnSoftKeyboardCloseListener() {
            @Override
            public void onKeyboardClose() {
                emojiPopup.dismiss();
            }
        }).build(chat_text);
    }

    private void sendSeen(String messageId, String userId, String to) {
        ServiceClasss.Emitters emitters = new ServiceClasss.Emitters(context);
        emitters.sendseen(messageId, userId, to, localChatRoomType);

    }

    private void sendMsg(String userID, String msg, String uniqueID, String currentTime, ChatType chatType,
                         String sender, String groupName, String groupId, String create_by, String sent_by, String chatRoomType, String cName, String cNumber, String shouldSign) {

        //long time = dbHandler.GetLastRow(userID);
        long time = Long.parseLong(SharedHelper.getHeader(ChatActivity.this, "header_time"));
        Log.d("sendMsg: ", "imagefile:" + image);
        Log.e("lastRowTime", String.valueOf(+time));
        try {
            String chatTime = Utils.formatToYesterdayOrToday(Utils.getDate(time, "dd/MM/yyyy hh:mm:ss.SSS"));
            Log.e("chatTime", chatTime);

            if (time == 0) {
                SharedHelper.putHeader(ChatActivity.this, "header_time", currentTime);
                addHeader(ChatMessages.HEADER, currentTime, String.valueOf(System.currentTimeMillis()), chatRoomType, groupId);
                addDB(userID, msg, uniqueID, currentTime, sender, chatType, groupName, create_by, sent_by, groupId, chatRoomType, cName, cNumber, shouldSign);

            } else if (chatTime.equalsIgnoreCase("Today")) {
                if (value == 0) {
                    value++;
                    SharedHelper.putHeader(ChatActivity.this, "header_time", currentTime);
                    SharedHelper.putInt(ChatActivity.this, "date", value);
                }
                value = Integer.parseInt(SharedHelper.getInt(ChatActivity.this, "date"));
                SharedHelper.putHeader(ChatActivity.this, "header_time", currentTime);
                addDB(userID, msg, uniqueID, currentTime, sender, chatType, groupName, create_by, sent_by, groupId, chatRoomType, cName, cNumber, shouldSign);

            } else {
                SharedHelper.putHeader(ChatActivity.this, "header_time", currentTime);
                addHeader(ChatMessages.HEADER, currentTime, String.valueOf(System.currentTimeMillis()), chatRoomType, groupId);
                addDB(userID, msg, uniqueID, currentTime, sender, chatType, groupName, create_by, sent_by, groupId, chatRoomType, cName, cNumber, shouldSign);
                value = 0;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (chatType.equals(ChatType.text)) {
            sendChat(userID, msg, uniqueID, currentTime, chatType, chatRoomType, groupId, cName, cNumber, shouldSign);
        } else if (chatType.equals(ChatType.location)) {
            sendChat(userID, msg, uniqueID, currentTime, chatType, chatRoomType, groupId, cName, cNumber, shouldSign);
        } else if (chatType.equals(ChatType.contact)) {
            sendChat(userID, msg, uniqueID, currentTime, chatType, chatRoomType, groupId, cName, cNumber, shouldSign);
        } else if (chatType.equals(ChatType.sticker)) {
            sendChat(userID, msg, uniqueID, currentTime, chatType, chatRoomType, groupId, cName, cNumber, shouldSign);

        }

    }

    // send the message
    public void sendChat(String userID, String msg, String uniqueID, String currentTime, ChatType chatType, String chatRoomType, String groupId, String cName, String cNumber, String shouldSign) {


        ServiceClasss.Emitters emitters = new ServiceClasss.Emitters(context);
        emitters.sendChat(userID, msg, uniqueID, currentTime, chatType, chatRoomType, groupId, cName, cNumber, shouldSign, title, description, logo, showPreview, object, lat, lng);


    }

    public void sendChatFailed(String userID, String msg, String uniqueID, String currentTime, ChatType chatType, String chatRoomType, String groupId, String cName, String cNumber, String shouldSign, String mtitle, String mdesc, String mlogo, String shoprev, String lati, String lngi) {
        Boolean prr;
        if (shoprev.equalsIgnoreCase("true")) {
            prr = true;
        } else {
            prr = false;
        }
        ServiceClasss.Emitters emitters = new ServiceClasss.Emitters(context);
        AsyncTask<Void, Void, Void> asynckobj = null;
        emitters.sendChat(userID, msg, uniqueID, currentTime, chatType, chatRoomType, groupId, cName, cNumber, shouldSign, mtitle, mdesc, mlogo, prr, asynckobj, Double.parseDouble(lati), Double.parseDouble(lngi));


    }

    public void addHeader(String header, String currentTime, String time, String chatRoomType, String groupId) {
        try {
            DBHandler dbHandler = new DBHandler(ChatActivity.this);
            dbHandler.InsertChatMsg(new ChatsMessagesModel(time, zoeChatID, "0", "0", "0", header, groupId, currentTime, "0", "0", 0, 0, 0, String.valueOf(lat), String.valueOf(lng), "", "", chatRoomType, "false", "", "", 0, "", "", "", ""));
            dbHandler.close();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userType", header);
            jsonObject.put("date", "Today");
            jsonArray.put(jsonObject);

            if (singleChatAdapter != null)
                singleChatAdapter.notifyDataSetChanged();

            scrollToBottom();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void addDB(String userID, String msg, String uniqueID, String currentTime, String sender,
                       ChatType chatType, String groupName, String create_by, String sent_by, String groupId, String chatRoomType, String cName, String cNumber, String shouldSign) {
        DBHandler dbHandler = new DBHandler(ChatActivity.this);
        if (chatRoomType.equalsIgnoreCase("0")) {
            if (!dbHandler.DoesChatsUser(zoeChatID)) {
                dbHandler.InsertChats(new ChatsModel(zoeChatID, chatRoomType, sender, msg, Status.SENDING.toString(), currentTime, chatType.toString(), 0, groupName, create_by, sent_by, "", "0", "0", "", "", "", "", "", "", shouldSign, "1", ""));
            } else {
                String isDeleted = dbHandler.getDeleteStatus(zoeChatID);
                if (isDeleted.equalsIgnoreCase("1")) {
                    dbHandler.GroupDeleteUpdate("0", zoeChatID);
                }
                dbHandler.UpdateLastMsg(zoeChatID, sender, msg, Status.SENDING.toString(), currentTime, chatType.toString(), 0);
            }
        } else {
            if (!dbHandler.DoesChatsUser(groupId)) {
                dbHandler.InsertChats(new ChatsModel(groupId, chatRoomType, sender, msg, Status.SENDING.toString(), currentTime, chatType.toString(), 0, groupName, create_by, sent_by, "", "0", "0", "", "", "", "", "", "", shouldSign, "1", ""));
            } else {
                String isDeleted = dbHandler.getDeleteStatus(groupId);
                if (isDeleted.equalsIgnoreCase("1")) {
                    dbHandler.GroupDeleteUpdate("0", groupId);
                }
                dbHandler.UpdateLastMsg(groupId, sender, msg, Status.SENDING.toString(), currentTime, chatType.toString(), 0);
            }
        }

        if (!chatRoomType.equalsIgnoreCase("2")) {
            dbHandler.InsertChatMsg(new ChatsMessagesModel(uniqueID.trim(), zoeChatID, msg, chatType.toString(), Status.SENDING.toString(), sender, groupId, currentTime, "0", "0", 0, 0, 0, String.valueOf(lat), String.valueOf(lng), cName, cNumber, chatRoomType, "false", msg, "", (showPreview) ? 1 : 0, title, description, logo, shouldSign));
        } else {
            dbHandler.InsertChatMsg(new ChatsMessagesModel(uniqueID.trim(), zoeChatID, msg, chatType.toString(), Status.SENDING.toString(), sender, groupId, currentTime, "0", "0", 0, 0, 0, String.valueOf(lat), String.valueOf(lng), cName, cNumber, chatRoomType, "false", msg, "", (showPreview) ? 1 : 0, title, description, logo, shouldSign));
            new addDBMul(msg, chatType.toString(), Status.SENT.toString(), sender, "", currentTime, "0", "0", 0, 0, 0, String.valueOf(lat), String.valueOf(lng), cName, cNumber, "0", "false", msg).execute();
        }

        sendMessage(userID, msg, sender, chatType, currentTime, uniqueID, chatRoomType, groupId, cName, cNumber, shouldSign);
        dbHandler.close();
    }

//    public void clearActionM() {
//
//
//        chatsAdapter = new ChatsAdapter(getActivity(), chatsList);
//        recyclerView.setAdapter(chatsAdapter);
//        ChatsAdapter. selectRemoveItem.clear();selectRemoveItemPosition.clear();
//        counter = 0;
//        //selectionList.clear();
//    }

    private void sendMessage(String userID, final String msg, String type, final ChatType chatType,
                             String currentTime, String uniqueID, String chatRoomType, String groupId, String cName, String cNumber, String shouldSign) {
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("msg", msg);
            jsonObject.put("userType", type);
            jsonObject.put("chatType", chatType);
            jsonObject.put("sentTime", currentTime);
            jsonObject.put("chatRoomType", chatRoomType);
            jsonObject.put("shouldSign", shouldSign);
            jsonObject.put("upload", "uploading");

            if (chatType.equals(ChatType.location)) {
                jsonObject.put("lat", lat);
                jsonObject.put("lng", lng);
            } else if (chatType.equals(ChatType.contact)) {
                jsonObject.put("cName", cName);
                jsonObject.put("cNumber", cNumber);
            } else if (chatType.equals(ChatType.audio)) {
                jsonObject.put("isPlaying", "0");
                jsonObject.put("playingStatus", "0");

            }
            jsonObject.put("groupId", groupId);
            jsonObject.put("msgId", uniqueID);
            jsonObject.put("userId", userID);
            if (showPreview) {
                jsonObject.put("showPreview", true);
                jsonObject.put("metaTitle", title);
                jsonObject.put("metaDescription", description);
                jsonObject.put("metaLogo", logo);

            } else {
                jsonObject.put("showPreview", false);
            }

            jsonArray.put(jsonObject);

            ChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    singleChatAdapter.notifyDataSetChanged();

                }
            });
            scrollToBottom();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (localChatRoomType.equalsIgnoreCase("0")) {
            if (zoeChatID.equalsIgnoreCase("34654745647")) {
                getMenuInflater().inflate(R.menu.single_support, menu);
            } else {
                getMenuInflater().inflate(R.menu.single_chat_menu, menu);
            }
        } else {
            getMenuInflater().inflate(R.menu.group_chat_menu, menu);
        }
        MenuItem menuItem = menu.getItem(0);

        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItems = menu.getItem(i);
            Drawable drawable = menuItems.getIcon();
            if (drawable != null) {
                // If we don't mutate the drawable, then all drawable's with this id will have a color
                // filter applied to it.
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

            }

        }

        return super.onCreateOptionsMenu(menu);
    }

    public static void scrollToBottom() {
        try {
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    chatListView.scrollToPosition(singleChatAdapter.getItemCount() - 1);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void deletechat() {
        new AsyncTask<String, String, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Utils.showSimpleProgressDialog(ChatActivity.this, "Please wait...", false);
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected String doInBackground(String... strings) {
                DBHandler dbHandler = new DBHandler(ChatActivity.this);
                if (localChatRoomType.equalsIgnoreCase("0")) {

                    dbHandler.DeleteChatsComplete(zoeChatID);
                    dbHandler.UpdateLastMsg(zoeChatID, zoeChatID, "", "", String.valueOf(System.currentTimeMillis()), ChatMessages.SENDER, 0);
//                    dbHandler.DeleteChats(zoeChatID);

                    JSONArray array = new JSONArray();
                    try {
//                        jsonArray=array;
                        if (jsonArray.length() > 0) {
                            deletejsonarraycomplete(jsonArray.length() - 1);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dbHandler.close();
                    Log.e("jsonArray_chat_msg", "Single_chat " + jsonArray.toString());
                } else {
//                    dbHandler.DeleteChats(groupId_loc);
                    dbHandler.DeleteChatsGrpComplete(groupId_loc);
                    dbHandler.UpdateLastMsg(groupId_loc, zoeChatID, "", "", String.valueOf(System.currentTimeMillis()), ChatMessages.SENDER, 0);


                    JSONArray array = new JSONArray();
                    try {
//                        jsonArray=array;
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            jsonArray.remove(0);
//
//                        }
                        deletejsonarraycomplete(jsonArray.length() - 1);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                ChatActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        singleChatAdapter = new SingleChatAdapter(ChatActivity.this, jsonArray, zoeChatID, localChatRoomType, groupId_loc);
//                        linearLayoutManager = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false);
//                        linearLayoutManager.setAutoMeasureEnabled(true);
//                        Log.d("onOptionsItemSelected: ", "first_values:" + value);
//                        chatListView.setLayoutManager(linearLayoutManager);
                        singleChatAdapter.notifyDataSetChanged();

                    }
                });

                dbHandler.close();
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Utils.removeProgressDialog();
                ChatFragment.mainActivity.clearActionM();
                refresh();
            }
        }.execute();

    }

    private void deletejsonarraycomplete(int v) {

        try {
            jsonArray.remove(v);
            int a = jsonArray.length() - 1;
            if (a != 0) {
                deletejsonarraycomplete(a);
            } else {
                jsonArray.remove(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addToPos(int pos, JSONObject jsonObj, JSONArray jsonArr) throws JSONException {
        for (int i = jsonArr.length(); i > pos; i--) {
            jsonArr.put(i, jsonArr.get(i - 1));
        }
        jsonArr.put(pos, jsonObj);
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_attachment:
                if (check) {
                    Animation logoMoveAnimation = AnimationUtils.loadAnimation(ChatActivity.this, R.anim.media_in_animation);
                    mRevealView.startAnimation(logoMoveAnimation);
                    mRevealView.setVisibility(View.VISIBLE);
                    check = false;
                } else {
                    Animation logoMoveAnimation = AnimationUtils.loadAnimation(ChatActivity.this, R.anim.media_out_animation);
                    mRevealView.startAnimation(logoMoveAnimation);
                    mRevealView.setVisibility(View.INVISIBLE);
                    check = true;
                }

                break;


            case R.id.wallpaper:

                mRevealView.setVisibility(GONE);
                check = true;
                bottomSheetBehavior.setPeekHeight(1000);
                bottomSheetDialog.show();
                break;

            case R.id.action_forward:
                DBHandler dbHandler = new DBHandler(ChatActivity.this);
                Intent forward = new Intent(ChatActivity.this, ForwardMessageActivity.class);
                ChatFragment.isGroup = "true";
                forward.putExtra("forward_msg", "true");
                forward.putExtra("message", dbHandler.GetMessage(SingleChatAdapter.selectRemoveItem.get(0)));
                forward.putExtra("message_type", dbHandler.GetMessageType(SingleChatAdapter.selectRemoveItem.get(0)));
                forward.putExtra("c_name", dbHandler.GetCName(SingleChatAdapter.selectRemoveItem.get(0)));
                forward.putExtra("c_id", dbHandler.GetCId(SingleChatAdapter.selectRemoveItem.get(0)));
                forward.putExtra("media", dbHandler.GetMedia(SingleChatAdapter.selectRemoveItem.get(0)));
                forward.putExtra("group_id", dbHandler.GetgroupID(SingleChatAdapter.selectRemoveItem.get(0)));
                forward.putExtra("showpreview", dbHandler.GetPreview(SingleChatAdapter.selectRemoveItem.get(0)));

                if (dbHandler.GetPreview(SingleChatAdapter.selectRemoveItem.get(0)).equalsIgnoreCase("true") || dbHandler.GetPreview(SingleChatAdapter.selectRemoveItem.get(0)).equalsIgnoreCase("1")) {
                    forward.putExtra("meta_title", dbHandler.GetMetatitle(SingleChatAdapter.selectRemoveItem.get(0)));
                    forward.putExtra("meta_description", dbHandler.GetMetaDescription(SingleChatAdapter.selectRemoveItem.get(0)));
                    forward.putExtra("meta_logo", dbHandler.GetLogo(SingleChatAdapter.selectRemoveItem.get(0)));
                } else {
                    forward.putExtra("meta_title", "");
                    forward.putExtra("meta_description", "");
                    forward.putExtra("meta_logo", "");
                }
                startActivity(forward);
                break;


            case R.id.add_call:

                final CustomDialog customDialog = new CustomDialog(ChatActivity.this);
                customDialog.setContentView(R.layout.call_dialog);
                LinearLayout voiceCall, videoCall;
                voiceCall = (LinearLayout) customDialog.findViewById(R.id.voice_call);
                videoCall = (LinearLayout) customDialog.findViewById(R.id.video_call);


                voiceCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedHelper.putKey(ChatActivity.this, "CallType", "VoiceCall");
                        customDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("from", SharedHelper.getKey(ChatActivity.this, "id"));
                            jsonObject.put("to", zoeChatID);
                            String channel = SharedHelper.getKey(ChatActivity.this, "id");
                            jsonObject.put("channelId", channel);
                            jsonObject.put("isVideoCall", "false");
                            new PostHelper(Const.Methods.CALL, jsonObject.toString(), Const.ServiceCode.CALL_CODE, ChatActivity.this, ChatActivity.this);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                videoCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedHelper.putKey(ChatActivity.this, "CallType", "VideoCall");
                        customDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("from", SharedHelper.getKey(ChatActivity.this, "id"));
                            jsonObject.put("to", zoeChatID);
                            String channel = SharedHelper.getKey(ChatActivity.this, "id");
                            jsonObject.put("channelId", channel);
                            jsonObject.put("isVideoCall", "true");
                            new PostHelper(Const.Methods.CALL, jsonObject.toString(), Const.ServiceCode.CALL_CODE, ChatActivity.this, ChatActivity.this);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                customDialog.show();

                break;


            case R.id.cancelPreview:
                linkPreviewLayout.setVisibility(GONE);
                showPreview = false;
                title = "";
                description = "";
                logo = "";
                break;

            case R.id.action_delete:


                for (int i = 0; i < SingleChatAdapter.selectRemoveItem.size(); i++) {
                    Log.d("onOptionsItemSelected:", "dele:" + SingleChatAdapter.selectRemoveItem.get(i));
                    dbHandler = new DBHandler(ChatActivity.this);
                    dbHandler.DeleteChatsMsg(SingleChatAdapter.selectRemoveItem.get(i));
                    jsonArray.remove(SingleChatAdapter.selectRemoveItemPosition.get(i));
                }


                if (jsonArray.length() == 0) {
                    SharedHelper.putHeader(ChatActivity.this, "header_time", "0");
                }
                toolbar.getMenu().clear();
                if (localChatRoomType.equalsIgnoreCase("0")) {
                    toolbar.inflateMenu(R.menu.single_chat_menu);
                } else {
                    toolbar.inflateMenu(R.menu.group_chat_menu);
                }
                toolbar.setNavigationIcon(null);
                back_layout.setVisibility(View.VISIBLE);
                counter = 0;
                singleChatAdapter.notifyDataSetChanged();

                Log.d("onOptionsItemSelected: ", "onScroll_pos:" + SingleChatAdapter.Scrollposition + singleChatAdapter.getItemCount());

                break;


            case R.id.action_info:
                Log.d("onOptionsItemSelected: ", "alert_values:" + read_time + "," + delievered_time);
                showDialog(ChatActivity.this, ChatActivity.read_time, ChatActivity.delievered_time);
                break;

            case R.id.clear_chat:
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this, R.style.AlertDialogCustom);

                builder.setTitle("");

                builder.setMessage("Are you sure you want to delete chats?");

                builder.setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>OK</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deletechat();
                    }
                });
                builder.setNegativeButton(Html.fromHtml("<font color='#FFFFFF'>Cancel</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;

            case R.id.action_copy:

                int pos = Integer.parseInt(SingleChatAdapter.select_position.get(0));
                String msg = SingleChatAdapter.mMessages.optJSONObject(pos).optString("msg");
                Log.d("onOptionsItemSelected: ", "message:" + msg);
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copy", msg);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(ChatActivity.this, "Text Copied!!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_star_remove:


                for (int i = 0; i < SingleChatAdapter.selectRemoveItem.size(); i++) {
                    Log.d("onOptionsItemSelected:", "dele:" + SingleChatAdapter.selectRemoveItem.get(i));
                    dbHandler = new DBHandler(ChatActivity.this);
                    dbHandler.UpdateStarred(SingleChatAdapter.selectRemoveItem.get(i), "false");
                    int removePos = SingleChatAdapter.selectRemoveItemPosition.get(i);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject = jsonArray.optJSONObject(removePos);
                    jsonArray.remove(removePos);
                    try {
                        jsonObject.remove("isStarred");
                        jsonObject.remove("isSelected");
                        jsonObject.put("isStarred", "false");
                        jsonObject.put("isSelected", "false");
                        addToPos(removePos, jsonObject, jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (jsonArray.length() == 0) {
                    SharedHelper.putHeader(ChatActivity.this, "header_time", "0");
                }
                toolbar.getMenu().clear();
                if (localChatRoomType.equalsIgnoreCase("0")) {
                    toolbar.inflateMenu(R.menu.single_chat_menu);
                } else {
                    toolbar.inflateMenu(R.menu.group_chat_menu);
                }
                toolbar.setNavigationIcon(null);
                back_layout.setVisibility(View.VISIBLE);
                counter = 0;
                singleChatAdapter.notifyDataSetChanged();
                break;


            case R.id.action_star:


                for (int i = 0; i < SingleChatAdapter.selectRemoveItem.size(); i++) {
                    Log.d("onOptionsItemSelected:", "dele:" + SingleChatAdapter.selectRemoveItem.get(i));
                    dbHandler = new DBHandler(ChatActivity.this);
                    dbHandler.UpdateStarred(SingleChatAdapter.selectRemoveItem.get(i), "true");
                    int removePos = SingleChatAdapter.selectRemoveItemPosition.get(i);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject = jsonArray.optJSONObject(removePos);
                    jsonArray.remove(removePos);
                    try {
                        jsonObject.remove("isStarred");
                        jsonObject.remove("isSelected");
                        jsonObject.put("isStarred", "true");
                        jsonObject.put("isSelected", "false");
                        addToPos(removePos, jsonObject, jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (jsonArray.length() == 0) {
                    SharedHelper.putHeader(ChatActivity.this, "header_time", "0");
                }
                toolbar.getMenu().clear();
                if (localChatRoomType.equalsIgnoreCase("0")) {
                    toolbar.inflateMenu(R.menu.single_chat_menu);
                } else {
                    toolbar.inflateMenu(R.menu.group_chat_menu);
                }
                toolbar.setNavigationIcon(null);
                back_layout.setVisibility(View.VISIBLE);
                counter = 0;
                singleChatAdapter.notifyDataSetChanged();
                break;
        }
        return true;

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String getRealPathFromURI(final Context context, final Uri uri) {
//        String result;
//        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
//        if (cursor == null) { // Source is Dropbox or other similar local file path
//            result = contentURI.getPath();
//        } else {
//            cursor.moveToFirst();
//            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//            result = cursor.getString(idx);
//            cursor.close();
//        }
//        return result;

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;


    }


    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    public void showDialog(Activity activity, String read_time_val, String del_time_val) {
        final Dialog dialog = new Dialog(activity);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.info_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);


        TextView read_time, del_time;

        read_time = (TextView) dialog.findViewById(R.id.read_time_text);
        del_time = (TextView) dialog.findViewById(R.id.delievered_time_text);
        if (read_time_val.equalsIgnoreCase("0")) {
            read_time.setText("-");
        } else {
            read_time.setText(read_time_val);
        }

        if (del_time_val.equalsIgnoreCase("0")) {
            del_time.setText("-");
        } else {

            del_time.setText(del_time_val);
        }


        dialog.show();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    //goclickmethod
    @Override
    public void onClick(View view) {
        Log.d("onClick: ", "" + view.getId());
        switch (view.getId()) {

            case R.id.photo:
                mRevealView.setVisibility(GONE);
                check = true;

                Intent intent = new Intent(this, ImagePickerActivity.class);
                intent.putExtra(ImagePickerActivity.INTENT_EXTRA_FOLDER_MODE, true);
                intent.putExtra(ImagePickerActivity.INTENT_EXTRA_MODE, ImagePickerActivity.MODE_MULTIPLE);
                intent.putExtra(ImagePickerActivity.INTENT_EXTRA_LIMIT, 3);
                intent.putExtra(ImagePickerActivity.INTENT_EXTRA_SHOW_CAMERA, false);
                intent.putExtra(ImagePickerActivity.INTENT_EXTRA_FOLDER_TITLE, "Send to " + name);
                intent.putExtra(ImagePickerActivity.INTENT_EXTRA_IMAGE_TITLE, "Tap to select images");
                intent.putExtra(ImagePickerActivity.INTENT_EXTRA_IMAGE_DIRECTORY, "Camera");
                startActivityForResult(intent, 1);
                break;

            case R.id.send_chat:
                if (typing) {

                    if (!chat_text.getText().toString().matches("")) {
                        String uniqueID;
                        uniqueID = UUID.randomUUID().toString();
                        Log.e("uniqueID", uniqueID);
                        long currentTime = System.currentTimeMillis();
                        Log.e("getDateMillies", "" + currentTime);

                        Log.e("getDate", Utils.getDate(currentTime, "dd/MM/yyyy hh:mm:ss.SSS"));

                        sendChatValue = chat_text.getText().toString().trim();

                        Log.d(TAG, "onClickemoji: " + escapeJavaString(sendChatValue));

                        linkPreviewLayout.setVisibility(GONE);

//                        for (int i = 0; i < 150; i++) {
//                            int val = i + 1;
//                            uniqueID = UUID.randomUUID().toString();
//                            sendMsg(zoeChatID, "" + val, uniqueID, String.valueOf(currentTime), ChatType.text, ChatMessages.SENDER, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));
//
//                        }
                        sendMsg(zoeChatID, sendChatValue, uniqueID, String.valueOf(currentTime), ChatType.text, ChatMessages.SENDER, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));

                        chat_text.setText("");

                    } else {
                        //Toast.makeText(ChatActivity.this, "Hold to record, release to send", Toast.LENGTH_LONG).show();
                    }
                }
                break;

            case R.id.camera_photo:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);

                } else {
                    String date1 = Utils.getDate(System.currentTimeMillis(), "dd/MM/yyyy");
                    try {
                        if (Utils.formatToYesterdayOrToday(date1).equalsIgnoreCase("Today")) {
                            randomImageNo++;
                            SharedHelper.putInt(ChatActivity.this, "image_count", randomImageNo);
                        } else {
                            randomImageNo = 0;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    String date = Utils.getDate(System.currentTimeMillis(), "ddMMyyyy");
                    File filepath = Environment.getExternalStorageDirectory();
                    final File zoeFolder = new File(filepath.getAbsolutePath(), getResources().getString(R.string.app_name)).getAbsoluteFile();
                    if (!zoeFolder.exists()) {
                        zoeFolder.mkdir();
                    }
                    File newFolder = new File(zoeFolder, getResources().getString(R.string.app_name) + " Image").getAbsoluteFile();
                    if (!newFolder.exists()) {
                        newFolder.mkdir();
                    }

                    String camera_captureFile = String.valueOf("IMG_" + date + "_ZC00" + randomImageNo);
                    final File file = new File(newFolder, camera_captureFile + ".jpg");

                    uri = Uri.fromFile(file);
                    Log.d(TAG, "onClick: " + uri.getPath());
                    SharedHelper.putKey(ChatActivity.this, "camera_path_image", uri.getPath());
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(takePictureIntent, 104);
                }

                break;

            case R.id.add_attachment:

                revealShow(check);

                break;

            case R.id.header_layout:
                Intent intent1 = new Intent(ChatActivity.this, UserDetails.class);

                if (localChatRoomType.equalsIgnoreCase("0")) {

                    if (!zoeChatID.equalsIgnoreCase("34654745647")) {


                        Log.d("onClick: ", "chatroomtype:" + localChatRoomType);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent1.putExtra("zoeChatID", zoeChatID);
                        intent1.putExtra("user_name", name);
                        intent1.putExtra("image", image);
                        intent1.putExtra("group_id", groupId_loc);
                        intent1.putExtra("room_type", localChatRoomType);
                        startActivity(intent1);
                    }
                } else {

                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent1.putExtra("zoeChatID", zoeChatID);
                    intent1.putExtra("user_name", name);
                    intent1.putExtra("image", image);
                    intent1.putExtra("group_id", groupId_loc);
                    intent1.putExtra("room_type", localChatRoomType);
                    intent1.putExtra("parti_details", String.valueOf(particpants_array));
                    intent1.putExtra("grp_details", String.valueOf(group_details));
                    startActivity(intent1);

                }
                break;

            case R.id.add_notifications:
                if (dbHandler.getNotifications(groupId_loc).equalsIgnoreCase("1")) {
                    dbHandler.UpdateNotifications(groupId_loc, "0");
                    notifications.setBackgroundResource(R.drawable.ic_notifications_off_grey);
                    Toast.makeText(ChatActivity.this, getResources().getString(R.string.off_toast), Toast.LENGTH_SHORT).show();


                } else {
                    dbHandler.UpdateNotifications(groupId_loc, "1");
                    Toast.makeText(ChatActivity.this, getResources().getString(R.string.on_toast), Toast.LENGTH_SHORT).show();
                    notifications.setBackgroundResource(R.drawable.ic_notifications_grey);

                }
                break;

            case R.id.video:
                mRevealView.setVisibility(GONE);
                intent = new Intent();
                intent.setTypeAndNormalize("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent, "Video"), 2);
                check = true;
                break;

            case R.id.doodle:
                mRevealView.setVisibility(GONE);
                Intent doodle = new Intent(ChatActivity.this, DoodleActivity.class);
                startActivityForResult(doodle, 800);
                break;


            case R.id.audio:
                mRevealView.setVisibility(GONE);
                Intent audio = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                audio.setType("audio/*");
                audio.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(audio, 10);
                check = true;
                break;
            case R.id.location:
                mRevealView.setVisibility(GONE);
                check = true;
                Intent location = new Intent(ChatActivity.this, ShareLocation.class);
                location.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(location, 101);
                break;
            case R.id.contact:
                mRevealView.setVisibility(GONE);
                Intent contact = new Intent(ChatActivity.this, ContactActivity.class);
                contact.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(contact, 103);
                check = true;
                break;

            case R.id.document:
                mRevealView.setVisibility(GONE);
                Log.d("onClick: ", "Entering");
                Intent doc = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                doc.addCategory(Intent.CATEGORY_OPENABLE);
                String[] mimeTypes = {"text/*", "application/pdf", "application/msword", "application/vnd.ms-excel", "application/mspowerpoint"};
                doc.setType("*/*");
                doc.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                startActivityForResult(doc, 155);
                check = true;
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ServiceClasss.current_id = "";


        MyCommon.getInstance().chatActivity = false;
        handler.removeCallbacks(checkOnline);
        handler1.removeCallbacks(internetCheck);
        isSending = false;
        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("id", SharedHelper.getKey(getApplicationContext(), "id"));
//            socket.emit("offline", jsonObject);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        FCMMsgService.current_id = "";
//        socket.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPlayer) {
            mPlayer.stop();
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String date1 = Utils.getDate(System.currentTimeMillis(), "dd/MM/yyyy");
                    try {
                        if (Utils.formatToYesterdayOrToday(date1).equalsIgnoreCase("Today")) {
                            randomImageNo++;
                            SharedHelper.putInt(ChatActivity.this, "image_count", randomImageNo);
                        } else {
                            randomImageNo = 0;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    String date = Utils.getDate(System.currentTimeMillis(), "ddMMyyyy");
                    File filepath = Environment.getExternalStorageDirectory();
                    final File zoeFolder = new File(filepath.getAbsolutePath(), getResources().getString(R.string.app_name)).getAbsoluteFile();
                    if (!zoeFolder.exists()) {
                        zoeFolder.mkdir();
                    }
                    File newFolder = new File(zoeFolder, getResources().getString(R.string.app_name) + " Image").getAbsoluteFile();
                    if (!newFolder.exists()) {
                        newFolder.mkdir();
                    }

                    String camera_captureFile = String.valueOf("IMG_" + date + "_ZC00" + randomImageNo);
                    final File file = new File(newFolder, camera_captureFile + ".jpg");

                    uri = Uri.fromFile(file);
                    SharedHelper.putKey(ChatActivity.this, "camera_path_image", uri.getPath());
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(takePictureIntent, 104);
                } else {
                    Toast.makeText(this, "You need to give permission to open camera", Toast.LENGTH_SHORT).show();
                }
                break;


            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isSending = false;
        firstTime = true;
        if (localChatRoomType.equalsIgnoreCase("0")) {
            ServiceClasss.current_id = zoeChatID;
        } else {
            ServiceClasss.current_id = groupId_loc;
        }

        try {
            MyCommon.getInstance().chatActivity = true;
            if (localChatRoomType.equalsIgnoreCase("1")) {
                groupimagevalue();
            }
            if (localChatRoomType.equalsIgnoreCase("3")) {
                String role = dbHandler.GetChannelMember(groupId_loc, MainActivity.my_id);
                if (role.equalsIgnoreCase("user")) {
                    member_layout.setVisibility(View.VISIBLE);
                } else {
                    member_layout.setVisibility(View.GONE);

                }
            }
            handler.post(checkOnline);
            handler1.post(internetCheck);
            Log.d("onResume: ", "checkResume:" + group_details.optString("image"));

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    private void groupimagevalue() {

        new AsyncTask<JSONObject, JSONObject, JSONObject>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();


            }

            @Override
            protected JSONObject doInBackground(JSONObject... jsonObjects) {

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("groupId", groupId_loc);

                    String postJson = jsonObject.toString();
                    PostHelper postHelper = new PostHelper(ChatActivity.this);
//                    Log.d("doInBackground: ", "json:" + jsonObject);
                    return postHelper.Post(Const.Methods.PARTICIPANTS_DETAILS, postJson);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;

            }

            @Override
            protected void onPostExecute(JSONObject object) {
                super.onPostExecute(object);
                Log.e("response_grp_details", "" + object);
                try {
                    JSONObject object1 = object.optJSONObject("groupDetails");
                    if (object1.optString("image").equalsIgnoreCase("") || object1.optString("image").equalsIgnoreCase(" ")) {
                        Picasso.with(context).load(R.drawable.ic_profile_group).error(context.getResources().getDrawable(R.drawable.ic_profile_group)).into(userIcon);

                    } else {

                        Picasso.with(ChatActivity.this).load(object1.optString("image")).error(R.drawable.ic_person).into(userIcon);
                    }
                } catch (NullPointerException e) {

                }

            }
        }.execute();
    }

    private void beginCrop(Uri source) {
        //String register_id = code + number;
        Uri outputUri = Uri.fromFile(new File(Environment
                .getExternalStorageDirectory(), Calendar.getInstance().getTimeInMillis() + ".jpg"));
        Crop.of(source, outputUri).start(this);
    }

    private void handleCrop(int resultCode, final Intent result) {

        if (resultCode == RESULT_OK) {

            // filePath = getRealPathFromURI(Crop.getOutput(result));

            final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            dialog.setContentView(R.layout.preview_screen);
            ImageView back = (ImageView) dialog.findViewById(R.id.back);
            Toolbar back_took = (Toolbar) dialog.findViewById(R.id.chat_toolbar);
            back_took.setBackgroundColor(getPrimaryCOlor(ChatActivity.this));
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            Button set = (Button) dialog.findViewById(R.id.set_dialog);
            LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.root_view);
            //Uri selectedImage = data.getData();
            String path = getRealPathFromURI(this, Crop.getOutput(result));
            Drawable d = Drawable.createFromPath(path);
            linearLayout.setBackground(d);
            Button cancel = (Button) dialog.findViewById(R.id.cance_dialog);
            set.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    handleresult(Crop.getOutput(result));

                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();

            //profileImage.setImageURI(Crop.getOutput(result));
            // Log.e("image_path", "" + filePath);
            //Picasso.with(this).load(Crop.getOutput(result)).into(pro_image);
            // textView.setVisibility(View.GONE);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Utils.showShortToast("Unable to select image", getApplicationContext());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 33 && resultCode == RESULT_OK && data != null) {

            uri = data.getData();
            if (uri != null) {
                beginCrop(uri);

            } else {
                Utils.showShortToast("Unable to select image", getApplicationContext());
            }

        }

        if (requestCode == Crop.REQUEST_CROP) {
            if (data != null)
                handleCrop(resultCode, data);

        }

        if (requestCode == 34 && resultCode == RESULT_OK && data != null) {
            int value = Integer.parseInt(data.getStringExtra("selected_color"));
            shownewdialogCOlor(value);
            Log.d(TAG, "onActivityResultCOlor: " + data.getStringExtra("selected_color"));
        }

        if (requestCode == 800 && resultCode == RESULT_OK) {

            try {


                try {
                    randomImageNo = Integer.parseInt(SharedHelper.getInt(ChatActivity.this, "image_count"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                try {

                    String date1 = Utils.getDate(System.currentTimeMillis(), "dd/MM/yyyy");
                    if (Utils.formatToYesterdayOrToday(date1).equalsIgnoreCase("Today")) {
                        randomImageNo++;
                        SharedHelper.putInt(ChatActivity.this, "image_count", randomImageNo);
                    } else {
                        randomImageNo = 0;
                    }


                    String date = Utils.getDate(System.currentTimeMillis(), "ddMMyyyy");
                    File filepath = Environment.getExternalStorageDirectory();
                    final File zoeFolder = new File(filepath.getAbsolutePath(), getResources().getString(R.string.app_name)).getAbsoluteFile();
                    if (!zoeFolder.exists()) {
                        zoeFolder.mkdir();
                    }
                    File newFolder = new File(zoeFolder, getResources().getString(R.string.app_name) + " Image").getAbsoluteFile();
                    if (!newFolder.exists()) {
                        newFolder.mkdir();
                    }

                    String newFileName = String.valueOf("IMG_" + date + "_ZC00" + randomImageNo);
                    Log.e("newFileName", "" + newFileName);
                    final File file = new File(newFolder, newFileName + ".jpg");


                    File source_f = new File(context.getCacheDir(), "dummy");
                    source_f.createNewFile();

//Convert bitmap to byte array

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    doodle_bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                    byte[] bitmapdata = bos.toByteArray();


                    FileOutputStream fos = new FileOutputStream(source_f);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();


                    try {
                        FileUtils.copyFile(source_f, file);

                        ChatActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String final_filePath = file.getAbsolutePath();
                                Log.e("final_filePath", "" + final_filePath);
                                String msgId = UUID.randomUUID().toString();
                                beginUpload(final_filePath, msgId, 0);
                                scrollToBottom();
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images;
            images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
            Log.e("images", "" + images);
            for (int i = 0, l = images.size(); i < l; i++) {
                String path = images.get(i).getPath();
                Log.e("images", "" + path);
                try {
                    FileInputStream fileinputstream = new FileInputStream(path);
                    Log.e("size", "" + fileinputstream.getChannel().size());
                    double megabytes = (fileinputstream.getChannel().size() / 1024);
                    Log.e("size_mb", "" + megabytes / 2014);
                    double size = megabytes / 2014;
                    if (size > 10) {
                        Toast.makeText(ChatActivity.this, "File size is too large.", Toast.LENGTH_SHORT).show();
                        break;
                    } else {

                        try {
                            randomImageNo = Integer.parseInt(SharedHelper.getInt(ChatActivity.this, "image_count"));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();

                        }
                        try {

                            String date1 = Utils.getDate(System.currentTimeMillis(), "dd/MM/yyyy");
                            if (Utils.formatToYesterdayOrToday(date1).equalsIgnoreCase("Today")) {
                                randomImageNo++;
                                SharedHelper.putInt(ChatActivity.this, "image_count", randomImageNo);
                            } else {
                                randomImageNo = 0;
                            }

                            File sourceFile = new File(path);

                            String date = Utils.getDate(System.currentTimeMillis(), "ddMMyyyy");
                            File filepath = Environment.getExternalStorageDirectory();
                            final File zoeFolder = new File(filepath.getAbsolutePath(), getResources().getString(R.string.app_name)).getAbsoluteFile();
                            if (!zoeFolder.exists()) {
                                zoeFolder.mkdir();
                            }
                            File newFolder = new File(zoeFolder, getResources().getString(R.string.app_name) + " Image").getAbsoluteFile();
                            if (!newFolder.exists()) {
                                newFolder.mkdir();
                            }

                            String newFileName = String.valueOf("IMG_" + date + "_ZC00" + randomImageNo);
                            Log.e("newFileName", "" + newFileName);
                            final File file = new File(newFolder, newFileName + ".jpg");


                            try {
                                FileUtils.copyFile(sourceFile, file);
                                ChatActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String final_filePath = file.getAbsolutePath();
                                        Log.e("final_filePath", "" + final_filePath);
                                        String msgId = UUID.randomUUID().toString();
                                        beginUpload(final_filePath, msgId, 0);
                                        scrollToBottom();
                                    }
                                });

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } else if (requestCode == 2 && data != null) {
            Log.d("data", "onActivityResult:" + data.getData());
            Uri filePath = data.getData();
            String path = Utils.getPath(this, filePath);

            Log.d("data_video", "onActivityResult:" + path);

            try {

                FileInputStream fileinputstream = new FileInputStream(path);
                Log.e("size", "" + fileinputstream.getChannel().size());
                double megabytes = (fileinputstream.getChannel().size() / 1024);
                Log.e("size_mb", "" + megabytes / 2014);
                double size = megabytes / 2014;
                if (size > 25) {
                    Toast.makeText(ChatActivity.this, "File size is too large.", Toast.LENGTH_SHORT).show();
                } else {
                    String date1 = Utils.getDate(System.currentTimeMillis(), "dd/MM/yyyy");
                    if (Utils.formatToYesterdayOrToday(date1).equalsIgnoreCase("Today")) {
                        randomImageNo++;
                        SharedHelper.putInt(ChatActivity.this, "image_count", randomImageNo);
                    } else {
                        randomImageNo = 0;
                    }

                    String date = Utils.getDate(System.currentTimeMillis(), "ddMMyyyy");
                    File filepath = Environment.getExternalStorageDirectory();
                    final File zoeFolder = new File(filepath.getAbsolutePath(), getResources().getString(R.string.app_name)).getAbsoluteFile();
                    if (!zoeFolder.exists()) {
                        zoeFolder.mkdir();
                    }
                    File newFolder = new File(zoeFolder, getResources().getString(R.string.app_name) + " Video").getAbsoluteFile();
                    if (!newFolder.exists()) {
                        newFolder.mkdir();
                    }

                    String newFileName = String.valueOf("VID" + date + "_ZC00" + randomImageNo);
                    final File videoFile = new File(newFolder, newFileName + ".mp4");

                    File sourceFile = new File(path);
                    FileUtils.copyFile(sourceFile, videoFile);
                    String videoPath = videoFile.getAbsolutePath();
                    String msgId = UUID.randomUUID().toString();
                    beginUpload(videoPath, msgId, 1);
                    scrollToBottom();
//                    if (localChatRoomType.equalsIgnoreCase("0")) {
//                        sendMsg(zoeChatID, videoPath, UUID.randomUUID().toString(), String.valueOf(System.currentTimeMillis()),
//                                ChatType.video, ChatMessages.SENDER_VIDEO, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));
//                    } else {
//                        sendMsg(groupId_loc, videoPath, UUID.randomUUID().toString(), String.valueOf(System.currentTimeMillis()),
//                                ChatType.video, ChatMessages.SENDER_VIDEO, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));
//                    }


                }
            } catch (Exception e) {
                Log.e("onActivityResult: ", "" + e);
                Toast.makeText(ChatActivity.this, "Couldn't find the file.", Toast.LENGTH_SHORT).show();
            }


        } else if (requestCode == 101 && data != null) {
            Log.e("location", "" + data.getStringExtra("location"));
            Log.e("lat", "" + data.getStringExtra("lat"));
            Log.e("lng", "" + data.getStringExtra("lng"));
            lat = Double.valueOf(data.getStringExtra("lat").trim());
            lng = Double.valueOf(data.getStringExtra("lng").trim());

            final String URL = "http://maps.google.com/maps/api/staticmap?center=" + lat + "," + lng + "&markers=size:large%7Ccolor:red%7C" + lat + "," + lng + "&zoom=12&size=350x280&sensor=false";

            ChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("final_filePath", "" + URL);

                    if (localChatRoomType.equalsIgnoreCase("0")) {
                        sendMsg(zoeChatID, URL, UUID.randomUUID().toString(),
                                String.valueOf(System.currentTimeMillis()), ChatType.location, ChatMessages.SENDER_LOCATION, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));
                    } else {
                        sendMsg(groupId_loc, URL, UUID.randomUUID().toString(),
                                String.valueOf(System.currentTimeMillis()), ChatType.location, ChatMessages.SENDER_LOCATION, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));
                    }
                    scrollToBottom();
                }
            });
        } else if (requestCode == 103 && data != null) {
            try {
                JSONArray array = new JSONArray(data.getStringExtra("selected_contact"));
                Log.e("array", "" + array);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.optJSONObject(i);
                    String value;
                    if (!jsonObject.optString("zoeChatId").equalsIgnoreCase("")) {
                        value = jsonObject.optString("zoeChatId");
                    } else {
                        value = jsonObject.optString("mobile");

                    }
                    if (localChatRoomType.equalsIgnoreCase("0")) {
                        sendMsg(zoeChatID, jsonObject.optString("image"), UUID.randomUUID().toString(),
                                String.valueOf(System.currentTimeMillis()), ChatType.contact, ChatMessages.SENDER_CONTACT, groupName, groupId_loc, create_by, sent_by, localChatRoomType, jsonObject.optString("name"), value, dbHandler.GetSign(groupId_loc));
                    } else {
                        sendMsg(groupId_loc, jsonObject.optString("image"), UUID.randomUUID().toString(),
                                String.valueOf(System.currentTimeMillis()), ChatType.contact, ChatMessages.SENDER_CONTACT, groupName, groupId_loc, create_by, sent_by, localChatRoomType, jsonObject.optString("name"), value, dbHandler.GetSign(groupId_loc));
                    }
                    scrollToBottom();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (requestCode == 10 && data != null) {
            Uri filePath = data.getData();
            String audio_link = Utils.getPath(this, filePath);

            Log.e("audio_link", "" + audio_link);
            try {

                FileInputStream fileinputstream = new FileInputStream(audio_link);
                Log.e("size", "" + fileinputstream.getChannel().size());
                double megabytes = (fileinputstream.getChannel().size() / 1024);
                Log.e("size_mb", "" + megabytes / 2014);
                double size = megabytes / 2014;
                if (size > 25) {
                    Toast.makeText(ChatActivity.this, "File size is too large.", Toast.LENGTH_SHORT).show();
                } else {
                    String date1 = Utils.getDate(System.currentTimeMillis(), "dd/MM/yyyy");
                    if (Utils.formatToYesterdayOrToday(date1).equalsIgnoreCase("Today")) {
                        randomImageNo++;
                        SharedHelper.putInt(ChatActivity.this, "image_count", randomImageNo);
                    } else {
                        randomImageNo = 0;
                    }

                    String date = Utils.getDate(System.currentTimeMillis(), "ddMMyyyy");
                    File filepath = Environment.getExternalStorageDirectory();
                    final File zoeFolder = new File(filepath.getAbsolutePath(), getResources().getString(R.string.app_name)).getAbsoluteFile();
                    if (!zoeFolder.exists()) {
                        zoeFolder.mkdir();
                    }
                    File newFolder = new File(zoeFolder, getResources().getString(R.string.app_name) + " Audio").getAbsoluteFile();
                    if (!newFolder.exists()) {
                        newFolder.mkdir();
                    }

                    String newFileName = String.valueOf("AUD" + date + "_ZC00" + randomImageNo);
                    final File audiofile = new File(newFolder, newFileName + ".mp3");

                    File sourceFile = new File(audio_link);
                    FileUtils.copyFile(sourceFile, audiofile);
                    String videoPath = audiofile.getAbsolutePath();
                    String msgId = UUID.randomUUID().toString();
                    beginUpload(videoPath, msgId, 2);
                    scrollToBottom();
//                    if (localChatRoomType.equalsIgnoreCase("0")) {
//                        sendMsg(zoeChatID, videoPath, UUID.randomUUID().toString(), String.valueOf(System.currentTimeMillis()), ChatType.audio, ChatMessages.SENDER_AUDIO, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));
//                    } else {
//                        sendMsg(groupId_loc, videoPath, UUID.randomUUID().toString(), String.valueOf(System.currentTimeMillis()), ChatType.audio, ChatMessages.SENDER_AUDIO, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));
//                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ChatActivity.this, "Couldn't find the file.", Toast.LENGTH_SHORT).show();
            }


        } else if (requestCode == 155 && data != null) {
            Log.d("onActivityResult: ", "" + data);
            Uri filePath = data.getData();
            Log.d("onActivityResult: ", "file:" + filePath);
            String audio_link = Utils.getPath(this, filePath);
            ContentResolver cR = ChatActivity.this.getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String type = mime.getExtensionFromMimeType(cR.getType(filePath));
            Log.d("onActivityResult: ", "doc_type:" + type);


            Log.e("dcoument_url", "" + audio_link);
            try {

                FileInputStream fileinputstream = new FileInputStream(audio_link);
                Log.e("size", "" + fileinputstream.getChannel().size());
                double megabytes = (fileinputstream.getChannel().size() / 1024);
                Log.e("size_mb", "" + megabytes / 2014);
                double size = megabytes / 2014;
                if (size > 25) {
                    Toast.makeText(ChatActivity.this, "File size is too large.", Toast.LENGTH_SHORT).show();
                } else {
                    String date1 = Utils.getDate(System.currentTimeMillis(), "dd/MM/yyyy");
                    if (Utils.formatToYesterdayOrToday(date1).equalsIgnoreCase("Today")) {
                        randomImageNo++;
                        SharedHelper.putInt(ChatActivity.this, "image_count", randomImageNo);
                    } else {
                        randomImageNo = 0;
                    }

                    String date = Utils.getDate(System.currentTimeMillis(), "ddMMyyyy");
                    File filepath = Environment.getExternalStorageDirectory();
                    final File zoeFolder = new File(filepath.getAbsolutePath(), getResources().getString(R.string.app_name)).getAbsoluteFile();
                    if (!zoeFolder.exists()) {
                        zoeFolder.mkdir();
                    }
                    File newFolder = new File(zoeFolder, getResources().getString(R.string.app_name) + " Documents").getAbsoluteFile();
                    if (!newFolder.exists()) {
                        newFolder.mkdir();
                    }

                    String newFileName = String.valueOf("DOC" + date + "_ZC00" + randomImageNo + "." + type);
                    final File audiofile = new File(newFolder, newFileName);

                    File sourceFile = new File(audio_link);
                    FileUtils.copyFile(sourceFile, audiofile);
                    String videoPath = audiofile.getAbsolutePath();
                    String msgId = UUID.randomUUID().toString();
                    beginDocumentUpload(videoPath, msgId);
                    if (localChatRoomType.equalsIgnoreCase("0")) {
                        Log.d("onActivityResult: ", "documentMess:" + getImageContentUri(ChatActivity.this, videoPath));
                        sendMsg(zoeChatID, String.valueOf(getImageContentUri(ChatActivity.this, videoPath)), UUID.randomUUID().toString(), String.valueOf(System.currentTimeMillis()), ChatType.document, ChatMessages.SENDER_DOC, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));
                    } else {
                        Log.d("onActivityResult: ", "documentMess:" + getImageContentUri(ChatActivity.this, videoPath));

                        sendMsg(groupId_loc, String.valueOf(getImageContentUri(ChatActivity.this, videoPath)), UUID.randomUUID().toString(), String.valueOf(System.currentTimeMillis()), ChatType.document, ChatMessages.SENDER_DOC, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ChatActivity.this, "Couldn't find the file.", Toast.LENGTH_SHORT).show();
            }


        } else if (resultCode==RESULT_OK&&requestCode==104)
        {

            String path = uri.getPath();
            Log.e("images", "" + path);
            try {
                FileInputStream fileinputstream = new FileInputStream(path);
                Log.e("size", "" + fileinputstream.getChannel().size());
                double megabytes = (fileinputstream.getChannel().size() / 1024);
                Log.e("size_mb", "" + megabytes / 2014);
                double size = megabytes / 2014;
                if (size > 10) {
                    Toast.makeText(ChatActivity.this, "File size is too large.", Toast.LENGTH_SHORT).show();
             return;
                } else {

                    try {
                        randomImageNo = Integer.parseInt(SharedHelper.getInt(ChatActivity.this, "image_count"));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();

                    }
                    try {

                        String date1 = Utils.getDate(System.currentTimeMillis(), "dd/MM/yyyy");
                        if (Utils.formatToYesterdayOrToday(date1).equalsIgnoreCase("Today")) {
                            randomImageNo++;
                            SharedHelper.putInt(ChatActivity.this, "image_count", randomImageNo);
                        } else {
                            randomImageNo = 0;
                        }

                        File sourceFile = new File(path);

                        String date = Utils.getDate(System.currentTimeMillis(), "ddMMyyyy");
                        File filepath = Environment.getExternalStorageDirectory();
                        final File zoeFolder = new File(filepath.getAbsolutePath(), getResources().getString(R.string.app_name)).getAbsoluteFile();
                        if (!zoeFolder.exists()) {
                            zoeFolder.mkdir();
                        }
                        File newFolder = new File(zoeFolder, getResources().getString(R.string.app_name) + " Image").getAbsoluteFile();
                        if (!newFolder.exists()) {
                            newFolder.mkdir();
                        }

                        String newFileName = String.valueOf("IMG_" + date + "_ZC00" + randomImageNo);
                        Log.e("newFileName", "" + newFileName);
                        final File file = new File(newFolder, newFileName + ".jpg");


                        try {
                            FileUtils.copyFile(sourceFile, file);
                            ChatActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String final_filePath = file.getAbsolutePath();
                                    Log.e("final_filePath", "" + final_filePath);
                                    String msgId = UUID.randomUUID().toString();
                                    beginUpload(final_filePath, msgId, 0);
                                    scrollToBottom();
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else if (resultCode == RESULT_OK && requestCode != 33 && requestCode != 800 && requestCode != 34 && requestCode != 6709) {
//            Log.d(TAG, "onActivityResult: data"+data.getData());

            if (data != null) {
                Log.d(TAG, "onActivityResults: " + uri.getPath());
                String path = SharedHelper.getKey(ChatActivity.this, "camera_path_image");
                if (localChatRoomType.equalsIgnoreCase("0")) {
                    sendMsg(zoeChatID, path, UUID.randomUUID().toString(), String.valueOf(System.currentTimeMillis()), ChatType.image, ChatMessages.SENDER_IMAGE, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));
                } else {
                    sendMsg(groupId_loc, path, UUID.randomUUID().toString(), String.valueOf(System.currentTimeMillis()), ChatType.image, ChatMessages.SENDER_IMAGE, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));
                }

            } else {
                String path = SharedHelper.getKey(ChatActivity.this, "camera_path_image");
                if (localChatRoomType.equalsIgnoreCase("0")) {
                    sendMsg(zoeChatID, path, UUID.randomUUID().toString(), String.valueOf(System.currentTimeMillis()), ChatType.image, ChatMessages.SENDER_IMAGE, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));
                } else {
                    sendMsg(groupId_loc, path, UUID.randomUUID().toString(), String.valueOf(System.currentTimeMillis()), ChatType.image, ChatMessages.SENDER_IMAGE, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));
                }
            }


            scrollToBottom();
        }

    }

    private void beginDocumentUpload(final String image_path, final String msgId) {

        final DBHandler dbHandler = new DBHandler(mContext);
        final String image_path_val;
        image_path_val = image_path;
        final File file = new File(image_path_val);
        resultChattype = ChatType.document;
        resultMmessageType = ChatMessages.SENDER_DOC;


        if (localChatRoomType.equalsIgnoreCase("0")) {
            sendMsg(zoeChatID, image_path, msgId, String.valueOf(System.currentTimeMillis()),
                    resultChattype, resultMmessageType, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));
        } else {
            sendMsg(groupId_loc, image_path, msgId, String.valueOf(System.currentTimeMillis()),
                    resultChattype, resultMmessageType, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));
        }

        s3Client.setEndpoint(Const.ENDPOINT);
        transferUtility = new TransferUtility(s3Client, mContext);
        transferObserver = transferUtility.upload(Const.bucket_name, file.getName(), file);

        singleChatAdapter.setIsUploaded("uploading", msgId);

        dbHandler.InsertAmzonId(msgId, "" + transferObserver.getId());
        transferObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.d("image_state", state.toString());
                String groupId;
                if (state.toString().equalsIgnoreCase("IN_PROGRESS")) {

                    dbHandler.UpdateIsUploaded(msgId, "uploading");


                } else if (state.toString().equalsIgnoreCase("WAITING_FOR_NETWORK")) {
                    dbHandler.UpdateIsUploaded(msgId, "uploading");

                } else if (state.toString().equalsIgnoreCase("CANCELED")) {
                    dbHandler.UpdateIsUploaded(msgId, "uploadFailed");
                    singleChatAdapter.setIsUploaded("uploadFailed", msgId);
                } else if (state.toString().equalsIgnoreCase("COMPLETED")) {
                    String userID;

                    if (localChatRoomType.equalsIgnoreCase("0")) {

                        userID = zoeChatID;
                    } else {
                        userID = groupId_loc;
                    }
                    groupId = userID;
                    dbHandler.UpdateIsUploaded(msgId, "uploaded");
                    singleChatAdapter.setIsUploaded("uploaded", msgId);
                    long currentTime = System.currentTimeMillis();

                    SendChatMsg(Const.amazons3ServerImagePath + file.getName(), msgId,
                            zoeChatID, String.valueOf(currentTime), resultChattype, SharedHelper.getKey(mContext, "id"), groupId, localChatRoomType, dbHandler.getNotifications(groupId));

                    if (localChatRoomType.equalsIgnoreCase("0")) {
                        if (!dbHandler.DoesChatsUser(userID)) {


                            dbHandler.InsertChats(new ChatsModel(userID, "0", resultMmessageType,
                                    Const.amazons3ServerImagePath + file.getName(), Status.SENT.toString(), String.valueOf(System.currentTimeMillis()), resultChattype.toString()
                                    , 0, "", "", "", "", "0", "0", "", "", "", "", "", "", "false", "1", ""));


                        } else {
                            String isDeleted = dbHandler.getDeleteStatus(userID);
                            if (isDeleted.equalsIgnoreCase("1")) {
                                dbHandler.GroupDeleteUpdate("0", userID);
                            }

                            Log.d("onStateChanged: ", "4");

                            dbHandler.UpdateLastMsg(userID, resultMmessageType, Const.amazons3ServerImagePath + file.getName(),
                                    Status.SENT.toString(), String.valueOf(System.currentTimeMillis()), resultChattype.toString(), 0);

                        }
                    } else if (localChatRoomType.equalsIgnoreCase("1")) {
                        if (!dbHandler.DoesChatsUser(userID)) {

                            Log.d("onStateChanged: ", "6");

                            dbHandler.InsertChats(new ChatsModel(groupId, "0", resultMmessageType,
                                    Const.amazons3ServerImagePath + file.getName(), Status.SENT.toString(), String.valueOf(System.currentTimeMillis()), resultChattype.toString()
                                    , 0, "", "", "", "", "0", "0", "", "", "", "", "", "", "false", "1", ""));
                        }


                    } else {
                        String isDeleted = dbHandler.getDeleteStatus(groupId);
                        if (isDeleted.equalsIgnoreCase("1")) {
                            dbHandler.GroupDeleteUpdate("0", groupId);
                        }

                        Log.d("onStateChanged: ", "8");

                        dbHandler.UpdateLastMsg(groupId, resultMmessageType, Const.amazons3ServerImagePath + file.getName(),
                                Status.SENT.toString(), String.valueOf(System.currentTimeMillis()), resultChattype.toString(), 0);

                    }

                    dbHandler.UpdateChatMsgStatus(msgId, Const.amazons3ServerImagePath + file.getName());
                    dbHandler.UpdateMedia(msgId, image_path);

                    SingleChatAdapter.refresh(msgId, Const.amazons3ServerImagePath + file.getName());
                }

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int percentage = (int) (bytesCurrent / bytesTotal * 100);
                Log.d("image_state_percentage", String.valueOf(percentage));
            }

            @SuppressWarnings("deprecation")
            @Override
            public void onError(int id, Exception ex) {
                dbHandler.UpdateIsUploaded(msgId, "uploadFailed");


            }
        });

        Log.e("image_state_error", String.valueOf(transferObserver.getBytesTransferred()));

        dbHandler.close();
    }


    private void beginUpload(final String image_path, final String msgId, int type) {

        final DBHandler dbHandler = new DBHandler(mContext);
        final String image_path_val;
        image_path_val = image_path;
        final File file = new File(image_path_val);


        if (type == 0) {

            resultChattype = ChatType.image;
            resultMmessageType = ChatMessages.SENDER_IMAGE;

        } else if (type == 1) {
            resultChattype = ChatType.video;
            resultMmessageType = ChatMessages.SENDER_VIDEO;
        } else if (type == 2) {
            resultChattype = ChatType.audio;
            resultMmessageType = ChatMessages.SENDER_AUDIO;
        } else if (type == 3) {
            resultChattype = ChatType.document;
            resultMmessageType = ChatMessages.SENDER_DOC;
        }

        if (localChatRoomType.equalsIgnoreCase("0")) {
            sendMsg(zoeChatID, image_path, msgId, String.valueOf(System.currentTimeMillis()),
                    resultChattype, resultMmessageType, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));
        } else {
            sendMsg(groupId_loc, image_path, msgId, String.valueOf(System.currentTimeMillis()),
                    resultChattype, resultMmessageType, groupName, groupId_loc, create_by, sent_by, localChatRoomType, "", "", dbHandler.GetSign(groupId_loc));
        }

        s3Client.setEndpoint(Const.ENDPOINT);
        transferUtility = new TransferUtility(s3Client, mContext);
        transferObserver = transferUtility.upload(Const.bucket_name, file.getName(), file);

        singleChatAdapter.setIsUploaded("uploading", msgId);
        Log.d(TAG, "beginUpload: "+transferObserver.getId());
        dbHandler.InsertAmzonId(msgId, "" + transferObserver.getId());
        transferObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.d("image_state", state.toString());
                String groupId;
                if (state.toString().equalsIgnoreCase("IN_PROGRESS")) {

                    dbHandler.UpdateIsUploaded(msgId, "uploading");


                } else if (state.toString().equalsIgnoreCase("WAITING_FOR_NETWORK")) {
                    dbHandler.UpdateIsUploaded(msgId, "uploading");

                } else if (state.toString().equalsIgnoreCase("CANCELED")) {
                    dbHandler.UpdateIsUploaded(msgId, "uploadFailed");
                    singleChatAdapter.setIsUploaded("uploadFailed", msgId);
                } else if (state.toString().equalsIgnoreCase("COMPLETED")) {
                    String userID;

                    if (localChatRoomType.equalsIgnoreCase("0")) {

                        userID = zoeChatID;
                    } else {
                        userID = groupId_loc;
                    }
                    groupId = userID;
                    dbHandler.UpdateIsUploaded(msgId, "uploaded");
                    dbHandler.amazonIdDelete(msgId);
                    singleChatAdapter.setIsUploaded("uploaded", msgId);
                    long currentTime = System.currentTimeMillis();

                    SendChatMsgwithThumb(Const.amazons3ServerImagePath + file.getName(), msgId,
                            userID, String.valueOf(currentTime), resultChattype, SharedHelper.getKey(mContext, "id"), groupId, localChatRoomType, "", dbHandler.getNotifications(groupId));


                    if (localChatRoomType.equalsIgnoreCase("0")) {
                        if (!dbHandler.DoesChatsUser(userID)) {


                            dbHandler.InsertChats(new ChatsModel(zoeChatID, "0", resultMmessageType,
                                    image_path, Status.SENT.toString(), String.valueOf(System.currentTimeMillis()), resultChattype.toString()
                                    , 0, "", "", "", "", "0", "0", "", "", "", "", "", "", "false", "1", ""));

                        } else {
                            String isDeleted = dbHandler.getDeleteStatus(userID);
                            if (isDeleted.equalsIgnoreCase("1")) {
                                dbHandler.GroupDeleteUpdate("0", userID);
                            }

                            Log.d("onStateChanged: ", "4");

                            dbHandler.UpdateLastMsg(userID, resultMmessageType, image_path,
                                    Status.SENT.toString(), String.valueOf(System.currentTimeMillis()), resultChattype.toString(), 0);

                        }
                    } else if (localChatRoomType.equalsIgnoreCase("1")) {
                        if (!dbHandler.DoesChatsUser(userID)) {

                            Log.d("onStateChanged: ", "6");

                            dbHandler.InsertChats(new ChatsModel(groupId, "0", resultMmessageType,
                                    image_path, Status.SENT.toString(), String.valueOf(System.currentTimeMillis()), resultChattype.toString()
                                    , 0, "", "", "", "", "0", "0", "", "", "", "", "", "", "false", "1", ""));
                        }


                    } else {
                        String isDeleted = dbHandler.getDeleteStatus(groupId);
                        if (isDeleted.equalsIgnoreCase("1")) {
                            dbHandler.GroupDeleteUpdate("0", groupId);
                        }

                        Log.d("onStateChanged: ", "8");

                        dbHandler.UpdateLastMsg(groupId, resultMmessageType, image_path,
                                Status.SENT.toString(), String.valueOf(System.currentTimeMillis()), resultChattype.toString(), 0);

                    }
                }

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int percentage = (int) (bytesCurrent / bytesTotal * 100);
                Log.d("image_state_percentage", String.valueOf(percentage));
            }

            @SuppressWarnings("deprecation")
            @Override
            public void onError(int id, Exception ex) {
                dbHandler.UpdateIsUploaded(msgId, "uploadFailed");


            }
        });

        Log.e("image_state_error", String.valueOf(transferObserver.getBytesTransferred()));

        dbHandler.close();
    }

    private void handleresult(Uri data) {

        //  Uri selectedImage = data.getData();
        Log.d(TAG, "onActivityResult: " + getRealPathFromURI(this, data));

        try {
            FileInputStream fileinputstream = new FileInputStream(getRealPathFromURI(this, data));
            Log.e("size", "" + fileinputstream.getChannel().size());
            double megabytes = (fileinputstream.getChannel().size() / 1024);
            Log.e("size_mb", "" + megabytes / 2014);
            double size = megabytes / 2014;
            if (size > 10) {
                Toast.makeText(ChatActivity.this, "File size is too large.", Toast.LENGTH_SHORT).show();
            } else {

                try {
                    randomImageNo = Integer.parseInt(SharedHelper.getInt(ChatActivity.this, "image_count"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();

                }
                try {

                    String date1 = Utils.getDate(System.currentTimeMillis(), "dd/MM/yyyy");
                    if (Utils.formatToYesterdayOrToday(date1).equalsIgnoreCase("Today")) {
                        randomImageNo++;
                        SharedHelper.putInt(ChatActivity.this, "image_count", randomImageNo);
                    } else {
                        randomImageNo = 0;
                    }

                    File sourceFile = new File(getRealPathFromURI(this, data));

                    String date = Utils.getDate(System.currentTimeMillis(), "ddMMyyyy");
                    File filepath = Environment.getExternalStorageDirectory();
                    final File zoeFolder = new File(filepath.getAbsolutePath(), getResources().getString(R.string.app_name)).getAbsoluteFile();
                    if (!zoeFolder.exists()) {
                        zoeFolder.mkdir();
                    }
                    File newFolder = new File(zoeFolder, getResources().getString(R.string.app_name) + " Wallpaper").getAbsoluteFile();
                    if (!newFolder.exists()) {
                        newFolder.mkdir();
                    }

                    String newFileName = String.valueOf("WALL_" + date + "_ZC00" + randomImageNo);
                    Log.e("newFileName", "" + newFileName);
                    final File file = new File(newFolder, newFileName + ".jpg");


                    try {
                        FileUtils.copyFile(sourceFile, file);
                        ChatActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String final_filePath = file.getAbsolutePath();
                                Drawable d = Drawable.createFromPath(final_filePath);
                                getWindow().setBackgroundDrawable(d);
                                if (localChatRoomType.equalsIgnoreCase("0")) {
                                    dbHandler.UpdateWallpaper(zoeChatID, final_filePath);
                                } else {
                                    dbHandler.UpdateWallpaper(groupId_loc, final_filePath);
                                }

                                //setwallpapaer
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void handlereesilt(final int s) {
        ChatActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ColorDrawable d = new ColorDrawable(s);
                getWindow().setBackgroundDrawable(d);
                if (localChatRoomType.equalsIgnoreCase("0")) {
                    dbHandler.UpdateWallpaper(zoeChatID, "val" + String.valueOf(s));
                } else {
                    dbHandler.UpdateWallpaper(groupId_loc, "val" + String.valueOf(s));
                }

                //setwallpapaer
            }
        });
    }

//    private void shownewdialog(final Intent data) {
//
//        final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
//        dialog.setContentView(R.layout.preview_screen);
//        ImageView back = (ImageView) dialog.findViewById(R.id.back);
//        Toolbar back_took = (Toolbar) dialog.findViewById(R.id.chat_toolbar);
//        back_took.setBackgroundColor(getPrimaryCOlor(ChatActivity.this));
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        Button set = (Button) dialog.findViewById(R.id.set_dialog);
//        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.root_view);
//        Uri selectedImage = data.getData();
//        String path = getRealPathFromURI(this, selectedImage);
//        Drawable d = Drawable.createFromPath(path);
//        linearLayout.setBackground(d);
//        Button cancel = (Button) dialog.findViewById(R.id.cance_dialog);
//        set.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                handleresult(data);
//
//            }
//        });
//
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
//
//
//    }

    private void shownewdialogCOlor(final int data) {

        final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.preview_screen);
        ImageView back = (ImageView) dialog.findViewById(R.id.back);
        Toolbar back_took = (Toolbar) dialog.findViewById(R.id.chat_toolbar);
        back_took.setBackgroundColor(getPrimaryCOlor(ChatActivity.this));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button set = (Button) dialog.findViewById(R.id.set_dialog);
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.root_view);

        linearLayout.setBackgroundColor(data);
        Button cancel = (Button) dialog.findViewById(R.id.cance_dialog);
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                handlereesilt(data);

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();


    }

    @Override
    public void onTaskCompleted(JSONObject response, int serviceCode) {
        switch (serviceCode) {
            case Const.ServiceCode.CALL_CODE:
                Utils.appLog("Call_res", response.toString());
                if (response != null) {
                    DBHandler dbHandler = new DBHandler(ChatActivity.this);
                    if (response.optString("error").equalsIgnoreCase("false")) {

                        if (SharedHelper.getKey(ChatActivity.this, "CallType").equalsIgnoreCase("VoiceCall")) {
                            dbHandler.InsertCalls(new CallsModel(zoeChatID, name, image, System.currentTimeMillis(), "VoiceCall", "0"));
                            Intent call = new Intent(ChatActivity.this, VideoChatViewActivity.class);
                            call.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            call.putExtra("zoeChatID", zoeChatID);
                            call.putExtra("user_name", name);
                            call.putExtra("image", image);
                            call.putExtra("receive", "no");
                            call.putExtra("call", "voiceCall");
                            startActivity(call);

                        } else if (SharedHelper.getKey(ChatActivity.this, "CallType").equalsIgnoreCase("VideoCall")) {
                            dbHandler.InsertCalls(new CallsModel(zoeChatID, name, image, System.currentTimeMillis(), "VideoCall", "0"));
                            Intent call = new Intent(ChatActivity.this, VideoChatViewActivity.class);
                            call.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            call.putExtra("zoeChatID", zoeChatID);
                            call.putExtra("user_name", name);
                            call.putExtra("image", image);
                            call.putExtra("call", "videoCall");
                            call.putExtra("receive", "no");
                            startActivity(call);
                        }

                    } else {
                        Utils.showShortToast(response.optString("message"), ChatActivity.this);
                    }
                    dbHandler.close();

                }
                break;
        }
    }

    private void validatelocalvalues(List<GroupParticiapntsModel> models, JSONArray particpants_array) {
        DBHandler dbHandler = new DBHandler(this);
        for (int i = 0; i < models.size(); i++) {
            GroupParticiapntsModel values = models.get(i);
            String id = values.getUser_id();
            Boolean exist = value(particpants_array, id);
            if (!exist) {
                dbHandler.DeleteGroupParticipants(id, groupId_loc);
            }
        }

    }

    private void validatelocalcvalues(List<ChannelParticiapntsModel> models, JSONArray particpants_array) {
        DBHandler dbHandler = new DBHandler(this);
        for (int i = 0; i < models.size(); i++) {
            ChannelParticiapntsModel values = models.get(i);
            String id = values.getUser_id();
            Boolean exist = value(particpants_array, id);
            if (!exist) {
                dbHandler.DeleteChannelParticipants(id, groupId_loc);
            }
        }

    }

    private boolean value(JSONArray jsonArray, String part_id) {
        return jsonArray.toString().contains("\"participantId\":\"" + part_id + "\"");
    }

    private void validateincomingvalues(JSONArray particpants_array) {
        DBHandler dbHandler = new DBHandler(ChatActivity.this);

        for (int i = 0; i < particpants_array.length(); i++) {
            String id = particpants_array.optJSONObject(i).optString("participantId");
            String grp_id = particpants_array.optJSONObject(i).optString("groupId");
            Boolean exist = dbHandler.CheckParticipantAlreadyInDBorNot(id, grp_id);
            if (exist) {
                String isAdmin = particpants_array.optJSONObject(i).optString("isAdmin");
                if (isAdmin.equalsIgnoreCase("")) {
                    isAdmin = "0";
                }
                dbHandler.groupPartiUpdate(grp_id, particpants_array.optJSONObject(i).optString("joinedAt"), isAdmin, particpants_array.optJSONObject(i).optString("addedBy"), id);
            } else {
                String isAdmin = particpants_array.optJSONObject(i).optString("isAdmin");
                if (isAdmin.equalsIgnoreCase("")) {
                    isAdmin = "0";
                }
                dbHandler.AddGroupParticipants(new GroupParticiapntsModel(id, grp_id, particpants_array.optJSONObject(i).optString("joinedAt"), particpants_array.optJSONObject(i).optString("addedBy"), isAdmin));

            }

        }


    }

    private void validateincomingvalueschannel(JSONArray particpants_array) {
        DBHandler dbHandler = new DBHandler(ChatActivity.this);

        for (int i = 0; i < particpants_array.length(); i++) {
            String id = particpants_array.optJSONObject(i).optString("participantId");
            String grp_id = particpants_array.optJSONObject(i).optString("channelId");
            Boolean exist = dbHandler.CheckChanneParticipantAlreadyInDBorNot(id, grp_id);
            if (exist) {
                String isAdmin = particpants_array.optJSONObject(i).optString("isAdmin");
                if (isAdmin.equalsIgnoreCase("")) {
                    isAdmin = "user";
                }
                dbHandler.channelPartiUpdate(grp_id, particpants_array.optJSONObject(i).optString("joinedAt"), isAdmin, particpants_array.optJSONObject(i).optString("addedBy"), id);
            } else {
                String isAdmin = particpants_array.optJSONObject(i).optString("isAdmin");
                if (isAdmin.equalsIgnoreCase("")) {
                    isAdmin = "user";
                }
                dbHandler.AddChannelParticipants(new ChannelParticiapntsModel(id, grp_id, particpants_array.optJSONObject(i).optString("joinedAt"), particpants_array.optJSONObject(i).optString("addedBy"), isAdmin));

            }

        }


    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    private Bitmap getBitmapFromString(String stringPicture) {
/*
* This Function converts the String back to Bitmap
* */
        byte[] decodedString = Base64.decode(stringPicture, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
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

    // Title AsyncTask
    public static class MetaData extends AsyncTask<Void, Void, Void> {
        String url;
        String s_url;
        JSONObject metaObject;
        AsyncResponse delegate = null;


        MetaData(String url, AsyncResponse delegate) {
            this.delegate = delegate;
            this.url = url;
            this.s_url = url;
        }


        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Connect to the web site
                Document document = Jsoup.connect(url).get();
//                Log.d(TAG, "doInBackground: " + document);
                // Get the html document title
                title = document.title();
                url = document.baseUri();
                Elements descriptionele = document
                        .select("meta[name=description]");
                // Locate the content attribute
                description = descriptionele.attr("content");
                Elements img = document.select("a[class=brand brand-image] img[src]");

                try {
                    Element e1 = document.head().select("link[href~=.*\\.(ico|png)]").first(); // example type 1 & 2
                    logo = e1.attr("href");

                } catch (NullPointerException e) {
                    Elements metaOgImage = document.select("meta[property=og:image]");
                    if (metaOgImage != null) {
                        logo = metaOgImage.attr("content");
                    }
                }


//                // Locate the src attribute
//                String imgSrc = img.attr("src");
//                // Download image from URL
//                String picture;
//                try {
//                    InputStream input = new java.net.URL(imgSrc).openStream();
//                    Bitmap bitmap = BitmapFactory.decodeStream(input);
//                    picture = getStringFromBitmap(bitmap);
//                }
//                catch (Exception e)
//                {
//                    picture = "";
//                    e.printStackTrace();
//                }
                // Decode Bitmap

                metaObject = new JSONObject();
                metaObject.put("title", title);
                metaObject.put("description", description);
                metaObject.put("logo", logo);
                metaObject.put("url", s_url);
                DBHandler dbHandler = new DBHandler(AppController.getContext());
                dbHandler.InsertLinks(metaObject);
                showPreview = true;
                metaObject.put("showPreview", true);
                System.out.println("Meta Data" + metaObject.toString());

            } catch (Exception e) {
                try {
                    metaObject = new JSONObject();
                    showPreview = false;
                    metaObject.put("showPreview", false);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.d(TAG, "ProcessonCancelled: ");
        }

        @Override
        protected void onPostExecute(Void result) {

            delegate.processFinish(metaObject);


        }

        interface AsyncResponse {
            void processFinish(JSONObject output);
        }
    }

    private class getGrpdetails extends AsyncTask<String, String, String> implements AsyncTaskCompleteListener {
        String result = "";

        @Override
        protected String doInBackground(String... params) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("groupId", groupId_loc);

                if (internet) {
                    new PostHelper(Const.Methods.PARTICIPANTS_DETAILS, jsonObject.toString(), Const.ServiceCode.PARTICIPANTS_DETAILS, ChatActivity.this, this);
                } else {
                    result = "yes";
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }


        @Override
        public void onTaskCompleted(JSONObject response, int serviceCode) {

            DBHandler dbHandler = new DBHandler(ChatActivity.this);
            try {
                particpants_array = response.optJSONArray("participants");
                group_details = response.optJSONObject("groupDetails");

                groupId_loc = group_details.optString("groupId");

                List<GroupParticiapntsModel> models = new ArrayList<>();

                if (dbHandler.DoesChatExist(groupId_loc)) {
                    models = dbHandler.GetPartiFromGrp(groupId_loc);

                    validatelocalvalues(models, particpants_array);
                    validateincomingvalues(particpants_array);
                } else {
                    for (int i = 0; i < particpants_array.length(); i++) {
                        String id = particpants_array.optJSONObject(i).optString("participantId");
                        String grp_id = particpants_array.optJSONObject(i).optString("groupId");
                        String isAdmin = particpants_array.optJSONObject(i).optString("isAdmin");
                        if (isAdmin.equalsIgnoreCase("")) {
                            isAdmin = "0";
                        }

                        dbHandler.AddGroupParticipants(new GroupParticiapntsModel(id, grp_id, particpants_array.optJSONObject(i).optString("joinedAt"), particpants_array.optJSONObject(i).optString("addedBy"), isAdmin));
                        dbHandler.InsertGroup_details(grp_id, group_details.optString("createdBy"), group_details.optString("createdAt"), group_details.optString("image"), group_details.optString("name"));
                    }
                }
            } catch (NullPointerException e) {

            }

            Log.d("onTaskCompleted: ", "" + response);

        }
    }

    private class getChanneldetails extends AsyncTask<String, String, String> implements AsyncTaskCompleteListener {
        String result = "";

        @Override
        protected String doInBackground(String... params) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("channelId", groupId_loc);

                if (internet) {
                    new PostHelper(Const.Methods.CHANNEL_PARTICIPANTS_DETAILS, jsonObject.toString(), Const.ServiceCode.CHANNEL_PARTICIPANTS_DETAILS, ChatActivity.this, this);
                } else {
                    result = "yes";
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }


        @Override
        public void onTaskCompleted(JSONObject response, int serviceCode) {

            DBHandler dbHandler = new DBHandler(ChatActivity.this);
            try {
                particpants_array = response.optJSONArray("participants");
                group_details = response.optJSONObject("channelDetails");

                groupId_loc = group_details.optString("channelId");

                List<ChannelParticiapntsModel> models = new ArrayList<>();

                if (dbHandler.DoesChatExist(groupId_loc)) {
                    models = dbHandler.GetPartiFromChannel(groupId_loc);

                    validatelocalcvalues(models, particpants_array);
                    validateincomingvalueschannel(particpants_array);
                } else {
                    for (int i = 0; i < particpants_array.length(); i++) {
                        String id = particpants_array.optJSONObject(i).optString("participantId");
                        String grp_id = particpants_array.optJSONObject(i).optString("groupId");
                        String isAdmin = particpants_array.optJSONObject(i).optString("isAdmin");
                        if (isAdmin.equalsIgnoreCase("")) {
                            isAdmin = "0";
                        }

                        dbHandler.AddChannelParticipants(new ChannelParticiapntsModel(id, grp_id, particpants_array.optJSONObject(i).optString("joinedAt"), particpants_array.optJSONObject(i).optString("addedBy"), isAdmin));
//                        dbHandler.InsertGroup_details(grp_id, group_details.optString("createdBy"), group_details.optString("createdAt"));
                    }
                }
            } catch (NullPointerException e) {

            }

            Log.d("onTaskCompleted: ", "" + response);

        }
    }

    private class getChatMessages extends AsyncTask<String, String, String> implements AsyncTaskCompleteListener {


        public getChatMessages() {

        }

        @Override
        protected String doInBackground(String... params) {

            DBHandler dbHandler = new DBHandler(context);
            jsonArray = dbHandler.GetLimitChatsMessages(zoeChatID, "", "");
            return "";

        }


        @Override
        public void onTaskCompleted(JSONObject response, int serviceCode) {

        }
    }

    private class addDBMul extends AsyncTask<String, String, String> implements AsyncTaskCompleteListener {
        String result = "";
        String content;
        String contenttype;
        String contentstatus;
        String sender;
        String groupID;
        String senttime;
        String delieverdtime;
        String seentime;
        int caption;
        int isUploaded;
        int isDownloaded;
        String lat;
        String longi;
        String cName;
        String cNumber;
        String chatroomtype;
        String isStarred;
        String medialinks;

        public addDBMul(String content, String contenttype, String contentstatus, String sender, String groupID, String senttime, String delieverdtime, String seentime, int caption, int isUploaded, int isDownloaded, String lat, String longi, String cName, String cNumber, String chatroomtype, String isStarred, String medialinks) {

            this.content = content;
            this.contenttype = contenttype;
            this.contentstatus = contentstatus;
            this.sender = sender;
            this.groupID = groupID;
            this.seentime = senttime;
            this.delieverdtime = delieverdtime;
            this.seentime = seentime;
            this.caption = caption;
            this.isUploaded = isUploaded;
            this.isDownloaded = isDownloaded;
            this.lat = lat;
            this.longi = longi;
            this.cName = cName;
            this.cNumber = cNumber;
            this.chatroomtype = chatroomtype;
            this.isStarred = isStarred;
            this.medialinks = medialinks;
        }

        @Override
        protected String doInBackground(String... params) {

            DBHandler dbHandler = new DBHandler(context);
            for (int i = 0; i < participan_model.size(); i++) {
                String uniqueID = UUID.randomUUID().toString();
                GroupParticiapntsModel model = participan_model.get(i);
                dbHandler.InsertChatMsg(new ChatsMessagesModel(uniqueID.trim(), model.getUser_id(), content, contenttype, contentstatus, sender, groupID, seentime, delievered_time, senttime, caption, isUploaded, isDownloaded, lat, longi, cName, cNumber, "0", isStarred, medialinks, "", 0, "", "", "", ""));
            }

            return result;
        }


        @Override
        public void onTaskCompleted(JSONObject response, int serviceCode) {

            Log.d("onTaskCompleted: ", "" + response);

        }
    }


}
