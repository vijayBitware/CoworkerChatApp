package com.bitware.coworker.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.bitware.coworker.Service.ServiceClasss;
import com.bitware.coworker.baseUtils.Const;
import com.bitware.coworker.baseUtils.SharedHelper;
import com.bitware.coworker.models.ChatMessages;
import com.bitware.coworker.models.ChatType;
import com.bitware.coworker.models.ChatsMessagesModel;
import com.bitware.coworker.models.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.UUID;

import io.socket.client.IO;
import io.socket.client.Socket;


public class ChangeGrpName extends Activity {
    public static Socket socket;

    RelativeLayout relativeLayout;
    ImageView emojiButton;
    Button ok, cancel;
    private EmojiPopup emojiPopup;
    private ViewGroup rootView;
    private EmojiEditText chat_text;
    private DBHandler dbHandler;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String themevalue=SharedHelper.getKey(this,"theme_value");
        Setheme(themevalue);
        setContentView(R.layout.activity_change_grp_name);


        chat_text = (EmojiEditText) findViewById(R.id.grp_name_new);
        rootView = (ViewGroup) findViewById(R.id.edit_grp_name);
        emojiButton = (ImageView) findViewById(R.id.emojiButton);
        emojiButton.setImageResource(R.drawable.emoji_people);
        relativeLayout=(RelativeLayout)findViewById(R.id.header) ;
        relativeLayout.setBackgroundColor(getPrimaryCOlor(ChangeGrpName.this));
        setUpEmojiPopup();

        Intent intent = getIntent();
        IO.Options opts = new IO.Options();
        opts.forceNew = true;
        opts.reconnection = false;
        dbHandler = new DBHandler(ChangeGrpName.this);

        try {
            socket = IO.socket(Const.chatSocketURL, opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.e("SOCKET.IO ", e.getMessage());
        }

        final String grp_id = intent.getStringExtra("grop_id");
        final String from_id = intent.getStringExtra("from_id");
        final String old_id = intent.getStringExtra("old_name");
        final String broadcast = intent.getStringExtra("broadcast");

        ok = (Button) findViewById(R.id.ok_grp);
        cancel = (Button) findViewById(R.id.cancel_grp);
        ok.setBackgroundColor(getPrimaryCOlor(ChangeGrpName.this));
        cancel.setBackgroundColor(getPrimaryCOlor(ChangeGrpName.this));
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String g_name = chat_text.getText().toString();

                if (g_name.trim().equalsIgnoreCase(""))
                {
                    Toast.makeText(ChangeGrpName.this, getResources().getString(R.string.empty_name), Toast.LENGTH_SHORT).show();
                }
                else {

                    if (broadcast.equalsIgnoreCase("no")) {
                        try {
                            changegroupname(grp_id, from_id, g_name, old_id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        dbHandler.broadnameUpdate(grp_id, g_name);
                        Intent intent = new Intent(ChangeGrpName.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emojiPopup.toggle();
            }
        });

    }

    private void changegroupname(final String grp_id, String from_id, final String g_name, final String old_id) throws JSONException {
//        groupId,groupName,from


        JSONObject jsonObject = new JSONObject();
        final String from = SharedHelper.getKey(ChangeGrpName.this, "my_zoe_id");
        ServiceClasss.Emitters emitters=new ServiceClasss.Emitters(ChangeGrpName.this);
        Boolean res = emitters.editgroupname( grp_id,from_id, g_name, old_id);


    }

    public static void trigger_editname(final String grp_id, String from_id, final String g_name, final String old_id,Context context)
    {
        DBHandler dbHandler=new DBHandler(context);
        final String from = SharedHelper.getKey(context, "my_zoe_id");

        long millis = System.currentTimeMillis();
        String msg_value = context.getResources().getString(R.string.you_changed_group_name)+old_id+" to "+ g_name  ;
        String uniqueID = UUID.randomUUID().toString();
        dbHandler.GroupNameUpdate(g_name, grp_id);
        dbHandler.InsertChatMsg(new ChatsMessagesModel(uniqueID.trim(), SharedHelper.getKey(context, "id"), msg_value, ChatType.editGroupName.toString(), Status.SENDING.toString(), ChatMessages.CREATE_GROUP, grp_id, "" + millis, "0", "0", 0, 0, 0, "0.0", "0.0", "", "", "1", "false", "", "", 0, "", "", "", ""));
        dbHandler.UpdateLastMsg(ChatActivity.groupId_loc, from, msg_value, "", String.valueOf(millis), ChatMessages.CREATE_GROUP, MainActivity.unreadCount);
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
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
        }).build(chat_text);
    }
}
