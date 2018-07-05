package com.bitware.coworker.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
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
import com.bitware.coworker.baseUtils.AsyncTaskCompleteListener;
import com.bitware.coworker.baseUtils.Const;
import com.bitware.coworker.baseUtils.CustomDialog;
import com.bitware.coworker.baseUtils.PostHelper;
import com.bitware.coworker.baseUtils.SharedHelper;
import com.bitware.coworker.baseUtils.Utils;
import com.bitware.coworker.fragment.CallsFragment;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by KrishnaDev on 1/30/17.
 */
public class CallsAdapter extends RecyclerView.Adapter<CallsAdapter.ViewHolder> {
    public static boolean is_in_action_mode = false;
    public static ArrayList<String> selectRemoveItem;
    public static ArrayList<String> selectRemoveItemcall_time;
    private List<JSONObject> jsonArray;
    private boolean internet;
    private Context context;
    private String selecteditem;
    public static void customView(View v, int backgroundColor, int borderColor)
    {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[] { 50, 50, 50, 50, 50, 50, 50, 50 });
        shape.setColor(backgroundColor);
        shape.setStroke(3, borderColor);
        v.setBackgroundDrawable(shape);
    }

    public static int getPrimaryCOlor (Context context) {
        final TypedValue value = new TypedValue ();
        context.getTheme ().resolveAttribute (R.attr.colorPrimary, value, true);
        return value.data;
    }
    public static int getPrimaryDark (Context context) {
        final TypedValue value = new TypedValue ();
        context.getTheme ().resolveAttribute (R.attr.colorPrimaryDark, value, true);
        return value.data;
    }

    public CallsAdapter(FragmentActivity activity, List<JSONObject> chatsList) {
        this.jsonArray = chatsList;
        this.context = activity;
        selectRemoveItem = new ArrayList<>();
        selectRemoveItemcall_time=new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.calls_fragment, parent, false);
        return new ViewHolder(view);
    }

    private List<Integer> getAllMaterialColors() throws IOException, XmlPullParserException {
        XmlResourceParser xrp = context.getResources().getXml(R.xml.select_color);
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


    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

//        List<Integer> allColors = null;
//
//        try {
//            allColors = getAllMaterialColors();
//        } catch (IOException | XmlPullParserException e) {
//            e.printStackTrace();
//        }
//
//
//
//        final String f_let = String.valueOf(jsonArray.get(position).optString("username").charAt(0));
//        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
////        Log.d("onBindViewHolder: ","f_let"+f_let);
//        int color= alphabet.indexOf(f_let.toUpperCase());
////        Log.d("onBindViewHolder: ","color"+color);
//        if (color==-1)
//        {
//            String number="0123456789";
//            color= number.indexOf(f_let);
//            if (color==-1)
//            {
//                String regExSpecialChars = "<([{\\^-=$!|]})?*+.>";
//                color= regExSpecialChars.indexOf(f_let);
//            }
//        }
//
//        int randomColor = allColors.get(color);

//        Log.d("onBindViewHolder: ","ccc:"+randomColor+" "+allColors.get(5));
        if (jsonArray.get(position).optString("userImage").equalsIgnoreCase(" ")||jsonArray.get(position).optString("userImage").equalsIgnoreCase("")) {
            Picasso.with(context).load(R.drawable.ic_account_circle).placeholder(context.getResources().getDrawable(R.drawable.ic_account_circle)).error(context.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.circularImageView);

//            holder.circularImageView.setImageDrawable(new ColorDrawable(randomColor));

//            holder.persOnName.setText(f_let.toUpperCase());
            holder.persOnName.setVisibility(View.GONE);

        } else {
            holder.persOnName.setVisibility(View.GONE);
            Picasso.with(context).load(jsonArray.get(position).optString("userImage")).placeholder(R.drawable.ic_account_circle).error(R.drawable.ic_account_circle).into(holder.circularImageView);
        }


        holder.Name.setText(jsonArray.get(position).optString("username"));
        if (jsonArray.get(position).optString("callLog").equalsIgnoreCase("0")) {

            holder.call_status.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_call_made_black_24dp));


        } else if (jsonArray.get(position).optString("callLog").equalsIgnoreCase("1")) {

            holder.call_status.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_call_received_black_24dp));



        }
        if (jsonArray.get(position).optString("callType").equalsIgnoreCase("VoiceCall")) {

            holder.callType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_call_color));


        } else if (jsonArray.get(position).optString("callType").equalsIgnoreCase("VideoCall")) {

            holder.callType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_videocam));


        }

        try {
            double time = Double.parseDouble(jsonArray.get(position).optString("call_time"));
            String date = Utils.getDate((long) time, "dd/MM/yyyy");
            String times = Utils.getDate((long) time, "hh:mm a");
            String sortDate = Utils.formatToYesterdayOrToday(date);
            holder.callTime.setText(sortDate + ", " + times);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!is_in_action_mode) {
                    MainActivity.mainActivity.toolbar.getMenu().clear();
                    MainActivity.mainActivity.toolbar.inflateMenu(R.menu.action_mode_menu_multi);
                    MainActivity.mainActivity.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
                    MainActivity.mainActivity.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CallsFragment.mainActivity.clearActionM();
                        }
                    });
                    is_in_action_mode = true;
                    notifyDataSetChanged();
                    CallsFragment.counter++;
                    updateCnt(CallsFragment.counter);
                    holder.remove_view.setVisibility(View.VISIBLE);
                    holder.itemView.setBackgroundResource(R.color.light_weight_gray);

                    String id = jsonArray.get(position).optString("zoeChat_id");
                    String call_tme = jsonArray.get(position).optString("call_time");
                    addItem(id, call_tme);

                    return true;
                }
                return false;
            }
        });


        holder.circularImageView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                final CustomDialog customDialog = new CustomDialog(context);
                customDialog.setContentView(R.layout.custom_dialog);

                ImageView userImage = (ImageView) customDialog.findViewById(R.id.user_image);
                TextView username = (TextView) customDialog.findViewById(R.id.user_name);
                TextView proName = (TextView) customDialog.findViewById(R.id.text_name_per);

                username.setText(jsonArray.get(position).optString("Name"));

                if (jsonArray.get(position).optString("userImage").equalsIgnoreCase(" ")||jsonArray.get(position).optString("userImage").equalsIgnoreCase("")) {

                    String f_let = String.valueOf(jsonArray.get(position).optString("username").charAt(0));
                    String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
//                    Log.d("onBindViewHolder: ","f_let"+f_let);
                    int color= alphabet.indexOf(f_let.toUpperCase());
//                    Log.d("onBindViewHolder: ","color"+color);
                    if (color==-1)
                    {
                        String number="0123456789";
                        color= number.indexOf(f_let);
                        if (color==-1)
                        {
                            String regExSpecialChars = "<([{\\^-=$!|]})?*+.>";
                            color= regExSpecialChars.indexOf(f_let);
                        }
                    }


                    Picasso.with(context).load(R.drawable.ic_account_circle).placeholder(context.getResources().getDrawable(R.drawable.ic_account_circle)).error(context.getResources().getDrawable(R.drawable.ic_account_circle)).into(userImage);

//                    userImage.setImageDrawable(new ColorDrawable(randomColor));
//                    proName.setText(f_let.toUpperCase());
                    proName.setVisibility(View.GONE);

                } else {
                    proName.setVisibility(View.GONE);
                    Picasso.with(context).load(jsonArray.get(position).optString("userImage")).placeholder(context.getResources().getDrawable(R.drawable.ic_account_circle)).error(context.getResources().getDrawable(R.drawable.ic_account_circle)).into(userImage);
                }

                ImageView msg = (ImageView) customDialog.findViewById(R.id.msg);
