package com.bitware.coworker.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
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
import com.bitware.coworker.models.ContactsModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by KrishnaDev on 1/5/17.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private List<ContactsModel> list;
    private Context myContext;
    private String selecteditem;
    private boolean internet;


    public ContactAdapter(Context activity, List<ContactsModel> jsonArray) {
        this.list = jsonArray;
        this.myContext = activity;
    }
//    private List<Integer> getAllMaterialColors() throws IOException, XmlPullParserException {
//        XmlResourceParser xrp = myContext.getResources().getXml(R.xml.select_color);
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
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.contact_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


//        List<Integer> allColors = null;
//
//        try {
//            allColors = getAllMaterialColors();
//        } catch (IOException | XmlPullParserException e) {
//            e.printStackTrace();
//        }
//        final String f_let = String.valueOf(list.get(position).getName().charAt(0));
//        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
//        Log.d("onBindViewHolder: ","f_let"+f_let);
//        int color= alphabet.indexOf(f_let.toUpperCase());
//        Log.d("onBindViewHolder: ","color"+color);
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
//        final int randomColor = allColors.get(color);
//

        if (list.get(position).getImage().equalsIgnoreCase("") || list.get(position).getImage().equalsIgnoreCase(" ")) {
//            holder.user_image.setImageDrawable(new ColorDrawable(randomColor));
//            holder.persOnName.setText(f_let.toUpperCase());

            holder.persOnName.setVisibility(View.GONE);
            Picasso.with(myContext).load("qwe").placeholder(myContext.getResources().getDrawable(R.drawable.ic_account_circle)).error(myContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.user_image);

        } else {


            Picasso.with(myContext).load(list.get(position).getImage()).placeholder(myContext.getResources().getDrawable(R.drawable.ic_account_circle)).error(myContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.user_image);

        }




        DBHandler dbHandler = new DBHandler(myContext);
        String isLocked = list.get(position).getZeoChatId();
        String status = dbHandler.Getlockedstatus(isLocked);

        if (status.equalsIgnoreCase("1") || isLocked.equalsIgnoreCase("true")) {
            holder.lock_button.setVisibility(View.VISIBLE);
        } else {
            holder.lock_button.setVisibility(View.INVISIBLE);
        }





        holder.user_name.setText(list.get(position).getName());
        holder.user_status.setText(list.get(position).getStatus());

