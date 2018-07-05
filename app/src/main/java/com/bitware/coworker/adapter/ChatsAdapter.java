package com.bitware.coworker.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.bitware.coworker.DBHelper.DBHandler;
import com.bitware.coworker.FCM.FCMMsgService;
import com.bitware.coworker.R;
import com.bitware.coworker.activity.ChatActivity;
import com.bitware.coworker.activity.MainActivity;
import com.bitware.coworker.baseUtils.AsyncTaskCompleteListener;
import com.bitware.coworker.baseUtils.Const;
import com.bitware.coworker.baseUtils.CustomDialog;
import com.bitware.coworker.baseUtils.PostHelper;
import com.bitware.coworker.baseUtils.SharedHelper;
import com.bitware.coworker.baseUtils.Utils;
import com.bitware.coworker.fragment.ChatFragment;
import com.bitware.coworker.models.ChatType;
import com.bitware.coworker.models.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by KrishnaDev on 1/30/17.
 */

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {
    public static boolean is_in_action_mode = false;
    public static ArrayList<String> selectRemoveItem;
    public static List<JSONObject> jsonArray;
    private Context context;
    private boolean internet;
    private String selecteditem;


    public ChatsAdapter(Context activity, List<JSONObject> chatsList) {
        this.jsonArray = chatsList;
        this.context = activity;
        selectRemoveItem = new ArrayList<>();
    }

    public static void clearall() throws JSONException {

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject object = jsonArray.get(i);
            object.remove("isSelected");
            object.put("isSelected", "false");
        }

    }

    public static void customView(View v, int backgroundColor, int borderColor) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[]{50, 50, 50, 50, 50, 50, 50, 50});
        shape.setColor(backgroundColor);
        shape.setStroke(3, borderColor);
        v.setBackgroundDrawable(shape);
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        try {
            view = LayoutInflater.from(context).inflate(R.layout.chats_fragment, parent, false);
        }
        catch ( Exception e)
        {

        }
        return new ViewHolder(view);
    }

    private int getMatColor(String typeColor) {
        int returnColor = Color.BLACK;
        int arrayId = context.getResources().getIdentifier("mdcolor_" + typeColor, "array", context.getPackageName());
        Log.d("getMatColor: ", "array_id" + arrayId);

        if (arrayId != 0) {
            TypedArray colors = context.getResources().obtainTypedArray(R.array.mdcolor_A400);
            Log.d("getMatColor: ", "array_val:" + colors);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.BLACK);
            colors.recycle();
        }
        return returnColor;
    }

//    private List<Integer> getAllMaterialColors() throws IOException, XmlPullParserException {
//        XmlResourceParser xrp = context.getResources().getXml(R.xml.select_color);
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

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        String isLocked = jsonArray.get(position).optString("isLocked");
        if (isLocked.equalsIgnoreCase("1") || isLocked.equalsIgnoreCase("true")) {
            holder.lock_button.setVisibility(View.VISIBLE);
        } else {
            holder.lock_button.setVisibility(View.INVISIBLE);
        }


        String selected = jsonArray.get(position).optString("isSelected");


        if (selected.equalsIgnoreCase("true")) {
            holder.remove_view.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundResource(R.color.light_weight_gray);
        } else {
            holder.remove_view.setVisibility(View.INVISIBLE);
            holder.itemView.setBackgroundResource(0);
        }
