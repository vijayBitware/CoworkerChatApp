package com.bitware.coworker.DBHelper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.bitware.coworker.baseUtils.Utils;
import com.bitware.coworker.models.CallsModel;
import com.bitware.coworker.models.ChannelParticiapntsModel;
import com.bitware.coworker.models.ChatMessages;
import com.bitware.coworker.models.ChatType;
import com.bitware.coworker.models.ChatsMessagesModel;
import com.bitware.coworker.models.ChatsModel;
import com.bitware.coworker.models.ContactsModel;
import com.bitware.coworker.models.GetUserFromDBModel;
import com.bitware.coworker.models.GroupParticiapntsModel;
import com.bitware.coworker.models.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by KrishnaDev on 12/31/16.
 */

public class DBHandler extends SQLiteOpenHelper {

    public static final String path = Environment.getExternalStorageDirectory() + "/Zoechat/";
    public static final String DATABASE_NAME = "Zoechat.db";
    private static final int DATABASE_VERSION = 1;
    //User Table fields
    private static final String MOBILE = "mobile";
    private static final String NAME = "name";
    private static final String REGED_NAME = "registeredName";
    private static final String IMAGE = "image";
    private static final String GRP_IMAGE = "groupImage";
    private static final String STATUS = "status";
    private static final String SHOW_IN_CONTACT_PAGE = "showInContactsPage";
    private static final String ZOECHAT_ID = "zeoChatId";
    //Chats Table fields
    private static final String ID = "id";
    private static final String CHAT_ROOM_ID = "chatRoomId";
    private static final String CHAT_ROOM_TYPE = "chatRoomType";
    private static final String SENDER = "sender";
    private static final String LAST_MESSAGE = "lastMessage";
    private static final String LAST_MESSAGE_STATUS = "lastMessageStatus";
    private static final String LAST_MESSAGE_TIME = "lastMessageTime";
    private static final String UNREAD_COUNT = "unreadCount";
    private static final String GROUP_NAME = "groupName";
    private static final String GROUP_CREATE_BY = "create_by";
    private static final String SENT_BY = "sent_by";
    //Chats messages Table fields
    private static final String USER_ID = "userId";
    private static final String MESS_ID = "msgId";
    private static final String CONTENT = "content";
    private static final String CONTENT_TYPE = "contentType";
    private static final String CONTENT_STATUS = "contentStatus";
    private static final String SENT_TIME = "sentTime";
    private static final String DELIVERED_TIME = "deliveredTime";
    private static final String SEEN_TIME = "seenTime";
    private static final String CAPTION = "caption";
    private static final String IS_DOWNLOADED = "isDownloaded";
    private static final String IS_UPLOADED = "isUploaded";
    private static final String LAT = "latitude";
    private static final String LNG = "longitude";
    private static final String CNAME = "CName";
    private static final String CNUMBER = "CNumber";
    private static final String GROUP_ID = "groupId";
    private static final String IS_STARRED = "isStarred";
    private static final String HEADER = "header";
    private static final String MEDIA_LINKS = "mediaLinks";
    String date = "";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        super(context, path + DATABASE_NAME, null, DATABASE_VERSION);


    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(
                "create table User (mobile integer primary key not null, name text,registeredName text,image text, status text," +
                        "showInContactsPage text,zeoChatId text,id text)"
        );
        //0Mobile,1Name,Reged_name,Image,status,ShowinContacts,zoechatid

        sqLiteDatabase.execSQL(
                "create table Chats (id integer primary key AUTOINCREMENT, chatRoomId integer,chatRoomType integer, sender text," +
                        "lastMessage text,lastMessageStatus text, lastMessageTime text,contentType text,unreadCount integer, groupName text,create_by text,sent_by text,groupImage text, isDeleted text, isLocked text, Password text, Wallpaper text, UniqueName text, Link text, Type text, Description text, shouldSign text, shouldNotify text ,timedNotify text )"
        );
        sqLiteDatabase.execSQL(
                "create table Calls (zoeChat_id text, username text, userImage text, call_time text, callType text, callLog text)"
        );
        sqLiteDatabase.execSQL(
                "create table ChatMessages (id text primary key, userId integer,content text,contentType text, contentStatus text,sender integer, groupId text," +
                        "sentTime text,deliveredTime text, seenTime text, caption integer,isUploaded integer, isDownloaded integer, latitude text,  longitude text,  CName text,  CNumber text,chatRoomType text, isStarred text, mediaLinks text, ActionType text, showPreview integer, metaTitle text, metaDescription text, metaLogo text , shouldSign text)"
        );

        sqLiteDatabase.execSQL("create table GroupParticipants (sNo integer primary key AUTOINCREMENT , userId integer, groupId integer, joinedAt text, addedBy text, isAdmin text)");

        sqLiteDatabase.execSQL("create table GroupDetails (groupId integer primary key , Created_By text , Created_At text , groupImage text , groupName text)");

        sqLiteDatabase.execSQL("create table ChannelParticipants (sNo integer primary key AUTOINCREMENT , userId integer, channelId integer, joinedAt text, addedBy text, participantRole text)");

        sqLiteDatabase.execSQL("create table PreviewLinks (title text , description text , logo text , url text primary key )");

        sqLiteDatabase.execSQL("create table AmazonIds (id integer primary key AUTOINCREMENT, messageId text , amazonId text )");

