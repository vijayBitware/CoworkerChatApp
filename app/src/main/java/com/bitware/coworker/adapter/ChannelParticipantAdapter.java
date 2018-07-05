package com.bitware.coworker.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.bitware.coworker.DBHelper.DBHandler;
import com.bitware.coworker.R;
import com.bitware.coworker.activity.ChatActivity;
import com.bitware.coworker.activity.MainActivity;
import com.bitware.coworker.activity.UserDetails;
import com.bitware.coworker.baseUtils.AsyncTaskCompleteListener;
import com.bitware.coworker.baseUtils.Const;
import com.bitware.coworker.baseUtils.CustomDialog;
import com.bitware.coworker.baseUtils.PostHelper;
import com.bitware.coworker.baseUtils.SharedHelper;
import com.bitware.coworker.baseUtils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by KrishnaDev on 1/5/17.
 */
public class ChannelParticipantAdapter extends RecyclerView.Adapter<ChannelParticipantAdapter.ViewHolder> {

    public static Dialog dialog;
    Boolean amAdmin = false;
    String group_id;
    String selected_item;
    private JSONArray list;
    private Context myContext;
    private boolean internet;

    public ChannelParticipantAdapter(Context activity, JSONArray jsonArray) {
        this.list = jsonArray;
        this.myContext = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.channel_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        JSONObject object = list.optJSONObject(position);
        DBHandler dbHandler = new DBHandler(myContext);

        final String user_image = dbHandler.GetUserImage(object.optString(""));
        final String user_name = dbHandler.GetUserName(object.optString("participantId"));
        String user_status = dbHandler.GetUserStatus(object.optString("participantId"));
        String my_id = SharedHelper.getKey(myContext, "id");


        Log.d("onBindViewHolder: ", "valeus:" + user_image + "," + user_name);

        String from = SharedHelper.getKey(myContext, "id");

        Log.d("onBindViewHolder: ", "values:from:" + from + ",parti_id:" + object.optString("participantId"));

        if (from.equalsIgnoreCase(object.optString("participantId"))) {
            group_id = ChatActivity.groupId_loc;

            if (user_image.equalsIgnoreCase(" ") || user_image.equalsIgnoreCase("")) {
                Picasso.with(myContext).load(R.drawable.ic_account_circle).error(myContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.user_image);
            } else {

                Picasso.with(myContext).load(user_image).error(myContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.user_image);
            }

            String role= dbHandler.GetChannelMember(group_id,object.optString("participantId"));
            Log.d("onBindViewHolder: ","channel_role:"+role);
            String r;
            if (role.equalsIgnoreCase("user"))
            {
                r="Member";
            }
            else if(role.equalsIgnoreCase("owner"))
            {
                r="Creator";

            }
            else {
                r="Admin";
            }

            holder.admin.setText(r);
            holder.user_name.setText(myContext.getResources().getString(R.string.you));
            holder.user_status.setText(user_status);
            holder.user_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        } else {
            group_id = ChatActivity.groupId_loc;

            String role= dbHandler.GetChannelMember(group_id,object.optString("participantId"));
            Log.d("onBindViewHolder: ","channel_role:"+role);
            String r;
            if (role.equalsIgnoreCase("user"))
            {
                r="Member";
            }
            else if(role.equalsIgnoreCase("owner"))
            {
                r="Creator";

            }
            else {
                r="Admin";
            }
            holder.admin.setText(r);


            if (user_image.equalsIgnoreCase(" ") || user_image.equalsIgnoreCase("")) {
                Picasso.with(myContext).load(R.drawable.ic_account_circle).placeholder(myContext.getResources().getDrawable(R.drawable.ic_account_circle)).error(myContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.user_image);

            } else {
                Picasso.with(myContext).load(user_image).placeholder(myContext.getResources().getDrawable(R.drawable.ic_account_circle)).error(myContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.user_image);
            }
            holder.user_name.setText(user_name);
            holder.user_status.setText(user_status);

            holder.user_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final CustomDialog customDialog = new CustomDialog(myContext);
                    customDialog.setContentView(R.layout.custom_dialog);
                    ImageView userImage = (ImageView) customDialog.findViewById(R.id.user_image);
                    TextView username = (TextView) customDialog.findViewById(R.id.user_name);

                    username.setText(user_name);

                    if (user_image.equalsIgnoreCase(" ") || user_image.equalsIgnoreCase("")) {
                        Picasso.with(myContext).load(R.drawable.ic_account_circle).error(myContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(userImage);
                    } else {

                        Picasso.with(myContext).load(user_image).error(myContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(userImage);
                    }

                    ImageView msg = (ImageView) customDialog.findViewById(R.id.msg);

                    msg.setImageDrawable(myContext.getResources().getDrawable(R.drawable.ic_chat_app_color));

                    customDialog.show();

                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return list.length();
    }
    public static int getPrimaryCOlor (Context context) {
        final TypedValue value = new TypedValue ();
        context.getTheme ().resolveAttribute (R.attr.colorPrimary, value, true);
        return value.data;
    }
    private void showapartidialog(final String parti_id) {

        String from = SharedHelper.getKey(myContext, "my_zoe_id");
        final DBHandler dbHandler = new DBHandler(myContext);

        dialog = new Dialog(myContext);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.not_admin_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        TextView message, view, make_admin, remove;
        message = (TextView) dialog.findViewById(R.id.message_group_part);
        view = (TextView) dialog.findViewById(R.id.view_group_part);

        message.setText(myContext.getResources().getString(R.string.message) + " "+ dbHandler.GetUserName(parti_id));
        view.setText(myContext.getResources().getString(R.string.view) + " "+ dbHandler.GetUserName(parti_id));


        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DBHandler dbHandler = new DBHandler(myContext);
                selected_item = parti_id;
                String statu = dbHandler.Getlockedstatus(parti_id);
                if (statu.equalsIgnoreCase("true") || statu.equalsIgnoreCase("1")) {
                    final Dialog dialog = new Dialog(myContext);
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

                            String confirm_password = dbHandler.GetPassword(parti_id);
                            if (password.getText().toString().equalsIgnoreCase(confirm_password)) {

                                SharedHelper.putKey(myContext, "single_chat_enable", "yes");
                                Intent intent = new Intent(myContext, ChatActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("user_name", dbHandler.GetUserName(parti_id));
                                intent.putExtra("image", dbHandler.GetUserImage(parti_id));
                                intent.putExtra("zoeChatID", parti_id);
                                intent.putExtra("groupId", "0");
                                intent.putExtra("chatRoomType", "0");

                                myContext.startActivity(intent);
                                dialog.dismiss();

                            } else {
                                Toast.makeText(myContext, "Password Mismatch. Kindly try again", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                    dialog.show();
                } else {

                    SharedHelper.putKey(myContext, "single_chat_enable", "yes");
                    Intent intent = new Intent(myContext, ChatActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("user_name", dbHandler.GetUserName(parti_id));
                    intent.putExtra("image", dbHandler.GetUserImage(parti_id));
                    intent.putExtra("zoeChatID", parti_id);
                    intent.putExtra("groupId", "0");
                    intent.putExtra("chatRoomType", "0");

                    myContext.startActivity(intent);
                }


            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("onClick: ", "chatroomtype:" + "0");
                Intent intent = new Intent(myContext, UserDetails.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("zoeChatID", parti_id);
                intent.putExtra("user_name", dbHandler.GetUserName(parti_id));
                intent.putExtra("image", dbHandler.GetUserName(parti_id));
                intent.putExtra("group_id", group_id);
                intent.putExtra("room_type", "0");
                myContext.startActivity(intent);
            }
        });

dialog.show();

    }

    private void showadmindialog(final String parti_id, final String created_by, final String joined_at) {

        String from = SharedHelper.getKey(myContext, "my_zoe_id");
        final DBHandler dbHandler = new DBHandler(myContext);

        dialog = new Dialog(myContext);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.admin_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        TextView message, view, make_admin, remove;
        message = (TextView) dialog.findViewById(R.id.message_group_part);
        view = (TextView) dialog.findViewById(R.id.view_group_part);
        make_admin = (TextView) dialog.findViewById(R.id.make_admin);
        remove = (TextView) dialog.findViewById(R.id.remove_from_group);

        message.setText(myContext.getResources().getString(R.string.message) + " "+ dbHandler.GetUserName(parti_id));
        view.setText(myContext.getResources().getString(R.string.view) + " "+ dbHandler.GetUserName(parti_id));
        remove.setText(myContext.getResources().getString(R.string.remove) + " "+ dbHandler.GetUserName(parti_id));

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DBHandler dbHandler = new DBHandler(myContext);
                selected_item = parti_id;
                String statu = dbHandler.Getlockedstatus(parti_id);
                if (statu.equalsIgnoreCase("true") || statu.equalsIgnoreCase("1")) {
                    final Dialog dialog = new Dialog(myContext);
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

                            String confirm_password = dbHandler.GetPassword(parti_id);
                            if (password.getText().toString().equalsIgnoreCase(confirm_password)) {


                                SharedHelper.putKey(myContext, "single_chat_enable", "yes");
                                Intent intent = new Intent(myContext, ChatActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("user_name", dbHandler.GetUserName(parti_id));
                                intent.putExtra("image", dbHandler.GetUserImage(parti_id));
                                intent.putExtra("zoeChatID", parti_id);
                                intent.putExtra("groupId", "0");
                                intent.putExtra("chatRoomType", "0");

                                myContext.startActivity(intent);
                                dialog.dismiss();

                            } else {
                                Toast.makeText(myContext, "Password Mismatch. Kindly try again", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                    dialog.show();
                } else {

                    SharedHelper.putKey(myContext, "single_chat_enable", "yes");
                    Intent intent = new Intent(myContext, ChatActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("user_name", dbHandler.GetUserName(parti_id));
                    intent.putExtra("image", dbHandler.GetUserImage(parti_id));
                    intent.putExtra("zoeChatID", parti_id);
                    intent.putExtra("groupId", "0");
                    intent.putExtra("chatRoomType", "0");

                    myContext.startActivity(intent);
                }


            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("onClick: ", "chatroomtype:" + "0");
                Intent intent = new Intent(myContext, UserDetails.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("zoeChatID", parti_id);
                intent.putExtra("user_name", dbHandler.GetUserName(parti_id));
                intent.putExtra("image", dbHandler.GetUserName(parti_id));
                intent.putExtra("group_id", group_id);
                intent.putExtra("room_type", "0");
                myContext.startActivity(intent);
            }
        });
        make_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    UserDetails.makeadminChannel(parti_id, group_id,dialog, myContext, created_by, joined_at);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.show();

    }

    private void showalertpass(final int i) {
        final Dialog dialog = new Dialog(myContext);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        dialog.setContentView(R.layout.password_layout);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);


        final EditText password, confirm_password;
        Button submit;
        password = (EditText) dialog.findViewById(R.id.password);
        confirm_password = (EditText) dialog.findViewById(R.id.confirm_password);
        submit = (Button) dialog.findViewById(R.id.password_submit);


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
                DBHandler dbHandler = new DBHandler(myContext);
                String dbpassword;
                if (password.getText().toString().equalsIgnoreCase(confirm_password.getText().toString())) {

                    dbHandler.Updatelock(selected_item, "1");
                    dbHandler.UpdatePassword(selected_item, password.getText().toString());
                    MainActivity.mainActivity.toolbar.getMenu().clear();
                    dialog.dismiss();
                } else {
                    Toast.makeText(myContext, "Password Mismatch. Kindly try again", Toast.LENGTH_SHORT).show();
                }


            }
        });

        dialog.show();

    }



    public static void customView(View v, int backgroundColor, int borderColor)
    {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[] { 5, 5, 5, 5, 5, 5, 5, 5 });
        shape.setColor(backgroundColor);
        shape.setStroke(3, borderColor);
        v.setBackgroundDrawable(shape);
    }

    private void showremoveadmindialog(final String parti_id, final String joined_at, final String created_by) {
        String from = SharedHelper.getKey(myContext, "my_zoe_id");
        final DBHandler dbHandler = new DBHandler(myContext);

        dialog = new Dialog(myContext);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.admin_dialog_remove_channel);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        TextView message, view, remove_admin, remove;
        message = (TextView) dialog.findViewById(R.id.message_group_part);
        view = (TextView) dialog.findViewById(R.id.view_group_part);
       remove_admin = (TextView) dialog.findViewById(R.id.remove_from_admin);
        remove = (TextView) dialog.findViewById(R.id.remove_from_group);

        message.setText(myContext.getResources().getString(R.string.message) + " "+ dbHandler.GetUserName(parti_id));
        view.setText(myContext.getResources().getString(R.string.view) + " "+ dbHandler.GetUserName(parti_id));


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("onClick: ", "chatroomtype:" + "0");
                Intent intent = new Intent(myContext, UserDetails.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("zoeChatID", parti_id);
                intent.putExtra("user_name", dbHandler.GetUserName(parti_id));
                intent.putExtra("image", dbHandler.GetUserName(parti_id));
                intent.putExtra("group_id", group_id);
                intent.putExtra("room_type", "0");
                myContext.startActivity(intent);
            }
        });

