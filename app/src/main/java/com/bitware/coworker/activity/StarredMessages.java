package com.bitware.coworker.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bitware.coworker.baseUtils.SharedHelper;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.bitware.coworker.DBHelper.DBHandler;
import com.bitware.coworker.R;
import com.bitware.coworker.adapter.SingleChatAdapter;
import com.bitware.coworker.adapter.StarChatAdapter;
import com.bitware.coworker.fragment.ChatFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StarredMessages extends AppCompatActivity {
    public static Toolbar toolbar;
    public static LinearLayout back_layout;
    //toolbar
    //private DBHandler dbHandler;
    public static ImageView back;
    public static int counter = 0;
    public static StarChatAdapter starChatAdapter;
    public static String read_time, delievered_time;
    JSONArray jsonArray;
    ImageView  img;
    DBHandler dbHandler;
    RecyclerView star_list_view;
    LinearLayoutManager linearLayoutManager;
    RelativeLayout empty_layout;

    public static void clearActionM() {
        toolbar.getMenu().clear();
        toolbar.setNavigationIcon(null);
        ChatActivity.counter = 0;
        StarredMessages.back_layout.setVisibility(View.VISIBLE);
        starChatAdapter.notifyDataSetChanged();

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

    public void setIconColour(Drawable drawable, int primaryCOlor) {

        drawable.setColorFilter(new PorterDuffColorFilter(getPrimaryCOlor(StarredMessages.this), PorterDuff.Mode.SRC_IN));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String themevalue= SharedHelper.getKey(this,"theme_value");
        Setheme(themevalue);
        setContentView(R.layout.activity_starred_messages);

        toolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        toolbar.setBackgroundColor(getPrimaryCOlor(StarredMessages.this));
        setSupportActionBar(toolbar);

        img=(ImageView)findViewById(R.id.img);
        setIconColour(img.getDrawable(),getPrimaryCOlor(StarredMessages.this));

        back_layout = (LinearLayout) findViewById(R.id.back_layout);
        empty_layout=(RelativeLayout)findViewById(R.id.empty_layout);
        empty_layout.setVisibility(View.GONE);
        star_list_view = (RecyclerView) findViewById(R.id.star_list_view);
        star_list_view.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(Color.parseColor("#dfdfdf"))
                .sizeResId(R.dimen.divider)
                .marginResId(R.dimen.leftmargin_star, R.dimen.rightmargin_star)
                .build());

        final LinearLayout linearLayout = (LinearLayout) toolbar.findViewById(R.id.back_layout);
        back = (ImageView) linearLayout.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });

        dbHandler = new DBHandler(StarredMessages.this);
        jsonArray = dbHandler.GetChatsStarred();
        Log.d("onCreate: ","Jsonarray:"+jsonArray);
        if (jsonArray.length()>0)
        {
            empty_layout.setVisibility(View.GONE);
        }
        else
            {
                empty_layout.setVisibility(View.VISIBLE);
        }
        linearLayoutManager = new LinearLayoutManager(StarredMessages.this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setAutoMeasureEnabled(true);


        starChatAdapter = new StarChatAdapter(StarredMessages.this, jsonArray);

        star_list_view.setLayoutManager(linearLayoutManager);
        star_list_view.setHasFixedSize(false);

        //chatListView.setAdapter(singleChatAdapter);
        star_list_view.swapAdapter(starChatAdapter, false);

    }


    public void addToPos(int pos, JSONObject jsonObj, JSONArray jsonArr) throws JSONException {
        for (int i = jsonArr.length(); i > pos; i--) {
            jsonArr.put(i, jsonArr.get(i - 1));
        }
        jsonArr.put(pos, jsonObj);
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_delete:
                for (int i = 0; i < SingleChatAdapter.selectRemoveItem.size(); i++) {
                    Log.d("onOptionsItemSelected:", "dele:" + SingleChatAdapter.selectRemoveItem.get(i));
                    dbHandler = new DBHandler(StarredMessages.this);
                    dbHandler.DeleteChatsMsg(SingleChatAdapter.selectRemoveItem.get(i));
                    jsonArray.remove(SingleChatAdapter.selectRemoveItemPosition.get(i));
                }


                if (jsonArray.length() == 0) {
                    SharedHelper.putHeader(StarredMessages.this, "header_time", "0");
                }
                toolbar.getMenu().clear();
                toolbar.setNavigationIcon(null);
                back_layout.setVisibility(View.VISIBLE);
                counter = 0;
                starChatAdapter.notifyDataSetChanged();


                break;


            case R.id.action_info:

                showDialog(StarredMessages.this, StarredMessages.read_time, StarredMessages.delievered_time);


                break;

            case R.id.action_forward:
                DBHandler dbHandler = new DBHandler(StarredMessages.this);
                Intent forward = new Intent(StarredMessages.this, ForwardMessageActivity.class);
                ChatFragment.isGroup = "true";
                forward.putExtra("forward_msg", "true");
                forward.putExtra("message", dbHandler.GetMessage(StarChatAdapter.selectRemoveItem.get(0)));
                forward.putExtra("message_type", dbHandler.GetMessageType(StarChatAdapter.selectRemoveItem.get(0)));
                forward.putExtra("c_name", dbHandler.GetCName(StarChatAdapter.selectRemoveItem.get(0)));
                forward.putExtra("c_id", dbHandler.GetCId(StarChatAdapter.selectRemoveItem.get(0)));
                forward.putExtra("media", dbHandler.GetMedia(StarChatAdapter.selectRemoveItem.get(0)));
                String group_id = dbHandler.GetgroupID(StarChatAdapter.selectRemoveItem.get(0));
                forward.putExtra("group_id", dbHandler.GetgroupID(StarChatAdapter.selectRemoveItem.get(0)));
                startActivity(forward);
                break;


            case R.id.action_copy:
                int pos = Integer.parseInt(SingleChatAdapter.select_position.get(0));
                String msg = SingleChatAdapter.mMessages.optJSONObject(pos).optString("msg");
                Log.d("onOptionsItemSelected: ", "message:" + msg);
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copy", msg);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(StarredMessages.this, "Text Copied!!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_star_remove:

                for (int i = 0; i < SingleChatAdapter.selectRemoveItem.size(); i++) {
                    Log.d("onOptionsItemSelected:", "dele:" + SingleChatAdapter.selectRemoveItem.get(i));
                    dbHandler = new DBHandler(StarredMessages.this);
                    dbHandler.UpdateStarred(SingleChatAdapter.selectRemoveItem.get(i), "false");
                    int removePos = SingleChatAdapter.selectRemoveItemPosition.get(i);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject = jsonArray.optJSONObject(removePos);
                    jsonArray.remove(removePos);
                    try {
                        jsonObject.remove("isStarred");
                        jsonObject.remove("isSelected");
                        jsonObject.put("isStarred", "false");
                        jsonObject.put("isSelected", "false");
                        addToPos(removePos, jsonObject, jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                toolbar.getMenu().clear();

                toolbar.setNavigationIcon(null);
                back_layout.setVisibility(View.VISIBLE);
                counter = 0;
                starChatAdapter.notifyDataSetChanged();
                if (jsonArray.length()==0)
                {
                    empty_layout.setVisibility(View.VISIBLE);
                }

                break;

        }
        return true;

    }


    public void showDialog(Activity activity, String read_time_val, String del_time_val) {
        final Dialog dialog = new Dialog(activity);

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.info_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);


        TextView read_time, del_time;

        read_time = (TextView) dialog.findViewById(R.id.read_time_text);
        del_time = (TextView) dialog.findViewById(R.id.delievered_time_text);
        if (read_time_val.equalsIgnoreCase("0")) {
            read_time.setText("-");
        } else {
            read_time.setText(read_time_val);
        }

        if (del_time_val.equalsIgnoreCase("0")) {
            del_time.setText("-");
        } else {

            del_time.setText(del_time_val);
        }


        dialog.show();
    }

}