        sqLiteDatabase.execSQL("create table VideoBitmap (id integer primary key AUTOINCREMENT, messageId text , imageBit BLOB )");


    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "User");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "Chats");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "ChatMessages");
        onCreate(sqLiteDatabase);
    }


    public void InsertUser(ContactsModel contactsModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, contactsModel.getMobile_no());
        contentValues.put(MOBILE, contactsModel.getMobile_no());
        contentValues.put(NAME, contactsModel.getName());
        contentValues.put(REGED_NAME, contactsModel.getRegisteredName());
        contentValues.put(IMAGE, contactsModel.getImage());
        contentValues.put(STATUS, contactsModel.getStatus());
        contentValues.put(SHOW_IN_CONTACT_PAGE, contactsModel.getShowInContactsPage());
        contentValues.put(ZOECHAT_ID, contactsModel.getZeoChatId());
        db.insert("User", null, contentValues);
        db.close();
    }


    public void addImageBit(String msgId, byte[] image) throws SQLiteException {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("messageId", msgId);
        cv.put("imageBit", image);
        database.insert("VideoBitmap", null, cv);
    }

    public byte[] getBitmap(String msgId) {

        byte[] eimage = new byte[0];
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from VideoBitmap where messageId='" + msgId + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            byte[] image = cursor.getBlob(1);
            image = cursor.getBlob(2);
            return image;
        }
        cursor.close();
        sqLiteDatabase.close();
        return null;
    }


    public void InsertGroup_details(String id, String created_by, String created_at, String image, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("groupId", id);
        contentValues.put("Created_By", created_by);
        contentValues.put("Created_At", created_at);
        contentValues.put("groupImage", image);
        contentValues.put("groupName", name);
        db.insert("GroupDetails", null, contentValues);
        db.close();
    }

    public void InsertLinks(JSONObject object) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", object.optString("title"));
        contentValues.put("logo", object.optString("logo"));
        contentValues.put("description", object.optString("description"));
        contentValues.put("url", object.optString("url"));
        db.insert("PreviewLinks", null, contentValues);
        db.close();
    }

    public void InsertAmzonId(String msgId, String amazonId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("messageId", msgId);
        contentValues.put("amazonId", amazonId);

        db.insert("AmazonIds", null, contentValues);
        db.close();
    }


    public String getAmazonId(String msgId) {
        String amazOnId = "";

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from AmazonIds where messageId='" + msgId + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            amazOnId = cursor.getString(2);

        }
        cursor.close();
        sqLiteDatabase.close();

        return amazOnId;
    }


    public JSONObject getLinks(String url) throws JSONException {
        JSONObject object = new JSONObject();

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from PreviewLinks where url='" + url + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            object.put("title", cursor.getString(0));
            object.put("logo", cursor.getString(2));
            object.put("description", cursor.getString(1));
            object.put("url", cursor.getString(3));
        }
        cursor.close();
        sqLiteDatabase.close();

        return object;
    }

    public boolean CheckLinkAvalible(String url) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor res = sqLiteDatabase.rawQuery("select * from PreviewLinks where url='" + url + "'", null);

        res.moveToFirst();
        while (!res.isAfterLast()) {
            res.moveToNext();
            res.close();
            return true;
        }
        res.close();
        sqLiteDatabase.close();
        return false;
    }


    public void PartiDelete(String part_id, String group_id) {


        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete("GroupParticipants", USER_ID + "=? AND groupId =?", new String[]{part_id, group_id});
        sqLiteDatabase.close();


    }

    public void amazonIdDelete(String messageId) {


        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete("AmazonIds", "messageId=?", new String[]{messageId});
        sqLiteDatabase.close();


    }

    public void AddGroupParticipants(GroupParticiapntsModel model) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("userId", model.getUser_id());
        contentValues.put("groupId", model.getGroup_id());
        contentValues.put("joinedAt", model.getJoined_at());
        contentValues.put("addedBy", model.getAdded_by());
        contentValues.put("isAdmin", model.getIsAdmin());
        db.insert("GroupParticipants", null, contentValues);
        db.close();
    }

    public void AddChannelParticipants(ChannelParticiapntsModel model) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("userId", model.getUser_id());
        contentValues.put("channelId", model.getChannel_id());
        contentValues.put("joinedAt", model.getJoined_at());
        contentValues.put("addedBy", model.getAdded_by());
        contentValues.put("participantRole", model.getParticipantRole());
        db.insert("ChannelParticipants", null, contentValues);
        db.close();
    }

    public List<GroupParticiapntsModel> GetGroupParticipants() {
        try {


            SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
            List<GroupParticiapntsModel> list = new ArrayList<>();
            Cursor cursor = sqLiteDatabase.query("User", null, null, null, null, null, null);
            cursor.moveToNext();

            while (!cursor.isAfterLast()) {

                list.add(new GroupParticiapntsModel(cursor.getString(1), cursor.getString(2), cursor.getString(3),
                        cursor.getString(4), cursor.getString(5)));
                cursor.moveToNext();

            }
            cursor.close();
            sqLiteDatabase.close();
            return list;
        } catch (NullPointerException e) {
            List<GroupParticiapntsModel> list = new ArrayList<>();
            e.printStackTrace();
            return list;
        }
    }


    //insert he calls items
    public void InsertCalls(CallsModel callsModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("zoeChat_id", callsModel.getZoeChatId());
        contentValues.put("username", callsModel.getUsername());
        contentValues.put("userImage", callsModel.getUserImage());
        contentValues.put("call_time", callsModel.getCall_time());
        contentValues.put("callType", callsModel.getCallType());
        contentValues.put("callLog", callsModel.getCallLog());
        db.insert("Calls", null, contentValues);
        db.close();
    }

    //get the call value from db
    public List<JSONObject> GetCalls() {
        List<JSONObject> jsonObjects = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("Calls", null, null, null, null, null, null, null);
        cursor.moveToNext();

        while (!cursor.isAfterLast()) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("zoeChat_id", cursor.getString(0));
                String name = GetUserName(cursor.getString(0));
                jsonObject.put("username", name);
                String image = GetUserImage(cursor.getString(0));
                jsonObject.put("userImage", image);
                jsonObject.put("call_time", cursor.getString(3));
                jsonObject.put("callType", cursor.getString(4));
                jsonObject.put("callLog", cursor.getString(5));

                jsonObjects.add(jsonObject);

                cursor.moveToNext();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        sqLiteDatabase.close();

        return jsonObjects;
    }


    @SuppressWarnings("LoopStatementThatDoesntLoop")
    public boolean CheckIsDataAlreadyInDBorNot(String mobile) {
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor res = db.rawQuery("select * from User where mobile='" + mobile + "'", null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            res.moveToNext();
            res.close();
            return true;
        }
        res.close();
        db.close();
        return false;
    }

    public boolean CheckParticipantAlreadyInDBorNot(String mobile, String group_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor res = db.rawQuery("select * from GroupParticipants where userId='" + mobile + "' AND groupId='" + group_id + "'", null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            res.moveToNext();
            res.close();
            return true;
        }
        res.close();
        db.close();
        return false;
    }

    public boolean CheckChanneParticipantAlreadyInDBorNot(String mobile, String group_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor res = db.rawQuery("select * from ChannelParticipants where userId='" + mobile + "' AND channelId='" + group_id + "'", null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            res.moveToNext();
            res.close();
            return true;
        }
        res.close();
        db.close();
        return false;
    }


    public int UserUpdate(String mobile, String registeredName, String image, String showInContactsPage, String zoeChatId, String status) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(REGED_NAME, registeredName);
        contentValues.put(IMAGE, image);
        contentValues.put(SHOW_IN_CONTACT_PAGE, showInContactsPage);
        contentValues.put(ZOECHAT_ID, zoeChatId);
        contentValues.put(STATUS, status);
        int i = sqLiteDatabase.update("User", contentValues, MOBILE + "=?", new String[]{mobile});

        sqLiteDatabase.close();
        return i;

    }

    public int groupPartiUpdate(String grp_id, String joined_at, String isAdmin, String added_by, String user_id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("userId", user_id);
        contentValues.put("groupId", grp_id);
        contentValues.put("joinedAt", joined_at);
        contentValues.put("addedBy", added_by);
        contentValues.put("isAdmin", isAdmin);
        int i = sqLiteDatabase.update("GroupParticipants", contentValues, "groupId=? AND userId=?", new String[]{grp_id, user_id});

        sqLiteDatabase.close();
        return i;

    }

    public int channelPartiUpdate(String grp_id, String joined_at, String isAdmin, String added_by, String user_id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("userId", user_id);
        contentValues.put("channelId", grp_id);
        contentValues.put("joinedAt", joined_at);
        contentValues.put("addedBy", added_by);
        contentValues.put("participantRole", isAdmin);
        int i = sqLiteDatabase.update("ChannelParticipants", contentValues, " channelId = ? AND userId = ? ", new String[]{grp_id, user_id});

        sqLiteDatabase.close();
        return i;

    }

    public int channelPartiUpdateOnly(String grp_id, String isAdmin, String user_id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("participantRole", isAdmin);
        int i = sqLiteDatabase.update("ChannelParticipants", contentValues, " channelId = ? AND userId = ? ", new String[]{grp_id, user_id});

        sqLiteDatabase.close();
        return i;

    }

    public int broadnameUpdate(String grp_id, String name) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("groupName", name);
        int i = sqLiteDatabase.update("Chats", contentValues, " chatRoomId = ? ", new String[]{grp_id});

        sqLiteDatabase.close();
        return i;

    }


    public void GrpImageUpdate(String image, String zoeChatId) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(GRP_IMAGE, image);
        sqLiteDatabase.update("Chats", contentValues, CHAT_ROOM_ID + "=?", new String[]{zoeChatId});
        sqLiteDatabase.close();

    }

    public void GroupSignUpdate(String image, String zoeChatId) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("shouldSign", image);
        sqLiteDatabase.update("Chats", contentValues, CHAT_ROOM_ID + "=?", new String[]{zoeChatId});
        sqLiteDatabase.close();

    }


    public void UserNameUpdate(String name, String mobile) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        sqLiteDatabase.update("User", contentValues, MOBILE + "=?", new String[]{mobile});
        sqLiteDatabase.close();

    }

    public void GroupNameUpdate(String name, String mobile) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(GROUP_NAME, name);
        sqLiteDatabase.update("Chats", contentValues, CHAT_ROOM_ID + "=?", new String[]{mobile});
        sqLiteDatabase.close();
    }

    public void GroupDescUpdate(String name, String mobile) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Description", name);
        sqLiteDatabase.update("Chats", contentValues, CHAT_ROOM_ID + "=?", new String[]{mobile});
        sqLiteDatabase.close();
    }


    public void GroupDeleteUpdate(String name, String chatRoomId) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("isDeleted", name);
        sqLiteDatabase.update("Chats", contentValues, CHAT_ROOM_ID + "=?", new String[]{chatRoomId});
        sqLiteDatabase.close();
    }

    public String GetUserName(String mobile) {
        String name = "";
        try {
            SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery("select * from User where zeoChatId='" + mobile + "'", null);
            if (cursor.getCount() > 0) {
                cursor.moveToNext();
                name = cursor.getString(1);
            } else {
                name = mobile;
            }
            cursor.close();
            sqLiteDatabase.close();
        } catch (Exception e) {

        }
        return name;
    }

    public String GetGroupName(String group_id) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String name = "";
        Cursor cursor = sqLiteDatabase.rawQuery("select * from Chats where chatRoomId='" + group_id + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            name = cursor.getString(9);
        } else {
            name = group_id;
        }
        cursor.close();
        sqLiteDatabase.close();
        return name;
    }

    public String GetChannelMember(String group_id, String participant_id) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String name = "";
        Cursor cursor = sqLiteDatabase.rawQuery("select * from ChannelParticipants where channelId='" + group_id + "' AND userId='" + participant_id + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            name = cursor.getString(5);
        } else {
            name = group_id;
        }
        cursor.close();
        sqLiteDatabase.close();
        return name;
    }

    public String GetGroupImage(String group_id) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String name = "";
        Cursor cursor = sqLiteDatabase.rawQuery("select * from Chats where chatRoomId='" + group_id + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            name = cursor.getString(12);
        } else {
            name = group_id;
        }
        cursor.close();
        sqLiteDatabase.close();
        return name;
    }


    public String GetWallpaper(String group_id) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String name = "";
        Cursor cursor = sqLiteDatabase.rawQuery("select * from Chats where chatRoomId='" + group_id + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            name = cursor.getString(16);
        } else {
            name = "";
        }
        cursor.close();
        sqLiteDatabase.close();
        return name;
    }

    public void UpdateWallpaper(String zoeChatID, String message) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Wallpaper", message);
        sqLiteDatabase.update("Chats", contentValues, CHAT_ROOM_ID + "=?", new String[]{zoeChatID});
        sqLiteDatabase.close();

    }

    public String GetUserStatus(String mobile) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String status = "";
        Cursor cursor = sqLiteDatabase.rawQuery("select * from User where zeoChatId='" + mobile + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            status = cursor.getString(4);
        } else {
            status = mobile;
        }
        cursor.close();
        sqLiteDatabase.close();
        return status;

    }

    public String GetUserImage(String mobile) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String image = "";
        Cursor cursor = sqLiteDatabase.rawQuery("select * from User where zeoChatId='" + mobile + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            image = cursor.getString(3);

        }
        cursor.close();
        sqLiteDatabase.close();
        return image;
    }

    public JSONObject GetUserInfo(String mobile) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        JSONObject jsonObject = new JSONObject();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from User where zeoChatId='" + mobile + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            try {
                jsonObject.put("Mobile", cursor.getString(0));
                jsonObject.put("Name", cursor.getString(1));
                jsonObject.put("Reged_Name", cursor.getString(2));
                jsonObject.put("Image", cursor.getString(3));
                jsonObject.put("Status", cursor.getString(4));
                jsonObject.put("ShownInContactsPage", cursor.getString(5));
                jsonObject.put("ZoeChatId", cursor.getString(6));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        cursor.close();
        sqLiteDatabase.close();
        return jsonObject;
    }


    public JSONArray GetAllUsers() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        JSONArray jsonArray = new JSONArray();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from User", null);
        cursor.moveToNext();

        while (!cursor.isAfterLast()) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Mobile", cursor.getString(0));
                jsonObject.put("Name", cursor.getString(1));
                jsonObject.put("Reged_Name", cursor.getString(2));
                jsonObject.put("Image", cursor.getString(3));
                jsonObject.put("Status", cursor.getString(4));
                jsonObject.put("ShownInContactsPage", cursor.getString(5));
                jsonObject.put("ZoeChatId", cursor.getString(6));
                jsonArray.put(jsonObject);
                cursor.moveToNext();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        cursor.close();
        sqLiteDatabase.close();
        return jsonArray;
    }


    public JSONArray GetAllUnRegUsers() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        JSONArray jsonArray = new JSONArray();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from User where showInContactsPage='" + 0 + "'", null);
        cursor.moveToNext();

        while (!cursor.isAfterLast()) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Mobile", cursor.getString(0));
                jsonObject.put("Name", cursor.getString(1));
                jsonObject.put("Reged_Name", cursor.getString(2));
                jsonObject.put("Image", cursor.getString(3));
                jsonObject.put("Status", cursor.getString(4));
                jsonObject.put("ShownInContactsPage", cursor.getString(5));
                jsonObject.put("ZoeChatId", cursor.getString(6));
                jsonArray.put(jsonObject);
                cursor.moveToNext();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        cursor.close();
        sqLiteDatabase.close();
        return jsonArray;
    }


    public List<GroupParticiapntsModel> GetPartiFromGrp(String group_id) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from GroupParticipants where groupId='" + group_id + "'", null);
        cursor.moveToNext();
        Log.d("GetPartiFromGrp: ", "cursorocunt:" + cursor.getCount());
        List<GroupParticiapntsModel> list = new ArrayList<>();


        while (!cursor.isAfterLast()) {
            list.add(new GroupParticiapntsModel(cursor.getString(1), cursor.getString(2), cursor.getString(3),
                    cursor.getString(4), cursor.getString(5)));

            Log.d("GetPartiFromGrp: ", "values::" + cursor.getString(5));
            cursor.moveToNext();

        }

        cursor.close();
        sqLiteDatabase.close();

        return list;
    }

    public List<ChannelParticiapntsModel> GetPartiFromChannel(String group_id) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from ChannelParticipants where channelId='" + group_id + "'", null);
        cursor.moveToNext();
        Log.d("GetPartiFromChannel: ", "cursorocunt:" + cursor.getCount());
        List<ChannelParticiapntsModel> list = new ArrayList<>();


        while (!cursor.isAfterLast()) {
            list.add(new ChannelParticiapntsModel(cursor.getString(1), cursor.getString(2), cursor.getString(3),
                    cursor.getString(4), cursor.getString(5)));

            Log.d("GetPartiFromGrp: ", "values::" + cursor.getString(5));
            cursor.moveToNext();

        }

        cursor.close();
        sqLiteDatabase.close();

        return list;
    }

    public List<ChannelParticiapntsModel> GetAdminFromChannel(String group_id) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from ChannelParticipants where channelId='" + group_id + "' AND participantRole != 'user'", null);
        cursor.moveToNext();
        Log.d("GetAdminFromChannel: ", "cursorocunt:" + cursor.getCount());
        List<ChannelParticiapntsModel> list = new ArrayList<>();


        while (!cursor.isAfterLast()) {
            list.add(new ChannelParticiapntsModel(cursor.getString(1), cursor.getString(2), cursor.getString(3),
                    cursor.getString(4), cursor.getString(5)));

            Log.d("GetPartiFromGrp: ", "values::" + cursor.getString(5));
            cursor.moveToNext();

        }

        cursor.close();
        sqLiteDatabase.close();

        return list;
    }


    public List<ChannelParticiapntsModel> GetUserFromChannel(String group_id) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from ChannelParticipants where channelId='" + group_id + "' AND participantRole = 'user'", null);
        cursor.moveToNext();
        Log.d("GetAdminFromChannel: ", "cursorocunt:" + cursor.getCount());
        List<ChannelParticiapntsModel> list = new ArrayList<>();


        while (!cursor.isAfterLast()) {
            list.add(new ChannelParticiapntsModel(cursor.getString(1), cursor.getString(2), cursor.getString(3),
                    cursor.getString(4), cursor.getString(5)));

            Log.d("GetPartiFromGrp: ", "values::" + cursor.getString(5));
            cursor.moveToNext();

        }

        cursor.close();
        sqLiteDatabase.close();

        return list;
    }


    public List<GetUserFromDBModel> GetAllUserFromDB() {
        try {
            SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();


            List<GetUserFromDBModel> list = new ArrayList<>();
            Cursor cursor = sqLiteDatabase.query("User", null, null, null, null, null, null);
            cursor.moveToNext();

            while (!cursor.isAfterLast()) {
                list.add(new GetUserFromDBModel(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6)));
                cursor.moveToNext();
            }
            cursor.close();
            sqLiteDatabase.close();
            return list;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;

        }
    }


    public List<ContactsModel> GetListOfContacts() {
        try {


            SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
            List<ContactsModel> list = new ArrayList<>();
            Cursor cursor = sqLiteDatabase.query("User", null, null, null, null, null, null);
            cursor.moveToNext();

            while (!cursor.isAfterLast()) {
                if (cursor.getString(5).equalsIgnoreCase("1")) {
                    list.add(new ContactsModel(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                            cursor.getString(3), cursor.getString(4), cursor.getInt(5), cursor.getString(6), cursor.getString(7), false));
                    cursor.moveToNext();
                } else {
                    cursor.moveToNext();
                }
            }
            cursor.close();
            sqLiteDatabase.close();
            return list;
        } catch (NullPointerException e) {
            List<ContactsModel> list = new ArrayList<>();
            e.printStackTrace();
            return list;
        }


    }

    /* Chats methods*/

    public void InsertChats(ChatsModel chatsModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put(ID, chatsModel.getId());
        contentValues.put(CHAT_ROOM_ID, chatsModel.getChatRoomID());
        contentValues.put(CHAT_ROOM_TYPE, chatsModel.getChatRoomType());
        contentValues.put(SENDER, chatsModel.getSender());
        contentValues.put(LAST_MESSAGE, chatsModel.getLastMessage());
        contentValues.put(LAST_MESSAGE_STATUS, chatsModel.getLastMessageStatus());
        contentValues.put(LAST_MESSAGE_TIME, chatsModel.getLastMessageTime());
        contentValues.put(CONTENT_TYPE, chatsModel.getContentType());
        contentValues.put(UNREAD_COUNT, chatsModel.getUnreadCount());
        contentValues.put(GROUP_NAME, chatsModel.getGroup_name());
        contentValues.put(SENT_BY, chatsModel.getSent_by());
        contentValues.put(GROUP_CREATE_BY, chatsModel.getCreate_by());
        contentValues.put("groupImage", chatsModel.getGroup_image());
        contentValues.put("isDeleted", chatsModel.getIs_deleted());
        contentValues.put("isLocked", chatsModel.getIsLocked());
        contentValues.put("Password", chatsModel.getPassword());
        contentValues.put("Wallpaper", chatsModel.getWallpaper());
        contentValues.put("UniqueName", chatsModel.getUniqueName());
        contentValues.put("Link", chatsModel.getLink());
        contentValues.put("Type", chatsModel.getC_type());
        contentValues.put("Description", chatsModel.getC_description());
        contentValues.put("shouldSign", chatsModel.getShouldSign());
        contentValues.put("shouldNotify", chatsModel.getShouldNotify());
        if (chatsModel.getTimedNotify().equalsIgnoreCase("")) {
            contentValues.put("timedNotify", "0");
        } else {
            contentValues.put("timedNotify", chatsModel.getTimedNotify());
        }

        db.insert("Chats", null, contentValues);
        Log.d("DB_VALUES", "InsertChat: " + contentValues);

        db.close();
    }


    public void UpdateLastMsg(String zoeChatID, String sender, String lastMessage, String lastMessageStatus,
                              String lastMessageTime, String contentType, int unreadCount) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SENDER, sender);
        contentValues.put(LAST_MESSAGE, lastMessage);
        contentValues.put(LAST_MESSAGE_STATUS, lastMessageStatus);
        contentValues.put(LAST_MESSAGE_TIME, lastMessageTime);
        contentValues.put(CONTENT_TYPE, contentType);
        contentValues.put(UNREAD_COUNT, unreadCount);

        sqLiteDatabase.update("Chats", contentValues, CHAT_ROOM_ID + "=?", new String[]{zoeChatID});
        sqLiteDatabase.close();

    }

    public void UpdateLastMsg_Status(String zoeChatID, String lastMessageStatus) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LAST_MESSAGE_STATUS, lastMessageStatus);
        sqLiteDatabase.update("Chats", contentValues, CHAT_ROOM_ID + "=?", new String[]{zoeChatID});
        sqLiteDatabase.close();

    }

    public void UpdatePassword(String zoeChatID, String Password) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Password", Password);
        sqLiteDatabase.update("Chats", contentValues, CHAT_ROOM_ID + "=?", new String[]{zoeChatID});
        sqLiteDatabase.close();

    }

    public void Updatelock(String zoeChatID, String Password) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("isLocked", Password);
        sqLiteDatabase.update("Chats", contentValues, CHAT_ROOM_ID + "=?", new String[]{zoeChatID});
        sqLiteDatabase.close();

    }

    public String GetPassword(String chatRoomId) {
        String password = "";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from Chats where chatRoomId='" + chatRoomId + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            password = cursor.getString(15);
        }
        cursor.close();
        sqLiteDatabase.close();
        return password;
    }

    public String Getlockedstatus(String chatRoomId) {
        String password = "";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from Chats where chatRoomId='" + chatRoomId + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            password = cursor.getString(14);
        }
        cursor.close();
        sqLiteDatabase.close();
        return password;
    }

    public void removeUnreadCount(String zoeChatID) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UNREAD_COUNT, "0");
        sqLiteDatabase.update("Chats", contentValues, CHAT_ROOM_ID + "=?", new String[]{zoeChatID});
        sqLiteDatabase.close();

    }

    public int GetUnReadCount(String chatRoomId) {
        int count = 0;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from Chats where chatRoomId='" + chatRoomId + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            count = cursor.getInt(8);
        }
        cursor.close();
        sqLiteDatabase.close();
        return count;
    }

    public List<JSONObject> GetChats() {

        List<JSONObject> jsonObjects = null;
        try {
            jsonObjects = new ArrayList<>();
            SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery("select * from Chats where isDeleted = 0", null);
            cursor.moveToNext();

            while (!cursor.isAfterLast()) {

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("chat_id", cursor.getString(0));
                    jsonObject.put("chatRoomId", cursor.getString(1));
                    String chatType = cursor.getString(2);
                    jsonObject.put("chatRoomType", cursor.getString(2));
                    if (chatType.equalsIgnoreCase("0")) {
                        if (GetUserName(cursor.getString(1)).equalsIgnoreCase(" ")) {
                            jsonObject.put("Name", cursor.getString(1));
                        } else {
                            jsonObject.put("Name", GetUserName(cursor.getString(1)));
                        }
                    } else if (chatType.equalsIgnoreCase("1")) {
                        jsonObject.put("Name", cursor.getString(9));
                    } else if (chatType.equalsIgnoreCase("2")) {
                        jsonObject.put("Name", cursor.getString(9));
                    } else if (chatType.equalsIgnoreCase("3")) {
                        jsonObject.put("Name", cursor.getString(9));
                    }
                    jsonObject.put("sender", cursor.getString(3));
                    jsonObject.put("lastMessage", cursor.getString(4));
                    jsonObject.put("lastMessageStatus", cursor.getString(5));
                    jsonObject.put("lastMessageTime", cursor.getString(6));
                    double sortTime = cursor.getDouble(6);
                    String sTime = Utils.getDate((long) sortTime, "yyyyMMddHHmmssSSS");
                    jsonObject.put("sortTime", sTime);
                    jsonObject.put("unreadCount", cursor.getString(8));
                    jsonObject.put("create_by", cursor.getString(10));
                    jsonObject.put("groupName", cursor.getString(9));
                    jsonObject.put("sent_by", cursor.getString(11));
                    jsonObject.put("contentType", cursor.getString(7));
                    jsonObject.put("Image", GetUserImage(cursor.getString(1)));
                    jsonObject.put("groupImage", cursor.getString(12));
                    jsonObject.put("isSelected", "false");
                    jsonObject.put("isDeleted", cursor.getString(13));
                    jsonObject.put("isLocked", cursor.getString(14));
                    jsonObject.put("Password", cursor.getString(15));
                    jsonObject.put("Wallpaper", cursor.getString(16));
                    jsonObject.put("UniqueName", cursor.getString(17));
                    jsonObject.put("Link", cursor.getString(18));
                    jsonObject.put("Type", cursor.getString(19));
                    jsonObject.put("Description", cursor.getString(20));
                    jsonObject.put("shouldSign", cursor.getString(21));
                    jsonObject.put("shouldNotify", cursor.getString(22));
                    jsonObject.put("timedNotify", cursor.getString(23));
                    jsonObjects.add(jsonObject);

                    cursor.moveToNext();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
            sqLiteDatabase.close();
            Log.d("DB_VALUES", "GetChats:" + jsonObjects);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObjects;
    }

    public void UpdatetimedNotifications(String chatRoomid, String time) {


        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("timedNotify", time);
        sqLiteDatabase.update("Chats", contentValues, "chatRoomId=?", new String[]{chatRoomid});
        sqLiteDatabase.close();

    }

    public String GettimedNotifications(String chatRoomid) {
        String status = "";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from Chats where chatRoomId='" + chatRoomid + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            status = cursor.getString(23);
        }
        cursor.close();
        sqLiteDatabase.close();
        return status;
    }

    public boolean DoesChatsUser(String zoeChatID) {
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor res = db.rawQuery("select * from Chats where chatRoomId='" + zoeChatID + "'", null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            res.moveToNext();
            return true;
        }
        return false;
    }

    public Boolean DoesChatExist(String chatRoomId) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String value = "";
        Cursor cursor = sqLiteDatabase.rawQuery("select * from Chats where chatRoomId='" + chatRoomId + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            value = cursor.getString(1);
        }
        if (value.isEmpty() || value == null) {
            return false;
        } else if (value.equals(chatRoomId)) {
            return true;
        } else {
            return true;
        }
    }


    public void DeleteChats(String mobile) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete("Chats", CHAT_ROOM_ID + "=?", new String[]{mobile});
        sqLiteDatabase.close();

    }

    public void DeleteCalls(String mobile, String s) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete("Calls", "zoeChat_id=? AND call_time=?", new String[]{mobile, s});
        sqLiteDatabase.close();

    }




    /* Chat Messages methods*/

    public void InsertChatMsg(ChatsMessagesModel chatsMessagesModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, chatsMessagesModel.getId());
        contentValues.put(USER_ID, chatsMessagesModel.getUserID());
        contentValues.put(CONTENT, chatsMessagesModel.getContent());
        contentValues.put(SENDER, chatsMessagesModel.getSender());
        contentValues.put(CONTENT_TYPE, chatsMessagesModel.getContentType());
        contentValues.put(CONTENT_STATUS, chatsMessagesModel.getContentStatus());
        contentValues.put(SENT_TIME, chatsMessagesModel.getSentTime());
        contentValues.put(DELIVERED_TIME, chatsMessagesModel.getDeliveredTime());
        contentValues.put(SEEN_TIME, chatsMessagesModel.getSeenTime());
        contentValues.put(CAPTION, chatsMessagesModel.getCaption());
        contentValues.put(IS_DOWNLOADED, chatsMessagesModel.getIsDownloaded());
        contentValues.put(IS_UPLOADED, chatsMessagesModel.getIsUploaded());
        contentValues.put(LAT, chatsMessagesModel.getLat());
        contentValues.put(LNG, chatsMessagesModel.getLng());
        contentValues.put(CNAME, chatsMessagesModel.getC_name());
        contentValues.put(CNUMBER, chatsMessagesModel.getC_number());
        contentValues.put(GROUP_ID, chatsMessagesModel.getGroupID());
        contentValues.put(CHAT_ROOM_TYPE, chatsMessagesModel.getChatRoomType());
        contentValues.put(IS_STARRED, chatsMessagesModel.getIs_starred());
        contentValues.put(MEDIA_LINKS, chatsMessagesModel.getMediaLinks());
        contentValues.put("ActionType", chatsMessagesModel.getActiontype());
        contentValues.put("showPreview", chatsMessagesModel.getShowPreview());
        contentValues.put("metaTitle", chatsMessagesModel.getMetaTitle());
        contentValues.put("metaDescription", chatsMessagesModel.getMetaDescritpion());
        contentValues.put("metaLogo", chatsMessagesModel.getMetaLogo());
        contentValues.put("shouldSign", chatsMessagesModel.getShouldSign());

