package com.bitware.coworker.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.bitware.coworker.DBHelper.DBHandler;
import com.bitware.coworker.R;
import com.bitware.coworker.baseUtils.SharedHelper;
import com.bitware.coworker.fragment.ChatFragment;
import com.bitware.coworker.fragment.ContactFragment;
import com.bitware.coworker.models.ChatMessages;
import com.bitware.coworker.models.ChatType;
import com.bitware.coworker.models.ChatsMessagesModel;
import com.bitware.coworker.models.ChatsModel;
import com.bitware.coworker.models.ContactsModel;
import com.bitware.coworker.models.GroupParticiapntsModel;
import com.bitware.coworker.models.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewBroadCast extends AppCompatActivity {
    ContactAdapterList contactAdapterList;
    List<JSONObject> participantsList;
    List<JSONObject> participants_List;
    DBHandler dbHandler;
    private RecyclerView contact_list;
    private ImageView next;

    public static int getPrimaryCOlor(Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, value, true);
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
        setContentView(R.layout.activity_new_broad_cast);
        participantsList = new ArrayList<>();
        participants_List = new ArrayList<>();
        Toolbar toolbar = (Toolbar) findViewById(R.id.group_toolbar);
        toolbar.setBackgroundColor(getPrimaryCOlor(NewBroadCast.this));
        dbHandler = new DBHandler(NewBroadCast.this);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toolbar.setTitle("New BroadCast");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitle("Add participants");
        contact_list = (RecyclerView) findViewById(R.id.contact_list);
        next = (ImageView) findViewById(R.id.next);

        Log.d("onCreate: ", "came2:" + ContactFragment.list_of_contacts.size());
        Log.e("group", "" + ContactFragment.list_of_contacts.toString());

        if (ContactFragment.list_of_contacts.size() > 0) {
            contactAdapterList = new ContactAdapterList(NewBroadCast.this, ContactFragment.list_of_contacts);
            contact_list.setHasFixedSize(true);
            contact_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            contact_list.setAdapter(contactAdapterList);
        } else {

        }
        customView(next, getPrimaryCOlor(NewBroadCast.this), getPrimaryCOlor(NewBroadCast.this));

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONArray jsonArray = new JSONArray(participantsList);

                if (jsonArray.length() <= 0) {

                    Toast.makeText(NewBroadCast.this, getResources().getString(R.string.empty_part), Toast.LENGTH_SHORT).show();
                } else {
                    String group_id = UUID.randomUUID().toString();
                    String message = "You Created the BroadCast list";
                    String time = String.valueOf(System.currentTimeMillis());
                    String group_name = "";
                    Log.d("onClick: ", "jsonAray:" + jsonArray);


                    //  dbHandler.AddGroupParticipants(new GroupParticiapntsModel(id, grp_id, particpants_array.optJSONObject(i).optString("joinedAt"), particpants_array.optJSONObject(i).optString("addedBy"), isAdmin));

                    for (int i = 0; i < participantsList.size(); i++) {
                        JSONObject object = jsonArray.optJSONObject(i);
                        dbHandler.AddGroupParticipants(new GroupParticiapntsModel(object.optString("zoeChatId"), group_id, time, SharedHelper.getKey(NewBroadCast.this, "id"), "0"));

                    }


                    for (int i = 0; i < participantsList.size(); i++) {
                        JSONObject object = jsonArray.optJSONObject(i);
                        Log.d("onClick: ", "jsonObject:" + object);
                        String name = object.optString("name");
                        Log.d("onClick: ", "sing:name:" + name);
                        if (group_name.equalsIgnoreCase("")) {
                            group_name = name;
                        } else {
                            group_name = group_name + ", " + name;
                        }
                        Log.d("onClick: ", "grp:name:" + group_name);


                    }


                    dbHandler.InsertChats(new ChatsModel(group_id, "2", ChatMessages.BROADCAST,
                            message, Status.SENDING.toString(), time,
                            ChatType.text.toString(), 0, group_name, SharedHelper.getKey(NewBroadCast.this, "id"), "", " ", "0", "0", "", "", "", "", "", "", "false", "1", ""));

                    String uniqueID = UUID.randomUUID().toString();


                    dbHandler.InsertChatMsg(new ChatsMessagesModel(uniqueID.trim(), SharedHelper.getKey(NewBroadCast.this, "id"), message, ChatType.createGroup.toString(), Status.SENDING.toString(), ChatMessages.CREATE_GROUP, group_id, time, "0", "0", 0, 0, 0, "0.0", "0.0", "", "", "1", "false", "", "", 0, "", "", "", ""));

                    ChatFragment.mainActivity.refresh();
                    Intent intent = new Intent(NewBroadCast.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    NewBroadCast.this.finish();
                }
            }
        });
    }


    public class ContactAdapterList extends RecyclerView.Adapter<ContactAdapterList.MyViewHolder> {
        private List<ContactsModel> list;
        private Context mContext;

        public ContactAdapterList(NewBroadCast newGroup_activity, List<ContactsModel> list_of_contacts) {
            this.list = list_of_contacts;
            this.mContext = newGroup_activity;
        }

        @Override
        public ContactAdapterList.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.contact_list_item, parent, false);
            return new ContactAdapterList.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ContactAdapterList.MyViewHolder holder, final int position) {

            if (list.get(position).getImage().equalsIgnoreCase("") || list.get(position).getImage().equalsIgnoreCase(" ")) {

                Picasso.with(mContext).load(R.drawable.ic_account_circle)
                        .error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.user_image);
            } else {
                Picasso.with(mContext).load(list.get(position).getImage()).into(holder.user_image);
            }


            holder.user_name.setText(list.get(position).getName());
            holder.user_status.setText(list.get(position).getStatus());
            if (list.get(position).getSelected())
            {
                holder.click.setVisibility(View.VISIBLE);
            }
            else {
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

//                        holder.click.setVisibility(View.GONE);
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
                            if (list.get(position).getZeoChatId().equalsIgnoreCase(SharedHelper.getKey(NewBroadCast.this, "my_zoe_id"))) {
                                pat_Obj.put("isAdmin", "1");
                            } else {
                                pat_Obj.put("isAdmin", "0");
                            }
                            jsonObject.put("status", list.get(position).getStatus());
                            participantsList.add(jsonObject);
                            participants_List.add(pat_Obj);
                            list.get(position).setSelected(true);
                            notifyDataSetChanged();
                            Log.d("addedList", "" + participantsList);
                            Log.d("select_list", "" + participants_List);
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
                customView(click, getPrimaryCOlor(NewBroadCast.this), getPrimaryCOlor(NewBroadCast.this));


            }
        }
    }
}