//                ImageView about = (ImageView) customDialog.findViewById(R.id.about);
                msg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selecteditem=jsonArray.get(position).optString("zoeChat_id");

                        final DBHandler dbHandler = new DBHandler(context);
                        String statu = dbHandler.Getlockedstatus(jsonArray.get(position).optString("zoeChat_id"));

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

                                    String confirm_password = dbHandler.GetPassword(jsonArray.get(position).optString("zoeChat_id"));
                                    if (password.getText().toString().equalsIgnoreCase(confirm_password)) {

                                        SharedHelper.putKey(context, "single_chat_enable", "yes");
                                        Intent intent = new Intent(context, ChatActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("user_name", jsonArray.get(position).optString("username"));
                                        intent.putExtra("image", jsonArray.get(position).optString("userImage"));
                                        intent.putExtra("zoeChatID", jsonArray.get(position).optString("zoeChat_id"));
                                        intent.putExtra("chatRoomType", "0");
                                        intent.putExtra("groupId", "0");
                                        intent.putExtra("grp_image", jsonArray.get(position).optString(""));
                                        context.startActivity(intent);
                                        dialog.dismiss();

                                    } else {
                                        Toast.makeText(context, "Password Mismatch. Kindly try again", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                            dialog.show();
                        }
                        else{

                        SharedHelper.putKey(context, "single_chat_enable", "yes");
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("user_name", jsonArray.get(position).optString("username"));
                        intent.putExtra("image", jsonArray.get(position).optString("userImage"));
                        intent.putExtra("zoeChatID", jsonArray.get(position).optString("zoeChat_id"));
                        intent.putExtra("chatRoomType", "0");
                        intent.putExtra("groupId", "0");
                        intent.putExtra("grp_image", jsonArray.get(position).optString(""));

                        context.startActivity(intent);


                        customDialog.dismiss();
                    }
                    } });