//        List<Integer> allColors = null;
//
//        try {
//            allColors = getAllMaterialColors();
//        } catch (IOException | XmlPullParserException e) {
//            e.printStackTrace();
//        }

        if (jsonArray.get(position).optString("chatRoomType").equalsIgnoreCase("0")) {
            if (jsonArray.get(position).optString("chatRoomId").equalsIgnoreCase("34654745647")) {

                holder.circularImageView.setBackgroundResource(R.drawable.ic_helpline);
            } else {
//                Drawable drawable;
//                final String f_let = String.valueOf(jsonArray.get(position).optString("Name").charAt(0));
//                String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
////                Log.d("onBindViewHolder: ", "f_let" + f_let);
//                int color = alphabet.indexOf(f_let.toUpperCase());
////                Log.d("onBindViewHolder: ", "color" + color);
//                if (color == -1) {
//                    String number = "0123456789";
//                    color = number.indexOf(f_let);
//                    if (color == -1) {
//                        String regExSpecialChars = "<([{\\^-=$!|]})?*+.>";
//                        color = regExSpecialChars.indexOf(f_let);
//                    }
//                }
////                Log.d("onBindViewHolder: ", "color" + color);
//                int randomColor = allColors.get(color);
//                drawable = new ColorDrawable(randomColor);
//                Log.d("onBindViewHolder: ", "ccc:" + randomColor + " " + allColors.get(5));
                if (jsonArray.get(position).optString("Image").equalsIgnoreCase("") || jsonArray.get(position).optString("Image").equalsIgnoreCase(" ")) {
                Picasso.with(context).load(R.drawable.ic_account_circle).placeholder(context.getResources().getDrawable(R.drawable.ic_account_circle)).error(context.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.circularImageView);
//                int randomIndex = new Random().nextInt(allColors.size());


//                    holder.circularImageView.setImageDrawable(drawable);

//                    holder.persOnName.setText(f_let.toUpperCase());
                    holder.persOnName.setVisibility(View.GONE);
                } else {
                    holder.persOnName.setVisibility(View.GONE);
                    Picasso.with(context).load(jsonArray.get(position).optString("Image")).placeholder(context.getResources().getDrawable(R.drawable.ic_account_circle)).error(context.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.circularImageView, new Callback() {
                        @Override
                        public void onSuccess() {


                        }

                        @Override
                        public void onError() {
                            Picasso.with(context).load(R.drawable.ic_account_circle).placeholder(context.getResources().getDrawable(R.drawable.ic_account_circle)).error(context.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.circularImageView);

//                            holder.persOnName.setText(f_let.toUpperCase());
                            holder.persOnName.setVisibility(View.GONE);

                        }
                    });

                }
            }
        } else if (jsonArray.get(position).optString("chatRoomType").equalsIgnoreCase("1")) {

            if (jsonArray.get(position).optString("groupImage").equalsIgnoreCase("") || jsonArray.get(position).optString("groupImage").equalsIgnoreCase(" ")) {
                Picasso.with(context).load(R.drawable.ic_profile_group).placeholder(context.getResources().getDrawable(R.drawable.ic_profile_group)).error(context.getResources().getDrawable(R.drawable.ic_profile_group)).into(holder.circularImageView);
            } else {
                Picasso.with(context).load(jsonArray.get(position).optString("groupImage")).placeholder(context.getResources().getDrawable(R.drawable.ic_profile_group)).error(context.getResources().getDrawable(R.drawable.ic_profile_group)).into(holder.circularImageView);
                holder.persOnName.setVisibility(View.GONE);
            }
        } else if (jsonArray.get(position).optString("chatRoomType").equalsIgnoreCase("3")) {
            if (jsonArray.get(position).optString("groupImage").equalsIgnoreCase("") || jsonArray.get(position).optString("groupImage").equalsIgnoreCase(" ")) {
                Picasso.with(context).load(R.drawable.ic_channel).placeholder(context.getResources().getDrawable(R.drawable.ic_channel)).error(context.getResources().getDrawable(R.drawable.ic_channel)).into(holder.circularImageView);
            } else {
                Picasso.with(context).load(jsonArray.get(position).optString("groupImage")).placeholder(context.getResources().getDrawable(R.drawable.ic_channel)).error(context.getResources().getDrawable(R.drawable.ic_channel)).into(holder.circularImageView);

            }
        } else {
            holder.persOnName.setVisibility(View.GONE);

            Picasso.with(context).load(R.drawable.ic_broad).placeholder(context.getResources().getDrawable(R.drawable.ic_broad)).error(context.getResources().getDrawable(R.drawable.ic_broad)).into(holder.circularImageView);


        }
        jsonArray.get(position).optString("");


        if (jsonArray.get(position).optString("chatRoomId").equalsIgnoreCase("34654745647")) {
            holder.Name.setText("Support Helpline");

        } else {
            holder.Name.setText(jsonArray.get(position).optString("Name"));

        }


        if (jsonArray.get(position).optString("contentType").equalsIgnoreCase(ChatType.image.toString())) {
            holder.chat_type_image.setVisibility(View.VISIBLE);

            holder.chat_type_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_camera_gray));


            holder.Status.setText(context.getResources().getString(R.string.photo));
        } else if (jsonArray.get(position).optString("contentType").equalsIgnoreCase(ChatType.location.toString())) {
            holder.chat_type_image.setVisibility(View.VISIBLE);

            holder.chat_type_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_location_on_gray));

            holder.Status.setText(context.getResources().getString(R.string.location));
        } else if (jsonArray.get(position).optString("contentType").equalsIgnoreCase(ChatType.video.toString())) {
            holder.chat_type_image.setVisibility(View.VISIBLE);

            holder.chat_type_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_videocam_gray));


            holder.Status.setText(context.getResources().getString(R.string.video));
        } else if (jsonArray.get(position).optString("contentType").equalsIgnoreCase(ChatType.contact.toString())) {
            holder.chat_type_image.setVisibility(View.VISIBLE);
            holder.chat_type_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_person_chat));
            holder.Status.setText(context.getResources().getString(R.string.contact));
        } else if (jsonArray.get(position).optString("contentType").equalsIgnoreCase(ChatType.audio.toString())) {
            holder.chat_type_image.setVisibility(View.VISIBLE);
            holder.chat_type_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_sound));
            holder.Status.setText(context.getResources().getString(R.string.audio));
        } else if (jsonArray.get(position).optString("contentType").equalsIgnoreCase(ChatType.document.toString())) {
            holder.chat_type_image.setVisibility(View.VISIBLE);
            holder.chat_type_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_insert_drive_file));
            holder.Status.setText(context.getResources().getString(R.string.document));
        } else if (jsonArray.get(position).optString("contentType").equalsIgnoreCase(ChatType.sticker.toString())) {
            holder.chat_type_image.setVisibility(View.VISIBLE);
            holder.chat_type_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_sticker));
            holder.Status.setText(context.getResources().getString(R.string.sticker));
        } else {
            holder.Status.setText(jsonArray.get(position).optString("lastMessage"));
        }

        if (jsonArray.get(position).optString("lastMessageTime") != null) {
            holder.lastMsgTime.setVisibility(View.VISIBLE);
            double time = Double.parseDouble(jsonArray.get(position).optString("lastMessageTime"));

            if (!jsonArray.get(position).optString("unreadCount").equalsIgnoreCase("0")) {
                holder.lastMsgTime.setTextColor(getPrimaryCOlor(context));
            }

            double t_d = System.currentTimeMillis();
            if (Utils.getFormattedDateFromTimestamp((long) time).equalsIgnoreCase(Utils.getFormattedDateFromTimestamp((long) t_d))) {
                holder.lastMsgTime.setText(Utils.getDate((long) time, "hh:mm aa"));
            } else {

                String date = Utils.getDate((long) time, "dd/MM/yyyy");
                String times = Utils.getDate((long) time, "hh:mm a");
                String sortDate = null;
                try {
                    sortDate = Utils.formatToYesterdayOrToday(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                holder.lastMsgTime.setText(sortDate);
            }
        }
        if (!jsonArray.get(position).optString("unreadCount").equalsIgnoreCase("0")) {
            holder.unReadCount.setVisibility(View.VISIBLE);
            holder.unReadCount.setText(jsonArray.get(position).optString("unreadCount"));
        }

        if (jsonArray.get(position).optString("sender").equalsIgnoreCase("0") || jsonArray.get(position).optString("sender").equalsIgnoreCase("2")
                || jsonArray.get(position).optString("sender").equalsIgnoreCase("5") || jsonArray.get(position).optString("sender").equalsIgnoreCase("7")) {

            if (jsonArray.get(position).optString("chatRoomType").equalsIgnoreCase("2")) {

            } else {
                holder.chatStatus.setVisibility(View.VISIBLE);


            }
            if (jsonArray.get(position).optString("lastMessageStatus").equalsIgnoreCase(Status.SENT.toString())) {
                holder.chatStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.sent));
            } else if (jsonArray.get(position).optString("lastMessageStatus").equalsIgnoreCase(Status.DELIVERED.toString())) {
                holder.chatStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.delivered));
            } else if (jsonArray.get(position).optString("lastMessageStatus").equalsIgnoreCase(Status.READ.toString())) {
                holder.chatStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.read));
            } else {

                holder.chatStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.sending_pending));


            }


        }

