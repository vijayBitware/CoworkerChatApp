package com.bitware.coworker.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.bitware.coworker.DBHelper.DBHandler;
import com.bitware.coworker.R;
import com.bitware.coworker.baseUtils.SharedHelper;
import com.bitware.coworker.baseUtils.Utils;
import com.bitware.coworker.models.ChatType;
import com.bitware.coworker.models.ContactsModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActionForward extends AppCompatActivity {

    public static List<JSONObject> chatsList = new ArrayList<>();
    public static List<ContactsModel> final_list_of_contacts;
    DBHandler dbHandler;
    String themevalue;
    String group_id;
   ForwardMessageAll_contacts forwardMessage;
    ForwardMessageMy_Chats my_chats_adapter;
    private RecyclerView all_contacts;
    private RecyclerView my_chats;
    String message,message_type;
    private List<ContactsModel> list_of_contacts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        themevalue=SharedHelper.getKey(ActionForward.this,"theme_value");
        Setheme(themevalue);
        setContentView(R.layout.activity_action_forward);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        dbHandler = new DBHandler(this);
        final_list_of_contacts = new ArrayList<>();
        final_list_of_contacts.clear();
        list_of_contacts = dbHandler.GetListOfContacts();

        Toolbar toolbar = (Toolbar) findViewById(R.id.group_toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toolbar.setTitle(getResources().getString(R.string.forward_to));
        toolbar.setTitleTextColor(Color.WHITE);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        Log.d("onCreate: ","StringExtra"+intent.getParcelableExtra(Intent.EXTRA_STREAM));
        Log.d("onCreate: ","type:"+intent.getType());

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                message_type=ChatType.text.toString();
                Log.d("handleSendText: ","SharedText:"+sharedText);
                if (sharedText != null) {
                    message=sharedText;
                    // Update UI to reflect text being shared
                }
            } else if (type.startsWith("image/")) {
                Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                Log.d("handleSendText: ","ShaerdImage:"+imageUri);
                message_type=ChatType.image.toString();

                message= Utils.getPath(ActionForward.this,imageUri);

            }
            else if (type.startsWith("video/")) {
                Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                Log.d("handleSendText: ","ShaerdImage:"+imageUri);
                message_type=ChatType.video.toString();
                message= Utils.getPath(ActionForward.this,imageUri);
            }
            else if (type.startsWith("audio/")) {
                Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                Log.d("handleSendText: ","ShaerdImage:"+imageUri);
                message_type=ChatType.audio.toString();
                message= Utils.getPath(ActionForward.this,imageUri);
            }
            else if (type.startsWith("audio/")) {
                Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                Log.d("handleSendText: ","ShaerdImage:"+imageUri);
                message_type=ChatType.audio.toString();
                message= Utils.getPath(ActionForward.this,imageUri);
            }
            else if (type.startsWith("application/"))
            {
                Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                Log.d("handleSendText: ","ShaerdImage:"+imageUri);
                message_type=ChatType.document.toString();
                message= imageUri.toString();

            }

        } else {
            Log.d("onCreate: ","other");

        }

        //MYCHATS_VALUES
        my_chats = (RecyclerView) findViewById(R.id.my_chat_list);
        my_chats.setNestedScrollingEnabled(false);
        my_chats.setLayoutManager(linearLayoutManager);
        chatsList = dbHandler.GetChats();
        Log.e("chatList", chatsList.toString());
        my_chats_adapter = new ForwardMessageMy_Chats(this, chatsList, message, message_type,"","",message);
        my_chats.setAdapter(my_chats_adapter);

        //ALL_CHATS VALUES


        for (int i = 0; i < list_of_contacts.size(); i++) {
            String id = list_of_contacts.get(i).getZeoChatId();

            if (!isValueexists(chatsList, id)) {
                final_list_of_contacts.add(list_of_contacts.get(i));
            }

        }
        all_contacts = (RecyclerView) findViewById(R.id.all_contact_list);
        all_contacts.setNestedScrollingEnabled(false);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);

        all_contacts.setLayoutManager(linearLayoutManager1);
        forwardMessage = new ForwardMessageAll_contacts(this, final_list_of_contacts, message, message_type, chatsList,"cname","cid",message);
        all_contacts.setAdapter(forwardMessage);


    }

    private void Setheme(String themevalue) {
        switch (themevalue)
        {
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


    void handleSendImage(Intent intent) throws URISyntaxException {

    }



    private boolean isValueexists(List<JSONObject> chatlists, String id) {
        Boolean isExists = false;
        for (int i = 0; i < chatlists.size(); i++) {
            if (chatlists.get(i).optString("chatRoomId").equalsIgnoreCase(id)) {
                isExists = true;
            }
        }
        return isExists;
    }

    private void forwardmessage(Context mContext, String sel_id, String message, String message_type, String cname, String cid, String media, String chatroomtype, String chatRoomId) {


        SharedHelper.putKey(mContext, "single_chat_enable", "yes");
        Intent intent = new Intent(mContext, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        String value1,value2,value3,value4;
        value1=dbHandler.getCreateby(group_id);
        value2=dbHandler.GetGroupImage(group_id);
        value3= dbHandler.GetUserName(sel_id);
        value4= dbHandler.GetUserImage(sel_id);


        String type=chatroomtype;
        if (type.equalsIgnoreCase("0")) {
            intent.putExtra("zoeChatID", sel_id);
            intent.putExtra("groupId", "0");
            intent.putExtra("user_name", dbHandler.GetUserName(sel_id));
            intent.putExtra("grp_image","");
            intent.putExtra("image", dbHandler.GetUserImage(sel_id));
        } else if (type.equalsIgnoreCase("1"))
        {

            intent.putExtra("zoeChatID", dbHandler.getCreateby(chatRoomId));
            intent.putExtra("groupId", chatRoomId);
            intent.putExtra("grp_image",dbHandler.GetGroupImage(chatRoomId));
            intent.putExtra("user_name", dbHandler.GetGroupName(chatRoomId));
            Log.d("onCreate:groupname ","gr:"+dbHandler.GetGroupName(chatRoomId));

        }
        intent.putExtra("chatRoomType", chatroomtype);
        intent.putExtra("send_message", "yes");
        intent.putExtra("send_message_ac", "yes");
        intent.putExtra("message", message);
        intent.putExtra("message_type", message_type);
        intent.putExtra("c_name",cname);
        intent.putExtra("preview","false");
        intent.putExtra("c_id",cid);
        intent.putExtra("media",media);
        Log.d("actionmessage: ","all:"+message+","+message_type+","+media);


        mContext.startActivity(intent);


    }



    private class ForwardMessageAll_contacts extends RecyclerView.Adapter<ForwardMessageAll_contacts.MyViewHolder> {


        String message, message_type,cname,cid,media;
        List<JSONObject> chatlists;
        private Context mContext;
        private List<ContactsModel> list;

        public ForwardMessageAll_contacts(Context context, List<ContactsModel> list_of_contacts, String message, String message_type, List<JSONObject> chatsList, String cName, String cId, String media) {
            this.mContext = context;
            this.list = list_of_contacts;
            this.message = message;
            this.message_type = message_type;
            this.chatlists = chatsList;
            this.cname=cName;
            this.cid=cId;
            this.media=media;
        }

        @Override
        public ForwardMessageAll_contacts.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.contact_list_item, parent, false);
            return new ForwardMessageAll_contacts.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ForwardMessageAll_contacts.MyViewHolder holder, final int position) {
            Log.d("onBindViewHolder: ", "list_values:" + list.get(position));
            String id = list.get(position).getZeoChatId();


            String isLocked = dbHandler.Getlockedstatus(id);
            if (isLocked.equalsIgnoreCase("1") || isLocked.equalsIgnoreCase("true")) {
                holder.lock_button.setVisibility(View.VISIBLE);
            } else {
                holder.lock_button.setVisibility(View.INVISIBLE);
            }

            if (!isValueexists(chatsList, id)) {

                DBHandler dbHandler = new DBHandler(mContext);

                if (list.get(position).getImage().equalsIgnoreCase("") || list.get(position).getImage().equalsIgnoreCase(" ")) {
                    Picasso.with(mContext).load(R.drawable.ic_account_circle)
                            .error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.user_image);
                }
                else {
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
                        try {
                            pat_Obj.put("participantId", list.get(position));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        forwardmessage(mContext, sel_id, message, message_type,cname,cid,media, "0", "");

                    }
                });
            } else {
                list.remove(position);
                notifyDataSetChanged();
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            final ImageView lock_button;
            CircleImageView user_image;
            TextView user_name, user_status;
            ImageView click;

            public MyViewHolder(View itemView) {
                super(itemView);
                user_image = (CircleImageView) itemView.findViewById(R.id.contact_image);
                user_name = (TextView) itemView.findViewById(R.id.user_name);
                user_status = (TextView) itemView.findViewById(R.id.user_status);
                click = (ImageView) itemView.findViewById(R.id.remove_view_check);
                lock_button=(ImageView)itemView.findViewById(R.id.lock_status);


            }
        }
    }

    private class ForwardMessageMy_Chats extends RecyclerView.Adapter<ForwardMessageMy_Chats.MyViewHolder> {

        String message, message_type,cname,cid,media;
        private Context mContext;
        private List<JSONObject> list;


        public ForwardMessageMy_Chats(Context context, List<JSONObject> list_of_contacts, String message, String message_type, String cName, String cId, String media) {
            this.mContext = context;
            this.list = list_of_contacts;
            this.message = message;
            this.message_type = message_type;
            this.cname=cName;
            this.cid=cId;
            this.media=media;
        }

        @Override
        public ForwardMessageMy_Chats.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.contact_list_item, parent, false);
            return new ForwardMessageMy_Chats.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ForwardMessageMy_Chats.MyViewHolder holder, final int position) {
            Log.d("onBindViewHolder: ", "list_values:" + list.get(position));

            DBHandler dbHandler = new DBHandler(mContext);

            final String name, image, status, ZoechatId, chatroomtype;



            chatroomtype = list.get(position).optString("chatRoomType");

            if (chatroomtype.equalsIgnoreCase("0")) {
                name = list.get(position).optString("Name");
                image = list.get(position).optString("Image");
                ZoechatId = list.get(position).optString("chatRoomId");
                status = dbHandler.GetUserStatus(ZoechatId);

            } else {
                name = list.get(position).optString("Name");
                image = list.get(position).optString("groupImage");
                status = "";
                ZoechatId = list.get(position).optString("groupId");
            }

            String isLocked = dbHandler.Getlockedstatus(ZoechatId);
            if (isLocked.equalsIgnoreCase("1") || isLocked.equalsIgnoreCase("true")) {
                holder.lock_button.setVisibility(View.VISIBLE);
            } else {
                holder.lock_button.setVisibility(View.INVISIBLE);
            }

            if (image.equalsIgnoreCase("") || image.equalsIgnoreCase(" ")) {
                Picasso.with(mContext).load(R.drawable.ic_account_circle)
                        .error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.user_image);
            } else {
                Picasso.with(mContext).load(image).placeholder(R.drawable.ic_account_circle).into(holder.user_image);
            }




            holder.user_name.setText(name);
            holder.user_status.setText(status);
            holder.user_name.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.user_status.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    JSONObject pat_Obj = new JSONObject();

                    final String sel_id = ZoechatId;

                    try {
                        pat_Obj.put("participantId", list.get(position));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("onClick: ",list.get(position).optString("chatRoomId"));
                    forwardmessage(mContext, sel_id, message, message_type,cname,cid, media,chatroomtype,list.get(position).optString("chatRoomId"));

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
            ImageView click,lock_button;

            public MyViewHolder(View itemView) {
                super(itemView);
                user_image = (CircleImageView) itemView.findViewById(R.id.contact_image);
                user_name = (TextView) itemView.findViewById(R.id.user_name);
                user_status = (TextView) itemView.findViewById(R.id.user_status);
                click = (ImageView) itemView.findViewById(R.id.remove_view_check);
                lock_button=(ImageView)itemView.findViewById(R.id.lock_status);


            }
        }
    }


}
