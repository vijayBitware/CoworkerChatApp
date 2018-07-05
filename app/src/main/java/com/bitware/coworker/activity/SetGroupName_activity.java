package com.bitware.coworker.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.bitware.coworker.baseUtils.AsyncTaskCompleteListener;
import com.bitware.coworker.baseUtils.Const;
import com.bitware.coworker.baseUtils.MyCommon;
import com.bitware.coworker.baseUtils.PostHelper;
import com.bitware.coworker.baseUtils.SharedHelper;
import com.bitware.coworker.baseUtils.Utils;
import com.bitware.coworker.fragment.ChatFragment;
import com.bitware.coworker.models.GroupParticiapntsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.IO;
import io.socket.client.Socket;

public class SetGroupName_activity extends AppCompatActivity implements AsyncTaskCompleteListener {

    public static String cImage, cName, cId, cType, cDescription;
    public static Socket socket;
    public static String filePath = "", select_list = "";
    public LinearLayout channel;
    File file;
    private ImageView create_group;
    private TextView no_participant, dmy_text;
    private RecyclerView parti_list;
    private PartiListAdapter partiListAdapter;
    private CircleImageView groupImage;
    private Uri uri;
    private EditText group_subject, description;
    private ProgressBar progressBar;

    private DBHandler dbHandler;
//    private Emitter.Listener onConnect = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            /*ChatActivity.this.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {*/
//            try {
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("id", SharedHelper.getKey(getApplicationContext(), "id"));
//                Log.e("connect", "online" + jsonObject);
//                socket.emit("online", jsonObject);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//             /*   }
//            });*/
//
//        }
//    };
    public static String groupId_loc;

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
        setContentView(R.layout.activity_set_group_name_activity);
        IO.Options opts = new IO.Options();
        opts.forceNew = true;
        opts.reconnection = false;
        try {
            socket = IO.socket(Const.chatSocketURL, opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.e("SOCKET.IO ", e.getMessage());
        }
//        socket.connect();
        JSONObject jsonObjectt = new JSONObject();
        try {
            jsonObjectt.put("id", SharedHelper.getKey(getApplicationContext(), "id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        socket.emit("online", jsonObjectt);

        create_group = (ImageView) findViewById(R.id.create_group);
        channel = (LinearLayout) findViewById(R.id.channel_text);

        customView(create_group, getPrimaryCOlor(SetGroupName_activity.this), getPrimaryCOlor(SetGroupName_activity.this));
        groupImage = (CircleImageView) findViewById(R.id.group_iamge);
        description = (EditText) findViewById(R.id.description);
        parti_list = (RecyclerView) findViewById(R.id.participant_list);
        no_participant = (TextView) findViewById(R.id.number_of_participant);
        group_subject = (EditText) findViewById(R.id.group_name);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        dmy_text = (TextView) findViewById(R.id.dmy_text);
        progressBar.setVisibility(View.GONE);


        dbHandler = new DBHandler(SetGroupName_activity.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.group_toolbar);
        toolbar.setBackgroundColor(getPrimaryCOlor(SetGroupName_activity.this));
        toolbar.setTitle("New Group");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitle("Add subject");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (NewGroup_activity.isChannel) {
            dmy_text.setVisibility(View.GONE);
            channel.setVisibility(View.VISIBLE);
            group_subject.setHint(getResources().getString(R.string.channel_name));
        }


        Intent intent = getIntent();
        if (intent != null) {
            String list = intent.getStringExtra("part_list");
            select_list = intent.getStringExtra("select_list");
            Log.d("list", "" + list);
            Log.d("select_list", "" + select_list);

            try {
                JSONArray jsonObject = new JSONArray(list);
                no_participant.setText(jsonObject.length() + " " + "Participants");
                partiListAdapter = new PartiListAdapter(SetGroupName_activity.this, jsonObject);
                parti_list.setLayoutManager(new LinearLayoutManager(SetGroupName_activity.this));
                parti_list.setAdapter(partiListAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        groupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        create_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!Utils.isNetworkAvailable(SetGroupName_activity.this)) {
                    Utils.showShortToast(getResources().getString(R.string.no_internet), SetGroupName_activity.this);
                    return;
                }
                if (group_subject.getText().toString().toString().matches("")) {
                    Utils.showShortToast("Enter group name", getApplicationContext());

                } else if (!filePath.isEmpty()) {
                    file = new File(filePath);
//                    AmazonS3Client s3Client = new AmazonS3Client(new BasicAWSCredentials(Const.accessKey, Const.secretKey));
//                    s3Client.setRegion(Const.Regionss3);
//                    s3Client.setEndpoint(Const.Endpoint);

                    System.setProperty(SDKGlobalConfiguration.ENFORCE_S3_SIGV4_SYSTEM_PROPERTY, "true");
                    CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(SetGroupName_activity.this, Const.IDENTIY_POOL_ID, Const.cognitoRegion);
                    AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider);
                    s3Client.setEndpoint(Const.ENDPOINT);


                    TransferUtility transferUtility = new TransferUtility(s3Client, SetGroupName_activity.this);

                    TransferObserver transferObserver = transferUtility.upload(Const.bucket_name, file.getName(), file);
                    transferObserver.setTransferListener(new TransferListener() {
                        @Override
                        public void onStateChanged(int id, TransferState state) {
                            Log.e("image_state", state.toString());
                            if (state.toString().equalsIgnoreCase("IN_PROGRESS")) {
                                progressBar.setVisibility(View.VISIBLE);
                            } else if (state.toString().equalsIgnoreCase("COMPLETED")) {

                                if (NewGroup_activity.isChannel) {

                                    cImage = Const.amazons3ServerImagePath + file.getName();
                                    cDescription = description.getText().toString();
                                    cName = group_subject.getText().toString().trim();
                                    progressBar.setVisibility(View.GONE);
                                    Intent channel = new Intent(SetGroupName_activity.this, ChannelSetings.class);
                                    startActivity(channel);

                                } else {
                                    creteGroup(file.getName());
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
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                } else {
                    if (NewGroup_activity.isChannel) {
                        if (file != null) {
                            cImage = Const.amazons3ServerImagePath + file.getName();

                        }
                        cImage = "";

                        cDescription = description.getText().toString();
                        cName = group_subject.getText().toString().trim();

                        progressBar.setVisibility(View.GONE);
                        Intent channel = new Intent(SetGroupName_activity.this, ChannelSetings.class);
                        startActivity(channel);
                    } else {
                        creteGroup("");
                    }
                    //new UploadImage(InitProfile_activity.this, file.getName(), file, this, Const.ServiceCode.REGISTER);
                }
            }
        });


    }

    private void creteGroup(final String s) {

        try {

            //Utils.showSimpleProgressDialog(SetGroupName_activity.this, "Please wait...", true);
            JSONObject jsonObject = new JSONObject();
            JSONObject my_id = new JSONObject();
            JSONArray post_select_array = new JSONArray(select_list);
            jsonObject.put("from", SharedHelper.getKey(SetGroupName_activity.this, "id"));
            jsonObject.put("groupName", group_subject.getText().toString().trim());
            if (!s.isEmpty()) {
                jsonObject.put("groupImage", Const.amazons3ServerImagePath + s);
            } else {
                jsonObject.put("groupImage", " ");
            }
            String group_id = UUID.randomUUID().toString();
            jsonObject.put("groupId", group_id);
            my_id.put("participantId", SharedHelper.getKey(SetGroupName_activity.this, "id"));
            my_id.put("isAdmin", "1");
            post_select_array.put(my_id);
            jsonObject.put("participants", post_select_array.toString());
            Utils.appLog("register_post", jsonObject.toString());
            Utils.appLog("post_select_array", "" + post_select_array);

            Log.d("createGroup", "" + jsonObject);


            ServiceClasss.Emitters emitters = new ServiceClasss.Emitters(SetGroupName_activity.this);
            emitters.createGroup(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public static void createGrouptrigger(Context context, String groupId)
    {
        new getGrpdetails(context).execute();
        filePath="";
        ChatFragment.mainActivity.refresh();
        groupId_loc=groupId;
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

    }

    private void showPictureDialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Choose your option");
        String[] items = {"Gallery", "Camera"};

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
        //String register_id = code + number;
        File file = new File(Environment.getExternalStorageDirectory(),
                (Calendar.getInstance().getTimeInMillis() + ".jpg"));
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
                        Utils.showShortToast("Unable to select image", getApplicationContext());
                    }
                }
                break;
            case 2:
                if (uri != null) {
                    beginCrop(uri);
                } else {
                    Utils.showShortToast("Unable to select image", getApplicationContext());
                }
                break;
            case Crop.REQUEST_CROP:

                if (data != null)
                    handleCrop(resultCode, data);
                break;
        }
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
            Picasso.with(this).load(Crop.getOutput(result)).into(groupImage);
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
    public void onTaskCompleted(JSONObject response, int serviceCode) {
        Utils.removeProgressDialog();
        Log.d("response", "" + response);
        if (response.optString("error").equalsIgnoreCase("false")) {

        } else {
            Utils.showLongToast(response.optString("message"), SetGroupName_activity.this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        socket.on(Socket.EVENT_CONNECT, onConnect);
//        socket.connect();
        MyCommon.getInstance().setGroupCreateName = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("onPause: ", "socket_stopped");
        MyCommon.getInstance().setGroupCreateName = false;
//        socket.disconnect();
        // SocketSingleton.getInstance().disconnectSocket();
    }

    private static class getGrpdetails extends AsyncTask<String, String, String> implements AsyncTaskCompleteListener {
        String result = "";
        Context context;
        private JSONArray particpants_array = new JSONArray();
        private JSONObject group_details = new JSONObject();

        public getGrpdetails(Context context)
        {
        this.context=context;
        }

        @Override
        protected String doInBackground(String... params) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("groupId", groupId_loc);

                new PostHelper(Const.Methods.PARTICIPANTS_DETAILS, jsonObject.toString(), Const.ServiceCode.PARTICIPANTS_DETAILS, context, this);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }


        @Override
        public void onTaskCompleted(JSONObject response, int serviceCode) {

            DBHandler dbHandler = new DBHandler(context);
            particpants_array = response.optJSONArray("participants");
            group_details = response.optJSONObject("groupDetails");


            List<GroupParticiapntsModel> models = new ArrayList<>();

            if (dbHandler.DoesChatExist(groupId_loc)) {
                models = dbHandler.GetPartiFromGrp(groupId_loc);

                validatelocalvalues(models, particpants_array,context);
                validateincomingvalues(particpants_array,context);
            } else {
                for (int i = 0; i < particpants_array.length(); i++) {
                    String id = particpants_array.optJSONObject(i).optString("participantId");
                    String grp_id = particpants_array.optJSONObject(i).optString("groupId");
                    String isAdmin = particpants_array.optJSONObject(i).optString("isAdmin");
                    if (isAdmin.equalsIgnoreCase("")) {
                        isAdmin = "0";
                    }


                    dbHandler.AddGroupParticipants(new GroupParticiapntsModel(id, grp_id, particpants_array.optJSONObject(i).optString("joinedAt"), particpants_array.optJSONObject(i).optString("addedBy"), isAdmin));

                }
            }


            Log.d("onTaskCompleted: ", "" + response);

        }
    }


    private static void validateincomingvalues(JSONArray particpants_array, Context context) {
        DBHandler dbHandler = new DBHandler(context);
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

    private static void validatelocalvalues(List<GroupParticiapntsModel> models, JSONArray particpants_array, Context context) {
        DBHandler dbHandler = new DBHandler(context);
        for (int i = 0; i < models.size(); i++) {
            GroupParticiapntsModel values = models.get(i);
            String id = values.getUser_id();
            Boolean exist = value(particpants_array, id);
            if (!exist) {
                dbHandler.DeleteGroupParticipants(id, groupId_loc);
            }
        }

    }

    private static boolean value(JSONArray jsonArray, String part_id) {
        return jsonArray.toString().contains("\"participantId\":\"" + part_id + "\"");
    }

    private class PartiListAdapter extends RecyclerView.Adapter<PartiListAdapter.MyViewHolder> {
        private JSONArray array;
        private Context mContext;

        public PartiListAdapter(SetGroupName_activity setGroupName_activity, JSONArray jsonObject) {
            this.array = jsonObject;
            this.mContext = setGroupName_activity;
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.contact_list_item, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            if (array.optJSONObject(position).optString("image").equalsIgnoreCase("")) {
                Picasso.with(mContext).load(R.drawable.ic_account_circle)
                        .error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.user_image);
            } else {
                Picasso.with(mContext).load(array.optJSONObject(position).optString("image")).into(holder.user_image);
            }


            holder.user_name.setText(array.optJSONObject(position).optString("name"));
            holder.user_status.setText(array.optJSONObject(position).optString("status"));
        }

        @Override
        public int getItemCount() {
            return array.length();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            CircleImageView user_image;
            TextView user_name, user_status;

            public MyViewHolder(View itemView) {
                super(itemView);
                user_image = (CircleImageView) itemView.findViewById(R.id.contact_image);
                user_name = (TextView) itemView.findViewById(R.id.user_name);
                user_status = (TextView) itemView.findViewById(R.id.user_status);
            }
        }
    }


}