//        final List<Integer> finalAllColors = allColors;
        holder.circularImageView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                final CustomDialog customDialog = new CustomDialog(context);
                customDialog.setContentView(R.layout.custom_dialog);
                customDialog.getWindow().getAttributes().windowAnimations = R.anim.media_in_animation;

                ImageView userImage = (ImageView) customDialog.findViewById(R.id.user_image);
                TextView username = (TextView) customDialog.findViewById(R.id.user_name);
                TextView personName = (TextView) customDialog.findViewById(R.id.text_name_per);
                if (jsonArray.get(position).optString("chatRoomId").equalsIgnoreCase("34654745647")) {
                    username.setText(jsonArray.get(position).optString("Support Helpline"));

                } else {
                    username.setText(jsonArray.get(position).optString("Name"));
                }

                if (jsonArray.get(position).optString("chatRoomType").equalsIgnoreCase("0")) {
                    if (jsonArray.get(position).optString("Image").equalsIgnoreCase("") || jsonArray.get(position).optString("Image").equalsIgnoreCase(" ")) {
                        if (jsonArray.get(position).optString("chatRoomId").equalsIgnoreCase("34654745647")) {

                            Picasso.with(context).load("qwe").placeholder(context.getResources().getDrawable(R.drawable.ic_helpline)).error(context.getResources().getDrawable(R.drawable.ic_helpline)).into(userImage);

                        } else {
//                            String f_let = String.valueOf(jsonArray.get(position).optString("Name").charAt(0));
//                            String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
//                            Log.d("onBindViewHolder: ", "f_let" + f_let);
//                            int color = alphabet.indexOf(f_let.toUpperCase());
//                            Log.d("onBindViewHolder: ", "color" + color);
//                            if (color == -1) {
//                                String number = "0123456789";
//                                color = number.indexOf(f_let);
//                                if (color == -1) {
//                                    String regExSpecialChars = "<([{\\^-=$!|]})?*+.>";
//                                    color = regExSpecialChars.indexOf(f_let);
//                                }
//                            }
//                            Log.d("onBindViewHolder: ", "color" + color);
//
//
//                            int randomColor = finalAllColors.get(color);
//
//                            Log.d("onBindViewHolder: ", "ccc:" + randomColor + " " + finalAllColors.get(5));
                            Picasso.with(context).load("qwe").placeholder(context.getResources().getDrawable(R.drawable.ic_account_circle)).error(context.getResources().getDrawable(R.drawable.ic_account_circle)).into(userImage);

//                            userImage.setImageDrawable(new ColorDrawable(randomColor));

//                            personName.setText(f_let.toUpperCase());
                            personName.setVisibility(View.GONE);
                        }
                    } else {
                        personName.setVisibility(View.GONE);
                        Picasso.with(context).load(jsonArray.get(position).optString("Image")).placeholder(context.getResources().getDrawable(R.drawable.ic_account_circle)).error(context.getResources().getDrawable(R.drawable.ic_account_circle)).into(userImage);

                    }
                } else if (jsonArray.get(position).optString("chatRoomType").equalsIgnoreCase("1")) {

                    if (jsonArray.get(position).optString("groupImage").equalsIgnoreCase("") || jsonArray.get(position).optString("groupImage").equalsIgnoreCase(" ")) {
                        Picasso.with(context).load(R.drawable.ic_profile_group).placeholder(context.getResources().getDrawable(R.drawable.ic_profile_group)).error(context.getResources().getDrawable(R.drawable.ic_profile_group)).into(userImage);
                    } else {
                        Picasso.with(context).load(jsonArray.get(position).optString("groupImage")).placeholder(context.getResources().getDrawable(R.drawable.ic_profile_group)).error(context.getResources().getDrawable(R.drawable.ic_profile_group)).into(userImage);

                    }
                } else if (jsonArray.get(position).optString("chatRoomType").equalsIgnoreCase("3")) {
                    if (jsonArray.get(position).optString("groupImage").equalsIgnoreCase("") || jsonArray.get(position).optString("groupImage").equalsIgnoreCase(" ")) {
                        Picasso.with(context).load(R.drawable.ic_channel).placeholder(context.getResources().getDrawable(R.drawable.ic_channel)).error(context.getResources().getDrawable(R.drawable.ic_channel)).into(userImage);
                    } else {
                        Picasso.with(context).load(jsonArray.get(position).optString("groupImage")).placeholder(context.getResources().getDrawable(R.drawable.ic_channel)).error(context.getResources().getDrawable(R.drawable.ic_channel)).into(userImage);

                    }
                } else {
                    Picasso.with(context).load(R.drawable.ic_broad).placeholder(context.getResources().getDrawable(R.drawable.ic_broad)).error(context.getResources().getDrawable(R.drawable.ic_broad)).into(userImage);


                }
                ImageView msg = (ImageView) customDialog.findViewById(R.id.msg);
