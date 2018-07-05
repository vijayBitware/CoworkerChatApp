package com.bitware.coworker.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.bitware.coworker.DBHelper.DBHandler;
import com.bitware.coworker.R;
import com.bitware.coworker.Service.ServiceClasss;
import com.bitware.coworker.baseUtils.Const;
import com.bitware.coworker.baseUtils.SharedHelper;
import com.bitware.coworker.baseUtils.Utils;
import com.bitware.coworker.fragment.ChatFragment;
import com.bitware.coworker.fragment.ContactFragment;
import com.bitware.coworker.models.ChatMessages;
import com.bitware.coworker.models.ChatType;
import com.bitware.coworker.models.ChatsMessagesModel;
import com.bitware.coworker.models.ContactsModel;
import com.bitware.coworker.models.GroupParticiapntsModel;
import com.bitware.coworker.models.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.IO;
import io.socket.client.Socket;

public class NewGroup_activity extends AppCompatActivity {

    public static JSONArray particpants_array;
    public static JSONObject group_details;
    public static Socket socket;
    public static Boolean isChannel = false;
    DBHandler dbHandler;
    public static List<ContactsModel> list_of_contacts;
    String room_type;
    ForwardMessage forwardMessage;
    GroupAdapterList grpadapterlist;
    ContactAdapterList contactAdapterList;
    List<JSONObject> participantsList;
    List<JSONObject> participants_List;
    List<JSONObject> grpparticipantsList;
    List<JSONObject> grpparticipants_List;
    String thorugh_add = "false";
    private RecyclerView contact_list;
    private ImageView next;
    private boolean select = false;
    private boolean internet;
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
//
//
//    };