        remove_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                try {
                    UserDetails.removefromadminchannel(parti_id,group_id,myContext,joined_at,created_by);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DBHandler dbHandler = new DBHandler(myContext);
                selected_item = parti_id;
                String statu = dbHandler.Getlockedstatus(parti_id);
                if (statu.equalsIgnoreCase("true") || statu.equalsIgnoreCase("1")) {
                    final Dialog dialog = new Dialog(myContext);
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

                            String confirm_password = dbHandler.GetPassword(parti_id);
                            if (password.getText().toString().equalsIgnoreCase(confirm_password)) {


                                SharedHelper.putKey(myContext, "single_chat_enable", "yes");
                                Intent intent = new Intent(myContext, ChatActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("user_name", dbHandler.GetUserName(parti_id));
                                intent.putExtra("image", dbHandler.GetUserImage(parti_id));
                                intent.putExtra("zoeChatID", parti_id);
                                intent.putExtra("groupId", "0");
                                intent.putExtra("chatRoomType", "0");

                                myContext.startActivity(intent);
                                dialog.dismiss();

                            } else {
                                Toast.makeText(myContext, "Password Mismatch. Kindly try again", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                    dialog.show();
                } else {

                    SharedHelper.putKey(myContext, "single_chat_enable", "yes");
                    Intent intent = new Intent(myContext, ChatActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("user_name", dbHandler.GetUserName(parti_id));
                    intent.putExtra("image", dbHandler.GetUserImage(parti_id));
                    intent.putExtra("zoeChatID", parti_id);
                    intent.putExtra("groupId", "0");
                    intent.putExtra("chatRoomType", "0");

                    myContext.startActivity(intent);
                }


            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    UserDetails.removefromgroup(parti_id, group_id, myContext);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.show();

    }

    private void showremoveadmindialogc(final String parti_id) {
        String from = SharedHelper.getKey(myContext, "my_zoe_id");
        final DBHandler dbHandler = new DBHandler(myContext);

        dialog = new Dialog(myContext);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.admin_dialog_remove);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        TextView message, view, make_admin, remove;
        message = (TextView) dialog.findViewById(R.id.message_group_part);
        view = (TextView) dialog.findViewById(R.id.view_group_part);
//        make_admin = (TextView) dialog.findViewById(R.id.make_admin);
        remove = (TextView) dialog.findViewById(R.id.remove_from_group);

        message.setText(myContext.getResources().getString(R.string.message) + " "+ dbHandler.GetUserName(parti_id));
        view.setText(myContext.getResources().getString(R.string.view) + " "+ dbHandler.GetUserName(parti_id));


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("onClick: ", "chatroomtype:" + "0");
                Intent intent = new Intent(myContext, UserDetails.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("zoeChatID", parti_id);
                intent.putExtra("user_name", dbHandler.GetUserName(parti_id));
                intent.putExtra("image", dbHandler.GetUserName(parti_id));
                intent.putExtra("group_id", group_id);
                intent.putExtra("room_type", "0");
                myContext.startActivity(intent);
            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DBHandler dbHandler = new DBHandler(myContext);
                selected_item = parti_id;
                String statu = dbHandler.Getlockedstatus(parti_id);
                if (statu.equalsIgnoreCase("true") || statu.equalsIgnoreCase("1")) {
                    final Dialog dialog = new Dialog(myContext);
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

                            String confirm_password = dbHandler.GetPassword(parti_id);
                            if (password.getText().toString().equalsIgnoreCase(confirm_password)) {


                                SharedHelper.putKey(myContext, "single_chat_enable", "yes");
                                Intent intent = new Intent(myContext, ChatActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("user_name", dbHandler.GetUserName(parti_id));
                                intent.putExtra("image", dbHandler.GetUserImage(parti_id));
                                intent.putExtra("zoeChatID", parti_id);
                                intent.putExtra("groupId", "0");
                                intent.putExtra("chatRoomType", "0");

                                myContext.startActivity(intent);
                                dialog.dismiss();

                            } else {
                                Toast.makeText(myContext, "Password Mismatch. Kindly try again", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                    dialog.show();
                } else {

                    SharedHelper.putKey(myContext, "single_chat_enable", "yes");
                    Intent intent = new Intent(myContext, ChatActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("user_name", dbHandler.GetUserName(parti_id));
                    intent.putExtra("image", dbHandler.GetUserImage(parti_id));
                    intent.putExtra("zoeChatID", parti_id);
                    intent.putExtra("groupId", "0");
                    intent.putExtra("chatRoomType", "0");

                    myContext.startActivity(intent);
                }


            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    UserDetails.removefromgroup(parti_id, group_id, myContext);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.show();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView user_image;
        TextView user_name, user_status, admin;

        public ViewHolder(View itemView) {
            super(itemView);

            user_image = (CircleImageView) itemView.findViewById(R.id.contact_image);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            user_status = (TextView) itemView.findViewById(R.id.user_status);
            admin = (TextView) itemView.findViewById(R.id.admin);
            customView(admin,myContext.getResources().getColor(R.color.white),getPrimaryCOlor(myContext));
            admin.setTextColor(getPrimaryCOlor(myContext));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DBHandler dbHandler=new DBHandler(myContext);
                    String isAdmin = dbHandler.GetChannelMember(ChatActivity.groupId_loc,list.optJSONObject(getAdapterPosition()).optString("participantId"));
                    String parti_id = list.optJSONObject(getAdapterPosition()).optString("participantId");
                    String created_by, joined_at = null;
                    String my_id = SharedHelper.getKey(myContext, "id");
                    if(dbHandler.GetChannelMember(ChatActivity.groupId_loc,my_id).equalsIgnoreCase("owner"))
                    {
                        amAdmin=true;
                    }
                    created_by = UserDetails.group_details_val.optString("createdBy");
                    try {
                        joined_at = list.getJSONObject(getAdapterPosition()).optString("joinedAt");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (!parti_id.equalsIgnoreCase(my_id)) {
                        if (!amAdmin) {
                            showapartidialog(parti_id);
                        } else {
                            if (isAdmin.equalsIgnoreCase("user")) {
                                showadmindialog(parti_id, created_by, joined_at);
                            } else if (isAdmin.equalsIgnoreCase("admin")) {
                                showremoveadmindialog(parti_id,joined_at,created_by);
                            }
                            else {
                                showremoveadmindialogc(parti_id);
                            }

                        }
                    }

                }
            });
        }
    }

    class hitapiforotp extends AsyncTask<String, String, String> implements AsyncTaskCompleteListener {


        String result = "";

        @Override
        protected String doInBackground(String... params) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", SharedHelper.getKey(myContext, "id"));
//                Log.d("doInBackground: ", "posting_values:" + jsonObject);
                internet = Utils.isNetworkAvailable(myContext);
                if (internet) {
                    new PostHelper(Const.Methods.OTP_REQUEST, jsonObject.toString(), Const.ServiceCode.PARTICIPANTS_DETAILS, myContext, this);
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

        private void showOTP(final String otp_val) {
            final Dialog dialog = new Dialog(myContext);
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
    }


}