//                ImageView about = (ImageView) customDialog.findViewById(R.id.about);
                msg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final DBHandler dbHandler = new DBHandler(context);
                        String statu = dbHandler.Getlockedstatus(jsonArray.get(position).optString("chatRoomId"));

                        if (statu.equalsIgnoreCase("true") || statu.equalsIgnoreCase("1")) {
                            final Dialog dialog = new Dialog(context);
                            dialog.setCancelable(true);
                            dialog.setCanceledOnTouchOutside(true);
                            dialog.setContentView(R.layout.chat_password);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            Window window = dialog.getWindow();
                            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                            window.setGravity(Gravity.CENTER);


                            final EditText password;
                            Button submit;
                            password = (EditText) dialog.findViewById(R.id.password);

                            InputFilter filter = new InputFilter() {
                                @Override
                                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                                    for (int i = start; i < end; i++)
                                        if (!Character.isLetter(source.charAt(i)) && !Character.isSpaceChar(source.charAt(i)))
                                            return "";

                                    return null;
                                }
                            };

                            password.setFilters(new InputFilter[]{filter});


                            submit = (Button) dialog.findViewById(R.id.password_submit);
                            TextView forgot_pass = (TextView) dialog.findViewById(R.id.pass_forgot);
                            forgot_pass.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new hitapiforotp().execute();
                                    dialog.dismiss();
                                }
                            });
                            submit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    String confirm_password = dbHandler.GetPassword(jsonArray.get(position).optString("chatRoomId"));
                                    if (password.getText().toString().equalsIgnoreCase(confirm_password)) {

                                        SharedHelper.putKey(context, "single_chat_enable", "yes");
                                        Intent intent = new Intent(context, ChatActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        String type = jsonArray.get(position).optString("chatRoomType");
                                        if (type.equalsIgnoreCase("0")) {
                                            intent.putExtra("zoeChatID", jsonArray.get(position).optString("chatRoomId"));
                                            intent.putExtra("groupId", "0");
                                            FCMMsgService.current_id = jsonArray.get(position).optString("chatRoomId");
                                        } else if (type.equalsIgnoreCase("1")) {
                                            intent.putExtra("zoeChatID", jsonArray.get(position).optString("create_by"));
                                            intent.putExtra("groupId", jsonArray.get(position).optString("chatRoomId"));
                                            FCMMsgService.current_id = jsonArray.get(position).optString("chatRoomId");


                                        }
                                        Log.d("onClick: ", "chatsadap:" + jsonArray.get(position).optString("chatRoomId"));
                                        intent.putExtra("create_by", jsonArray.get(position).optString("create_by"));
                                        intent.putExtra("chatRoomType", jsonArray.get(position).optString("chatRoomType"));
                                        intent.putExtra("user_name", jsonArray.get(position).optString("Name"));
                                        intent.putExtra("image", jsonArray.get(position).optString("Image"));
                                        intent.putExtra("grp_image", jsonArray.get(position).optString("groupImage"));
                                        context.startActivity(intent);
                                        dialog.dismiss();

                                    } else {
                                        Toast.makeText(context, "Password Mismatch. Kindly try again", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                            dialog.show();
                        } else {
                            Log.d("onClick: chats", "adapter_values:" + jsonArray.get(position));
                            SharedHelper.putKey(context, "single_chat_enable", "yes");
                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            String type = jsonArray.get(position).optString("chatRoomType");
                            if (type.equalsIgnoreCase("0")) {
                                intent.putExtra("zoeChatID", jsonArray.get(position).optString("chatRoomId"));
                                intent.putExtra("groupId", "0");
                                intent.putExtra("grp_image", jsonArray.get(position).optString(""));

                            } else {
                                intent.putExtra("zoeChatID", jsonArray.get(position).optString("create_by"));
                                intent.putExtra("groupId", jsonArray.get(position).optString("chatRoomId"));
                                intent.putExtra("grp_image", jsonArray.get(position).optString("groupImage"));
                            }

                            intent.putExtra("image", jsonArray.get(position).optString("Image"));
                            intent.putExtra("chatRoomType", jsonArray.get(position).optString("chatRoomType"));
                            intent.putExtra("user_name", jsonArray.get(position).optString("Name"));
                            intent.putExtra("zoeChatID", jsonArray.get(position).optString("chatRoomId"));
                            context.startActivity(intent);
                        }
                        customDialog.dismiss();
                    }
                });

