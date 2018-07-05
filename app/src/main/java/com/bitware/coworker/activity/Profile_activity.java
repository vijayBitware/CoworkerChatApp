package com.bitware.coworker.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;
import com.bitware.coworker.DBHelper.DBHandler;
import com.bitware.coworker.R;
import com.bitware.coworker.baseUtils.AsyncTaskCompleteListener;
import com.bitware.coworker.baseUtils.Const;
import com.bitware.coworker.baseUtils.PostHelper;
import com.bitware.coworker.baseUtils.SharedHelper;
import com.bitware.coworker.baseUtils.Utils;
import com.bitware.coworker.models.ContactsModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile_activity extends AppCompatActivity implements AsyncTaskCompleteListener, View.OnClickListener {

    TextView textView;
    private Uri uri;
    private String filePath = "";
    private CircleImageView pro_image;
    private Drawable drawable;

    private ImageView profile_image_edit;
    private DBHandler dbHandler;
    private ContactsModel contactsModel;
    private EditText user_name, user_status;
    private ProgressBar progressBar;
    private List<Integer> allColors = new ArrayList<>();
    private String TAG = Profile_activity.class.getSimpleName();
    ;

    //private boolean uploadStatus = false;
    public static void customView(View v, int backgroundColor, int borderColor) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[]{4, 4, 4, 4, 4, 4, 4, 4});
        shape.setColor(backgroundColor);
        shape.setStroke(3, borderColor);
        v.setBackgroundDrawable(shape);
    }

    public static int getPrimaryCOlor(Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, value, true);
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

