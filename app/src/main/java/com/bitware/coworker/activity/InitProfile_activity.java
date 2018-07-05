package com.bitware.coworker.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import android.widget.TextView;
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
import com.bitware.coworker.R;
import com.bitware.coworker.baseUtils.AsyncTaskCompleteListener;
import com.bitware.coworker.baseUtils.Const;
import com.bitware.coworker.baseUtils.PostHelper;
import com.bitware.coworker.baseUtils.SharedHelper;
import com.bitware.coworker.baseUtils.Utils;
import com.bitware.coworker.models.ContactsModel;
import com.bitware.coworker.models.GetUserFromDBModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class InitProfile_activity extends AppCompatActivity implements
        View.OnClickListener, AsyncTaskCompleteListener {

    Boolean checkagain = false;
    ImageView emojiButton;
    ViewGroup rootView;
    private Uri uri;
    private String filePath = "";
    private CircleImageView pro_image;
    private ProgressBar progressBar;
    private String number = "", code = "";
    private EmojiEditText user_name;
    private EmojiPopup emojiPopup;
    private long final_percentage;
    private List<GetUserFromDBModel> contact_list;
    private Dialog dialog;
    private TextView percentage_text;

    public static boolean insertContact(ContentResolver contactAdder, String firstName, String mobileNumber) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI).withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null).withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE).withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, firstName).build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE).withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mobileNumber).withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build());
        try {
            contactAdder.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            Log.d("insertContact: ", "error" + e);
            return false;
        }
        return true;
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
        setContentView(R.layout.activity_init_profile_activity);
        rootView = (ViewGroup) findViewById(R.id.activity_init_profile_activity);
        pro_image = (CircleImageView) findViewById(R.id.profile_image);
        pro_image.setOnClickListener(this);
        emojiButton = (ImageView) findViewById(R.id.emojiButton);

        emojiButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_mood));


        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emojiPopup.toggle();
            }
        });

        Utils.enableStrictMode();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            getPermission();
        }

        //if (Utils.buildVersion()) {
        pro_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_circle));
        /*} else {
            pro_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_circle_icon));
        }*/

        final Intent intent = getIntent();
        if (intent != null) {
            number = intent.getStringExtra("number");
            code = intent.getStringExtra("code");
        }

        progressBar = (ProgressBar) findViewById(R.id.profile_image_progress);
        progressBar.setVisibility(View.GONE);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        user_name = (EmojiEditText) findViewById(R.id.username);
        setUpEmojiPopup();

        findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registernow();
            }
        });

    }

    private void registernow() {
        if (!Utils.isNetworkAvailable(InitProfile_activity.this)) {
            Utils.showShortToast(getResources().getString(R.string.no_internet), InitProfile_activity.this);
            return;
        }
        if (user_name.getText().toString().matches("")) {
            Utils.showShortToast(getResources().getString(R.string.enter_you_name), getApplicationContext());

        } else if (!filePath.isEmpty()) {
            final File file = new File(filePath);
//            AmazonS3Client s3Client = new AmazonS3Client(new BasicAWSCredentials(Const.accessKey, Const.secretKey));
//            s3Client.setRegion(Const.Regionss3);
//            s3Client.setEndpoint(Const.Endpoint);

            System.setProperty(SDKGlobalConfiguration.ENFORCE_S3_SIGV4_SYSTEM_PROPERTY, "true");
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(InitProfile_activity.this, Const.IDENTIY_POOL_ID, Const.cognitoRegion);
            AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider);
            s3Client.setEndpoint(Const.ENDPOINT);


            TransferUtility transferUtility = new TransferUtility(s3Client, InitProfile_activity.this);

            TransferObserver transferObserver = transferUtility.upload(Const.bucket_name, file.getName(), file);
            transferObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    Log.e("image_state", state.toString());
                    if (state.toString().equalsIgnoreCase("IN_PROGRESS")) {
                        progressBar.setVisibility(View.VISIBLE);
                    } else if (state.toString().equalsIgnoreCase("COMPLETED")) {
                        progressBar.setVisibility(View.GONE);

                        registerProfile(file.getName());
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
            registerProfile("");
            //new UploadImage(InitProfile_activity.this, file.getName(), file, this, Const.ServiceCode.REGISTER);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.profile_image:
                showPictureDialog();
                break;
        }
    }

    private void setpercentage(int curr_val, int second_val, int last_per) {
        Long percentage = (long) ((float) curr_val / second_val * 100);
        Long initpercentage = (long) ((float) percentage / 100 * 30);
        final_percentage = last_per + initpercentage;
        try {
            if (dialog.isShowing()) {
                percentage_text.setText("" + final_percentage + "%");
            }
        }
        catch (Exception e)
        {

        }
    }


    public void showdialog()
    {
        dialog = new Dialog(InitProfile_activity.this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setContentView(R.layout.syncing_dialog);
        percentage_text=(TextView)dialog.findViewById(R.id.percentage);
        percentage_text.setText(""+final_percentage+"%");

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        dialog.show();

    }
    public void dismiss()
    {
        if(dialog.isShowing())
        {
            dialog.dismiss();
            movetomain();

        }
    }
    private class readAllContacts extends AsyncTask<String, Integer, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showdialog();

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                DBHandler dbHandler = new DBHandler(InitProfile_activity.this);
                ContentResolver cr = getContentResolver();
                Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                        null, null, null, null);


                if (cur.getCount() > 0) {
                    while (cur.moveToNext()) {
                        String id = cur.getString(
                                cur.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cur.getString(cur.getColumnIndex(
                                ContactsContract.Contacts.DISPLAY_NAME));

                        if (cur.getInt(cur.getColumnIndex(
                                ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {

                            publishProgress(cur.getPosition(), cur.getCount());

                            Cursor pCur = cr.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    new String[]{id}, null);

                            while (pCur.moveToNext()) {
                                String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                String log = "Name: " + name + ", Phone No: " + phoneNo;
                                //Log.e("log", "" + log);
                                String rp_phone;
                                if (phoneNo.contains("+")) {
                                    rp_phone = phoneNo.replaceAll("[\\-\\+\\.\\^:,\\s+\\(\\)\\#\\*]", "");
                                } else {
                                    String number = SharedHelper.getKey(InitProfile_activity.this, "country_code_default");
                                    rp_phone = number + phoneNo;
                                    rp_phone = rp_phone.replaceAll("[\\-\\+\\.\\^:,\\s+\\(\\)\\#\\*]", "");

                                }

                                if (rp_phone.length() < 14) {
                                    if (rp_phone.length() > 3) {
                                        try {

                                            String registerNumber = SharedHelper.getKey(InitProfile_activity.this,"my_zoe_id");
                                            registerNumber = registerNumber.substring(registerNumber.length() - 10);
                                            if (!rp_phone.equalsIgnoreCase(registerNumber)) {
                                                if (!dbHandler.CheckIsDataAlreadyInDBorNot(rp_phone)) {
                                                    dbHandler.InsertUser(new ContactsModel(rp_phone, name, "", "", "", 0, "", id, false));
                                                } else {
                                                    dbHandler.UserNameUpdate(name, rp_phone);
                                                }
                                            }
                                        } catch (NullPointerException e) {

                                        }
                                    }
                                }

                            }
                            pCur.close();
                        }
                    }
                }
                cur.close();
                dbHandler.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int curr_val = values[0];
            int second_val = values[1];

            setpercentage(curr_val, second_val, 1);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new getContacts().execute();

        }

    }

    private class getContacts extends AsyncTask<String, Integer, String> implements AsyncTaskCompleteListener {
        String result = "";

        @Override
        protected String doInBackground(String... strings) {
            JSONArray jsonArray = new JSONArray();
            DBHandler dbHandler = new DBHandler(InitProfile_activity.this);
            try {


                contact_list = dbHandler.GetAllUserFromDB();

//                Log.d(TAG, "doInBackground: " + contact_list);


                //Utils.appLog("jsonArray", contact_list.toString());

                for (int i = 0; i < contact_list.size(); i++) {
                    publishProgress(i+1,contact_list.size());
                    JSONObject jsonObject = new JSONObject();
                    try {
                        if (!contact_list.get(i).getMobile().equalsIgnoreCase(SharedHelper.getKey(InitProfile_activity.this,"my_zoe_id"))) {
                            jsonObject.put("mobileNumber", contact_list.get(i).getMobile());
                            jsonObject.put("contactName", contact_list.get(i).getName());
//                            Log.d(TAG, "doInBackground: "+jsonObject);
                            jsonArray.put(jsonObject);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Collections.sort(contact_list, new Comparator<GetUserFromDBModel>() {
                    @Override
                    public int compare(GetUserFromDBModel getUserFromDB, GetUserFromDBModel t1) {
                        return getUserFromDB.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
                    }
                });
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            //Utils.appLog("get_contact_FromDB", jsonArray.toString());
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("contacts", jsonArray.toString());
                jsonObject.put("from", SharedHelper.getKey(InitProfile_activity.this,"my_zoe_id"));
                Log.d("doInBackground: ","input:"+jsonObject);
//                Utils.appLog("Contact_log", jsonArray.toString());
//                if (internet) {
                new PostHelper(Const.Methods.SYNC_CONTACTS, jsonObject.toString(), Const.ServiceCode.SYNC_CONTACTS, InitProfile_activity.this, this);


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int curr_val = values[0];
            int second_val = values[1];

            setpercentage(curr_val, second_val, 31);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }

        @Override
        public void onTaskCompleted(final JSONObject response, int serviceCode) {
            switch (serviceCode) {
                case Const.ServiceCode.SYNC_CONTACTS:
//                    Utils.appLog("Contact_Response", response.toString());
                    if (response != null) {
                        if (response.optString("error").equalsIgnoreCase("false")) {
                            JSONArray jsonArray = response.optJSONArray("contacts");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                setpercentage(i+1,jsonArray.length(),61);
                                JSONObject jsonObject = jsonArray.optJSONObject(i);
                                String mobile_id = jsonObject.optString("mobileNumber");
                                DBHandler dbHandler = new DBHandler(InitProfile_activity.this);
                                try {
                                    if (!dbHandler.CheckIsDataAlreadyInDBorNot(mobile_id)) {
                                        if (jsonObject.optString("showInContactsPage").equalsIgnoreCase("true")) {
                                            dbHandler.InsertUser(new ContactsModel(mobile_id, jsonObject.optString("name"), jsonObject.optString("image"), "", jsonObject.optString("status"), 1, "", jsonObject.optString("contactid"), false));
                                        } else {
                                            if (dbHandler.UserUpdate(mobile_id, jsonObject.optString("name"), jsonObject.optString("image"), "1", jsonObject.optString("zoechatid"), jsonObject.optString("status")) > 0) {
                                            } else {
                                                dbHandler.InsertUser(new ContactsModel(mobile_id, jsonObject.optString("name"), "", "", jsonObject.optString("status"), 0, "", jsonObject.optString("contactid"), false));
                                            }
                                        }

                                    } else if (jsonObject.optString("showInContactsPage").equalsIgnoreCase("true")) {
                                        dbHandler.UserUpdate(mobile_id, jsonObject.optString("name"), jsonObject.optString("image"), "1", jsonObject.optString("zoechatid"), jsonObject.optString("status"));
                                    }
                                    dbHandler.close();
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                            }

                            dismiss();


                        } else {
                            try {

                                InitProfile_activity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(InitProfile_activity.this, response.optString("message"), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (NullPointerException e) {

                            }
                            //new printListOfContacts().execute();
                        }
                    } else {
                        try {
                            InitProfile_activity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(InitProfile_activity.this, response.optString("message"), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (NullPointerException e) {

                        }

                    }

                    break;
            }

        }
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
        }).build(user_name);
    }

    private void showPictureDialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Choose your option");
        String[] items = {"Gallery", "Camera"};

        dialog.setItems(items, new DialogInterface.OnClickListener() {

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                switch (which) {
                    case 0:
                        choosePhotoFromGallary();
                        break;
                    case 1:

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            // Android M Permission check
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                        } else {
                            takePhotoFromCamera();
                        }


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


            Picasso.with(this).load(Crop.getOutput(result)).into(pro_image);

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

    private void registerProfile(String name) {

        if (getPermssiosn()) {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("mobileNumber", number);
                jsonObject.put("countryCode", code);
                jsonObject.put("os", "android");
                jsonObject.put("buildVersion", Const.buildvversion);
                jsonObject.put("name", user_name.getText().toString());
                if (!name.isEmpty()) {
                    jsonObject.put("image", Const.amazons3ServerImagePath + name);
                }
                jsonObject.put("pushNotificationId", SharedHelper.getKey(InitProfile_activity.this, "token"));
                Utils.appLog("register_post", jsonObject.toString());
                new PostHelper(Const.Methods.REGISTER, jsonObject.toString(), Const.ServiceCode.REGISTER, this, this);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            Toast.makeText(this, "You need to give permission to use this app", Toast.LENGTH_SHORT).show();
            getPermission();

        }


    }

    @Override
    public void onTaskCompleted(JSONObject response, int serviceCode) {
        switch (serviceCode) {
            case Const.ServiceCode.REGISTER:
                Utils.appLog("register_result", response.toString());
                if (response.optString("error").equalsIgnoreCase("false")) {
                    insertContact(getContentResolver(), "Support Zoechat", "+34654745647");

                    SharedHelper.putKey(InitProfile_activity.this, "sign", "true");
                    SharedHelper.putKey(InitProfile_activity.this, "id", response.optString("_id"));
                    SharedHelper.putKey(InitProfile_activity.this, "status", response.optString("status"));
                    SharedHelper.putKey(InitProfile_activity.this, "name", response.optString("name"));
                    SharedHelper.putKey(InitProfile_activity.this, "image", response.optString("image"));

                    SharedHelper.putKey(InitProfile_activity.this, "my_zoe_id", response.optString("_id"));
                    new readAllContacts().execute();

                } else {
                    Utils.showShortToast(response.optString("message"), InitProfile_activity.this);
                }
                break;
        }

    }

    private void movetomain() {
        Intent intent = new Intent(InitProfile_activity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        InitProfile_activity.this.finish();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("requestCode", "" + requestCode);
        int a = 0;


        switch (requestCode) {
            case 100:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhotoFromCamera();
                } else {
                    Toast.makeText(this, "You need to give permission to open camera", Toast.LENGTH_SHORT).show();
                }
                break;

            case 10000:

                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    } else {

                    }


                }


            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }


    }


    @SuppressLint("NewApi")
    public Boolean getPermission() {
        Boolean returnw = false;

        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP) {

            String[] permissions_dummy = new String[7];
            int i = 0;


            String permission = Manifest.permission.READ_CONTACTS;
            int res = checkCallingOrSelfPermission(permission);
            if (res != PackageManager.PERMISSION_GRANTED) {

                permissions_dummy[i] = permission;
                i = i + 1;

            }

            permission = Manifest.permission.WRITE_CONTACTS;
            res = checkCallingOrSelfPermission(permission);
            if (res != PackageManager.PERMISSION_GRANTED) {

                permissions_dummy[i] = permission;
                i = i + 1;

            }
            permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            res = checkCallingOrSelfPermission(permission);
            if (res != PackageManager.PERMISSION_GRANTED) {


                permissions_dummy[i] = permission;
                i = i + 1;


            }
            permission = Manifest.permission.RECORD_AUDIO;
            res = checkCallingOrSelfPermission(permission);
            if (res != PackageManager.PERMISSION_GRANTED) {


                permissions_dummy[i] = permission;
                i = i + 1;


            }
            permission = Manifest.permission.CAMERA;
            res = checkCallingOrSelfPermission(permission);
            if (res != PackageManager.PERMISSION_GRANTED) {
                permissions_dummy[i] = permission;
                i = i + 1;
            }

            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
            res = checkCallingOrSelfPermission(permission);
            if (res != PackageManager.PERMISSION_GRANTED) {
                permissions_dummy[i] = permission;
                i = i + 1;
            }



            String[] permissions = new String[i];

            for (int j = 0; j < i; j++) {

                permissions[j] = permissions_dummy[j];

            }
            int yourRequestId = 10000;
            if (i != 0) {
                // Do something for lollipop and above versions
                requestPermissions(permissions, yourRequestId);
                returnw = false;

            } else {
                returnw = true;
            }
        }

        return returnw;

    }


    public boolean getPermssiosn(){
        Boolean returnw = false;

        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP) {

            String[] permissions_dummy = new String[7];
            int i = 0;


            String permission = Manifest.permission.READ_CONTACTS;
            int res = checkCallingOrSelfPermission(permission);
            if (res != PackageManager.PERMISSION_GRANTED) {

                permissions_dummy[i] = permission;
                i = i + 1;

            }

            permission = Manifest.permission.WRITE_CONTACTS;
            res = checkCallingOrSelfPermission(permission);
            if (res != PackageManager.PERMISSION_GRANTED) {

                permissions_dummy[i] = permission;
                i = i + 1;

            }
            permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            res = checkCallingOrSelfPermission(permission);
            if (res != PackageManager.PERMISSION_GRANTED) {


                permissions_dummy[i] = permission;
                i = i + 1;


            }
            permission = Manifest.permission.RECORD_AUDIO;
            res = checkCallingOrSelfPermission(permission);
            if (res != PackageManager.PERMISSION_GRANTED) {


                permissions_dummy[i] = permission;
                i = i + 1;


            }
            permission = Manifest.permission.CAMERA;
            res = checkCallingOrSelfPermission(permission);
            if (res != PackageManager.PERMISSION_GRANTED) {
                permissions_dummy[i] = permission;
                i = i + 1;
            }

            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
            res = checkCallingOrSelfPermission(permission);
            if (res != PackageManager.PERMISSION_GRANTED) {
                permissions_dummy[i] = permission;
                i = i + 1;
            }



            String[] permissions = new String[i];

            for (int j = 0; j < i; j++) {

                permissions[j] = permissions_dummy[j];

            }
            int yourRequestId = 10000;
            if (i != 0) {
                // Do something for lollipop and above versions
//                requestPermissions(permissions, yourRequestId);
                returnw = false;

            } else {
                returnw = true;
            }
        }
        else {
            returnw=true;
        }

        return returnw;

    }


}