//                about.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent = new Intent(context, UserDetails.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        intent.putExtra("user_name", jsonArray.get(position).optString("Name"));
//                        intent.putExtra("image", jsonArray.get(position).optString("Image"));
//                        intent.putExtra("zoeChatID", jsonArray.get(position).optString("chatRoomId"));
//                        context.startActivity(intent);
//                        customDialog.dismiss();
//                    }
//                });
                customDialog.show();

              /*  Intent i = new Intent(new Intent(context, CustomDialog.class));
                i.putExtra("Name", jsonArray.get(position).optString("Name"));
                i.putExtra("Image", jsonArray.get(position).optString("Image"));
                i.putExtra("zoeChatID", jsonArray.get(position).optString("chatRoomId"));
                @SuppressLint({"NewApi", "LocalSuppress"}) ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation((Activity) context, view, "transition");
                context.startActivity(i, options.toBundle());*/

            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if (!is_in_action_mode) {
                    try {
                        MainActivity.mainActivity.toolbar.getMenu().clear();
                        Log.d("onLongClick: ", "chatsadaptervalues:" + jsonArray.get(position));
                        if (jsonArray.get(position).optString("isLocked").equalsIgnoreCase("1") || jsonArray.get(position).optString("isLocked").equalsIgnoreCase("true")) {
                            MainActivity.mainActivity.toolbar.inflateMenu(R.menu.action_menu_mode_unlock);

                        } else {

                            MainActivity.mainActivity.toolbar.inflateMenu(R.menu.action_mode_menu);
                        }



                        MainActivity.mainActivity.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
                        MainActivity.mainActivity.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ChatFragment.mainActivity.clearActionM();
                            }
                        });

                        Menu menu=MainActivity.mainActivity.toolbar.getMenu();
                        for (int i=0;i<menu.size();i++)
                        {
                            MenuItem menuItems=menu.getItem(i);
                            Drawable drawable = menuItems.getIcon();
                            if (drawable != null) {
                                // If we don't mutate the drawable, then all drawable's with this id will have a color
                                // filter applied to it.
                                drawable.mutate();
                                drawable.setColorFilter(context.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

                            }

                        }

                        is_in_action_mode = true;
                        notifyDataSetChanged();
                        ChatFragment.counter++;
                        updateCnt(ChatFragment.counter);
                        JSONObject object = jsonArray.get(position);
                        object.remove("isSelected");
                        object.put("isSelected", "true");
                        holder.remove_view.setVisibility(View.VISIBLE);
                        holder.itemView.setBackgroundResource(R.color.light_weight_gray);
                        String id = jsonArray.get(position).optString("chatRoomId");
                        addItem(id);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return true;
                } else {
                    return false;
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return jsonArray.size();
    }

    public void setFilter(List<JSONObject> filter) {
        Log.d("setFilter: ", "filter_setting");
        jsonArray.clear();
        jsonArray = filter;
//        jsonArray.addAll(filter);
        Log.d("setFilter: ", "jsonvalues:" + jsonArray);
        Log.d("setFilter: ", "filtervalues:" + filter);

    }

    private void addItem(String zoeChatID) {
        selectRemoveItem.add(zoeChatID);
        Log.e("items", selectRemoveItem.toString());
    }

    public void removeitem(String msgId) {

        for (int i = 0; i < selectRemoveItem.size(); i++) {
            String s1 = selectRemoveItem.get(i);
            if (s1.equalsIgnoreCase(msgId)) {
                selectRemoveItem.remove(i);
            }
        }
    }

    private void updateCnt(int counter) {
        if (counter == 0) {
            ChatFragment.mainActivity.clearActionM();
            selectRemoveItem.clear();
        } else {
            MainActivity.mainActivity.counterTextView.setText(counter + "  selected");
        }
    }

    private void showOTP(final String otp_val) {
        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.otp_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        final EditText otp;
        Button submit;
        otp = (EditText) dialog.findViewById(R.id.otp_edit);

        submit = (Button) dialog.findViewById(R.id.otp_submit);
        otp.setText(otp_val);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp_value = otp.getText().toString();
                if (otp_val.equalsIgnoreCase(otp_value)) {
                    showalertpass(3);
                }
                dialog.dismiss();

            }
        });

        dialog.show();


    }

    private void showalertpass(final int i) {
        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        if (i == 0 || i == 3) {
            dialog.setContentView(R.layout.password_layout);
        } else {
            dialog.setContentView(R.layout.password_layout_forgot);

        }
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);


        final EditText password, confirm_password;
        Button submit;
        password = (EditText) dialog.findViewById(R.id.password);
        confirm_password = (EditText) dialog.findViewById(R.id.confirm_password);
        submit = (Button) dialog.findViewById(R.id.password_submit);
        if (i == 1) {
            TextView forgot_pass = (TextView) dialog.findViewById(R.id.pass_forgot);
            forgot_pass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new hitapiforotp().execute();
                }
            });
        }


        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (Character.isSpaceChar(source.charAt(i))) {
                        return "";
                    }
                }
                return null;


            }
        };

        password.setFilters(new InputFilter[]{filter});
        confirm_password.setFilters(new InputFilter[]{filter});


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHandler dbHandler = new DBHandler(context);
                String dbpassword;
                if (password.getText().toString().equalsIgnoreCase(confirm_password.getText().toString())) {
                    if (i == 0) {
                        //updateDB
                        if (ChatsAdapter.selectRemoveItem.size() == 0) {
                            dbpassword = dbHandler.GetPassword(selecteditem);

                        } else {
                            dbpassword = dbHandler.GetPassword(ChatsAdapter.selectRemoveItem.get(0));
                        }
                        if (password.getText().toString().equalsIgnoreCase(dbpassword)) {
                            Log.d("onClick: ", "Chatadapter:" + ChatsAdapter.selectRemoveItem.get(0));
                            dbHandler.Updatelock(ChatsAdapter.selectRemoveItem.get(0), "1");
                            dbHandler.UpdatePassword(ChatsAdapter.selectRemoveItem.get(0), password.getText().toString());
                            try {
                                clearall();
                                MainActivity.mainActivity.toolbar.getMenu().clear();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                        } else {
                            Toast.makeText(context, "Kindly Enter the correct password", Toast.LENGTH_SHORT).show();
                        }

                    } else if (i == 3) {

                        if (password.getText().toString().equalsIgnoreCase(confirm_password.getText().toString())) {

                            dbHandler.Updatelock(selecteditem, "1");
                            dbHandler.UpdatePassword(selecteditem, password.getText().toString());
                            try {
                                clearall();
                                MainActivity.mainActivity.toolbar.getMenu().clear();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                        } else {
                            Toast.makeText(context, "Password Mismatch. Kindly try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(context, "Password Mismatch. Kindly try again", Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialog.show();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circularImageView;
        TextView Name, Status, unReadCount, lastMsgTime, persOnName;
        ImageView chatStatus, chat_type_image, remove_view, lock_button;
        RelativeLayout unreadCount_text_Layout;


        public ViewHolder (final View itemView) {
            super(itemView);

            circularImageView = (CircleImageView) itemView.findViewById(R.id.contact_image);
            Name = (TextView) itemView.findViewById(R.id.user_name);
            Status = (TextView) itemView.findViewById(R.id.user_status);
            lastMsgTime = (TextView) itemView.findViewById(R.id.lastMsgTime_text);
            unReadCount = (TextView) itemView.findViewById(R.id.unreadCount_text);
            customView(unReadCount, getPrimaryCOlor(context), getPrimaryCOlor(context));

            chatStatus = (ImageView) itemView.findViewById(R.id.chat_status);
            chat_type_image = (ImageView) itemView.findViewById(R.id.chat_type_image);
            remove_view = (ImageView) itemView.findViewById(R.id.remove_view_check);
            customView(remove_view, getPrimaryCOlor(context), getPrimaryCOlor(context));
            lock_button = (ImageView) itemView.findViewById(R.id.lock_status);
            persOnName = (TextView) itemView.findViewById(R.id.text_name_per);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int pos = getAdapterPosition();
                    Log.d("onClick: ", "adapter_pos_1:" + pos);
                    JSONObject object = jsonArray.get(pos);

                    if (is_in_action_mode) {
                        MainActivity.mainActivity.toolbar.getMenu().clear();
                        MainActivity.mainActivity.toolbar.inflateMenu(R.menu.action_mode_menu_multi);
                        MainActivity.mainActivity.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);

                        Menu menu=MainActivity.mainActivity.toolbar.getMenu();
                        for (int i=0;i<menu.size();i++)
                        {
                            MenuItem menuItems=menu.getItem(i);
                            Drawable drawable = menuItems.getIcon();
                            if (drawable != null) {
                                // If we don't mutate the drawable, then all drawable's with this id will have a color
                                // filter applied to it.
                                drawable.mutate();
                                drawable.setColorFilter(context.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

                            }

                        }
                        MainActivity.mainActivity.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ChatFragment.mainActivity.clearActionM();
                            }
                        });

                        if (object.optString("isSelected").equalsIgnoreCase("true")) {
                            try {
                                Log.e("view_", "Visible");
                                remove_view.setVisibility(View.GONE);
                                itemView.setBackgroundResource(0);
                                object.remove("isSelected");
                                object.put("isSelected", "false");
                                ChatFragment.counter--;
                                updateCnt(ChatFragment.counter);
                                Log.d("onClick: ", "adapter_pos_valu:" + jsonArray.size());
                                Log.d("onClick: ", "adapter_pos:" + pos);
                                String id = jsonArray.get(pos).optString("msgId");
                                removeitem(id);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                Log.e("view_", "UNVisible");
                                remove_view.setVisibility(View.VISIBLE);
                                itemView.setBackgroundResource(R.color.light_weight_gray);
                                ChatFragment.counter++;
                                object.remove("isSelected");
                                object.put("isSelected", "true");
                                updateCnt(ChatFragment.counter);
                                addItem(jsonArray.get(pos).optString("chatRoomId"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    } else {

                        selecteditem = jsonArray.get(pos).optString("chatRoomId");
                        final DBHandler dbHandler = new DBHandler(context);
                        String statu = dbHandler.Getlockedstatus(jsonArray.get(pos).optString("chatRoomId"));

                        if (statu.equalsIgnoreCase("true") || statu.equalsIgnoreCase("1")) {
                            final Dialog dialog = new Dialog(context);
                            dialog.setCancelable(true);
                            dialog.setCanceledOnTouchOutside(true);
                            dialog.setContentView(R.layout.chat_password);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            Window window = dialog.getWindow();
                            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                            window.setGravity(Gravity.CENTER);


                            final EditText password;
                            Button submit;
                            password = (EditText) dialog.findViewById(R.id.password);


                            InputFilter filter = new InputFilter() {
                                public CharSequence filter(CharSequence source, int start, int end,
                                                           Spanned dest, int dstart, int dend) {
                                    for (int i = start; i < end; i++) {
                                        if (Character.isSpaceChar(source.charAt(i))) {
                                            return "";
                                        }
                                    }
                                    return null;


                                }
                            };

                            password.setFilters(new InputFilter[]{filter});

                            submit = (Button) dialog.findViewById(R.id.password_submit);
                            TextView forgot_pass = (TextView) dialog.findViewById(R.id.pass_forgot);
                            forgot_pass.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new hitapiforotp().execute();
                                    dialog.dismiss();
                                }
                            });
                            submit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    String confirm_password = dbHandler.GetPassword(jsonArray.get(pos).optString("chatRoomId"));
                                    if (password.getText().toString().equalsIgnoreCase(confirm_password)) {
                                        remove_view.setVisibility(View.GONE);

                                        SharedHelper.putKey(context, "single_chat_enable", "yes");
                                        Intent intent = new Intent(context, ChatActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        String type = jsonArray.get(pos).optString("chatRoomType");
                                        if (type.equalsIgnoreCase("0")) {
                                            intent.putExtra("zoeChatID", jsonArray.get(pos).optString("chatRoomId"));
                                            intent.putExtra("groupId", "0");
                                            FCMMsgService.current_id = jsonArray.get(pos).optString("chatRoomId");
                                        } else if (type.equalsIgnoreCase("1")) {
                                            intent.putExtra("zoeChatID", jsonArray.get(pos).optString("create_by"));
                                            intent.putExtra("groupId", jsonArray.get(pos).optString("chatRoomId"));
                                            FCMMsgService.current_id = jsonArray.get(pos).optString("chatRoomId");


                                        }
                                        Log.d("onClick: ", "chatsadap:" + jsonArray.get(pos).optString("chatRoomId"));
                                        intent.putExtra("create_by", jsonArray.get(pos).optString("create_by"));
                                        intent.putExtra("chatRoomType", jsonArray.get(pos).optString("chatRoomType"));
                                        intent.putExtra("user_name", jsonArray.get(pos).optString("Name"));
                                        intent.putExtra("image", jsonArray.get(pos).optString("Image"));
                                        intent.putExtra("grp_image", jsonArray.get(pos).optString("groupImage"));
                                        context.startActivity(intent);
                                        dialog.dismiss();

                                    } else {
                                        Toast.makeText(context, "Password Mismatch. Kindly try again", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                            dialog.show();
                        } else {
                            remove_view.setVisibility(View.GONE);

                            SharedHelper.putKey(context, "single_chat_enable", "yes");
                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            Log.d("onClick: ", "values:" + jsonArray.get(pos));
                            String type = jsonArray.get(pos).optString("chatRoomType");
                            if (type.equalsIgnoreCase("0")) {
                                intent.putExtra("zoeChatID", jsonArray.get(pos).optString("chatRoomId"));
                                intent.putExtra("groupId", "0");
                                FCMMsgService.current_id = jsonArray.get(pos).optString("chatRoomId");
                            } else if (type.equalsIgnoreCase("1")) {
                                intent.putExtra("zoeChatID", jsonArray.get(pos).optString("create_by"));
                                intent.putExtra("groupId", jsonArray.get(pos).optString("chatRoomId"));
                                FCMMsgService.current_id = jsonArray.get(pos).optString("chatRoomId");
                            } else if (type.equalsIgnoreCase("2")) {
                                intent.putExtra("zoeChatID", jsonArray.get(pos).optString("create_by"));
                                intent.putExtra("groupId", jsonArray.get(pos).optString("chatRoomId"));
                            } else if (type.equalsIgnoreCase("3")) {
                                intent.putExtra("zoeChatID", jsonArray.get(pos).optString("create_by"));
                                intent.putExtra("groupId", jsonArray.get(pos).optString("chatRoomId"));
                                FCMMsgService.current_id = jsonArray.get(pos).optString("chatRoomId");
                            }
                            Log.d("onClick: ", "chatsadap:" + jsonArray.get(pos).optString("chatRoomId"));
                            intent.putExtra("create_by", jsonArray.get(pos).optString("create_by"));
                            intent.putExtra("chatRoomType", jsonArray.get(pos).optString("chatRoomType"));
                            intent.putExtra("user_name", jsonArray.get(pos).optString("Name"));
                            intent.putExtra("image", jsonArray.get(pos).optString("Image"));
                            intent.putExtra("grp_image", jsonArray.get(pos).optString("groupImage"));

                            context.startActivity(intent);
                        }
                    }


                }
            });


        }
    }

    private class hitapiforotp extends AsyncTask<String, String, String> implements AsyncTaskCompleteListener {


        String result = "";

        @Override
        protected String doInBackground(String... params) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", SharedHelper.getKey(context, "id"));
//                Log.d("doInBackground: ", "posting_values:" + jsonObject);
                internet = Utils.isNetworkAvailable(context);
                if (internet) {
                    new PostHelper(Const.Methods.OTP_REQUEST, jsonObject.toString(), Const.ServiceCode.PARTICIPANTS_DETAILS, context, this);
                } else {
                    result = "yes";
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }


        @Override
        public void onTaskCompleted(final JSONObject response, int serviceCode) {

            Log.d("onTaskCompleted: ", "" + response);


            if (response.optString("error").equalsIgnoreCase("true")) {


            } else {
                MainActivity.mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showOTP(response.optString("otp"));

                    }
                });

            }

        }
    }


}
