package com.bitware.coworker.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;
import com.bitware.coworker.DBHelper.DBHandler;
import com.bitware.coworker.R;
import com.bitware.coworker.Service.ServiceClasss;
import com.bitware.coworker.baseUtils.Const;
import com.bitware.coworker.baseUtils.MyCommon;
import com.bitware.coworker.baseUtils.SharedHelper;
import com.bitware.coworker.baseUtils.Utils;
import com.bitware.coworker.fragment.ChatFragment;
import com.bitware.coworker.models.ChatMessages;
import com.bitware.coworker.models.ChatType;
import com.bitware.coworker.models.ChatsMessagesModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.IO;
import io.socket.client.Socket;


import static com.bitware.coworker.models.Status.SENDING;

public class ChannelEdit extends AppCompatActivity {
    public static Socket socket;
    public static String group_id;
    public Boolean imgChng = false;
    String c_name, c_desc, c_type, c_link, c_sign, c_image;
    CircleImageView group_iamge;
    DBHandler dbHandler;
    ProgressBar progress_bar;
    EditText cname, cdesc, ctype;
    Switch sign;
    LinearLayout delete_channel;
    RelativeLayout group_lay;
    private Uri uri;
    public static String filePath = "";
    private Context context = ChannelEdit.this;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String themevalue = SharedHelper.getKey(this, "theme_value");
        Setheme(themevalue);
        setContentView(R.layout.activity_channel_edit);
        IO.Options opts = new IO.Options();
        opts.forceNew = true;
        opts.reconnection = false;