//        final List<Integer> finalAllColors = allColors;
        holder.user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CustomDialog customDialog = new CustomDialog(myContext);
                customDialog.setContentView(R.layout.custom_dialog);
                ImageView userImage = (ImageView) customDialog.findViewById(R.id.user_image);
                TextView proName = (TextView) customDialog.findViewById(R.id.text_name_per);
                TextView username = (TextView) customDialog.findViewById(R.id.user_name);

                username.setText(list.get(position).getName());

                if (list.get(position).getImage().equalsIgnoreCase("") || list.get(position).getImage().equalsIgnoreCase(" ")) {

            Picasso.with(myContext).load(R.drawable.ic_account_circle).placeholder(myContext.getResources().getDrawable(R.drawable.ic_account_circle)).error(myContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.user_image);


//                    String f_let = String.valueOf(list.get(position).getName().charAt(0));
//                    String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
//                    Log.d("onBindViewHolder: ","f_let"+f_let);
//                    int color= alphabet.indexOf(f_let.toUpperCase());
//                    Log.d("onBindViewHolder: ","color"+color);
//                    if (color==-1)
//                    {
//                        String number="0123456789";
//                        color= number.indexOf(f_let);
//                        if (color==-1)
//                        {
//                            String regExSpecialChars = "<([{\\^-=$!|]})?*+.>";
//                            color= regExSpecialChars.indexOf(f_let);
//                        }
//                    }
//
//                    int randomColor = finalAllColors.get(color);
//
////                Drawable image = context.getResources().getDrawable(R.drawable.bar);
//
//
//                    Log.d("onBindViewHolder: ","ccc:"+randomColor+" "+ finalAllColors.get(5));
//                    userImage.setImageDrawable(new ColorDrawable(randomColor));

//                    proName.setText(f_let.toUpperCase());
                    proName.setVisibility(View.GONE);
                } else {
                proName.setVisibility(View.GONE);

                    Picasso.with(myContext).load(list.get(position).getImage()).placeholder(myContext.getResources().getDrawable(R.drawable.ic_account_circle)).error(myContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(userImage);

                }

                ImageView msg = (ImageView) customDialog.findViewById(R.id.msg);

                msg.setImageDrawable(myContext.getResources().getDrawable(R.drawable.ic_chat_app_color));

//                ImageView about = (ImageView) customDialog.findViewById(R.id.about);

//                about.setImageDrawable(myContext.getResources().getDrawable(R.drawable.ic_error_outline));

                msg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selecteditem = list.get(position).getZeoChatId();
                        final DBHandler dbHandler = new DBHandler(myContext);
                        String statu = dbHandler.Getlockedstatus(selecteditem);
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

                                    String confirm_password = dbHandler.GetPassword(selecteditem);
                                    if (password.getText().toString().equalsIgnoreCase(confirm_password)) {


                                        SharedHelper.putKey(myContext, "single_chat_enable", "yes");
                                        Intent intent = new Intent(myContext, ChatActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("user_name", list.get(position).getName());
                                        intent.putExtra("image", list.get(position).getImage());
                                        intent.putExtra("zoeChatID", list.get(position).getZeoChatId());
                                        intent.putExtra("groupId", "0");
                                        intent.putExtra("chatRoomType", "0");
                                        FCMMsgService.current_id = list.get(position).getZeoChatId();
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
                            intent.putExtra("user_name", list.get(position).getName());
                            intent.putExtra("image", list.get(position).getImage());
                            intent.putExtra("zoeChatID", list.get(position).getZeoChatId());
                            intent.putExtra("groupId", "0");
                            intent.putExtra("chatRoomType", "0");
                            FCMMsgService.current_id = list.get(position).getZeoChatId();
                            myContext.startActivity(intent);
                        }
                        customDialog.dismiss();
                    }
                });

                customDialog.show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setFilter(List<ContactsModel> filter) {
        list = new ArrayList<>();
        list.addAll(filter);
        notifyDataSetChanged();
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

                    dbHandler.Updatelock(selecteditem, "1");
                    dbHandler.UpdatePassword(selecteditem, password.getText().toString());
                    MainActivity.mainActivity.toolbar.getMenu().clear();
                    dialog.dismiss();
                } else {
                    Toast.makeText(myContext, "Password Mismatch. Kindly try again", Toast.LENGTH_SHORT).show();
                }


            }
        });

        dialog.show();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView user_image;
        TextView user_name, user_status,persOnName;
        ImageView lock_button;

        public ViewHolder(View itemView) {
            super(itemView);

            user_image = (CircleImageView) itemView.findViewById(R.id.contact_image);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            user_status = (TextView) itemView.findViewById(R.id.user_status);
            lock_button = (ImageView) itemView.findViewById(R.id.lock_status);
            persOnName = (TextView) itemView.findViewById(R.id.text_name_per);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    selecteditem = list.get(getAdapterPosition()).getZeoChatId();
                    final DBHandler dbHandler = new DBHandler(myContext);
                    String statu = dbHandler.Getlockedstatus(selecteditem);
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

                                String confirm_password = dbHandler.GetPassword(selecteditem);
                                if (password.getText().toString().equalsIgnoreCase(confirm_password)) {


                                    SharedHelper.putKey(myContext, "single_chat_enable", "yes");
                                    Intent intent = new Intent(myContext, ChatActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("user_name", list.get(getAdapterPosition()).getName());
                                    intent.putExtra("image", list.get(getAdapterPosition()).getImage());
                                    intent.putExtra("zoeChatID", list.get(getAdapterPosition()).getZeoChatId());
                                    intent.putExtra("groupId", "0");
                                    intent.putExtra("chatRoomType", "0");
                                    FCMMsgService.current_id = list.get(getAdapterPosition()).getZeoChatId();
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
                        intent.putExtra("user_name", list.get(getAdapterPosition()).getName());
                        intent.putExtra("image", list.get(getAdapterPosition()).getImage());
                        intent.putExtra("zoeChatID", list.get(getAdapterPosition()).getZeoChatId());
                        intent.putExtra("groupId", "0");
                        intent.putExtra("chatRoomType", "0");
                        FCMMsgService.current_id = list.get(getAdapterPosition()).getZeoChatId();
                        myContext.startActivity(intent);
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