    public static JSONArray sortJsonArray(JSONArray array) throws JSONException {
        List<JSONObject> jsons = new ArrayList<JSONObject>();
        for (int i = 0; i < array.length(); i++) {
            jsons.add(array.getJSONObject(i));
        }
        Collections.sort(jsons, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                String lid = null;
                String rid = null;
                try {
                    lid = lhs.getString("Name");

                    rid = rhs.getString("Name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Here you could parse string id to integer and then compare.
                return lid.compareTo(rid);
            }
        });
        return new JSONArray(jsons);
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

    public static void customView(View v, int backgroundColor, int borderColor) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[]{100, 100, 100, 100, 100, 100, 100, 100});
        shape.setColor(backgroundColor);
        shape.setStroke(3, borderColor);
        v.setBackgroundDrawable(shape);
    }

    public static void customViews(View v, int backgroundColor, int borderColor) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[]{10, 10, 10, 10, 10, 10, 10, 10});
        shape.setColor(backgroundColor);
        shape.setStroke(3, borderColor);
        v.setBackgroundDrawable(shape);
    }

    public static void addgrouptrigger(JSONObject jsonObject, Context context) {
        DBHandler dbHandler = new DBHandler(context);
        long millis = System.currentTimeMillis();

        String msg_value = "You added " + dbHandler.GetUserName(jsonObject.optString("participantId"));
        String uniqueID = UUID.randomUUID().toString();
        dbHandler.InsertChatMsg(new ChatsMessagesModel(uniqueID.trim(), SharedHelper.getKey(context, "id"), msg_value, ChatType.editGroupName.toString(), Status.SENDING.toString(), ChatMessages.CREATE_GROUP, ChatActivity.groupId_loc, "" + millis, "0", "0", 0, 0, 0, "0.0", "0.0", "", "", "1", "false", "", "", 0, "", "", "", ""));
        dbHandler.UpdateLastMsg(jsonObject.optString("groupId"), SharedHelper.getKey(context, "id"), msg_value, "", "" + millis, ChatMessages.CREATE_GROUP, 0);

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
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
        setContentView(R.layout.activity_new_group_activity);
        internet = Utils.isNetworkAvailable(NewGroup_activity.this);
        dbHandler = new DBHandler(NewGroup_activity.this);
        IO.Options opts = new IO.Options();
        opts.forceNew = true;
        opts.reconnection = false;
        try {
            ContactFragment.refresh.clearActionM();
        } catch (Exception e) {

        }

        try {
            socket = IO.socket(Const.chatSocketURL, opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.e("SOCKET.IO ", e.getMessage());
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.group_toolbar);
        toolbar.setBackgroundColor(getPrimaryCOlor(NewGroup_activity.this));

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //part_list = (RecyclerView) findViewById(R.id.participants_list);
        contact_list = (RecyclerView) findViewById(R.id.contact_list);
        next = (ImageView) findViewById(R.id.next);
        customView(next, getPrimaryCOlor(NewGroup_activity.this), getPrimaryCOlor(NewGroup_activity.this));


        if (ChatFragment.isGroup.equalsIgnoreCase("false")) {
            next.setVisibility(View.VISIBLE);
        } else if (ChatFragment.isGroup.equalsIgnoreCase("Channel")) {
            next.setVisibility(View.VISIBLE);

        } else {
            next.setVisibility(View.GONE);
        }
        participantsList = new ArrayList<>();
        participants_List = new ArrayList<>();
        grpparticipantsList = new ArrayList<>();
        grpparticipants_List = new ArrayList<>();
        final Intent intent = getIntent();
        try {
            if (ChatFragment.isGroup.equalsIgnoreCase("true")) {
                String forward_mesg = "";


                if (intent.getStringExtra("forward_msg").equalsIgnoreCase("true")) {
                    toolbar.setTitle(getResources().getString(R.string.forward_to));
                    toolbar.setTitleTextColor(Color.WHITE);
                    String message = intent.getStringExtra("message");
                    String message_type = intent.getStringExtra("message_type");
                    forwardMessage = new ForwardMessage(NewGroup_activity.this, ContactFragment.list_of_contacts, message, message_type);
                    contact_list.setHasFixedSize(true);
                    contact_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    contact_list.setAdapter(forwardMessage);

                } else {

                    thorugh_add = intent.getStringExtra("through_add");

                    toolbar.setTitle(getResources().getString(R.string.add_part));
                    toolbar.setTitleTextColor(Color.WHITE);
                    toolbar.setSubtitle(getResources().getString(R.string.select_part));


                    try {
                        particpants_array = new JSONArray(intent.getStringExtra("particpants_array_val"));
                        group_details = new JSONObject(intent.getStringExtra("group_details"));
                        room_type = intent.getStringExtra("room_type");

                        Log.d("onCreate: ", "new_parti_detaills:" + intent.getStringExtra("particpants_array_val"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (ContactFragment.list_of_contacts.size() > 0) {

                        grpadapterlist = new GroupAdapterList(NewGroup_activity.this, ContactFragment.list_of_contacts, particpants_array);
                        contact_list.setHasFixedSize(true);
                        contact_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        contact_list.setAdapter(grpadapterlist);
                    }
                }

            } else if (ChatFragment.isGroup.equalsIgnoreCase("Channel")) {
                ChatFragment.isGroup = "";
                isChannel = true;
                toolbar.setTitle(getResources().getString(R.string.add_friends));
                toolbar.setTitleTextColor(Color.WHITE);
                toolbar.setSubtitleTextColor(Color.WHITE);
                toolbar.setSubtitle(getResources().getString(R.string.select_contacts));


                if (ContactFragment.list_of_contacts.size() > 0) {
                    contactAdapterList = new ContactAdapterList(NewGroup_activity.this, ContactFragment.list_of_contacts);
                    contact_list.setHasFixedSize(true);
                    contact_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    contact_list.setAdapter(contactAdapterList);
                } else {

                }
            } else if (ChatFragment.isGroup.equalsIgnoreCase("Invite")) {

                ChatFragment.isGroup = "";
                toolbar.setTitle(getResources().getString(R.string.invite_your));
                toolbar.setTitleTextColor(Color.WHITE);
                toolbar.setSubtitle(getResources().getString(R.string.select_contacts));

                JSONArray fullcontacrs = dbHandler.GetAllUnRegUsers();
                Log.d("onCreate: ", "UnregisteredU:" + fullcontacrs);
                JSONArray sorted = new JSONArray();

                try {
                    sorted = sortJsonArray(fullcontacrs);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                InviteAdapter inviteAdapter = new InviteAdapter(NewGroup_activity.this, sorted);
                contact_list.setHasFixedSize(true);
                contact_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                contact_list.setAdapter(inviteAdapter);
            } else {
                isChannel = false;
                toolbar.setTitle(getResources().getString(R.string.new_group));
                toolbar.setTitleTextColor(Color.WHITE);
                toolbar.setSubtitle(getResources().getString(R.string.add_part));
                Log.d("onCreate: ", "came2:" + ContactFragment.list_of_contacts.size());
                Log.e("group", "" + ContactFragment.list_of_contacts.toString());
                dbHandler = new DBHandler(this);
                list_of_contacts = dbHandler.GetListOfContacts();

                Collections.sort(list_of_contacts, new Comparator<ContactsModel>() {
                    @Override
                    public int compare(ContactsModel getUserFromDB, ContactsModel t1) {
                        return getUserFromDB.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
                    }
                });
                if (list_of_contacts.size() > 0) {
                    contactAdapterList = new ContactAdapterList(NewGroup_activity.this, list_of_contacts);
                    contact_list.setHasFixedSize(true);
                    contact_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    contact_list.setAdapter(contactAdapterList);
                } else {

                }

            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (participantsList.size() <= 0) {
                    Toast.makeText(NewGroup_activity.this, getResources().getString(R.string.empty_part), Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(NewGroup_activity.this, SetGroupName_activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("part_list", participantsList.toString());
                    intent.putExtra("select_list", participants_List.toString());
                    startActivity(intent);
                }

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

//        socket.on(Socket.EVENT_CONNECT, onConnect);
//        socket.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();

//        socket.disconnect();
    }

    private void addgroup(final String id, String grp_name, String grp_image) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        String from = SharedHelper.getKey(NewGroup_activity.this, "my_zoe_id");
        Log.d("addgroup: ", "values:" + from + "," + grp_name + "," + grp_image + "," + ChatActivity.groupId_loc);
        jsonObject.put("groupId", ChatActivity.groupId_loc);
        jsonObject.put("from", from);
        jsonObject.put("groupName", grp_name);
        jsonObject.put("groupImage", grp_image);
        jsonObject.put("participantId", id);

        Log.d("addgroup: ", "" + jsonObject);


        ServiceClasss.Emitters emitters = new ServiceClasss.Emitters(NewGroup_activity.this);
        Boolean res = emitters.addParticipants(jsonObject);

        if (res) {

        }


    }

    private boolean isValueexists(JSONArray jsonArray, String usernameToFind) {
        return jsonArray.toString().contains("\"participantId\":\"" + usernameToFind + "\"");
    }

    private void openmessages(String mobile_no) {
        Uri uri = Uri.parse("smsto:" + mobile_no);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", "Hey, Check out Coworker Messenger to connect with your friends,family and Colleagues . Download it today from:  https://play.google.com/store/apps/details?id=com.bitware.coworker");
        startActivity(it);
    }

    private void adddb(String sel_id) {

        dbHandler.AddGroupParticipants(new GroupParticiapntsModel(sel_id, ChatActivity.groupId_loc, String.valueOf(System.currentTimeMillis()), SharedHelper.getKey(NewGroup_activity.this, "id"), "0"));
        Intent intent = new Intent(NewGroup_activity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void forwardmessage(Context mContext, String sel_id, String message, String message_type) {

        SharedHelper.putKey(mContext, "single_chat_enable", "yes");
        Intent intent = new Intent(mContext, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("zoeChatID", sel_id);
        intent.putExtra("groupId", "0");
        intent.putExtra("chatRoomType", "0");
        intent.putExtra("user_name", dbHandler.GetUserName(sel_id));
        intent.putExtra("image", dbHandler.GetUserImage(sel_id));
        intent.putExtra("zoeChatID", sel_id);
        intent.putExtra("send_message", "yes");
        intent.putExtra("message", message);
        intent.putExtra("message_type", message_type);
        mContext.startActivity(intent);


    }

    private class ContactAdapterList extends RecyclerView.Adapter<ContactAdapterList.MyViewHolder> {
        private List<ContactsModel> list;
        private Context mContext;


        public ContactAdapterList(NewGroup_activity newGroup_activity, List<ContactsModel> list_of_contacts) {
            this.list = list_of_contacts;
            this.mContext = newGroup_activity;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.contact_list_item, parent, false);
            return new ContactAdapterList.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            if (list.get(position).getImage().equalsIgnoreCase("") || list.get(position).getImage().equalsIgnoreCase(" ")) {

                Picasso.with(mContext).load(R.drawable.ic_account_circle)
                        .error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.user_image);
            } else {
                Picasso.with(mContext).load(list.get(position).getImage()).into(holder.user_image);
            }


            holder.user_name.setText(list.get(position).getName());
            holder.user_status.setText(list.get(position).getStatus());

            if (list.get(position).getSelected()) {
                holder.click.setVisibility(View.VISIBLE);
            } else {
                holder.click.setVisibility(View.GONE);

            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (holder.click.getVisibility() == View.VISIBLE) {
                        String clickId = list.get(position).getZeoChatId();
                        if (participantsList.size() == 1) {
                            participantsList.clear();
                            participants_List.clear();
                        } else {
                            for (int i = 0; i < participantsList.size(); i++) {
                                String id = participantsList.get(i).optString("zoeChatId");
                                if (id.equalsIgnoreCase(clickId)) {
                                    participantsList.remove(i);
                                    participants_List.remove(i);
                                    break;
                                }
                            }
                        }

                        list.get(position).setSelected(false);
                        notifyDataSetChanged();
                        Log.d("addedList", "" + participantsList);
                        Log.d("select_list", "" + participants_List);
                        //addedAdapter.notifyDataSetChanged();

                    } else if (holder.click.getVisibility() == View.GONE) {

                        JSONObject jsonObject = new JSONObject();
                        JSONObject pat_Obj = new JSONObject();
                        try {
                            jsonObject.put("name", list.get(position).getName());
                            jsonObject.put("image", list.get(position).getImage());
                            jsonObject.put("zoeChatId", list.get(position).getZeoChatId());

                            pat_Obj.put("participantId", list.get(position).getZeoChatId());

                            if (isChannel) {
                                if (list.get(position).getZeoChatId().equalsIgnoreCase(SharedHelper.getKey(NewGroup_activity.this, "my_zoe_id"))) {
                                    pat_Obj.put("participantType", "owner");
                                } else {
                                    pat_Obj.put("participantType", "user");
                                }
                            } else {
                                if (list.get(position).getZeoChatId().equalsIgnoreCase(SharedHelper.getKey(NewGroup_activity.this, "my_zoe_id"))) {
                                    pat_Obj.put("isAdmin", "1");
                                } else {
                                    pat_Obj.put("isAdmin", "0");
                                }
                            }
                            jsonObject.put("status", list.get(position).getStatus());
                            participantsList.add(jsonObject);
                            participants_List.add(pat_Obj);
                            list.get(position).setSelected(true);
                            notifyDataSetChanged();

                            //addedAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }
            });


        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            CircleImageView user_image;
            TextView user_name, user_status;
            ImageView click;

            public MyViewHolder(View itemView) {
                super(itemView);
                user_image = (CircleImageView) itemView.findViewById(R.id.contact_image);
                user_name = (TextView) itemView.findViewById(R.id.user_name);
                user_status = (TextView) itemView.findViewById(R.id.user_status);
                click = (ImageView) itemView.findViewById(R.id.remove_view_check);
                customView(click, getPrimaryCOlor(NewGroup_activity.this), getPrimaryCOlor(NewGroup_activity.this));


            }
        }
    }

    private class GroupAdapterList extends RecyclerView.Adapter<GroupAdapterList.MyViewHolder> {
        JSONArray parti_value;
        //        private List<String> list;
        private Context mContext;
        private List<ContactsModel> list;

//        public GroupAdapterList(NewGroup_activity newGroup_activity, List<String> list_of_contacts) {
//            this.list = list_of_contacts;
//            this.mContext = newGroup_activity;
//        }

        public GroupAdapterList(NewGroup_activity newGroup_activity, List<ContactsModel> list_of_contacts, JSONArray particpants_array) {
            this.mContext = newGroup_activity;
            this.list = list_of_contacts;
            this.parti_value = particpants_array;

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.contact_list_item, parent, false);
            return new GroupAdapterList.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            Log.d("onBindViewHolder: ", "list_values:" + list.get(position));

            DBHandler dbHandler = new DBHandler(mContext);

            if (list.get(position).getImage().equalsIgnoreCase("") || list.get(position).getImage().equalsIgnoreCase(" ")) {
                Picasso.with(mContext).load(R.drawable.ic_account_circle)
                        .error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.user_image);
            } else {
                Picasso.with(mContext).load(list.get(position).getImage()).placeholder(R.drawable.ic_account_circle).into(holder.user_image);

            }


            if (isValueexists(parti_value, list.get(position).getZeoChatId())) {
                holder.user_name.setText(list.get(position).getName());
                holder.user_status.setText(getResources().getString(R.string.already_in_group));
                holder.user_name.setTextColor(mContext.getResources().getColor(R.color.grey));
                holder.user_status.setTextColor(mContext.getResources().getColor(R.color.grey));
                holder.itemView.setEnabled(false);
                holder.itemView.setClickable(false);

            } else {
                holder.user_name.setText(list.get(position).getName());
                holder.user_status.setText(list.get(position).getStatus());
                holder.user_name.setTextColor(mContext.getResources().getColor(R.color.black));
                holder.user_status.setTextColor(mContext.getResources().getColor(R.color.black));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        JSONObject pat_Obj = new JSONObject();

                        final String sel_id = list.get(position).getZeoChatId();
                        final String sel_name = list.get(position).getZeoChatId();

                        try {
                            pat_Obj.put("participantId", list.get(position));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        grpparticipants_List.add(pat_Obj);

                        Log.d("select_list", "" + grpparticipants_List);
                        Log.d("select_list_id", "" + sel_id);


                        AlertDialog.Builder builder = new AlertDialog.Builder(NewGroup_activity.this, R.style.AlertDialogCustom);
                        builder.setMessage("Are you sure you want to add the person?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        try {
                                            if (room_type.equalsIgnoreCase("2")) {
                                                adddb(sel_id);
                                            } else {
                                                addgroup(sel_id, group_details.optString("name"), group_details.optString("image"));
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        grpparticipants_List.clear();
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();

                    }
                });


            }

        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            CircleImageView user_image;
            TextView user_name, user_status;
            ImageView click;

            public MyViewHolder(View itemView) {
                super(itemView);
                user_image = (CircleImageView) itemView.findViewById(R.id.contact_image);
                user_name = (TextView) itemView.findViewById(R.id.user_name);
                user_status = (TextView) itemView.findViewById(R.id.user_status);
                click = (ImageView) itemView.findViewById(R.id.remove_view_check);
                customView(click, getPrimaryCOlor(NewGroup_activity.this), getPrimaryCOlor(NewGroup_activity.this));


            }
        }
    }

    private class InviteAdapter extends RecyclerView.Adapter<InviteAdapter.MyViewHolder> {

        //        private List<String> list;
        private Context mContext;
        private JSONArray list;


        public InviteAdapter(NewGroup_activity newGroup_activity, JSONArray list_of_contacts) {
            this.mContext = newGroup_activity;
            this.list = list_of_contacts;


        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.invite_list_item, parent, false);
            return new InviteAdapter.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            try {
                Log.d("onBindViewHolder: ", "list_values:" + list.get(position));


                Picasso.with(mContext).load(R.drawable.ic_account_circle)
                        .error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.user_image);


                holder.user_name.setText(list.optJSONObject(position).optString("Name"));
                holder.user_status.setText(getResources().getString(R.string.invite_this));
                holder.user_name.setTextColor(mContext.getResources().getColor(R.color.black));
                holder.user_status.setTextColor(mContext.getResources().getColor(R.color.black));
                holder.invite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(NewGroup_activity.this, R.style.AlertDialogCustom);
                        builder.setMessage("Are you sure you want to Invite the person?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        openmessages(list.optJSONObject(position).optString("Mobile"));
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        grpparticipants_List.clear();
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return list.length();
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            CircleImageView user_image;
            TextView user_name, user_status;
            ImageView click;
            Button invite;

            public MyViewHolder(View itemView) {
                super(itemView);
                user_image = (CircleImageView) itemView.findViewById(R.id.contact_image);
                user_name = (TextView) itemView.findViewById(R.id.user_name);
                user_status = (TextView) itemView.findViewById(R.id.user_status);
                click = (ImageView) itemView.findViewById(R.id.remove_view_check);
                customView(click, getPrimaryCOlor(NewGroup_activity.this), getPrimaryCOlor(NewGroup_activity.this));
                invite = (Button) itemView.findViewById(R.id.btn_invite);
                customViews(invite, getPrimaryCOlor(NewGroup_activity.this), getPrimaryCOlor(NewGroup_activity.this));
            }
        }


    }

    private class ForwardMessage extends RecyclerView.Adapter<ForwardMessage.MyViewHolder> {
        JSONArray parti_value;
        String message, message_type;
        private Context mContext;
        private List<ContactsModel> list;

        public ForwardMessage(NewGroup_activity newGroup_activity, List<ContactsModel> list_of_contacts, String message, String message_type) {
            this.mContext = newGroup_activity;
            this.list = list_of_contacts;
            this.parti_value = particpants_array;
            this.message = message;
            this.message_type = message_type;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.contact_list_item, parent, false);
            return new ForwardMessage.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            Log.d("onBindViewHolder: ", "list_values:" + list.get(position));

            DBHandler dbHandler = new DBHandler(mContext);

            if (list.get(position).getImage().equalsIgnoreCase("") || list.get(position).getImage().equalsIgnoreCase(" ")) {
                Picasso.with(mContext).load(R.drawable.ic_account_circle)
                        .error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.user_image);
            } else {
                Picasso.with(mContext).load(list.get(position).getImage()).placeholder(R.drawable.ic_account_circle).into(holder.user_image);

            }

            holder.user_name.setText(list.get(position).getName());
            holder.user_status.setText(list.get(position).getStatus());
            holder.user_name.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.user_status.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    JSONObject pat_Obj = new JSONObject();

                    final String sel_id = list.get(position).getZeoChatId();
                    final String sel_name = list.get(position).getZeoChatId();

                    try {
                        pat_Obj.put("participantId", list.get(position));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    grpparticipants_List.add(pat_Obj);

                    Log.d("select_list", "" + grpparticipants_List);
                    Log.d("select_list_id", "" + sel_id);

                    forwardmessage(mContext, sel_id, message, message_type);

                }
            });


        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            CircleImageView user_image;
            TextView user_name, user_status;
            ImageView click;

            public MyViewHolder(View itemView) {
                super(itemView);
                user_image = (CircleImageView) itemView.findViewById(R.id.contact_image);
                user_name = (TextView) itemView.findViewById(R.id.user_name);
                user_status = (TextView) itemView.findViewById(R.id.user_status);
                click = (ImageView) itemView.findViewById(R.id.remove_view_check);


            }
        }
    }


}