        try {
            socket = IO.socket(Const.chatSocketURL, opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.e("SOCKET.IO ", e.getMessage());
        }

        dbHandler = new DBHandler(ChannelEdit.this);
        Intent intent = getIntent();
        group_iamge = (CircleImageView) findViewById(R.id.group_iamge);
        group_id = intent.getStringExtra("group_id");
        c_name = dbHandler.GetGroupName(group_id);
        delete_channel = (LinearLayout) findViewById(R.id.delete_channel);
        c_image = dbHandler.GetGroupImage(group_id);
        c_desc = dbHandler.GetGroupDesc(group_id);
        c_link = dbHandler.GetChannelLink(group_id);
        c_type = dbHandler.GetChannelType(group_id);
        c_sign = dbHandler.GetSign(group_id);
        sign = (Switch) findViewById(R.id.sign_switch);

        if (c_image.equalsIgnoreCase("") || c_image.equalsIgnoreCase(" ")) {
            Picasso.with(context).load(R.drawable.ic_channel).placeholder(context.getResources().getDrawable(R.drawable.ic_channel)).error(context.getResources().getDrawable(R.drawable.ic_channel)).into(group_iamge);
        } else {
            Picasso.with(context).load(c_image).placeholder(context.getResources().getDrawable(R.drawable.ic_channel)).error(context.getResources().getDrawable(R.drawable.ic_channel)).into(group_iamge);
        }
        group_lay = (RelativeLayout) findViewById(R.id.group_lay);

        group_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });
        if (c_sign.equalsIgnoreCase("true")) {
            sign.setChecked(true);
        } else {
            sign.setChecked(false);
        }

        cname = (EditText) findViewById(R.id.channel_name);
        cdesc = (EditText) findViewById(R.id.channel_description);
        cname.setText(c_name);
        cdesc.setText(c_desc);


        Toolbar toolbar = (Toolbar) findViewById(R.id.group_toolbar);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        progress_bar.setVisibility(View.GONE);
        toolbar.setBackgroundColor(getPrimaryCOlor(ChannelEdit.this));
        toolbar.setTitle(getResources().getString(R.string.edit));

        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        delete_channel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    exitchannel();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void exitchannel() throws JSONException {

        JSONObject object = new JSONObject();
        object.put("from", MainActivity.my_id);
        object.put("channelId", group_id);

        ServiceClasss.Emitters emitters = new ServiceClasss.Emitters(context);
        Boolean res = emitters.exitChannel(object,0);
        if (res) {

            dbHandler.DeleteChatsGrpComplete(group_id);
            dbHandler.DeleteChats(group_id);
            Intent intent = new Intent(ChannelEdit.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }


    }
    public static void exitchanneltrigger0(Context context)
    {
        DBHandler dbHandler=new DBHandler(context);

        dbHandler.DeleteChatsGrpComplete(group_id);
        dbHandler.DeleteChats(group_id);
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();


        MyCommon.getInstance().mainActivity = true;
//        socket.on(Socket.EVENT_CONNECT, onConnect);
//        socket.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        socket.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.tick_menu, menu);
        return true;
    }


    private void showPictureDialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getResources().getString(R.string.choose_your_option));
        String[] items = {getResources().getString(R.string.gallery), getResources().getString(R.string.camera_small)};

        dialog.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                switch (which) {
                    case 0:
                        choosePhotoFromGallary();
                        break;
                    case 1:
                        takePhotoFromCamera();
                        break;

                }
            }
        });
        dialog.show();
    }

    private void choosePhotoFromGallary() {
        Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 1);

    }

    private void takePhotoFromCamera() {
        String register_id = SharedHelper.getKey(ChannelEdit.this, "id");
        File file = new File(Environment.getExternalStorageDirectory(), register_id + ".jpg");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {

            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        uri = Uri.fromFile(file);
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(i, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Activity_Res", "" + requestCode);
        switch (requestCode) {
            case 1:
                if (data != null) {

                    uri = data.getData();
                    if (uri != null) {
                        beginCrop(uri);

                    } else {
                        Utils.showShortToast(getResources().getString(R.string.unable_to), getApplicationContext());

                    }
                }
                break;
            case 2:
                if (uri != null) {
                    beginCrop(uri);

                } else {
                    Utils.showShortToast(getResources().getString(R.string.unable_to), getApplicationContext());

                }
                break;
            case Crop.REQUEST_CROP:

                if (data != null)
                    handleCrop(resultCode, data);

                break;
        }
    }

    private void uploadImage() {
        if (!filePath.isEmpty()) {
            final File file = new File(filePath);
//            AmazonS3Client s3Client = new AmazonS3Client(new BasicAWSCredentials(Const.accessKey, Const.secretKey));
//            s3Client.setRegion(Const.Regionss3);
//            s3Client.setEndpoint(Const.Endpoint);

            System.setProperty(SDKGlobalConfiguration.ENFORCE_S3_SIGV4_SYSTEM_PROPERTY, "true");
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(ChannelEdit.this, Const.IDENTIY_POOL_ID, Const.cognitoRegion);
            AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider);
            s3Client.setEndpoint(Const.ENDPOINT);


            TransferUtility transferUtility = new TransferUtility(s3Client, ChannelEdit.this);

            TransferObserver transferObserver = transferUtility.upload(Const.bucket_name, file.getName(), file);
            transferObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    Log.e("image_state", state.toString());
                    if (state.toString().equalsIgnoreCase("IN_PROGRESS")) {
                        progress_bar.setVisibility(View.VISIBLE);
                    } else if (state.toString().equalsIgnoreCase("COMPLETED")) {
                        progress_bar.setVisibility(View.GONE);
                        imgChng = true;

                        //uploadStatus = true;
                        try {
                            upDate(1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    int percentage = (int) (bytesCurrent / bytesTotal * 100);
                    Log.e("image_state_percentage", String.valueOf(percentage));
                }

                @Override
                public void onError(int id, Exception ex) {
                    Log.e("image_state_error", ex.getMessage());
                    progress_bar.setVisibility(View.GONE);
                }
            });
        } else {
            try {
                upDate(2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void upDate(int i) throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        final JSONObject edited = new JSONObject();
        final File file = new File(filePath);
        final String from = SharedHelper.getKey(ChannelEdit.this, "my_zoe_id");
        final String valuess = Const.amazons3ServerImagePath + file.getName();
        /////////////////
        jsonObject.put("channelName", cname.getText().toString());
        if (!cname.getText().toString().equalsIgnoreCase(c_name)) {
            edited.put("channelName", cname.getText().toString());
        } else {
            edited.put("channelName", "false");
        }

        ///////////////
        jsonObject.put("channelDescription", cdesc.getText().toString());

        if (!cdesc.getText().toString().equalsIgnoreCase(c_desc)) {
            edited.put("channelDescription", cdesc.getText().toString());
        } else {
            edited.put("channelDescription", "false");
        }

        ///////////////////
        if (i == 1) {
            jsonObject.put("channelImage", Const.amazons3ServerImagePath + file.getName());

        } else {
            jsonObject.put("channelImage", c_image);

        }
        if (!jsonObject.optString("channelImage").equalsIgnoreCase(c_image)) {
            edited.put("channelImage", jsonObject.optString("channelImage"));
        } else {
            edited.put("channelImage", "false");
        }

        ////////////////
        if (sign.isChecked()) {
            jsonObject.put("signAdmin", "true");
        } else {
            jsonObject.put("signAdmin", "false");

        }
        jsonObject.put("channelId", group_id);
        jsonObject.put("editedFields", edited.toString());

        ServiceClasss.Emitters emitters = new ServiceClasss.Emitters(context);
        Boolean res = emitters.editChannel(jsonObject);


    }

    public static void editchanneltrigger(JSONObject jsonObject,Context context)
    {
        DBHandler dbHandler=new DBHandler(context);
        long millis = System.currentTimeMillis();
        File file = new File(filePath);
        final String from = SharedHelper.getKey(context, "my_zoe_id");

        String las_msg = "";
        try {
            JSONObject ed_obj = new JSONObject(jsonObject.optString("editedFields"));
            String image = ed_obj.optString("channelImage");
            String name = ed_obj.optString("channelName");
            String descrip = ed_obj.optString("channelDescription");

            if (!image.equalsIgnoreCase("false")) {
                dbHandler.GrpImageUpdate(Const.amazons3ServerImagePath + file.getName(), group_id);
                las_msg = context.getResources().getString(R.string.channel_image_changed);
                String uniqueID = UUID.randomUUID().toString();
                dbHandler.InsertChatMsg(new ChatsMessagesModel(uniqueID.trim(), SharedHelper.getKey(context, "id"), las_msg, ChatType.editGroupName.toString(), SENDING.toString(), ChatMessages.CREATE_GROUP, group_id, "" + millis, "0", "0", 0, 0, 0, "0.0", "0.0", "", "", "1", "false", "", "", 0, "", "", "", ""));

            }
            if (!name.equalsIgnoreCase("false")) {
                dbHandler.GroupNameUpdate(name, group_id);

                las_msg =context.getResources().getString(R.string.channel_name_changed_to)  + name;
                String uniqueID = UUID.randomUUID().toString();
                dbHandler.InsertChatMsg(new ChatsMessagesModel(uniqueID.trim(), SharedHelper.getKey(context, "id"), las_msg, ChatType.editGroupName.toString(), SENDING.toString(), ChatMessages.CREATE_GROUP, group_id, "" + millis, "0", "0", 0, 0, 0, "0.0", "0.0", "", "", "1", "false", "", "", 0, "", "", "", ""));

            }
            if (!descrip.equalsIgnoreCase("false")) {
                dbHandler.GroupDescUpdate(descrip, group_id);
                las_msg = context.getResources().getString(R.string.channel_description_changed_to) + descrip;
                String uniqueID = UUID.randomUUID().toString();
                dbHandler.InsertChatMsg(new ChatsMessagesModel(uniqueID.trim(), SharedHelper.getKey(context, "id"), las_msg, ChatType.editGroupName.toString(), SENDING.toString(), ChatMessages.CREATE_GROUP, group_id, "" + millis, "0", "0", 0, 0, 0, "0.0", "0.0", "", "", "1", "false", "", "", 0, "", "", "", ""));


            }
            dbHandler.GroupSignUpdate(jsonObject.optString("signAdmin"), group_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        dbHandler.UpdateLastMsg(ChatActivity.groupId_loc, from, las_msg, "", String.valueOf(millis), ChatMessages.CREATE_GROUP, MainActivity.unreadCount);

        ChatFragment.refreshLay();
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    private void beginCrop(Uri source) {
        //String register_id = code + number;
        Uri outputUri = Uri.fromFile(new File(Environment
                .getExternalStorageDirectory(), Calendar.getInstance().getTimeInMillis() + ".jpg"));
        Crop.of(source, outputUri).asSquare().start(this);
    }


    private void handleCrop(int resultCode, Intent result) {

        if (resultCode == RESULT_OK) {
            filePath = getRealPathFromURI(Crop.getOutput(result));
            //profileImage.setImageURI(Crop.getOutput(result));
            Log.e("image_path", "" + filePath);
            Picasso.with(this).load(Crop.getOutput(result)).into(group_iamge);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Utils.showShortToast("Unable to select image", getApplicationContext());
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null,
                null, null, null);

        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor
                    .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                if (cname.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(ChannelEdit.this, getResources().getString(R.string.empty_name), Toast.LENGTH_SHORT).show();
                } else {
                    uploadImage();
                }

                break;
            default:
                return false;
        }
        return true;
    }
}