//showPreview integer, metaTitle text, metaDescription text, metaLogo text
        db.insert("ChatMessages", null, contentValues);
        Log.d("DB_VALUES", "InsertChatMsg: " + contentValues);
        db.close();
    }


    public void UpdateDeliveredTime(String id, String deliveredTime) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE ChatMessages SET deliveredTime = '" + deliveredTime + "' WHERE id = '" + id + "'");
        db.close();
    }


    public void UpdateSeenTime(String id, String seen_time) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SEEN_TIME, seen_time);
        sqLiteDatabase.update("ChatMessages", contentValues, ID + "=?", new String[]{id});
        sqLiteDatabase.close();

    }

    public void UpdateSeenMsgTime(String id, String seen_time) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE ChatMessages SET seenTime = '" + seen_time + "' WHERE id = '" + id + "'");
        db.close();


    }


    public void UpdateContentStatus(String id, String content_status) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTENT_STATUS, content_status);
        sqLiteDatabase.update("ChatMessages", contentValues, ID + "=?", new String[]{id});
        sqLiteDatabase.close();

    }


    public void UpdateIsUploaded(String id, String status) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(IS_UPLOADED, status);
        sqLiteDatabase.update("ChatMessages", contentValues, ID + "=?", new String[]{id});
        sqLiteDatabase.close();
    }

    public String isDownloadStatus(String msgId) {
        String status = "";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from ChatMessages where id='" + msgId + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            status = cursor.getString(12);
        }
        cursor.close();
        sqLiteDatabase.close();
        return status;
    }

    public void UpdateChatMsgStatus(String id, String msg) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTENT, msg);
        contentValues.put(IS_DOWNLOADED, "1");
        Log.d("UpdateChatMsgStatus: ", "values:" + msg);
        sqLiteDatabase.update("ChatMessages", contentValues, ID + "=?", new String[]{id});
        sqLiteDatabase.close();

    }

    public void UpdateUploadStatus(String id, String msg) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTENT, msg);
        contentValues.put(IS_DOWNLOADED, "1");
        Log.d("UpdateChatMsgStatus: ", "values:" + msg);
        sqLiteDatabase.update("ChatMessages", contentValues, ID + "=?", new String[]{id});
        sqLiteDatabase.close();

    }


    public String isUploadStatus(String msgId) {
        String status = "";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from ChatMessages where id='" + msgId + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            status = cursor.getString(11);
        }
        cursor.close();
        sqLiteDatabase.close();
        return status;
    }


    public String getZoechatId(String msgId) {
        String status = "";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from ChatMessages where id='" + msgId + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            status = cursor.getString(1);
        }
        cursor.close();
        sqLiteDatabase.close();
        return status;
    }

    public String getChatRoomType(String msgId) {
        String status = "";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from ChatMessages where id='" + msgId + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            status = cursor.getString(17);
        }
        cursor.close();
        sqLiteDatabase.close();
        return status;
    }


    public JSONArray GetLimitChatsMessages(String zeoChatID, String start_value, String no_values) {
        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        int counts = getProfilesCount(zeoChatID);
        int start_val = counts - 70;
        Cursor cursor = sqLiteDatabase.rawQuery("select * from ChatMessages where userId='" + zeoChatID + "' ORDER BY sentTime LIMIT " + start_val + ", " + counts, null);
        int count = cursor.getCount();
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(17).equalsIgnoreCase("0")) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("msgId", cursor.getString(0));
                        jsonObject.put("userId", cursor.getString(1));
                        jsonObject.put("msg", cursor.getString(2));

                        if ("text".equalsIgnoreCase(cursor.getString(3))) {
                            jsonObject.put("chatType", ChatType.text);
                        } else if ("image".equalsIgnoreCase(cursor.getString(3))) {
                            jsonObject.put("chatType", ChatType.image);
                        } else if (ChatType.location.equals(cursor.getString(3))) {
                            jsonObject.put("chatType", ChatType.location);
                        } else if (ChatType.contact.equals(cursor.getString(3))) {
                            jsonObject.put("chatType", ChatType.contact);
                        } else if (ChatType.video.equals(cursor.getString(3))) {
                            jsonObject.put("chatType", ChatType.video);

                        } else if ("audio".equalsIgnoreCase(cursor.getString(3))) {
                            jsonObject.put("chatType", ChatType.audio);
                            jsonObject.put("isPlaying", "0");
                            jsonObject.put("playingStatus", "0");

                        }
                        jsonObject.put("contentStatus", cursor.getString(4));
                        jsonObject.put("sentTime", cursor.getString(7));
                        jsonObject.put("deliveredTime", cursor.getString(8));
                        jsonObject.put("seenTime", cursor.getString(9));
                        jsonObject.put("caption", cursor.getString(10));
                        jsonObject.put("upload", cursor.getString(11));
                        jsonObject.put("download", cursor.getString(12));
                        jsonObject.put("groupId", cursor.getString(6));
                        jsonObject.put("chatRoomType", cursor.getString(17));
                        jsonObject.put("isStarred", cursor.getString(18));
                        jsonObject.put("userName", GetUserName(cursor.getString(1)));
                        jsonObject.put("mediaLinks", cursor.getString(19));
                        jsonObject.put("ActionType", cursor.getString(20));
                        jsonObject.put("isSelected", "false");

                        if (ChatMessages.SENDER.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.SENDER);
                        } else if (ChatMessages.SENDER_STICKER.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.SENDER_STICKER);
                        } else if (ChatMessages.RECEIVER_STICKER.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.RECEIVER_STICKER);
                        } else if (ChatMessages.SENDER_IMAGE.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.SENDER_IMAGE);
                        } else if (ChatMessages.RECEIVER.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.RECEIVER);
                        } else if (ChatMessages.RECEIVER_IMAGE.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.RECEIVER_IMAGE);
                        } else if (ChatMessages.RECEIVER_VIDEO.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.RECEIVER_VIDEO);
                        } else if (ChatMessages.RECEIVER_AUDIO.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.RECEIVER_AUDIO);
                        } else if (ChatMessages.SENDER_AUDIO.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.SENDER_AUDIO);
                        } else if (ChatMessages.SENDER_DOC.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.SENDER_DOC);
                        } else if (ChatMessages.RECEIVER_DOC.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.RECEIVER_DOC);
                        } else if (ChatMessages.SENDER_VIDEO.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.SENDER_VIDEO);
                        } else if (ChatMessages.SENDER_LOCATION.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.SENDER_LOCATION);
                            jsonObject.put("lat", cursor.getString(12));
                            jsonObject.put("lng", cursor.getString(13));
                        } else if (ChatMessages.RECEIVER_LOCATION.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.RECEIVER_LOCATION);
                            jsonObject.put("lat", cursor.getString(12));
                            jsonObject.put("lng", cursor.getString(13));

                        } else if (ChatMessages.HEADER.equalsIgnoreCase(cursor.getString(5))) {
                            try {
                                jsonObject.put("userType", ChatMessages.HEADER);
                                double time = Double.parseDouble(cursor.getString(7));
                                String date = Utils.getDate((long) time, "dd/MM/yyyy");
                                String sortDate = Utils.formatToYesterdayOrToday(date);
                                jsonObject.put("date", sortDate);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        } else if (ChatMessages.SENDER_CONTACT.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.SENDER_CONTACT);
                            jsonObject.put("cName", cursor.getString(15));
                            jsonObject.put("cNumber", cursor.getString(16));
                        } else if (ChatMessages.RECEIVER_CONTACT.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.RECEIVER_CONTACT);
                            jsonObject.put("cName", cursor.getString(15));
                            jsonObject.put("cNumber", cursor.getString(16));
                        }


                        jsonArray.put(jsonObject);

                        Log.d("GetChatsMessages: ", "");

                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();
        return jsonArray;
    }

    public int getProfilesCount(String userId) {

        int cnt = 0;
        try {
            String countQuery = "SELECT  * FROM " + "ChatMessages where userId=" + userId;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(countQuery, null);
            cnt = cursor.getCount();
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cnt;
    }

    public JSONArray GetRemChatsMessages(String zeoChatID, String start_value, String no_values) {
        JSONArray jsonArray = new JSONArray();

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        int f_val = getProfilesCount(zeoChatID) - 70;
        Cursor cursor = sqLiteDatabase.rawQuery("select * from ChatMessages where userId='" + zeoChatID + "' ORDER BY sentTime LIMIT " + "0" + ", " + f_val, null);
        int count = cursor.getCount();
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(17).equalsIgnoreCase("0")) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("msgId", cursor.getString(0));
                        jsonObject.put("userId", cursor.getString(1));
                        jsonObject.put("msg", cursor.getString(2));

                        if ("text".equalsIgnoreCase(cursor.getString(3))) {
                            jsonObject.put("chatType", ChatType.text);
                        } else if ("image".equalsIgnoreCase(cursor.getString(3))) {
                            jsonObject.put("chatType", ChatType.image);
                        } else if (ChatType.location.equals(cursor.getString(3))) {
                            jsonObject.put("chatType", ChatType.location);
                        } else if (ChatType.contact.equals(cursor.getString(3))) {
                            jsonObject.put("chatType", ChatType.contact);
                        } else if (ChatType.video.equals(cursor.getString(3))) {
                            jsonObject.put("chatType", ChatType.video);
                        } else if ("audio".equalsIgnoreCase(cursor.getString(3))) {
                            jsonObject.put("chatType", ChatType.audio);
                            jsonObject.put("isPlaying", "0");
                            jsonObject.put("playingStatus", "0");

                        }
                        jsonObject.put("contentStatus", cursor.getString(4));
                        jsonObject.put("sentTime", cursor.getString(7));
                        jsonObject.put("deliveredTime", cursor.getString(8));
                        jsonObject.put("seenTime", cursor.getString(9));
                        jsonObject.put("caption", cursor.getString(10));
                        jsonObject.put("upload", cursor.getString(11));
                        jsonObject.put("download", cursor.getString(12));
                        jsonObject.put("groupId", cursor.getString(6));
                        jsonObject.put("chatRoomType", cursor.getString(17));
                        jsonObject.put("isStarred", cursor.getString(18));
                        jsonObject.put("userName", GetUserName(cursor.getString(1)));
                        jsonObject.put("mediaLinks", cursor.getString(19));
                        jsonObject.put("ActionType", cursor.getString(20));
                        jsonObject.put("isSelected", "false");

                        if (ChatMessages.SENDER.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.SENDER);
                        } else if (ChatMessages.SENDER_STICKER.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.SENDER_STICKER);
                        } else if (ChatMessages.RECEIVER_STICKER.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.RECEIVER_STICKER);
                        } else if (ChatMessages.SENDER_IMAGE.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.SENDER_IMAGE);
                        } else if (ChatMessages.RECEIVER.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.RECEIVER);
                        } else if (ChatMessages.RECEIVER_IMAGE.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.RECEIVER_IMAGE);
                        } else if (ChatMessages.RECEIVER_VIDEO.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.RECEIVER_VIDEO);
                        } else if (ChatMessages.RECEIVER_AUDIO.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.RECEIVER_AUDIO);

                        } else if (ChatMessages.SENDER_AUDIO.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.SENDER_AUDIO);
                        } else if (ChatMessages.SENDER_DOC.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.SENDER_DOC);
                        } else if (ChatMessages.RECEIVER_DOC.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.RECEIVER_DOC);
                        } else if (ChatMessages.SENDER_VIDEO.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.SENDER_VIDEO);
                        } else if (ChatMessages.SENDER_LOCATION.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.SENDER_LOCATION);
                            jsonObject.put("lat", cursor.getString(12));
                            jsonObject.put("lng", cursor.getString(13));
                        } else if (ChatMessages.RECEIVER_LOCATION.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.RECEIVER_LOCATION);
                            jsonObject.put("lat", cursor.getString(12));
                            jsonObject.put("lng", cursor.getString(13));

                        } else if (ChatMessages.HEADER.equalsIgnoreCase(cursor.getString(5))) {
                            try {
                                jsonObject.put("userType", ChatMessages.HEADER);
                                double time = Double.parseDouble(cursor.getString(7));
                                String date = Utils.getDate((long) time, "dd/MM/yyyy");
                                String sortDate = Utils.formatToYesterdayOrToday(date);
                                jsonObject.put("date", sortDate);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        } else if (ChatMessages.SENDER_CONTACT.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.SENDER_CONTACT);
                            jsonObject.put("cName", cursor.getString(15));
                            jsonObject.put("cNumber", cursor.getString(16));
                        } else if (ChatMessages.RECEIVER_CONTACT.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.RECEIVER_CONTACT);
                            jsonObject.put("cName", cursor.getString(15));
                            jsonObject.put("cNumber", cursor.getString(16));
                        }


                        jsonArray.put(jsonObject);

                        Log.d("GetChatsMessages: ", "");

                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();
        return jsonArray;
    }


    public JSONArray GetGroupChatslimitMessages(String groupid, String start_value, String no_values) {
        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from ChatMessages where groupId='" + groupid + "' ORDER BY sentTime LIMIT " + start_value + ", " + no_values, null);
//        Cursor cursor = sqLiteDatabase.rawQuery("select * from ChatMessages where userId='" + zeoChatID + "'", null);groupId

        Log.d("GetGroupChatsMessages: ", ",cursor:" + cursor.getCount() + "select * from ChatMessages where groupId='" + groupid + "'");
        if (cursor.moveToFirst()) {
            do {
                String groupId = cursor.getString(6);
                if (groupId.equalsIgnoreCase(groupid)) {
                    if (cursor.getString(17).equalsIgnoreCase("1")) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("msgId", cursor.getString(0));
                            jsonObject.put("userId", cursor.getString(1));
                            jsonObject.put("msg", cursor.getString(2));

                            if ("text".equalsIgnoreCase(cursor.getString(3))) {
                                jsonObject.put("chatType", ChatType.text);
                            } else if ("image".equalsIgnoreCase(cursor.getString(3))) {
                                jsonObject.put("chatType", ChatType.image);
                            } else if (ChatType.location.equals(cursor.getString(3))) {
                                jsonObject.put("chatType", ChatType.location);
                            } else if (ChatType.contact.equals(cursor.getString(3))) {
                                jsonObject.put("chatType", ChatType.contact);
                            } else if (ChatType.video.equals(cursor.getString(3))) {
                                jsonObject.put("chatType", ChatType.video);
                            }
                            jsonObject.put("contentStatus", cursor.getString(4));
                            jsonObject.put("sentTime", cursor.getString(7));
                            jsonObject.put("deliveredTime", cursor.getString(8));
                            jsonObject.put("seenTime", cursor.getString(9));
                            jsonObject.put("caption", cursor.getString(10));
                            jsonObject.put("upload", cursor.getString(11));
                            jsonObject.put("download", cursor.getString(12));
                            jsonObject.put("cName", cursor.getString(15));
                            jsonObject.put("cNumber", cursor.getString(16));
                            jsonObject.put("groupId", cursor.getString(6));
                            jsonObject.put("chatRoomType", cursor.getString(17));
                            jsonObject.put("isStarred", cursor.getString(18));
                            jsonObject.put("mediaLinks", cursor.getString(19));
                            jsonObject.put("ActionType", cursor.getString(20));
                            jsonObject.put("userName", GetUserName(cursor.getString(1)));
                            jsonObject.put("isSelected", "false");

                            if (ChatMessages.SENDER.equalsIgnoreCase(cursor.getString(5))) {
                                jsonObject.put("userType", ChatMessages.SENDER);
                            } else if (ChatMessages.SENDER_IMAGE.equalsIgnoreCase(cursor.getString(5))) {
                                jsonObject.put("userType", ChatMessages.SENDER_IMAGE);
                            } else if (ChatMessages.SENDER_STICKER.equalsIgnoreCase(cursor.getString(5))) {
                                jsonObject.put("userType", ChatMessages.SENDER_STICKER);
                            } else if (ChatMessages.RECEIVER_STICKER.equalsIgnoreCase(cursor.getString(5))) {
                                jsonObject.put("userType", ChatMessages.RECEIVER_STICKER);
                            } else if (ChatMessages.CREATE_GROUP.equalsIgnoreCase(cursor.getString(5))) {
                                jsonObject.put("userType", ChatMessages.CREATE_GROUP);
                            } else if (ChatMessages.RECEIVER.equalsIgnoreCase(cursor.getString(5))) {
                                jsonObject.put("userType", ChatMessages.RECEIVER);
                            } else if (ChatMessages.RECEIVER_IMAGE.equalsIgnoreCase(cursor.getString(5))) {
                                jsonObject.put("userType", ChatMessages.RECEIVER_IMAGE);
                            } else if (ChatMessages.RECEIVER_AUDIO.equalsIgnoreCase(cursor.getString(5))) {
                                jsonObject.put("userType", ChatMessages.RECEIVER_AUDIO);
                            } else if (ChatMessages.SENDER_AUDIO.equalsIgnoreCase(cursor.getString(5))) {
                                jsonObject.put("userType", ChatMessages.SENDER_AUDIO);
                            } else if (ChatMessages.SENDER_DOC.equalsIgnoreCase(cursor.getString(5))) {
                                jsonObject.put("userType", ChatMessages.SENDER_DOC);
                            } else if (ChatMessages.RECEIVER_DOC.equalsIgnoreCase(cursor.getString(5))) {
                                jsonObject.put("userType", ChatMessages.RECEIVER_DOC);
                            } else if (ChatMessages.RECEIVER_VIDEO.equalsIgnoreCase(cursor.getString(5))) {
                                jsonObject.put("userType", ChatMessages.RECEIVER_VIDEO);
                            } else if (ChatMessages.SENDER_VIDEO.equalsIgnoreCase(cursor.getString(5))) {
                                jsonObject.put("userType", ChatMessages.SENDER_VIDEO);
                            } else if (ChatMessages.SENDER_LOCATION.equalsIgnoreCase(cursor.getString(5))) {
                                jsonObject.put("userType", ChatMessages.SENDER_LOCATION);
                                jsonObject.put("lat", cursor.getString(12));
                                jsonObject.put("lng", cursor.getString(13));
                            } else if (ChatMessages.RECEIVER_LOCATION.equalsIgnoreCase(cursor.getString(5))) {
                                jsonObject.put("userType", ChatMessages.RECEIVER_LOCATION);
                                jsonObject.put("lat", cursor.getString(12));
                                jsonObject.put("lng", cursor.getString(13));

                            } else if (ChatMessages.HEADER.equalsIgnoreCase(cursor.getString(5))) {
                                try {
                                    jsonObject.put("userType", ChatMessages.HEADER);
                                    double time = Double.parseDouble(cursor.getString(7));
                                    String date = Utils.getDate((long) time, "dd/MM/yyyy");
                                    String sortDate = Utils.formatToYesterdayOrToday(date);
                                    jsonObject.put("date", sortDate);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }

                            jsonArray.put(jsonObject);

                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }


            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();
        return jsonArray;
    }


    public JSONArray GetChatsStarred() {
        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from ChatMessages where isStarred = 'true' ", null);
        if (cursor.moveToFirst()) {
            do {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("msgId", cursor.getString(0));
                    jsonObject.put("userId", cursor.getString(1));
                    jsonObject.put("msg", cursor.getString(2));

                    if ("text".equalsIgnoreCase(cursor.getString(3))) {
                        jsonObject.put("chatType", ChatType.text);
                    } else if ("image".equalsIgnoreCase(cursor.getString(3))) {
                        jsonObject.put("chatType", ChatType.image);
                    } else if (ChatType.location.equals(cursor.getString(3))) {
                        jsonObject.put("chatType", ChatType.location);
                    } else if (ChatType.contact.equals(cursor.getString(3))) {
                        jsonObject.put("chatType", ChatType.contact);
                    } else if (ChatType.video.equals(cursor.getString(3))) {
                        jsonObject.put("chatType", ChatType.video);
                    } else if ("audio".equalsIgnoreCase(cursor.getString(3))) {
                        jsonObject.put("chatType", ChatType.audio);
                        jsonObject.put("isPlaying", "0");
                        jsonObject.put("playingStatus", "0");

                    }
                    jsonObject.put("contentStatus", cursor.getString(4));
                    jsonObject.put("sentTime", cursor.getString(7));
                    jsonObject.put("deliveredTime", cursor.getString(8));
                    jsonObject.put("seenTime", cursor.getString(9));
                    jsonObject.put("caption", cursor.getString(10));
                    jsonObject.put("upload", cursor.getString(11));
                    jsonObject.put("download", cursor.getString(12));
                    jsonObject.put("groupId", cursor.getString(6));
                    jsonObject.put("chatRoomType", cursor.getString(17));
                    jsonObject.put("isStarred", cursor.getString(18));
                    jsonObject.put("userName", GetUserName(cursor.getString(1)));
                    jsonObject.put("mediaLinks", cursor.getString(19));
                    jsonObject.put("ActionType", cursor.getString(20));

                    jsonObject.put("isSelected", "false");
                    jsonObject.put("showPreview", cursor.getString(21));
                    jsonObject.put("metaTitle", cursor.getString(22));
                    jsonObject.put("metaDescription", cursor.getString(23));
                    jsonObject.put("metaLogo", cursor.getString(24));
                    jsonObject.put("shouldSign", cursor.getString(25));

                    if (ChatMessages.SENDER.equalsIgnoreCase(cursor.getString(5))) {
                        jsonObject.put("userType", ChatMessages.SENDER);
                    } else if (ChatMessages.SENDER_STICKER.equalsIgnoreCase(cursor.getString(5))) {
                        jsonObject.put("userType", ChatMessages.SENDER_STICKER);
                    } else if (ChatMessages.RECEIVER_STICKER.equalsIgnoreCase(cursor.getString(5))) {
                        jsonObject.put("userType", ChatMessages.RECEIVER_STICKER);
                    } else if (ChatMessages.SENDER_IMAGE.equalsIgnoreCase(cursor.getString(5))) {
                        jsonObject.put("userType", ChatMessages.SENDER_IMAGE);
                    } else if (ChatMessages.RECEIVER.equalsIgnoreCase(cursor.getString(5))) {
                        jsonObject.put("userType", ChatMessages.RECEIVER);
                    } else if (ChatMessages.RECEIVER_IMAGE.equalsIgnoreCase(cursor.getString(5))) {
                        jsonObject.put("userType", ChatMessages.RECEIVER_IMAGE);
                    } else if (ChatMessages.RECEIVER_VIDEO.equalsIgnoreCase(cursor.getString(5))) {
                        jsonObject.put("userType", ChatMessages.RECEIVER_VIDEO);
                    } else if (ChatMessages.RECEIVER_AUDIO.equalsIgnoreCase(cursor.getString(5))) {
                        jsonObject.put("userType", ChatMessages.RECEIVER_AUDIO);
                    } else if (ChatMessages.SENDER_AUDIO.equalsIgnoreCase(cursor.getString(5))) {
                        jsonObject.put("userType", ChatMessages.SENDER_AUDIO);
                    } else if (ChatMessages.SENDER_DOC.equalsIgnoreCase(cursor.getString(5))) {
                        jsonObject.put("userType", ChatMessages.SENDER_DOC);
                    } else if (ChatMessages.RECEIVER_DOC.equalsIgnoreCase(cursor.getString(5))) {
                        jsonObject.put("userType", ChatMessages.RECEIVER_DOC);
                    } else if (ChatMessages.SENDER_VIDEO.equalsIgnoreCase(cursor.getString(5))) {
                        jsonObject.put("userType", ChatMessages.SENDER_VIDEO);
                    } else if (ChatMessages.SENDER_LOCATION.equalsIgnoreCase(cursor.getString(5))) {
                        jsonObject.put("userType", ChatMessages.SENDER_LOCATION);
                        jsonObject.put("lat", cursor.getString(12));
                        jsonObject.put("lng", cursor.getString(13));
                    } else if (ChatMessages.RECEIVER_LOCATION.equalsIgnoreCase(cursor.getString(5))) {
                        jsonObject.put("userType", ChatMessages.RECEIVER_LOCATION);
                        jsonObject.put("lat", cursor.getString(12));
                        jsonObject.put("lng", cursor.getString(13));

                    } else if (ChatMessages.HEADER.equalsIgnoreCase(cursor.getString(5))) {
                        try {
                            jsonObject.put("userType", ChatMessages.HEADER);
                            double time = Double.parseDouble(cursor.getString(7));
                            String date = Utils.getDate((long) time, "dd/MM/yyyy");
                            String sortDate = Utils.formatToYesterdayOrToday(date);
                            jsonObject.put("date", sortDate);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    } else if (ChatMessages.SENDER_CONTACT.equalsIgnoreCase(cursor.getString(5))) {
                        jsonObject.put("userType", ChatMessages.SENDER_CONTACT);
                        jsonObject.put("cName", cursor.getString(15));
                        jsonObject.put("cNumber", cursor.getString(16));
                    } else if (ChatMessages.RECEIVER_CONTACT.equalsIgnoreCase(cursor.getString(5))) {
                        jsonObject.put("userType", ChatMessages.RECEIVER_CONTACT);
                        jsonObject.put("cName", cursor.getString(15));
                        jsonObject.put("cNumber", cursor.getString(16));
                    }

                    jsonArray.put(jsonObject);


                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }


            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();
        Log.d("getstarredmsg: ", "star_value:" + jsonArray.toString());
        return jsonArray;
    }


    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    public JSONArray GetChatsMessages(String zeoChatID) {
        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from ChatMessages where userId='" + zeoChatID + "'", null);
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(17).equalsIgnoreCase("0") || cursor.getString(17).equalsIgnoreCase("2")) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("msgId", cursor.getString(0));
                        jsonObject.put("userId", cursor.getString(1));
                        jsonObject.put("msg", cursor.getString(2));

                        if ("text".equalsIgnoreCase(cursor.getString(3))) {
                            jsonObject.put("chatType", ChatType.text);
                        } else if ("image".equalsIgnoreCase(cursor.getString(3))) {
                            jsonObject.put("chatType", ChatType.image);
                        } else if (ChatType.location.equals(cursor.getString(3))) {
                            jsonObject.put("chatType", ChatType.location);
                        } else if (ChatType.contact.equals(cursor.getString(3))) {
                            jsonObject.put("chatType", ChatType.contact);
                        } else if (ChatType.video.equals(cursor.getString(3))) {
                            jsonObject.put("chatType", ChatType.video);
                        }
                        jsonObject.put("contentStatus", cursor.getString(4));
                        jsonObject.put("sentTime", cursor.getString(7));
                        jsonObject.put("deliveredTime", cursor.getString(8));
                        jsonObject.put("seenTime", cursor.getString(9));
                        jsonObject.put("caption", cursor.getString(10));
                        jsonObject.put("upload", cursor.getString(11));
                        jsonObject.put("download", cursor.getString(12));
                        jsonObject.put("groupId", cursor.getString(6));
                        jsonObject.put("chatRoomType", cursor.getString(17));
                        jsonObject.put("isStarred", cursor.getString(18));
                        jsonObject.put("userName", GetUserName(cursor.getString(1)));
                        jsonObject.put("mediaLinks", cursor.getString(19));
                        jsonObject.put("ActionType", cursor.getString(20));
                        jsonObject.put("isSelected", "false");
                        jsonObject.put("showPreview", cursor.getString(21));
                        String showp = cursor.getString(22);
                        String metaTitle = cursor.getString(22);
                        String metaDescription = cursor.getString(23);
                        String metaLogo = cursor.getString(24);
                        jsonObject.put("metaTitle", metaTitle);
                        jsonObject.put("metaDescription", metaDescription);
                        jsonObject.put("metaLogo", metaLogo);
                        jsonObject.put("shouldSign", cursor.getString(25));


                        if (ChatMessages.SENDER.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.SENDER);
                        } else if (ChatMessages.SENDER_IMAGE.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.SENDER_IMAGE);
                        } else if (ChatMessages.RECEIVER.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.RECEIVER);
                        } else if (ChatMessages.RECEIVER_IMAGE.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.RECEIVER_IMAGE);
                        } else if (ChatMessages.RECEIVER_VIDEO.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.RECEIVER_VIDEO);
                        } else if (ChatMessages.RECEIVER_AUDIO.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.RECEIVER_AUDIO);
                        } else if (ChatMessages.SENDER_AUDIO.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.SENDER_AUDIO);
                        } else if (ChatMessages.SENDER_STICKER.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.SENDER_STICKER);
                        } else if (ChatMessages.RECEIVER_STICKER.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.RECEIVER_STICKER);
                        } else if (ChatMessages.SENDER_DOC.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.SENDER_DOC);
                        } else if (ChatMessages.RECEIVER_DOC.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.RECEIVER_DOC);
                        } else if (ChatMessages.SENDER_VIDEO.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.SENDER_VIDEO);
                        } else if (ChatMessages.SENDER_LOCATION.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.SENDER_LOCATION);
                            jsonObject.put("lat", cursor.getString(13));
                            jsonObject.put("lng", cursor.getString(14));
                        } else if (ChatMessages.RECEIVER_LOCATION.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.RECEIVER_LOCATION);
                            jsonObject.put("lat", cursor.getString(13));
                            jsonObject.put("lng", cursor.getString(14));

                        } else if (ChatMessages.HEADER.equalsIgnoreCase(cursor.getString(5))) {
                            try {
                                jsonObject.put("userType", ChatMessages.HEADER);
                                double time = Double.parseDouble(cursor.getString(7));
                                String date = Utils.getDate((long) time, "dd/MM/yyyy");
                                String sortDate = Utils.formatToYesterdayOrToday(date);
                                jsonObject.put("date", sortDate);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        } else if (ChatMessages.SENDER_CONTACT.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.SENDER_CONTACT);
                            jsonObject.put("cName", cursor.getString(15));
                            jsonObject.put("cNumber", cursor.getString(16));
                        } else if (ChatMessages.RECEIVER_CONTACT.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.RECEIVER_CONTACT);
                            jsonObject.put("cName", cursor.getString(15));
                            jsonObject.put("cNumber", cursor.getString(16));
                        }

                        jsonArray.put(jsonObject);

                        Log.d("GetChatsMessages: ", "");

                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();
        return jsonArray;
    }


    public JSONArray GetChatsMessagesUnSent() {
        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from ChatMessages where contentStatus='" + Status.SENDING + "'", null);
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(17).equalsIgnoreCase("0")) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("msgId", cursor.getString(0));
                        jsonObject.put("userId", cursor.getString(1));
                        jsonObject.put("msg", cursor.getString(2));

                        if ("text".equalsIgnoreCase(cursor.getString(3))) {
                            jsonObject.put("chatType", ChatType.text);
                        } else if ("image".equalsIgnoreCase(cursor.getString(3))) {
                            jsonObject.put("chatType", ChatType.image);
                        } else if (ChatType.location.equals(cursor.getString(3))) {
                            jsonObject.put("chatType", ChatType.location);
                        } else if (ChatType.contact.equals(cursor.getString(3))) {
                            jsonObject.put("chatType", ChatType.contact);
                        } else if (ChatType.video.equals(cursor.getString(3))) {
                            jsonObject.put("chatType", ChatType.video);
                        }
                        jsonObject.put("contentStatus", cursor.getString(4));
                        jsonObject.put("sentTime", cursor.getString(7));
                        jsonObject.put("deliveredTime", cursor.getString(8));
                        jsonObject.put("seenTime", cursor.getString(9));
                        jsonObject.put("caption", cursor.getString(10));
                        jsonObject.put("upload", cursor.getString(11));
                        jsonObject.put("download", cursor.getString(12));
                        jsonObject.put("groupId", cursor.getString(6));
                        jsonObject.put("chatRoomType", cursor.getString(17));
                        jsonObject.put("isStarred", cursor.getString(18));
                        jsonObject.put("userName", GetUserName(cursor.getString(1)));
                        jsonObject.put("mediaLinks", cursor.getString(19));
                        jsonObject.put("ActionType", cursor.getString(20));
                        jsonObject.put("isSelected", "false");
                        jsonObject.put("showPreview", cursor.getString(21));
                        String metaTitle = cursor.getString(22);
                        String metaDescription = cursor.getString(23);
                        String metaLogo = cursor.getString(24);
                        jsonObject.put("metaTitle", metaTitle);
                        jsonObject.put("metaDescription", metaDescription);
                        jsonObject.put("metaLogo", metaLogo);
                        jsonObject.put("shouldSign", cursor.getString(25));

                        if (ChatMessages.SENDER.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.SENDER);
                        } else if (ChatMessages.SENDER_IMAGE.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.SENDER_IMAGE);
                        } else if (ChatMessages.RECEIVER.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.RECEIVER);
                        } else if (ChatMessages.RECEIVER_IMAGE.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.RECEIVER_IMAGE);
                        } else if (ChatMessages.RECEIVER_VIDEO.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.RECEIVER_VIDEO);
                        } else if (ChatMessages.SENDER_STICKER.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.SENDER_STICKER);
                        } else if (ChatMessages.RECEIVER_STICKER.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.RECEIVER_STICKER);
                        } else if (ChatMessages.RECEIVER_AUDIO.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.RECEIVER_AUDIO);
                        } else if (ChatMessages.SENDER_AUDIO.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.SENDER_AUDIO);
                        } else if (ChatMessages.SENDER_DOC.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.SENDER_DOC);
                        } else if (ChatMessages.RECEIVER_DOC.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.RECEIVER_DOC);
                        } else if (ChatMessages.SENDER_VIDEO.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.SENDER_VIDEO);
                        } else if (ChatMessages.SENDER_LOCATION.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.SENDER_LOCATION);
                            jsonObject.put("lat", cursor.getString(13));
                            jsonObject.put("lng", cursor.getString(14));
                        } else if (ChatMessages.RECEIVER_LOCATION.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.RECEIVER_LOCATION);
                            jsonObject.put("lat", cursor.getString(13));
                            jsonObject.put("lng", cursor.getString(14));

                        } else if (ChatMessages.HEADER.equalsIgnoreCase(cursor.getString(5))) {
                            try {
                                jsonObject.put("userType", ChatMessages.HEADER);
                                double time = Double.parseDouble(cursor.getString(7));
                                String date = Utils.getDate((long) time, "dd/MM/yyyy");
                                String sortDate = Utils.formatToYesterdayOrToday(date);
                                jsonObject.put("date", sortDate);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        } else if (ChatMessages.SENDER_CONTACT.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.SENDER_CONTACT);
                            jsonObject.put("cName", cursor.getString(15));
                            jsonObject.put("cNumber", cursor.getString(16));
                        } else if (ChatMessages.RECEIVER_CONTACT.equalsIgnoreCase(cursor.getString(5))) {
                            jsonObject.put("userType", ChatMessages.RECEIVER_CONTACT);
                            jsonObject.put("cName", cursor.getString(15));
                            jsonObject.put("cNumber", cursor.getString(16));
                        }

                        jsonArray.put(jsonObject);

                        Log.d("GetChatsMessages: ", "");

                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();
        return jsonArray;
    }


    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    public JSONArray GetGroupChatsMessages(String zoeChatID) {
        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from ChatMessages where groupId='" + zoeChatID + "'", null);
//        Cursor cursor = sqLiteDatabase.rawQuery("select * from ChatMessages where userId='" + zeoChatID + "'", null);groupId

        Log.d("GetGroupChatsMessages: ", ",cursor:" + cursor.getCount() + "select * from ChatMessages where groupId='" + zoeChatID + "'");
        if (cursor.moveToFirst()) {
            do {
                String groupId = cursor.getString(6);
                if (groupId.equalsIgnoreCase(zoeChatID)) {
                    if (cursor.getString(17).equalsIgnoreCase("1") || cursor.getString(17).equalsIgnoreCase("2") || cursor.getString(17).equalsIgnoreCase("3")) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("msgId", cursor.getString(0));
                            jsonObject.put("userId", cursor.getString(1));
                            jsonObject.put("msg", cursor.getString(2));
                            Log.d("GetGroupChatsMessages: ", "cursor:" + cursor.getString(3));
                            if ("text".equalsIgnoreCase(cursor.getString(3))) {
                                jsonObject.put("chatType", ChatType.text);
                            } else if ("image".equalsIgnoreCase(cursor.getString(3))) {
                                jsonObject.put("chatType", ChatType.image);
                            } else if (ChatType.location.equals(cursor.getString(3))) {
                                jsonObject.put("chatType", ChatType.location);
                            } else if (ChatType.contact.equals(cursor.getString(3))) {
                                jsonObject.put("chatType", ChatType.contact);
                            } else if (ChatType.video.equals(cursor.getString(3))) {
                                jsonObject.put("chatType", ChatType.video);
                            }
                            jsonObject.put("contentStatus", cursor.getString(4));
                            jsonObject.put("sentTime", cursor.getString(7));
                            jsonObject.put("deliveredTime", cursor.getString(8));
                            jsonObject.put("seenTime", cursor.getString(9));
                            jsonObject.put("caption", cursor.getString(10));
                            jsonObject.put("upload", cursor.getString(11));
                            jsonObject.put("download", cursor.getString(12));
                            jsonObject.put("cName", cursor.getString(15));
                            jsonObject.put("cNumber", cursor.getString(16));
                            jsonObject.put("groupId", cursor.getString(6));
                            jsonObject.put("chatRoomType", cursor.getString(17));
                            jsonObject.put("isStarred", cursor.getString(18));
                            jsonObject.put("mediaLinks", cursor.getString(19));
                            jsonObject.put("ActionType", cursor.getString(20));
                            jsonObject.put("userName", GetUserName(cursor.getString(1)));
                            jsonObject.put("isSelected", "false");
                            jsonObject.put("showPreview", cursor.getString(21));
                            jsonObject.put("shouldSign", cursor.getString(24));
                            String showp = cursor.getString(22);
                            String metaTitle = cursor.getString(22);
                            String metaDescription = cursor.getString(23);
                            String metaLogo = cursor.getString(24);
                            jsonObject.put("metaTitle", metaTitle);
                            jsonObject.put("metaDescription", metaDescription);
                            jsonObject.put("metaLogo", metaLogo);

                            if (ChatMessages.SENDER.equalsIgnoreCase(cursor.getString(5))) {
                                jsonObject.put("userType", ChatMessages.SENDER);
                            } else if (ChatMessages.SENDER_IMAGE.equalsIgnoreCase(cursor.getString(5))) {
                                jsonObject.put("userType", ChatMessages.SENDER_IMAGE);
                            } else if (ChatMessages.SENDER_STICKER.equalsIgnoreCase(cursor.getString(5))) {
                                jsonObject.put("userType", ChatMessages.SENDER_STICKER);
                            } else if (ChatMessages.SENDER_CONTACT.equalsIgnoreCase(cursor.getString(5))) {
                                jsonObject.put("userType", ChatMessages.SENDER_CONTACT);
                                jsonObject.put("cName", cursor.getString(15));
                                jsonObject.put("cNumber", cursor.getString(16));
                            } else if (ChatMessages.RECEIVER_CONTACT.equalsIgnoreCase(cursor.getString(5))) {
                                jsonObject.put("userType", ChatMessages.RECEIVER_CONTACT);
                                jsonObject.put("cName", cursor.getString(15));
                                jsonObject.put("cNumber", cursor.getString(16));
                            } else if (ChatMessages.RECEIVER_STICKER.equalsIgnoreCase(cursor.getString(5))) {
                                jsonObject.put("userType", ChatMessages.RECEIVER_STICKER);
                            } else if (ChatMessages.CREATE_GROUP.equalsIgnoreCase(cursor.getString(5))) {
                                jsonObject.put("userType", ChatMessages.CREATE_GROUP);
                            } else if (ChatMessages.RECEIVER.equalsIgnoreCase(cursor.getString(5))) {
                                jsonObject.put("userType", ChatMessages.RECEIVER);
                            } else if (ChatMessages.RECEIVER_IMAGE.equalsIgnoreCase(cursor.getString(5))) {
                                jsonObject.put("userType", ChatMessages.RECEIVER_IMAGE);
                            } else if (ChatMessages.RECEIVER_AUDIO.equalsIgnoreCase(cursor.getString(5))) {
                                jsonObject.put("userType", ChatMessages.RECEIVER_AUDIO);
                            } else if (ChatMessages.SENDER_AUDIO.equalsIgnoreCase(cursor.getString(5))) {
                                jsonObject.put("userType", ChatMessages.SENDER_AUDIO);
                            } else if (ChatMessages.SENDER_DOC.equalsIgnoreCase(cursor.getString(5))) {
                                jsonObject.put("userType", ChatMessages.SENDER_DOC);
                            } else if (ChatMessages.RECEIVER_DOC.equalsIgnoreCase(cursor.getString(5))) {
                                jsonObject.put("userType", ChatMessages.RECEIVER_DOC);
                            } else if (ChatMessages.RECEIVER_VIDEO.equalsIgnoreCase(cursor.getString(5))) {
                                jsonObject.put("userType", ChatMessages.RECEIVER_VIDEO);
                            } else if (ChatMessages.SENDER_VIDEO.equalsIgnoreCase(cursor.getString(5))) {
                                jsonObject.put("userType", ChatMessages.SENDER_VIDEO);
                            } else if (ChatMessages.SENDER_LOCATION.equalsIgnoreCase(cursor.getString(5))) {
                                jsonObject.put("userType", ChatMessages.SENDER_LOCATION);
                                jsonObject.put("lat", cursor.getString(13));
                                jsonObject.put("lng", cursor.getString(14));
                            } else if (ChatMessages.RECEIVER_LOCATION.equalsIgnoreCase(cursor.getString(5))) {
                                jsonObject.put("userType", ChatMessages.RECEIVER_LOCATION);
                                jsonObject.put("lat", cursor.getString(13));
                                jsonObject.put("lng", cursor.getString(14));

                            } else if (ChatMessages.HEADER.equalsIgnoreCase(cursor.getString(5))) {
                                try {
                                    jsonObject.put("userType", ChatMessages.HEADER);
                                    double time = Double.parseDouble(cursor.getString(7));
                                    String date = Utils.getDate((long) time, "dd/MM/yyyy");
                                    String sortDate = Utils.formatToYesterdayOrToday(date);
                                    jsonObject.put("date", sortDate);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }

                            jsonArray.put(jsonObject);

                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }


            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();
        return jsonArray;
    }

    public void DeleteChatsMsg(String id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete("ChatMessages", "id = ? ", new String[]{id});
        sqLiteDatabase.close();

    }

    public void DeleteChatsComplete(String id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete("ChatMessages", "userId =? AND chatRoomType =? ", new String[]{id, "0"});
        sqLiteDatabase.close();

    }


    public void DeleteGroupParticipants(String part_id, String group_id) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete("GroupParticipants", "userId" + "=? AND groupId =?", new String[]{part_id, group_id});
        sqLiteDatabase.close();


    }

    public void DeleteChannelParticipants(String part_id, String group_id) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete("ChannelParticipants", "userId" + "=? AND channelId =?", new String[]{part_id, group_id});
        sqLiteDatabase.close();


    }


    public void DeleteGrpMsg(String id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete("ChatMessages", "groupId = ? ", new String[]{id});
        sqLiteDatabase.close();

    }

    public void UpdateStarred(String id, String isStarred) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE ChatMessages SET isStarred = '" + isStarred + "' WHERE id = '" + id + "'");
        db.close();


    }

    public String getDeleteStatus(String groupId) {
        String value = "";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from Chats where chatRoomId='" + groupId + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            value = cursor.getString(13);
        }
        cursor.close();
        sqLiteDatabase.close();
        return value;


    }

    public String getNotifications(String groupId) {
        String value = "";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from Chats where chatRoomId='" + groupId + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            value = cursor.getString(22);
        }
        cursor.close();
        sqLiteDatabase.close();
        return value;

    }

    public void UpdateNotifications(String chatRoomId, String isStarred) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Chats SET shouldNotify = '" + isStarred + "' WHERE chatRoomId = '" + chatRoomId + "'");
        db.close();


    }

    public String GetMessage(String s) {
        String value = "";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from ChatMessages where id='" + s + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            value = cursor.getString(2);
        }
        cursor.close();
        sqLiteDatabase.close();
        return value;

    }

    public String GetMessagetime(String s) {
        String value = "";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from ChatMessages where id='" + s + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            value = cursor.getString(7);
        }
        cursor.close();
        sqLiteDatabase.close();
        return value;

    }

    public String GetMessageRoomtype(String s) {
        String value = "";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from ChatMessages where id='" + s + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            value = cursor.getString(17);
        }
        cursor.close();
        sqLiteDatabase.close();
        return value;

    }

    public String GetMessageType(String s) {
        String value = "";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from ChatMessages where id='" + s + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            value = cursor.getString(3);
        }
        cursor.close();
        sqLiteDatabase.close();
        return value;

    }

    public String GetMessageLinks(String s) {
        String value = "";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from ChatMessages where id='" + s + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            value = cursor.getString(19);
        }
        cursor.close();
        sqLiteDatabase.close();
        return value;

    }


    public String GetCId(String s) {
        String value = "";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from ChatMessages where id='" + s + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            value = cursor.getString(16);
        }
        cursor.close();
        sqLiteDatabase.close();
        return value;

    }


    public String GetCName(String s) {
        String value = "";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from ChatMessages where id='" + s + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            value = cursor.getString(15);
        }
        cursor.close();
        sqLiteDatabase.close();
        return value;

    }


    public String GetMedia(String s) {
        String value = "";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from ChatMessages where id='" + s + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            value = cursor.getString(19);
        }
        cursor.close();
        sqLiteDatabase.close();
        return value;

    }


    public String GetgroupID(String s) {
        String value = "";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from ChatMessages where id='" + s + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            value = cursor.getString(6);
        }
        cursor.close();
        sqLiteDatabase.close();
        return value;

    }

    public String GetLogo(String s) {
        String value = "";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from ChatMessages where id='" + s + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            value = cursor.getString(24);
        }
        cursor.close();
        sqLiteDatabase.close();
        return value;

    }

    public String GetPreview(String s) {
        String value = "";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from ChatMessages where id='" + s + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            value = cursor.getString(21);
        }
        cursor.close();
        sqLiteDatabase.close();
        return value;

    }

    public String GetMetatitle(String s) {
        String value = "";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from ChatMessages where id='" + s + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            value = cursor.getString(22);
        }
        cursor.close();
        sqLiteDatabase.close();
        return value;

    }

    public String GetMetaDescription(String s) {
        String value = "";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from ChatMessages where id='" + s + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            value = cursor.getString(23);
        }
        cursor.close();
        sqLiteDatabase.close();
        return value;

    }

    public String getCreateby(String s) {
        String value = "";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from Chats where chatRoomId='" + s + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            value = cursor.getString(10);
        }
        cursor.close();
        sqLiteDatabase.close();
        return value;

    }

    public void UpdateMedia(String msgId, String absolutePath) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("mediaLinks", absolutePath);
        sqLiteDatabase.update("ChatMessages", contentValues, ID + "=?", new String[]{msgId});
        sqLiteDatabase.close();
    }

    public void DeleteChatsGrpComplete(String zoeChatID) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete("ChatMessages", "groupId =?", new String[]{zoeChatID});
        sqLiteDatabase.close();
    }

    public String GetGroupDesc(String group_id) {
        String value = "";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from Chats where chatRoomId='" + group_id + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            value = cursor.getString(20);
        }
        cursor.close();
        sqLiteDatabase.close();
        return value;
    }

    public String GetChannelLink(String group_id) {
        String value = "";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from Chats where chatRoomId='" + group_id + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            value = cursor.getString(18);
        }
        cursor.close();
        sqLiteDatabase.close();
        return value;
    }

    public String GetChannelType(String group_id) {
        String value = "";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from Chats where chatRoomId='" + group_id + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            value = cursor.getString(19);
        }
        cursor.close();
        sqLiteDatabase.close();
        return value;
    }

    public String GetSign(String group_id) {
        String value = "";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from Chats where chatRoomId='" + group_id + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            value = cursor.getString(21);
        }
        cursor.close();
        sqLiteDatabase.close();
        return value;
    }

    public JSONObject GetGroupdetails(String groupId_loc) throws JSONException {
        JSONObject object = new JSONObject();

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from GroupDetails where groupId='" + groupId_loc + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            object.put("name", cursor.getString(0));
            object.put("image", cursor.getString(3));
            object.put("createdBy", cursor.getString(1));
            object.put("createdAt", cursor.getString(2));
            object.put("groupId", cursor.getString(0));
        }
        cursor.close();
        sqLiteDatabase.close();

        return object;
    }
}