//    private List<Integer> getAllMaterialColors() throws IOException, XmlPullParserException {
//        XmlResourceParser xrp = Profile_activity.this.getResources().getXml(R.xml.select_color);
//        List<Integer> allColors = new ArrayList<>();
//        int nextEvent;
//        while ((nextEvent = xrp.next()) != XmlResourceParser.END_DOCUMENT) {
//            String s = xrp.getName();
//            if ("color".equals(s)) {
//                String color = xrp.nextText();
//                allColors.add(Color.parseColor(color));
//            }
//        }
//        return allColors;
//    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String themevalue = SharedHelper.getKey(this, "theme_value");
        Setheme(themevalue);
        setContentView(R.layout.activity_profile_activity);
        textView = (TextView) findViewById(R.id.text_name_per);
        Utils.enableStrictMode();
        pro_image = (CircleImageView) findViewById(R.id.profile_image);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pro_image.setTransitionName("DetailsTransition");
        }


        if (SharedHelper.getKey(Profile_activity.this, "image").equalsIgnoreCase("") || SharedHelper.getKey(Profile_activity.this, "image").equalsIgnoreCase(" ")) {
            Picasso.with(Profile_activity.this).load(R.drawable.ic_account_circle).placeholder(getResources().getDrawable(R.drawable.ic_account_circle)).error(getResources().getDrawable(R.drawable.ic_account_circle)).into(pro_image);
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.GONE);
            Log.d(TAG, "onCreate: " + SharedHelper.getKey(Profile_activity.this, "image"));
            Glide.with(Profile_activity.this).load(SharedHelper.getKey(Profile_activity.this, "image")).into(pro_image);

        }

        profile_image_edit = (ImageView) findViewById(R.id.image_edit);
        profile_image_edit.setOnClickListener(this);
        profile_image_edit.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.profile_image_progress);
        progressBar.setVisibility(View.GONE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");
        toolbar.setBackgroundColor(getPrimaryCOlor(Profile_activity.this));
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportFinishAfterTransition();
            }
        });


        user_name = (EditText) findViewById(R.id.username);
        user_status = (EditText) findViewById(R.id.user_status);
        user_name.setText(SharedHelper.getKey(Profile_activity.this, "name"));
        user_status.setText(SharedHelper.getKey(Profile_activity.this, "status"));
        user_name.setEnabled(false);
        user_name.setTextColor(Color.BLACK);
        user_status.setTextColor(Color.GRAY);
        user_status.setEnabled(false);

        final Button update = (Button) findViewById(R.id.btn_done);
        customView(update, getPrimaryCOlor(Profile_activity.this), getPrimaryCOlor(Profile_activity.this));
        update.setText(getResources().getString(R.string.edit));
        update.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                boolean visible;

                String value = update.getText().toString();
                if (value.equalsIgnoreCase("Edit")) {
                    Animation logoMoveAnimation = AnimationUtils.loadAnimation(Profile_activity.this, R.anim.zoom_in_animation);
                    profile_image_edit.startAnimation(logoMoveAnimation);
                    profile_image_edit.setVisibility(View.VISIBLE);
                    user_name.setEnabled(true);
                    user_status.setEnabled(true);
                    update.setText(getResources().getString(R.string.update));
                } else {
                    if (user_name.getText().toString().matches("")) {
                        Utils.showShortToast(getResources().getString(R.string.enter_valid_name), getApplicationContext());
                    } else {
                        Animation logoMoveAnimation = AnimationUtils.loadAnimation(Profile_activity.this, R.anim.zoom_out_animation);
                        profile_image_edit.startAnimation(logoMoveAnimation);
                        profile_image_edit.setVisibility(View.GONE);
                        user_name.setEnabled(false);
                        user_status.setEnabled(false);
                        update.setText(getResources().getString(R.string.edit));
                        uploadImage();
                        /*Intent intent = new Intent(Profile_activity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);*/

                    }
                }

            }


        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


    private void upDate() {
        JSONObject jsonObject = new JSONObject();
        File file = new File(filePath);
        try {
            jsonObject.put(Const.NAME, user_name.getText().toString());
            jsonObject.put(Const.ZOE_CHAT_ID, SharedHelper.getKey(Profile_activity.this, "id"));
            if (!filePath.isEmpty()) {
                jsonObject.put(Const.IMAGE, Const.amazons3ServerImagePath + file.getName());
            }
            jsonObject.put(Const.STATUS, user_status.getText().toString());
            Utils.appLog("update", jsonObject.toString());
            new PostHelper(Const.Methods.USER_UPDATE, jsonObject.toString(), Const.ServiceCode.USER_UPDATE, this, this);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTaskCompleted(JSONObject response, int serviceCode) {
        switch (serviceCode) {
            case Const.ServiceCode.USER_UPDATE:
                Utils.appLog("update_result", response.toString());
                if (response.optString("error").equalsIgnoreCase("false")) {
                    Utils.showShortToast("Update successfully.", Profile_activity.this);


                    SharedHelper.putKey(Profile_activity.this, "status", response.optString("status"));
                    SharedHelper.putKey(Profile_activity.this, "name", response.optString("name"));
                    SharedHelper.putKey(Profile_activity.this, "image", response.optString("image"));
                    SharedHelper.putKey(Profile_activity.this, "id", response.optString("zoechatid"));
                } else {
                    Utils.showShortToast("Update failed, Please try again.", Profile_activity.this);
                }
                break;
        }

    }

    private void changeback() {

//        final String f_let = String.valueOf(SharedHelper.getKey(Profile_activity.this, "name").charAt(0));
//        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
//        Log.d("onBindViewHolder: ", "f_let" + f_let);
//        int color = alphabet.indexOf(f_let.toUpperCase());
//        Log.d("onBindViewHolder: ", "color" + color);
//        if (color == -1) {
//            String number = "0123456789";
//            color = number.indexOf(f_let);
//            if (color == -1) {
//                String regExSpecialChars = "<([{\\^-=$!|]})?*+.>";
//                color = regExSpecialChars.indexOf(f_let);
//            }
//        }
//        Log.d("onBindViewHolder: ", "color" + color);
//        int randomColor = allColors.get(color);
//        drawable = new ColorDrawable(randomColor);
//        Log.d("onBindViewHolder: ", "ccc:" + randomColor + " " + allColors.get(5));
        if (SharedHelper.getKey(Profile_activity.this, "image").equalsIgnoreCase("") || SharedHelper.getKey(Profile_activity.this, "image").equalsIgnoreCase(" ")) {
            Picasso.with(Profile_activity.this).load(R.drawable.ic_account_circle).placeholder(getResources().getDrawable(R.drawable.ic_account_circle)).error(getResources().getDrawable(R.drawable.ic_account_circle)).into(SettingsActivity.pro_image);
//                int randomIndex = new Random().nextInt(allColors.size());
//            SettingsActivity.pro_image.setImageDrawable(drawable);
//            SettingsActivity.textView.setText(f_let.toUpperCase());
//            SettingsActivity.textView.setVisibility(View.VISIBLE);


        } else {
            SettingsActivity.progress_bar.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);


            Glide.with(Profile_activity.this)
                    .load(SharedHelper.getKey(Profile_activity.this, "image"))
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            SettingsActivity.progress_bar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            SettingsActivity.progress_bar.setVisibility(View.GONE);

                            return false;
                        }
                    })
                    .into(SettingsActivity.pro_image);

        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.image_edit:
                showPictureDialog();
                break;
        }
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
        String register_id = SharedHelper.getKey(Profile_activity.this, "id");
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

    private void uploadImage() {
        if (!filePath.isEmpty()) {
            final File file = new File(filePath);
//            AmazonS3Client s3Client = new AmazonS3Client(new BasicAWSCredentials(Const.accessKey, Const.secretKey));
//            s3Client.setRegion(Const.Regionss3);
//            s3Client.setEndpoint(Const.Endpoint);

            System.setProperty(SDKGlobalConfiguration.ENFORCE_S3_SIGV4_SYSTEM_PROPERTY, "true");
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(Profile_activity.this, Const.IDENTIY_POOL_ID, Const.cognitoRegion);
            AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider);
            s3Client.setEndpoint(Const.ENDPOINT);


            TransferUtility transferUtility = new TransferUtility(s3Client, Profile_activity.this);

            TransferObserver transferObserver = transferUtility.upload(Const.bucket_name, file.getName(), file);
            transferObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    Log.e("image_state", state.toString());
                    if (state.toString().equalsIgnoreCase("IN_PROGRESS")) {
                        progressBar.setVisibility(View.VISIBLE);
                    } else if (state.toString().equalsIgnoreCase("COMPLETED")) {
                        progressBar.setVisibility(View.GONE);
                        //uploadStatus = true;
                        upDate();
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
            upDate();
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
            textView.setVisibility(View.GONE);
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

}