//                about.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent = new Intent(context, UserDetails.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        intent.putExtra("user_name", jsonArray.get(position).optString("username"));
//                        intent.putExtra("image", jsonArray.get(position).optString("userImage"));
//                        intent.putExtra("zoeChatID", jsonArray.get(position).optString("zoeChat_id"));
//                        context.startActivity(intent);
//                        customDialog.dismiss();
//                    }
//                });
                customDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return jsonArray.size();
    }

    public void setFilter(List<JSONObject> filter) {
        jsonArray = new ArrayList<>();
        jsonArray.addAll(filter);
        notifyDataSetChanged();
    }

    private void addItem(String zoeChatID, String call_tme) {
        selectRemoveItem.add(zoeChatID);
        selectRemoveItemcall_time.add(call_tme);
        Log.e("items", selectRemoveItem.toString());
    }

    private void updateCnt(int counter) {
        if (counter == 0) {
            CallsFragment.mainActivity.clearActionM();
            selectRemoveItem.clear();
        } else {
            MainActivity.mainActivity.counterTextView.setText(counter + "  selected");
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circularImageView;
        TextView Name, callTime,persOnName;
        ImageView call_status, callType, remove_view;

        public ViewHolder(final View itemView) {
            super(itemView);

            circularImageView = (CircleImageView) itemView.findViewById(R.id.contact_image);
            Name = (TextView) itemView.findViewById(R.id.user_name);
            callTime = (TextView) itemView.findViewById(R.id.call_time);
            persOnName = (TextView) itemView.findViewById(R.id.text_name_per);

            remove_view = (ImageView) itemView.findViewById(R.id.remove_view_check);
            customView(remove_view,getPrimaryCOlor(context),getPrimaryCOlor(context));
            call_status = (ImageView) itemView.findViewById(R.id.call_status);
            callType = (ImageView) itemView.findViewById(R.id.call_type);

            remove_view = (ImageView) itemView.findViewById(R.id.remove_view_check);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (is_in_action_mode) {
                        if (remove_view.getVisibility() == View.VISIBLE) {
                            Log.e("view_", "Visible");
                            remove_view.setVisibility(View.GONE);
                            itemView.setBackgroundResource(0);
                            CallsFragment.counter--;
                            updateCnt(CallsFragment.counter);
                        } else {
                            Log.e("view_", "UNVisible");
                            remove_view.setVisibility(View.VISIBLE);
                            itemView.setBackgroundResource(R.color.light_weight_gray);
                            CallsFragment.counter++;
                            updateCnt(CallsFragment.counter);
                            addItem(jsonArray.get(getAdapterPosition()).optString("zoeChat_id"), jsonArray.get(getAdapterPosition()).optString("call_time"));
                        }

                    } else {
                        remove_view.setVisibility(View.GONE);
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

   public void showOTP(final String otp_val) {
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
        if (i == 0||i==3) {
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
                            dbpassword=dbHandler.GetPassword(selecteditem);

                        } else {
                            dbpassword = dbHandler.GetPassword(ChatsAdapter.selectRemoveItem.get(0));
                        }
                        if (password.getText().toString().equalsIgnoreCase(dbpassword)) {
                            Log.d("onClick: ", "Chatadapter:" + ChatsAdapter.selectRemoveItem.get(0));
                            dbHandler.Updatelock(ChatsAdapter.selectRemoveItem.get(0), "1");
                            dbHandler.UpdatePassword(ChatsAdapter.selectRemoveItem.get(0), password.getText().toString());
                            MainActivity.mainActivity.toolbar.getMenu().clear();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(context, "Kindly Enter the correct password", Toast.LENGTH_SHORT).show();
                        }

                    } else if (i== 3)
                    {

                        if (password.getText().toString().equalsIgnoreCase(confirm_password.getText().toString())) {

                            dbHandler.Updatelock(selecteditem, "1");
                            dbHandler.UpdatePassword(selecteditem, password.getText().toString());

                            MainActivity.mainActivity.toolbar.getMenu().clear();
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




}
